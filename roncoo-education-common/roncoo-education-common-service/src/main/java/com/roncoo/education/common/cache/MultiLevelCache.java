package com.roncoo.education.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.concurrent.Callable;

/**
 * 多级缓存实现
 * 实现L1(Caffeine本地缓存) + L2(Redis分布式缓存)的读写策略
 *
 * @author wujing
 */
@Slf4j
public class MultiLevelCache implements Cache {

    /**
     * 缓存名称
     */
    private final String name;

    /**
     * L1本地缓存（Caffeine）
     */
    private final Cache caffeineCache;

    /**
     * L2分布式缓存（Redis）
     */
    private final Cache redisCache;

    public MultiLevelCache(String name, Cache caffeineCache, Cache redisCache) {
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        log.debug("多级缓存查询 - 缓存名: {}, Key: {}", name, key);
        
        // 先查本地缓存
        ValueWrapper localValue = caffeineCache.get(key);
        if (localValue != null) {
            log.debug("本地缓存命中 - 缓存名: {}, Key: {}", name, key);
            return localValue;
        }
        
        // 本地缓存未命中，查询Redis缓存
        ValueWrapper redisValue = redisCache.get(key);
        if (redisValue != null) {
            log.debug("Redis缓存命中 - 缓存名: {}, Key: {}", name, key);
            // 将Redis中的数据写入本地缓存
            caffeineCache.put(key, redisValue.get());
            return redisValue;
        }
        
        log.debug("缓存未命中 - 缓存名: {}, Key: {}", name, key);
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            Object value = wrapper.get();
            if (value != null && type.isAssignableFrom(value.getClass())) {
                return type.cast(value);
            }
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            @SuppressWarnings("unchecked")
            T value = (T) wrapper.get();
            return value;
        }
        
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            log.error("缓存加载失败 - 缓存名: {}, Key: {}", name, key, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        log.debug("多级缓存写入 - 缓存名: {}, Key: {}", name, key);
        
        // 同时写入本地缓存和Redis缓存
        caffeineCache.put(key, value);
        redisCache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        log.debug("多级缓存清除 - 缓存名: {}, Key: {}", name, key);
        
        // 同时清除本地缓存和Redis缓存
        caffeineCache.evict(key);
        redisCache.evict(key);
    }

    @Override
    public void clear() {
        log.info("清空多级缓存 - 缓存名: {}", name);
        
        // 同时清空本地缓存和Redis缓存
        caffeineCache.clear();
        redisCache.clear();
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    public CacheStats getStats() {
        CacheStats stats = new CacheStats();
        stats.setCacheName(name);
        
        // 获取Caffeine缓存统计
        if (caffeineCache instanceof CaffeineCache) {
            CaffeineCache caffeineCache1 = (CaffeineCache) caffeineCache;
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = 
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) caffeineCache1.getNativeCache();
            
            com.github.benmanes.caffeine.cache.stats.CacheStats caffeineStats = nativeCache.stats();
            stats.setCaffeineHitRate(caffeineStats.hitRate());
            stats.setCaffeineSize(nativeCache.estimatedSize());
            stats.setCaffeineEvictionCount(caffeineStats.evictionCount());
            stats.setCaffeineLoadTime(caffeineStats.averageLoadPenalty());
        }
        
        return stats;
    }

    /**
     * 获取本地缓存
     *
     * @return 本地缓存实例
     */
    public Cache getCaffeineCache() {
        return caffeineCache;
    }

    /**
     * 获取Redis缓存
     *
     * @return Redis缓存实例
     */
    public Cache getRedisCache() {
        return redisCache;
    }
}