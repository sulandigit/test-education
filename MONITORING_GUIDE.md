# 领课教育系统 Prometheus 监控部署指南

## 概述

本文档提供了领课教育系统 Prometheus 监控系统的完整部署和使用指南。监控系统包含以下组件：

- **Prometheus**: 指标收集和存储
- **Grafana**: 数据可视化和仪表板
- **AlertManager**: 告警管理和通知
- **Node Exporter**: 系统资源监控
- **钉钉通知服务**: 告警通知集成

## 架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Gateway       │    │   User Service  │    │  Course Service │
│   端口: 7700    │    │   端口: 7720    │    │   端口: 7730    │
└─────┬───────────┘    └─────┬───────────┘    └─────┬───────────┘
      │                      │                      │
      │ /actuator/prometheus │ /actuator/prometheus │ /actuator/prometheus
      │                      │                      │
      └──────────────────────┼──────────────────────┘
                             │
                    ┌────────▼────────┐
                    │   Prometheus    │
                    │   端口: 9090    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
     ┌────────▼────────┐ ┌───▼────┐ ┌──────▼──────┐
     │    Grafana      │ │AlertMgr│ │Node Exporter│
     │   端口: 3000    │ │端口:9093│ │ 端口: 9100  │
     └─────────────────┘ └───┬────┘ └─────────────┘
                             │
                    ┌────────▼────────┐
                    │钉钉通知服务      │
                    │   端口: 8080    │
                    └─────────────────┘
```

## 快速开始

### 1. 环境准备

确保你的系统满足以下要求：

- **操作系统**: Linux (推荐 Ubuntu 20.04+)
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **内存**: 至少 8GB
- **磁盘空间**: 至少 50GB 可用空间

### 2. 克隆项目

```bash
git clone <项目地址>
cd test-education
```

### 3. 初始化监控数据目录

```bash
# 运行数据目录初始化脚本
chmod +x monitoring/init-monitoring-dirs.sh
sudo ./monitoring/init-monitoring-dirs.sh
```

### 4. 配置环境变量

```bash
# 复制环境变量模板
cp monitoring/.env.example monitoring/.env

# 编辑配置文件
nano monitoring/.env
```

配置钉钉机器人 Webhook（可选）：

```bash
# 设置钉钉机器人配置
export DINGTALK_WEBHOOK_URL="https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN"
export DINGTALK_SECRET="YOUR_SECRET"
```

### 5. 部署监控系统

有两种部署方式可选：

#### 方式一：仅部署监控基础设施

```bash
# 启动监控服务
cd monitoring
docker-compose -f docker-compose.monitoring.yml up -d

# 查看服务状态
docker-compose -f docker-compose.monitoring.yml ps
```

#### 方式二：完整部署（应用 + 监控）

```bash
# 启动完整系统（包含应用和监控）
docker-compose -f docker-compose.complete.yml up -d

# 查看服务状态
docker-compose -f docker-compose.complete.yml ps
```

### 6. 验证部署

检查各个服务是否正常运行：

```bash
# 检查 Prometheus
curl http://localhost:9090/api/v1/query?query=up

# 检查 Grafana
curl http://localhost:3000/api/health

# 检查 AlertManager
curl http://localhost:9093/api/v1/status

