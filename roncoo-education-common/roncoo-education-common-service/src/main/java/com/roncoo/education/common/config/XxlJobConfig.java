package com.roncoo.education.common.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-JOB 分布式任务调度配置类
 * 配置 XXL-JOB 执行器，用于接收和执行来自调度中心的任务
 * 
 * 主要配置项：
 * - 调度中心地址
 * - 访问令牌
 * - 执行器应用名称、地址、端口
 * - 日志路径和保存天数
 *
 * @author fengyw
 * @date 2022/1/1
 */
@Configuration
public class XxlJobConfig {
    /**
     * 日志记录器
     */
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    /**
     * XXL-JOB 调度中心地址，支持多个地址，用逗号分隔
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /**
     * 访问令牌，用于执行器和调度中心之间的身份验证
     */
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    /**
     * 执行器应用名称，唯一标识一个执行器集群
     */
    @Value("${xxl.job.executor.appname}")
    private String appname;

    /**
     * 执行器地址，可为空，系统会自动获取
     */
    @Value("${xxl.job.executor.address:}")
    private String address;

    /**
     * 执行器IP地址，可为空，系统会自动获取
     */
    @Value("${xxl.job.executor.ip:}")
    private String ip;

    /**
     * 执行器端口号，用于接收调度中心的任务调度请求
     */
    @Value("${xxl.job.executor.port}")
    private int port;

    /**
     * 执行器日志存储路径
     */
    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    /**
     * 执行器日志保存天数，超过此天数的日志会被自动清理
     */
    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    /**
     * 配置 XXL-JOB 执行器 Bean
     * 初始化并配置 XXL-JOB 执行器的各项参数
     * 
     * @return 配置好的 XXL-JOB 执行器实例
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        // 设置调度中心地址
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        // 设置执行器应用名
        xxlJobSpringExecutor.setAppname(appname);
        // 设置执行器地址
        xxlJobSpringExecutor.setAddress(address);
        // 设置执行器IP
        xxlJobSpringExecutor.setIp(ip);
        // 设置执行器端口
        xxlJobSpringExecutor.setPort(port);
        // 设置访问令牌
        xxlJobSpringExecutor.setAccessToken(accessToken);
        // 设置日志路径
        xxlJobSpringExecutor.setLogPath(logPath);
        // 设置日志保存天数
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }

}
