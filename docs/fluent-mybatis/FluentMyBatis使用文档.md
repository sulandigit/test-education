# FluentMyBatis 使用文档和最佳实践指南

## 1. 概述

本文档为领课教育系统引入FluentMyBatis提供详细的使用指南和最佳实践建议。FluentMyBatis是MyBatis的增强工具，通过编译时代码生成提供流式API，显著提升开发效率和代码质量。

### 1.1 FluentMyBatis的核心优势

- **类型安全**: 编译时检查，避免字段名错误
- **IDE友好**: 完整的代码提示和自动完成
- **链式API**: 直观的查询构建方式
- **零配置**: 基于注解的代码生成
- **兼容性**: 与原生MyBatis完全兼容

### 1.2 适用场景

✅ **推荐使用场景**:
- 复杂的条件查询
- 动态SQL构建
- 批量数据处理
- 统计分析查询
- 新功能开发

❌ **不推荐场景**:
- 简单的CRUD操作（性能要求极高）
- 复杂的SQL优化场景
- 需要精确控制SQL的场景

## 2. 快速开始

### 2.1 环境准备

确保项目已经添加FluentMyBatis依赖：

```xml
<!-- FluentMyBatis核心依赖 -->
<dependency>
    <groupId>com.github.atool</groupId>
    <artifactId>fluent-mybatis</artifactId>
    <version>1.9.8</version>
</dependency>

<!-- 编译时处理器 -->
<dependency>
    <groupId>com.github.atool</groupId>
    <artifactId>fluent-mybatis-processor</artifactId>
    <version>1.9.8</version>
    <scope>provided</scope>
</dependency>

<!-- 测试依赖 -->
<dependency>
    <groupId>com.github.atool</groupId>
    <artifactId>fluent-mybatis-test</artifactId>
    <version>1.9.8</version>
    <scope>test</scope>
</dependency>
```

### 2.2 实体类配置

为现有实体类添加FluentMyBatis注解：

```java
@FluentMybatis(table = "users")
public class Users extends RichEntity implements Serializable {
    @TableId
    private Long id;
    
    @TableField("mobile")
    private String mobile;
    
    @TableField("nickname")
    private String nickname;
    
    @TableField("status_id")
    private Integer statusId;
    
    // getter/setter...
}
```

### 2.3 代码生成

运行代码生成器：

```java
@Test
public void generateCode() {
    FileGenerator.build(Fluent4UserModule.class);
}

@Tables(
    url = "jdbc:mysql://localhost:3306/roncoo_education",
    username = "root", password = "password",
    basePack = "com.roncoo.education.user.dao.fluent",
    tables = {
        @Table(value = {"users"}, entity = "Users")
    }
)
static class Fluent4UserModule {
}
```

## 3. 基础用法

### 3.1 简单查询

```java
// 根据ID查询
Users user = new UsersQuery()
    .where().id().eq(userId)
    .to().findOne().orElse(null);

// 根据手机号查询
Users user = new UsersQuery()
    .where().mobile().eq(mobile)
    .to().findOne().orElse(null);

// 条件查询列表
List<Users> users = new UsersQuery()
    .where().statusId().eq(1)
    .and().gmtCreate().gt(LocalDateTime.now().minusDays(30))
    .to().listEntity();
```

### 3.2 复杂条件查询

```java
// 多条件组合查询
List<Users> users = new UsersQuery()
    .where().statusId().eq(1)
    .and().mobile().like("%188%")
    .and().userAge().between(18, 65)
    .or().nickname().like("%VIP%")
    .orderBy().gmtCreate().desc()
    .limit(10)
    .to().listEntity();

// 分组查询
List<Map<String, Object>> ageGroups = new UsersQuery()
    .select().apply("CASE WHEN user_age < 25 THEN '青年' " +
                   "WHEN user_age < 45 THEN '中年' " +
                   "ELSE '中老年' END").as("age_group")
    .select().count().as("user_count")
    .where().statusId().eq(1)
    .groupBy().apply("CASE WHEN user_age < 25 THEN '青年' " +
                    "WHEN user_age < 45 THEN '中年' " +
                    "ELSE '中老年' END")
    .to().listMaps();
```

