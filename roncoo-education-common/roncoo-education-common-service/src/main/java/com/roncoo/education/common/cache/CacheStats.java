package com.roncoo.education.common.cache;

import lombok.Data;

/**
 * 缓存统计信息
 *
 * @author wujing
 */
@Data
public class CacheStats {

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * Caffeine缓存命中率
     */
    private double caffeineHitRate;

    /**
     * Caffeine缓存大小
     */
    private long caffeineSize;

    /**
     * Caffeine缓存驱逐次数
     */
    private long caffeineEvictionCount;

    /**
     * Caffeine缓存平均加载时间
     */
    private double caffeineLoadTime;

    /**
     * Redis缓存命中率
     */
    private double redisHitRate;

    /**
     * Redis缓存大小
     */
    private long redisSize;

    /**
     * 缓存总访问次数
     */
    private long totalRequestCount;

    /**
     * 缓存总命中次数
     */
    private long totalHitCount;

    /**
     * 缓存总命中率
     */
    public double getTotalHitRate() {
        return totalRequestCount > 0 ? (double) totalHitCount / totalRequestCount : 0.0;
    }
}