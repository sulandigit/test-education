package com.roncoo.education.dashboard.service.admin.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-大屏实时数据响应
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "管理后台-大屏实时数据响应")
public class AdminDashboardRealTimeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "数据更新时间")
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "当前在线用户数")
    private Integer onlineUsers;

    @ApiModelProperty(value = "今日新增用户数")
    private Integer dailyNewUsers;

    @ApiModelProperty(value = "用户总数")
    private Integer totalUsers;

    @ApiModelProperty(value = "今日订单数")
    private Integer dailyOrders;

    @ApiModelProperty(value = "今日收入")
    private BigDecimal dailyRevenue;

    @ApiModelProperty(value = "本月收入")
    private BigDecimal monthlyRevenue;

    @ApiModelProperty(value = "今日课程播放次数")
    private Integer dailyVideoViews;

    @ApiModelProperty(value = "热门课程TOP10")
    private List<HotCourseItem> hotCourses;

    @ApiModelProperty(value = "支付方式分布")
    private List<PaymentTypeItem> paymentTypes;

    @ApiModelProperty(value = "用户地域分布TOP10")
    private List<UserLocationItem> userLocations;

    @ApiModelProperty(value = "系统运行状态")
    private SystemStatusItem systemStatus;

    /**
     * 热门课程项
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "热门课程项")
    public static class HotCourseItem implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "课程ID")
        private Long courseId;

        @ApiModelProperty(value = "课程名称")
        private String courseName;

        @ApiModelProperty(value = "学习人数")
        private Integer studyCount;

        @ApiModelProperty(value = "课程收入")
        private BigDecimal revenue;

        @ApiModelProperty(value = "排名")
        private Integer rank;
    }

    /**
     * 支付方式项
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "支付方式项")
    public static class PaymentTypeItem implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "支付方式：1微信支付，2支付宝支付")
        private Integer payType;

        @ApiModelProperty(value = "支付方式名称")
        private String payTypeName;

        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;

        @ApiModelProperty(value = "收入金额")
        private BigDecimal amount;

        @ApiModelProperty(value = "占比")
        private Double percentage;
    }

    /**
     * 用户地域分布项
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "用户地域分布项")
    public static class UserLocationItem implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "地区名称")
        private String locationName;

        @ApiModelProperty(value = "用户数量")
        private Integer userCount;

        @ApiModelProperty(value = "占比")
        private Double percentage;
    }

    /**
     * 系统状态项
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "系统状态项")
    public static class SystemStatusItem implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "在线服务数")
        private Integer onlineServices;

        @ApiModelProperty(value = "总服务数")
        private Integer totalServices;

        @ApiModelProperty(value = "今日异常数")
        private Integer todayErrors;

        @ApiModelProperty(value = "系统负载")
        private Double systemLoad;

        @ApiModelProperty(value = "数据库连接数")
        private Integer dbConnections;
    }

}