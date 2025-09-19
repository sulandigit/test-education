/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 基础测试类
 * 提供Web层测试的基础设施和通用方法
 *
 * @author Test Framework
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {

    /**
     * 默认测试用户ID
     */
    protected static final Long DEFAULT_USER_ID = 1L;
    
    /**
     * 默认管理员用户ID
     */
    protected static final Long DEFAULT_ADMIN_ID = 1000L;

    /**
     * MockMvc实例，用于Web层测试
     */
    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * 测试前置方法，初始化MockMvc
     */
    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(buildDefaultRequest())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    /**
     * 构建默认的Mock请求
     *
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildDefaultRequest() {
        return MockMvcRequestBuilders.post("/")
                .accept("application/json;charset=UTF-8")
                .header("userId", DEFAULT_USER_ID)
                .contentType("application/json");
    }

    /**
     * 构建Mock请求，带自定义用户ID
     *
     * @param userId 用户ID
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildMockRequest(Long userId) {
        return MockMvcRequestBuilders.post("/")
                .accept("application/json;charset=UTF-8")
                .header("userId", userId)
                .contentType("application/json");
    }

    /**
     * 构建GET请求
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildGetRequest(String url) {
        return MockMvcRequestBuilders.get(url)
                .accept("application/json;charset=UTF-8")
                .header("userId", DEFAULT_USER_ID);
    }

    /**
     * 构建POST请求
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildPostRequest(String url) {
        return MockMvcRequestBuilders.post(url)
                .accept("application/json;charset=UTF-8")
                .header("userId", DEFAULT_USER_ID)
                .contentType("application/json");
    }

    /**
     * 构建PUT请求
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildPutRequest(String url) {
        return MockMvcRequestBuilders.put(url)
                .accept("application/json;charset=UTF-8")
                .header("userId", DEFAULT_USER_ID)
                .contentType("application/json");
    }

    /**
     * 构建DELETE请求
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder buildDeleteRequest(String url) {
        return MockMvcRequestBuilders.delete(url)
                .accept("application/json;charset=UTF-8")
                .header("userId", DEFAULT_USER_ID);
    }
}