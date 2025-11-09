package com.roncoo.generator.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存管理器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class CacheManager {
    
    /**
     * 缓存存储
     */
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    /**
     * 缓存配置
     */
    private final CacheConfig config;
    
    /**
     * 定时清理任务执行器
     */
    private final ScheduledExecutorService cleanupExecutor;
    
    /**
     * 缓存统计信息
     */
    private final CacheStatistics statistics = new CacheStatistics();
    
    /**
     * 构造函数
     * 
     * @param config 缓存配置
     */
    public CacheManager(CacheConfig config) {
        this.config = config;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "cache-cleanup");
            thread.setDaemon(true);
            return thread;
        });
        
        // 启动定时清理任务
        startCleanupTask();
        
        log.info("缓存管理器初始化完成 - 最大容量: {}, TTL: {}ms", 
                config.getMaxSize(), config.getTtlMillis());
    }
    
    /**
     * 获取缓存值
     * 
     * @param key 缓存键
     * @param <T> 值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            statistics.incrementMisses();
            return null;
        }
        
        // 检查是否过期
        if (entry.isExpired()) {
            cache.remove(key);
            statistics.incrementMisses();
            return null;
        }
        
        // 更新访问时间
        entry.updateAccessTime();
        statistics.incrementHits();
        
        return (T) entry.getValue();
    }
    
    /**
     * 获取缓存值，如果不存在则加载
     * 
     * @param key 缓存键
     * @param loader 值加载器
     * @param <T> 值类型
     * @return 缓存值
     */
    public <T> T get(String key, Supplier<T> loader) {
        T value = get(key);
        if (value != null) {
            return value;
        }
        
        // 加载并缓存新值
        T newValue = loader.get();
        if (newValue != null) {
            put(key, newValue);
        }
        
        return newValue;
    }
    
    /**
     * 存储缓存值
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 值类型
     */
    public <T> void put(String key, T value) {
        if (key == null || value == null) {
            return;
        }
        
        // 检查缓存大小限制
        if (cache.size() >= config.getMaxSize()) {
            evictLeastRecentlyUsed();
        }
        
        CacheEntry entry = new CacheEntry(value, config.getTtlMillis());
        cache.put(key, entry);
        
        statistics.incrementPuts();
        
        log.debug("缓存存储: {} -> {}", key, value.getClass().getSimpleName());
    }
    
    /**
     * 移除缓存
     * 
     * @param key 缓存键
     */
    public void remove(String key) {
        CacheEntry removed = cache.remove(key);
        if (removed != null) {
            statistics.incrementEvictions();
            log.debug("缓存移除: {}", key);
        }
    }
    
    /**
     * 清空所有缓存
     */
    public void clear() {
        int size = cache.size();
        cache.clear();
        statistics.addEvictions(size);
        log.info("清空所有缓存，共清理 {} 个条目", size);
    }
    
    /**
     * 获取缓存大小
     * 
     * @return 缓存大小
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * 检查缓存是否为空
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * 检查是否包含指定键
     * 
     * @param key 缓存键
     * @return 是否包含
     */
    public boolean containsKey(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }
    
    /**
     * 获取缓存统计信息
     * 
     * @return 统计信息
     */
    public CacheStatistics getStatistics() {
        return statistics.copy();
    }
    
    /**
     * 驱逐最近最少使用的缓存条目
     */
    private void evictLeastRecentlyUsed() {
        if (cache.isEmpty()) {
            return;
        }
        
        String lruKey = null;
        long oldestAccess = Long.MAX_VALUE;
        
        for (var entry : cache.entrySet()) {
            long accessTime = entry.getValue().getLastAccessTime();
            if (accessTime < oldestAccess) {
                oldestAccess = accessTime;
                lruKey = entry.getKey();
            }
        }
        
        if (lruKey != null) {
            cache.remove(lruKey);
            statistics.incrementEvictions();
            log.debug("LRU驱逐缓存: {}", lruKey);
        }
    }
    
    /**
     * 启动定时清理任务
     */
    private void startCleanupTask() {
        cleanupExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredEntries,
                config.getCleanupInterval(),
                config.getCleanupInterval(),
                TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * 清理过期条目
     */
    private void cleanupExpiredEntries() {
        int removedCount = 0;
        
        var iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                removedCount++;
            }
        }
        
        if (removedCount > 0) {
            statistics.addEvictions(removedCount);
            log.debug("定时清理过期缓存，共清理 {} 个条目", removedCount);
        }
    }
    
    /**
     * 关闭缓存管理器
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        clear();
        log.info("缓存管理器已关闭");
    }
    
    /**
     * 缓存条目内部类
     */
    private static class CacheEntry {
        private final Object value;
        private final long createTime;
        private final long ttlMillis;
        private volatile long lastAccessTime;
        
        public CacheEntry(Object value, long ttlMillis) {
            this.value = value;
            this.ttlMillis = ttlMillis;
            this.createTime = System.currentTimeMillis();
            this.lastAccessTime = createTime;
        }
        
        public Object getValue() {
            return value;
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void updateAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            if (ttlMillis <= 0) {
                return false; // 永不过期
            }
            return System.currentTimeMillis() - createTime > ttlMillis;
        }
    }
}