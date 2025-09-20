package com.roncoo.education.course.service.auth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * API-AUTH-课程评论点赞
 * </p>
 *
 * @author assistant
 * @date 2025-09-20
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-AUTH-课程评论点赞")
public class AuthUserCourseCommentLikeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "评论ID不能为空")
    @ApiModelProperty(value = "评论ID", required = true)
    private Long commentId;

    @ApiModelProperty(value = "点赞类型：1-点赞，0-取消点赞", required = true)
    private Integer likeType = 1;
}