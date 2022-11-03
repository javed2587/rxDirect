package com.ssa.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ssa.cms.modelinterfaces.CommonOrderFunctionalityI;
import com.ssa.cms.modellisteners.OrderListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "Orders")
@EntityListeners(OrderListener.class)
public class Order extends AuditFields implements java.io.Serializable, CommonOrderFunctionalityI {

    private String id;
    private Date olversion;
    private String firstName;
    private String lastName;
    private String streetAddress;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private Double payment;
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiry;
    private String cardCvv;
    private String cardType;
    private String orderTrackingNo;
    private OrderStatus orderStatus;
    private Pharmacy pharmacy;
    private List<OrderHistory> orderHistory;
    private String transactionNo;
    private DailyRedemption dailyRedemption;
    private String doDirectTransactionId;
    private String type;
    private String fromDate;
    private String toDate;
    private Integer pharmacyId;
    private Integer showFolder;
    private Integer orderStatusId;
    private String patientName;
    private String rxNo;
    private OrderCarriers orderCarriers;
    private PatientProfile patientProfile;
    private PatientProfileMembers patientProfileMembers;
    private Drug drug;
    private String redeemPoints;
    private Double redeemPointsCost;
    private Double handLingFee;
    private String strength;
    private String drugName;
    private String drugType;
    private String qty;
    private Double drugPrice;
    private String apartment;
    private List<OrdersPtMessage> ordersPtMessagesList;
    private List<RewardHistory> rewardHistorySet;
    private DrugDetail drugDetail;
    private OrderChain orderChain;
    private OrderBatch orderBatch;
    private DeliveryPreferences deliveryPreference;
    private DeliveryPreferences deliveryPreferenceUsed;
    private Date deliveryDate;
    private String miles;

    private BigDecimal outOfPocket;
    private Integer selectedStatus;
    private long itemsOrder;
    private String communicationId;
    private String orderNo;
    private String currentIndex;
    private String previousIndex;
    private int totalOrder;
    private int openOrder;
    private int completeOrder;
    private int totalfillOrder;
    private int totalDeniedOrders;
    private double totalRevenue;
    private double totalIngredientCost;
    private double netProfit;
    private double GenRxAWP;
    private String rph;
    private String comments;
    private String quality;
    private String daysBack;
    private Boolean showPtMessage;
    private String orderStatusName;
    private String orderDate;
    private String shippingAddress;
    private String viewStatus;
    private Date statusCreatedOn;
    private String orderType;
    private TransferDetail transferDetail;
    private BigDecimal awardedPoints;
    private Double rxAcqCost;
    private Double rxReimbCost;
    private Double originalPtCopay;
    private Double additionalMargin;
    private Double profitMargin;
    private Double finalPayment;
    private Double paymentExcludingDelivery;
    private Double mfrCost;
    private Double profitShareCost;
    private String insuranceType;
    private Boolean isBottleAvailable;

    private Integer daysSupply;
    private Integer refillsAllowed;
    private Integer refillsRemaining;
    private String paymentMode;
    private String pharmacyName;
    private String pharmacyPhone;
    private String prescriberName;
    private String prescriberPhone;
    private String prescriberNPI;
    private String rxNumber;
    private String video;
    private String imagePath;
    private String patientComments;
    private Date filledDate;
    private String lastFilledDate;
    private Integer refillIndex;
    private String deliverycarrier;
    private String traclingno;
    private String clerk;
    private Boolean isPrescriptionHardCopy;
    private Boolean addCopyCard;
    private String paymentType;
    private String paymentAuthorization;
    private Integer paymentId;
    private String prefName;
    private String requiresHandDelivery;
    private Date nextRefillDate;
    private Date autoRefillDate;
    private String nextRefillFlag;
    private Integer refillDone;
    private Double priceIncludingMargins;
    private Date rxExpiredDate;
    private String rxExpiredDateStr;
    private Date rxDate;
    private List<CoPayCardDetails> coPayCardDetails;
    private Date deliveryTo;
    private Date deliveryFrom;
    private String finalPaymentMode;
    private Double estimatedPrice;
    private String serviceRequested;
    private Integer profitSharePoint;
    private Float actualProfitShare;
    private String oldRxNumber;
    private String responseRequired;
    private String patientResponse;
    private Integer careGiverRequest;
    private Integer disabledFlds;
    private List<OrderTransferImages> orderTransferImages;
//    private List<OrderCustomDocuments> orderCustomDocumentses;

