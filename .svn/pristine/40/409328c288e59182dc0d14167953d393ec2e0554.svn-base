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
@Table(name="IVRLevel")
public class Ivrlevel  implements java.io.Serializable {


     private IvrlevelId id;
     private Campaigns campaigns;
     private String ivrlevelCode;
     private String ivrlevelShortDescription;
     private String ivrlevelLongDescription;
     private String isActive;
     private String isDelete;
     private String createdBy;
     private Date createOn;
     private String lastModifiedBy;
     private Date lastModifiedOn;
     private Set<IvractionInfo> ivractionInfos = new HashSet<IvractionInfo>(0);

    public Ivrlevel() {
    }

	
    public Ivrlevel(IvrlevelId id, Campaigns campaigns) {
        this.id = id;
        this.campaigns = campaigns;
    }
    public Ivrlevel(IvrlevelId id, Campaigns campaigns, String ivrlevelCode, String ivrlevelShortDescription, String ivrlevelLongDescription, String isActive, String isDelete, String createdBy, Date createOn, String lastModifiedBy, Date lastModifiedOn, Set<IvractionInfo> ivractionInfos) {
       this.id = id;
       this.campaigns = campaigns;
       this.ivrlevelCode = ivrlevelCode;
       this.ivrlevelShortDescription = ivrlevelShortDescription;
       this.ivrlevelLongDescription = ivrlevelLongDescription;
       this.isActive = isActive;
       this.isDelete = isDelete;
       this.createdBy = createdBy;
       this.createOn = createOn;
       this.lastModifiedBy = lastModifiedBy;
       this.lastModifiedOn = lastModifiedOn;
       this.ivractionInfos = ivractionInfos;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="ivrlevelId", column=@Column(name="IVRLevelId", nullable=false) ), 
        @AttributeOverride(name="campaignId", column=@Column(name="CampaignId", nullable=false) ) } )
    public IvrlevelId getId() {
        return this.id;
    }
    
    public void setId(IvrlevelId id) {
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
    
    @Column(name="IVRLevelCode", length=5)
    public String getIvrlevelCode() {
        return this.ivrlevelCode;
    }
    
    public void setIvrlevelCode(String ivrlevelCode) {
        this.ivrlevelCode = ivrlevelCode;
    }
    
    @Column(name="IVRLevelShortDescription", length=100)
    public String getIvrlevelShortDescription() {
        return this.ivrlevelShortDescription;
    }
    
    public void setIvrlevelShortDescription(String ivrlevelShortDescription) {
        this.ivrlevelShortDescription = ivrlevelShortDescription;
    }
    
    @Column(name="IVRLevelLongDescription", length=65535)
    public String getIvrlevelLongDescription() {
        return this.ivrlevelLongDescription;
    }
    
    public void setIvrlevelLongDescription(String ivrlevelLongDescription) {
        this.ivrlevelLongDescription = ivrlevelLongDescription;
    }
    
    @Column(name="IsActive", length=4)
    public String getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    
    @Column(name="IsDelete", length=4)
    public String getIsDelete() {
        return this.isDelete;
    }
    
    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
    
    @Column(name="CreatedBy", length=20)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreateOn", length=19)
    public Date getCreateOn() {
        return this.createOn;
    }
    
    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }
    
    @Column(name="LastModifiedBy", length=20)
    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LastModifiedOn", length=19)
    public Date getLastModifiedOn() {
        return this.lastModifiedOn;
    }
    
    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="ivrlevel")
    public Set<IvractionInfo> getIvractionInfos() {
        return this.ivractionInfos;
    }
    
    public void setIvractionInfos(Set<IvractionInfo> ivractionInfos) {
        this.ivractionInfos = ivractionInfos;
    }




}


