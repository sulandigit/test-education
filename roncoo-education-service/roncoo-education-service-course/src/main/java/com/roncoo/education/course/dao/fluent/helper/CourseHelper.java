package com.roncoo.education.course.dao.fluent.helper;

import cn.org.atool.fluent.mybatis.base.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.IBaseUpdater;
import com.roncoo.education.course.dao.impl.mapper.entity.Course;

/**
 * Course FluentMyBatis 辅助工具类示例
 * 
 * 注意：这是示例文件，实际的辅助类将由FluentMyBatis编译时处理器自动生成
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
public class CourseHelper {

    /**
     * 创建Course查询构建器
     * 实际使用时，这会是编译生成的CourseQuery类
     */
    public static IBaseQuery<Course> query() {
        // return new CourseQuery();
        throw new UnsupportedOperationException("请使用编译生成的CourseQuery类");
    }

    /**
     * 创建Course更新构建器
     * 实际使用时，这会是编译生成的CourseUpdater类
     */
    public static IBaseUpdater<Course> updater() {
        // return new CourseUpdater();
        throw new UnsupportedOperationException("请使用编译生成的CourseUpdater类");
    }

    /**
     * 示例：复杂查询方法
     * 查询指定分类的免费课程
     */
    public static String exampleFreeCoursesByCategory() {
        return """
        // 实际使用示例：
        List<Course> freeCourses = query()
            .where().categoryId().eq(categoryId)
            .and().isFree().eq(1)
            .and().isPutaway().eq(1)
            .orderBy().courseSort().asc()
            .orderBy().gmtCreate().desc()
            .limit(20)
            .to().listEntity();
        """;
    }

    /**
     * 示例：价格范围查询
     * 查询指定价格范围的付费课程
     */
    public static String exampleCoursesByPriceRange() {
        return """
        // 实际使用示例：
        List<Course> coursesByPrice = query()
            .where().isFree().eq(0)
            .and().coursePrice().between(minPrice, maxPrice)
            .and().isPutaway().eq(1)
            .orderBy().coursePrice().asc()
            .to().listEntity();
        """;
    }

    /**
     * 示例：热门课程查询
     * 根据购买量和学习人数查询热门课程
     */
    public static String examplePopularCourses() {
        return """
        // 实际使用示例：
        List<Course> popularCourses = query()
            .where().isPutaway().eq(1)
            .and().countBuy().gt(100)
            .orderBy().countBuy().desc()
            .orderBy().countStudy().desc()
            .limit(10)
            .to().listEntity();
        """;
    }

    /**
     * 示例：批量更新课程状态
     * 批量上架或下架课程
     */
    public static String exampleBatchUpdateStatus() {
        return """
        // 实际使用示例：
        int count = updater()
            .set().isPutaway().is(1)
            .set().gmtModified().is(LocalDateTime.now())
            .where().id().in(courseIds)
            .to().updateBy();
        """;
    }

    /**
     * 示例：课程统计查询
     * 按分类统计课程数量和平均价格
     */
    public static String exampleCourseStatsByCategory() {
        return """
        // 实际使用示例：
        List<Map<String, Object>> stats = query()
            .select().categoryId()
            .select().count().as(\"course_count\")
            .select().avg(\"course_price\").as(\"avg_price\")
            .where().isPutaway().eq(1)
            .groupBy().categoryId()
            .having().count().gt(5)
            .orderBy().count().desc()
            .to().listMaps();
        """;
    }

    /**
     * 示例：讲师课程关联查询
     * 查询指定讲师的所有课程
     */
    public static String exampleCoursesByLecturer() {
        return """
        // 实际使用示例：
        List<Map<String, Object>> lecturerCourses = query()
            .select().apply(\"c.id\", \"c.course_name\", \"c.course_price\", \"l.lecturer_name\")
            .from().table(\"course c\")
            .leftJoin(\"lecturer l\").on(\"c.lecturer_id = l.id\")
            .where().apply(\"l.id = ?\", lecturerId)
            .and().apply(\"c.is_putaway = 1\")
            .orderBy().apply(\"c.gmt_create desc\")
            .to().listMaps();
        """;
    }
}