package com.roncoo.education.exam.service.admin.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * ADMIN-考试信息-分页
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "ADMIN-考试信息-分页")
public class AdminExamPageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private int pageCurrent = 1;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true)
    private int pageSize = 20;

    /**
     * 考试名称
     */
    @ApiModelProperty(value = "考试名称")
    private String examName;

    /**
     * 关联课程ID
     */
    @ApiModelProperty(value = "关联课程ID")
    private Long courseId;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;
}