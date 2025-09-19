package com.roncoo.education.exam.dao;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamRecord;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;

import java.util.List;

/**
 * 考试记录 服务类
 *
 * @author wujing
 * @date 2024-01-01
 */
public interface ExamRecordDao {

    /**
     * 保存考试记录
     *
     * @param record 考试记录
     * @return 影响记录数
     */
    int save(ExamRecord record);

    /**
     * 根据ID删除考试记录
     *
     * @param id 主键ID
     * @return 影响记录数
     */
    int deleteById(Long id);

    /**
     * 修改考试记录
     *
     * @param record 考试记录
     * @return 影响记录数
     */
    int updateById(ExamRecord record);

    /**
     * 根据ID获取考试记录
     *
     * @param id 主键ID
     * @return 考试记录
     */
    ExamRecord getById(Long id);

    /**
     * 考试记录--分页查询
     *
     * @param pageCurrent 当前页
     * @param pageSize    分页大小
     * @param example     查询条件
     * @return 分页结果
     */
    Page<ExamRecord> page(int pageCurrent, int pageSize, ExamExample example);

    /**
     * 根据用户ID和考试ID获取考试记录
     *
     * @param userId 用户ID
     * @param examId 考试ID
     * @return 考试记录列表
     */
    List<ExamRecord> listByUserIdAndExamId(Long userId, Long examId);

    /**
     * 根据用户ID获取考试记录
     *
     * @param userId 用户ID
     * @return 考试记录列表
     */
    List<ExamRecord> listByUserId(Long userId);

    /**
     * 获取用户考试次数
     *
     * @param userId 用户ID
     * @param examId 考试ID
     * @return 考试次数
     */
    int countByUserIdAndExamId(Long userId, Long examId);
}