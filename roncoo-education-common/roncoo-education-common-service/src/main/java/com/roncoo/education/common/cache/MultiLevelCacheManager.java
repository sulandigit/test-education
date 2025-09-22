package com.roncoo.education.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存管理器
 * 实现L1(Caffeine本地缓存) + L2(Redis分布式缓存)的多级缓存架构
 *
 * @author wujing
 */
@Slf4j
public class MultiLevelCacheManager implements CacheManager {

    /**
     * 缓存容器，存储已创建的缓存实例
     */
    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * Caffeine缓存配置
     */
    private final CaffeineConfiguration caffeineConfiguration;

    /**
     * Redis连接工厂
     */
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * Redis缓存配置
     */
    private final RedisCacheConfiguration redisCacheConfiguration;

    /**
     * Redis缓存管理器
     */
    private final RedisCacheManager redisCacheManager;

    public MultiLevelCacheManager(CaffeineConfiguration caffeineConfiguration,
                                  RedisConnectionFactory redisConnectionFactory) {
        this.caffeineConfiguration = caffeineConfiguration;
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisCacheConfiguration = createRedisCacheConfiguration();
        this.redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::createMultiLevelCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    /**
     * 创建多级缓存实例
     *
     * @param name 缓存名称
     * @return 多级缓存实例
     */
    private Cache createMultiLevelCache(String name) {
        log.info("创建多级缓存实例: {}", name);
        
        // 创建Caffeine本地缓存
        Cache caffeineCache = createCaffeineCache(name);
        
        // 创建Redis分布式缓存
        Cache redisCache = createRedisCache(name);
        
        return new MultiLevelCache(name, caffeineCache, redisCache);
    }

    /**
     * 创建Caffeine本地缓存
     *
     * @param name 缓存名称
     * @return Caffeine缓存实例
     */
    private Cache createCaffeineCache(String name) {
        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(caffeineConfiguration.getMaximumSize())
                .expireAfterWrite(caffeineConfiguration.getExpireAfterWrite(), TimeUnit.MINUTES)
                .recordStats()
                .build();
        
        return new CaffeineCache(name + ":caffeine", caffeine);
    }

    /**
     * 创建Redis分布式缓存
     *
     * @param name 缓存名称
     * @return Redis缓存实例
     */
    private Cache createRedisCache(String name) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        return new RedisCache(name + ":redis", cacheWriter, redisCacheConfiguration);
    }

    /**
     * 创建Redis缓存配置
     *
     * @return Redis缓存配置
     */
    private RedisCacheConfiguration createRedisCacheConfiguration() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
    }

    /**
     * 获取缓存统计信息
     *
     * @param cacheName 缓存名称
     * @return 缓存统计信息
     */
    public CacheStats getCacheStats(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache instanceof MultiLevelCache) {
            return ((MultiLevelCache) cache).getStats();
        }
        return null;
    }

    /**
     * 清空所有缓存
     */
    public void clearAll() {
        log.info("清空所有多级缓存");
        cacheMap.values().forEach(Cache::clear);
    }

    /**
     * 清空指定缓存
     *
     * @param cacheName 缓存名称
     */
    public void clearCache(String cacheName) {
        log.info("清空指定缓存: {}", cacheName);
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}