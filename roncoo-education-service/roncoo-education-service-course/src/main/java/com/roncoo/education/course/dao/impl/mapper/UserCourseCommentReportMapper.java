package com.roncoo.education.course.dao.impl.mapper;

import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程评论举报 Mapper接口
 *
 * @author assistant
 * @date 2025-09-20
 */
@Mapper
public interface UserCourseCommentReportMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(UserCourseCommentReport record);

    UserCourseCommentReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCourseCommentReport record);

    /**
     * 根据用户ID和评论ID查询举报记录
     */
    UserCourseCommentReport selectByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 条件查询
     */
    List<UserCourseCommentReport> selectByExample(Object example);

    /**
     * 条件统计
     */
    int countByExample(Object example);
}