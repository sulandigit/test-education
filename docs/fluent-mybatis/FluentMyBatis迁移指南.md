# FluentMyBatis 渐进式迁移指南

## 1. 迁移概述

本文档为领课教育系统从原生MyBatis到FluentMyBatis的渐进式迁移提供详细指导。

### 1.1 迁移目标
- 🎯 **渐进迁移**: 分阶段引入FluentMyBatis，降低风险
- 🔄 **兼容并存**: 新旧两套方案可以并存使用
- 📈 **逐步优化**: 持续优化开发效率和代码质量
- 🛡️ **风险控制**: 最小化对现有业务的影响

### 1.2 迁移原则
1. **业务优先**: 确保业务功能不受影响
2. **渐进演进**: 分阶段、分模块迁移
3. **向后兼容**: 保持对现有代码的兼容
4. **质量保证**: 充分测试验证每个迁移步骤

## 2. 迁移阶段规划

### 2.1 迁移阶段划分

| 阶段 | 持续时间 | 主要工作 | 成功标准 |
|------|----------|----------|----------|
| 阶段0 | 1-2周 | 环境准备、团队培训 | 环境搭建完成，团队掌握基础用法 |
| 阶段1 | 2-3周 | 基础设施搭建 | 代码生成成功，基础框架可用 |
| 阶段2 | 4-6周 | 新功能使用FluentMyBatis | 新功能开发效率提升30% |
| 阶段3 | 6-8周 | 复杂查询迁移 | 关键复杂查询迁移完成 |
| 阶段4 | 4-6周 | 全面优化 | 性能达标，代码质量提升 |

## 3. 阶段0：准备阶段

### 3.1 环境准备检查清单

#### 技术环境
- [ ] Java 8+ 环境确认
- [ ] Maven/Gradle 构建工具版本确认
- [ ] IDE插件安装（推荐IDEA MyBatis插件）
- [ ] 数据库连接测试
- [ ] 现有MyBatis版本兼容性确认

#### 依赖配置
```xml
<!-- 根POM依赖管理 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.atool</groupId>
            <artifactId>fluent-mybatis</artifactId>
            <version>1.9.8</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3.2 风险评估

| 风险类型 | 风险等级 | 应对措施 |
|----------|----------|----------|
| 技术风险 | 中 | 充分培训，逐步迁移 |
| 兼容性风险 | 低 | 并行运行，充分测试 |
| 性能风险 | 中 | 性能监控，基准测试 |
| 人员风险 | 中 | 培训支持，文档完善 |

## 4. 阶段1：基础设施搭建

### 4.1 代码生成配置

```java
@Tables(
    url = "jdbc:mysql://localhost:3306/roncoo_education",
    username = "root", password = "password",
    basePack = "com.roncoo.education.user.dao.fluent",
    tables = {
        @Table(value = {"users"}, entity = "Users"),
        @Table(value = {"order_info"}, entity = "OrderInfo")
    }
)
static class Fluent4UserModule {}
```

### 4.2 基础框架搭建

```java
@Repository
public abstract class FluentMyBatisBaseDao<T extends RichEntity, Q extends IBaseQuery<T>, U extends IBaseUpdater<T>> {
    
    @Autowired
    protected IEntityMapper<T> mapper;
    
    protected abstract Q query();
    protected abstract U updater();
    
    public int save(T entity) {
        return mapper.save(entity);
    }
    
    public Page<T> page(int pageCurrent, int pageSize, Q queryBuilder) {
        // 实现分页逻辑
    }
}
```

## 5. 阶段2：新功能优先使用

### 5.1 新功能开发规范
- 🆕 **新功能强制使用**: 所有新开发的功能必须使用FluentMyBatis
- 📝 **代码审查**: 新功能的代码审查重点关注FluentMyBatis使用
- 📊 **效果跟踪**: 跟踪新功能的开发效率和代码质量

### 5.2 示例：新用户统计功能

```java
@Service
public class UserAnalysisService {
    
