package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import com.roncoo.generator.validation.ConfigValidator;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 代码生成器主配置类
 * 
 * @author roncoo-generator
 */
@Data
public class GeneratorConfig {
    
    /**
     * 数据库配置
     */
    private DatabaseConfig database;
    
    /**
     * 项目配置
     */
    private ProjectConfig project;
    
    /**
     * 模板配置
     */
    private TemplateConfig template;
    
    /**
     * 插件配置列表
     */
    private List<PluginConfig> plugins;
    
    /**
     * 输出配置
     */
    private OutputConfig output;
    
    /**
     * 全局变量
     */
    private Map<String, Object> globalVariables;
    
    /**
     * 验证配置的有效性
     * 
     * @return 验证结果
     * @throws ConfigurationException 配置异常
     */
    public boolean validate() throws ConfigurationException {
        ConfigValidator validator = new ConfigValidator();
        
        // 验证必需的配置项
        if (database == null) {
            throw new ConfigurationException("数据库配置不能为空");
        }
        
        if (project == null) {
            throw new ConfigurationException("项目配置不能为空");
        }
        
        if (template == null) {
            throw new ConfigurationException("模板配置不能为空");
        }
        
        if (output == null) {
            throw new ConfigurationException("输出配置不能为空");
        }
        
        // 验证各个子配置
        database.validate();
        project.validate();
        template.validate();
        output.validate();
        
        // 验证插件配置
        if (plugins != null) {
            for (PluginConfig plugin : plugins) {
                plugin.validate();
            }
        }
        
        return true;
    }
    
    /**
     * 获取配置的摘要信息
     * 
     * @return 配置摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("代码生成器配置摘要:\n");
        summary.append("数据库: ").append(database.getUrl()).append("\n");
        summary.append("项目: ").append(project.getProjectName()).append("\n");
        summary.append("模板路径: ").append(template.getTemplatePath()).append("\n");
        summary.append("输出路径: ").append(output.getBaseDirectory()).append("\n");
        
        if (plugins != null && !plugins.isEmpty()) {
            summary.append("插件数量: ").append(plugins.size()).append("\n");
        }
        
        return summary.toString();
    }
}