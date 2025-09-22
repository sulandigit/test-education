package com.roncoo.education.common.sharding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 分库分表配置属性
 * 
 * @author sharding
 */
@Configuration
@ConfigurationProperties(prefix = "spring.shardingsphere")
public class ShardingProperties {
    
    /**
     * 是否启用分库分表
     */
    private boolean enabled = true;
    
    /**
     * 是否显示SQL
     */
    private boolean sqlShow = true;
    
    /**
     * 用户库分库数量
     */
    private int userDatabaseCount = 4;
    
    /**
     * 用户表分表数量
     */
    private int userTableCount = 16;
    
    /**
     * 课程库分库数量
     */
    private int courseDatabaseCount = 2;
    
    /**
     * 课程表分表数量
     */
    private int courseTableCount = 16;
    
    /**
     * 订单表分表数量
     */
    private int orderTableCount = 32;
    
    /**
     * 讲师表分表数量
     */
    private int lecturerTableCount = 8;

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSqlShow() {
        return sqlShow;
    }

    public void setSqlShow(boolean sqlShow) {
        this.sqlShow = sqlShow;
    }

    public int getUserDatabaseCount() {
        return userDatabaseCount;
    }

    public void setUserDatabaseCount(int userDatabaseCount) {
        this.userDatabaseCount = userDatabaseCount;
    }

    public int getUserTableCount() {
        return userTableCount;
    }

    public void setUserTableCount(int userTableCount) {
        this.userTableCount = userTableCount;
    }

    public int getCourseDatabaseCount() {
        return courseDatabaseCount;
    }

    public void setCourseDatabaseCount(int courseDatabaseCount) {
        this.courseDatabaseCount = courseDatabaseCount;
    }

    public int getCourseTableCount() {
        return courseTableCount;
    }

    public void setCourseTableCount(int courseTableCount) {
        this.courseTableCount = courseTableCount;
    }

    public int getOrderTableCount() {
        return orderTableCount;
    }

    public void setOrderTableCount(int orderTableCount) {
        this.orderTableCount = orderTableCount;
    }

    public int getLecturerTableCount() {
        return lecturerTableCount;
    }

    public void setLecturerTableCount(int lecturerTableCount) {
        this.lecturerTableCount = lecturerTableCount;
    }
}