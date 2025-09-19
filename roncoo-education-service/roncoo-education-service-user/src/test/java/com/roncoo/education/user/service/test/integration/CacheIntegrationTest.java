/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.integration;

import com.roncoo.education.common.cache.CacheRedis;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.user.dao.UsersDao;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import com.roncoo.education.user.service.api.biz.ApiUsersBiz;
import com.roncoo.education.user.service.api.req.LoginReq;
import com.roncoo.education.user.service.api.req.RegisterReq;
import com.roncoo.education.user.service.api.req.SendCodeReq;
import com.roncoo.education.user.service.api.resp.UsersLoginResp;
import com.roncoo.education.user.service.test.base.BaseTest;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import com.roncoo.education.user.service.test.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存机制集成测试
 * 测试Redis缓存在实际业务场景中的工作情况
 *
 * @author Test Framework
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("缓存机制集成测试")
class CacheIntegrationTest extends BaseTest {

    @Autowired
    private ApiUsersBiz apiUsersBiz;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private CacheRedis cacheRedis;

    @Test
    @DisplayName("测试验证码缓存机制")
    void testVerificationCodeCache() {
        // Given
        String mobile = TestUtils.generateTestMobile(5001);
        SendCodeReq req = new SendCodeReq();
        req.setMobile(mobile);

        // When - 发送验证码
        Result<String> result = apiUsersBiz.sendCode(req);

        // Then
        assertTrue(result.getSuccess(), "验证码发送应该成功");

        // 验证缓存中存在验证码
        String cacheKey = "code::" + mobile;
        String cachedCode = cacheRedis.get(cacheKey);
        assertNotNull(cachedCode, "缓存中应该存在验证码");
        assertEquals(6, cachedCode.length(), "验证码长度应为6位");
        assertTrue(cachedCode.matches("\\d{6}"), "验证码应全为数字");

        // 验证发送次数统计缓存
        String statKey = "code::stat::" + mobile;
        String statCount = cacheRedis.get(statKey);
        assertNotNull(statCount, "缓存中应该存在发送次数统计");
        assertEquals("1", statCount, "首次发送，次数应为1");
    }

    @Test
    @DisplayName("测试用户注册过程中的缓存操作")
    void testUserRegistrationCacheOperations() {
        // Given
        String mobile = TestUtils.generateTestMobile(5002);
        String verCode = "123456";
        
        // 预先设置验证码缓存
        String codeKey = "code::" + mobile;
        cacheRedis.set(codeKey, verCode, 5, TimeUnit.MINUTES);

        RegisterReq req = new RegisterReq();
        req.setMobile(mobile);
        req.setCode(verCode);
        req.setMobilePwd("test123456");
        req.setRepassword("test123456");

        // When - 用户注册
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertTrue(result.getSuccess(), "用户注册应该成功");
        assertNotNull(result.getData(), "返回数据不应为空");
        assertNotNull(result.getData().getToken(), "应该返回登录token");

        // 验证验证码缓存已被清除
        String cachedCode = cacheRedis.get(codeKey);
        assertNull(cachedCode, "注册成功后验证码缓存应被清除");

        // 验证用户确实已保存到数据库
        Users savedUser = usersDao.getByMobile(mobile);
        assertNotNull(savedUser, "用户应该已保存到数据库");
        assertEquals(mobile, savedUser.getMobile(), "手机号应该匹配");
    }

