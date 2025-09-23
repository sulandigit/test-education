package com.roncoo.generator.engine;

import com.roncoo.generator.config.GeneratorConfig;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.context.TableMetadata;
import com.roncoo.generator.context.ProjectMetadata;
import com.roncoo.generator.exception.GeneratorException;
import com.roncoo.generator.adapter.DatabaseAdapter;
import com.roncoo.generator.template.TemplateManager;
import com.roncoo.generator.plugin.PluginManager;
import com.roncoo.generator.validation.ConfigValidator;
import com.roncoo.generator.cache.CacheManager;
import com.roncoo.generator.cache.CacheConfig;
import com.roncoo.generator.performance.MemoryMonitor;
import com.roncoo.generator.performance.PerformanceMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 代码生成引擎
 * 
 * @author roncoo-generator
 */
@Slf4j
public class GeneratorEngine {
    
    /**
     * 生成器配置
     */
    private final GeneratorConfig config;
    
    /**
     * 数据库适配器
     */
    private final DatabaseAdapter databaseAdapter;
    
    /**
     * 模板管理器
     */
    private final TemplateManager templateManager;
    
    /**
     * 插件管理器
     */
    private final PluginManager pluginManager;
    
    /**
     * 配置验证器
     */
    private final ConfigValidator configValidator;
    
    /**
     * 线程池
     */
    private final ExecutorService executorService;
    
    /**
     * 缓存管理器
     */
    private final CacheManager cacheManager;
    
    /**
     * 内存监控器
     */
    private final MemoryMonitor memoryMonitor;
    
    /**
     * 性能监控器
     */
    private final PerformanceMonitor performanceMonitor;
    
    /**
     * 构造函数
     * 
     * @param config 生成器配置
     */
    public GeneratorEngine(GeneratorConfig config) {
        this.config = config;
        this.databaseAdapter = createDatabaseAdapter(config);
        this.templateManager = new TemplateManager(config.getTemplate());
        this.pluginManager = new PluginManager(config.getPlugins());
        this.configValidator = new ConfigValidator();
        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        
        // 初始化性能组件
        this.cacheManager = new CacheManager(CacheConfig.defaultConfig());
        this.memoryMonitor = new MemoryMonitor(MemoryMonitor.MonitorConfig.defaultConfig());
        this.performanceMonitor = PerformanceMonitor.getInstance();
    }
    
