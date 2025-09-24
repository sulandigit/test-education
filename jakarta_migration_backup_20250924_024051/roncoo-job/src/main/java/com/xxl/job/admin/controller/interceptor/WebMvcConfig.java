package com.xxl.job.admin.controller.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web MVC Configuration - Spring MVC interceptor registration
 * 
 * This configuration class registers all custom interceptors with the Spring MVC
 * framework. It defines the order and scope of interceptor execution for the
 * web application, ensuring proper request processing flow.
 *
 * Registered interceptors:
 * - PermissionInterceptor: Handles authentication and authorization
 * - CookieInterceptor: Processes cookies and template utilities
 *
 * All interceptors are applied to all URL patterns (/**) to ensure
 * comprehensive request processing coverage.
 *
 * @author xuxueli 2018-04-02 20:48:20
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;

    /**
     * Register custom interceptors with Spring MVC
     * 
     * Configures the interceptor chain for request processing. The order of
     * registration determines the execution order - permission checking occurs
     * first, followed by cookie processing.
     *
     * @param registry the InterceptorRegistry to configure
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register permission interceptor for all paths (authentication/authorization)
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        // Register cookie interceptor for all paths (template data enhancement)
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
    }

}