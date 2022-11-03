package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class IvrlevelId  implements java.io.Serializable {


     private int ivrlevelId;
     private int campaignId;

    public IvrlevelId() {
    }

    public IvrlevelId(int ivrlevelId, int campaignId) {
       this.ivrlevelId = ivrlevelId;
       this.campaignId = campaignId;
    }
   

    @Column(name="IVRLevelId", nullable=false)
    public int getIvrlevelId() {
        return this.ivrlevelId;
    }
    
    public void setIvrlevelId(int ivrlevelId) {
        this.ivrlevelId = ivrlevelId;
    }

    @Column(name="CampaignId", nullable=false)
    public int getCampaignId() {
        return this.campaignId;
    }
    
    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof IvrlevelId) ) return false;
		 IvrlevelId castOther = ( IvrlevelId ) other; 
         
		 return (this.getIvrlevelId()==castOther.getIvrlevelId())
 && (this.getCampaignId()==castOther.getCampaignId());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIvrlevelId();
         result = 37 * result + this.getCampaignId();
         return result;
   }   


}


