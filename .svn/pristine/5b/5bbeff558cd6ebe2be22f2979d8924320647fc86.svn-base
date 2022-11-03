package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="IVRServerInfo")
public class IvrserverInfo  implements java.io.Serializable {


     private int ivrserverId;
     private Integer channelId;
     private String tollFreeNumber;
     private String ivrserverUrl;
     private String isActive;
     private String isDelete;
     private String createdBy;
     private Date createOn;
     private String lastModifiedBy;
     private Date lastModifiedOn;

    public IvrserverInfo() {
    }

	
    public IvrserverInfo(int ivrserverId, String isActive, String isDelete, String createdBy, Date createOn, String lastModifiedBy, Date lastModifiedOn) {
        this.ivrserverId = ivrserverId;
        this.isActive = isActive;
        this.isDelete = isDelete;
        this.createdBy = createdBy;
        this.createOn = createOn;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedOn = lastModifiedOn;
    }
    public IvrserverInfo(int ivrserverId, Integer channelId, String tollFreeNumber, String ivrserverUrl, String isActive, String isDelete, String createdBy, Date createOn, String lastModifiedBy, Date lastModifiedOn) {
       this.ivrserverId = ivrserverId;
       this.channelId = channelId;
       this.tollFreeNumber = tollFreeNumber;
       this.ivrserverUrl = ivrserverUrl;
       this.isActive = isActive;
       this.isDelete = isDelete;
       this.createdBy = createdBy;
       this.createOn = createOn;
       this.lastModifiedBy = lastModifiedBy;
       this.lastModifiedOn = lastModifiedOn;
    }
   
     @Id 
    
    @Column(name="IVRServerId", unique=true, nullable=false)
    public int getIvrserverId() {
        return this.ivrserverId;
    }
    
    public void setIvrserverId(int ivrserverId) {
        this.ivrserverId = ivrserverId;
    }
    
    @Column(name="ChannelId")
    public Integer getChannelId() {
        return this.channelId;
    }
    
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
    
    @Column(name="TollFreeNumber", length=15)
    public String getTollFreeNumber() {
        return this.tollFreeNumber;
    }
    
    public void setTollFreeNumber(String tollFreeNumber) {
        this.tollFreeNumber = tollFreeNumber;
    }
    
    @Column(name="IVRServerUrl", length=100)
    public String getIvrserverUrl() {
        return this.ivrserverUrl;
    }
    
    public void setIvrserverUrl(String ivrserverUrl) {
        this.ivrserverUrl = ivrserverUrl;
    }
    
    @Column(name="IsActive", nullable=false, length=4)
    public String getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    
    @Column(name="IsDelete", nullable=false, length=4)
    public String getIsDelete() {
        return this.isDelete;
    }
    
    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
    
    @Column(name="CreatedBy", nullable=false, length=20)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreateOn", nullable=false, length=19)
    public Date getCreateOn() {
        return this.createOn;
    }
    
    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }
    
    @Column(name="LastModifiedBy", nullable=false, length=20)
    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LastModifiedOn", nullable=false, length=19)
    public Date getLastModifiedOn() {
        return this.lastModifiedOn;
    }
    
    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }




}


