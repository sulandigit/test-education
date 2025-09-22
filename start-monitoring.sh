#!/bin/bash

# 领课教育监控系统快速启动脚本
# 自动化部署和配置监控基础设施

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查系统要求
check_requirements() {
    log_info "检查系统要求..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    # 检查可用内存
    total_mem=$(free -m | awk 'NR==2{printf "%.0f", $2}')
    if [ "$total_mem" -lt 4096 ]; then
        log_warning "系统内存少于4GB，可能影响监控系统性能"
    fi
    
    # 检查磁盘空间
    available_space=$(df / | awk 'NR==2{print $4}')
    if [ "$available_space" -lt 10485760 ]; then  # 10GB in KB
        log_warning "根分区可用空间少于10GB，可能影响数据存储"
    fi
    
    log_success "系统要求检查完成"
}

# 初始化数据目录
init_data_dirs() {
    log_info "初始化监控数据目录..."
    
    # 创建数据目录
    sudo mkdir -p /data/monitoring/{prometheus,grafana,alertmanager}
    
    # 设置权限
    sudo chown -R 65534:65534 /data/monitoring/prometheus  # nobody用户
    sudo chown -R 472:472 /data/monitoring/grafana        # grafana用户
    sudo chown -R 65534:65534 /data/monitoring/alertmanager # nobody用户
    
    # 设置目录权限
    sudo chmod -R 755 /data/monitoring
    
    log_success "数据目录初始化完成"
}

# 配置环境变量
setup_environment() {
    log_info "配置环境变量..."
    
    if [ ! -f "monitoring/.env" ]; then
        if [ -f "monitoring/.env.example" ]; then
            cp monitoring/.env.example monitoring/.env
            log_info "已创建环境变量配置文件: monitoring/.env"
            log_warning "请编辑 monitoring/.env 文件配置钉钉机器人等参数"
        else
            log_warning "未找到环境变量模板文件"
        fi
    else
        log_info "环境变量配置文件已存在"
    fi
}

# 启动监控服务
start_monitoring() {
    local mode=$1
    
    log_info "启动监控服务 (模式: $mode)..."
    
    case $mode in
        "monitoring-only")
            log_info "启动监控基础设施..."
            cd monitoring
            docker-compose -f docker-compose.monitoring.yml up -d
            cd ..
            ;;
        "complete")
            log_info "启动完整系统（应用+监控）..."
            docker-compose -f docker-compose.complete.yml up -d
            ;;
        *)
            log_error "未知的启动模式: $mode"
            exit 1
            ;;
    esac
}

