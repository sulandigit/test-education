package com.roncoo.education.common.service.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 领课教育监控配置类
 * 配置 Micrometer 和 Prometheus 监控
 * 
 * @author 领课教育
 */
@Configuration
public class MonitoringConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * 自定义 MeterRegistry 配置
     * 添加通用标签和过滤器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            // 添加通用标签
            registry.config()
                    .commonTags("application", applicationName)
                    .commonTags("environment", activeProfile)
                    .commonTags("version", "14.0.0-RELEASE");

            // 添加指标过滤器
            registry.config()
                    // 过滤掉不需要的JVM指标
                    .meterFilter(MeterFilter.deny(id -> {
                        String name = id.getName();
                        return name.startsWith("jvm.gc.memory") && 
                               (name.contains("allocated") || name.contains("promoted"));
                    }))
                    // 重命名指标，添加教育系统前缀
                    .meterFilter(MeterFilter.renameTag("uri", "endpoint"))
                    .meterFilter(MeterFilter.renameTag("exception", "error_type"))
                    // 限制URI标签的值，避免高基数问题
                    .meterFilter(MeterFilter.maximumAllowableTags("http.server.requests", "uri", 100, 
                                MeterFilter.deny()));
        };
    }

    /**
     * 业务指标收集器Bean
     */
    @Bean
    public BusinessMetricsCollector businessMetricsCollector() {
        return new BusinessMetricsCollector();
    }
}