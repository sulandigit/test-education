package com.roncoo.generator.plugin;

import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 文档生成插件
 * 
 * @author roncoo-generator
 */
@Slf4j
public class DocumentGeneratorPlugin implements GeneratorPlugin {
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 文档类型
     */
    private String documentType = "markdown";
    
    /**
     * 是否生成API文档
     */
    private boolean generateApiDoc = true;
    
    /**
     * 是否生成数据库文档
     */
    private boolean generateDbDoc = true;
    
    @Override
    public String getName() {
        return "DocumentGenerator";
    }
    
    @Override
    public String getDescription() {
        return "文档生成插件，用于生成API文档和数据库文档";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public int getOrder() {
        return 300; // 在代码生成后执行
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        if (properties != null) {
            enabled = (Boolean) properties.getOrDefault("enabled", true);
            documentType = (String) properties.getOrDefault("documentType", "markdown");
            generateApiDoc = (Boolean) properties.getOrDefault("generateApiDoc", true);
            generateDbDoc = (Boolean) properties.getOrDefault("generateDbDoc", true);
        }
        
        log.info("文档生成插件初始化完成 - 类型: {}, API文档: {}, 数据库文档: {}", 
                documentType, generateApiDoc, generateDbDoc);
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        if (!enabled) {
            return;
        }
        
        try {
            if (generateApiDoc) {
                GeneratedFile apiDoc = generateApiDocumentation(context);
                if (apiDoc != null) {
                    generatedFiles.add(apiDoc);
                    log.info("生成API文档: {}", apiDoc.getFilePath());
                }
            }
            
            if (generateDbDoc) {
                GeneratedFile dbDoc = generateDatabaseDocumentation(context);
                if (dbDoc != null) {
                    generatedFiles.add(dbDoc);
                    log.info("生成数据库文档: {}", dbDoc.getFilePath());
                }
            }
        } catch (Exception e) {
            log.error("生成文档失败", e);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 生成API文档
     * 
     * @param context 模板上下文
     * @return 生成的文档文件
     */
    private GeneratedFile generateApiDocumentation(TemplateContext context) {
        StringBuilder doc = new StringBuilder();
        String entityName = context.getTable().getEntityName();
        String tableName = context.getTable().getTableName();
        
        doc.append("# ").append(entityName).append(" API 文档\n\n");
        doc.append("## 基本信息\n\n");
        doc.append("- **实体名称**: ").append(entityName).append("\n");
        doc.append("- **表名**: ").append(tableName).append("\n");
        doc.append("- **包名**: ").append(context.getPackageName()).append("\n");
        doc.append("- **生成时间**: ").append(context.getDate()).append("\n\n");
        
        doc.append("## API 接口\n\n");
        doc.append("### 1. 创建").append(entityName).append("\n\n");
        doc.append("**接口地址**: POST /").append(tableName.toLowerCase()).append("\n\n");
        doc.append("**请求参数**:\n");
        doc.append("```json\n");
        doc.append("{\n");
        
        context.getTable().getColumns().forEach(column -> {
            if (!column.getPrimaryKey() && !isSystemField(column.getColumnName())) {
                doc.append("  \"").append(column.getPropertyName()).append("\": ");
                if (column.isStringType()) {
                    doc.append("\"string\"");
                } else if (column.isNumericType()) {
                    doc.append("0");
                } else if (column.isBooleanType()) {
                    doc.append("true");
                } else {
                    doc.append("\"value\"");
                }
                doc.append(", // ").append(column.getColumnComment()).append("\n");
            }
        });
        
        doc.append("}\n");
        doc.append("```\n\n");
        
        doc.append("### 2. 更新").append(entityName).append("\n\n");
        doc.append("**接口地址**: PUT /").append(tableName.toLowerCase()).append("/{id}\n\n");
        
        doc.append("### 3. 删除").append(entityName).append("\n\n");
        doc.append("**接口地址**: DELETE /").append(tableName.toLowerCase()).append("/{id}\n\n");
        
        doc.append("### 4. 查询").append(entityName).append("\n\n");
        doc.append("**接口地址**: GET /").append(tableName.toLowerCase()).append("/{id}\n\n");
        
        doc.append("### 5. 分页查询").append(entityName).append("\n\n");
        doc.append("**接口地址**: GET /").append(tableName.toLowerCase()).append("\n\n");
        
        String filePath = "docs/api/" + entityName + "API.md";
        return new GeneratedFile(filePath, doc.toString(), "API文档");
    }
    
    /**
     * 生成数据库文档
     * 
     * @param context 模板上下文
     * @return 生成的文档文件
     */
    private GeneratedFile generateDatabaseDocumentation(TemplateContext context) {
        StringBuilder doc = new StringBuilder();
        String entityName = context.getTable().getEntityName();
        String tableName = context.getTable().getTableName();
        
        doc.append("# ").append(tableName).append(" 表结构文档\n\n");
        doc.append("## 表信息\n\n");
        doc.append("- **表名**: ").append(tableName).append("\n");
        doc.append("- **表注释**: ").append(context.getTable().getTableComment()).append("\n");
        doc.append("- **实体类**: ").append(entityName).append("\n");
        doc.append("- **生成时间**: ").append(context.getDate()).append("\n\n");
        
        doc.append("## 字段信息\n\n");
        doc.append("| 字段名 | 类型 | 长度 | 允许为空 | 默认值 | 注释 |\n");
        doc.append("|--------|------|------|----------|--------|------|\n");
        
        context.getTable().getColumns().forEach(column -> {
            doc.append("| ").append(column.getColumnName());
            doc.append(" | ").append(column.getColumnType());
            doc.append(" | ").append(column.getColumnSize() != null ? column.getColumnSize() : "-");
            doc.append(" | ").append(column.getNullable() ? "是" : "否");
            doc.append(" | ").append(column.getDefaultValue() != null ? column.getDefaultValue() : "-");
            doc.append(" | ").append(column.getColumnComment() != null ? column.getColumnComment() : "-");
            doc.append(" |\n");
        });
        
        doc.append("\n## 索引信息\n\n");
        if (context.getTable().getIndexes() != null && !context.getTable().getIndexes().isEmpty()) {
            doc.append("| 索引名 | 类型 | 字段 |\n");
            doc.append("|--------|------|------|\n");
            context.getTable().getIndexes().forEach(index -> {
                doc.append("| ").append(index.getIndexName());
                doc.append(" | ").append(index.getUnique() ? "唯一索引" : "普通索引");
                doc.append(" | ").append(String.join(", ", index.getColumnNames()));
                doc.append(" |\n");
            });
        } else {
            doc.append("无索引信息\n");
        }
        
        String filePath = "docs/database/" + tableName + ".md";
        return new GeneratedFile(filePath, doc.toString(), "数据库文档");
    }
    
    /**
     * 检查是否为系统字段
     * 
     * @param columnName 字段名
     * @return 是否为系统字段
     */
    private boolean isSystemField(String columnName) {
        return columnName.equals("createTime") || 
               columnName.equals("updateTime") || 
               columnName.equals("createBy") || 
               columnName.equals("updateBy") ||
               columnName.equals("delFlag") ||
               columnName.equals("version");
    }
}