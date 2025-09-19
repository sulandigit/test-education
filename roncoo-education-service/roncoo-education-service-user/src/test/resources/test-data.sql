-- 测试数据初始化脚本

-- 清理现有数据
DELETE FROM users WHERE id >= 1000;
DELETE FROM order_info WHERE id >= 1000;
DELETE FROM order_pay WHERE id >= 1000;
DELETE FROM log_login WHERE id >= 1000;
DELETE FROM msg WHERE id >= 1000;
DELETE FROM msg_user WHERE id >= 1000;
DELETE FROM lecturer WHERE id >= 1000;
DELETE FROM region WHERE id >= 1000;

-- 插入测试用户数据
INSERT INTO users (id, mobile, mobile_salt, mobile_psw, nickname, user_head, create_time, update_time) VALUES
(1001, '13800001001', 'salt001', SHA1(CONCAT('salt001', '123456')), '测试用户001', 'https://static.roncoos.com/lingke.png', NOW(), NOW()),
(1002, '13800001002', 'salt002', SHA1(CONCAT('salt002', '123456')), '测试用户002', 'https://static.roncoos.com/lingke.png', NOW(), NOW()),
(1003, '13800001003', 'salt003', SHA1(CONCAT('salt003', '123456')), '测试用户003', 'https://static.roncoos.com/lingke.png', NOW(), NOW()),
(1004, '13800001004', 'salt004', SHA1(CONCAT('salt004', '123456')), '异常状态用户', 'https://static.roncoos.com/lingke.png', NOW(), NOW()),
(1005, '13800001005', 'salt005', SHA1(CONCAT('salt005', '123456')), 'VIP用户', 'https://static.roncoos.com/lingke.png', NOW(), NOW());

-- 插入测试地区数据
INSERT INTO region (id, province_name, city_name, sort_no, status_id, create_time, update_time) VALUES
(1001, '广东省', '广州市', 1, 1, NOW(), NOW()),
(1002, '广东省', '深圳市', 2, 1, NOW(), NOW()),
(1003, '北京市', '北京市', 3, 1, NOW(), NOW()),
(1004, '上海市', '上海市', 4, 1, NOW(), NOW()),
(1005, '江苏省', '南京市', 5, 1, NOW(), NOW());

-- 插入测试讲师数据
INSERT INTO lecturer (id, lecturer_name, lecturer_mobile, lecturer_head, introduce, status_id, sort_no, create_time, update_time) VALUES
(1001, '张老师', '13800002001', 'https://static.roncoos.com/lecturer1.png', '资深Java讲师', 1, 1, NOW(), NOW()),
(1002, '李老师', '13800002002', 'https://static.roncoos.com/lecturer2.png', 'Python专家', 1, 2, NOW(), NOW()),
(1003, '王老师', '13800002003', 'https://static.roncoos.com/lecturer3.png', '前端技术专家', 1, 3, NOW(), NOW());

-- 插入测试订单数据
INSERT INTO order_info (id, order_no, user_id, course_id, order_title, order_amount, pay_amount, buy_type, order_status, create_time, update_time) VALUES
(1001, 202401001001, 1001, 2001, 'Java入门课程', 99.00, 99.00, 1, 1, NOW(), NOW()),
(1002, 202401001002, 1002, 2002, 'Python基础课程', 199.00, 199.00, 1, 2, NOW(), NOW()),
(1003, 202401001003, 1003, 2003, '前端开发课程', 299.00, 299.00, 1, 3, NOW(), NOW()),
(1004, 202401001004, 1004, 2004, '数据库课程', 399.00, 399.00, 1, 1, NOW(), NOW()),
(1005, 202401001005, 1005, 2005, '算法课程', 499.00, 499.00, 1, 4, NOW(), NOW());

-- 插入测试支付数据
INSERT INTO order_pay (id, order_no, user_id, course_id, pay_amount, pay_status, order_status, create_time, update_time) VALUES
(1001, 202401001001, 1001, 2001, 99.00, 1, 1, NOW(), NOW()),
(1002, 202401001002, 1002, 2002, 199.00, 2, 2, NOW(), NOW()),
(1003, 202401001003, 1003, 2003, 299.00, 3, 3, NOW(), NOW());

-- 插入测试登录日志数据
INSERT INTO log_login (id, user_id, login_ip, login_status, province, city, create_time, update_time) VALUES
(1001, 1001, '192.168.1.100', 1, '广东省', '广州市', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
(1002, 1002, '192.168.1.101', 1, '广东省', '深圳市', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(1003, 1003, '192.168.1.102', 2, '北京市', '北京市', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
(1004, 1001, '192.168.1.100', 1, '广东省', '广州市', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
(1005, 1002, '192.168.1.101', 1, '广东省', '深圳市', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR);

-- 插入测试消息数据
INSERT INTO msg (id, msg_title, msg_text, msg_type, send_type, status_id, create_time, update_time) VALUES
(1001, '系统通知', '欢迎使用领课教育平台', 1, 1, 1, NOW(), NOW()),
(1002, '课程推荐', '新课程上线啦', 2, 2, 1, NOW(), NOW()),
(1003, '优惠活动', '限时优惠活动', 3, 1, 1, NOW(), NOW());

-- 插入测试用户消息数据
INSERT INTO msg_user (id, msg_id, user_id, msg_text, is_read, create_time, update_time) VALUES
(1001, 1001, 1001, '欢迎使用领课教育平台', 0, NOW(), NOW()),
(1002, 1002, 1001, '新课程上线啦', 1, NOW(), NOW()),
(1003, 1003, 1002, '限时优惠活动', 0, NOW(), NOW()),
(1004, 1001, 1003, '欢迎使用领课教育平台', 1, NOW(), NOW());