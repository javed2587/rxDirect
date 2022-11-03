/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.dto;

import com.ssa.cms.model.Order;
import com.ssa.cms.model.PatientAddress;
import com.ssa.cms.model.PatientDeliveryAddress;
import com.ssa.cms.model.PatientProfileHealth;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mzubair
 */
public class LoginDTO {

    private Integer id;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private Integer verificationCode;
    private String emailAddress;
    private String dob;
    private String gender;
    private String allergies;
    private String securityToken;
    private Integer deliveryPreferenceId;
    private String planId;
    private String groupNumber;
    private String providerAddress;
    private String providerPhone;
    private Integer patientId;
    private String expiryDate;
    private String memberID;
    private String cardHolderRelationship;
    private String insuranceFrontCardPath;
    private String insuranceBackCardPath;
    private Long availableRewardPoints;
    private Long availedRewardPoints;
    private Long lifetimeRewardPoints;
    private String createdOn;
    private String updatedOn;
    private Long dependentCount;
    private Long insCardCount;
    private long dependentInsCardCount;
    private String cardHolderRelation;
    private Integer dprefaId;
    private String miles;
    private BigDecimal deliveryFee;
    private PatientAddress billingAddress;
    private String alternatePhoneNumber;
    private float stateTaxPercent;
    private Object patientProfileMembersList;
    private List<Order> orders;
    private Long totalRewardPoints;
    private String defaultAddress;
    private String defaultAddresszip;
    private boolean isOldPatient;
    private Date birthDate;
    private Date createdDate;
    private List<PatientDeliveryAddress> patientDeliveryAddresses;
    private String state;
    private Date updatedDate;
    private String osType;
    private String status;
    private List<PatientProfileHealth> patientProfileHealthsList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Integer verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public Integer getDeliveryPreferenceId() {
        return deliveryPreferenceId;
    }

    public void setDeliveryPreferenceId(Integer deliveryPreferenceId) {
        this.deliveryPreferenceId = deliveryPreferenceId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getCardHolderRelationship() {
        return cardHolderRelationship;
    }

    public void setCardHolderRelationship(String cardHolderRelationship) {
        this.cardHolderRelationship = cardHolderRelationship;
    }

    public String getInsuranceFrontCardPath() {
        return insuranceFrontCardPath;
    }

    public void setInsuranceFrontCardPath(String insuranceFrontCardPath) {
        this.insuranceFrontCardPath = insuranceFrontCardPath;
    }

    public String getInsuranceBackCardPath() {
        return insuranceBackCardPath;
    }

    public void setInsuranceBackCardPath(String insuranceBackCardPath) {
        this.insuranceBackCardPath = insuranceBackCardPath;
    }

    public Long getAvailableRewardPoints() {
        return availableRewardPoints;
    }

    public void setAvailableRewardPoints(Long availableRewardPoints) {
        this.availableRewardPoints = availableRewardPoints;
    }

    public Long getAvailedRewardPoints() {
        return availedRewardPoints;
    }

    public void setAvailedRewardPoints(Long availedRewardPoints) {
        this.availedRewardPoints = availedRewardPoints;
    }

    public Long getLifetimeRewardPoints() {
        return lifetimeRewardPoints;
    }

    public void setLifetimeRewardPoints(Long lifetimeRewardPoints) {
        this.lifetimeRewardPoints = lifetimeRewardPoints;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getDependentCount() {
        return dependentCount;
    }

    public void setDependentCount(Long dependentCount) {
        this.dependentCount = dependentCount;
    }

    public Long getInsCardCount() {
        return insCardCount;
    }

    public void setInsCardCount(Long insCardCount) {
        this.insCardCount = insCardCount;
    }

    public long getDependentInsCardCount() {
        return dependentInsCardCount;
    }

    public void setDependentInsCardCount(long dependentInsCardCount) {
        this.dependentInsCardCount = dependentInsCardCount;
    }

    public String getCardHolderRelation() {
        return cardHolderRelation;
    }

    public void setCardHolderRelation(String cardHolderRelation) {
        this.cardHolderRelation = cardHolderRelation;
    }

    public Integer getDprefaId() {
        return dprefaId;
    }

    public void setDprefaId(Integer dprefaId) {
        this.dprefaId = dprefaId;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public PatientAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(PatientAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public float getStateTaxPercent() {
        return stateTaxPercent;
    }

    public void setStateTaxPercent(float stateTaxPercent) {
        this.stateTaxPercent = stateTaxPercent;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(Long totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDefaultAddresszip() {
        return defaultAddresszip;
    }

    public void setDefaultAddresszip(String defaultAddresszip) {
        this.defaultAddresszip = defaultAddresszip;
    }

    public boolean isIsOldPatient() {
        return isOldPatient;
    }

    public void setIsOldPatient(boolean isOldPatient) {
        this.isOldPatient = isOldPatient;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<PatientDeliveryAddress> getPatientDeliveryAddresses() {
        return patientDeliveryAddresses;
    }

    public void setPatientDeliveryAddresses(List<PatientDeliveryAddress> patientDeliveryAddresses) {
        this.patientDeliveryAddresses = patientDeliveryAddresses;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PatientProfileHealth> getPatientProfileHealthsList() {
        return patientProfileHealthsList;
    }

    public void setPatientProfileHealthsList(List<PatientProfileHealth> patientProfileHealthsList) {
        this.patientProfileHealthsList = patientProfileHealthsList;
    }

    public Object getPatientProfileMembersList() {
        return patientProfileMembersList;
    }

    public void setPatientProfileMembersList(Object patientProfileMembersList) {
        this.patientProfileMembersList = patientProfileMembersList;
    }

}
