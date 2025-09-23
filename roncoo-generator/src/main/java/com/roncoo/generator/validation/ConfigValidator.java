package com.roncoo.generator.validation;

import com.roncoo.generator.config.GeneratorConfig;
import com.roncoo.generator.exception.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置验证器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class ConfigValidator {
    
    /**
     * 验证配置的完整性和有效性
     * 
     * @param config 生成器配置
     * @throws ConfigurationException 配置异常
     */
    public void validate(GeneratorConfig config) throws ConfigurationException {
        log.info("开始验证配置...");
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证数据库连接
            validateDatabaseConnection(config, errors);
            
            // 验证表名有效性
            validateTableNames(config, errors);
            
            // 验证路径权限
            validatePathPermissions(config, errors);
            
            // 验证模板完整性
            validateTemplateIntegrity(config, errors);
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "配置验证失败:\n" + String.join("\n", errors);
                throw new ConfigurationException(errorMessage);
            }
            
            log.info("配置验证成功");
        } catch (Exception e) {
            if (e instanceof ConfigurationException) {
                throw e;
            }
            throw new ConfigurationException("配置验证过程中发生异常", e);
        }
    }
    
    /**
     * 验证数据库连接
     * 
     * @param config 生成器配置
     * @param errors 错误列表
     */
    private void validateDatabaseConnection(GeneratorConfig config, List<String> errors) {
        log.info("验证数据库连接...");
        
        try {
            // 加载数据库驱动
            Class.forName(config.getDatabase().getDriverClassName());
            
            // 尝试建立连接
            try (Connection connection = DriverManager.getConnection(
                    config.getDatabase().getUrl(),
                    config.getDatabase().getUsername(),
                    config.getDatabase().getPassword())) {
                
                if (connection.isValid(5)) {
                    log.info("数据库连接验证成功");
                } else {
                    errors.add("数据库连接无效");
                }
            }
        } catch (ClassNotFoundException e) {
            errors.add("数据库驱动类未找到: " + config.getDatabase().getDriverClassName());
        } catch (SQLException e) {
            errors.add("数据库连接失败: " + e.getMessage());
            // 提供常见问题的诊断信息
            provideDatabaseDiagnostics(e, errors);
        }
    }
    
    /**
     * 提供数据库连接问题的诊断信息
     * 
     * @param e SQL异常
     * @param errors 错误列表
     */
    private void provideDatabaseDiagnostics(SQLException e, List<String> errors) {
        String errorCode = String.valueOf(e.getErrorCode());
        String sqlState = e.getSQLState();
        
        if ("08001".equals(sqlState) || "08S01".equals(sqlState)) {
            errors.add("诊断: 网络连接问题，请检查数据库服务器是否启动，网络是否通畅");
        } else if ("28000".equals(sqlState)) {
            errors.add("诊断: 身份验证失败，请检查用户名和密码是否正确");
        } else if ("3D000".equals(sqlState)) {
            errors.add("诊断: 数据库不存在，请检查数据库名称是否正确");
        } else if ("42000".equals(sqlState)) {
            errors.add("诊断: 权限不足，请检查用户是否有足够的数据库权限");
        } else {
            errors.add("诊断: SQL错误代码=" + errorCode + ", SQL状态=" + sqlState);
        }
    }
    
    /**
     * 验证表名有效性
     * 
     * @param config 生成器配置
     * @param errors 错误列表
     */
    private void validateTableNames(GeneratorConfig config, List<String> errors) {
        log.info("验证表名有效性...");
        
        List<String> tableNames = config.getDatabase().getTableNames();
        if (tableNames == null || tableNames.isEmpty()) {
            errors.add("表名列表不能为空");
            return;
        }
        
        // 检查是否包含通配符
        boolean hasWildcard = tableNames.stream().anyMatch(name -> name.contains("%"));
        
        if (!hasWildcard) {
            // 验证具体表名是否存在
            validateSpecificTables(config, tableNames, errors);
        } else {
            log.info("检测到通配符，将在运行时验证表名");
        }
    }
    
    /**
     * 验证具体的表名是否存在
     * 
     * @param config 生成器配置
     * @param tableNames 表名列表
     * @param errors 错误列表
     */
    private void validateSpecificTables(GeneratorConfig config, List<String> tableNames, List<String> errors) {
        try (Connection connection = DriverManager.getConnection(
                config.getDatabase().getUrl(),
                config.getDatabase().getUsername(),
                config.getDatabase().getPassword())) {
            
            for (String tableName : tableNames) {
                if (!tableExists(connection, tableName)) {
                    errors.add("表不存在: " + tableName);
                }
            }
        } catch (SQLException e) {
            log.warn("无法验证表名有效性: " + e.getMessage());
        }
    }
    
    /**
     * 检查表是否存在
     * 
     * @param connection 数据库连接
     * @param tableName 表名
     * @return 表是否存在
     * @throws SQLException SQL异常
     */
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        return connection.getMetaData()
                .getTables(null, null, tableName, new String[]{"TABLE"})
                .next();
    }
    
    /**
     * 验证路径权限
     * 
     * @param config 生成器配置
     * @param errors 错误列表
     */
    private void validatePathPermissions(GeneratorConfig config, List<String> errors) {
        log.info("验证路径权限...");
        
        String outputPath = config.getOutput().getBaseDirectory();
        
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(outputPath);
            
            // 检查路径是否存在，不存在则尝试创建
            if (!java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
            }
            
            // 检查是否可写
            if (!java.nio.file.Files.isWritable(path)) {
                errors.add("输出路径不可写: " + outputPath);
                errors.add("修复建议: 请检查目录权限，确保应用程序有写入权限");
            }
            
        } catch (Exception e) {
            errors.add("路径权限验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证模板完整性
     * 
     * @param config 生成器配置
     * @param errors 错误列表
     */
    private void validateTemplateIntegrity(GeneratorConfig config, List<String> errors) {
        log.info("验证模板完整性...");
        
        String templatePath = config.getTemplate().getActualTemplatePath();
        
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(templatePath);
            
            if (!java.nio.file.Files.exists(path)) {
                errors.add("模板路径不存在: " + templatePath);
                return;
            }
            
            if (!java.nio.file.Files.isDirectory(path)) {
                errors.add("模板路径不是目录: " + templatePath);
                return;
            }
            
            // 检查必需的模板文件
            validateRequiredTemplates(path, errors);
            
        } catch (Exception e) {
            errors.add("模板完整性验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证必需的模板文件
     * 
     * @param templatePath 模板路径
     * @param errors 错误列表
     */
    private void validateRequiredTemplates(java.nio.file.Path templatePath, List<String> errors) {
        String[] requiredTemplates = {
                "entity.java.ftl",
                "dao.java.ftl",
                "service.java.ftl",
                "controller.java.ftl"
        };
        
        for (String templateFile : requiredTemplates) {
            java.nio.file.Path templateFilePath = templatePath.resolve(templateFile);
            if (!java.nio.file.Files.exists(templateFilePath)) {
                errors.add("缺失必需的模板文件: " + templateFile);
            }
        }
    }
}