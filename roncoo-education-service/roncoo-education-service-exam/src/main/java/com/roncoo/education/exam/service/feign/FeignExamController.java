package com.roncoo.education.exam.service.feign;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.dao.ExamDao;
import com.roncoo.education.exam.dao.ExamRecordDao;
import com.roncoo.education.exam.dao.impl.mapper.entity.Exam;
import com.roncoo.education.exam.dao.impl.mapper.entity.ExamRecord;
import com.roncoo.education.exam.feign.dto.ExamDto;
import com.roncoo.education.exam.feign.dto.ExamRecordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Feign-考试信息
 *
 * @author wujing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/exam/feign")
public class FeignExamController {

    @NotNull
    private final ExamDao examDao;

    @NotNull
    private final ExamRecordDao examRecordDao;

    /**
     * 根据课程ID获取考试列表
     */
    @RequestMapping(value = "/exam/listByCourseId")
    public Result<List<ExamDto>> listByCourseId(@RequestParam("courseId") Long courseId) {
        List<Exam> examList = examDao.listByCourseId(courseId);
        List<ExamDto> examDtoList = examList.stream().map(this::copyToDto).toList();
        return Result.success(examDtoList);
    }

    /**
     * 根据ID获取考试信息
     */
    @RequestMapping(value = "/exam/getById")
    public Result<ExamDto> getById(@RequestParam("examId") Long examId) {
        Exam exam = examDao.getById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }
        ExamDto examDto = copyToDto(exam);
        return Result.success(examDto);
    }

    /**
     * 根据用户ID和考试ID获取考试记录
     */
    @RequestMapping(value = "/examRecord/listByUserIdAndExamId")
    public Result<List<ExamRecordDto>> listByUserIdAndExamId(@RequestParam("userId") Long userId, @RequestParam("examId") Long examId) {
        List<ExamRecord> recordList = examRecordDao.listByUserIdAndExamId(userId, examId);
        List<ExamRecordDto> recordDtoList = recordList.stream().map(this::copyToRecordDto).toList();
        return Result.success(recordDtoList);
    }

    /**
     * 根据用户ID获取考试记录
     */
    @RequestMapping(value = "/examRecord/listByUserId")
    public Result<List<ExamRecordDto>> listByUserId(@RequestParam("userId") Long userId) {
        List<ExamRecord> recordList = examRecordDao.listByUserId(userId);
        List<ExamRecordDto> recordDtoList = recordList.stream().map(this::copyToRecordDto).toList();
        return Result.success(recordDtoList);
    }

    /**
     * 获取用户考试统计信息
     */
    @RequestMapping(value = "/examRecord/getUserExamStats")
    public Result<String> getUserExamStats(@RequestParam("userId") Long userId) {
        List<ExamRecord> recordList = examRecordDao.listByUserId(userId);
        
        // 统计信息
        long totalExams = recordList.size();
        long passedExams = recordList.stream().filter(record -> record.getIsPassed() == 1).count();
        long failedExams = totalExams - passedExams;
        
        String stats = String.format("总考试次数: %d, 通过次数: %d, 未通过次数: %d", totalExams, passedExams, failedExams);
        return Result.success(stats);
    }

    /**
     * 转换为DTO
     */
    private ExamDto copyToDto(Exam exam) {
        ExamDto dto = new ExamDto();
        BeanUtils.copyProperties(exam, dto);
        return dto;
    }

    /**
     * 转换为考试记录DTO
     */
    private ExamRecordDto copyToRecordDto(ExamRecord record) {
        ExamRecordDto dto = new ExamRecordDto();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}