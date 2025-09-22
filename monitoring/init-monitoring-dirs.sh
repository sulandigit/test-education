#!/bin/bash

# 领课教育监控系统数据目录初始化脚本
# 创建监控服务所需的数据存储目录

echo "🚀 初始化领课教育监控系统数据目录..."

# 创建监控数据根目录
sudo mkdir -p /data/monitoring/{prometheus,grafana,alertmanager}

# 设置目录权限
echo "📁 设置目录权限..."
sudo chown -R 65534:65534 /data/monitoring/prometheus  # nobody用户，Prometheus容器使用
sudo chown -R 472:472 /data/monitoring/grafana        # grafana用户
sudo chown -R 65534:65534 /data/monitoring/alertmanager # nobody用户

# 设置目录权限
sudo chmod -R 755 /data/monitoring

echo "✅ 监控数据目录初始化完成!"
echo "📂 Prometheus 数据目录: /data/monitoring/prometheus"
echo "📂 Grafana 数据目录: /data/monitoring/grafana"
echo "📂 AlertManager 数据目录: /data/monitoring/alertmanager"

echo ""
echo "📋 下一步操作:"
echo "1. 启动监控服务: docker-compose -f monitoring/docker-compose.monitoring.yml up -d"
echo "2. 访问 Grafana: http://localhost:3000 (admin/education123!)"
echo "3. 访问 Prometheus: http://localhost:9090"
echo "4. 访问 AlertManager: http://localhost:9093"