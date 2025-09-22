# 分库分表实施文档

## 1. 概述

本文档描述了领课教育在线系统的分库分表实施方案，基于Apache ShardingSphere实现水平分库分表，提升系统的并发处理能力和数据存储扩展性。

## 2. 架构设计

### 2.1 整体架构

```
应用层 (Spring Boot微服务)
    ↓
ShardingSphere JDBC核心
    ↓
分片路由引擎
    ↓
多数据源连接池 (HikariCP)
    ↓
分片数据库集群
```

### 2.2 分片策略

#### 用户域分片
- **分库策略**: 4个库，按 `user_id % 4` 分库
- **分表策略**: 每库16张表，按 `user_id % 16` 分表
- **涉及表**: users, lecturer, order_info

#### 课程域分片
- **分库策略**: 2个库，按 `course_id % 2` 分库
- **分表策略**: 每库16张表，按 `course_id % 16` 分表
- **涉及表**: course, course_chapter, course_chapter_period, user_course, user_study

#### 系统域分片
- **策略**: 不分片，使用广播表
- **涉及表**: sys_config, sys_user, region等

## 3. 实施步骤

### 3.1 环境准备

#### 3.1.1 数据库准备

**用户库创建脚本：**
```sql
-- 创建用户分库
CREATE DATABASE os_user_0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE os_user_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE os_user_2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE os_user_3 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建课程分库
CREATE DATABASE os_course_0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE os_course_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 系统库（不分片）
CREATE DATABASE os_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**分表创建脚本示例（以users表为例）：**
```sql
-- 在每个用户库中创建16张users分表
-- user_db_0库中
CREATE TABLE users_0 LIKE users;
CREATE TABLE users_1 LIKE users;
-- ... 创建到users_15

-- 在每个用户库中创建32张order_info分表
CREATE TABLE order_info_0 LIKE order_info;
CREATE TABLE order_info_1 LIKE order_info;
-- ... 创建到order_info_31
```

#### 3.1.2 依赖配置

在主pom.xml中添加ShardingSphere依赖：
```xml
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
    <version>5.3.2</version>
</dependency>
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-transaction-xa-core</artifactId>
    <version>5.3.2</version>
</dependency>
```

### 3.2 配置文件

#### 3.2.1 用户服务分片配置

配置文件：`roncoo-education-service-user/src/main/resources/sharding-databases.yaml`

关键配置项：
- 数据源配置：4个主库 + 4个从库
- 分片算法：用户分库、用户分表、订单分表
- 读写分离：主从读写分离配置
- 分布式事务：XA事务支持

#### 3.2.2 课程服务分片配置

配置文件：`roncoo-education-service-course/src/main/resources/sharding-databases.yaml`

关键配置项：
- 数据源配置：2个主库 + 2个从库
- 分片算法：课程分库、课程分表
- 用户课程关联：按user_id分片以支持用户维度查询

#### 3.2.3 系统服务配置

配置文件：`roncoo-education-service-system/src/main/resources/sharding-databases.yaml`

关键配置项：
- 单数据源配置
- 广播表配置
- 读写分离配置

### 3.3 代码实现

#### 3.3.1 分片算法类

实现了以下自定义分片算法：
- `UserDatabaseShardingAlgorithm`: 用户分库算法
- `UserTableShardingAlgorithm`: 用户分表算法
- `CourseDatabaseShardingAlgorithm`: 课程分库算法
- `CourseTableShardingAlgorithm`: 课程分表算法

#### 3.3.2 工具类

`ShardingUtils`: 提供分片计算相关的工具方法：
- 分库索引计算
- 分表索引计算
- 数据库名称构造
- 表名称构造
- 分片键验证

#### 3.3.3 监控组件

- `ShardingMonitor`: 性能监控器
- `ShardingLogInterceptor`: SQL路由日志拦截器
- `ShardingMonitorController`: 监控API接口

## 4. 使用指南

### 4.1 开发规范

#### 4.1.1 SQL编写规范

**推荐的查询方式：**
```java
// ✅ 推荐：包含分片键的查询
Users user = usersDao.getById(userId);  // 根据用户ID查询

