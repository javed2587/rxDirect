package com.ssa.cms.model;

import com.ssa.cms.modelinterfaces.CommonPatientFunctionalityI;
import com.ssa.cms.modellisteners.PatientListener;
import com.ssa.cms.util.CustomJsonDmyFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.OrderBy;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Saber Hussain
 */
@Entity
@Table(name = "PatientProfileInfo")
@EntityListeners(PatientListener.class)
public class PatientProfile extends AuditFields implements Serializable, CommonPatientFunctionalityI {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private Integer verificationCode;
    private String emailAddress;
    private Date dob;
    private String birthDate;
    private String gender;
    private String allergies;
    private List<PatientPaymentInfo> paymentInfoList;
    private String cardHolderRelation;
    private String securityToken;
    private String memberId;
    private Integer discountPercentage;
    private String status;
    private String comments;
    private String alternatePhoneNumber;
    private PatientAddress billingAddress;
    private List<ContactUs> contactUsList;
    private List<NotificationMessages> notificationMessagesList;
    private List<Order> orders;
    private List<PatientProfileHealth> patientProfileHealthsList;
    private List<PatientDeliveryAddress> patientDeliveryAddresses;
    private String miles;
    private BigDecimal deliveryFee;
    private List<DrugSearches> drugSearchesList;
    private List<PatientGlucoseResults> patientGlucoseResultsList;
    private List<PatientProfileNotes> patientProfileNotesList;
    private List<YearEndStatementInfo> yearEndStatementInfoList;
    private List<OrderBatch> orderBatchs;
    private List<MessageResponses> messageResponseses;
    private List<OrdersPtMessage> ordersPtMessagesList;
    private String signature;

    private String allergyStatus;
    private String successOrFailure;
    private String description;
    private String insuranceProvider;
    private String planId;
    private String groupNumber;
    private String providerPhoneNumber;
    private String providerAddress;
    private Date insuranceExpiryDate;
    private String insuranceBackCardPath;
    private String insuranceFrontCardPath;
    private DeliveryPreferences deliveryPreferenceId;
    private List<PatientProfileMembers> patientProfileMembersList;
    private Long totalRewardPoints;
    private Integer dprefaId;

    private String osType;
    private String deviceToken;
    private String state;
    private boolean isOldPatient;
    private String defaultAddress;
    private String defaultAddresszip;
    private float stateTaxPercent;
    private Long dependentCount;
    private Long insCardCount;
    private long dependentInsCardCount;

    public PatientProfile() {
    }

