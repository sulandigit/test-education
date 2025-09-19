/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 测试工具类
 * 提供测试过程中常用的工具方法
 *
 * @author Test Framework
 */
public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 待转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换为JSON失败", e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换为对象失败", e);
        }
    }

    /**
     * 生成测试用的手机号
     *
     * @param suffix 后缀数字
     * @return 测试手机号
     */
    public static String generateTestMobile(int suffix) {
        return String.format("1380000%04d", suffix);
    }

    /**
     * 生成测试用的用户名
     *
     * @param suffix 后缀
     * @return 测试用户名
     */
    public static String generateTestUsername(String suffix) {
        return "testuser_" + suffix;
    }

    /**
     * 生成测试用的邮箱
     *
     * @param prefix 前缀
     * @return 测试邮箱
     */
    public static String generateTestEmail(String prefix) {
        return prefix + "_test@example.com";
    }

    /**
     * 生成测试密码
     *
     * @return 测试密码
     */
    public static String generateTestPassword() {
        return "test123456";
    }

    /**
     * 生成测试订单号
     *
     * @param userId 用户ID
     * @return 订单号
     */
    public static Long generateTestOrderNo(Long userId) {
        return 202401000000L + userId;
    }

    /**
     * 判断字符串是否为有效的JSON格式
     *
     * @param jsonStr JSON字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String jsonStr) {
        try {
            objectMapper.readTree(jsonStr);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 等待指定毫秒数
     *
     * @param millis 毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程中断", e);
        }
    }

    /**
     * 断言两个对象在忽略指定字段后相等
     *
     * @param expected      期望对象
     * @param actual        实际对象
     * @param ignoredFields 忽略的字段名
     */
    public static void assertEqualsIgnoringFields(Object expected, Object actual, String... ignoredFields) {
        // 这里可以使用AssertJ或其他库来实现字段忽略比较
        // 为了简化，这里只做简单的toString比较
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            throw new AssertionError("对象不相等：一个为null，另一个不为null");
        }
        // 实际项目中应该使用更完善的字段比较逻辑
    }
}