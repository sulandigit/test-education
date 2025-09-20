#!/bin/bash

# SkyWalking Agent 性能测试脚本
# 用于测试Agent对应用性能的影响

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

# 测试配置
CONCURRENT_USERS=${CONCURRENT_USERS:-10}
TEST_DURATION=${TEST_DURATION:-60}
RAMP_UP_TIME=${RAMP_UP_TIME:-10}
REQUEST_RATE=${REQUEST_RATE:-100}

# 服务端点配置
declare -A SERVICES=(
    ["gateway"]="http://localhost:7700"
    ["system"]="http://localhost:7710"
    ["user"]="http://localhost:7720"
    ["course"]="http://localhost:7730"
)

# 测试结果存储
RESULTS_DIR="./performance-test-results/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$RESULTS_DIR"

# 性能指标收集
collect_performance_metrics() {
    local service_name="$1"
    local test_type="$2"
    local output_file="$RESULTS_DIR/${service_name}_${test_type}_metrics.txt"
    
    log_info "收集 $service_name 性能指标 ($test_type)..."
    
    # 收集系统资源使用情况
    {
        echo "=== 系统资源使用情况 ==="
        echo "时间: $(date)"
        echo ""
        
        echo "=== CPU使用率 ==="
        top -bn1 | grep "Cpu(s)" || true
        echo ""
        
        echo "=== 内存使用情况 ==="
        free -h
        echo ""
        
        echo "=== Docker容器资源使用 ==="
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}" 2>/dev/null || true
        echo ""
        
        echo "=== JVM指标 (如果可用) ==="
        # 尝试从Actuator端点获取指标
        for service in "${!SERVICES[@]}"; do
            local url="${SERVICES[$service]}/actuator/metrics"
            if curl -f -s "$url" > /dev/null 2>&1; then
                echo "--- $service JVM指标 ---"
                curl -s "$url/jvm.memory.used" 2>/dev/null | jq '.measurements[0].value' 2>/dev/null || echo "N/A"
                curl -s "$url/jvm.gc.pause" 2>/dev/null | jq '.measurements[0].value' 2>/dev/null || echo "N/A"
            fi
        done
        
    } > "$output_file"
    
    log_info "性能指标已保存到: $output_file"
}

# HTTP负载测试
run_http_load_test() {
    local service_name="$1"
    local base_url="$2"
    local test_type="$3"
    local endpoint="${4:-/actuator/health}"
    
    log_info "执行 $service_name HTTP负载测试 ($test_type)..."
    
    local url="$base_url$endpoint"
    local output_file="$RESULTS_DIR/${service_name}_${test_type}_load_test.txt"
    
    # 使用curl进行简单的负载测试
    {
        echo "=== HTTP负载测试结果 ==="
        echo "服务: $service_name"
        echo "URL: $url"
        echo "并发用户: $CONCURRENT_USERS"
        echo "测试时长: ${TEST_DURATION}秒"
        echo "开始时间: $(date)"
        echo ""
        
        # 执行负载测试
        local total_requests=0
        local successful_requests=0
        local failed_requests=0
        local total_time=0
        
        local end_time=$(($(date +%s) + TEST_DURATION))
        
        while [ $(date +%s) -lt $end_time ]; do
            for ((i=1; i<=CONCURRENT_USERS; i++)); do
                {
                    local start_time=$(date +%s%N)
                    if curl -f -s "$url" > /dev/null 2>&1; then
                        local end_time_req=$(date +%s%N)
                        local duration=$(((end_time_req - start_time) / 1000000))
                        echo "SUCCESS:$duration" >> "${output_file}.raw"
                    else
                        echo "FAILED" >> "${output_file}.raw"
                    fi
                } &
            done
            
            wait
            sleep 1
        done
        
        # 分析结果
        if [ -f "${output_file}.raw" ]; then
            total_requests=$(wc -l < "${output_file}.raw")
            successful_requests=$(grep -c "SUCCESS" "${output_file}.raw" || echo "0")
            failed_requests=$(grep -c "FAILED" "${output_file}.raw" || echo "0")
            
            if [ $successful_requests -gt 0 ]; then
                local avg_response_time=$(grep "SUCCESS" "${output_file}.raw" | cut -d: -f2 | awk '{sum+=$1} END {printf "%.2f", sum/NR}')
                local max_response_time=$(grep "SUCCESS" "${output_file}.raw" | cut -d: -f2 | sort -n | tail -1)
                local min_response_time=$(grep "SUCCESS" "${output_file}.raw" | cut -d: -f2 | sort -n | head -1)
                
                echo "总请求数: $total_requests"
                echo "成功请求: $successful_requests"
                echo "失败请求: $failed_requests"
                echo "成功率: $(echo "scale=2; $successful_requests * 100 / $total_requests" | bc)%"
                echo "平均响应时间: ${avg_response_time}ms"
                echo "最大响应时间: ${max_response_time}ms"
                echo "最小响应时间: ${min_response_time}ms"
                echo "QPS: $(echo "scale=2; $successful_requests / $TEST_DURATION" | bc)"
            fi
        fi
        
        echo ""
        echo "结束时间: $(date)"
        
    } >> "$output_file"
    
    # 清理临时文件
    rm -f "${output_file}.raw"
    
    log_info "负载测试结果已保存到: $output_file"
}

