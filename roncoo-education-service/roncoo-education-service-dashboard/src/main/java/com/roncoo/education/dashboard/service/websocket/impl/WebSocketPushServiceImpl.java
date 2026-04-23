package com.roncoo.education.dashboard.service.websocket.impl;

import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.websocket.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket推送服务实现
 *
 * @author wujing
 * @date 2025-09-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPushServiceImpl implements WebSocketPushService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // 连接计数器
    private final AtomicInteger connectionCount = new AtomicInteger(0);

    // WebSocket目标地址常量
    private static final String TOPIC_REALTIME_DATA = "/topic/dashboard/realtime";
    private static final String TOPIC_METRIC_UPDATE = "/topic/dashboard/metric";
    private static final String TOPIC_NOTIFICATION = "/topic/dashboard/notification";
    private static final String TOPIC_ERROR = "/topic/dashboard/error";

    @Override
    public void pushRealTimeData(AdminDashboardRealTimeResp data) {
        try {
            // 添加推送时间戳
            data.setTimestamp(LocalDateTime.now());
            
            // 推送到实时数据主题
            messagingTemplate.convertAndSend(TOPIC_REALTIME_DATA, data);
            
            log.debug("推送实时数据成功，连接数: {}", connectionCount.get());
        } catch (Exception e) {
            log.error("推送实时数据失败", e);
        }
    }

    @Override
    public void pushMetricUpdate(String metricType, Object value) {
        try {
            MetricUpdate update = new MetricUpdate()
                    .setMetricType(metricType)
                    .setValue(value)
                    .setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend(TOPIC_METRIC_UPDATE, update);
            
            log.debug("推送指标更新成功: {} = {}", metricType, value);
        } catch (Exception e) {
            log.error("推送指标更新失败: {}", metricType, e);
        }
    }

    @Override
    public void pushSystemNotification(String notification) {
        try {
            SystemNotification sysNotification = new SystemNotification()
                    .setMessage(notification)
                    .setType("INFO")
                    .setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend(TOPIC_NOTIFICATION, sysNotification);
            
            log.debug("推送系统通知成功: {}", notification);
        } catch (Exception e) {
            log.error("推送系统通知失败", e);
        }
    }

    @Override
    public void pushError(String error) {
        try {
            SystemNotification errorNotification = new SystemNotification()
                    .setMessage(error)
                    .setType("ERROR")
                    .setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend(TOPIC_ERROR, errorNotification);
            
            log.warn("推送错误信息: {}", error);
        } catch (Exception e) {
            log.error("推送错误信息失败", e);
        }
    }

    @Override
    public int getConnectedClientsCount() {
        return connectionCount.get();
    }

    @Override
    public void broadcast(String destination, Object message) {
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.debug("广播消息成功: {} -> {}", destination, message);
        } catch (Exception e) {
            log.error("广播消息失败: {}", destination, e);
        }
    }

    /**
     * 增加连接数
     */
    public void incrementConnection() {
        int count = connectionCount.incrementAndGet();
        log.info("新建WebSocket连接，当前连接数: {}", count);
    }

    /**
     * 减少连接数
     */
    public void decrementConnection() {
        int count = connectionCount.decrementAndGet();
        log.info("WebSocket连接断开，当前连接数: {}", count);
    }

    /**
     * 指标更新消息
     */
    public static class MetricUpdate {
        private String metricType;
        private Object value;
        private LocalDateTime timestamp;

        public String getMetricType() {
            return metricType;
        }

        public MetricUpdate setMetricType(String metricType) {
            this.metricType = metricType;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public MetricUpdate setValue(Object value) {
            this.value = value;
            return this;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public MetricUpdate setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
    }

    /**
     * 系统通知消息
     */
    public static class SystemNotification {
        private String message;
        private String type;
        private LocalDateTime timestamp;

        public String getMessage() {
            return message;
        }

        public SystemNotification setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getType() {
            return type;
        }

        public SystemNotification setType(String type) {
            this.type = type;
            return this;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public SystemNotification setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
    }

}