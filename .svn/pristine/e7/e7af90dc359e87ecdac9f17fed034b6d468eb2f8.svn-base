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
@Table(name = "ClinicalMessageLog")
public class ClinicalMessageLog implements Serializable {

    private int id;
    private String communicationId;
    private String communicationMethod;
    private int campaignId;
    private int messageCategoryId;
    private String ndc;
    private int messageOrder;
    private String messageText;
    private String status;
    private Date createdOn;
    private String createdBy;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "LogId", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Column(name = "Status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "MessageCategoryId", nullable = false)
    public int getMessageCategoryId() {
        return messageCategoryId;
    }

    public void setMessageCategoryId(int messageCategoryId) {
        this.messageCategoryId = messageCategoryId;
    }

    @Column(name = "MessageOrder", nullable = false)
    public int getMessageOrder() {
        return messageOrder;
    }

    public void setMessageOrder(int messageOrder) {
        this.messageOrder = messageOrder;
    }

    @Column(name = "MessageText", nullable = false)
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "EffectiveDate", nullable = false)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "CreatedBy", nullable = false)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "Ndc", nullable = false)
    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

}
