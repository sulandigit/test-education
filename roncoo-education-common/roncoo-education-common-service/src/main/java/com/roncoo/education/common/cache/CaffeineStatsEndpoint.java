package com.roncoo.education.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Caffeine缓存监控端点
 * 提供缓存统计信息查询和管理功能
 *
 * @author wujing
 */
@Slf4j
@Component
@Endpoint(id = "caffeine-stats")
@RequiredArgsConstructor
public class CaffeineStatsEndpoint {

    private final CacheManager cacheManager;

    /**
     * 获取所有缓存的统计信息
     *
     * @return 缓存统计信息
     */
    @ReadOperation
    public Map<String, Object> stats() {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheManager instanceof MultiLevelCacheManager) {
            MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
            Map<String, CacheStats> cacheStatsMap = new HashMap<>();
            
            for (String cacheName : cacheManager.getCacheNames()) {
                CacheStats stats = multiLevelCacheManager.getCacheStats(cacheName);
                if (stats != null) {
                    cacheStatsMap.put(cacheName, stats);
                }
            }
            
            result.put("cacheStats", cacheStatsMap);
            result.put("cacheCount", cacheStatsMap.size());
            result.put("timestamp", new Date());
        }
        
        return result;
    }

    /**
     * 获取指定缓存的统计信息
     *
     * @param cacheName 缓存名称
     * @return 缓存统计信息
     */
    @ReadOperation
    public Map<String, Object> statsFor(@Selector String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheManager instanceof MultiLevelCacheManager) {
            MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
            CacheStats stats = multiLevelCacheManager.getCacheStats(cacheName);
            
            if (stats != null) {
                result.put("cacheName", cacheName);
                result.put("stats", stats);
                result.put("timestamp", new Date());
            } else {
                result.put("error", "Cache not found: " + cacheName);
            }
        }
        
        return result;
    }

    /**
     * 清空指定缓存
     *
     * @param cacheName 缓存名称
     * @return 操作结果
     */
    @DeleteOperation
    public Map<String, Object> clearCache(@Selector String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (cacheManager instanceof MultiLevelCacheManager) {
                MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
                multiLevelCacheManager.clearCache(cacheName);
                
                result.put("success", true);
                result.put("message", "Cache cleared successfully: " + cacheName);
                result.put("timestamp", new Date());
                
                log.info("手动清空缓存: {}", cacheName);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to clear cache: " + e.getMessage());
            result.put("timestamp", new Date());
            
            log.error("清空缓存失败: {}", cacheName, e);
        }
        
        return result;
    }

    /**
     * 清空所有缓存
     *
     * @return 操作结果
     */
    @DeleteOperation
    public Map<String, Object> clearAll() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (cacheManager instanceof MultiLevelCacheManager) {
                MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
                multiLevelCacheManager.clearAll();
                
                result.put("success", true);
                result.put("message", "All caches cleared successfully");
                result.put("timestamp", new Date());
                
                log.info("手动清空所有缓存");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to clear all caches: " + e.getMessage());
            result.put("timestamp", new Date());
            
            log.error("清空所有缓存失败", e);
        }
        
        return result;
    }
}