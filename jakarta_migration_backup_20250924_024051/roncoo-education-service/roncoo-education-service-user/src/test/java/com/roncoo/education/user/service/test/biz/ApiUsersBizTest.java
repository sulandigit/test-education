/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.biz;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.roncoo.education.common.cache.CacheRedis;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.common.core.tools.Constants;
import com.roncoo.education.common.core.tools.JWTUtil;
import com.roncoo.education.system.feign.interfaces.IFeignSysConfig;
import com.roncoo.education.user.dao.LogLoginDao;
import com.roncoo.education.user.dao.UsersDao;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import com.roncoo.education.user.service.api.biz.ApiUsersBiz;
import com.roncoo.education.user.service.api.req.LoginReq;
import com.roncoo.education.user.service.api.req.PasswordReq;
import com.roncoo.education.user.service.api.req.RegisterReq;
import com.roncoo.education.user.service.api.req.SendCodeReq;
import com.roncoo.education.user.service.api.resp.UsersLoginResp;
import com.roncoo.education.user.service.test.base.BaseBizTest;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import com.roncoo.education.user.service.test.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ApiUsersBiz 单元测试
 * 测试用户API业务逻辑层的各种场景
 *
 * @author Test Framework
 */
@DisplayName("用户API业务逻辑层测试")
class ApiUsersBizTest extends BaseBizTest {

    @Mock
    private UsersDao userDao;

    @Mock
    private LogLoginDao logLoginDao;

    @Mock
    private CacheRedis cacheRedis;

    @Mock
    private IFeignSysConfig feignSysConfig;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ApiUsersBiz apiUsersBiz;

    private Users testUser;
    private String testMobile;
    private String testPassword;

    @BeforeEach
    void setUp() {
        super.setUp();
        testMobile = TestUtils.generateTestMobile(1001);
        testPassword = TestUtils.generateTestPassword();
        testUser = TestDataFactory.createStandardUser(1001L);
        testUser.setMobile(testMobile);
    }

    @Test
    @DisplayName("测试用户注册 - 正常情况")
    void testRegister_Success() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword(testPassword);

