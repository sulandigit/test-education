package com.roncoo.education.dashboard.service.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统数据聚合结果
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
public class SystemDataAggregation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 在线服务数
     */
    private Integer onlineServices;

    /**
     * 总服务数
     */
    private Integer totalServices;

    /**
     * 今日异常数
     */
    private Integer todayErrors;

    /**
     * 系统负载
     */
    private Double systemLoad;

    /**
     * 数据库连接数
     */
    private Integer dbConnections;

    /**
     * 系统性能指标
     */
    private SystemPerformanceData performance;

    /**
     * 服务健康状态
     */
    private ServiceHealthData serviceHealth;

    /**
     * 系统性能数据
     */
    @Data
    @Accessors(chain = true)
    public static class SystemPerformanceData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Double cpuUsage;
        private Double memoryUsage;
        private Double diskUsage;
        private Double networkUsage;
        private Integer activeConnections;
    }

    /**
     * 服务健康数据
     */
    @Data
    @Accessors(chain = true)
    public static class ServiceHealthData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Integer healthyServices;
        private Integer unhealthyServices;
        private Integer unknownServices;
        private Double healthRate;
    }

}