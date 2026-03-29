package com.roncoo.education.course.service.auth.biz;

import com.roncoo.education.common.config.ThreadContext;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.common.core.tools.BeanUtil;
import com.roncoo.education.common.service.BaseBiz;
import com.roncoo.education.course.dao.UserCourseCommentDao;
import com.roncoo.education.course.dao.UserCourseCommentLikeDao;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseComment;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentLike;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentLikeReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

/**
 * AUTH-课程评论点赞
 *
 * @author assistant
 * @date 2025-09-20
 */
@Component
@RequiredArgsConstructor
public class AuthUserCourseCommentLikeBiz extends BaseBiz {

    @NotNull
    private final UserCourseCommentLikeDao likeDao;
    @NotNull
    private final UserCourseCommentDao commentDao;

    /**
     * 课程评论点赞/取消点赞
     *
     * @param req 点赞请求
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> toggleLike(AuthUserCourseCommentLikeReq req) {
        Long userId = ThreadContext.userId();
        Long commentId = req.getCommentId();

        // 检查评论是否存在
        UserCourseComment comment = commentDao.getById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 检查用户是否已经点赞
        UserCourseCommentLike existingLike = likeDao.getByUserIdAndCommentId(userId, commentId);

        if (req.getLikeType() == 1) {
            // 点赞操作
            if (existingLike != null) {
                return Result.error("您已经点过赞了");
            }

            // 创建点赞记录
            UserCourseCommentLike like = new UserCourseCommentLike();
            like.setUserId(userId);
            like.setCommentId(commentId);
            like.setLikeType(1);
            like.setStatusId(1);
            likeDao.save(like);

            // 更新评论点赞数量
            updateCommentLikeCount(commentId);

            return Result.success("点赞成功");
        } else {
            // 取消点赞操作
            if (existingLike == null) {
                return Result.error("您还没有点赞");
            }

            // 删除点赞记录
            likeDao.deleteByUserIdAndCommentId(userId, commentId);

            // 更新评论点赞数量
            updateCommentLikeCount(commentId);

            return Result.success("取消点赞成功");
        }
    }

    /**
     * 更新评论点赞数量
     *
     * @param commentId 评论ID
     */
    private void updateCommentLikeCount(Long commentId) {
        int likeCount = likeDao.countLikesByCommentId(commentId);
        UserCourseComment comment = commentDao.getById(commentId);
        if (comment != null) {
            comment.setLikeCount(likeCount);
            commentDao.updateById(comment);
        }
    }

    /**
     * 检查用户是否点赞了评论
     *
     * @param commentId 评论ID
     * @return 是否点赞
     */
    public Result<Boolean> checkUserLiked(Long commentId) {
        Long userId = ThreadContext.userId();
        UserCourseCommentLike like = likeDao.getByUserIdAndCommentId(userId, commentId);
        return Result.success(like != null);
    }
}