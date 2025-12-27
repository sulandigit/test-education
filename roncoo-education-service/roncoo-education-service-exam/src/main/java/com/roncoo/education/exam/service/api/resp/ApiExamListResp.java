package com.roncoo.education.exam.service.api.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API-考试列表响应
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "API-考试列表响应")
public class ApiExamListResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 考试名称
     */
    @ApiModelProperty(value = "考试名称")
    private String examName;

    /**
     * 考试描述
     */
    @ApiModelProperty(value = "考试描述")
    private String examDesc;

    /**
     * 考试时长(分钟)
     */
    @ApiModelProperty(value = "考试时长(分钟)")
    private Integer duration;

    /**
     * 及格分数
     */
    @ApiModelProperty(value = "及格分数")
    private Integer passScore;

    /**
     * 总分
     */
    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    /**
     * 考试类型(1:练习模式,2:考试模式)
     */
    @ApiModelProperty(value = "考试类型(1:练习模式,2:考试模式)")
    private Integer examType;

    /**
     * 考试开始时间
     */
    @ApiModelProperty(value = "考试开始时间")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @ApiModelProperty(value = "考试结束时间")
    private LocalDateTime endTime;

    /**
     * 允许考试次数(-1:无限次,其他:具体次数)
     */
    @ApiModelProperty(value = "允许考试次数(-1:无限次,其他:具体次数)")
    private Integer allowTimes;

    /**
     * 参考人数
     */
    @ApiModelProperty(value = "参考人数")
    private Integer examCount;
}