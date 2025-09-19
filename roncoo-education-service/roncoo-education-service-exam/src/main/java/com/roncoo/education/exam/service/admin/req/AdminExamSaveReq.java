package com.roncoo.education.exam.service.admin.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ADMIN-考试信息-添加
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "ADMIN-考试信息-添加")
public class AdminExamSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort = 1;

    /**
     * 考试名称
     */
    @NotBlank(message = "考试名称不能为空")
    @ApiModelProperty(value = "考试名称", required = true)
    private String examName;

    /**
     * 考试描述
     */
    @ApiModelProperty(value = "考试描述")
    private String examDesc;

    /**
     * 关联课程ID
     */
    @ApiModelProperty(value = "关联课程ID")
    private Long courseId;

    /**
     * 试卷ID
     */
    @NotNull(message = "试卷ID不能为空")
    @ApiModelProperty(value = "试卷ID", required = true)
    private Long paperId;

    /**
     * 考试时长(分钟)
     */
    @NotNull(message = "考试时长不能为空")
    @ApiModelProperty(value = "考试时长(分钟)", required = true)
    private Integer duration;

    /**
     * 及格分数
     */
    @NotNull(message = "及格分数不能为空")
    @ApiModelProperty(value = "及格分数", required = true)
    private Integer passScore;

    /**
     * 总分
     */
    @NotNull(message = "总分不能为空")
    @ApiModelProperty(value = "总分", required = true)
    private Integer totalScore;

    /**
     * 考试类型(1:练习模式,2:考试模式)
     */
    @NotNull(message = "考试类型不能为空")
    @ApiModelProperty(value = "考试类型(1:练习模式,2:考试模式)", required = true)
    private Integer examType;

    /**
     * 是否启用(1:启用,0:禁用)
     */
    @ApiModelProperty(value = "是否启用(1:启用,0:禁用)")
    private Integer isEnable = 1;

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
    private Integer allowTimes = -1;

    /**
     * 是否打乱题目顺序(1:打乱,0:不打乱)
     */
    @ApiModelProperty(value = "是否打乱题目顺序(1:打乱,0:不打乱)")
    private Integer isRandomQuestion = 0;

    /**
     * 是否打乱选项顺序(1:打乱,0:不打乱)
     */
    @ApiModelProperty(value = "是否打乱选项顺序(1:打乱,0:不打乱)")
    private Integer isRandomOption = 0;
}