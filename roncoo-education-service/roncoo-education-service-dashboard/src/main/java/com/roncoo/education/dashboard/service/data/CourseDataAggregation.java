package com.roncoo.education.dashboard.service.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程数据聚合结果
 *
 * @author wujing
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
public class CourseDataAggregation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 今日课程播放次数
     */
    private Integer dailyVideoViews;

    /**
     * 热门课程列表
     */
    private List<HotCourseData> hotCourses;

    /**
     * 课程分类统计
     */
    private List<CourseCategoryData> categoryStats;

    /**
     * 课程完成率统计
     */
    private CourseCompletionData completionStats;

    /**
     * 热门课程数据
     */
    @Data
    @Accessors(chain = true)
    public static class HotCourseData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Long courseId;
        private String courseName;
        private Integer studyCount;
        private BigDecimal revenue;
        private Integer rank;
        private Double completionRate;
    }

    /**
     * 课程分类数据
     */
    @Data
    @Accessors(chain = true)
    public static class CourseCategoryData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Long categoryId;
        private String categoryName;
        private Integer courseCount;
        private Integer studyCount;
        private BigDecimal revenue;
    }

    /**
     * 课程完成数据
     */
    @Data
    @Accessors(chain = true)
    public static class CourseCompletionData implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private Double averageCompletionRate;
        private Integer completedCourses;
        private Integer inProgressCourses;
        private Integer notStartedCourses;
    }

}