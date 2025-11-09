package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据库配置类
 * 
 * @author roncoo-generator
 */
@Data
public class DatabaseConfig {
    
    /**
     * 数据库连接URL
     */
    private String url;
    
    /**
     * 数据库用户名
     */
    private String username;
    
    /**
     * 数据库密码
     */
    private String password;
    
    /**
     * 数据库驱动类名
     */
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
    
    /**
     * 需要生成代码的表名列表
     * 支持通配符 % 表示所有表
     */
    private List<String> tableNames;
    
    /**
     * 表名前缀过滤
     */
    private String tablePrefix;
    
    /**
     * 数据库类型与Java类型的映射
     */
    private Map<String, String> typeMapping;
    
    /**
     * 数据库模式/库名
     */
    private String schema;
    
    /**
     * 连接池配置
     */
    private ConnectionPoolConfig connectionPool;
    
    /**
     * 验证数据库配置
     * 
     * @throws ConfigurationException 配置异常
     */
    public void validate() throws ConfigurationException {
        if (url == null || url.trim().isEmpty()) {
            throw new ConfigurationException("数据库连接URL不能为空");
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new ConfigurationException("数据库用户名不能为空");
        }
        
        if (password == null) {
            throw new ConfigurationException("数据库密码不能为null");
        }
        
        if (driverClassName == null || driverClassName.trim().isEmpty()) {
            throw new ConfigurationException("数据库驱动类名不能为空");
        }
        
        if (tableNames == null || tableNames.isEmpty()) {
            throw new ConfigurationException("表名列表不能为空");
        }
        
        // 验证URL格式
        if (!url.startsWith("jdbc:")) {
            throw new ConfigurationException("数据库URL格式不正确，应以'jdbc:'开头");
        }
    }
    
    /**
     * 获取实际的表名列表（处理通配符）
     * 
     * @return 处理后的表名列表
     */
    public List<String> getActualTableNames() {
        // 这里可以实现通配符处理逻辑
        // 如果包含%，则需要从数据库查询所有表名
        return tableNames;
    }
    
    /**
     * 连接池配置内部类
     */
    @Data
    public static class ConnectionPoolConfig {
        /**
         * 最大连接数
         */
        private int maxConnections = 10;
        
        /**
         * 最小连接数
         */
        private int minConnections = 1;
        
        /**
         * 连接超时时间（毫秒）
         */
        private long connectionTimeout = 30000;
        
        /**
         * 空闲超时时间（毫秒）
         */
        private long idleTimeout = 600000;
    }
}