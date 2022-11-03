package com.ssa.cms.model;

import com.ssa.cms.modelinterfaces.CommonPatientPaymentInfoI;
import com.ssa.cms.modellisteners.PatientPaymentInfoListener;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author msheraz
 */
@Entity
@Table(name = "PatientProfilePaymentInfo")
@EntityListeners(PatientPaymentInfoListener.class)
public class PatientPaymentInfo extends AuditFields implements Serializable,CommonPatientPaymentInfoI {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String cardHolderName;
    private String cardType;
    private String cardNumber;
    private String expiryDate;
    private String cvvNumber;
    private String defaultCard;
    private PatientProfile patientProfile;
    private String expiryMonth;
    private String expiryYear;
    private String sameAddress;
    private Integer totalRecord;
    private List<TransferRequest> transferRequestList;
    private PatientAddress billingAddress;
    private String address;
    private String apartment;
    private String city;
    private Short stateId;
    private String zip;
    private String state;
    private Integer billingAddressId;

    public PatientPaymentInfo() {
    }

    public PatientPaymentInfo(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "CardHolderName")
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    @Column(name = "CardType")
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Column(name = "CardNumber")
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Column(name = "ExpiryDate")
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    @Transient
    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    @Transient
    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    @Transient
    public String getSameAddress() {
        return sameAddress;
    }

    public void setSameAddress(String sameAddress) {
        this.sameAddress = sameAddress;
    }

    @Column(name = "DefaultCard")
    public String getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(String defaultCard) {
        this.defaultCard = defaultCard;
    }

    @Transient
    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "patientPaymentInfo", orphanRemoval = true)
    public List<TransferRequest> getTransferRequestList() {
        return transferRequestList;
    }

    public void setTransferRequestList(List<TransferRequest> transferRequestList) {
        this.transferRequestList = transferRequestList;
    }

    @Column(name = "CvvNumber")
    public String getCvvNumber() {
        return cvvNumber;
    }

    public void setCvvNumber(String cvvNumber) {
        this.cvvNumber = cvvNumber;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientBillingAddressId", insertable = true, updatable = true)
    public PatientAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(PatientAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Transient
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Transient
    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    @Transient
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Transient
    public Short getStateId() {
        return stateId;
    }

    public void setStateId(Short stateId) {
        this.stateId = stateId;
    }

    @Transient
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Transient
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Transient
    public Integer getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(Integer billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

}
