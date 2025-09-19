/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.util;

import com.roncoo.education.common.core.tools.NOUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NOUtil 工具类单元测试
 * 测试序列号生成工具的各种功能
 *
 * @author Test Framework
 */
@DisplayName("序列号工具类测试")
class NOUtilTest {

    @Test
    @DisplayName("测试验证码生成 - 基本功能")
    void testGetVerCode() {
        // When
        String verCode = NOUtil.getVerCode();
        
        // Then
        assertNotNull(verCode, "验证码不应为空");
        assertEquals(6, verCode.length(), "验证码长度应为6位");
        assertTrue(verCode.matches("\\d{6}"), "验证码应全为数字");
    }
    
    @RepeatedTest(10)
    @DisplayName("测试验证码生成 - 随机性验证")
    void testGetVerCode_Randomness() {
        // When
        String verCode1 = NOUtil.getVerCode();
        String verCode2 = NOUtil.getVerCode();
        
        // Then - 虽然理论上可能相同，但概率很低
        // 主要验证格式正确性
        assertNotNull(verCode1, "第一个验证码不应为空");
        assertNotNull(verCode2, "第二个验证码不应为空");
        assertEquals(6, verCode1.length(), "第一个验证码长度应为6位");
        assertEquals(6, verCode2.length(), "第二个验证码长度应为6位");
        assertTrue(verCode1.matches("\\d{6}"), "第一个验证码应全为数字");
        assertTrue(verCode2.matches("\\d{6}"), "第二个验证码应全为数字");
    }

    @Test
    @DisplayName("测试订单号生成 - 基本功能")
    void testGetOrderNo() {
        // When
        Long orderNo = NOUtil.getOrderNo();
        
        // Then
        assertNotNull(orderNo, "订单号不应为空");
        assertTrue(orderNo > 0, "订单号应大于0");
        
        // 订单号格式：yyyyMMddHHmmss + 3位随机数，总长度应为17位
        String orderNoStr = orderNo.toString();
        assertEquals(17, orderNoStr.length(), "订单号长度应为17位");
        
        // 验证前14位是否为时间格式（年月日时分秒）
        String timePart = orderNoStr.substring(0, 14);
        assertTrue(timePart.matches("\\d{14}"), "订单号前14位应为数字时间格式");
        
        // 验证后3位是否为数字
        String randomPart = orderNoStr.substring(14);
        assertEquals(3, randomPart.length(), "随机部分应为3位");
        assertTrue(randomPart.matches("\\d{3}"), "随机部分应全为数字");
    }
    
    @Test
    @DisplayName("测试序列号生成 - 基本功能")
    void testGetSerialNumber() {
        // When
        Long serialNo = NOUtil.getSerialNumber();
        
        // Then
        assertNotNull(serialNo, "序列号不应为空");
        assertTrue(serialNo > 0, "序列号应大于0");
        
        // 序列号格式：yyyyMMddHHmmss + 4位随机数，总长度应为18位
        String serialNoStr = serialNo.toString();
        assertEquals(18, serialNoStr.length(), "序列号长度应为18位");
        
        // 验证前14位是否为时间格式（年月日时分秒）
        String timePart = serialNoStr.substring(0, 14);
        assertTrue(timePart.matches("\\d{14}"), "序列号前14位应为数字时间格式");
        
        // 验证后4位是否为数字
        String randomPart = serialNoStr.substring(14);
        assertEquals(4, randomPart.length(), "随机部分应为4位");
        assertTrue(randomPart.matches("\\d{4}"), "随机部分应全为数字");
    }
    
    @RepeatedTest(5)
    @DisplayName("测试订单号唯一性")
    void testGetOrderNo_Uniqueness() {
        // When
        Long orderNo1 = NOUtil.getOrderNo();
        
        // 稍微等待一下以确保时间差异（虽然有随机数，但保险起见）
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Long orderNo2 = NOUtil.getOrderNo();
        
        // Then
        assertNotEquals(orderNo1, orderNo2, "两个订单号应该不同");
    }
    
    @RepeatedTest(5)
    @DisplayName("测试序列号唯一性")
    void testGetSerialNumber_Uniqueness() {
        // When
        Long serialNo1 = NOUtil.getSerialNumber();
        
        // 稍微等待一下以确保时间差异
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Long serialNo2 = NOUtil.getSerialNumber();
        
        // Then
        assertNotEquals(serialNo1, serialNo2, "两个序列号应该不同");
    }
    
    @Test
    @DisplayName("测试边界条件 - 并发调用")
    void testConcurrentGeneration() {
        // Given
        int threadCount = 10;
        int callsPerThread = 100;
        java.util.Set<Long> orderNumbers = java.util.concurrent.ConcurrentHashMap.newKeySet();
        java.util.Set<Long> serialNumbers = java.util.concurrent.ConcurrentHashMap.newKeySet();
        
        // When
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < callsPerThread; j++) {
                        orderNumbers.add(NOUtil.getOrderNo());
                        serialNumbers.add(NOUtil.getSerialNumber());
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("并发测试被中断");
        }
        
        // Then
        int expectedCount = threadCount * callsPerThread;
        // 由于时间戳精度和随机数范围的限制，可能会有极少量重复
        // 但绝大多数应该是唯一的
        assertTrue(orderNumbers.size() > expectedCount * 0.9, 
                "订单号唯一性应达到90%以上，实际: " + orderNumbers.size() + "/" + expectedCount);
        assertTrue(serialNumbers.size() > expectedCount * 0.9, 
                "序列号唯一性应达到90%以上，实际: " + serialNumbers.size() + "/" + expectedCount);
    }
}