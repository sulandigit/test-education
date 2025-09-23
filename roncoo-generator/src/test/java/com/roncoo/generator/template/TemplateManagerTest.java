package com.roncoo.generator.template;

import com.roncoo.generator.config.TemplateConfig;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.context.TableMetadata;
import com.roncoo.generator.context.ProjectMetadata;
import com.roncoo.generator.context.ColumnMetadata;
import com.roncoo.generator.engine.GeneratedFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TemplateManager 测试类
 * 
 * @author roncoo-generator
 */
public class TemplateManagerTest {
    
    @TempDir
    Path tempDir;
    
    private TemplateManager templateManager;
    private TemplateContext context;
    
    @BeforeEach
    void setUp() throws Exception {
        // 创建模板目录和文件
        createTestTemplates();
        
        // 配置模板管理器
        TemplateConfig config = new TemplateConfig();
        config.setTemplatePath(tempDir.toString());
        config.setCacheEnabled(false); // 测试时禁用缓存
        
        templateManager = new TemplateManager(config);
        
        // 准备测试上下文
        context = createTestContext();
    }
    
    @Test
    void testGetTemplateGroups() {
        var groups = templateManager.getTemplateGroups();
        
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        
        // 验证是否包含我们创建的测试模板组
        boolean hasTestGroup = groups.stream()
                .anyMatch(group -> "entity".equals(group.getName()));
        assertTrue(hasTestGroup);
    }
    
    @Test
    void testGetTemplateGroup() {
        TemplateGroup group = templateManager.getTemplateGroup("entity");
        
        assertNotNull(group);
        assertEquals("entity", group.getName());
        assertNotNull(group.getTemplates());
        assertFalse(group.getTemplates().isEmpty());
    }
    
    @Test
    void testRenderTemplate() throws Exception {
        // 创建模板信息
        TemplateInfo templateInfo = new TemplateInfo();
        templateInfo.setName("entity.java.ftl");
        templateInfo.setGroup("entity");
        templateInfo.setOutputPattern("src/main/java/{packagePath}/entity/{entityName}.java");
        
        // 渲染模板
        GeneratedFile file = templateManager.renderTemplate(templateInfo, context);
        
        assertNotNull(file);
        assertNotNull(file.getContent());
        assertTrue(file.getContent().contains("User"));
        assertTrue(file.getContent().contains("package com.test.module.entity"));
    }
    
    @Test
    void testRenderTemplates() throws Exception {
        List<GeneratedFile> files = templateManager.renderTemplates(context);
        
        assertNotNull(files);
        assertFalse(files.isEmpty());
        
        // 验证生成的文件
        GeneratedFile entityFile = files.stream()
                .filter(f -> f.getFilePath().contains("entity"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(entityFile);
        assertTrue(entityFile.getContent().contains("User"));
    }
    
    @Test
    void testClearCache() {
        assertDoesNotThrow(() -> templateManager.clearCache());
    }
    
    /**
     * 创建测试模板文件
     */
    private void createTestTemplates() throws Exception {
        // 创建entity目录
        Path entityDir = tempDir.resolve("entity");
        Files.createDirectories(entityDir);
        
        // 创建实体类模板
        String entityTemplate = """
                package ${project.packageName}.entity;
                
                import lombok.Data;
                
                /**
                 * ${table.tableComment!''}
                 * 
                 * @author ${project.author}
                 * @date ${project.date}
                 */
                @Data
                public class ${table.entityName} {
                
                <#list table.columns as column>
                    /**
                     * ${column.columnComment!''}
                     */
                    private ${column.propertyType} ${column.propertyName};
                
                </#list>
                }
                """;
        
        Files.write(entityDir.resolve("entity.java.ftl"), entityTemplate.getBytes());
    }
    
    /**
     * 创建测试上下文
     */
    private TemplateContext createTestContext() {
        TemplateContext context = new TemplateContext();
        
        // 创建表元数据
        TableMetadata table = new TableMetadata();
        table.setTableName("user");
        table.setEntityName("User");
        table.setTableComment("用户表");
        
        // 创建字段元数据
        ColumnMetadata idColumn = new ColumnMetadata();
        idColumn.setColumnName("id");
        idColumn.setPropertyName("id");
        idColumn.setPropertyType("Long");
        idColumn.setColumnComment("主键ID");
        
        ColumnMetadata nameColumn = new ColumnMetadata();
        nameColumn.setColumnName("name");
        nameColumn.setPropertyName("name");
        nameColumn.setPropertyType("String");
        nameColumn.setColumnComment("用户名");
        
        table.setColumns(Arrays.asList(idColumn, nameColumn));
        context.setTable(table);
        
        // 创建项目元数据
        ProjectMetadata project = new ProjectMetadata();
        project.setPackageName("com.test.module");
        project.setAuthor("test-author");
        project.setCreateTime(LocalDateTime.now());
        context.setProject(project);
        
        return context;
    }
}