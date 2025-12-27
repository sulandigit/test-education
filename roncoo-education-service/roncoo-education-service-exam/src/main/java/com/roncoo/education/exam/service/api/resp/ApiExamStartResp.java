package com.roncoo.education.exam.service.api.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API-开始考试响应
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-开始考试响应")
public class ApiExamStartResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考试记录ID
     */
    @ApiModelProperty(value = "考试记录ID")
    private Long id;

    /**
     * 考试ID
     */
    @ApiModelProperty(value = "考试ID")
    private Long examId;

    /**
     * 试卷ID
     */
    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    /**
     * 总分
     */
    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    /**
     * 考试状态(1:未开始,2:进行中,3:已完成,4:已超时)
     */
    @ApiModelProperty(value = "考试状态(1:未开始,2:进行中,3:已完成,4:已超时)")
    private Integer examStatus;

    /**
     * 考试次数
     */
    @ApiModelProperty(value = "考试次数")
    private Integer examTimes;
}