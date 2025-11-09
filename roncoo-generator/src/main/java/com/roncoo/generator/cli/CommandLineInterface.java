package com.roncoo.generator.cli;

import com.roncoo.generator.config.GeneratorConfig;
import com.roncoo.generator.engine.GeneratorEngine;
import com.roncoo.generator.engine.GenerationResult;
import lombok.extern.slf4j.Slf4j;

import java.io.Console;
import java.util.Scanner;

/**
 * 命令行界面
 * 
 * @author roncoo-generator
 */
@Slf4j
public class CommandLineInterface {
    
    private final Scanner scanner;
    private final Console console;
    
    public CommandLineInterface() {
        this.scanner = new Scanner(System.in);
        this.console = System.console();
    }
    
    /**
     * 启动交互式界面
     */
    public void start() {
        printWelcomeMessage();
        
        while (true) {
            try {
                printMainMenu();
                int choice = readIntInput("请选择操作", 1, 5);
                
                switch (choice) {
                    case 1:
                        quickGenerate();
                        break;
                    case 2:
                        configureAndGenerate();
                        break;
                    case 3:
                        showSystemInfo();
                        break;
                    case 4:
                        showHelp();
                        break;
                    case 5:
                        System.out.println("感谢使用 Roncoo Generator！再见！");
                        return;
                    default:\n                        System.out.println("无效选择，请重试。");
                }
                
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("操作失败: " + e.getMessage());
                log.error("CLI操作失败", e);
            }
        }
    }
    
