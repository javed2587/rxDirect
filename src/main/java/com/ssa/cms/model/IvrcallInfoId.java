package com.ssa.cms.model;
// Generated Apr 16, 2013 7:26:01 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class IvrcallInfoId  implements java.io.Serializable {


     private long ivrcallInfoId;
     private int campaignId;

    public IvrcallInfoId() {
    }

    public IvrcallInfoId(long ivrcallInfoId, int campaignId) {
       this.ivrcallInfoId = ivrcallInfoId;
       this.campaignId = campaignId;
    }
   

    @Column(name="IVRCallInfoId", nullable=false)
    public long getIvrcallInfoId() {
        return this.ivrcallInfoId;
    }
    
    public void setIvrcallInfoId(long ivrcallInfoId) {
        this.ivrcallInfoId = ivrcallInfoId;
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
		 if ( !(other instanceof IvrcallInfoId) ) return false;
		 IvrcallInfoId castOther = ( IvrcallInfoId ) other; 
         
		 return (this.getIvrcallInfoId()==castOther.getIvrcallInfoId())
 && (this.getCampaignId()==castOther.getCampaignId());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + (int) this.getIvrcallInfoId();
         result = 37 * result + this.getCampaignId();
         return result;
   }   


}


