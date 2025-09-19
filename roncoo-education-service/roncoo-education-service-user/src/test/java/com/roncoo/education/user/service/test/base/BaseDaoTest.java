/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.user.service.test.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Dao层测试基类
 * 提供数据访问层测试的基础设施
 *
 * @author Test Framework
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseDaoTest {

    /**
     * 测试实体管理器
     */
    @Autowired
    protected TestEntityManager entityManager;

    /**
     * 事务模板
     */
    @Autowired
    protected TransactionTemplate transactionTemplate;

    /**
     * 数据源
     */
    @Autowired
    protected DataSource dataSource;

    /**
     * 测试前置方法，加载测试数据
     */
    @BeforeEach
    public void setUp() throws SQLException {
        loadTestData();
    }

    /**
     * 测试后置方法，清理测试数据
     */
    @AfterEach
    public void tearDown() throws SQLException {
        clearDatabase();
    }

    /**
     * 加载测试数据
     * 子类可以重写此方法加载特定的测试数据
     */
    protected void loadTestData() throws SQLException {
        // 默认不加载数据，子类可以重写
    }

    /**
     * 清理数据库
     * 清理测试过程中产生的数据
     */
    protected void clearDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            // 禁用外键约束检查
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // 清理用户相关表数据
            statement.execute("DELETE FROM users WHERE id > 1000");
            statement.execute("DELETE FROM order_info WHERE id > 1000");
            statement.execute("DELETE FROM order_pay WHERE id > 1000");
            statement.execute("DELETE FROM log_login WHERE id > 1000");
            statement.execute("DELETE FROM msg WHERE id > 1000");
            statement.execute("DELETE FROM msg_user WHERE id > 1000");
            statement.execute("DELETE FROM lecturer WHERE id > 1000");
            
            // 重启外键约束检查
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * 刷新实体管理器
     * 确保数据持久化到数据库
     */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 执行SQL查询并返回结果数量
     *
     * @param sql SQL查询语句
     * @return 查询结果数量
     */
    protected int executeCountQuery(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    /**
     * 执行SQL更新语句
     *
     * @param sql SQL更新语句
     * @return 影响的行数
     */
    protected int executeUpdate(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }
}