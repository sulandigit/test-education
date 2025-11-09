package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import lombok.Data;

import java.util.Map;

/**
 * 项目配置类
 * 
 * @author roncoo-generator
 */
@Data
public class ProjectConfig {
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 基础包名前缀
     */
    private String packagePrefix;
    
    /**
     * 模块包名
     */
    private String packageName;
    
    /**
     * 作者信息
     */
    private String author = "roncoo-generator";
    
    /**
     * 项目版本
     */
    private String version = "1.0.0";
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 模块结构配置
     * key: 模块类型 (dao, service, controller等)
     * value: 相对路径
     */
    private Map<String, String> moduleStructure;
    
    /**
     * 命名策略配置
     */
    private NamingConfig naming;
    
    /**
     * 代码风格配置
     */
    private CodeStyleConfig codeStyle;
    
    /**
     * 验证项目配置
     * 
     * @throws ConfigurationException 配置异常
     */
    public void validate() throws ConfigurationException {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new ConfigurationException("项目名称不能为空");
        }
        
        if (packagePrefix == null || packagePrefix.trim().isEmpty()) {
            throw new ConfigurationException("包名前缀不能为空");
        }
        
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new ConfigurationException("模块包名不能为空");
        }
        
        // 验证包名格式
        if (!isValidPackageName(packagePrefix)) {
            throw new ConfigurationException("包名前缀格式不正确: " + packagePrefix);
        }
        
        if (!isValidPackageName(packageName)) {
            throw new ConfigurationException("模块包名格式不正确: " + packageName);
        }
        
        // 验证命名配置
        if (naming != null) {
            naming.validate();
        }
        
        // 验证代码风格配置
        if (codeStyle != null) {
            codeStyle.validate();
        }
    }
    
    /**
     * 验证包名格式
     * 
     * @param packageName 包名
     * @return 是否有效
     */
    private boolean isValidPackageName(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            return false;
        }
        
        // 简单的包名格式验证
        return packageName.matches("^[a-z][a-z0-9]*(\\.[-a-z0-9]*)*$");
    }
    
    /**
     * 获取完整的包路径
     * 
     * @return 完整包路径
     */
    public String getFullPackageName() {
        return packagePrefix + "." + packageName;
    }
    
    /**
     * 获取包路径（用于文件系统路径）
     * 
     * @return 包路径
     */
    public String getPackagePath() {
        return getFullPackageName().replace('.', '/');
    }
    
    /**
     * 命名策略配置内部类
     */
    @Data
    public static class NamingConfig {
        /**
         * 实体类命名策略
         */
        private String entityNaming = "PascalCase";
        
        /**
         * DAO类命名后缀
         */
        private String daoSuffix = "Dao";
        
        /**
         * Service类命名后缀
         */
        private String serviceSuffix = "Service";
        
        /**
         * Controller类命名后缀
         */
        private String controllerSuffix = "Controller";
        
        /**
         * 表名到类名的转换策略
         */
        private String tableToEntityStrategy = "underline_to_camel";
        
        /**
         * 字段名到属性名的转换策略
         */
        private String columnToPropertyStrategy = "underline_to_camel";
        
        public void validate() throws ConfigurationException {
            // 验证命名策略的有效性
            if (entityNaming == null || entityNaming.trim().isEmpty()) {
                throw new ConfigurationException("实体类命名策略不能为空");
            }
        }
    }
    
    /**
     * 代码风格配置内部类
     */
    @Data
    public static class CodeStyleConfig {
        /**
         * 是否使用Lombok注解
         */
        private boolean useLombok = true;
        
        /**
         * 是否生成Swagger注解
         */
        private boolean useSwagger = true;
        
        /**
         * 是否生成详细注释
         */
        private boolean generateComments = true;
        
        /**
         * 缩进字符（空格或制表符）
         */
        private String indentChar = "    ";
        
        /**
         * 行结束符
         */
        private String lineEnding = "\n";
        
        public void validate() throws ConfigurationException {
            if (indentChar == null) {
                throw new ConfigurationException("缩进字符不能为null");
            }
            
            if (lineEnding == null) {
                throw new ConfigurationException("行结束符不能为null");
            }
        }
    }
}