### 3.3 关联查询

```java
// 用户订单关联查询
List<Map<String, Object>> userOrders = new UsersQuery()
    .select().apply("u.id", "u.nickname", "o.order_no", "o.course_price")
    .from().table("users u")
    .leftJoin("order_info o").on("u.id = o.user_id")
    .where().apply("u.status_id = 1")
    .and().apply("o.order_status = 1")
    .orderBy().apply("o.gmt_create desc")
    .to().listMaps();
```

### 3.4 统计查询

```java
// 用户统计
long totalUsers = new UsersQuery()
    .where().statusId().eq(1)
    .to().count();

// 复杂统计
Map<String, Object> userStats = new UsersQuery()
    .select().count().as("total_count")
    .select().avg("user_age").as("avg_age")
    .select().apply("COUNT(CASE WHEN user_sex = 1 THEN 1 END)").as("male_count")
    .select().apply("COUNT(CASE WHEN user_sex = 0 THEN 1 END)").as("female_count")
    .where().statusId().eq(1)
    .to().findOne(Map.class).orElse(new HashMap<>());
```

## 4. 数据更新操作

### 4.1 简单更新

```java
// 根据ID更新
int updated = new UsersUpdater()
    .set().nickname().is("新昵称")
    .set().gmtModified().is(LocalDateTime.now())
    .where().id().eq(userId)
    .to().updateBy();

// 条件更新
int updated = new UsersUpdater()
    .set().statusId().is(0)
    .set().gmtModified().is(LocalDateTime.now())
    .where().mobile().in(mobileList)
    .to().updateBy();
```

### 4.2 批量更新

```java
// 批量状态更新
int updated = new UsersUpdater()
    .set().statusId().is(1)
    .set().gmtModified().is(LocalDateTime.now())
    .where().id().in(userIds)
    .to().updateBy();

// 基于条件的批量更新
int updated = new UsersUpdater()
    .set().apply("last_login_time", LocalDateTime.now())
    .where().gmtModified().lt(LocalDateTime.now().minusDays(30))
    .and().statusId().eq(1)
    .to().updateBy();
```

### 4.3 复杂更新

```java
// 基于子查询的更新
int updated = new UsersUpdater()
    .set().apply("user_level", 
        "(SELECT CASE WHEN COUNT(*) > 10 THEN 'VIP' ELSE 'NORMAL' END " +
        "FROM order_info WHERE user_id = users.id)")
    .where().statusId().eq(1)
    .to().updateBy();
```

## 5. 高级特性

### 5.1 动态查询构建

```java
public List<Users> searchUsers(UserSearchDTO searchDTO) {
    UsersQuery query = new UsersQuery();
    
    // 动态添加条件
    if (StringUtils.hasText(searchDTO.getMobile())) {
        query.where().mobile().like("%" + searchDTO.getMobile() + "%");
    }
    
    if (searchDTO.getMinAge() != null) {
        query.and().userAge().ge(searchDTO.getMinAge());
    }
    
    if (searchDTO.getMaxAge() != null) {
        query.and().userAge().le(searchDTO.getMaxAge());
    }
    
    if (searchDTO.getGender() != null) {
        query.and().userSex().eq(searchDTO.getGender());
    }
    
    // 动态排序
    if ("age".equals(searchDTO.getSortField())) {
        query.orderBy().userAge().desc();
    } else {
        query.orderBy().gmtCreate().desc();
    }
    
    return query.to().listEntity();
}
```

### 5.2 分页查询

