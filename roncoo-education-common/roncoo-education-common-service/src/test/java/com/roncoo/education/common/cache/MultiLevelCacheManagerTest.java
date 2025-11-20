package com.roncoo.education.common.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 多级缓存管理器单元测试
 *
 * @author wujing
 */
@ExtendWith(MockitoExtension.class)
class MultiLevelCacheManagerTest {

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    private CaffeineConfiguration caffeineConfiguration;
    private MultiLevelCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        caffeineConfiguration = new CaffeineConfiguration();
        caffeineConfiguration.setMaximumSize(1000L);
        caffeineConfiguration.setExpireAfterWrite(30L);
        
        cacheManager = new MultiLevelCacheManager(caffeineConfiguration, redisConnectionFactory);
    }

    @Test
    void testGetCache() {
        // Given
        String cacheName = "test-cache";

        // When
        Cache cache = cacheManager.getCache(cacheName);

        // Then
        assertNotNull(cache);
        assertEquals(cacheName, cache.getName());
        assertTrue(cache instanceof MultiLevelCache);
    }

    @Test
    void testGetCacheReturnsSharedInstance() {
        // Given
        String cacheName = "test-cache";

        // When
        Cache cache1 = cacheManager.getCache(cacheName);
        Cache cache2 = cacheManager.getCache(cacheName);

        // Then
        assertSame(cache1, cache2);
    }

    @Test
    void testGetCacheNames() {
        // Given
        String cacheName1 = "cache1";
        String cacheName2 = "cache2";

        // When
        cacheManager.getCache(cacheName1);
        cacheManager.getCache(cacheName2);

        // Then
        assertTrue(cacheManager.getCacheNames().contains(cacheName1));
        assertTrue(cacheManager.getCacheNames().contains(cacheName2));
        assertEquals(2, cacheManager.getCacheNames().size());
    }

    @Test
    void testClearCache() {
        // Given
        String cacheName = "test-cache";
        Cache cache = cacheManager.getCache(cacheName);
        cache.put("key", "value");

        // When
        cacheManager.clearCache(cacheName);

        // Then
        assertNull(cache.get("key"));
    }

    @Test
    void testClearAll() {
        // Given
        Cache cache1 = cacheManager.getCache("cache1");
        Cache cache2 = cacheManager.getCache("cache2");
        cache1.put("key1", "value1");
        cache2.put("key2", "value2");

        // When
        cacheManager.clearAll();

        // Then
        assertNull(cache1.get("key1"));
        assertNull(cache2.get("key2"));
    }

    @Test
    void testGetCacheStats() {
        // Given
        String cacheName = "test-cache";
        Cache cache = cacheManager.getCache(cacheName);
        
        // 模拟一些缓存操作
        cache.put("key1", "value1");
        cache.get("key1"); // 命中
        cache.get("key2"); // 未命中

        // When
        CacheStats stats = cacheManager.getCacheStats(cacheName);

        // Then
        assertNotNull(stats);
        assertEquals(cacheName, stats.getCacheName());
    }
}