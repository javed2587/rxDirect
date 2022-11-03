package com.ssa.cms.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "ClinicalMessageQueue")
public class ClinicalMessageQueue implements Serializable {

    private int queueId;
    private String communicationId;
    private String communicationMethod;
    private int campaignId;
    private int messageCategoryId;
    private int messageOrder;
    private String messageText;
    private String ndc;
    private int scheduleMonth;
    private Date scheduleDate;
    private String createdBy;
    private Date createdOn;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "QueueId", unique = true, nullable = false)
    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    @Column(name = "CommunicationId", nullable = false)
    public String getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    @Column(name = "CommunicationMethod", nullable = false)
    public String getCommunicationMethod() {
        return communicationMethod;
    }

    public void setCommunicationMethod(String communicationMethod) {
        this.communicationMethod = communicationMethod;
    }

    @Column(name = "CampaignId", nullable = false)
    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "MessageText", nullable = false)
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Column(name = "MessageOrder", nullable = false)
    public int getMessageOrder() {
        return messageOrder;
    }

    public void setMessageOrder(int messageOrder) {
        this.messageOrder = messageOrder;
    }

    @Column(name = "ScheduleMonth", nullable = false)
    public int getScheduleMonth() {
        return scheduleMonth;
    }

    public void setScheduleMonth(int scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

    @Column(name = "ScheduleDate", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    @Column(name = "CreatedBy", nullable = false)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "CreatedOn", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "Ndc", nullable = false)
    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    @Column(name = "MessageCategoryId", nullable = false)
    public int getMessageCategoryId() {
        return messageCategoryId;
    }

    public void setMessageCategoryId(int messageCategoryId) {
        this.messageCategoryId = messageCategoryId;
    }

}
