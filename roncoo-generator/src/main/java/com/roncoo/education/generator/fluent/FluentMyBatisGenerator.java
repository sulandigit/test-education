package com.roncoo.education.generator.fluent;

import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import org.junit.jupiter.api.Test;

/**
 * FluentMyBatis 代码生成器配置
 * 
 * 用于生成FluentMyBatis相关的辅助类
 * 包括：Entity、Mapper、Query、Updater等
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
public class FluentMyBatisGenerator {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/roncoo_education?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 生成用户模块相关的FluentMyBatis辅助类
     */
    @Test
    public void generateUserModule() {
        FileGenerator.build(Fluent4UserModule.class);
    }

    /**
     * 生成课程模块相关的FluentMyBatis辅助类
     */
    @Test 
    public void generateCourseModule() {
        FileGenerator.build(Fluent4CourseModule.class);
    }

    /**
     * 生成系统模块相关的FluentMyBatis辅助类
     */
    @Test
    public void generateSystemModule() {
        FileGenerator.build(Fluent4SystemModule.class);
    }

    /**
     * 用户模块表配置
     */
    @Tables(
        url = URL, username = USERNAME, password = PASSWORD,
        basePack = "com.roncoo.education.user.dao.fluent",
        gmtCreated = "gmt_create", gmtModified = "gmt_modified",
        tables = {
            @Table(value = {"users"}, entity = "Users"),
            @Table(value = {"order_info"}, entity = "OrderInfo"),
            @Table(value = {"order_pay"}, entity = "OrderPay"),
            @Table(value = {"msg"}, entity = "Msg"),
            @Table(value = {"msg_user"}, entity = "MsgUser"),
            @Table(value = {"log_login"}, entity = "LogLogin"),
            @Table(value = {"region"}, entity = "Region")
        }
    )
    static class Fluent4UserModule {
        // 配置类，实际生成时会被FluentMyBatis处理
    }

    /**
     * 课程模块表配置
     */
    @Tables(
        url = URL, username = USERNAME, password = PASSWORD,
        basePack = "com.roncoo.education.course.dao.fluent",
        gmtCreated = "gmt_create", gmtModified = "gmt_modified",
        tables = {
            @Table(value = {"course"}, entity = "Course"),
            @Table(value = {"course_chapter"}, entity = "CourseChapter"),
            @Table(value = {"course_chapter_period"}, entity = "CourseChapterPeriod"),
            @Table(value = {"category"}, entity = "Category"),
            @Table(value = {"lecturer"}, entity = "Lecturer"),
            @Table(value = {"course_user"}, entity = "CourseUser"),
            @Table(value = {"course_comment"}, entity = "CourseComment"),
            @Table(value = {"course_collect"}, entity = "CourseCollect"),
            @Table(value = {"resource"}, entity = "Resource")
        }
    )
    static class Fluent4CourseModule {
        // 配置类，实际生成时会被FluentMyBatis处理
    }

    /**
     * 系统模块表配置
     */
    @Tables(
        url = URL, username = USERNAME, password = PASSWORD,
        basePack = "com.roncoo.education.system.dao.fluent",
        gmtCreated = "gmt_create", gmtModified = "gmt_modified",
        tables = {
            @Table(value = {"sys_config"}, entity = "SysConfig"),
            @Table(value = {"sys_menu"}, entity = "SysMenu"),
            @Table(value = {"sys_role"}, entity = "SysRole"),
            @Table(value = {"sys_role_user"}, entity = "SysRoleUser"),
            @Table(value = {"sys_user"}, entity = "SysUser"),
            @Table(value = {"banner"}, entity = "Banner"),
            @Table(value = {"file_storage"}, entity = "FileStorage"),
            @Table(value = {"nav"}, entity = "Nav"),
            @Table(value = {"website_nav"}, entity = "WebsiteNav")
        }
    )
    static class Fluent4SystemModule {
        // 配置类，实际生成时会被FluentMyBatis处理
    }
}