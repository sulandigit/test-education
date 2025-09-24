package com.roncoo.education.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 配置类
 * 适配Spring Boot 3.x和Spring Cloud 2023.x
 * 
 * @author roncoo
 */
@Configuration
public class FeignConfig {

    /**
     * Feign日志级别配置
     * NONE: 不记录日志(默认)
     * BASIC: 仅记录请求方法和URL以及响应状态码和执行时间
     * HEADERS: 记录请求和响应的头信息
     * FULL: 记录请求和响应的头信息、正文和元数据
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}