# 检查钉钉通知服务
curl http://localhost:8080/
```

## 访问地址

| 服务 | 访问地址 | 默认账号 | 说明 |
|------|----------|----------|------|
| Grafana | http://localhost:3000 | admin/education123! | 监控仪表板 |
| Prometheus | http://localhost:9090 | 无 | 指标查询界面 |
| AlertManager | http://localhost:9093 | 无 | 告警管理界面 |

## 监控指标说明

### 系统指标

| 指标名称 | 类型 | 说明 |
|---------|------|------|
| `up` | Gauge | 服务可用性 (1=正常, 0=异常) |
| `http_server_requests_seconds_count` | Counter | HTTP请求总数 |
| `http_server_requests_seconds_sum` | Counter | HTTP请求总耗时 |
| `jvm_memory_used_bytes` | Gauge | JVM内存使用量 |
| `node_cpu_seconds_total` | Counter | CPU使用时间 |
| `node_memory_MemAvailable_bytes` | Gauge | 可用内存 |

### 业务指标

| 指标名称 | 类型 | 说明 |
|---------|------|------|
| `education_user_register_total` | Counter | 用户注册总数 |
| `education_user_login_total` | Counter | 用户登录总数 |
| `education_user_active_count` | Gauge | 当前活跃用户数 |
| `education_course_play_total` | Counter | 课程播放总次数 |
| `education_course_play_error_total` | Counter | 课程播放错误次数 |
| `education_payment_total` | Counter | 支付请求总数 |
| `education_payment_success_total` | Counter | 支付成功总数 |

## 告警配置

### 告警级别

- **Critical**: 严重告警，需要立即处理
- **Warning**: 警告告警，需要关注
- **Info**: 信息告警，仅作记录

### 告警规则

#### 系统级告警

- **服务宕机**: 服务实例无法访问超过2分钟
- **CPU高负载**: CPU使用率超过80%持续5分钟
- **内存不足**: 内存使用率超过85%持续5分钟
- **磁盘空间**: 磁盘使用率超过90%
- **接口异常**: 接口错误率超过5%持续3分钟

#### 业务级告警

- **支付成功率下降**: 支付成功率低于95%持续10分钟
- **用户注册异常**: 1小时内无用户注册
- **课程播放异常**: 课程播放错误率超过10%持续3分钟

### 通知配置

告警通知支持以下渠道：

1. **邮件通知**: 发送到指定邮箱
2. **钉钉通知**: 发送到钉钉群聊
3. **短信通知**: 发送到手机（需配置短信服务）

## 使用指南

### Grafana 仪表板

#### 系统概览仪表板

- **服务状态**: 显示各微服务的健康状态
- **API请求率**: 显示接口调用频率
- **系统资源**: CPU、内存、磁盘使用情况
- **响应时间**: API响应时间分布
- **错误率**: 系统错误率监控

#### 业务监控仪表板

- **用户指标**: 注册数、活跃用户、登录趋势
- **课程指标**: 播放次数、学习时长、课程类型分布
- **订单指标**: 订单量、支付成功率
- **业务健康度**: 关键业务指标的健康状态

### Prometheus 查询

#### 常用查询语句

```promql
# 服务可用性
up{job=~"education.*"}

# API请求率
rate(http_server_requests_seconds_count[5m])

