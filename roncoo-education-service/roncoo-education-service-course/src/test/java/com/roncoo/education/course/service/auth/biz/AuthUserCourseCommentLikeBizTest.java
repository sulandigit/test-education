package com.roncoo.education.course.service.auth.biz;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.course.dao.UserCourseCommentDao;
import com.roncoo.education.course.dao.UserCourseCommentLikeDao;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseComment;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentLike;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentLikeReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 课程评论点赞业务逻辑测试
 *
 * @author assistant
 * @date 2025-09-20
 */
public class AuthUserCourseCommentLikeBizTest {

    @Mock
    private UserCourseCommentLikeDao likeDao;

    @Mock
    private UserCourseCommentDao commentDao;

    @InjectMocks
    private AuthUserCourseCommentLikeBiz commentLikeBiz;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToggleLike_Success_Like() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentLikeReq req = new AuthUserCourseCommentLikeReq();
        req.setCommentId(commentId);
        req.setLikeType(1);

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(2L);

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(likeDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(null);
        when(likeDao.save(any(UserCourseCommentLike.class))).thenReturn(1);
        when(likeDao.countLikesByCommentId(commentId)).thenReturn(1);
        when(commentDao.updateById(any(UserCourseComment.class))).thenReturn(1);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentLikeBiz.toggleLike(req);

            // 验证结果
            assertTrue(result.getSuccess());
            assertEquals("点赞成功", result.getData());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(likeDao).getByUserIdAndCommentId(userId, commentId);
            verify(likeDao).save(any(UserCourseCommentLike.class));
            verify(likeDao).countLikesByCommentId(commentId);
            verify(commentDao).updateById(any(UserCourseComment.class));
        }
    }

    @Test
    void testToggleLike_Success_UnLike() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentLikeReq req = new AuthUserCourseCommentLikeReq();
        req.setCommentId(commentId);
        req.setLikeType(0);

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(2L);

        UserCourseCommentLike existingLike = new UserCourseCommentLike();
        existingLike.setUserId(userId);
        existingLike.setCommentId(commentId);

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(likeDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(existingLike);
        when(likeDao.deleteByUserIdAndCommentId(userId, commentId)).thenReturn(1);
        when(likeDao.countLikesByCommentId(commentId)).thenReturn(0);
        when(commentDao.updateById(any(UserCourseComment.class))).thenReturn(1);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentLikeBiz.toggleLike(req);

            // 验证结果
            assertTrue(result.getSuccess());
            assertEquals("取消点赞成功", result.getData());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(likeDao).getByUserIdAndCommentId(userId, commentId);
            verify(likeDao).deleteByUserIdAndCommentId(userId, commentId);
            verify(likeDao).countLikesByCommentId(commentId);
            verify(commentDao).updateById(any(UserCourseComment.class));
        }
    }

    @Test
    void testToggleLike_CommentNotFound() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentLikeReq req = new AuthUserCourseCommentLikeReq();
        req.setCommentId(commentId);
        req.setLikeType(1);

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(null);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentLikeBiz.toggleLike(req);

            // 验证结果
            assertFalse(result.getSuccess());
            assertEquals("评论不存在", result.getMsg());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(likeDao, never()).getByUserIdAndCommentId(anyLong(), anyLong());
        }
    }

    @Test
    void testToggleLike_AlreadyLiked() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentLikeReq req = new AuthUserCourseCommentLikeReq();
        req.setCommentId(commentId);
        req.setLikeType(1);

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(2L);

        UserCourseCommentLike existingLike = new UserCourseCommentLike();
        existingLike.setUserId(userId);
        existingLike.setCommentId(commentId);

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(likeDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(existingLike);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentLikeBiz.toggleLike(req);

            // 验证结果
            assertFalse(result.getSuccess());
            assertEquals("您已经点过赞了", result.getMsg());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(likeDao).getByUserIdAndCommentId(userId, commentId);
            verify(likeDao, never()).save(any(UserCourseCommentLike.class));
        }
    }

    @Test
    void testCheckUserLiked_True() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;

        UserCourseCommentLike existingLike = new UserCourseCommentLike();
        existingLike.setUserId(userId);
        existingLike.setCommentId(commentId);

        // Mock 依赖方法
        when(likeDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(existingLike);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<Boolean> result = commentLikeBiz.checkUserLiked(commentId);

            // 验证结果
            assertTrue(result.getSuccess());
            assertTrue(result.getData());

            // 验证方法调用
            verify(likeDao).getByUserIdAndCommentId(userId, commentId);
        }
    }

    @Test
    void testCheckUserLiked_False() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;

        // Mock 依赖方法
        when(likeDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(null);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<Boolean> result = commentLikeBiz.checkUserLiked(commentId);

            // 验证结果
            assertTrue(result.getSuccess());
            assertFalse(result.getData());

            // 验证方法调用
            verify(likeDao).getByUserIdAndCommentId(userId, commentId);
        }
    }
}