```java
public Page<Users> pageUsers(int pageNum, int pageSize, UsersExample example) {
    // 方式1：使用基础DAO的分页方法
    UsersQuery query = buildQueryFromExample(example);
    return super.page(pageNum, pageSize, query);
    
    // 方式2：手动分页
    long total = query.to().count();
    List<Users> records = query
        .limit((pageNum - 1) * pageSize, pageSize)
        .to().listEntity();
    
    return new Page<>(total, pageNum, pageSize, records);
}
```

### 5.3 事务处理

```java
@Transactional(rollbackFor = Exception.class)
public void transferUserData(Long fromUserId, Long toUserId) {
    // 查询源用户数据
    Users fromUser = new UsersQuery()
        .where().id().eq(fromUserId)
        .to().findOne().orElseThrow(() -> new DataNotFoundException("用户不存在"));
    
    // 更新目标用户数据
    int updated = new UsersUpdater()
        .set().apply("total_score", "total_score + " + fromUser.getTotalScore())
        .where().id().eq(toUserId)
        .to().updateBy();
    
    if (updated == 0) {
        throw new DataUpdateException("数据转移失败");
    }
    
    // 清零源用户数据
    new UsersUpdater()
        .set().totalScore().is(0)
        .set().gmtModified().is(LocalDateTime.now())
        .where().id().eq(fromUserId)
        .to().updateBy();
}
```

## 6. 性能优化

### 6.1 查询优化

```java
// 避免SELECT *，明确指定字段
List<UserInfoDTO> users = new UsersQuery()
    .select().id().mobile().nickname()  // 只查询需要的字段
    .where().statusId().eq(1)
    .to().listEntity(UserInfoDTO.class);

// 使用EXISTS替代IN（大数据量时）
List<Users> activeUsers = new UsersQuery()
    .where().statusId().eq(1)
    .and().apply("EXISTS (SELECT 1 FROM order_info o WHERE o.user_id = users.id)")
    .to().listEntity();
```

### 6.2 批量操作优化

```java
// 批量插入优化
@Transactional
public int batchSaveUsers(List<Users> users) {
    int batchSize = 1000;
    int totalSaved = 0;
    
    for (int i = 0; i < users.size(); i += batchSize) {
        List<Users> batch = users.subList(i, 
            Math.min(i + batchSize, users.size()));
        
        for (Users user : batch) {
            totalSaved += mapper.save(user);
        }
        
        // 每批次后刷新，避免内存溢出
        if (i % batchSize == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
    
    return totalSaved;
}
```

### 6.3 缓存策略

```java
@Cacheable(value = "users", key = "#mobile")
public Users getUserByMobile(String mobile) {
    return new UsersQuery()
        .where().mobile().eq(mobile)
        .to().findOne().orElse(null);
}

@CacheEvict(value = "users", key = "#user.mobile")
public int updateUser(Users user) {
    return new UsersUpdater()
        .set().byEntity(user)
        .where().id().eq(user.getId())
        .to().updateBy();
}
```

## 7. 最佳实践

### 7.1 代码组织

```java
// 将复杂查询封装到专门的方法中
public class UserQueryHelper {
    
    public static UsersQuery activeUsersQuery() {
        return new UsersQuery()
            .where().statusId().eq(1);
    }
    
    public static UsersQuery recentUsersQuery(int days) {
        return new UsersQuery()
            .where().gmtCreate().gt(LocalDateTime.now().minusDays(days));
    }
    
    public static UsersQuery vipUsersQuery() {
        return new UsersQuery()
            .where().apply("EXISTS (SELECT 1 FROM user_vip uv WHERE uv.user_id = users.id AND uv.status = 1)");
    }
}

// 使用示例
List<Users> activeVipUsers = UserQueryHelper.activeUsersQuery()
    .and(UserQueryHelper.vipUsersQuery().where)
    .orderBy().gmtCreate().desc()
    .to().listEntity();
```

### 7.2 错误处理

```java
public Users getUserById(Long id) {
    try {
        return new UsersQuery()
            .where().id().eq(id)
            .to().findOne()
            .orElseThrow(() -> new UserNotFoundException("用户不存在: " + id));
    } catch (Exception e) {
        log.error("查询用户失败, id: {}", id, e);
        throw new DataAccessException("查询用户失败", e);
    }
}
```

