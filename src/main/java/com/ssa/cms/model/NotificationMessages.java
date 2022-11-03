package com.ssa.cms.model;

import com.ssa.cms.modelinterfaces.CommonMessageFunctionalityI;
import com.ssa.cms.modellisteners.MessageListener;
import com.ssa.cms.util.JsonDateSerializer;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author mzubair
 */
@Entity
@Table(name = "NotificationMessages")
@EntityListeners(MessageListener.class)
public class NotificationMessages implements Serializable, CommonMessageFunctionalityI {

    private Integer id;
    private MessageType messageType;
    private PatientProfile patientProfile;
    private String subject;
    private String messageText;
    private String status;
    private Date createdOn;
    private Integer messageTypeId;
    private Integer profileId;
    private Boolean isRead;
    private Long readMesages;
    private Long unReadMessages;
    private String messageCategory;
    private Order orders;
    private TransferDetail transferDetail;
    private String orderId = "";
    private String orderStatus = "";
    private String attachmentPath;
    private String attachmentName;
    private String contentType;
    private String timeAgo;
    private Integer isCritical;
    private Integer pointsAwarded;
    private String mobileNumber;
    private List<MessageResponses> messageResponses;
    private PatientProfileMembers patientProfileMembers;
    private String isTestMsg;
    private String orderPrefix;
    private String PushSubject;
    private String rxNo;
    private Integer dependentId;
    private Boolean isEventFire;

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
    @JoinColumns({
        @JoinColumn(name = "MessageTypeId", referencedColumnName = "MessageTypeId", nullable = false)
        ,
        @JoinColumn(name = "FolderId", referencedColumnName = "FolderId", nullable = false)})
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileId", insertable = true, updatable = true)
    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", insertable = true, updatable = true, nullable = true)
    public Order getOrders() {
        return orders;
    }

    public void setOrders(Order orders) {
        this.orders = orders;
    }

    @Column(name = "Subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "MessageText")
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Column(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "CreatedOn")
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Transient
    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    @Transient
    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    @Column(name = "IsRead")
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @Transient
    public Long getReadMesages() {
        return readMesages;
    }

    public void setReadMesages(Long readMesages) {
        this.readMesages = readMesages;
    }

    @Transient
    public Long getUnReadMessages() {
        return unReadMessages;
    }

    public void setUnReadMessages(Long unReadMessages) {
        this.unReadMessages = unReadMessages;
    }

    @Transient
    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TransferDetailID", nullable = true, insertable = true, updatable = true)
    public TransferDetail getTransferDetail() {
        return transferDetail;
    }

    /**
     * @param transferDetail the transferDetail to set
     */
    public void setTransferDetail(TransferDetail transferDetail) {
        this.transferDetail = transferDetail;
    }

    @Transient
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Transient
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Transient
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Transient
    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    @Transient
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Transient
    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    @Column(name = "IsCritical")
    public Integer getIsCritical() {
        return isCritical;
    }

    public void setIsCritical(Integer isCritical) {
        this.isCritical = isCritical;
    }

    @Column(name = "pointsAwarded")
    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    @Transient
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @OneToMany(mappedBy = "notificationMessages", fetch = FetchType.LAZY)
    public List<MessageResponses> getMessageResponses() {
        return messageResponses;
    }

    public void setMessageResponses(List<MessageResponses> messageResponses) {
        this.messageResponses = messageResponses;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DependentId")
    public PatientProfileMembers getPatientProfileMembers() {
        return patientProfileMembers;
    }

    public void setPatientProfileMembers(PatientProfileMembers patientProfileMembers) {
        this.patientProfileMembers = patientProfileMembers;
    }

    @Column(name = "IsTestMsg")
    public String getIsTestMsg() {
        return isTestMsg;
    }

    public void setIsTestMsg(String isTestMsg) {
        this.isTestMsg = isTestMsg;
    }

    @Transient
    public String getOrderPrefix() {
        return orderPrefix;
    }

    public void setOrderPrefix(String orderPrefix) {
        this.orderPrefix = orderPrefix;
    }

    @Column(name = "PushSubject")
    public String getPushSubject() {
        return PushSubject;
    }

    public void setPushSubject(String PushSubject) {
        this.PushSubject = PushSubject;
    }

    @Transient
    public String getRxNo() {
        return rxNo;
    }

    public void setRxNo(String rxNo) {
        this.rxNo = rxNo;
    }

    @Transient
    public Integer getDependentId() {
        return dependentId;
    }

    public void setDependentId(Integer dependentId) {
        this.dependentId = dependentId;
    }

    @Column(name = "IsEventFire")
    public Boolean getIsEventFire() {
        return isEventFire;
    }

    public void setIsEventFire(Boolean isEventFire) {
        this.isEventFire = isEventFire;
    }

    @Override
    @Transient
    public String getMessage() {
        return "";
    }

    @Override
    public void setMessage(String s) {

    }

    @Override
    @Transient
    public String getPhoneNumber() {
        return "";
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

}
