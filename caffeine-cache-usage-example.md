# Caffeine多级缓存使用示例

## 基本使用方法

### 1. 在业务类中使用缓存注解

```java
@Component
@CacheConfig(cacheNames = {"user-data"})
public class UserBiz {

    @Autowired
    private UserDao userDao;

    // 查询时自动缓存
    @Cacheable(key = "#userId")
    public User getUserById(Long userId) {
        return userDao.getById(userId);
    }

    // 更新时自动清除缓存
    @CacheEvict(key = "#user.id")
    public void updateUser(User user) {
        userDao.updateById(user);
    }

    // 删除时清除所有相关缓存
    @CacheEvict(allEntries = true)
    public void deleteAllUsers() {
        userDao.deleteAll();
    }
}
```

### 2. 编程式缓存操作

```java
@Service
public class CustomCacheService {

    @Autowired
    private CacheManager cacheManager;

    public void manualCacheOperation() {
        // 获取缓存实例
        Cache cache = cacheManager.getCache("custom-cache");
        
        // 手动添加缓存
        cache.put("key1", "value1");
        
        // 手动查询缓存
        Cache.ValueWrapper wrapper = cache.get("key1");
        String value = wrapper != null ? (String) wrapper.get() : null;
        
        // 手动清除缓存
        cache.evict("key1");
    }
}
```

### 3. 缓存统计信息获取

```java
@RestController
@RequestMapping("/admin/cache")
public class CacheAdminController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/stats/{cacheName}")
    public CacheStats getCacheStats(@PathVariable String cacheName) {
        if (cacheManager instanceof MultiLevelCacheManager) {
            MultiLevelCacheManager manager = (MultiLevelCacheManager) cacheManager;
            return manager.getCacheStats(cacheName);
        }
        return null;
    }
}
```

## 配置示例

### application.properties 配置
```properties
# 启用多级缓存
spring.cache.multi-level.enabled=true

# Caffeine本地缓存配置
spring.cache.caffeine.maximum-size=10000
spring.cache.caffeine.expire-after-write=30
spring.cache.caffeine.expire-after-access=60
spring.cache.caffeine.initial-capacity=100
spring.cache.caffeine.record-stats=true

# 缓存管理接口
spring.cache.multi-level.management-enabled=true

# 监控端点
management.endpoints.web.exposure.include=caffeine-stats
management.endpoint.caffeine-stats.enabled=true
```

## 最佳实践

### 1. 缓存命名规范
```java
// 推荐的缓存命名方式
@CacheConfig(cacheNames = {"system-config"})    // 系统配置
@CacheConfig(cacheNames = {"course-category"})  // 课程分类
@CacheConfig(cacheNames = {"user-profile"})     // 用户资料
@CacheConfig(cacheNames = {"global-dict"})      // 全局字典
```

### 2. 缓存Key设计
```java
// 简单Key
@Cacheable(key = "#id")
public User findById(Long id) { ... }

// 复合Key
@Cacheable(key = "#type + '_' + #status")
public List<Course> findByTypeAndStatus(String type, Integer status) { ... }

// SpEL表达式Key
@Cacheable(key = "T(String).valueOf(#user.id).concat('_').concat(#user.type)")
public UserDetail getUserDetail(User user) { ... }
```

### 3. 条件缓存
```java
// 只缓存特定条件的数据
@Cacheable(condition = "#result != null and #result.size() > 0")
public List<Category> getActiveCategories() { ... }

// 排除特定条件
@Cacheable(unless = "#result == null")
public User findUser(String username) { ... }
```

### 4. 缓存预热
```java
@Component
public class CacheWarmupService {

    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private SystemConfigService systemConfigService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        // 预热系统配置缓存
        systemConfigService.getAllConfigs().forEach(config -> {
            Cache cache = cacheManager.getCache("system-config");
            cache.put(config.getConfigKey(), config.getConfigValue());
        });
    }
}
```

## 监控和调试

### 1. 缓存状态监控
```bash
# 查看所有缓存统计
curl http://localhost:8080/actuator/caffeine-stats

# 输出示例
{
  "cacheStats": {
    "system-config": {
      "cacheName": "system-config",
      "caffeineHitRate": 0.85,
      "caffeineSize": 150,
      "caffeineEvictionCount": 5
    }
  },
  "cacheCount": 3,
  "timestamp": "2024-01-15T10:30:00"
}
```

### 2. 缓存管理操作
```bash
# 清空指定缓存
curl -X DELETE http://localhost:8080/system/cache/clear/system-config

# 清空所有缓存
curl -X DELETE http://localhost:8080/system/cache/clear-all

# 缓存预热
curl -X POST http://localhost:8080/system/cache/warmup/system-config
```

### 3. 日志配置
```properties
# 开启缓存调试日志
logging.level.com.roncoo.education.common.cache=DEBUG
logging.level.org.springframework.cache=DEBUG
```

## 故障排除

### 1. 常见问题

**问题：缓存不生效**
```java
// 错误：自调用不会触发缓存
@Service
public class UserService {
    @Cacheable("users")
    public User getUser(Long id) { ... }
    
    public User getActiveUser(Long id) {
        return this.getUser(id); // 不会触发缓存
    }
}

// 正确：通过注入的代理对象调用
@Autowired
private UserService userService;

public User getActiveUser(Long id) {
    return userService.getUser(id); // 会触发缓存
}
```

**问题：缓存Key冲突**
```java
// 错误：不同方法使用相同Key
@Cacheable(cacheNames = "data", key = "#id")
public User getUser(Long id) { ... }

@Cacheable(cacheNames = "data", key = "#id") 
public Order getOrder(Long id) { ... } // Key可能冲突

// 正确：使用不同的缓存名或Key前缀
@Cacheable(cacheNames = "users", key = "#id")
public User getUser(Long id) { ... }

@Cacheable(cacheNames = "orders", key = "#id")
public Order getOrder(Long id) { ... }
```

### 2. 性能调优

**调整缓存大小**
```properties
# 根据内存情况调整
spring.cache.caffeine.maximum-size=20000  # 增加容量
spring.cache.caffeine.expire-after-write=60  # 延长过期时间
```

**监控关键指标**
- 命中率应该 > 80%
- 内存使用应该 < 90%
- 驱逐次数应该较低

这些示例帮助开发者快速上手并有效使用Caffeine多级缓存系统。