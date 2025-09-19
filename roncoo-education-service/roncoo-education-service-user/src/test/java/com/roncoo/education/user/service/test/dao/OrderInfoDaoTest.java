/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.user.dao.OrderInfoDao;
import com.roncoo.education.user.dao.impl.mapper.entity.OrderInfo;
import com.roncoo.education.user.dao.impl.mapper.entity.OrderInfoExample;
import com.roncoo.education.user.service.test.base.BaseDaoTest;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import com.roncoo.education.user.service.test.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderInfoDao 单元测试
 * 测试订单信息数据访问层的CRUD操作和业务查询
 *
 * @author Test Framework
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("订单信息数据访问层测试")
class OrderInfoDaoTest extends BaseDaoTest {

    @Autowired
    private OrderInfoDao orderInfoDao;

    @Test
    @DisplayName("测试保存订单信息 - 正常情况")
    void testSave_Normal() {
        // Given
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3001L, 1001L);
        
        // When
        int result = orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "保存操作应该返回1");
        assertNotNull(orderInfo.getId(), "保存后订单ID不应为空");
        
        // 验证数据是否真正保存到数据库
        OrderInfo savedOrder = orderInfoDao.getById(orderInfo.getId());
        assertNotNull(savedOrder, "从数据库查询的订单不应为空");
        assertEquals(orderInfo.getOrderNo(), savedOrder.getOrderNo(), "订单号应该相同");
        assertEquals(orderInfo.getUserId(), savedOrder.getUserId(), "用户ID应该相同");
        assertEquals(orderInfo.getCourseId(), savedOrder.getCourseId(), "课程ID应该相同");
    }

    @Test
    @DisplayName("测试根据ID查询订单信息")
    void testGetById() {
        // Given
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3002L, 1002L);
        orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // When
        OrderInfo foundOrder = orderInfoDao.getById(orderInfo.getId());
        
        // Then
        assertNotNull(foundOrder, "查询结果不应为空");
        assertEquals(orderInfo.getId(), foundOrder.getId(), "订单ID应该相同");
        assertEquals(orderInfo.getOrderTitle(), foundOrder.getOrderTitle(), "订单标题应该相同");
        assertEquals(0, orderInfo.getOrderAmount().compareTo(foundOrder.getOrderAmount()), "订单金额应该相同");
    }

    @Test
    @DisplayName("测试更新订单信息")
    void testUpdateById() {
        // Given
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3003L, 1003L);
        orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // When
        Integer newOrderStatus = 2; // 已支付
        BigDecimal newPayAmount = new BigDecimal("199.00");
        orderInfo.setOrderStatus(newOrderStatus);
        orderInfo.setPayAmount(newPayAmount);
        int result = orderInfoDao.updateById(orderInfo);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "更新操作应该返回1");
        
        // 验证更新是否生效
        OrderInfo updatedOrder = orderInfoDao.getById(orderInfo.getId());
        assertEquals(newOrderStatus, updatedOrder.getOrderStatus(), "订单状态应该已更新");
        assertEquals(0, newPayAmount.compareTo(updatedOrder.getPayAmount()), "支付金额应该已更新");
    }

    @Test
    @DisplayName("测试删除订单信息")
    void testDeleteById() {
        // Given
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3004L, 1004L);
        orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // 确认订单存在
        assertNotNull(orderInfoDao.getById(orderInfo.getId()), "删除前订单应该存在");
        
        // When
        int result = orderInfoDao.deleteById(orderInfo.getId());
        flushAndClear();
        
        // Then
        assertEquals(1, result, "删除操作应该返回1");
        assertNull(orderInfoDao.getById(orderInfo.getId()), "删除后订单应该不存在");
    }

    @Test
    @DisplayName("测试分页查询订单信息")
    void testPage() {
        // Given
        Long userId = 1005L;
        for (int i = 0; i < 5; i++) {
            OrderInfo orderInfo = TestDataFactory.createOrderInfo(3010L + i, userId);
            orderInfoDao.save(orderInfo);
        }
        flushAndClear();
        
        // When
        OrderInfoExample example = new OrderInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        Page<OrderInfo> page = orderInfoDao.page(1, 3, example);
        
        // Then
        assertNotNull(page, "分页结果不应为空");
        assertEquals(5, page.getTotal(), "总数应该为5");
        assertEquals(3, page.getList().size(), "当前页应该有3条记录");
        assertEquals(1, page.getPageCurrent(), "当前页应该为1");
        assertEquals(2, page.getTotalPage(), "总页数应该为2");
    }

    @Test
    @DisplayName("测试根据条件查询订单列表")
    void testListByExample() {
        // Given
        Long userId = 1006L;
        OrderInfo orderInfo1 = TestDataFactory.createOrderInfo(3020L, userId);
        orderInfo1.setOrderStatus(1); // 待支付
        OrderInfo orderInfo2 = TestDataFactory.createOrderInfo(3021L, userId);
        orderInfo2.setOrderStatus(2); // 已支付
        OrderInfo orderInfo3 = TestDataFactory.createOrderInfo(3022L, userId);
        orderInfo3.setOrderStatus(1); // 待支付
        
        orderInfoDao.save(orderInfo1);
        orderInfoDao.save(orderInfo2);
        orderInfoDao.save(orderInfo3);
        flushAndClear();
        
        // When
        OrderInfoExample example = new OrderInfoExample();
        example.createCriteria().andUserIdEqualTo(userId).andOrderStatusEqualTo(1);
        List<OrderInfo> orders = orderInfoDao.listByExample(example);
        
        // Then
        assertNotNull(orders, "查询结果不应为空");
        assertEquals(2, orders.size(), "应该查询到2个待支付订单");
        for (OrderInfo order : orders) {
            assertEquals(userId, order.getUserId(), "用户ID应该匹配");
            assertEquals(Integer.valueOf(1), order.getOrderStatus(), "订单状态应该为待支付");
        }
    }

    @Test
    @DisplayName("测试根据条件统计订单数量")
    void testCountByExample() {
        // Given
        Long userId = 1007L;
        for (int i = 0; i < 3; i++) {
            OrderInfo orderInfo = TestDataFactory.createOrderInfo(3030L + i, userId);
            orderInfoDao.save(orderInfo);
        }
        flushAndClear();
        
        // When
        OrderInfoExample example = new OrderInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        int count = orderInfoDao.countByExample(example);
        
        // Then
        assertEquals(3, count, "应该统计到3个订单");
    }

    @Test
    @DisplayName("测试根据用户ID和课程ID查询订单")
    void testGetByUserAndCourseId() {
        // Given
        Long userId = 1008L;
        Long courseId = 2008L;
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3040L, userId);
        orderInfo.setCourseId(courseId);
        orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // When
        OrderInfo foundOrder = orderInfoDao.getByUserAndCourseId(userId, courseId);
        
        // Then
        assertNotNull(foundOrder, "查询结果不应为空");
        assertEquals(userId, foundOrder.getUserId(), "用户ID应该匹配");
        assertEquals(courseId, foundOrder.getCourseId(), "课程ID应该匹配");
    }

    @Test
    @DisplayName("测试根据用户ID和课程ID查询订单 - 不存在的记录")
    void testGetByUserAndCourseId_NotFound() {
        // Given
        Long nonExistingUserId = 9999L;
        Long nonExistingCourseId = 9999L;
        
        // When
        OrderInfo foundOrder = orderInfoDao.getByUserAndCourseId(nonExistingUserId, nonExistingCourseId);
        
        // Then
        assertNull(foundOrder, "不存在的记录查询结果应为空");
    }

    @Test
    @DisplayName("测试根据订单号查询订单")
    void testGetByOrderNo() {
        // Given
        Long userId = 1009L;
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3050L, userId);
        Long orderNo = TestUtils.generateTestOrderNo(userId);
        orderInfo.setOrderNo(orderNo);
        orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // When
        OrderInfo foundOrder = orderInfoDao.getByOrderNo(orderNo);
        
        // Then
        assertNotNull(foundOrder, "查询结果不应为空");
        assertEquals(orderNo, foundOrder.getOrderNo(), "订单号应该匹配");
        assertEquals(userId, foundOrder.getUserId(), "用户ID应该匹配");
    }

    @Test
    @DisplayName("测试根据订单号查询订单 - 不存在的订单号")
    void testGetByOrderNo_NotFound() {
        // Given
        Long nonExistingOrderNo = 999999999L;
        
        // When
        OrderInfo foundOrder = orderInfoDao.getByOrderNo(nonExistingOrderNo);
        
        // Then
        assertNull(foundOrder, "不存在的订单号查询结果应为空");
    }

    @Test
    @DisplayName("测试订单金额边界值处理")
    void testOrderAmount_BoundaryValues() {
        // Given
        OrderInfo orderInfo = TestDataFactory.createOrderInfo(3060L, 1010L);
        
        // Test minimum amount
        orderInfo.setOrderAmount(new BigDecimal("0.01"));
        orderInfo.setPayAmount(new BigDecimal("0.01"));
        
        // When
        int result = orderInfoDao.save(orderInfo);
        flushAndClear();
        
        // Then
        assertEquals(1, result, "保存最小金额订单应该成功");
        
        OrderInfo savedOrder = orderInfoDao.getById(orderInfo.getId());
        assertEquals(0, new BigDecimal("0.01").compareTo(savedOrder.getOrderAmount()), "最小金额应该正确保存");
        
        // Test large amount
        orderInfo.setId(3061L);
        orderInfo.setOrderAmount(new BigDecimal("999999.99"));
        orderInfo.setPayAmount(new BigDecimal("999999.99"));
        
        result = orderInfoDao.save(orderInfo);
        flushAndClear();
        
        assertEquals(1, result, "保存大金额订单应该成功");
        
        savedOrder = orderInfoDao.getById(orderInfo.getId());
        assertEquals(0, new BigDecimal("999999.99").compareTo(savedOrder.getOrderAmount()), "大金额应该正确保存");
    }
}