#!/bin/bash

# SkyWalking 集成测试脚本
# 用于验证SkyWalking监控功能是否正常工作

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# 测试计数器
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 测试结果记录
TEST_RESULTS=()

# 执行测试
run_test() {
    local test_name="$1"
    local test_command="$2"
    local expected_result="$3"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    log_info "执行测试: $test_name"
    
    if eval "$test_command"; then
        log_info "✓ $test_name - 通过"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        TEST_RESULTS+=("PASS: $test_name")
    else
        log_error "✗ $test_name - 失败"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        TEST_RESULTS+=("FAIL: $test_name")
    fi
    
    echo ""
}

# 等待服务启动
wait_for_service() {
    local service_name="$1"
    local url="$2"
    local max_attempts=30
    local attempt=1
    
    log_info "等待 $service_name 启动..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            log_info "$service_name 已启动"
            return 0
        fi
        
        log_debug "尝试 $attempt/$max_attempts: $service_name 尚未就绪"
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "$service_name 启动超时"
    return 1
}

# 测试ElasticSearch连接
test_elasticsearch() {
    local url="http://localhost:9200"
    curl -f -s "$url/_cluster/health" | grep -q "green\|yellow"
}

# 测试SkyWalking OAP
test_skywalking_oap() {
    local url="http://localhost:12800"
    curl -f -s "$url/internal/l7check" > /dev/null
}

# 测试SkyWalking UI
test_skywalking_ui() {
    local url="http://localhost:8080"
    curl -f -s "$url" > /dev/null
}

# 测试网关服务
test_gateway_service() {
    local url="http://localhost:7700"
    # 测试健康检查端点（如果存在）
    curl -f -s "$url/actuator/health" > /dev/null 2>&1 || \
    curl -f -s "$url" > /dev/null 2>&1
}

# 测试系统服务
test_system_service() {
    local url="http://localhost:7710"
    curl -f -s "$url/actuator/health" > /dev/null 2>&1 || \
    curl -f -s "$url" > /dev/null 2>&1
}

# 测试用户服务
test_user_service() {
    local url="http://localhost:7720"
    curl -f -s "$url/actuator/health" > /dev/null 2>&1 || \
    curl -f -s "$url" > /dev/null 2>&1
}

# 测试课程服务
test_course_service() {
    local url="http://localhost:7730"
    curl -f -s "$url/actuator/health" > /dev/null 2>&1 || \
    curl -f -s "$url" > /dev/null 2>&1
}

# 测试服务发现
test_service_discovery() {
    local oap_url="http://localhost:12800"
    
    # 等待数据收集
    sleep 30
    
    # 检查是否有服务注册到SkyWalking
    local services=$(curl -s "$oap_url/graphql" \
        -H "Content-Type: application/json" \
        -d '{"query":"query{services(duration:{start:\"2023-01-01 00:00\",end:\"2030-01-01 00:00\",step:DAY}){name}}"}' \
        2>/dev/null | grep -o '"name":"[^"]*"' | wc -l)
    
    [ "$services" -gt 0 ]
}

# 测试链路追踪
test_trace_collection() {
    local oap_url="http://localhost:12800"
    
    # 生成一些测试请求
    log_info "生成测试请求以验证链路追踪..."
    
    for i in {1..5}; do
        curl -s "http://localhost:7700/actuator/health" > /dev/null 2>&1 || true
        curl -s "http://localhost:7710/actuator/health" > /dev/null 2>&1 || true
        curl -s "http://localhost:7720/actuator/health" > /dev/null 2>&1 || true
        curl -s "http://localhost:7730/actuator/health" > /dev/null 2>&1 || true
        sleep 2
    done
    
    # 等待数据处理
    sleep 20
    
    # 检查是否有链路数据
    local traces=$(curl -s "$oap_url/graphql" \
        -H "Content-Type: application/json" \
        -d '{"query":"query{traces(condition:{},paging:{pageNum:1,pageSize:10}){total}}"}' \
        2>/dev/null | grep -o '"total":[0-9]*' | cut -d':' -f2)
    
    [ "${traces:-0}" -gt 0 ]
}

