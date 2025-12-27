package com.roncoo.education.exam.dao.impl.mapper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试信息
 *
 * @author wujing
 */
public class Exam implements Serializable {

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
     * 排序
     */
    private Integer sort;

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
     * 是否打乱题目顺序(1:打乱,0:不打乱)
     */
    private Integer isRandomQuestion;

    /**
     * 是否打乱选项顺序(1:打乱,0:不打乱)
     */
    private Integer isRandomOption;

    /**
     * 参考人数
     */
    private Integer examCount;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName == null ? null : examName.trim();
    }

    public String getExamDesc() {
        return examDesc;
    }

    public void setExamDesc(String examDesc) {
        this.examDesc = examDesc == null ? null : examDesc.trim();
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
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

    public Integer getAllowTimes() {
        return allowTimes;
    }

    public void setAllowTimes(Integer allowTimes) {
        this.allowTimes = allowTimes;
    }

    public Integer getIsRandomQuestion() {
        return isRandomQuestion;
    }

    public void setIsRandomQuestion(Integer isRandomQuestion) {
        this.isRandomQuestion = isRandomQuestion;
    }

    public Integer getIsRandomOption() {
        return isRandomOption;
    }

    public void setIsRandomOption(Integer isRandomOption) {
        this.isRandomOption = isRandomOption;
    }

    public Integer getExamCount() {
        return examCount;
    }

    public void setExamCount(Integer examCount) {
        this.examCount = examCount;
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
        sb.append(", sort=").append(sort);
        sb.append(", examName=").append(examName);
        sb.append(", examDesc=").append(examDesc);
        sb.append(", courseId=").append(courseId);
        sb.append(", paperId=").append(paperId);
        sb.append(", duration=").append(duration);
        sb.append(", passScore=").append(passScore);
        sb.append(", totalScore=").append(totalScore);
        sb.append(", examType=").append(examType);
        sb.append(", isEnable=").append(isEnable);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", allowTimes=").append(allowTimes);
        sb.append(", isRandomQuestion=").append(isRandomQuestion);
        sb.append(", isRandomOption=").append(isRandomOption);
        sb.append(", examCount=").append(examCount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}