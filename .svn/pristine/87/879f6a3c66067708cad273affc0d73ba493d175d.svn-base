package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class IvractionInfoId  implements java.io.Serializable {


     private int ivractionInfoId;
     private int campaignId;
     private long ivrcallInfoId;
     private int ivrlevelId;

    public IvractionInfoId() {
    }

    public IvractionInfoId(int ivractionInfoId, int campaignId, long ivrcallInfoId, int ivrlevelId) {
       this.ivractionInfoId = ivractionInfoId;
       this.campaignId = campaignId;
       this.ivrcallInfoId = ivrcallInfoId;
       this.ivrlevelId = ivrlevelId;
    }
   

    @Column(name="IVRActionInfoId", nullable=false)
    public int getIvractionInfoId() {
        return this.ivractionInfoId;
    }
    
    public void setIvractionInfoId(int ivractionInfoId) {
        this.ivractionInfoId = ivractionInfoId;
    }

    @Column(name="CampaignId", nullable=false)
    public int getCampaignId() {
        return this.campaignId;
    }
    
    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name="IVRCallInfoId", nullable=false)
    public long getIvrcallInfoId() {
        return this.ivrcallInfoId;
    }
    
    public void setIvrcallInfoId(long ivrcallInfoId) {
        this.ivrcallInfoId = ivrcallInfoId;
    }

    @Column(name="IVRLevelId", nullable=false)
    public int getIvrlevelId() {
        return this.ivrlevelId;
    }
    
    public void setIvrlevelId(int ivrlevelId) {
        this.ivrlevelId = ivrlevelId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof IvractionInfoId) ) return false;
		 IvractionInfoId castOther = ( IvractionInfoId ) other; 
         
		 return (this.getIvractionInfoId()==castOther.getIvractionInfoId())
 && (this.getCampaignId()==castOther.getCampaignId())
 && (this.getIvrcallInfoId()==castOther.getIvrcallInfoId())
 && (this.getIvrlevelId()==castOther.getIvrlevelId());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIvractionInfoId();
         result = 37 * result + this.getCampaignId();
         result = 37 * result + (int) this.getIvrcallInfoId();
         result = 37 * result + this.getIvrlevelId();
         return result;
   }   


}


