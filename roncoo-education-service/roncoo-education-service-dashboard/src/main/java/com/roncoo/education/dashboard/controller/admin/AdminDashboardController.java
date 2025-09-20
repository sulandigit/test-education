package com.roncoo.education.dashboard.controller.admin;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.dashboard.service.admin.AdminDashboardService;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardTrendResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-大屏数据控制器
 *
 * @author wujing
 * @date 2025-09-20
 */
@Api(tags = "管理后台-大屏数据管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @ApiOperation(value = "获取实时数据")
    @GetMapping("/realtime-data")
    public Result<AdminDashboardRealTimeResp> getRealTimeData() {
        return adminDashboardService.getRealTimeData();
    }

    @ApiOperation(value = "获取趋势数据")
    @GetMapping("/trend-data")
    public Result<AdminDashboardTrendResp> getTrendData(
            @ApiParam(value = "指标类型", required = true) @RequestParam String metricType,
            @ApiParam(value = "时间范围", required = true) @RequestParam String timeRange) {
        return adminDashboardService.getTrendData(metricType, timeRange);
    }

}