### 7.3 日志记录

```java
@Slf4j
public class UserService {
    
    public List<Users> searchUsers(UserSearchDTO searchDTO) {
        log.info("搜索用户开始, 条件: {}", searchDTO);
        
        long startTime = System.currentTimeMillis();
        
        List<Users> users = buildSearchQuery(searchDTO)
            .to().listEntity();
        
        long endTime = System.currentTimeMillis();
        
        log.info("搜索用户完成, 结果数量: {}, 耗时: {}ms", 
            users.size(), endTime - startTime);
        
        return users;
    }
}
```

### 7.4 单元测试

```java
@Test
@DisplayName("测试复杂用户查询")
void testComplexUserQuery() {
    // Given
    Users testUser = createTestUser();
    usersMapper.save(testUser);
    
    // When
    List<Users> result = new UsersQuery()
        .where().statusId().eq(1)
        .and().mobile().like("%188%")
        .to().listEntity();
    
    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getMobile()).contains("188");
}
```

## 8. 常见问题和解决方案

### 8.1 编译时问题

**问题**: 代码生成失败
```
解决方案:
1. 检查数据库连接配置
2. 确保实体类注解正确
3. 清理target目录后重新编译
4. 检查Maven/Gradle配置
```

**问题**: IDE无法识别生成的类
```
解决方案:
1. 刷新项目
2. 重新导入Maven依赖
3. 设置正确的generated sources目录
```

### 8.2 运行时问题

**问题**: 查询结果为空
```java
// 错误示例
List<Users> users = new UsersQuery()
    .where().mobile().eq(null)  // null值查询
    .to().listEntity();

// 正确示例
List<Users> users = new UsersQuery()
    .where().mobile().isNotNull()
    .to().listEntity();
```

**问题**: 性能问题
```java
// 避免在循环中执行查询
// 错误示例
for (Long userId : userIds) {
    Users user = new UsersQuery()
        .where().id().eq(userId)
        .to().findOne().orElse(null);
}

// 正确示例
List<Users> users = new UsersQuery()
    .where().id().in(userIds)
    .to().listEntity();
```

### 8.3 兼容性问题

**问题**: 与原生MyBatis混用
```java
// 可以在同一个项目中混用
@Service
public class UserService {
    
    @Autowired
    private UsersMapper originalMapper;  // 原生MyBatis
    
    // 简单查询使用原生MyBatis
    public Users getById(Long id) {
        return originalMapper.selectByPrimaryKey(id);
    }
    
    // 复杂查询使用FluentMyBatis
    public List<Users> complexSearch(SearchDTO searchDTO) {
        return buildFluentQuery(searchDTO).to().listEntity();
    }
}
```

## 9. 迁移指南

### 9.1 渐进式迁移策略

1. **阶段1**: 添加依赖和配置，保持现有代码不变
2. **阶段2**: 为新功能使用FluentMyBatis
3. **阶段3**: 逐步迁移复杂查询场景
4. **阶段4**: 保留简单CRUD的原生实现

### 9.2 迁移检查清单

- [ ] 依赖配置完成
- [ ] 实体类注解添加完成
- [ ] 代码生成成功
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 性能测试无显著退化
- [ ] 团队培训完成

## 10. 总结

FluentMyBatis为领课教育系统提供了强大的数据访问能力，通过合理使用可以显著提升开发效率和代码质量。关键在于：

1. **适度使用**: 在合适的场景使用FluentMyBatis
2. **性能监控**: 持续监控查询性能
3. **团队培训**: 确保团队掌握最佳实践
4. **渐进迁移**: 采用渐进式迁移策略
5. **持续优化**: 根据实际使用情况不断优化

遵循本指南的建议，可以充分发挥FluentMyBatis的优势，同时避免常见的陷阱和问题。