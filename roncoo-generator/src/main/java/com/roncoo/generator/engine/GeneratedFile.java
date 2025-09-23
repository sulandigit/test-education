package com.roncoo.generator.engine;

import com.roncoo.generator.config.OutputConfig;
import lombok.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 生成的文件
 * 
 * @author roncoo-generator
 */
@Data
public class GeneratedFile {
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件内容
     */
    private String content;
    
    /**
     * 文件编码
     */
    private String encoding = "UTF-8";
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 文件类型
     */
    private FileType fileType = FileType.JAVA;
    
    /**
     * 是否覆盖已存在的文件
     */
    private boolean overwrite = false;
    
    /**
     * 文件大小（字节）
     */
    private long size;
    
    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime = java.time.LocalDateTime.now();
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     * @param content 文件内容
     */
    public GeneratedFile(String filePath, String content) {
        this.filePath = filePath;
        this.content = content;
        this.size = content.getBytes(StandardCharsets.UTF_8).length;
    }
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     * @param content 文件内容
     * @param templateName 模板名称
     */
    public GeneratedFile(String filePath, String content, String templateName) {
        this(filePath, content);
        this.templateName = templateName;
    }
    
    /**
     * 写入文件
     * 
     * @param outputConfig 输出配置
     * @throws IOException IO异常
     */
    public void writeToFile(OutputConfig outputConfig) throws IOException {
        Path path = Paths.get(outputConfig.getBaseDirectory(), filePath);
        
        // 创建父目录
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        // 检查文件是否已存在
        if (Files.exists(path) && !outputConfig.isOverwriteFiles() && !overwrite) {
            // 创建备份
            if (outputConfig.isCreateBackup()) {
                createBackup(path, outputConfig);
            } else {
                throw new IOException("文件已存在且未允许覆盖: " + path);
            }
        }
        
        // 写入文件
        Files.write(path, content.getBytes(outputConfig.getEncoding()), 
                   StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        // 设置文件权限（Unix系统）
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                setFilePermissions(path, outputConfig.getFilePermissions());
            } catch (Exception e) {
                // 权限设置失败不影响文件生成
                System.out.println("设置文件权限失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 创建备份文件
     * 
     * @param originalPath 原文件路径
     * @param outputConfig 输出配置
     * @throws IOException IO异常
     */
    private void createBackup(Path originalPath, OutputConfig outputConfig) throws IOException {
        String backupDir = outputConfig.getActualBackupDirectory();
        Path backupPath = Paths.get(backupDir);
        
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }
        
        // 生成备份文件名
        String fileName = originalPath.getFileName().toString();
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = fileName + ".bak." + timestamp;
        
        Path backupFilePath = backupPath.resolve(backupFileName);
        Files.copy(originalPath, backupFilePath);
    }
    
    /**
     * 设置文件权限
     * 
     * @param path 文件路径
     * @param permissions 权限字符串
     * @throws IOException IO异常
     */
    private void setFilePermissions(Path path, String permissions) throws IOException {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        
        try {
            // 使用 chmod 命令设置权限
            ProcessBuilder pb = new ProcessBuilder("chmod", permissions, path.toString());
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new IOException("设置文件权限失败，退出代码: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("设置文件权限被中断", e);
        }
    }
    
    /**
     * 获取文件扩展名
     * 
     * @return 文件扩展名
     */
    public String getFileExtension() {
        if (filePath == null) {
            return "";
        }
        
        int lastDot = filePath.lastIndexOf('.');
        return lastDot >= 0 ? filePath.substring(lastDot + 1) : "";
    }
    
    /**
     * 获取文件名（不含路径）
     * 
     * @return 文件名
     */
    public String getFileName() {
        if (filePath == null) {
            return "";
        }
        
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }
    
    /**
     * 获取相对路径
     * 
     * @param basePath 基础路径
     * @return 相对路径
     */
    public String getRelativePath(String basePath) {
        if (filePath == null || basePath == null) {
            return filePath;
        }
        
        try {
            Path base = Paths.get(basePath);
            Path file = Paths.get(filePath);
            return base.relativize(file).toString();
        } catch (Exception e) {
            return filePath;
        }
    }
    
    /**
     * 文件类型枚举
     */
    public enum FileType {
        JAVA("java"),
        XML("xml"),
        YAML("yaml"),
        PROPERTIES("properties"),
        SQL("sql"),
        HTML("html"),
        MARKDOWN("md"),
        OTHER("");
        
        private final String extension;
        
        FileType(String extension) {
            this.extension = extension;
        }
        
        public String getExtension() {
            return extension;
        }
        
        public static FileType fromExtension(String extension) {
            for (FileType type : values()) {
                if (type.extension.equals(extension)) {
                    return type;
                }
            }
            return OTHER;
        }
    }
}