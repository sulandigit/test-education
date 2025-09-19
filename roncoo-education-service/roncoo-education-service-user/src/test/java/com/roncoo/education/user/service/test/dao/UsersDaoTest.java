/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.user.dao.UsersDao;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import com.roncoo.education.user.dao.impl.mapper.entity.UsersExample;
import com.roncoo.education.user.service.test.base.BaseDaoTest;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UsersDao 单元测试
 * 测试用户数据访问层的CRUD操作、复杂查询和边界条件
 *
 * @author Test Framework
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("用户数据访问层测试")
class UsersDaoTest extends BaseDaoTest {

    @Autowired
    private UsersDao usersDao;

    @Test
    @DisplayName("测试保存用户 - 正常情况")
    void testSave_Normal() {
        // Given
        Users user = TestDataFactory.createStandardUser(2001L);
        
        // When
        int result = usersDao.save(user);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "保存操作应该返回1");
        assertNotNull(user.getId(), "保存后用户ID不应为空");
        
        // 验证数据是否真正保存到数据库
        Users savedUser = usersDao.getById(user.getId());
        assertNotNull(savedUser, "从数据库查询的用户不应为空");
        assertEquals(user.getMobile(), savedUser.getMobile(), "手机号应该相同");
        assertEquals(user.getNickname(), savedUser.getNickname(), "昵称应该相同");
    }

    @Test
    @DisplayName("测试保存用户 - ID为空自动生成")
    void testSave_WithNullId() {
        // Given
        Users user = TestDataFactory.createStandardUser(null);
        user.setId(null); // 确保ID为空
        
        // When
        int result = usersDao.save(user);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "保存操作应该返回1");
        assertNotNull(user.getId(), "保存后应该自动生成ID");
        assertTrue(user.getId() > 0, "自动生成的ID应该大于0");
    }

    @Test
    @DisplayName("测试根据ID查询用户 - 存在的用户")
    void testGetById_ExistingUser() {
        // Given
        Users user = TestDataFactory.createStandardUser(2002L);
        usersDao.save(user);
        flushAndClear();
        
        // When
        Users foundUser = usersDao.getById(user.getId());
        
        // Then
        assertNotNull(foundUser, "查询结果不应为空");
        assertEquals(user.getId(), foundUser.getId(), "用户ID应该相同");
        assertEquals(user.getMobile(), foundUser.getMobile(), "手机号应该相同");
        assertEquals(user.getNickname(), foundUser.getNickname(), "昵称应该相同");
    }

    @Test
    @DisplayName("测试根据ID查询用户 - 不存在的用户")
    void testGetById_NonExistingUser() {
        // Given
        Long nonExistingId = 99999L;
        
        // When
        Users foundUser = usersDao.getById(nonExistingId);
        
        // Then
        assertNull(foundUser, "不存在的用户查询结果应为空");
    }

    @Test
    @DisplayName("测试根据手机号查询用户 - 存在的手机号")
    void testGetByMobile_ExistingMobile() {
        // Given
        Users user = TestDataFactory.createStandardUser(2003L);
        usersDao.save(user);
        flushAndClear();
        
        // When
        Users foundUser = usersDao.getByMobile(user.getMobile());
        
        // Then
        assertNotNull(foundUser, "查询结果不应为空");
        assertEquals(user.getMobile(), foundUser.getMobile(), "手机号应该相同");
        assertEquals(user.getId(), foundUser.getId(), "用户ID应该相同");
    }

    @Test
    @DisplayName("测试根据手机号查询用户 - 不存在的手机号")
    void testGetByMobile_NonExistingMobile() {
        // Given
        String nonExistingMobile = "19999999999";
        
        // When
        Users foundUser = usersDao.getByMobile(nonExistingMobile);
        
        // Then
        assertNull(foundUser, "不存在的手机号查询结果应为空");
    }

    @Test
    @DisplayName("测试更新用户信息")
    void testUpdateById() {
        // Given
        Users user = TestDataFactory.createStandardUser(2004L);
        usersDao.save(user);
        flushAndClear();
        
        // When
        String newNickname = "更新后的昵称";
        user.setNickname(newNickname);
        int result = usersDao.updateById(user);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "更新操作应该返回1");
        
        // 验证更新是否生效
        Users updatedUser = usersDao.getById(user.getId());
        assertEquals(newNickname, updatedUser.getNickname(), "昵称应该已更新");
    }

    @Test
    @DisplayName("测试删除用户")
    void testDeleteById() {
        // Given
        Users user = TestDataFactory.createStandardUser(2005L);
        usersDao.save(user);
        flushAndClear();
        
        // 确认用户存在
        assertNotNull(usersDao.getById(user.getId()), "删除前用户应该存在");
        
        // When
        int result = usersDao.deleteById(user.getId());
        flushAndClear();
        
        // Then
        assertEquals(1, result, "删除操作应该返回1");
        assertNull(usersDao.getById(user.getId()), "删除后用户应该不存在");
    }

    @Test
    @DisplayName("测试分页查询用户")
    void testPage() {
        // Given
        List<Users> testUsers = TestDataFactory.createUsersBatch(2010L, 5);
        for (Users user : testUsers) {
            usersDao.save(user);
        }
        flushAndClear();
        
        // When
        UsersExample example = new UsersExample();
        example.createCriteria().andIdGreaterThanOrEqualTo(2010L);
        Page<Users> page = usersDao.page(1, 3, example);
        
        // Then
        assertNotNull(page, "分页结果不应为空");
        assertTrue(page.getTotal() >= 5, "总数应该至少为5");
        assertEquals(3, page.getList().size(), "当前页应该有3条记录");
        assertEquals(1, page.getPageCurrent(), "当前页应该为1");
        assertTrue(page.getTotalPage() >= 2, "总页数应该至少为2");
    }

    @Test
    @DisplayName("测试根据ID列表查询用户")
    void testListByIds() {
        // Given
        List<Users> testUsers = TestDataFactory.createUsersBatch(2020L, 3);
        for (Users user : testUsers) {
            usersDao.save(user);
        }
        flushAndClear();
        
        List<Long> userIds = Arrays.asList(2020L, 2021L, 2022L);
        
        // When
        List<Users> foundUsers = usersDao.listByIds(userIds);
        
        // Then
        assertNotNull(foundUsers, "查询结果不应为空");
        assertEquals(3, foundUsers.size(), "应该查询到3个用户");
        
        // 验证每个用户都在查询列表中
        for (Users user : foundUsers) {
            assertTrue(userIds.contains(user.getId()), "查询到的用户ID应该在请求列表中");
        }
    }

    @Test
    @DisplayName("测试根据空ID列表查询用户")
    void testListByIds_EmptyList() {
        // Given
        List<Long> emptyIds = Arrays.asList();
        
        // When
        List<Users> foundUsers = usersDao.listByIds(emptyIds);
        
        // Then
        assertNotNull(foundUsers, "查询结果不应为空");
        assertTrue(foundUsers.isEmpty(), "空ID列表查询结果应该为空");
    }

    @Test
    @DisplayName("测试分页查询边界条件")
    void testPage_BoundaryConditions() {
        // Given
        Users user = TestDataFactory.createStandardUser(2030L);
        usersDao.save(user);
        flushAndClear();
        
        // When - 查询第1页，每页10条
        UsersExample example = new UsersExample();
        example.createCriteria().andIdEqualTo(2030L);
        Page<Users> page = usersDao.page(1, 10, example);
        
        // Then
        assertNotNull(page, "分页结果不应为空");
        assertEquals(1, page.getTotal(), "总数应该为1");
        assertEquals(1, page.getList().size(), "当前页应该有1条记录");
        assertEquals(1, page.getPageCurrent(), "当前页应该为1");
        assertEquals(1, page.getTotalPage(), "总页数应该为1");
    }

    @Test
    @DisplayName("测试保存用户时的必填字段验证")
    void testSave_RequiredFields() {
        // Given
        Users user = new Users();
        user.setId(2040L);
        user.setMobile("13800002040"); // 必填字段
        
        // When & Then
        assertDoesNotThrow(() -> {
            int result = usersDao.save(user);
            assertEquals(1, result, "保存操作应该成功");
        }, "保存包含必填字段的用户应该成功");
    }
}