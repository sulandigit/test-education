package com.roncoo.education.exam.feign.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试信息DTO
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
public class ExamDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 考试描述
     */
    private String examDesc;

    /**
     * 关联课程ID
     */
    private Long courseId;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 考试时长(分钟)
     */
    private Integer duration;

    /**
     * 及格分数
     */
    private Integer passScore;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 考试类型(1:练习模式,2:考试模式)
     */
    private Integer examType;

    /**
     * 是否启用(1:启用,0:禁用)
     */
    private Integer isEnable;

    /**
     * 考试开始时间
     */
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    private LocalDateTime endTime;

    /**
     * 允许考试次数(-1:无限次,其他:具体次数)
     */
    private Integer allowTimes;

    /**
     * 参考人数
     */
    private Integer examCount;
}