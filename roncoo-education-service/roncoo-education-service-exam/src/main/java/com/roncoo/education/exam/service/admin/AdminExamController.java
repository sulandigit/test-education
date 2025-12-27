package com.roncoo.education.exam.service.admin;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.Result;
import com.roncoo.education.exam.service.admin.biz.AdminExamBiz;
import com.roncoo.education.exam.service.admin.req.AdminExamEditReq;
import com.roncoo.education.exam.service.admin.req.AdminExamPageReq;
import com.roncoo.education.exam.service.admin.req.AdminExamSaveReq;
import com.roncoo.education.exam.service.admin.resp.AdminExamPageResp;
import com.roncoo.education.exam.service.admin.resp.AdminExamViewResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * ADMIN-考试信息
 *
 * @author wujing
 */
@Api(tags = "admin-考试信息")
@RestController
@RequiredArgsConstructor
@RequestMapping("/exam/admin/exam")
public class AdminExamController {

    @NotNull
    private final AdminExamBiz biz;

    @ApiOperation(value = "考试信息分页", notes = "考试信息分页")
    @PostMapping(value = "/page")
    public Result<Page<AdminExamPageResp>> page(@RequestBody AdminExamPageReq req) {
        return biz.page(req);
    }

    @ApiOperation(value = "考试信息添加", notes = "考试信息添加")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody @Valid AdminExamSaveReq req) {
        return biz.save(req);
    }

    @ApiOperation(value = "考试信息查看", notes = "考试信息查看")
    @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = Long.class, paramType = "query", required = true)
    @GetMapping(value = "/view")
    public Result<AdminExamViewResp> view(@RequestParam Long id) {
        return biz.view(id);
    }

    @ApiOperation(value = "考试信息修改", notes = "考试信息修改")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody @Valid AdminExamEditReq req) {
        return biz.edit(req);
    }

    @ApiOperation(value = "考试信息删除", notes = "考试信息删除")
    @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = Long.class, paramType = "query", required = true)
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam Long id) {
        return biz.delete(id);
    }
}