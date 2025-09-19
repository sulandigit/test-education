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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * ADMIN-考试信息
 *
 * @author wujing
 */
@Component
@RequiredArgsConstructor
public class AdminExamBiz {

    @NotNull
    private final ExamDao dao;

    /**
     * 考试信息分页
     *
     * @param req 分页查询参数
     * @return 分页结果
     */
    public Result<Page<AdminExamPageResp>> page(AdminExamPageReq req) {
        ExamExample example = new ExamExample();
        ExamExample.Criteria criteria = example.createCriteria();
        
        if (req.getCourseId() != null) {
            criteria.andCourseIdEqualTo(req.getCourseId());
        }
        if (StringUtils.hasText(req.getExamName())) {
            // 这里需要扩展Example类来支持like查询
            // criteria.andExamNameLike("%" + req.getExamName() + "%");
        }
        if (req.getIsEnable() != null) {
            criteria.andIsEnableEqualTo(req.getIsEnable());
        }
        
        example.setOrderByClause("id desc");
        
        Page<Exam> page = dao.page(req.getPageCurrent(), req.getPageSize(), example);
        Page<AdminExamPageResp> respPage = new Page<>();
        respPage.setPageCurrent(page.getPageCurrent());
        respPage.setPageSize(page.getPageSize());
        respPage.setTotalCount(page.getTotalCount());
        respPage.setTotalPage(page.getTotalPage());
        
        // 转换数据
        respPage.setList(page.getList().stream().map(this::copyToPageResp).toList());
        
        return Result.success(respPage);
    }

    /**
     * 考试信息添加
     *
     * @param req 添加参数
     * @return 添加结果
     */
    public Result<String> save(AdminExamSaveReq req) {
        Exam record = new Exam();
        BeanUtils.copyProperties(req, record);
        record.setGmtCreate(LocalDateTime.now());
        record.setGmtModified(LocalDateTime.now());
        record.setStatusId(1);
        record.setExamCount(0);
        
        int rows = dao.save(record);
        if (rows > 0) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 考试信息查看
     *
     * @param id 主键ID
     * @return 查看结果
     */
    public Result<AdminExamViewResp> view(Long id) {
        Exam record = dao.getById(id);
        if (record == null) {
            return Result.error("考试不存在");
        }
        
        AdminExamViewResp resp = new AdminExamViewResp();
        BeanUtils.copyProperties(record, resp);
        return Result.success(resp);
    }

    /**
     * 考试信息修改
     *
     * @param req 修改参数
     * @return 修改结果
     */
    public Result<String> edit(AdminExamEditReq req) {
        Exam record = dao.getById(req.getId());
        if (record == null) {
            return Result.error("考试不存在");
        }
        
        BeanUtils.copyProperties(req, record);
        record.setGmtModified(LocalDateTime.now());
        
        int rows = dao.updateById(record);
        if (rows > 0) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 考试信息删除
     *
     * @param id 主键ID
     * @return 删除结果
     */
    public Result<String> delete(Long id) {
        Exam record = dao.getById(id);
        if (record == null) {
            return Result.error("考试不存在");
        }
        
        int rows = dao.deleteById(id);
        if (rows > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 转换为分页响应对象
     */
    private AdminExamPageResp copyToPageResp(Exam exam) {
        AdminExamPageResp resp = new AdminExamPageResp();
        BeanUtils.copyProperties(exam, resp);
        return resp;
    }
}