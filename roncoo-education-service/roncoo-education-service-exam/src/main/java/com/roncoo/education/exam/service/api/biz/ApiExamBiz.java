package com.roncoo.education.exam.service.api.biz;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.dao.ExamDao;
import com.roncoo.education.exam.dao.ExamRecordDao;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamRecord;
import com.roncoo.education.exam.service.api.req.ApiExamStartReq;
import com.roncoo.education.exam.service.api.req.ApiExamSubmitReq;
import com.roncoo.education.exam.service.api.resp.ApiExamDetailResp;
import com.roncoo.education.exam.service.api.resp.ApiExamListResp;
import com.roncoo.education.exam.service.api.resp.ApiExamStartResp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API-考试信息
 *
 * @author wujing
 */
@Component
@RequiredArgsConstructor
public class ApiExamBiz {

    @NotNull
    private final ExamDao examDao;

    @NotNull
    private final ExamRecordDao examRecordDao;

    /**
     * 根据课程ID获取考试列表
     *
     * @param courseId 课程ID
     * @return 考试列表
     */
    public Result<List<ApiExamListResp>> listByCourseId(Long courseId) {
        List<Exam> examList = examDao.listByCourseId(courseId);
        List<ApiExamListResp> respList = examList.stream().map(this::copyToListResp).toList();
        return Result.success(respList);
    }

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     * @return 考试详情
     */
    public Result<ApiExamDetailResp> detail(Long examId) {
        Exam exam = examDao.getById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        // 检查考试是否启用
        if (exam.getIsEnable() == 0) {
            return Result.error("考试已禁用");
        }

        // 检查考试时间
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            return Result.error("考试尚未开始");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            return Result.error("考试已结束");
        }

        ApiExamDetailResp resp = new ApiExamDetailResp();
        BeanUtils.copyProperties(exam, resp);
        return Result.success(resp);
    }

    /**
     * 开始考试
     *
     * @param req 开始考试请求
     * @return 考试信息
     */
    public Result<ApiExamStartResp> startExam(ApiExamStartReq req) {
        Exam exam = examDao.getById(req.getExamId());
        if (exam == null) {
            return Result.error("考试不存在");
        }

        // 检查考试次数限制
        if (exam.getAllowTimes() > 0) {
            int userExamTimes = examRecordDao.countByUserIdAndExamId(req.getUserId(), req.getExamId());
            if (userExamTimes >= exam.getAllowTimes()) {
                return Result.error("已超过考试次数限制");
            }
        }

        // 检查是否有进行中的考试
        List<ExamRecord> ongoingRecords = examRecordDao.listByUserIdAndExamId(req.getUserId(), req.getExamId());
        ExamRecord ongoingRecord = ongoingRecords.stream()
                .filter(record -> record.getExamStatus() == 2) // 进行中状态
                .findFirst()
                .orElse(null);

        if (ongoingRecord != null) {
            // 返回正在进行的考试
            ApiExamStartResp resp = new ApiExamStartResp();
            BeanUtils.copyProperties(ongoingRecord, resp);
            return Result.success(resp);
        }

        // 创建新的考试记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setUserId(req.getUserId());
        examRecord.setExamId(req.getExamId());
        examRecord.setPaperId(exam.getPaperId());
        examRecord.setStartTime(LocalDateTime.now());
        examRecord.setTotalScore(exam.getTotalScore());
        examRecord.setExamStatus(2); // 进行中
        examRecord.setExamTimes(ongoingRecords.size() + 1);
        examRecord.setStatusId(1);
        examRecord.setGmtCreate(LocalDateTime.now());
        examRecord.setGmtModified(LocalDateTime.now());

        int rows = examRecordDao.save(examRecord);
        if (rows > 0) {
            // 增加考试参与人数
            examDao.addExamCount(req.getExamId());

            ApiExamStartResp resp = new ApiExamStartResp();
            BeanUtils.copyProperties(examRecord, resp);
            return Result.success(resp);
        }

        return Result.error("开始考试失败");
    }

    /**
     * 提交考试
     *
     * @param req 提交考试请求
     * @return 提交结果
     */
    public Result<String> submitExam(ApiExamSubmitReq req) {
        ExamRecord examRecord = examRecordDao.getById(req.getExamRecordId());
        if (examRecord == null) {
            return Result.error("考试记录不存在");
        }

        if (!examRecord.getUserId().equals(req.getUserId())) {
            return Result.error("无权限操作此考试");
        }

        if (examRecord.getExamStatus() != 2) {
            return Result.error("考试状态异常");
        }

        // 计算考试用时
        LocalDateTime now = LocalDateTime.now();
        long useTime = java.time.Duration.between(examRecord.getStartTime(), now).getSeconds();

        // 更新考试记录
        examRecord.setEndTime(now);
        examRecord.setUseTime((int) useTime);
        examRecord.setExamStatus(3); // 已完成
        examRecord.setGmtModified(now);

        // TODO: 这里需要实现自动阅卷逻辑，计算分数
        // 暂时设置为0分
        examRecord.setScore(0);
        examRecord.setCorrectCount(0);
        examRecord.setErrorCount(0);
        examRecord.setNoAnswerCount(0);

        // 判断是否通过
        Exam exam = examDao.getById(examRecord.getExamId());
        if (exam != null && examRecord.getScore() >= exam.getPassScore()) {
            examRecord.setIsPassed(1);
        } else {
            examRecord.setIsPassed(0);
        }

        int rows = examRecordDao.updateById(examRecord);
        if (rows > 0) {
            return Result.success("提交成功");
        }

        return Result.error("提交失败");
    }

    /**
     * 转换为列表响应对象
     */
    private ApiExamListResp copyToListResp(Exam exam) {
        ApiExamListResp resp = new ApiExamListResp();
        BeanUtils.copyProperties(exam, resp);
        return resp;
    }
}