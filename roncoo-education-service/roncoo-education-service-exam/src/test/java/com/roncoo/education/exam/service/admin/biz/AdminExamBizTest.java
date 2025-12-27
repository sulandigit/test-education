package com.roncoo.education.exam.service.admin.biz;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.dao.ExamDao;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;
import com.roncoo.education.exam.service.admin.req.AdminExamEditReq;
import com.roncoo.education.exam.service.admin.req.AdminExamPageReq;
import com.roncoo.education.exam.service.admin.req.AdminExamSaveReq;
import com.roncoo.education.exam.service.admin.resp.AdminExamPageResp;
import com.roncoo.education.exam.service.admin.resp.AdminExamViewResp;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * AdminExamBiz测试类
 *
 * @author wujing
 */
public class AdminExamBizTest {

    @Mock
    private ExamDao examDao;

    @InjectMocks
    private AdminExamBiz adminExamBiz;

    public AdminExamBizTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        // 准备测试数据
        AdminExamSaveReq req = new AdminExamSaveReq();
        req.setExamName("Java基础考试");
        req.setExamDesc("Java基础知识考试");
        req.setCourseId(1L);
        req.setPaperId(1L);
        req.setDuration(60);
        req.setPassScore(60);
        req.setTotalScore(100);
        req.setExamType(1);
        req.setIsEnable(1);

        // 模拟返回结果
        when(examDao.save(any(Exam.class))).thenReturn(1);

        // 执行测试
        Result<String> result = adminExamBiz.save(req);

        // 验证结果
        assertTrue(result.getSuccess());
        assertEquals("添加成功", result.getData());
    }

    @Test
    public void testSaveFailed() {
        // 准备测试数据
        AdminExamSaveReq req = new AdminExamSaveReq();
        req.setExamName("Java基础考试");
        req.setExamDesc("Java基础知识考试");
        req.setCourseId(1L);
        req.setPaperId(1L);
        req.setDuration(60);
        req.setPassScore(60);
        req.setTotalScore(100);
        req.setExamType(1);
        req.setIsEnable(1);

        // 模拟返回结果
        when(examDao.save(any(Exam.class))).thenReturn(0);

        // 执行测试
        Result<String> result = adminExamBiz.save(req);

        // 验证结果
        assertFalse(result.getSuccess());
        assertEquals("添加失败", result.getMessage());
    }

    @Test
    public void testView() {
        // 准备测试数据
        Long examId = 1L;
        Exam exam = new Exam();
        exam.setId(examId);
        exam.setExamName("Java基础考试");
        exam.setExamDesc("Java基础知识考试");
        exam.setCourseId(1L);
        exam.setPaperId(1L);
        exam.setDuration(60);
        exam.setPassScore(60);
        exam.setTotalScore(100);
        exam.setExamType(1);
        exam.setIsEnable(1);
        exam.setGmtCreate(LocalDateTime.now());
        exam.setGmtModified(LocalDateTime.now());

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(exam);

        // 执行测试
        Result<AdminExamViewResp> result = adminExamBiz.view(examId);

        // 验证结果
        assertTrue(result.getSuccess());
        AdminExamViewResp resp = result.getData();
        assertNotNull(resp);
        assertEquals(examId, resp.getId());
        assertEquals("Java基础考试", resp.getExamName());
        assertEquals("Java基础知识考试", resp.getExamDesc());
        assertEquals(1L, resp.getCourseId());
        assertEquals(1L, resp.getPaperId());
        assertEquals(60, resp.getDuration());
        assertEquals(60, resp.getPassScore());
        assertEquals(100, resp.getTotalScore());
        assertEquals(1, resp.getExamType());
        assertEquals(1, resp.getIsEnable());
    }

    @Test
    public void testViewNotFound() {
        // 准备测试数据
        Long examId = 1L;

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(null);

        // 执行测试
        Result<AdminExamViewResp> result = adminExamBiz.view(examId);

        // 验证结果
        assertFalse(result.getSuccess());
        assertEquals("考试不存在", result.getMessage());
    }

    @Test
    public void testEdit() {
        // 准备测试数据
        AdminExamEditReq req = new AdminExamEditReq();
        req.setId(1L);
        req.setExamName("Java基础考试（修改版）");
        req.setExamDesc("Java基础知识考试（修改版）");
        req.setCourseId(1L);
        req.setPaperId(1L);
        req.setDuration(90);
        req.setPassScore(70);
        req.setTotalScore(100);
        req.setExamType(1);
        req.setIsEnable(1);

        Exam existingExam = new Exam();
        existingExam.setId(1L);
        existingExam.setExamName("Java基础考试");
        existingExam.setExamDesc("Java基础知识考试");

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(existingExam);
        when(examDao.updateById(any(Exam.class))).thenReturn(1);

        // 执行测试
        Result<String> result = adminExamBiz.edit(req);

        // 验证结果
        assertTrue(result.getSuccess());
        assertEquals("修改成功", result.getData());
    }

    @Test
    public void testEditNotFound() {
        // 准备测试数据
        AdminExamEditReq req = new AdminExamEditReq();
        req.setId(1L);
        req.setExamName("Java基础考试（修改版）");

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(null);

        // 执行测试
        Result<String> result = adminExamBiz.edit(req);

        // 验证结果
        assertFalse(result.getSuccess());
        assertEquals("考试不存在", result.getMessage());
    }

    @Test
    public void testDelete() {
        // 准备测试数据
        Long examId = 1L;
        Exam exam = new Exam();
        exam.setId(examId);
        exam.setExamName("Java基础考试");

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(exam);
        when(examDao.deleteById(anyLong())).thenReturn(1);

        // 执行测试
        Result<String> result = adminExamBiz.delete(examId);

        // 验证结果
        assertTrue(result.getSuccess());
        assertEquals("删除成功", result.getData());
    }

    @Test
    public void testDeleteNotFound() {
        // 准备测试数据
        Long examId = 1L;

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(null);

        // 执行测试
        Result<String> result = adminExamBiz.delete(examId);

        // 验证结果
        assertFalse(result.getSuccess());
        assertEquals("考试不存在", result.getMessage());
    }

    @Test
    public void testPage() {
        // 准备测试数据
        AdminExamPageReq req = new AdminExamPageReq();
        req.setPageCurrent(1);
        req.setPageSize(10);
        req.setIsEnable(1);

        List<Exam> examList = new ArrayList<>();
        Exam exam = new Exam();
        exam.setId(1L);
        exam.setExamName("Java基础考试");
        exam.setExamDesc("Java基础知识考试");
        exam.setCourseId(1L);
        exam.setPaperId(1L);
        exam.setDuration(60);
        exam.setPassScore(60);
        exam.setTotalScore(100);
        exam.setExamType(1);
        exam.setIsEnable(1);
        examList.add(exam);

        Page<Exam> mockPage = new Page<>();
        mockPage.setPageCurrent(1);
        mockPage.setPageSize(10);
        mockPage.setTotalCount(1);
        mockPage.setTotalPage(1);
        mockPage.setList(examList);

        // 模拟返回结果
        when(examDao.page(any(Integer.class), any(Integer.class), any(ExamExample.class))).thenReturn(mockPage);

        // 执行测试
        Result<Page<AdminExamPageResp>> result = adminExamBiz.page(req);

        // 验证结果
        assertTrue(result.getSuccess());
        Page<AdminExamPageResp> respPage = result.getData();
        assertNotNull(respPage);
        assertEquals(1, respPage.getTotalCount());
        assertEquals(1, respPage.getList().size());
        assertEquals("Java基础考试", respPage.getList().get(0).getExamName());
    }
}