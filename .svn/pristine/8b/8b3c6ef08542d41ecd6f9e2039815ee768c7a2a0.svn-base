package com.ssa.cms.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author msheraz
 */
@Entity
@Table(name = "PatientProfileAddress")
public class PatientAddress extends AuditFields implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String address;
    private String apartment;
    private String city;
    private Short stateId;
    private String zip;
    private String addressDescription;
    private String addressType;
    private List<PatientProfile> patientProfileInfoList1;
    private State state;
    private Integer profileId;
    private Integer dprefaId;
    private String miles;
    private BigDecimal deliveryFee;
    private List<PatientPaymentInfo> patientPaymentInfoList;

    public PatientAddress() {
    }

    public PatientAddress(Integer id) {
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

    @Column(name = "Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "Apartment")
    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    @Column(name = "City")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "StateId")
    public Short getStateId() {
        return stateId;
    }

    public void setStateId(Short stateId) {
        this.stateId = stateId;
    }

    @Column(name = "Zip")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name = "AddressDescription")
    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    @OneToMany(mappedBy = "billingAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<PatientProfile> getPatientProfileInfoList1() {
        return patientProfileInfoList1;
    }

    public void setPatientProfileInfoList1(List<PatientProfile> patientProfileInfoList1) {
        this.patientProfileInfoList1 = patientProfileInfoList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PatientAddress)) {
            return false;
        }
        PatientAddress other = (PatientAddress) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "javaapplication1.PatientProfileAddress[ id=" + id + " ]";
    }

    @Transient
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Column(name = "AddressType")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @Transient
    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    @Transient
    public Integer getDprefaId() {
        return dprefaId;
    }

    public void setDprefaId(Integer dprefaId) {
        this.dprefaId = dprefaId;
    }

    @Transient
    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    @Transient
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "billingAddress")
    public List<PatientPaymentInfo> getPatientPaymentInfoList() {
        return patientPaymentInfoList;
    }

    public void setPatientPaymentInfoList(List<PatientPaymentInfo> patientPaymentInfoList) {
        this.patientPaymentInfoList = patientPaymentInfoList;
    }

}
