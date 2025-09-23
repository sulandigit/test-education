package com.roncoo.generator.plugin;

import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import com.roncoo.generator.quality.CodeQualityChecker;
import com.roncoo.generator.quality.QualityCheckResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 代码质量检查插件
 * 
 * @author roncoo-generator
 */
@Slf4j
public class CodeQualityPlugin implements GeneratorPlugin {
    
    /**
     * 质量检查器
     */
    private CodeQualityChecker qualityChecker;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 最低质量分数要求
     */
    private double minQualityScore = 70.0;
    
    /**
     * 是否在质量不达标时失败
     */
    private boolean failOnLowQuality = false;
    
    @Override
    public String getName() {
        return "CodeQuality";
    }
    
    @Override
    public String getDescription() {
        return "代码质量检查插件，检查生成代码的质量并提供改进建议";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public int getOrder() {
        return 500; // 在其他插件之后执行
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        if (properties != null) {
            enabled = (Boolean) properties.getOrDefault("enabled", true);
            minQualityScore = ((Number) properties.getOrDefault("minQualityScore", 70.0)).doubleValue();
            failOnLowQuality = (Boolean) properties.getOrDefault("failOnLowQuality", false);
        }
        
        qualityChecker = new CodeQualityChecker();
        
        log.info("代码质量检查插件初始化完成 - 最低分数要求: {}, 低质量时失败: {}", 
                minQualityScore, failOnLowQuality);
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        if (!enabled) {
            return;
        }
        
        try {
            log.info("开始代码质量检查...");
            
            QualityCheckResult result = qualityChecker.checkFiles(generatedFiles);
            result.complete();
            
            // 输出检查结果
            log.info("代码质量检查完成");
            log.info(result.generateSummary());
            
            // 详细报告
            if (log.isDebugEnabled()) {
                log.debug("质量检查详细报告:\\n{}", result.generateReport());
            }
            
            // 检查是否满足质量要求
            if (!result.passesQualityStandard(minQualityScore)) {
                String message = String.format(
                        "代码质量不达标: 得分%.1f < 要求%.1f，错误数量: %d",
                        result.getQualityScore(), minQualityScore, result.getErrorCount()
                );
                
                if (failOnLowQuality) {
                    throw new RuntimeException(message);
                } else {
                    log.warn(message);
                }
            }
            
            // 添加质量报告文件
            if (result.getTotalIssues() > 0) {
                GeneratedFile reportFile = new GeneratedFile(
                        "quality-report.txt",
                        result.generateReport(),
                        "质量报告"
                );
                generatedFiles.add(reportFile);
            }
            
        } catch (Exception e) {
            log.error("代码质量检查失败", e);
            if (failOnLowQuality) {
                throw new RuntimeException("代码质量检查失败", e);
            }
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 获取质量检查器
     * 
     * @return 质量检查器
     */
    public CodeQualityChecker getQualityChecker() {
        return qualityChecker;
    }
}