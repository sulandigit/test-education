package com.roncoo.generator.engine;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代码生成结果
 * 
 * @author roncoo-generator
 */
@Data
public class GenerationResult {
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 是否成功
     */
    private boolean success = false;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 处理的表数量
     */
    private int tableCount;
    
    /**
     * 成功处理的表数量
     */
    private final AtomicInteger successCount = new AtomicInteger(0);
    
    /**
     * 失败处理的表数量
     */
    private final AtomicInteger errorCount = new AtomicInteger(0);
    
    /**
     * 生成的文件列表
     */
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    
    /**
     * 错误详情
     */
    private final Map<String, String> errors = new ConcurrentHashMap<>();
    
    /**
     * 统计信息
     */
    private final Map<String, Object> statistics = new ConcurrentHashMap<>();
    
    /**
     * 获取耗时（毫秒）
     * 
     * @return 耗时
     */
    public long getDuration() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return ChronoUnit.MILLIS.between(startTime, endTime);
    }
    
    /**
     * 增加成功计数
     */
    public void incrementSuccessCount() {
        successCount.incrementAndGet();
    }
    
    /**
     * 增加错误计数
     */
    public void incrementErrorCount() {
        errorCount.incrementAndGet();
    }
    
    /**
     * 获取成功计数
     * 
     * @return 成功计数
     */
    public int getSuccessCount() {
        return successCount.get();
    }
    
    /**
     * 获取错误计数
     * 
     * @return 错误计数
     */
    public int getErrorCount() {
        return errorCount.get();
    }
    
    /**
     * 添加生成的文件
     * 
     * @param files 文件列表
     */
    public synchronized void addGeneratedFiles(List<GeneratedFile> files) {
        this.generatedFiles.addAll(files);
    }
    
    /**
     * 添加错误信息
     * 
     * @param tableName 表名
     * @param error 错误信息
     */
    public void addError(String tableName, String error) {
        errors.put(tableName, error);
    }
    
    /**
     * 添加统计信息
     * 
     * @param key 统计项
     * @param value 统计值
     */
    public void addStatistic(String key, Object value) {
        statistics.put(key, value);
    }
    
    /**
     * 获取生成文件总数
     * 
     * @return 文件总数
     */
    public int getTotalFileCount() {
        return generatedFiles.size();
    }
    
    /**
     * 获取成功率
     * 
     * @return 成功率（百分比）
     */
    public double getSuccessRate() {
        if (tableCount == 0) {
            return 0.0;
        }
        return (double) getSuccessCount() / tableCount * 100;
    }
    
    /**
     * 获取生成摘要
     * 
     * @return 生成摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("代码生成结果摘要:\n");
        summary.append("- 状态: ").append(success ? "成功" : "失败").append("\n");
        summary.append("- 耗时: ").append(getDuration()).append("ms\n");
        summary.append("- 处理表数: ").append(tableCount).append("\n");
        summary.append("- 成功数: ").append(getSuccessCount()).append("\n");
        summary.append("- 失败数: ").append(getErrorCount()).append("\n");
        summary.append("- 成功率: ").append(String.format("%.1f", getSuccessRate())).append("%\n");
        summary.append("- 生成文件数: ").append(getTotalFileCount()).append("\n");
        
        if (!errors.isEmpty()) {
            summary.append("- 错误详情:\n");
            errors.forEach((table, error) -> 
                    summary.append("  ").append(table).append(": ").append(error).append("\n"));
        }
        
        return summary.toString();
    }
}