package com.ssa.cms.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "IVRInputLog")
public class IVRInputLog implements Serializable {

    private int id;
    private String phoneNumber;
    private String memberId;
    private String remortIp;
    private String notificationMessage;
    private String notification_Status;
    private String comments;
    private String responseXml;
    private Date effectiveDate;
    private Date endDate;
    private int campaignId;

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the phoneNumber
     */
    @Column(name = "Phone_Number", length = 20)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the memberId
     */
    @Column(name = "Member_Id", length = 20)
    public String getMemberId() {
        return memberId;
    }

    /**
     * @param memberId the memberId to set
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * @return the remortIp
     */
    @Column(name = "Remote_IP", length = 25)
    public String getRemortIp() {
        return remortIp;
    }

    /**
     * @param remortIp the remortIp to set
     */
    public void setRemortIp(String remortIp) {
        this.remortIp = remortIp;
    }

    /**
     * @return the notificationMessage
     */
    @Column(name = "Notification_Message", length = 250)
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * @param notificationMessage the notificationMessage to set
     */
    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    /**
     * @return the notification_Status
     */
    @Column(name = "Notification_Status", length = 2)
    public String getNotification_Status() {
        return notification_Status;
    }

    /**
     * @param notification_Status the notification_Status to set
     */
    public void setNotification_Status(String notification_Status) {
        this.notification_Status = notification_Status;
    }

    /**
     * @return the comments
     */
    @Column(name = "Comments", length = 250)
    public String getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the responseXml
     */
    @Column(name = "Response_XML", length = 1000)
    public String getResponseXml() {
        return responseXml;
    }

    /**
     * @param responseXml the responseXml to set
     */
    public void setResponseXml(String responseXml) {
        this.responseXml = responseXml;
    }

    /**
     * @return the effectiveDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Effective_Date", nullable = false, length = 19)
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * @param effectiveDate the effectiveDate to set
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * @return the endDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "End_Date", length = 19)
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the campaignId
     */
    @Column(name = "Campaign_Id", length = 20)
    public int getCampaignId() {
        return campaignId;
    }

    /**
     * @param campaignId the campaignId to set
     */
    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }
}
