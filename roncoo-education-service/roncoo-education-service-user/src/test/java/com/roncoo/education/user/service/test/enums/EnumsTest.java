/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.enums;

import com.roncoo.education.common.core.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 枚举类单元测试
 * 测试各种业务枚举的功能和边界条件
 *
 * @author Test Framework
 */
@DisplayName("业务枚举测试")
class EnumsTest {

    @Test
    @DisplayName("测试LoginStatusEnum - 基本属性")
    void testLoginStatusEnum_BasicProperties() {
        // Test SUCCESS
        assertEquals(Integer.valueOf(1), LoginStatusEnum.SUCCESS.getCode());
        assertEquals("登录成功", LoginStatusEnum.SUCCESS.getDesc());
        assertEquals("", LoginStatusEnum.SUCCESS.getColor());
        
        // Test FAIL
        assertEquals(Integer.valueOf(0), LoginStatusEnum.FAIL.getCode());
        assertEquals("登录失败", LoginStatusEnum.FAIL.getDesc());
        assertEquals("red", LoginStatusEnum.FAIL.getColor());
        
        // Test REGISTER
        assertEquals(Integer.valueOf(2), LoginStatusEnum.REGISTER.getCode());
        assertEquals("注册成功", LoginStatusEnum.REGISTER.getDesc());
        assertEquals("", LoginStatusEnum.REGISTER.getColor());
    }

    @ParameterizedTest
    @EnumSource(LoginStatusEnum.class)
    @DisplayName("测试LoginStatusEnum - 枚举完整性")
    void testLoginStatusEnum_Completeness(LoginStatusEnum status) {
        assertNotNull(status.getCode(), "状态码不应为空");
        assertNotNull(status.getDesc(), "描述不应为空");
        assertNotNull(status.getColor(), "颜色不应为空");
        assertTrue(status.getCode() >= 0, "状态码应大于等于0");
        assertFalse(status.getDesc().trim().isEmpty(), "描述不应为空字符串");
    }

    @Test
    @DisplayName("测试OrderStatusEnum - 基本属性")
    void testOrderStatusEnum_BasicProperties() {
        // Test WAIT
        assertEquals(Integer.valueOf(1), OrderStatusEnum.WAIT.getCode());
        assertEquals("待支付", OrderStatusEnum.WAIT.getDesc());
        
        // Test SUCCESS
        assertEquals(Integer.valueOf(2), OrderStatusEnum.SUCCESS.getCode());
        assertEquals("支付成功", OrderStatusEnum.SUCCESS.getDesc());
        
        // Test FAIL
        assertEquals(Integer.valueOf(3), OrderStatusEnum.FAIL.getCode());
        assertEquals("支付失败", OrderStatusEnum.FAIL.getDesc());
        
        // Test CLOSE
        assertEquals(Integer.valueOf(4), OrderStatus.CLOSE.getCode());
        assertEquals("关闭支付", OrderStatusEnum.CLOSE.getDesc());
    }

    @Test
    @DisplayName("测试PayTypeEnum - byCode方法")
    void testPayTypeEnum_ByCode() {
        // Test valid codes
        assertEquals(PayTypeEnum.WEIXIN_SCAN, PayTypeEnum.byCode(1));
        assertEquals(PayTypeEnum.ALIPAY_SCAN, PayTypeEnum.byCode(2));
        
        // Test invalid codes
        assertNull(PayTypeEnum.byCode(null));
        assertNull(PayTypeEnum.byCode(0));
        assertNull(PayTypeEnum.byCode(999));
        assertNull(PayTypeEnum.byCode(-1));
    }

    @Test
    @DisplayName("测试PayTypeEnum - 实现类属性")
    void testPayTypeEnum_ImplProperties() {
        assertEquals("wxScanPay", PayTypeEnum.WEIXIN_SCAN.getImpl());
        assertEquals("aliScanPay", PayTypeEnum.ALIPAY_SCAN.getImpl());
        
        // 验证实现类名称不为空
        for (PayTypeEnum payType : PayTypeEnum.values()) {
            assertNotNull(payType.getImpl(), "实现类名称不应为空");
            assertFalse(payType.getImpl().trim().isEmpty(), "实现类名称不应为空字符串");
        }
    }

    @Test
    @DisplayName("测试CategoryTypeEnum - byCode方法")
    void testCategoryTypeEnum_ByCode() {
        // Test valid codes
        assertEquals(CategoryTypeEnum.COURSE, CategoryTypeEnum.byCode(1));
        assertEquals(CategoryTypeEnum.RESOURCE, CategoryTypeEnum.byCode(2));
        
        // Test invalid codes
        assertNull(CategoryTypeEnum.byCode(null));
        assertNull(CategoryTypeEnum.byCode(0));
        assertNull(CategoryTypeEnum.byCode(999));
    }

    @Test
    @DisplayName("测试ConfigTypeEnum - byCode方法")
    void testConfigTypeEnum_ByCode() {
        // Test valid codes
        assertEquals(ConfigTypeEnum.SYSTEM, ConfigTypeEnum.byCode(1));
        assertEquals(ConfigTypeEnum.PAY, ConfigTypeEnum.byCode(6));
        
        // Test invalid codes
        assertNull(ConfigTypeEnum.byCode(null));
        assertNull(ConfigTypeEnum.byCode(0));
        assertNull(ConfigTypeEnum.byCode(999));
    }