# 95%响应时间
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# 错误率
rate(http_server_requests_seconds_count{status=~"4..|5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) * 100

# CPU使用率
100 - (avg by (instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# 内存使用率
(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100

# 用户注册速率
rate(education_user_register_total[1h])

# 支付成功率
(education_payment_success_total / education_payment_total) * 100
```

## 运维管理

### 日常维护

#### 1. 检查服务状态

```bash
# 查看所有监控服务状态
docker-compose -f monitoring/docker-compose.monitoring.yml ps

# 查看服务日志
docker-compose -f monitoring/docker-compose.monitoring.yml logs -f prometheus
docker-compose -f monitoring/docker-compose.monitoring.yml logs -f grafana
docker-compose -f monitoring/docker-compose.monitoring.yml logs -f alertmanager
```

#### 2. 备份数据

```bash
# 备份 Prometheus 数据
sudo tar -czf prometheus-backup-$(date +%Y%m%d).tar.gz /data/monitoring/prometheus

# 备份 Grafana 配置
sudo tar -czf grafana-backup-$(date +%Y%m%d).tar.gz /data/monitoring/grafana

# 备份 AlertManager 数据
sudo tar -czf alertmanager-backup-$(date +%Y%m%d).tar.gz /data/monitoring/alertmanager
```

#### 3. 更新配置

```bash
# 重新加载 Prometheus 配置
curl -X POST http://localhost:9090/-/reload

# 重启 AlertManager
docker-compose -f monitoring/docker-compose.monitoring.yml restart alertmanager

# 重启 Grafana
docker-compose -f monitoring/docker-compose.monitoring.yml restart grafana
```

### 性能优化

#### 1. Prometheus 优化

```yaml
# prometheus.yml 优化配置
global:
  scrape_interval: 30s  # 调整采集间隔
  evaluation_interval: 30s

# 存储优化
storage:
  tsdb:
    retention.time: 30d  # 数据保留时间
    wal-compression: true  # 启用WAL压缩
```

#### 2. 指标过滤

```yaml
# 在 prometheus.yml 中添加指标过滤
scrape_configs:
  - job_name: 'education-gateway'
    static_configs:
      - targets: ['gateway:7700']
    metric_relabel_configs:
      # 过滤掉不需要的指标
      - source_labels: [__name__]
        regex: 'jvm_gc_memory_allocated_bytes_total|jvm_gc_memory_promoted_bytes_total'
        action: drop
```

#### 3. 告警优化

```yaml
# 设置告警抑制规则
inhibit_rules:
  - source_match:
      alertname: 'ServiceDown'
    target_match_re:
      alertname: '(HighErrorRate|HighResponseTime)'
    equal: ['instance']
```

## 故障排除

### 常见问题

#### 1. 服务无法启动

**问题**: 监控服务无法启动

**解决方案**:
```bash
# 检查端口占用
netstat -tlnp | grep -E '(9090|3000|9093|9100|8080)'

# 检查数据目录权限
sudo chown -R 65534:65534 /data/monitoring/prometheus
sudo chown -R 472:472 /data/monitoring/grafana

# 检查Docker日志
docker logs education-prometheus
docker logs education-grafana
```

#### 2. 指标采集异常

**问题**: Prometheus 无法采集到应用指标

**解决方案**:
```bash
# 检查应用健康状态
curl http://localhost:7700/actuator/health
curl http://localhost:7720/actuator/prometheus

# 检查网络连通性
docker network ls
docker network inspect education

# 检查 Prometheus 配置
curl http://localhost:9090/api/v1/targets
```

#### 3. 告警不触发

**问题**: 告警规则不生效

**解决方案**:
```bash
# 检查告警规则状态
curl http://localhost:9090/api/v1/rules

# 检查 AlertManager 配置
curl http://localhost:9093/api/v1/status

# 检查告警历史
curl http://localhost:9093/api/v1/alerts
```

#### 4. Grafana 仪表板异常

**问题**: 仪表板显示无数据

**解决方案**:
```bash
# 检查数据源连接
curl http://localhost:3000/api/datasources

# 检查查询语句
# 在 Prometheus 界面执行相同的查询语句

# 重新导入仪表板
# 通过 Grafana 界面重新导入 JSON 配置
```

### 监控指标异常

#### 高内存使用

```bash
# 查看内存使用情况
docker stats

# 调整JVM参数
# 在 docker-compose.yml 中修改 JAVA_OPTS
JAVA_OPTS: '-Xmn512M -Xmx1G -XX:+UseG1GC'
```

#### 磁盘空间不足

```bash
# 清理旧数据
# 调整 Prometheus 数据保留时间
docker exec education-prometheus promtool tsdb analyze /prometheus

# 清理 Docker 镜像和容器
docker system prune -a
```

## 安全考虑

### 访问控制

1. **Grafana 安全**:
   - 修改默认管理员密码
   - 启用 HTTPS 访问
   - 配置用户权限和角色

2. **Prometheus 安全**:
   - 限制网络访问
   - 启用基本认证
   - 配置防火墙规则

3. **数据安全**:
   - 定期备份监控数据
   - 加密敏感配置信息
   - 监控系统访问日志

### 网络安全

```bash
# 配置防火墙规则
sudo ufw allow from <trusted_ip> to any port 9090
sudo ufw allow from <trusted_ip> to any port 3000
sudo ufw allow from <trusted_ip> to any port 9093
```

## 扩展和定制

### 添加自定义指标

1. **在应用代码中添加指标**:

```java
@Autowired
private BusinessMetricsCollector metricsCollector;

// 记录业务指标
metricsCollector.recordUserRegister();
metricsCollector.recordCoursePlay("video");
```

2. **添加新的告警规则**:

在 `monitoring/prometheus/rules/alerts.yml` 中添加：

```yaml
- alert: CustomBusinessAlert
  expr: custom_metric > threshold
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "自定义业务告警"
    description: "自定义指标超过阈值"
```

3. **创建新的仪表板**:

使用 Grafana 界面创建新的仪表板，或编辑 JSON 配置文件。

### 集成其他监控组件

1. **添加 Jaeger 链路追踪**
2. **集成 ELK 日志分析**
3. **添加 Blackbox Exporter 黑盒监控**

## 总结

本监控系统为领课教育平台提供了全面的可观测性解决方案：

✅ **完整的监控覆盖**: 从基础设施到业务指标的全方位监控  
✅ **实时告警通知**: 多渠道告警通知确保及时响应  
✅ **可视化仪表板**: 直观的数据展示和分析  
✅ **自动化部署**: 一键部署和配置管理  
✅ **高可用设计**: 监控系统本身的高可用保障  

通过这套监控系统，运维团队可以：
- 实时掌握系统运行状态
- 快速定位和解决问题
- 分析系统性能趋势
- 支持业务决策制定

如有问题或需要支持，请联系运维团队。