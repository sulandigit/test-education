# 分库分表快速入门指南

## 1. 快速开始

### 1.1 环境要求

- JDK 1.8+
- MySQL 5.7+
- Maven 3.6+
- Spring Boot 2.6.3

### 1.2 依赖配置

在主项目的 `pom.xml` 中已添加ShardingSphere依赖：

```xml
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
    <version>5.3.2</version>
</dependency>
```

### 1.3 数据库准备

执行以下SQL创建分片数据库：

```sql
-- 用户分库
CREATE DATABASE os_user_0;
CREATE DATABASE os_user_1;
CREATE DATABASE os_user_2;
CREATE DATABASE os_user_3;

-- 课程分库
CREATE DATABASE os_course_0;
CREATE DATABASE os_course_1;

-- 系统库
CREATE DATABASE os_system;
```

### 1.4 启动应用

```bash
# 启动用户服务
cd roncoo-education-service/roncoo-education-service-user
mvn spring-boot:run

# 启动课程服务
cd roncoo-education-service/roncoo-education-service-course
mvn spring-boot:run

# 启动系统服务
cd roncoo-education-service/roncoo-education-service-system
mvn spring-boot:run
```

## 2. 基本使用

### 2.1 用户数据操作

```java
@Autowired
private UsersDao usersDao;

// 创建用户（自动路由到对应分片）
Users user = new Users();
user.setId(1001L);  // 分片键
user.setMobile("13800001001");
user.setNickname("测试用户");
usersDao.save(user);

// 查询用户（根据分片键）
Users savedUser = usersDao.getById(1001L);
```

### 2.2 课程数据操作

```java
@Autowired
private CourseDao courseDao;

// 创建课程
Course course = new Course();
course.setId(2001L);  // 分片键
course.setCourseName("Java基础教程");
course.setLecturerId(1001L);
courseDao.save(course);

// 查询课程
Course savedCourse = courseDao.getById(2001L);
```

### 2.3 订单数据操作

```java
@Autowired
private OrderInfoDao orderInfoDao;

// 创建订单（按用户ID分片）
OrderInfo order = new OrderInfo();
order.setId(3001L);
order.setUserId(1001L);  // 分片键
order.setCourseId(2001L);
order.setOrderNo(System.currentTimeMillis());
orderInfoDao.save(order);

// 查询用户订单
List<OrderInfo> orders = orderInfoDao.getByUserId(1001L);
```

## 3. 配置说明

### 3.1 分片规则

#### 用户分片
- **分库**：`user_id % 4` → user-db-0, user-db-1, user-db-2, user-db-3
- **分表**：`user_id % 16` → users_0, users_1, ..., users_15

#### 课程分片  
- **分库**：`course_id % 2` → course-db-0, course-db-1
- **分表**：`course_id % 16` → course_0, course_1, ..., course_15

#### 订单分片
- **分库**：`user_id % 4` → user-db-0, user-db-1, user-db-2, user-db-3  
- **分表**：`user_id % 32` → order_info_0, order_info_1, ..., order_info_31

### 3.2 数据源配置

每个服务模块有独立的分片配置文件：
- `roncoo-education-service-user/src/main/resources/sharding-databases.yaml`
- `roncoo-education-service-course/src/main/resources/sharding-databases.yaml`
- `roncoo-education-service-system/src/main/resources/sharding-databases.yaml`

## 4. 工具使用

### 4.1 分片工具类

```java
import com.roncoo.education.common.sharding.utils.ShardingUtils;

// 计算用户分库索引
int dbIndex = ShardingUtils.calculateUserDatabaseIndex(1001L, 4);
System.out.println("用户1001在数据库索引: " + dbIndex);

// 计算用户分表索引
int tableIndex = ShardingUtils.calculateUserTableIndex(1001L, 16);
System.out.println("用户1001在表索引: " + tableIndex);

// 构造数据库名称
String dbName = ShardingUtils.buildUserDatabaseName(1001L, 4);
System.out.println("数据库名称: " + dbName);

// 构造表名称
String tableName = ShardingUtils.buildUserTableName("users", 1001L, 16);
System.out.println("表名称: " + tableName);
```

### 4.2 性能监控

访问监控接口：
```bash
# 查看整体性能统计
curl http://localhost:7720/sharding/monitor/stats

# 查看系统健康状态
curl http://localhost:7720/sharding/monitor/health

# 查看性能建议
curl http://localhost:7720/sharding/monitor/recommendations
```

## 5. 常见场景

### 5.1 单用户查询

```java
// ✅ 推荐：包含分片键的查询
public UserProfile getUserProfile(Long userId) {
    Users user = usersDao.getById(userId);
    List<OrderInfo> orders = orderInfoDao.getByUserId(userId);
    
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    profile.setOrders(orders);
    return profile;
}
```

