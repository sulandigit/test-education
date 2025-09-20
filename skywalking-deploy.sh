#!/bin/bash

# SkyWalking 部署管理脚本
# 使用方法: ./skywalking-deploy.sh [start|stop|restart|status|logs]

set -e

COMPOSE_FILE="docker-compose-skywalking.yml"
PROJECT_NAME="education-skywalking"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 检查依赖
check_dependencies() {
    log_info "检查系统依赖..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    log_info "系统依赖检查完成"
}

# 检查SkyWalking Agent
check_skywalking_agent() {
    log_info "检查 SkyWalking Agent..."
    
    if [ ! -d "./skywalking-agent" ]; then
        log_warn "SkyWalking Agent 目录不存在，正在创建..."
        mkdir -p ./skywalking-agent/{config,plugins,logs}
        
        log_warn "请手动下载 SkyWalking Agent 并放置到 ./skywalking-agent 目录"
        log_warn "下载地址: https://skywalking.apache.org/downloads/"
        log_warn "或者运行: ./download-skywalking-agent.sh"
    fi
    
    if [ ! -f "./skywalking-agent/skywalking-agent.jar" ]; then
        log_warn "skywalking-agent.jar 文件不存在"
        log_warn "请确保已正确下载并配置 SkyWalking Agent"
    fi
}

# 启动服务
start_services() {
    log_info "启动 SkyWalking 监控系统..."
    
    check_dependencies
    check_skywalking_agent
    
    if [ ! -f "$COMPOSE_FILE" ]; then
        log_error "Docker Compose 文件 $COMPOSE_FILE 不存在"
        exit 1
    fi
    
    log_info "启动基础设施服务 (ElasticSearch, OAP, UI)..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d elasticsearch oap-server skywalking-ui
    
    log_info "等待基础设施服务启动完成..."
    sleep 30
    
    log_info "启动应用服务..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d
    
    log_info "所有服务启动完成"
    show_status
}

# 停止服务
stop_services() {
    log_info "停止 SkyWalking 监控系统..."
    
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down
    
    log_info "所有服务已停止"
}

# 重启服务
restart_services() {
    log_info "重启 SkyWalking 监控系统..."
    
    stop_services
    sleep 5
    start_services
}

# 显示服务状态
show_status() {
    log_info "服务状态:"
    
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME ps
    
    echo ""
    log_info "访问地址:"
    echo "  - SkyWalking UI: http://localhost:8080"
    echo "  - ElasticSearch: http://localhost:9200"
    echo "  - Gateway: http://localhost:7700"
    echo "  - System Service: http://localhost:7710"
    echo "  - User Service: http://localhost:7720"
    echo "  - Course Service: http://localhost:7730"
}

# 查看日志
show_logs() {
    local service=$1
    if [ -z "$service" ]; then
        log_info "显示所有服务日志..."
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f
    else
        log_info "显示 $service 服务日志..."
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f $service
    fi
}

# 健康检查
health_check() {
    log_info "执行健康检查..."
    
    # 检查ElasticSearch
    if curl -f http://localhost:9200/_cluster/health &> /dev/null; then
        log_info "✓ ElasticSearch 运行正常"
    else
        log_error "✗ ElasticSearch 健康检查失败"
    fi
    
    # 检查SkyWalking OAP
    if curl -f http://localhost:12800/internal/l7check &> /dev/null; then
        log_info "✓ SkyWalking OAP 运行正常"
    else
        log_error "✗ SkyWalking OAP 健康检查失败"
    fi
    
    # 检查SkyWalking UI
    if curl -f http://localhost:8080 &> /dev/null; then
        log_info "✓ SkyWalking UI 运行正常"
    else
        log_error "✗ SkyWalking UI 健康检查失败"
    fi
}

# 清理数据
clean_data() {
    log_warn "这将删除所有 SkyWalking 数据，确定要继续吗? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        log_info "清理 SkyWalking 数据..."
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down -v
        docker volume rm ${PROJECT_NAME}_skywalking_elasticsearch_data 2>/dev/null || true
        log_info "数据清理完成"
    else
        log_info "取消清理操作"
    fi
}

# 备份数据
backup_data() {
    local backup_dir="./backup/$(date +%Y%m%d_%H%M%S)"
    log_info "备份 SkyWalking 数据到 $backup_dir..."
    
    mkdir -p "$backup_dir"
    
    # 备份ElasticSearch数据（需要实现具体的备份逻辑）
    log_warn "数据备份功能需要根据实际需求实现"
    
    log_info "备份完成"
}

# 使用说明
show_usage() {
    echo "SkyWalking 部署管理脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 start          启动所有服务"
    echo "  $0 stop           停止所有服务"
    echo "  $0 restart        重启所有服务"
    echo "  $0 status         显示服务状态"
    echo "  $0 logs [service] 查看日志"
    echo "  $0 health         健康检查"
    echo "  $0 clean          清理数据"
    echo "  $0 backup         备份数据"
    echo "  $0 help           显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 start          # 启动所有服务"
    echo "  $0 logs oap-server # 查看 OAP 服务日志"
    echo "  $0 health         # 执行健康检查"
}

# 主函数
main() {
    case "${1:-help}" in
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        health)
            health_check
            ;;
        clean)
            clean_data
            ;;
        backup)
            backup_data
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            log_error "未知命令: $1"
            show_usage
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"