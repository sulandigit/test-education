package com.roncoo.education.dashboard.service.websocket.impl;

import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;
import com.roncoo.education.dashboard.service.websocket.WebSocketPushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WebSocket推送服务测试
 *
 * @author wujing
 * @date 2025-09-20
 */
@ExtendWith(MockitoExtension.class)
class WebSocketPushServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private WebSocketPushServiceImpl webSocketPushService;

    @BeforeEach
    void setUp() {
        webSocketPushService = new WebSocketPushServiceImpl(messagingTemplate);
    }

    @Test
    void testPushRealTimeData() {
        // Given
        AdminDashboardRealTimeResp data = new AdminDashboardRealTimeResp()
                .setOnlineUsers(100)
                .setDailyNewUsers(50)
                .setDailyRevenue(new BigDecimal("1000.00"));

        // When
        webSocketPushService.pushRealTimeData(data);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/dashboard/realtime"), any(AdminDashboardRealTimeResp.class));
        assertNotNull(data.getTimestamp());
    }

    @Test
    void testPushMetricUpdate() {
        // Given
        String metricType = "online_users";
        Object value = 150;

        // When
        webSocketPushService.pushMetricUpdate(metricType, value);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/dashboard/metric"), any(WebSocketPushServiceImpl.MetricUpdate.class));
    }

    @Test
    void testPushSystemNotification() {
        // Given
        String notification = "系统正常运行";

        // When
        webSocketPushService.pushSystemNotification(notification);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/dashboard/notification"), any(WebSocketPushServiceImpl.SystemNotification.class));
    }

    @Test
    void testPushError() {
        // Given
        String error = "数据更新失败";

        // When
        webSocketPushService.pushError(error);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/dashboard/error"), any(WebSocketPushServiceImpl.SystemNotification.class));
    }

    @Test
    void testBroadcast() {
        // Given
        String destination = "/topic/test";
        String message = "测试消息";

        // When
        webSocketPushService.broadcast(destination, message);

        // Then
        verify(messagingTemplate).convertAndSend(destination, message);
    }

    @Test
    void testConnectionCount() {
        // Given
        int initialCount = webSocketPushService.getConnectedClientsCount();

        // When
        webSocketPushService.incrementConnection();
        int afterIncrement = webSocketPushService.getConnectedClientsCount();

        webSocketPushService.decrementConnection();
        int afterDecrement = webSocketPushService.getConnectedClientsCount();

        // Then
        assertEquals(0, initialCount);
        assertEquals(1, afterIncrement);
        assertEquals(0, afterDecrement);
    }

}