package com.roncoo.education.dashboard.service.data.impl;

import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.data.*;
import com.roncoo.education.feign.course.interfaces.CourseFeign;
import com.roncoo.education.feign.user.interfaces.UserFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据聚合服务实现
 *
 * @author wujing
 * @date 2025-09-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAggregationServiceImpl implements DataAggregationService {

    private final UserDataService userDataService;
    private final CourseDataService courseDataService;
    private final OrderDataService orderDataService;
    private final SystemDataService systemDataService;

    @Override
    public UserDataAggregation aggregateUserData() {
        log.debug("开始聚合用户数据");
        
        UserDataAggregation aggregation = new UserDataAggregation();
        
        try {
            // 获取用户基础统计
            aggregation.setOnlineUsers(userDataService.getOnlineUserCount());
            aggregation.setDailyNewUsers(userDataService.getDailyNewUserCount());
            aggregation.setTotalUsers(userDataService.getTotalUserCount());
            
            // 获取用户地域分布
            List<UserDataAggregation.UserLocationData> locations = userDataService.getUserLocationDistribution();
            aggregation.setUserLocations(locations);
            
            // 获取用户活跃度
            UserDataAggregation.UserActivityData activity = userDataService.getUserActivityData();
            aggregation.setUserActivity(activity);
            
            log.debug("用户数据聚合完成: 在线用户={}, 今日新增={}, 总用户数={}", 
                    aggregation.getOnlineUsers(), aggregation.getDailyNewUsers(), aggregation.getTotalUsers());
                    
        } catch (Exception e) {
            log.error("聚合用户数据失败", e);
            // 返回默认值，避免空指针
            aggregation.setOnlineUsers(0)
                      .setDailyNewUsers(0)
                      .setTotalUsers(0)
                      .setUserLocations(new ArrayList<>());
        }
        
        return aggregation;
    }

    @Override
    public CourseDataAggregation aggregateCourseData() {
        log.debug("开始聚合课程数据");
        
        CourseDataAggregation aggregation = new CourseDataAggregation();
        
        try {
            // 获取视频播放统计
            aggregation.setDailyVideoViews(courseDataService.getDailyVideoViews());
            
            // 获取热门课程
            List<CourseDataAggregation.HotCourseData> hotCourses = courseDataService.getHotCourses();
            aggregation.setHotCourses(hotCourses);
            
            // 获取分类统计
            List<CourseDataAggregation.CourseCategoryData> categoryStats = courseDataService.getCategoryStats();
            aggregation.setCategoryStats(categoryStats);
            
            // 获取完成率统计
            CourseDataAggregation.CourseCompletionData completionStats = courseDataService.getCompletionStats();
            aggregation.setCompletionStats(completionStats);
            
            log.debug("课程数据聚合完成: 今日播放={}, 热门课程数={}", 
                    aggregation.getDailyVideoViews(), hotCourses.size());
                    
        } catch (Exception e) {
            log.error("聚合课程数据失败", e);
            aggregation.setDailyVideoViews(0)
                      .setHotCourses(new ArrayList<>())
                      .setCategoryStats(new ArrayList<>());
        }
        
        return aggregation;
    }

    @Override
    public OrderDataAggregation aggregateOrderData() {
        log.debug("开始聚合订单数据");
        
        OrderDataAggregation aggregation = new OrderDataAggregation();
        
        try {
            // 获取订单基础统计
            aggregation.setDailyOrders(orderDataService.getDailyOrderCount());
            aggregation.setDailyRevenue(orderDataService.getDailyRevenue());
            aggregation.setMonthlyRevenue(orderDataService.getMonthlyRevenue());
            
            // 获取支付方式分布
            List<OrderDataAggregation.PaymentTypeData> paymentTypes = orderDataService.getPaymentTypeDistribution();
            aggregation.setPaymentTypes(paymentTypes);
            
            // 获取订单状态统计
            OrderDataAggregation.OrderStatusData orderStatus = orderDataService.getOrderStatusData();
            aggregation.setOrderStatus(orderStatus);
            
            // 获取收入趋势
            OrderDataAggregation.RevenueTrendData revenueTrend = orderDataService.getRevenueTrendData();
            aggregation.setRevenueTrend(revenueTrend);
            
            log.debug("订单数据聚合完成: 今日订单={}, 今日收入={}, 本月收入={}", 
                    aggregation.getDailyOrders(), aggregation.getDailyRevenue(), aggregation.getMonthlyRevenue());
                    
        } catch (Exception e) {
            log.error("聚合订单数据失败", e);
            aggregation.setDailyOrders(0)
                      .setDailyRevenue(BigDecimal.ZERO)
                      .setMonthlyRevenue(BigDecimal.ZERO)
                      .setPaymentTypes(new ArrayList<>());
        }
        
        return aggregation;
    }

    @Override
    public SystemDataAggregation aggregateSystemData() {
        log.debug("开始聚合系统数据");
        
        SystemDataAggregation aggregation = new SystemDataAggregation();
        
        try {
            // 获取系统基础统计
            aggregation.setOnlineServices(systemDataService.getOnlineServiceCount());
            aggregation.setTotalServices(systemDataService.getTotalServiceCount());
            aggregation.setTodayErrors(systemDataService.getTodayErrorCount());
            aggregation.setSystemLoad(systemDataService.getSystemLoad());
            aggregation.setDbConnections(systemDataService.getDbConnectionCount());
            
            // 获取系统性能指标
            SystemDataAggregation.SystemPerformanceData performance = systemDataService.getPerformanceData();
            aggregation.setPerformance(performance);
            
            // 获取服务健康状态
            SystemDataAggregation.ServiceHealthData serviceHealth = systemDataService.getServiceHealthData();
            aggregation.setServiceHealth(serviceHealth);
            
            log.debug("系统数据聚合完成: 在线服务={}, 系统负载={}", 
                    aggregation.getOnlineServices(), aggregation.getSystemLoad());
                    
        } catch (Exception e) {
            log.error("聚合系统数据失败", e);
            aggregation.setOnlineServices(0)
                      .setTotalServices(0)
                      .setTodayErrors(0)
                      .setSystemLoad(0.0)
                      .setDbConnections(0);
        }
        
        return aggregation;
    }

    @Override
    public AdminDashboardRealTimeResp getRealTimeData() {
        log.debug("开始获取完整的实时数据");
        
        AdminDashboardRealTimeResp response = new AdminDashboardRealTimeResp();
        response.setTimestamp(LocalDateTime.now());
        
        try {
            // 聚合各模块数据
            UserDataAggregation userData = aggregateUserData();
            CourseDataAggregation courseData = aggregateCourseData();
            OrderDataAggregation orderData = aggregateOrderData();
            SystemDataAggregation systemData = aggregateSystemData();
            
            // 组装用户数据
            response.setOnlineUsers(userData.getOnlineUsers());
            response.setDailyNewUsers(userData.getDailyNewUsers());
            response.setTotalUsers(userData.getTotalUsers());
            
            // 组装课程数据
            response.setDailyVideoViews(courseData.getDailyVideoViews());
            List<AdminDashboardRealTimeResp.HotCourseItem> hotCourses = courseData.getHotCourses()
                    .stream()
                    .map(this::convertToHotCourseItem)
                    .collect(Collectors.toList());
            response.setHotCourses(hotCourses);
            
            // 组装订单数据
            response.setDailyOrders(orderData.getDailyOrders());
            response.setDailyRevenue(orderData.getDailyRevenue());
            response.setMonthlyRevenue(orderData.getMonthlyRevenue());
            List<AdminDashboardRealTimeResp.PaymentTypeItem> paymentTypes = orderData.getPaymentTypes()
                    .stream()
                    .map(this::convertToPaymentTypeItem)
                    .collect(Collectors.toList());
            response.setPaymentTypes(paymentTypes);
            
            // 组装地域数据
            List<AdminDashboardRealTimeResp.UserLocationItem> userLocations = userData.getUserLocations()
                    .stream()
                    .map(this::convertToUserLocationItem)
                    .collect(Collectors.toList());
            response.setUserLocations(userLocations);
            
            // 组装系统状态
            AdminDashboardRealTimeResp.SystemStatusItem systemStatus = convertToSystemStatusItem(systemData);
            response.setSystemStatus(systemStatus);
            
            log.info("实时数据获取完成");
            
        } catch (Exception e) {
            log.error("获取实时数据失败", e);
            // 设置默认值
            response.setOnlineUsers(0)
                   .setDailyNewUsers(0)
                   .setTotalUsers(0)
                   .setDailyOrders(0)
                   .setDailyRevenue(BigDecimal.ZERO)
                   .setMonthlyRevenue(BigDecimal.ZERO)
                   .setDailyVideoViews(0);
        }
        
        return response;
    }
    
    private AdminDashboardRealTimeResp.HotCourseItem convertToHotCourseItem(CourseDataAggregation.HotCourseData source) {
        return new AdminDashboardRealTimeResp.HotCourseItem()
                .setCourseId(source.getCourseId())
                .setCourseName(source.getCourseName())
                .setStudyCount(source.getStudyCount())
                .setRevenue(source.getRevenue())
                .setRank(source.getRank());
    }
    
    private AdminDashboardRealTimeResp.PaymentTypeItem convertToPaymentTypeItem(OrderDataAggregation.PaymentTypeData source) {
        return new AdminDashboardRealTimeResp.PaymentTypeItem()
                .setPayType(source.getPayType())
                .setPayTypeName(source.getPayTypeName())
                .setOrderCount(source.getOrderCount())
                .setAmount(source.getAmount())
                .setPercentage(source.getPercentage());
    }
    
    private AdminDashboardRealTimeResp.UserLocationItem convertToUserLocationItem(UserDataAggregation.UserLocationData source) {
        return new AdminDashboardRealTimeResp.UserLocationItem()
                .setLocationName(source.getLocationName())
                .setUserCount(source.getUserCount())
                .setPercentage(source.getPercentage());
    }
    
    private AdminDashboardRealTimeResp.SystemStatusItem convertToSystemStatusItem(SystemDataAggregation systemData) {
        return new AdminDashboardRealTimeResp.SystemStatusItem()
                .setOnlineServices(systemData.getOnlineServices())
                .setTotalServices(systemData.getTotalServices())
                .setTodayErrors(systemData.getTodayErrors())
                .setSystemLoad(systemData.getSystemLoad())
                .setDbConnections(systemData.getDbConnections());
    }

}