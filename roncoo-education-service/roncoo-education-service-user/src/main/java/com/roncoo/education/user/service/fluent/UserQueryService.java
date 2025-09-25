package com.roncoo.education.user.service.fluent;

import cn.org.atool.fluent.mybatis.base.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.IBaseUpdater;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户复杂查询服务
 * 
 * 展示FluentMyBatis在复杂业务查询场景中的应用
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
@Service
@RequiredArgsConstructor
public class UserQueryService {

    // 注意：实际使用时需要注入编译生成的Mapper
    // @Autowired
    // private UsersMapper usersMapper;

    /**
     * 复杂查询1：用户活跃度分析
     * 查询最近N天内活跃的用户，按活跃度排序
     */
    public List<Map<String, Object>> analyzeUserActivity(int days, int limit) {
        return showFluentExample_analyzeUserActivity(days, limit);
    }

    /**
     * 复杂查询2：用户注册趋势统计
     * 按月统计用户注册数量和增长趋势
     */
    public List<Map<String, Object>> getUserRegistrationTrend(LocalDateTime startDate, LocalDateTime endDate) {
        return showFluentExample_getUserRegistrationTrend(startDate, endDate);
    }

    /**
     * 复杂查询3：用户分群查询
     * 根据年龄段、性别、地域等条件进行用户分群
     */
    public List<Users> getUsersBySegmentation(Integer minAge, Integer maxAge, Integer gender, String region) {
        return showFluentExample_getUsersBySegmentation(minAge, maxAge, gender, region);
    }

    /**
     * 复杂查询4：用户行为关联查询
     * 查询用户及其相关的订单、学习记录等信息
     */
    public List<Map<String, Object>> getUserWithRelatedData(Long userId) {
        return showFluentExample_getUserWithRelatedData(userId);
    }

    /**
     * 复杂查询5：批量用户状态更新
     * 根据特定条件批量更新用户状态
     */
    public int batchUpdateUserStatus(List<String> mobileList, Integer newStatus, String reason) {
        return showFluentExample_batchUpdateUserStatus(mobileList, newStatus, reason);
    }

    /**
     * 复杂查询6：用户重复数据检测
     * 检测可能重复的用户数据（相同手机号、相似昵称等）
     */
    public List<Map<String, Object>> detectDuplicateUsers() {
        return showFluentExample_detectDuplicateUsers();
    }

    /**
     * 复杂查询7：用户价值分析
     * 基于用户的消费行为、活跃度等计算用户价值分数
     */
    public List<Map<String, Object>> analyzeUserValue(int topN) {
        return showFluentExample_analyzeUserValue(topN);
    }

    // ==================== FluentMyBatis示例实现 ====================

