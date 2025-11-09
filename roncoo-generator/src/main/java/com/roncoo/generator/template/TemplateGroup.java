package com.roncoo.generator.template;

import lombok.Data;

import java.util.List;

/**
 * 模板组
 * 
 * @author roncoo-generator
 */
@Data
public class TemplateGroup {
    
    /**
     * 组名称
     */
    private String name;
    
    /**
     * 组路径
     */
    private String path;
    
    /**
     * 组描述
     */
    private String description;
    
    /**
     * 模板列表
     */
    private List<TemplateInfo> templates;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 执行顺序
     */
    private int order = 100;
    
    /**
     * 组类型
     */
    private GroupType type = GroupType.CORE;
    
    /**
     * 获取模板数量
     * 
     * @return 模板数量
     */
    public int getTemplateCount() {
        return templates != null ? templates.size() : 0;
    }
    
    /**
     * 根据名称获取模板
     * 
     * @param templateName 模板名称
     * @return 模板信息
     */
    public TemplateInfo getTemplate(String templateName) {
        if (templates == null) {
            return null;
        }
        
        return templates.stream()
                .filter(template -> template.getName().equals(templateName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查是否包含指定模板
     * 
     * @param templateName 模板名称
     * @return 是否包含
     */
    public boolean containsTemplate(String templateName) {
        return getTemplate(templateName) != null;
    }
    
    /**
     * 组类型枚举
     */
    public enum GroupType {
        /**
         * 核心模板组
         */
        CORE,
        
        /**
         * 扩展模板组
         */
        EXTENSION,
        
        /**
         * 自定义模板组
         */
        CUSTOM
    }
}