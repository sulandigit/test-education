/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.controller;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.user.service.api.ApiUsersController;
import com.roncoo.education.user.service.api.biz.ApiUsersBiz;
import com.roncoo.education.user.service.api.req.LoginReq;
import com.roncoo.education.user.service.api.req.PasswordReq;
import com.roncoo.education.user.service.api.req.RegisterReq;
import com.roncoo.education.user.service.api.req.SendCodeReq;
import com.roncoo.education.user.service.api.resp.UsersLoginResp;
import com.roncoo.education.user.service.test.base.BaseTest;
import com.roncoo.education.user.service.test.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ApiUsersController 单元测试
 * 测试用户API控制器的HTTP请求处理
 *
 * @author Test Framework
 */
@WebMvcTest(ApiUsersController.class)
@DisplayName("用户API控制器测试")
class ApiUsersControllerTest extends BaseTest {

    @MockBean
    private ApiUsersBiz apiUsersBiz;

    @Test
    @DisplayName("测试发送验证码接口 - 正常情况")
    void testSendCode_Success() throws Exception {
        // Given
        SendCodeReq req = new SendCodeReq();
        req.setMobile("13800001001");

        Result<String> mockResult = Result.success("验证码发送成功，请查收");
        when(apiUsersBiz.sendCode(any(SendCodeReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/send/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("验证码发送成功，请查收"));

        verify(apiUsersBiz).sendCode(any(SendCodeReq.class));
    }

    @Test
    @DisplayName("测试发送验证码接口 - 业务失败")
    void testSendCode_BusinessError() throws Exception {
        // Given
        SendCodeReq req = new SendCodeReq();
        req.setMobile("13800001001");

        Result<String> mockResult = Result.error("验证码发送失败，请稍后再试");
        when(apiUsersBiz.sendCode(any(SendCodeReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/send/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("验证码发送失败，请稍后再试"));

        verify(apiUsersBiz).sendCode(any(SendCodeReq.class));
    }

    @Test
    @DisplayName("测试用户注册接口 - 正常情况")
    void testRegister_Success() throws Exception {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile("13800001002");
        req.setCode("123456");
        req.setMobilePwd("test123456");
        req.setRepassword("test123456");

        UsersLoginResp loginResp = new UsersLoginResp();
        loginResp.setMobile("13800001002");
        loginResp.setToken("test-token-12345");

        Result<UsersLoginResp> mockResult = Result.success(loginResp);
        when(apiUsersBiz.register(any(RegisterReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mobile").value("13800001002"))
                .andExpect(jsonPath("$.data.token").value("test-token-12345"));

        verify(apiUsersBiz).register(any(RegisterReq.class));
    }

    @Test
    @DisplayName("测试用户注册接口 - 参数验证失败")
    void testRegister_ValidationError() throws Exception {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile(""); // 空手机号
        req.setCode("123456");
        req.setMobilePwd("test123456");
        req.setRepassword("test123456");

        Result<UsersLoginResp> mockResult = Result.error("手机号不能为空");
        when(apiUsersBiz.register(any(RegisterReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("手机号不能为空"));

        verify(apiUsersBiz).register(any(RegisterReq.class));
    }

    @Test
    @DisplayName("测试用户登录接口 - 正常情况")
    void testLogin_Success() throws Exception {
        // Given
        LoginReq req = new LoginReq();
        req.setMobile("13800001003");
        req.setPassword("test123456");

        UsersLoginResp loginResp = new UsersLoginResp();
        loginResp.setMobile("13800001003");
        loginResp.setToken("login-token-67890");

        Result<UsersLoginResp> mockResult = Result.success(loginResp);
        when(apiUsersBiz.login(any(LoginReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mobile").value("13800001003"))
                .andExpect(jsonPath("$.data.token").value("login-token-67890"));

        verify(apiUsersBiz).login(any(LoginReq.class));
    }

    @Test
    @DisplayName("测试用户登录接口 - 认证失败")
    void testLogin_AuthenticationFailed() throws Exception {
        // Given
        LoginReq req = new LoginReq();
        req.setMobile("13800001003");
        req.setPassword("wrong_password");

        Result<UsersLoginResp> mockResult = Result.error("账号或者密码不正确");
        when(apiUsersBiz.login(any(LoginReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("账号或者密码不正确"));

        verify(apiUsersBiz).login(any(LoginReq.class));
    }

    @Test
    @DisplayName("测试密码重置接口 - 正常情况")
    void testPassword_Success() throws Exception {
        // Given
        PasswordReq req = new PasswordReq();
        req.setMobile("13800001004");
        req.setCode("123456");
        req.setMobilePwd("new_password123");
        req.setRepassword("new_password123");

        Result<String> mockResult = Result.success("重置成功");
        when(apiUsersBiz.password(any(PasswordReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("重置成功"));

        verify(apiUsersBiz).password(any(PasswordReq.class));
    }

    @Test
    @DisplayName("测试密码重置接口 - 验证码错误")
    void testPassword_WrongCode() throws Exception {
        // Given
        PasswordReq req = new PasswordReq();
        req.setMobile("13800001004");
        req.setCode("wrong_code");
        req.setMobilePwd("new_password123");
        req.setRepassword("new_password123");

        Result<String> mockResult = Result.error("验证码不正确");
        when(apiUsersBiz.password(any(PasswordReq.class))).thenReturn(mockResult);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("验证码不正确"));

        verify(apiUsersBiz).password(any(PasswordReq.class));
    }

    @Test
    @DisplayName("测试请求格式错误")
    void testInvalidJsonFormat() throws Exception {
        // Given - Invalid JSON
        String invalidJson = "{invalid json}";

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(apiUsersBiz, never()).register(any(RegisterReq.class));
    }

    @Test
    @DisplayName("测试不支持的HTTP方法")
    void testUnsupportedHttpMethod() throws Exception {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile("13800001005");

        // When & Then - GET method not supported for register
        mvc.perform(MockMvcRequestBuilders.get("/user/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        verify(apiUsersBiz, never()).register(any(RegisterReq.class));
    }

    @Test
    @DisplayName("测试Content-Type不正确")
    void testWrongContentType() throws Exception {
        // Given
        RegisterReq req = new RegisterReq();
        req.setMobile("13800001006");

        // When & Then - Wrong content type
        mvc.perform(MockMvcRequestBuilders.post("/user/api/users/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(TestUtils.toJson(req)))
                .andExpect(status().isUnsupportedMediaType());

        verify(apiUsersBiz, never()).register(any(RegisterReq.class));
    }
}