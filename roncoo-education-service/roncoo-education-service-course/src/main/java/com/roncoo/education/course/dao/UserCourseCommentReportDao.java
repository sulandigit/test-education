package com.roncoo.education.course.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReport;

import java.util.List;

/**
 * 课程评论举报 服务类
 *
 * @author assistant
 * @date 2025-09-20
 */
public interface UserCourseCommentReportDao {

    /**
     * 保存课程评论举报
     *
     * @param record 课程评论举报
     * @return 影响记录数
     */
    int save(UserCourseCommentReport record);

    /**
     * 根据ID删除课程评论举报
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改课程评论举报
     *
     * @param record 课程评论举报
     * @return 影响记录数
     */
    int updateById(UserCourseCommentReport record);

    /**
     * 根据ID获取课程评论举报
     *
     * @param id 主键ID
     * @return 课程评论举报
     */
    UserCourseCommentReport getById(Long id);

    /**
     * 根据用户ID和评论ID获取举报记录
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 举报记录
     */
    UserCourseCommentReport getByUserIdAndCommentId(Long userId, Long commentId);

    /**
     * 分页查询举报记录
     *
     * @param pageCurrent 当前页
     * @param pageSize 页面大小
     * @param handleStatus 处理状态
     * @return 分页结果
     */
    Page<UserCourseCommentReport> page(int pageCurrent, int pageSize, Integer handleStatus);

    /**
     * 根据评论ID统计举报数量
     *
     * @param commentId 评论ID
     * @return 举报数量
     */
    int countReportsByCommentId(Long commentId);
}