package com.roncoo.generator.plugin;

import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 代码格式化插件
 * 
 * @author roncoo-generator
 */
@Slf4j
public class CodeFormatterPlugin implements GeneratorPlugin {
    
    /**
     * 是否启用格式化
     */
    private boolean enabled = true;
    
    /**
     * 格式化风格
     */
    private String style = "google";
    
    /**
     * 缩进大小
     */
    private int indentSize = 4;
    
    /**
     * 是否使用制表符
     */
    private boolean useTabs = false;
    
    @Override
    public String getName() {
        return "CodeFormatter";
    }
    
    @Override
    public String getDescription() {
        return "代码格式化插件，用于格式化生成的Java代码";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public int getOrder() {
        return 200; // 在其他插件之后执行
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        if (properties != null) {
            enabled = (Boolean) properties.getOrDefault("enabled", true);
            style = (String) properties.getOrDefault("style", "google");
            indentSize = (Integer) properties.getOrDefault("indentSize", 4);
            useTabs = (Boolean) properties.getOrDefault("useTabs", false);
        }
        
        log.info("代码格式化插件初始化完成 - 风格: {}, 缩进: {}, 制表符: {}", 
                style, indentSize, useTabs);
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        if (!enabled) {
            return;
        }
        
        for (GeneratedFile file : generatedFiles) {
            if (isJavaFile(file)) {
                try {
                    String formattedContent = formatJavaCode(file.getContent());
                    file.setContent(formattedContent);
                    log.debug("格式化Java文件: {}", file.getFilePath());
                } catch (Exception e) {
                    log.warn("格式化文件失败: {}", file.getFilePath(), e);
                }
            }
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 检查是否为Java文件
     * 
     * @param file 生成的文件
     * @return 是否为Java文件
     */
    private boolean isJavaFile(GeneratedFile file) {
        return file.getFilePath().endsWith(".java");
    }
    
    /**
     * 格式化Java代码
     * 
     * @param code Java代码
     * @return 格式化后的代码
     */
    private String formatJavaCode(String code) {
        // 简单的代码格式化实现
        StringBuilder formatted = new StringBuilder();
        String[] lines = code.split("\n");
        int indentLevel = 0;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // 处理缩进
            if (trimmedLine.endsWith("{")) {
                formatted.append(getIndent(indentLevel)).append(trimmedLine).append("\n");
                indentLevel++;
            } else if (trimmedLine.startsWith("}")) {
                indentLevel = Math.max(0, indentLevel - 1);
                formatted.append(getIndent(indentLevel)).append(trimmedLine).append("\n");
            } else if (!trimmedLine.isEmpty()) {
                formatted.append(getIndent(indentLevel)).append(trimmedLine).append("\n");
            } else {
                formatted.append("\n");
            }
        }
        
        return optimizeImports(formatted.toString());
    }
    
    /**
     * 获取缩进字符串
     * 
     * @param level 缩进级别
     * @return 缩进字符串
     */
    private String getIndent(int level) {
        if (useTabs) {
            return "\t".repeat(level);
        } else {
            return " ".repeat(level * indentSize);
        }
    }
    
    /**
     * 优化导入语句
     * 
     * @param code Java代码
     * @return 优化后的代码
     */
    private String optimizeImports(String code) {
        String[] lines = code.split("\n");
        StringBuilder result = new StringBuilder();
        boolean inImportSection = false;
        java.util.Set<String> imports = new java.util.TreeSet<>();
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.startsWith("import ")) {
                inImportSection = true;
                imports.add(trimmedLine);
            } else if (inImportSection && (trimmedLine.isEmpty() || 
                      trimmedLine.startsWith("package ") || 
                      trimmedLine.startsWith("public ") || 
                      trimmedLine.startsWith("@"))) {
                // 输出所有导入语句
                if (!imports.isEmpty()) {
                    for (String importStmt : imports) {
                        result.append(importStmt).append("\n");
                    }
                    result.append("\n");
                    imports.clear();
                }
                inImportSection = false;
                result.append(line).append("\n");
            } else {
                result.append(line).append("\n");
            }
        }
        
        return result.toString();
    }
}