package com.ssa.cms.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "PharmacyZipCodes")
public class PharmacyZipCodes extends AuditFields implements Serializable {

    private Integer id;
    private String pharmacyName;
    private String pharmacyZip;
    private List<DeliveryDistanceFee> deliveryDistanceFeesList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "PharmacyName")
    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    @Column(name = "PharmacyZip")
    public String getPharmacyZip() {
        return pharmacyZip;
    }

    public void setPharmacyZip(String pharmacyZip) {
        this.pharmacyZip = pharmacyZip;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pharmacyZipCodes")
    public List<DeliveryDistanceFee> getDeliveryDistanceFeesList() {
        return deliveryDistanceFeesList;
    }

    public void setDeliveryDistanceFeesList(List<DeliveryDistanceFee> deliveryDistanceFeesList) {
        this.deliveryDistanceFeesList = deliveryDistanceFeesList;
    }
}
