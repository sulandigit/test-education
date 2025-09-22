package com.roncoo.education.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 多级缓存自动配置
 * 
 * @author wujing
 */
@Slf4j
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.cache.multi-level", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MultiLevelCacheAutoConfiguration {

    /**
     * 配置多级缓存管理器为主要的缓存管理器
     *
     * @param caffeineConfiguration Caffeine配置
     * @param redisConnectionFactory Redis连接工厂
     * @return 多级缓存管理器
     */
    @Bean
    @Primary
    public CacheManager multiLevelCacheManager(CaffeineConfiguration caffeineConfiguration,
                                               RedisConnectionFactory redisConnectionFactory) {
        log.info("初始化多级缓存管理器 - Caffeine配置: {}", caffeineConfiguration);
        return new MultiLevelCacheManager(caffeineConfiguration, redisConnectionFactory);
    }
}