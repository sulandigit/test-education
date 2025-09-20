package com.roncoo.education.dashboard.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 性能优化配置
 *
 * @author wujing
 * @date 2025-09-20
 */
@Configuration
@EnableCaching
@EnableAsync
public class PerformanceConfig {

    /**
     * 异步任务线程池配置
     */
    @Bean(name = "dashboardTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(4);
        
        // 最大线程数
        executor.setMaxPoolSize(8);
        
        // 队列容量
        executor.setQueueCapacity(200);
        
        // 线程名前缀
        executor.setThreadNamePrefix("Dashboard-Task-");
        
        // 线程存活时间
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：调用者运行策略
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }

    /**
     * WebSocket消息处理线程池
     */
    @Bean(name = "websocketTaskExecutor")
    public Executor websocketTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // WebSocket消息处理需要更多线程
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("WebSocket-Task-");
        executor.setKeepAliveSeconds(30);
        
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }

}