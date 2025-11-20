package com.roncoo.education.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存管理控制器
 * 提供缓存管理的REST API接口
 *
 * @author wujing
 */
@Slf4j
@RestController
@RequestMapping("/system/cache")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.cache.multi-level", name = "management-enabled", havingValue = "true", matchIfMissing = false)
public class CacheManagementController {

    private final CacheManager cacheManager;

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称列表
     */
    @GetMapping("/names")
    public Map<String, Object> getCacheNames() {
        Map<String, Object> result = new HashMap<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        
        result.put("cacheNames", cacheNames);
        result.put("count", cacheNames.size());
        result.put("timestamp", new Date());
        
        return result;
    }

    /**
     * 获取所有缓存统计信息
     *
     * @return 缓存统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getAllCacheStats() {
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
        }
        
        result.put("timestamp", new Date());
        return result;
    }

    /**
     * 获取指定缓存的统计信息
     *
     * @param cacheName 缓存名称
     * @return 缓存统计信息
     */
    @GetMapping("/stats/{cacheName}")
    public Map<String, Object> getCacheStats(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheManager instanceof MultiLevelCacheManager) {
            MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
            CacheStats stats = multiLevelCacheManager.getCacheStats(cacheName);
            
            if (stats != null) {
                result.put("cacheName", cacheName);
                result.put("stats", stats);
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "Cache not found: " + cacheName);
            }
        }
        
        result.put("timestamp", new Date());
        return result;
    }

    /**
     * 清空指定缓存
     *
     * @param cacheName 缓存名称
     * @return 操作结果
     */
    @DeleteMapping("/clear/{cacheName}")
    public Map<String, Object> clearCache(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (cacheManager instanceof MultiLevelCacheManager) {
                MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
                multiLevelCacheManager.clearCache(cacheName);
                
                result.put("success", true);
                result.put("message", "Cache cleared successfully: " + cacheName);
                
                log.info("通过REST API清空缓存: {}", cacheName);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to clear cache: " + e.getMessage());
            
            log.error("通过REST API清空缓存失败: {}", cacheName, e);
        }
        
        result.put("timestamp", new Date());
        return result;
    }

    /**
     * 清空所有缓存
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear-all")
    public Map<String, Object> clearAllCaches() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (cacheManager instanceof MultiLevelCacheManager) {
                MultiLevelCacheManager multiLevelCacheManager = (MultiLevelCacheManager) cacheManager;
                multiLevelCacheManager.clearAll();
                
                result.put("success", true);
                result.put("message", "All caches cleared successfully");
                
                log.info("通过REST API清空所有缓存");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to clear all caches: " + e.getMessage());
            
            log.error("通过REST API清空所有缓存失败", e);
        }
        
        result.put("timestamp", new Date());
        return result;
    }

    /**
     * 预热指定缓存
     *
     * @param cacheName 缓存名称
     * @return 操作结果
     */
    @PostMapping("/warmup/{cacheName}")
    public Map<String, Object> warmupCache(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里可以根据具体业务需求实现缓存预热逻辑
            // 例如：预加载热点数据到缓存中
            
            result.put("success", true);
            result.put("message", "Cache warmup initiated for: " + cacheName);
            result.put("note", "Warmup implementation depends on specific business requirements");
            
            log.info("缓存预热请求: {}", cacheName);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to warmup cache: " + e.getMessage());
            
            log.error("缓存预热失败: {}", cacheName, e);
        }
        
        result.put("timestamp", new Date());
        return result;
    }
}