// ✅ 推荐：同分片内的批量查询
List<OrderInfo> orders = orderInfoDao.getByUserId(userId);

// ❌ 避免：不包含分片键的查询
List<Users> allUsers = usersDao.getAllUsers(); // 会触发全表扫描
```

**分片键使用规范：**
- 用户相关操作必须包含 `user_id` 作为查询条件
- 课程相关操作必须包含 `course_id` 作为查询条件
- 避免跨分片的JOIN查询

#### 4.1.2 事务处理

**本地事务（推荐）：**
```java
@Transactional
public void createUser(Users user) {
    // 同一分片内的操作，使用本地事务
    usersDao.save(user);
    // 其他同分片操作...
}
```

**分布式事务（谨慎使用）：**
```java
@Transactional
@ShardingTransactionType(TransactionType.XA)
public void createUserAndOrder(Users user, OrderInfo order) {
    // 跨分片操作，使用XA分布式事务
    usersDao.save(user);
    orderInfoDao.save(order);
}
```

### 4.2 查询优化

#### 4.2.1 单分片查询

```java
// 按用户ID查询用户信息（单分片）
public Users getUserById(Long userId) {
    return usersDao.getById(userId);
}

// 按用户ID查询订单列表（单分片）
public List<OrderInfo> getUserOrders(Long userId) {
    return orderInfoDao.getByUserId(userId);
}
```

#### 4.2.2 批量查询优化

```java
// 批量查询时按分片键分组
public Map<Long, Users> getUsersByIds(List<Long> userIds) {
    // 按分片分组
    Map<Integer, List<Long>> shardingGroups = userIds.stream()
        .collect(Collectors.groupingBy(id -> ShardingUtils.calculateUserDatabaseIndex(id, 4)));
    
    Map<Long, Users> result = new HashMap<>();
    for (List<Long> ids : shardingGroups.values()) {
        List<Users> users = usersDao.getByIds(ids);
        users.forEach(user -> result.put(user.getId(), user));
    }
    return result;
}
```

#### 4.2.3 范围查询

```java
// 范围查询需要指定分片键范围
public List<Users> getUsersByIdRange(Long startId, Long endId) {
    // 计算涉及的分片
    int[] shardingIndexes = ShardingUtils.getTableIndexRange(startId, endId, 16);
    
    List<Users> result = new ArrayList<>();
    for (int index : shardingIndexes) {
        List<Users> users = usersDao.getByIdRangeInShard(startId, endId, index);
        result.addAll(users);
    }
    return result;
}
```

### 4.3 性能监控

#### 4.3.1 监控API

访问监控接口获取性能数据：
```bash
# 获取整体性能统计
GET /sharding/monitor/stats

# 获取查询性能统计  
GET /sharding/monitor/query-stats

# 获取系统健康状态
GET /sharding/monitor/health

