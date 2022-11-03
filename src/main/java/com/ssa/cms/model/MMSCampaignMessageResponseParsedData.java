package com.ssa.cms.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "MMSCampaignMessageResponseParsedData")
public class MMSCampaignMessageResponseParsedData implements java.io.Serializable {

    // Fields
    private Long mmsCmrSeqNo;
    private String transactionMustUnderstand;
    private String cmrspdMm7version;
    private String cmrspdStatusCode;
    private String cmrspdStatusText;
    private String cmrspdMessageId;
    private Date effectiveDate;
    private BigDecimal transactionId;

    // Constructors
    /**
     * default constructor
     */
    public MMSCampaignMessageResponseParsedData() {
    }

    /**
     * minimal constructor
     */
    public MMSCampaignMessageResponseParsedData(Long mmsCmrSeqNo,
            Timestamp effectiveDate) {
        this.mmsCmrSeqNo = mmsCmrSeqNo;
        this.effectiveDate = effectiveDate;
    }

    /**
     * full constructor
     */
    public MMSCampaignMessageResponseParsedData(Long mmsCmrSeqNo,
            String transactionMustUnderstand, String cmrspdMm7version,
            String cmrspdStatusCode, String cmrspdStatusText,
            String cmrspdMessageId, Date effectiveDate, Timestamp endDate,
            BigDecimal transactionId) {
        this.mmsCmrSeqNo = mmsCmrSeqNo;
        this.transactionMustUnderstand = transactionMustUnderstand;
        this.cmrspdMm7version = cmrspdMm7version;
        this.cmrspdStatusCode = cmrspdStatusCode;
        this.cmrspdStatusText = cmrspdStatusText;
        this.cmrspdMessageId = cmrspdMessageId;
        this.effectiveDate = effectiveDate;
        this.transactionId = transactionId;
    }

    // Property accessors
    @Id
    @Column(name = "MMS_CMR_Seq_No", unique = true, nullable = false)
    public Long getMmsCmrSeqNo() {
        return this.mmsCmrSeqNo;
    }

    public void setMmsCmrSeqNo(Long mmsCmrSeqNo) {
        this.mmsCmrSeqNo = mmsCmrSeqNo;
    }

    @Column(name = "Transaction_Must_Understand", length = 5)
    public String getTransactionMustUnderstand() {
        return this.transactionMustUnderstand;
    }

    public void setTransactionMustUnderstand(String transactionMustUnderstand) {
        this.transactionMustUnderstand = transactionMustUnderstand;
    }

    @Column(name = "CMRSPD_MM7Version", length = 5)
    public String getCmrspdMm7version() {
        return this.cmrspdMm7version;
    }

    public void setCmrspdMm7version(String cmrspdMm7version) {
        this.cmrspdMm7version = cmrspdMm7version;
    }

    @Column(name = "CMRSPD_StatusCode", length = 6)
    public String getCmrspdStatusCode() {
        return this.cmrspdStatusCode;
    }

    public void setCmrspdStatusCode(String cmrspdStatusCode) {
        this.cmrspdStatusCode = cmrspdStatusCode;
    }

    @Column(name = "CMRSPD_StatusText", length = 200)
    public String getCmrspdStatusText() {
        return this.cmrspdStatusText;
    }

    public void setCmrspdStatusText(String cmrspdStatusText) {
        this.cmrspdStatusText = cmrspdStatusText;
    }

    @Column(name = "CMRSPD_MessageID", length = 50)
    public String getCmrspdMessageId() {
        return this.cmrspdMessageId;
    }

    public void setCmrspdMessageId(String cmrspdMessageId) {
        this.cmrspdMessageId = cmrspdMessageId;
    }

    @Column(name = "Transaction_ID", precision = 20, scale = 0)
    public BigDecimal getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(BigDecimal transactionId) {
        this.transactionId = transactionId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Effective_Date", nullable = false, length = 19)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}