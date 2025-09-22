-- 分库分表数据库创建脚本
-- 领课教育在线系统分库分表实施脚本

-- ====================================
-- 1. 创建分库
-- ====================================

-- 用户分库（4个库）
CREATE DATABASE IF NOT EXISTS os_user_0 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS os_user_1 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS os_user_2 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS os_user_3 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 课程分库（2个库）
CREATE DATABASE IF NOT EXISTS os_course_0 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS os_course_1 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 系统库（不分片）
CREATE DATABASE IF NOT EXISTS os_system 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ====================================
-- 2. 用户库表结构创建
-- ====================================

-- 切换到用户库0
USE os_user_0;

-- 用户表分片（16张表：users_0 到 users_15）
CREATE TABLE users_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    mobile varchar(20) NOT NULL COMMENT '手机号码',
    mobile_salt varchar(32) NOT NULL COMMENT '密码盐',
    mobile_psw varchar(64) NOT NULL COMMENT '登录密码',
    nickname varchar(50) DEFAULT NULL COMMENT '昵称',
    user_sex tinyint(1) DEFAULT '3' COMMENT '用户性别(1男，2女，3保密)',
    user_age tinyint(3) DEFAULT NULL COMMENT '用户年龄',
    user_head varchar(255) DEFAULT NULL COMMENT '用户头像',
    remark text COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mobile (mobile),
    KEY idx_status_id (status_id),
    KEY idx_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表_0';

-- 创建用户表分片（1-15）
CREATE TABLE users_1 LIKE users_0;
ALTER TABLE users_1 COMMENT='用户信息表_1';

CREATE TABLE users_2 LIKE users_0;
ALTER TABLE users_2 COMMENT='用户信息表_2';

CREATE TABLE users_3 LIKE users_0;
ALTER TABLE users_3 COMMENT='用户信息表_3';

CREATE TABLE users_4 LIKE users_0;
ALTER TABLE users_4 COMMENT='用户信息表_4';

CREATE TABLE users_5 LIKE users_0;
ALTER TABLE users_5 COMMENT='用户信息表_5';

CREATE TABLE users_6 LIKE users_0;
ALTER TABLE users_6 COMMENT='用户信息表_6';

CREATE TABLE users_7 LIKE users_0;
ALTER TABLE users_7 COMMENT='用户信息表_7';

CREATE TABLE users_8 LIKE users_0;
ALTER TABLE users_8 COMMENT='用户信息表_8';

CREATE TABLE users_9 LIKE users_0;
ALTER TABLE users_9 COMMENT='用户信息表_9';

CREATE TABLE users_10 LIKE users_0;
ALTER TABLE users_10 COMMENT='用户信息表_10';

CREATE TABLE users_11 LIKE users_0;
ALTER TABLE users_11 COMMENT='用户信息表_11';

CREATE TABLE users_12 LIKE users_0;
ALTER TABLE users_12 COMMENT='用户信息表_12';

CREATE TABLE users_13 LIKE users_0;
ALTER TABLE users_13 COMMENT='用户信息表_13';

CREATE TABLE users_14 LIKE users_0;
ALTER TABLE users_14 COMMENT='用户信息表_14';

CREATE TABLE users_15 LIKE users_0;
ALTER TABLE users_15 COMMENT='用户信息表_15';

