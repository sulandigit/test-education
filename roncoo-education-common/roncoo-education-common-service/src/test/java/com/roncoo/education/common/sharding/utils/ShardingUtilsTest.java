package com.roncoo.education.common.sharding.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分片工具类测试
 * 
 * @author test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("分片工具类测试")
class ShardingUtilsTest {

    @Test
    @DisplayName("测试用户分库索引计算")
    void testCalculateUserDatabaseIndex() {
        // Test case 1: userId = 1, databaseCount = 4
        assertEquals(1, ShardingUtils.calculateUserDatabaseIndex(1L, 4));
        
        // Test case 2: userId = 4, databaseCount = 4
        assertEquals(0, ShardingUtils.calculateUserDatabaseIndex(4L, 4));
        
        // Test case 3: userId = 1000001, databaseCount = 4
        assertEquals(1, ShardingUtils.calculateUserDatabaseIndex(1000001L, 4));
        
        // Test case 4: userId = 100, databaseCount = 8
        assertEquals(4, ShardingUtils.calculateUserDatabaseIndex(100L, 8));
    }

    @Test
    @DisplayName("测试用户分库索引计算 - 异常情况")
    void testCalculateUserDatabaseIndex_Exception() {
        // Test null userId
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.calculateUserDatabaseIndex(null, 4)
        );
        assertEquals("用户ID不能为空或小于等于0", exception1.getMessage());
        
