/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

import com.ssa.cms.modelinterfaces.CommonPatientInsuranceDetailFunctionlityI;
import com.ssa.cms.modellisteners.CommonPatientInsuranceDetailListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Osman.Khan
 */
@Entity
@Table(name = "CoPayCardDetails")
@EntityListeners(CommonPatientInsuranceDetailListener.class)
public class CoPayCardDetails implements Serializable, CommonPatientInsuranceDetailFunctionlityI {

    private Long id;
    private PatientProfile patientProfile;
    private String manufacturerName;
    private String copayFrontCardPath;
    private String copayBackCardPath;
    private Date effectiveDate;
    private Date expiryDate;
    private String isArchive;
    private Order order;
    private DrugBasic drugbasic;
    private Date updatedOn;
    private Date createdOn;

    public CoPayCardDetails() {
    }

    public CoPayCardDetails(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ManufacturerName")
    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    @Column(name = "CopayFrontCardPath")
    public String getCopayFrontCardPath() {
        return copayFrontCardPath;
    }

    public void setCopayFrontCardPath(String copayFrontCardPath) {
        this.copayFrontCardPath = copayFrontCardPath;
    }

    @Column(name = "CopayBackCardPath")
    public String getCopayBackCardPath() {
        return copayBackCardPath;
    }

    public void setCopayBackCardPath(String copayBackCardPath) {
        this.copayBackCardPath = copayBackCardPath;
    }

    @Column(name = "EffectiveDate")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(name = "ExpiryDate")
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId", insertable = true)
    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "isArchive")
    public String getIsArchive() {
        return isArchive;
    }

    public void setIsArchive(String isArchive) {
        this.isArchive = isArchive;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", insertable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DrugBasicId", insertable = true)
    public DrugBasic getDrugbasic() {
        return drugbasic;
    }

    public void setDrugbasic(DrugBasic drugbasic) {
        this.drugbasic = drugbasic;
    }

    @Column(name = "UpdatedOn")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Column(name = "CreatedOn")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Transient
    @Override
    public String getProviderPhone() {
        return "";
    }

    @Override
    public void setProviderPhone(String providerPhone) {

    }

    @Transient
    @Override
    public String getInsuranceFrontCardPath() {
        return "";
    }

    @Override
    public void setInsuranceFrontCardPath(String insuranceFrontCardPath) {

    }

    @Transient
    @Override
    public String getInsuranceBackCardPath() {
        return "";
    }

    @Override
    public void setInsuranceBackCardPath(String insuranceBackCardPath) {

    }

}
