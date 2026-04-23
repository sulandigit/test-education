package com.roncoo.education.dashboard.job;

import com.roncoo.education.dashboard.service.cache.CacheManagerService;
import com.roncoo.education.dashboard.service.data.DataAggregationService;
import com.roncoo.education.dashboard.service.websocket.WebSocketPushService;
import com.roncoo.education.dashboard.common.constants.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 大屏数据定时更新任务
 *
 * @author wujing
 * @date 2025-09-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardDataScheduledJob {

    private final DataAggregationService dataAggregationService;
    private final CacheManagerService cacheManagerService;
    private final WebSocketPushService webSocketPushService;

    /**
     * 每5秒更新实时数据
     */
    @Scheduled(fixedRate = 5000)
    public void updateRealTimeData() {
        try {
            log.debug("开始更新实时数据");
            
            // 获取最新的实时数据
            var realTimeData = dataAggregationService.getRealTimeData();
            
            // 缓存实时数据
            cacheManagerService.cacheHotData(
                CacheConstants.REALTIME_DATA_KEY, 
                realTimeData, 
                CacheConstants.ExpireTime.REALTIME_DATA
            );
            
            // 通过WebSocket推送到前端
            webSocketPushService.pushRealTimeData(realTimeData);
            
            log.debug("实时数据更新完成");
            
        } catch (Exception e) {
            log.error("更新实时数据失败", e);
            webSocketPushService.pushError("实时数据更新失败: " + e.getMessage());
        }
    }

    /**
     * 每1分钟更新用户统计数据
     */
    @Scheduled(fixedRate = 60000)
    public void updateUserStats() {
        try {
            log.debug("开始更新用户统计数据");
            
            var userData = dataAggregationService.aggregateUserData();
            
            // 缓存用户数据
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.ONLINE_USERS, 
                userData.getOnlineUsers(), CacheConstants.ExpireTime.USER_STATS);
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.DAILY_NEW_USERS, 
                userData.getDailyNewUsers(), CacheConstants.ExpireTime.USER_STATS);
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.TOTAL_USERS, 
                userData.getTotalUsers(), CacheConstants.ExpireTime.USER_STATS);
            
            // 推送用户指标更新
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.ONLINE_USERS, userData.getOnlineUsers());
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.DAILY_NEW_USERS, userData.getDailyNewUsers());
            
            log.debug("用户统计数据更新完成");
            
        } catch (Exception e) {
            log.error("更新用户统计数据失败", e);
        }
    }

    /**
     * 每5分钟更新课程统计数据
     */
    @Scheduled(fixedRate = 300000)
    public void updateCourseStats() {
        try {
            log.debug("开始更新课程统计数据");
            
            var courseData = dataAggregationService.aggregateCourseData();
            
            // 缓存课程数据
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.HOT_COURSES, 
                courseData.getHotCourses(), CacheConstants.ExpireTime.COURSE_STATS);
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.DAILY_VIDEO_VIEWS, 
                courseData.getDailyVideoViews(), CacheConstants.ExpireTime.COURSE_STATS);
            
            // 推送课程指标更新
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.HOT_COURSES, courseData.getHotCourses());
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.DAILY_VIDEO_VIEWS, courseData.getDailyVideoViews());
            
            log.debug("课程统计数据更新完成");
            
        } catch (Exception e) {
            log.error("更新课程统计数据失败", e);
        }
    }

    /**
     * 每1分钟更新订单统计数据
     */
    @Scheduled(fixedRate = 60000)
    public void updateOrderStats() {
        try {
            log.debug("开始更新订单统计数据");
            
            var orderData = dataAggregationService.aggregateOrderData();
            
            // 缓存订单数据
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.DAILY_ORDERS, 
                orderData.getDailyOrders(), CacheConstants.ExpireTime.ORDER_STATS);
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.DAILY_REVENUE, 
                orderData.getDailyRevenue(), CacheConstants.ExpireTime.ORDER_STATS);
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.MONTHLY_REVENUE, 
                orderData.getMonthlyRevenue(), CacheConstants.ExpireTime.ORDER_STATS);
            
            // 推送订单指标更新
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.DAILY_ORDERS, orderData.getDailyOrders());
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.DAILY_REVENUE, orderData.getDailyRevenue());
            
            log.debug("订单统计数据更新完成");
            
        } catch (Exception e) {
            log.error("更新订单统计数据失败", e);
        }
    }

    /**
     * 每30秒更新系统状态数据
     */
    @Scheduled(fixedRate = 30000)
    public void updateSystemStatus() {
        try {
            log.debug("开始更新系统状态数据");
            
            var systemData = dataAggregationService.aggregateSystemData();
            
            // 缓存系统状态数据
            cacheManagerService.cacheMetric(CacheConstants.CacheKey.SYSTEM_STATUS, 
                systemData, CacheConstants.ExpireTime.SYSTEM_STATUS);
            
            // 推送系统状态更新
            webSocketPushService.pushMetricUpdate(CacheConstants.CacheKey.SYSTEM_STATUS, systemData);
            
            log.debug("系统状态数据更新完成");
            
        } catch (Exception e) {
            log.error("更新系统状态数据失败", e);
        }
    }

    /**
     * 每10分钟清理过期缓存
     */
    @Scheduled(fixedRate = 600000)
    public void cleanExpiredCache() {
        try {
            log.debug("开始清理过期缓存");
            
            cacheManagerService.cleanExpiredCache();
            
            // 获取缓存统计
            var cacheStats = cacheManagerService.getCacheStats();
            log.info("缓存清理完成，当前缓存统计: {}", cacheStats);
            
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
        }
    }

    /**
     * 每小时执行系统健康检查
     */
    @Scheduled(fixedRate = 3600000)
    public void systemHealthCheck() {
        try {
            log.info("开始系统健康检查");
            
            // 检查WebSocket连接数
            int wsConnections = webSocketPushService.getConnectedClientsCount();
            
            // 检查缓存状态
            var cacheStats = cacheManagerService.getCacheStats();
            
            // 推送系统通知
            String healthMessage = String.format("系统健康检查: WebSocket连接数=%d, 缓存数量=%s", 
                wsConnections, cacheStats.get("totalCacheCount"));
            webSocketPushService.pushSystemNotification(healthMessage);
            
            log.info("系统健康检查完成: {}", healthMessage);
            
        } catch (Exception e) {
            log.error("系统健康检查失败", e);
            webSocketPushService.pushError("系统健康检查失败: " + e.getMessage());
        }
    }

}