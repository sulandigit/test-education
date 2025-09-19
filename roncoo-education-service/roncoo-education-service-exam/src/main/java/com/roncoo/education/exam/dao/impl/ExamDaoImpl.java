package com.roncoo.education.exam.dao.impl;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.PageUtil;
import com.roncoo.education.common.core.tools.IdWorker;
import com.roncoo.education.common.jdbc.AbstractBaseJdbc;
import com.roncoo.education.exam.dao.ExamDao;
import com.roncoo.education.exam.dao.impl.mapper.ExamMapper;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamExample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 考试信息 服务实现类
 *
 * @author wujing
 * @date 2024-01-01
 */
@Repository
@RequiredArgsConstructor
public class ExamDaoImpl extends AbstractBaseJdbc implements ExamDao {

    @NotNull
    private final ExamMapper mapper;

    @Override
    public int save(Exam record) {
        if (record.getId() == null) {
            record.setId(IdWorker.getId());
        }
        return this.mapper.insertSelective(record);
    }

    @Override
    public int deleteById(Long id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateById(Exam record) {
        record.setGmtCreate(null);
        record.setGmtModified(null);
        return this.mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Exam getById(Long id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<Exam> page(int pageCurrent, int pageSize, ExamExample example) {
        int count = this.mapper.countByExample(example);
        pageSize = PageUtil.checkPageSize(pageSize);
        pageCurrent = PageUtil.checkPageCurrent(count, pageSize, pageCurrent);
        int totalPage = PageUtil.countTotalPage(count, pageSize);
        example.setLimitStart(PageUtil.countOffset(pageCurrent, pageSize));
        example.setPageSize(pageSize);
        return new Page<>(count, totalPage, pageCurrent, pageSize, this.mapper.selectByExample(example));
    }

    @Override
    public List<Exam> listByExample(ExamExample example) {
        return this.mapper.selectByExample(example);
    }

    @Override
    public int countByExample(ExamExample example) {
        return (int) this.mapper.countByExample(example);
    }

    @Override
    public List<Exam> listByCourseId(Long courseId) {
        return this.mapper.selectByCourseId(courseId);
    }

    @Override
    public void addExamCount(Long examId) {
        this.mapper.addExamCount(examId);
    }
}