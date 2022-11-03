package com.ssa.cms.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "IVRLog")
public class IVRLog implements java.io.Serializable {

    private Long id;
    private String communicationMethod;
    private String communicationId;
    private String phoneType;
    private String shortCode;
    private String eventName;
    private String triggerValue;
    private String userId;
    private String password;
    private String requestURI;
    private String httpMethod;
    private String remoteIpAddress;
    private String notificationType;
    private String notificationMessage;
    private String notificationXml;
    private String requestDate;
    private Date effectiveDate;
    private String ivrPath;
    private Long ivrId;

    public IVRLog() {
    }

    public IVRLog(String communicationMethod, String communicationId, String phoneType, String shortCode, String eventName, String triggerValue, String userId, String password, String uri, String httpMethod, String remoteIpAddress, String notificationType, String notificationMessage, String notificationXml, String requestDate, Date effectiveDate, String ivrPath, Long ivrId) {
        this.communicationMethod = communicationMethod;
        this.communicationId = communicationId;
        this.phoneType = phoneType;
        this.shortCode = shortCode;
        this.eventName = eventName;
        this.triggerValue = triggerValue;
        this.userId = userId;
        this.password = password;
        this.requestURI = uri;
        this.httpMethod = httpMethod;
        this.remoteIpAddress = remoteIpAddress;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.notificationXml = notificationXml;
        this.requestDate = requestDate;
        this.effectiveDate = effectiveDate;
        this.ivrId = ivrId;
        this.ivrPath = ivrPath;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "Communication_Method", length = 1)
    public String getCommunicationMethod() {
        return this.communicationMethod;
    }

    public void setCommunicationMethod(String communicationMethod) {
        this.communicationMethod = communicationMethod;
    }

    @Column(name = "Communication_Id", length = 250)
    public String getCommunicationId() {
        return this.communicationId;
    }

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }
    @Column(name = "Phone_Type", length = 250)
    public String getPhoneType() {
        return this.phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @Column(name = "Short_Code", length = 5)
    public String getShortCode() {
        return this.shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @Column(name = "Event_Name", length = 50)
    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Column(name = "Trigger_Value", length = 20)
    public String getTriggerValue() {
        return this.triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

    @Column(name = "User_Id", length = 50)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "Password", length = 100)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "HTTP_Method", length = 5)
    public String getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Column(name = "Remote_IP_Address", length = 20)
    public String getRemoteIpAddress() {
        return this.remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }

    @Column(name = "Notification_Type", length = 15)
    public String getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    @Column(name = "Notification_Message", length = 250)
    public String getNotificationMessage() {
        return this.notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Column(name = "Notification_XML", length = 1000)
    public String getNotificationXml() {
        return this.notificationXml;
    }

    public void setNotificationXml(String notificationXml) {
        this.notificationXml = notificationXml;
    }

    @Column(name = "Request_Date", length = 1000)
    public String getRequestDate() {
        return this.requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Effective_Date", length = 19)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(name = "Request_URI", length = 250)
    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Column(name = "IVR_Path", length = 3)
    public String getIvrPath() {
        return ivrPath;
    }

    public void setIvrPath(String ivrPath) {
        this.ivrPath = ivrPath;
    }

    @Column(name = "IVR_Id")
    public Long getIvrId() {
        return ivrId;
    }

    public void setIvrId(Long ivrId) {
        this.ivrId = ivrId;
    }
}
