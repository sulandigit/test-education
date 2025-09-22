package com.roncoo.education.common.service.monitoring;

import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 领课教育API监控拦截器
 * 自动记录API请求指标
 * 
 * @author 领课教育
 */
@Component
public class ApiMonitoringInterceptor implements HandlerInterceptor {

    @Autowired
    private BusinessMetricsCollector metricsCollector;

    private static final String TIMER_ATTRIBUTE = "apiTimer";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录API请求
        String endpoint = getEndpoint(request);
        String method = request.getMethod();
        
        metricsCollector.recordApiRequest(endpoint, method);
        
        // 开始计时
        Timer.Sample timer = metricsCollector.startApiTimer();
        request.setAttribute(TIMER_ATTRIBUTE, timer);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 结束计时
        Timer.Sample timer = (Timer.Sample) request.getAttribute(TIMER_ATTRIBUTE);
        if (timer != null) {
            String endpoint = getEndpoint(request);
            metricsCollector.recordApiResponse(timer, endpoint);
        }
        
        // 记录错误
        if (response.getStatus() >= 400) {
            String endpoint = getEndpoint(request);
            String errorCode = String.valueOf(response.getStatus());
            metricsCollector.recordApiError(endpoint, errorCode);
        }
    }

    /**
     * 获取标准化的端点名称
     */
    private String getEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // 移除路径参数，避免高基数
        uri = uri.replaceAll("/\\d+", "/{id}");
        uri = uri.replaceAll("/[a-f0-9-]{36}", "/{uuid}");
        
        // 限制URI长度
        if (uri.length() > 100) {
            uri = uri.substring(0, 100) + "...";
        }
        
        return uri;
    }
}