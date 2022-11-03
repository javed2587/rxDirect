package com.ssa.cms.model;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "MMSCampaignMessageReqRes")
public class MMSCampaignMessageReqRes implements java.io.Serializable {

    // Fields
    private Long mmsCmrSeqNo;
    private long crSeqNo;
    private String campaignName;
    private Integer campaignId;
    private Integer shortCode;
    private Integer folderId;
    private Integer messageTypeId;
    private Integer messageId;
    private String phoneNumber;
    private String mmsContext;
    private String mmsRequest;
    private String mmsResponse;
    private Date effectiveDate;
    private String mtId;
    private Long inputReferenceNumber;
    private long intervalId;
    private String intervalDescription;
    private String eventDetail;

    // Constructors
    /**
     * default constructor
     */
    public MMSCampaignMessageReqRes() {
    }

    /**
     * minimal constructor
     */
    public MMSCampaignMessageReqRes(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * full constructor
     */
    public MMSCampaignMessageReqRes(long crSeqNo, String campaignName,
            Integer campaignId, Integer shortCode, Integer folderId,
            Integer messageTypeId, Integer messageId,
            String phoneNumber, String mmsRequest, String mmsResponse,
            Date effectiveDate, String mtId, long intervalId, String intervalDescription,
            String eventDetail) {
        this.crSeqNo = crSeqNo;
        this.campaignName = campaignName;
        this.campaignId = campaignId;
        this.shortCode = shortCode;
        this.folderId = folderId;
        this.messageTypeId = messageTypeId;
        this.messageId = messageId;
        this.phoneNumber = phoneNumber;
        this.mmsRequest = mmsRequest;
        this.mmsResponse = mmsResponse;
        this.effectiveDate = effectiveDate;
        this.mtId = mtId;
        this.intervalId = intervalId;
        this.intervalDescription = intervalDescription;
        this.eventDetail = eventDetail;
    }

    // Property accessors
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "MMS_CMR_Seq_No", unique = true, nullable = false)
    public Long getMmsCmrSeqNo() {
        return this.mmsCmrSeqNo;
    }

    public void setMmsCmrSeqNo(Long mmsCmrSeqNo) {
        this.mmsCmrSeqNo = mmsCmrSeqNo;
    }

    @Column(name = "Campaign_Name")
    public String getCampaignName() {
        return this.campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    @Column(name = "Campaign_Id")
    public Integer getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "Short_Code")
    public Integer getShortCode() {
        return this.shortCode;
    }

    public void setShortCode(Integer shortCode) {
        this.shortCode = shortCode;
    }

    @Column(name = "Folder_Id")
    public Integer getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    @Column(name = "Message_Type_Id")
    public Integer getMessageTypeId() {
        return this.messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    @Column(name = "Message_Id")
    public Integer getMessageId() {
        return this.messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    @Column(name = "MMS_Request", length = 2000)
    public String getMmsRequest() {
        return this.mmsRequest;
    }

    public void setMmsRequest(String mmsRequest) {
        this.mmsRequest = mmsRequest;
    }

    @Column(name = "MMS_Response", length = 2000)
    public String getMmsResponse() {
        return this.mmsResponse;
    }

    public void setMmsResponse(String mmsResponse) {
        this.mmsResponse = mmsResponse;
    }

    @Column(name = "MT_ID", length = 20)
    public String getMtId() {
        return this.mtId;
    }

    public void setMtId(String mtId) {
        this.mtId = mtId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Effective_Date", nullable = false, length = 19)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(name = "CR_Seq_No")
    public long getCrSeqNo() {
        return crSeqNo;
    }

    public void setCrSeqNo(long crSeqNo) {
        this.crSeqNo = crSeqNo;
    }

    @Column(name = "Phone_Number", length = 10)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "MMS_Context", length = 100)
    public String getMmsContext() {
        return mmsContext;
    }

    public void setMmsContext(String mmsContext) {
        this.mmsContext = mmsContext;
    }

    @Column(name = "Input_Reference_No")
    public Long getInputReferenceNumber() {
        return inputReferenceNumber;
    }

    public void setInputReferenceNumber(Long inputReferenceNumber) {
        this.inputReferenceNumber = inputReferenceNumber;
    }

    @Column(name = "Interval_Id")
    public long getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(long intervalId) {
        this.intervalId = intervalId;
    }

    @Column(name = "Interval_Description", length = 50)
    public String getIntervalDescription() {
        return intervalDescription;
    }

    public void setIntervalDescription(String intervalDescription) {
        this.intervalDescription = intervalDescription;
    }

    @Column(name = "Event_Detail", length = 50)
    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }
}