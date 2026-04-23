package com.roncoo.education.dashboard.service.admin;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardTrendResp;

/**
 * 大屏数据服务接口
 *
 * @author wujing
 * @date 2025-09-20
 */
public interface AdminDashboardService {

    /**
     * 获取实时数据
     *
     * @return 实时数据响应
     */
    Result<AdminDashboardRealTimeResp> getRealTimeData();

    /**
     * 获取趋势数据
     *
     * @param metricType 指标类型
     * @param timeRange 时间范围
     * @return 趋势数据响应
     */
    Result<AdminDashboardTrendResp> getTrendData(String metricType, String timeRange);

}