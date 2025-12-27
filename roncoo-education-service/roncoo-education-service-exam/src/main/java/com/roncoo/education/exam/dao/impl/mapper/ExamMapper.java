package com.roncoo.education.exam.dao.impl.mapper;

import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试信息 Mapper接口
 *
 * @author wujing
 * @date 2024-01-01
 */
@Mapper
public interface ExamMapper {

    /**
     * 根据条件统计
     *
     * @param example 条件
     * @return 统计结果
     */
    long countByExample(ExamExample example);

    /**
     * 根据条件删除
     *
     * @param example 条件
     * @return 影响行数
     */
    int deleteByExample(ExamExample example);

    /**
     * 根据主键删除
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     *
     * @param record 数据
     * @return 影响行数
     */
    int insert(Exam record);

    /**
     * 插入数据(字段为null时不插入)
     *
     * @param record 数据
     * @return 影响行数
     */
    int insertSelective(Exam record);

    /**
     * 根据条件查询
     *
     * @param example 条件
     * @return 结果
     */
    List<Exam> selectByExample(ExamExample example);

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 结果
     */
    Exam selectByPrimaryKey(Long id);

    /**
     * 根据条件更新(字段为null时不更新)
     *
     * @param record  数据
     * @param example 条件
     * @return 影响行数
     */
    int updateByExampleSelective(@Param("record") Exam record, @Param("example") ExamExample example);

    /**
     * 根据条件更新
     *
     * @param record  数据
     * @param example 条件
     * @return 影响行数
     */
    int updateByExample(@Param("record") Exam record, @Param("example") ExamExample example);

    /**
     * 根据主键更新(字段为null时不更新)
     *
     * @param record 数据
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Exam record);

    /**
     * 根据主键更新
     *
     * @param record 数据
     * @return 影响行数
     */
    int updateByPrimaryKey(Exam record);

    /**
     * 根据课程ID查询考试列表
     *
     * @param courseId 课程ID
     * @return 考试列表
     */
    List<Exam> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 增加考试参与人数
     *
     * @param examId 考试ID
     */
    void addExamCount(@Param("examId") Long examId);
}