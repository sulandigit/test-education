package com.roncoo.education.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 大屏数据服务启动类
 *
 * @author wujing
 * @date 2025-09-20
 */
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.roncoo.education"})
@SpringBootApplication(scanBasePackages = {"com.roncoo.education"})
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

}