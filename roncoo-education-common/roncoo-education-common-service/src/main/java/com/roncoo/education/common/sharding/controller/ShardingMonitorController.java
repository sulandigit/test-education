package com.roncoo.education.common.sharding.controller;

import com.roncoo.education.common.sharding.monitor.ShardingMonitor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 分库分表监控控制器
 * 提供分库分表性能监控数据的API接口
 * 
 * @author monitor
 */
@Api(tags = "分库分表监控")
@RestController
@RequestMapping("/sharding/monitor")
public class ShardingMonitorController {

    @Autowired
    private ShardingMonitor shardingMonitor;

    @ApiOperation(value = "获取分库分表性能统计", notes = "获取分库分表各类操作的性能统计数据")
    @GetMapping("/stats")
    public Map<String, Object> getShardingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 操作计数统计
        Map<String, Object> operationCounts = new HashMap<>();
        operationCounts.put("queryCount", shardingMonitor.getQueryCount());
        operationCounts.put("insertCount", shardingMonitor.getInsertCount());
        operationCounts.put("updateCount", shardingMonitor.getUpdateCount());
        operationCounts.put("deleteCount", shardingMonitor.getDeleteCount());
        operationCounts.put("crossDatabaseQueryCount", shardingMonitor.getCrossDatabaseQueryCount());
        stats.put("operationCounts", operationCounts);
        
        // 性能统计
        Map<String, Object> performanceStats = new HashMap<>();
        performanceStats.put("queryMeanTime", shardingMonitor.getQueryMeanTime());
        performanceStats.put("insertMeanTime", shardingMonitor.getInsertMeanTime());
        performanceStats.put("updateMeanTime", shardingMonitor.getUpdateMeanTime());
        performanceStats.put("deleteMeanTime", shardingMonitor.getDeleteMeanTime());
        stats.put("performanceStats", performanceStats);
        
        // 计算总操作数和平均性能
        double totalOperations = shardingMonitor.getQueryCount() + 
                                shardingMonitor.getInsertCount() + 
                                shardingMonitor.getUpdateCount() + 
                                shardingMonitor.getDeleteCount();
        stats.put("totalOperations", totalOperations);
        
        // 跨库查询比例
        double crossDatabaseQueryRatio = totalOperations > 0 ? 
            (shardingMonitor.getCrossDatabaseQueryCount() / totalOperations) * 100 : 0;
        stats.put("crossDatabaseQueryRatio", crossDatabaseQueryRatio);
        
