package com.roncoo.education.dashboard.service.websocket;

import com.roncoo.education.dashboard.service.admin.resp.AdminDashboardRealTimeResp;

/**
 * WebSocket推送服务接口
 *
 * @author wujing
 * @date 2025-09-20
 */
public interface WebSocketPushService {

    /**
     * 推送实时数据到所有连接的客户端
     *
     * @param data 实时数据
     */
    void pushRealTimeData(AdminDashboardRealTimeResp data);

    /**
     * 推送指标更新到指定主题
     *
     * @param metricType 指标类型
     * @param value 指标值
     */
    void pushMetricUpdate(String metricType, Object value);

    /**
     * 推送系统通知
     *
     * @param notification 通知内容
     */
    void pushSystemNotification(String notification);

    /**
     * 推送错误信息
     *
     * @param error 错误信息
     */
    void pushError(String error);

    /**
     * 获取当前连接的客户端数量
     *
     * @return 连接数
     */
    int getConnectedClientsCount();

    /**
     * 广播消息到所有客户端
     *
     * @param destination 目标地址
     * @param message 消息内容
     */
    void broadcast(String destination, Object message);

}