    /**
     * 执行代码生成
     * 
     * @return 生成结果
     * @throws GeneratorException 生成异常
     */
    public GenerationResult generate() throws GeneratorException {
        log.info("开始执行代码生成...");
        
        GenerationResult result = new GenerationResult();
        result.setStartTime(LocalDateTime.now());
        
        try (PerformanceMonitor.Timer totalTimer = performanceMonitor.startTimer("total_generation")) {
            // 1. 验证配置
            validateConfiguration();
            
            // 2. 初始化数据源
            initializeDataSource();
            
            // 3. 加载表元数据
            List<TableMetadata> tables = loadTableMetadata();
            result.setTableCount(tables.size());
            
            // 4. 准备项目元数据
            ProjectMetadata projectMetadata = prepareProjectMetadata();
            
            // 5. 执行代码生成
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (TableMetadata table : tables) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        generateForTable(table, projectMetadata, result);
                    } catch (Exception e) {
                        log.error("生成表 {} 的代码时发生错误", table.getTableName(), e);
                        result.addError(table.getTableName(), e.getMessage());
                        performanceMonitor.recordError("table_generation_" + table.getTableName());
                    }
                }, executorService);
                
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            // 6. 生成报告
            if (config.getOutput().isGenerateReport()) {
                generateReport(result);
            }
            
            result.setEndTime(LocalDateTime.now());
            result.setSuccess(true);
            
            log.info("代码生成完成，耗时: {}ms", result.getDuration());
            
            // 记录性能指标
            performanceMonitor.incrementCounter("successful_generations");
            
        } catch (Exception e) {
            result.setEndTime(LocalDateTime.now());
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            log.error("代码生成失败", e);
            performanceMonitor.recordError("total_generation");
            performanceMonitor.incrementCounter("failed_generations");
            throw new GeneratorException("代码生成失败", e);
        }
        
        return result;
    }
    
    /**
     * 验证配置
     * 
     * @throws GeneratorException 生成异常
     */
    private void validateConfiguration() throws GeneratorException {
        log.info("验证配置中...");
        try {
            configValidator.validate(config);
        } catch (Exception e) {
            throw new GeneratorException("配置验证失败", e);
        }
    }
    
    /**
     * 初始化数据源
     * 
     * @throws GeneratorException 生成异常
     */
    private void initializeDataSource() throws GeneratorException {
        log.info("初始化数据源中...");
        try {
            databaseAdapter.connect();
        } catch (Exception e) {
            throw new GeneratorException("数据源初始化失败", e);
        }
    }
    
    /**
     * 加载表元数据
     * 
     * @return 表元数据列表
     * @throws GeneratorException 生成异常
     */
    private List<TableMetadata> loadTableMetadata() throws GeneratorException {
        log.info("加载表元数据中...");
        try (PerformanceMonitor.Timer timer = performanceMonitor.startTimer("load_table_metadata")) {
            List<String> tableNames = config.getDatabase().getActualTableNames();
            List<TableMetadata> tables = new ArrayList<>();
            
            for (String tableName : tableNames) {
                // 优先从缓存获取
                String cacheKey = "table_metadata_" + tableName;
                TableMetadata table = cacheManager.get(cacheKey, () -> {
                    try {
                        if (tableName.contains("%")) {
                            // 处理通配符
                            List<String> actualTables = databaseAdapter.getTableNames(tableName);
                            return actualTables.stream()
                                    .map(actualTable -> {
                                        try {
                                            return databaseAdapter.getTableMetadata(actualTable);
                                        } catch (GeneratorException e) {
                                            log.error("获取表 {} 元数据失败", actualTable, e);
                                            return null;
                                        }
                                    })
                                    .filter(java.util.Objects::nonNull)
                                    .collect(java.util.stream.Collectors.toList());
                        } else {
                            return databaseAdapter.getTableMetadata(tableName);
                        }
                    } catch (GeneratorException e) {
                        log.error("获取表 {} 元数据失败", tableName, e);
                        return null;
                    }
                });
                
                if (table instanceof List) {
                    tables.addAll((List<TableMetadata>) table);
                } else if (table != null) {
                    tables.add(table);
                }
            }
            
            log.info("成功加载 {} 个表的元数据", tables.size());
            performanceMonitor.incrementCounter("tables_loaded", tables.size());
            return tables;
            
        } catch (Exception e) {
            performanceMonitor.recordError("load_table_metadata");
            throw new GeneratorException("加载表元数据失败", e);
        }
    }
    
    /**
     * 准备项目元数据
     * 
     * @return 项目元数据
     */
    private ProjectMetadata prepareProjectMetadata() {
        ProjectMetadata metadata = new ProjectMetadata();
        metadata.setPackageName(config.getProject().getFullPackageName());
        metadata.setModuleName(config.getProject().getProjectName());
        metadata.setAuthor(config.getProject().getAuthor());
        metadata.setVersion(config.getProject().getVersion());
        metadata.setDescription(config.getProject().getDescription());
        metadata.setCreateTime(LocalDateTime.now());
        
        return metadata;
    }
    
    /**
     * 为单个表生成代码
     * 
     * @param table 表元数据
     * @param projectMetadata 项目元数据
     * @param result 生成结果
     * @throws GeneratorException 生成异常
     */
    private void generateForTable(TableMetadata table, ProjectMetadata projectMetadata, 
                                  GenerationResult result) throws GeneratorException {
        log.info("正在为表 {} 生成代码...", table.getTableName());
        
        try {
            // 准备模板上下文
            TemplateContext context = prepareTemplateContext(table, projectMetadata);
            
            // 执行前置插件
            pluginManager.executeBeforeGeneration(context);
            
            // 渲染模板
            List<GeneratedFile> files = templateManager.renderTemplates(context);
            
            // 执行后置插件
            pluginManager.executeAfterGeneration(context, files);
            
            // 输出文件
            outputFiles(files);
            
            result.addGeneratedFiles(files);
            result.incrementSuccessCount();
            
            log.info("表 {} 的代码生成完成，生成了 {} 个文件", table.getTableName(), files.size());
            
        } catch (Exception e) {
            result.incrementErrorCount();
            throw new GeneratorException("为表 " + table.getTableName() + " 生成代码失败", e);
        }
    }
    
    /**
     * 准备模板上下文
     * 
     * @param table 表元数据
     * @param projectMetadata 项目元数据
     * @return 模板上下文
     */
    private TemplateContext prepareTemplateContext(TableMetadata table, ProjectMetadata projectMetadata) {
        TemplateContext context = new TemplateContext();
        context.setTable(table);
        context.setProject(projectMetadata);
        
        // 添加全局变量
        if (config.getGlobalVariables() != null) {
            config.getGlobalVariables().forEach(context::addGlobalVariable);
        }
        
        // 添加配置参数
        context.addConfig("packagePrefix", config.getProject().getPackagePrefix());
        context.addConfig("packageName", config.getProject().getPackageName());
        context.addConfig("projectName", config.getProject().getProjectName());
        context.addConfig("author", config.getProject().getAuthor());
        context.addConfig("useLombok", config.getProject().getCodeStyle() != null && 
                          config.getProject().getCodeStyle().isUseLombok());
        context.addConfig("useSwagger", config.getProject().getCodeStyle() != null && 
                          config.getProject().getCodeStyle().isUseSwagger());
        
        return context;
    }
    
    /**
     * 输出文件
     * 
     * @param files 生成的文件列表
     * @throws GeneratorException 生成异常
     */
    private void outputFiles(List<GeneratedFile> files) throws GeneratorException {
        for (GeneratedFile file : files) {
            try {
                file.writeToFile(config.getOutput());
            } catch (Exception e) {
                throw new GeneratorException("输出文件失败: " + file.getFilePath(), e);
            }
        }
    }
    
    /**
     * 生成报告
     * 
     * @param result 生成结果
     */
    private void generateReport(GenerationResult result) {
        // TODO: 实现生成报告功能
        log.info("生成报告中...");
    }
    
    /**
     * 创建数据库适配器
     * 
     * @param config 配置
     * @return 数据库适配器
     */
    private DatabaseAdapter createDatabaseAdapter(GeneratorConfig config) {
        // TODO: 根据数据库类型创建相应的适配器
        return new com.roncoo.generator.adapter.MySQLAdapter(config.getDatabase());
    }
    
    /**
     * 关闭资源
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        if (databaseAdapter != null) {
            try {
                databaseAdapter.close();
            } catch (Exception e) {
                log.warn("关闭数据库适配器时发生错误", e);
            }
        }
        
        // 关闭性能组件
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
        
        if (memoryMonitor != null) {
            memoryMonitor.stopMonitoring();
        }
        
        // 输出性能报告
        log.info("性能统计\n{}", performanceMonitor.getPerformanceReport());
        log.info("缓存统计: {}", cacheManager.getStatistics());
    }
}