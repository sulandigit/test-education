package com.roncoo.education.feign.dashboard.interfaces.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 大屏趋势数据VO
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "大屏趋势数据")
public class DashboardTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指标类型")
    private String metricType;

    @ApiModelProperty(value = "时间范围")
    private String timeRange;

    @ApiModelProperty(value = "趋势数据点列表")
    private List<TrendDataPoint> dataPoints;

    /**
     * 趋势数据点
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "趋势数据点")
    public static class TrendDataPoint implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "时间点")
        private LocalDateTime timestamp;

        @ApiModelProperty(value = "数值")
        private BigDecimal value;

        @ApiModelProperty(value = "变化率")
        private Double changeRate;

        @ApiModelProperty(value = "标签")
        private String label;
    }

}