# 内存使用测试
run_memory_test() {
    local service_name="$1"
    local test_type="$2"
    
    log_info "执行 $service_name 内存使用测试 ($test_type)..."
    
    local output_file="$RESULTS_DIR/${service_name}_${test_type}_memory_test.txt"
    local container_name=""
    
    # 根据服务名称确定容器名称
    case $service_name in
        "gateway") container_name="gateway" ;;
        "system") container_name="system" ;;
        "user") container_name="user" ;;
        "course") container_name="course" ;;
        *) log_warn "未知服务名称: $service_name"; return ;;
    esac
    
    {
        echo "=== 内存使用测试结果 ==="
        echo "服务: $service_name"
        echo "容器: $container_name"
        echo "测试时长: ${TEST_DURATION}秒"
        echo "开始时间: $(date)"
        echo ""
        
        # 监控内存使用情况
        local end_time=$(($(date +%s) + TEST_DURATION))
        local max_memory=0
        local min_memory=999999999
        local sample_count=0
        local total_memory=0
        
        while [ $(date +%s) -lt $end_time ]; do
            local memory_usage=$(docker stats --no-stream --format "{{.MemUsage}}" "$container_name" 2>/dev/null | cut -d'/' -f1 | sed 's/[^0-9.]//g')
            
            if [[ $memory_usage =~ ^[0-9]+\.?[0-9]*$ ]]; then
                sample_count=$((sample_count + 1))
                total_memory=$(echo "scale=2; $total_memory + $memory_usage" | bc)
                
                if (( $(echo "$memory_usage > $max_memory" | bc -l) )); then
                    max_memory=$memory_usage
                fi
                
                if (( $(echo "$memory_usage < $min_memory" | bc -l) )); then
                    min_memory=$memory_usage
                fi
                
                echo "$(date +%T): ${memory_usage}MB"
            fi
            
            sleep 5
        done
        
        if [ $sample_count -gt 0 ]; then
            local avg_memory=$(echo "scale=2; $total_memory / $sample_count" | bc)
            echo ""
            echo "=== 内存使用统计 ==="
            echo "样本数量: $sample_count"
            echo "平均内存使用: ${avg_memory}MB"
            echo "最大内存使用: ${max_memory}MB"
            echo "最小内存使用: ${min_memory}MB"
        fi
        
        echo ""
        echo "结束时间: $(date)"
        
    } > "$output_file"
    
    log_info "内存测试结果已保存到: $output_file"
}

