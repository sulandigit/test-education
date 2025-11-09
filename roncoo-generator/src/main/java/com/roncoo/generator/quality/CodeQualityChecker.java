package com.roncoo.generator.quality;

import com.roncoo.generator.engine.GeneratedFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 代码质量检查器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class CodeQualityChecker {
    
    /**
     * 质量规则列表
     */
    private final List<QualityRule> rules = new ArrayList<>();
    
    /**
     * 是否启用检查
     */
    private boolean enabled = true;
    
    /**
     * 构造函数
     */
    public CodeQualityChecker() {
        initializeDefaultRules();
    }
    
    /**
     * 初始化默认规则
     */
    private void initializeDefaultRules() {
        // Java代码规则
        addRule(new QualityRule("java_class_naming", 
                "类名应使用PascalCase命名",
                ".*\\.java$",
                "public\\s+class\\s+([A-Z][a-zA-Z0-9]*)\\s*\\{",
                QualityLevel.ERROR));
        
        addRule(new QualityRule("java_method_naming",
                "方法名应使用camelCase命名", 
                ".*\\.java$",
                "public\\s+\\w+\\s+([a-z][a-zA-Z0-9]*)\\s*\\(",
                QualityLevel.WARNING));
        
        addRule(new QualityRule("java_imports_organized",
                "导入语句应该有序组织",
                ".*\\.java$", 
                "import\\s+java\\.",
                QualityLevel.INFO));
        
        addRule(new QualityRule("java_no_unused_imports",
                "不应有未使用的导入",
                ".*\\.java$",
                "import\\s+([\\w\\.]+);",
                QualityLevel.WARNING));
        
        addRule(new QualityRule("java_proper_javadoc",
                "公共方法应有JavaDoc注释",
                ".*\\.java$",
                "public\\s+\\w+\\s+\\w+\\s*\\(",
                QualityLevel.INFO));
        
        // 通用规则
        addRule(new QualityRule("no_trailing_whitespace",
                "行末不应有多余空格",
                ".*",
                "\\s+$",
                QualityLevel.WARNING));
        
        addRule(new QualityRule("consistent_line_endings",
                "应使用一致的行结束符",
                ".*",
                "\\r\\n|\\n|\\r",
                QualityLevel.INFO));
        
        log.info("已加载 {} 条代码质量规则", rules.size());
    }
    
    /**
     * 添加质量规则
     * 
     * @param rule 质量规则
     */
    public void addRule(QualityRule rule) {
        rules.add(rule);
    }
    
    /**
     * 检查生成的文件
     * 
     * @param files 生成的文件列表
     * @return 质量检查结果
     */
    public QualityCheckResult checkFiles(List<GeneratedFile> files) {
        if (!enabled) {
            return new QualityCheckResult();
        }
        
        log.info("开始代码质量检查，检查 {} 个文件", files.size());
        
        QualityCheckResult result = new QualityCheckResult();
        
        for (GeneratedFile file : files) {
            try {
                List<QualityIssue> issues = checkFile(file);
                result.addFileResult(file.getFilePath(), issues);
            } catch (Exception e) {
                log.error("检查文件质量失败: {}", file.getFilePath(), e);
                result.addError(file.getFilePath(), e.getMessage());
            }
        }
        
        log.info("代码质量检查完成，发现 {} 个问题", result.getTotalIssues());
        
        return result;
    }
    
    /**
     * 检查单个文件
     * 
     * @param file 文件
     * @return 质量问题列表
     */
    private List<QualityIssue> checkFile(GeneratedFile file) {
        List<QualityIssue> issues = new ArrayList<>();
        String content = file.getContent();
        String filePath = file.getFilePath();
        
        for (QualityRule rule : rules) {
            if (rule.appliesToFile(filePath)) {
                List<QualityIssue> ruleIssues = checkRule(rule, content, filePath);
                issues.addAll(ruleIssues);
            }
        }
        
        return issues;
    }
    
    /**
     * 检查单个规则
     * 
     * @param rule 规则
     * @param content 文件内容
     * @param filePath 文件路径
     * @return 质量问题列表
     */
    private List<QualityIssue> checkRule(QualityRule rule, String content, String filePath) {
        List<QualityIssue> issues = new ArrayList<>();
        
        try {
            Pattern pattern = Pattern.compile(rule.getPattern(), Pattern.MULTILINE);
            java.util.regex.Matcher matcher = pattern.matcher(content);
            
            String[] lines = content.split("\\n");
            int lineNumber = 1;
            int currentPos = 0;
            
            while (matcher.find()) {
                // 计算行号
                while (currentPos < matcher.start() && lineNumber <= lines.length) {
                    if (content.charAt(currentPos) == '\\n') {
                        lineNumber++;
                    }
                    currentPos++;
                }
                
                QualityIssue issue = new QualityIssue();
                issue.setRuleId(rule.getId());
                issue.setMessage(rule.getMessage());
                issue.setLevel(rule.getLevel());
                issue.setFilePath(filePath);
                issue.setLineNumber(lineNumber);
                issue.setColumn(matcher.start() - getLineStart(content, matcher.start()));
                issue.setMatchedText(matcher.group());
                
                issues.add(issue);
            }
            
        } catch (Exception e) {
            log.warn("规则检查失败: {}", rule.getId(), e);
        }
        
        return issues;
    }
    
    /**
     * 获取指定位置所在行的起始位置
     * 
     * @param content 内容
     * @param position 位置
     * @return 行起始位置
     */
    private int getLineStart(String content, int position) {
        for (int i = position - 1; i >= 0; i--) {
            if (content.charAt(i) == '\\n') {
                return i + 1;
            }
        }
        return 0;
    }
    
    /**
     * 启用质量检查
     */
    public void enable() {
        this.enabled = true;
        log.info("代码质量检查已启用");
    }
    
    /**
     * 禁用质量检查
     */
    public void disable() {
        this.enabled = false;
        log.info("代码质量检查已禁用");
    }
    
    /**
     * 获取所有规则
     * 
     * @return 规则列表
     */
    public List<QualityRule> getRules() {
        return new ArrayList<>(rules);
    }
    
    /**
     * 质量规则类
     */
    @Data
    public static class QualityRule {
        /**
         * 规则ID
         */
        private String id;
        
        /**
         * 规则描述
         */
        private String message;
        
        /**
         * 文件名模式
         */
        private String filePattern;
        
        /**
         * 检查模式
         */
        private String pattern;
        
        /**
         * 质量级别
         */
        private QualityLevel level;
        
        public QualityRule(String id, String message, String filePattern, 
                          String pattern, QualityLevel level) {
            this.id = id;
            this.message = message;
            this.filePattern = filePattern;
            this.pattern = pattern;
            this.level = level;
        }
        
        /**
         * 检查规则是否适用于文件
         * 
         * @param filePath 文件路径
         * @return 是否适用
         */
        public boolean appliesToFile(String filePath) {
            return filePath.matches(filePattern);
        }
    }
    
    /**
     * 质量问题类
     */
    @Data
    public static class QualityIssue {
        /**
         * 规则ID
         */
        private String ruleId;
        
        /**
         * 问题描述
         */
        private String message;
        
        /**
         * 问题级别
         */
        private QualityLevel level;
        
        /**
         * 文件路径
         */
        private String filePath;
        
        /**
         * 行号
         */
        private int lineNumber;
        
        /**
         * 列号
         */
        private int column;
        
        /**
         * 匹配的文本
         */
        private String matchedText;
        
        @Override
        public String toString() {
            return String.format("%s:%d:%d [%s] %s", 
                    filePath, lineNumber, column, level, message);
        }
    }
    
    /**
     * 质量级别枚举
     */
    public enum QualityLevel {
        ERROR("错误"),
        WARNING("警告"),
        INFO("信息");
        
        private final String displayName;
        
        QualityLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}