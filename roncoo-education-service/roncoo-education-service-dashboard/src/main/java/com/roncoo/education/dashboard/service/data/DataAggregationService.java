package com.roncoo.education.dashboard.service.data;

import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;

/**
 * 数据聚合服务接口
 *
 * @author wujing
 * @date 2025-09-20
 */
public interface DataAggregationService {

    /**
     * 聚合用户相关数据
     *
     * @return 用户数据
     */
    UserDataAggregation aggregateUserData();

    /**
     * 聚合课程相关数据
     *
     * @return 课程数据
     */
    CourseDataAggregation aggregateCourseData();

    /**
     * 聚合订单相关数据
     *
     * @return 订单数据
     */
    OrderDataAggregation aggregateOrderData();

    /**
     * 聚合系统状态数据
     *
     * @return 系统状态数据
     */
    SystemDataAggregation aggregateSystemData();

    /**
     * 获取完整的实时数据
     *
     * @return 实时数据
     */
    AdminDashboardRealTimeResp getRealTimeData();

}