-- 讲师表分片（8张表：lecturer_0 到 lecturer_7）
CREATE TABLE lecturer_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    lecturer_name varchar(50) NOT NULL COMMENT '讲师名称',
    lecturer_mobile varchar(20) DEFAULT NULL COMMENT '讲师手机',
    lecturer_position varchar(50) DEFAULT NULL COMMENT '讲师职位',
    lecturer_head varchar(255) DEFAULT NULL COMMENT '讲师头像',
    introduce text COMMENT '简介',
    PRIMARY KEY (id),
    KEY idx_status_id (status_id),
    KEY idx_sort (sort),
    KEY idx_lecturer_name (lecturer_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讲师信息表_0';

-- 创建讲师表分片（1-7）
CREATE TABLE lecturer_1 LIKE lecturer_0;
ALTER TABLE lecturer_1 COMMENT='讲师信息表_1';

CREATE TABLE lecturer_2 LIKE lecturer_0;
ALTER TABLE lecturer_2 COMMENT='讲师信息表_2';

CREATE TABLE lecturer_3 LIKE lecturer_0;
ALTER TABLE lecturer_3 COMMENT='讲师信息表_3';

CREATE TABLE lecturer_4 LIKE lecturer_0;
ALTER TABLE lecturer_4 COMMENT='讲师信息表_4';

CREATE TABLE lecturer_5 LIKE lecturer_0;
ALTER TABLE lecturer_5 COMMENT='讲师信息表_5';

CREATE TABLE lecturer_6 LIKE lecturer_0;
ALTER TABLE lecturer_6 COMMENT='讲师信息表_6';

CREATE TABLE lecturer_7 LIKE lecturer_0;
ALTER TABLE lecturer_7 COMMENT='讲师信息表_7';

-- 订单表分片（32张表：order_info_0 到 order_info_31）
CREATE TABLE order_info_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    order_no bigint(20) NOT NULL COMMENT '订单号',
    user_id bigint(20) NOT NULL COMMENT '下单用户编号',
    mobile varchar(20) DEFAULT NULL COMMENT '下单用户电话',
    register_time datetime DEFAULT NULL COMMENT '下单用户注册时间',
    course_id bigint(20) NOT NULL COMMENT '课程ID',
    ruling_price decimal(8,2) DEFAULT '0.00' COMMENT '划线价',
    course_price decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '课程价格',
    pay_type tinyint(1) DEFAULT '1' COMMENT '支付方式：1微信支付，2支付宝支付',
    order_status tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态：1待支付，2成功支付，3支付失败，4关闭支付',
    remark_cus varchar(255) DEFAULT NULL COMMENT '客户备注',
    remark varchar(255) DEFAULT NULL COMMENT '后台备注',
    pay_time datetime DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_course_id (course_id),
    KEY idx_order_status (order_status),
    KEY idx_pay_time (pay_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表_0';

-- 由于订单表较多，使用循环创建剩余31张表
-- 这里列出前几张表作为示例，实际部署时需要创建所有32张表
CREATE TABLE order_info_1 LIKE order_info_0;
ALTER TABLE order_info_1 COMMENT='订单信息表_1';

CREATE TABLE order_info_2 LIKE order_info_0;
ALTER TABLE order_info_2 COMMENT='订单信息表_2';

CREATE TABLE order_info_3 LIKE order_info_0;
ALTER TABLE order_info_3 COMMENT='订单信息表_3';

-- ... 继续创建 order_info_4 到 order_info_31
-- （为节省空间，这里省略，实际使用时需要创建完整的32张表）

-- 复制表结构到其他用户库
-- 需要在 os_user_1, os_user_2, os_user_3 中执行相同的建表语句

-- ====================================
-- 3. 课程库表结构创建
-- ====================================

-- 切换到课程库0
USE os_course_0;

-- 课程表分片（16张表：course_0 到 course_15）
CREATE TABLE course_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    lecturer_id bigint(20) NOT NULL COMMENT '讲师ID',
    category_id bigint(20) DEFAULT NULL COMMENT '分类ID',
    course_name varchar(200) NOT NULL COMMENT '课程名称',
    course_logo varchar(255) DEFAULT NULL COMMENT '课程封面',
    is_free tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否免费(1:免费，0:收费)',
    ruling_price decimal(8,2) DEFAULT '0.00' COMMENT '原价',
    course_price decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '优惠价',
    is_putaway tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否上架(1:上架，0:下架)',
    course_sort int(11) DEFAULT '1' COMMENT '课程排序(前端显示使用)',
    count_buy int(11) DEFAULT '0' COMMENT '购买人数',
    count_study int(11) DEFAULT '0' COMMENT '学习人数',
    introduce text COMMENT '课程简介',
    PRIMARY KEY (id),
    KEY idx_lecturer_id (lecturer_id),
    KEY idx_category_id (category_id),
    KEY idx_status_id (status_id),
    KEY idx_is_putaway (is_putaway),
    KEY idx_course_sort (course_sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程信息表_0';

-- 创建课程表分片（1-15）
CREATE TABLE course_1 LIKE course_0;
ALTER TABLE course_1 COMMENT='课程信息表_1';

CREATE TABLE course_2 LIKE course_0;
ALTER TABLE course_2 COMMENT='课程信息表_2';

-- ... 继续创建 course_3 到 course_15

-- 课程章节表分片（16张表：course_chapter_0 到 course_chapter_15）
CREATE TABLE course_chapter_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    course_id bigint(20) NOT NULL COMMENT '课程ID',
    chapter_name varchar(200) NOT NULL COMMENT '章节名称',
    chapter_desc text COMMENT '章节描述',
    is_free tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否免费试听(1:免费，0:收费)',
    PRIMARY KEY (id),
    KEY idx_course_id (course_id),
    KEY idx_status_id (status_id),
    KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表_0';

-- 创建课程章节表分片（1-15）
CREATE TABLE course_chapter_1 LIKE course_chapter_0;
ALTER TABLE course_chapter_1 COMMENT='课程章节表_1';

-- ... 继续创建其他分片表

-- 用户课程关联表分片（32张表：user_course_0 到 user_course_31）
CREATE TABLE user_course_0 (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    user_id bigint(20) NOT NULL COMMENT '用户ID',
    course_id bigint(20) NOT NULL COMMENT '课程ID',
    buy_type tinyint(1) NOT NULL DEFAULT '1' COMMENT '购买类型(1支付，2免费)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_course (user_id, course_id),
    KEY idx_user_id (user_id),
    KEY idx_course_id (course_id),
    KEY idx_status_id (status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程关联表_0';

-- 创建用户课程关联表分片（1-31）
CREATE TABLE user_course_1 LIKE user_course_0;
ALTER TABLE user_course_1 COMMENT='用户课程关联表_1';

-- ... 继续创建其他分片表

-- 复制表结构到课程库1
-- 需要在 os_course_1 中执行相同的建表语句

-- ====================================
-- 4. 系统库表结构创建（不分片）
-- ====================================

USE os_system;

-- 系统配置表
CREATE TABLE sys_config (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    config_key varchar(100) NOT NULL COMMENT '配置键',
    config_value text COMMENT '配置值',
    remark varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key),
    KEY idx_status_id (status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 系统用户表
CREATE TABLE sys_user (
    id bigint(20) NOT NULL COMMENT '主键',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    sort int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    mobile varchar(20) NOT NULL COMMENT '手机',
    mobile_salt varchar(32) NOT NULL COMMENT '密码盐',
    mobile_psw varchar(64) NOT NULL COMMENT '密码',
    real_name varchar(50) NOT NULL COMMENT '真实姓名',
    remark varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mobile (mobile),
    KEY idx_status_id (status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 地区表（广播表）
CREATE TABLE region (
    id bigint(20) NOT NULL COMMENT '主键',
    province_name varchar(50) NOT NULL COMMENT '省份名称',
    city_name varchar(50) NOT NULL COMMENT '城市名称',
    sort_no int(11) DEFAULT '1' COMMENT '排序号',
    status_id tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_province (province_name),
    KEY idx_city (city_name),
    KEY idx_sort (sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区表';

-- ====================================
-- 5. 插入初始数据
-- ====================================

-- 插入系统配置数据
INSERT INTO sys_config (id, config_key, config_value, remark) VALUES
(1, 'system.name', '领课教育在线系统', '系统名称'),
(2, 'system.version', '14.0.0', '系统版本'),
(3, 'sharding.enabled', 'true', '分库分表开关'),
(4, 'database.count.user', '4', '用户库数量'),
(5, 'table.count.user', '16', '用户表数量'),
(6, 'database.count.course', '2', '课程库数量'),
(7, 'table.count.course', '16', '课程表数量');

-- 插入地区数据
INSERT INTO region (id, province_name, city_name, sort_no) VALUES
(1, '广东省', '广州市', 1),
(2, '广东省', '深圳市', 2),
(3, '北京市', '北京市', 3),
(4, '上海市', '上海市', 4),
(5, '江苏省', '南京市', 5),
(6, '浙江省', '杭州市', 6),
(7, '四川省', '成都市', 7),
(8, '湖北省', '武汉市', 8),
(9, '福建省', '厦门市', 9),
(10, '山东省', '青岛市', 10);

-- ====================================
-- 6. 创建用户和权限
-- ====================================

-- 创建分片数据库专用用户
CREATE USER IF NOT EXISTS 'sharding_user'@'%' IDENTIFIED BY 'ShardingPass123!';

-- 授权用户库权限
GRANT SELECT, INSERT, UPDATE, DELETE ON os_user_0.* TO 'sharding_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON os_user_1.* TO 'sharding_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON os_user_2.* TO 'sharding_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON os_user_3.* TO 'sharding_user'@'%';

-- 授权课程库权限
GRANT SELECT, INSERT, UPDATE, DELETE ON os_course_0.* TO 'sharding_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON os_course_1.* TO 'sharding_user'@'%';

-- 授权系统库权限
GRANT SELECT, INSERT, UPDATE, DELETE ON os_system.* TO 'sharding_user'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- ====================================
-- 脚本执行完成
-- ====================================

SELECT '分库分表数据库创建完成！' AS message;
SELECT CONCAT('用户库数量: ', COUNT(*)) AS user_databases 
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME LIKE 'os_user_%';

SELECT CONCAT('课程库数量: ', COUNT(*)) AS course_databases 
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME LIKE 'os_course_%';

SELECT '建议下一步: 执行分表创建脚本并配置应用程序' AS next_step;