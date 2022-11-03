package com.ssa.cms.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "DeliveryPreferencesDistance")
public class DeliveryPreferencesDistance extends AuditFields implements Serializable {

    private Integer id;
    private Integer milesFrom;
    private Integer milesTo;
    private Double fee;
    private PharmacyZipCodes pharmacyZipCodes;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MilesFrom")
    public Integer getMilesFrom() {
        return milesFrom;
    }

    public void setMilesFrom(Integer milesFrom) {
        this.milesFrom = milesFrom;
    }

    @Column(name = "MilesTo")
    public Integer getMilesTo() {
        return milesTo;
    }

    public void setMilesTo(Integer milesTo) {
        this.milesTo = milesTo;
    }

    @Column(name = "Fee")
    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PharmacyZipId", insertable = true, updatable = true)
    public PharmacyZipCodes getPharmacyZipCodes() {
        return pharmacyZipCodes;
    }

    public void setPharmacyZipCodes(PharmacyZipCodes pharmacyZipCodes) {
        this.pharmacyZipCodes = pharmacyZipCodes;
    }

}
