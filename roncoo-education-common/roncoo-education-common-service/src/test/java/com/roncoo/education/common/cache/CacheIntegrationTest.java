package com.roncoo.education.common.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存集成测试
 * 测试Spring Cache注解与多级缓存的集成
 *
 * @author wujing
 */
@SpringBootTest(classes = {
    CacheIntegrationTest.TestConfiguration.class,
    CacheIntegrationTest.TestService.class,
    MultiLevelCacheAutoConfiguration.class,
    CaffeineConfiguration.class
})
@TestPropertySource(properties = {
    "spring.cache.multi-level.enabled=true",
    "spring.cache.caffeine.maximum-size=1000",
    "spring.cache.caffeine.expire-after-write=30"
})
class CacheIntegrationTest {

    @Autowired
    private TestService testService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheManagerIsMultiLevel() {
        assertNotNull(cacheManager);
        assertTrue(cacheManager instanceof MultiLevelCacheManager);
    }

    @Test
    void testCacheableAnnotation() {
        // Given
        String key = "test-key";
        String expectedValue = "test-value-" + key;

        // When - 第一次调用，应该执行方法并缓存结果
        String result1 = testService.getCachedValue(key);

        // Then
        assertEquals(expectedValue, result1);

        // When - 第二次调用，应该从缓存返回
        String result2 = testService.getCachedValue(key);

        // Then
        assertEquals(expectedValue, result2);
        assertEquals(result1, result2);

        // 验证方法只被调用了一次（通过服务的计数器）
        assertEquals(1, testService.getCallCount(key));
    }

    @Test
    void testCacheEviction() {
        // Given
        String key = "evict-test-key";
        String expectedValue = "test-value-" + key;

        // When - 缓存数据
        String result1 = testService.getCachedValue(key);
        assertEquals(expectedValue, result1);

        // When - 清除缓存
        testService.evictCache(key);

        // When - 再次调用，应该重新执行方法
        String result2 = testService.getCachedValue(key);

        // Then
        assertEquals(expectedValue, result2);
        // 方法应该被调用了两次
        assertEquals(2, testService.getCallCount(key));
    }

    @Test
    void testCacheStats() {
        // Given
        String cacheName = "test-cache";

        // When - 执行一些缓存操作
        testService.getCachedValue("stats-key-1");
        testService.getCachedValue("stats-key-2");
        testService.getCachedValue("stats-key-1"); // 这个应该命中缓存

        // Then - 验证统计信息
        if (cacheManager instanceof MultiLevelCacheManager) {
            MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
            CacheStats stats = multiLevelCacheManager.getCacheStats(cacheName);
            
            assertNotNull(stats);
            assertEquals(cacheName, stats.getCacheName());
            // 验证 Caffeine 缓存大小
            assertEquals(2, stats.getCaffeineSize()); // 应该有两个不同的key
        }
    }

    /**
     * 测试配置类
     */
    @Configuration
    @EnableCaching
    static class TestConfiguration {

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    /**
     * 测试服务类
     */
    @Service
    static class TestService {

        private final java.util.Map<String, Integer> callCounts = new java.util.concurrent.ConcurrentHashMap<>();

        @Cacheable(cacheNames = "test-cache", key = "#key")
        public String getCachedValue(String key) {
            // 记录方法调用次数
            callCounts.merge(key, 1, Integer::sum);
            
            // 模拟耗时操作
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "test-value-" + key;
        }

        @org.springframework.cache.annotation.CacheEvict(cacheNames = "test-cache", key = "#key")
        public void evictCache(String key) {
            // 清除缓存
        }

        public int getCallCount(String key) {
            return callCounts.getOrDefault(key, 0);
        }
    }
}