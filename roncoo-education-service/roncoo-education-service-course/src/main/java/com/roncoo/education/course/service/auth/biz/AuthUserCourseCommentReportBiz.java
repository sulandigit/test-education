package com.roncoo.education.course.service.auth.biz;

import com.roncoo.education.common.config.ThreadContext;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.common.service.BaseBiz;
import com.roncoo.education.course.dao.UserCourseCommentDao;
import com.roncoo.education.course.dao.UserCourseCommentReportDao;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseComment;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReport;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentReportReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * AUTH-课程评论举报
 *
 * @author assistant
 * @date 2025-09-20
 */
@Component
@RequiredArgsConstructor
public class AuthUserCourseCommentReportBiz extends BaseBiz {

    @NotNull
    private final UserCourseCommentReportDao reportDao;
    @NotNull
    private final UserCourseCommentDao commentDao;

    /**
     * 举报评论
     *
     * @param req 举报请求
     * @return 操作结果
     */
    public Result<String> reportComment(AuthUserCourseCommentReportReq req) {
        Long userId = ThreadContext.userId();
        Long commentId = req.getCommentId();

        // 检查评论是否存在
        UserCourseComment comment = commentDao.getById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 检查用户是否已经举报过该评论
        UserCourseCommentReport existingReport = reportDao.getByUserIdAndCommentId(userId, commentId);
        if (existingReport != null) {
            return Result.error("您已经举报过该评论了");
        }

        // 用户不能举报自己的评论
        if (comment.getUserId().equals(userId)) {
            return Result.error("不能举报自己的评论");
        }

        // 创建举报记录
        UserCourseCommentReport report = new UserCourseCommentReport();
        report.setUserId(userId);
        report.setCommentId(commentId);
        report.setReportType(req.getReportType());
        report.setReportReason(req.getReportReason());
        report.setHandleStatus(0); // 待处理
        report.setStatusId(1);

        reportDao.save(report);

        return Result.success("举报成功，感谢您的反馈！");
    }

    /**
     * 检查用户是否举报了评论
     *
     * @param commentId 评论ID
     * @return 是否举报
     */
    public Result<Boolean> checkUserReported(Long commentId) {
        Long userId = ThreadContext.userId();
        UserCourseCommentReport report = reportDao.getByUserIdAndCommentId(userId, commentId);
        return Result.success(report != null);
    }
}