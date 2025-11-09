package com.roncoo.generator.plugin;

import com.roncoo.generator.config.PluginConfig;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.context.TableMetadata;
import com.roncoo.generator.context.ProjectMetadata;
import com.roncoo.generator.engine.GeneratedFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginManager 测试类
 * 
 * @author roncoo-generator
 */
public class PluginManagerTest {
    
    private PluginManager pluginManager;
    private TemplateContext context;
    private List<GeneratedFile> generatedFiles;
    
    @BeforeEach
    void setUp() {
        // 创建插件配置
        PluginConfig codeFormatterConfig = new PluginConfig();
        codeFormatterConfig.setName("CodeFormatter");
        codeFormatterConfig.setClassName("com.roncoo.generator.plugin.CodeFormatterPlugin");
        codeFormatterConfig.setEnabled(true);
        codeFormatterConfig.setOrder(100);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("enabled", true);
        properties.put("style", "google");
        codeFormatterConfig.setProperties(properties);
        
        List<PluginConfig> configs = Arrays.asList(codeFormatterConfig);
        
        // 创建插件管理器
        pluginManager = new PluginManager(configs);
        
        // 准备测试数据
        context = createTestContext();
        generatedFiles = createTestFiles();
    }
    
    @Test
    void testGetRegisteredPlugins() {
        var plugins = pluginManager.getRegisteredPlugins();
        
        assertNotNull(plugins);
        assertFalse(plugins.isEmpty());
        
        // 验证内置插件是否已注册
        boolean hasCodeFormatter = plugins.stream()
                .anyMatch(p -> "CodeFormatter".equals(p.getName()));
        assertTrue(hasCodeFormatter);
        
        boolean hasDocumentGenerator = plugins.stream()
                .anyMatch(p -> "DocumentGenerator".equals(p.getName()));
        assertTrue(hasDocumentGenerator);
    }
    
    @Test
    void testGetInitializedPlugins() {
        var plugins = pluginManager.getInitializedPlugins();
        
        assertNotNull(plugins);
        assertFalse(plugins.isEmpty());
        
        // 验证插件是否按顺序排列
        for (int i = 1; i < plugins.size(); i++) {
            assertTrue(plugins.get(i - 1).getOrder() <= plugins.get(i).getOrder());
        }
    }
    
    @Test
    void testGetPlugin() {
        GeneratorPlugin plugin = pluginManager.getPlugin("CodeFormatter");
        
        assertNotNull(plugin);
        assertEquals("CodeFormatter", plugin.getName());
        assertTrue(plugin.isEnabled());
    }
    
    @Test
    void testExecuteBeforeGeneration() {
        assertDoesNotThrow(() -> 
                pluginManager.executeBeforeGeneration(context));
    }
    
    @Test
    void testExecuteAfterGeneration() {
        assertDoesNotThrow(() -> 
                pluginManager.executeAfterGeneration(context, generatedFiles));
    }
    
    @Test
    void testRegisterPlugin() {
        GeneratorPlugin customPlugin = new TestPlugin();
        
        pluginManager.registerPlugin(customPlugin);
        
        GeneratorPlugin registered = pluginManager.getPlugin("TestPlugin");
        assertNotNull(registered);
        assertEquals("TestPlugin", registered.getName());
    }
    
    @Test
    void testRegisterNullPlugin() {
        assertThrows(IllegalArgumentException.class, () -> 
                pluginManager.registerPlugin(null));
    }
    
    @Test
    void testDestroy() {
        assertDoesNotThrow(() -> pluginManager.destroy());
        
        // 验证插件列表已清空
        assertTrue(pluginManager.getInitializedPlugins().isEmpty());
    }
    
    /**
     * 创建测试上下文
     */
    private TemplateContext createTestContext() {
        TemplateContext context = new TemplateContext();
        
        TableMetadata table = new TableMetadata();
        table.setTableName("user");
        table.setEntityName("User");
        table.setTableComment("用户表");
        context.setTable(table);
        
        ProjectMetadata project = new ProjectMetadata();
        project.setPackageName("com.test.module");
        project.setAuthor("test-author");
        project.setCreateTime(LocalDateTime.now());
        context.setProject(project);
        
        return context;
    }
    
    /**
     * 创建测试文件
     */
    private List<GeneratedFile> createTestFiles() {
        GeneratedFile javaFile = new GeneratedFile(
                "src/main/java/com/test/User.java",
                "public class User { }",
                "entity"
        );
        
        GeneratedFile xmlFile = new GeneratedFile(
                "src/main/resources/mapper/UserMapper.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "mapper"
        );
        
        return Arrays.asList(javaFile, xmlFile);
    }
    
    /**
     * 测试插件实现
     */
    static class TestPlugin implements GeneratorPlugin {
        
        @Override
        public String getName() {
            return "TestPlugin";
        }
        
        @Override
        public String getDescription() {
            return "测试插件";
        }
        
        @Override
        public String getVersion() {
            return "1.0.0";
        }
        
        @Override
        public int getOrder() {
            return 999;
        }
        
        @Override
        public void initialize(Map<String, Object> properties) {
            // 测试插件初始化
        }
    }
}