package com.roncoo.education.dashboard.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 大屏统计指标实体
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "大屏统计指标")
public class DashboardMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指标ID")
    private String metricId;

    @ApiModelProperty(value = "指标类型")
    private String metricType;

    @ApiModelProperty(value = "指标名称")
    private String metricName;

    @ApiModelProperty(value = "指标值")
    private BigDecimal metricValue;

    @ApiModelProperty(value = "时间维度")
    private String timeDimension;

    @ApiModelProperty(value = "统计时间")
    private LocalDateTime statTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 指标类型常量
     */
    public static class MetricType {
        
        public static final String ONLINE_USERS = "online_users";
        public static final String DAILY_NEW_USERS = "daily_new_users";
        public static final String TOTAL_USERS = "total_users";
        public static final String DAILY_ORDERS = "daily_orders";
        public static final String DAILY_REVENUE = "daily_revenue";
        public static final String MONTHLY_REVENUE = "monthly_revenue";
        public static final String COURSE_VIEWS = "course_views";
        public static final String HOT_COURSES = "hot_courses";
        public static final String USER_LOCATIONS = "user_locations";
        public static final String PAYMENT_TYPES = "payment_types";
        public static final String SYSTEM_STATUS = "system_status";
    }

    /**
     * 时间维度常量
     */
    public static class TimeDimension {
        
        public static final String REAL_TIME = "real_time";
        public static final String HOURLY = "hourly";
        public static final String DAILY = "daily";
        public static final String WEEKLY = "weekly";
        public static final String MONTHLY = "monthly";
    }

}