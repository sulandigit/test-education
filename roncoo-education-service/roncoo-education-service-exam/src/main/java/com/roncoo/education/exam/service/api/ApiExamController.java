package com.roncoo.education.exam.service.api;

import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.service.api.biz.ApiExamBiz;
import com.roncoo.education.exam.service.api.req.ApiExamStartReq;
import com.roncoo.education.exam.service.api.req.ApiExamSubmitReq;
import com.roncoo.education.exam.service.api.resp.ApiExamDetailResp;
import com.roncoo.education.exam.service.api.resp.ApiExamListResp;
import com.roncoo.education.exam.service.api.resp.ApiExamStartResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * API-考试信息
 *
 * @author wujing
 */
@Api(tags = "api-考试信息")
@RestController
@RequiredArgsConstructor
@RequestMapping("/exam/api/exam")
public class ApiExamController {

    @NotNull
    private final ApiExamBiz biz;

    @ApiOperation(value = "根据课程ID获取考试列表", notes = "根据课程ID获取考试列表")
    @ApiImplicitParam(name = "courseId", value = "课程ID", dataTypeClass = Long.class, paramType = "query", required = true)
    @GetMapping(value = "/listByCourseId")
    public Result<List<ApiExamListResp>> listByCourseId(@RequestParam Long courseId) {
        return biz.listByCourseId(courseId);
    }

    @ApiOperation(value = "获取考试详情", notes = "获取考试详情")
    @ApiImplicitParam(name = "examId", value = "考试ID", dataTypeClass = Long.class, paramType = "query", required = true)
    @GetMapping(value = "/detail")
    public Result<ApiExamDetailResp> detail(@RequestParam Long examId) {
        return biz.detail(examId);
    }

    @ApiOperation(value = "开始考试", notes = "开始考试")
    @PostMapping(value = "/start")
    public Result<ApiExamStartResp> startExam(@RequestBody @Valid ApiExamStartReq req) {
        return biz.startExam(req);
    }

    @ApiOperation(value = "提交考试", notes = "提交考试")
    @PostMapping(value = "/submit")
    public Result<String> submitExam(@RequestBody @Valid ApiExamSubmitReq req) {
        return biz.submitExam(req);
    }
}