        // Mock验证码校验
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("123456");
        // Mock手机号不存在
        when(userDao.getByMobile(testMobile)).thenReturn(null);
        // Mock保存用户
        when(userDao.save(any(Users.class))).thenReturn(1);
        // Mock保存日志
        when(logLoginDao.save(any())).thenReturn(1);

        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.create(anyLong(), any())).thenReturn("test-token");

            // When
            Result<UsersLoginResp> result = apiUsersBiz.register(req);

            // Then
            assertTrue(result.getSuccess(), "注册应该成功");
            assertNotNull(result.getData(), "返回数据不应为空");
            assertEquals(testMobile, result.getData().getMobile(), "手机号应该匹配");
            assertEquals("test-token", result.getData().getToken(), "Token应该匹配");

            // 验证方法调用
            verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
            verify(cacheRedis).delete(Constants.RedisPre.CODE + testMobile);
            verify(userDao).getByMobile(testMobile);
            verify(userDao).save(any(Users.class));
            verify(logLoginDao).save(any());
        }
    }

    @Test
    @DisplayName("测试用户注册 - 手机号为空")
    void testRegister_EmptyMobile() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile("");
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword(testPassword);

        // When
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertFalse(result.getSuccess(), "注册应该失败");
        assertEquals("手机号不能为空", result.getMsg(), "错误信息应该匹配");

        // 验证不应该调用其他方法
        verify(cacheRedis, never()).get(anyString());
        verify(userDao, never()).getByMobile(anyString());
    }

    @Test
    @DisplayName("测试用户注册 - 验证码已过期")
    void testRegister_CodeExpired() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword(testPassword);

        // Mock验证码不存在（已过期）
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn(null);

        // When
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertFalse(result.getSuccess(), "注册应该失败");
        assertEquals("验证码已经过期", result.getMsg(), "错误信息应该匹配");

        verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
        verify(userDao, never()).getByMobile(anyString());
    }

    @Test
    @DisplayName("测试用户注册 - 验证码不正确")
    void testRegister_WrongCode() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword(testPassword);

        // Mock验证码不匹配
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("654321");

        // When
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertFalse(result.getSuccess(), "注册应该失败");
        assertEquals("验证码不正确", result.getMsg(), "错误信息应该匹配");

        verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
        verify(userDao, never()).getByMobile(anyString());
    }

    @Test
    @DisplayName("测试用户注册 - 密码不一致")
    void testRegister_PasswordMismatch() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword("different_password");

        // Mock验证码校验通过
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("123456");

        // When
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertFalse(result.getSuccess(), "注册应该失败");
        assertEquals("密码不一致", result.getMsg(), "错误信息应该匹配");

        verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
        verify(userDao, never()).getByMobile(anyString());
    }

    @Test
    @DisplayName("测试用户注册 - 手机号已存在")
    void testRegister_MobileExists() {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd(testPassword);
        req.setRepassword(testPassword);

        // Mock验证码校验通过
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("123456");
        // Mock手机号已存在
        when(userDao.getByMobile(testMobile)).thenReturn(testUser);

        // When
        Result<UsersLoginResp> result = apiUsersBiz.register(req);

        // Then
        assertFalse(result.getSuccess(), "注册应该失败");
        assertEquals("该手机号已经注册，请更换手机号", result.getMsg(), "错误信息应该匹配");

        verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
        verify(cacheRedis).delete(Constants.RedisPre.CODE + testMobile);
        verify(userDao).getByMobile(testMobile);
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("测试用户登录 - 正常情况")
    void testLogin_Success() {
        // Given
        LoginReq req = new LoginReq();
        req.setMobile(testMobile);
        req.setPassword(testPassword);

        // 设置用户密码加密信息
        String salt = "test_salt";
        testUser.setMobileSalt(salt);
        testUser.setMobilePsw(DigestUtil.sha1Hex(salt + testPassword));

        // Mock用户存在
        when(userDao.getByMobile(testMobile)).thenReturn(testUser);
        // Mock缓存操作
        when(cacheRedis.set(anyString(), any(), anyInt(), any(TimeUnit.class))).thenReturn(1);
        // Mock日志保存
        when(logLoginDao.save(any())).thenReturn(1);

        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.create(anyLong(), any())).thenReturn("login-token");

            // When
            Result<UsersLoginResp> result = apiUsersBiz.login(req);

            // Then
            assertTrue(result.getSuccess(), "登录应该成功");
            assertNotNull(result.getData(), "返回数据不应为空");
            assertEquals(testMobile, result.getData().getMobile(), "手机号应该匹配");
            assertEquals("login-token", result.getData().getToken(), "Token应该匹配");

            verify(userDao).getByMobile(testMobile);
            verify(cacheRedis).set(anyString(), eq(testUser.getId()), eq(1), eq(TimeUnit.DAYS));
        }
    }

    @Test
    @DisplayName("测试用户登录 - 用户不存在")
    void testLogin_UserNotFound() {
        // Given
        LoginReq req = new LoginReq();
        req.setMobile(testMobile);
        req.setPassword(testPassword);

        // Mock用户不存在
        when(userDao.getByMobile(testMobile)).thenReturn(null);

        // When
        Result<UsersLoginResp> result = apiUsersBiz.login(req);

        // Then
        assertFalse(result.getSuccess(), "登录应该失败");
        assertEquals("账号或者密码不正确", result.getMsg(), "错误信息应该匹配");

        verify(userDao).getByMobile(testMobile);
        verify(cacheRedis, never()).set(anyString(), any(), anyInt(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("测试用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        // Given
        LoginReq req = new LoginReq();
        req.setMobile(testMobile);
        req.setPassword("wrong_password");

        // 设置用户密码加密信息
        String salt = "test_salt";
        testUser.setMobileSalt(salt);
        testUser.setMobilePsw(DigestUtil.sha1Hex(salt + testPassword));

        // Mock用户存在
        when(userDao.getByMobile(testMobile)).thenReturn(testUser);
        // Mock保存登录失败日志
        when(logLoginDao.save(any())).thenReturn(1);

        // When
        Result<UsersLoginResp> result = apiUsersBiz.login(req);

        // Then
        assertFalse(result.getSuccess(), "登录应该失败");
        assertEquals("账号或者密码不正确", result.getMsg(), "错误信息应该匹配");

        verify(userDao).getByMobile(testMobile);
        verify(logLoginDao).save(any()); // 应该记录登录失败日志
        verify(cacheRedis, never()).set(anyString(), any(), anyInt(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("测试发送验证码 - 正常情况")
    void testSendCode_Success() {
        // Given
        SendCodeReq req = new SendCodeReq();
        req.setMobile(testMobile);

        // Mock验证码发送次数检查通过
        when(cacheRedis.get(Constants.RedisPre.CODE_STAT + testMobile)).thenReturn(null);
        when(cacheRedis.set(eq(Constants.RedisPre.CODE_STAT + testMobile), eq(1), eq(5), eq(TimeUnit.MINUTES))).thenReturn(1);
        when(cacheRedis.set(eq(Constants.RedisPre.CODE + testMobile), anyString(), eq(5), eq(TimeUnit.MINUTES))).thenReturn(1);

        try (MockedStatic<IdUtil> idUtilMock = Mockito.mockStatic(IdUtil.class)) {
            // When
            Result<String> result = apiUsersBiz.sendCode(req);

            // Then
            assertTrue(result.getSuccess(), "发送验证码应该成功");
            assertEquals("验证码发送成功，请查收", result.getData(), "返回信息应该匹配");

            verify(cacheRedis).get(Constants.RedisPre.CODE_STAT + testMobile);
            verify(cacheRedis).set(eq(Constants.RedisPre.CODE_STAT + testMobile), eq(1), eq(5), eq(TimeUnit.MINUTES));
            verify(cacheRedis).set(eq(Constants.RedisPre.CODE + testMobile), anyString(), eq(5), eq(TimeUnit.MINUTES));
        }
    }

    @Test
    @DisplayName("测试密码重置 - 正常情况")
    void testPassword_Success() {
        // Given
        PasswordReq req = new PasswordReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd("new_password");
        req.setRepassword("new_password");

        // Mock验证码校验通过
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("123456");
        // Mock用户存在
        when(userDao.getByMobile(testMobile)).thenReturn(testUser);
        // Mock更新用户
        when(userDao.updateById(any(Users.class))).thenReturn(1);

        try (MockedStatic<IdUtil> idUtilMock = Mockito.mockStatic(IdUtil.class)) {
            idUtilMock.when(IdUtil::simpleUUID).thenReturn("new_salt");

            // When
            Result<String> result = apiUsersBiz.password(req);

            // Then
            assertTrue(result.getSuccess(), "密码重置应该成功");
            assertEquals("重置成功", result.getData(), "返回信息应该匹配");

            verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
            verify(cacheRedis).delete(Constants.RedisPre.CODE + testMobile);
            verify(userDao).getByMobile(testMobile);
            verify(userDao).updateById(any(Users.class));
        }
    }

    @Test
    @DisplayName("测试密码重置 - 用户不存在")
    void testPassword_UserNotFound() {
        // Given
        PasswordReq req = new PasswordReq();
        req.setMobile(testMobile);
        req.setCode("123456");
        req.setMobilePwd("new_password");
        req.setRepassword("new_password");

        // Mock验证码校验通过
        when(cacheRedis.get(Constants.RedisPre.CODE + testMobile)).thenReturn("123456");
        // Mock用户不存在
        when(userDao.getByMobile(testMobile)).thenReturn(null);

        // When
        Result<String> result = apiUsersBiz.password(req);

        // Then
        assertFalse(result.getSuccess(), "密码重置应该失败");
        assertEquals("该手机号没注册，请先注册", result.getMsg(), "错误信息应该匹配");

        verify(cacheRedis).get(Constants.RedisPre.CODE + testMobile);
        verify(cacheRedis).delete(Constants.RedisPre.CODE + testMobile);
        verify(userDao).getByMobile(testMobile);
        verify(userDao, never()).updateById(any());
    }
}