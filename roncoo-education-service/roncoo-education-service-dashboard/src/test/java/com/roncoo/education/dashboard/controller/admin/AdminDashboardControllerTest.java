package com.roncoo.education.dashboard.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.dashboard.service.admin.AdminDashboardService;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardTrendResp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 管理后台大屏控制器测试
 *
 * @author wujing
 * @date 2025-09-20
 */
@WebMvcTest(AdminDashboardController.class)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRealTimeData() throws Exception {
        // Given
        AdminDashboardRealTimeResp mockData = new AdminDashboardRealTimeResp()
                .setTimestamp(LocalDateTime.now())
                .setOnlineUsers(200)
                .setDailyNewUsers(50)
                .setDailyRevenue(new BigDecimal("2000.00"))
                .setMonthlyRevenue(new BigDecimal("50000.00"));

        Result<AdminDashboardRealTimeResp> mockResult = Result.success(mockData);
        when(adminDashboardService.getRealTimeData()).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(get("/admin/dashboard/realtime-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onlineUsers").value(200))
                .andExpect(jsonPath("$.data.dailyNewUsers").value(50))
                .andExpect(jsonPath("$.data.dailyRevenue").value(2000.00));

        verify(adminDashboardService).getRealTimeData();
    }

    @Test
    void testGetTrendData() throws Exception {
        // Given
        AdminDashboardTrendResp mockData = new AdminDashboardTrendResp()
                .setMetricType("user_count")
                .setTimeRange("7d")
                .setGranularity("daily");

        Result<AdminDashboardTrendResp> mockResult = Result.success(mockData);
        when(adminDashboardService.getTrendData(anyString(), anyString())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(get("/admin/dashboard/trend-data")
                        .param("metricType", "user_count")
                        .param("timeRange", "7d")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.metricType").value("user_count"))
                .andExpect(jsonPath("$.data.timeRange").value("7d"));

        verify(adminDashboardService).getTrendData("user_count", "7d");
    }

    @Test
    void testGetTrendDataWithMissingParams() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/dashboard/trend-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}