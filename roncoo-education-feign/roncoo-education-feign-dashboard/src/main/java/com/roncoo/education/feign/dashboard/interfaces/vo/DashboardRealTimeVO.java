package com.roncoo.education.feign.dashboard.interfaces.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 大屏实时数据VO
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "大屏实时数据")
public class DashboardRealTimeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "当前在线用户数")
    private Integer onlineUsers;

    @ApiModelProperty(value = "今日新增用户数")
    private Integer dailyNewUsers;

    @ApiModelProperty(value = "今日总收入")
    private BigDecimal dailyRevenue;

    @ApiModelProperty(value = "本月总收入")
    private BigDecimal monthlyRevenue;

    @ApiModelProperty(value = "热门课程列表")
    private List<HotCourseVO> hotCourses;

    @ApiModelProperty(value = "系统状态信息")
    private SystemStatusVO systemStatus;

    @ApiModelProperty(value = "用户地域分布")
    private List<UserLocationVO> userLocations;

    /**
     * 热门课程VO
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "热门课程")
    public static class HotCourseVO implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "课程ID")
        private Long courseId;

        @ApiModelProperty(value = "课程名称")
        private String courseName;

        @ApiModelProperty(value = "学习人数")
        private Integer studyCount;

        @ApiModelProperty(value = "课程收入")
        private BigDecimal revenue;
    }

    /**
     * 系统状态VO
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "系统状态")
    public static class SystemStatusVO implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "CPU使用率")
        private Double cpuUsage;

        @ApiModelProperty(value = "内存使用率")
        private Double memoryUsage;

        @ApiModelProperty(value = "在线服务数")
        private Integer onlineServices;

        @ApiModelProperty(value = "异常数量")
        private Integer errorCount;
    }

    /**
     * 用户地域分布VO
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "用户地域分布")
    public static class UserLocationVO implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "省份/城市名称")
        private String locationName;

        @ApiModelProperty(value = "用户数量")
        private Integer userCount;

        @ApiModelProperty(value = "占比")
        private Double percentage;
    }

}