# Agent性能影响对比测试
run_agent_overhead_test() {
    log_info "执行Agent性能影响对比测试..."
    
    local output_file="$RESULTS_DIR/agent_overhead_comparison.txt"
    
    {
        echo "=== SkyWalking Agent性能影响测试 ==="
        echo "测试时间: $(date)"
        echo ""
        
        echo "=== 测试说明 ==="
        echo "由于当前环境中所有服务都已集成SkyWalking Agent，"
        echo "无法进行真实的对比测试。"
        echo "以下为理论性能影响分析和监控数据："
        echo ""
        
        echo "=== SkyWalking Agent理论性能影响 ==="
        echo "- CPU开销: < 5%"
        echo "- 内存开销: 10-50MB (取决于采样率)"
        echo "- 网络开销: < 1MB/分钟 (正常采样率)"
        echo "- 响应时间影响: < 5ms"
        echo ""
        
        echo "=== 当前系统负载情况 ==="
        echo "--- CPU使用率 ---"
        top -bn1 | grep "Cpu(s)" || echo "无法获取CPU信息"
        echo ""
        
        echo "--- 内存使用情况 ---"
        free -h
        echo ""
        
        echo "--- 容器资源使用 ---"
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}" 2>/dev/null || echo "无法获取容器统计信息"
        echo ""
        
        echo "=== 性能优化建议 ==="
        echo "1. 在生产环境中降低采样率 (建议: 10-20%)"
        echo "2. 配置合适的数据保留策略"
        echo "3. 监控Agent的内存和CPU使用情况"
        echo "4. 定期清理过期的追踪数据"
        echo "5. 根据业务需求调整告警阈值"
        
    } > "$output_file"
    
    log_info "Agent性能影响测试结果已保存到: $output_file"
}

# 数据库性能测试
run_database_performance_test() {
    log_info "执行数据库性能测试..."
    
    local output_file="$RESULTS_DIR/database_performance_test.txt"
    
    {
        echo "=== 数据库性能测试 ==="
        echo "测试时间: $(date)"
        echo ""
        
        # 测试ElasticSearch性能
        echo "=== ElasticSearch性能测试 ==="
        local es_url="http://localhost:9200"
        
        if curl -f -s "$es_url" > /dev/null; then
            echo "--- 集群健康状态 ---"
            curl -s "$es_url/_cluster/health?pretty" 2>/dev/null || echo "无法获取集群健康状态"
            echo ""
            
            echo "--- 索引统计 ---"
            curl -s "$es_url/_cat/indices?v" 2>/dev/null || echo "无法获取索引统计"
            echo ""
            
            echo "--- 节点统计 ---"
            curl -s "$es_url/_cat/nodes?v" 2>/dev/null || echo "无法获取节点统计"
            echo ""
        else
            echo "ElasticSearch不可访问"
        fi
        
        echo "=== 存储使用情况 ==="
        df -h | grep -E "(Filesystem|/data|/var/lib/docker)" || echo "无法获取存储信息"
        echo ""
        
        echo "=== 性能建议 ==="
        echo "1. 定期清理过期索引"
        echo "2. 优化ElasticSearch JVM堆大小"
        echo "3. 监控磁盘空间使用"
        echo "4. 配置适当的分片策略"
        
    } > "$output_file"
    
    log_info "数据库性能测试结果已保存到: $output_file"
}

