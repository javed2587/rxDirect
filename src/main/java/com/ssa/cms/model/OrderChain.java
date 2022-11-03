/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "OrderChain")
public class OrderChain extends AuditFields implements java.io.Serializable {

    private Long id;
    private Date olversion;
    private DrugDetail drugDetail;
    private int refillAllow;
    private int refillRemaing;
    private int refillDayes;
    private Date lastRefillDate;
    private Date nextRefillDate;
    private List<Order> orderList;
    private PatientProfile patientProfile;
    private Integer optOutRefillReminder;
    private Integer daysSupply;
    private Date rxExpiredDate;
    private Date rxDate;

    @Id
    @GenericGenerator(name = "OrderChainId", strategy = "com.ssa.cms.model.OrderChainIdGenerator")
    @GeneratedValue(generator = "OrderChainId")
    @Column(name = "Id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    @Version
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "OlVersion")
    @Transient
    public Date getOlversion() {
        return olversion;
    }

    public void setOlversion(Date olversion) {
        this.olversion = olversion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DrugDetailId", insertable = true, updatable = true)
    public DrugDetail getDrugDetail() {
        return drugDetail;
    }

    public void setDrugDetail(DrugDetail drugDetail) {
        this.drugDetail = drugDetail;
    }

    @Column(name = "RefillAllow")
    public int getRefillAllow() {
        return refillAllow;
    }

    public void setRefillAllow(int refillAllow) {
        this.refillAllow = refillAllow;
    }

    @Column(name = "RefillRemaing")
    public int getRefillRemaing() {
        return refillRemaing;
    }

    public void setRefillRemaing(int refillRemaing) {
        this.refillRemaing = refillRemaing;
    }

    @Column(name = "RefillDayes")
    public int getRefillDayes() {
        return refillDayes;
    }

    public void setRefillDayes(int refillDayes) {
        this.refillDayes = refillDayes;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "LastRefillDate")
    public Date getLastRefillDate() {
        return lastRefillDate;
    }

    public void setLastRefillDate(Date lastRefillDate) {
        this.lastRefillDate = lastRefillDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "NextRefillDate")
    public Date getNextRefillDate() {
        return nextRefillDate;
    }

    public void setNextRefillDate(Date nextRefillDate) {
        this.nextRefillDate = nextRefillDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Patient_Id", nullable = false, insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    @OneToMany(mappedBy = "orderChain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Column(name = "optOutRefillReminder")
    public Integer getOptOutRefillReminder() {
        return optOutRefillReminder;
    }

    public void setOptOutRefillReminder(Integer optOutRefillReminder) {
        this.optOutRefillReminder = optOutRefillReminder;
    }

    @Column(name = "DaysSupply")
    public Integer getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(Integer daysSupply) {
        this.daysSupply = daysSupply;
    }

    @Column(name = "RxExpireDate")
    public Date getRxExpiredDate() {
        return rxExpiredDate;
    }

    public void setRxExpiredDate(Date rxExpiredDate) {
        this.rxExpiredDate = rxExpiredDate;
    }

    @Column(name = "RxDate")
    public Date getRxDate() {
        return rxDate;
    }

    public void setRxDate(Date rxDate) {
        this.rxDate = rxDate;
    }

}
