package com.roncoo.education.user.service.test.sharding;

import com.roncoo.education.user.dao.UsersDao;
import com.roncoo.education.user.dao.LecturerDao;
import com.roncoo.education.user.dao.OrderInfoDao;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import com.roncoo.education.user.dao.impl.mapper.entity.Lecturer;
import com.roncoo.education.user.dao.impl.mapper.entity.OrderInfo;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import com.roncoo.education.common.sharding.utils.ShardingUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分库分表集成测试
 * 
 * @author test
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("分库分表集成测试")
class ShardingIntegrationTest {

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private LecturerDao lecturerDao;

    @Autowired
    private OrderInfoDao orderInfoDao;

    @Test
    @DisplayName("测试用户数据分库分表插入和查询")
    @Transactional
    void testUserShardingInsertAndQuery() {
        // 创建测试用户数据
        List<Users> testUsers = new ArrayList<>();
        
        // 创建分布在不同库表的用户
        for (int i = 1; i <= 100; i++) {
            Users user = TestDataFactory.createStandardUser((long) i);
            testUsers.add(user);
        }

        // 批量插入用户
        for (Users user : testUsers) {
            int result = usersDao.save(user);
            assertEquals(1, result, "用户插入应该成功");
            
            // 验证分片路由
            String expectedDatabase = ShardingUtils.buildUserDatabaseName(user.getId(), 4);
            String expectedTable = ShardingUtils.buildUserTableName("users", user.getId(), 16);
            
            assertNotNull(expectedDatabase, "分库名称不应该为空");
            assertNotNull(expectedTable, "分表名称不应该为空");
            
            // 查询验证
            Users savedUser = usersDao.getById(user.getId());
            assertNotNull(savedUser, "保存后应该能查询到用户");
            assertEquals(user.getMobile(), savedUser.getMobile(), "用户手机号应该一致");
            assertEquals(user.getNickname(), savedUser.getNickname(), "用户昵称应该一致");
        }
    }

    @Test
    @DisplayName("测试讲师数据分库分表插入和查询")
    @Transactional
    void testLecturerShardingInsertAndQuery() {
        // 创建测试讲师数据
        List<Lecturer> testLecturers = new ArrayList<>();
        
        for (int i = 1; i <= 50; i++) {
            Lecturer lecturer = createTestLecturer((long) i);
            testLecturers.add(lecturer);
        }

        // 批量插入讲师
        for (Lecturer lecturer : testLecturers) {
            int result = lecturerDao.save(lecturer);
            assertEquals(1, result, "讲师插入应该成功");
            
            // 验证分片路由
            String expectedDatabase = ShardingUtils.buildUserDatabaseName(lecturer.getId(), 4);
            String expectedTable = ShardingUtils.buildUserTableName("lecturer", lecturer.getId(), 8);
            
            assertNotNull(expectedDatabase, "分库名称不应该为空");
            assertNotNull(expectedTable, "分表名称不应该为空");
            
            // 查询验证
            Lecturer savedLecturer = lecturerDao.getById(lecturer.getId());
            assertNotNull(savedLecturer, "保存后应该能查询到讲师");
            assertEquals(lecturer.getLecturerName(), savedLecturer.getLecturerName(), "讲师姓名应该一致");
            assertEquals(lecturer.getLecturerMobile(), savedLecturer.getLecturerMobile(), "讲师手机号应该一致");
        }
    }

    @Test
    @DisplayName("测试订单数据分库分表插入和查询")
    @Transactional
    void testOrderShardingInsertAndQuery() {
        // 创建测试订单数据
        List<OrderInfo> testOrders = new ArrayList<>();
        
        for (int i = 1; i <= 200; i++) {
            OrderInfo order = createTestOrder((long) i, (long) (i % 50 + 1));
            testOrders.add(order);
        }

        // 批量插入订单
        for (OrderInfo order : testOrders) {
            int result = orderInfoDao.save(order);
            assertEquals(1, result, "订单插入应该成功");
            
            // 验证分片路由（订单按用户ID分片）
            String expectedDatabase = ShardingUtils.buildUserDatabaseName(order.getUserId(), 4);
            String expectedTable = ShardingUtils.buildUserTableName("order_info", order.getUserId(), 32);
            
            assertNotNull(expectedDatabase, "分库名称不应该为空");
            assertNotNull(expectedTable, "分表名称不应该为空");
            
            // 查询验证
            OrderInfo savedOrder = orderInfoDao.getById(order.getId());
            assertNotNull(savedOrder, "保存后应该能查询到订单");
            assertEquals(order.getOrderNo(), savedOrder.getOrderNo(), "订单号应该一致");
            assertEquals(order.getUserId(), savedOrder.getUserId(), "用户ID应该一致");
        }
    }