    @Test
    @DisplayName("测试VodPlatformEnum - byCode方法")
    void testVodPlatformEnum_ByCode() {
        // Test valid codes
        assertEquals(VodPlatformEnum.PRIVATEY, VodPlatformEnum.byCode(1));
        assertEquals(VodPlatformEnum.POLYV, VodPlatformEnum.byCode(2));
        assertEquals(VodPlatformEnum.BAIJY, VodPlatformEnum.byCode(3));
        assertEquals(VodPlatformEnum.BOKECC, VodPlatformEnum.byCode(4));
        
        // Test invalid codes
        assertNull(VodPlatformEnum.byCode(null));
        assertNull(VodPlatformEnum.byCode(0));
        assertNull(VodPlatformEnum.byCode(999));
    }

    @Test
    @DisplayName("测试VodPlatformEnum - 标签属性")
    void testVodPlatformEnum_TagProperties() {
        assertEquals("priy%", VodPlatformEnum.PRIVATEY.getTag());
        assertEquals("polyv%", VodPlatformEnum.POLYV.getTag());
        assertEquals("baijy%", VodPlatformEnum.BAIJY.getTag());
        assertEquals("bokecc%", VodPlatformEnum.BOKECC.getTag());
    }

    @Test
    @DisplayName("测试颜色属性枚举")
    void testColorEnums() {
        // Test BuyTypeEnum
        assertEquals("", BuyTypeEnum.BUY.getColor());
        assertEquals("red", BuyTypeEnum.FREE.getColor());
        
        // Test FreeEnum
        assertEquals("", FreeEnum.FREE.getColor());
        assertEquals("red", FreeEnum.CHARGE.getColor());
        
        // Test PutawayEnum
        assertEquals("", PutawayEnum.UP.getColor());
        assertEquals("red", PutawayEnum.DOWN.getColor());
        
        // Test VideoStatusEnum
        assertEquals("", VideoStatusEnum.WAIT.getColor());
        assertEquals("green", VideoStatusEnum.SUCCES.getColor());
        assertEquals("red", VideoStatusEnum.FINAL.getColor());
    }

    @Test
    @DisplayName("测试枚举值唯一性")
    void testEnumUniqueness() {
        // Test LoginStatusEnum codes are unique
        java.util.Set<Integer> loginStatusCodes = java.util.Arrays.stream(LoginStatusEnum.values())
                .map(LoginStatusEnum::getCode)
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(LoginStatusEnum.values().length, loginStatusCodes.size(), 
                "LoginStatusEnum代码应该唯一");
        
        // Test OrderStatusEnum codes are unique
        java.util.Set<Integer> orderStatusCodes = java.util.Arrays.stream(OrderStatusEnum.values())
                .map(OrderStatusEnum::getCode)
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(OrderStatusEnum.values().length, orderStatusCodes.size(), 
                "OrderStatusEnum代码应该唯一");
        
        // Test PayTypeEnum codes are unique
        java.util.Set<Integer> payTypeCodes = java.util.Arrays.stream(PayTypeEnum.values())
                .map(PayTypeEnum::getCode)
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(PayTypeEnum.values().length, payTypeCodes.size(), 
                "PayTypeEnum代码应该唯一");
    }

    @Test
    @DisplayName("测试ResourceTypeEnum属性")
    void testResourceTypeEnum() {
        assertEquals(Integer.valueOf(1), ResourceTypeEnum.VIDEO.getCode());
        assertEquals("视频", ResourceTypeEnum.VIDEO.getDesc());
        
        assertEquals(Integer.valueOf(2), ResourceTypeEnum.AUDIO.getCode());
        assertEquals("音频", ResourceTypeEnum.AUDIO.getDesc());
        
        assertEquals(Integer.valueOf(3), ResourceTypeEnum.DOC.getCode());
        assertEquals("文档", ResourceTypeEnum.DOC.getDesc());
    }

    @Test
    @DisplayName("测试TradeTypeEnum属性")
    void testTradeTypeEnum() {
        assertEquals(Integer.valueOf(1), TradeTypeEnum.ONLINE.getCode());
        assertEquals("线上支付", TradeTypeEnum.ONLINE.getDesc());
        
        assertEquals(Integer.valueOf(2), TradeTypeEnum.OFFLINE.getCode());
        assertEquals("线下支付", TradeTypeEnum.OFFLINE.getDesc());
    }

    @Test
    @DisplayName("测试FileTypeEnum属性")
    void testFileTypeEnum() {
        assertEquals(Integer.valueOf(1), FileTypeEnum.VIDEO.getCode());
        assertEquals("视频", FileTypeEnum.VIDEO.getDesc());
        
        assertEquals(Integer.valueOf(2), FileTypeEnum.FILE.getCode());
        assertEquals("文件", FileTypeEnum.FILE.getDesc());
    }

    @Test
    @DisplayName("测试枚举边界值")
    void testEnumBoundaryValues() {
        // 测试所有枚举的code都不为负数
        for (LoginStatusEnum status : LoginStatusEnum.values()) {
            assertTrue(status.getCode() >= 0, "LoginStatusEnum代码不应为负数");
        }
        
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            assertTrue(status.getCode() > 0, "OrderStatusEnum代码应为正数");
        }
        
        for (PayTypeEnum payType : PayTypeEnum.values()) {
            assertTrue(payType.getCode() > 0, "PayTypeEnum代码应为正数");
        }
        
        // 测试所有枚举的描述都不为空
        for (ResourceTypeEnum resourceType : ResourceTypeEnum.values()) {
            assertNotNull(resourceType.getDesc(), "ResourceTypeEnum描述不应为空");
            assertFalse(resourceType.getDesc().trim().isEmpty(), "ResourceTypeEnum描述不应为空字符串");
        }
    }
}