/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Biz层测试基类
 * 提供业务逻辑层测试的基础设施
 *
 * @author Test Framework
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseBizTest {

    /**
     * Mock对象关闭器
     */
    private AutoCloseable mockCloser;

    /**
     * 测试前置方法，初始化Mock对象
     */
    @BeforeEach
    public void setUp() {
        mockCloser = MockitoAnnotations.openMocks(this);
        initMocks();
    }

    /**
     * 测试后置方法，清理Mock对象
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (mockCloser != null) {
            mockCloser.close();
        }
        resetMocks();
    }

    /**
     * 初始化Mock对象
     * 子类可以重写此方法进行特定的Mock初始化
     */
    protected void initMocks() {
        // 默认实现为空，子类可以重写
    }

    /**
     * 重置Mock对象
     * 子类可以重写此方法进行特定的Mock重置
     */
    protected void resetMocks() {
        // 默认实现为空，子类可以重写
    }

    /**
     * 验证所有Mock对象的交互
     * 子类可以重写此方法进行特定的验证
     */
    protected void verifyMocks() {
        // 默认实现为空，子类可以重写
    }
}