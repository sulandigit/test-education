package com.roncoo.education.common.sharding.utils;

/**
 * 分片工具类
 * 提供分库分表相关的工具方法
 * 
 * @author sharding
 */
public class ShardingUtils {
    
    /**
     * 计算用户分库索引
     * 
     * @param userId 用户ID
     * @param databaseCount 数据库数量
     * @return 分库索引
     */
    public static int calculateUserDatabaseIndex(Long userId, int databaseCount) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }
        return (int) (userId % databaseCount);
    }
    
    /**
     * 计算用户分表索引
     * 
     * @param userId 用户ID
     * @param tableCount 表数量
     * @return 分表索引
     */
    public static int calculateUserTableIndex(Long userId, int tableCount) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }
        return (int) (userId % tableCount);
    }
    
    /**
     * 计算课程分库索引
     * 
     * @param courseId 课程ID
     * @param databaseCount 数据库数量
     * @return 分库索引
     */
    public static int calculateCourseDatabaseIndex(Long courseId, int databaseCount) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("课程ID不能为空或小于等于0");
        }
        return (int) (courseId % databaseCount);
    }
    
    /**
     * 计算课程分表索引
     * 
     * @param courseId 课程ID
     * @param tableCount 表数量
     * @return 分表索引
     */
    public static int calculateCourseTableIndex(Long courseId, int tableCount) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("课程ID不能为空或小于等于0");
        }
        return (int) (courseId % tableCount);
    }
    
    /**
     * 根据用户ID构造用户库名称
     * 
     * @param userId 用户ID
     * @param databaseCount 数据库数量
     * @return 数据库名称
     */
    public static String buildUserDatabaseName(Long userId, int databaseCount) {
        int dbIndex = calculateUserDatabaseIndex(userId, databaseCount);
        return String.format("user-db-%d", dbIndex);
    }
    
    /**
     * 根据用户ID构造用户表名称
     * 
     * @param tableName 逻辑表名
     * @param userId 用户ID
     * @param tableCount 表数量
     * @return 物理表名
     */
    public static String buildUserTableName(String tableName, Long userId, int tableCount) {
        int tableIndex = calculateUserTableIndex(userId, tableCount);
        return String.format("%s_%d", tableName, tableIndex);
    }
    
    /**
     * 根据课程ID构造课程库名称
     * 
     * @param courseId 课程ID
     * @param databaseCount 数据库数量
     * @return 数据库名称
     */
    public static String buildCourseDatabaseName(Long courseId, int databaseCount) {
        int dbIndex = calculateCourseDatabaseIndex(courseId, databaseCount);
        return String.format("course-db-%d", dbIndex);
    }
    
    /**
     * 根据课程ID构造课程表名称
     * 
     * @param tableName 逻辑表名
     * @param courseId 课程ID
     * @param tableCount 表数量
     * @return 物理表名
     */
    public static String buildCourseTableName(String tableName, Long courseId, int tableCount) {
        int tableIndex = calculateCourseTableIndex(courseId, tableCount);
        return String.format("%s_%d", tableName, tableIndex);
    }
    
    /**
     * 验证分片键是否有效
     * 
     * @param shardingKey 分片键
     * @return 是否有效
     */
    public static boolean isValidShardingKey(Long shardingKey) {
        return shardingKey != null && shardingKey > 0;
    }
    
    /**
     * 获取分表范围（用于范围查询）
     * 
     * @param start 起始值
     * @param end 结束值
     * @param tableCount 表数量
     * @return 涉及的表索引数组
     */
    public static int[] getTableIndexRange(Long start, Long end, int tableCount) {
        if (start == null || end == null || start > end) {
            throw new IllegalArgumentException("起始值和结束值不能为空，且起始值不能大于结束值");
        }
        
        // 计算涉及的表索引
        java.util.Set<Integer> indexSet = new java.util.LinkedHashSet<>();
        for (long i = start; i <= end; i++) {
            int tableIndex = (int) (i % tableCount);
            indexSet.add(tableIndex);
        }
        
        return indexSet.stream().mapToInt(Integer::intValue).toArray();
    }
}