        // Test zero userId
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.calculateUserDatabaseIndex(0L, 4)
        );
        assertEquals("用户ID不能为空或小于等于0", exception2.getMessage());
        
        // Test negative userId
        IllegalArgumentException exception3 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.calculateUserDatabaseIndex(-1L, 4)
        );
        assertEquals("用户ID不能为空或小于等于0", exception3.getMessage());
    }

    @Test
    @DisplayName("测试用户分表索引计算")
    void testCalculateUserTableIndex() {
        // Test case 1: userId = 1, tableCount = 16
        assertEquals(1, ShardingUtils.calculateUserTableIndex(1L, 16));
        
        // Test case 2: userId = 16, tableCount = 16
        assertEquals(0, ShardingUtils.calculateUserTableIndex(16L, 16));
        
        // Test case 3: userId = 1000017, tableCount = 16
        assertEquals(1, ShardingUtils.calculateUserTableIndex(1000017L, 16));
        
        // Test case 4: userId = 100, tableCount = 8
        assertEquals(4, ShardingUtils.calculateUserTableIndex(100L, 8));
    }

    @Test
    @DisplayName("测试用户分表索引计算 - 异常情况")
    void testCalculateUserTableIndex_Exception() {
        // Test null userId
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.calculateUserTableIndex(null, 16)
        );
        assertEquals("用户ID不能为空或小于等于0", exception.getMessage());
    }

    @Test
    @DisplayName("测试课程分库索引计算")
    void testCalculateCourseDatabaseIndex() {
        // Test case 1: courseId = 1, databaseCount = 2
        assertEquals(1, ShardingUtils.calculateCourseDatabaseIndex(1L, 2));
        
        // Test case 2: courseId = 2, databaseCount = 2
        assertEquals(0, ShardingUtils.calculateCourseDatabaseIndex(2L, 2));
        
        // Test case 3: courseId = 1000003, databaseCount = 2
        assertEquals(1, ShardingUtils.calculateCourseDatabaseIndex(1000003L, 2));
    }

    @Test
    @DisplayName("测试课程分库索引计算 - 异常情况")
    void testCalculateCourseDatabaseIndex_Exception() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.calculateCourseDatabaseIndex(null, 2)
        );
        assertEquals("课程ID不能为空或小于等于0", exception.getMessage());
    }

    @Test
    @DisplayName("测试课程分表索引计算")
    void testCalculateCourseTableIndex() {
        // Test case 1: courseId = 1, tableCount = 16
        assertEquals(1, ShardingUtils.calculateCourseTableIndex(1L, 16));
        
        // Test case 2: courseId = 16, tableCount = 16
        assertEquals(0, ShardingUtils.calculateCourseTableIndex(16L, 16));
    }

    @Test
    @DisplayName("测试构造用户库名称")
    void testBuildUserDatabaseName() {
        // Test case 1
        assertEquals("user-db-1", ShardingUtils.buildUserDatabaseName(1L, 4));
        
        // Test case 2
        assertEquals("user-db-0", ShardingUtils.buildUserDatabaseName(4L, 4));
        
        // Test case 3
        assertEquals("user-db-1", ShardingUtils.buildUserDatabaseName(1000001L, 4));
    }

    @Test
    @DisplayName("测试构造用户表名称")
    void testBuildUserTableName() {
        // Test case 1
        assertEquals("users_1", ShardingUtils.buildUserTableName("users", 1L, 16));
        
        // Test case 2
        assertEquals("users_0", ShardingUtils.buildUserTableName("users", 16L, 16));
        
        // Test case 3
        assertEquals("lecturer_1", ShardingUtils.buildUserTableName("lecturer", 1L, 8));
    }

    @Test
    @DisplayName("测试构造课程库名称")
    void testBuildCourseDatabaseName() {
        // Test case 1
        assertEquals("course-db-1", ShardingUtils.buildCourseDatabaseName(1L, 2));
        
        // Test case 2
        assertEquals("course-db-0", ShardingUtils.buildCourseDatabaseName(2L, 2));
    }

    @Test
    @DisplayName("测试构造课程表名称")
    void testBuildCourseTableName() {
        // Test case 1
        assertEquals("course_1", ShardingUtils.buildCourseTableName("course", 1L, 16));
        
        // Test case 2
        assertEquals("course_0", ShardingUtils.buildCourseTableName("course", 16L, 16));
    }

    @Test
    @DisplayName("测试验证分片键有效性")
    void testIsValidShardingKey() {
        // Valid cases
        assertTrue(ShardingUtils.isValidShardingKey(1L));
        assertTrue(ShardingUtils.isValidShardingKey(1000L));
        assertTrue(ShardingUtils.isValidShardingKey(Long.MAX_VALUE));
        
        // Invalid cases
        assertFalse(ShardingUtils.isValidShardingKey(null));
        assertFalse(ShardingUtils.isValidShardingKey(0L));
        assertFalse(ShardingUtils.isValidShardingKey(-1L));
    }

    @Test
    @DisplayName("测试获取分表范围")
    void testGetTableIndexRange() {
        // Test case 1: range 1-5, tableCount 16
        int[] result1 = ShardingUtils.getTableIndexRange(1L, 5L, 16);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result1);
        
        // Test case 2: range 15-17, tableCount 16
        int[] result2 = ShardingUtils.getTableIndexRange(15L, 17L, 16);
        assertArrayEquals(new int[]{15, 0, 1}, result2);
        
        // Test case 3: range 1-32, tableCount 16 (should include all tables)
        int[] result3 = ShardingUtils.getTableIndexRange(1L, 32L, 16);
        assertEquals(16, result3.length);
        
        // Verify all indices from 0 to 15 are present
        for (int i = 0; i < 16; i++) {
            boolean found = false;
            for (int index : result3) {
                if (index == i) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "索引 " + i + " 应该在结果中");
        }
    }

    @Test
    @DisplayName("测试获取分表范围 - 异常情况")
    void testGetTableIndexRange_Exception() {
        // Test null start
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.getTableIndexRange(null, 5L, 16)
        );
        assertEquals("起始值和结束值不能为空，且起始值不能大于结束值", exception1.getMessage());
        
        // Test null end
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.getTableIndexRange(1L, null, 16)
        );
        assertEquals("起始值和结束值不能为空，且起始值不能大于结束值", exception2.getMessage());
        
        // Test start > end
        IllegalArgumentException exception3 = assertThrows(
            IllegalArgumentException.class,
            () -> ShardingUtils.getTableIndexRange(5L, 3L, 16)
        );
        assertEquals("起始值和结束值不能为空，且起始值不能大于结束值", exception3.getMessage());
    }

    @Test
    @DisplayName("测试边界值情况")
    void testBoundaryValues() {
        // Test with minimum valid value
        assertEquals(1, ShardingUtils.calculateUserDatabaseIndex(1L, 4));
        assertEquals(1, ShardingUtils.calculateUserTableIndex(1L, 16));
        
        // Test with maximum long value
        int dbIndex = ShardingUtils.calculateUserDatabaseIndex(Long.MAX_VALUE, 4);
        assertTrue(dbIndex >= 0 && dbIndex < 4, "数据库索引应该在有效范围内");
        
        int tableIndex = ShardingUtils.calculateUserTableIndex(Long.MAX_VALUE, 16);
        assertTrue(tableIndex >= 0 && tableIndex < 16, "表索引应该在有效范围内");
    }

    @Test
    @DisplayName("测试大数据量场景")
    void testLargeDataScenario() {
        // 测试1000万用户ID的分布情况
        int[] dbCounts = new int[4];
        int[] tableCounts = new int[16];
        
        for (long userId = 1; userId <= 10000; userId++) {
            int dbIndex = ShardingUtils.calculateUserDatabaseIndex(userId, 4);
            int tableIndex = ShardingUtils.calculateUserTableIndex(userId, 16);
            
            dbCounts[dbIndex]++;
            tableCounts[tableIndex]++;
        }
        
        // 验证分布相对均匀（每个库应该有大约2500个用户）
        for (int count : dbCounts) {
            assertTrue(count > 2000 && count < 3000, 
                "数据库分布应该相对均匀，实际计数: " + count);
        }
        
        // 验证分表分布相对均匀（每个表应该有大约625个用户）
        for (int count : tableCounts) {
            assertTrue(count > 500 && count < 800, 
                "表分布应该相对均匀，实际计数: " + count);
        }
    }
}