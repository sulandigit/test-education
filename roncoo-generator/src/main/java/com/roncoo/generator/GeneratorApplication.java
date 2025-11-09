package com.roncoo.generator;

import com.roncoo.generator.config.GeneratorConfig;
import com.roncoo.generator.engine.GeneratorEngine;
import com.roncoo.generator.engine.GenerationResult;
import com.roncoo.generator.cli.CommandLineInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码生成器主入口
 * 
 * @author roncoo-generator
 */
@Slf4j
public class GeneratorApplication {
    
    public static void main(String[] args) {
        try {
            // 检查是否是交互式模式
            boolean interactiveMode = args.length == 0 || 
                    (args.length == 1 && "-i".equals(args[0]));
            
            if (interactiveMode) {
                // 启动交互式界面
                CommandLineInterface cli = new CommandLineInterface();
                cli.start();
            } else {
                // 命令行模式
                runCommandLine(args);
            }
            
        } catch (Exception e) {
            log.error("应用程序启动失败", e);
            System.exit(1);
        }
    }
    
    /**
     * 命令行模式运行
     * 
     * @param args 命令行参数
     */
    private static void runCommandLine(String[] args) {
        try {
            log.info("Roncoo Generator v2.0.0 启动中...");
            
            // 加载配置
            GeneratorConfig config = loadConfiguration(args);
            
            // 输出配置摘要
            log.info("配置摘要:\n{}", config.getSummary());
            
            // 创建生成引擎
            GeneratorEngine engine = new GeneratorEngine(config);
            
            try {
                // 执行代码生成
                GenerationResult result = engine.generate();
                
                // 输出生成结果
                log.info("代码生成完成!");
                log.info("生成结果摘要:\n{}", result.getSummary());
                
                if (!result.isSuccess()) {
                    System.exit(1);
                }
                
            } finally {
                // 关闭引擎资源
                engine.close();
            }
            
        } catch (Exception e) {
            log.error("代码生成失败", e);
            System.exit(1);
        }
    }
    
    /**
     * 加载配置
     * 
     * @param args 命令行参数
     * @return 生成器配置
     */
    private static GeneratorConfig loadConfiguration(String[] args) {
        String configFile = "generator.yml";
        
        // 解析命令行参数
        if (args.length > 0) {
            configFile = args[0];
        }
        
        log.info("加载配置文件: {}", configFile);
        
        // TODO: 实现配置加载器
        // return ConfigLoader.loadFromYaml(configFile);
        
        // 临时返回示例配置
        return createSampleConfig();
    }
    
    /**
     * 创建示例配置（临时实现）
     * 
     * @return 示例配置
     */
    private static GeneratorConfig createSampleConfig() {
        GeneratorConfig config = new GeneratorConfig();
        
        // 数据库配置
        com.roncoo.generator.config.DatabaseConfig dbConfig = new com.roncoo.generator.config.DatabaseConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/roncoo_education?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        dbConfig.setUsername("root");
        dbConfig.setPassword("123456");
        dbConfig.setTableNames(java.util.Arrays.asList("user", "course"));
        config.setDatabase(dbConfig);
        
        // 项目配置
        com.roncoo.generator.config.ProjectConfig projectConfig = new com.roncoo.generator.config.ProjectConfig();
        projectConfig.setProjectName("roncoo-education-service-user");
        projectConfig.setPackagePrefix("com.roncoo.education");
        projectConfig.setPackageName("user");
        projectConfig.setAuthor("roncoo-generator");
        config.setProject(projectConfig);
        
        // 模板配置
        com.roncoo.generator.config.TemplateConfig templateConfig = new com.roncoo.generator.config.TemplateConfig();
        templateConfig.setTemplatePath("template");
        config.setTemplate(templateConfig);
        
        // 输出配置
        com.roncoo.generator.config.OutputConfig outputConfig = new com.roncoo.generator.config.OutputConfig();
        outputConfig.setBaseDirectory("generated-code");
        config.setOutput(outputConfig);
        
        // 插件配置
        java.util.List<com.roncoo.generator.config.PluginConfig> plugins = new java.util.ArrayList<>();
        
        com.roncoo.generator.config.PluginConfig codeFormatter = new com.roncoo.generator.config.PluginConfig();
        codeFormatter.setName("CodeFormatter");
        codeFormatter.setClassName("com.roncoo.generator.plugin.CodeFormatterPlugin");
        codeFormatter.setEnabled(true);
        plugins.add(codeFormatter);
        
        config.setPlugins(plugins);
        
        return config;
    }
}