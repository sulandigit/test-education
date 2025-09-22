package com.roncoo.education.common.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine缓存配置类
 * 用于配置本地缓存的容量、过期时间等参数
 *
 * @author wujing
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.cache.caffeine")
public class CaffeineConfiguration {

    /**
     * 最大缓存容量，默认10000条记录
     */
    private long maximumSize = 10000L;

    /**
     * 写入后多久过期（分钟），默认30分钟
     */
    private long expireAfterWrite = 30L;

    /**
     * 访问后多久过期（分钟），默认60分钟
     */
    private long expireAfterAccess = 60L;

    /**
     * 初始化容量，默认100
     */
    private int initialCapacity = 100;

    /**
     * 是否启用统计，默认启用
     */
    private boolean recordStats = true;

    /**
     * 是否启用异步刷新，默认启用
     */
    private boolean refreshAfterWrite = true;

    /**
     * 异步刷新时间（分钟），默认10分钟
     */
    private long refreshAfterWriteMinutes = 10L;
}