package com.roncoo.generator.plugin;

import com.roncoo.generator.config.PluginConfig;
import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import com.roncoo.generator.exception.GeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件管理器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class PluginManager {
    
    /**
     * 已注册的插件
     */
    private final Map<String, GeneratorPlugin> registeredPlugins = new ConcurrentHashMap<>();
    
    /**
     * 插件配置列表
     */
    private final List<PluginConfig> pluginConfigs;
    
    /**
     * 已初始化的插件
     */
    private final List<GeneratorPlugin> initializedPlugins = new ArrayList<>();
    
    /**
     * 构造函数
     * 
     * @param pluginConfigs 插件配置列表
     */
    public PluginManager(List<PluginConfig> pluginConfigs) {
        this.pluginConfigs = pluginConfigs != null ? pluginConfigs : new ArrayList<>();
        registerBuiltinPlugins();
        initializePlugins();
    }
    
    /**
     * 注册内置插件
     */
    private void registerBuiltinPlugins() {
        // 注册代码格式化插件
        registerPlugin(new CodeFormatterPlugin());
        
        // 注册文档生成插件
        registerPlugin(new DocumentGeneratorPlugin());
        
        // 注册测试代码生成插件
        registerPlugin(new TestGeneratorPlugin());
        
        // 注册 Swagger 文档插件
        registerPlugin(new SwaggerGeneratorPlugin());
        
        // 注册代码质量检查插件
        registerPlugin(new CodeQualityPlugin());
        
        log.info("已注册 {} 个内置插件", registeredPlugins.size());
    }
    
    /**
     * 注册插件
     * 
     * @param plugin 插件实例
     */
    public void registerPlugin(GeneratorPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("插件不能为null");
        }
        
        String pluginName = plugin.getName();
        if (pluginName == null || pluginName.trim().isEmpty()) {
            throw new IllegalArgumentException("插件名称不能为空");
        }
        
        registeredPlugins.put(pluginName, plugin);
        log.debug("注册插件: {}", pluginName);
    }
    
    /**
     * 初始化插件
     */
    private void initializePlugins() {
        for (PluginConfig config : pluginConfigs) {
            if (!config.isEnabled()) {
                continue;
            }
            
            try {
                GeneratorPlugin plugin = createPluginInstance(config);
                if (plugin != null) {
                    plugin.initialize(config.getProperties());
                    initializedPlugins.add(plugin);
                    log.info("初始化插件成功: {}", config.getName());
                }
            } catch (Exception e) {
                log.error("初始化插件失败: {}", config.getName(), e);
            }
        }
        
        // 按执行顺序排序
        initializedPlugins.sort(Comparator.comparingInt(GeneratorPlugin::getOrder));
        
        log.info("成功初始化 {} 个插件", initializedPlugins.size());
    }
    
    /**
     * 创建插件实例
     * 
     * @param config 插件配置
     * @return 插件实例
     */
    private GeneratorPlugin createPluginInstance(PluginConfig config) {
        // 首先检查是否已注册的内置插件
        GeneratorPlugin plugin = registeredPlugins.get(config.getName());
        if (plugin != null) {
            return plugin;
        }
        
        // 尝试通过类名创建插件实例
        try {
            Class<?> pluginClass = Class.forName(config.getClassName());
            if (GeneratorPlugin.class.isAssignableFrom(pluginClass)) {
                return (GeneratorPlugin) pluginClass.getDeclaredConstructor().newInstance();
            } else {
                log.warn("插件类不实现 GeneratorPlugin 接口: {}", config.getClassName());
            }
        } catch (Exception e) {
            log.error("创建插件实例失败: {}", config.getClassName(), e);
        }
        
        return null;
    }
    
    /**
     * 执行代码生成前的插件
     * 
     * @param context 模板上下文
     * @throws GeneratorException 生成异常
     */
    public void executeBeforeGeneration(TemplateContext context) throws GeneratorException {
        for (GeneratorPlugin plugin : initializedPlugins) {
            if (plugin.isEnabled()) {
                try {
                    log.debug("执行插件 beforeGeneration: {}", plugin.getName());
                    plugin.beforeGeneration(context);
                } catch (Exception e) {
                    log.error("插件 {} 的 beforeGeneration 执行失败", plugin.getName(), e);
                    throw new GeneratorException("插件执行失败: " + plugin.getName(), e);
                }
            }
        }
    }
    
    /**
     * 执行代码生成后的插件
     * 
     * @param context 模板上下文
     * @param generatedFiles 生成的文件列表
     * @throws GeneratorException 生成异常
     */
    public void executeAfterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) 
            throws GeneratorException {
        for (GeneratorPlugin plugin : initializedPlugins) {
            if (plugin.isEnabled()) {
                try {
                    log.debug("执行插件 afterGeneration: {}", plugin.getName());
                    plugin.afterGeneration(context, generatedFiles);
                } catch (Exception e) {
                    log.error("插件 {} 的 afterGeneration 执行失败", plugin.getName(), e);
                    throw new GeneratorException("插件执行失败: " + plugin.getName(), e);
                }
            }
        }
    }
    
    /**
     * 获取已注册的插件列表
     * 
     * @return 插件列表
     */
    public Collection<GeneratorPlugin> getRegisteredPlugins() {
        return Collections.unmodifiableCollection(registeredPlugins.values());
    }
    
    /**
     * 获取已初始化的插件列表
     * 
     * @return 插件列表
     */
    public List<GeneratorPlugin> getInitializedPlugins() {
        return Collections.unmodifiableList(initializedPlugins);
    }
    
    /**
     * 根据名称获取插件
     * 
     * @param name 插件名称
     * @return 插件实例
     */
    public GeneratorPlugin getPlugin(String name) {
        return registeredPlugins.get(name);
    }
    
    /**
     * 销毁所有插件
     */
    public void destroy() {
        for (GeneratorPlugin plugin : initializedPlugins) {
            try {
                plugin.destroy();
                log.debug("销毁插件: {}", plugin.getName());
            } catch (Exception e) {
                log.warn("销毁插件失败: {}", plugin.getName(), e);
            }
        }
        
        initializedPlugins.clear();
        log.info("所有插件已销毁");
    }
}