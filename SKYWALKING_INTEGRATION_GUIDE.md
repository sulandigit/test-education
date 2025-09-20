# SkyWalking APM 集成指南

## 目录

1. [概述](#概述)
2. [集成架构](#集成架构)
3. [环境要求](#环境要求)
4. [快速开始](#快速开始)
5. [详细配置](#详细配置)
6. [监控面板](#监控面板)
7. [告警配置](#告警配置)
8. [性能优化](#性能优化)
9. [故障排除](#故障排除)
10. [最佳实践](#最佳实践)

## 概述

本文档描述了如何在领课教育系统中集成Apache SkyWalking APM监控解决方案。SkyWalking是一个开源的应用性能监控系统，专为微服务、云原生和容器化应用而设计。

### 集成内容

- ✅ 全链路追踪监控
- ✅ 性能指标收集
- ✅ 服务拓扑图展示
- ✅ 告警与通知
- ✅ 容器化部署
- ✅ 自动化运维脚本

### 支持的服务

- **网关服务** (education-gateway) - 端口7700
- **系统服务** (education-service-system) - 端口7710
- **用户服务** (education-service-user) - 端口7720
- **课程服务** (education-service-course) - 端口7730

## 集成架构

```
┌─────────────────┐    ┌─────────────────┐
│   前端门户      │    │   后台管理      │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────┬───────────┘
                     │
          ┌─────────────────┐
          │   API Gateway   │ ◄──┐
          └─────────┬───────┘    │
                    │            │
        ┌───────────┼───────────┐│
        │           │           ││
   ┌────▼───┐  ┌───▼───┐  ┌───▼▼──┐
   │System  │  │ User  │  │Course │
   │Service │  │Service│  │Service│
   └────────┘  └───────┘  └───────┘
              │
              │ SkyWalking Agent
              ▼
   ┌─────────────────┐
   │ SkyWalking OAP  │
   └─────────┬───────┘
             │
   ┌─────────▼───────┐    ┌─────────────────┐
   │ ElasticSearch   │    │ SkyWalking UI   │
   └─────────────────┘    └─────────────────┘
```

## 环境要求

### 基础环境

- **操作系统**: Linux (推荐Ubuntu 18.04+)
- **Docker**: 20.10+
- **Docker Compose**: 1.29+
- **内存**: 最少4GB (推荐8GB+)
- **磁盘**: 最少20GB可用空间

### 软件版本

- **SkyWalking**: 8.16.0
- **ElasticSearch**: 7.17.9
- **Java**: OpenJDK 8
- **Spring Boot**: 2.6.3
- **Spring Cloud**: 2021.0.1

## 快速开始

### 1. 下载SkyWalking Agent

```bash
# 运行下载脚本
./download-skywalking-agent.sh

# 或手动下载并解压
wget https://archive.apache.org/dist/skywalking/8.16.0/apache-skywalking-apm-8.16.0.tar.gz
tar -xzf apache-skywalking-apm-8.16.0.tar.gz
cp -r apache-skywalking-apm-bin/agent/* ./skywalking-agent/
```

### 2. 启动SkyWalking监控系统

```bash
# 启动所有服务
./skywalking-deploy.sh start

# 查看服务状态
./skywalking-deploy.sh status

# 查看日志
./skywalking-deploy.sh logs
```

### 3. 访问监控面板

- **SkyWalking UI**: http://localhost:8080
- **ElasticSearch**: http://localhost:9200
- **网关服务**: http://localhost:7700

### 4. 运行集成测试

```bash
# 执行功能测试
./skywalking-integration-test.sh

# 执行性能测试
./skywalking-performance-test.sh
```

## 详细配置

### SkyWalking Agent配置

Agent配置文件位于 `skywalking-config/` 目录下：

```
skywalking-config/
├── agent.config              # 通用Agent配置
├── gateway-agent.config      # 网关服务配置
├── system-agent.config       # 系统服务配置
├── user-agent.config         # 用户服务配置
├── course-agent.config       # 课程服务配置
└── oap-config/
    ├── application.yml       # OAP服务器配置
    └── alarm-settings.yml    # 告警规则配置
```

#### 关键配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `agent.service_name` | ${SW_AGENT_NAME} | 服务名称标识 |
| `collector.backend_service` | oap-server:11800 | OAP服务器地址 |
| `agent.sample_n_per_3_secs` | 3 | 采样频率 |
| `logging.level` | INFO | 日志级别 |

### Docker Compose配置

主要的Docker Compose文件：

- `docker-compose-skywalking.yml` - 包含SkyWalking组件的完整配置
- `docker-compose.yml` - 原有的应用服务配置

#### 服务端口映射

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|----------|----------|------|
| ElasticSearch | 9200/9300 | 9200/9300 | 数据存储 |
| SkyWalking OAP | 11800/12800 | 11800/12800 | 数据收集处理 |
| SkyWalking UI | 8080 | 8080 | Web界面 |
| Gateway | 7700 | 7700 | API网关 |
| System Service | 7710 | 7710 | 系统服务 |
| User Service | 7720 | 7720 | 用户服务 |
| Course Service | 7730 | 7730 | 课程服务 |

### Maven依赖配置

在父POM中已添加SkyWalking依赖管理：

```xml
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-trace</artifactId>
    <version>8.16.0</version>
</dependency>
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-logback-1.x</artifactId>
    <version>8.16.0</version>
</dependency>
```

## 监控面板

### 主要监控视图

1. **仪表板 (Dashboard)**
   - 系统概览
   - 关键指标展示
   - 服务健康状态

2. **拓扑图 (Topology)**
   - 服务依赖关系
   - 调用量和健康状态
   - 实时性能指标

3. **追踪 (Trace)**
   - 请求链路详情
   - 性能瓶颈分析
   - 错误定位

4. **服务 (Service)**
   - 单个服务性能
   - 接口级监控
   - 实例状态

5. **数据库 (Database)**
   - SQL执行监控
   - 慢查询分析
   - 连接池状态

### 关键指标

| 指标类型 | 指标名称 | 描述 |
|----------|----------|------|
| 性能指标 | 平均响应时间 | 服务处理请求的平均时间 |
| 性能指标 | P95/P99响应时间 | 95%/99%的请求响应时间 |
| 吞吐量 | CPM (每分钟调用数) | 服务每分钟处理的请求数 |
| 可用性 | 成功率 | 成功请求占总请求的百分比 |
| 错误率 | 错误百分比 | 失败请求占总请求的百分比 |

## 告警配置

### 告警规则

系统预配置的告警规则包括：

1. **服务可用性告警** - 可用性低于80%
2. **响应时间告警** - 平均响应时间超过3秒
3. **P99响应时间告警** - P99响应时间超过5秒
4. **数据库慢查询告警** - 数据库响应时间超过1秒
5. **端点性能告警** - 端点平均响应时间超过2秒

### 告警通知配置

支持多种告警通知方式：

```yaml
# 在 alarm-settings.yml 中配置
webhooks:
  # 钉钉机器人
  - http://your-dingtalk-webhook-url
  
  # 企业微信机器人  
  - http://your-wechat-work-webhook-url

email:
  host: smtp.example.com
  port: 587
  username: your_email@example.com
  password: your_password
```

### 自定义告警规则

可以通过修改 `skywalking-config/oap-config/alarm-settings.yml` 添加自定义规则：

```yaml
rules:
  custom_rule_name:
    metrics-name: service_resp_time
    op: ">"
    threshold: 2000
    period: 2
    count: 3
    silence-period: 10
    message: "自定义告警消息"
```

## 性能优化

### Agent性能调优

1. **采样率优化**
   ```properties
   # 生产环境建议设置较低采样率
   agent.sample_n_per_3_secs=1  # 开发环境: 3-5, 生产环境: 1-2
   ```

2. **日志级别调整**
   ```properties
   # 生产环境使用WARN或ERROR级别
   logging.level=WARN
   ```

3. **忽略无关端点**
   ```properties
   # 忽略静态资源和健康检查
   agent.ignore_suffix=.jpg,.css,.js,/actuator/health,/actuator/info
   ```

### OAP服务器优化

1. **JVM内存配置**
   ```yaml
   environment:
     JAVA_OPTS: "-Xmx2048m -Xms2048m"
   ```

2. **数据保留策略**
   ```yaml
   core:
     default:
       recordDataTTL: 3    # 3天
       metricsDataTTL: 7   # 7天
   ```

### ElasticSearch优化

1. **内存配置**
   ```yaml
   environment:
     ES_JAVA_OPTS: "-Xms1g -Xmx1g"
   ```

2. **索引设置**
   ```yaml
   storage:
     elasticsearch:
       indexShardsNumber: 1
       indexReplicasNumber: 0
   ```

## 故障排除

### 常见问题

#### 1. Agent无法连接到OAP服务器

**症状**: 服务启动正常，但SkyWalking UI中看不到服务

**解决方案**:
```bash
# 检查网络连通性
docker exec gateway ping oap-server

# 检查OAP服务状态
./skywalking-deploy.sh health

# 查看Agent日志
docker logs gateway
```

#### 2. ElasticSearch启动失败

**症状**: ElasticSearch容器无法启动

**解决方案**:
```bash
# 检查系统内存
free -h

# 增加虚拟内存限制
sudo sysctl -w vm.max_map_count=262144

# 重启ElasticSearch
docker-compose restart elasticsearch
```

#### 3. 数据显示延迟

**症状**: 监控数据更新延迟较大

**解决方案**:
```bash
# 检查采样率配置
grep "sample" skywalking-config/*-agent.config

# 检查OAP处理能力
curl http://localhost:12800/internal/l7check

# 调整刷新间隔
# 在 application.yml 中修改 flushInterval
```

### 日志分析

#### Agent日志位置
- 容器内路径: `/skywalking-agent/logs/`
- 主机映射路径: `./skywalking-agent/logs/`

#### 关键日志文件
- `skywalking-api.log` - Agent运行日志
- `skywalking-api.error` - Agent错误日志

#### 常用日志查看命令
```bash
# 查看Agent日志
docker exec gateway tail -f /skywalking-agent/logs/skywalking-api.log

# 查看OAP日志
./skywalking-deploy.sh logs oap-server

# 查看所有服务日志
./skywalking-deploy.sh logs
```

### 性能问题诊断

#### 内存使用过高
```bash
# 查看容器内存使用
docker stats

# 调整Agent内存配置
# 在相应的agent.config中添加:
# agent.class_cache_mode=MEMORY
```

#### CPU使用率过高
```bash
# 查看容器CPU使用
docker stats --format "table {{.Container}}\t{{.CPUPerc}}"

# 降低采样率
# 修改 agent.sample_n_per_3_secs 参数
```

## 最佳实践

### 开发环境

1. **高采样率配置**
   ```properties
   agent.sample_n_per_3_secs=5
   logging.level=DEBUG
   ```

2. **快速数据清理**
   ```bash
   # 定期清理测试数据
   ./skywalking-deploy.sh clean
   ```

### 测试环境

1. **中等采样率配置**
   ```properties
   agent.sample_n_per_3_secs=3
   logging.level=INFO
   ```

2. **性能基准测试**
   ```bash
   # 执行性能测试
   ./skywalking-performance-test.sh -c 20 -d 300
   ```

### 生产环境

1. **低采样率配置**
   ```properties
   agent.sample_n_per_3_secs=1
   logging.level=WARN
   plugin.jdbc.trace_sql_parameters=false
   ```

2. **监控和告警**
   - 配置关键业务指标告警
   - 设置合理的告警阈值
   - 建立告警处理流程

3. **数据管理**
   ```bash
   # 定期备份重要数据
   ./skywalking-deploy.sh backup
   
   # 监控存储空间使用
   df -h
   docker system df
   ```

### 安全配置

1. **网络安全**
   ```yaml
   # 在生产环境中限制端口访问
   ports:
     - "127.0.0.1:8080:8080"  # 仅本地访问SkyWalking UI
   ```

2. **认证配置**
   ```properties
   # 启用Agent认证（如果需要）
   agent.authentication=your-secret-token
   ```

### 扩展性考虑

1. **多环境部署**
   - 为不同环境配置独立的SkyWalking实例
   - 使用不同的服务名称前缀区分环境

2. **集群部署**
   - 配置OAP集群模式
   - 使用外部的ElasticSearch集群
   - 配置负载均衡

## 管理脚本使用

### 部署管理脚本

```bash
# 启动所有服务
./skywalking-deploy.sh start

# 停止所有服务
./skywalking-deploy.sh stop

# 重启服务
./skywalking-deploy.sh restart

# 查看服务状态
./skywalking-deploy.sh status

# 健康检查
./skywalking-deploy.sh health

# 查看特定服务日志
./skywalking-deploy.sh logs oap-server

# 清理所有数据
./skywalking-deploy.sh clean
```

### 测试脚本

```bash
# 功能集成测试
./skywalking-integration-test.sh

# 性能测试（自定义参数）
./skywalking-performance-test.sh -c 50 -d 300

# 详细性能测试
./skywalking-performance-test.sh --verbose
```

## 技术支持

### 官方资源

- [SkyWalking官方文档](https://skywalking.apache.org/docs/)
- [GitHub仓库](https://github.com/apache/skywalking)
- [社区论坛](https://github.com/apache/skywalking/discussions)

### 故障报告

如遇到问题，请提供以下信息：

1. 系统环境信息 (`uname -a`)
2. Docker版本 (`docker --version`)
3. 相关日志文件
4. 错误截图或详细描述
5. 复现步骤

---

**更新时间**: 2024-01-20  
**版本**: v1.0  
**维护者**: 领课教育技术团队