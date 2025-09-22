# 领课教育系统监控集成更新

## 📊 新增监控功能

本次更新为领课教育系统集成了完整的 Prometheus 监控解决方案，提供全方位的系统可观测性。

### 🚀 监控特性

- ✅ **实时监控**: 系统性能和业务指标实时采集
- ✅ **智能告警**: 多级告警规则和自动通知
- ✅ **可视化仪表板**: Grafana 丰富的图表展示
- ✅ **业务指标**: 用户注册、课程学习、订单支付等关键业务数据
- ✅ **系统指标**: CPU、内存、网络、JVM 等基础设施监控
- ✅ **告警通知**: 邮件、钉钉等多渠道通知支持

### 📁 新增文件结构

```
monitoring/                          # 监控系统根目录
├── docker-compose.monitoring.yml    # 监控服务 Docker Compose 配置
├── init-monitoring-dirs.sh          # 数据目录初始化脚本
├── .env.example                     # 环境变量配置模板
├── Dockerfile.dingtalk              # 钉钉通知服务镜像
├── dingtalk-webhook.py              # 钉钉通知服务代码
├── prometheus/                      # Prometheus 配置
│   ├── prometheus.yml               # Prometheus 主配置文件
│   └── rules/
│       └── alerts.yml               # 告警规则配置
├── alertmanager/                    # AlertManager 配置
│   └── alertmanager.yml             # 告警管理配置
└── grafana/                         # Grafana 配置
    ├── provisioning/                # 自动配置
    │   ├── datasources/
    │   │   └── prometheus.yml       # 数据源配置
    │   └── dashboards/
    │       └── dashboard-config.yml # 仪表板配置
    └── dashboards/                  # 仪表板文件
        ├── system-overview.json     # 系统概览仪表板
        └── business-monitoring.json # 业务监控仪表板
```

### 🔧 应用代码集成

#### 监控配置文件

为各微服务添加了监控配置：

- `roncoo-education-gateway/src/main/resources/application-monitoring.yml`
- `roncoo-education-service-user/src/main/resources/application-monitoring.yml`
- `roncoo-education-service-system/src/main/resources/application-monitoring.yml`
- `roncoo-education-service-course/src/main/resources/application-monitoring.yml`

#### 业务指标收集

在公共模块中添加了业务指标收集器：

- `BusinessMetricsCollector.java` - 统一的业务指标收集器
- `MonitoringConfiguration.java` - 监控配置类
- `ApiMonitoringInterceptor.java` - API监控拦截器
- `WebMonitoringConfiguration.java` - Web监控配置

#### 依赖更新

在 `roncoo-education-common-service/pom.xml` 中添加了：

```xml
<!-- Micrometer Prometheus 监控 -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 📊 监控指标

#### 系统指标
- 服务可用性监控
- CPU、内存、磁盘使用率
- JVM 性能监控
- HTTP 接口性能和错误率
- 数据库连接池状态

#### 业务指标
- 用户注册和登录统计
- 课程播放次数和时长
- 订单创建和支付成功率
- 系统活跃用户数
- 业务错误率监控

### 🚨 告警规则

#### 系统级告警
- 服务宕机检测
- 资源使用率超限
- 接口性能异常
- 系统错误率过高

#### 业务级告警
- 支付成功率下降
- 用户注册异常
- 课程播放异常

### 🎯 快速开始

#### 1. 启动监控系统

```bash
# 方式一：使用快速启动脚本（推荐）
./start-monitoring.sh -m monitoring-only

# 方式二：手动启动
cd monitoring
./init-monitoring-dirs.sh
docker-compose -f docker-compose.monitoring.yml up -d
```

#### 2. 启动完整系统

```bash
# 启动应用 + 监控
./start-monitoring.sh -m complete

# 或使用 Docker Compose
docker-compose -f docker-compose.complete.yml up -d
```

#### 3. 访问监控界面

- **Grafana**: http://localhost:3000 (admin/education123!)
- **Prometheus**: http://localhost:9090
- **AlertManager**: http://localhost:9093

### 📖 文档

- 详细部署和使用指南: [MONITORING_GUIDE.md](MONITORING_GUIDE.md)
- 快速启动脚本帮助: `./start-monitoring.sh --help`

### 🛠️ 管理命令

```bash
# 查看服务状态
./start-monitoring.sh --status

# 查看服务日志
./start-monitoring.sh --logs prometheus

# 重启服务
./start-monitoring.sh --restart

# 停止服务
./start-monitoring.sh --stop
```

### ⚙️ 配置说明

#### 环境变量配置

复制并编辑环境变量配置：

```bash
cp monitoring/.env.example monitoring/.env
nano monitoring/.env
```

主要配置项：
- `DINGTALK_WEBHOOK_URL`: 钉钉机器人 Webhook 地址
- `DINGTALK_SECRET`: 钉钉机器人密钥
- `MONITORING_DATA_PATH`: 监控数据存储路径

#### 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| Grafana | 3000 | 监控仪表板 |
| Prometheus | 9090 | 指标查询服务 |
| AlertManager | 9093 | 告警管理 |
| Node Exporter | 9100 | 系统指标收集 |
| 钉钉通知服务 | 8080 | 告警通知服务 |

### 🔐 安全注意事项

1. **修改默认密码**: 请及时修改 Grafana 默认管理员密码
2. **网络访问控制**: 生产环境请配置防火墙规则限制访问
3. **敏感信息保护**: 妥善保管钉钉机器人等敏感配置信息

### 🎉 效果展示

监控系统提供：

1. **系统概览仪表板**
   - 服务健康状态一览
   - 系统资源使用情况
   - API 性能监控
   - 错误率趋势分析

2. **业务监控仪表板**
   - 用户行为分析
   - 课程学习统计
   - 订单和支付监控
   - 业务健康度指标

3. **实时告警通知**
   - 系统异常自动检测
   - 多渠道告警通知
   - 告警级别智能分类
   - 告警历史记录

通过这套监控系统，运维团队可以：
- 🔍 实时掌握系统运行状态
- ⚡ 快速定位和解决问题  
- 📈 分析系统性能趋势
- 💡 支持业务决策制定

---

**注意**: 本监控系统已经过测试验证，可直接用于生产环境。如有问题或建议，请联系开发团队。