    //shippng info updated
    private PharmacyUser shippedBy;
    private Date shippedOn;
    private Date customerShippingDate;
    private Boolean multiRx;
    private Integer requiresDeletion;
    private Integer handDelivery;
    private Integer handDeliveryAccepted;
    private Boolean onlineOrder;
    private int deliveryId;

    private String prescriberLastName;
    private String systemGeneratedRxNumber;
    private Integer ptOverridePrice;

    private String rxExpiry;
    private String pharmacyExtension;
    private String prescriberExtension;
    private Integer autoDeletionFlag;
    private Date autoDeletionDate;
    private String responseFullFilled;
    private Date lastReminderDate;
    private Date nextRefill;
    private String rxPrefix;
    private Date readyToDeliverRxDate;
    private ReadyToDeliverRxOrders readyToDeliverRxOrders;

    @Id
    @GenericGenerator(name = "OrderId", strategy = "com.ssa.cms.model.OrderIdGenerator")
    @GeneratedValue(generator = "OrderId")
    @Column(name = "Id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Column(name = "FirstName", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "LastName", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "ShippingStreetAddress")
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @Column(name = "ShippingAddressLine2")
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Column(name = "ShippingCity")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "ShippingState")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "ShippingZip")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name = "CardNumber")
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Column(name = "CardExpiry")
    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    @Column(name = "CardCvv")
    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    @Column(name = "CardHolderName")
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    @Column(name = "CardType")
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Transient
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderStatus", nullable = false, insertable = true, updatable = true)
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Transient
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @Transient
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Transient
    public Integer getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Integer pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    @Transient
    public Integer getShowFolder() {
        return showFolder;
    }

    public void setShowFolder(Integer showFolder) {
        this.showFolder = showFolder;
    }

    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy(clause = "id asc")
    public List<OrderHistory> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<OrderHistory> orderHistory) {
        this.orderHistory = orderHistory;
    }

    @Column(name = "OrderTrackingNo")
    public String getOrderTrackingNo() {
        return orderTrackingNo;
    }

    public void setOrderTrackingNo(String orderTrackingNo) {
        this.orderTrackingNo = orderTrackingNo;
    }

    @Column(name = "TransactionNumber")
    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    @Transient
    public DailyRedemption getDailyRedemption() {
        return dailyRedemption;
    }

    public void setDailyRedemption(DailyRedemption dailyRedemption) {
        this.dailyRedemption = dailyRedemption;
    }

    @Transient
    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Integer orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PharmacyId", nullable = true, insertable = true, updatable = true)
    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    @Transient
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Transient
    public String getRxNo() {
        return rxNo;
    }

    public void setRxNo(String rxNo) {
        this.rxNo = rxNo;
    }

    @Transient
    public BigDecimal getOutOfPocket() {
        return outOfPocket;
    }

    public void setOutOfPocket(BigDecimal outOfPocket) {
        this.outOfPocket = outOfPocket;
    }

    @Column(name = "Payment")
    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    @Column(name = "RxAcqCost")
    public Double getRxAcqCost() {
        return rxAcqCost;
    }

    public void setRxAcqCost(Double rxAcqCost) {
        this.rxAcqCost = rxAcqCost;
    }

    @Column(name = "RxReimbCost")
    public Double getRxReimbCost() {
        return rxReimbCost;
    }

    public void setRxReimbCost(Double rxReimbCost) {
        this.rxReimbCost = rxReimbCost;
    }

    @Column(name = "OriginalPtCopay")
    public Double getOriginalPtCopay() {
        return originalPtCopay;
    }

    public void setOriginalPtCopay(Double originalPtCopay) {
        this.originalPtCopay = originalPtCopay;
    }

    @Column(name = "AdditionalMargin")
    public Double getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(Double additionalMargin) {
        this.additionalMargin = additionalMargin;
    }

    @Column(name = "ProfitMargin")
    public Double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Double profitMargin) {
        this.profitMargin = profitMargin;
    }

    @Column(name = "FINALPAYMENT")
    public Double getFinalPayment() {
        return finalPayment;
    }

    public void setFinalPayment(Double finalPayment) {
        this.finalPayment = finalPayment;
    }

    @Column(name = "PaymentExcludingDelivery")
    public Double getPaymentExcludingDelivery() {
        return paymentExcludingDelivery;
    }

    public void setPaymentExcludingDelivery(Double paymentExcludingDelivery) {
        this.paymentExcludingDelivery = paymentExcludingDelivery;
    }

    @Transient
    public Integer getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(Integer selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    @Transient
    public String getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    @Transient
    public long getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(long itemsOrder) {
        this.itemsOrder = itemsOrder;
    }

    @Transient
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "DoDirectTransactionId")
    public String getDoDirectTransactionId() {
        return doDirectTransactionId;
    }

    public void setDoDirectTransactionId(String doDirectTransactionId) {
        this.doDirectTransactionId = doDirectTransactionId;
    }

    @Transient
    public String getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(String currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Transient
    public String getPreviousIndex() {
        return previousIndex;
    }

    public void setPreviousIndex(String previousIndex) {
        this.previousIndex = previousIndex;
    }

    @Transient
    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    @Transient
    public int getOpenOrder() {
        return openOrder;
    }

    public void setOpenOrder(int openOrder) {
        this.openOrder = openOrder;
    }

    @Transient
    public int getCompleteOrder() {
        return completeOrder;
    }

    public void setCompleteOrder(int completeOrder) {
        this.completeOrder = completeOrder;
    }

    @Transient
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Transient
    public int getTotalfillOrder() {
        return totalfillOrder;
    }

    public void setTotalfillOrder(int totalfillOrder) {
        this.totalfillOrder = totalfillOrder;
    }

    @Transient
    public double getTotalIngredientCost() {
        return totalIngredientCost;
    }

    public void setTotalIngredientCost(double totalIngredientCost) {
        this.totalIngredientCost = totalIngredientCost;
    }

    @Transient
    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    @Transient
    public int getTotalDeniedOrders() {
        return totalDeniedOrders;
    }

    public void setTotalDeniedOrders(int totalDeniedOrders) {
        this.totalDeniedOrders = totalDeniedOrders;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrdersCarriersId", nullable = true, insertable = true, updatable = true)
    public OrderCarriers getOrderCarriers() {
        return orderCarriers;
    }

    public void setOrderCarriers(OrderCarriers orderCarriers) {
        this.orderCarriers = orderCarriers;
    }

    @Transient
    public double getGenRxAWP() {
        return GenRxAWP;
    }

    public void setGenRxAWP(double GenRxAWP) {
        this.GenRxAWP = GenRxAWP;
    }

    @Transient
    public String getRph() {
        return rph;
    }

    public void setRph(String rph) {
        this.rph = rph;
    }

    @Transient
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Transient
    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Transient
    public String getDaysBack() {
        return daysBack;
    }

    public void setDaysBack(String daysBack) {
        this.daysBack = daysBack;
    }

    @Transient
    public Boolean getShowPtMessage() {
        return showPtMessage;
    }

    public void setShowPtMessage(Boolean showPtMessage) {
        this.showPtMessage = showPtMessage;
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
    @JoinColumn(name = "DependentId", insertable = true, updatable = true)
    public PatientProfileMembers getPatientProfileMembers() {
        return patientProfileMembers;
    }

    public void setPatientProfileMembers(PatientProfileMembers patientProfileMembers) {
        this.patientProfileMembers = patientProfileMembers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DrugId", nullable = true, insertable = true, updatable = true)
    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    @Column(name = "RedeemPoints")
    public String getRedeemPoints() {
        return redeemPoints;
    }

    public void setRedeemPoints(String redeemPoints) {
        this.redeemPoints = redeemPoints;
    }

    @Column(name = "RedeemPointsCost")
    public Double getRedeemPointsCost() {
        return redeemPointsCost;
    }

    public void setRedeemPointsCost(Double redeemPointsCost) {
        this.redeemPointsCost = redeemPointsCost;
    }

    @Column(name = "HandLingFee")
    public Double getHandLingFee() {
        return handLingFee;
    }

    public void setHandLingFee(Double handLingFee) {
        this.handLingFee = handLingFee;
    }

    @Column(name = "Strength")
    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    @Column(name = "DrugName")
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    @Column(name = "DrugType")
    public String getDrugType() {
        return drugType;
    }

    public void setDrugType(String drugType) {
        this.drugType = drugType;
    }

    @Column(name = "DrugPrice")
    public Double getDrugPrice() {
        return drugPrice;
    }

    public void setDrugPrice(Double drugPrice) {
        this.drugPrice = drugPrice;
    }

    @Column(name = "Quantity")
    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    @Transient
    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    @Transient
    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    @Transient
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Column(name = "Apartment")
    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    @Column(name = "ViewStatus")
    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StatusCreatedOn", length = 19)
    public Date getStatusCreatedOn() {
        return statusCreatedOn;
    }

    /**
     * @param statusCreatedOn the statusCreatedOn to set
     */
    public void setStatusCreatedOn(Date statusCreatedOn) {
        this.statusCreatedOn = statusCreatedOn;
    }

    /**
     * @return the orderType
     */
    @Column(name = "OrderType")
    public String getOrderType() {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    ///////////////////////////////////////////////////////////
    @Column(name = "DaysSupply")
    public Integer getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(Integer daysSupply) {
        this.daysSupply = daysSupply;
    }

    @Column(name = "RefillsAllowed")
    public Integer getRefillsAllowed() {
        return refillsAllowed;
    }

    public void setRefillsAllowed(Integer refillsAllowed) {
        this.refillsAllowed = refillsAllowed;
    }

    @Column(name = "RefillsRemaining")
    public Integer getRefillsRemaining() {
        return refillsRemaining;
    }

    public void setRefillsRemaining(Integer refillsRemaining) {
        this.refillsRemaining = refillsRemaining;
    }

    @Column(name = "PaymentMode")
    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    @Column(name = "PharmacyName")
    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    @Column(name = "PharmacyPhone")
    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    @Column(name = "PrescriberName")
    public String getPrescriberName() {
        return prescriberName;
    }

    public void setPrescriberName(String prescriberName) {
        this.prescriberName = prescriberName;
    }

    @Column(name = "PrescriberPhone")
    public String getPrescriberPhone() {
        return prescriberPhone;
    }

    public void setPrescriberPhone(String prescriberPhone) {
        this.prescriberPhone = prescriberPhone;
    }

    @Column(name = "PrescriberNPI")
    public String getPrescriberNPI() {
        return prescriberNPI;
    }

    public void setPrescriberNPI(String prescriberNPI) {
        this.prescriberNPI = prescriberNPI;
    }

    @Column(name = "RxNumber")
    public String getRxNumber() {
        return rxNumber;
    }

    public void setRxNumber(String rxNumber) {
        this.rxNumber = rxNumber;
    }

    //////////////////////////////////////////////////////////
    @Transient
    public BigDecimal getAwardedPoints() {
        return awardedPoints;
    }

    public void setAwardedPoints(BigDecimal awardedPoints) {
        this.awardedPoints = awardedPoints;
    }

    /**
     * @return the transferDetail
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TransferDetailId", nullable = true, insertable = true, updatable = true)
    public TransferDetail getTransferDetail() {
        return transferDetail;
    }

    /**
     * @param transferDetail the transferDetail to set
     */
    public void setTransferDetail(TransferDetail transferDetail) {
        this.transferDetail = transferDetail;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    public List<OrdersPtMessage> getOrdersPtMessagesList() {
        return ordersPtMessagesList;
    }

    public void setOrdersPtMessagesList(List<OrdersPtMessage> ordersPtMessagesList) {
        this.ordersPtMessagesList = ordersPtMessagesList;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    public List<RewardHistory> getRewardHistorySet() {
        return rewardHistorySet;
    }

    public void setRewardHistorySet(List<RewardHistory> rewardHistorySet) {
        this.rewardHistorySet = rewardHistorySet;
    }

    @Column(name = "Video")
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Column(name = "ImagePath")
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Column(name = "Comments")
    public String getPatientComments() {
        return patientComments;
    }

    public void setPatientComments(String patientComments) {
        this.patientComments = patientComments;
    }

    @Column(name = "FILLEDDATE")
    public Date getFilledDate() {
        return filledDate;
    }

    public void setFilledDate(Date filledDate) {
        this.filledDate = filledDate;
    }

    @Column(name = "REFILLINDEX")
    public Integer getRefillIndex() {
        return refillIndex;
    }

    public void setRefillIndex(Integer refillIndex) {
        this.refillIndex = refillIndex;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DrugNdc", nullable = true, insertable = true, updatable = true)
    public DrugDetail getDrugDetail() {
        return drugDetail;
    }

    public void setDrugDetail(DrugDetail drugDetail) {
        this.drugDetail = drugDetail;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderChainId", nullable = true, insertable = true, updatable = true)
    public OrderChain getOrderChain() {
        return orderChain;
    }

    public void setOrderChain(OrderChain orderChain) {
        this.orderChain = orderChain;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderBatchId", nullable = true, insertable = true, updatable = true)
    public OrderBatch getOrderBatch() {
        return orderBatch;
    }

    public void setOrderBatch(OrderBatch orderBatch) {
        this.orderBatch = orderBatch;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DeleiveryPreferenceId", nullable = true, insertable = true, updatable = true)
    public DeliveryPreferences getDeliveryPreference() {
        return deliveryPreference;
    }

    public void setDeliveryPreference(DeliveryPreferences deliveryPreference) {
        this.deliveryPreference = deliveryPreference;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DeliveryPreferenceUsed")
    public DeliveryPreferences getDeliveryPreferenceUsed() {
        return deliveryPreferenceUsed;
    }

    public void setDeliveryPreferenceUsed(DeliveryPreferences deliveryPreferenceUsed) {
        this.deliveryPreferenceUsed = deliveryPreferenceUsed;
    }

    @Column(name = "DELIVERYCARRIER")
    public String getDeliverycarrier() {
        return deliverycarrier;
    }

    public void setDeliverycarrier(String deliverycarrier) {
        this.deliverycarrier = deliverycarrier;
    }

    @Column(name = "TRACLINGNO")
    public String getTraclingno() {
        return traclingno;
    }

    public void setTraclingno(String traclingno) {
        this.traclingno = traclingno;
    }

    @Column(name = "CLERK")
    public String getClerk() {
        return clerk;
    }

    public void setClerk(String clerk) {
        this.clerk = clerk;
    }

    @Column(name = "IsPrescriptionHardCopy")
    public Boolean getIsPrescriptionHardCopy() {
        return isPrescriptionHardCopy;
    }

    public void setIsPrescriptionHardCopy(Boolean isPrescriptionHardCopy) {
        this.isPrescriptionHardCopy = isPrescriptionHardCopy;
    }

    @Column(name = "AddCopyCard")
    public Boolean getAddCopyCard() {
        return addCopyCard;
    }

    public void setAddCopyCard(Boolean addCopyCard) {
        this.addCopyCard = addCopyCard;
    }

    @Column(name = "PaymentType", nullable = false)
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "PaymentAuthorization", nullable = false)
    public String getPaymentAuthorization() {
        return paymentAuthorization;
    }

    public void setPaymentAuthorization(String paymentAuthorization) {
        this.paymentAuthorization = paymentAuthorization;
    }

    @Column(name = "PaymentId", nullable = false)
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @Transient
    public String getPrefName() {
        return prefName;
    }

    public void setPrefName(String prefName) {
        this.prefName = prefName;
    }

    @Transient
    public String getRequiresHandDelivery() {
        return requiresHandDelivery;
    }

    public void setRequiresHandDelivery(String requiresHandDelivery) {
        this.requiresHandDelivery = requiresHandDelivery;
    }

    /**
     *
     * @return
     */
    @Column(name = "nextRefillDate")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getNextRefillDate() {
        return nextRefillDate;
    }

    public void setNextRefillDate(Date nextRefillDate) {
        this.nextRefillDate = nextRefillDate;
    }

    public Order() {
    }

    @Column(name = "nextRefillFlag")
    public String getNextRefillFlag() {
        return nextRefillFlag;
    }

    public void setNextRefillFlag(String nextRefillReminderFlag) {
        this.nextRefillFlag = nextRefillReminderFlag;
    }

    @Column(name = "refilldone")
    public Integer getRefillDone() {
        return refillDone;
    }

    public void setRefillDone(Integer refilldone) {
        this.refillDone = refilldone;
    }

    @Column(name = "PriceIncludingMargins")
    public Double getPriceIncludingMargins() {
        return priceIncludingMargins;
    }

    public void setPriceIncludingMargins(Double priceIncludingMargins) {
        this.priceIncludingMargins = priceIncludingMargins;
    }

    @Column(name = "RxExpireDate")
    public Date getRxExpiredDate() {
        return rxExpiredDate;
    }

    public void setRxExpiredDate(Date rxExpiredDate) {
        this.rxExpiredDate = rxExpiredDate;
    }

    @Transient
    public String getRxExpiredDateStr() {
        return rxExpiredDateStr;
    }

    public void setRxExpiredDateStr(String rxExpiredDateStr) {
        this.rxExpiredDateStr = rxExpiredDateStr;
    }

    @Transient
    public List<CoPayCardDetails> getCoPayCardDetails() {
        return coPayCardDetails;
    }

    public void setCoPayCardDetails(List<CoPayCardDetails> coPayCardDetails) {
        this.coPayCardDetails = coPayCardDetails;
    }

    @Column(name = "MfrCost")
    public Double getMfrCost() {
        return mfrCost;
    }

    public void setMfrCost(Double mfrCost) {
        this.mfrCost = mfrCost;
    }

    @Column(name = "ProfitShareCost")
    public Double getProfitShareCost() {
        return profitShareCost;
    }

    public void setProfitShareCost(Double profitShareCost) {
        this.profitShareCost = profitShareCost;
    }

    @Column(name = "InsuranceType")
    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    @Transient
    public Date getRxDate() {
        return rxDate;
    }

    public void setRxDate(Date rxDate) {
        this.rxDate = rxDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DeliveryTo")
    public Date getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(Date deliveryTo) {
        this.deliveryTo = deliveryTo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DeliveryFrom")
    public Date getDeliveryFrom() {
        return deliveryFrom;
    }

    public void setDeliveryFrom(Date deliveryFrom) {
        this.deliveryFrom = deliveryFrom;
    }

    @Column(name = "FinalPaymentMode")
    public String getFinalPaymentMode() {
        return finalPaymentMode;
    }

    public void setFinalPaymentMode(String finalPaymentMode) {
        this.finalPaymentMode = finalPaymentMode;
    }

    @Column(name = "EstimatedPrice")
    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    @Transient
    public String getServiceRequested() {
        return serviceRequested;
    }

    public void setServiceRequested(String serviceRequested) {
        this.serviceRequested = serviceRequested;
    }

    @Column(name = "IsBottleAvailable")
    public Boolean getIsBottleAvailable() {
        return isBottleAvailable;
    }

    public void setIsBottleAvailable(Boolean isBottleAvailable) {
        this.isBottleAvailable = isBottleAvailable;
    }

    @Column(name = "ProfitSharePoint")
    public Integer getProfitSharePoint() {
        return profitSharePoint;
    }

    public void setProfitSharePoint(Integer profitSharePoint) {
        this.profitSharePoint = profitSharePoint;
    }

    @Column(name = "ActualProfitShare")
    public Float getActualProfitShare() {
        return actualProfitShare;
    }

    public void setActualProfitShare(Float actualProfitShare) {
        this.actualProfitShare = actualProfitShare;
    }

    @Column(name = "OldRxNumber")
    public String getOldRxNumber() {
        return oldRxNumber;
    }

    public void setOldRxNumber(String oldRxNumber) {
        this.oldRxNumber = oldRxNumber;
    }

    @Column(name = "ResponseRequired")
    public String getResponseRequired() {
        return responseRequired;
    }

    public void setResponseRequired(String responseRequired) {
        this.responseRequired = responseRequired;
    }

    @Column(name = "PatientResponse")
    public String getPatientResponse() {
        return patientResponse;
    }

    public void setPatientResponse(String patientResponse) {
        this.patientResponse = patientResponse;
    }

    @Column(name = "DeliveryDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Column(name = "Miles")
    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    @Column(name = "CareGiverRequest")
    public Integer getCareGiverRequest() {
        return careGiverRequest;
    }

    public void setCareGiverRequest(Integer careGiverRequest) {
        this.careGiverRequest = careGiverRequest;
    }

    @Column(name = "DisabledFlds")
    public Integer getDisabledFlds() {
        return disabledFlds;
    }

    public void setDisabledFlds(Integer disabledFlds) {
        this.disabledFlds = disabledFlds;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ShippedBy", nullable = true, insertable = true, updatable = true)
    public PharmacyUser getShippedBy() {
        return shippedBy;
    }

    public void setShippedBy(PharmacyUser shippedBy) {
        this.shippedBy = shippedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ShippedOn", length = 19)
    public Date getShippedOn() {
        return shippedOn;
    }

    public void setShippedOn(Date shippedOn) {
        this.shippedOn = shippedOn;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CustomerShippingDate")
    public Date getCustomerShippingDate() {
        return customerShippingDate;
    }

    public void setCustomerShippingDate(Date customerShippingDate) {
        this.customerShippingDate = customerShippingDate;
    }

    @Column(name = "MultiRx")
    public Boolean isMultiRx() {
        return multiRx;
    }

    public void setMultiRx(Boolean multiRx) {
        this.multiRx = multiRx;
    }

    @Column(name = "RequiresDeleteion")
    public Integer getRequiresDeletion() {
        return requiresDeletion;
    }

    public void setRequiresDeletion(Integer requiresDeletion) {
        this.requiresDeletion = requiresDeletion;
    }

    @Column(name = "OnlineOrder")
    public Boolean getOnlineOrder() {
        return onlineOrder;
    }

    public void setOnlineOrder(Boolean onlineOrder) {
        this.onlineOrder = onlineOrder;
    }

    @Column(name = "HandDelivery")
    public Integer getHandDelivery() {
        return handDelivery;
    }

    public void setHandDelivery(Integer handDelivery) {
        this.handDelivery = handDelivery;
    }

    @Column(name = "HandDeliveryAccepted")
    public Integer getHandDeliveryAccepted() {
        return handDeliveryAccepted;
    }

    public void setHandDeliveryAccepted(Integer handDeliveryAccepted) {
        this.handDeliveryAccepted = handDeliveryAccepted;
    }

    @Column(name = "prescriberLastName")
    public String getPrescriberLastName() {
        return prescriberLastName;
    }

    public void setPrescriberLastName(String prescriberLastName) {
        this.prescriberLastName = prescriberLastName;
    }

    @Column(name = "SystemGeneratedRxNumber")
    public String getSystemGeneratedRxNumber() {
        return systemGeneratedRxNumber;
    }

    public void setSystemGeneratedRxNumber(String systemGeneratedRxNumber) {
        this.systemGeneratedRxNumber = systemGeneratedRxNumber;
    }

    @Column(name = "ptOverridePrice")
    public Integer getPtOverridePrice() {
        return ptOverridePrice;
    }

    public void setPtOverridePrice(Integer ptOverridePrice) {
        this.ptOverridePrice = ptOverridePrice;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    public List<OrderTransferImages> getOrderTransferImages() {
        return orderTransferImages;
    }

    public void setOrderTransferImages(List<OrderTransferImages> orderTransferImages) {
        this.orderTransferImages = orderTransferImages;
    }

    @Column(name = "PharmacyExtension")
    public String getPharmacyExtension() {
        return pharmacyExtension;
    }

    public void setPharmacyExtension(String pharmacyExtension) {
        this.pharmacyExtension = pharmacyExtension;
    }

    @Column(name = "PrescriberExtension")
    public String getPrescriberExtension() {
        return prescriberExtension;
    }

    public void setPrescriberExtension(String prescriberExtension) {
        this.prescriberExtension = prescriberExtension;
    }

    @Transient
    public String getRxExpiry() {
        return rxExpiry;
    }

    public void setRxExpiry(String rxExpiry) {
        this.rxExpiry = rxExpiry;
    }

    @Column(name = "AutoDeletionFlag")
    public Integer getAutoDeletionFlag() {
        return autoDeletionFlag;
    }

    public void setAutoDeletionFlag(Integer autoDeletionFlag) {
        this.autoDeletionFlag = autoDeletionFlag;
    }

    @Column(name = "AutoDeletionDate")
    public Date getAutoDeletionDate() {
        return autoDeletionDate;
    }

    public void setAutoDeletionDate(Date autoDeletionDate) {
        this.autoDeletionDate = autoDeletionDate;
    }

    @Column(name = "ResponseFullFilled")
    public String getResponseFullFilled() {
        return responseFullFilled;
    }

    public void setResponseFullFilled(String responseFullFilled) {
        this.responseFullFilled = responseFullFilled;
    }

    @Column(name = "AutoRefillDate")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getAutoRefillDate() {
        return autoRefillDate;
    }

    public void setAutoRefillDate(Date autoRefillDate) {
        this.autoRefillDate = autoRefillDate;
    }

    @Column(name = "LastReminderDate")
    public Date getLastReminderDate() {
        return lastReminderDate;
    }

    public void setLastReminderDate(Date lastReminderDate) {
        this.lastReminderDate = lastReminderDate;
    }

    @Transient
    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    @Transient
    public String getLastFilledDate() {
        return lastFilledDate;
    }

    public void setLastFilledDate(String lastFilledDate) {
        this.lastFilledDate = lastFilledDate;
    }

    @Transient
    public Date getNextRefill() {
        return nextRefill;
    }

    public void setNextRefill(Date nextRefill) {
        this.nextRefill = nextRefill;
    }

    @Column(name = "RxPrefix")
    public String getRxPrefix() {
        return rxPrefix;
    }

    public void setRxPrefix(String rxPrefix) {
        this.rxPrefix = rxPrefix;
    }

    @Column(name = "ReadyToDeliverRxDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getReadyToDeliverRxDate() {
        return readyToDeliverRxDate;
    }

    public void setReadyToDeliverRxDate(Date readyToDeliverRxDate) {
        this.readyToDeliverRxDate = readyToDeliverRxDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReadyToDeliverRxOrdersId", insertable = true, updatable = true)
    public ReadyToDeliverRxOrders getReadyToDeliverRxOrders() {
        return readyToDeliverRxOrders;
    }

    public void setReadyToDeliverRxOrders(ReadyToDeliverRxOrders readyToDeliverRxOrders) {
        this.readyToDeliverRxOrders = readyToDeliverRxOrders;
    }

    @Transient
    @Override
    public String getCvvNumber() {
        return "";
    }

    @Override
    public void setCvvNumber(String cvvNumber) {

    }

    @Transient
    @Override
    public String getDrugImg() {
        return "";
    }

    @Override
    public void setDrugImg(String drugImg) {

    }

    @Transient
    @Override
    public String getCustomDocument() {
        return "";
    }

    @Override
    public void setCustomDocument(String customDocument) {

    }

}