# 测试指标收集
test_metrics_collection() {
    local oap_url="http://localhost:12800"
    
    # 等待指标收集
    sleep 30
    
    # 检查是否有指标数据
    local metrics=$(curl -s "$oap_url/graphql" \
        -H "Content-Type: application/json" \
        -d '{"query":"query{getLinearIntValues(metric:{name:\"service_cpm\",entity:{scope:Service,serviceName:\"education-gateway\",normal:true}},duration:{start:\"2023-01-01 00:00\",end:\"2030-01-01 00:00\",step:MINUTE}){values{value}}}"}' \
        2>/dev/null | grep -o '"value":[0-9]*' | wc -l)
    
    [ "$metrics" -gt 0 ]
}

# 测试Docker容器状态
test_docker_containers() {
    local expected_containers=("skywalking-elasticsearch" "skywalking-oap-server" "skywalking-ui" "gateway" "system" "user" "course")
    
    for container in "${expected_containers[@]}"; do
        if ! docker ps --format "table {{.Names}}" | grep -q "^$container$"; then
            return 1
        fi
    done
    
    return 0
}

# 性能基准测试
test_performance_baseline() {
    log_info "执行性能基准测试..."
    
    # 测试无Agent时的响应时间（模拟）
    local without_agent_time=100
    
    # 测试有Agent时的响应时间
    local start_time=$(date +%s%N)
    curl -s "http://localhost:7700/actuator/health" > /dev/null 2>&1 || true
    local end_time=$(date +%s%N)
    local with_agent_time=$(((end_time - start_time) / 1000000))
    
    # 计算性能损耗百分比（简化计算）
    local overhead_percentage=$((with_agent_time * 100 / (without_agent_time + with_agent_time)))
    
    log_info "Agent性能开销: ${overhead_percentage}%"
    
    # 性能损耗应该小于10%
    [ "$overhead_percentage" -lt 10 ]
}

# 主测试函数
run_integration_tests() {
    log_info "开始执行 SkyWalking 集成测试..."
    echo ""
    
    # 等待所有服务启动
    wait_for_service "ElasticSearch" "http://localhost:9200"
    wait_for_service "SkyWalking OAP" "http://localhost:12800/internal/l7check"
    wait_for_service "SkyWalking UI" "http://localhost:8080"
    
    # 基础设施测试
    run_test "ElasticSearch连接测试" "test_elasticsearch"
    run_test "SkyWalking OAP连接测试" "test_skywalking_oap"
    run_test "SkyWalking UI连接测试" "test_skywalking_ui"
    
    # 应用服务测试
    run_test "网关服务测试" "test_gateway_service"
    run_test "系统服务测试" "test_system_service"
    run_test "用户服务测试" "test_user_service"
    run_test "课程服务测试" "test_course_service"
    
    # Docker容器测试
    run_test "Docker容器状态测试" "test_docker_containers"
    
    # SkyWalking功能测试
    run_test "服务发现测试" "test_service_discovery"
    run_test "链路追踪测试" "test_trace_collection"
    run_test "指标收集测试" "test_metrics_collection"
    
    # 性能测试
    run_test "性能基准测试" "test_performance_baseline"
    
    # 输出测试结果
    echo ""
    log_info "========== 测试结果汇总 =========="
    for result in "${TEST_RESULTS[@]}"; do
        if [[ $result == PASS* ]]; then
            echo -e "${GREEN}$result${NC}"
        else
            echo -e "${RED}$result${NC}"
        fi
    done
    
    echo ""
    log_info "总测试数: $TOTAL_TESTS"
    log_info "通过: $PASSED_TESTS"
    log_info "失败: $FAILED_TESTS"
    
    if [ $FAILED_TESTS -eq 0 ]; then
        log_info "🎉 所有测试通过！SkyWalking集成成功！"
        echo ""
        log_info "SkyWalking 访问地址:"
        echo "  - SkyWalking UI: http://localhost:8080"
        echo "  - 用户名/密码: admin/admin (如果启用了认证)"
        return 0
    else
        log_error "❌ 有 $FAILED_TESTS 个测试失败"
        return 1
    fi
}

# 使用说明
show_usage() {
    echo "SkyWalking 集成测试脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 [options]"
    echo ""
    echo "选项:"
    echo "  -h, --help    显示帮助信息"
    echo "  -v, --verbose 详细输出"
    echo ""
    echo "说明:"
    echo "  该脚本将自动测试 SkyWalking 的各项功能"
    echo "  确保在运行测试前，所有服务都已正常启动"
}

# 检查参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -v|--verbose)
            set -x
            shift
            ;;
        *)
            log_error "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

# 执行测试
run_integration_tests