# 等待服务启动
wait_for_services() {
    log_info "等待服务启动..."
    
    local services=("prometheus:9090" "grafana:3000" "alertmanager:9093")
    
    for service in "${services[@]}"; do
        local name=${service%:*}
        local port=${service#*:}
        local retry_count=0
        local max_retries=30
        
        log_info "等待 $name 服务启动..."
        
        while [ $retry_count -lt $max_retries ]; do
            if curl -s http://localhost:$port/api/v1/query &>/dev/null || \
               curl -s http://localhost:$port/api/health &>/dev/null || \
               curl -s http://localhost:$port/-/ready &>/dev/null; then
                log_success "$name 服务已启动"
                break
            fi
            
            sleep 5
            retry_count=$((retry_count + 1))
            
            if [ $retry_count -eq $max_retries ]; then
                log_error "$name 服务启动超时"
                return 1
            fi
        done
    done
    
    log_success "所有服务已启动"
}

# 验证部署
verify_deployment() {
    log_info "验证部署状态..."
    
    # 检查服务健康状态
    local health_checks=(
        "Prometheus:http://localhost:9090/api/v1/query?query=up"
        "Grafana:http://localhost:3000/api/health"
        "AlertManager:http://localhost:9093/api/v1/status"
    )
    
    for check in "${health_checks[@]}"; do
        local name=${check%:*}
        local url=${check#*:}
        
        if curl -s "$url" > /dev/null; then
            log_success "$name 健康检查通过"
        else
            log_error "$name 健康检查失败"
        fi
    done
}

# 显示访问信息
show_access_info() {
    log_success "🎉 监控系统部署完成！"
    echo
    echo "📊 访问地址："
    echo "  Grafana:     http://localhost:3000"
    echo "  Prometheus:  http://localhost:9090"
    echo "  AlertManager: http://localhost:9093"
    echo
    echo "🔐 默认账户："
    echo "  Grafana: admin / education123!"
    echo
    echo "📋 下一步操作："
    echo "  1. 访问 Grafana 配置仪表板"
    echo "  2. 检查 Prometheus 指标采集"
    echo "  3. 配置告警通知渠道"
    echo "  4. 查看监控指南: cat MONITORING_GUIDE.md"
    echo
}

# 显示帮助信息
show_help() {
    echo "领课教育监控系统快速启动脚本"
    echo
    echo "用法: $0 [选项]"
    echo
    echo "选项:"
    echo "  -m, --mode MODE          启动模式 (monitoring-only|complete)"
    echo "  -h, --help              显示帮助信息"
    echo "  --check-only            仅检查系统要求"
    echo "  --stop                  停止监控服务"
    echo "  --restart               重启监控服务"
    echo "  --status                查看服务状态"
    echo "  --logs SERVICE          查看服务日志"
    echo
    echo "启动模式:"
    echo "  monitoring-only         仅启动监控基础设施"
    echo "  complete               启动完整系统（应用+监控）"
    echo
    echo "示例:"
    echo "  $0 -m monitoring-only   # 仅启动监控服务"
    echo "  $0 -m complete          # 启动完整系统"
    echo "  $0 --status             # 查看服务状态"
    echo "  $0 --logs prometheus    # 查看Prometheus日志"
}

# 停止服务
stop_services() {
    log_info "停止监控服务..."
    
    if [ -f "docker-compose.complete.yml" ]; then
        docker-compose -f docker-compose.complete.yml down
    fi
    
    if [ -f "monitoring/docker-compose.monitoring.yml" ]; then
        cd monitoring
        docker-compose -f docker-compose.monitoring.yml down
        cd ..
    fi
    
    log_success "监控服务已停止"
}

# 重启服务
restart_services() {
    local mode=$1
    log_info "重启监控服务..."
    
    stop_services
    sleep 5
    start_monitoring "$mode"
    wait_for_services
    
    log_success "监控服务已重启"
}

# 查看服务状态
show_status() {
    log_info "查看服务状态..."
    
    echo
    echo "🔍 Docker 容器状态："
    docker ps --filter "name=education-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo
    echo "📊 服务健康检查："
    
    local services=("prometheus:9090" "grafana:3000" "alertmanager:9093")
    for service in "${services[@]}"; do
        local name=${service%:*}
        local port=${service#*:}
        
        if curl -s http://localhost:$port > /dev/null 2>&1; then
            echo "  ✅ $name: 正常"
        else
            echo "  ❌ $name: 异常"
        fi
    done
}

# 查看服务日志
show_logs() {
    local service=$1
    
    if [ -z "$service" ]; then
        log_error "请指定服务名称"
        echo "可用服务: prometheus, grafana, alertmanager, node-exporter, dingtalk-webhook"
        exit 1
    fi
    
    log_info "查看 $service 服务日志..."
    
    local container_name="education-$service"
    if docker ps --filter "name=$container_name" --format "{{.Names}}" | grep -q "$container_name"; then
        docker logs -f "$container_name"
    else
        log_error "服务 $service 未运行"
    fi
}

# 主函数
main() {
    local mode="monitoring-only"
    local action="start"
    local service=""
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -m|--mode)
                mode="$2"
                shift 2
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            --check-only)
                action="check"
                shift
                ;;
            --stop)
                action="stop"
                shift
                ;;
            --restart)
                action="restart"
                shift
                ;;
            --status)
                action="status"
                shift
                ;;
            --logs)
                action="logs"
                service="$2"
                shift 2
                ;;
            *)
                log_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # 执行相应操作
    case $action in
        "check")
            check_requirements
            ;;
        "start")
            check_requirements
            init_data_dirs
            setup_environment
            start_monitoring "$mode"
            wait_for_services
            verify_deployment
            show_access_info
            ;;
        "stop")
            stop_services
            ;;
        "restart")
            restart_services "$mode"
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs "$service"
            ;;
        *)
            log_error "未知操作: $action"
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@"