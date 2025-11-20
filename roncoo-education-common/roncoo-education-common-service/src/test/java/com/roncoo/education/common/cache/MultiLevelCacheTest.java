package com.roncoo.education.common.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 多级缓存实现单元测试
 *
 * @author wujing
 */
@ExtendWith(MockitoExtension.class)
class MultiLevelCacheTest {

    @Mock
    private Cache caffeineCache;

    @Mock
    private Cache redisCache;

    private MultiLevelCache multiLevelCache;

    @BeforeEach
    void setUp() {
        multiLevelCache = new MultiLevelCache("test-cache", caffeineCache, redisCache);
    }

    @Test
    void testGetName() {
        assertEquals("test-cache", multiLevelCache.getName());
    }

    @Test
    void testGetNativeCache() {
        assertEquals(multiLevelCache, multiLevelCache.getNativeCache());
    }

    @Test
    void testGetFromCaffeineCache() {
        // Given
        String key = "test-key";
        String value = "test-value";
        Cache.ValueWrapper valueWrapper = () -> value;
        when(caffeineCache.get(key)).thenReturn(valueWrapper);

        // When
        Cache.ValueWrapper result = multiLevelCache.get(key);

        // Then
        assertNotNull(result);
        assertEquals(value, result.get());
        verify(caffeineCache).get(key);
        verify(redisCache, never()).get(key);
    }

    @Test
    void testGetFromRedisCache() {
        // Given
        String key = "test-key";
        String value = "test-value";
        Cache.ValueWrapper valueWrapper = () -> value;
        when(caffeineCache.get(key)).thenReturn(null);
        when(redisCache.get(key)).thenReturn(valueWrapper);

        // When
        Cache.ValueWrapper result = multiLevelCache.get(key);

        // Then
        assertNotNull(result);
        assertEquals(value, result.get());
        verify(caffeineCache).get(key);
        verify(redisCache).get(key);
        verify(caffeineCache).put(key, value);
    }

    @Test
    void testGetCacheMiss() {
        // Given
        String key = "test-key";
        when(caffeineCache.get(key)).thenReturn(null);
        when(redisCache.get(key)).thenReturn(null);

        // When
        Cache.ValueWrapper result = multiLevelCache.get(key);

        // Then
        assertNull(result);
        verify(caffeineCache).get(key);
        verify(redisCache).get(key);
    }

    @Test
    void testPut() {
        // Given
        String key = "test-key";
        String value = "test-value";

        // When
        multiLevelCache.put(key, value);

        // Then
        verify(caffeineCache).put(key, value);
        verify(redisCache).put(key, value);
    }

    @Test
    void testEvict() {
        // Given
        String key = "test-key";

        // When
        multiLevelCache.evict(key);

        // Then
        verify(caffeineCache).evict(key);
        verify(redisCache).evict(key);
    }

    @Test
    void testClear() {
        // When
        multiLevelCache.clear();

        // Then
        verify(caffeineCache).clear();
        verify(redisCache).clear();
    }

    @Test
    void testGetWithType() {
        // Given
        String key = "test-key";
        String value = "test-value";
        Cache.ValueWrapper valueWrapper = () -> value;
        when(caffeineCache.get(key)).thenReturn(valueWrapper);

        // When
        String result = multiLevelCache.get(key, String.class);

        // Then
        assertEquals(value, result);
    }

    @Test
    void testGetWithTypeReturnNull() {
        // Given
        String key = "test-key";
        when(caffeineCache.get(key)).thenReturn(null);
        when(redisCache.get(key)).thenReturn(null);

        // When
        String result = multiLevelCache.get(key, String.class);

        // Then
        assertNull(result);
    }

    @Test
    void testGetWithCallable() {
        // Given
        String key = "test-key";
        String value = "loaded-value";
        when(caffeineCache.get(key)).thenReturn(null);
        when(redisCache.get(key)).thenReturn(null);

        // When
        String result = multiLevelCache.get(key, () -> value);

        // Then
        assertEquals(value, result);
        verify(caffeineCache).put(key, value);
        verify(redisCache).put(key, value);
    }

    @Test
    void testGetWithCallableException() {
        // Given
        String key = "test-key";
        when(caffeineCache.get(key)).thenReturn(null);
        when(redisCache.get(key)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            multiLevelCache.get(key, () -> {
                throw new Exception("Test exception");
            });
        });
    }

    @Test
    void testGetCaffeineCache() {
        assertEquals(caffeineCache, multiLevelCache.getCaffeineCache());
    }

    @Test
    void testGetRedisCache() {
        assertEquals(redisCache, multiLevelCache.getRedisCache());
    }
}