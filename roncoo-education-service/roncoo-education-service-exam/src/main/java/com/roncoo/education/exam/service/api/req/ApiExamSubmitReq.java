package com.roncoo.education.exam.service.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * API-提交考试请求
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-提交考试请求")
public class ApiExamSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    /**
     * 考试记录ID
     */
    @NotNull(message = "考试记录ID不能为空")
    @ApiModelProperty(value = "考试记录ID", required = true)
    private Long examRecordId;
}