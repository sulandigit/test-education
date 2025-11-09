package com.roncoo.generator.performance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * 性能监控器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class PerformanceMonitor {
    
    /**
     * 性能统计数据
     */
    private final Map<String, PerformanceMetric> metrics = new ConcurrentHashMap<>();
    
    /**
     * 是否启用监控
     */
    private volatile boolean enabled = true;
    
    /**
     * 单例实例
     */
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    /**
     * 获取单例实例
     * 
     * @return 单例实例
     */
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private PerformanceMonitor() {
    }
    
    /**
     * 开始计时
     * 
     * @param name 操作名称
     * @return 计时器
     */
    public Timer startTimer(String name) {
        return new Timer(name);
    }
    
    /**
     * 记录操作时间
     * 
     * @param name 操作名称
     * @param duration 持续时间（毫秒）
     */
    public void recordTime(String name, long duration) {
        if (!enabled) {
            return;
        }
        
        PerformanceMetric metric = metrics.computeIfAbsent(name, k -> new PerformanceMetric(k));
        metric.addSample(duration);
        
        log.debug("性能记录: {} = {}ms", name, duration);
    }
    
    /**
     * 增加计数器
     * 
     * @param name 计数器名称
     */
    public void incrementCounter(String name) {
        incrementCounter(name, 1);
    }
    
    /**
     * 增加计数器
     * 
     * @param name 计数器名称
     * @param delta 增量
     */
    public void incrementCounter(String name, long delta) {
        if (!enabled) {
            return;
        }
        
        PerformanceMetric metric = metrics.computeIfAbsent(name, k -> new PerformanceMetric(k));
        metric.incrementCounter(delta);
    }
    
    /**
     * 记录错误
     * 
     * @param name 操作名称
     */
    public void recordError(String name) {
        if (!enabled) {
            return;
        }
        
        PerformanceMetric metric = metrics.computeIfAbsent(name, k -> new PerformanceMetric(k));
        metric.incrementErrors();
        
        log.debug("错误记录: {}", name);
    }
    
    /**
     * 获取性能指标
     * 
     * @param name 指标名称
     * @return 性能指标
     */
    public PerformanceMetric getMetric(String name) {
        return metrics.get(name);
    }
    
    /**
     * 获取所有性能指标
     * 
     * @return 所有指标
     */
    public Map<String, PerformanceMetric> getAllMetrics() {
        return new ConcurrentHashMap<>(metrics);
    }
    
    /**
     * 重置所有指标
     */
    public void reset() {
        metrics.clear();
        log.info("性能监控数据已重置");
    }
    
    /**
     * 重置指定指标
     * 
     * @param name 指标名称
     */
    public void reset(String name) {
        PerformanceMetric metric = metrics.get(name);
        if (metric != null) {
            metric.reset();
            log.debug("性能指标 {} 已重置", name);
        }
    }
    
    /**
     * 启用监控
     */
    public void enable() {
        this.enabled = true;
        log.info("性能监控已启用");
    }
    
    /**
     * 禁用监控
     */
    public void disable() {
        this.enabled = false;
        log.info("性能监控已禁用");
    }
    
    /**
     * 获取性能报告
     * 
     * @return 性能报告
     */
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 性能监控报告 ===\n");
        
        if (metrics.isEmpty()) {
            report.append("暂无性能数据\n");
            return report.toString();
        }
        
        // 按名称排序
        metrics.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String name = entry.getKey();
                    PerformanceMetric metric = entry.getValue();
                    report.append(String.format("%-30s: %s\n", name, metric.getSummary()));
                });
        
        return report.toString();
    }
    
    /**
     * 计时器类
     */
    public class Timer implements AutoCloseable {
        private final String name;
        private final long startTime;
        
        public Timer(String name) {
            this.name = name;
            this.startTime = System.currentTimeMillis();
        }
        
        /**
         * 停止计时并记录
         * 
         * @return 持续时间（毫秒）
         */
        public long stop() {
            long duration = System.currentTimeMillis() - startTime;
            recordTime(name, duration);
            return duration;
        }
        
        @Override
        public void close() {
            stop();
        }
    }
    
    /**
     * 性能指标类
     */
    @Data
    public static class PerformanceMetric {
        /**
         * 指标名称
         */
        private final String name;
        
        /**
         * 调用次数
         */
        private final AtomicLong count = new AtomicLong(0);
        
        /**
         * 总时间
         */
        private final AtomicLong totalTime = new AtomicLong(0);
        
        /**
         * 最小时间
         */
        private volatile long minTime = Long.MAX_VALUE;
        
        /**
         * 最大时间
         */
        private volatile long maxTime = Long.MIN_VALUE;
        
        /**
         * 错误次数
         */
        private final AtomicLong errors = new AtomicLong(0);
        
        /**
         * 计数器值
         */
        private final AtomicLong counterValue = new AtomicLong(0);
        
        public PerformanceMetric(String name) {
            this.name = name;
        }
        
        /**
         * 添加时间样本
         * 
         * @param time 时间（毫秒）
         */
        public synchronized void addSample(long time) {
            count.incrementAndGet();
            totalTime.addAndGet(time);
            
            if (time < minTime) {
                minTime = time;
            }
            if (time > maxTime) {
                maxTime = time;
            }
        }
        
        /**
         * 增加计数器
         * 
         * @param delta 增量
         */
        public void incrementCounter(long delta) {
            counterValue.addAndGet(delta);
        }
        
        /**
         * 增加错误计数
         */
        public void incrementErrors() {
            errors.incrementAndGet();
        }
        
        /**
         * 获取平均时间
         * 
         * @return 平均时间（毫秒）
         */
        public double getAverageTime() {
            long c = count.get();
            return c > 0 ? (double) totalTime.get() / c : 0.0;
        }
        
        /**
         * 获取错误率
         * 
         * @return 错误率（0.0-1.0）
         */
        public double getErrorRate() {
            long c = count.get();
            return c > 0 ? (double) errors.get() / c : 0.0;
        }
        
        /**
         * 获取每秒调用次数（假设统计时间为1分钟）
         * 
         * @return TPS
         */
        public double getTps() {
            // 简化计算，假设统计时间窗口为1分钟
            return count.get() / 60.0;
        }
        
        /**
         * 重置指标
         */
        public synchronized void reset() {
            count.set(0);
            totalTime.set(0);
            minTime = Long.MAX_VALUE;
            maxTime = Long.MIN_VALUE;
            errors.set(0);
            counterValue.set(0);
        }
        
        /**
         * 获取指标摘要
         * 
         * @return 指标摘要
         */
        public String getSummary() {
            long c = count.get();
            if (c == 0) {
                return String.format("count=0, counter=%d", counterValue.get());
            }
            
            return String.format(
                    "count=%d, avg=%.1fms, min=%dms, max=%dms, errors=%d(%.1f%%), counter=%d",
                    c, getAverageTime(), 
                    minTime == Long.MAX_VALUE ? 0 : minTime,
                    maxTime == Long.MIN_VALUE ? 0 : maxTime,
                    errors.get(), getErrorRate() * 100,
                    counterValue.get()
            );
        }
    }
}