    /**
     * 打印欢迎信息
     */
    private void printWelcomeMessage() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Roncoo Generator v2.0                  ║");
        System.out.println("║                   强大的代码生成工具                        ║");
        System.out.println("║                                                            ║");
        System.out.println("║  官网: https://www.roncoo.com                              ║");
        System.out.println("║  文档: https://docs.roncoo.com/generator                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * 打印主菜单
     */
    private void printMainMenu() {
        System.out.println("主菜单:");
        System.out.println("  1. 快速生成 (使用默认配置)");
        System.out.println("  2. 配置生成 (交互式配置)");
        System.out.println("  3. 系统信息");
        System.out.println("  4. 帮助");
        System.out.println("  5. 退出");
        System.out.println();
    }
    
    /**
     * 快速生成
     */
    private void quickGenerate() {
        System.out.println("=== 快速生成模式 ===");
        System.out.println("将使用 generator.yml 配置文件进行生成。");
        System.out.println();
        
        String configFile = readStringInput("配置文件路径", "generator.yml");
        
        if (!confirm("确认开始生成吗？")) {
            System.out.println("已取消生成。");
            return;
        }
        
        try {
            // TODO: 实现配置文件加载
            GeneratorConfig config = createSampleConfig();
            executeGeneration(config);
        } catch (Exception e) {\n            System.err.println("生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 配置生成
     */
    private void configureAndGenerate() {
        System.out.println("=== 交互式配置模式 ===");
        
        try {
            GeneratorConfig config = buildConfigurationInteractively();
            
            System.out.println("\\n配置完成！");
            printConfigSummary(config);
            
            if (confirm("确认使用此配置开始生成吗？")) {
                executeGeneration(config);
            } else {
                System.out.println("已取消生成。");
            }
        } catch (Exception e) {
            System.err.println("配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 交互式构建配置
     */
    private GeneratorConfig buildConfigurationInteractively() {
        System.out.println("请按提示输入配置信息：");
        System.out.println();
        
        // 数据库配置
        System.out.println("--- 数据库配置 ---");
        String dbUrl = readStringInput("数据库URL", "jdbc:mysql://localhost:3306/roncoo_education");
        String dbUsername = readStringInput("数据库用户名", "root");
        String dbPassword = readPasswordInput("数据库密码");
        String tables = readStringInput("表名列表 (逗号分隔)", "user,course");
        
        // 项目配置
        System.out.println("\\n--- 项目配置 ---");
        String projectName = readStringInput("项目名称", "roncoo-education-service");
        String packagePrefix = readStringInput("包名前缀", "com.roncoo.education");
        String packageName = readStringInput("模块包名", "user");
        String author = readStringInput("作者", "roncoo-generator");
        
        // 输出配置
        System.out.println("\\n--- 输出配置 ---");
        String outputDir = readStringInput("输出目录", "generated-code");
        boolean overwrite = readBooleanInput("是否覆盖已存在文件", false);
        
        // 构建配置对象
        return buildGeneratorConfig(dbUrl, dbUsername, dbPassword, tables,
                projectName, packagePrefix, packageName, author,
                outputDir, overwrite);
    }
    
    /**
     * 构建生成器配置
     */
    private GeneratorConfig buildGeneratorConfig(String dbUrl, String dbUsername, String dbPassword, String tables,
                                                String projectName, String packagePrefix, String packageName, String author,
                                                String outputDir, boolean overwrite) {
        GeneratorConfig config = new GeneratorConfig();
        
        // 数据库配置
        com.roncoo.generator.config.DatabaseConfig dbConfig = new com.roncoo.generator.config.DatabaseConfig();
        dbConfig.setUrl(dbUrl);
        dbConfig.setUsername(dbUsername);
        dbConfig.setPassword(dbPassword);
        dbConfig.setTableNames(java.util.Arrays.asList(tables.split(",")));
        config.setDatabase(dbConfig);
        
        // 项目配置
        com.roncoo.generator.config.ProjectConfig projectConfig = new com.roncoo.generator.config.ProjectConfig();
        projectConfig.setProjectName(projectName);
        projectConfig.setPackagePrefix(packagePrefix);
        projectConfig.setPackageName(packageName);
        projectConfig.setAuthor(author);
        config.setProject(projectConfig);
        
        // 模板配置
        com.roncoo.generator.config.TemplateConfig templateConfig = new com.roncoo.generator.config.TemplateConfig();
        templateConfig.setTemplatePath("template");
        config.setTemplate(templateConfig);
        
        // 输出配置
        com.roncoo.generator.config.OutputConfig outputConfig = new com.roncoo.generator.config.OutputConfig();
        outputConfig.setBaseDirectory(outputDir);
        outputConfig.setOverwriteFiles(overwrite);
        config.setOutput(outputConfig);
        
        return config;
    }
    
    /**
     * 执行代码生成
     */
    private void executeGeneration(GeneratorConfig config) {
        System.out.println("\\n=== 开始生成代码 ===");
        
        GeneratorEngine engine = new GeneratorEngine(config);
        ProgressIndicator progress = new ProgressIndicator();
        
        try {\n            progress.start();
            
            GenerationResult result = engine.generate();
            
            progress.stop();
            
            System.out.println("\\n=== 生成完成 ===");
            printGenerationResult(result);
            
        } catch (Exception e) {
            progress.stop();
            System.err.println("\\n生成失败: " + e.getMessage());
        } finally {
            engine.close();
        }
    }
    
    /**
     * 显示系统信息
     */
    private void showSystemInfo() {
        System.out.println("=== 系统信息 ===");
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.printf("Java版本: %s\\n", System.getProperty("java.version"));
        System.out.printf("操作系统: %s %s\\n", 
                System.getProperty("os.name"), System.getProperty("os.version"));
        System.out.printf("可用处理器: %d\\n", runtime.availableProcessors());
        System.out.printf("最大内存: %s\\n", formatBytes(maxMemory));
        System.out.printf("总内存: %s\\n", formatBytes(totalMemory));
        System.out.printf("已用内存: %s\\n", formatBytes(usedMemory));
        System.out.printf("空闲内存: %s\\n", formatBytes(freeMemory));
        System.out.printf("内存使用率: %.1f%%\\n", (double) usedMemory / totalMemory * 100);
    }
    
    /**
     * 显示帮助信息
     */
    private void showHelp() {
        System.out.println("=== 帮助信息 ===");
        System.out.println();
        System.out.println("快速生成:");\n        System.out.println("  使用默认的 generator.yml 配置文件快速生成代码");
        System.out.println();
        System.out.println("配置生成:");
        System.out.println("  通过交互式问答配置生成参数，适合首次使用");
        System.out.println();
        System.out.println("配置文件格式:");
        System.out.println("  支持 YAML 格式的配置文件");
        System.out.println("  参考文档: https://docs.roncoo.com/generator/config");
        System.out.println();
        System.out.println("常见问题:");
        System.out.println("  1. 数据库连接失败 - 检查URL、用户名、密码");
        System.out.println("  2. 表不存在 - 确认表名拼写正确");
        System.out.println("  3. 权限不足 - 检查输出目录的写入权限");
        System.out.println();
        System.out.println("技术支持:");\n        System.out.println("  官网: https://www.roncoo.com");
        System.out.println("  文档: https://docs.roncoo.com/generator");
        System.out.println("  问题反馈: https://github.com/roncoo/roncoo-generator/issues");
    }
    
    /**
     * 打印配置摘要
     */
    private void printConfigSummary(GeneratorConfig config) {
        System.out.println("\\n配置摘要:");
        System.out.println("  数据库: " + config.getDatabase().getUrl());
        System.out.println("  项目: " + config.getProject().getProjectName());
        System.out.println("  包名: " + config.getProject().getFullPackageName());
        System.out.println("  输出: " + config.getOutput().getBaseDirectory());
    }
    
    /**
     * 打印生成结果
     */
    private void printGenerationResult(GenerationResult result) {
        System.out.println("生成统计:");
        System.out.printf("  总耗时: %d ms\\n", result.getDuration());
        System.out.printf("  处理表数: %d\\n", result.getTableCount());
        System.out.printf("  成功数: %d\\n", result.getSuccessCount());
        System.out.printf("  失败数: %d\\n", result.getErrorCount());
        System.out.printf("  生成文件数: %d\\n", result.getTotalFileCount());
        System.out.printf("  成功率: %.1f%%\\n", result.getSuccessRate());
        
        if (result.getErrorCount() > 0) {
            System.out.println("\\n错误详情:");
            result.getErrors().forEach((table, error) -> 
                    System.out.printf("  %s: %s\\n", table, error));
        }
    }
    
    // 输入工具方法
    private String readStringInput(String prompt, String defaultValue) {
        System.out.printf("%s [%s]: ", prompt, defaultValue);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    private String readPasswordInput(String prompt) {
        if (console != null) {
            char[] password = console.readPassword("%s: ", prompt);
            return new String(password);
        } else {
            System.out.printf("%s: ", prompt);
            return scanner.nextLine();
        }
    }
    
    private int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%s (%d-%d): ", prompt, min, max);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("请输入 " + min + " 到 " + max + " 之间的数字。");
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字。");
            }
        }
    }
    
    private boolean readBooleanInput(String prompt, boolean defaultValue) {
        String defaultStr = defaultValue ? "y" : "n";
        System.out.printf("%s [%s]: ", prompt + " (y/n)", defaultStr);
        String input = scanner.nextLine().trim().toLowerCase();
        
        if (input.isEmpty()) {
            return defaultValue;
        }
        
        return input.equals("y") || input.equals("yes") || input.equals("true");
    }
    
    private boolean confirm(String message) {
        return readBooleanInput(message, false);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    // 临时方法，实际应该从配置文件加载
    private GeneratorConfig createSampleConfig() {
        // 实现示例配置创建逻辑
        return new GeneratorConfig();
    }
}