    private List<Map<String, Object>> showFluentExample_analyzeUserActivity(int days, int limit) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户活跃度分析\n" +
            "\n" +
            "LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);\n" +
            "return query()\n" +
            "    .select().apply(\"u.id\", \"u.nickname\", \"u.mobile\")\n" +
            "    .select().apply(\"COUNT(DISTINCT o.id)\").as(\"order_count\")\n" +
            "    .select().apply(\"COUNT(DISTINCT uc.id)\").as(\"course_count\")\n" +
            "    .select().apply(\"MAX(u.gmt_modified)\").as(\"last_activity\")\n" +
            "    .from().table(\"users u\")\n" +
            "    .leftJoin(\"order_info o\").on(\"u.id = o.user_id AND o.gmt_create >= ?\", cutoffDate)\n" +
            "    .leftJoin(\"user_course uc\").on(\"u.id = uc.user_id AND uc.gmt_create >= ?\", cutoffDate)\n" +
            "    .where().apply(\"u.status_id = 1\")\n" +
            "    .and().apply(\"u.gmt_modified >= ?\", cutoffDate)\n" +
            "    .groupBy().apply(\"u.id\", \"u.nickname\", \"u.mobile\")\n" +
            "    .having().apply(\"COUNT(DISTINCT o.id) > 0 OR COUNT(DISTINCT uc.id) > 0\")\n" +
            "    .orderBy().apply(\"(COUNT(DISTINCT o.id) + COUNT(DISTINCT uc.id)) DESC\")\n" +
            "    .limit(limit)\n" +
            "    .to().listMaps();"
        );
    }

    private List<Map<String, Object>> showFluentExample_getUserRegistrationTrend(LocalDateTime startDate, LocalDateTime endDate) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户注册趋势统计\n" +
            "\n" +
            "return query()\n" +
            "    .select().apply(\"DATE_FORMAT(gmt_create, '%Y-%m')\").as(\"month\")\n" +
            "    .select().count().as(\"registration_count\")\n" +
            "    .select().apply(\"AVG(user_age)\").as(\"avg_age\")\n" +
            "    .select().apply(\"SUM(CASE WHEN user_sex = 1 THEN 1 ELSE 0 END)\").as(\"male_count\")\n" +
            "    .select().apply(\"SUM(CASE WHEN user_sex = 0 THEN 1 ELSE 0 END)\").as(\"female_count\")\n" +
            "    .where().gmtCreate().between(startDate, endDate)\n" +
            "    .groupBy().apply(\"DATE_FORMAT(gmt_create, '%Y-%m')\")\n" +
            "    .orderBy().apply(\"month ASC\")\n" +
            "    .to().listMaps();"
        );
    }

    private List<Users> showFluentExample_getUsersBySegmentation(Integer minAge, Integer maxAge, Integer gender, String region) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户分群查询\n" +
            "\n" +
            "Q queryBuilder = query().where().statusId().eq(1);\n" +
            "\n" +
            "if (minAge != null && maxAge != null) {\n" +
            "    queryBuilder.and().userAge().between(minAge, maxAge);\n" +
            "}\n" +
            "if (gender != null) {\n" +
            "    queryBuilder.and().userSex().eq(gender);\n" +
            "}\n" +
            "if (region != null) {\n" +
            "    queryBuilder.and().apply(\"EXISTS (SELECT 1 FROM user_region ur WHERE ur.user_id = users.id AND ur.region_name LIKE ?)\", \"%\" + region + \"%\");\n" +
            "}\n" +
            "\n" +
            "return queryBuilder\n" +
            "    .orderBy().gmtCreate().desc()\n" +
            "    .to().listEntity();"
        );
    }

    private List<Map<String, Object>> showFluentExample_getUserWithRelatedData(Long userId) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户行为关联查询\n" +
            "\n" +
            "return query()\n" +
            "    .select().apply(\"u.*\")\n" +
            "    .select().apply(\"COUNT(DISTINCT o.id)\").as(\"total_orders\")\n" +
            "    .select().apply(\"SUM(o.course_price)\").as(\"total_spent\")\n" +
            "    .select().apply(\"COUNT(DISTINCT uc.course_id)\").as(\"enrolled_courses\")\n" +
            "    .select().apply(\"MAX(ll.gmt_create)\").as(\"last_login\")\n" +
            "    .from().table(\"users u\")\n" +
            "    .leftJoin(\"order_info o\").on(\"u.id = o.user_id AND o.order_status = 1\")\n" +
            "    .leftJoin(\"user_course uc\").on(\"u.id = uc.user_id\")\n" +
            "    .leftJoin(\"log_login ll\").on(\"u.id = ll.user_id\")\n" +
            "    .where().apply(\"u.id = ?\", userId)\n" +
            "    .groupBy().apply(\"u.id\")\n" +
            "    .to().listMaps();"
        );
    }

    private int showFluentExample_batchUpdateUserStatus(List<String> mobileList, Integer newStatus, String reason) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂更新示例：批量用户状态更新\n" +
            "\n" +
            "// 第一步：记录操作日志\n" +
            "LocalDateTime now = LocalDateTime.now();\n" +
            "String logReason = \"批量状态更新：\" + reason;\n" +
            "\n" +
            "// 第二步：执行批量更新\n" +
            "return updater()\n" +
            "    .set().statusId().is(newStatus)\n" +
            "    .set().gmtModified().is(now)\n" +
            "    .set().remark().is(logReason)\n" +
            "    .where().mobile().in(mobileList)\n" +
            "    .to().updateBy();"
        );
    }

    private List<Map<String, Object>> showFluentExample_detectDuplicateUsers() {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户重复数据检测\n" +
            "\n" +
            "// 检测相同手机号的用户\n" +
            "List<Map<String, Object>> duplicateMobiles = query()\n" +
            "    .select().mobile()\n" +
            "    .select().count().as(\"user_count\")\n" +
            "    .select().apply(\"GROUP_CONCAT(id)\").as(\"user_ids\")\n" +
            "    .select().apply(\"GROUP_CONCAT(nickname)\").as(\"nicknames\")\n" +
            "    .where().mobile().isNotNull()\n" +
            "    .and().mobile().ne(\"\")\n" +
            "    .groupBy().mobile()\n" +
            "    .having().count().gt(1)\n" +
            "    .orderBy().count().desc()\n" +
            "    .to().listMaps();\n" +
            "\n" +
            "return duplicateMobiles;"
        );
    }

    private List<Map<String, Object>> showFluentExample_analyzeUserValue(int topN) {
        throw new UnsupportedOperationException(
            "FluentMyBatis复杂查询示例：用户价值分析\n" +
            "\n" +
            "return query()\n" +
            "    .select().apply(\"u.id\", \"u.nickname\", \"u.mobile\")\n" +
            "    .select().apply(\"COALESCE(SUM(o.course_price), 0)\").as(\"total_spent\")\n" +
            "    .select().apply(\"COUNT(DISTINCT o.id)\").as(\"order_count\")\n" +
            "    .select().apply(\"COUNT(DISTINCT uc.course_id)\").as(\"course_count\")\n" +
            "    .select().apply(\"DATEDIFF(NOW(), u.gmt_create)\").as(\"days_since_registration\")\n" +
            "    .select().apply(\n" +
            "        \"ROUND((COALESCE(SUM(o.course_price), 0) * 0.4 + \" +\n" +
            "        \"COUNT(DISTINCT o.id) * 20 + \" +\n" +
            "        \"COUNT(DISTINCT uc.course_id) * 10 + \" +\n" +
            "        \"CASE WHEN DATEDIFF(NOW(), MAX(ll.gmt_create)) <= 7 THEN 50 ELSE 0 END), 2)\"\n" +
            "    ).as(\"value_score\")\n" +
            "    .from().table(\"users u\")\n" +
            "    .leftJoin(\"order_info o\").on(\"u.id = o.user_id AND o.order_status = 1\")\n" +
            "    .leftJoin(\"user_course uc\").on(\"u.id = uc.user_id\")\n" +
            "    .leftJoin(\"log_login ll\").on(\"u.id = ll.user_id\")\n" +
            "    .where().apply(\"u.status_id = 1\")\n" +
            "    .groupBy().apply(\"u.id\", \"u.nickname\", \"u.mobile\")\n" +
            "    .orderBy().apply(\"value_score DESC\")\n" +
            "    .limit(topN)\n" +
            "    .to().listMaps();"
        );
    }
}