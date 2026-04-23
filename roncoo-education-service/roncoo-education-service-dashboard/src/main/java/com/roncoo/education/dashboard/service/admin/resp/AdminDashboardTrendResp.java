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
 * 管理后台-大屏趋势数据响应
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "管理后台-大屏趋势数据响应")
public class AdminDashboardTrendResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指标类型")
    private String metricType;

    @ApiModelProperty(value = "时间范围")
    private String timeRange;

    @ApiModelProperty(value = "数据粒度")
    private String granularity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询时间")
    private LocalDateTime queryTime;

    @ApiModelProperty(value = "趋势数据列表")
    private List<TrendDataItem> trendData;

    @ApiModelProperty(value = "汇总统计")
    private TrendSummary summary;

    /**
     * 趋势数据项
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "趋势数据项")
    public static class TrendDataItem implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "时间点")
        private LocalDateTime timestamp;

        @ApiModelProperty(value = "时间标签")
        private String timeLabel;

        @ApiModelProperty(value = "数值")
        private BigDecimal value;

        @ApiModelProperty(value = "对比上期变化率")
        private Double changeRate;

        @ApiModelProperty(value = "是否为峰值")
        private Boolean isPeak;
    }

    /**
     * 趋势汇总
     */
    @Data
    @Accessors(chain = true)
    @ApiModel(description = "趋势汇总")
    public static class TrendSummary implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "最大值")
        private BigDecimal maxValue;

        @ApiModelProperty(value = "最小值")
        private BigDecimal minValue;

        @ApiModelProperty(value = "平均值")
        private BigDecimal avgValue;

        @ApiModelProperty(value = "总和")
        private BigDecimal totalValue;

        @ApiModelProperty(value = "整体增长率")
        private Double growthRate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "峰值时间")
        private LocalDateTime peakTime;
    }

}