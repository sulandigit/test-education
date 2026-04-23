package com.roncoo.education.dashboard.service.cache.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roncoo.education.dashboard.service.cache.CacheManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 缓存管理服务测试
 *
 * @author wujing
 * @date 2025-09-20
 */
@ExtendWith(MockitoExtension.class)
class CacheManagerServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    private CacheManagerService cacheManagerService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheManagerService = new CacheManagerServiceImpl(redisTemplate, objectMapper);
    }

    @Test
    void testCacheMetric() {
        // Given
        String metricType = "test_metric";
        Object value = "test_value";
        int expireSeconds = 60;

        // When
        cacheManagerService.cacheMetric(metricType, value, expireSeconds);

        // Then
        verify(valueOperations).set("dashboard:metric:" + metricType, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Test
    void testGetCachedMetric() {
        // Given
        String metricType = "test_metric";
        String expectedValue = "test_value";
        when(valueOperations.get("dashboard:metric:" + metricType)).thenReturn(expectedValue);

        // When
        String result = cacheManagerService.getCachedMetric(metricType, String.class);

        // Then
        assertEquals(expectedValue, result);
    }

    @Test
    void testCacheMetrics() {
        // Given
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("metric1", "value1");
        metrics.put("metric2", "value2");

        // When
        cacheManagerService.cacheMetrics(metrics);

        // Then
        verify(valueOperations).multiSet(any(Map.class));
    }

    @Test
    void testDeleteCachedMetric() {
        // Given
        String metricType = "test_metric";

        // When
        cacheManagerService.deleteCachedMetric(metricType);

        // Then
        verify(redisTemplate).delete("dashboard:metric:" + metricType);
    }

    @Test
    void testExistsCachedMetric() {
        // Given
        String metricType = "test_metric";
        when(redisTemplate.hasKey("dashboard:metric:" + metricType)).thenReturn(true);

        // When
        boolean result = cacheManagerService.existsCachedMetric(metricType);

        // Then
        assertTrue(result);
    }

    @Test
    void testSetExpire() {
        // Given
        String key = "test_key";
        int expireSeconds = 120;

        // When
        cacheManagerService.setExpire(key, expireSeconds);

        // Then
        verify(redisTemplate).expire(key, expireSeconds, TimeUnit.SECONDS);
    }

}