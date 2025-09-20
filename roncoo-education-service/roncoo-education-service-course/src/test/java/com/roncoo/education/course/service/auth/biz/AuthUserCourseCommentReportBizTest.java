package com.roncoo.education.course.service.auth.biz;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.course.dao.UserCourseCommentDao;
import com.roncoo.education.course.dao.UserCourseCommentReportDao;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseComment;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReport;
import com.roncoo.education.course.service.auth.req.AuthUserCourseCommentReportReq;
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
 * 课程评论举报业务逻辑测试
 *
 * @author assistant
 * @date 2025-09-20
 */
public class AuthUserCourseCommentReportBizTest {

    @Mock
    private UserCourseCommentReportDao reportDao;

    @Mock
    private UserCourseCommentDao commentDao;

    @InjectMocks
    private AuthUserCourseCommentReportBiz commentReportBiz;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReportComment_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentReportReq req = new AuthUserCourseCommentReportReq();
        req.setCommentId(commentId);
        req.setReportType(1);
        req.setReportReason("垃圾广告");

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(2L); // 不同的用户ID

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(reportDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(null);
        when(reportDao.save(any(UserCourseCommentReport.class))).thenReturn(1);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentReportBiz.reportComment(req);

            // 验证结果
            assertTrue(result.getSuccess());
            assertEquals("举报成功，感谢您的反馈！", result.getData());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(reportDao).getByUserIdAndCommentId(userId, commentId);
            verify(reportDao).save(any(UserCourseCommentReport.class));
        }
    }

    @Test
    void testReportComment_CommentNotFound() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentReportReq req = new AuthUserCourseCommentReportReq();
        req.setCommentId(commentId);
        req.setReportType(1);
        req.setReportReason("垃圾广告");

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(null);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentReportBiz.reportComment(req);

            // 验证结果
            assertFalse(result.getSuccess());
            assertEquals("评论不存在", result.getMsg());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(reportDao, never()).getByUserIdAndCommentId(anyLong(), anyLong());
            verify(reportDao, never()).save(any(UserCourseCommentReport.class));
        }
    }

    @Test
    void testReportComment_AlreadyReported() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentReportReq req = new AuthUserCourseCommentReportReq();
        req.setCommentId(commentId);
        req.setReportType(1);
        req.setReportReason("垃圾广告");

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(2L);

        UserCourseCommentReport existingReport = new UserCourseCommentReport();
        existingReport.setUserId(userId);
        existingReport.setCommentId(commentId);

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(reportDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(existingReport);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentReportBiz.reportComment(req);

            // 验证结果
            assertFalse(result.getSuccess());
            assertEquals("您已经举报过该评论了", result.getMsg());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(reportDao).getByUserIdAndCommentId(userId, commentId);
            verify(reportDao, never()).save(any(UserCourseCommentReport.class));
        }
    }

    @Test
    void testReportComment_CannotReportOwnComment() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;
        AuthUserCourseCommentReportReq req = new AuthUserCourseCommentReportReq();
        req.setCommentId(commentId);
        req.setReportType(1);
        req.setReportReason("测试举报自己的评论");

        UserCourseComment comment = new UserCourseComment();
        comment.setId(commentId);
        comment.setUserId(userId); // 相同的用户ID

        // Mock 依赖方法
        when(commentDao.getById(commentId)).thenReturn(comment);
        when(reportDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(null);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<String> result = commentReportBiz.reportComment(req);

            // 验证结果
            assertFalse(result.getSuccess());
            assertEquals("不能举报自己的评论", result.getMsg());

            // 验证方法调用
            verify(commentDao).getById(commentId);
            verify(reportDao).getByUserIdAndCommentId(userId, commentId);
            verify(reportDao, never()).save(any(UserCourseCommentReport.class));
        }
    }

    @Test
    void testCheckUserReported_True() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;

        UserCourseCommentReport existingReport = new UserCourseCommentReport();
        existingReport.setUserId(userId);
        existingReport.setCommentId(commentId);

        // Mock 依赖方法
        when(reportDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(existingReport);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<Boolean> result = commentReportBiz.checkUserReported(commentId);

            // 验证结果
            assertTrue(result.getSuccess());
            assertTrue(result.getData());

            // 验证方法调用
            verify(reportDao).getByUserIdAndCommentId(userId, commentId);
        }
    }

    @Test
    void testCheckUserReported_False() {
        // 准备测试数据
        Long userId = 1L;
        Long commentId = 100L;

        // Mock 依赖方法
        when(reportDao.getByUserIdAndCommentId(userId, commentId)).thenReturn(null);

        // 使用 MockedStatic 模拟 ThreadContext
        try (MockedStatic<com.roncoo.education.common.config.ThreadContext> mockedThreadContext = 
             mockStatic(com.roncoo.education.common.config.ThreadContext.class)) {
            
            mockedThreadContext.when(com.roncoo.education.common.config.ThreadContext::userId).thenReturn(userId);

            // 执行测试
            Result<Boolean> result = commentReportBiz.checkUserReported(commentId);

            // 验证结果
            assertTrue(result.getSuccess());
            assertFalse(result.getData());

            // 验证方法调用
            verify(reportDao).getByUserIdAndCommentId(userId, commentId);
        }
    }
}