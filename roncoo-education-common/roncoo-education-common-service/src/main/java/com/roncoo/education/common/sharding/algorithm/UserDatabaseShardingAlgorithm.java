package com.roncoo.education.common.sharding.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * 用户分库算法
 * 根据用户ID进行分库，采用取模算法
 * 
 * @author sharding
 */
public class UserDatabaseShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    private Properties props;
    private int shardingCount = 4; // 默认分4个库

    @Override
    public void init(Properties props) {
        this.props = props;
        if (props.containsKey("sharding.count")) {
            this.shardingCount = Integer.parseInt(props.getProperty("sharding.count"));
        }
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        // 获取分片值（用户ID）
        Long userId = preciseShardingValue.getValue();
        // 计算分库索引
        int dbIndex = (int) (userId % shardingCount);
        
        // 根据配置的数据源名称前缀构造目标数据源名称
        String logicTableName = preciseShardingValue.getLogicTableName();
        String targetDataSource = String.format("user-db-%d", dbIndex);
        
        // 验证目标数据源是否存在
        for (String dataSourceName : collection) {
            if (dataSourceName.equals(targetDataSource)) {
                return targetDataSource;
            }
        }
        
        throw new IllegalArgumentException(String.format("数据源 %s 不存在，可用数据源: %s", targetDataSource, collection));
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        // 范围查询时，需要计算涉及的所有分库
        Collection<String> result = new LinkedHashSet<>();
        
        // 获取范围值
        Long lowerEndpoint = rangeShardingValue.getValueRange().lowerEndpoint();
        Long upperEndpoint = rangeShardingValue.getValueRange().upperEndpoint();
        
        // 计算涉及的分库
        for (long userId = lowerEndpoint; userId <= upperEndpoint; userId++) {
            int dbIndex = (int) (userId % shardingCount);
            String targetDataSource = String.format("user-db-%d", dbIndex);
            
            // 验证目标数据源是否存在并添加到结果集
            for (String dataSourceName : collection) {
                if (dataSourceName.equals(targetDataSource)) {
                    result.add(targetDataSource);
                    break;
                }
            }
        }
        
        return result;
    }

    @Override
    public String getType() {
        return "USER_DATABASE_INLINE";
    }

    @Override
    public Properties getProps() {
        return props;
    }
}