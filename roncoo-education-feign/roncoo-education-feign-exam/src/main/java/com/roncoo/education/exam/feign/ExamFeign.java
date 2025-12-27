package com.roncoo.education.exam.feign;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.feign.dto.ExamDto;
import com.roncoo.education.exam.feign.dto.ExamRecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 考试模块Feign接口
 *
 * @author wujing
 */
@FeignClient(value = "service-exam")
public interface ExamFeign {

    /**
     * 根据课程ID获取考试列表
     *
     * @param courseId 课程ID
     * @return 考试列表
     */
    @GetMapping(value = "/exam/feign/exam/listByCourseId")
    Result<List<ExamDto>> listByCourseId(@RequestParam("courseId") Long courseId);

    /**
     * 根据ID获取考试信息
     *
     * @param examId 考试ID
     * @return 考试信息
     */
    @GetMapping(value = "/exam/feign/exam/getById")
    Result<ExamDto> getById(@RequestParam("examId") Long examId);

    /**
     * 根据用户ID和考试ID获取考试记录
     *
     * @param userId 用户ID
     * @param examId 考试ID
     * @return 考试记录列表
     */
    @GetMapping(value = "/exam/feign/examRecord/listByUserIdAndExamId")
    Result<List<ExamRecordDto>> listByUserIdAndExamId(@RequestParam("userId") Long userId, @RequestParam("examId") Long examId);

    /**
     * 根据用户ID获取考试记录
     *
     * @param userId 用户ID
     * @return 考试记录列表
     */
    @GetMapping(value = "/exam/feign/examRecord/listByUserId")
    Result<List<ExamRecordDto>> listByUserId(@RequestParam("userId") Long userId);

    /**
     * 获取用户考试统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    @GetMapping(value = "/exam/feign/examRecord/getUserExamStats")
    Result<String> getUserExamStats(@RequestParam("userId") Long userId);
}