package com.roncoo.education.exam.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamPaper;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamPaperExample;

import java.util.List;

/**
 * 试卷信息 服务类
 *
 * @author wujing
 * @date 2024-01-01
 */
public interface ExamPaperDao {

    /**
     * 保存试卷信息
     *
     * @param record 试卷信息
     * @return 影响记录数
     */
    int save(ExamPaper record);

    /**
     * 根据ID删除试卷信息
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改试卷信息
     *
     * @param record 试卷信息
     * @return 影响记录数
     */
    int updateById(ExamPaper record);

    /**
     * 根据ID获取试卷信息
     *
     * @param id 主键ID
     * @return 试卷信息
     */
    ExamPaper getById(Long id);

    /**
     * 试卷信息--分页查询
     *
     * @param pageCurrent 当前页
     * @param pageSize    分页大小
     * @param example     查询条件
     * @return 分页结果
     */
    Page<ExamPaper> page(int pageCurrent, int pageSize, ExamPaperExample example);

    /**
     * 试卷信息--条件列出
     *
     * @param example 查询条件
     * @return 试卷信息列表
     */
    List<ExamPaper> listByExample(ExamPaperExample example);

    /**
     * 试卷信息--条件统计
     *
     * @param example 统计条件
     * @return 试卷信息数量
     */
    int countByExample(ExamPaperExample example);
}