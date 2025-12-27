package com.roncoo.education.exam.dao.impl.mapper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目答案选项
 *
 * @author wujing
 */
public class ExamAnswer implements Serializable {

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
     * 题目ID
     */
    private Long questionId;

    /**
     * 选项标识(A,B,C,D等)
     */
    private String answerTag;

    /**
     * 选项内容
     */
    private String answerContent;

    /**
     * 是否正确答案(1:正确,0:错误)
     */
    private Integer isCorrect;

    /**
     * 选项序号
     */
    private Integer answerNo;

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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswerTag() {
        return answerTag;
    }

    public void setAnswerTag(String answerTag) {
        this.answerTag = answerTag == null ? null : answerTag.trim();
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent == null ? null : answerContent.trim();
    }

    public Integer getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Integer isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getAnswerNo() {
        return answerNo;
    }

    public void setAnswerNo(Integer answerNo) {
        this.answerNo = answerNo;
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
        sb.append(", questionId=").append(questionId);
        sb.append(", answerTag=").append(answerTag);
        sb.append(", answerContent=").append(answerContent);
        sb.append(", isCorrect=").append(isCorrect);
        sb.append(", answerNo=").append(answerNo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}