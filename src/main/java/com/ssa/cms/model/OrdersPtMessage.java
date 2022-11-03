package com.ssa.cms.model;

import com.ssa.cms.modelinterfaces.CommonMessageFunctionalityI;
import com.ssa.cms.modellisteners.MessageListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Zubair
 * 
 */
@Entity
@Table(name = "Pharmacy_PtNotifications")
@EntityListeners(MessageListener.class)
public class OrdersPtMessage implements Serializable, CommonMessageFunctionalityI {

    private Integer id;
    //private String orderNo;
    private Order order;
    private PatientProfile patientProfile;
    private String subject;
    private String message;
    private Boolean isRead;
    private Integer createdBy;
    private Date createdOn;
    private String sentBy;
    private String sentOn;
    private String attachmentPath;
    private String attachmentName;
    private String contentType;
    private Integer isCritical;
    private Integer pointsAwarded;
    private String sort;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false, unique = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    @Column(name = "OrderNo")
//    public String getOrderNo() {
//        return orderNo;
//    }
//
//    public void setOrderNo(String orderNo) {
//        this.orderNo = orderNo;
//    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderNo", nullable = false, insertable = true, updatable = true)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false, insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    @Column(name = "Subject", length = 255)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "Message", length = 1000)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "CreatedBy")
    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedOn")
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "IsRead")
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @Transient
    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    @Transient
    public String getSentOn() {
        return sentOn;
    }

    public void setSentOn(String sentOn) {
        this.sentOn = sentOn;
    }

    @Column(name = "AttachmentPath")
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Column(name = "AttachmentName")
    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    @Column(name = "ContentType")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Column(name = "IsCritical")
    public Integer getIsCritical() {
        return isCritical;
    }

    public void setIsCritical(Integer isCritical) {
        this.isCritical = isCritical;
    }

    @Column(name = "pointsAwarded")
    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    @Transient
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    @Transient
    public String getMessageText() {
        return "";
    }

    @Override
    public void setMessageText(String s) {

    }

    @Override
    @Transient
    public String getPushSubject() {
        return "";
    }

    @Override
    public void setPushSubject(String PushSubject) {

    }

    @Override
    @Transient
    public String getPhoneNumber() {
        return "";
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

}
