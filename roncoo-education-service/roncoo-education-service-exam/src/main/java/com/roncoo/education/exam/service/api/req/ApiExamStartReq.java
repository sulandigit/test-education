package com.roncoo.education.exam.service.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * API-开始考试请求
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-开始考试请求")
public class ApiExamStartReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    @ApiModelProperty(value = "考试ID", required = true)
    private Long examId;
}