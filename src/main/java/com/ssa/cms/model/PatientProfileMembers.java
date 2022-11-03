/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

import com.ssa.cms.dto.OrderDetailDTO;
import com.ssa.cms.modelinterfaces.CommonPatientProfileMembersFunctionalityI;
import com.ssa.cms.modellisteners.PatientProfileMembersListener;
import com.ssa.cms.util.CustomJsonDmyFormat;
import com.ssa.cms.util.JsonDateSerializer;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "PatientProfileMembers")
@EntityListeners(PatientProfileMembersListener.class)
public class PatientProfileMembers implements Serializable, CommonPatientProfileMembersFunctionalityI {

    private Integer id;
    private Integer patientId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String birthDate;
    private String gender;
    private String allergies;
    private String dermatologist;
    private Date createdOn;
    private String frontPOAImage;
    private String backPOAImage;
    private String state;
    private String expiryDate;
    private Boolean isAdult;
    private Boolean isApproved;
    private Date poaApprovalDate;
    private Date poaExpirationDate;

    private String selectedDob;
    private String fullPatientName;
    private Long rxCount;
    private Integer disapprove;
    private Date disApproveDate;
    private String comments;
    private String approvalStr;
    private String disabledStr;
    private Integer archived;
    private int pharmacyQueueParam;
    private String approvalDateStr;
    private String expiryDateStr;
    private String age;
    private List<OrderDetailDTO> ordersDTOList;
    private String fullName;
    private String email;
    private Boolean optOut;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "PatientId")
    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
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

//    @Column(name = "Dob")
//    @Temporal(javax.persistence.TemporalType.DATE)
//    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonSerialize(using = CustomJsonDmyFormat.class)
    @Transient
    public Date getDob() {
        return dob;
    }

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

    @Column(name = "Allergies")
    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    @Column(name = "Dermatologist")
    public String getDermatologist() {
        return dermatologist;
    }

    public void setDermatologist(String dermatologist) {
        this.dermatologist = dermatologist;
    }

    @Column(name = "CreatedOn")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Transient
    public String getSelectedDob() {
        return selectedDob;
    }

    public void setSelectedDob(String selectedDob) {
        this.selectedDob = selectedDob;
    }

    @Transient
    public String getFullPatientName() {
        return fullPatientName;
    }

    public void setFullPatientName(String fullPatientName) {
        this.fullPatientName = fullPatientName;
    }

    @Column(name = "FrontPOAImage")
    public String getFrontPOAImage() {
        return frontPOAImage;
    }

    public void setFrontPOAImage(String frontPOAImage) {
        this.frontPOAImage = frontPOAImage;
    }

    @Column(name = "BackPOAImage")
    public String getBackPOAImage() {
        return backPOAImage;
    }

    public void setBackPOAImage(String backPOAImage) {
        this.backPOAImage = backPOAImage;
    }

    @Column(name = "State")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "ExpiryDate")
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Column(name = "IsAdult")
    public Boolean getIsAdult() {
        return isAdult;
    }

    public void setIsAdult(Boolean isAdult) {
        this.isAdult = isAdult;
    }

    @Column(name = "IsApproved")
    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    @Transient
    public Long getRxCount() {
        return rxCount;
    }

    public void setRxCount(Long rxCount) {
        this.rxCount = rxCount;
    }

    @Column(name = "POAApprovalDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getPoaApprovalDate() {
        return poaApprovalDate;
    }

    public void setPoaApprovalDate(Date poaApprovalDate) {
        this.poaApprovalDate = poaApprovalDate;
    }

    @Column(name = "POAExpirationDate")
    @Temporal(javax.persistence.TemporalType.DATE)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    public Date getPoaExpirationDate() {
        return poaExpirationDate;
    }

    public void setPoaExpirationDate(Date poaExpirationDate) {
        this.poaExpirationDate = poaExpirationDate;
    }

    @Column(name = "DisApprove")
    public Integer getDisapprove() {
        return disapprove;
    }

    public void setDisapprove(Integer disapprove) {
        this.disapprove = disapprove;
    }

    @Column(name = "DisApproveDate")
    @Temporal(TemporalType.DATE)
    public Date getDisApproveDate() {
        return disApproveDate;
    }

    public void setDisApproveDate(Date disApproveDate) {
        this.disApproveDate = disApproveDate;
    }

    @Column(name = "Comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Transient
    public String getApprovalStr() {
        return approvalStr;
    }

    public void setApprovalStr(String approvalStr) {
        this.approvalStr = approvalStr;
    }

    @Transient
    public String getDisabledStr() {
        return disabledStr;
    }

    public void setDisabledStr(String disabledStr) {
        this.disabledStr = disabledStr;
    }

    @Column(name = "IsArchived")
    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

    @Column(name = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Transient
    public int getPharmacyQueueParam() {
        return pharmacyQueueParam;
    }

    public void setPharmacyQueueParam(int pharmacyQueueParam) {
        this.pharmacyQueueParam = pharmacyQueueParam;
    }

    @Transient
    public String getApprovalDateStr() {
        return approvalDateStr;
    }

    public void setApprovalDateStr(String approvalDateStr) {
        this.approvalDateStr = approvalDateStr;
    }

    @Transient
    public String getExpiryDateStr() {
        return expiryDateStr;
    }

    public void setExpiryDateStr(String expiryDateStr) {
        this.expiryDateStr = expiryDateStr;
    }

    @Transient
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Transient
    public List<OrderDetailDTO> getOrdersDTOList() {
        return ordersDTOList;
    }

    public void setOrdersDTOList(List<OrderDetailDTO> ordersDTOList) {
        this.ordersDTOList = ordersDTOList;
    }

    @Formula(value = " concat(firstName, ' ', lastName) ")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "OptOut")
    public Boolean getOptOut() {
        return optOut;
    }

    public void setOptOut(Boolean optOut) {
        this.optOut = optOut;
    }

    @Column(name = "BirthDate")
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

}
