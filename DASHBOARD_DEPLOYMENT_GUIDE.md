# 领课教育用户数据展示大屏系统部署文档

## 1. 概述

本文档详细说明了领课教育用户数据展示大屏系统的部署流程、配置要求和使用说明。

## 2. 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端大屏应用    │    │   Spring Gateway │    │  Dashboard服务   │
│   (Vue.js 3.x)  │◄──►│     (网关)       │◄──►│   (微服务)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                ▲                        ▲
                                │                        │
                       ┌─────────────────┐    ┌─────────────────┐
                       │   Nacos注册中心  │    │   Redis缓存      │
                       │   (服务发现)     │    │   (实时数据)     │
                       └─────────────────┘    └─────────────────┘
                                                        ▲
                                               ┌─────────────────┐
                                               │   MySQL数据库    │
                                               │   (业务数据)     │
                                               └─────────────────┘
```

## 3. 环境要求

### 3.1 基础环境
- **JDK**: 1.8 或以上版本
- **Node.js**: 14.x 或以上版本
- **Maven**: 3.6.0 或以上版本

### 3.2 中间件要求
- **MySQL**: 5.7 或以上版本
- **Redis**: 5.0 或以上版本  
- **Nacos**: 2.1.0 或以上版本

### 3.3 硬件要求
- **CPU**: 4核心或以上
- **内存**: 8GB 或以上
- **磁盘**: 100GB 可用空间
- **网络**: 千兆网络

## 4. 部署步骤

### 4.1 准备工作

1. **克隆项目代码**
```bash
git clone https://github.com/roncoo/roncoo-education.git
cd roncoo-education
```

2. **创建数据库**
```sql
CREATE DATABASE roncoo_education DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入基础数据**
```bash
mysql -u root -p roncoo_education < sql/roncoo_education.sql
```

### 4.2 配置Nacos

1. **启动Nacos服务**
```bash
cd nacos/bin
./startup.sh -m standalone
```

2. **添加配置文件**
登录Nacos控制台 (http://localhost:8848/nacos)，添加以下配置：

**配置ID**: `application-dev.yml`
**配置内容**: 
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/roncoo_education?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    database: 0
```

**配置ID**: `gateway.properties`
**配置内容**: 参考 `gateway-dashboard-routes.properties` 文件

### 4.3 部署后端服务

1. **编译项目**
```bash
mvn clean package -DskipTests
```

2. **启动网关服务**
```bash
cd roncoo-education-gateway/target
java -jar gateway.jar --spring.profiles.active=dev
```

3. **启动业务服务**
```bash
# 启动系统服务
cd roncoo-education-service/roncoo-education-service-system/target
java -jar system.jar --spring.profiles.active=dev

# 启动用户服务
cd roncoo-education-service/roncoo-education-service-user/target
java -jar user.jar --spring.profiles.active=dev

# 启动课程服务
cd roncoo-education-service/roncoo-education-service-course/target
java -jar course.jar --spring.profiles.active=dev

# 启动大屏服务
cd roncoo-education-service/roncoo-education-service-dashboard/target
java -jar dashboard.jar --spring.profiles.active=dev
```

### 4.4 部署前端应用

1. **安装依赖**
```bash
cd dashboard-frontend
npm install
```

2. **构建生产版本**
```bash
npm run build
```

3. **部署到Web服务器**
```bash
# 使用nginx部署
sudo cp -r dist/* /var/www/html/dashboard/
```

4. **配置nginx**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location /dashboard-ui/ {
        root /var/www/html;
        index index.html;
        try_files $uri $uri/ /dashboard/index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:7700;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /ws/ {
        proxy_pass http://localhost:7700;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

## 5. 配置说明

### 5.1 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/roncoo_education
    username: root
    password: your_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### 5.2 Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 10
    timeout: 30000
    jedis:
      pool:
        max-active: 50
        max-idle: 10
```

### 5.3 大屏服务配置
```yaml
server:
  port: 8084

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 6. 访问地址

- **大屏展示页面**: http://your-domain.com/dashboard-ui/
- **网关管理**: http://localhost:7700/
- **Nacos控制台**: http://localhost:8848/nacos/
- **API文档**: http://localhost:7700/swagger-ui.html

## 7. 监控和维护

### 7.1 健康检查
```bash
# 检查服务状态
curl http://localhost:8084/actuator/health

# 检查指标
curl http://localhost:8084/actuator/metrics
```

### 7.2 日志管理
```bash
# 查看服务日志
tail -f logs/dashboard.log

# 查看错误日志
grep ERROR logs/dashboard.log
```

### 7.3 性能监控
- **JVM监控**: 使用 JVisualVM 或 JProfiler
- **数据库监控**: 使用 MySQL Workbench
- **Redis监控**: 使用 Redis-cli monitor

## 8. 常见问题

### 8.1 服务启动失败
- 检查端口是否被占用
- 确认数据库连接配置正确
- 验证Nacos服务是否正常运行

### 8.2 前端页面无法访问
- 检查nginx配置是否正确
- 确认静态资源路径配置
- 验证代理转发是否正常

### 8.3 WebSocket连接失败
- 检查防火墙设置
- 确认WebSocket端点配置
- 验证网关路由配置

### 8.4 数据不更新
- 检查定时任务是否正常执行
- 确认缓存服务是否正常
- 验证数据源连接状态

## 9. 安全建议

1. **数据库安全**
   - 使用强密码
   - 限制访问IP
   - 定期备份数据

2. **Redis安全**
   - 设置访问密码
   - 绑定特定IP
   - 关闭危险命令

3. **应用安全**
   - 使用HTTPS协议
   - 配置请求限流
   - 启用访问日志

## 10. 性能优化

1. **数据库优化**
   - 添加必要索引
   - 优化查询语句
   - 配置连接池

2. **缓存优化**
   - 合理设置过期时间
   - 使用缓存预热
   - 监控缓存命中率

3. **前端优化**
   - 启用Gzip压缩
   - 配置CDN加速
   - 优化图表渲染

## 11. 技术支持

如遇到部署问题，请通过以下方式获取支持：

- **项目地址**: https://github.com/roncoo/roncoo-education
- **官方文档**: https://eduos.roncoo.net/doc
- **技术交流群**: 见README文档中的群信息

---

*文档版本: v1.0*  
*更新时间: 2025-09-20*