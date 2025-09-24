package com.roncoo.education.user.dao.fluent.helper;

import cn.org.atool.fluent.mybatis.base.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.IBaseUpdater;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;

/**
 * Users FluentMyBatis 辅助工具类示例
 * 
 * 注意：这是示例文件，实际的辅助类将由FluentMyBatis编译时处理器自动生成
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
public class UsersHelper {

    /**
     * 创建Users查询构建器
     * 实际使用时，这会是编译生成的UsersQuery类
     */
    public static IBaseQuery<Users> query() {
        // return new UsersQuery();
        throw new UnsupportedOperationException("请使用编译生成的UsersQuery类");
    }

    /**
     * 创建Users更新构建器
     * 实际使用时，这会是编译生成的UsersUpdater类
     */
    public static IBaseUpdater<Users> updater() {
        // return new UsersUpdater();
        throw new UnsupportedOperationException("请使用编译生成的UsersUpdater类");
    }

    /**
     * 示例：复杂查询方法
     * 查询活跃用户（按手机号和状态）
     */
    public static String exampleComplexQuery() {
        return """
        // 实际使用示例：
        List<Users> activeUsers = query()
            .where().statusId().eq(1)
            .and().mobile().like(\"%123%\")
            .and().gmtCreate().gt(LocalDateTime.now().minusDays(30))
            .orderBy().gmtCreate().desc()
            .limit(10)
            .to().listEntity();
        """;
    }

    /**
     * 示例：批量更新方法
     * 批量更新用户状态
     */
    public static String exampleBatchUpdate() {
        return """
        // 实际使用示例：
        int count = updater()
            .set().statusId().is(0)
            .set().gmtModified().is(LocalDateTime.now())
            .where().id().in(Arrays.asList(1L, 2L, 3L))
            .to().updateBy();
        """;
    }

    /**
     * 示例：聚合查询方法
     * 统计用户数量
     */
    public static String exampleAggregateQuery() {
        return """
        // 实际使用示例：
        long userCount = query()
            .select().count()
            .where().statusId().eq(1)
            .to().count();
        """;
    }

    /**
     * 示例：关联查询方法
     * 查询用户及其订单信息
     */
    public static String exampleJoinQuery() {
        return """
        // 实际使用示例：
        List<Map<String, Object>> userOrders = query()
            .select().apply(\"u.id\", \"u.nickname\", \"o.order_no\", \"o.order_status\")
            .from().table(\"users u\")
            .leftJoin(\"order_info o\").on(\"u.id = o.user_id\")
            .where().apply(\"u.status_id = 1\")
            .orderBy().apply(\"u.gmt_create desc\")
            .to().listMaps();
        """;
    }
}