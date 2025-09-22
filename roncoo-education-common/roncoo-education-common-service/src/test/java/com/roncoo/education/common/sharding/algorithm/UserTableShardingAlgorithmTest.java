package com.roncoo.education.common.sharding.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 用户分表算法测试
 * 
 * @author test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户分表算法测试")
class UserTableShardingAlgorithmTest {

    private UserTableShardingAlgorithm algorithm;
    private Collection<String> availableTargetNames;

    @BeforeEach
    void setUp() {
        algorithm = new UserTableShardingAlgorithm();
        Properties props = new Properties();
        props.setProperty("sharding.count", "16");
        algorithm.init(props);
        
        // 模拟可用的表名称
        availableTargetNames = Arrays.asList(
            "users_0", "users_1", "users_2", "users_3",
            "users_4", "users_5", "users_6", "users_7",
            "users_8", "users_9", "users_10", "users_11",
            "users_12", "users_13", "users_14", "users_15"
        );
    }

    @Test
    @DisplayName("测试精确分片 - 用户ID为1")
    void testDoSharding_PreciseValue_UserId1() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1L);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertEquals("users_1", result, "用户ID为1时应该路由到users_1表");
    }

    @Test
    @DisplayName("测试精确分片 - 用户ID为16")
    void testDoSharding_PreciseValue_UserId16() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(16L);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertEquals("users_0", result, "用户ID为16时应该路由到users_0表");
    }

    @Test
    @DisplayName("测试精确分片 - 用户ID为1000017")
    void testDoSharding_PreciseValue_UserId1000017() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1000017L);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertEquals("users_1", result, "用户ID为1000017时应该路由到users_1表");
    }

    @Test
    @DisplayName("测试范围分片 - 用户ID从1到32")
    void testDoSharding_RangeValue_UserIdRange1To32() {
        // Arrange
        RangeShardingValue<Long> rangeShardingValue = mock(RangeShardingValue.class);
        com.google.common.collect.Range<Long> range = com.google.common.collect.Range.closed(1L, 32L);
        when(rangeShardingValue.getValueRange()).thenReturn(range);
        when(rangeShardingValue.getLogicTableName()).thenReturn("users");

        // Act
        Collection<String> result = algorithm.doSharding(availableTargetNames, rangeShardingValue);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertFalse(result.isEmpty(), "结果不应该为空集合");
        assertEquals(16, result.size(), "应该包含所有16张表");
        
        // 验证包含所有表
        for (int i = 0; i < 16; i++) {
            assertTrue(result.contains("users_" + i), "应该包含users_" + i + "表");
        }
    }

    @Test
    @DisplayName("测试范围分片 - 小范围用户ID从1到5")
    void testDoSharding_RangeValue_SmallRange() {
        // Arrange
        RangeShardingValue<Long> rangeShardingValue = mock(RangeShardingValue.class);
        com.google.common.collect.Range<Long> range = com.google.common.collect.Range.closed(1L, 5L);
        when(rangeShardingValue.getValueRange()).thenReturn(range);
        when(rangeShardingValue.getLogicTableName()).thenReturn("users");

        // Act
        Collection<String> result = algorithm.doSharding(availableTargetNames, rangeShardingValue);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertFalse(result.isEmpty(), "结果不应该为空集合");
        assertTrue(result.size() <= 16, "结果数量不应该超过表数量");
        
        // 验证包含的表
        assertTrue(result.contains("users_1"), "应该包含users_1表");
        assertTrue(result.contains("users_2"), "应该包含users_2表");
        assertTrue(result.contains("users_3"), "应该包含users_3表");
        assertTrue(result.contains("users_4"), "应该包含users_4表");
        assertTrue(result.contains("users_5"), "应该包含users_5表");
    }

    @Test
    @DisplayName("测试表不存在异常")
    void testDoSharding_TableNotExists() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1L);
        when(shardingValue.getLogicTableName()).thenReturn("users");
        
        Collection<String> invalidTargetNames = Arrays.asList("invalid_table_0", "invalid_table_1");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> algorithm.doSharding(invalidTargetNames, shardingValue),
            "应该抛出IllegalArgumentException异常"
        );
        
        assertTrue(exception.getMessage().contains("表"), "异常信息应该包含表相关描述");
        assertTrue(exception.getMessage().contains("不存在"), "异常信息应该包含不存在的描述");
    }

    @Test
    @DisplayName("测试算法类型")
    void testGetType() {
        // Act
        String type = algorithm.getType();

        // Assert
        assertEquals("USER_TABLE_INLINE", type, "算法类型应该为USER_TABLE_INLINE");
    }

    @Test
    @DisplayName("测试属性获取")
    void testGetProps() {
        // Act
        Properties props = algorithm.getProps();

        // Assert
        assertNotNull(props, "属性不应该为空");
        assertEquals("16", props.getProperty("sharding.count"), "分表数量应该为16");
    }

    @Test
    @DisplayName("测试不同逻辑表名")
    void testDoSharding_DifferentLogicTableName() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1L);
        when(shardingValue.getLogicTableName()).thenReturn("lecturer");
        
        Collection<String> lecturerTables = Arrays.asList(
            "lecturer_0", "lecturer_1", "lecturer_2", "lecturer_3",
            "lecturer_4", "lecturer_5", "lecturer_6", "lecturer_7"
        );

        // Act
        String result = algorithm.doSharding(lecturerTables, shardingValue);

        // Assert
        assertEquals("lecturer_1", result, "讲师ID为1时应该路由到lecturer_1表");
    }

    @Test
    @DisplayName("测试边界值 - 最大长整型值")
    void testDoSharding_MaxLongValue() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(Long.MAX_VALUE);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertTrue(availableTargetNames.contains(result), "结果应该在可用表列表中");
    }
}