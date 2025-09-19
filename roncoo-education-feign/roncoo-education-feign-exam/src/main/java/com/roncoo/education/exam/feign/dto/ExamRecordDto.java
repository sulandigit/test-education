package com.roncoo.education.exam.feign.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试记录DTO
 *
 * @author wujing
 */
@Data
@Accessors(chain = true)
public class ExamRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 用时(秒)
     */
    private Integer useTime;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 正确题数
     */
    private Integer correctCount;

    /**
     * 错误题数
     */
    private Integer errorCount;

    /**
     * 未答题数
     */
    private Integer noAnswerCount;

    /**
     * 考试状态(1:未开始,2:进行中,3:已完成,4:已超时)
     */
    private Integer examStatus;

    /**
     * 是否通过(1:通过,0:未通过)
     */
    private Integer isPassed;

    /**
     * 考试次数
     */
    private Integer examTimes;
}