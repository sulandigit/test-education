package com.roncoo.education.common.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Caffeine配置类单元测试
 *
 * @author wujing
 */
class CaffeineConfigurationTest {

    @Test
    void testDefaultValues() {
        // Given
        CaffeineConfiguration configuration = new CaffeineConfiguration();

        // Then
        assertEquals(10000L, configuration.getMaximumSize());
        assertEquals(30L, configuration.getExpireAfterWrite());
        assertEquals(60L, configuration.getExpireAfterAccess());
        assertEquals(100, configuration.getInitialCapacity());
        assertTrue(configuration.isRecordStats());
        assertTrue(configuration.isRefreshAfterWrite());
        assertEquals(10L, configuration.getRefreshAfterWriteMinutes());
    }

    @Test
    void testSetValues() {
        // Given
        CaffeineConfiguration configuration = new CaffeineConfiguration();

        // When
        configuration.setMaximumSize(5000L);
        configuration.setExpireAfterWrite(15L);
        configuration.setExpireAfterAccess(30L);
        configuration.setInitialCapacity(50);
        configuration.setRecordStats(false);
        configuration.setRefreshAfterWrite(false);
        configuration.setRefreshAfterWriteMinutes(5L);

        // Then
        assertEquals(5000L, configuration.getMaximumSize());
        assertEquals(15L, configuration.getExpireAfterWrite());
        assertEquals(30L, configuration.getExpireAfterAccess());
        assertEquals(50, configuration.getInitialCapacity());
        assertFalse(configuration.isRecordStats());
        assertFalse(configuration.isRefreshAfterWrite());
        assertEquals(5L, configuration.getRefreshAfterWriteMinutes());
    }
}