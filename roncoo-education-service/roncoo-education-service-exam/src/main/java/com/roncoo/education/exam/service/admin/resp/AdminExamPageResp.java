package com.roncoo.education.exam.service.admin.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ADMIN-考试信息-分页
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "ADMIN-考试信息-分页")
public class AdminExamPageResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 状态(1:正常，0:禁用)
     */
    @ApiModelProperty(value = "状态(1:正常，0:禁用)")
    private Integer statusId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

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
     * 关联课程ID
     */
    @ApiModelProperty(value = "关联课程ID")
    private Long courseId;

    /**
     * 试卷ID
     */
    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

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
     * 是否启用(1:启用,0:禁用)
     */
    @ApiModelProperty(value = "是否启用(1:启用,0:禁用)")
    private Integer isEnable;

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
     * 是否打乱题目顺序(1:打乱,0:不打乱)
     */
    @ApiModelProperty(value = "是否打乱题目顺序(1:打乱,0:不打乱)")
    private Integer isRandomQuestion;

    /**
     * 是否打乱选项顺序(1:打乱,0:不打乱)
     */
    @ApiModelProperty(value = "是否打乱选项顺序(1:打乱,0:不打乱)")
    private Integer isRandomOption;

    /**
     * 参考人数
     */
    @ApiModelProperty(value = "参考人数")
    private Integer examCount;
}