### 5.2 批量查询

```java
// ✅ 推荐：按分片分组的批量查询
public List<Users> getUsersByIds(List<Long> userIds) {
    List<Users> result = new ArrayList<>();
    
    // 按分片分组
    Map<Integer, List<Long>> groups = userIds.stream()
        .collect(Collectors.groupingBy(id -> 
            ShardingUtils.calculateUserDatabaseIndex(id, 4)));
    
    // 分组查询
    for (List<Long> ids : groups.values()) {
        result.addAll(usersDao.getByIds(ids));
    }
    
    return result;
}
```

### 5.3 分页查询

```java
// ⚠️ 注意：分页查询需要指定分片范围
public Page<Users> getUsersPage(Long startUserId, Long endUserId, int pageSize) {
    List<Users> allUsers = new ArrayList<>();
    
    // 计算涉及的分片
    int[] shards = ShardingUtils.getTableIndexRange(startUserId, endUserId, 16);
    
    for (int shard : shards) {
        List<Users> shardUsers = usersDao.getByIdRangeInShard(
            startUserId, endUserId, shard);
        allUsers.addAll(shardUsers);
    }
    
    // 应用层分页
    return PageUtil.getPage(allUsers, pageSize);
}
```

## 6. 开发建议

### 6.1 DO & DON'T

**✅ 推荐做法：**
- 查询时始终包含分片键
- 使用批量操作提升性能
- 优先使用单分片事务
- 设计时考虑分片键的分布均匀性

**❌ 避免做法：**
- 不包含分片键的全表查询
- 频繁的跨分片JOIN查询
- 过度使用分布式事务
- 在分片键上使用范围分片但查询不均匀

### 6.2 性能优化

1. **索引优化**：确保分片键和查询字段有索引
2. **批量操作**：使用批量插入替代单条插入
3. **缓存使用**：对热点数据使用Redis缓存
4. **连接池调优**：根据并发调整连接池参数

## 7. 测试验证

### 7.1 运行单元测试

```bash
# 测试分片算法
mvn test -Dtest=UserDatabaseShardingAlgorithmTest
mvn test -Dtest=ShardingUtilsTest

# 测试集成功能
mvn test -Dtest=ShardingIntegrationTest
```

### 7.2 验证分片路由

启用SQL日志查看分片路由：
```yaml
logging:
  level:
    org.apache.shardingsphere: DEBUG
    com.roncoo.education.common.sharding: DEBUG
```

### 7.3 性能基准测试

使用提供的性能测试：
```java
@Test
public void testBatchOperationPerformance() {
    // 批量插入1000个用户
    // 验证性能指标
}
```

## 8. 故障排查

### 8.1 常见错误

**错误1：分片键为空**
```
Sharding value can't be null in SQL
```
解决：检查查询条件是否包含分片键

**错误2：跨分片JOIN**
```
Cross database join is not supported
```
解决：优化查询逻辑，避免跨分片关联

**错误3：数据源连接失败**
```
Failed to obtain JDBC Connection
```
解决：检查数据库连接配置和网络连通性

### 8.2 调试技巧

1. **启用SQL日志**：查看实际执行的SQL和路由信息
2. **使用监控接口**：实时查看性能指标
3. **检查分片计算**：使用工具类验证分片路由逻辑

## 9. 进阶使用

### 9.1 自定义分片算法

```java
public class CustomShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> collection, 
                           PreciseShardingValue<Long> preciseShardingValue) {
        // 自定义分片逻辑
        Long value = preciseShardingValue.getValue();
        int index = (int) (value % collection.size());
        return collection.stream().skip(index).findFirst().orElse(null);
    }
    // ... 其他方法实现
}
```

### 9.2 读写分离配置

```yaml
spring:
  shardingsphere:
    rules:
      readwrite-splitting:
        data-sources:
          user-readwrite:
            type: Static
            props:
              write-data-source-name: user-db-0
              read-data-source-names: user-db-0-slave
```

### 9.3 分布式事务

```java
@Transactional
@ShardingTransactionType(TransactionType.XA)
public void crossShardTransaction() {
    // 跨分片操作
    usersDao.save(user);
    orderInfoDao.save(order);
}
```

## 10. 支持和帮助

### 10.1 文档资源

- [完整实施文档](./sharding-implementation-guide.md)
- [Apache ShardingSphere官方文档](https://shardingsphere.apache.org/document/current/cn/overview/)

### 10.2 问题反馈

如遇到问题，请提供以下信息：
- 错误日志
- 相关配置文件
- 重现步骤
- 环境信息

通过以上快速入门指南，您可以快速了解和使用分库分表功能。建议先在测试环境熟悉相关操作，然后再部署到生产环境。