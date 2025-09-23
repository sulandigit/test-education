package com.roncoo.generator.cache;

import lombok.Data;

/**
 * 缓存配置
 * 
 * @author roncoo-generator
 */
@Data
public class CacheConfig {
    
    /**
     * 最大缓存条目数
     */
    private int maxSize = 1000;
    
    /**
     * 缓存生存时间（毫秒）
     * 0或负数表示永不过期
     */
    private long ttlMillis = 30 * 60 * 1000; // 默认30分钟
    
    /**
     * 清理间隔（毫秒）
     */
    private long cleanupInterval = 5 * 60 * 1000; // 默认5分钟
    
    /**
     * 是否启用缓存
     */
    private boolean enabled = true;
    
    /**
     * 是否启用统计
     */
    private boolean statisticsEnabled = true;
    
    /**
     * 创建默认配置
     * 
     * @return 默认配置
     */
    public static CacheConfig defaultConfig() {
        return new CacheConfig();
    }
    
    /**
     * 创建无过期时间的配置
     * 
     * @param maxSize 最大大小
     * @return 配置
     */
    public static CacheConfig neverExpire(int maxSize) {
        CacheConfig config = new CacheConfig();
        config.setMaxSize(maxSize);
        config.setTtlMillis(0);
        return config;
    }
    
    /**
     * 创建指定TTL的配置
     * 
     * @param maxSize 最大大小
     * @param ttlMinutes TTL（分钟）
     * @return 配置
     */
    public static CacheConfig withTtl(int maxSize, int ttlMinutes) {
        CacheConfig config = new CacheConfig();
        config.setMaxSize(maxSize);
        config.setTtlMillis(ttlMinutes * 60 * 1000L);
        return config;
    }
}