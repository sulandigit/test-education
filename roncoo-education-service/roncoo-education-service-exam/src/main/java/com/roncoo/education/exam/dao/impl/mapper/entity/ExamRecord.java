package com.roncoo.education.exam.dao.impl.mapper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试记录
 *
 * @author wujing
 */
public class ExamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 状态(1:正常，0:禁用)
     */
    private Integer statusId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getUseTime() {
        return useTime;
    }

    public void setUseTime(Integer useTime) {
        this.useTime = useTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getNoAnswerCount() {
        return noAnswerCount;
    }

    public void setNoAnswerCount(Integer noAnswerCount) {
        this.noAnswerCount = noAnswerCount;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    public Integer getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Integer isPassed) {
        this.isPassed = isPassed;
    }

    public Integer getExamTimes() {
        return examTimes;
    }

    public void setExamTimes(Integer examTimes) {
        this.examTimes = examTimes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", statusId=").append(statusId);
        sb.append(", userId=").append(userId);
        sb.append(", examId=").append(examId);
        sb.append(", paperId=").append(paperId);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", useTime=").append(useTime);
        sb.append(", score=").append(score);
        sb.append(", totalScore=").append(totalScore);
        sb.append(", correctCount=").append(correctCount);
        sb.append(", errorCount=").append(errorCount);
        sb.append(", noAnswerCount=").append(noAnswerCount);
        sb.append(", examStatus=").append(examStatus);
        sb.append(", isPassed=").append(isPassed);
        sb.append(", examTimes=").append(examTimes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}