    public PatientProfile(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MobileNumber")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Column(name = "FirstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "LastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Transient
//    @Column(name = "Dob")
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonSerialize(using = CustomJsonDmyFormat.class)
    @Override
    public Date getDob() {
        return dob;
    }

    @Override
    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Column(name = "Gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Column(name = "CardHolderRelationship")
    public String getCardHolderRelation() {
        return cardHolderRelation;
    }

    public void setCardHolderRelation(String cardHolderRelation) {
        this.cardHolderRelation = cardHolderRelation;
    }

    @Column(name = "Allergies")
    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    @JoinColumn(name = "BillingAddressId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public PatientAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(PatientAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Column(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "Comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Transient
    public String getAllergyStatus() {
        return allergyStatus;
    }

    public void setAllergyStatus(String allergyStatus) {
        this.allergyStatus = allergyStatus;
    }

    @JoinColumn(name = "DeliveryPreferenceId", referencedColumnName = "Id")
    @ManyToOne
    public DeliveryPreferences getDeliveryPreferenceId() {
        return deliveryPreferenceId;
    }

    @Column(name = "VerificationCode")
    public Integer getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Integer verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setDeliveryPreferenceId(DeliveryPreferences deliveryPreferenceId) {
        this.deliveryPreferenceId = deliveryPreferenceId;
    }

    @Column(name = "EmailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "MemberId")
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @Column(name = "DiscountPercentage")
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Transient
    public String getSuccessOrFailure() {
        return successOrFailure;
    }

    public void setSuccessOrFailure(String successOrFailure) {
        this.successOrFailure = successOrFailure;
    }

    @Transient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "InsuranceProvider")
    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    @Column(name = "PlanId")
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Column(name = "GroupNumber")
    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    @Column(name = "ProviderPhoneNumber")
    public String getProviderPhoneNumber() {
        return providerPhoneNumber;
    }

    public void setProviderPhoneNumber(String providerPhoneNumber) {
        this.providerPhoneNumber = providerPhoneNumber;
    }

    @Column(name = "ProviderAddress")
    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    @Column(name = "InsuranceExpiryDate")
    @Temporal(javax.persistence.TemporalType.DATE)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    public Date getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(Date insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    @Column(name = "SecurityToken")
    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    @Column(name = "InsuranceBackCardPath")
    public String getInsuranceBackCardPath() {
        return insuranceBackCardPath;
    }

    public void setInsuranceBackCardPath(String insuranceBackCardPath) {
        this.insuranceBackCardPath = insuranceBackCardPath;
    }

    @Column(name = "InsuranceFrontCardPath")
    public String getInsuranceFrontCardPath() {
        return insuranceFrontCardPath;
    }

    public void setInsuranceFrontCardPath(String insuranceFrontCardPath) {
        this.insuranceFrontCardPath = insuranceFrontCardPath;
    }

    @Column(name = "AlternatePhoneNumber")
    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    @Transient
    public List<PatientProfileMembers> getPatientProfileMembersList() {
        return patientProfileMembersList;
    }

    public void setPatientProfileMembersList(List<PatientProfileMembers> patientProfileMembersList) {
        this.patientProfileMembersList = patientProfileMembersList;
    }

    @Transient
    public Long getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(Long totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<ContactUs> getContactUsList() {
        return contactUsList;
    }

    public void setContactUsList(List<ContactUs> contactUsList) {
        this.contactUsList = contactUsList;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<NotificationMessages> getNotificationMessagesList() {
        return notificationMessagesList;
    }

    public void setNotificationMessagesList(List<NotificationMessages> notificationMessagesList) {
        this.notificationMessagesList = notificationMessagesList;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<PatientProfileHealth> getPatientProfileHealthsList() {
        return patientProfileHealthsList;
    }

    public void setPatientProfileHealthsList(List<PatientProfileHealth> patientProfileHealthsList) {
        this.patientProfileHealthsList = patientProfileHealthsList;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy(clause = "defaultAddress ASC")
    public List<PatientDeliveryAddress> getPatientDeliveryAddresses() {
        return patientDeliveryAddresses;
    }

    public void setPatientDeliveryAddresses(List<PatientDeliveryAddress> patientDeliveryAddresses) {
        this.patientDeliveryAddresses = patientDeliveryAddresses;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<PatientPaymentInfo> getPaymentInfoList() {
        return paymentInfoList;
    }

    public void setPaymentInfoList(List<PatientPaymentInfo> paymentInfoList) {
        this.paymentInfoList = paymentInfoList;
    }

    @Transient
    public Integer getDprefaId() {
        return dprefaId;
    }

    public void setDprefaId(Integer dprefaId) {
        this.dprefaId = dprefaId;
    }

    @Column(name = "DeliveryFee")
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    @Column(name = "Distance")
    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    @Column(name = "OS_TYPE")
    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    @Column(name = "DeviceToken")
    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Transient
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Transient
    public boolean isIsOldPatient() {
        return isOldPatient;
    }

    public void setIsOldPatient(boolean isOldPatient) {
        this.isOldPatient = isOldPatient;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<DrugSearches> getDrugSearchesList() {
        return drugSearchesList;
    }

    public void setDrugSearchesList(List<DrugSearches> drugSearchesList) {
        this.drugSearchesList = drugSearchesList;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<PatientGlucoseResults> getPatientGlucoseResultsList() {
        return patientGlucoseResultsList;
    }

    public void setPatientGlucoseResultsList(List<PatientGlucoseResults> patientGlucoseResultsList) {
        this.patientGlucoseResultsList = patientGlucoseResultsList;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<PatientProfileNotes> getPatientProfileNotesList() {
        return patientProfileNotesList;
    }

    public void setPatientProfileNotesList(List<PatientProfileNotes> patientProfileNotesList) {
        this.patientProfileNotesList = patientProfileNotesList;
    }

    @Transient
    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @Transient
    public String getDefaultAddresszip() {
        return defaultAddresszip;
    }

    public void setDefaultAddresszip(String defaultAddresszip) {
        this.defaultAddresszip = defaultAddresszip;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<YearEndStatementInfo> getYearEndStatementInfoList() {
        return yearEndStatementInfoList;
    }

    public void setYearEndStatementInfoList(List<YearEndStatementInfo> yearEndStatementInfoList) {
        this.yearEndStatementInfoList = yearEndStatementInfoList;
    }

    @Transient
    public float getStateTaxPercent() {
        return stateTaxPercent;
    }

    public void setStateTaxPercent(float stateTaxPercent) {
        this.stateTaxPercent = stateTaxPercent;
    }

    @Transient
    public Long getDependentCount() {
        return dependentCount;
    }

    public void setDependentCount(Long dependentCount) {
        this.dependentCount = dependentCount;
    }

    @Transient
    public Long getInsCardCount() {
        return insCardCount;
    }

    public void setInsCardCount(Long insCardCount) {
        this.insCardCount = insCardCount;
    }

    @Transient
    public long getDependentInsCardCount() {
        return dependentInsCardCount;
    }

    public void setDependentInsCardCount(long dependentInsCardCount) {
        this.dependentInsCardCount = dependentInsCardCount;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<OrderBatch> getOrderBatchs() {
        return orderBatchs;
    }

    public void setOrderBatchs(List<OrderBatch> orderBatchs) {
        this.orderBatchs = orderBatchs;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<MessageResponses> getMessageResponseses() {
        return messageResponseses;
    }

    public void setMessageResponseses(List<MessageResponses> messageResponseses) {
        this.messageResponseses = messageResponseses;
    }

    @OneToMany(mappedBy = "patientProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<OrdersPtMessage> getOrdersPtMessagesList() {
        return ordersPtMessagesList;
    }

    public void setOrdersPtMessagesList(List<OrdersPtMessage> ordersPtMessagesList) {
        this.ordersPtMessagesList = ordersPtMessagesList;
    }

    //@Transient
    @Column(name = "BirthDate")
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name = "Signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
