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
@Table(name = "MMSCampaignMessageRequestParsedData")
public class MMSCampaignMessageRequestParsedData implements
		java.io.Serializable {

	// Fields

	private Long mmsCmrSeqNo;
	private String transactionMustUnderstand;
	private String cmrqpdMm7version;
	private String cmrqpdVasid;
	private String cmrqpdVaspid;
	private String cmrqpdSenderAddressShortCode;
	private String cmrqpdRecipientsToNumber;
	private String cmrqpdRecipientsToNumberDispOnly;
	private String cmrqpdDeliveryReport;
	private String cmrqpdSubject;
	private String cmrqpdContentAllowAdaptations;
	private String cmrqpdContentHref;
	private Date effectiveDate;	
	private BigDecimal transactionId;

	// Constructors

	/** default constructor */
	public MMSCampaignMessageRequestParsedData() {
	}

	/** minimal constructor */
	public MMSCampaignMessageRequestParsedData(Long mmsCmrSeqNo,
			Timestamp effectiveDate) {
		this.mmsCmrSeqNo = mmsCmrSeqNo;
		this.effectiveDate = effectiveDate;
	}

	/** full constructor */
	public MMSCampaignMessageRequestParsedData(Long mmsCmrSeqNo,
			String transactionMustUnderstand, String cmrqpdMm7version,
			String cmrqpdVasid, String cmrqpdVaspid,
			String cmrqpdSenderAddressShortCode,
			String cmrqpdRecipientsToNumber,
			String cmrqpdRecipientsToNumberDispOnly,
			String cmrqpdDeliveryReport, String cmrqpdSubject,
			String cmrqpdContentAllowAdaptations, String cmrqpdContentHref,
			Date effectiveDate,BigDecimal transactionId) {
		this.mmsCmrSeqNo = mmsCmrSeqNo;
		this.transactionMustUnderstand = transactionMustUnderstand;
		this.cmrqpdMm7version = cmrqpdMm7version;
		this.cmrqpdVasid = cmrqpdVasid;
		this.cmrqpdVaspid = cmrqpdVaspid;
		this.cmrqpdSenderAddressShortCode = cmrqpdSenderAddressShortCode;
		this.cmrqpdRecipientsToNumber = cmrqpdRecipientsToNumber;
		this.cmrqpdRecipientsToNumberDispOnly = cmrqpdRecipientsToNumberDispOnly;
		this.cmrqpdDeliveryReport = cmrqpdDeliveryReport;
		this.cmrqpdSubject = cmrqpdSubject;
		this.cmrqpdContentAllowAdaptations = cmrqpdContentAllowAdaptations;
		this.cmrqpdContentHref = cmrqpdContentHref;
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

	@Column(name = "CMRQPD_MM7Version", length = 5)
	public String getCmrqpdMm7version() {
		return this.cmrqpdMm7version;
	}

	public void setCmrqpdMm7version(String cmrqpdMm7version) {
		this.cmrqpdMm7version = cmrqpdMm7version;
	}

	@Column(name = "CMRQPD_VASID", length = 18)
	public String getCmrqpdVasid() {
		return this.cmrqpdVasid;
	}

	public void setCmrqpdVasid(String cmrqpdVasid) {
		this.cmrqpdVasid = cmrqpdVasid;
	}

	@Column(name = "CMRQPD_VASPID", length = 18)
	public String getCmrqpdVaspid() {
		return this.cmrqpdVaspid;
	}

	public void setCmrqpdVaspid(String cmrqpdVaspid) {
		this.cmrqpdVaspid = cmrqpdVaspid;
	}

	@Column(name = "CMRQPD_SenderAddress_ShortCode", length = 6)
	public String getCmrqpdSenderAddressShortCode() {
		return this.cmrqpdSenderAddressShortCode;
	}

	public void setCmrqpdSenderAddressShortCode(
			String cmrqpdSenderAddressShortCode) {
		this.cmrqpdSenderAddressShortCode = cmrqpdSenderAddressShortCode;
	}

	@Column(name = "CMRQPD_Recipients_To_Number", length = 12)
	public String getCmrqpdRecipientsToNumber() {
		return this.cmrqpdRecipientsToNumber;
	}

	public void setCmrqpdRecipientsToNumber(String cmrqpdRecipientsToNumber) {
		this.cmrqpdRecipientsToNumber = cmrqpdRecipientsToNumber;
	}

	@Column(name = "CMRQPD_Recipients_To_Number_DispOnly", length = 5)
	public String getCmrqpdRecipientsToNumberDispOnly() {
		return this.cmrqpdRecipientsToNumberDispOnly;
	}

	public void setCmrqpdRecipientsToNumberDispOnly(
			String cmrqpdRecipientsToNumberDispOnly) {
		this.cmrqpdRecipientsToNumberDispOnly = cmrqpdRecipientsToNumberDispOnly;
	}

	@Column(name = "CMRQPD_DeliveryReport", length = 5)
	public String getCmrqpdDeliveryReport() {
		return this.cmrqpdDeliveryReport;
	}

	public void setCmrqpdDeliveryReport(String cmrqpdDeliveryReport) {
		this.cmrqpdDeliveryReport = cmrqpdDeliveryReport;
	}

	@Column(name = "CMRQPD_Subject", length = 50)
	public String getCmrqpdSubject() {
		return this.cmrqpdSubject;
	}

	public void setCmrqpdSubject(String cmrqpdSubject) {
		this.cmrqpdSubject = cmrqpdSubject;
	}

	@Column(name = "CMRQPD_Content_AllowAdaptations", length = 5)
	public String getCmrqpdContentAllowAdaptations() {
		return this.cmrqpdContentAllowAdaptations;
	}

	public void setCmrqpdContentAllowAdaptations(
			String cmrqpdContentAllowAdaptations) {
		this.cmrqpdContentAllowAdaptations = cmrqpdContentAllowAdaptations;
	}

	@Column(name = "CMRQPD_Content_Href", length = 50)
	public String getCmrqpdContentHref() {
		return this.cmrqpdContentHref;
	}

	public void setCmrqpdContentHref(String cmrqpdContentHref) {
		this.cmrqpdContentHref = cmrqpdContentHref;
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