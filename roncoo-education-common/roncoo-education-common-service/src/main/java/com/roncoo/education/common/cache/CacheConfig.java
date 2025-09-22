package com.roncoo.education.common.cache;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 自定义缓存管理器配置类
 * 用于配置 Spring Cache 的相关设置，包括自定义的缓存键生成策略
 * 启用 Spring 的缓存功能，并提供自定义的键生成器
 *
 * @author wujing
 * @date 2022/1/1
 */
@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 自定义缓存键生成器
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key
     * 即使@Cacheable中的value属性一样，key也会不一样
     * 
     * 键的格式：类名::方法名+参数值1+参数值2+...
     * 例如：com.example.UserService::getUserById123
     * 
     * @return KeyGenerator 缓存键生成器
     */
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            // 添加类名
            sb.append(o.getClass().getName());
            sb.append("::");
            // 添加方法名
            sb.append(method.getName());
            // 添加所有参数值
            for (Object obj : objects) {
                if (obj != null) {
                    sb.append(obj.toString());
                }
            }
            return sb.toString();
        };
    }

}
