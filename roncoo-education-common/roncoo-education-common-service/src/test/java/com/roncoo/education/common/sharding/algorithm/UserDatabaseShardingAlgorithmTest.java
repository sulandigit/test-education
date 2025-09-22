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
 * 用户分库算法测试
 * 
 * @author test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户分库算法测试")
class UserDatabaseShardingAlgorithmTest {

    private UserDatabaseShardingAlgorithm algorithm;
    private Collection<String> availableTargetNames;

    @BeforeEach
    void setUp() {
        algorithm = new UserDatabaseShardingAlgorithm();
        Properties props = new Properties();
        props.setProperty("sharding.count", "4");
        algorithm.init(props);
        
        // 模拟可用的数据源名称
        availableTargetNames = Arrays.asList("user-db-0", "user-db-1", "user-db-2", "user-db-3");
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
        assertEquals("user-db-1", result, "用户ID为1时应该路由到user-db-1");
    }

    @Test
    @DisplayName("测试精确分片 - 用户ID为4")
    void testDoSharding_PreciseValue_UserId4() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(4L);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertEquals("user-db-0", result, "用户ID为4时应该路由到user-db-0");
    }

    @Test
    @DisplayName("测试精确分片 - 用户ID为1000001")
    void testDoSharding_PreciseValue_UserId1000001() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1000001L);
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertEquals("user-db-1", result, "用户ID为1000001时应该路由到user-db-1");
    }

    @Test
    @DisplayName("测试范围分片 - 用户ID从1到10")
    void testDoSharding_RangeValue_UserIdRange1To10() {
        // Arrange
        RangeShardingValue<Long> rangeShardingValue = mock(RangeShardingValue.class);
        com.google.common.collect.Range<Long> range = com.google.common.collect.Range.closed(1L, 10L);
        when(rangeShardingValue.getValueRange()).thenReturn(range);
        when(rangeShardingValue.getLogicTableName()).thenReturn("users");

        // Act
        Collection<String> result = algorithm.doSharding(availableTargetNames, rangeShardingValue);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertFalse(result.isEmpty(), "结果不应该为空集合");
        assertTrue(result.size() <= 4, "结果数量不应该超过数据库数量");
        
        // 验证包含的数据库
        assertTrue(result.contains("user-db-1"), "应该包含user-db-1");
        assertTrue(result.contains("user-db-2"), "应该包含user-db-2");
        assertTrue(result.contains("user-db-3"), "应该包含user-db-3");
        assertTrue(result.contains("user-db-0"), "应该包含user-db-0");
    }

    @Test
    @DisplayName("测试数据源不存在异常")
    void testDoSharding_DataSourceNotExists() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1L);
        when(shardingValue.getLogicTableName()).thenReturn("users");
        
        Collection<String> invalidTargetNames = Arrays.asList("invalid-db-0", "invalid-db-1");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> algorithm.doSharding(invalidTargetNames, shardingValue),
            "应该抛出IllegalArgumentException异常"
        );
        
        assertTrue(exception.getMessage().contains("数据源"), "异常信息应该包含数据源相关描述");
        assertTrue(exception.getMessage().contains("不存在"), "异常信息应该包含不存在的描述");
    }

    @Test
    @DisplayName("测试算法类型")
    void testGetType() {
        // Act
        String type = algorithm.getType();

        // Assert
        assertEquals("USER_DATABASE_INLINE", type, "算法类型应该为USER_DATABASE_INLINE");
    }

    @Test
    @DisplayName("测试属性获取")
    void testGetProps() {
        // Act
        Properties props = algorithm.getProps();

        // Assert
        assertNotNull(props, "属性不应该为空");
        assertEquals("4", props.getProperty("sharding.count"), "分片数量应该为4");
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
        assertTrue(availableTargetNames.contains(result), "结果应该在可用数据源列表中");
    }

    @Test
    @DisplayName("测试边界值 - 最小长整型值")
    void testDoSharding_MinLongValue() {
        // Arrange
        PreciseShardingValue<Long> shardingValue = mock(PreciseShardingValue.class);
        when(shardingValue.getValue()).thenReturn(1L); // 使用1作为最小有效值
        when(shardingValue.getLogicTableName()).thenReturn("users");

        // Act
        String result = algorithm.doSharding(availableTargetNames, shardingValue);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertTrue(availableTargetNames.contains(result), "结果应该在可用数据源列表中");
    }
}