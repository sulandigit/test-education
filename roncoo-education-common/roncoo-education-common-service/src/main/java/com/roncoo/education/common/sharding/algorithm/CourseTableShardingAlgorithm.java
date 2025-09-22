package com.roncoo.education.common.sharding.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * 课程分表算法
 * 根据课程ID进行分表，采用取模算法
 * 
 * @author sharding
 */
public class CourseTableShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    private Properties props;
    private int shardingCount = 16; // 默认分16张表

    @Override
    public void init(Properties props) {
        this.props = props;
        if (props.containsKey("sharding.count")) {
            this.shardingCount = Integer.parseInt(props.getProperty("sharding.count"));
        }
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        // 获取分片值（课程ID）
        Long courseId = preciseShardingValue.getValue();
        // 计算分表索引
        int tableIndex = (int) (courseId % shardingCount);
        
        // 根据逻辑表名构造目标表名
        String logicTableName = preciseShardingValue.getLogicTableName();
        String targetTableName = String.format("%s_%d", logicTableName, tableIndex);
        
        // 验证目标表是否存在
        for (String tableName : collection) {
            if (tableName.equals(targetTableName)) {
                return targetTableName;
            }
        }
        
        throw new IllegalArgumentException(String.format("表 %s 不存在，可用表: %s", targetTableName, collection));
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        // 范围查询时，需要计算涉及的所有分表
        Collection<String> result = new LinkedHashSet<>();
        
        // 获取范围值
        Long lowerEndpoint = rangeShardingValue.getValueRange().lowerEndpoint();
        Long upperEndpoint = rangeShardingValue.getValueRange().upperEndpoint();
        
        // 计算涉及的分表
        for (long courseId = lowerEndpoint; courseId <= upperEndpoint; courseId++) {
            int tableIndex = (int) (courseId % shardingCount);
            String logicTableName = rangeShardingValue.getLogicTableName();
            String targetTableName = String.format("%s_%d", logicTableName, tableIndex);
            
            // 验证目标表是否存在并添加到结果集
            for (String tableName : collection) {
                if (tableName.equals(targetTableName)) {
                    result.add(targetTableName);
                    break;
                }
            }
        }
        
        return result;
    }

    @Override
    public String getType() {
        return "COURSE_TABLE_INLINE";
    }

    @Override
    public Properties getProps() {
        return props;
    }
}