    @Test
    @DisplayName("测试用户登录Token缓存")
    void testUserLoginTokenCache() {
        // Given - 先创建用户
        String mobile = TestUtils.generateTestMobile(5003);
        String password = "test123456";
        Users user = TestDataFactory.createStandardUser(5003L);
        user.setMobile(mobile);
        user.setMobileSalt("test_salt");
        user.setMobilePsw(cn.hutool.crypto.digest.DigestUtil.sha1Hex("test_salt" + password));
        usersDao.save(user);

        LoginReq req = new LoginReq();
        req.setMobile(mobile);
        req.setPassword(password);

        // When - 用户登录
        Result<UsersLoginResp> result = apiUsersBiz.login(req);

        // Then
        assertTrue(result.getSuccess(), "用户登录应该成功");
        assertNotNull(result.getData(), "返回数据不应为空");
        
        String token = result.getData().getToken();
        assertNotNull(token, "应该返回登录token");

        // 验证token已缓存
        Object cachedUserId = cacheRedis.get(token);
        assertNotNull(cachedUserId, "token应该已缓存");
        assertEquals(user.getId().toString(), cachedUserId.toString(), "缓存的用户ID应该匹配");
    }

    @Test
    @DisplayName("测试缓存过期机制")
    void testCacheExpiration() throws InterruptedException {
        // Given
        String mobile = TestUtils.generateTestMobile(5004);
        String testKey = "test::cache::" + mobile;
        String testValue = "test_value";

        // When - 设置短期缓存（1秒）
        cacheRedis.set(testKey, testValue, 1, TimeUnit.SECONDS);

        // Then - 立即验证缓存存在
        String cachedValue = cacheRedis.get(testKey);
        assertEquals(testValue, cachedValue, "缓存值应该匹配");

        // Wait for expiration
        Thread.sleep(1100); // 等待1.1秒确保缓存过期

        // 验证缓存已过期
        String expiredValue = cacheRedis.get(testKey);
        assertNull(expiredValue, "缓存应该已过期");
    }

    @Test
    @DisplayName("测试验证码发送频率限制")
    void testVerificationCodeRateLimit() {
        // Given
        String mobile = TestUtils.generateTestMobile(5005);
        SendCodeReq req = new SendCodeReq();
        req.setMobile(mobile);

        // When - 首次发送
        Result<String> result1 = apiUsersBiz.sendCode(req);
        assertTrue(result1.getSuccess(), "首次发送应该成功");

        // When - 第二次发送
        Result<String> result2 = apiUsersBiz.sendCode(req);
        assertTrue(result2.getSuccess(), "第二次发送应该成功");

        // When - 第三次发送（应该被限制）
        Result<String> result3 = apiUsersBiz.sendCode(req);
        assertFalse(result3.getSuccess(), "第三次发送应该失败（频率限制）");
        assertTrue(result3.getMsg().contains("验证码发送次数过多"), "错误信息应该包含频率限制提示");
    }

    @Test
    @DisplayName("测试缓存删除操作")
    void testCacheDelete() {
        // Given
        String testKey = "test::delete::key";
        String testValue = "test_delete_value";
        
        // 设置缓存
        cacheRedis.set(testKey, testValue, 10, TimeUnit.MINUTES);
        assertEquals(testValue, cacheRedis.get(testKey), "缓存应该已设置");

        // When - 删除缓存
        cacheRedis.delete(testKey);

        // Then - 验证缓存已删除
        String deletedValue = cacheRedis.get(testKey);
        assertNull(deletedValue, "缓存应该已删除");
    }

    @Test
    @DisplayName("测试缓存并发访问")
    void testConcurrentCacheAccess() throws InterruptedException {
        // Given
        String baseKey = "concurrent::test::";
        int threadCount = 5;
        int operationsPerThread = 10;
        
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);

        // When - 并发设置和读取缓存
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = baseKey + threadId + "::" + j;
                        String value = "thread_" + threadId + "_op_" + j;
                        
                        // 设置缓存
                        cacheRedis.set(key, value, 5, TimeUnit.MINUTES);
                        
                        // 读取缓存
                        String cachedValue = cacheRedis.get(key);
                        if (value.equals(cachedValue)) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Then
        latch.await(10, TimeUnit.SECONDS);
        int expectedSuccessCount = threadCount * operationsPerThread;
        assertEquals(expectedSuccessCount, successCount.get(), 
                "所有并发缓存操作都应该成功");
    }
}