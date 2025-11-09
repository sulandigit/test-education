package com.roncoo.generator.plugin;

import com.roncoo.generator.context.ColumnMetadata;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Swagger文档生成插件
 * 
 * @author roncoo-generator
 */
@Slf4j
public class SwaggerGeneratorPlugin implements GeneratorPlugin {
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * Swagger版本
     */
    private String swaggerVersion = "3.0";
    
    /**
     * API标题
     */
    private String apiTitle = "API 文档";
    
    /**
     * API版本
     */
    private String apiVersion = "1.0.0";
    
    /**
     * API描述
     */
    private String apiDescription = "自动生成的API文档";
    
    @Override
    public String getName() {
        return "SwaggerGenerator";
    }
    
    @Override
    public String getDescription() {
        return "Swagger API文档生成插件";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public int getOrder() {
        return 150; // 在代码格式化之前执行
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        if (properties != null) {
            enabled = (Boolean) properties.getOrDefault("enabled", true);
            swaggerVersion = (String) properties.getOrDefault("swaggerVersion", "3.0");
            apiTitle = (String) properties.getOrDefault("apiTitle", "API 文档");
            apiVersion = (String) properties.getOrDefault("apiVersion", "1.0.0");
            apiDescription = (String) properties.getOrDefault("apiDescription", "自动生成的API文档");
        }
        
        log.info("Swagger文档生成插件初始化完成 - 版本: {}, 标题: {}", swaggerVersion, apiTitle);
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        if (!enabled) {
            return;
        }
        
        try {
            // 为Controller文件添加Swagger注解
            for (GeneratedFile file : generatedFiles) {
                if (isControllerFile(file)) {
                    String enhancedContent = addSwaggerAnnotations(file.getContent(), context);
                    file.setContent(enhancedContent);
                    log.debug("为Controller文件添加Swagger注解: {}", file.getFilePath());
                }
            }
            
            // 生成Swagger配置文件
            GeneratedFile swaggerConfig = generateSwaggerConfig(context);
            if (swaggerConfig != null) {
                generatedFiles.add(swaggerConfig);
                log.info("生成Swagger配置文件: {}", swaggerConfig.getFilePath());
            }
            
        } catch (Exception e) {
            log.error("生成Swagger文档失败", e);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 检查是否为Controller文件
     * 
     * @param file 生成的文件
     * @return 是否为Controller文件
     */
    private boolean isControllerFile(GeneratedFile file) {
        return file.getFilePath().contains("controller") && file.getFilePath().endsWith(".java");
    }
    
    /**
     * 为Controller添加Swagger注解
     * 
     * @param content 原始内容
     * @param context 模板上下文
     * @return 增强后的内容
     */
    private String addSwaggerAnnotations(String content, TemplateContext context) {
        StringBuilder enhanced = new StringBuilder();
        String[] lines = content.split("\n");
        boolean inImportSection = false;
        boolean classAnnotationAdded = false;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // 添加Swagger导入
            if (trimmedLine.startsWith("import ") && !inImportSection) {
                inImportSection = true;
                enhanced.append(line).append("\n");
                enhanced.append("import io.swagger.v3.oas.annotations.Operation;\n");
                enhanced.append("import io.swagger.v3.oas.annotations.Parameter;\n");
                enhanced.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
                enhanced.append("import io.swagger.v3.oas.annotations.responses.ApiResponse;\n");
                continue;
            }
            
            // 为类添加@Tag注解
            if (trimmedLine.startsWith("public class") && !classAnnotationAdded) {
                String entityName = context.getTable().getEntityName();
                String tableComment = context.getTable().getTableComment();
                enhanced.append("@Tag(name = \"").append(entityName).append("\", description = \"");
                enhanced.append(tableComment != null ? tableComment : entityName + " 管理");
                enhanced.append("\")\n");
                classAnnotationAdded = true;
            }
            
            // 为方法添加@Operation注解
            if (trimmedLine.contains("@PostMapping") || 
                trimmedLine.contains("@GetMapping") || 
                trimmedLine.contains("@PutMapping") || 
                trimmedLine.contains("@DeleteMapping")) {
                
                String operation = inferOperation(trimmedLine);
                enhanced.append("    @Operation(summary = \"").append(operation).append("\")\n");
            }
            
            enhanced.append(line).append("\n");
        }
        
        return enhanced.toString();
    }
    
    /**
     * 推断操作类型
     * 
     * @param mappingLine 映射注解行
     * @return 操作描述
     */
    private String inferOperation(String mappingLine) {
        if (mappingLine.contains("@PostMapping")) {
            return "创建";
        } else if (mappingLine.contains("@GetMapping")) {
            if (mappingLine.contains("{id}")) {
                return "根据ID查询";
            } else {
                return "分页查询";
            }
        } else if (mappingLine.contains("@PutMapping")) {
            return "更新";
        } else if (mappingLine.contains("@DeleteMapping")) {
            return "删除";
        }
        return "操作";
    }
    
    /**
     * 生成Swagger配置文件
     * 
     * @param context 模板上下文
     * @return Swagger配置文件
     */
    private GeneratedFile generateSwaggerConfig(TemplateContext context) {
        StringBuilder config = new StringBuilder();
        String packageName = context.getPackageName();
        
        config.append("package ").append(packageName).append(".config;\n\n");
        
        // 导入语句
        config.append("import io.swagger.v3.oas.models.OpenAPI;\n");
        config.append("import io.swagger.v3.oas.models.info.Info;\n");
        config.append("import io.swagger.v3.oas.models.info.Contact;\n");
        config.append("import org.springframework.context.annotation.Bean;\n");
        config.append("import org.springframework.context.annotation.Configuration;\n\n");
        
        // 类定义
        config.append("/**\n");
        config.append(" * Swagger配置类\n");
        config.append(" * \n");
        config.append(" * @author ").append(context.getAuthor()).append("\n");
        config.append(" * @date ").append(context.getDate()).append("\n");
        config.append(" */\n");
        config.append("@Configuration\n");
        config.append("public class SwaggerConfig {\n\n");
        
        config.append("    @Bean\n");
        config.append("    public OpenAPI openAPI() {\n");
        config.append("        return new OpenAPI()\n");
        config.append("                .info(new Info()\n");
        config.append("                        .title(\"").append(apiTitle).append("\")\n");
        config.append("                        .description(\"").append(apiDescription).append("\")\n");
        config.append("                        .version(\"").append(apiVersion).append("\")\n");
        config.append("                        .contact(new Contact()\n");
        config.append("                                .name(\"").append(context.getAuthor()).append("\")\n");
        config.append("                                .email(\"developer@example.com\")));\n");
        config.append("    }\n");
        config.append("}\n");
        
        String filePath = "src/main/java/" + packageName.replace('.', '/') + "/config/SwaggerConfig.java";
        return new GeneratedFile(filePath, config.toString(), "Swagger配置");
    }
}