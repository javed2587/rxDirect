package com.ssa.cms.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "PatientDeliveryAddress")
public class PatientDeliveryAddress implements Serializable {

    private Integer id;
    private PatientProfile patientProfile;
    private DeliveryPreferences deliveryPreferences;
    private String address;
    private String apartment;
    private String city;
    private State state;
    private String zip;
    private String description;
    private String addressType;
    private String defaultAddress;
    private Date createdOn;
    private Date updatedOn;
    private List<TransferRequest> transferRequestList;

    public PatientDeliveryAddress() {
    }

    public PatientDeliveryAddress(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StateId", insertable = true, updatable = true)
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Column(name = "Zip")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name = "AddressDescription")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "AddressType")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "CreatedOn")
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "UpdatedOn")
    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Column(name = "DefaultAddress")
    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "patientDeliveryAddress")
    public List<TransferRequest> getTransferRequestList() {
        return transferRequestList;
    }

    public void setTransferRequestList(List<TransferRequest> transferRequestList) {
        this.transferRequestList = transferRequestList;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DeliveryPreferenceId", insertable = true, updatable = true)
    public DeliveryPreferences getDeliveryPreferences() {
        return deliveryPreferences;
    }

    public void setDeliveryPreferences(DeliveryPreferences deliveryPreferences) {
        this.deliveryPreferences = deliveryPreferences;
    }

}