    // 用户注册趋势
    public List<Map<String, Object>> getRegistrationTrend(LocalDateTime start, LocalDateTime end) {
        return new UsersQuery()
            .select().apply("DATE_FORMAT(gmt_create, '%Y-%m')").as("month")
            .select().count().as("registration_count")
            .where().gmtCreate().between(start, end)
            .groupBy().apply("DATE_FORMAT(gmt_create, '%Y-%m')")
            .orderBy().apply("month ASC")
            .to().listMaps();
    }
}
```

## 6. 阶段3：复杂查询迁移

### 6.1 迁移优先级评估

| 复杂度 | 特征 | 迁移优先级 | 示例 |
|--------|------|------------|------|
| 低 | 简单条件查询 | 低 | 根据ID查询用户 |
| 中 | 多表关联、分组统计 | 高 | 用户订单统计 |
| 高 | 复杂子查询、函数计算 | 中 | 用户价值分析 |
| 极高 | 存储过程、特殊SQL | 暂不迁移 | 复杂报表生成 |

### 6.2 并行运行策略

```java
@Service
public class UserReportService {
    
    @Value("${migration.use-fluent-mybatis:false}")
    private boolean useFluentMyBatis;
    
    public List<UserReportDTO> getUserReport(UserReportQuery query) {
        if (useFluentMyBatis) {
            return fluentDAO.getUserReport(query);
        } else {
            return originalDAO.getUserReport(query);
        }
    }
}
```

## 7. 兼容性保障

### 7.1 接口兼容性

```java
// 保持接口不变，实现可替换
public interface UsersDao {
    int save(Users record);
    Users getById(Long id);
    List<Users> listByIds(List<Long> userIds);
}

// 原生MyBatis实现
@Repository("originalUsersDao")
public class UsersDaoImpl implements UsersDao {
    // 现有实现
}

// FluentMyBatis实现
@Repository("fluentUsersDao")
public class FluentUsersDaoImpl implements UsersDao {
    // FluentMyBatis实现
}
```

### 7.2 配置兼容性

```java
@Configuration
public class CompatibilityConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "fluent.mybatis.enabled", havingValue = "true")
    public SqlSessionFactoryBean fluentSqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        
        // 同时支持原生MyBatis和FluentMyBatis
        bean.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mybatis/**/*Mapper.xml")
        );
        
        return bean;
    }
}
```

### 7.3 数据一致性保障

```java
@Component
public class MigrationValidator {
    
    // 对比验证方法
    @Profile("dev")
    public void validateMigration(UserReportQuery query) {
        List<UserReportDTO> originalResult = originalDAO.getUserReport(query);
        List<UserReportDTO> fluentResult = fluentDAO.getUserReport(query);
        
        if (!Objects.equals(originalResult, fluentResult)) {
            log.warn("迁移验证失败: 查询结果不一致");
        }
    }
}
```

## 8. 监控和回滚机制

### 8.1 性能监控

```java
@Component
public class FluentMyBatisMonitor {
    
    @EventListener
    public void monitorSlowQuery(QueryExecutionEvent event) {
        if (event.getExecutionTime() > 1000) {
            log.warn("慢查询检测: {}, 耗时: {}ms", 
                event.getQueryInfo(), event.getExecutionTime());
        }
    }
}
```

### 8.2 自动回滚配置

```yaml
migration:
  auto-rollback:
    enabled: true
    error-threshold: 0.05  # 错误率超过5%自动回滚
    response-time-threshold: 2000  # 响应时间超过2秒回滚
```

## 9. 最佳实践建议

### 9.1 开发规范
- 新功能优先使用FluentMyBatis
- 复杂查询逐步迁移
- 保持向后兼容性
- 充分的单元测试和集成测试

### 9.2 性能优化
- 避免SELECT *
- 合理使用批量操作
- 监控慢查询
- 适当使用缓存

### 9.3 团队协作
- 定期技术分享
- 代码审查重点关注FluentMyBatis使用
- 建立问题反馈机制
- 持续优化和改进

## 10. 总结

通过渐进式迁移策略，可以安全地将领课教育系统从原生MyBatis迁移到FluentMyBatis，在保证系统稳定性的同时，逐步提升开发效率和代码质量。

关键成功因素：
1. **充分的准备和计划**
2. **渐进式的实施策略**
3. **完善的监控和回滚机制**
4. **团队的技术能力建设**