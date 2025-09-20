package com.roncoo.education.course.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentLike;

import java.util.List;

/**
 * 课程评论点赞 服务类
 *
 * @author assistant
 * @date 2025-09-20
 */
public interface UserCourseCommentLikeDao {

    /**
     * 保存课程评论点赞
     *
     * @param record 课程评论点赞
     * @return 影响记录数
     */
    int save(UserCourseCommentLike record);

    /**
     * 根据ID删除课程评论点赞
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改课程评论点赞
     *
     * @param record 课程评论点赞
     * @return 影响记录数
     */
    int updateById(UserCourseCommentLike record);

    /**
     * 根据ID获取课程评论点赞
     *
     * @param id 主键ID
     * @return 课程评论点赞
     */
    UserCourseCommentLike getById(Long id);

    /**
     * 根据用户ID和评论ID获取点赞记录
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 点赞记录
     */
    UserCourseCommentLike getByUserIdAndCommentId(Long userId, Long commentId);

    /**
     * 根据评论ID统计点赞数量
     *
     * @param commentId 评论ID
     * @return 点赞数量
     */
    int countLikesByCommentId(Long commentId);

    /**
     * 根据评论ID列表批量统计点赞数量
     *
     * @param commentIds 评论ID列表
     * @return 点赞数量映射（评论ID -> 点赞数量）
     */
    List<UserCourseCommentLike> listLikeCountsByCommentIds(List<Long> commentIds);

    /**
     * 根据用户ID和评论列表获取用户点赞状态
     *
     * @param userId 用户ID
     * @param commentIds 评论ID列表
     * @return 用户点赞记录列表
     */
    List<UserCourseCommentLike> listUserLikesByCommentIds(Long userId, List<Long> commentIds);

    /**
     * 删除用户对评论的点赞
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 影响记录数
     */
    int deleteByUserIdAndCommentId(Long userId, Long commentId);
}