# 获取性能建议
GET /sharding/monitor/recommendations
```

#### 4.3.2 日志监控

**SQL路由日志：**
```
logs/sharding-sql.log - 记录SQL路由信息
logs/slow-sql.log - 记录慢SQL（>1秒）
logs/sharding-monitor.log - 记录性能监控数据
```

**关键监控指标：**
- 平均查询响应时间 < 100ms
- 平均插入响应时间 < 200ms
- 跨库查询比例 < 5%
- 数据库连接使用率 < 80%

### 4.4 故障处理

#### 4.4.1 常见问题

**问题1：分片键为null导致路由失败**
```
错误：Sharding value can't be null in SQL
解决：确保查询条件包含有效的分片键
```

**问题2：跨分片JOIN查询失败**
```
错误：Cross database join is not supported
解决：优化查询逻辑，避免跨分片JOIN
```

**问题3：分布式事务超时**
```
错误：XA transaction timeout
解决：减少事务范围或增加超时时间配置
```

#### 4.4.2 性能优化建议

1. **索引优化**：确保分片键和常用查询字段有适当索引
2. **批量操作**：使用批量插入/更新减少网络开销
3. **连接池调优**：根据并发量调整连接池大小
4. **读写分离**：查询操作使用从库，写操作使用主库
5. **缓存策略**：对热点数据使用Redis缓存

### 4.5 扩容方案

#### 4.5.1 垂直扩容

提升单库性能：
- 增加CPU和内存
- 使用SSD存储
- 优化数据库参数

#### 4.5.2 水平扩容

增加分片数量：
```yaml
# 从4个库扩展到8个库
spring:
  shardingsphere:
    rules:
      sharding:
        sharding-algorithms:
          user-database-inline:
            props:
              algorithm-expression: user-db-$->{user_id % 8}  # 改为8
```

**扩容步骤：**
1. 创建新的分片库
2. 修改分片算法配置
3. 数据迁移和重平衡
4. 测试验证
5. 切换生产环境

## 5. 测试验证

### 5.1 单元测试

运行分片算法单元测试：
```bash
mvn test -Dtest=*ShardingAlgorithmTest
mvn test -Dtest=ShardingUtilsTest
```

### 5.2 集成测试

运行分库分表集成测试：
```bash
mvn test -Dtest=ShardingIntegrationTest
```

### 5.3 性能测试

使用JMeter进行压力测试：
- 并发用户数：1000
- 测试时长：30分钟
- 目标TPS：10000

## 6. 运维监控

### 6.1 关键指标

| 指标类别 | 监控指标 | 正常范围 | 告警阈值 |
|---------|---------|---------|----------|
| 响应时间 | 查询平均耗时 | <100ms | >500ms |
| 吞吐量 | QPS | 根据业务需求 | 下降50% |
| 错误率 | SQL执行错误率 | <0.01% | >0.1% |
| 资源使用 | 数据库连接使用率 | <80% | >90% |

### 6.2 告警配置

配置Prometheus + Grafana监控告警：
```yaml
# prometheus.yml
- job_name: 'sharding-monitor'
  static_configs:
    - targets: ['localhost:8080']
  metrics_path: '/actuator/prometheus'
```

### 6.3 日常维护

**每日检查项：**
- [ ] 查看慢SQL日志
- [ ] 检查数据库连接状态
- [ ] 监控跨库查询比例
- [ ] 验证主从同步状态

**每周检查项：**
- [ ] 分析性能趋势
- [ ] 检查磁盘空间使用
- [ ] 验证备份策略
- [ ] 评估扩容需求

## 7. 注意事项

### 7.1 限制和约束

1. **JOIN限制**：不支持跨分片的JOIN查询
2. **事务限制**：跨分片事务性能较差，应谨慎使用
3. **聚合限制**：跨分片的SUM、COUNT等需要应用层聚合
4. **唯一约束**：分片键之外的全局唯一约束难以保证

### 7.2 最佳实践

1. **分片键选择**：选择分布均匀、查询频繁的字段
2. **避免热点**：避免大量数据集中在少数分片
3. **逐步推进**：先在测试环境充分验证再上生产
4. **监控先行**：完善监控体系，及时发现问题
5. **文档更新**：及时更新配置和操作文档

## 8. 附录

### 8.1 配置文件模板

参考项目中的配置文件：
- `sharding-databases.yaml` - 分片配置
- `application.yml` - 应用配置
- `logback-sharding.xml` - 日志配置

### 8.2 脚本工具

- 数据库创建脚本
- 分表创建脚本  
- 数据迁移脚本
- 性能测试脚本

### 8.3 故障处理手册

详细的故障排查和处理流程，包括：
- 常见错误及解决方案
- 性能问题排查步骤
- 紧急恢复流程
- 联系方式和升级路径