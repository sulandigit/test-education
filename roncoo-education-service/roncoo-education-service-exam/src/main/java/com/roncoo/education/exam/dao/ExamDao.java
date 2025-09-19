package com.roncoo.education.exam.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;

import java.util.List;

/**
 * 考试信息 服务类
 *
 * @author wujing
 * @date 2024-01-01
 */
public interface ExamDao {

    /**
     * 保存考试信息
     *
     * @param record 考试信息
     * @return 影响记录数
     */
    int save(Exam record);

    /**
     * 根据ID删除考试信息
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改考试信息
     *
     * @param record 考试信息
     * @return 影响记录数
     */
    int updateById(Exam record);

    /**
     * 根据ID获取考试信息
     *
     * @param id 主键ID
     * @return 考试信息
     */
    Exam getById(Long id);

    /**
     * 考试信息--分页查询
     *
     * @param pageCurrent 当前页
     * @param pageSize    分页大小
     * @param example     查询条件
     * @return 分页结果
     */
    Page<Exam> page(int pageCurrent, int pageSize, ExamExample example);

    /**
     * 考试信息--条件列出
     *
     * @param example 查询条件
     * @return 考试信息列表
     */
    List<Exam> listByExample(ExamExample example);

    /**
     * 考试信息--条件统计
     *
     * @param example 统计条件
     * @return 考试信息数量
     */
    int countByExample(ExamExample example);

    /**
     * 根据课程ID查询考试列表
     *
     * @param courseId 课程ID
     * @return 考试列表
     */
    List<Exam> listByCourseId(Long courseId);

    /**
     * 增加考试参与人数
     *
     * @param examId 考试ID
     */
    void addExamCount(Long examId);
}