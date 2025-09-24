# Spring Boot 3.x 升级配置变更指南

## 核心版本升级完成

### 框架版本更新
- ✅ **Spring Boot**: 2.6.3 → 3.2.10
- ✅ **Spring Cloud**: 2021.0.1 → 2023.0.3  
- ✅ **Spring Cloud Alibaba**: 2021.0.1.0 → 2023.0.1.0
- ✅ **Java**: 1.8 → 17
- ✅ **Jakarta EE**: javax.* → jakarta.*

### 依赖组件更新
| 组件 | 原版本 | 新版本 | 状态 |
|------|--------|--------|------|
| JUnit Jupiter | 5.9.2 | 5.10.1 | ✅ |
| Mockito | 4.5.1 | 5.7.0 | ✅ |
| ByteBuddy | 1.12.22 | 1.14.10 | ✅ |
| Knife4j | 3.0.3 | 4.3.0 | ✅ |

## 配置文件适配要求

### 1. Spring Security配置现代化

**移除已弃用的WebSecurityConfigurerAdapter**

原配置方式（需要更新）：
```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 旧的配置方式
    }
}
```

新配置方式：
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

### 2. 配置属性变更

**服务器配置**：
```yaml
# 旧配置
server:
  max-http-header-size: 8KB

# 新配置  
server:
  max-http-request-header-size: 8KB
```

**日志配置**：
```yaml
# 日期格式现在默认为ISO-8601
logging:
  pattern:
    dateformat: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
```

### 3. MyBatis配置适配

验证MyBatis配置兼容性：
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.roncoo.education.*.dao.impl.mapper.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
```

### 4. Actuator端点配置

Spring Boot 3.x中Actuator端点配置：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

## API文档配置 (Knife4j)

### OpenAPI 3.0配置

```java
@Configuration
@EnableKnife4j
public class Knife4jConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("领课教育API文档")
                .version("3.0")
                .description("Spring Boot 3.x API Documentation"));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build();
    }
}
```

## Spring Cloud Gateway配置

### 路由配置更新

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: system-service
          uri: lb://roncoo-education-service-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
        - id: user-service  
          uri: lb://roncoo-education-service-user
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
```

### 负载均衡配置

Spring Cloud LoadBalancer替代Ribbon：
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
      cache:
        enabled: true
        ttl: 30s
```

## Feign客户端配置

### OpenFeign配置更新

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: basic
  compression:
    request:
      enabled: true
    response:
      enabled: true
```

## Nacos配置适配

### 服务发现和配置中心

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:public}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
      config:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:public}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        file-extension: yml
        refresh-enabled: true
```

## 数据源配置

### Druid连接池配置

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
```

## 测试配置

### JUnit 5配置

```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ApplicationTests {
    
    @Test
    @Order(1)
    void contextLoads() {
        // 测试Spring上下文加载
    }
}
```

## 下一步操作清单

### 必须执行的配置更新

1. **更新Spring Security配置** - 移除WebSecurityConfigurerAdapter
2. **验证API文档配置** - 检查Knife4j OpenAPI 3.0兼容性  
3. **更新Gateway路由配置** - 确保路由规则正确
4. **检查Feign客户端配置** - 验证服务间调用
5. **验证Nacos配置** - 确保服务注册发现正常

### 验证步骤

```bash
# 1. 编译验证
mvn clean compile

# 2. 测试验证  
mvn test

# 3. 启动验证
mvn spring-boot:run

# 4. 健康检查
curl http://localhost:8080/actuator/health
```

## 已知问题和解决方案

### 1. 循环依赖问题
Spring Boot 3.x默认禁止循环依赖，如遇到需要重构代码结构。

### 2. 配置绑定更严格
配置属性绑定更加严格，确保配置文件格式正确。

### 3. 反射访问限制  
Java 17的模块系统可能影响反射，需要添加必要的opens声明。

---

*配置指南更新时间：2025-09-24*