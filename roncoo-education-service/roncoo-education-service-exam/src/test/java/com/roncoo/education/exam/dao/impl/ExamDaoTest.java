package com.roncoo.education.exam.dao.impl;

import com.roncoo.education.exam.dao.ExamDao;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;
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
 * ExamDao测试类
 *
 * @author wujing
 */
public class ExamDaoTest {

    @Mock
    private ExamDao examDao;

    public ExamDaoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        // 准备测试数据
        Exam exam = new Exam();
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
        when(examDao.save(any(Exam.class))).thenReturn(1);

        // 执行测试
        int result = examDao.save(exam);

        // 验证结果
        assertEquals(1, result);
    }

    @Test
    public void testGetById() {
        // 准备测试数据
        Long examId = 1L;
        Exam expectedExam = new Exam();
        expectedExam.setId(examId);
        expectedExam.setExamName("Java基础考试");
        expectedExam.setExamDesc("Java基础知识考试");
        expectedExam.setCourseId(1L);
        expectedExam.setPaperId(1L);
        expectedExam.setDuration(60);
        expectedExam.setPassScore(60);
        expectedExam.setTotalScore(100);
        expectedExam.setExamType(1);
        expectedExam.setIsEnable(1);

        // 模拟返回结果
        when(examDao.getById(anyLong())).thenReturn(expectedExam);

        // 执行测试
        Exam actualExam = examDao.getById(examId);

        // 验证结果
        assertNotNull(actualExam);
        assertEquals(examId, actualExam.getId());
        assertEquals("Java基础考试", actualExam.getExamName());
        assertEquals("Java基础知识考试", actualExam.getExamDesc());
        assertEquals(1L, actualExam.getCourseId());
        assertEquals(1L, actualExam.getPaperId());
        assertEquals(60, actualExam.getDuration());
        assertEquals(60, actualExam.getPassScore());
        assertEquals(100, actualExam.getTotalScore());
        assertEquals(1, actualExam.getExamType());
        assertEquals(1, actualExam.getIsEnable());
    }

    @Test
    public void testListByCourseId() {
        // 准备测试数据
        Long courseId = 1L;
        List<Exam> expectedExams = new ArrayList<>();
        
        Exam exam1 = new Exam();
        exam1.setId(1L);
        exam1.setExamName("Java基础考试");
        exam1.setCourseId(courseId);
        expectedExams.add(exam1);
        
        Exam exam2 = new Exam();
        exam2.setId(2L);
        exam2.setExamName("Java进阶考试");
        exam2.setCourseId(courseId);
        expectedExams.add(exam2);

        // 模拟返回结果
        when(examDao.listByCourseId(anyLong())).thenReturn(expectedExams);

        // 执行测试
        List<Exam> actualExams = examDao.listByCourseId(courseId);

        // 验证结果
        assertNotNull(actualExams);
        assertEquals(2, actualExams.size());
        assertEquals("Java基础考试", actualExams.get(0).getExamName());
        assertEquals("Java进阶考试", actualExams.get(1).getExamName());
        assertEquals(courseId, actualExams.get(0).getCourseId());
        assertEquals(courseId, actualExams.get(1).getCourseId());
    }

    @Test
    public void testUpdateById() {
        // 准备测试数据
        Exam exam = new Exam();
        exam.setId(1L);
        exam.setExamName("Java基础考试（修改版）");
        exam.setExamDesc("Java基础知识考试（修改版）");
        exam.setGmtModified(LocalDateTime.now());

        // 模拟返回结果
        when(examDao.updateById(any(Exam.class))).thenReturn(1);

        // 执行测试
        int result = examDao.updateById(exam);

        // 验证结果
        assertEquals(1, result);
    }

    @Test
    public void testDeleteById() {
        // 准备测试数据
        Long examId = 1L;

        // 模拟返回结果
        when(examDao.deleteById(anyLong())).thenReturn(1);

        // 执行测试
        int result = examDao.deleteById(examId);

        // 验证结果
        assertEquals(1, result);
    }

    @Test
    public void testCountByExample() {
        // 准备测试数据
        ExamExample example = new ExamExample();
        example.createCriteria().andIsEnableEqualTo(1);

        // 模拟返回结果
        when(examDao.countByExample(any(ExamExample.class))).thenReturn(5);

        // 执行测试
        int count = examDao.countByExample(example);

        // 验证结果
        assertEquals(5, count);
    }
}