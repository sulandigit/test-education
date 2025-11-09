package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import lombok.Data;

import java.util.Map;

/**
 * 模板配置类
 * 
 * @author roncoo-generator
 */
@Data
public class TemplateConfig {
    
    /**
     * 模板根路径
     */
    private String templatePath = "template";
    
    /**
     * 模板引擎类型
     */
    private TemplateEngine engine = TemplateEngine.FREEMARKER;
    
    /**
     * 全局模板变量
     */
    private Map<String, Object> globalVariables;
    
    /**
     * 模板编码
     */
    private String encoding = "UTF-8";
    
    /**
     * 是否启用模板缓存
     */
    private boolean cacheEnabled = true;
    
    /**
     * 缓存更新延迟（秒）
     */
    private int cacheUpdateDelay = 5;
    
    /**
     * 自定义模板路径
     */
    private String customTemplatePath;
    
    /**
     * 模板组织配置
     */
    private TemplateOrganization organization;
    
    /**
     * 验证模板配置
     * 
     * @throws ConfigurationException 配置异常
     */
    public void validate() throws ConfigurationException {
        if (templatePath == null || templatePath.trim().isEmpty()) {
            throw new ConfigurationException("模板路径不能为空");
        }
        
        if (engine == null) {
            throw new ConfigurationException("模板引擎不能为空");
        }
        
        if (encoding == null || encoding.trim().isEmpty()) {
            throw new ConfigurationException("模板编码不能为空");
        }
        
        if (cacheUpdateDelay < 0) {
            throw new ConfigurationException("缓存更新延迟不能为负数");
        }
        
        // 验证模板组织配置
        if (organization != null) {
            organization.validate();
        }
    }
    
    /**
     * 获取实际的模板路径
     * 
     * @return 实际模板路径
     */
    public String getActualTemplatePath() {
        return customTemplatePath != null ? customTemplatePath : templatePath;
    }
    
    /**
     * 模板引擎枚举
     */
    public enum TemplateEngine {
        FREEMARKER("freemarker", "ftl"),
        VELOCITY("velocity", "vm"),
        THYMELEAF("thymeleaf", "html");
        
        private final String name;
        private final String extension;
        
        TemplateEngine(String name, String extension) {
            this.name = name;
            this.extension = extension;
        }
        
        public String getName() {
            return name;
        }
        
        public String getExtension() {
            return extension;
        }
    }
    
    /**
     * 模板组织配置内部类
     */
    @Data
    public static class TemplateOrganization {
        /**
         * 核心模板目录
         */
        private String coreTemplateDir = "core";
        
        /**
         * 扩展模板目录
         */
        private String extensionTemplateDir = "extension";
        
        /**
         * 自定义模板目录
         */
        private String customTemplateDir = "custom";
        
        /**
         * 模板分组配置
         */
        private Map<String, TemplateGroup> templateGroups;
        
        public void validate() throws ConfigurationException {
            if (coreTemplateDir == null || coreTemplateDir.trim().isEmpty()) {
                throw new ConfigurationException("核心模板目录不能为空");
            }
            
            if (extensionTemplateDir == null || extensionTemplateDir.trim().isEmpty()) {
                throw new ConfigurationException("扩展模板目录不能为空");
            }
            
            if (customTemplateDir == null || customTemplateDir.trim().isEmpty()) {
                throw new ConfigurationException("自定义模板目录不能为空");
            }
            
            // 验证模板分组配置
            if (templateGroups != null) {
                for (Map.Entry<String, TemplateGroup> entry : templateGroups.entrySet()) {
                    String groupName = entry.getKey();
                    TemplateGroup group = entry.getValue();
                    
                    if (groupName == null || groupName.trim().isEmpty()) {
                        throw new ConfigurationException("模板分组名称不能为空");
                    }
                    
                    if (group != null) {
                        group.validate();
                    }
                }
            }
        }
    }
    
    /**
     * 模板分组配置内部类
     */
    @Data
    public static class TemplateGroup {
        /**
         * 分组名称
         */
        private String name;
        
        /**
         * 分组描述
         */
        private String description;
        
        /**
         * 模板文件列表
         */
        private Map<String, String> templates;
        
        /**
         * 输出路径模式
         */
        private String outputPathPattern;
        
        /**
         * 是否启用此分组
         */
        private boolean enabled = true;
        
        public void validate() throws ConfigurationException {
            if (name == null || name.trim().isEmpty()) {
                throw new ConfigurationException("模板分组名称不能为空");
            }
            
            if (templates == null || templates.isEmpty()) {
                throw new ConfigurationException("模板分组中必须至少包含一个模板文件");
            }
            
            // 验证模板文件路径
            for (Map.Entry<String, String> template : templates.entrySet()) {
                String templateName = template.getKey();
                String templatePath = template.getValue();
                
                if (templateName == null || templateName.trim().isEmpty()) {
                    throw new ConfigurationException("模板名称不能为空");
                }
                
                if (templatePath == null || templatePath.trim().isEmpty()) {
                    throw new ConfigurationException("模板路径不能为空: " + templateName);
                }
            }
        }
    }
}