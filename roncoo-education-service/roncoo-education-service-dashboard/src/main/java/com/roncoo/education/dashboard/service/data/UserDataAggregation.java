package com.roncoo.education.dashboard.service.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户数据聚合结果
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
public class UserDataAggregation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前在线用户数
     */
    private Integer onlineUsers;

    /**
     * 今日新增用户数
     */
    private Integer dailyNewUsers;

    /**
     * 用户总数
     */
    private Integer totalUsers;

    /**
     * 用户地域分布
     */
    private List<UserLocationData> userLocations;

    /**
     * 用户活跃度统计
     */
    private UserActivityData userActivity;

    /**
     * 用户地域数据
     */
    @Data
    @Accessors(chain = true)
    public static class UserLocationData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private String locationName;
        private Integer userCount;
        private Double percentage;
    }

    /**
     * 用户活跃度数据
     */
    @Data
    @Accessors(chain = true)
    public static class UserActivityData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Integer activeUsersToday;
        private Integer activeUsersThisWeek;
        private Integer activeUsersThisMonth;
        private Double activityRate;
    }

}