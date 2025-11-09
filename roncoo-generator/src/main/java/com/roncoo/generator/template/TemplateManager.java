package com.roncoo.generator.template;

import com.roncoo.generator.config.TemplateConfig;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import com.roncoo.generator.exception.GeneratorException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模板管理器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class TemplateManager {
    
    /**
     * 模板配置
     */
    private final TemplateConfig templateConfig;
    
    /**
     * Freemarker配置
     */
    private final Configuration freemarkerConfig;
    
    /**
     * 模板缓存
     */
    private final Map<String, Template> templateCache = new ConcurrentHashMap<>();
    
    /**
     * 模板组织信息
     */
    private final Map<String, TemplateGroup> templateGroups = new HashMap<>();
    
    /**
     * 构造函数
     * 
     * @param templateConfig 模板配置
     */
    public TemplateManager(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
        this.freemarkerConfig = initializeFreemarker();
        loadTemplateGroups();
    }
    
    /**
     * 初始化Freemarker配置
     * 
     * @return Freemarker配置
     */
    private Configuration initializeFreemarker() {
        Configuration config = new Configuration(Configuration.VERSION_2_3_31);
        
        try {
            // 设置模板加载器
            String templatePath = templateConfig.getActualTemplatePath();
            config.setDirectoryForTemplateLoading(new File(templatePath));
            
            // 设置编码
            config.setDefaultEncoding(templateConfig.getEncoding());
            
            // 设置数字格式
            config.setNumberFormat("0.######");
            
            // 设置日期格式
            config.setDateFormat("yyyy-MM-dd");
            config.setTimeFormat("HH:mm:ss");
            config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
            
            // 设置异常处理
            config.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
            
            // 设置缓存
            if (templateConfig.isCacheEnabled()) {
                config.setCacheStorage(new freemarker.cache.MruCacheStorage(100, 300));
                config.setTemplateUpdateDelayMilliseconds(templateConfig.getCacheUpdateDelay() * 1000L);
            } else {
                config.setCacheStorage(new freemarker.cache.NullCacheStorage());
            }
            
            log.info("Freemarker配置初始化完成");
            
        } catch (Exception e) {
            log.error("Freemarker配置初始化失败", e);
            throw new RuntimeException("Freemarker配置初始化失败", e);
        }
        
        return config;
    }
    
    /**
     * 加载模板组织信息
     */
    private void loadTemplateGroups() {
        try {
            Path templatePath = Paths.get(templateConfig.getActualTemplatePath());
            if (!Files.exists(templatePath)) {
                log.warn("模板路径不存在: {}", templatePath);
                return;
            }
            
            // 扫描模板目录
            scanTemplateDirectory(templatePath);
            
            log.info("加载了 {} 个模板组", templateGroups.size());
            
        } catch (Exception e) {
            log.error("加载模板组失败", e);
        }
    }
    
    /**
     * 扫描模板目录
     * 
     * @param templatePath 模板路径
     * @throws IOException IO异常
     */
    private void scanTemplateDirectory(Path templatePath) throws IOException {
        Files.walk(templatePath)
                .filter(Files::isDirectory)
                .forEach(this::processTemplateDirectory);
    }
    
    /**
     * 处理模板目录
     * 
     * @param dirPath 目录路径
     */
    private void processTemplateDirectory(Path dirPath) {
        try {
            String dirName = dirPath.getFileName().toString();
            
            // 跳过隐藏目录和根目录
            if (dirName.startsWith(".") || dirPath.equals(Paths.get(templateConfig.getActualTemplatePath()))) {
                return;
            }
            
            TemplateGroup group = new TemplateGroup();
            group.setName(dirName);
            group.setPath(dirPath.toString());
            
            // 扫描模板文件
            List<TemplateInfo> templates = scanTemplateFiles(dirPath);
            group.setTemplates(templates);
            
            if (!templates.isEmpty()) {
                templateGroups.put(dirName, group);
                log.debug("发现模板组: {} (包含 {} 个模板)", dirName, templates.size());
            }
            
        } catch (Exception e) {
            log.warn("处理模板目录失败: {}", dirPath, e);
        }
    }
    
    /**
     * 扫描模板文件
     * 
     * @param dirPath 目录路径
     * @return 模板信息列表
     * @throws IOException IO异常
     */
    private List<TemplateInfo> scanTemplateFiles(Path dirPath) throws IOException {
        return Files.list(dirPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ftl"))
                .map(this::createTemplateInfo)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建模板信息
     * 
     * @param templatePath 模板文件路径
     * @return 模板信息
     */
    private TemplateInfo createTemplateInfo(Path templatePath) {
        TemplateInfo info = new TemplateInfo();
        info.setName(templatePath.getFileName().toString());
        info.setPath(templatePath.toString());
        info.setGroup(templatePath.getParent().getFileName().toString());
        
        // 推断输出路径模式
        String outputPattern = inferOutputPattern(info.getName(), info.getGroup());
        info.setOutputPattern(outputPattern);
        
        return info;
    }
    
    /**
     * 推断输出路径模式
     * 
     * @param templateName 模板名称
     * @param groupName 组名称
     * @return 输出路径模式
     */
    private String inferOutputPattern(String templateName, String groupName) {
        String baseName = templateName.replace(".ftl", "");
        
        // 根据模板名称和组名称推断输出路径
        if ("dao".equals(groupName)) {
            if (baseName.contains("impl")) {
                return "src/main/java/{packagePath}/dao/impl/{entityName}DaoImpl.java";
            } else {
                return "src/main/java/{packagePath}/dao/{entityName}Dao.java";
            }
        } else if ("service".equals(groupName)) {
            return "src/main/java/{packagePath}/service/{entityName}Service.java";
        } else if ("controller".equals(groupName)) {
            return "src/main/java/{packagePath}/controller/{entityName}Controller.java";
        } else if ("entity".equals(groupName)) {
            return "src/main/java/{packagePath}/entity/{entityName}.java";
        } else if ("mapper".equals(groupName)) {
            return "src/main/resources/mapper/{entityName}Mapper.xml";
        }
        
        // 默认模式
        return "src/main/java/{packagePath}/" + groupName + "/{entityName}" + 
               baseName.substring(0, 1).toUpperCase() + baseName.substring(1) + ".java";
    }
    
    /**
     * 渲染模板
     * 
     * @param context 模板上下文
     * @return 生成的文件列表
     * @throws GeneratorException 生成异常
     */
    public List<GeneratedFile> renderTemplates(TemplateContext context) throws GeneratorException {
        List<GeneratedFile> files = new ArrayList<>();
        
        try {
            // 渲染所有启用的模板组
            for (TemplateGroup group : templateGroups.values()) {
                if (group.isEnabled()) {
                    files.addAll(renderTemplateGroup(group, context));
                }
            }
            
            log.info("成功渲染 {} 个文件", files.size());
            
        } catch (Exception e) {
            throw new GeneratorException("渲染模板失败", e);
        }
        
        return files;
    }
    
    /**
     * 渲染模板组
     * 
     * @param group 模板组
     * @param context 模板上下文
     * @return 生成的文件列表
     * @throws GeneratorException 生成异常
     */
    private List<GeneratedFile> renderTemplateGroup(TemplateGroup group, TemplateContext context) 
            throws GeneratorException {
        List<GeneratedFile> files = new ArrayList<>();
        
        for (TemplateInfo templateInfo : group.getTemplates()) {
            try {
                GeneratedFile file = renderTemplate(templateInfo, context);
                if (file != null) {
                    files.add(file);
                }
            } catch (Exception e) {
                log.error("渲染模板失败: {}", templateInfo.getName(), e);
                throw new GeneratorException("渲染模板失败: " + templateInfo.getName(), e);
            }
        }
        
        return files;
    }
    
    /**
     * 渲染单个模板
     * 
     * @param templateInfo 模板信息
     * @param context 模板上下文
     * @return 生成的文件
     * @throws GeneratorException 生成异常
     */
    public GeneratedFile renderTemplate(TemplateInfo templateInfo, TemplateContext context) 
            throws GeneratorException {
        try {
            // 获取模板
            Template template = getTemplate(templateInfo.getRelativePath());
            
            // 准备上下文数据
            Map<String, Object> contextData = context.toContextMap();
            
            // 渲染模板
            StringWriter writer = new StringWriter();
            template.process(contextData, writer);
            String content = writer.toString();
            
            // 生成输出文件路径
            String outputPath = generateOutputPath(templateInfo, context);
            
            // 创建生成的文件对象
            GeneratedFile file = new GeneratedFile(outputPath, content, templateInfo.getName());
            
            log.debug("成功渲染模板: {} -> {}", templateInfo.getName(), outputPath);
            
            return file;
            
        } catch (TemplateException e) {
            throw new GeneratorException("模板处理异常: " + templateInfo.getName(), e);
        } catch (IOException e) {
            throw new GeneratorException("模板IO异常: " + templateInfo.getName(), e);
        }
    }
    
    /**
     * 获取模板
     * 
     * @param templatePath 模板路径
     * @return 模板对象
     * @throws IOException IO异常
     */
    private Template getTemplate(String templatePath) throws IOException {
        if (templateConfig.isCacheEnabled()) {
            return templateCache.computeIfAbsent(templatePath, path -> {
                try {
                    return freemarkerConfig.getTemplate(path);
                } catch (IOException e) {
                    log.error("加载模板失败: {}", path, e);
                    throw new RuntimeException("加载模板失败: " + path, e);
                }
            });
        } else {
            return freemarkerConfig.getTemplate(templatePath);
        }
    }
    
    /**
     * 生成输出文件路径
     * 
     * @param templateInfo 模板信息
     * @param context 模板上下文
     * @return 输出文件路径
     */
    private String generateOutputPath(TemplateInfo templateInfo, TemplateContext context) {
        String pattern = templateInfo.getOutputPattern();
        
        // 替换占位符
        pattern = pattern.replace("{packagePath}", context.getProject().getPackageName().replace('.', '/'));
        pattern = pattern.replace("{entityName}", context.getTable().getEntityName());
        pattern = pattern.replace("{tableName}", context.getTable().getTableName());
        pattern = pattern.replace("{author}", context.getProject().getAuthor());
        pattern = pattern.replace("{date}", context.getProject().getDate());
        
        return pattern;
    }
    
    /**
     * 获取所有模板组
     * 
     * @return 模板组列表
     */
    public Collection<TemplateGroup> getTemplateGroups() {
        return templateGroups.values();
    }
    
    /**
     * 获取指定组的模板
     * 
     * @param groupName 组名称
     * @return 模板组
     */
    public TemplateGroup getTemplateGroup(String groupName) {
        return templateGroups.get(groupName);
    }
    
    /**
     * 清除模板缓存
     */
    public void clearCache() {
        templateCache.clear();
        freemarkerConfig.clearTemplateCache();
        log.info("模板缓存已清除");
    }
}