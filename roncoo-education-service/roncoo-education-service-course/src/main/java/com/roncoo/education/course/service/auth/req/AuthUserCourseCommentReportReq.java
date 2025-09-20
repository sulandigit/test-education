package com.roncoo.education.course.service.auth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * API-AUTH-课程评论举报
 * </p>
 *
 * @author assistant
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-AUTH-课程评论举报")
public class AuthUserCourseCommentReportReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "评论ID不能为空")
    @ApiModelProperty(value = "评论ID", required = true)
    private Long commentId;

    @NotNull(message = "举报类型不能为空")
    @ApiModelProperty(value = "举报类型：1-垃圾广告，2-违法违规，3-不良信息，4-其他", required = true)
    private Integer reportType;

    @NotBlank(message = "举报原因不能为空")
    @ApiModelProperty(value = "举报原因", required = true)
    private String reportReason;
}