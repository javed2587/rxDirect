/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

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
 * @author javed iqbal
 */
@Entity
@Table(name = "PatientAllergies")
public class PatientAllergies implements java.io.Serializable{
     
    private Integer id;
    private String allergies;
    private PatientProfile patientProfile;
    private PatientProfileMembers patientProfileMembers;
  

     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer Id) {
        this.id = Id;
    }
    
    @Column(name = "Allergies")
    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false, insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DependentId", nullable = false, insertable = true, updatable = true)
    public PatientProfileMembers getPatientProfileMembers() {
        return patientProfileMembers;
    }

    public void setPatientProfileMembers(PatientProfileMembers patientProfileMembers) {
        this.patientProfileMembers = patientProfileMembers;
    }
    
}
