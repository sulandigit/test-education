/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.common.jdbc;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;


/**
 * Spring JDBC 基础抽象类
 * 为 Spring JDBC 操作提供通用的封装方法
 * 包括单对象查询、列表查询和分页查询功能
 * 
 * 主要功能：
 * 1. 提供统一的 JDBC 操作接口
 * 2. 封装常用的查询方法
 * 3. 支持自动分页功能
 * 4. 自动将结果集映射为 Java 对象
 *
 * @author hugovon
 * @version 1.0
 * @date 2022/1/1
 */
public abstract class AbstractBaseJdbc {

    /**
     * Spring JDBC 模板，用于执行 SQL 语句
     */
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * 命名参数 JDBC 模板，支持命名参数的 SQL 语句
     */
    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 获取当前事务最后一次 INSERT 操作的自增主键值
     * 使用 MySQL 的 LAST_INSERT_ID() 函数
     * 
     * 注意：只在当前连接会话中有效，且只适用于自增主键
     * 
     * @return 最后一次插入的自增主键值
     */
    public Long getLastId() {
        return jdbcTemplate.queryForObject("select last_insert_id() as id", Long.class);
    }

    /**
     * 查询单个对象
     * 执行 SQL 查询并将结果映射为指定类型的对象
     * 
     * @param sql   SQL 查询语句
     * @param clazz 目标对象类型
     * @param args  SQL 参数
     * @param <T>   结果对象类型
     * @return 查询结果对象
     * @throws org.springframework.dao.EmptyResultDataAccessException 如果查询结果为空
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果查询结果多于一条
     */
    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        Assert.hasText(sql, "sql 语句不能为空");
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<T>(clazz), args);
    }

    /**
     * 查询对象列表
     * 执行 SQL 查询并将结果映射为指定类型的对象列表
     * 
     * @param sql   SQL 查询语句
     * @param clazz 目标对象类型
     * @param args  SQL 参数
     * @param <T>   结果对象类型
     * @return 查询结果对象列表，如果无结果返回空列表
     */
    public <T> List<T> queryForObjectList(String sql, Class<T> clazz, Object... args) {
        Assert.hasText(sql, "sql 语句不能为空");
        return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<T>(clazz));
    }

    /**
     * 分页查询
     * 执行分页查询并返回包含分页信息的结果对象
     * 
     * 实现步骤：
     * 1. 执行总数查询获取记录总数
     * 2. 校验和调整分页参数
     * 3. 执行分页查询获取数据列表
     * 4. 封装分页结果
     * 
     * @param sql         原始 SQL 查询语句（不包含 LIMIT 子句）
     * @param pageCurrent 当前页码（从 1 开始）
     * @param pageSize    每页显示数量
     * @param clazz       目标对象类型
     * @param <T>         结果对象类型，必须实现 Serializable 接口
     * @return 包含分页信息和数据列表的 Page 对象
     */
    public <T extends Serializable> Page<T> queryForPage(String sql, int pageCurrent, int pageSize, Class<T> clazz) {
        Assert.hasText(sql, "sql 语句不能为空");
        Assert.isTrue(pageCurrent >= 1, "pageNo 必须大于等于1");
        Assert.isTrue(clazz != null, "clazz 不能为空");
        
        // 构造并执行总数查询
        String sqlCount = PageUtil.countSql(sql);
        int count = jdbcTemplate.queryForObject(sqlCount, Integer.class);
        
        // 校验并调整分页参数
        pageCurrent = PageUtil.checkPageCurrent(count, pageSize, pageCurrent);
        pageSize = PageUtil.checkPageSize(pageSize);
        int totalPage = PageUtil.countTotalPage(count, pageSize);
        
        // 构造并执行分页查询
        String sqlList = sql + PageUtil.limitSql(count, pageCurrent, pageSize);
        List<T> list = jdbcTemplate.query(sqlList, new BeanPropertyRowMapper<T>(clazz));
        
        return new Page<T>(count, totalPage, pageCurrent, pageSize, list);
    }

}
