/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.factory;

import com.roncoo.education.user.dao.impl.mapper.entity.*;
import com.roncoo.education.user.service.test.util.TestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试数据工厂
 * 提供各种实体类的测试数据创建方法
 *
 * @author Test Framework
 */
public class TestDataFactory {

    /**
     * 创建标准用户测试数据
     *
     * @param id 用户ID
     * @return Users对象
     */
    public static Users createStandardUser(Long id) {
        Users user = new Users();
        user.setId(id);
        user.setMobile(TestUtils.generateTestMobile(id.intValue()));
        user.setMobileSalt("salt" + id);
        user.setMobilePsw("encrypted_password_" + id);
        user.setNickname("测试用户" + id);
        user.setUserSex(id % 2 == 0 ? 1 : 2); // 1-男，2-女
        user.setUserAge(20 + (id.intValue() % 30));
        user.setUserHead("https://static.roncoos.com/user" + id + ".png");
        user.setStatusId(1); // 正常状态
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());
        user.setRemark("测试用户备注" + id);
        return user;
    }

    /**
     * 创建VIP用户测试数据
     *
     * @param id 用户ID
     * @return Users对象
     */
    public static Users createVipUser(Long id) {
        Users user = createStandardUser(id);
        user.setNickname("VIP用户" + id);
        user.setRemark("VIP用户备注" + id);
        return user;
    }

    /**
     * 创建异常状态用户测试数据
     *
     * @param id 用户ID
     * @return Users对象
     */
    public static Users createDisabledUser(Long id) {
        Users user = createStandardUser(id);
        user.setStatusId(0); // 禁用状态
        user.setNickname("禁用用户" + id);
        return user;
    }

    /**
     * 批量创建用户测试数据
     *
     * @param startId 起始ID
     * @param count   创建数量
     * @return Users列表
     */
    public static List<Users> createUsersBatch(Long startId, int count) {
        List<Users> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createStandardUser(startId + i));
        }
        return users;
    }

    /**
     * 创建订单信息测试数据
     *
     * @param id     订单ID
     * @param userId 用户ID
     * @return OrderInfo对象
     */
    public static OrderInfo createOrderInfo(Long id, Long userId) {
        OrderInfo order = new OrderInfo();
        order.setId(id);
        order.setOrderNo(TestUtils.generateTestOrderNo(userId));
        order.setUserId(userId);
        order.setCourseId(2000L + id);
        order.setOrderTitle("测试课程" + id);
        order.setOrderAmount(java.math.BigDecimal.valueOf(99.00 + id));
        order.setPayAmount(java.math.BigDecimal.valueOf(99.00 + id));
        order.setBuyType(1); // 立即购买
        order.setOrderStatus(1); // 待支付
        order.setGmtCreate(LocalDateTime.now());
        order.setGmtModified(LocalDateTime.now());
        order.setStatusId(1);
        order.setRemark("测试订单" + id);
        return order;
    }

    /**
     * 创建支付订单测试数据
     *
     * @param id      支付ID
     * @param orderNo 订单号
     * @param userId  用户ID
     * @return OrderPay对象
     */
    public static OrderPay createOrderPay(Long id, Long orderNo, Long userId) {
        OrderPay orderPay = new OrderPay();
        orderPay.setId(id);
        orderPay.setOrderNo(orderNo);
        orderPay.setUserId(userId);
        orderPay.setCourseId(2000L + id);
        orderPay.setPayAmount(java.math.BigDecimal.valueOf(99.00 + id));
        orderPay.setPayStatus(1); // 待支付
        orderPay.setOrderStatus(1); // 待支付
        orderPay.setGmtCreate(LocalDateTime.now());
        orderPay.setGmtModified(LocalDateTime.now());
        orderPay.setStatusId(1);
        orderPay.setRemark("测试支付" + id);
        return orderPay;
    }

    /**
     * 创建登录日志测试数据
     *
     * @param id     日志ID
     * @param userId 用户ID
     * @return LogLogin对象
     */
    public static LogLogin createLogLogin(Long id, Long userId) {
        LogLogin logLogin = new LogLogin();
        logLogin.setId(id);
        logLogin.setUserId(userId);
        logLogin.setLoginIp("192.168.1." + (100 + id % 155));
        logLogin.setLoginStatus(1); // 登录成功
        logLogin.setProvince("广东省");
        logLogin.setCity("广州市");
        logLogin.setGmtCreate(LocalDateTime.now());
        logLogin.setGmtModified(LocalDateTime.now());
        logLogin.setStatusId(1);
        logLogin.setRemark("测试登录日志" + id);
        return logLogin;
    }

    /**
     * 创建讲师测试数据
     *
     * @param id 讲师ID
     * @return Lecturer对象
     */
    public static Lecturer createLecturer(Long id) {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(id);
        lecturer.setLecturerName("测试讲师" + id);
        lecturer.setLecturerMobile(TestUtils.generateTestMobile(id.intValue() + 1000));
        lecturer.setLecturerHead("https://static.roncoos.com/lecturer" + id + ".png");
        lecturer.setIntroduce("资深讲师，拥有" + (5 + id % 10) + "年教学经验");
        lecturer.setSort((int) (id % 100));
        lecturer.setGmtCreate(LocalDateTime.now());
        lecturer.setGmtModified(LocalDateTime.now());
        lecturer.setStatusId(1);
        lecturer.setRemark("测试讲师备注" + id);
        return lecturer;
    }

    /**
     * 创建地区测试数据
     *
     * @param id 地区ID
     * @return Region对象
     */
    public static Region createRegion(Long id) {
        Region region = new Region();
        region.setId(id);
        String[] provinces = {"广东省", "北京市", "上海市", "江苏省", "浙江省"};
        String[] cities = {"广州市", "北京市", "上海市", "南京市", "杭州市"};
        int index = (int) (id % provinces.length);
        region.setProvinceName(provinces[index]);
        region.setCityName(cities[index]);
        region.setSort((int) (id % 100));
        region.setGmtCreate(LocalDateTime.now());
        region.setGmtModified(LocalDateTime.now());
        region.setStatusId(1);
        region.setRemark("测试地区" + id);
        return region;
    }

    /**
     * 创建消息测试数据
     *
     * @param id 消息ID
     * @return Msg对象
     */
    public static Msg createMsg(Long id) {
        Msg msg = new Msg();
        msg.setId(id);
        msg.setMsgTitle("测试消息标题" + id);
        msg.setMsgText("测试消息内容" + id + "，这是一条用于测试的消息。");
        msg.setMsgType(1); // 系统消息
        msg.setSendType(1); // 全部用户
        msg.setGmtCreate(LocalDateTime.now());
        msg.setGmtModified(LocalDateTime.now());
        msg.setStatusId(1);
        msg.setRemark("测试消息备注" + id);
        return msg;
    }

    /**
     * 创建用户消息测试数据
     *
     * @param id     用户消息ID
     * @param msgId  消息ID
     * @param userId 用户ID
     * @return MsgUser对象
     */
    public static MsgUser createMsgUser(Long id, Long msgId, Long userId) {
        MsgUser msgUser = new MsgUser();
        msgUser.setId(id);
        msgUser.setMsgId(msgId);
        msgUser.setUserId(userId);
        msgUser.setMsgText("测试用户消息内容" + id);
        msgUser.setIsRead(0); // 未读
        msgUser.setGmtCreate(LocalDateTime.now());
        msgUser.setGmtModified(LocalDateTime.now());
        msgUser.setStatusId(1);
        msgUser.setRemark("测试用户消息" + id);
        return msgUser;
    }
}