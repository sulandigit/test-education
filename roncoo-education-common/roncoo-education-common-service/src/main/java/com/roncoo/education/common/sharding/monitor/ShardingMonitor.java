package com.roncoo.education.common.sharding.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分库分表性能监控
 * 
 * @author monitor
 */
@Component
public class ShardingMonitor {

    private final MeterRegistry meterRegistry;
    
    // 数据库操作计数器
    private final Counter shardingQueryCounter;
    private final Counter shardingInsertCounter;
    private final Counter shardingUpdateCounter;
    private final Counter shardingDeleteCounter;
    
    // 跨库查询计数器
    private final Counter crossDatabaseQueryCounter;
    
    // 性能计时器
    private final Timer shardingQueryTimer;
    private final Timer shardingInsertTimer;
    private final Timer shardingUpdateTimer;
    private final Timer shardingDeleteTimer;

    @Autowired
    public ShardingMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 初始化计数器
        this.shardingQueryCounter = Counter.builder("sharding.query.count")
                .description("分库分表查询操作计数")
                .register(meterRegistry);
                
        this.shardingInsertCounter = Counter.builder("sharding.insert.count")
                .description("分库分表插入操作计数")
                .register(meterRegistry);
                
        this.shardingUpdateCounter = Counter.builder("sharding.update.count")
                .description("分库分表更新操作计数")
                .register(meterRegistry);
                
        this.shardingDeleteCounter = Counter.builder("sharding.delete.count")
                .description("分库分表删除操作计数")
                .register(meterRegistry);
                
        this.crossDatabaseQueryCounter = Counter.builder("sharding.cross.database.query.count")
                .description("跨库查询计数")
                .register(meterRegistry);
        
        // 初始化计时器
        this.shardingQueryTimer = Timer.builder("sharding.query.time")
                .description("分库分表查询执行时间")
                .register(meterRegistry);
                
        this.shardingInsertTimer = Timer.builder("sharding.insert.time")
                .description("分库分表插入执行时间")
                .register(meterRegistry);
                
        this.shardingUpdateTimer = Timer.builder("sharding.update.time")
                .description("分库分表更新执行时间")
                .register(meterRegistry);
                
        this.shardingDeleteTimer = Timer.builder("sharding.delete.time")
                .description("分库分表删除执行时间")
                .register(meterRegistry);
    }

    /**
     * 记录查询操作
     */
    public void recordQuery() {
        shardingQueryCounter.increment();
    }

    /**
     * 记录查询操作耗时
     */
    public void recordQueryTime(long duration, TimeUnit timeUnit) {
        shardingQueryTimer.record(duration, timeUnit);
    }

    /**
     * 记录插入操作
     */
    public void recordInsert() {
        shardingInsertCounter.increment();
    }

    /**
     * 记录插入操作耗时
     */
    public void recordInsertTime(long duration, TimeUnit timeUnit) {
        shardingInsertTimer.record(duration, timeUnit);
    }

    /**
     * 记录更新操作
     */
    public void recordUpdate() {
        shardingUpdateCounter.increment();
    }

    /**
     * 记录更新操作耗时
     */
    public void recordUpdateTime(long duration, TimeUnit timeUnit) {
        shardingUpdateTimer.record(duration, timeUnit);
    }

    /**
     * 记录删除操作
     */
    public void recordDelete() {
        shardingDeleteCounter.increment();
    }

    /**
     * 记录删除操作耗时
     */
    public void recordDeleteTime(long duration, TimeUnit timeUnit) {
        shardingDeleteTimer.record(duration, timeUnit);
    }

    /**
     * 记录跨库查询
     */
    public void recordCrossDatabaseQuery() {
        crossDatabaseQueryCounter.increment();
    }

    /**
     * 创建自定义计数器
     */
    public Counter createCounter(String name, String description, String... tags) {
        return Counter.builder(name)
                .description(description)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * 创建自定义计时器
     */
    public Timer createTimer(String name, String description, String... tags) {
        return Timer.builder(name)
                .description(description)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * 获取查询操作总数
     */
    public double getQueryCount() {
        return shardingQueryCounter.count();
    }

    /**
     * 获取插入操作总数
     */
    public double getInsertCount() {
        return shardingInsertCounter.count();
    }

    /**
     * 获取更新操作总数
     */
    public double getUpdateCount() {
        return shardingUpdateCounter.count();
    }

    /**
     * 获取删除操作总数
     */
    public double getDeleteCount() {
        return shardingDeleteCounter.count();
    }

    /**
     * 获取跨库查询总数
     */
    public double getCrossDatabaseQueryCount() {
        return crossDatabaseQueryCounter.count();
    }

    /**
     * 获取查询平均耗时
     */
    public double getQueryMeanTime() {
        return shardingQueryTimer.mean(TimeUnit.MILLISECONDS);
    }

    /**
     * 获取插入平均耗时
     */
    public double getInsertMeanTime() {
        return shardingInsertTimer.mean(TimeUnit.MILLISECONDS);
    }

    /**
     * 获取更新平均耗时
     */
    public double getUpdateMeanTime() {
        return shardingUpdateTimer.mean(TimeUnit.MILLISECONDS);
    }

    /**
     * 获取删除平均耗时
     */
    public double getDeleteMeanTime() {
        return shardingDeleteTimer.mean(TimeUnit.MILLISECONDS);
    }
}