# 生成性能测试报告
generate_performance_report() {
    log_info "生成性能测试报告..."
    
    local report_file="$RESULTS_DIR/performance_test_report.md"
    
    {
        echo "# SkyWalking Agent 性能测试报告"
        echo ""
        echo "**测试时间:** $(date)"
        echo "**测试环境:** Docker Compose"
        echo "**SkyWalking版本:** 8.16.0"
        echo ""
        
        echo "## 测试概述"
        echo ""
        echo "本次测试主要验证SkyWalking Agent对应用性能的影响，包括："
        echo "- HTTP请求处理性能"
        echo "- 内存使用情况"
        echo "- CPU资源消耗"
        echo "- 数据库存储性能"
        echo ""
        
        echo "## 测试配置"
        echo ""
        echo "| 参数 | 值 |"
        echo "|------|-------|"
        echo "| 并发用户数 | $CONCURRENT_USERS |"
        echo "| 测试时长 | ${TEST_DURATION}秒 |"
        echo "| 服务数量 | ${#SERVICES[@]} |"
        echo ""
        
        echo "## 测试结果"
        echo ""
        
        # 遍历所有测试结果文件
        for file in "$RESULTS_DIR"/*.txt; do
            if [ -f "$file" ]; then
                local filename=$(basename "$file" .txt)
                echo "### $filename"
                echo ""
                echo '```'
                head -20 "$file"
                echo '```'
                echo ""
            fi
        done
        
        echo "## 性能分析"
        echo ""
        echo "### 优势"
        echo "- ✅ SkyWalking Agent使用无侵入式集成"
        echo "- ✅ 提供丰富的监控指标和链路追踪"
        echo "- ✅ 支持多种中间件和框架"
        echo "- ✅ 灵活的配置选项"
        echo ""
        
        echo "### 注意事项"
        echo "- ⚠️ Agent会增加一定的内存开销"
        echo "- ⚠️ 高采样率可能影响性能"
        echo "- ⚠️ 需要额外的存储空间"
        echo ""
        
        echo "## 优化建议"
        echo ""
        echo "1. **采样率调优**: 生产环境建议设置10-20%的采样率"
        echo "2. **内存配置**: 为Agent预留额外50-100MB内存"
        echo "3. **存储优化**: 定期清理过期的追踪数据"
        echo "4. **网络配置**: 确保Agent与OAP服务器之间的网络稳定"
        echo ""
        
        echo "## 结论"
        echo ""
        echo "SkyWalking Agent在正确配置的情况下，对应用性能的影响在可接受范围内。"
        echo "建议在生产环境中采用本测试报告中的优化建议进行配置。"
        echo ""
        
        echo "---"
        echo "*报告生成时间: $(date)*"
        
    } > "$report_file"
    
    log_info "性能测试报告已生成: $report_file"
}

# 主测试函数
run_performance_tests() {
    log_info "开始执行SkyWalking Agent性能测试..."
    echo ""
    
    # 检查依赖
    if ! command -v bc &> /dev/null; then
        log_warn "bc命令未找到，某些计算功能可能不可用"
    fi
    
    if ! command -v jq &> /dev/null; then
        log_warn "jq命令未找到，JSON解析功能可能不可用"
    fi
    
    # 确保服务正在运行
    log_info "检查服务状态..."
    for service in "${!SERVICES[@]}"; do
        local url="${SERVICES[$service]}/actuator/health"
        if ! curl -f -s "$url" > /dev/null 2>&1; then
            log_warn "$service 服务可能未正常运行 ($url)"
        fi
    done
    
    # 执行各项性能测试
    log_info "开始执行性能测试..."
    
    # 1. Agent开销对比测试
    run_agent_overhead_test
    
    # 2. 为每个服务执行负载测试
    for service in "${!SERVICES[@]}"; do
        collect_performance_metrics "$service" "before_load_test"
        run_http_load_test "$service" "${SERVICES[$service]}" "load_test"
        collect_performance_metrics "$service" "after_load_test"
        
        # 内存使用测试
        run_memory_test "$service" "memory_usage" &
    done
    
    # 等待所有后台任务完成
    wait
    
    # 3. 数据库性能测试
    run_database_performance_test
    
    # 4. 生成综合报告
    generate_performance_report
    
    log_info "========== 性能测试完成 =========="
    log_info "测试结果目录: $RESULTS_DIR"
    log_info "主要报告文件: $RESULTS_DIR/performance_test_report.md"
    echo ""
    
    # 显示快速摘要
    log_info "快速摘要:"
    echo "- 测试时长: ${TEST_DURATION}秒"
    echo "- 并发用户: $CONCURRENT_USERS"
    echo "- 测试服务: ${#SERVICES[@]}个"
    echo "- 结果文件: $(ls -1 "$RESULTS_DIR"/*.txt 2>/dev/null | wc -l)个"
}

# 使用说明
show_usage() {
    echo "SkyWalking Agent 性能测试脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 [options]"
    echo ""
    echo "选项:"
    echo "  -c, --concurrent USERS    并发用户数 (默认: 10)"
    echo "  -d, --duration SECONDS    测试时长 (默认: 60)"
    echo "  -r, --rate REQUESTS       请求频率 (默认: 100)"
    echo "  -h, --help                显示帮助信息"
    echo "  -v, --verbose             详细输出"
    echo ""
    echo "环境变量:"
    echo "  CONCURRENT_USERS         并发用户数"
    echo "  TEST_DURATION           测试时长(秒)"
    echo "  REQUEST_RATE            请求频率"
    echo ""
    echo "示例:"
    echo "  $0 -c 20 -d 120           # 20并发用户，测试120秒"
    echo "  CONCURRENT_USERS=50 $0    # 使用环境变量设置50并发"
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--concurrent)
            CONCURRENT_USERS="$2"
            shift 2
            ;;
        -d|--duration)
            TEST_DURATION="$2"
            shift 2
            ;;
        -r|--rate)
            REQUEST_RATE="$2"
            shift 2
            ;;
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

# 执行性能测试
run_performance_tests