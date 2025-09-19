/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.suite;

import com.roncoo.education.user.service.test.biz.ApiUsersBizTest;
import com.roncoo.education.user.service.test.controller.ApiUsersControllerTest;
import com.roncoo.education.user.service.test.dao.OrderInfoDaoTest;
import com.roncoo.education.user.service.test.dao.UsersDaoTest;
import com.roncoo.education.user.service.test.enums.EnumsTest;
import com.roncoo.education.user.service.test.integration.CacheIntegrationTest;
import com.roncoo.education.user.service.test.util.NOUtilTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 用户服务完整测试套件
 * 包含所有层级的单元测试和集成测试
 *
 * @author Test Framework
 */
@Suite
@SelectClasses({
    // Dao层测试
    UsersDaoTest.class,
    OrderInfoDaoTest.class,
    
    // Biz层测试
    ApiUsersBizTest.class,
    
    // Controller层测试
    ApiUsersControllerTest.class,
    
    // 工具类和枚举测试
    NOUtilTest.class,
    EnumsTest.class,
    
    // 集成测试
    CacheIntegrationTest.class
})
@DisplayName("用户服务完整测试套件")
public class UserServiceTestSuite {
    // 测试套件类，无需实现方法
    // JUnit 5会自动运行@SelectClasses指定的所有测试类
}