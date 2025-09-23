package com.roncoo.generator.cache;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存统计信息
 * 
 * @author roncoo-generator
 */
@Data
public class CacheStatistics {
    
    /**
     * 命中次数
     */
    private final AtomicLong hits = new AtomicLong(0);
    
    /**
     * 未命中次数
     */
    private final AtomicLong misses = new AtomicLong(0);
    
    /**
     * 存储次数
     */
    private final AtomicLong puts = new AtomicLong(0);
    
    /**
     * 驱逐次数
     */
    private final AtomicLong evictions = new AtomicLong(0);
    
    /**
     * 增加命中次数
     */
    public void incrementHits() {
        hits.incrementAndGet();
    }
    
    /**
     * 增加未命中次数
     */
    public void incrementMisses() {
        misses.incrementAndGet();
    }
    
    /**
     * 增加存储次数
     */
    public void incrementPuts() {
        puts.incrementAndGet();
    }
    
    /**
     * 增加驱逐次数
     */
    public void incrementEvictions() {
        evictions.incrementAndGet();
    }
    
    /**
     * 增加驱逐次数
     * 
     * @param count 驱逐数量
     */
    public void addEvictions(long count) {
        evictions.addAndGet(count);
    }
    
    /**
     * 获取命中次数
     * 
     * @return 命中次数
     */
    public long getHits() {
        return hits.get();
    }
    
    /**
     * 获取未命中次数
     * 
     * @return 未命中次数
     */
    public long getMisses() {
        return misses.get();
    }
    
    /**
     * 获取存储次数
     * 
     * @return 存储次数
     */
    public long getPuts() {
        return puts.get();
    }
    
    /**
     * 获取驱逐次数
     * 
     * @return 驱逐次数
     */
    public long getEvictions() {
        return evictions.get();
    }
    
    /**
     * 获取总请求次数
     * 
     * @return 总请求次数
     */
    public long getTotalRequests() {
        return hits.get() + misses.get();
    }
    
    /**
     * 获取命中率
     * 
     * @return 命中率（0.0-1.0）
     */
    public double getHitRate() {
        long totalRequests = getTotalRequests();
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) hits.get() / totalRequests;
    }
    
    /**
     * 获取未命中率
     * 
     * @return 未命中率（0.0-1.0）
     */
    public double getMissRate() {
        return 1.0 - getHitRate();
    }
    
    /**
     * 重置统计信息
     */
    public void reset() {
        hits.set(0);
        misses.set(0);
        puts.set(0);
        evictions.set(0);
    }
    
    /**
     * 复制统计信息
     * 
     * @return 统计信息副本
     */
    public CacheStatistics copy() {
        CacheStatistics copy = new CacheStatistics();
        copy.hits.set(this.hits.get());
        copy.misses.set(this.misses.get());
        copy.puts.set(this.puts.get());
        copy.evictions.set(this.evictions.get());
        return copy;
    }
    
    /**
     * 获取统计摘要
     * 
     * @return 统计摘要
     */
    public String getSummary() {
        return String.format(
                "CacheStatistics{hits=%d, misses=%d, puts=%d, evictions=%d, hitRate=%.2f%%}",
                getHits(), getMisses(), getPuts(), getEvictions(), getHitRate() * 100
        );
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
}