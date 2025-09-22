package com.roncoo.education.common.cache;

import com.roncoo.education.common.core.tools.JSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作工具类
 * 提供常用的 Redis 缓存操作方法，包括存储、读取、删除等功能
 * 支持对象的 JSON 序列化和反序列化
 * 
 * @author wujing
 * @date 2022/1/1
 */
@Component
public class CacheRedis {

    /**
     * 默认缓存存活时间（毫秒），从配置文件读取，默认60秒
     */
    @Value("${spring.cache.redis.time-to-live:60000}")
    private int timeToLive;

    /**
     * Spring Redis 字符串模板，用于执行 Redis 操作
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取 StringRedisTemplate 实例
     * 
     * @return StringRedisTemplate 实例
     */
    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    /**
     * 存储对象到 Redis 缓存中，使用默认过期时间
     * 支持基本类型（String、Long、Integer）和复杂对象（自动 JSON 序列化）
     * 
     * @param key 缓存键
     * @param t   要缓存的对象
     * @param <T> 对象类型
     * @return 返回存储的对象
     */
    public <T> T set(String key, T t) {
        if (t != null) {
            String value = t.toString();
            if (!(t instanceof String || t instanceof Long || t instanceof Integer)) {
                value = JSUtil.toJsonString(t);
            }
            stringRedisTemplate.opsForValue().set(key, value, timeToLive, TimeUnit.MILLISECONDS);
        }
        return t;
    }

    /**
     * 存储对象到 Redis 缓存中，指定过期时间和时间单位
     * 支持基本类型（String、Long、Integer）和复杂对象（自动 JSON 序列化）
     * 
     * @param key      缓存键
     * @param t        要缓存的对象
     * @param time     过期时间
     * @param timeUnit 时间单位
     * @param <T>      对象类型
     * @return 返回存储的对象
     */
    public <T> T set(String key, T t, int time, TimeUnit timeUnit) {
        if (t != null) {
            String value = t.toString();
            if (!(t instanceof String || t instanceof Long || t instanceof Integer)) {
                value = JSUtil.toJsonString(t);
            }
            stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
        }
        return t;
    }

    /**
     * 从 Redis 缓存中获取字符串值
     * 
     * @param key 缓存键
     * @return 缓存的字符串值，如果不存在返回 null
     */
    public String get(String key) {
        if (null != key) {
            return stringRedisTemplate.opsForValue().get(key);
        }
        return null;
    }

    /**
     * 从 Redis 缓存中获取指定类型的对象
     * 自动将 JSON 字符串反序列化为指定类型的对象
     * 
     * @param key   缓存键
     * @param clazz 目标对象的 Class 类型
     * @param <T>   对象类型
     * @return 反序列化后的对象，如果不存在或反序列化失败返回 null
     */
    public <T> T get(String key, Class<T> clazz) {
        String value = get(key);
        if (StringUtils.hasText(value)) {
            return JSUtil.parseObject(value, clazz);
        }
        return null;
    }

    /**
     * 从 Redis 缓存中获取指定类型的对象列表
     * 自动将 JSON 数组字符串反序列化为指定类型的对象列表
     * 
     * @param key   缓存键
     * @param clazz 列表元素的 Class 类型
     * @param <T>   对象类型
     * @return 反序列化后的对象列表，如果不存在或反序列化失败返回空列表
     */
    public <T> List<T> list(String key, Class<T> clazz) {
        return JSUtil.parseArray(get(key), clazz);
    }

    /**
     * 从 Redis 缓存中删除指定键的数据
     * 
     * @param key 要删除的缓存键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }


}
