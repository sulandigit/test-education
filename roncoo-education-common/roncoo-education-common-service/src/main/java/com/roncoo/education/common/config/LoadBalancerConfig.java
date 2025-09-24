package com.roncoo.education.common.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cloud LoadBalancer 配置类
 * 替代Ribbon，适配Spring Boot 3.x和Spring Cloud 2023.x
 * 
 * @author roncoo
 */
@Configuration
@LoadBalancerClients({
    @LoadBalancerClient(name = "service-system", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "service-user", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "service-course", configuration = LoadBalancerConfig.class)
})
public class LoadBalancerConfig {

    /**
     * 配置服务实例提供者
     * 使用缓存机制提升性能
     */
    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withCaching()
                .build(context);
    }
}