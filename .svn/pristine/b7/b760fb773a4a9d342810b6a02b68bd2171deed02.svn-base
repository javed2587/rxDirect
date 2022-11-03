package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="IVRActionInfo")
public class IvractionInfo  implements java.io.Serializable {


     private IvractionInfoId id;
     private IvrcallInfo ivrcallInfo;
     private Ivrlevel ivrlevel;
     private String ivractionSelectedOption;
     private Date createOn;
     private Date lastModifiedOn;

    public IvractionInfo() {
    }

	
    public IvractionInfo(IvractionInfoId id, IvrcallInfo ivrcallInfo, Ivrlevel ivrlevel) {
        this.id = id;
        this.ivrcallInfo = ivrcallInfo;
        this.ivrlevel = ivrlevel;
    }
    public IvractionInfo(IvractionInfoId id, IvrcallInfo ivrcallInfo, Ivrlevel ivrlevel, String ivractionSelectedOption, Date createOn, Date lastModifiedOn) {
       this.id = id;
       this.ivrcallInfo = ivrcallInfo;
       this.ivrlevel = ivrlevel;
       this.ivractionSelectedOption = ivractionSelectedOption;
       this.createOn = createOn;
       this.lastModifiedOn = lastModifiedOn;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="ivractionInfoId", column=@Column(name="IVRActionInfoId", nullable=false) ), 
        @AttributeOverride(name="campaignId", column=@Column(name="CampaignId", nullable=false) ), 
        @AttributeOverride(name="ivrcallInfoId", column=@Column(name="IVRCallInfoId", nullable=false) ), 
        @AttributeOverride(name="ivrlevelId", column=@Column(name="IVRLevelId", nullable=false) ) } )
    public IvractionInfoId getId() {
        return this.id;
    }
    
    public void setId(IvractionInfoId id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns( { 
        @JoinColumn(name="IVRCallInfoId", referencedColumnName="IVRCallInfoId", nullable=false, insertable=false, updatable=false), 
        @JoinColumn(name="CampaignId", referencedColumnName="CampaignId", nullable=false, insertable=false, updatable=false) } )
    public IvrcallInfo getIvrcallInfo() {
        return this.ivrcallInfo;
    }
    
    public void setIvrcallInfo(IvrcallInfo ivrcallInfo) {
        this.ivrcallInfo = ivrcallInfo;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns( { 
        @JoinColumn(name="IVRLevelId", referencedColumnName="IVRLevelId", nullable=false, insertable=false, updatable=false), 
        @JoinColumn(name="CampaignId", referencedColumnName="CampaignId", nullable=false, insertable=false, updatable=false) } )
    public Ivrlevel getIvrlevel() {
        return this.ivrlevel;
    }
    
    public void setIvrlevel(Ivrlevel ivrlevel) {
        this.ivrlevel = ivrlevel;
    }
    
    @Column(name="IVRActionSelectedOption", length=20)
    public String getIvractionSelectedOption() {
        return this.ivractionSelectedOption;
    }
    
    public void setIvractionSelectedOption(String ivractionSelectedOption) {
        this.ivractionSelectedOption = ivractionSelectedOption;
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




}


