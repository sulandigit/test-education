package com.roncoo.education.dashboard.service.cache;

import com.roncoo.education.dashboard.dao.entity.DashboardMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 缓存管理服务接口
 *
 * @author wujing
 * @date 2025-09-20
 */
public interface CacheManagerService {

    /**
     * 缓存实时指标数据
     *
     * @param metricType 指标类型
     * @param value 指标值
     * @param expireSeconds 过期时间（秒）
     */
    void cacheMetric(String metricType, Object value, int expireSeconds);

    /**
     * 获取缓存的指标数据
     *
     * @param metricType 指标类型
     * @param clazz 数据类型
     * @return 指标值
     */
    <T> T getCachedMetric(String metricType, Class<T> clazz);

    /**
     * 批量缓存指标数据
     *
     * @param metrics 指标数据
     */
    void cacheMetrics(Map<String, Object> metrics);

    /**
     * 删除缓存的指标数据
     *
     * @param metricType 指标类型
     */
    void deleteCachedMetric(String metricType);

    /**
     * 检查缓存是否存在
     *
     * @param metricType 指标类型
     * @return 是否存在
     */
    boolean existsCachedMetric(String metricType);

    /**
     * 缓存热点数据
     *
     * @param key 缓存键
     * @param data 数据
     * @param expireSeconds 过期时间
     */
    void cacheHotData(String key, Object data, int expireSeconds);

    /**
     * 获取热点数据
     *
     * @param key 缓存键
     * @param clazz 数据类型
     * @return 数据
     */
    <T> T getHotData(String key, Class<T> clazz);

    /**
     * 缓存列表数据
     *
     * @param key 缓存键
     * @param list 列表数据
     * @param expireSeconds 过期时间
     */
    void cacheList(String key, List<?> list, int expireSeconds);

    /**
     * 获取列表数据
     *
     * @param key 缓存键
     * @param clazz 元素类型
     * @return 列表数据
     */
    <T> List<T> getList(String key, Class<T> clazz);

    /**
     * 设置缓存过期时间
     *
     * @param key 缓存键
     * @param expireSeconds 过期时间（秒）
     */
    void setExpire(String key, int expireSeconds);

    /**
     * 清理过期的缓存数据
     */
    void cleanExpiredCache();

    /**
     * 获取缓存使用统计
     *
     * @return 缓存统计信息
     */
    Map<String, Object> getCacheStats();

}