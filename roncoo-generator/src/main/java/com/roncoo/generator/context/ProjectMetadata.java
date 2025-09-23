package com.roncoo.generator.context;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目元数据
 * 
 * @author roncoo-generator
 */
@Data
public class ProjectMetadata {
    
    /**
     * 包名
     */
    private String packageName;
    
    /**
     * 模块名
     */
    private String moduleName;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 项目版本
     */
    private String version;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> properties = new HashMap<>();
    
    /**
     * 获取当前日期字符串
     * 
     * @return 日期字符串
     */
    public String getDate() {
        return createTime != null ? 
                createTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) : 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * 获取当前时间字符串
     * 
     * @return 时间字符串
     */
    public String getDateTime() {
        return createTime != null ? 
                createTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * 获取年份
     * 
     * @return 年份
     */
    public String getYear() {
        return createTime != null ? 
                String.valueOf(createTime.getYear()) : 
                String.valueOf(LocalDateTime.now().getYear());
    }
}