package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import lombok.Data;

import java.util.Map;

/**
 * 插件配置类
 * 
 * @author roncoo-generator
 */
@Data
public class PluginConfig {
    
    /**
     * 插件名称
     */
    private String name;
    
    /**
     * 插件类名（全限定名）
     */
    private String className;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 执行顺序（数值越小越先执行）
     */
    private int order = 100;
    
    /**
     * 插件配置参数
     */
    private Map<String, Object> properties;
    
    /**
     * 插件描述
     */
    private String description;
    
    /**
     * 插件版本
     */
    private String version;
    
    /**
     * 验证插件配置
     * 
     * @throws ConfigurationException 配置异常
     */
    public void validate() throws ConfigurationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ConfigurationException("插件名称不能为空");
        }
        
        if (className == null || className.trim().isEmpty()) {
            throw new ConfigurationException("插件类名不能为空: " + name);
        }
        
        // 验证类名格式
        if (!isValidClassName(className)) {
            throw new ConfigurationException("插件类名格式不正确: " + className);
        }
        
        if (order < 0) {
            throw new ConfigurationException("插件执行顺序不能为负数: " + name);
        }
    }
    
    /**
     * 验证类名格式
     * 
     * @param className 类名
     * @return 是否有效
     */
    private boolean isValidClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            return false;
        }
        
        // 简单的类名格式验证
        return className.matches("^[a-zA-Z_$][a-zA-Z0-9_$]*(\\.[-a-zA-Z0-9_$]*)*$");
    }
    
    /**
     * 获取插件属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @param <T> 属性值类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        if (properties == null || !properties.containsKey(key)) {
            return defaultValue;
        }
        
        try {
            return (T) properties.get(key);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
    
    /**
     * 设置插件属性值
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new java.util.HashMap<>();
        }
        properties.put(key, value);
    }
}