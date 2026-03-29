package com.roncoo.education.course.service.auth;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.course.service.auth.biz.AuthUserCourseCommentLikeBiz;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentLikeReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * AUTH-课程评论点赞
 *
 * @author assistant
 * @date 2025-09-20
 */
@Api(tags = "auth-课程评论点赞")
@RestController
@RequiredArgsConstructor
@RequestMapping("/course/auth/user/course/comment/like")
public class AuthUserCourseCommentLikeController {

    @NotNull
    private final AuthUserCourseCommentLikeBiz biz;

    /**
     * 课程评论点赞/取消点赞
     *
     * @param req 点赞请求
     * @return 操作结果
     */
    @ApiOperation(value = "课程评论点赞/取消点赞", notes = "用户对课程评论进行点赞或取消点赞")
    @RequestMapping(value = "/toggle", method = RequestMethod.POST)
    public Result<String> toggleLike(@RequestBody @Valid AuthUserCourseCommentLikeReq req) {
        return biz.toggleLike(req);
    }

    /**
     * 检查用户点赞状态
     *
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @ApiOperation(value = "检查用户点赞状态", notes = "检查当前用户是否已点赞该评论")
    @RequestMapping(value = "/check/{commentId}", method = RequestMethod.GET)
    public Result<Boolean> checkUserLiked(@PathVariable Long commentId) {
        return biz.checkUserLiked(commentId);
    }
}