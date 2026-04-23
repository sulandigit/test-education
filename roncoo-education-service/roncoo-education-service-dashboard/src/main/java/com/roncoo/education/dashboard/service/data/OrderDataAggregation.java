package com.roncoo.education.dashboard.service.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单数据聚合结果
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
public class OrderDataAggregation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 今日订单数
     */
    private Integer dailyOrders;

    /**
     * 今日收入
     */
    private BigDecimal dailyRevenue;

    /**
     * 本月收入
     */
    private BigDecimal monthlyRevenue;

    /**
     * 支付方式分布
     */
    private List<PaymentTypeData> paymentTypes;

    /**
     * 订单状态统计
     */
    private OrderStatusData orderStatus;

    /**
     * 收入趋势数据
     */
    private RevenueTrendData revenueTrend;

    /**
     * 支付方式数据
     */
    @Data
    @Accessors(chain = true)
    public static class PaymentTypeData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Integer payType;
        private String payTypeName;
        private Integer orderCount;
        private BigDecimal amount;
        private Double percentage;
    }

    /**
     * 订单状态数据
     */
    @Data
    @Accessors(chain = true)
    public static class OrderStatusData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Integer pendingOrders;
        private Integer successOrders;
        private Integer failedOrders;
        private Integer closedOrders;
        private Double successRate;
    }

    /**
     * 收入趋势数据
     */
    @Data
    @Accessors(chain = true)
    public static class RevenueTrendData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private BigDecimal todayRevenue;
        private BigDecimal yesterdayRevenue;
        private BigDecimal thisWeekRevenue;
        private BigDecimal lastWeekRevenue;
        private Double dailyGrowthRate;
        private Double weeklyGrowthRate;
    }

}