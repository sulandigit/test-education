package com.roncoo.generator.template;

import lombok.Data;

import java.nio.file.Paths;

/**
 * 模板信息
 * 
 * @author roncoo-generator
 */
@Data
public class TemplateInfo {
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 模板文件路径
     */
    private String path;
    
    /**
     * 所属组
     */
    private String group;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 输出路径模式
     */
    private String outputPattern;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 模板类型
     */
    private TemplateType type = TemplateType.JAVA;
    
    /**
     * 执行顺序
     */
    private int order = 100;
    
    /**
     * 模板变量
     */
    private java.util.Map<String, Object> variables;
    
    /**
     * 获取相对路径
     * 
     * @return 相对路径
     */
    public String getRelativePath() {
        if (path == null) {
            return null;
        }
        
        // 如果路径包含组信息，则使用组名作为相对路径的一部分
        if (group != null && !group.isEmpty()) {
            return group + "/" + name;
        }
        
        return name;
    }
    
    /**
     * 获取文件扩展名
     * 
     * @return 文件扩展名
     */
    public String getFileExtension() {
        if (name == null) {
            return "";
        }
        
        int lastDot = name.lastIndexOf('.');
        return lastDot >= 0 ? name.substring(lastDot + 1) : "";
    }
    
    /**
     * 获取基础文件名（不含扩展名）
     * 
     * @return 基础文件名
     */
    public String getBaseName() {
        if (name == null) {
            return "";
        }
        
        int lastDot = name.lastIndexOf('.');
        return lastDot >= 0 ? name.substring(0, lastDot) : name;
    }
    
    /**
     * 检查是否为指定类型的模板
     * 
     * @param templateType 模板类型
     * @return 是否匹配
     */
    public boolean isType(TemplateType templateType) {
        return this.type == templateType;
    }
    
    /**
     * 获取模板的目标文件类型
     * 
     * @return 目标文件类型
     */
    public String getTargetFileType() {
        if (outputPattern == null) {
            return "";
        }
        
        int lastDot = outputPattern.lastIndexOf('.');
        return lastDot >= 0 ? outputPattern.substring(lastDot + 1) : "";
    }
    
    /**
     * 模板类型枚举
     */
    public enum TemplateType {
        /**
         * Java代码模板
         */
        JAVA,
        
        /**
         * XML配置模板
         */
        XML,
        
        /**
         * SQL脚本模板
         */
        SQL,
        
        /**
         * 配置文件模板
         */
        CONFIG,
        
        /**
         * 文档模板
         */
        DOCUMENT,
        
        /**
         * 其他类型
         */
        OTHER
    }
}