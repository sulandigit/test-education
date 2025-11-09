package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GeneratorConfig 测试类
 * 
 * @author roncoo-generator
 */
public class GeneratorConfigTest {
    
    private GeneratorConfig config;
    
    @BeforeEach
    void setUp() {
        config = new GeneratorConfig();
        
        // 设置数据库配置
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/test");
        dbConfig.setUsername("test");
        dbConfig.setPassword("test");
        dbConfig.setTableNames(Arrays.asList("user", "order"));
        config.setDatabase(dbConfig);
        
        // 设置项目配置
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setProjectName("test-project");
        projectConfig.setPackagePrefix("com.test");
        projectConfig.setPackageName("module");
        config.setProject(projectConfig);
        
        // 设置模板配置
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setTemplatePath("template");
        config.setTemplate(templateConfig);
        
        // 设置输出配置
        OutputConfig outputConfig = new OutputConfig();
        outputConfig.setBaseDirectory("target/generated");
        config.setOutput(outputConfig);
    }
    
    @Test
    void testValidateSuccess() {
        assertDoesNotThrow(() -> config.validate());
    }
    
    @Test
    void testValidateWithNullDatabase() {
        config.setDatabase(null);
        
        ConfigurationException exception = assertThrows(
                ConfigurationException.class, 
                () -> config.validate()
        );
        
        assertTrue(exception.getMessage().contains("数据库配置不能为空"));
    }
    
    @Test
    void testValidateWithNullProject() {
        config.setProject(null);
        
        ConfigurationException exception = assertThrows(
                ConfigurationException.class, 
                () -> config.validate()
        );
        
        assertTrue(exception.getMessage().contains("项目配置不能为空"));
    }
    
    @Test
    void testValidateWithNullTemplate() {
        config.setTemplate(null);
        
        ConfigurationException exception = assertThrows(
                ConfigurationException.class, 
                () -> config.validate()
        );
        
        assertTrue(exception.getMessage().contains("模板配置不能为空"));
    }
    
    @Test
    void testValidateWithNullOutput() {
        config.setOutput(null);
        
        ConfigurationException exception = assertThrows(
                ConfigurationException.class, 
                () -> config.validate()
        );
        
        assertTrue(exception.getMessage().contains("输出配置不能为空"));
    }
    
    @Test
    void testGetSummary() {
        String summary = config.getSummary();
        
        assertNotNull(summary);
        assertTrue(summary.contains("代码生成器配置摘要"));
        assertTrue(summary.contains("jdbc:mysql://localhost:3306/test"));
        assertTrue(summary.contains("test-project"));
        assertTrue(summary.contains("template"));
        assertTrue(summary.contains("target/generated"));
    }
    
    @Test
    void testGetSummaryWithPlugins() {
        // 添加插件配置
        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.setName("TestPlugin");
        pluginConfig.setEnabled(true);
        config.setPlugins(Arrays.asList(pluginConfig));
        
        String summary = config.getSummary();
        
        assertTrue(summary.contains("插件数量: 1"));
    }
}