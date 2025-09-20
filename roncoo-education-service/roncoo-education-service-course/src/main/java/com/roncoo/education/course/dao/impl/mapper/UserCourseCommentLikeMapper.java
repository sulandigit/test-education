package com.roncoo.education.course.dao.impl.mapper;

import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程评论点赞 Mapper接口
 *
 * @author assistant
 * @date 2025-09-20
 */
@Mapper
public interface UserCourseCommentLikeMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(UserCourseCommentLike record);

    UserCourseCommentLike selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCourseCommentLike record);

    /**
     * 根据用户ID和评论ID查询点赞记录
     */
    UserCourseCommentLike selectByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 根据评论ID统计点赞数量
     */
    int countLikesByCommentId(@Param("commentId") Long commentId);

    /**
     * 根据评论ID列表批量统计点赞数量
     */
    List<UserCourseCommentLike> listLikeCountsByCommentIds(@Param("commentIds") List<Long> commentIds);

    /**
     * 根据用户ID和评论列表获取用户点赞状态
     */
    List<UserCourseCommentLike> listUserLikesByCommentIds(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);

    /**
     * 删除用户对评论的点赞
     */
    int deleteByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);
}