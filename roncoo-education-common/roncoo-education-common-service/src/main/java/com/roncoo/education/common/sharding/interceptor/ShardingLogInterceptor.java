package com.roncoo.education.common.sharding.interceptor;

import com.roncoo.education.common.sharding.monitor.ShardingMonitor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 分库分表SQL路由日志拦截器
 * 记录SQL执行的路由信息和性能指标
 * 
 * @author interceptor
 */
@Component
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class ShardingLogInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ShardingLogInterceptor.class);
    
    @Autowired
    private ShardingMonitor shardingMonitor;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String sql = statementHandler.getBoundSql().getSql();
        
        // 记录SQL路由信息
        logSqlRouting(sql);
        
        try {
            // 执行原始方法
            Object result = invocation.proceed();
            
            // 记录成功执行的性能指标
            long duration = System.currentTimeMillis() - startTime;
            recordSuccessMetrics(sql, duration);
            
            return result;
        } catch (Exception e) {
            // 记录执行失败的信息
            long duration = System.currentTimeMillis() - startTime;
            recordFailureMetrics(sql, duration, e);
            throw e;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以通过properties配置拦截器参数
    }

    /**
     * 记录SQL路由信息
     */
    private void logSqlRouting(String sql) {
        if (logger.isDebugEnabled()) {
            // 分析SQL类型
            String sqlType = getSqlType(sql);
            String tableName = extractTableName(sql);
            String databaseInfo = extractDatabaseInfo(sql);
            
            logger.debug("分片SQL路由 - 类型: {}, 表: {}, 库: {}, SQL: {}", 
                sqlType, tableName, databaseInfo, sql);
        }
    }

    /**
     * 记录成功执行的性能指标
     */
    private void recordSuccessMetrics(String sql, long duration) {
        String sqlType = getSqlType(sql);
        
        switch (sqlType.toUpperCase()) {
            case "SELECT":
                if (shardingMonitor != null) {
                    shardingMonitor.recordQuery();
                    shardingMonitor.recordQueryTime(duration, TimeUnit.MILLISECONDS);
                }
                break;
            case "INSERT":
                if (shardingMonitor != null) {
                    shardingMonitor.recordInsert();
                    shardingMonitor.recordInsertTime(duration, TimeUnit.MILLISECONDS);
                }
                break;
            case "UPDATE":
                if (shardingMonitor != null) {
                    shardingMonitor.recordUpdate();
                    shardingMonitor.recordUpdateTime(duration, TimeUnit.MILLISECONDS);
                }
                break;
            case "DELETE":
                if (shardingMonitor != null) {
                    shardingMonitor.recordDelete();
                    shardingMonitor.recordDeleteTime(duration, TimeUnit.MILLISECONDS);
                }
                break;
        }
        
        // 记录慢SQL
        if (duration > 1000) { // 大于1秒的SQL
            logger.warn("慢SQL检测 - 执行时间: {}ms, SQL: {}", duration, sql);
        }
        
        // 检测跨库查询
        if (isCrossDatabaseQuery(sql)) {
            if (shardingMonitor != null) {
                shardingMonitor.recordCrossDatabaseQuery();
            }
            logger.info("跨库查询检测 - 执行时间: {}ms, SQL: {}", duration, sql);
        }
    }

    /**
     * 记录执行失败的指标
     */
    private void recordFailureMetrics(String sql, long duration, Exception e) {
        String sqlType = getSqlType(sql);
        logger.error("SQL执行失败 - 类型: {}, 执行时间: {}ms, 错误: {}, SQL: {}", 
            sqlType, duration, e.getMessage(), sql);
    }

    /**
     * 获取SQL类型
     */
    private String getSqlType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "UNKNOWN";
        }
        
        String trimmedSql = sql.trim().toUpperCase();
        if (trimmedSql.startsWith("SELECT")) {
            return "SELECT";
        } else if (trimmedSql.startsWith("INSERT")) {
            return "INSERT";
        } else if (trimmedSql.startsWith("UPDATE")) {
            return "UPDATE";
        } else if (trimmedSql.startsWith("DELETE")) {
            return "DELETE";
        } else {
            return "OTHER";
        }
    }

    /**
     * 提取表名
     */
    private String extractTableName(String sql) {
        try {
            String upperSql = sql.toUpperCase();
            int fromIndex = upperSql.indexOf("FROM");
            int intoIndex = upperSql.indexOf("INTO");
            int updateIndex = upperSql.indexOf("UPDATE");
            
            if (fromIndex != -1) {
                // SELECT ... FROM table_name
                String afterFrom = sql.substring(fromIndex + 4).trim();
                String[] parts = afterFrom.split("\\s+");
                return parts.length > 0 ? parts[0] : "UNKNOWN";
            } else if (intoIndex != -1) {
                // INSERT INTO table_name
                String afterInto = sql.substring(intoIndex + 4).trim();
                String[] parts = afterInto.split("\\s+");
                return parts.length > 0 ? parts[0] : "UNKNOWN";
            } else if (updateIndex != -1) {
                // UPDATE table_name
                String afterUpdate = sql.substring(updateIndex + 6).trim();
                String[] parts = afterUpdate.split("\\s+");
                return parts.length > 0 ? parts[0] : "UNKNOWN";
            }
            
            return "UNKNOWN";
        } catch (Exception e) {
            logger.debug("提取表名失败: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * 提取数据库信息
     */
    private String extractDatabaseInfo(String sql) {
        // 这里可以根据实际的分片规则来判断数据库
        // 简单实现，可以根据需要扩展
        try {
            String tableName = extractTableName(sql);
            if (tableName.contains("_")) {
                // 假设分表命名格式为 table_name_index
                String[] parts = tableName.split("_");
                if (parts.length >= 2) {
                    String lastPart = parts[parts.length - 1];
                    if (lastPart.matches("\\d+")) {
                        return "分片表: " + tableName;
                    }
                }
            }
            return "单表: " + tableName;
        } catch (Exception e) {
            logger.debug("提取数据库信息失败: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * 检测是否为跨库查询
     */
    private boolean isCrossDatabaseQuery(String sql) {
        try {
            // 简单检测：如果SQL包含多个分片表，可能是跨库查询
            String upperSql = sql.toUpperCase();
            
            // 检测JOIN操作
            if (upperSql.contains("JOIN") || upperSql.contains("UNION")) {
                return true;
            }
            
            // 检测子查询
            if (upperSql.contains("(SELECT")) {
                return true;
            }
            
            // 其他跨库查询特征...
            
            return false;
        } catch (Exception e) {
            logger.debug("检测跨库查询失败: {}", e.getMessage());
            return false;
        }
    }
}