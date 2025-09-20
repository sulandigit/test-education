package com.roncoo.education.dashboard.service.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roncoo.education.dashboard.service.cache.CacheManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存管理服务实现
 *
 * @author wujing
 * @date 2025-09-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheManagerServiceImpl implements CacheManagerService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "dashboard:";
    private static final String METRIC_PREFIX = CACHE_PREFIX + "metric:";
    private static final String HOT_DATA_PREFIX = CACHE_PREFIX + "hot:";
    private static final String LIST_PREFIX = CACHE_PREFIX + "list:";

    @Override
    public void cacheMetric(String metricType, Object value, int expireSeconds) {
        String key = METRIC_PREFIX + metricType;
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("缓存指标数据: {} = {}, 过期时间: {}秒", metricType, value, expireSeconds);
        } catch (Exception e) {
            log.error("缓存指标数据失败: {}", metricType, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCachedMetric(String metricType, Class<T> clazz) {
        String key = METRIC_PREFIX + metricType;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            
            if (clazz.isInstance(value)) {
                return (T) value;
            }
            
            // 如果是字符串，尝试JSON转换
            if (value instanceof String) {
                return objectMapper.readValue((String) value, clazz);
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取缓存指标数据失败: {}", metricType, e);
            return null;
        }
    }

    @Override
    public void cacheMetrics(Map<String, Object> metrics) {
        try {
            Map<String, Object> cacheMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : metrics.entrySet()) {
                String key = METRIC_PREFIX + entry.getKey();
                cacheMap.put(key, entry.getValue());
            }
            redisTemplate.opsForValue().multiSet(cacheMap);
            log.debug("批量缓存指标数据: {}个", metrics.size());
        } catch (Exception e) {
            log.error("批量缓存指标数据失败", e);
        }
    }

    @Override
    public void deleteCachedMetric(String metricType) {
        String key = METRIC_PREFIX + metricType;
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存指标数据: {}", metricType);
        } catch (Exception e) {
            log.error("删除缓存指标数据失败: {}", metricType, e);
        }
    }

    @Override
    public boolean existsCachedMetric(String metricType) {
        String key = METRIC_PREFIX + metricType;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查缓存指标数据失败: {}", metricType, e);
            return false;
        }
    }

    @Override
    public void cacheHotData(String key, Object data, int expireSeconds) {
        String cacheKey = HOT_DATA_PREFIX + key;
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(cacheKey, jsonData, expireSeconds, TimeUnit.SECONDS);
            log.debug("缓存热点数据: {}, 过期时间: {}秒", key, expireSeconds);
        } catch (Exception e) {
            log.error("缓存热点数据失败: {}", key, e);
        }
    }

    @Override
    public <T> T getHotData(String key, Class<T> clazz) {
        String cacheKey = HOT_DATA_PREFIX + key;
        try {
            String jsonData = (String) redisTemplate.opsForValue().get(cacheKey);
            if (jsonData == null) {
                return null;
            }
            return objectMapper.readValue(jsonData, clazz);
        } catch (Exception e) {
            log.error("获取热点数据失败: {}", key, e);
            return null;
        }
    }

    @Override
    public void cacheList(String key, List<?> list, int expireSeconds) {
        String cacheKey = LIST_PREFIX + key;
        try {
            String jsonData = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(cacheKey, jsonData, expireSeconds, TimeUnit.SECONDS);
            log.debug("缓存列表数据: {}, 大小: {}, 过期时间: {}秒", key, list.size(), expireSeconds);
        } catch (Exception e) {
            log.error("缓存列表数据失败: {}", key, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        String cacheKey = LIST_PREFIX + key;
        try {
            String jsonData = (String) redisTemplate.opsForValue().get(cacheKey);
            if (jsonData == null) {
                return null;
            }
            
            return objectMapper.readValue(jsonData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.error("获取列表数据失败: {}", key, e);
            return null;
        }
    }

    @Override
    public void setExpire(String key, int expireSeconds) {
        try {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            log.debug("设置缓存过期时间: {}, {}秒", key, expireSeconds);
        } catch (Exception e) {
            log.error("设置缓存过期时间失败: {}", key, e);
        }
    }

    @Override
    public void cleanExpiredCache() {
        try {
            // 获取所有dashboard相关的键
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                // 清理已过期的键（Redis会自动清理，这里主要是日志记录）
                long expiredCount = keys.stream()
                        .filter(key -> Boolean.FALSE.equals(redisTemplate.hasKey(key)))
                        .count();
                
                log.debug("清理过期缓存完成，过期键数量: {}", expiredCount);
            }
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
        }
    }

    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 统计各类型缓存的数量
            Set<String> metricKeys = redisTemplate.keys(METRIC_PREFIX + "*");
            Set<String> hotDataKeys = redisTemplate.keys(HOT_DATA_PREFIX + "*");
            Set<String> listKeys = redisTemplate.keys(LIST_PREFIX + "*");
            
            stats.put("metricCacheCount", metricKeys != null ? metricKeys.size() : 0);
            stats.put("hotDataCacheCount", hotDataKeys != null ? hotDataKeys.size() : 0);
            stats.put("listCacheCount", listKeys != null ? listKeys.size() : 0);
            stats.put("totalCacheCount", 
                    (metricKeys != null ? metricKeys.size() : 0) +
                    (hotDataKeys != null ? hotDataKeys.size() : 0) +
                    (listKeys != null ? listKeys.size() : 0));
            
            log.debug("缓存统计: {}", stats);
        } catch (Exception e) {
            log.error("获取缓存统计失败", e);
            stats.put("error", e.getMessage());
        }
        return stats;
    }

}