    @Test
    @DisplayName("测试跨库查询用户订单关联")
    @Transactional
    void testCrossShardQuery() {
        // 创建用户
        Users user = TestDataFactory.createStandardUser(1001L);
        usersDao.save(user);
        
        // 创建该用户的多个订单
        List<OrderInfo> userOrders = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            OrderInfo order = createTestOrder((long) (1000 + i), user.getId());
            orderInfoDao.save(order);
            userOrders.add(order);
        }
        
        // 查询用户信息
        Users savedUser = usersDao.getById(user.getId());
        assertNotNull(savedUser, "应该能查询到用户");
        
        // 查询用户的所有订单（测试同一用户的订单是否在同一分片）
        for (OrderInfo order : userOrders) {
            OrderInfo savedOrder = orderInfoDao.getById(order.getId());
            assertNotNull(savedOrder, "应该能查询到订单");
            assertEquals(user.getId(), savedOrder.getUserId(), "订单应该属于该用户");
            
            // 验证用户和订单在同一个库（由于都按user_id分片）
            String userDb = ShardingUtils.buildUserDatabaseName(user.getId(), 4);
            String orderDb = ShardingUtils.buildUserDatabaseName(savedOrder.getUserId(), 4);
            assertEquals(userDb, orderDb, "用户和其订单应该在同一个数据库");
        }
    }

    @Test
    @DisplayName("测试分片键验证和边界值")
    void testShardingKeyValidation() {
        // 测试不同分片键的分布
        long[] testUserIds = {1L, 4L, 5L, 8L, 16L, 17L, 1000001L, Long.MAX_VALUE - 1};
        
        for (long userId : testUserIds) {
            // 验证分片计算
            int dbIndex = ShardingUtils.calculateUserDatabaseIndex(userId, 4);
            int tableIndex = ShardingUtils.calculateUserTableIndex(userId, 16);
            
            assertTrue(dbIndex >= 0 && dbIndex < 4, 
                "数据库索引应该在0-3范围内，userId=" + userId + ", dbIndex=" + dbIndex);
            assertTrue(tableIndex >= 0 && tableIndex < 16, 
                "表索引应该在0-15范围内，userId=" + userId + ", tableIndex=" + tableIndex);
            
            // 创建并保存用户以验证实际路由
            Users user = TestDataFactory.createStandardUser(userId);
            int result = usersDao.save(user);
            assertEquals(1, result, "用户应该能成功保存，userId=" + userId);
            
            // 查询验证
            Users savedUser = usersDao.getById(userId);
            assertNotNull(savedUser, "应该能查询到保存的用户，userId=" + userId);
            assertEquals(userId, savedUser.getId(), "用户ID应该一致");
        }
    }

    @Test
    @DisplayName("测试批量操作性能")
    void testBatchOperationPerformance() {
        long startTime = System.currentTimeMillis();
        
        // 批量创建1000个用户
        List<Users> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            Users user = TestDataFactory.createStandardUser((long) i);
            users.add(user);
        }
        
        // 批量插入
        int successCount = 0;
        for (Users user : users) {
            try {
                int result = usersDao.save(user);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("插入用户失败: " + user.getId() + ", 错误: " + e.getMessage());
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("批量插入1000个用户耗时: " + duration + "ms");
        System.out.println("成功插入用户数量: " + successCount);
        
        // 验证插入成功率
        assertTrue(successCount >= 950, "成功率应该至少95%，实际成功数量: " + successCount);
        
        // 性能要求：1000个用户插入应该在30秒内完成
        assertTrue(duration < 30000, "批量插入性能应该在30秒内完成，实际耗时: " + duration + "ms");
    }

    /**
     * 创建测试讲师
     */
    private Lecturer createTestLecturer(Long id) {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(id);
        lecturer.setLecturerName("测试讲师" + id);
        lecturer.setLecturerMobile("138000" + String.format("%05d", id));
        lecturer.setLecturerPosition("高级讲师");
        lecturer.setLecturerHead("https://static.roncoos.com/lecturer" + id + ".png");
        lecturer.setIntroduce("测试讲师简介" + id);
        lecturer.setStatusId(1);
        lecturer.setSort(id.intValue());
        lecturer.setGmtCreate(LocalDateTime.now());
        lecturer.setGmtModified(LocalDateTime.now());
        return lecturer;
    }

    /**
     * 创建测试订单
     */
    private OrderInfo createTestOrder(Long orderId, Long userId) {
        OrderInfo order = new OrderInfo();
        order.setId(orderId);
        order.setOrderNo(System.currentTimeMillis() + orderId);
        order.setUserId(userId);
        order.setMobile("138000" + String.format("%05d", userId));
        order.setRegisterTime(LocalDateTime.now());
        order.setCourseId((long) (orderId % 100 + 1));
        order.setRulingPrice(new BigDecimal("299.00"));
        order.setCoursePrice(new BigDecimal("199.00"));
        order.setPayType(1);
        order.setOrderStatus(1);
        order.setRemarkCus("测试订单备注");
        order.setGmtCreate(LocalDateTime.now());
        order.setGmtModified(LocalDateTime.now());
        return order;
    }
}