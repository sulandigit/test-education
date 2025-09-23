package com.roncoo.generator.plugin;

import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;

import java.util.List;

/**
 * 代码生成器插件接口
 * 
 * @author roncoo-generator
 */
public interface GeneratorPlugin {
    
    /**
     * 获取插件名称
     * 
     * @return 插件名称
     */
    String getName();
    
    /**
     * 获取插件描述
     * 
     * @return 插件描述
     */
    String getDescription();
    
    /**
     * 获取插件版本
     * 
     * @return 插件版本
     */
    String getVersion();
    
    /**
     * 获取插件执行顺序
     * 数值越小越先执行
     * 
     * @return 执行顺序
     */
    int getOrder();
    
    /**
     * 插件初始化
     * 
     * @param properties 插件配置属性
     */
    void initialize(java.util.Map<String, Object> properties);
    
    /**
     * 代码生成前执行
     * 
     * @param context 模板上下文
     */
    default void beforeGeneration(TemplateContext context) {
        // 默认空实现
    }
    
    /**
     * 代码生成后执行
     * 
     * @param context 模板上下文
     * @param generatedFiles 生成的文件列表
     */
    default void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        // 默认空实现
    }
    
    /**
     * 插件销毁
     */
    default void destroy() {
        // 默认空实现
    }
    
    /**
     * 检查插件是否启用
     * 
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
}