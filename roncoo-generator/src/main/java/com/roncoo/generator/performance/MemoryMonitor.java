package com.roncoo.generator.performance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 内存监控器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class MemoryMonitor {
    
    /**
     * 内存管理Bean
     */
    private final MemoryMXBean memoryBean;
    
    /**
     * 监控配置
     */
    private final MonitorConfig config;
    
    /**
     * 定时任务执行器
     */
    private final ScheduledExecutorService scheduler;
    
    /**
     * 内存统计信息
     */
    private volatile MemoryStatistics statistics;
    
    /**
     * 监控监听器
     */
    private MemoryMonitorListener listener;
    
    /**
     * 构造函数
     * 
     * @param config 监控配置
     */
    public MemoryMonitor(MonitorConfig config) {
        this.config = config;
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "memory-monitor");
            thread.setDaemon(true);
            return thread;
        });
        this.statistics = new MemoryStatistics();
        
        if (config.isEnabled()) {
            startMonitoring();
        }
        
        log.info("内存监控器初始化完成 - 监控间隔: {}ms, 警告阈值: {}%", 
                config.getMonitorInterval(), config.getWarningThreshold() * 100);
    }
    
    /**
     * 开始监控
     */
    public void startMonitoring() {
        scheduler.scheduleWithFixedDelay(
                this::collectMemoryData,
                0,
                config.getMonitorInterval(),
                TimeUnit.MILLISECONDS
        );
        log.info("内存监控已启动");
    }
    
    /**
     * 停止监控
     */
    public void stopMonitoring() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("内存监控已停止");
    }
    
    /**
     * 获取当前内存使用情况
     * 
     * @return 内存使用情况
     */
    public MemoryInfo getCurrentMemoryInfo() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        MemoryInfo info = new MemoryInfo();
        info.setHeapUsed(heapUsage.getUsed());
        info.setHeapMax(heapUsage.getMax());
        info.setHeapCommitted(heapUsage.getCommitted());
        info.setNonHeapUsed(nonHeapUsage.getUsed());
        info.setNonHeapMax(nonHeapUsage.getMax());
        info.setNonHeapCommitted(nonHeapUsage.getCommitted());
        info.setTimestamp(System.currentTimeMillis());
        
        return info;
    }
    
    /**
     * 获取内存统计信息
     * 
     * @return 统计信息
     */
    public MemoryStatistics getStatistics() {
        return statistics;
    }
    
    /**
     * 设置监控监听器
     * 
     * @param listener 监听器
     */
    public void setListener(MemoryMonitorListener listener) {
        this.listener = listener;
    }
    
    /**
     * 手动触发垃圾回收
     */
    public void triggerGC() {
        log.info("手动触发垃圾回收");
        System.gc();
        
        // 等待一段时间让GC完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        collectMemoryData();
    }
    
    /**
     * 收集内存数据
     */
    private void collectMemoryData() {
        try {
            MemoryInfo currentInfo = getCurrentMemoryInfo();
            updateStatistics(currentInfo);
            
            // 检查是否需要警告
            double heapUsageRatio = (double) currentInfo.getHeapUsed() / currentInfo.getHeapMax();
            if (heapUsageRatio > config.getWarningThreshold()) {
                handleHighMemoryUsage(currentInfo, heapUsageRatio);
            }
            
            // 检查是否需要自动GC
            if (config.isAutoGcEnabled() && heapUsageRatio > config.getAutoGcThreshold()) {
                log.warn("内存使用率达到 {:.1f}%，触发自动垃圾回收", heapUsageRatio * 100);
                triggerGC();
            }
            
            // 通知监听器
            if (listener != null) {
                listener.onMemoryUpdate(currentInfo);
            }
            
        } catch (Exception e) {
            log.error("收集内存数据失败", e);
        }
    }
    
    /**
     * 更新统计信息
     * 
     * @param info 内存信息
     */
    private void updateStatistics(MemoryInfo info) {
        statistics.updateMaxHeapUsed(info.getHeapUsed());
        statistics.updateMaxNonHeapUsed(info.getNonHeapUsed());
        statistics.incrementSampleCount();
        
        double heapUsageRatio = (double) info.getHeapUsed() / info.getHeapMax();
        statistics.updateMaxHeapUsageRatio(heapUsageRatio);
        
        if (heapUsageRatio > config.getWarningThreshold()) {
            statistics.incrementHighUsageCount();
        }
    }
    
    /**
     * 处理高内存使用情况
     * 
     * @param info 内存信息
     * @param usageRatio 使用率
     */
    private void handleHighMemoryUsage(MemoryInfo info, double usageRatio) {
        String message = String.format(
                "内存使用率过高: %.1f%% (已用: %s, 最大: %s)",
                usageRatio * 100,
                formatBytes(info.getHeapUsed()),
                formatBytes(info.getHeapMax())
        );
        
        log.warn(message);
        
        if (listener != null) {
            listener.onHighMemoryUsage(info, usageRatio);
        }
    }
    
    /**
     * 格式化字节数
     * 
     * @param bytes 字节数
     * @return 格式化字符串
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double value = bytes;
        
        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", value, units[unitIndex]);
    }
    
    /**
     * 内存信息类
     */
    @Data
    public static class MemoryInfo {
        /**
         * 堆内存已使用
         */
        private long heapUsed;
        
        /**
         * 堆内存最大值
         */
        private long heapMax;
        
        /**
         * 堆内存已提交
         */
        private long heapCommitted;
        
        /**
         * 非堆内存已使用
         */
        private long nonHeapUsed;
        
        /**
         * 非堆内存最大值
         */
        private long nonHeapMax;
        
        /**
         * 非堆内存已提交
         */
        private long nonHeapCommitted;
        
        /**
         * 时间戳
         */
        private long timestamp;
        
        /**
         * 获取堆内存使用率
         * 
         * @return 使用率（0.0-1.0）
         */
        public double getHeapUsageRatio() {
            return heapMax > 0 ? (double) heapUsed / heapMax : 0.0;
        }
        
        /**
         * 获取非堆内存使用率
         * 
         * @return 使用率（0.0-1.0）
         */
        public double getNonHeapUsageRatio() {
            return nonHeapMax > 0 ? (double) nonHeapUsed / nonHeapMax : 0.0;
        }
    }
    
    /**
     * 内存统计信息类
     */
    @Data
    public static class MemoryStatistics {
        /**
         * 最大堆内存使用量
         */
        private volatile long maxHeapUsed = 0;
        
        /**
         * 最大非堆内存使用量
         */
        private volatile long maxNonHeapUsed = 0;
        
        /**
         * 最大堆内存使用率
         */
        private volatile double maxHeapUsageRatio = 0.0;
        
        /**
         * 采样次数
         */
        private volatile long sampleCount = 0;
        
        /**
         * 高使用率次数
         */
        private volatile long highUsageCount = 0;
        
        public void updateMaxHeapUsed(long heapUsed) {
            if (heapUsed > this.maxHeapUsed) {
                this.maxHeapUsed = heapUsed;
            }
        }
        
        public void updateMaxNonHeapUsed(long nonHeapUsed) {
            if (nonHeapUsed > this.maxNonHeapUsed) {
                this.maxNonHeapUsed = nonHeapUsed;
            }
        }
        
        public void updateMaxHeapUsageRatio(double ratio) {
            if (ratio > this.maxHeapUsageRatio) {
                this.maxHeapUsageRatio = ratio;
            }
        }
        
        public void incrementSampleCount() {
            this.sampleCount++;
        }
        
        public void incrementHighUsageCount() {
            this.highUsageCount++;
        }
        
        /**
         * 获取高使用率比例
         * 
         * @return 高使用率比例
         */
        public double getHighUsageRatio() {
            return sampleCount > 0 ? (double) highUsageCount / sampleCount : 0.0;
        }
    }
    
    /**
     * 内存监控监听器接口
     */
    public interface MemoryMonitorListener {
        /**
         * 内存信息更新
         * 
         * @param info 内存信息
         */
        void onMemoryUpdate(MemoryInfo info);
        
        /**
         * 高内存使用警告
         * 
         * @param info 内存信息
         * @param usageRatio 使用率
         */
        void onHighMemoryUsage(MemoryInfo info, double usageRatio);
    }
    
    /**
     * 监控配置类
     */
    @Data
    public static class MonitorConfig {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;
        
        /**
         * 监控间隔（毫秒）
         */
        private long monitorInterval = 30000; // 30秒
        
        /**
         * 警告阈值（0.0-1.0）
         */
        private double warningThreshold = 0.8; // 80%
        
        /**
         * 是否启用自动GC
         */
        private boolean autoGcEnabled = false;
        
        /**
         * 自动GC阈值（0.0-1.0）
         */
        private double autoGcThreshold = 0.9; // 90%
        
        /**
         * 创建默认配置
         * 
         * @return 默认配置
         */
        public static MonitorConfig defaultConfig() {
            return new MonitorConfig();
        }
    }
}