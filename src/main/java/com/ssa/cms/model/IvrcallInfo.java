package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="IVRCallInfo")
public class IvrcallInfo  implements java.io.Serializable {


     private IvrcallInfoId id;
     private Campaigns campaigns;
     private String phoneNumber;
     private Integer numberOfCalls;
     private String statusCode;
     private String providerSessionId;
     private Integer ivrserverId;
     private Date createOn;
     private Date lastModifiedOn;
     private Set<IvractionInfo> ivractionInfos = new HashSet<IvractionInfo>(0);

    public IvrcallInfo() {
    }

	
    public IvrcallInfo(IvrcallInfoId id, Campaigns campaigns) {
        this.id = id;
        this.campaigns = campaigns;
    }
    public IvrcallInfo(IvrcallInfoId id, Campaigns campaigns, String phoneNumber, Integer numberOfCalls, String statusCode, String providerSessionId, Integer ivrserverId, Date createOn, Date lastModifiedOn, Set<IvractionInfo> ivractionInfos) {
       this.id = id;
       this.campaigns = campaigns;
       this.phoneNumber = phoneNumber;
       this.numberOfCalls = numberOfCalls;
       this.statusCode = statusCode;
       this.providerSessionId = providerSessionId;
       this.ivrserverId = ivrserverId;
       this.createOn = createOn;
       this.lastModifiedOn = lastModifiedOn;
       this.ivractionInfos = ivractionInfos;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="ivrcallInfoId", column=@Column(name="IVRCallInfoId", nullable=false) ), 
        @AttributeOverride(name="campaignId", column=@Column(name="CampaignId", nullable=false) ) } )
    public IvrcallInfoId getId() {
        return this.id;
    }
    
    public void setId(IvrcallInfoId id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CampaignId", nullable=false, insertable=false, updatable=false)
    public Campaigns getCampaigns() {
        return this.campaigns;
    }
    
    public void setCampaigns(Campaigns campaigns) {
        this.campaigns = campaigns;
    }
    
    @Column(name="PhoneNumber", length=11)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Column(name="NumberOfCalls")
    public Integer getNumberOfCalls() {
        return this.numberOfCalls;
    }
    
    public void setNumberOfCalls(Integer numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }
    
    @Column(name="StatusCode", length=2)
    public String getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    @Column(name="ProviderSessionId", length=50)
    public String getProviderSessionId() {
        return this.providerSessionId;
    }
    
    public void setProviderSessionId(String providerSessionId) {
        this.providerSessionId = providerSessionId;
    }
    
    @Column(name="IVRServerId")
    public Integer getIvrserverId() {
        return this.ivrserverId;
    }
    
    public void setIvrserverId(Integer ivrserverId) {
        this.ivrserverId = ivrserverId;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreateOn", length=19)
    public Date getCreateOn() {
        return this.createOn;
    }
    
    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LastModifiedOn", length=19)
    public Date getLastModifiedOn() {
        return this.lastModifiedOn;
    }
    
    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="ivrcallInfo")
    public Set<IvractionInfo> getIvractionInfos() {
        return this.ivractionInfos;
    }
    
    public void setIvractionInfos(Set<IvractionInfo> ivractionInfos) {
        this.ivractionInfos = ivractionInfos;
    }




}


