/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Javed.Iqbal
 */
@Entity
@Table(name = "QuestionAnswer")
public class QuestionAnswer implements Serializable {

    private Integer id;
    private Order order;
    private String question;
    private String answer;
    private Date questionTime;
    private Date answerTime;
    private PatientProfile patientProfile;
    private PatientProfileMembers member;
    private NotificationMessages notificationMessages;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId")
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "Question")
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Column(name = "Answer")
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Column(name = "QuestionTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Date questionTime) {
        this.questionTime = questionTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId")
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientprofile) {
        this.patientProfile = patientprofile;
    }

    @Column(name = "AnswerTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DependentID")
    public PatientProfileMembers getMember() {
        return member;
    }

    public void setMember(PatientProfileMembers member) {
        this.member = member;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationMsgId")
    public NotificationMessages getNotificationMessages() {
        return notificationMessages;
    }

    public void setNotificationMessages(NotificationMessages notificationMessages) {
        this.notificationMessages = notificationMessages;
    }

}
