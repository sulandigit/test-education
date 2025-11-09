package com.roncoo.generator.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板上下文
 * 
 * @author roncoo-generator
 */
@Data
public class TemplateContext {
    
    /**
     * 表元数据
     */
    private TableMetadata table;
    
    /**
     * 项目元数据
     */
    private ProjectMetadata project;
    
    /**
     * 全局变量
     */
    private Map<String, Object> globalVariables = new HashMap<>();
    
    /**
     * 工具方法
     */
    private Map<String, Object> utilityMethods = new HashMap<>();
    
    /**
     * 配置参数
     */
    private Map<String, Object> config = new HashMap<>();
    
    /**
     * 添加全局变量
     * 
     * @param key 变量名
     * @param value 变量值
     */
    public void addGlobalVariable(String key, Object value) {
        globalVariables.put(key, value);
    }
    
    /**
     * 获取全局变量
     * 
     * @param key 变量名
     * @param defaultValue 默认值
     * @param <T> 变量类型
     * @return 变量值
     */
    @SuppressWarnings("unchecked")
    public <T> T getGlobalVariable(String key, T defaultValue) {
        Object value = globalVariables.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 添加工具方法
     * 
     * @param name 方法名
     * @param method 方法对象
     */
    public void addUtilityMethod(String name, Object method) {
        utilityMethods.put(name, method);
    }
    
    /**
     * 添加配置参数
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public void addConfig(String key, Object value) {
        config.put(key, value);
    }
    
    /**
     * 获取配置参数
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @param <T> 配置值类型
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key, T defaultValue) {
        Object value = config.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 获取实体名称
     * 
     * @return 实体名称
     */
    public String getEntityName() {
        return table != null ? table.getEntityName() : null;
    }
    
    /**
     * 获取表名
     * 
     * @return 表名
     */
    public String getTableName() {
        return table != null ? table.getTableName() : null;
    }
    
    /**
     * 获取包名
     * 
     * @return 包名
     */
    public String getPackageName() {
        return project != null ? project.getPackageName() : null;
    }
    
    /**
     * 获取作者
     * 
     * @return 作者
     */
    public String getAuthor() {
        return project != null ? project.getAuthor() : null;
    }
    
    /**
     * 获取日期
     * 
     * @return 日期
     */
    public String getDate() {
        return project != null ? project.getDate() : null;
    }
    
    /**
     * 获取完整的上下文数据（用于模板渲染）
     * 
     * @return 上下文数据
     */
    public Map<String, Object> toContextMap() {
        Map<String, Object> context = new HashMap<>();
        
        // 添加表数据
        if (table != null) {
            context.put("table", table);
            context.put("tableName", table.getTableName());
            context.put("entityName", table.getEntityName());
            context.put("columns", table.getColumns());
        }
        
        // 添加项目数据
        if (project != null) {
            context.put("project", project);
            context.put("packageName", project.getPackageName());
            context.put("author", project.getAuthor());
            context.put("date", project.getDate());
            context.put("year", project.getYear());
        }
        
        // 添加全局变量
        context.putAll(globalVariables);
        
        // 添加工具方法
        context.putAll(utilityMethods);
        
        // 添加配置参数
        context.put("cfg", config);
        
        return context;
    }
}