package com.roncoo.generator.quality;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 质量检查结果
 * 
 * @author roncoo-generator
 */
@Data
public class QualityCheckResult {
    
    /**
     * 文件检查结果
     */
    private final Map<String, List<CodeQualityChecker.QualityIssue>> fileResults = new HashMap<>();
    
    /**
     * 检查错误
     */
    private final Map<String, String> errors = new HashMap<>();
    
    /**
     * 检查开始时间
     */
    private long startTime = System.currentTimeMillis();
    
    /**
     * 检查结束时间
     */
    private long endTime;
    
    /**
     * 添加文件检查结果
     * 
     * @param filePath 文件路径
     * @param issues 质量问题列表
     */
    public void addFileResult(String filePath, List<CodeQualityChecker.QualityIssue> issues) {
        fileResults.put(filePath, new ArrayList<>(issues));
    }
    
    /**
     * 添加检查错误
     * 
     * @param filePath 文件路径
     * @param error 错误信息
     */
    public void addError(String filePath, String error) {
        errors.put(filePath, error);
    }
    
    /**
     * 完成检查
     */
    public void complete() {
        this.endTime = System.currentTimeMillis();
    }
    
    /**
     * 获取检查耗时
     * 
     * @return 耗时（毫秒）
     */
    public long getDuration() {
        long end = endTime > 0 ? endTime : System.currentTimeMillis();
        return end - startTime;
    }
    
    /**
     * 获取检查的文件总数
     * 
     * @return 文件总数
     */
    public int getTotalFiles() {
        return fileResults.size();
    }
    
    /**
     * 获取问题总数
     * 
     * @return 问题总数
     */
    public int getTotalIssues() {
        return fileResults.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    /**
     * 获取错误级别问题数量
     * 
     * @return 错误数量
     */
    public int getErrorCount() {
        return getIssueCountByLevel(CodeQualityChecker.QualityLevel.ERROR);
    }
    
    /**
     * 获取警告级别问题数量
     * 
     * @return 警告数量
     */
    public int getWarningCount() {
        return getIssueCountByLevel(CodeQualityChecker.QualityLevel.WARNING);
    }
    
    /**
     * 获取信息级别问题数量
     * 
     * @return 信息数量
     */
    public int getInfoCount() {
        return getIssueCountByLevel(CodeQualityChecker.QualityLevel.INFO);
    }
    
    /**
     * 获取指定级别的问题数量
     * 
     * @param level 问题级别
     * @return 问题数量
     */
    private int getIssueCountByLevel(CodeQualityChecker.QualityLevel level) {
        return fileResults.values().stream()
                .flatMap(List::stream)
                .mapToInt(issue -> issue.getLevel() == level ? 1 : 0)
                .sum();
    }
    
    /**
     * 获取所有问题
     * 
     * @return 所有问题列表
     */
    public List<CodeQualityChecker.QualityIssue> getAllIssues() {
        return fileResults.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定文件的问题
     * 
     * @param filePath 文件路径
     * @return 问题列表
     */
    public List<CodeQualityChecker.QualityIssue> getFileIssues(String filePath) {
        return fileResults.getOrDefault(filePath, new ArrayList<>());
    }
    
    /**
     * 获取有问题的文件数量
     * 
     * @return 有问题的文件数量
     */
    public int getProblematicFileCount() {
        return (int) fileResults.values().stream()
                .filter(issues -> !issues.isEmpty())
                .count();
    }
    
    /**
     * 获取质量得分（0-100）
     * 
     * @return 质量得分
     */
    public double getQualityScore() {
        int totalFiles = getTotalFiles();
        if (totalFiles == 0) {
            return 100.0;
        }
        
        int problematicFiles = getProblematicFileCount();
        int errorCount = getErrorCount();
        int warningCount = getWarningCount();
        
        // 计算得分：基础分100，每个错误扣10分，每个警告扣5分，有问题的文件扣2分
        double score = 100.0;
        score -= errorCount * 10.0;
        score -= warningCount * 5.0;
        score -= problematicFiles * 2.0;
        
        return Math.max(0.0, score);
    }
    
    /**
     * 检查是否通过质量标准
     * 
     * @param minScore 最低分数要求
     * @return 是否通过
     */
    public boolean passesQualityStandard(double minScore) {
        return getQualityScore() >= minScore && getErrorCount() == 0;
    }
    
    /**
     * 生成质量报告
     * 
     * @return 质量报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== 代码质量检查报告 ===\\n");
        report.append("检查时间: ").append(getDuration()).append("ms\\n");
        report.append("检查文件: ").append(getTotalFiles()).append("\\n");
        report.append("问题总数: ").append(getTotalIssues()).append("\\n");
        report.append("错误: ").append(getErrorCount()).append("\\n");
        report.append("警告: ").append(getWarningCount()).append("\\n");
        report.append("信息: ").append(getInfoCount()).append("\\n");
        report.append("质量得分: ").append(String.format("%.1f", getQualityScore())).append("/100\\n");
        report.append("\\n");
        
        if (!errors.isEmpty()) {
            report.append("=== 检查错误 ===\\n");
            errors.forEach((file, error) -> 
                    report.append(file).append(": ").append(error).append("\\n"));
            report.append("\\n");
        }
        
        if (getTotalIssues() > 0) {
            report.append("=== 质量问题详情 ===\\n");
            
            // 按文件分组显示问题
            fileResults.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> {
                        String filePath = entry.getKey();
                        List<CodeQualityChecker.QualityIssue> issues = entry.getValue();
                        
                        report.append("文件: ").append(filePath).append("\\n");
                        
                        issues.forEach(issue -> 
                                report.append("  ").append(issue).append("\\n"));
                        
                        report.append("\\n");
                    });
        }
        
        return report.toString();
    }
    
    /**
     * 生成简要报告
     * 
     * @return 简要报告
     */
    public String generateSummary() {
        return String.format(
                "质量检查完成: 检查了%d个文件，发现%d个问题（错误:%d，警告:%d，信息:%d），质量得分:%.1f/100",
                getTotalFiles(), getTotalIssues(), getErrorCount(), 
                getWarningCount(), getInfoCount(), getQualityScore()
        );
    }
}