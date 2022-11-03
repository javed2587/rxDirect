/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.dto;

import java.util.Date;

/**
 *
 * @author adeel.usmani
 */
public class QuestionAnswerDTO {

    private int id;
    private String question;
    private String answer;
    private Date questionTime;
    private String questionTimeStr;
    private Date answerTime;
    private String patientName;
    private String patientEmail;
    private String patientPhoneNumber;
    private String msgTitle;
    private String osType;
    private String systemGeneratedRxNumber;
    private String systemRxNumberLabel;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Date questionTime) {
        this.questionTime = questionTime;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public String getQuestionTimeStr() {
        return questionTimeStr;
    }

    public void setQuestionTimeStr(String questionTimeStr) {
        this.questionTimeStr = questionTimeStr;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientPhoneNumber() {
        return patientPhoneNumber;
    }

    public void setPatientPhoneNumber(String patientPhoneNumber) {
        this.patientPhoneNumber = patientPhoneNumber;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getSystemGeneratedRxNumber() {
        return systemGeneratedRxNumber;
    }

    public void setSystemGeneratedRxNumber(String systemGeneratedRxNumber) {
        this.systemGeneratedRxNumber = systemGeneratedRxNumber;
    }

    public String getSystemRxNumberLabel() {
        return systemRxNumberLabel;
    }

    public void setSystemRxNumberLabel(String systemRxNumberLabel) {
        this.systemRxNumberLabel = systemRxNumberLabel;
    }

    
    
    

}
