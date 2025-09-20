package com.roncoo.education.dashboard.websocket;

import com.roncoo.education.dashboard.service.websocket.impl.WebSocketPushServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket连接事件监听器
 *
 * @author wujing
 * @date 2025-09-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketPushServiceImpl webSocketPushService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 监听WebSocket连接事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        log.info("WebSocket连接建立: sessionId={}", sessionId);
        
        // 增加连接计数
        webSocketPushService.incrementConnection();
        
        // 向新连接的客户端发送欢迎消息
        try {
            messagingTemplate.convertAndSendToUser(
                sessionId, 
                "/queue/welcome", 
                "欢迎连接到大屏数据推送服务"
            );
        } catch (Exception e) {
            log.error("发送欢迎消息失败", e);
        }
    }

    /**
     * 监听WebSocket断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("WebSocket连接断开: sessionId={}", sessionId);
        
        // 减少连接计数
        webSocketPushService.decrementConnection();
    }

}