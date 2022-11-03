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
@Table(name = "ClinicalMessage")
public class ClinicalMessage implements Serializable {

    private int messageId;
    private String messageCategoryId;
    private String messageCategory;
    private int messageOrder;
    private String messageText;
    private int createdBy;
    private int modifiedBy;
    private Date modifiedOn;
    private Date createdOn;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "MessageId", unique = true, nullable = false)
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Column(name = "MessageCategoryId", nullable = false)
    public String getMessageCategoryId() {
        return messageCategoryId;
    }

    public void setMessageCategoryId(String messageCategoryId) {
        this.messageCategoryId = messageCategoryId;
    }

    @Column(name = "MessageCategory", nullable = false)
    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
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

    @Column(name = "CreatedBy")
    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "ModifiedBy")
    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "ModifiedOn")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Column(name = "CreatedOn")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

}
