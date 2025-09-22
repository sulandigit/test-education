package com.roncoo.education.common.config;

import com.roncoo.education.common.core.tools.Constants;
import com.roncoo.education.common.upload.impl.LocalUploadImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web MVC 配置类
 * 配置 Web 应用的相关设置，包括静态资源处理和请求拦截器
 * 
 * 主要功能：
 * 1. 配置本地文件上传的静态资源访问路径
 * 2. 配置请求拦截器，从请求头中获取用户ID并存储到线程上下文中
 * 
 * @author LYQ
 * @date 2022/1/1
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器
     * 为本地文件上传的图片和文档设置 URL 访问路径
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片和文档的访问路径映射
        registry.addResourceHandler(LocalUploadImpl.PATH_IMAGES + "/**").addResourceLocations("file:" + LocalUploadImpl.LOCALPATH_IMAGES, LocalUploadImpl.PATH_DOCS + "/**").addResourceLocations("file:" + LocalUploadImpl.LOCALPATH_DOCS);
    }

    /**
     * 添加请求拦截器
     * 注册用户信息拦截器，用于处理请求中的用户ID
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorConfig());
    }

    /**
     * 请求拦截器配置类
     * 在请求处理前后进行用户信息的设置和清理
     */
    static class InterceptorConfig implements AsyncHandlerInterceptor {
        
        /**
         * 请求处理前的预处理
         * 从请求头中获取用户ID并存储到线程上下文中
         * 
         * @param request  HTTP 请求对象
         * @param response HTTP 响应对象
         * @param handler  处理器对象
         * @return true 继续处理，false 中断处理
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // 从请求头中获取用户ID并设置到线程上下文
            ThreadContext.setUserId(request.getHeader(Constants.USER_ID));
            return true;
        }

        /**
         * 请求处理完成后的清理工作
         * 清除线程上下文中的用户ID，防止内存泄漏
         * 
         * @param request  HTTP 请求对象
         * @param response HTTP 响应对象
         * @param handler  处理器对象
         * @param ex       异常对象（如果有）
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            // 清除线程上下文中的用户ID
            ThreadContext.removeUserId();
        }
    }
}


