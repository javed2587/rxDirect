package com.ssa.cms.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author mzubair
 * 
 * CustomerRequest table not exist
 */
//@Entity
//@Table(name = "CustomerRequest")
public class CustomerRequest implements java.io.Serializable {

    private static final long serialVersionUID = 4992470704105622550L;

    private Long crSeqNo;
    private String phoneNumber;
    private Integer campaignId;
    private String campaignName;
    private Integer ycount;
    private String keywordCode;
    private String statusCode;
    private Integer shortCode;
    private Date effectiveDate;
    private Date lastUpdatedOn;
    private String communicationSourceCode;
    private String widgetName;
    private Long inputReferenceNumber;
    private Long ivrId;
    private String cardNumber;
    private String ivrPath;
    private String communicationPath;
    private String yearOfBirth;

    // Constructors
    /**
     * default constructor
     */
    public CustomerRequest() {
    }

    /**
     * minimal constructor
     */
    public CustomerRequest(Integer ycount, Integer shortCode,
            Date effectiveDate, Date lastUpdatedOn) {
        this.ycount = ycount;
        this.shortCode = shortCode;
        this.effectiveDate = effectiveDate;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    /**
     * full constructor
     */
    public CustomerRequest(String phoneNumber, Integer campaignId,
            String campaignName, Integer ycount, String keywordCode,
            String statusCode, Integer shortCode, Date effectiveDate,
            Date lastUpdatedOn, String communicationSourceCode,
            String widgetName, Long inputReferenceNumber, Long ivrId,
            String cardNumber, String ivrPath, String communicationPath) {
        this.phoneNumber = phoneNumber;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.ycount = ycount;
        this.keywordCode = keywordCode;
        this.statusCode = statusCode;
        this.shortCode = shortCode;
        this.effectiveDate = effectiveDate;
        this.lastUpdatedOn = lastUpdatedOn;
        this.communicationSourceCode = communicationSourceCode;
        this.widgetName = widgetName;
        this.inputReferenceNumber = inputReferenceNumber;
        this.ivrId = ivrId;
        this.cardNumber = cardNumber;
        this.ivrPath = ivrPath;
        this.communicationPath = communicationPath;
    }

//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(name = "CR_Seq_No", unique = true, nullable = false)
    public Long getCrSeqNo() {
        return this.crSeqNo;
    }

    public void setCrSeqNo(Long crSeqNo) {
        this.crSeqNo = crSeqNo;
    }

//    @Column(name = "Phone_Number", length = 11)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    @Column(name = "Campaign_Id")
    public Integer getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

//    @Column(name = "Campaign_Name")
    public String getCampaignName() {
        return this.campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

//    @Column(name = "yCount", nullable = false)
    public Integer getYcount() {
        return this.ycount;
    }

    public void setYcount(Integer ycount) {
        this.ycount = ycount;
    }

//    @Column(name = "Keyword_Code", length = 12)
    public String getKeywordCode() {
        return this.keywordCode;
    }

    public void setKeywordCode(String keywordCode) {
        this.keywordCode = keywordCode;
    }

   // @Column(name = "Status_Code", length = 2)
    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

   // @Column(name = "Short_Code", nullable = false)
    public Integer getShortCode() {
        return this.shortCode;
    }

    public void setShortCode(Integer shortCode) {
        this.shortCode = shortCode;
    }

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "Effective_Date", nullable = false, length = 19)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "Last_Updated_On", nullable = false, length = 19)
    public Date getLastUpdatedOn() {
        return this.lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

//    @Column(name = "Communication_Source_Code", length = 6)
    public String getCommunicationSourceCode() {
        return this.communicationSourceCode;
    }

    public void setCommunicationSourceCode(String communicationSourceCode) {
        this.communicationSourceCode = communicationSourceCode;
    }

//    @Column(name = "Widget_Name", length = 10)
    public String getWidgetName() {
        return this.widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

//    @Column(name = "Input_Reference_Number")
    public Long getInputReferenceNumber() {
        return this.inputReferenceNumber;
    }

    public void setInputReferenceNumber(Long inputReferenceNumber) {
        this.inputReferenceNumber = inputReferenceNumber;
    }

//    @Column(name = "IVR_Id")
    public Long getIvrId() {
        return this.ivrId;
    }

    public void setIvrId(Long ivrId) {
        this.ivrId = ivrId;
    }

//    @Column(name = "Card_Number", length = 20)
    public String getCardNumber() {
        return this.cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

//    @Column(name = "IVR_Path", length = 3)
    public String getIvrPath() {
        return this.ivrPath;
    }

    public void setIvrPath(String ivrPath) {
        this.ivrPath = ivrPath;
    }

//    @Column(name = "Communication_Path", length = 10)
    public String getCommunicationPath() {
        return communicationPath;
    }

    public void setCommunicationPath(String communicationPath) {
        this.communicationPath = communicationPath;
    }

//    @Column(name = "YearOfBirth", length = 10)
    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

}
