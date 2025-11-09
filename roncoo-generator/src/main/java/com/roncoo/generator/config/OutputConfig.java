package com.roncoo.generator.config;

import com.roncoo.generator.exception.ConfigurationException;
import lombok.Data;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 输出配置类
 * 
 * @author roncoo-generator
 */
@Data
public class OutputConfig {
    
    /**
     * 基础输出目录
     */
    private String baseDirectory;
    
    /**
     * 是否覆盖已存在的文件
     */
    private boolean overwriteFiles = false;
    
    /**
     * 是否创建备份
     */
    private boolean createBackup = true;
    
    /**
     * 备份目录
     */
    private String backupDirectory;
    
    /**
     * 输出文件编码
     */
    private String encoding = "UTF-8";
    
    /**
     * 是否生成输出报告
     */
    private boolean generateReport = true;
    
    /**
     * 报告输出路径
     */
    private String reportPath;
    
    /**
     * 文件权限设置（Unix系统）
     */
    private String filePermissions = "644";
    
    /**
     * 目录权限设置（Unix系统）
     */
    private String directoryPermissions = "755";
    
    /**
     * 验证输出配置
     * 
     * @throws ConfigurationException 配置异常
     */
    public void validate() throws ConfigurationException {
        if (baseDirectory == null || baseDirectory.trim().isEmpty()) {
            throw new ConfigurationException("基础输出目录不能为空");
        }
        
        // 检查目录是否存在，如果不存在尝试创建
        try {
            if (!Files.exists(Paths.get(baseDirectory))) {
                Files.createDirectories(Paths.get(baseDirectory));
            }
        } catch (Exception e) {
            throw new ConfigurationException("无法创建输出目录: " + baseDirectory, e);
        }
        
        // 检查目录是否可写
        if (!Files.isWritable(Paths.get(baseDirectory))) {
            throw new ConfigurationException("输出目录不可写: " + baseDirectory);
        }
        
        if (encoding == null || encoding.trim().isEmpty()) {
            throw new ConfigurationException("输出文件编码不能为空");
        }
        
        // 验证备份目录
        if (createBackup) {
            if (backupDirectory == null || backupDirectory.trim().isEmpty()) {
                // 使用默认备份目录
                backupDirectory = baseDirectory + "/backup";
            }
            
            try {
                if (!Files.exists(Paths.get(backupDirectory))) {
                    Files.createDirectories(Paths.get(backupDirectory));
                }
            } catch (Exception e) {
                throw new ConfigurationException("无法创建备份目录: " + backupDirectory, e);
            }
        }
        
        // 验证权限设置格式
        if (filePermissions != null && !isValidPermission(filePermissions)) {
            throw new ConfigurationException("文件权限格式不正确: " + filePermissions);
        }
        
        if (directoryPermissions != null && !isValidPermission(directoryPermissions)) {
            throw new ConfigurationException("目录权限格式不正确: " + directoryPermissions);
        }
    }
    
    /**
     * 验证权限格式
     * 
     * @param permission 权限字符串
     * @return 是否有效
     */
    private boolean isValidPermission(String permission) {
        if (permission == null || permission.length() != 3) {
            return false;
        }
        
        // 验证八进制权限格式
        try {
            int perm = Integer.parseInt(permission, 8);
            return perm >= 0 && perm <= 777;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 获取实际的备份目录
     * 
     * @return 备份目录路径
     */
    public String getActualBackupDirectory() {
        if (backupDirectory != null) {
            return backupDirectory;
        }
        return baseDirectory + "/backup";
    }
    
    /**
     * 获取实际的报告路径
     * 
     * @return 报告路径
     */
    public String getActualReportPath() {
        if (reportPath != null) {
            return reportPath;
        }
        return baseDirectory + "/generation-report.html";
    }
}