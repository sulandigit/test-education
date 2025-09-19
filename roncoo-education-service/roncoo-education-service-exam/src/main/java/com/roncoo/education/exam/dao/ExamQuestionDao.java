package com.roncoo.education.exam.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamQuestion;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;

import java.util.List;

/**
 * 考试题目 服务类
 *
 * @author wujing
 * @date 2024-01-01
 */
public interface ExamQuestionDao {

    /**
     * 保存题目信息
     *
     * @param record 题目信息
     * @return 影响记录数
     */
    int save(ExamQuestion record);

    /**
     * 根据ID删除题目信息
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改题目信息
     *
     * @param record 题目信息
     * @return 影响记录数
     */
    int updateById(ExamQuestion record);

    /**
     * 根据ID获取题目信息
     *
     * @param id 主键ID
     * @return 题目信息
     */
    ExamQuestion getById(Long id);

    /**
     * 题目信息--分页查询
     *
     * @param pageCurrent 当前页
     * @param pageSize    分页大小
     * @param example     查询条件
     * @return 分页结果
     */
    Page<ExamQuestion> page(int pageCurrent, int pageSize, ExamExample example);

    /**
     * 题目信息--条件列出
     *
     * @param paperId 试卷ID
     * @return 题目信息列表
     */
    List<ExamQuestion> listByPaperId(Long paperId);

    /**
     * 根据试卷ID获取题目数量
     *
     * @param paperId 试卷ID
     * @return 题目数量
     */
    int countByPaperId(Long paperId);

    /**
     * 根据试卷ID删除题目
     *
     * @param paperId 试卷ID
     * @return 影响记录数
     */
    int deleteByPaperId(Long paperId);
}