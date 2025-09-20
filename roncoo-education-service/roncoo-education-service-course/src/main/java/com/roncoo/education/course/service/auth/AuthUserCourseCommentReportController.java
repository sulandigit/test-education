package com.roncoo.education.course.service.auth;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.course.service.auth.biz.AuthUserCourseCommentReportBiz;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentReportReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * AUTH-课程评论举报
 *
 * @author assistant
 * @date 2025-09-20
 */
@Api(tags = "auth-课程评论举报")
@RestController
@RequiredArgsConstructor
@RequestMapping("/course/auth/user/course/comment/report")
public class AuthUserCourseCommentReportController {

    @NotNull
    private final AuthUserCourseCommentReportBiz biz;

    /**
     * 举报评论
     *
     * @param req 举报请求
     * @return 操作结果
     */
    @ApiOperation(value = "举报评论", notes = "用户举报课程评论")
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public Result<String> reportComment(@RequestBody @Valid AuthUserCourseCommentReportReq req) {
        return biz.reportComment(req);
    }

    /**
     * 检查用户举报状态
     *
     * @param commentId 评论ID
     * @return 是否已举报
     */
    @ApiOperation(value = "检查用户举报状态", notes = "检查当前用户是否已举报该评论")
    @RequestMapping(value = "/check/{commentId}", method = RequestMethod.GET)
    public Result<Boolean> checkUserReported(@PathVariable Long commentId) {
        return biz.checkUserReported(commentId);
    }
}