package com.roncoo.education.common.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存统计信息单元测试
 *
 * @author wujing
 */
class CacheStatsTest {

    @Test
    void testDefaultValues() {
        // Given
        CacheStats stats = new CacheStats();

        // Then
        assertNull(stats.getCacheName());
        assertEquals(0.0, stats.getCaffeineHitRate());
        assertEquals(0L, stats.getCaffeineSize());
        assertEquals(0L, stats.getCaffeineEvictionCount());
        assertEquals(0.0, stats.getCaffeineLoadTime());
        assertEquals(0.0, stats.getRedisHitRate());
        assertEquals(0L, stats.getRedisSize());
        assertEquals(0L, stats.getTotalRequestCount());
        assertEquals(0L, stats.getTotalHitCount());
    }

    @Test
    void testSetValues() {
        // Given
        CacheStats stats = new CacheStats();

        // When
        stats.setCacheName("test-cache");
        stats.setCaffeineHitRate(0.85);
        stats.setCaffeineSize(1000L);
        stats.setCaffeineEvictionCount(10L);
        stats.setCaffeineLoadTime(100.5);
        stats.setRedisHitRate(0.90);
        stats.setRedisSize(5000L);
        stats.setTotalRequestCount(1000L);
        stats.setTotalHitCount(850L);

        // Then
        assertEquals("test-cache", stats.getCacheName());
        assertEquals(0.85, stats.getCaffeineHitRate());
        assertEquals(1000L, stats.getCaffeineSize());
        assertEquals(10L, stats.getCaffeineEvictionCount());
        assertEquals(100.5, stats.getCaffeineLoadTime());
        assertEquals(0.90, stats.getRedisHitRate());
        assertEquals(5000L, stats.getRedisSize());
        assertEquals(1000L, stats.getTotalRequestCount());
        assertEquals(850L, stats.getTotalHitCount());
    }

    @Test
    void testGetTotalHitRate() {
        // Given
        CacheStats stats = new CacheStats();

        // When & Then - 没有请求时返回0
        assertEquals(0.0, stats.getTotalHitRate());

        // When - 设置请求和命中数据
        stats.setTotalRequestCount(1000L);
        stats.setTotalHitCount(850L);

        // Then
        assertEquals(0.85, stats.getTotalHitRate(), 0.001);
    }

    @Test
    void testGetTotalHitRateWithZeroRequests() {
        // Given
        CacheStats stats = new CacheStats();
        stats.setTotalRequestCount(0L);
        stats.setTotalHitCount(0L);

        // When & Then
        assertEquals(0.0, stats.getTotalHitRate());
    }
}