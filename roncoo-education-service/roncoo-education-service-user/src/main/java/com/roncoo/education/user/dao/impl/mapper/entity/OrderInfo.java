package com.roncoo.education.user.dao.impl.mapper.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@FluentMybatis(table = "order_info")

public class OrderInfo extends RichEntity implements Serializable {
    @TableId
    private Long id;

    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    @TableField("order_no")
    private Long orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("mobile")
    private String mobile;

    @TableField("register_time")
    private LocalDateTime registerTime;

    @TableField("course_id")
    private Long courseId;

    @TableField("ruling_price")
    private BigDecimal rulingPrice;

    @TableField("course_price")
    private BigDecimal coursePrice;

    @TableField("pay_type")
    private Integer payType;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField("remark_cus")
    private String remarkCus;

    @TableField("remark")
    private String remark;

    @TableField("pay_time")
    private LocalDateTime payTime;

    private static final long serialVersionUID = 1L;

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

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public BigDecimal getRulingPrice() {
        return rulingPrice;
    }

    public void setRulingPrice(BigDecimal rulingPrice) {
        this.rulingPrice = rulingPrice;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRemarkCus() {
        return remarkCus;
    }

    public void setRemarkCus(String remarkCus) {
        this.remarkCus = remarkCus == null ? null : remarkCus.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
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
        sb.append(", orderNo=").append(orderNo);
        sb.append(", userId=").append(userId);
        sb.append(", mobile=").append(mobile);
        sb.append(", registerTime=").append(registerTime);
        sb.append(", courseId=").append(courseId);
        sb.append(", rulingPrice=").append(rulingPrice);
        sb.append(", coursePrice=").append(coursePrice);
        sb.append(", payType=").append(payType);
        sb.append(", orderStatus=").append(orderStatus);
        sb.append(", remarkCus=").append(remarkCus);
        sb.append(", remark=").append(remark);
        sb.append(", payTime=").append(payTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}