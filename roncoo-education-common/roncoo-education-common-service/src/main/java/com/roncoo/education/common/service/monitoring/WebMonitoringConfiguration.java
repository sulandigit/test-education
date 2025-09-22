package com.roncoo.education.common.service.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 领课教育Web监控配置
 * 注册API监控拦截器
 * 
 * @author 领课教育
 */
@Configuration
public class WebMonitoringConfiguration implements WebMvcConfigurer {

    @Autowired
    private ApiMonitoringInterceptor apiMonitoringInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiMonitoringInterceptor)
                .addPathPatterns("/**")
                // 排除监控端点和静态资源
                .excludePathPatterns(
                        "/actuator/**",
                        "/error",
                        "/favicon.ico",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}