package com.ssa.cms.model;
// Generated Jul 4, 2013 6:03:19 PM by Hibernate Tools 3.2.1.GA


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
@Table(name="IVROptInOut")
public class IvroptInOut  implements java.io.Serializable {


     private Long seqNo;
     private Integer campaignId;
     private String campaignName;
     private Date effectiveDate;
     private String optInOut;
     private String phoneNumber;
     private String statusCode;
     private Integer shortCode;

    public IvroptInOut() {
    }

	
    public IvroptInOut(Date effectiveDate, String optInOut, String phoneNumber) {
        this.effectiveDate = effectiveDate;
        this.optInOut = optInOut;
        this.phoneNumber = phoneNumber;
    }
    public IvroptInOut(Integer campaignId, String campaignName, Date effectiveDate, String optInOut, String phoneNumber, String statusCode, Integer shortCode) {
       this.campaignId = campaignId;
       this.campaignName = campaignName;
       this.effectiveDate = effectiveDate;
       this.optInOut = optInOut;
       this.phoneNumber = phoneNumber;
       this.statusCode = statusCode;
       this.shortCode = shortCode;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="Seq_No", unique=true, nullable=false)
    public Long getSeqNo() {
        return this.seqNo;
    }
    
    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }
    
    @Column(name="Campaign_Id")
    public Integer getCampaignId() {
        return this.campaignId;
    }
    
    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }
    
    @Column(name="Campaign_Name")
    public String getCampaignName() {
        return this.campaignName;
    }
    
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="Effective_Date", nullable=false, length=19)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }
    
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    @Column(name="Opt_In_Out", nullable=false, length=1)
    public String getOptInOut() {
        return this.optInOut;
    }
    
    public void setOptInOut(String optInOut) {
        this.optInOut = optInOut;
    }
    
    @Column(name="Phone_Number", nullable=false, length=10)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Column(name="Status_Code", length=2)
    public String getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    @Column(name="Short_Code")
    public Integer getShortCode() {
        return this.shortCode;
    }
    
    public void setShortCode(Integer shortCode) {
        this.shortCode = shortCode;
    }


}


