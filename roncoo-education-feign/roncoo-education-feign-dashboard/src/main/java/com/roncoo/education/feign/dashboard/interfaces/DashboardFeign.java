package com.roncoo.education.feign.dashboard.interfaces;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.feign.dashboard.interfaces.vo.DashboardRealTimeVO;
import com.roncoo.education.feign.dashboard.interfaces.vo.DashboardTrendVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 大屏数据服务Feign接口
 *
 * @author wujing
 * @date 2025-09-20
 */
@FeignClient(value = "roncoo-education-service-dashboard", path = "/dashboard")
public interface DashboardFeign {

    /**
     * 获取实时数据
     *
     * @return 实时数据
     */
    @GetMapping(value = "/realtime-data")
    Result<DashboardRealTimeVO> getRealTimeData();

    /**
     * 获取趋势数据
     *
     * @param metricType 指标类型
     * @param timeRange 时间范围
     * @return 趋势数据
     */
    @GetMapping(value = "/trend-data")
    Result<DashboardTrendVO> getTrendData(@RequestParam("metricType") String metricType, 
                                          @RequestParam("timeRange") String timeRange);

}