        return stats;
    }

    @ApiOperation(value = "获取查询操作统计", notes = "获取查询操作的详细统计信息")
    @GetMapping("/query-stats")
    public Map<String, Object> getQueryStats() {
        Map<String, Object> queryStats = new HashMap<>();
        queryStats.put("count", shardingMonitor.getQueryCount());
        queryStats.put("meanTime", shardingMonitor.getQueryMeanTime());
        queryStats.put("unit", "milliseconds");
        return queryStats;
    }

    @ApiOperation(value = "获取插入操作统计", notes = "获取插入操作的详细统计信息")
    @GetMapping("/insert-stats")
    public Map<String, Object> getInsertStats() {
        Map<String, Object> insertStats = new HashMap<>();
        insertStats.put("count", shardingMonitor.getInsertCount());
        insertStats.put("meanTime", shardingMonitor.getInsertMeanTime());
        insertStats.put("unit", "milliseconds");
        return insertStats;
    }

    @ApiOperation(value = "获取更新操作统计", notes = "获取更新操作的详细统计信息")
    @GetMapping("/update-stats")
    public Map<String, Object> getUpdateStats() {
        Map<String, Object> updateStats = new HashMap<>();
        updateStats.put("count", shardingMonitor.getUpdateCount());
        updateStats.put("meanTime", shardingMonitor.getUpdateMeanTime());
        updateStats.put("unit", "milliseconds");
        return updateStats;
    }

    @ApiOperation(value = "获取删除操作统计", notes = "获取删除操作的详细统计信息")
    @GetMapping("/delete-stats")
    public Map<String, Object> getDeleteStats() {
        Map<String, Object> deleteStats = new HashMap<>();
        deleteStats.put("count", shardingMonitor.getDeleteCount());
        deleteStats.put("meanTime", shardingMonitor.getDeleteMeanTime());
        deleteStats.put("unit", "milliseconds");
        return deleteStats;
    }

    @ApiOperation(value = "获取跨库查询统计", notes = "获取跨库查询的统计信息")
    @GetMapping("/cross-database-stats")
    public Map<String, Object> getCrossDatabaseStats() {
        Map<String, Object> crossDbStats = new HashMap<>();
        crossDbStats.put("count", shardingMonitor.getCrossDatabaseQueryCount());
        
        // 计算跨库查询比例
        double totalQueries = shardingMonitor.getQueryCount();
        double crossDbRatio = totalQueries > 0 ? 
            (shardingMonitor.getCrossDatabaseQueryCount() / totalQueries) * 100 : 0;
        crossDbStats.put("ratio", crossDbRatio);
        crossDbStats.put("unit", "percent");
        
        return crossDbStats;
    }

    @ApiOperation(value = "获取系统健康状态", notes = "获取分库分表系统的健康状态")
    @GetMapping("/health")
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        // 基本健康检查
        boolean isHealthy = true;
        StringBuilder healthMessage = new StringBuilder();
        
        // 检查平均响应时间
        double avgQueryTime = shardingMonitor.getQueryMeanTime();
        if (avgQueryTime > 1000) { // 超过1秒
            isHealthy = false;
            healthMessage.append("查询平均响应时间过长(").append(avgQueryTime).append("ms); ");
        }
        
        double avgInsertTime = shardingMonitor.getInsertMeanTime();
        if (avgInsertTime > 2000) { // 超过2秒
            isHealthy = false;
            healthMessage.append("插入平均响应时间过长(").append(avgInsertTime).append("ms); ");
        }
        
        // 检查跨库查询比例
        double totalOperations = shardingMonitor.getQueryCount() + 
                               shardingMonitor.getInsertCount() + 
                               shardingMonitor.getUpdateCount() + 
                               shardingMonitor.getDeleteCount();
        
        if (totalOperations > 0) {
            double crossDbRatio = (shardingMonitor.getCrossDatabaseQueryCount() / totalOperations) * 100;
            if (crossDbRatio > 10) { // 跨库查询超过10%
                isHealthy = false;
                healthMessage.append("跨库查询比例过高(").append(String.format("%.2f", crossDbRatio)).append("%); ");
            }
        }
        
        health.put("status", isHealthy ? "HEALTHY" : "WARNING");
        health.put("message", healthMessage.length() > 0 ? healthMessage.toString() : "系统运行正常");
        health.put("timestamp", System.currentTimeMillis());
        
        // 添加关键指标
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("avgQueryTime", avgQueryTime);
        metrics.put("avgInsertTime", avgInsertTime);
        metrics.put("totalOperations", totalOperations);
        metrics.put("crossDbQueryCount", shardingMonitor.getCrossDatabaseQueryCount());
        health.put("metrics", metrics);
        
        return health;
    }

    @ApiOperation(value = "获取性能建议", notes = "基于统计数据提供性能优化建议")
    @GetMapping("/recommendations")
    public Map<String, Object> getPerformanceRecommendations() {
        Map<String, Object> recommendations = new HashMap<>();
        
        double avgQueryTime = shardingMonitor.getQueryMeanTime();
        double avgInsertTime = shardingMonitor.getInsertMeanTime();
        double totalOperations = shardingMonitor.getQueryCount() + 
                               shardingMonitor.getInsertCount() + 
                               shardingMonitor.getUpdateCount() + 
                               shardingMonitor.getDeleteCount();
        
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        
        // 查询性能建议
        if (avgQueryTime > 500) {
            suggestions.add("查询平均响应时间较长，建议检查索引优化和分片键设计");
        }
        
        // 插入性能建议
        if (avgInsertTime > 1000) {
            suggestions.add("插入平均响应时间较长，建议考虑批量插入或连接池优化");
        }
        
        // 跨库查询建议
        if (totalOperations > 0) {
            double crossDbRatio = (shardingMonitor.getCrossDatabaseQueryCount() / totalOperations) * 100;
            if (crossDbRatio > 5) {
                suggestions.add("跨库查询比例较高，建议优化业务逻辑或调整分片策略");
            }
        }
        
        // 负载均衡建议
        double queryRatio = totalOperations > 0 ? (shardingMonitor.getQueryCount() / totalOperations) * 100 : 0;
        if (queryRatio > 80) {
            suggestions.add("查询操作占比较高，建议启用读写分离或增加从库");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("系统性能表现良好，暂无特别建议");
        }
        
        recommendations.put("suggestions", suggestions);
        recommendations.put("timestamp", System.currentTimeMillis());
        
        return recommendations;
    }
}