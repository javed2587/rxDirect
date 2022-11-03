package com.ssa.cms.service;

import com.google.gson.Gson;
import com.ssa.cms.bean.BusinessObject;
import com.ssa.cms.common.Constants;
import com.ssa.cms.common.JsonResponseComposer;
import com.ssa.cms.dao.OrderDAO;
import com.ssa.cms.dao.PMSTextFlowDAO;
import com.ssa.cms.dao.PatientProfileDAO;
import com.ssa.cms.dto.BaseDTO;
import com.ssa.cms.dto.CapsuleDTO;
import com.ssa.cms.dto.CoPayCardDetailsDTO;
import com.ssa.cms.dto.DeliveryDistanceFeeDTO;
import com.ssa.cms.dto.DeliveryPreferencesDTO;
import com.ssa.cms.dto.DrugBrandDTO;
import com.ssa.cms.dto.DrugCategoryListDTO;
import com.ssa.cms.dto.DrugDTO;
import com.ssa.cms.dto.DrugDetailDTO;
import com.ssa.cms.dto.DrugSearchesDTO;
import com.ssa.cms.dto.InsuranceCardDataDTO;
import com.ssa.cms.dto.LoginDTO;
import com.ssa.cms.dto.NotificationMessagesDTO;
import com.ssa.cms.dto.OrderCountDTO;
import com.ssa.cms.dto.OrderDetailDTO;
import com.ssa.cms.dto.OrderStatusDTO;
import com.ssa.cms.dto.OrderTransferImagesDTO;
import com.ssa.cms.dto.PatientDeliveryAddressDTO;
import com.ssa.cms.dto.PatientDeliveryAddressWithStatesDTO;
import com.ssa.cms.dto.PatientDependantDTO;
import com.ssa.cms.dto.PatientInsuranceDetailsDTO;
import com.ssa.cms.dto.PatientPaymentDTO;
import com.ssa.cms.dto.PatientPreferencesDTO;
import com.ssa.cms.dto.PatientProfileDTO;
import com.ssa.cms.dto.PatientProfileUpdateRequestResponseDTO;
import com.ssa.cms.dto.QuestionAnswerDTO;
import com.ssa.cms.dto.ReadyToDeliveryRxDTO;
import com.ssa.cms.dto.TabletDTO;
import com.ssa.cms.dto.TransferRxQueueDTO;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CoPayCardDetails;
import com.ssa.cms.model.ContactUs;
import com.ssa.cms.model.DeliveryDistanceFee;
import com.ssa.cms.model.DeliveryPreferences;
import com.ssa.cms.model.Drug;
import com.ssa.cms.model.DrugAdditionalMarginPrices;
import com.ssa.cms.model.DrugBasic;
import com.ssa.cms.model.DrugBrand;
import com.ssa.cms.model.DrugCategory;
import com.ssa.cms.model.DrugDetail;
import com.ssa.cms.model.DrugGeneric;
import com.ssa.cms.model.DrugGenericTypes;
import com.ssa.cms.model.DrugSearches;
import com.ssa.cms.model.DrugTherapyClass;
import com.ssa.cms.model.Event;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.FeeSettings;
import com.ssa.cms.model.JsonResponse;
import com.ssa.cms.model.MessageType;
import com.ssa.cms.model.MessageTypeId;
import com.ssa.cms.model.MessageResponses;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.NotificationStatus;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.OrderBatch;
import com.ssa.cms.model.OrderChain;
import com.ssa.cms.model.OrderHistory;
import com.ssa.cms.model.OrderPlaceEmail;
import com.ssa.cms.model.OrderStatus;
import com.ssa.cms.model.OrderTransferImages;
import com.ssa.cms.model.OrdersPtMessage;
import com.ssa.cms.model.PatientAddress;
import com.ssa.cms.model.PatientAllergies;
import com.ssa.cms.model.PatientDeliveryAddress;
import com.ssa.cms.model.PatientGlucoseResults;
import com.ssa.cms.model.PatientInsuranceDetails;
import com.ssa.cms.model.PatientPaymentInfo;
import com.ssa.cms.model.PatientProfile;
import com.ssa.cms.model.PatientProfileHealth;
import com.ssa.cms.model.PatientProfileMembers;
import com.ssa.cms.model.PatientProfileNotes;
import com.ssa.cms.model.PatientProfilePreferences;
import com.ssa.cms.model.PatientRewardLevel;
import com.ssa.cms.model.PharmacyZipCodes;
import com.ssa.cms.model.PreferencesSetting;
import com.ssa.cms.model.QuestionAnswer;
import com.ssa.cms.model.ReadyToDeliverRxOrders;
import com.ssa.cms.model.RewardHistory;
import com.ssa.cms.model.RewardPoints;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.model.State;
import com.ssa.cms.model.StateRewardTaxes;
import com.ssa.cms.model.StrengthJsonList;
import com.ssa.cms.model.TransferDetail;
import com.ssa.cms.model.TransferRequest;
import com.ssa.cms.model.ValidResponse;
import com.ssa.cms.model.YearEndStatementInfo;
import com.ssa.cms.model.ZipCodeCalculation;
import com.ssa.cms.util.AppUtil;
import com.ssa.cms.util.CommonUtil;
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EncryptionHandlerUtil;
import com.ssa.cms.util.FileUtil;
import com.ssa.cms.util.PropertiesUtil;
import com.ssa.cms.util.SMSUtil;
import com.ssa.decorator.MTDecorator;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Saber Hussain
 */
@Service
@Transactional
public class PatientProfileService {

    @Autowired
    private PatientProfileDAO patientProfileDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    PMSTextFlowDAO textFlowDAO;
    @Autowired
    ServletContext servletContext;
    private static final Log logger = LogFactory.getLog(PatientProfileService.class.getName());

    public boolean update(Object object) {
        boolean isUpdate = false;
        try {
            patientProfileDAO.update(object);
            isUpdate = true;
        } catch (Exception e) {
            isUpdate = false;
            logger.error("Exception: PatientProfileService -> update", e);
            e.printStackTrace();
        }
        return isUpdate;
    }

    public boolean merge(Object object) {
        boolean isUpdate = false;
        try {
            patientProfileDAO.merge(object);
            isUpdate = true;
        } catch (Exception e) {
            isUpdate = false;
            logger.error("Exception: PatientProfileService -> merge", e);
        }
        return isUpdate;
    }

    public List<LoginDTO> getPatientProfileList() {
        List<LoginDTO> list = new ArrayList<>();
        try {
            List<Object[]> lstObj = patientProfileDAO.getPatientProfilesListWithDefaultAddress();//.getPatientProfilesList();
            for (Object[] arr : lstObj) {
                PatientProfile profile = (PatientProfile) arr[0];
                LoginDTO loginDTO = CommonUtil.populateProfileUserData(profile);
                if (arr[1] != null) {
                    PatientDeliveryAddress addr = (PatientDeliveryAddress) arr[1];
                    loginDTO.setState(addr.getState() != null ? addr.getState().getAbbr() : "");
                } else {
                    loginDTO.setState("");
                }
                list.add(loginDTO);

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception: PatientProfileService -> getPatientProfileList", e);
        }
        return list;
    }

    public List<State> getStates() {
        List<State> list = new ArrayList<>();
        try {
            List<State> dblist = patientProfileDAO.getAllRecords(new State());
            for (State state : dblist) {
                State newState = new State();
                newState.setId(state.getId());
                newState.setName(state.getName());
                newState.setAbbr(state.getAbbr());
                list.add(newState);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getStates", e);
        }
        return list;
    }

    ////////////////////////////////////////////////
    public boolean clearDeviceTokenFromProfile(PatientProfile profile) throws Exception {
        if (profile != null && profile.getId() != null) {
            profile.setDeviceToken(null);
            this.patientProfileDAO.saveOrUpdate(profile);
            return true;
        }
        return false;

    }
    ///////////////////////////////////////////////

    public boolean savePatientInfo(PatientProfile patientProfile) {
        boolean isSaved;
        try {
            boolean isNew = patientProfile.getId() == null;
            if (patientProfile.getSecurityToken() != null) {
                patientProfile.setStatus(Constants.COMPLETED);
            } else {
                patientProfile.setStatus("Pending");
            }
            setPatientChild(patientProfile);

            patientProfile.setCreatedBy(0);
            patientProfile.setCreatedOn(new Date());
            patientProfile.setUpdatedBy(0);
            patientProfile.setUpdatedOn(new Date());
            patientProfileDAO.saveOrUpdate(patientProfile);
            if (isNew) {
                System.out.println("going to save preferences FOR:");
                System.out.println("Patient id " + patientProfile.getId());
                this.saveAllPreference(patientProfile.getId());
            }

            isSaved = true;
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> savePatientInfo", e);
            e.printStackTrace();
            isSaved = false;
        }
        return isSaved;
    }

    public void setPatientChild(PatientProfile patientProfile) {
        patientProfile.setContactUsList(null);
        patientProfile.setNotificationMessagesList(null);
        patientProfile.setOrders(null);
        patientProfile.setPatientGlucoseResultsList(null);
        patientProfile.setPatientProfileNotesList(null);
        patientProfile.setDrugSearchesList(null);
        patientProfile.setYearEndStatementInfoList(null);
    }

    public boolean save(Object obj) {
        boolean isSaved;
        try {
            patientProfileDAO.save(obj);
            isSaved = true;
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> savePatientInfo", e);
            isSaved = false;
        }
        return isSaved;
    }

    public String getURL(String urlCode) {
        String urlString = "";
        try {
            urlString = patientProfileDAO.getURL(urlCode);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return urlString;
    }

    public Object getObjectById(Object obj, int id) {
        return patientProfileDAO.getObjectById(obj, id);
    }

    public Object getObjectById(Object obj, String id) {
        return patientProfileDAO.findRecordById(obj, id);
    }

    public Order getOrderById(Object obj, String id) throws Exception {
        Order order = (Order) patientProfileDAO.findRecordById(obj, id);
        if (order != null) {
            Hibernate.initialize(order.getDrugDetail());
            if (order.getOrderChain() != null && order.getOrderChain().getRxExpiredDate() != null) {
                order.setRxExpiredDateStr(DateUtil.dateToString(order.getOrderChain().getRxExpiredDate(), "MM/dd/yyyy"));
            } else {
                order.setRxExpiredDateStr("N/A");
            }

            if (order.getOrderChain() != null && order.getOrderChain().getLastRefillDate() != null) {
                order.setLastFilledDate(DateUtil.dateToString(order.getOrderChain().getLastRefillDate(), "MM/dd/yyyy"));
            }
        }
        return order;//patientProfileDAO.findRecordById(obj, id);
    }

    public List<RewardHistory> getPatientRewardHistory(Integer id) {
        try {
            List<RewardHistory> list = this.patientProfileDAO.getRewardHistoryByProfileId(id);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
            // Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List getPatientInsuranceCards(Integer patientId) {
        List<InsuranceCardDataDTO> list = new ArrayList<>();
        List<BusinessObject> lstObj = new ArrayList();
        BusinessObject obj = new BusinessObject("patientProfile.id", patientId, Constants.HIBERNATE_EQ_OPERATOR);
        lstObj.add(obj);
        obj = new BusinessObject("isArchived", 0, Constants.HIBERNATE_EQ_OPERATOR);
        lstObj.add(obj);
        List<PatientInsuranceDetails> lst = this.patientProfileDAO.findByNestedProperty(new PatientInsuranceDetails(), lstObj, "isPrimary", Constants.HIBERNATE_DESC_ORDER);
        for (PatientInsuranceDetails patientInsuranceDetail : lst) {
            InsuranceCardDataDTO icddto = populatePatientInsuranceCardDetail(patientInsuranceDetail);
            list.add(icddto);
        }
        return list;
    }

    private InsuranceCardDataDTO populatePatientInsuranceCardDetail(PatientInsuranceDetails patientInsuranceDetail) {
        InsuranceCardDataDTO icddto = new InsuranceCardDataDTO();
        icddto.setId(patientInsuranceDetail.getId());
        if (patientInsuranceDetail.getIsPrimary() == null) {
            icddto.setIsPrimary(0);
        }
        icddto.setCardHolderRelationship(patientInsuranceDetail.getCardHolderRelationship());
        icddto.setInsuranceFrontCardPath(patientInsuranceDetail.getInsuranceFrontCardPath());
        icddto.setInsuranceBackCardPath(patientInsuranceDetail.getInsuranceBackCardPath());
        return icddto;
    }

    public PatientProfile getPatientProfileById(Integer id) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfile(id);
            if (patientProfile != null && patientProfile.getId() != null) {
                if (patientProfile.getBillingAddress() != null) {
                    State state = (State) getObjectById(new State(), patientProfile.getBillingAddress().getStateId());
                    patientProfile.getBillingAddress().setState(state);
                    List lstTaxes = this.patientProfileDAO.findByNestedProperty(
                            new StateRewardTaxes(),
                            "state.id", state.getId(), Constants.HIBERNATE_EQ_OPERATOR, "", 0);
                    if (lstTaxes == null || lstTaxes.isEmpty()) {
                        patientProfile.setStateTaxPercent(0f);
                    } else {
                        StateRewardTaxes rewardTaxes = (StateRewardTaxes) lstTaxes.get(0);
                        patientProfile.setStateTaxPercent(rewardTaxes.getTaxPercentage() == null ? 0f : rewardTaxes.getTaxPercentage());
                    }
                }
                List<PatientProfileMembers> profileMemberses = patientProfileDAO.getPatientProfileMembersListById(id);
                if (profileMemberses != null && profileMemberses.size() > 0) {
                    patientProfile.setPatientProfileMembersList(profileMemberses);
                }
                Long totalRewardPoint = patientProfileDAO.getTotalRewardHistoryPoints(id);
                if (totalRewardPoint != null) {
                    patientProfile.setTotalRewardPoints(totalRewardPoint);
                } else {
                    patientProfile.setTotalRewardPoints(0L);
                }
                List<PatientDeliveryAddress> deliveryAddressList = patientProfile.getPatientDeliveryAddresses();
                if (deliveryAddressList != null && deliveryAddressList.size() > 0) {
                    PatientDeliveryAddress address = deliveryAddressList.get(0);
                    patientProfile.setDefaultAddress(AppUtil.getSafeStr(address.getDescription(), "") + " " + AppUtil.getSafeStr(address.getAddress(), "") + ":"
                            + AppUtil.getSafeStr(address.getZip(), ""));
                    patientProfile.setDefaultAddresszip(AppUtil.getSafeStr(address.getZip(), ""));
                }
                List<Order> ordersList = patientProfileDAO.getOrdersListByProfileId(id);
                if (ordersList.size() > 0) {
                    patientProfile.setOrders(ordersList);
                } else {
                    patientProfile.setOrders(ordersList);
                }
            } else {
                logger.info("PatientProfile id is null: " + id);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getPatientProfileById", e);
        }
        return patientProfile;
    }

    public boolean updatePatientProfile(PatientProfile patientProfile) {
        boolean isUpdate = false;
        try {
            if (patientProfile.getStatus() == null) {
                patientProfile.setStatus("Pending");
            }
            isUpdate = patientProfileDAO.updatePatientInfo(patientProfile);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> updatePatientProfile", e);
        }
        return isUpdate;
    }

    public String isCommunicationIdExist(String communicationId) {
        String statusCode = "";
        try {
            statusCode = patientProfileDAO.isCommunicationIdExist(communicationId);
            logger.info("StatusCode value is: " + statusCode);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> isCommunicationIdExist", e);
        }
        return statusCode;
    }

    public SmtpServerInfo getSmtpServerInfo(String campaignId) {
        SmtpServerInfo smtpServerInfo = null;
        try {
            smtpServerInfo = patientProfileDAO.getSmtpServerInfo(campaignId);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getSmtpServerInfo", e);
        }
        return smtpServerInfo;
    }

    public boolean sendVerificationCode(String mobileNumber, String message) throws Exception {
        boolean phoneValidity = false;
        try {
            logger.info("Mobile Number: " + mobileNumber);
            if (!isPatientPhoneNumberExist(mobileNumber)) {
                logger.info("This phone# is not registered.");
                throw new Exception("This phone# is not registered.");
            }
            String phonevalidationUrl = Constants.PHONE_VALIDATION_URL; //patientProfileDAO.getURL(Constants.PVURL);
            PhoneValidationService phoneValidationService = new PhoneValidationService(phonevalidationUrl);
            phoneValidity = phoneValidationService.checkPhoneValidity(mobileNumber, "", "GenRx");
            if (phoneValidity) {
                //send verification code
                MTDecorator decorator = SMSUtil.sendSmsText(mobileNumber, message);
                NotificationStatus notificationStatus = new NotificationStatus();
                notificationStatus.setEffectiveDate(new Date());
                notificationStatus.setEndDate(new Date());
                if (decorator.getErrorCode() != null) {
                    logger.info("Error code is: " + decorator.getErrorCode());
                    notificationStatus.setStatusCode(decorator.getErrorCode());
                }
                notificationStatus.setNarrative(decorator.getErrorDescription());
                notificationStatus.setPhoneNumber(EncryptionHandlerUtil.getEncryptedString(mobileNumber));
                patientProfileDAO.save(notificationStatus);
                logger.info("Messsage Sent: " + phoneValidity);
            }
        } catch (Exception ex) {
            phoneValidity = false;
            logger.error("Exception", ex);
            ex.printStackTrace();
        }
        return phoneValidity;
    }

    public List<DeliveryPreferences> getDeliveryPreferences() {
        List<DeliveryPreferences> list = new ArrayList<>();
        try {
            List<DeliveryPreferences> dbList = patientProfileDAO.getAllRecords(new DeliveryPreferences());
            for (DeliveryPreferences preference : dbList) {
                DeliveryPreferences deliveryPreferences = new DeliveryPreferences();
                deliveryPreferences.setId(preference.getId());
                deliveryPreferences.setName(preference.getName());
                deliveryPreferences.setDescription(preference.getDescription());
                list.add(deliveryPreferences);
            }
        } catch (Exception ex) {
            logger.error("Exception", ex);
        }
        return list;
    }

    public PatientAddress saveAddress(String addressLine, String appt, String description, String city, String stateId, String zip, String addressType, PatientAddress patientAddress) throws NumberFormatException {
        PatientAddress address = new PatientAddress();
        try {
            if (patientAddress != null && !CommonUtil.isNullOrEmpty(patientAddress.getId())) {
                address = patientProfileDAO.getPatientAddressById(patientAddress.getId());
            } else {
                address.setCreatedBy(0);
                address.setCreatedOn(new Date());
            }
            address.setAddress(addressLine);
            address.setApartment(appt);
            address.setAddressDescription(description);
            address.setCity(city);
            address.setStateId(Short.parseShort(stateId));
            address.setZip(zip);
            address.setAddressType(addressType);
            address.setUpdatedBy(0);
            address.setUpdatedOn(new Date());
            patientProfileDAO.saveOrUpdate(address);

        } catch (Exception ex) {
            logger.error("Exception", ex);
        }
        return address;
    }

    public PatientPaymentInfo savePaymentInfo(Integer profileId, String cardHolderName, String cardNumber, String cvv, String expiry, String cardType, String defaultCard) {
        PatientPaymentInfo payment = new PatientPaymentInfo();
        try {
            logger.info("Expiry date before save: " + expiry);
            System.out.println("Default CARD " + defaultCard);
            List<PatientPaymentInfo> list = patientProfileDAO.getPatientPaymentInfoListByProfileId(profileId);
            updatePreviousPaymentDefaultCard(list, defaultCard);
            payment.setPatientProfile(new PatientProfile(profileId));
            payment.setCreatedBy(0);
            payment.setCreatedOn(new Date());
            payment.setCardHolderName(cardHolderName);
            payment.setCardNumber(cardNumber);
            payment.setCvvNumber(cvv);
            payment.setExpiryDate(expiry);
            payment.setCardType(cardType);
            payment.setDefaultCard(defaultCard);
            payment.setUpdatedBy(0);
            payment.setUpdatedOn(new Date());
            patientProfileDAO.save(payment);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception", ex);
        }
        return payment;

    }

    private void updatePreviousPaymentDefaultCard(List<PatientPaymentInfo> list, String defaultCard, Integer paymentId) throws Exception {
        if (list != null && list.size() > 0) {
            if (defaultCard.equalsIgnoreCase("Yes")) {
                for (PatientPaymentInfo info : list) {
                    logger.info("Previous Default Crad " + info.getDefaultCard() + " Id is: " + info.getId());
                    if (!Objects.equals(info.getId(), paymentId) && info.getDefaultCard().equalsIgnoreCase("Yes")) {
                        info.setDefaultCard("No");
                        patientProfileDAO.merge(info);
                    }
                }
            }
        }
    }

    public boolean isVerificationCodeExist(String phoneNumber, Integer code) {
        boolean isExist = false;
        try {
            isExist = patientProfileDAO.isVerificationCodeExist(EncryptionHandlerUtil.getEncryptedString(phoneNumber), code);
            logger.info("isVerificationCodeExist: " + isExist);
        } catch (Exception e) {
            logger.error("Exception -> isVerificationCodeExist", e);
        }
        return isExist;
    }

    public boolean isPatientPhoneNumberExist(String phoneNumber) {
        boolean isExist = false;
        try {
            isExist = patientProfileDAO.isPatientPhoneNumberExist(EncryptionHandlerUtil.getEncryptedString(phoneNumber));

        } catch (Exception e) {
            logger.error("Exception -> isVerificationCodeExist", e);
        }
        return isExist;
    }

    public boolean isVerificationCodeExist(String phoneNumber, Integer code, String firebaseToken) {
        boolean isExist = false;
        try {
            //isExist = patientProfileDAO.isVerificationCodeExist(phoneNumber, code);
            List lst = this.patientProfileDAO.retrieveatientByPhoneAndCode(EncryptionHandlerUtil.getEncryptedString(phoneNumber), code);
            if (lst != null && lst.size() > 0) {
                PatientProfile profile = (PatientProfile) lst.get(0);
                profile.setDeviceToken(firebaseToken);
                this.createDeviceTokenForPatient(profile, firebaseToken);
                isExist = true;
            }
            logger.info("isVerificationCodeExist: " + isExist);
        } catch (Exception e) {
            logger.error("Exception -> isVerificationCodeExist", e);
        }
        return isExist;
    }

    public boolean isVerificationCodeExist(String phoneNumber, Integer code, String firebaseToken, String osType) {
        boolean isExist = false;
        try {
            //isExist = patientProfileDAO.isVerificationCodeExist(phoneNumber, code);
            List lst = this.patientProfileDAO.retrieveatientByPhoneAndCode(EncryptionHandlerUtil.getEncryptedString(phoneNumber), code);
            if (lst != null && lst.size() > 0) {
                PatientProfile profile = (PatientProfile) lst.get(0);
                profile.setDeviceToken(firebaseToken);
                profile.setOsType(AppUtil.getSafeStr(osType, "20"));
                this.createDeviceTokenForPatient(profile, firebaseToken);
                isExist = true;
            }
            logger.info("isVerificationCodeExist: " + isExist);
        } catch (Exception e) {
            logger.error("Exception -> isVerificationCodeExist", e);
            e.printStackTrace();
        }
        return isExist;
    }

    public boolean savePatientInsuranceCardInfo(PatientProfile patientProfile) {
        boolean isSaved = false;
        try {
            PatientProfile oldPatientProfile = patientProfileDAO.getPatientProfile(patientProfile.getId());
            if (oldPatientProfile != null) {
                oldPatientProfile.setUpdatedBy(0);
                oldPatientProfile.setUpdatedOn(new Date());
                oldPatientProfile.setCardHolderRelation(patientProfile.getCardHolderRelation());
                //  oldPatientProfile.setInsuranceCardFront(patientProfile.getInsuranceCardFront());
                //oldPatientProfile.setInsuranceCardBack(patientProfile.getInsuranceCardBack());
                oldPatientProfile.setInsuranceBackCardPath(patientProfile.getInsuranceBackCardPath());
                oldPatientProfile.setInsuranceFrontCardPath(patientProfile.getInsuranceFrontCardPath());
                oldPatientProfile.setSecurityToken(patientProfile.getSecurityToken());
                oldPatientProfile.setStatus(Constants.COMPLETED);
                patientProfileDAO.merge(oldPatientProfile);
                isSaved = true;
            }
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> savePatientInsuranceCardInfo", e);
        }
        return isSaved;
    }

    public RewardPoints getRewardPoints(Integer id) {
        RewardPoints rewardPoints = new RewardPoints();
        try {
            rewardPoints = patientProfileDAO.getRewardPoints(id);
        } catch (Exception e) {
            logger.error("Exception -> getRewardPoints", e);
        }
        return rewardPoints;
    }

    public boolean saveRewardHistory(String description, Integer point, Integer patientId, String type) {
        boolean isSaved = false;
        try {
            logger.info("Reward Points: " + point);
            RewardHistory rewardHistory = new RewardHistory();
            rewardHistory.setPatientId(patientId);
            rewardHistory.setDescription(description);
            rewardHistory.setType(type);
            rewardHistory.setPoint(point);
            rewardHistory.setCreatedDate(new Date());
            patientProfileDAO.save(rewardHistory);
            isSaved = true;
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> saveRewardHistory", e);
        }
        return isSaved;
    }

    public boolean saveRewardHistory(String description, Integer point, Integer patientId, String type, Order order) {
        boolean isSaved = false;
        try {
            logger.info("Reward Points: " + point);
            RewardHistory rewardHistory = new RewardHistory();
            rewardHistory.setPatientId(patientId);
            rewardHistory.setDescription(description);
            rewardHistory.setType(type);
            rewardHistory.setPoint(point);
            rewardHistory.setCreatedDate(new Date());
            rewardHistory.setOrder(order);
            patientProfileDAO.save(rewardHistory);
            isSaved = true;
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> saveRewardHistory", e);
        }
        return isSaved;
    }

    public boolean updateVerificationCode(Integer verificationCode, String mobileNumber) {
        boolean isUpdate = false;
        try {
            isUpdate = patientProfileDAO.updateVerificationCode(verificationCode, EncryptionHandlerUtil.getEncryptedString(mobileNumber));
        } catch (Exception e) {
            e.printStackTrace();
            isUpdate = false;
            logger.error("Exception -> saveRewardHistory", e);
        }
        return isUpdate;
    }

    public PatientProfile getPatientProfileByMobileNumber(String mobileNumber) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfileByMobileNumber(EncryptionHandlerUtil.getEncryptedString(mobileNumber));
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByMobileNumber", e);
            e.printStackTrace();
        }
        return patientProfile;
    }

    public boolean deleteRecord(Integer id) {
        boolean isDeleted = false;
        try {
            List<PatientProfile> patientProfilesList = patientProfileDAO.findByNestedProperty(new PatientProfile(), "id", id, Constants.HIBERNATE_EQ_OPERATOR, null, 0);
            if (CommonUtil.isNullOrEmpty(patientProfilesList)) {
                return isDeleted;
            }
            for (PatientProfile patientProfile : patientProfilesList) {
                CommonUtil.delPatientProfileData(patientProfile, patientProfileDAO);
                isDeleted = true;
            }
        } catch (Exception e) {
            isDeleted = false;
            logger.error("Exception:: deleteRecord", e);
        }
        return isDeleted;
    }

    public PatientDependantDTO savePatientProfileMembers(Integer profileId, String firstName, String lastName,
            String dob, String gender, String allergies, String dermatologist, String email) {
        PatientDependantDTO patientProfileDependantDTO = null;
        PatientAllergies patientAllergies = new PatientAllergies();
        PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
        try {
            PatientProfileMembers patientProfileMembers = new PatientProfileMembers();
            patientProfileMembers.setPatientId(profileId);
            patientProfileMembers.setFirstName(firstName);
            patientProfileMembers.setLastName(lastName);
            patientProfileMembers.setGender(gender);
            if (dob != null && !"undefined".equals(dob)) {
                patientProfileMembers.setDob(DateUtil.stringToDate(dob, "MM/dd/yyyy"));
            }
            patientProfileMembers.setAllergies(allergies);
            patientProfileMembers.setDermatologist(dermatologist);
            patientProfileMembers.setEmail(AppUtil.getSafeStr(email, ""));
            patientProfileMembers.setCreatedOn(new Date());
            patientProfileMembers.setIsAdult(Boolean.FALSE);
            patientProfileMembers.setIsApproved(Boolean.FALSE);
            //before save populate data in patientProfileDependantDTO
            patientProfileDependantDTO = CommonUtil.populatePatientDependant(patientProfileMembers);
            patientProfileDAO.save(patientProfileMembers);
            if (AppUtil.getSafeStr(allergies, "").length() > 0) {
                patientAllergies.setAllergies(AppUtil.getSafeStr(allergies, ""));
                patientAllergies.setPatientProfileMembers(patientProfileMembers);
                patientAllergies.setPatientProfile(profile);
                patientProfileDAO.save(patientAllergies);
            }
            patientProfileDependantDTO.setId(patientProfileMembers.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> savePatientProfileMembers", e);
        }
        return patientProfileDependantDTO;
    }

    public boolean updateAllergies(String token, String allergies) {
        boolean isUpdate = false;
        PatientAllergies patientAllergies = new PatientAllergies();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null) {
                patientProfile.setAllergies(allergies);
                patientProfileDAO.merge(patientProfile);

                patientAllergies.setAllergies(allergies);
                patientAllergies.setPatientProfile(patientProfile);
                patientProfileDAO.save(patientAllergies);
                isUpdate = true;
            } else {
                logger.info("In valid token: " + token);
                isUpdate = false;
            }
        } catch (Exception e) {
            isUpdate = false;
            e.printStackTrace();
            logger.error("Exception -> updateAllergies", e);
        }
        return isUpdate;
    }

    public PatientProfile getPatientProfileByToken(String token) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null) {
                this.populateDependentAndIsuranceCountsForPatient(patientProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientProfileByToken", e);
        }
        return patientProfile;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    public PatientProfile getPatientProfileByToken(String token, Integer dependentId) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null) {
                if (dependentId != null && dependentId > 0) {
                    this.populateDependentAndIsuranceCountsForPatient(patientProfile, dependentId);
                } else {
                    this.populateDependentAndIsuranceCountsForPatient(patientProfile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientProfileByToken", e);
        }
        return patientProfile;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public void populateDependentAndIsuranceCountsForPatient(PatientProfile profile) throws Exception {
        if (profile != null) {
            Long count = this.patientProfileDAO.populateDependentsCount(profile.getId());
            profile.setDependentCount(count != null ? count : 0L);
            Long insCount = this.patientProfileDAO.populateInsCardCount(profile.getId());
            profile.setInsCardCount(insCount != null ? insCount : 0L);

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public void populateDependentAndIsuranceCountsForPatient(PatientProfile profile, Integer dpendentId) throws Exception {
        if (profile != null) {
            Long count = this.patientProfileDAO.populateDependentsCount(profile.getId());
            profile.setDependentCount(count != null ? count : 0L);
            Long insCount = this.patientProfileDAO.populateInsCardCountForDependent(dpendentId);
            profile.setDependentInsCardCount(insCount != null ? insCount : 0L);

        }
    }
    //////////////////////////////////////////////////////////////////////////////////////

    public PatientProfile createDeviceTokenForPatient(String securityToken, String deviceToken) {

        try {
            PatientProfile patientProfile = this.getPatientProfileBySecurityToken(securityToken);
            if (patientProfile != null) {
                patientProfile.setDeviceToken(deviceToken);
                this.patientProfileDAO.saveOrUpdate(patientProfile);
            }
            return patientProfile;
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
            return null;
        }

    }

    public PatientProfile createDeviceTokenForPatient(PatientProfile patientProfile, String deviceToken) {

        try {

            if (patientProfile != null) {
                patientProfile.setDeviceToken(deviceToken);
                this.patientProfileDAO.saveOrUpdate(patientProfile);
            }
            return patientProfile;
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
            return null;
        }

    }

    public boolean updateInsuranceCardWs(String token, String cardHolderRelation, byte[] insuranceCardFront, byte[] insuranceCardBack, String imgFcPath, String imgBcPath) {
        boolean isUpdate = false;
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null) {
                patientProfile.setCardHolderRelation(cardHolderRelation);
                //  patientProfile.setInsuranceCardFront(insuranceCardFront);
                //patientProfile.setInsuranceCardBack(insuranceCardBack);
                patientProfile.setInsuranceFrontCardPath(imgFcPath);
                patientProfile.setInsuranceBackCardPath(imgBcPath);
                patientProfileDAO.merge(patientProfile);
                isUpdate = true;
            } else {
                logger.info("In valid token: " + token);
                isUpdate = false;
            }
        } catch (Exception e) {
            logger.error("Exception:: updateInsuranceCardWs", e);
        }
        return isUpdate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //patientProfile, cardHolderRelation, 
//                    decodedFrontCard, decodedBackCard, imgFcPath, imgBcPath, memberId, planId, 
//                    effectiveDate, expiryDate, createdOn, updatedOn, insuranceProvider, 
//                    groupNumber, providerPhone, providerAddress, isPramiry
    public boolean addInsuranceCard(PatientProfile patientProfile, String cardHolderRelation,
            byte[] insuranceCardFront, byte[] insuranceCardBack, String imgFcPath, String imgBcPath,
            String memberId, Long cardId, String planId, String effectiveDate, String expiryDate, String createdOn,
            String updatedOn, String insuranceProvider, String groupNumber, String providerPhone,
            String providerAddress, int isPramiry, Integer dependentId) throws Exception {
        boolean isAdded = false;
        PatientInsuranceDetails insuranceCard = new PatientInsuranceDetails();
        PatientProfileMembers dependent = null;
        if (cardId != 0) {
            insuranceCard = (PatientInsuranceDetails) patientProfileDAO.findRecordById(new PatientInsuranceDetails(), cardId);
        }

        if (dependentId != null && dependentId > 0) {
            dependent = (PatientProfileMembers) patientProfileDAO.findRecordById(new PatientProfileMembers(), dependentId);
        }

        if (patientProfile != null) {
            List<BusinessObject> lstObj = new ArrayList();
            List<PatientInsuranceDetails> patientInsuranceDetailsList = new ArrayList<PatientInsuranceDetails>();

            if (isPramiry == 1) {
                lstObj.add(new BusinessObject("patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR));
                lstObj.add(new BusinessObject("isPrimary", 1, Constants.HIBERNATE_EQ_OPERATOR));
                if (dependent != null && dependent.getId() != null) {
                    lstObj.add(new BusinessObject("dependent.id", dependent.getId(), Constants.HIBERNATE_EQ_OPERATOR));
                } else {
                    lstObj.add(new BusinessObject("dependent.id", null, Constants.HIBERNATE_EQ_OPERATOR));
                }
                patientInsuranceDetailsList = patientProfileDAO.findByProperty(new PatientInsuranceDetails(), lstObj, "", Constants.HIBERNATE_ASC_ORDER);
                if (!CommonUtil.isNullOrEmpty(patientInsuranceDetailsList)) {
                    patientInsuranceDetailsList.get(0).setIsPrimary(0);
                    patientProfileDAO.update(patientInsuranceDetailsList.get(0));
                }
            }

            insuranceCard.setPatientProfile(patientProfile);
            insuranceCard.setMemberID(AppUtil.getSafeStr(memberId, ""));
            insuranceCard.setInsuranceProvider(AppUtil.getSafeStr(insuranceProvider, ""));
            insuranceCard.setGroupNumber(AppUtil.getSafeStr(groupNumber, ""));
            insuranceCard.setPlanId(AppUtil.getSafeStr(planId, ""));
            insuranceCard.setProviderPhone(AppUtil.getSafeStr(providerPhone, ""));
            insuranceCard.setProviderAddress(AppUtil.getSafeStr(providerAddress, ""));

            if (expiryDate != null) {
                insuranceCard.setExpiryDate(DateUtil.stringToDate(expiryDate, Constants.DATE_FORMATE_SHORT));
            }
            if (createdOn != null) {
                insuranceCard.setCreatedOn(DateUtil.stringToDate(createdOn, Constants.DATE_FORMATE_SHORT));
            }
            if (updatedOn != null) {
                insuranceCard.setCreatedOn(DateUtil.stringToDate(updatedOn, Constants.DATE_FORMATE_SHORT));
            }
            if (cardHolderRelation != null) {
                insuranceCard.setCardHolderRelationship(cardHolderRelation);
            }
            if (effectiveDate != "") {
                insuranceCard.setEffectiveDate(DateUtil.stringToDate(effectiveDate, Constants.DATE_FORMATE_SHORT));
            }
            insuranceCard.setInsuranceFrontCardPath(imgFcPath);
            insuranceCard.setInsuranceBackCardPath(imgBcPath);
            insuranceCard.setIsPrimary(isPramiry);
            insuranceCard.setIsArchived(0);
            if (dependent != null) {
                insuranceCard.setDependent(dependent);
            }
            patientProfileDAO.saveOrUpdate(insuranceCard);
            isAdded = true;
        }
        return isAdded;
    }

    public boolean archiveInsuranceCard(PatientProfile patientProfile, Long cardId) throws Exception {
        boolean isArchived = false;
        PatientInsuranceDetails insurance = new PatientInsuranceDetails();

        if (patientProfile != null) {
            Object obj = patientProfileDAO.findRecordById(insurance, cardId);
            if (obj != null) {
                insurance = (PatientInsuranceDetails) obj;
                insurance.setIsArchived(1);
                patientProfileDAO.update(insurance);
                isArchived = true;
            } else {
                logger.info("In valid Card!");
                isArchived = false;
                throw new Exception("Card not existed!");
            }
        }
        return isArchived;
    }

    public List<PatientInsuranceDetailsDTO> getInsuranceCard(PatientProfile patientProfile, Integer dependentId) throws Exception {
        List<PatientInsuranceDetailsDTO> patientInsuranceDetailsDTOList = new ArrayList<>();
        List<BusinessObject> lstObj = new ArrayList<>();
        List<PatientInsuranceDetails> patientInsuranceDetailslist = new ArrayList<>();

        if (patientProfile != null) {
            if (dependentId != null) {
                patientInsuranceDetailslist = patientProfileDAO.populateInsCardListForDependent(dependentId);
            } else {
                patientInsuranceDetailslist = patientProfileDAO.populateInsCardList(patientProfile.getId());
            }
            if (CommonUtil.isNullOrEmpty(patientInsuranceDetailslist)) {
                logger.info("No Insurance Card Avalible");
                throw new Exception("No Insurance Card Avalible.Please add new.");
            }
        }

        patientInsuranceDetailsDTOList = populatePatientInsuranceDetails(patientInsuranceDetailslist);
        return patientInsuranceDetailsDTOList;
    }

    public List<PatientInsuranceDetailsDTO> populatePatientInsuranceDetails(List<PatientInsuranceDetails> patientInsuranceDetailslist) throws Exception {
        List<PatientInsuranceDetailsDTO> patientInsuranceDetailsDTOList = new ArrayList<>();
        for (PatientInsuranceDetails patientInsuranceDetails : patientInsuranceDetailslist) {
            PatientInsuranceDetailsDTO patientInsuranceDetailsDTO = new PatientInsuranceDetailsDTO();
            patientInsuranceDetailsDTO.setId(patientInsuranceDetails.getId());
            patientInsuranceDetailsDTO.setGroupNumber(AppUtil.getSafeStr(patientInsuranceDetails.getGroupNumber(), ""));
            patientInsuranceDetailsDTO.setPatientProfileId(AppUtil.getSafeInt(patientInsuranceDetails.getPatientProfile().getId() + "", 0));
            patientInsuranceDetailsDTO.setInsuranceProvider(AppUtil.getSafeStr(patientInsuranceDetails.getInsuranceProvider(), ""));
            patientInsuranceDetailsDTO.setMemberID(AppUtil.getSafeStr(patientInsuranceDetails.getMemberID(), ""));
            patientInsuranceDetailsDTO.setPlanId(AppUtil.getSafeStr(patientInsuranceDetails.getPlanId(), ""));
            patientInsuranceDetailsDTO.setProviderAddress(AppUtil.getSafeStr(patientInsuranceDetails.getProviderAddress(), ""));
            patientInsuranceDetailsDTO.setProviderPhone(AppUtil.getSafeStr(patientInsuranceDetails.getProviderPhone(), ""));
            patientInsuranceDetailsDTO.setCardHolderRelationship(AppUtil.getSafeStr(patientInsuranceDetails.getCardHolderRelationship(), ""));
            patientInsuranceDetailsDTO.setInsuranceFrontCardPath(AppUtil.getSafeStr(patientInsuranceDetails.getInsuranceFrontCardPath(), ""));
            patientInsuranceDetailsDTO.setInsuranceBackCardPath(AppUtil.getSafeStr(patientInsuranceDetails.getInsuranceBackCardPath(), ""));
            patientInsuranceDetailsDTO.setIsPramiry(AppUtil.getSafeInt(patientInsuranceDetails.getIsPrimary() + "", 0));
            patientInsuranceDetailsDTO.setIsArchived(AppUtil.getSafeInt(patientInsuranceDetails.getIsArchived() + "", 0));
            if (patientInsuranceDetails.getEffectiveDate() == null) {
                patientInsuranceDetailsDTO.setEffectiveDate("");
            } else {
                patientInsuranceDetailsDTO.setEffectiveDate(DateUtil.dateToString(patientInsuranceDetails.getEffectiveDate(), Constants.DATE_MONTH_YEAR_FORMATE));
            }
            patientInsuranceDetailsDTO.setPatientId(AppUtil.getSafeInt(patientInsuranceDetails.getPatientProfile().getId() + "", 0));
            patientInsuranceDetailsDTOList.add(patientInsuranceDetailsDTO);
        }
        return patientInsuranceDetailsDTOList;
    }

    public List<OrderDetailDTO> getYearEndStatment(PatientProfile patientProfile, Date startDate, Date endDate) throws Exception {
        List<Order> orderList = new ArrayList<>();
        if (patientProfile != null && patientProfile.getId() != null) {
            orderList = patientProfileDAO.getYearEndStatment(patientProfile.getId(), startDate, endDate);
        }
        return populateYearEndStatment(orderList);
    }

    public List<OrderDetailDTO> populateYearEndStatment(List<Order> orderList) throws Exception {
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            ///////////////////////////////////////////
            String drug = AppUtil.getSafeStr(order.getDrugName(), "");
            String[] arr = AppUtil.getBrandAndGenericFromDrugName(drug);
            if (arr != null) {
                if (arr.length == 2) {
                    drug = arr[0] + "(" + arr[1] + ")";
//                    genericName = arr[1];
                } else if (AppUtil.getSafeStr(drug, "").indexOf(Constants.BRAND_NAME_ONLY) >= 0) {
                    drug = arr[0] + "(" + Constants.BRAND_NAME_ONLY + ")";
                }
//                else if (AppUtil.getSafeStr(drug, "").length() > 0) {
//                    genericName = drug;
//                    drug = "User Input";
//                }
            }
            ///////////////////////////////////////////
            orderDetailDTO.setDrugName(drug);
            orderDetailDTO.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "-"));
            orderDetailDTO.setId(order.getId());
            orderDetailDTO.setOriginalPtCopayStr(AppUtil.roundOffNumberToCurrencyFormat(order.getOriginalPtCopay(), "en", "US"));
            orderDetailDTO.setRxReimbCost(order.getRxReimbCost() != null && order.getRxReimbCost() > 0d ? order.getRxReimbCost() : 0d);
            orderDetailDTO.setRedeemPoints(order.getProfitSharePoint() != null ? AppUtil.convertNumberToThousandSeparatedFormat(order.getProfitSharePoint()) : "0");
            orderDetailDTO.setRedeemPointsCost(order.getActualProfitShare() != null ? order.getActualProfitShare() : 0d);
            if (order.getMfrCost() != null) {
                orderDetailDTO.setMfrCost(order.getMfrCost());
            } else {
                orderDetailDTO.setMfrCost(0.00);
            }
            if (order.getPriceIncludingMargins() != null) {
                orderDetailDTO.setPriceIncludingMargins(order.getPriceIncludingMargins());
            } else {
                orderDetailDTO.setPriceIncludingMargins(0.00);
            }
            if (order.getCreatedOn() != null) {
                orderDetailDTO.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), Constants.USA_DATE_TIME_FORMATE));
            } else {
                orderDetailDTO.setOrderDate("01/01/1900");
            }
            if (order.getDrugPrice() != null) {
                orderDetailDTO.setDrugPrice(order.getDrugPrice());
            } else {
                orderDetailDTO.setDrugPrice(0.00);
            }
            if (order.getHandLingFee() != null) {
                orderDetailDTO.setHandLingFee(order.getHandLingFee());
            } else {
                orderDetailDTO.setHandLingFee(0.00);
            }
            if (order.getRedeemPointsCost() != null) {
                orderDetailDTO.setRedeemPointsCost(order.getRedeemPointsCost());
            } else {
                orderDetailDTO.setRedeemPointsCost(0.00);
            }
            if (order.getProfitMargin() != null) {
                orderDetailDTO.setProfitMargin(order.getProfitMargin());
            } else {
                orderDetailDTO.setProfitMargin(0.00);
            }
            if (order.getFinalPayment() != null) {
                orderDetailDTO.setFinalPayment(order.getFinalPayment());
            } else {
                orderDetailDTO.setFinalPayment(0.00);
            }
            orderDetailDTO.setPaymentIncludingShipping(
                    AppUtil.roundOffNumberToCurrencyFormat(orderDetailDTO.getFinalPayment()
                            + orderDetailDTO.getHandLingFee(), "en", "us"));
            orderDetailDTOList.add(orderDetailDTO);
        }
        return orderDetailDTOList;
    }

    public boolean updatePrimaryInsuranceCard(PatientProfile patientProfile, Integer cardId) throws Exception {
        boolean isUpdated = false;
        List<BusinessObject> lstObj = new ArrayList();
        List<PatientInsuranceDetails> patientInsuranceDetailsList = new ArrayList();

        if (patientProfile != null) {

            PatientInsuranceDetails makePrimary = (PatientInsuranceDetails) patientProfileDAO.findRecordById(new PatientInsuranceDetails(), cardId);
            if (makePrimary != null) {
                lstObj.add(new BusinessObject("PatientProfile.Id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR));
                lstObj.add(new BusinessObject("PatientInsuranceDetails.IS_PRIMARY", 1, Constants.HIBERNATE_EQ_OPERATOR));
                patientInsuranceDetailsList = patientProfileDAO.findByProperty(new PatientInsuranceDetails(), lstObj, "patientProfile.id", Constants.HIBERNATE_ASC_ORDER);
                if (CommonUtil.isNullOrEmpty(patientInsuranceDetailsList)) {
                    patientInsuranceDetailsList.get(0).setIsPrimary(0);
                    patientProfileDAO.update(patientInsuranceDetailsList.get(0));
                }
                makePrimary.setIsPrimary(1);
                patientProfileDAO.saveOrUpdate(makePrimary);
                isUpdated = true;
            } else {
                logger.info("In valid Card!");
                isUpdated = false;
                throw new Exception("Card is not existed!");
            }
        }
        return isUpdated;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////
     // Co-Pay Card Start                                                                                  //
     //////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public boolean addCopayCard(PatientProfile patientProfile, byte[] insuranceCardFront, byte[] insuranceCardBack,
            String imgFcPath, String imgBcPath, String effectiveDate, String expiryDate, String manufacturerName, Integer drugBasicId) throws Exception {
        boolean isAdded = false;
        CoPayCardDetails coPayCardDetails = new CoPayCardDetails();
        DrugBasic drugBasic = null;
        if (drugBasicId != null && drugBasicId > 0) {
            drugBasic = (DrugBasic) this.patientProfileDAO.findByPropertyUnique(new DrugBasic(), "drugBasicId", drugBasicId, Constants.HIBERNATE_EQ_OPERATOR, "", 0);
        }
        if (patientProfile != null) {
            coPayCardDetails.setPatientProfile(patientProfile);
            coPayCardDetails.setManufacturerName(manufacturerName);
//            coPayCardDetails.setOrder(order);
            if (effectiveDate != null) {
                coPayCardDetails.setEffectiveDate(DateUtil.stringToDate(effectiveDate, Constants.DATE_FORMATE_SHORT));
            }
            if (expiryDate != null) {
                coPayCardDetails.setExpiryDate(DateUtil.stringToDate(expiryDate, Constants.DATE_FORMATE_SHORT));
            }
            coPayCardDetails.setCopayFrontCardPath(imgFcPath);
            coPayCardDetails.setCopayBackCardPath(imgBcPath);
            if (drugBasic != null) {
                coPayCardDetails.setDrugbasic(drugBasic);
            }
            coPayCardDetails.setIsArchive("0");
            coPayCardDetails.setOrder(null);
            coPayCardDetails.setCreatedOn(new Date());
            coPayCardDetails.setUpdatedOn(new Date());
            patientProfileDAO.save(coPayCardDetails);
            isAdded = true;
        }
        return isAdded;
    }

    public List<CoPayCardDetailsDTO> getCoPayCards(PatientProfile patientProfile) throws Exception {
        List<BusinessObject> lstObj = new ArrayList();
        List<CoPayCardDetails> coPayCardDetailslist = new ArrayList();
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = new ArrayList();
        try {
            if (patientProfile != null) {
                lstObj.add(new BusinessObject("patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR));
                lstObj.add(new BusinessObject("isArchive", "0", Constants.HIBERNATE_EQ_OPERATOR));
                lstObj.add(new BusinessObject("orderId", null, Constants.HIBERNATE_EQ_OPERATOR));
                coPayCardDetailslist = patientProfileDAO.getCoPayCardWithNoOrder(patientProfile.getId(), "1", "0");
                if (CommonUtil.isNullOrEmpty(coPayCardDetailslist)) {
                    logger.info("No Co-Pay Card Avalible");
                    throw new Exception("No Co-Pay Card Avalible");
                }
            }
            coPayCardDetailsDTOList = populateCoPayCardDetails(coPayCardDetailslist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coPayCardDetailsDTOList;
    }

    public List<CoPayCardDetailsDTO> getCoPayCardsForAnOrder(Integer id, String orderId) throws Exception {

        List<CoPayCardDetails> coPayCardDetailslist = new ArrayList();
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = new ArrayList();
        try {
            PatientProfile patientProfile = (PatientProfile) this.patientProfileDAO.findRecordById(new PatientProfile(), id);
            if (patientProfile != null) {

                coPayCardDetailslist = patientProfileDAO.getCoPayCardForAnOrder(patientProfile.getId(), orderId);
                if (coPayCardDetailslist == null) {
                    coPayCardDetailslist = new ArrayList<>();
                }
//                if (CommonUtil.isNullOrEmpty(coPayCardDetailslist)) {
//                    logger.info("No Co-Pay Card Avalible");
//                    throw new Exception("No Co-Pay Card Avalible");
//                }
            }
            coPayCardDetailsDTOList = populateCoPayCardDetails(coPayCardDetailslist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coPayCardDetailsDTOList;
    }

    public List<QuestionAnswerDTO> getQuestionAnswerList(String orderId) {
        List<QuestionAnswerDTO> questionDTOLst = new ArrayList();
        try {
            List<QuestionAnswer> questionsList = this.patientProfileDAO.findByNestedProperty(new QuestionAnswer(), "order.id", orderId, Constants.HIBERNATE_EQ_OPERATOR, "id", Constants.HIBERNATE_DESC_ORDER);

            if (questionsList != null) {
                for (QuestionAnswer answer : questionsList) {
                    QuestionAnswerDTO dto = new QuestionAnswerDTO();
                    dto.setId(answer.getId());
                    dto.setQuestion(answer.getQuestion());
                    dto.setQuestionTimeStr(answer.getQuestionTime() != null
                            ? DateUtil.dateToString(answer.getQuestionTime(), "MM/dd/yyyy hh:mm a") : "");
                    questionDTOLst.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionDTOLst;
    }

    public List<CoPayCardDetailsDTO> populateCoPayCardDetails(List<CoPayCardDetails> coPayCardDetailslist) throws Exception {
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = new ArrayList<>();
        for (CoPayCardDetails coPayCardDetails : coPayCardDetailslist) {
            CoPayCardDetailsDTO coPayCardDetailsDTO = new CoPayCardDetailsDTO();
            coPayCardDetailsDTO.setId(coPayCardDetails.getId());
            coPayCardDetailsDTO.setMfrId(coPayCardDetails.getId());
            coPayCardDetailsDTO.setManufacturerName(coPayCardDetails.getManufacturerName());
            coPayCardDetailsDTO.setPatientId(coPayCardDetails.getPatientProfile().getId());
            coPayCardDetailsDTO.setOrderId(coPayCardDetails.getOrder() != null && coPayCardDetails.getOrder().getId() != null && !"0".equals(coPayCardDetails.getOrder().getId()) ? coPayCardDetails.getOrder().getId() : "");
            coPayCardDetailsDTO.setIsArchive(coPayCardDetails.getIsArchive());
            coPayCardDetailsDTO.setCopayFrontCardPath(AppUtil.getSafeStr(coPayCardDetails.getCopayFrontCardPath(), ""));
            coPayCardDetailsDTO.setCopayBackCardPath(AppUtil.getSafeStr(coPayCardDetails.getCopayBackCardPath(), ""));

            if (coPayCardDetails.getExpiryDate() == null) {
                coPayCardDetailsDTO.setExpiryDate("");
            } else {
                coPayCardDetailsDTO.setExpiryDate(DateUtil.dateToString(coPayCardDetails.getExpiryDate(), Constants.DATE_FORMATE_SHORT));
            }
            if (coPayCardDetails.getEffectiveDate() == null) {
                coPayCardDetailsDTO.setEffectiveDate("");
            } else {
                coPayCardDetailsDTO.setEffectiveDate(DateUtil.dateToString(coPayCardDetails.getEffectiveDate(), Constants.DATE_FORMATE_SHORT));
            }
            coPayCardDetailsDTO.setDrugBasicId(coPayCardDetails.getDrugbasic() != null && coPayCardDetails.getDrugbasic().getDrugBasicId() != null ? coPayCardDetails.getDrugbasic().getDrugBasicId() : 0);
            coPayCardDetailsDTO.setDrugBrand(coPayCardDetails.getDrugbasic() != null && coPayCardDetails.getDrugbasic().getDrugBasicId() != null ? AppUtil.getSafeStr(coPayCardDetails.getDrugbasic().getBrandName(), "") : "N/A");
            coPayCardDetailsDTOList.add(coPayCardDetailsDTO);
        }
        return coPayCardDetailsDTOList;
    }

    public boolean deleteCopayCard(PatientProfile patientProfile, Long cardId) throws Exception {
        boolean isArchive = false;
        CoPayCardDetails coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), cardId);

        if (coPayCardDetails != null) {
            coPayCardDetails.setIsArchive("1");
            coPayCardDetails.setUpdatedOn(new Date());
            patientProfileDAO.update(coPayCardDetails);
            isArchive = true;
        }
        return isArchive;
    }

    public DrugDTO getDrugInfo(Integer basicDrugId) {

        DrugBasic drugBasic = (DrugBasic) patientProfileDAO.findRecordById(new DrugBasic(), basicDrugId);
        DrugDTO drugDTO = new DrugDTO();
        if (drugBasic != null) {

            drugDTO.setDrugId(drugBasic.getDrugBasicId());
            drugDTO.setBrandName(drugBasic.getBrandName());
            drugDTO.setGenaricName(drugBasic.getDrugGeneric().getGenericName());
            drugDTO.setDrugNDC(Long.MAX_VALUE);
            drugDTO.setStrength("");
        }

        return drugDTO;

    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////
     // Co-Pay Card End                                                                                    //
     //////////////////////////////////////////////////////////////////////////////////////////////////////*/

 /*//////////////////////////////////////////////////////////////////////////////////////////////////////
     // Refill Reminder Start                                                                              //
     //////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public boolean optOutRefillReminder(PatientProfile patientProfile, Integer orderChainId) throws Exception {
        boolean isAdded = false;
        OrderChain orderChain = (OrderChain) patientProfileDAO.findRecordById(new OrderChain(), orderChainId);

        if (orderChain != null) {
            orderChain.setOptOutRefillReminder(1);
            patientProfileDAO.update(orderChain);
            isAdded = true;
        }
        return isAdded;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////
     // Refill Reminder End                                                                                //
     //////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public boolean updateDeliveryAddressWs(PatientProfile patientProfile, Integer id, String address, String appt, String city, String stateId, String zip, String description, String addressType, String defaultAddress, Integer dprefId) {
        boolean isUpdate = false;
        try {
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("patientProfile id is: " + patientProfile.getId());
                updatePreviousDefaultAddress(patientProfile.getId(), defaultAddress);
                logger.info("Update Ptient DeliveryAddress: " + id);
                PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getPatientDeliveryAddressById(patientProfile.getId(), id);
                setDeliveryAddress(patientDeliveryAddress, patientProfile.getId(), address, appt, description, city, Integer.parseInt(stateId), zip, addressType, defaultAddress, dprefId);
                patientDeliveryAddress.setUpdatedOn(new Date());
                patientProfileDAO.update(patientDeliveryAddress);
                isUpdate = true;
            }
        } catch (Exception e) {
            logger.error("Exception -> updateDeliveryAddressWs", e);
        }
        return isUpdate;
    }

    public PatientProfile getPatientProfileByPhoneNumber(String mobileNumber) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfileByPhoneNumber(EncryptionHandlerUtil.getEncryptedString(mobileNumber));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientProfileByPhoneNumber", e);
        }
        return patientProfile;
    }

    public PatientPaymentInfo updatePaymentWsDetails(String token, Integer paymentId,
            String cardHolderName, String cardNumber, String cardType, String expiry,
            String cvv, String defaultCard, PatientAddress billingAddress) throws Exception {
        PatientPaymentInfo paymentInfo = new PatientPaymentInfo();
        PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
        if (patientProfile != null && patientProfile.getId() != null) {
            List<PatientPaymentInfo> list = patientProfileDAO.getPatientPaymentInfoListByPatientId(patientProfile.getId());
            updatePreviousPaymentDefaultCard(list, defaultCard, paymentId);
            PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoById(paymentId, patientProfile.getId());
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                if (AppUtil.getSafeStr(patientPaymentInfo.getDefaultCard(), "").equalsIgnoreCase("Yes")
                        && defaultCard.equalsIgnoreCase("No")) {
                    throw new Exception("Default option for this card can't be turned off until another card is set to default.");
                }
                if (cardHolderName != null) {
                    patientPaymentInfo.setCardHolderName(cardHolderName);
                }
                if (cardNumber != null) {
                    patientPaymentInfo.setCardNumber(cardNumber);
                }
                if (cardType != null) {
                    patientPaymentInfo.setCardType(cardType);
                }
                if (expiry != null) {
                    patientPaymentInfo.setExpiryDate(expiry);
                }
                if (cvv != null) {
                    patientPaymentInfo.setCvvNumber(cvv);
                }
                if (defaultCard != null) {
                    patientPaymentInfo.setDefaultCard(defaultCard);
                }
                if (billingAddress != null) {
                    patientPaymentInfo.setBillingAddress(billingAddress);
                }
                patientProfileDAO.merge(patientPaymentInfo);
                paymentInfo = getPatientPaymentInfo(paymentId, patientProfile.getId());

            } else {
                logger.info("Payment info is null:");
            }
        } else {
            logger.info("In valid token: " + token);

        }
        return paymentInfo;
    }

    public PatientProfileMembers updateFamilyMembers(Integer id, String firstName,
            String lastName, String gender, String dob, String allergies,
            String dermatologist, String emailAddr) throws Exception {
        PatientProfileMembers patientProfileMembers = new PatientProfileMembers();

        patientProfileMembers = (PatientProfileMembers) patientProfileDAO.getObjectById(new PatientProfileMembers(), id);
        if (patientProfileMembers != null && patientProfileMembers.getId() != null) {
            if (patientProfileMembers.getIsAdult())//if adult dependent
            {
                Date birthDate = DateUtil.stringToDate(dob, "MM/dd/yyyy");
                int years = DateUtil.getDiffYears(birthDate, new Date());
                if (years < 18) {
                    throw new Exception(Constants.INVALID_AGE);
                }
            }
            patientProfileMembers.setFirstName(firstName);
            patientProfileMembers.setLastName(lastName);
            patientProfileMembers.setGender(gender);
            if (dob != null) {
                patientProfileMembers.setDob(DateUtil.stringToDate(dob, "MM/dd/yyyy"));
            }
            patientProfileMembers.setAllergies(allergies);
            patientProfileMembers.setDermatologist(dermatologist);
            patientProfileMembers.setEmail(AppUtil.getSafeStr(emailAddr, ""));
            patientProfileDAO.update(patientProfileMembers);
        }

        return patientProfileMembers;
    }

    public List<PatientProfileMembers> getProfileMemberses(Integer profileId) {
        List<PatientProfileMembers> list = new ArrayList<>();
        try {
            list = patientProfileDAO.getPatientProfileMembersListById(profileId);
        } catch (Exception e) {
            logger.error("Exception -> getProfileMemberses", e);
        }
        return list;
    }

    public List<PatientRewardLevel> getPatientRewardLevelList() {
        List<PatientRewardLevel> list = new ArrayList<>();
        try {
            list = patientProfileDAO.getAllRecords(new PatientRewardLevel());
        } catch (Exception e) {
            logger.error("Exception -> getPatientRewardLevelList", e);
        }
        return list;
    }

    public boolean saveContactUs(PatientProfile patientProfile, String complainType, String message) {
        boolean isSaved = false;
        try {
            ContactUs contactUs = new ContactUs();
            if (patientProfile != null && patientProfile.getId() != null) {
                contactUs = patientProfileDAO.getContactUsByProfileId(patientProfile.getId());
            }
            if (contactUs == null) {
                contactUs = new ContactUs();
                contactUs.setCreatedOn(new Date());
            }
            contactUs.setPatientProfile(patientProfile);
            if (patientProfile != null) {
                contactUs.setName(patientProfile.getFirstName() + " " + patientProfile.getLastName());
                contactUs.setEmail(patientProfile.getEmailAddress());
            }
            contactUs.setMessage(message);
            contactUs.setComplainType(complainType);
            contactUs.setUpdatedOn(new Date());
            patientProfileDAO.saveOrUpdate(contactUs);
            isSaved = true;
        } catch (Exception e) {
            logger.error("Exception -> saveContactUs", e);
        }
        return isSaved;
    }

    public ContactUs getContactUs(Integer id) {
        ContactUs contactUs = new ContactUs();
        try {
            contactUs = patientProfileDAO.getContactUsByProfileId(id);
        } catch (Exception e) {
            logger.error("Exception -> getContactUs", e);
        }
        return contactUs;
    }

    public Set<DrugBrandDTO> getDrugBrandsList(String name) {

        SortedSet<DrugBrandDTO> list = new TreeSet<>(
                Comparator.comparing(DrugBrandDTO::getDrugBrandName));
        try {

            List<DrugBasic> drugGenericList = patientProfileDAO.retrieveDrugWithGeneric(name);
            List<DrugBasic> drugBrandList = patientProfileDAO.retrieveDrugWithoutGeneric(name);

            if ((drugGenericList == null && drugBrandList == null) || (drugGenericList.size() == 0 && drugBrandList.size() == 0)) {
                DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                drugBrandDTO.setId(0);
                drugBrandDTO.setDrugBrandName(Constants.EMPTY_MESSAGE);
                list.add(drugBrandDTO);
                return list;
            }

            if (drugGenericList.size() > 0) {
                for (DrugBasic drugBrand : drugGenericList) {
                    DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                    drugBrandDTO.setId(drugBrand.getDrugBasicId());
//                    drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "") + "("
//                            + AppUtil.getSafeStr(drugBrand.getBrandName(), "") + ")");
                    if (AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").equalsIgnoreCase(Constants.BRAND_NAME_ONLY_WITH_STARIC)) {
                        // if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N")) 
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + " "
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), ""));
                    } else {
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + "("
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").toLowerCase() + ")");

                    }

                    list.add(drugBrandDTO);
                }
            }

            if (drugBrandList.size() > 0) {
                logger.info("Drug Brand list size: " + drugBrandList.size());
                for (DrugBasic drugBrand : drugBrandList) {
                    DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                    drugBrandDTO.setId(drugBrand.getDrugBasicId());
                    //drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), ""));
                    if (AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").equalsIgnoreCase(Constants.BRAND_NAME_ONLY_WITH_STARIC)) {
                        // if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N")) 
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + " "
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), ""));
                    } else {
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + "("
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").toLowerCase() + ")");

                    }

                    list.add(drugBrandDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugBrandsList", e);
        }
        return list;
    }

//    public Set<DrugBrandDTO> getDrugBrandsList(String name) {
//        Set<DrugBrandDTO> list = new HashSet<>();
//        try {
//            List<DrugBrand> drugBrandList = patientProfileDAO.getDrugBrandsList(name);
//            if (drugBrandList.size() > 0) {
//                logger.info("Drug Brand list size: " + drugBrandList.size());
//                for (DrugBrand drugBrand : drugBrandList) {
//                    DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
//                    drugBrandDTO.setId(drugBrand.getId());
//                    if (drugBrand.getDrugGenericTypes() != null && drugBrand.getDrugGenericTypes().getId() != null) {
//                        drugBrandDTO.setDrugBrandName(drugBrand.getDrugBrandName() + "(" + drugBrand.getDrugGenericTypes().getDrugGenericName() + ")");
//                    } else {
//                        logger.info("Drug Generic Name");
//                        drugBrandDTO.setDrugBrandName(drugBrand.getDrugBrandName());
//                    }
//                    list.add(drugBrandDTO);
//                }
//            } else {
//                DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
//                drugBrandDTO.setId(0);
//                drugBrandDTO.setDrugBrandName(Constants.EMPTY_MESSAGE);
//                list.add(drugBrandDTO);
//            }
//        } catch (Exception e) {
//            logger.error("Exception -> getDrugBrandsList", e);
//        }
//        return list;
//    }
    /////////////////////////////////////////////////////////////////
//    public Drug getDrugList(Integer drugBrandId) {
//        Drug drug = new Drug();
//        try {
//            List<StrengthJsonList> strengthList = new ArrayList<>();
//            Set<TabletDTO> tabletDTOs = new LinkedHashSet<>();
//            List<CapsuleDTO> capsuleList = new ArrayList<>();
//            Set<String> drugTypeDTOs = new LinkedHashSet<>();
//            List<BusinessObject> lstBusiness = new ArrayList();
//            
//            DrugBasic drugBasic = (DrugBasic) patientProfileDAO.findRecordById(new DrugBasic(), drugBrandId);
//            if (drugBasic == null) {
//                return null;
//            }
//            Set<DrugDetail> list = drugBasic.getDrugDetailSet();
////            BusinessObject bizz=new BusinessObject("drugBasic", drugBrandId, Constants.HIBERNATE_EQ_OPERATOR);
////            lstBusiness.add(bizz);
////            bizz=new BusinessObject("archived","N",Constants.HIBERNATE_EQ_OPERATOR);
////            lstBusiness.add(bizz);
////            List<Drug> list = patientProfileDAO.findByProperty(new DrugDetail(), lstBusiness, "", 0);
//            StrengthJsonList strengthJsonList = new StrengthJsonList();
//            if (list != null && list.size() > 0) {
//                for (DrugDetail d : list) {
//                    //String type=AppUtil.getSafeStr(d.getDrugForm().getFormDescr(), "");
//                    if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N")) {
//                        TabletDTO tabletDTO = new TabletDTO();
//                        CapsuleDTO capsuleDTO = new CapsuleDTO();
//                        //drug.setDrugNdc(d.getDrugNDC());
//                        drug.setDrugGpi("" + d.getDrugGPI());
//                        //drug.setRouteDescription(d.getRouteDescription());
//                        drug.setDrugGcn("" + d.getDrugGCN());
//                        //drug.setDrugNdc(d.getDrugDetailId());
//                        //drug.setdType(d.getFormPrefix());
//                        //drug.setDrugType(d.getDrugForm().getFormDescr());
//                        //drug.setDefQty(d.getDefQty());
//                        makeDrugTypeList(d, tabletDTO, tabletDTOs, strengthJsonList, capsuleDTO, capsuleList);
//                        drug.setdType(tabletDTO.getFormPrefix());
//                        drugTypeDTOs.add(AppUtil.getSafeStr(tabletDTO.getFormPrefix(), "").length() > 0
//                                ? AppUtil.getSafeStr(tabletDTO.getFormPrefix(), "")
//                                : AppUtil.getSafeStr(capsuleDTO.getFormPrefix(), ""));//.replaceAll("\\s", ""));
//                    }
//                }
//                strengthList.add(strengthJsonList);
//                drug.setStrengthList(strengthList);
//                drug.setdType(drugTypeDTOs);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Exception -> getDrugList", e);
//        }
//        return drug;
//    }
    ////////////////////////////////////////////////////////////////
    public Drug getDrugList(Integer drugBrandId) {
        Drug drug = new Drug();
        try {
            List<StrengthJsonList> strengthList = new ArrayList<>();
            Set<DrugDTO> tabletDTOs = new LinkedHashSet<>();
            Set<String> drugTypeDTOs = new LinkedHashSet<>();
            List<BusinessObject> lstBusiness = new ArrayList();

            DrugBasic drugBasic = (DrugBasic) patientProfileDAO.findRecordById(new DrugBasic(), drugBrandId);
            if (drugBasic == null) {
                return null;
            }
            Set<DrugDetail> list = drugBasic.getDrugDetailSet();

            StrengthJsonList strengthJsonList = new StrengthJsonList();
            Map map = new HashMap();
            if (list != null && list.size() > 0) {
                for (DrugDetail d : list) {
                    //String type=AppUtil.getSafeStr(d.getDrugForm().getFormDescr(), "");
                    if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N")) {
                        DrugDTO tabletDTO = new DrugDTO();
                        drug.setDrugGpi("" + d.getDrugGPI());
                        //drug.setRouteDescription(d.getRouteDescription());
                        drug.setDrugGcn("" + d.getDrugGCN());
                        //drug.setDrugNdc(d.getDrugDetailId());
                        //drug.setdType(d.getFormPrefix());
                        //drug.setDrugType(d.getDrugForm().getFormDescr());
                        //drug.setDefQty(d.getDefQty());
                        tabletDTO = makeDrugTypeList(d, tabletDTO, tabletDTOs, strengthJsonList);
                        drug.setdType(tabletDTO.getFormPrefix());
                        drugTypeDTOs.add(AppUtil.getSafeStr(tabletDTO.getFormPrefix(), ""));//.replaceAll("\\s", ""));
                        if (map.get(tabletDTO.getFormPrefix()) != null) {
                            List lst = (List) map.get(tabletDTO.getFormPrefix());
                            lst.add(tabletDTO);
                        } else {
                            List lst = new ArrayList();
                            lst.add(tabletDTO);
                            map.put(tabletDTO.getFormPrefix(), lst);
                        }
                        //map.put(tabletDTO.getFormPrefix(), tabletDTO);

                    }
                }
//                strengthList.add(strengthJsonList);
//                drug.setStrengthList(strengthList);
                drug.setDrugMap(map);
                drug.setdType(drugTypeDTOs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    ////////////////////////////////////////////////////////////////
//    public Drug getDrugList(Integer drugBrandId) {
//        Drug drug = new Drug();
//        try {
//            List<StrengthJsonList> strengthList = new ArrayList<>();
//            Set<TabletDTO> tabletDTOs = new LinkedHashSet<>();
//            List<CapsuleDTO> capsuleList = new ArrayList<>();
//            Set<String> drugTypeDTOs = new LinkedHashSet<>();
//            List<Drug> list = patientProfileDAO.getAllDrug(drugBrandId);
//            StrengthJsonList strengthJsonList = new StrengthJsonList();
//            if (list.size() > 0) {
//                for (Drug d : list) {
//
//                    TabletDTO tabletDTO = new TabletDTO();
//                    CapsuleDTO capsuleDTO = new CapsuleDTO();
//                    drug.setDrugId(d.getDrugId());
//                    drug.setDrugGpi(d.getDrugGpi());
//                    drug.setRouteDescription(d.getRouteDescription());
//                    drug.setDrugGcn(d.getDrugGcn());
//                    drug.setdType(d.getDrugType());
//                    makeDrugTypeList(d, tabletDTO, tabletDTOs, strengthJsonList, capsuleDTO, capsuleList);
//                    drugTypeDTOs.add(d.getDrugType().replaceAll("\\s", ""));
//                }
//                strengthList.add(strengthJsonList);
//                drug.setStrengthList(strengthList);
//                drug.setdType(drugTypeDTOs);
//            }
//        } catch (Exception e) {
//            logger.error("Exception -> getDrugList", e);
//        }
//        return drug;
//    }
//    private void makeDrugTypeList(Drug d, TabletDTO tabletDTO, Set<TabletDTO> tabletDTOs, StrengthJsonList strengthJsonList, CapsuleDTO capsuleDTO, List<CapsuleDTO> capsuleList) {
//        if (d.getDrugType().replaceAll("\\s", "").equalsIgnoreCase(Constants.TABLET)) {
//            tabletDTO.setDrugId(d.getDrugId());
//            tabletDTO.setStrength(d.getStrength() + "" + d.getDrugUnits().getName());
//            tabletDTOs.add(tabletDTO);
//            strengthJsonList.setTablet(tabletDTOs);
//        } else {
//            capsuleDTO.setDrugId(d.getDrugId());
//            capsuleDTO.setStrength(d.getStrength() + "" + d.getDrugUnits().getName());
//            capsuleList.add(capsuleDTO);
//            Set<CapsuleDTO> os = new LinkedHashSet<>();
//            for (CapsuleDTO cdto : capsuleList) {
//                os.add(cdto);
//            }
//            strengthJsonList.setCapsule(os);
//        }
//    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private DrugDTO makeDrugTypeList(DrugDetail d, DrugDTO tabletDTO, Set<DrugDTO> tabletDTOs,
            StrengthJsonList strengthJsonList) {
        String type = AppUtil.getSafeStr(d.getDrugForm().getFormDescr(), "");
        tabletDTO.setType(type);
        tabletDTO.setFormPrefix(type);
        tabletDTO.setDrugNDC(d.getDrugDetailId());//d.getDrugNDC());
        tabletDTO.setStrength(d.getStrength());
        tabletDTO.setDefQty(d.getDefQty());
        String qtyStr = AppUtil.getSafeStr(d.getPackageSizeValues(), "");//.split(",");
        String[] qtyStrArr = qtyStr.split(",");
        int[] qtyInt = new int[qtyStrArr != null ? qtyStrArr.length : 0];
        try {

            for (int i = 0; qtyStrArr != null && i < qtyStrArr.length; i++) {
                qtyStrArr[i] = AppUtil.getSafeStr(qtyStrArr[i], "").length() <= 4 ? AppUtil.getSafeStr(qtyStrArr[i], "")
                        : AppUtil.getSafeStr(qtyStrArr[i], "").substring(0, 3);
                qtyInt[i] = AppUtil.getSafeInt(qtyStrArr[i], 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            qtyStr = "30";
            qtyInt = Arrays.stream(qtyStr.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        }
//        int[] qtyInt = Arrays.stream(qtyStr.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        List<Integer> list = new ArrayList<Integer>();

        for (int index = 0; index < qtyInt.length; index++) {
            list.add(qtyInt[index]);
        }
        Collections.sort(list);//, Collections.reverseOrder());
//            Collections.sort(list, Collections.reverseOrder());
        String[] qtyValues = new String[list.size()];
        //Object[] aryArr = list.toArray();
        for (int i = 0; i < list.size(); i++) {
            int n = list.get(i);
            qtyValues[i] = "" + n;//.toString();
        }
        tabletDTO.setQtyValues(qtyValues);
        tabletDTOs.add(tabletDTO);
        strengthJsonList.setDrug(tabletDTOs);
        return tabletDTO;

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void makeDrugTypeList(DrugDetail d, TabletDTO tabletDTO, Set<TabletDTO> tabletDTOs,
            StrengthJsonList strengthJsonList, CapsuleDTO capsuleDTO, List<CapsuleDTO> capsuleList) {
        String type = AppUtil.getSafeStr(d.getDrugForm().getFormDescr(), "");
        if (d.getDrugForm().getFormDescr().toUpperCase().indexOf(Constants.TABLET) >= 0) //.replaceAll("\\s", "").equalsIgnoreCase(Constants.TABLET)) 
        {
            tabletDTO.setType(type);
            tabletDTO.setFormPrefix(Constants.TABLET);
            tabletDTO.setDrugNDC(d.getDrugDetailId());//d.getDrugNDC());
            tabletDTO.setStrength(d.getStrength());
            tabletDTO.setDefQty(d.getDefQty());
            String qtyStr = d.getPackageSizeValues();//.split(",");
            int[] qtyInt = Arrays.stream(qtyStr.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
            List<Integer> list = new ArrayList<Integer>();

            for (int index = 0; index < qtyInt.length; index++) {
                list.add(qtyInt[index]);
            }
            Collections.sort(list);//, Collections.reverseOrder());
//            Collections.sort(list, Collections.reverseOrder());
            String[] qtyValues = new String[list.size()];
            //Object[] aryArr = list.toArray();
            for (int i = 0; i < list.size(); i++) {
                int n = list.get(i);
                qtyValues[i] = "" + n;//.toString();
            }
//            String[] arr = d.getPackageSizeValues().split(",");
//            if (arr.length > 0) {
//                String[] qtyValues = new String[arr.length];
//                
//                for (int i = 0; i < qtyValues.length; i++) {
//                    qtyValues[i] = "120";
//                }
//                qtyValues[0] = "" + d.getDefQty();
//                for (int i = 1; i < arr.length; i++) {
//                    if (!arr[i].equals(qtyValues[0])) {
//                        qtyValues[i] = arr[i];
//                    }
//                }
//                Arrays.sort(qtyValues);
//                List<String> list = Arrays.asList(qtyValues);
//                Collections.reverse(list);
//                qtyValues = (String []) list.toArray();
            tabletDTO.setQtyValues(qtyValues);
//                
//            } 
//            else {
//                String[] qtyValues = new String[1];
//                for (int i = 0; i < qtyValues.length; i++) {
//                    qtyValues[i] = "120";
//                }
//                Arrays.sort(qtyValues);
//                List<String> list = Arrays.asList(qtyValues);
//                Collections.reverse(list);
//                qtyValues = (String []) list.toArray();
//                qtyValues[0] = "" + d.getDefQty();
//                tabletDTO.setQtyValues(qtyValues);
//            }
            //tabletDTO.setQtyValues(d.getPackageSizeValues().split(","));
            tabletDTOs.add(tabletDTO);
            strengthJsonList.setTablet(tabletDTOs);
        } else {
            capsuleDTO.setType(type);
            capsuleDTO.setFormPrefix(Constants.CAPSULE);
            capsuleDTO.setDrugNDC(d.getDrugDetailId());//.getDrugNDC());
            capsuleDTO.setStrength(d.getStrength());
            capsuleDTO.setDefQty(d.getDefQty());
            String qtyStr = d.getPackageSizeValues();//.split(",");
            int[] qtyInt = Arrays.stream(qtyStr.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
            List<Integer> list = new ArrayList<Integer>();

            for (int index = 0; index < qtyInt.length; index++) {
                list.add(qtyInt[index]);
            }
            Collections.sort(list, Collections.reverseOrder());
//            Collections.sort(list, Collections.reverseOrder());
            String[] qtyValues = new String[list.size()];
            //Object[] aryArr = list.toArray();
            for (int i = 0; i < list.size(); i++) {
                int n = list.get(i);
                qtyValues[i] = "" + n;//.toString();
            }

//            String[] arr = d.getPackageSizeValues().split(",");
//            if (arr.length > 0) {
//                String[] qtyValues = new String[arr.length];
//                for (int i = 0; i < qtyValues.length; i++) {
//                    qtyValues[i] = "120";
//                }
//                qtyValues[0] = "" + d.getDefQty();
//                for (int i = 1; i < arr.length; i++) {
//                    if (!arr[i].equals(qtyValues[0])) {
//                        qtyValues[i] = arr[i];
//                    }
//                }
//                capsuleDTO.setQtyValues(qtyValues);
//                
//            } else {
//                String[] qtyValues = new String[1];
//                for (int i = 0; i < qtyValues.length; i++) {
//                    qtyValues[i] = "120";
//                }
//                qtyValues[0] = "" + d.getDefQty();
            capsuleDTO.setQtyValues(qtyValues);
//            }
            //capsuleDTO.setQtyValues(d.getPackageSizeValues().split(","));
            capsuleList.add(capsuleDTO);
            Set<CapsuleDTO> os = new LinkedHashSet<>();
            for (CapsuleDTO cdto : capsuleList) {
                os.add(cdto);
            }
            strengthJsonList.setCapsule(os);
        }
    }

    ///////////////////////////////////////////////////////
    public DrugDetail getDrugDetailInfo(Long drugId, Integer qty, PatientProfile patientProfile) {
        DrugDetail drug = new DrugDetail();
        try {
            DrugDetail newDrug = (DrugDetail) patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (newDrug != null && newDrug.getDrugNDC() != null) {
                float drugProfitPercent = newDrug.getMarginPercent();
                float additionalFee = newDrug.getAdditionalFee();
                float basePrice = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(newDrug.getBasePrice()), 0f);
                newDrug.setBasePrice(basePrice);
                float drugCost = newDrug.getBasePrice() * qty;
                drug.setDrugCost(drugCost);
                float drugProfit = drugCost * (drugProfitPercent / 100);
                float totalFee = drugCost + drugProfit + additionalFee;
                drug.setTotalPrice(totalFee);
                //drug.setDrugId(drugI);
                //setDrugAdditionalMarginPricesField(newDrug, qty, drug);
                // setDeliveryPreferences(patientProfile, drug);
                getDrugsLookUpCalculation(patientProfile, drug);
                drug.setDrugNDC(newDrug.getDrugDetailId());//(newDrug.getDrugNDC());
                drug.setBrandName(AppUtil.getSafeStr(newDrug.getDrugBasic().getBrandName(), ""));
                drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                drug.setFormDesc(AppUtil.getSafeStr(newDrug.getDrugForm().getFormDescr(), "") + "(" + qty + ")");
                drug.setAdditionalMargin(additionalFee);
                drug.setProfitValue(drugProfit);
                drug.setImgUrl(AppUtil.getSafeStr(newDrug.getImgUrl(), ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    /////////////////////////////////////////////////////
    public DrugDetail populateOrderInfoInDrug(DrugDetail drug, String orderId) {
        if (AppUtil.getSafeStr(orderId, "").length() > 0) {
            Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
            if (order != null) {
                drug.setRxNo(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), ""));
                drug.setPymentType(AppUtil.getSafeStr(order.getFinalPaymentMode(), "")
                        .equalsIgnoreCase("SELF PAY") ? "SELF PAY" : "Insurance");
                if (order.getOrderChain() != null) {
                    drug.setLastRefillDate(order.getOrderChain().getLastRefillDate());
                    drug.setExpiryDate(order.getOrderChain().getRxExpiredDate());
                }
                drug.setRefillsRemaining(order.getRefillsRemaining() != null ? order.getRefillsRemaining() : 0);
                drug.setDaysSupply(order.getDaysSupply() != null ? order.getDaysSupply() : 0);
            }
        }
        return drug;

    }

    /////////////////////////////////////////////////////
    public DrugDetail getGenericBasedDrugDetailInfoHandler(Long drugId, Integer qty, PatientProfile patientProfile,
            String orderId) {
        DrugDetail detail = this.getGenericBasedDrugDetailInfo(drugId, qty, patientProfile);
        return this.populateOrderInfoInDrug(detail, orderId);
    }

    /////////////////////////////////////////////////////
    public DrugDetail getGenericBasedDrugDetailInfo(Long drugId, Integer qty, PatientProfile patientProfile) {
        DrugDetail drug = new DrugDetail();
        try {
            DrugDetail newDrug = (DrugDetail) patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (newDrug != null && newDrug.getDrugNDC() != null) {
                float drugProfitPercent = newDrug.getMarginPercent();
                float additionalFee = newDrug.getAdditionalFee();
                float basePrice = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(newDrug.getBasePrice()), 0f);
                logger.info("basePrice# " + basePrice + " Qty# " + qty);
                float drugCost = basePrice * qty; //newDrug.getBasePrice() * qty;
                drug.setRxAcqCost(drugCost);
                drug.setBasePrice(basePrice);
                float drugProfit = drugCost * (drugProfitPercent / 100);
                float mktSurcharge = newDrug.getMktSurcharge() != null ? newDrug.getMktSurcharge() : 0.0f;
                float totalFee = drugCost + drugProfit; //drugCost + drugProfit + additionalFee + newDrug.getMktSurcharge();
                drug.setTotalPrice(totalFee);
                float profitValue = totalFee - drugCost;
                float profitShare = profitValue * Constants.PROFIT_SHARE_PERCENT / 100;
                drug.setProfitValue(profitValue);
                drug.setProfitShare(profitShare);
                drug.setDrugCost(drugCost);
                //drug.setDrugId(drugI);
                //setDrugAdditionalMarginPricesField(newDrug, qty, drug);
                // setDeliveryPreferences(patientProfile, drug);
                getDrugsLookUpCalculation(patientProfile, drug);
                drug.setDrugNDC(newDrug.getDrugDetailId());//(newDrug.getDrugNDC());
                drug.setBrandName(AppUtil.getSafeStr(newDrug.getDrugBasic().getBrandName(), ""));
                //drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                //////////////////////////////////////////////
                if (AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), "").equalsIgnoreCase("* BRAND NAME ONLY *")) {
                    String tmp = "";
                    drug.setGenericName(tmp);
                } else {
                    drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                }
                //////////////////////////////////////////////
                drug.setFormDesc(AppUtil.getSafeStr(newDrug.getDrugForm().getFormDescr(), "") + "(" + qty + ")");
                drug.setAdditionalMargin(additionalFee);
                //drug.setProfitValue(drugProfit);
                drug.setImgUrl(AppUtil.getSafeStr(newDrug.getImgUrl(), ""));
                if (drug.getFinalPrice() != null && drug.getFinalPrice() < 0) {
                    drug.setFinalPrice(0.0f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    //////////////////////////////////////////////////////
    public DrugDetail getGenericBasedDrugDetailInfo(Long drugId, Integer qty, PatientProfile patientProfile, String orderId) {
        DrugDetail drug = new DrugDetail();
        try {
            DrugDetail newDrug = (DrugDetail) patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (newDrug != null && newDrug.getDrugNDC() != null) {
                float drugProfitPercent = newDrug.getMarginPercent();
                float additionalFee = newDrug.getAdditionalFee();
                float basePrice = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(newDrug.getBasePrice()), 0f);
                float drugCost = basePrice * qty; //newDrug.getBasePrice() * qty;
                drug.setDrugCost(drugCost);
                drug.setBasePrice(basePrice);
                float drugProfit = drugCost * (drugProfitPercent / 100);
                float mktSurcharge = newDrug.getMktSurcharge() != null ? newDrug.getMktSurcharge() : 0.0f;
                float totalFee = drugCost + drugProfit + (additionalFee * qty) + newDrug.getMktSurcharge();
                drug.setTotalPrice(totalFee);
                float profitValue = totalFee - drugCost;
                profitValue = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(profitValue), 0f);
                float profitShare = profitValue * Constants.PROFIT_SHARE_PERCENT / 100;
                profitShare = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(profitShare), 0f);
                drug.setProfitValue(profitValue);
                drug.setProfitShare(profitShare);
                //drug.setDrugId(drugI);
                //setDrugAdditionalMarginPricesField(newDrug, qty, drug);
                // setDeliveryPreferences(patientProfile, drug);
                int profitSharePoint = calculatePointsFromProfitShare(drug);
                drug.setPointsFromShare(profitSharePoint);
                float costFromPoints = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(calculateCostFromPoints(profitSharePoint)), 0f);
                drug.setCostFromPoints(costFromPoints);

                //////////////////////////////////SAVING Profit Share Points in Reward History/////////////////////////////////////////////
//                if (profitSharePoint > 0) {
//                    Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
//                    RewardHistory history = new RewardHistory();
//                    history.setDescription(Constants.PROFIT_SHARE_DESCRIPTION);
//                    history.setOrder(order);
//                    history.setPatientId(patientProfile.getId());
//                    history.setCreatedDate(new Date());
//                    this.patientProfileDAO.save(history);
//                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                getDrugsLookUpCalculation(patientProfile, drug);
                drug.setDrugNDC(newDrug.getDrugDetailId());//(newDrug.getDrugNDC());
                drug.setBrandName(AppUtil.getSafeStr(newDrug.getDrugBasic().getBrandName(), ""));
                //drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                //////////////////////////////////////////////
                if (AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), "").equalsIgnoreCase("* BRAND NAME ONLY *")) {
                    String tmp = "";
                    drug.setGenericName(tmp);
                } else {
                    drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                }
                //////////////////////////////////////////////
                drug.setFormDesc(AppUtil.getSafeStr(newDrug.getDrugForm().getFormDescr(), "") + "(" + qty + ")");
                drug.setAdditionalMargin(additionalFee);
                //drug.setProfitValue(drugProfit);
                drug.setImgUrl(AppUtil.getSafeStr(newDrug.getImgUrl(), ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    /////////////////////////////////////////////////////
    public DrugDetail getGenericBasedDrugDetailInfo(float profitValue) {
        DrugDetail drug = new DrugDetail();
        try {

            profitValue = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(profitValue), 0f);
            float profitShare = profitValue * Constants.PROFIT_SHARE_PERCENT / 100;
            profitShare = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(profitShare), 0f);
            drug.setProfitValue(profitValue);
            drug.setProfitShare(profitShare);
            //drug.setDrugId(drugI);
            //setDrugAdditionalMarginPricesField(newDrug, qty, drug);
            // setDeliveryPreferences(patientProfile, drug);
            int profitSharePoint = calculatePointsFromProfitShare(drug);
            drug.setPointsFromShare(profitSharePoint);
            float costFromPoints = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(calculateCostFromPoints(profitSharePoint)), 0f);
            drug.setCostFromPoints(costFromPoints);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    //////////////////////////////////////////////////////
    public DrugDetail getDrugDetailInfoTemp(Long drugId, Integer qty, PatientProfile patientProfile) {
        DrugDetail drug = new DrugDetail();
        try {
            DrugDetail newDrug = (DrugDetail) patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (newDrug != null && newDrug.getDrugNDC() != null) {
                float drugProfitPercent = newDrug.getMarginPercent();
                float additionalFee = newDrug.getAdditionalFee();
                float basePrice = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(newDrug.getBasePrice()), 0f);
                newDrug.setBasePrice(basePrice);
                float drugCost = newDrug.getBasePrice() * qty;
                drug.setDrugCost(drugCost);
                float drugProfit = drugCost * (drugProfitPercent / 100);
                float totalFee = drugCost + drugProfit + additionalFee;
                drug.setTotalPrice(totalFee);
                //drug.setDrugId(drugI);
                //setDrugAdditionalMarginPricesField(newDrug, qty, drug);
                // setDeliveryPreferences(patientProfile, drug);
                getDrugsLookUpCalculation(patientProfile, drug);
                drug.setDrugDetailId(newDrug.getDrugDetailId());
                drug.setDrugNDC(newDrug.getDrugNDC());//(newDrug.getDrugNDC());
                drug.setBrandName(AppUtil.getSafeStr(newDrug.getDrugBasic().getBrandName(), ""));
                drug.setGenericName(AppUtil.getSafeStr(newDrug.getDrugBasic().getDrugGeneric().getGenericName(), ""));
                drug.setFormDesc(AppUtil.getSafeStr(newDrug.getDrugForm().getFormDescr(), "") + "(" + qty + ")");
                drug.setAdditionalMargin(additionalFee);
                drug.setProfitValue(drugProfit);
                drug.setRequiresHandDelivery(newDrug.getRequiresHandDelivery());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    //////////////////////////////////////////////////////
    public Drug getDrugDetails(Integer drugId, Integer qty, PatientProfile patientProfile) {
        Drug drug = new Drug();
        try {
            Drug newDrug = patientProfileDAO.getDrugById(drugId);
            if (newDrug != null && newDrug.getDrugId() != null) {
                drug.setDrugId(drugId);
                setDrugAdditionalMarginPricesField(newDrug, qty, drug);
                // setDeliveryPreferences(patientProfile, drug);
                getDrugsLookUpCalculation(patientProfile, drug);
                drug.setDrugName(newDrug.getDrugBrand().getDrugBrandName());
                drug.setGenericName(newDrug.getDrugBrand().getDrugGenericTypes().getDrugGenericName());
            }
        } catch (Exception e) {
            logger.error("Exception -> getDrugList", e);
        }
        return drug;
    }

    /////////////////////////////////////////////////////////////////////////
    public int calculatePointsFromProfitShare(DrugDetail drug) {
        float profitShare = drug.getProfitShare();
        float pointsCostwithoutPrcessingCharges = 0;
        int pointsFromShare = 0;
        FeeSettings settings = (FeeSettings) this.patientProfileDAO.findRecordById(new FeeSettings(), 4);
        if (settings != null) {
            pointsCostwithoutPrcessingCharges = settings.getFee().floatValue();
            if (pointsCostwithoutPrcessingCharges > 0) {
                Float f = (profitShare / pointsCostwithoutPrcessingCharges);
                pointsFromShare = f.intValue();
            }
        }
        return pointsFromShare;
    }

    /////////////////////////////////////////////////////////////////////////
    public float calculateCostFromPoints(int points) {

        float cost = 0f;
        FeeSettings settings = (FeeSettings) this.patientProfileDAO.findRecordById(new FeeSettings(), 2);
        if (settings != null) {
            float pointsCostwithoutPrcessingCharges = settings.getFee().floatValue();
            if (pointsCostwithoutPrcessingCharges > 0) {
                cost = points * pointsCostwithoutPrcessingCharges;
            }
        }
        return cost;
    }

    /////////////////////////////////////////////////////////////////////////
    private void getDrugsLookUpCalculation(PatientProfile patientProfile, DrugDetail drug) throws Exception {
        try {

            //calculatePointsFromProfitShare(drug);
            RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
            OrderDetailDTO detailDTO = getRedeemPointsWs(rewardPoints.getAvailablePoints().toString());
            //Double dp = drug.getTotalPrice() * 0.9;
            Double dp = drug.getDrugCost().doubleValue(); //Todo drug.getTotalPrice().doubleValue();
            logger.info("Drug Price:: " + dp);
            if (detailDTO.getRedeemPointsCost() <= dp) {
                logger.info("Drug total Price:: " + drug.getTotalPrice() + " Total RedeemPointsCost:: " + detailDTO.getRedeemPointsCost());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, rewardPoints.getAvailablePoints(), dp);
            } else {
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                Double p = dp / feeSettings.getFee().doubleValue();
                Long redemainPoints = Math.round(p);
                logger.info("Round value:: " + redemainPoints);
                detailDTO = getRedeemPointsWs(redemainPoints.toString());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, redemainPoints, dp);
            }
        } catch (Exception e) {
            logger.error("Exception:: getDrugsLookUpCalculation", e);
        }

    }

    private Long getDrugsLookUpCalculation(DrugDetail drug,
            Long lifeTimePoints, Long availablePoints) throws Exception {
        try {

            //calculatePointsFromProfitShare(drug);
//            RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
            OrderDetailDTO detailDTO = getRedeemPointsWs(availablePoints.toString());
            //Double dp = drug.getTotalPrice() * 0.9;
            Double dp = drug.getDrugCost().doubleValue(); //Todo drug.getTotalPrice().doubleValue();
            //logger.info("90% of Drug Price:: " + dp);
            if (detailDTO.getRedeemPointsCost() <= dp) {
                logger.info("Drug total Price:: " + drug.getTotalPrice() + " Total RedeemPointsCost:: " + detailDTO.getRedeemPointsCost());
                getDrugLookUpCalculation(drug, detailDTO, lifeTimePoints, availablePoints, dp);
                return 0L;
            } else {
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                Double p = dp / feeSettings.getFee().doubleValue();
                Long redemainPoints = Math.round(p);
                logger.info("Round value:: " + redemainPoints);
                detailDTO = getRedeemPointsWs(redemainPoints.toString());
                getDrugLookUpCalculation(drug, detailDTO, lifeTimePoints, redemainPoints, dp);
                return Math.abs(availablePoints - redemainPoints);
            }
        } catch (Exception e) {
            logger.error("Exception:: getDrugsLookUpCalculation", e);
            return 0L;
        }
//        return availablePoints;

    }

    /////////////////////////////////////////////////////////////////////////
    private void getDrugLookUpCalculation(DrugDetail drug, OrderDetailDTO detailDTO, Long lifeTimePoints,
            Long redemainPoints, Double dp) {
        try {
            Double finalPrice = dp - detailDTO.getRedeemPointsCost();
            logger.info("Final Price:: " + finalPrice);
            if (drug.getDrugCost() != null) {
                Double finalDrugCost = drug.getDrugCost() - detailDTO.getRedeemPointsCost();
                drug.setFinalDrugCost(AppUtil.getSafeDouble(AppUtil.roundOffNumberToTwoDecimalPlaces(finalDrugCost), 0d));
            }

            //Double finalTotalPrice = (drug.getTotalPrice() * 0.1) + finalPrice;
            //logger.info("After Calculate :: " + finalTotalPrice);
            drug.setTotalPrice(AppUtil.getSafeFloat(CommonUtil.getDecimalFormat(drug.getTotalPrice().doubleValue()), 0f));
            drug.setFinalPrice(AppUtil.getSafeFloat(CommonUtil.getDecimalFormat(finalPrice.doubleValue()), 0f));
            drug.setRedeemedPoints(redemainPoints);
            drug.setRedeemedPointsPrice(detailDTO.getRedeemPointsCost().floatValue());
            drug.setLifeTimePoints(lifeTimePoints);
            //saveRewardHistory("Drug Look Up", availablePoints.intValue(), patientProfile.getId(), Constants.MINUS);
        } catch (Exception e) {
            logger.error("Exception:: getDrugLookUpCalculation", e);
        }

    }

    /////////////////////////////////////////////////////////////////////////
    private void getDrugsLookUpCalculation(PatientProfile patientProfile, Drug drug) throws Exception {
        try {
            RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
            OrderDetailDTO detailDTO = getRedeemPointsWs(rewardPoints.getAvailablePoints().toString());
            //Double dp = drug.getTotalPrice() * 0.9;
            Double dp = drug.getDrugCostWithoutMargin(); //Todo drug.getTotalPrice();
            logger.info("90% of Drug Price:: " + dp);
            if (detailDTO.getRedeemPointsCost() <= dp) {
                logger.info("Drug total Price:: " + drug.getTotalPrice() + " Total RedeemPointsCost:: " + detailDTO.getRedeemPointsCost());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, rewardPoints.getAvailablePoints(), dp);
            } else {
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                Double p = dp / feeSettings.getFee().doubleValue();
                Long redemainPoints = Math.round(p);
                logger.info("Round value:: " + redemainPoints);
                detailDTO = getRedeemPointsWs(redemainPoints.toString());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, redemainPoints, dp);
            }
        } catch (Exception e) {
            logger.error("Exception:: getDrugsLookUpCalculation", e);
        }

    }

    private void getDrugLookUpCalculation(Drug drug, OrderDetailDTO detailDTO, RewardPoints rewardPoints, Long redemainPoints, Double dp) {
        try {
            Double finalPrice = dp - detailDTO.getRedeemPointsCost();
            logger.info("Final Price:: " + finalPrice);
            drug.setFinalDrugCost(AppUtil.getSafeDouble(AppUtil.roundOffNumberToTwoDecimalPlaces(finalPrice), 0d));
            //Double finalTotalPrice = (drug.getTotalPrice() * 0.1) + finalPrice;
            //logger.info("After Calculate :: " + finalTotalPrice);
            drug.setTotalPrice(Double.parseDouble(CommonUtil.getDecimalFormat(drug.getTotalPrice())));
            drug.setFinalPrice(Double.parseDouble(CommonUtil.getDecimalFormat(finalPrice)));
            drug.setRedeemedPoints(redemainPoints);
            drug.setRedeemedPointsPrice(detailDTO.getRedeemPointsCost().toString());
            drug.setLifeTimePoints(rewardPoints.getLifeTimePoints());
            //saveRewardHistory("Drug Look Up", availablePoints.intValue(), patientProfile.getId(), Constants.MINUS);
        } catch (Exception e) {
            logger.error("Exception:: getDrugLookUpCalculation", e);
        }

    }

    /////////////////////////////////////////////////////////////////////////
    private void getDrugLookUpCalculation(DrugDetail drug, OrderDetailDTO detailDTO, RewardPoints rewardPoints,
            Long redemainPoints, Double dp) {
        try {
            Double finalPrice = dp - detailDTO.getRedeemPointsCost();
            logger.info("Final Price:: " + finalPrice);
            Double finalDrugCost = drug.getDrugCost() - detailDTO.getRedeemPointsCost();
            logger.info("Final Drug Cost# " + finalDrugCost);
            drug.setFinalDrugCost(AppUtil.getSafeDouble(AppUtil.roundOffNumberToTwoDecimalPlaces(finalDrugCost), 0d));
            //Double finalTotalPrice = (drug.getTotalPrice() * 0.1) + finalPrice;
            //logger.info("After Calculate :: " + finalTotalPrice);
            drug.setTotalPrice(AppUtil.getSafeFloat(CommonUtil.getDecimalFormat(drug.getTotalPrice().doubleValue()), 0f));
            drug.setFinalPrice(AppUtil.getSafeFloat(CommonUtil.getDecimalFormat(finalPrice), 0f));
            drug.setRedeemedPoints(redemainPoints);
            drug.setRedeemedPointsPrice(detailDTO.getRedeemPointsCost().floatValue());
            drug.setLifeTimePoints(rewardPoints.getLifeTimePoints());
            //saveRewardHistory("Drug Look Up", availablePoints.intValue(), patientProfile.getId(), Constants.MINUS);
        } catch (Exception e) {
            logger.error("Exception:: getDrugLookUpCalculation", e);
        }

    }

    /////////////////////////////////////////////////////////////////////////
    private void setDrugAdditionalMarginPricesField(Drug newDrug, Integer qty, Drug drug) throws Exception {
        List<DrugAdditionalMarginPrices> list = patientProfileDAO.getDrugAdditionalMarginPrices(newDrug.getDrugBrand().getDrugGenericTypes().getDrugTherapyClass().getDrugCategory().getId());
        if (list.size() > 0) {
            for (DrugAdditionalMarginPrices damp : list) {
                if (damp != null && damp.getId() != null) {
                    if (qty >= damp.getDrugQtyFrom() && qty <= damp.getDrugQtyTo()) {
                        logger.info("Drug Mac Price: " + newDrug.getDrugMacPrice() + " Drug Margin Value: " + damp.getDrugMarginValue());
                        Double cost = newDrug.getDrugMacPrice() * qty;
                        logger.info("Drug Mac Price * qty: " + cost);
                        BigDecimal totalCost = damp.getDrugMarginValue().add(BigDecimal.valueOf(cost));
                        logger.info("Total Cost is: " + totalCost);
                        DecimalFormat df = new DecimalFormat("#.00");
                        String drugCost = df.format(totalCost);
                        logger.info("Drug Cost: " + drugCost);
                        drug.setDrugCostWithoutMargin(cost);
                        drug.setCost(totalCost.doubleValue());
                        drug.setTotalPrice(totalCost.doubleValue());
                        drug.setAdditionalMargin(damp.getDrugMarginValue());

                        break;
                    } else {
                        logger.info("Qty not exist: DrugQtyFrom:: " + damp.getDrugQtyFrom() + " DrugQtyTo is: " + damp.getDrugQtyTo());
                    }
                } else {
                    logger.info("DrugAdditionalMarginPrices is null");
                }
            }
        } else {
            logger.info("DrugAdditionalMarginPrices list is empty:");
        }
    }

    private void setDeliveryPreferences(PatientProfile patientProfile, Drug drug) throws Exception {
        PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(patientProfile.getId());
        if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getZip() != null && patientDeliveryAddress.getDeliveryPreferences() != null && patientDeliveryAddress.getDeliveryPreferences().getId() != null) {
            DeliveryPreferences deliveryPreferences = patientDeliveryAddress.getDeliveryPreferences();
            if (deliveryPreferences != null && deliveryPreferences.getId() != null) {
                if (patientDeliveryAddress.getId() != null && patientDeliveryAddress.getZip() != null && patientDeliveryAddress.getDeliveryPreferences() != null && patientDeliveryAddress.getDeliveryPreferences().getId() != null) {
                    logger.info("Patient zip code is: " + patientDeliveryAddress.getZip() + " deliveryPreferences Id is: " + deliveryPreferences.getId());
                    ZipCodeCalculation zipCodeCalculations = patientProfileDAO.getZipCodeCalculationByProfileId(patientProfile.getId(), patientDeliveryAddress.getDeliveryPreferences().getId(), patientDeliveryAddress.getZip());
                    if (zipCodeCalculations == null) {
                        getDistanceFeeDTO(patientDeliveryAddress.getZip(), patientProfile.getId());
                    }
                    zipCodeCalculations = patientProfileDAO.getZipCodeCalculationByProfileId(patientProfile.getId(), patientDeliveryAddress.getDeliveryPreferences().getId(), patientDeliveryAddress.getZip());
                    if (zipCodeCalculations != null && zipCodeCalculations.getId() != null) {
                        logger.info("Miles is: " + zipCodeCalculations.getMiles());
                        PharmacyZipCodes pharmacyZipCodes = this.getPharmacyZipCodes();
                        for (DeliveryDistanceFee deliveryDistanceFee : pharmacyZipCodes.getDeliveryDistanceFeesList()) {
                            if (deliveryDistanceFee != null && deliveryDistanceFee.getId() != null) {
                                if (zipCodeCalculations.getMiles() >= deliveryDistanceFee.getDeliveryDistances().getDistanceFrom() && zipCodeCalculations.getMiles() <= deliveryDistanceFee.getDeliveryDistances().getDistanceTo()) {
                                    logger.info("Distance Delivery Preferences id: " + deliveryDistanceFee.getDeliveryPreferenceses().getId() + " deliveryPreferences Id: " + deliveryPreferences.getId());
                                    if (deliveryDistanceFee.getDeliveryPreferenceses().getId().equals(patientDeliveryAddress.getDeliveryPreferences().getId())) {
                                        DeliveryPreferencesDTO deliveryPreferencesDTO = new DeliveryPreferencesDTO();
                                        deliveryPreferencesDTO.setId(deliveryPreferences.getId());
                                        deliveryPreferencesDTO.setName(deliveryPreferences.getName());
                                        if (deliveryDistanceFee.getDeliveryFee() != null) {
                                            logger.info("Delivery fee is: " + deliveryDistanceFee.getDeliveryFee());
                                            deliveryPreferencesDTO.setCost(deliveryDistanceFee.getDeliveryFee());
                                            if (drug.getCost() != null) {
                                                BigDecimal totalPrice = deliveryDistanceFee.getDeliveryFee().add(new BigDecimal(drug.getCost()));
                                                logger.info("finalPrice is: " + totalPrice);
                                                drug.setTotalPrice(totalPrice.doubleValue());
                                            }
                                        }
                                        drug.setShippingFee(deliveryPreferencesDTO);
                                    } else {
                                        logger.info("DeliveryPreferenceses id doest not match");
                                    }
                                }
                            }
                        }
                    } else {
                        logger.info("Zip Code Calculations is null");
                    }
                } else {
                    drug.setShippingFee(new String[0]);
                    logger.info("Select No Defualt Address against this profile id: " + patientProfile.getId());
                }
            } else {
                drug.setShippingFee(new String[0]);
                logger.info("deliveryPreferences null against profile id");
            }
        } else {
            drug.setShippingFee(new String[0]);
            logger.info("Cannot associate shipping fee with profile");
        }
    }

    public String loadLastMessageFromPharmacyQueue(String orderId) {
        String msg = "";
        try {

            OrdersPtMessage ptMsg = (OrdersPtMessage) this.patientProfileDAO.findByPropertyUnique(new OrdersPtMessage(), "order.id", orderId, Constants.HIBERNATE_EQ_OPERATOR, "id", Constants.HIBERNATE_DESC_ORDER);
            if (ptMsg != null) {
                msg = ptMsg.getMessage();
            }
            return msg;
        } catch (Exception e) {
            return msg;
        }
    }

    public String loadDrugGeneric(String drugBrandName) {
        String msg = "";
        try {

            DrugBasic drug = (DrugBasic) this.patientProfileDAO.findByPropertyUnique(new DrugBasic(), "brandName", drugBrandName, Constants.HIBERNATE_EQ_OPERATOR, "brandName", Constants.HIBERNATE_DESC_ORDER);
            if (drug != null) {
                msg = drug.getDrugGeneric().getGenericName();
            }
            return msg;
        } catch (Exception e) {
            return msg;
        }
    }

    public CampaignMessages getNotificationMsgs(String message, String eventName) {
        CampaignMessages campaignMessages = new CampaignMessages();
        try {
            //proccessing Y,YES,YEP ect.
            ValidResponse validResponse = textFlowDAO.getValidResponse(message);
            if (validResponse != null && validResponse.getVresponseId() != null) {
                Event event = textFlowDAO.getEventByStaticValue(eventName.trim());
                if (event == null) {
                    logger.info("No such event defined (System will return)");
                    return null;
                }
                EventHasFolderHasCampaigns eventHasFolderHasCampaigns = textFlowDAO.getEventHasFolderHasCampaign(Integer.parseInt(Constants.campaignId), event.getEventId(), Constants.SMS);
                if (eventHasFolderHasCampaigns == null) {
                    logger.info("No folder associated to this campaign (System will return)");
                    return null;
                }
                int folderId = eventHasFolderHasCampaigns.getFolderId();
                logger.info("Folder Id : " + folderId);
                List<CampaignMessages> campaignMessagesList = textFlowDAO.getCampaignMessagesByCommunicationType(Integer.parseInt(Constants.campaignId), folderId);
                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    logger.info("No messages found for (System will return)");
                    return null;
                }

                CampaignMessagesResponse campaignMessagesResponse = textFlowDAO.getCampaignMessagesResponseByResComm(Integer.parseInt(Constants.campaignId), folderId, validResponse.getResponse().getResponseTitle());
                if (campaignMessagesResponse == null || campaignMessagesResponse.getCampaignMessagesResponseId() == null) {
                    logger.info("No Campaign Messages Response.");
                    return null;
                }
                campaignMessages = campaignMessagesResponse.getCampaignMessages();
            }
        } catch (Exception e) {
            logger.error("Exception -> getNotificationMsgs", e);
        }
        return campaignMessages;
    }

    ////////////////////////////////////////////////////////////////////////////
    public void reminderPOAExpiry(org.apache.log4j.Logger successed, org.apache.log4j.Logger failed, CampaignMessages campaignMessages) {
        try {
            Date date = new Date();
            Date aboutToExpiry = DateUtil.addDaysToDate(date, Constants.POA_EXPIRY_DAYS);
            List<PatientProfileMembers> dependentList = this.orderDAO.getpopulatePOAExpiresRecod(aboutToExpiry);
            int countOrders = dependentList.size();
            if (CommonUtil.isNullOrEmpty(dependentList)) {
                failed.info("There are no dependent.");
                return;
            }

            for (PatientProfileMembers patientProfileMembers : dependentList) {
                String dateExpiry = patientProfileMembers.getExpiryDate();
                int result = DateUtil.formatDate(aboutToExpiry, Constants.USA_DATE_FORMATE).compareTo(DateUtil.stringToDate(dateExpiry, Constants.USA_DATE_FORMATE));
                if (result >= 0) {
                    String message = campaignMessages.getSmstext();
                    message = (message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                            .replace("[dependent_name]", patientProfileMembers.getFirstName() + " " + patientProfileMembers.getLastName())
                            .replace("[poa_expiration_date]", patientProfileMembers.getPoaExpirationDate() != null
                                    ? DateUtil.dateToString(patientProfileMembers.getPoaExpirationDate(), Constants.USA_DATE_FORMATE) : "-")
                            .replace("[POA_ID]", AppUtil.getSafeStr(patientProfileMembers.getId().toString(), ""))
                            .replace("[request_no]", AppUtil.getSafeStr(patientProfileMembers.getId().toString(), "")));
                    saveNotificationMessages(campaignMessages, Constants.NO, patientProfileMembers.getPatientId(), message, campaignMessages.getSubject());
                    successed.info("Reminder Message : Your Dependent will be exppired on  : " + dateExpiry);
                    successed.info("Record has been added successfully.");
                }
            }
            successed.info("These " + (countOrders) + "Orders are going to be expired ");
        } catch (Exception e) {
            e.printStackTrace();
            failed.error("Exception# reminderPOAExpiry# " + e);
        }

    }
    ///////////////////////////////////////////////////////////////////////////

    public boolean saveNotificationMessages(CampaignMessages campaignMessages, String isTestMsg, Integer profileId) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(this.getMessageSubjectWithprocessedPlaceHolders(
                        campaignMessages.getSubject(), "0"));
                notificationMessages.setMessageText(campaignMessages.getSmstext());
                notificationMessages.setPushSubject((this.getMessageSubjectWithprocessedPlaceHolders(
                        AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), "0")));
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setIsTestMsg(isTestMsg);
            notificationMessages.setPushSubject(AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""));
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null && notificationMessages != null) {
                if (AppUtil.getSafeStr(profile.getOsType(), "20").equals("20")) {

                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                }
            }
        } catch (Exception e) {
            isSaved = false;
            e.printStackTrace();
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public boolean saveNotificationMessages(CampaignMessages campaignMessages, String isTestMsg, Integer profileId, String message, String subject) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(this.getMessageSubjectWithprocessedPlaceHolders(
                        subject, "0"));
                notificationMessages.setMessageText(message);
                notificationMessages.setPushSubject((this.getMessageSubjectWithprocessedPlaceHolders(
                        AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), "0")));
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setIsTestMsg(isTestMsg);
            notificationMessages.setPushSubject(AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""));
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null && notificationMessages != null) {
                if (AppUtil.getSafeStr(profile.getOsType(), "20").equals("20")) {

                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                }
            }
        } catch (Exception e) {
            isSaved = false;
            e.printStackTrace();
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    public boolean saveNotificationMessages(CampaignMessages campaignMessages, String isTestMsg,
            Integer profileId, String orderId) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(getMessageSubjectWithprocessedPlaceHolders(
                        AppUtil.getSafeStr(campaignMessages.getSubject(), ""), orderId));// notificationMessages.setSubject(campaignMessages.getSubject());
                notificationMessages.setMessageText(campaignMessages.getSmstext());
                notificationMessages.setPushSubject(campaignMessages.getPushNotification());
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setIsTestMsg(isTestMsg);
            notificationMessages.setOrderPrefix("RXD");
            Order order = this.findOrderById(orderId);
            if (order != null) {
                order.setLastReminderDate(new Date());
                notificationMessages.setOrders(order);
                if (order.getPatientProfileMembers() != null && order.getPatientProfileMembers().getId() != null) {
                    order.setPatientProfileMembers(order.getPatientProfileMembers());
                }
                patientProfileDAO.saveOrUpdate(order);
            }
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null) {
                System.out.println("Profile for sending push " + profile.getFirstName());
                String osType = AppUtil.getSafeStr(profile.getOsType(), "20");
                if (osType.equals("20"))//android
                {
                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", notificationMessages.getPushSubject(), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", notificationMessages.getPushSubject(), profile);
                }
            }

        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean saveNotificationMessages(CampaignMessages campaignMessages, String isTestMsg,
            Integer profileId, String orderId, String message, String subject) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(getMessageSubjectWithprocessedPlaceHolders(
                        AppUtil.getSafeStr(subject, ""), orderId));// notificationMessages.setSubject(campaignMessages.getSubject());
                notificationMessages.setMessageText(message);
                notificationMessages.setPushSubject(campaignMessages.getPushNotification());
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setIsTestMsg(isTestMsg);
            notificationMessages.setOrderPrefix("RXD");
            Order order = this.findOrderById(orderId);
            if (order != null) {
                order.setLastReminderDate(new Date());
                notificationMessages.setOrders(order);
                if (order.getPatientProfileMembers() != null && order.getPatientProfileMembers().getId() != null) {
                    order.setPatientProfileMembers(order.getPatientProfileMembers());
                }
                patientProfileDAO.saveOrUpdate(order);
            }
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null) {
                System.out.println("Profile for sending push " + profile.getFirstName());
                String osType = AppUtil.getSafeStr(profile.getOsType(), "20");
                if (osType.equals("20"))//android
                {
                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", notificationMessages.getPushSubject(), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", notificationMessages.getPushSubject(), profile);
                }
            }

        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean saveNotificationMessages(CampaignMessages campaignMessages, Integer profileId, String orderId) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(getMessageSubjectWithprocessedPlaceHolders(
                        AppUtil.getSafeStr(campaignMessages.getSubject(), ""), orderId));
                notificationMessages.setMessageText(campaignMessages.getSmstext());
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            //////////////////////////////////
            Order order = (Order) orderDAO.findRecordById(new Order(), orderId);
//            order.setId(orderId);
            notificationMessages.setOrders(order);
            /////////////////////////////////
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setIsEventFire(false);
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null) {
                String osType = AppUtil.getSafeStr(profile.getOsType(), "20");
                if (osType.equals("20"))//android
                {
                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), orderId, "" + notificationMessages.getId(),
                            "RXD", AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    public List<NotificationMessages> getNotificationMessagesByProfileId(Integer profileId) {
        List<NotificationMessages> list = new ArrayList<>();
        try {
            for (NotificationMessages messages : patientProfileDAO.getNotificationMessagesByProfileId(profileId)) {
                NotificationMessages notificationMessages = getNotificationMessages(messages);
                list.add(notificationMessages);
            }

            //////Pharmacy Notification Messages///////////////////
            for (OrdersPtMessage messages : patientProfileDAO.getPharmacyNotificationMessagesByProfileId(profileId)) {
                NotificationMessages notificationMessages = getPharmacyNotification(messages);
                list.add(notificationMessages);
            }
            Collections.sort(list, CommonUtil.byDate);
        } catch (Exception e) {
            logger.error("Exception -> getNotificationMessagesByProfileId", e);
            e.printStackTrace();
        }
        return list;
    }

    private NotificationMessages getNotificationMessages(NotificationMessages messages) {
        NotificationMessages notificationMessages = new NotificationMessages();
        notificationMessages.setId(messages.getId());
        if (messages.getPatientProfileMembers() != null && messages.getPatientProfileMembers().getId() != null) {
            notificationMessages.setDependentId(messages.getPatientProfileMembers().getId());
        }
        if (messages.getOrders() != null && messages.getOrders().getId() != null) {
            notificationMessages.setOrderId(messages.getOrders().getId());
            notificationMessages.setRxNo(AppUtil.getSafeStr(messages.getOrders().getSystemGeneratedRxNumber(), ""));
            if (messages.getOrders().getOrderStatus() != null && !messages.getOrders().getOrderStatus().getName().isEmpty()) {
                notificationMessages.setOrderStatus(messages.getOrders().getOrderStatus().getName());
            }
            if (CommonUtil.isNullOrEmpty(notificationMessages.getDependentId())) {
                if (messages.getOrders().getPatientProfileMembers() != null && messages.getOrders().getPatientProfileMembers().getId() != null) {
                    notificationMessages.setDependentId(messages.getOrders().getPatientProfileMembers().getId());
                }
            }
        }
        if (messages.getMessageType() != null && messages.getMessageType().getId() != null) {
            notificationMessages.setMessageTypeId(messages.getMessageType().getId().getMessageTypeId());
        }
        if (messages.getPatientProfile() != null && messages.getPatientProfile().getId() != null) {
            notificationMessages.setProfileId(messages.getPatientProfile().getId());
            notificationMessages.setMobileNumber(EncryptionHandlerUtil.getDecryptedString(messages.getPatientProfile().getMobileNumber()));
        }

        notificationMessages.setSubject(EncryptionHandlerUtil.getDecryptedString(messages.getSubject()));
        notificationMessages.setMessageText(CommonUtil.replaceNotificationMessagesPlaceHolder(EncryptionHandlerUtil.getDecryptedString(messages.getMessageText()), messages.getCreatedOn()));
        notificationMessages.setCreatedOn(messages.getCreatedOn());
        notificationMessages.setTimeAgo(DateUtil.getDateDiffInSecondsFromCurrentDate(messages.getCreatedOn()));
        notificationMessages.setIsRead(messages.getIsRead());
        notificationMessages.setIsCritical(messages.getIsCritical());
        notificationMessages.setMessageCategory(Constants.ORDER_NOTIFICATION);
        return notificationMessages;
    }

    private NotificationMessages getPharmacyNotification(OrdersPtMessage messages) {
        NotificationMessages notificationMessages = new NotificationMessages();
        notificationMessages.setId(messages.getId());
        notificationMessages.setMessageTypeId(0);
        notificationMessages.setProfileId(messages.getOrder().getPatientProfile().getId());
        notificationMessages.setSubject(messages.getSubject());
        if (messages.getOrder() != null && messages.getOrder().getId() != null) {
            notificationMessages.setOrderId(messages.getOrder().getId());
            notificationMessages.setRxNo(AppUtil.getSafeStr(messages.getOrder().getSystemGeneratedRxNumber(), ""));
            if (messages.getOrder().getOrderStatus() != null && !messages.getOrder().getOrderStatus().getName().isEmpty()) {
                notificationMessages.setOrderStatus(messages.getOrder().getOrderStatus().getName());
            }
            if (messages.getOrder().getPatientProfileMembers() != null && messages.getOrder().getPatientProfileMembers().getId() != null) {
                notificationMessages.setDependentId(messages.getOrder().getPatientProfileMembers().getId());
            }
        }
        notificationMessages.setMessageText(CommonUtil.replaceNotificationMessagesPlaceHolder(messages.getMessage(), messages.getCreatedOn()));
        notificationMessages.setAttachmentName(messages.getAttachmentName());
        notificationMessages.setAttachmentPath(messages.getAttachmentPath());
        if (CommonUtil.isNotEmpty(messages.getAttachmentPath())) {
            String attachment = notificationMessages.getMessageText() + "<br/> <a href='" + messages.getAttachmentPath() + "' style='font-weight:bold;' target=\"_blank\">View Attachment</a>";
            notificationMessages.setMessageText(attachment);
        }
        notificationMessages.setContentType(messages.getContentType());
        notificationMessages.setCreatedOn(messages.getCreatedOn());
        notificationMessages.setTimeAgo(DateUtil.getDateDiffInSecondsFromCurrentDate(messages.getCreatedOn()));
        notificationMessages.setIsRead(messages.getIsRead());
        notificationMessages.setIsCritical(messages.getIsCritical());
        notificationMessages.setMessageCategory(messages.getSubject());
        return notificationMessages;
    }

    /////////////////////////////////////////////////////////////////////
    public List<OrdersPtMessage> getOrderPtMessagesByOrderId(Integer orderId) {
        List<OrdersPtMessage> list = new ArrayList<>();
        try {

            //////Pharmacy Notification Messages///////////////////
            for (OrdersPtMessage messages : patientProfileDAO.getPharmacyNotificationMessagesByOrderId(orderId)) {
                OrdersPtMessage notificationMessages = new OrdersPtMessage();
                notificationMessages.setId(messages.getId());
                notificationMessages.setSubject(messages.getSubject());
                notificationMessages.setMessage(messages.getMessage());
                notificationMessages.setAttachmentName(messages.getAttachmentName());
                notificationMessages.setAttachmentPath(messages.getAttachmentPath());
                notificationMessages.setContentType(messages.getContentType());
                notificationMessages.setCreatedOn(messages.getCreatedOn());
                list.add(notificationMessages);
            }

        } catch (Exception e) {
            logger.error("Exception -> getNotificationMessagesByProfileId", e);
            e.printStackTrace();
        }
        return list;
    }

    ////////////////////////////////////////////////////////////////////
    public TransferRequest saveRxTransferRequest(Integer profileId, String patientName, String pharmacyName, String pharmacyPhone, String drug, Integer quantity, String videoPath, String rxNumber, String transferId, String drugImg) {
        TransferRequest transferRequest = new TransferRequest();
        try {
            if (CommonUtil.isNotEmpty(transferId)) {
                transferRequest = (TransferRequest) patientProfileDAO.getObjectById(new TransferRequest(), Integer.parseInt(transferId));
            }
            transferRequest.setPatientId(profileId);
            transferRequest.setPatientName(patientName);
            transferRequest.setPharmacyName(pharmacyName);
            transferRequest.setPharmacyPhone(pharmacyPhone);
            transferRequest.setDrug(drug);
            transferRequest.setQuantity(quantity);
            transferRequest.setVideo(videoPath);
            transferRequest.setDrugImg(drugImg);
            transferRequest.setRxNumber(rxNumber);
            transferRequest.setRequestedOn(new Date());
            patientProfileDAO.saveOrUpdate(transferRequest);
        } catch (Exception e) {
            logger.error("Exception -> saveRxTransferRequest", e);
        }
        return transferRequest;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    public TransferRequest saveRxTransferRequest(Integer profileId, String patientName, String pharmacyName, String pharmacyPhone,
            String drug, Integer quantity, String videoPath, String rxNumber, String transferId, String drugImg,
            String prescriberName, String prescriberPhone, String orderType) {
        TransferRequest transferRequest = new TransferRequest();
        try {
            if (CommonUtil.isNotEmpty(transferId)) {
                transferRequest = (TransferRequest) patientProfileDAO.getObjectById(new TransferRequest(), Integer.parseInt(transferId));
            }
            transferRequest.setPatientId(profileId);
            transferRequest.setPatientName(patientName);
            transferRequest.setPharmacyName(pharmacyName);
            transferRequest.setPharmacyPhone(pharmacyPhone);
            transferRequest.setDrug(drug);
            transferRequest.setQuantity(quantity);
            transferRequest.setVideo(videoPath);
            transferRequest.setDrugImg(drugImg);
            transferRequest.setRxNumber(rxNumber);
            transferRequest.setRequestedOn(new Date());
            transferRequest.setPrescriberName(prescriberName);
            transferRequest.setPrescribeerPhone(prescriberPhone);
            transferRequest.setIsOrder(orderType);
            patientProfileDAO.saveOrUpdate(transferRequest);
        } catch (Exception e) {
            logger.error("Exception:: saveRxTransferRequest", e);
        }
        return transferRequest;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public OrderChain saveRxOrderChainForTransfer(Integer profileId, String patientName, String pharmacyName, String pharmacyPhone,
            String drug, Integer quantity, String videoPath, String rxNumber, String transferId, String drugImg,
            String prescriberName, String prescriberPhone, String orderType) {
        OrderChain orderChain = new OrderChain();
        try {

            PatientProfile patientProfile = new PatientProfile(profileId);
            orderChain.setPatientProfile(patientProfile);
            orderChain.setCreatedOn(new Date());
            patientProfileDAO.saveOrUpdate(orderChain);
            Order order = new Order();
            order.setOrderChain(orderChain);
            order.setVideo(videoPath);
            order.setImagePath(drugImg);
            order.setPatientProfile(patientProfile);
            order.setViewStatus("Pending");
            order.setCreatedOn(new Date());
            order.setOrderType("Transfer");
            if (CommonUtil.isNotEmpty(patientName)) {
                String[] patientArr = patientName.split(" ");
                if (patientArr != null && patientArr.length >= 2) {
                    order.setFirstName(patientArr[0]);
                    order.setLastName(patientArr[1]);
                }
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            patientProfileDAO.saveOrUpdate(order);
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrderChainForTransfer", e);
        }
        return orderChain;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public OrderChain saveRxOrderChainForTransferReplaceImages(String orderId, String videoPath, String drugImg, List<OrderTransferImages> orderTransferImageses) {
        OrderChain orderChain = new OrderChain();
        try {

//            PatientProfile patientProfile = new PatientProfile(profileId);
            Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
            if (order != null) {
                orderChain = order.getOrderChain();
                order.setVideo(videoPath);
                order.setImagePath(drugImg);

                patientProfileDAO.saveOrUpdate(order);
                logger.info("Save Order Transfer Imageses::");
                if (!CommonUtil.isNullOrEmpty(orderTransferImageses)) {
                    for (OrderTransferImages orderTransferImages : orderTransferImageses) {
                        orderTransferImages.setOrder(order);
                        patientProfileDAO.save(orderTransferImages);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: saveRxOrderChainForTransfer", e);
        }
        return orderChain;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public OrderChain saveRxOrderChainForTransfer(Integer profileId, String patientName, String firstName, String lastName, String pharmacyName, String pharmacyPhone,
            String drug, String strength, Integer quantity, String videoPath, String rxNumber, String transferId, String drugImg,
            String prescriberName, String prescriberPhone, String orderType, String dependentId, List<OrderTransferImages> orderTransferImageses) {
        OrderChain orderChain = new OrderChain();
        try {

            PatientProfile patientProfile = new PatientProfile(profileId);
            orderChain.setPatientProfile(patientProfile);
            orderChain.setCreatedOn(new Date());
            patientProfileDAO.saveOrUpdate(orderChain);
            Order order = new Order();
            order.setOrderChain(orderChain);
            order.setVideo(videoPath);
            order.setImagePath(drugImg);
            order.setPatientProfile(patientProfile);
            order.setViewStatus("Pending");
            order.setCreatedOn(new Date());
            order.setOrderType("Transfer");
            order.setFirstName(firstName);
            order.setLastName(lastName);
            order.setPharmacyName(pharmacyName);
            order.setPharmacyPhone(pharmacyPhone);
            order.setPrescriberLastName(prescriberName);
            order.setPrescriberPhone(prescriberPhone);
            order.setOldRxNumber(rxNumber);
            order.setIsBottleAvailable(AppUtil.getSafeStr(pharmacyName, "").length() > 0
                    || AppUtil.getSafeStr(pharmacyPhone, "").length() > 0
                    || AppUtil.getSafeStr(rxNumber, "").length() > 0);
            order.setDrugName(drug);
            order.setStrength(strength);
            if (quantity != null) {
                order.setQty(quantity.toString());
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            PatientProfileMembers members = (PatientProfileMembers) this.patientProfileDAO.findRecordById(new PatientProfileMembers(), AppUtil.getSafeInt(dependentId, 0));
            if (members != null) {
                order.setPatientProfileMembers(members);
            }
            if (members != null && members.getIsAdult() != null && members.getIsAdult() == true && members.getPoaApprovalDate() == null && members.getDisApproveDate() == null) {
                order.setCareGiverRequest(1);
            } else {
                order.setCareGiverRequest(0);
            }

            patientProfileDAO.saveOrUpdate(order);
            logger.info("Save Order Transfer Imageses::");
            if (!CommonUtil.isNullOrEmpty(orderTransferImageses)) {
                for (OrderTransferImages orderTransferImages : orderTransferImageses) {
                    orderTransferImages.setOrder(order);
                    orderTransferImages.setCreatedOn(new Date());
                    orderTransferImages.setUpdatedOn(new Date());
                    patientProfileDAO.save(orderTransferImages);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: saveRxOrderChainForTransfer", e);
        }
        return orderChain;
    }
    ///////////////////////////////////////////////////////////////////////////////////

    public List<NotificationMessages> getNotificationMessagesListById(Integer id) {
        List<NotificationMessages> list = new ArrayList<>();
        try {
            for (NotificationMessages messages : patientProfileDAO.getNotificationMessagesListById(id)) {
                NotificationMessages notificationMessages = getNotificationMessages(messages);
                list.add(notificationMessages);
            }
        } catch (Exception e) {
            logger.error("Exception -> getNotificationMessagesListById", e);
        }
        return list;
    }

    public NotificationMessages updateNotificationMessages(Integer id) {
        NotificationMessages notificationMessages = new NotificationMessages();
        try {
            notificationMessages = (NotificationMessages) patientProfileDAO.getObjectById(new NotificationMessages(), id);
            if (notificationMessages != null && notificationMessages.getId() != null) {
                notificationMessages.setIsRead(Boolean.TRUE);
                patientProfileDAO.update(notificationMessages);
                notificationMessages = getNotificationMessages(notificationMessages);
            }
        } catch (Exception e) {
            logger.error("Exception -> updateNotificationMessages", e);
        }
        return notificationMessages;
    }

    public NotificationMessages getIsReadNotificationMessagesCount(Integer profileId) {
        NotificationMessages notificationMessages = new NotificationMessages();
        try {
            Long readMessages = patientProfileDAO.getTotalReadNotificationMessages(profileId);
            if (readMessages != null) {
                notificationMessages.setReadMesages(readMessages);
            }
            Long unReadMessages = patientProfileDAO.getTotalUnReadNotificationMessages(profileId);
            if (unReadMessages != null) {
                notificationMessages.setUnReadMessages(unReadMessages);
            }
        } catch (Exception e) {
            logger.error("Exception -> getIsReadNotificationMessagesCount", e);
        }
        return notificationMessages;
    }

    public RewardPoints getRxTransferPoints() {
        RewardPoints newRewardPoints = new RewardPoints();
        try {
            newRewardPoints = (RewardPoints) patientProfileDAO.getRecordByType(new RewardPoints(), Constants.TRANSFERRX);
            if (newRewardPoints != null && newRewardPoints.getId() != null) {
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                if (feeSettings != null && feeSettings.getId() != null) {
                    BigDecimal totalCost = newRewardPoints.getPoint().multiply(feeSettings.getFee());
                    logger.info("Total cost of rewardpoint * fee value: " + totalCost);
                    Integer point = newRewardPoints.getPoint().intValueExact();
                    logger.info("Points are: " + point);
                    newRewardPoints.setCost(totalCost);
                    newRewardPoints.setPoints(point);
                }
            }
        } catch (Exception e) {
            logger.error("Exception -> getRxTransferPoints", e);
        }
        return newRewardPoints;
    }

    public RewardPoints getMyRewardsPoints(Integer profileId) {
        RewardPoints rewardPoints = new RewardPoints();
        try {
            Long lifeTimePoints = patientProfileDAO.getTotalRewardHistoryPointByType("PLUS", profileId);
            logger.info("life Time Points: " + lifeTimePoints);
            if (lifeTimePoints != null) {
                rewardPoints.setLifeTimePoints(lifeTimePoints);
            } else {
                rewardPoints.setLifeTimePoints(0L);
            }
            Long totalMinusPoints = patientProfileDAO.getTotalRewardHistoryPointByType("MINUS", profileId);
            rewardPoints.setAvailedRewardPoints(totalMinusPoints);
            logger.info("total Minus Points: " + totalMinusPoints);
            if (totalMinusPoints != null && lifeTimePoints != null) {
                Long availablePoints = lifeTimePoints - totalMinusPoints;
                logger.info("availablePoints is: " + availablePoints);
                if (availablePoints > 0) {
                    rewardPoints.setAvailablePoints(availablePoints);
                } else {
                    rewardPoints.setAvailablePoints(0L);
                }
            } else {
                rewardPoints.setAvailablePoints(rewardPoints.getLifeTimePoints());
            }
            List<RewardPoints> rewardPointsList = new ArrayList<>();
            List<RewardPoints> dbRewardPointses = patientProfileDAO.getAllRecords(new RewardPoints());
            for (RewardPoints rp : dbRewardPointses) {
                Integer points = rp.getPoint().intValueExact();
                logger.info("Points are: " + points);
                rp.setPoints(points);
                rewardPointsList.add(rp);
            }
            rewardPoints.setBonusPoints(rewardPointsList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getMyRewardsPoints", e);
        }
        return rewardPoints;
    }

    public void saveImageOrVideo(byte[] decodedFrontCard, File file, String imageFormat, String command) throws IOException {
        try {
            logger.info("Image format is: " + imageFormat);
            BufferedImage imag = ImageIO.read(new ByteArrayInputStream(decodedFrontCard));
            ImageIO.write(imag, imageFormat, file);
            String output = CommonUtil.executeCommand(command);
            logger.info("Command Result:: " + output);
        } catch (Exception e) {
            logger.error("Exception -> saveImageOrVideo", e);
        }
    }

    /**
     * @param offset
     * @return
     * @Author Haider Ali
     */
    public List getDrugCategory(Integer offset) {
        logger.info("Getting Drug Category: ");
        try {
            List lst_drugCategory = patientProfileDAO.getDrugCategoryListAll(offset, Constants.PAGING_CONSTANT.RECORDS_PER_PAGE);
            logger.info("Total Drug Category size : " + lst_drugCategory.size());
            return lst_drugCategory;
        } catch (Exception ex) {
            logger.error("Exception -> getDrugCategory", ex);
        }
        return null;

    }

    public Long getTotalDrugCategory() {

        logger.debug("Getting Drug Category: ");
        try {

            Long l_totalRecords = patientProfileDAO.getTotalRecords(DrugCategory.class);
            logger.debug("Total Drug Category size : " + l_totalRecords);
            return l_totalRecords;
        } catch (Exception ex) {
            logger.error("Exception -> getDrugCategory", ex);
        }
        return null;

    }

    public List getDrugsByParameter(String a_searchParameter) {
        logger.info("Getting Drug Category: ");
        try {
            List lst_drugCategory = patientProfileDAO.getDrugSearch(DrugCategory.class, a_searchParameter);
            logger.info("Total Drug Category size : " + lst_drugCategory.size());
            return lst_drugCategory;
        } catch (Exception ex) {
            logger.error("Exception -> getDrugCategory", ex);
        }
        return null;

    }

    public CampaignMessages getNextMessages(CampaignMessages campaignMessages) {
        CampaignMessages messages = new CampaignMessages();
        try {
            logger.info("Next Message Type id: " + campaignMessages.getMessageType().getId().getMessageTypeId());
            if (campaignMessages.getMessageType().getId().getMessageTypeId() != 0) {
                CampaignMessagesResponse campaignMessagesResponse = patientProfileDAO.getCampaignMessagesResponseByCampaignMessageId(campaignMessages.getMessageType().getId().getMessageTypeId());
                if (campaignMessagesResponse != null && campaignMessagesResponse.getCampaignMessagesResponseId() != null) {
                    if (campaignMessagesResponse.getNextMessage() != null) {
                        messages = textFlowDAO.getCampaignMessagesByMessageTypeId(campaignMessagesResponse.getNextMessage(), campaignMessagesResponse.getFolderId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception -> sendNextMessages", e);
        }
        return messages;
    }

    public void sendNextMessages(CampaignMessages campaignMessages, Integer profileId) {
        try {
            CampaignMessages nextCampaignMessages = this.getNextMessages(campaignMessages);
            if (nextCampaignMessages != null && nextCampaignMessages.getMessageId() != null) {
                logger.info("Send Next Notification messages.");
                logger.info("Next Msg subject are: " + nextCampaignMessages.getSubject() + " Next Message Text are:" + nextCampaignMessages.getSmstext());
                System.out.println("Next Msg subject are: " + nextCampaignMessages.getSubject() + " Next Message Text are:" + nextCampaignMessages.getSmstext());
                nextCampaignMessages.setSmstext(AppUtil.getSafeStr(nextCampaignMessages.getSmstext(), "").
                        replace("[date_time]", DateUtil.dateToString(new Date(), "MM/dd/YYYY hh:mm a")));
                if (this.saveNotificationMessages(nextCampaignMessages, Constants.NO, profileId)) {
                    logger.info("next Notification messages sent and saved.");
                }
            } else {
                logger.info("There is no Next Notification messages.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrderDetailDTO getPlaceRxOrderDetailsWs(PatientProfile patientProfile, long drugId, Integer qty, String drugType, String strength) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        try {
            DrugDetail drugDetail = this.getDrugDetailInfo(drugId, qty, patientProfile);
            orderDetailDTO.setDrugDetail(drugDetail);
            RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
            if (rewardPoints != null && rewardPoints.getAvailablePoints() != null) {
                orderDetailDTO.setAvailablePoints(rewardPoints.getAvailablePoints());
            }
            orderDetailDTO.setPatientName(patientProfile.getFirstName() + " " + patientProfile.getLastName());
            PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                State state = (State) patientProfileDAO.getObjectById(new State(), patientDeliveryAddress.getState().getId());
                if (patientDeliveryAddress.getApartment() != null && !"".equals(patientDeliveryAddress.getApartment())) {
                    orderDetailDTO.setShippingAddress(patientDeliveryAddress.getAddress() + " " + patientDeliveryAddress.getApartment() + " " + patientDeliveryAddress.getCity() + ", " + state.getName() + " " + patientDeliveryAddress.getZip());
                } else {
                    orderDetailDTO.setShippingAddress(patientDeliveryAddress.getAddress() + " " + patientDeliveryAddress.getCity() + ", " + state.getName() + " " + patientDeliveryAddress.getZip());
                }
            } else {
                orderDetailDTO.setShippingAddress("");
            }
            PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                orderDetailDTO.setCardHolderName(patientPaymentInfo.getCardHolderName());
                orderDetailDTO.setCardInfo(patientPaymentInfo.getCardType() + " ***" + patientPaymentInfo.getCardNumber().substring(patientPaymentInfo.getCardNumber().length() - 4));
            } else {
                logger.info("Payment info is null against this profile id: " + patientProfile.getId());
                orderDetailDTO.setCardHolderName("");
                orderDetailDTO.setCardInfo("");
            }
        } catch (Exception e) {
            logger.error("Exception -> getPlaceRxOrderDetailsWs", e);
        }
        return orderDetailDTO;
    }

    public Order saveRxOrder(PatientProfile patientProfile, Integer drugId, String finalPayment, String redeemPoints, String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty, String drugPrice) {
        Order order = new Order();
        try {
            order.setFirstName(patientProfile.getFirstName());
            order.setLastName(patientProfile.getLastName());
            order.setDrug(new Drug(drugId));
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                order.setPayment(Double.parseDouble(finalPayment));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }
            order.setStrength(strength);
            order.setDrugName(drugName);
            order.setDrugType(drugType);
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            order.setOrderType(Constants.ORDERTYPE_RXORDER);
            patientProfileDAO.save(order);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrder", e);
        }
        return order;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public Order saveRxOrder(PatientProfile patientProfile, Integer drugId, String finalPayment, String redeemPoints,
            String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty,
            String drugPrice, PatientPaymentInfo patientPaymentInfo, String addressId) {
        Order order = new Order();
        try {
            order.setFirstName(patientProfile.getFirstName());
            order.setLastName(patientProfile.getLastName());
            order.setDrug(new Drug(drugId));
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            //PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                order.setPayment(Double.parseDouble(finalPayment));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }
            order.setStrength(strength);
            order.setDrugName(drugName);
            order.setDrugType(drugType);
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            order.setOrderType(Constants.ORDERTYPE_RXORDER);
            //save OrderChain
            OrderChain orderChain = saveOrderChain(drugId);
            order.setOrderChain(orderChain);
            patientProfileDAO.save(order);
            //saveRewardOrderHistory(redeemPoints, patientProfile);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrder", e);
        }
        return order;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public Order saveRxOrder(PatientProfile patientProfile, Integer drugId, String finalPayment, String redeemPoints,
            String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty,
            String drugPrice, String additionalMargin, PatientPaymentInfo patientPaymentInfo, String addressId, String requestId) {
        Order order = new Order();
        try {
            TransferRequest transferRequest = new TransferRequest();
            transferRequest = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(requestId, 0));
            if (transferRequest != null) {
                String patientName = AppUtil.getSafeStr(transferRequest.getPatientName(), "");
                if (patientName.length() > 50) {
                    patientName = patientName.substring(0, 49);
                }
                order.setFirstName(patientName);
                order.setLastName("");
            } else {
                order.setFirstName(patientProfile.getFirstName());
                order.setLastName(patientProfile.getLastName());
            }
            order.setDrug(new Drug(drugId));
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            //PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                order.setPayment(Double.parseDouble(finalPayment));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }
            order.setStrength(strength);
            order.setDrugName(drugName);
            order.setDrugType(drugType);
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setAdditionalMargin(AppUtil.getSafeDouble(additionalMargin, 0d));
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            order.setOrderType(Constants.ORDERTYPE_RXORDER);
            patientProfileDAO.save(order);
            //saveRewardOrderHistory(redeemPoints, patientProfile);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrder", e);
        }
        return order;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public Order saveRxOrder(PatientProfile patientProfile, Long drugId, String finalPayment, String redeemPoints,
            String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty,
            String drugPrice, String additionalMargin, PatientPaymentInfo patientPaymentInfo, String addressId, String requestId,
            String video, String img, String comments, Boolean isRefill, Boolean isPrescriptionHardCopy, Boolean addCopyCard) {
        Order order = new Order();
        try {

            OrderChain orderChain = null;
            if (AppUtil.getSafeLong(requestId, 0L) > 0L) {
                orderChain = (OrderChain) this.patientProfileDAO.findRecordById(new OrderChain(), AppUtil.getSafeLong(requestId, 0L));
                if (orderChain != null) {
                    List<Order> lstOrder = orderChain.getOrderList();
                    if (lstOrder != null && lstOrder.size() > 0 && lstOrder.get(0) != null) {
                        order = lstOrder.get(0);
                    }
                }
            }

            TransferRequest transferRequest = new TransferRequest();
            transferRequest = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(requestId, 0));
            if (transferRequest != null) {
                String patientName = AppUtil.getSafeStr(transferRequest.getPatientName(), "");
                if (patientName.length() > 50) {
                    patientName = patientName.substring(0, 49);
                }
                order.setFirstName(patientName);
                order.setLastName("");
            } else {
                order.setFirstName(patientProfile.getFirstName());
                order.setLastName(patientProfile.getLastName());
            }
            //order.setDrug(new Drug(drugId));
            DrugDetail detail = new DrugDetail();
            detail.setDrugNDC(drugId);
            order.setDrugDetail(detail);
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            //PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                order.setPayment(Double.parseDouble(finalPayment));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }

            DrugDetail tmp = (DrugDetail) this.patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (tmp != null) {
                order.setStrength(tmp.getStrength());
                order.setDrugName(tmp.getDrugBasic().getBrandName());
                order.setDrugType(tmp.getDrugForm().getFormDescr());
                order.setRxExpiry(tmp.getMonthRxExpiration());
//                order.setStrength(strength);
//                order.setDrugName(drugName);
//                order.setDrugType(drugType);
            }
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setAdditionalMargin(AppUtil.getSafeDouble(additionalMargin, 0d));
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            if (AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Transfer")) {
                order.setOrderType(Constants.ORDERTYPE_RXORDER);
            }
            order.setVideo(video);
            order.setImagePath(img);
            order.setPatientComments(comments);
            order.setRxAcqCost(AppUtil.getSafeDouble(drugPrice, 0d));
            if (orderChain == null) {
                orderChain = new OrderChain();
                orderChain.setDrugDetail(detail);
                order.setPatientProfile(new PatientProfile(patientProfile.getId()));
                if (!CommonUtil.isNullOrEmpty(detail.getDrugDetailId())) {
                    orderChain.setRefillAllow(0);
                    orderChain.setRefillDayes(0);
                    orderChain.setRefillRemaing(0);
                    orderChain.setLastRefillDate(new Date());
                    orderChain.setNextRefillDate(new Date());

                    String rxExpiry = AppUtil.getSafeStr(order.getRxExpiry(), "");
                    Date expiredDate = DateUtil.addDaysToDate(new Date(), 365);
                    if (rxExpiry.equalsIgnoreCase("y"))//for controlled drug
                    {
                        expiredDate = DateUtil.addDaysToDate(new Date(), 182);
                    }
                    orderChain.setRxExpiredDate(expiredDate);
                }
                order.setOrderChain(orderChain);
            }
            order.setIsPrescriptionHardCopy(isPrescriptionHardCopy);
            order.setAddCopyCard(addCopyCard);
            patientProfileDAO.save(order);
            //saveRewardOrderHistory(redeemPoints, patientProfile);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrder", e);
        }
        return order;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public Order saveRxOrder(PatientProfile patientProfile, Long drugId, String finalPayment, String priceWithMargin, String redeemPoints,
            String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty,
            String drugPrice, String additionalMargin, PatientPaymentInfo patientPaymentInfo, String addressId, String requestId,
            String video, String img, String comments, Boolean isRefill, Boolean isPrescriptionHardCopy, Boolean addCopyCard,
            String paymentType, DeliveryPreferences deliveryPreference, String cardId, String copayIds, String miles, String rxAcqCost) {
        Order order = new Order();
        try {

            OrderChain orderChain = null;
            if (AppUtil.getSafeLong(requestId, 0L) > 0L) {
                orderChain = (OrderChain) this.patientProfileDAO.findRecordById(new OrderChain(), AppUtil.getSafeLong(requestId, 0L));
                if (orderChain != null) {
                    List<Order> lstOrder = orderChain.getOrderList();
                    if (lstOrder != null && lstOrder.size() > 0 && lstOrder.get(0) != null) {
                        order = lstOrder.get(0);
                    }
                }
            }

            TransferRequest transferRequest = new TransferRequest();
            transferRequest = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(requestId, 0));
//            if (transferRequest != null) {
//                String patientName = AppUtil.getSafeStr(transferRequest.getPatientName(), "");
//                if (patientName.length() > 50) {
//                    patientName = patientName.substring(0, 49);
//                }
//                order.setFirstName(patientName);
//                order.setLastName("");
//            } else {
//                order.setFirstName(patientProfile.getFirstName());
//                order.setLastName(patientProfile.getLastName());
//            }
            //order.setDrug(new Drug(drugId));
            DrugDetail detail = new DrugDetail();
            //detail.setDrugNDC(drugId);
            detail.setDrugDetailId(drugId);
            order.setDrugDetail(detail);
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            //PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                //order.setPayment(Double.parseDouble(finalPayment));
                order.setFinalPayment(Double.parseDouble(finalPayment));
                order.setEstimatedPrice(AppUtil.getSafeDouble(finalPayment, 0d));
            }
            if (drugPrice != null) {
                order.setPriceIncludingMargins(AppUtil.getSafeDouble(drugPrice, 0d));
//                order.setEstimatedPrice(AppUtil.getSafeDouble(drugPrice, 0d));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            order.setNextRefillFlag("0");
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }

            DrugDetail tmp = (DrugDetail) this.patientProfileDAO.findRecordById(new DrugDetail(), drugId);
            if (tmp != null) {
                order.setStrength(tmp.getStrength());
                String brandName = tmp.getDrugBasic() != null ? tmp.getDrugBasic().getBrandName() : "";
                String genericName = tmp.getDrugBasic() != null && tmp.getDrugBasic().getDrugGeneric() != null
                        ? tmp.getDrugBasic().getDrugGeneric().getGenericName() : "";
                order.setDrugName(brandName + "{" + genericName + "}");
                order.setDrugType(tmp.getDrugForm().getFormDescr());
//                order.setStrength(strength);
//                order.setDrugName(drugName);
//                order.setDrugType(drugType);
            }
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setAdditionalMargin(AppUtil.getSafeDouble(additionalMargin, 0d));
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            if (AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Transfer")) {
                order.setOrderType(Constants.ORDERTYPE_RXORDER);
            }
            order.setVideo(video);
            order.setImagePath(img);
            order.setPatientComments(comments);
            //To do with ios team
            if (CommonUtil.isNotEmpty(rxAcqCost)) {
                order.setRxAcqCost(AppUtil.getSafeDouble(rxAcqCost, 0d));
            } else {
                order.setRxAcqCost(AppUtil.getSafeDouble(drugPrice, 0d));
            }
            if (orderChain == null) {
                orderChain = new OrderChain();
                orderChain.setDrugDetail(detail);
                order.setPatientProfile(new PatientProfile(patientProfile.getId()));
                order.setOrderChain(orderChain);
            }
            order.setIsPrescriptionHardCopy(isPrescriptionHardCopy);
            order.setAddCopyCard(addCopyCard);
            order.setPaymentType(paymentType);
            order.setFinalPaymentMode(paymentType);
            if (deliveryPreference != null) {
                order.setDeliveryPreference(deliveryPreference);
            }
            order.setPaymentId(AppUtil.getSafeInt(cardId, 0));
            order.setMiles(miles);
            patientProfileDAO.save(order);
            //saveRewardOrderHistory(redeemPoints, patientProfile);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
            logger.info("copayIds:: " + copayIds);
            if (CommonUtil.isNotEmpty(copayIds)) {
                String[] copayIdArr = copayIds.split(",");
                if (copayIdArr != null && copayIdArr.length > 0) {
                    for (int i = 0; copayIdArr != null && i < copayIdArr.length; i++) {
                        CoPayCardDetails coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), AppUtil.getSafeLong(copayIdArr[i], 0L));
                        if (coPayCardDetails != null && coPayCardDetails.getId() != null) {
                            coPayCardDetails.setOrder(order);
                            patientProfileDAO.update(coPayCardDetails);
                        }
                    }
                }
            }

//            if (addCopyCard && coPayCardDetailsList!=null) {
//                CoPayCardDetails coPayCardDetails = new CoPayCardDetails();
//                for (CoPayCardDetails CoPayCardDetails : coPayCardDetailsList) {
////                    coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), coPayCardId);
//                    coPayCardDetails.setOrder(order);
//                    patientProfileDAO.update(coPayCardDetails);
//                }
//            }
//          Hibernate.initialize(order.getDrugDetail());
//          Hibernate.initialize(order.getDrugDetail().getDrugBasic());
//          Hibernate.initialize(order.getDrugDetail().getDrugBasic().getDrugGeneric());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> saveRxOrder", e);
        }

        return order;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public Order saveRxOrder(PatientProfile patientProfile, Integer drugId, String finalPayment, String redeemPoints,
            String redeemPointsCost, String handLingFee, String strength, String drugName, String drugType, String qty,
            String drugPrice, String additionalMargin, PatientPaymentInfo patientPaymentInfo, String addressId, String requestId,
            String video) {
        Order order = new Order();
        try {
            TransferRequest transferRequest = new TransferRequest();
            transferRequest = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(requestId, 0));
            if (transferRequest != null) {
                String patientName = AppUtil.getSafeStr(transferRequest.getPatientName(), "");
                if (patientName.length() > 50) {
                    patientName = patientName.substring(0, 49);
                }
                order.setFirstName(patientName);
                order.setLastName("");
            } else {
                order.setFirstName(patientProfile.getFirstName());
                order.setLastName(patientProfile.getLastName());
            }
            order.setDrug(new Drug(drugId));
            order.setPatientProfile(new PatientProfile(patientProfile.getId()));
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                }
            }
            //PatientPaymentInfo patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Set payment info. ");
                order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                order.setCardNumber(patientPaymentInfo.getCardNumber());
                order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                order.setCardCvv(patientPaymentInfo.getCvvNumber());
                order.setCardType(patientPaymentInfo.getCardType());
            } else {
                logger.info("PatientPaymentInfo id is null against profile id: " + patientProfile.getId());
            }
            order.setRedeemPoints(redeemPoints);
            if (finalPayment != null && !finalPayment.equalsIgnoreCase("undefined")) {
                order.setPayment(Double.parseDouble(finalPayment));
            }
            if (redeemPointsCost != null && !redeemPointsCost.equalsIgnoreCase("undefined")) {
                order.setRedeemPointsCost(Double.parseDouble(redeemPointsCost));
            }
            OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.ORDER_STATUS_UNASSIGNED_ID);
            if (orderStatus != null && orderStatus.getId() != null) {
                order.setOrderStatus(orderStatus);
            }
            if (handLingFee != null && !handLingFee.equalsIgnoreCase("undefined")) {
                order.setHandLingFee(Double.parseDouble(handLingFee));
            }
            order.setStrength(strength);
            order.setDrugName(drugName);
            order.setDrugType(drugType);
            order.setQty(qty);
            if (drugPrice != null) {
                logger.info("Drug Price is: " + drugPrice);
                order.setDrugPrice(Double.parseDouble(drugPrice));
            }
            order.setAdditionalMargin(AppUtil.getSafeDouble(additionalMargin, 0d));
            order.setCreatedOn(new Date());
            order.setUpdatedOn(new Date());
            order.setOrderHistory(null);
            order.setOrderType(Constants.ORDERTYPE_RXORDER);
            order.setVideo(video);
            patientProfileDAO.save(order);
            //saveRewardOrderHistory(redeemPoints, patientProfile);
            order.setAwardedPoints(saveRewardOrderHistory(redeemPoints, patientProfile, order));
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrder", e);
        }
        return order;
    }
    //////////////////////////////////////////////////////////////////////////////////////

    public OrderHistory saveOrderHistory(OrderHistory history) throws Exception {
        history.setCreatedOn(new Date());
        history.setUpdatedOn(new Date());
        history.setUpdatedBy(0);
        history.setCreatedBy(0);
        patientProfileDAO.save(history);
        return history;
    }

    private BigDecimal saveRewardOrderHistory(String redeemPoints, PatientProfile patientProfile, Order order) throws NumberFormatException, Exception {
        logger.info("Redeem Points are: " + redeemPoints);
        RewardHistory rewardHistory = new RewardHistory();
        rewardHistory.setDescription("Place Order");
        rewardHistory.setPatientId(patientProfile.getId());
        rewardHistory.setType(Constants.MINUS);
        if (CommonUtil.isNotEmpty(redeemPoints)) {
            rewardHistory.setPoint(AppUtil.getSafeFloat(redeemPoints, 0f).intValue());
        }
        rewardHistory.setCreatedDate(new Date());
        patientProfileDAO.save(rewardHistory);
        RewardPoints rewardPoints = this.getRewardPoints(Constants.REWARD_ORDER_PLACE_POINT);
        if (rewardPoints != null && rewardPoints.getId() != null) {
            logger.info("Redeem Points are: " + rewardPoints.getPoint());
            rewardHistory = new RewardHistory();
            rewardHistory.setDescription("Place Order");
            rewardHistory.setPatientId(patientProfile.getId());
            rewardHistory.setType(Constants.PLUS);
            if (rewardPoints.getPoint() != null) {
                rewardHistory.setPoint(rewardPoints.getPoint().intValueExact());
            }
            rewardHistory.setCreatedDate(new Date());
            rewardHistory.setOrder(order);
            patientProfileDAO.save(rewardHistory);
        }
        return rewardPoints != null ? rewardPoints.getPoint() : BigDecimal.ZERO;
    }

    private BigDecimal saveRewardOrderHistory(String redeemPoints, PatientProfile patientProfile, Order order, String rewardKey, int rewardId) throws NumberFormatException, Exception {
        logger.info("Redeem Points are: " + redeemPoints);
        RewardHistory rewardHistory = new RewardHistory();
        rewardHistory.setDescription(rewardKey);
        rewardHistory.setPatientId(patientProfile.getId());
        rewardHistory.setType(Constants.MINUS);
        if (CommonUtil.isNotEmpty(redeemPoints)) {
            rewardHistory.setPoint(new Integer(redeemPoints));
        }
        rewardHistory.setCreatedDate(new Date());
        patientProfileDAO.save(rewardHistory);
        RewardPoints rewardPoints = this.getRewardPoints(rewardId);
        if (rewardPoints != null && rewardPoints.getId() != null) {
            logger.info("Redeem Points are: " + rewardPoints.getPoint());
            rewardHistory = new RewardHistory();
            rewardHistory.setDescription(rewardKey);
            rewardHistory.setPatientId(patientProfile.getId());
            rewardHistory.setType(Constants.PLUS);
            if (rewardPoints.getPoint() != null) {
                rewardHistory.setPoint(rewardPoints.getPoint().intValueExact());
            }
            rewardHistory.setCreatedDate(new Date());
            rewardHistory.setOrder(order);
            patientProfileDAO.save(rewardHistory);
        }
        return rewardPoints != null ? rewardPoints.getPoint() : BigDecimal.ZERO;
    }

    public OrderDetailDTO getRedeemPointsWs(String redeemPoints) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        try {
            if (CommonUtil.isNotEmpty(redeemPoints)) {
                logger.info("Redeem Points Points: " + redeemPoints);
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                if (feeSettings != null && feeSettings.getId() != null) {
                    logger.info("FeeSettings value: " + feeSettings.getFee().doubleValue());
                    DecimalFormat df = new DecimalFormat("#.##");
                    Double totalRedeemPoints = feeSettings.getFee().doubleValue() * Double.parseDouble(redeemPoints);
                    logger.info("Before format Total Redeem Points is " + totalRedeemPoints);
                    totalRedeemPoints = Double.valueOf(df.format(totalRedeemPoints));
                    logger.info("After Format Total Redeem Points is " + totalRedeemPoints);
                    orderDetailDTO.setRedeemPointsCost(totalRedeemPoints);
                    orderDetailDTO.setRedeemPoints(redeemPoints);
                }
            }
        } catch (Exception e) {
            logger.error("Exception -> getHandlingFee", e);
        }
        return orderDetailDTO;
    }

    public OrderDetailDTO getQuickStatsWs(Integer patientId) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        try {
            Long totalRxShipped = patientProfileDAO.getTotalRxOrderStatusRecordByPatientId(patientId, Constants.ORDER_STATUS.SHIPPED_ID);
            logger.info("Total Rx Shipped Order Count: " + totalRxShipped);
            if (totalRxShipped != null) {
                orderDetailDTO.setTotalRxShipped(totalRxShipped);
            } else {
                orderDetailDTO.setTotalRxShipped(0L);
            }
            Long totalRxOpenOrder = patientProfileDAO.getTotalRxOrderStatusRecordByPatientId(patientId, Constants.ORDER_STATUS_UNASSIGNED_ID);
            logger.info("Total Rx Open Order Count: " + totalRxOpenOrder);
            if (totalRxOpenOrder != null) {
                orderDetailDTO.setTotalOpenOrders(totalRxOpenOrder);
            } else {
                orderDetailDTO.setTotalOpenOrders(0L);
            }
            Double totalSavingAmount = patientProfileDAO.getTotalRxOrderSaving(patientId);
            if (totalSavingAmount != null) {
                orderDetailDTO.setSavingsToDate(totalSavingAmount);
            } else {
                orderDetailDTO.setSavingsToDate(0.00);
            }
            Long totalDependent = patientProfileDAO.getTotalPatientMembersByProfileId(patientId);
            logger.info("Total Dependent is: " + totalDependent);
            if (totalDependent != null) {
                orderDetailDTO.setTotalDependent(totalDependent);
            } else {
                orderDetailDTO.setTotalDependent(0L);
            }
            Long totalPlaceOrders = patientProfileDAO.getTotalActiveOrdersRecordsByProfileId(patientId);
            logger.info("Total Place Orders: " + totalPlaceOrders);
            if (totalPlaceOrders != null) {
                orderDetailDTO.setTotalPlaceOrders(totalPlaceOrders);
            } else {
                orderDetailDTO.setTotalPlaceOrders(0L);
            }
            Long totalDeliveryAddress = patientProfileDAO.getTotalDeliveryAddressByProfileId(patientId);
            if (totalDeliveryAddress != null) {
                orderDetailDTO.setTotalDeliveryAddress(totalDeliveryAddress);
            } else {
                orderDetailDTO.setTotalDeliveryAddress(0L);
            }
            Long totalPaymentCard = patientProfileDAO.getTotalPaymentCardsByProfileId(patientId);
            if (totalPaymentCard != null) {
                orderDetailDTO.setTotalPaymentCard(totalPaymentCard);
            } else {
                orderDetailDTO.setTotalPaymentCard(0L);
            }

            List<String> orderStatusList = new ArrayList<>();
            orderStatusList.add(Constants.ORDER_STATUS.PROCESSING);
            orderStatusList.add(Constants.ORDER_STATUS.INTERPRETED_IMAGE);
            orderStatusList.add(Constants.ORDER_STATUS.RESPONSE_RECEIVED);
            Long processingOrderCount = patientProfileDAO.getTotalOrders(orderStatusList, patientId); //patientProfileDAO.getTotalRxOrderStatusRecordByPatientId(patientId, Constants.ORDER_STATUS.PROCESSING_ID);
            if (totalPaymentCard != null) {
                orderDetailDTO.setProcessingOrdersCount(processingOrderCount);
            } else {
                orderDetailDTO.setProcessingOrdersCount(0L);
            }
            Long rxShippingOrderCount = patientProfileDAO.getTotalRxOrderStatusRecordByPatientId(patientId, Constants.ORDER_STATUS.FILLED_ID);
            if (rxShippingOrderCount != null) {
                orderDetailDTO.setShippingOrdersCount(rxShippingOrderCount);
            } else {
                orderDetailDTO.setShippingOrdersCount(0L);
            }
            List watingResponseList = patientProfileDAO.getNotificationMessagesListForWaitingResponses(patientId);
            Integer rxWaitingPtResponseOrderCount = watingResponseList != null && watingResponseList.size() > 0 ? watingResponseList.size() : 0;
            if (rxWaitingPtResponseOrderCount != null) {
                orderDetailDTO.setTotalWaitingPtResponseCount(rxWaitingPtResponseOrderCount);
            } else {
                orderDetailDTO.setTotalWaitingPtResponseCount(0);
            }
            orderDetailDTO.setMsgCount(patientProfileDAO.getTotalUnReadNotificationMessages(patientId));

            RewardPoints rewardPoints = getMyRewardsPoints(patientId);
            orderDetailDTO.setAvailablePoints(rewardPoints.getAvailablePoints());
            orderDetailDTO.setAvailedRewardPoints(rewardPoints.getAvailedRewardPoints());
            orderDetailDTO.setLifetimeRewardPoints(rewardPoints.getLifeTimePoints());
            orderDetailDTO.setRefillAbleCount(patientProfileDAO.getRefillableOrdersListByProfileId(patientId).size());
            orderDetailDTO.setInsuranceCardsCount(patientProfileDAO.getInsuranceCardsCount(patientId));
            orderDetailDTO.setCopayCardsCount(patientProfileDAO.getCopayCardsCount(patientId));
            List<BusinessObject> lstObj = new ArrayList();
            BusinessObject obj = new BusinessObject("patientProfile.id", patientId, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            obj = new BusinessObject("orderStatus.id", Constants.ORDER_STATUS.FILLED_ID, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            obj = new BusinessObject("deliveryPreference.id", Constants.DELIVERY_PREfFERENCES.PICK_UP_AT_PHARMACY_ID, Constants.HIBERNATE_NE_OPERATOR);
            lstObj.add(obj);
            orderDetailDTO.setTotalReadyToDeliverRxOrders(patientProfileDAO.getTotalRecordsByNestedProperty(new Order(), lstObj, "Count(*)"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getQuickStatsWs", e);
        }
        return orderDetailDTO;
    }

    public Set<OrderDetailDTO> getFilledRxHistory(Integer profileId) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.getActiveOrdersListByProfileId(profileId);
            list = setOrderList2(dbOrders, Constants.FILLED_RX_HISTORY, "");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getFilledRxHistory", e);
        }
        return list;
    }

    ///////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> getRefillRx(Integer profileId, String orderId) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.getRefillableOrdersListByProfileId(profileId);
            list = setOrderList2(dbOrders, Constants.FILLED_RX_HISTORY, orderId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getFilledRxHistory", e);
        }
        return list;
    }
    //////////////////////////////////////////////////////////////////

    public Set<OrderDetailDTO> viewOrderReceiptWs(Integer patientId, String orderId) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.viewOrderReceiptList(patientId, orderId);
            list = this.setOrderList(dbOrders, null);
            logger.info("New Order list size: " + list.size());
        } catch (Exception e) {
            logger.error("Exception -> viewOrderReceiptWs", e);
        }
        return list;
    }

    private Set<OrderDetailDTO> setOrderList(List<Order> dbOrders, String type) throws Exception {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        for (Order order : dbOrders) {
            OrderDetailDTO newOrder = new OrderDetailDTO();
            newOrder.setId(order.getId());
            newOrder.setFirstName("");//order.getFirstName());//Commented on Adil/Rafay(IOS team) Request to fix the problems during confirmpaymentws & updaterxorderws
            newOrder.setLastName("");//order.getLastName());//Commented on Adil/Rafay(IOS team) Request to fix the problems during confirmpaymentws & updaterxorderws
            if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
                newOrder.setOrderChainId(order.getOrderChain().getId());
            }
            CommonUtil.populateDecryptedOrderData(newOrder, order);
            newOrder.setPatientName(AppUtil.getSafeStr(
                    AppUtil.getSafeStr(newOrder.getFirstName(), "") + " "
                    + AppUtil.getSafeStr(newOrder.getLastName(), ""), ""));
            if (order.getCreatedOn() != null) {
                newOrder.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
                newOrder.setOrderTime(DateUtil.dateToString(order.getCreatedOn(), "hh:mm:ss"));
            }
//            if (order.getOrderStatus() != null && order.getOrderStatus().getId() != null) {
//                newOrder.setOrderStatusName(order.getOrderStatus().getName());
//                if (order.getOrderStatus().getId() == 8) {
//                    newOrder.setOrderType(1);
//                } else {
//                    newOrder.setOrderType(0);
//                }
//            }

            PatientProfileMembers dependent = order.getPatientProfileMembers();
            if (dependent == null) {
                newOrder.setCareGiver(0);
            } else {
                Boolean isAdult = dependent.getIsAdult();

                if (isAdult == null || isAdult == false) {
                    newOrder.setCareGiver(0);
                } else {
                    newOrder.setCareGiver(1);
                }
            }

            newOrder.setOrderType(AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Transfer") ? 1 : 0);
            newOrder.setStrength(order.getStrength());
            newOrder.setDrugName(order.getDrugName());
            if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.TABLET)) {
                newOrder.setDrugType("TAB");
            } else if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.CAPSULE)) {
                newOrder.setDrugType("CAP");
            } else {
                newOrder.setDrugType(AppUtil.getSafeStr(order.getDrugType(), "-"));
            }
            newOrder.setQty(order.getQty());
            newOrder.setDrugPrice(order.getDrugPrice());
            newOrder.setRedeemPoints(order.getRedeemPoints());
            newOrder.setRedeemPointsCost(order.getRedeemPointsCost() != null ? order.getRedeemPointsCost() : 0d);
            newOrder.setHandLingFee(order.getHandLingFee() != null ? order.getHandLingFee() : 0d);
            double payment = order.getPayment() != null ? order.getPayment() : 0d;
            newOrder.setPaymentIncludingRedmeenCost(AppUtil.roundOffNumberToCurrencyFormat(payment + newOrder.getRedeemPointsCost(), "en", "US"));
            newOrder.setPaymentIncludingShipping(AppUtil.roundOffNumberToCurrencyFormat(payment + newOrder.getHandLingFee(), "en", "US"));
            newOrder.setCardType(order.getCardType());
//            newOrder.setRxNumber(order.getSystemGeneratedRxNumber());
            String cardNumber = order.getCardNumber();
            logger.info("Card Number is: " + cardNumber);
            if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                logger.info("card number is: " + cardNumber.substring(order.getCardNumber().length() - 4));
                newOrder.setCardNumber(cardNumber.substring(order.getCardNumber().length() - 4));
            } else {
                logger.info("Card Number length is less than 4: " + cardNumber);
                newOrder.setCardNumber(cardNumber);
            }
            if (order.getPayment() != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                String formatPayment = decimalFormat.format(order.getPayment());
                logger.info("formatPayment" + formatPayment);
                newOrder.setPayment(Double.parseDouble(formatPayment));
            }

            if (order.getApartment() != null && !"".equals(order.getApartment())) {
                logger.info("Appartmnet is: " + order.getApartment());
                newOrder.setShippingAddress(AppUtil.getSafeStr(order.getStreetAddress(), "") + " "
                        + AppUtil.getSafeStr(order.getApartment(), "") + " " + AppUtil.getSafeStr(order.getCity(), "")
                        + ", " + AppUtil.getSafeStr(order.getState(), "") + " "
                        + AppUtil.getSafeStr(order.getZip(), ""));
            } else {
                newOrder.setShippingAddress(AppUtil.getSafeStr(order.getStreetAddress(), "") + " "
                        + AppUtil.getSafeStr(order.getCity(), "") + ", " + AppUtil.getSafeStr(order.getState(), "")
                        + " " + AppUtil.getSafeStr(order.getZip(), ""));
            }
            //PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(order.getPatientProfile().getId());

            if (order.getDeliveryPreference() != null
                    && order.getDeliveryPreference().getId() != null) {
                newOrder.setDeliveryPreferencesName(order.getDeliveryPreference().getName());
                String pref = newOrder.getDeliveryPreferencesName();
                String name = AppUtil.getSafeStr(pref, "").toLowerCase();
                if (name.contains("2nd day")) {
                    newOrder.setHandLingFee(0.0d);
                    newOrder.setIncludedStr("Included");
                } else {
                    newOrder.setHandLingFee(order.getHandLingFee());
                    newOrder.setIncludedStr("");
                }
            }

            DrugDetail detail = order.getDrugDetail();
            if (detail != null && detail.getDrugDetailId() != null) {
                if (detail.getDrugBasic() != null) {
                    newOrder.setBrandName(detail.getDrugBasic().getBrandName());
                    if (detail.getDrugBasic().getDrugGeneric() != null) {
                        newOrder.setGenericName(detail.getDrugBasic().getDrugGeneric().getGenericName());
                    }
                }
            }
            newOrder.setComments(AppUtil.getSafeStr(order.getPatientComments(), ""));
            newOrder.setPaymentType(AppUtil.getSafeStr(order.getPaymentType(), ""));
            String source = "";
            if (AppUtil.getSafeStr(order.getVideo(), "").length() > 0) {
                source = "Video";
            } else if (AppUtil.getSafeStr(order.getImagePath(), "").length() > 0) {
                source = "Image";
            }
            newOrder.setSource(source);
            newOrder.setIsCopayCard(order.getAddCopyCard());
            newOrder.setOrderStatusName(order.getOrderStatus() != null ? AppUtil.getSafeStr(order.getOrderStatus().getName(), "") : "");
            getOrderRewardDetail(order.getPatientProfile().getId(), newOrder);
            if (order.getRefillsAllowed() != null && order.getRefillsRemaining() != null) {
                String status = order.getOrderStatus() != null ? AppUtil.getSafeStr(order.getOrderStatus().getName(), "") : "";
                Date nextRefillDate = order.getNextRefillDate();
                Date today = new Date();
                int refillDone = order.getRefillDone() != null ? order.getRefillDone() : 0;
                newOrder.setRefill(order.getRefillsRemaining() != null && order.getRefillsRemaining() > 0 //&& !nextRefillDate.before(today)
                        && refillDone == 0
                        && (status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("DELIVERY")) ? 1 : 0);
            } else {
                newOrder.setRefill(0);
            }
            newOrder.setDaysSupply(order.getDaysSupply() != null ? order.getDaysSupply() : 0);

            if (order.getRefillsRemaining() == null || order.getRefillsRemaining() == 0) {
                newOrder.setDaysToRefill("No Refills remaining.");
            } else if ((AppUtil.getSafeStr(newOrder.getOrderStatusName(), "").equalsIgnoreCase("Pending")
                    || AppUtil.getSafeStr(newOrder.getOrderStatusName(), "").equalsIgnoreCase("Processing"))
                    && order.getOrderType().equalsIgnoreCase("Refill")) {
                newOrder.setDaysToRefill("Pending Pharmacy Review");
            } else if (AppUtil.getSafeStr(newOrder.getOrderStatusName(), "").equalsIgnoreCase("DELIVERY")
                    || AppUtil.getSafeStr(newOrder.getOrderStatusName(), "").equalsIgnoreCase("Shipped")) {
                if (order.getNextRefillDate() != null) {
                    if (order.getRefillDone() != null && order.getRefillDone() == 1) {
                        newOrder.setDaysToRefill("Already filled");
                    } else {
                        long daysCount = DateUtil.dateDiffInDays(new Date(), order.getNextRefillDate());
                        newOrder.setRefillRemainingDaysCount(daysCount);
                        if (daysCount == 0) {
                            newOrder.setDaysToRefill("NEXT REFILL SAME DAY");
                        } else if (daysCount > 0) {
                            String dayStr = daysCount > 1 ? "days" : "day";
                            newOrder.setDaysToRefill("NEXT REFILL in " + daysCount + " " + dayStr);
                        } else {
                            String dayStr = daysCount == -1 ? "day" : "days";
                            newOrder.setDaysToRefill("REFILL OVER DUE " + daysCount + " " + dayStr);
                        }

                    }
                } else {
                    newOrder.setDaysToRefill("Not Refillable Yet");
                }
            } else {
                newOrder.setDaysToRefill("Not Refillable Yet");
            }

            if (AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Refill")) {
                newOrder.setRequestType("Refill Requested");
            } else {
                String requestType = "";
                if (order.getIsBottleAvailable() != null && order.getIsBottleAvailable()) {
                    requestType = "X-FER LABEL SCAN";
                    newOrder.setRequestType(requestType);
                } else {
                    requestType = "X-FER RX SCAN";
                    newOrder.setRequestType(requestType);
                }
            }

            if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
                newOrder.setPrescriptionNumber(order.getOrderChain().getId().toString());
                newOrder.setOrderChainId(order.getOrderChain().getId());
                if (order.getOrderChain().getLastRefillDate() != null) {
                    newOrder.setLastFilledDate(DateUtil.dateToString(order.getOrderChain().getLastRefillDate(), "MM/dd/yyyy"));
                } else {
                    newOrder.setLastFilledDate("");
                }
                Integer refillRemaining = order.getOrderChain().getRefillRemaing();
                if (refillRemaining == null) {
                    refillRemaining = order.getRefillsRemaining() != null ? order.getRefillsRemaining() : 0;
                }
                newOrder.setRefillsRemaining(refillRemaining + "");
            } else {
                newOrder.setPrescriptionNumber(order.getId());
                newOrder.setLastFilledDate("");
            }
            newOrder.setPatientId(order.getPatientProfile().getId().toString());
            newOrder.setDependentId(order.getPatientProfileMembers() != null ? order.getPatientProfileMembers().getId().toString() : "0");
            Integer paymentId = order.getPaymentId();
            try {
                PatientPaymentInfo patientPaymentInfo = this.patientProfileDAO.getPatientPaymentInfoById(paymentId,
                        order.getPatientProfile() != null ? order.getPatientProfile().getId() : 0);
                if (patientPaymentInfo != null) {
                    newOrder.setCardType(patientPaymentInfo.getCardType());
                    newOrder.setCardNumber(patientPaymentInfo.getCardNumber());
                } else {
                    newOrder.setCardType("");
                    newOrder.setCardNumber("");
                }
                newOrder.setIsUpdateMfr(CommonUtil.isEditableOrder(AppUtil.getSafeStr(newOrder.getOrderStatusName(), "")));
                newOrder.setIsBottleAvailable(order.getIsBottleAvailable() == null ? Boolean.FALSE : order.getIsBottleAvailable());
            } catch (Exception e) {
                logger.error("Exception:: setOrderList", e);
            }
            newOrder.setPharmacyName(AppUtil.getSafeStr(order.getPharmacyName(), ""));
            newOrder.setPharmacyPhone(AppUtil.getSafeStr(order.getPharmacyPhone(), ""));
            newOrder.setPrescriberName(AppUtil.getSafeStr(order.getPrescriberLastName(), ""));
            newOrder.setPrescriberPhone(AppUtil.getSafeStr(order.getPrescriberPhone(), ""));
//            if (order.getRxNumber() != null) {
//                newOrder.setOrigRx(AppUtil.getSafeStr(order.getRxNumber(), ""));
//            } else {
            newOrder.setOrigRx(AppUtil.getSafeStr(order.getOldRxNumber(), ""));
            System.out.println("Pharmacy phone " + AppUtil.getSafeStr(newOrder.getPharmacyPhone(), ""));
            System.out.println("ORIG RX " + AppUtil.getSafeStr(newOrder.getOrigRx(), ""));
//            }
            newOrder.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), ""));

            List<OrderTransferImagesDTO> orderTransferImagesDTOs = populateOrderTransferImages(order);
            newOrder.setOrderTransferImages(orderTransferImagesDTOs);
            list.add(newOrder);
        }
        return list;
    }

    private List<OrderTransferImagesDTO> populateOrderTransferImages(Order order) {
        List<OrderTransferImagesDTO> orderTransferImagesDTOs = new ArrayList<>();
        if (!CommonUtil.isNullOrEmpty(order.getOrderTransferImages())) {
            order.getOrderTransferImages().stream().map((orderTransferImages) -> {
                OrderTransferImagesDTO orderTransferImagesDTO = new OrderTransferImagesDTO();
                orderTransferImagesDTO.setDrugImg(EncryptionHandlerUtil.getDecryptedString(orderTransferImages.getDrugImg()));
                return orderTransferImagesDTO;
            }).forEach((orderTransferImagesDTO) -> {
                orderTransferImagesDTOs.add(orderTransferImagesDTO);
            });
        }
        return orderTransferImagesDTOs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    private Set<OrderDetailDTO> setOrderList2(List<Order> dbOrders, String type, String primaryOrderId) throws Exception {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        for (Order order : dbOrders) {
            OrderDetailDTO newOrder = populateRefillRxData(order, primaryOrderId);
            list.add(newOrder);
        }
        return list;
    }

    private OrderDetailDTO populateRefillRxData(Order order, String primaryOrderId) {
        OrderDetailDTO newOrder = new OrderDetailDTO();
        try {
            newOrder.setId(order.getId());
            newOrder.setFirstName("");//order.getFirstName());//Commented on Adil/Rafay(IOS team) Request to fix the problems during confirmpaymentws & updaterxorderws
            newOrder.setLastName("");//order.getLastName());//Commented on Adil/Rafay(IOS team) Request to fix the problems during confirmpaymentws & updaterxorderws
            if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
                newOrder.setOrderChainId(order.getOrderChain().getId());
            }
            getOrderRewardDetail(order.getPatientProfile().getId(), newOrder);
            newOrder.setPatientName(AppUtil.getSafeStr(
                    AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getFirstName()), "") + " "
                    + AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getLastName()), ""), ""));
            if (order.getCreatedOn() != null) {
                newOrder.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
                newOrder.setOrderTime(DateUtil.dateToString(order.getCreatedOn(), "hh:mm:ss"));
            }
            if (order.getFilledDate() != null) {
                newOrder.setFilledDateStr(DateUtil.dateToString(order.getFilledDate(), "MM/dd/yyyy"));
                newOrder.setFilledTime(DateUtil.dateToString(order.getFilledDate(), "hh:mm:ss"));
            }
            if (order.getShippedOn() != null) {
                newOrder.setShippedDate(DateUtil.dateToString(order.getShippedOn(), "MM/dd/yyyy"));
                newOrder.setShippedTime(DateUtil.dateToString(order.getShippedOn(), "hh:mm:ss"));
            }
            try {
                double finalPayment = order.getFinalPayment() != null ? order.getFinalPayment() : 0d;
                double handlingFee = order.getHandLingFee() != null ? order.getHandLingFee() : 0d;
                double redeemPointCost = order.getRedeemPointsCost() != null ? order.getRedeemPointsCost() : 0d;
                newOrder.setPaymentIncludingShipping(AppUtil.roundOffNumberToCurrencyFormat(finalPayment + handlingFee,
                        "en", "US"));
                newOrder.setFinalPaymentStr(AppUtil.roundOffNumberToCurrencyFormat(finalPayment, "en", "US"));
                newOrder.setOriginalPtCopay(order.getOriginalPtCopay() != null ? order.getOriginalPtCopay() : 0d);
                newOrder.setOriginalPtCopayStr(AppUtil.roundOffNumberToCurrencyFormat(newOrder.getOriginalPtCopay(), "en", "US"));
            } catch (Exception e) {
                System.out.println("EXCEPTION IN BLOCK OF setOrderList2");
                e.printStackTrace();
            }
            newOrder.setStateTaxStr(AppUtil.roundOffNumberToCurrencyFormat(0d, "en", "US"));
//            if (order.getOrderStatus() != null && order.getOrderStatus().getId() != null) {
//                newOrder.setOrderStatusName(order.getOrderStatus().getName());
//                if (order.getOrderStatus().getId() == 8) {
//                    newOrder.setOrderType(1);
//                } else {
//                    newOrder.setOrderType(0);
//                }
//            }

            PatientProfileMembers dependent = order.getPatientProfileMembers();
            if (dependent == null) {
                newOrder.setCareGiver(0);
            } else {
                Boolean isAdult = dependent.getIsAdult();

                if (isAdult == null || isAdult == false) {
                    newOrder.setCareGiver(0);
                } else {
                    newOrder.setCareGiver(1);
                }
            }

            newOrder.setOrderType(AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Transfer") ? 1 : 0);
            newOrder.setStrength(order.getStrength());
            newOrder.setDrugName(EncryptionHandlerUtil.getDecryptedString(order.getDrugName()));
            if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.TABLET)) {
                newOrder.setDrugType("TAB");
            } else if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.CAPSULE)) {
                newOrder.setDrugType("CAP");
            } else {
                newOrder.setDrugType(AppUtil.getSafeStr(order.getDrugType(), "-"));
            }
            newOrder.setQty(order.getQty());
            newOrder.setDrugPrice(order.getDrugPrice());
            double drugPrice = order.getDrugPrice() != null ? order.getDrugPrice() : 0d;
            newOrder.setPaymentIncludingRedmeenCost(AppUtil.roundOffNumberToCurrencyFormat(drugPrice,
                    "en", "US"));
            newOrder.setRedeemPoints(order.getRedeemPoints());
            newOrder.setRedeemPointsCost(order.getRedeemPointsCost());
            newOrder.setHandLingFee(order.getHandLingFee());
            newOrder.setCardType(EncryptionHandlerUtil.getDecryptedString(order.getCardType()));
            String cardNumber = EncryptionHandlerUtil.getDecryptedString(order.getCardNumber());
            logger.info("Card Number is: " + cardNumber);
            newOrder.setCardNumber(cardNumber);
            if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                logger.info("card number is: " + cardNumber.substring(newOrder.getCardNumber().length() - 4));
                newOrder.setCardNumber(cardNumber.substring(newOrder.getCardNumber().length() - 4));
            } else {
                logger.info("Card Number length is less than 4: " + cardNumber);
                newOrder.setCardNumber(cardNumber);
            }
            if (order.getPayment() != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                String formatPayment = decimalFormat.format(order.getPayment());
                logger.info("formatPayment" + formatPayment);
                newOrder.setPayment(Double.parseDouble(formatPayment));
            }

            if (order.getApartment() != null && !"".equals(order.getApartment())) {
                logger.info("Appartmnet is: " + order.getApartment());
                newOrder.setShippingAddress(AppUtil.getSafeStr(order.getStreetAddress(), "") + " "
                        + AppUtil.getSafeStr(order.getApartment(), "") + " " + AppUtil.getSafeStr(order.getCity(), "")
                        + ", " + AppUtil.getSafeStr(order.getState(), "") + " "
                        + AppUtil.getSafeStr(order.getZip(), ""));
            } else {
                newOrder.setShippingAddress(AppUtil.getSafeStr(order.getStreetAddress(), "") + " "
                        + AppUtil.getSafeStr(order.getCity(), "") + ", " + AppUtil.getSafeStr(order.getState(), "")
                        + " " + AppUtil.getSafeStr(order.getZip(), ""));
            }
            //PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(order.getPatientProfile().getId());

            if (order.getDeliveryPreference() != null
                    && order.getDeliveryPreference().getId() != null) {
                newOrder.setDeliveryPreferenceId(order.getDeliveryPreference().getId());
                newOrder.setDeliveryPreferencesName(order.getDeliveryPreference().getName());
                String pref = newOrder.getDeliveryPreferencesName();
                String name = AppUtil.getSafeStr(pref, "").toLowerCase();
                if (name.contains("2nd day")) {
                    newOrder.setHandLingFee(0.0d);
                    newOrder.setIncludedStr("Included");
                } else {
                    newOrder.setHandLingFee(order.getHandLingFee());
                    newOrder.setIncludedStr("");
                }
            } else {
                newOrder.setDeliveryPreferencesName("-");
                newOrder.setHandLingFee(0.0d);
                newOrder.setIncludedStr("");
            }

            DrugDetail detail = order.getDrugDetail();
            if (detail != null && detail.getDrugDetailId() != null) {
                if (detail.getDrugBasic() != null) {
                    newOrder.setBrandName(detail.getDrugBasic().getBrandName());
                    if (detail.getDrugBasic().getDrugGeneric() != null) {
                        newOrder.setGenericName(detail.getDrugBasic().getDrugGeneric().getGenericName());
                    }
                }
                newOrder.setImageURL(AppUtil.getSafeStr(detail.getImgUrl(), ""));
                newOrder.setDrugDocURL(AppUtil.getSafeStr(detail.getDrugDocUrl(), ""));
                newOrder.setPatientDocURL(AppUtil.getSafeStr(detail.getPdfDocUrl(), ""));
            }
            newOrder.setComments(AppUtil.getSafeStr(order.getPatientComments(), ""));
            newOrder.setPaymentType(AppUtil.getSafeStr(order.getPaymentType(), ""));
            String source = "";
            if (AppUtil.getSafeStr(order.getVideo(), "").length() > 0) {
                source = "Video";
            } else if (AppUtil.getSafeStr(order.getImagePath(), "").length() > 0) {
                source = "Image";
            }
            newOrder.setSource(source);
            newOrder.setIsCopayCard(order.getAddCopyCard());
            newOrder.setOrderStatusName(order.getOrderStatus() != null ? AppUtil.getSafeStr(order.getOrderStatus().getName(), "") : "");
            if (AppUtil.getSafeStr(newOrder.getOrderStatusName(), "").equalsIgnoreCase("pending")) {
                if (order.getDrugDetail() == null) {
                    newOrder.setOrderStatusName("Pending Verification");
                }
            }

            int refillDone = order.getRefillDone() != null ? order.getRefillDone() : 0;
            String status = order.getOrderStatus() != null ? AppUtil.getSafeStr(order.getOrderStatus().getName(), "") : "";
            int daysDiff = 0;
            int refillValid = 0;
            newOrder.setDaysSupply(order.getDaysSupply() != null ? order.getDaysSupply() : 0);
            if (order.getRefillsAllowed() != null && order.getRefillsAllowed() > 0
                    && order.getRefillsRemaining() != null && order.getRefillsRemaining() > 0 && refillDone == 0
                    && order.getFilledDate() != null
                    && order.getNextRefillDate() != null) {
                Date filledDate = order.getFilledDate();
                Date nextRefillDate = order.getNextRefillDate();
                Date today = new Date();
                int refillValidDays = newOrder.getDaysSupply() * Constants.REFILL_PERCENT / 100;
                Date refillValidDate = DateUtil.addDaysToDate(filledDate, refillValidDays);
                long dayDiff = DateUtil.dateDiffInDays(nextRefillDate, today);
                System.out.println("DAYS DIFF " + dayDiff + " ID " + order.getId());
                if (dayDiff >= 0) {
                    refillValid = 1;
                    newOrder.setDaysToRefill("" + dayDiff);
                    newOrder.setRefillRemainingDaysCount(dayDiff);
                    newOrder.setRefillDone(0);
                } else {
                    newOrder.setRefillDone(1);
                }
//                refillValid = refillValidDate.compareTo(today) <= 0 ? 1 : 0;
//                if (refillValid == 1) {
//
//                    long days = DateUtil.dateDiffInDays(nextRefillDate, today);
//                    newOrder.setRefillRemainingDaysCount(days);
//                }
                newOrder.setFilledValid(refillValid);
            } else {
                newOrder.setRefillDone(1);
            }
            String orderStatusName = newOrder.getOrderStatusName();
//            if (orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy")
//                    || orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("DELIVERY")) {
            newOrder.setRefillRemainingCount(order.getOrderChain() != null ? order.getOrderChain().getRefillRemaing() : 0);
//            } else {
//                newOrder.setRefillRemainingCount(-1);
//            }
            newOrder.setStatusId(order.getOrderStatus() != null ? order.getOrderStatus().getId() : 0);
            newOrder.setAlreadyFilled(refillDone);
            newOrder.setRefill(order.getRefillsRemaining() != null && order.getRefillsRemaining() > 0 //&& !nextRefillDate.before(today)
                    && order.getRefillsAllowed() != null && order.getRefillsAllowed() > 0 && refillDone == 0
                    && (status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("DELIVERY")
                    || status.equalsIgnoreCase("Pickup At Pharmacy") || status.equalsIgnoreCase("Filled")) ? 1 : 0);
            if (AppUtil.getSafeStr(order.getOrderType(), "").equalsIgnoreCase("Refill")) {
                newOrder.setRequestType("Refill Requested");
            } else {
                String requestType = "";
                if (order.getIsBottleAvailable() != null && order.getIsBottleAvailable()) {
                    requestType = "X-FER LABEL SCAN";
                    newOrder.setRequestType(requestType);
                } else {
                    requestType = "X-FER RX SCAN";
                    newOrder.setRequestType(requestType);
                }
            }
            if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
                newOrder.setPrescriptionNumber(order.getOrderChain().getId().toString());
                newOrder.setOrderChainId(order.getOrderChain().getId());
                if (order.getOrderChain().getLastRefillDate() != null) {
                    newOrder.setLastFilledDate(DateUtil.dateToString(order.getOrderChain().getLastRefillDate(), "MM/dd/yyyy"));
                } else {
                    newOrder.setLastFilledDate("");
                }
                Integer refillRemaining = order.getOrderChain().getRefillRemaing();
                if (CommonUtil.isNullOrEmpty(refillRemaining)) {
                    refillRemaining = order.getRefillsRemaining() != null ? order.getRefillsRemaining() : 0;
                }
                newOrder.setRefillsRemaining(refillRemaining + "");
            } else {
                newOrder.setPrescriptionNumber(order.getId());
                newOrder.setLastFilledDate("");
            }
            newOrder.setPatientId(order.getPatientProfile().getId().toString());
            newOrder.setDependentId(order.getPatientProfileMembers() != null ? order.getPatientProfileMembers().getId().toString() : "0");
            Integer paymentId = order.getPaymentId();
            try {
                PatientPaymentInfo patientPaymentInfo = this.patientProfileDAO.getPatientPaymentInfoById(paymentId,
                        order.getPatientProfile() != null ? order.getPatientProfile().getId() : 0);
                if (patientPaymentInfo != null) {
                    newOrder.setCardType(EncryptionHandlerUtil.getDecryptedString(patientPaymentInfo.getCardType()));
                    newOrder.setCardNumber(EncryptionHandlerUtil.getDecryptedString(patientPaymentInfo.getCardNumber()));
                } else {
                    newOrder.setCardType("");
                    newOrder.setCardNumber("");
                }
                newOrder.setIsUpdateMfr(CommonUtil.isEditableOrder(AppUtil.getSafeStr(newOrder.getOrderStatusName(), "")));
                newOrder.setIsBottleAvailable(order.getIsBottleAvailable() == null ? Boolean.FALSE : order.getIsBottleAvailable());
            } catch (Exception e) {
                logger.error("Exception:: setOrderList", e);
            }
            newOrder.setPharmacyPhone(AppUtil.getSafeStr(order.getPharmacyPhone(), ""));
            if (AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "").length() > 0) {
                newOrder.setRxNumber(AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), ""));
            } else {
                newOrder.setRxNumber("");
            }
            newOrder.setOrigRx(order.getOldRxNumber());
            newOrder.setPharmacyName(order.getPharmacyName());
            //            if (order.getRxNumber() != null) {
//                newOrder.setRxNumber(AppUtil.getSafeStr(order.getRxNumber(), ""));
//            } else {
//                newOrder.setRxNumber(AppUtil.getSafeStr(order.getOldRxNumber(), ""));
//            }
            if (order.getOrderChain() != null && order.getOrderChain().getNextRefillDate() != null) {
                newOrder.setNextRefillDate(order.getOrderChain().getNextRefillDate());
            }
            if (order.getOrderChain() != null && order.getOrderChain().getRxExpiredDate() != null) {
                newOrder.setRxRxpiredDate(order.getOrderChain().getRxExpiredDate());
            }
            if (AppUtil.getSafeStr(primaryOrderId, "").length() > 0) {
                if (order.getId().equals(primaryOrderId)) {
                    newOrder.setPrimaryOrder(1);
                } else {
                    newOrder.setPrimaryOrder(0);
                }
            }
            if (order.getDrugDetail() != null) {
                newOrder.setDrugId(order.getDrugDetail().getDrugDetailId() != null
                        ? order.getDrugDetail().getDrugDetailId() : 0L);
            }
            newOrder.setPrescriberName(AppUtil.getSafeStr(order.getPrescriberLastName(), ""));
            newOrder.setPrescriberPhone(AppUtil.getSafeStr(order.getPrescriberPhone(), ""));
        } catch (Exception e) {
            logger.error("Exception# populateRefillRxData# ", e);
        }
        return newOrder;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void getOrderRewardDetail(Integer profileId, OrderDetailDTO newOrder) throws Exception {
        OrderDetailDTO quickStats = getQuickStatsWs(profileId);
        newOrder.setOpenOrdersCuont(quickStats.getTotalOpenOrders());
        newOrder.setRxShippedCount(quickStats.getTotalRxShipped());
        newOrder.setProcessingOrdersCount(quickStats.getProcessingOrdersCount());
        newOrder.setShippingOrdersCount(quickStats.getShippingOrdersCount());
        if (quickStats.getSavingsToDate() != null) {
            newOrder.setTotalSavings(quickStats.getSavingsToDate());
        } else {
            newOrder.setTotalSavings(0.00);
        }
        RewardPoints rewardPoints = getMyRewardsPoints(profileId);
        newOrder.setLifetimeRewardPoints(rewardPoints.getLifeTimePoints());
        Long totalMinusPoints = patientProfileDAO.getTotalRewardHistoryPointByType("MINUS", profileId);
        if (totalMinusPoints != null) {
            newOrder.setAvailedRewardPoints(rewardPoints.getAvailablePoints());
        } else {
            newOrder.setAvailedRewardPoints(0L);
        }
        newOrder.setAvailableRewardPoints(rewardPoints.getAvailablePoints());
    }

    public OrderDetailDTO getNoInsurancesWs(String memberId, Integer profileId) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        try {
            String disValue = "";
            FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getObjectById(new FeeSettings(), Constants.FEE_SETTING_ID);
            if (feeSettings != null && feeSettings.getId() != null) {
                orderDetailDTO.setLocalDisCount(feeSettings.getFee().intValueExact());
                Integer discountValue = feeSettings.getFee().intValueExact();
                disValue = discountValue.toString();
            }
            logger.info("Discount value is: " + disValue);
            logger.info("Member Id is: " + memberId);
            orderDetailDTO.setMemberId(memberId);

            String webappRoot = servletContext.getRealPath("/");
            String relativeFolder = File.separator + "resources" + File.separator
                    + "noinsurancecard" + File.separator;
            String filename = Constants.INSURANCE_CARD_PATH + "NoInsuranceCard" + profileId + ".png";
            String noInsuranceCardPath = Constants.INSURANCE_CARD_URL + "NoInsuranceCard" + profileId + ".png";
            setDiscountValueOnCard(disValue, filename);
            setMemberIdOnCard(filename, memberId);
            orderDetailDTO.setNoInsuranceCard(noInsuranceCardPath);
        } catch (Exception e) {
            logger.error("Exception -> getNoInsurancesWs", e);
        }
        return orderDetailDTO;
    }

    private void setDiscountValueOnCard(String disValue, String noInsuranceCardPath) throws IOException {
        final BufferedImage image = ImageIO.read(new File(Constants.NO_INSURANCE_CARD_PATH + "/" + "Card_01.png"));
        Graphics g = image.getGraphics();
        g.setFont(new Font("Serif", Font.BOLD, 118));
        g.drawString(disValue.trim(), 200, 117);
        g.dispose();
        ImageIO.write(image, "png", new File(noInsuranceCardPath));
        String output = CommonUtil.executeCommand(Constants.COMMAND);
        logger.info("Command Result:: " + output);
    }

    private void setMemberIdOnCard(String noInsuranceCardPath, String memberId) throws IOException {
        final BufferedImage image1 = ImageIO.read(new File(noInsuranceCardPath));
        Graphics2D g1 = image1.createGraphics();
        g1.setPaint(Color.BLACK);
        g1.setFont(g1.getFont().deriveFont(18f));
        g1.drawString(memberId, 587, 350);
        g1.dispose();
        ImageIO.write(image1, "png", new File(noInsuranceCardPath));
        String output = CommonUtil.executeCommand(Constants.COMMAND);
        logger.info("Command Result:: " + output);
    }

    public String getPatientProfileHealth(Integer id) {
        String json = "";
        try {
            PatientProfileHealth patientProfileHealth = patientProfileDAO.getPatientProfileHealthByPatientId(id);
            if (patientProfileHealth != null && patientProfileHealth.getId() != null) {
                PatientProfileHealth newProfileHealth = new PatientProfileHealth();
                newProfileHealth.setId(patientProfileHealth.getId());
                newProfileHealth.setHeightFeet(patientProfileHealth.getHeightFeet());
                newProfileHealth.setHeightInch(patientProfileHealth.getHeightInch());
                newProfileHealth.setWeight(patientProfileHealth.getWeight());
                newProfileHealth.setBmi(patientProfileHealth.getBmi());
                ObjectMapper objectMapper = new ObjectMapper();
                json = objectMapper.writeValueAsString(newProfileHealth);
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileHealth", e);
        }
        return json;
    }

    public boolean savePatientProfileHealth(PatientProfileHealth patientProfileHealth, Integer currentUserId) {
        boolean saved = false;
        try {
            PatientProfileHealth oldPatientProfileHealth = patientProfileDAO.getPatientProfileHealthByPatientId(patientProfileHealth.getPatientId());
            if (oldPatientProfileHealth != null && oldPatientProfileHealth.getId() != null) {
                patientProfileHealth.setCreatedBy(oldPatientProfileHealth.getCreatedBy());
                patientProfileHealth.setCreatedOn(oldPatientProfileHealth.getCreatedOn());
            } else {
                patientProfileHealth.setCreatedBy(currentUserId);
                patientProfileHealth.setCreatedOn(new Date());
            }
            patientProfileHealth.setPatientProfile(new PatientProfile(patientProfileHealth.getPatientId()));
            patientProfileHealth.setUpdatedBy(currentUserId);
            patientProfileHealth.setUpdatedOn(new Date());
            patientProfileDAO.merge(patientProfileHealth);
            saved = true;
        } catch (Exception e) {
            saved = false;
            logger.error("Exception -> savePatientProfileHealth", e);
        }
        return saved;
    }

    public boolean savePatientProfileHealth(PatientProfileHealth patientProfileHealth) {
        boolean saved = false;
        try {
            PatientProfileHealth oldPatientProfileHealth = patientProfileDAO.getPatientProfileHealthByPatientId(patientProfileHealth.getPatientId());
            if (oldPatientProfileHealth != null && oldPatientProfileHealth.getId() != null) {
                patientProfileHealth.setCreatedBy(oldPatientProfileHealth.getCreatedBy());
                patientProfileHealth.setCreatedOn(oldPatientProfileHealth.getCreatedOn());
            } else {
                //patientProfileHealth.setCreatedBy(currentUserId);
                patientProfileHealth.setCreatedOn(new Date());
            }
            patientProfileHealth.setPatientProfile(new PatientProfile(patientProfileHealth.getPatientId()));
            //patientProfileHealth.setUpdatedBy(currentUserId);
            patientProfileHealth.setUpdatedOn(new Date());
            patientProfileDAO.merge(patientProfileHealth);
            saved = true;
        } catch (Exception e) {
            saved = false;
            logger.error("Exception -> savePatientProfileHealth", e);
        }
        return saved;
    }

    public boolean updatePatientAddress(PatientAddress patientAddress, Integer currentUserId) {
        boolean saved = false;
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfile(patientAddress.getProfileId());
            patientProfileDAO.saveOrUpdate(patientProfile);
            saved = true;
        } catch (Exception e) {
            saved = false;
            logger.error("Exception -> updatePatientAddress", e);
        }
        return saved;
    }

    public boolean dependentHasOrders(Integer membeId) throws Exception {
        BusinessObject obj = new BusinessObject("patientProfileMembers.id", membeId, Constants.HIBERNATE_EQ_OPERATOR);
        List objLst = new ArrayList();
        objLst.add(obj);
        List lst = this.patientProfileDAO.findByNestedProperty(new Order(), objLst, "", 0);
        return lst != null && lst.size() > 0;
    }

    public boolean deletePatientDependentWs(Integer profileId, Integer memberId) throws Exception {
        boolean deleted = false;
//        try {
        //deleted = patientProfileDAO.deletePatientDependent(profileId, memberId);
        PatientProfileMembers members = (PatientProfileMembers) this.patientProfileDAO.findRecordById(
                new PatientProfileMembers(), memberId);

        if (members != null) {
            members.setArchived(1);
            this.patientProfileDAO.saveOrUpdate(members);
            logger.info("Delete Patient Dependent: " + deleted);
            deleted = true;
        }
//        } catch (Exception e) {
//            logger.error("Exception -> deletePatientDependentWs", e);
//        }
        return deleted;
    }

    public PatientDeliveryAddress savePatientDeliveryAddress(Integer profileId, String address, String apartment, String description, String city, Integer stateId, String zip, String addressType, String defaultAddress, Integer dprefId) {
        PatientDeliveryAddress patientDeliveryAddress = new PatientDeliveryAddress();
        try {
            updatePreviousDefaultAddress(profileId, defaultAddress);
            setDeliveryAddress(patientDeliveryAddress, profileId, address, apartment, description, city, stateId, zip, addressType, defaultAddress, dprefId);
            patientDeliveryAddress.setCreatedOn(new Date());
            patientDeliveryAddress.setUpdatedOn(new Date());
            patientProfileDAO.save(patientDeliveryAddress);
        } catch (Exception e) {
            logger.error("Exception -> savePatientDeliveryAddress", e);
        }
        return patientDeliveryAddress;
    }

    private void updatePreviousDefaultAddress(Integer profileId, String defaultAddress) throws Exception {
        logger.info("updatePreviousDefaultAddress: " + profileId);
        if (defaultAddress.equalsIgnoreCase(Constants.YES)) {
            patientProfileDAO.updatePreviousDefaultAddress(profileId, Constants.NO);
        }
    }

    private void setDeliveryAddress(PatientDeliveryAddress patientDeliveryAddress, Integer profileId, String address, String apartment, String description, String city, Integer stateId, String zip, String addressType, String defaultAddress, Integer dprefId) {
        patientDeliveryAddress.setPatientProfile(new PatientProfile(profileId));
        patientDeliveryAddress.setAddress(address);
        patientDeliveryAddress.setApartment(apartment);
        patientDeliveryAddress.setDescription(description);
        patientDeliveryAddress.setCity(city);
        patientDeliveryAddress.setState(new State(stateId));
        patientDeliveryAddress.setZip(zip);
        patientDeliveryAddress.setAddressType(addressType);
        if (defaultAddress == null || defaultAddress.isEmpty()) {
            defaultAddress = Constants.NO;
        }
        patientDeliveryAddress.setDefaultAddress(defaultAddress);
        if (dprefId != null) {
            patientDeliveryAddress.setDeliveryPreferences(new DeliveryPreferences(dprefId));
        }
    }

    public List<PatientDeliveryAddressDTO> getPatientDeliveryAddressesByProfileId(Integer profileId) {
        List<PatientDeliveryAddressDTO> list = new ArrayList<>();
        try {
            for (PatientDeliveryAddress patientDeliveryAddress : patientProfileDAO.getPatientDeliveryAddressesByProfileId(profileId)) {
                PatientDeliveryAddressDTO patientDeliveryAddressDTO = setPatientDeliveryAddress(patientDeliveryAddress);
                patientDeliveryAddressDTO.setTotalAddress(patientProfileDAO.getPatientDeliveryAddressesByProfileId(profileId).size());
                list.add(patientDeliveryAddressDTO);
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientDeliveryAddressesByProfileId", e);
        }
        return list;
    }

    public PatientDeliveryAddressDTO getPatientDeliveryAddressById(Integer profileId, Integer id) {
        PatientDeliveryAddressDTO patientDeliveryAddressDTO = new PatientDeliveryAddressDTO();
        try {
            PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getPatientDeliveryAddressById(profileId, id);
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null) {
                patientDeliveryAddressDTO = setPatientDeliveryAddress(patientDeliveryAddress);
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientDeliveryAddressById", e);
        }
        return patientDeliveryAddressDTO;
    }

    private PatientDeliveryAddressDTO setPatientDeliveryAddress(PatientDeliveryAddress patientDeliveryAddress) {
        PatientDeliveryAddressDTO patientDeliveryAddressDTO = new PatientDeliveryAddressDTO();
        patientDeliveryAddressDTO.setId(patientDeliveryAddress.getId());
        if (patientDeliveryAddress.getPatientProfile() != null && patientDeliveryAddress.getPatientProfile().getId() != null) {
            patientDeliveryAddressDTO.setProfileId(patientDeliveryAddress.getPatientProfile().getId());
        }
        if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
            patientDeliveryAddressDTO.setStateId(patientDeliveryAddress.getState().getId());
        }
        patientDeliveryAddressDTO.setAddress(patientDeliveryAddress.getAddress());
        patientDeliveryAddressDTO.setApartment(patientDeliveryAddress.getApartment());
        patientDeliveryAddressDTO.setAddressType(patientDeliveryAddress.getAddressType());
        patientDeliveryAddressDTO.setCity(patientDeliveryAddress.getCity());
        patientDeliveryAddressDTO.setZip(patientDeliveryAddress.getZip());
        patientDeliveryAddressDTO.setDescription(patientDeliveryAddress.getDescription());
        patientDeliveryAddressDTO.setDefaultAddress(patientDeliveryAddress.getDefaultAddress());
        if (patientDeliveryAddress.getDeliveryPreferences() != null && patientDeliveryAddress.getDeliveryPreferences().getId() != null) {
            patientDeliveryAddressDTO.setDprefaId(patientDeliveryAddress.getDeliveryPreferences().getId());
        }
        patientDeliveryAddressDTO.setAddressDescription(patientDeliveryAddress.getDescription());
        return patientDeliveryAddressDTO;
    }

    public boolean updateBillingAddress(String token, String address, String appt, String city, String stateId, String zip) {
        boolean isUpdate = false;
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null && patientProfile.getId() != null) {
                PatientAddress patientAddress = new PatientAddress();
                if (patientProfile.getBillingAddress() != null && patientProfile.getBillingAddress().getId() != null) {
                    logger.info("Billing address id is: " + patientProfile.getBillingAddress().getId());
                    patientAddress = patientProfileDAO.getPatientAddressById(patientProfile.getBillingAddress().getId());
                } else {
                    patientAddress.setCreatedOn(new Date());
                    patientAddress.setUpdatedOn(new Date());
                }
                patientAddress.setAddress(address);
                patientAddress.setApartment(appt);
                patientAddress.setCity(city);
                patientAddress.setStateId(Short.parseShort(stateId));
                patientAddress.setZip(zip);
                patientProfileDAO.saveOrUpdate(patientAddress);
                if (patientProfile.getBillingAddress() == null) {
                    logger.info("Update Billing Address:: " + patientProfile.getId());
                    patientProfile.setBillingAddress(patientAddress);
                    patientProfileDAO.update(patientProfile);
                }
                isUpdate = true;
            } else {
                logger.info("In valid token: " + token);
                isUpdate = false;
            }
        } catch (Exception e) {
            logger.error("Exception -> updateBillingAddress", e);
        }
        return isUpdate;
    }

    public PatientDeliveryAddress getPatientDeliveryDefaultAddress(Integer profileId) {
        PatientDeliveryAddress patientDeliveryAddress = new PatientDeliveryAddress();
        try {
            patientDeliveryAddress = patientProfileDAO.getDefaultPatientDeliveryAddress(profileId);
        } catch (Exception e) {
            logger.error("Exception -> getPatientDeliveryDefaultAddress", e);
        }
        return patientDeliveryAddress;
    }

    public List<PatientPaymentInfo> getPatientPaymentInfoListByProfileId(Integer profileId) {
        List<PatientPaymentInfo> list = new ArrayList<>();
        try {
            for (PatientPaymentInfo patientPaymentInfo : patientProfileDAO.getPatientPaymentInfoListByProfileId(profileId)) {
                PatientPaymentInfo info = setPatientPaymentInfo(patientPaymentInfo);
                list.add(info);
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientPaymentInfoListByProfileId", e);
        }
        return list;
    }

    private PatientPaymentInfo setPatientPaymentInfo(PatientPaymentInfo patientPaymentInfo) {
        PatientPaymentInfo info = new PatientPaymentInfo();
        info.setId(patientPaymentInfo.getId());
        info.setCardHolderName(patientPaymentInfo.getCardHolderName());
        info.setCardType(patientPaymentInfo.getCardType());
        info.setCardNumber(patientPaymentInfo.getCardNumber());
        info.setExpiryDate(patientPaymentInfo.getExpiryDate());
        info.setCvvNumber(patientPaymentInfo.getCvvNumber());
        info.setDefaultCard(patientPaymentInfo.getDefaultCard());
        if (patientPaymentInfo.getBillingAddress() != null && patientPaymentInfo.getBillingAddress().getId() != null) {
            if (CommonUtil.isNotEmpty(patientPaymentInfo.getBillingAddress().getAddress())) {
                info.setAddress(patientPaymentInfo.getBillingAddress().getAddress());
            } else {
                info.setAddress("");
            }
            if (CommonUtil.isNotEmpty(patientPaymentInfo.getBillingAddress().getApartment())) {
                info.setApartment(patientPaymentInfo.getBillingAddress().getApartment());
            } else {
                info.setApartment("");
            }
            info.setStateId(patientPaymentInfo.getBillingAddress().getStateId());
            if (CommonUtil.isNotEmpty(patientPaymentInfo.getBillingAddress().getCity())) {
                info.setCity(patientPaymentInfo.getBillingAddress().getCity());
            } else {
                info.setCity("");
            }
            if (CommonUtil.isNotEmpty(patientPaymentInfo.getBillingAddress().getZip())) {
                info.setZip(patientPaymentInfo.getBillingAddress().getZip());
            } else {
                info.setZip("");
            }
            if (patientPaymentInfo.getBillingAddress().getStateId() != null) {
                State state = (State) patientProfileDAO.getObjectById(new State(), patientPaymentInfo.getBillingAddress().getStateId());
                info.setState(state.getAbbr());
            }
            info.setBillingAddressId(patientPaymentInfo.getBillingAddress().getId());
        }
        return info;
    }

    public PatientPaymentInfo getPatientPaymentInfoDefaultCardByProfileId(Integer profileId, String defaultCard) {
        PatientPaymentInfo patientPaymentInfo = new PatientPaymentInfo();
        try {
            patientPaymentInfo = patientProfileDAO.getPatientPaymentInfoDefaultCardByProfileId(profileId, defaultCard);
        } catch (Exception e) {
            logger.error("Exception -> getPatientPaymentInfoDefaultCardByProfileId ", e);
        }
        return patientPaymentInfo;
    }

    public PatientPaymentInfo getPatientPaymentInfoByCardId(String cardId) {
        PatientPaymentInfo patientPaymentInfo = new PatientPaymentInfo();
        try {
            if (CommonUtil.isNotEmpty(cardId)) {
                patientPaymentInfo = (PatientPaymentInfo) patientProfileDAO.getObjectById(new PatientPaymentInfo(), AppUtil.getSafeInt(cardId, 0));
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientPaymentInfoDefaultCardByProfileId ", e);
        }
        return patientPaymentInfo;
    }

    public Order getOrderById(String orderId) {
        Order orderInfo = new Order();
        try {
            orderInfo = (Order) patientProfileDAO.findRecordById(new Order(), orderId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getOrderById ", e);
        }
        return orderInfo;
    }

    public String getOrderStatusName(String orderId) {
        Order order = getOrderById(orderId);
        String name = "";
        try {
            OrderStatus orderStatus = order.getOrderStatus();
            if (orderStatus != null && !CommonUtil.isNullOrEmpty(orderStatus.getId())) {
                name = orderStatus.getName();
            }

        } catch (Exception e) {
            logger.error("Exception -> getOrderStatusName ", e);
        }
        return name;
    }

    public OrderStatusDTO getOrderStatusDTO(String orderId) {
        Order order = getOrderById(orderId);
        OrderStatusDTO dto = new OrderStatusDTO();

        try {
            OrderStatus orderStatus = order.getOrderStatus();
            if (orderStatus != null && !CommonUtil.isNullOrEmpty(orderStatus.getId())) {
                dto.setId(orderStatus.getId());
                dto.setName(orderStatus.getName());
            }

        } catch (Exception e) {
            logger.error("Exception -> getOrderStatusDTO ", e);
        }
        return dto;
    }

    public PatientPaymentInfo getPatientPaymentInfo(Integer id, Integer profileId) {
        PatientPaymentInfo patientPaymentInfo = new PatientPaymentInfo();
        try {
            logger.info("Payment Id:: " + id);
            PatientPaymentInfo dbPatientPaymentInfo = patientProfileDAO.getPatientPaymentInfoById(id, profileId);
            patientPaymentInfo = setPatientPaymentInfo(dbPatientPaymentInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientPaymentInfo ", e);
        }
        return patientPaymentInfo;
    }

    public List<DeliveryDistanceFeeDTO> getDistanceFeeDTO(String zipCode, Integer profileId) {
        List<DeliveryDistanceFeeDTO> dDFTlist = new ArrayList<>();
        try {
            List<PharmacyZipCodes> list = patientProfileDAO.getPharmacyZipCodesList();
            if (list.size() > 0) {
                PharmacyZipCodes pharmacyZipCodes = list.get(0);
                if (pharmacyZipCodes != null && pharmacyZipCodes.getId() != null) {
                    DeliveryDistanceFeeDTO deliveryDistanceFeeDTO = new DeliveryDistanceFeeDTO();
                    logger.info("PharmacyZipCodes: " + pharmacyZipCodes.getPharmacyZip() + " Patient Zip Code: " + zipCode);
                    String sURL = Constants.ZIP_CODE_API_URL + pharmacyZipCodes.getPharmacyZip() + "/" + zipCode + "/mile";
                    logger.info("URL: " + sURL);
                    URL url = new URL(sURL);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    if (http != null) {
                        if (http.getResponseCode() == 400 || http.getResponseCode() == 429) {
                            logger.info("Response Code is: " + http.getResponseCode() + " Message: " + http.getResponseMessage());
                            deliveryDistanceFeeDTO.setErrorMessage("Invalid zip code or Usage limit exceeded.");
                            deliveryDistanceFeeDTO.setError_code(http.getResponseCode());
                            dDFTlist.add(deliveryDistanceFeeDTO);
                            return dDFTlist;
                        } else if (http.getResponseCode() == 404) {
                            logger.info("Response Code is: " + http.getResponseCode() + " Message: " + http.getResponseMessage());
                            deliveryDistanceFeeDTO.setErrorMessage("Zip code " + zipCode + " not found.");
                            deliveryDistanceFeeDTO.setError_code(http.getResponseCode());
                            dDFTlist.add(deliveryDistanceFeeDTO);
                            return dDFTlist;
                        }
                    }
                    String genreJson = IOUtils.toString(url.openStream());
                    deliveryDistanceFeeDTO = new Gson().fromJson(genreJson, DeliveryDistanceFeeDTO.class);
                    setDeliveryDistanceFee(deliveryDistanceFeeDTO, pharmacyZipCodes, zipCode, dDFTlist);

                    //save calculation in a table
                    saveZipCodeCalculation(dDFTlist, profileId);
                    ////////////////////////////////////////////////
                    Comparator<DeliveryDistanceFeeDTO> byName
                            = (DeliveryDistanceFeeDTO o1, DeliveryDistanceFeeDTO o2) -> Integer.compare(o1.getSeqNo(), o2.getSeqNo());

                    Collections.sort(dDFTlist, byName);
                    ////////////////////////////////////////////////
                }
            }
        } catch (Exception e) {
            logger.error("Exception -> getDistanceFeeDTO ", e);
        }
        return dDFTlist;
    }

    /////////////////////////////////////////////////////////////////////////////////
    public DeliveryDistanceFeeDTO validateDistanceFeeDTO(String zipCode, Integer profileId) {

        DeliveryDistanceFeeDTO deliveryDistanceFeeDTO = new DeliveryDistanceFeeDTO();
        try {
            List<PharmacyZipCodes> list = patientProfileDAO.getPharmacyZipCodesList();

            ////////////////////////////////////////////////////////////////////////////////
            if (list.size() > 0) {
                PharmacyZipCodes pharmacyZipCodes = list.get(0);
                if (pharmacyZipCodes != null && pharmacyZipCodes.getId() != null) {

                    logger.info("PharmacyZipCodes: " + pharmacyZipCodes.getPharmacyZip() + " Patient Zip Code: " + zipCode);
                    String sURL = Constants.ZIP_CODE_API_URL + pharmacyZipCodes.getPharmacyZip() + "/" + zipCode + "/mile";
                    logger.info("URL: " + sURL);
                    URL url = new URL(sURL);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    if (http != null) {
                        System.out.println("Response Code is: " + http.getResponseCode() + " Message: " + http.getResponseMessage());
                        if (http.getResponseCode() == 400 || http.getResponseCode() == 429) {
                            logger.info("Response Code is: " + http.getResponseCode() + " Message: " + http.getResponseMessage());
                            deliveryDistanceFeeDTO.setErrorMessage("Invalid zip code or Usage limit exceeded.");
                            deliveryDistanceFeeDTO.setError_code(http.getResponseCode());
                            return deliveryDistanceFeeDTO;
                        } else if (http.getResponseCode() == 404) {
                            logger.info("Response Code is: " + http.getResponseCode() + " Message: " + http.getResponseMessage());
                            deliveryDistanceFeeDTO.setErrorMessage("Zip code " + zipCode + " not found.");
                            deliveryDistanceFeeDTO.setError_code(http.getResponseCode());
                            return deliveryDistanceFeeDTO;
                        }
                    }
                    String genreJson = IOUtils.toString(url.openStream());
                    deliveryDistanceFeeDTO = new Gson().fromJson(genreJson, DeliveryDistanceFeeDTO.class);
//                    setDeliveryDistanceFee(deliveryDistanceFeeDTO, pharmacyZipCodes, zipCode, dDFTlist);
                    return deliveryDistanceFeeDTO;
                }
            }

            ////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            logger.error("Exception -> getDistanceFeeDTO ", e);
            e.printStackTrace();

        }
        return deliveryDistanceFeeDTO;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void setDeliveryDistanceFee(DeliveryDistanceFeeDTO deliveryDistanceFeeDTO, PharmacyZipCodes pharmacyZipCodes, String zipCode, List<DeliveryDistanceFeeDTO> dDFTlist) {
        if (deliveryDistanceFeeDTO != null && deliveryDistanceFeeDTO.getDistance() != null) {
            Long distance = Math.round(deliveryDistanceFeeDTO.getDistance());
            logger.info("Distance from api: " + deliveryDistanceFeeDTO.getDistance() + " After round zipcode distance value: " + distance);
            for (DeliveryDistanceFee deliveryDistanceFee : pharmacyZipCodes.getDeliveryDistanceFeesList()) {
                DeliveryDistanceFeeDTO dTO = new DeliveryDistanceFeeDTO();
                if (deliveryDistanceFee.getDeliveryDistances() != null && deliveryDistanceFee.getDeliveryDistances().getId() != null) {

                    if (distance >= deliveryDistanceFee.getDeliveryDistances().getDistanceFrom() && distance <= deliveryDistanceFee.getDeliveryDistances().getDistanceTo()) {
                        dTO.setMiles(distance);
                        dTO.setDeliveryFee(deliveryDistanceFee.getDeliveryFee());
                        if (deliveryDistanceFee.getDeliveryPreferenceses() != null) {
                            dTO.setDprefaId(deliveryDistanceFee.getDeliveryPreferenceses().getId());
                            dTO.setName(deliveryDistanceFee.getDeliveryPreferenceses().getName());
                            if (AppUtil.getSafeStr(dTO.getName(), "").contains("2nd Day")) {
                                dTO.setDeliveryFee(BigDecimal.ZERO);
                                dTO.setIncludedStr("Included");
                            } else {
                                dTO.setIncludedStr("");
                            }
                            dTO.setDescription(deliveryDistanceFee.getDeliveryPreferenceses().getDescription());
                            dTO.setSeqNo(deliveryDistanceFee.getDeliveryPreferenceses().getSeqNo());
                            logger.info("Name: " + deliveryDistanceFee.getDeliveryPreferenceses().getName());
                        }
                        dTO.setZip(zipCode);
                        if (!checkDuplicateRecordInList(dDFTlist, deliveryDistanceFee.getDeliveryPreferenceses().getId())) {
                            dDFTlist.add(dTO);
                        }
                    } else {
                        logger.info("Distance From: " + deliveryDistanceFee.getDeliveryDistances().getDistanceFrom() + " Distance To: " + deliveryDistanceFee.getDeliveryDistances().getDistanceTo());
                        logger.info("Delivery distance does not match with zip code distance");
                    }
                } else {
                    logger.info("DeliveryDistanceFee is null");
                }
            }
        } else {
            logger.info("Error Msg is: ");
            dDFTlist.add(deliveryDistanceFeeDTO);
        }
    }

    public void saveZipCodeCalculation(List<DeliveryDistanceFeeDTO> list, Integer patientId) {
        try {
            for (DeliveryDistanceFeeDTO dTO : list) {
                ZipCodeCalculation zipCodeCalculation = new ZipCodeCalculation();
                zipCodeCalculation.setPatientId(patientId);
                zipCodeCalculation.setMiles(dTO.getMiles());
                zipCodeCalculation.setDeliveryFee(dTO.getDeliveryFee());
                zipCodeCalculation.setDeliveryPreferencesId(dTO.getDprefaId());
                zipCodeCalculation.setCreatedOn(new Date());
                zipCodeCalculation.setZip(dTO.getZip());
                patientProfileDAO.save(zipCodeCalculation);
            }

        } catch (Exception e) {
            logger.error("Exception -> saveZipCodeCalculation ", e);
        }

    }

    public void updateDeliveryPreferencesByProfileId(Integer profileId, Integer dprefId, String status, String deliveryFee, String distance) {
        try {
            logger.info("Update Delivery Preferences By ProfileId " + profileId + " Delivery Preferences id: " + dprefId + " status: " + status);
            patientProfileDAO.updateDeliveryPreferencesByProfileId(profileId, dprefId, status, deliveryFee, distance);
        } catch (Exception e) {
            logger.error("Exception -> updateDeliveryPreferencesByProfileId ", e);
        }
    }

    public boolean updateTransferRequest(Integer profileId, Integer transferRxId, Integer devliveryAddressId, Integer paymentId, Integer dprefId,
            String zip, String miles, String deliveryFee, Long orderChainId) {
        boolean update = false;
        try {
            update = patientProfileDAO.updateTransferRequest(profileId, transferRxId, devliveryAddressId, paymentId, dprefId, zip, miles, deliveryFee);
            RewardPoints rewardPoints = patientProfileDAO.getRewardPoints(Constants.RX_TRANSFER_Id);
            if (rewardPoints != null && rewardPoints.getId() != null) {
                logger.info("Save Rx Transfer Reward Point.");
                OrderChain orderChain = (OrderChain) this.patientProfileDAO.findRecordById(new OrderChain(), orderChainId);
                if (orderChain != null) {
                    if (orderChain.getOrderList() != null && orderChain.getOrderList().size() > 0) {
                        Order order = orderChain.getOrderList().get(0);
                        saveRewardHistory(Constants.TRANSFERRX, rewardPoints.getPoint().intValueExact(), profileId, Constants.PLUS, order);
                    }

                }
            }
            //saveRxTransferOrder(transferRxId, profileId, devliveryAddressId, paymentId);
        } catch (Exception e) {
            logger.error("Exception -> updateTransferRequest ", e);
        }
        return update;
    }

    ////////////////////////////////////////////////////////
    public Order updateTransferOrder(Integer profileId, Integer transferRxId, Integer devliveryAddressId, Integer paymentId, Integer dprefId,
            String zip, String miles, String deliveryFee, Long orderChainId, String comments, String paymentType, Boolean addCopyCard, Long[] coPayCardIdList,
            Integer dPrefId, String copayIds, Boolean isBottleAvailable) {
        boolean update = false;
        Order order = new Order();
        try {
            if (!CommonUtil.isNullOrEmpty(paymentId)) {
                update = patientProfileDAO.updateTransferRequest(profileId, transferRxId, devliveryAddressId, paymentId, dprefId, zip, miles, deliveryFee);
            }

            RewardPoints rewardPoints = patientProfileDAO.getRewardPoints(Constants.RX_TRANSFER_Id);
            if (rewardPoints != null && rewardPoints.getId() != null) {
                logger.info("Save Rx Transfer Reward Point.");
                PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), devliveryAddressId);
                OrderChain orderChain = (OrderChain) this.patientProfileDAO.findRecordById(new OrderChain(), orderChainId);

                if (orderChain != null) {
                    if (orderChain.getOrderList() != null && orderChain.getOrderList().size() > 0) {
                        order = orderChain.getOrderList().get(0);
                        order.setPatientComments(comments);
                        order.setPaymentType(paymentType);
                        order.setFinalPaymentMode(paymentType);
                        order.setAddCopyCard(addCopyCard);
                        order.setPaymentId(paymentId);
                        order.setNextRefillFlag("0");
                        order.setIsBottleAvailable(isBottleAvailable);
                        DeliveryPreferences deliveryPreference = (DeliveryPreferences) this.patientProfileDAO.findRecordById(new DeliveryPreferences(), dPrefId);
                        if (deliveryPreference != null && AppUtil.getSafeStr(deliveryPreference.getName(), "").contains("2nd Day")) {
                            deliveryFee = "0.0";
                        }
                        order.setDeliveryPreference(deliveryPreference);
                        order.setHandLingFee(AppUtil.getSafeDouble(deliveryFee, 0d));
                        order.setMiles(miles);
                        //////////////////////////////////////////////////
                        if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                            logger.info("Set shipping address info. ");
                            order.setStreetAddress(patientDeliveryAddress.getAddress());
                            order.setCity(patientDeliveryAddress.getCity());
                            order.setZip(patientDeliveryAddress.getZip());
                            order.setAddressLine2(patientDeliveryAddress.getDescription());
                            order.setApartment(patientDeliveryAddress.getApartment());
                            if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                                order.setState(patientDeliveryAddress.getState().getName());
                            }
                        }
                        /////////////////////////////////////////////////
                        this.patientProfileDAO.saveOrUpdate(order);
                        saveRewardHistory(Constants.TRANSFERRX, rewardPoints.getPoint().intValueExact(), profileId, Constants.PLUS, order);
                        if (addCopyCard) {
                            String[] copayIdArr = copayIds.split(",");
                            for (int i = 0; copayIdArr != null && i < copayIdArr.length; i++) {
                                CoPayCardDetails coPayCardDetails = new CoPayCardDetails();
                                coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), AppUtil.getSafeLong(copayIdArr[i], 0L));
                                coPayCardDetails.setOrder(order);
                                patientProfileDAO.update(coPayCardDetails);
                            }
//                            CoPayCardDetails coPayCardDetails = new CoPayCardDetails();
//                            for (Long coPayCardId : coPayCardIdList) {
//                                coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), coPayCardId);
//                                coPayCardDetails.setOrder(order);
//                                patientProfileDAO.update(coPayCardDetails);
//                            }
                        }
                    }

                }
            }
            //saveRxTransferOrder(transferRxId, profileId, devliveryAdd`ressId, paymentId);
        } catch (Exception e) {
            logger.error("Exception -> updateTransferRequest ", e);
        }
        return order;
    }
    ///////////////////////////////////////////////////////

    public boolean updateTransferRequest(Integer profileId, Integer transferRxId, Integer devliveryAddressId, Integer paymentId, Integer dprefId,
            String zip, String miles, String deliveryFee, Long orderChainId, String comments, String paymentType, Order order) {
        boolean update = false;
        try {
            update = patientProfileDAO.updateTransferRequest(profileId, transferRxId, devliveryAddressId, paymentId, dprefId, zip, miles, deliveryFee);
            RewardPoints rewardPoints = patientProfileDAO.getRewardPoints(Constants.RX_TRANSFER_Id);
            if (rewardPoints != null && rewardPoints.getId() != null) {
                logger.info("Save Rx Transfer Reward Point.");
                OrderChain orderChain = (OrderChain) this.patientProfileDAO.findRecordById(new OrderChain(), orderChainId);
                if (orderChain != null) {
                    if (orderChain.getOrderList() != null && orderChain.getOrderList().size() > 0) {
                        order = orderChain.getOrderList().get(0);
                        order.setPatientComments(comments);
                        order.setPaymentType(paymentType);
                        this.patientProfileDAO.saveOrUpdate(order);
                        saveRewardHistory(Constants.TRANSFERRX, rewardPoints.getPoint().intValueExact(), profileId, Constants.PLUS, order);
                    }

                }
            }
            //saveRxTransferOrder(transferRxId, profileId, devliveryAddressId, paymentId);
        } catch (Exception e) {
            logger.error("Exception -> updateTransferRequest ", e);
        }
        return update;
    }

    private void saveRxTransferOrder(Integer transferRxId, Integer profileId, Integer devliveryAddressId, Integer paymentId) throws Exception {
        logger.info("Save RxTransfer Order ");
        Order order = new Order();
        TransferRequest transferRequest = (TransferRequest) patientProfileDAO.getObjectById(new TransferRequest(), transferRxId);
        TransferDetail transferDetail = (TransferDetail) patientProfileDAO.getTransferDetailByTranferRequestId(transferRequest.getId());
        if (transferRequest != null && transferRequest.getId() != null) {
            if (transferRequest.getVideo() == null) {
                order.setFirstName(transferRequest.getPatientName());
                order.setLastName(transferRequest.getPatientName());
                order.setPatientProfile(new PatientProfile(profileId));
                order.setOrderType(Constants.ORDERTYPE_TRANSFER);
                order.setViewStatus(Constants.VIEW_STATUS_CLOSED);
                if (transferDetail != null) {
                    order.setTransferDetail(transferDetail);
                } else {
                    order.setTransferDetail(null);
                }
                PatientDeliveryAddress patientDeliveryAddress = patientProfileDAO.getPatientDeliveryAddressById(profileId, devliveryAddressId);
                if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null) {
                    order.setAddressLine2(patientDeliveryAddress.getDescription());
                    order.setShippingAddress(patientDeliveryAddress.getAddress());
                    order.setCity(patientDeliveryAddress.getCity());
                    order.setZip(patientDeliveryAddress.getZip());
                    order.setApartment(patientDeliveryAddress.getApartment());
                    if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                        order.setState(patientDeliveryAddress.getState().getName());
                    }
                }
                order.setDrug(null);
                order.setDrugName(transferRequest.getDrug());
                if (transferRequest.getQuantity() != null) {
                    order.setQty(transferRequest.getQuantity().toString());
                }
                if (transferRequest.getDeliveryFee() != null) {
                    order.setPayment(transferRequest.getDeliveryFee().doubleValue());
                }
                PatientPaymentInfo patientPaymentInfo = (PatientPaymentInfo) patientProfileDAO.getObjectById(new PatientPaymentInfo(), paymentId);
                if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                    order.setCardHolderName(patientPaymentInfo.getCardHolderName());
                    order.setCardNumber(patientPaymentInfo.getCardNumber());
                    order.setCardType(patientPaymentInfo.getCardType());
                    order.setCardCvv(patientPaymentInfo.getCvvNumber());
                    order.setCardExpiry(patientPaymentInfo.getExpiryDate());
                }
                OrderStatus orderStatus = (OrderStatus) patientProfileDAO.getObjectById(new OrderStatus(), Constants.TRANSFER_RX_ORDER_STATUS_Id);
                if (orderStatus != null && orderStatus.getId() != null) {
                    order.setOrderStatus(orderStatus);
                }
                order.setOrderHistory(null);
                order.setCreatedOn(new Date());
                order.setUpdatedOn(new Date());
                patientProfileDAO.save(order);
            }
        }
    }

    /**
     *
     * @param deliveryAddressId
     * @return
     */
    public String getPatientDeliveryAddressById(Integer deliveryAddressId) {

        String patientDeliveryAddressJson = "";
        try {
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(PatientDeliveryAddress.class, deliveryAddressId);
            List<State> list_states = getStates();

            PatientDeliveryAddressWithStatesDTO patientDeliveryAddressWithStatesDTO = entityToDtoPatientDeliveryAddress(patientDeliveryAddress, list_states);

            ObjectMapper objectMapper = new ObjectMapper();
            patientDeliveryAddressJson = objectMapper.writeValueAsString(patientDeliveryAddressWithStatesDTO);
        } catch (Exception e) {
            logger.error("Exception -> getPatientDeliveryAddressbyId", e);
        }
        return patientDeliveryAddressJson;
    }

    private PatientDeliveryAddressWithStatesDTO entityToDtoPatientDeliveryAddress(PatientDeliveryAddress patientDeliveryAddress, List<State> list) {

        PatientDeliveryAddressWithStatesDTO patientDeliveryAddressWithStatesDTO = new PatientDeliveryAddressWithStatesDTO();
        patientDeliveryAddressWithStatesDTO.setId(patientDeliveryAddress.getId());
        patientDeliveryAddressWithStatesDTO.setApartment(patientDeliveryAddress.getApartment());
        patientDeliveryAddressWithStatesDTO.setZip(patientDeliveryAddress.getZip());
        patientDeliveryAddressWithStatesDTO.setCity(patientDeliveryAddress.getCity());
        patientDeliveryAddressWithStatesDTO.setAddress(patientDeliveryAddress.getAddress());
        patientDeliveryAddressWithStatesDTO.setDescription(patientDeliveryAddress.getDescription());
        patientDeliveryAddressWithStatesDTO.setDefaultAddress(patientDeliveryAddress.getDefaultAddress());
        patientDeliveryAddressWithStatesDTO.setAddressType(patientDeliveryAddress.getAddressType());
        patientDeliveryAddressWithStatesDTO.setStateId(patientDeliveryAddress.getState().getId());
        patientDeliveryAddressWithStatesDTO.setStateName(patientDeliveryAddress.getState().getName());

        patientDeliveryAddressWithStatesDTO.setStates(list);

        return patientDeliveryAddressWithStatesDTO;

    }

    /**
     * save delivery Address
     *
     * @param patientDeliveryAddressDTO
     * @return
     */
    public String updatePatientDeliveryAddress(PatientDeliveryAddressDTO patientDeliveryAddressDTO) {

        String patientDeliveryAddressJson = "";
        try {
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(PatientDeliveryAddress.class, patientDeliveryAddressDTO.getId());
            patientDeliveryAddress.setAddress(patientDeliveryAddressDTO.getAddress());
            patientDeliveryAddress.setApartment(patientDeliveryAddressDTO.getApartment());
            patientDeliveryAddress.setCity(patientDeliveryAddressDTO.getCity());
            State state = new State();
            state.setId(patientDeliveryAddressDTO.getStateId());
            patientDeliveryAddress.setState(state);
            patientDeliveryAddress.setZip(patientDeliveryAddressDTO.getZip());
            patientDeliveryAddress.setDescription(patientDeliveryAddressDTO.getDescription());
            patientDeliveryAddress.setAddressType(patientDeliveryAddressDTO.getAddressType());
            patientDeliveryAddress.setDefaultAddress(patientDeliveryAddressDTO.getDefaultAddress());
            patientProfileDAO.saveOrUpdate(patientDeliveryAddress);

            /**
             * send back save object
             */
            List<State> list_states = getStates();
            PatientDeliveryAddressWithStatesDTO patientDeliveryAddressWithStatesDTO = entityToDtoPatientDeliveryAddress(patientDeliveryAddress, list_states);

            ObjectMapper objectMapper = new ObjectMapper();
            patientDeliveryAddressJson = objectMapper.writeValueAsString(patientDeliveryAddressWithStatesDTO);

        } catch (Exception e) {
            logger.error("Exception -> getPatientDeliveryAddressbyId", e);
            return "";
        }
        return patientDeliveryAddressJson;
    }

    /**
     *
     * @param patientProfileUpdateRequestResponseDTO
     * @return
     */
    public String updatePatientProfileAllergies(PatientProfileUpdateRequestResponseDTO patientProfileUpdateRequestResponseDTO) {

        String patientProfileUpdateJson = "";
        try {
            PatientProfile patientProfile = new PatientProfile();
            patientProfile.setId(patientProfileUpdateRequestResponseDTO.getPatientProfileId());
            patientProfile.setAllergies(patientProfileUpdateRequestResponseDTO.getAllergies());
            patientProfileDAO.updatePatientInfoAllergies(patientProfile);

            patientProfileUpdateRequestResponseDTO.setStatus(Constants.JSON_STATUS.SUCCESS);
            patientProfileUpdateRequestResponseDTO.setStatuscode(Constants.JSON_STATUS.CODE_SUCCESS);

            ObjectMapper objectMapper = new ObjectMapper();
            patientProfileUpdateJson = objectMapper.writeValueAsString(patientProfileUpdateRequestResponseDTO);
            return patientProfileUpdateJson;

        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> updatePatientProfile", e);
            patientProfileUpdateRequestResponseDTO.setStatus(Constants.JSON_STATUS.FAIL);
            patientProfileUpdateRequestResponseDTO.setErrorMessage(e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                patientProfileUpdateJson = objectMapper.writeValueAsString(patientProfileUpdateRequestResponseDTO);
            } catch (Exception ex) {
                logger.error("Exception: PatientProfileService -> updatePatientProfile -> unable to parse response", ex);
            }
            return patientProfileUpdateJson;
        }

    }

    /**
     * search by Drug Cetory
     */
    public String searchDrugCategoryByParameter(String searchParameter) {

        String responseDrugCategoryJson = "";
        try {
            List<DrugCategory> lst_drugCategory = patientProfileDAO.searchDrugCategoryListByParameter(searchParameter);
            List<DrugCategoryListDTO> drugCategoryListDTO = new ArrayList<>();
            convertDrugCategoryModelToDto(lst_drugCategory, drugCategoryListDTO);

            ObjectMapper objectMapper = new ObjectMapper();
            responseDrugCategoryJson = objectMapper.writeValueAsString(drugCategoryListDTO);
            return responseDrugCategoryJson;

        } catch (Exception ex) {
            logger.error("Exception: PatientProfileService -> search Drug Category -> unable to search", ex);
        }
        return responseDrugCategoryJson;
    }

    private void convertDrugCategoryModelToDto(List<DrugCategory> lst_drugCetegoryModel, List<DrugCategoryListDTO> lst_drugCategoryListDTO) {

        for (DrugCategory drugCategory : lst_drugCetegoryModel) {

            DrugCategoryListDTO drugCategoryListDTO = new DrugCategoryListDTO();
            drugCategoryListDTO.setCategoryId("" + drugCategory.getId());
            drugCategoryListDTO.setCategoryName(drugCategory.getDrugCategoryName());

            lst_drugCategoryListDTO.add(drugCategoryListDTO);
        }
    }

    /**
     * Therapy Classes
     *
     * @param searchParameter
     * @return
     */
    public String searchTherapyClassesByParameter(Integer drugCatId, String drugTherapyClassname) {

        String responseDrugCategoryJson = "";
        try {

            List<DrugTherapyClass> lst_drugTherapyClass = patientProfileDAO.searchDrugTherapyClassListByParameter(drugCatId, drugTherapyClassname);
            List<DrugCategoryListDTO> drugCategoryListDTO = new ArrayList<>();
            convertTherapyClassModelToDto(lst_drugTherapyClass, drugCategoryListDTO);

            ObjectMapper objectMapper = new ObjectMapper();
            responseDrugCategoryJson = objectMapper.writeValueAsString(drugCategoryListDTO);
            return responseDrugCategoryJson;

        } catch (Exception ex) {
            logger.error("Exception: PatientProfileService -> search Drug Category -> unable to search", ex);
        }
        return responseDrugCategoryJson;
    }

    private void convertTherapyClassModelToDto(List<DrugTherapyClass> lst_drugTherapyClassModel, List<DrugCategoryListDTO> lst_drugCategoryListDTO) {

        for (DrugTherapyClass drugTherapyClass : lst_drugTherapyClassModel) {

            DrugCategoryListDTO drugCategoryListDTO = new DrugCategoryListDTO();
            drugCategoryListDTO.setCategoryId("" + drugTherapyClass.getDrugCategory().getId());
            drugCategoryListDTO.setCategoryName(drugTherapyClass.getDrugCategory().getDrugCategoryName());
            drugCategoryListDTO.setTherapyClassId("" + drugTherapyClass.getId());
            drugCategoryListDTO.setTherapyClassName("" + drugTherapyClass.getDrugTherapyClassName());

            lst_drugCategoryListDTO.add(drugCategoryListDTO);
        }
    }

    /**
     * Generic Name
     *
     * @param searchParameter
     * @return
     */
    public String searchGenericNameByParameter(Integer drugTherapyClassId, String genericNamePrefix) {

        String responseDrugCategoryJson = "";
        try {

            List<DrugGenericTypes> lst_drugTherapyClass = patientProfileDAO.searchDrugGenericTypesListByParameter(drugTherapyClassId, genericNamePrefix);
            List<DrugCategoryListDTO> drugCategoryListDTO = new ArrayList<>();
            convertGenericTypeModelToDto(lst_drugTherapyClass, drugCategoryListDTO);

            ObjectMapper objectMapper = new ObjectMapper();
            responseDrugCategoryJson = objectMapper.writeValueAsString(drugCategoryListDTO);
            return responseDrugCategoryJson;

        } catch (Exception ex) {
            logger.error("Exception: PatientProfileService -> search Drug Category -> unable to search", ex);
        }
        return responseDrugCategoryJson;
    }

    private void convertGenericTypeModelToDto(List<DrugGenericTypes> lst_drugTherapyClass, List<DrugCategoryListDTO> lst_drugCategoryListDTO) {

        for (DrugGenericTypes drugGenericTypes : lst_drugTherapyClass) {

            DrugCategoryListDTO drugCategoryListDTO = new DrugCategoryListDTO();
            drugCategoryListDTO.setGenericNameId("" + drugGenericTypes.getId());
            drugCategoryListDTO.setGenericName(drugGenericTypes.getDrugGenericName());
            drugCategoryListDTO.setTherapyClassId("" + drugGenericTypes.getDrugTherapyClass().getId());

            lst_drugCategoryListDTO.add(drugCategoryListDTO);
        }
    }

    /**
     * Brand Name
     */
    public String searchBrandNameByParameter(Integer drugGenericNameId, String brandNamePrefix) {

        String responseDrugCategoryJson = "";
        try {

            List<DrugBrand> lst_brandName = patientProfileDAO.searchDrugBrandNameListByParameter(drugGenericNameId, brandNamePrefix);
            List<DrugCategoryListDTO> drugCategoryListDTO = new ArrayList<>();
            convertDrugBrandModelToDto(lst_brandName, drugCategoryListDTO);

            ObjectMapper objectMapper = new ObjectMapper();
            responseDrugCategoryJson = objectMapper.writeValueAsString(drugCategoryListDTO);
            return responseDrugCategoryJson;

        } catch (Exception ex) {
            logger.error("Exception: PatientProfileService -> search Drug Category -> unable to search", ex);
        }
        return responseDrugCategoryJson;
    }

    private void convertDrugBrandModelToDto(List<DrugBrand> lst_drugBrand, List<DrugCategoryListDTO> lst_drugCategoryListDTO) {

        for (DrugBrand drugBrand : lst_drugBrand) {

            DrugCategoryListDTO drugCategoryListDTO = new DrugCategoryListDTO();

            drugCategoryListDTO.setBrandNameId("" + drugBrand.getId());
            drugCategoryListDTO.setBrandName(drugBrand.getDrugBrandName());
            drugCategoryListDTO.setGenericNameId("" + drugBrand.getDrugGenericTypes().getId());

            lst_drugCategoryListDTO.add(drugCategoryListDTO);
        }
    }

    public boolean saveDrugSearchesWs(Integer patientId, Integer drugId, String drugName, String drugType, String qty, String drugPrice, String strength, String genericName) {
        boolean isSaved = false;
        try {
            DrugSearches drugSearches = new DrugSearches();
            drugSearches.setPatientProfile(new PatientProfile(patientId));
            drugSearches.setDrug(new Drug(drugId));
            drugSearches.setGenericName(genericName);
            drugSearches.setDrugBrandName(drugName);
            drugSearches.setDrugType(drugType);
            drugSearches.setQty(qty);
            if (drugPrice != null) {
                drugSearches.setDrugPrice(Double.parseDouble(drugPrice));
            }
            drugSearches.setStrength(strength);
            drugSearches.setCreatedOn(new Date());
            patientProfileDAO.save(drugSearches);
            isSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception: PatientProfileService -> saveDrugSearchesWs", e);
        }
        return isSaved;
    }

    /////////////////////////////////////////////////////////////////
    public boolean saveDrugSearchesWs(Integer patientId, Long drugId, String drugName, String drugType, String qty, String drugPrice, String strength, String genericName) {
        boolean isSaved = false;
        try {
            DrugSearches drugSearches = new DrugSearches();
            drugSearches.setPatientProfile(new PatientProfile(patientId));
            DrugDetail detail = new DrugDetail();
            detail.setDrugDetailId(drugId);//.setDrugNDC(drugId);
            drugSearches.setDrugDetail(detail);
            drugSearches.setGenericName(genericName);
            drugSearches.setDrugBrandName(drugName);
            drugSearches.setDrugType(drugType);
            drugSearches.setQty(qty);
            if (drugPrice != null) {
                drugSearches.setDrugPrice(Double.parseDouble(drugPrice));
            }
            drugSearches.setStrength(strength);
            drugSearches.setCreatedOn(new Date());
            patientProfileDAO.save(drugSearches);
            isSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception: PatientProfileService -> saveDrugSearchesWs", e);
        }
        return isSaved;
    }

    ////////////////////////////////////////////////////////////////
    public List<DrugSearchesDTO> getSearchesRecordList(Integer patientId) {
        List<DrugSearchesDTO> list = new ArrayList<>();
        try {
            List<DrugSearches> drugSearcheses = patientProfileDAO.getDrugSearchesList(patientId);
            logger.info("Total Record: " + drugSearcheses.size());
            for (DrugSearches ds : drugSearcheses) {
                DrugSearchesDTO drugSearchesDTO = new DrugSearchesDTO();
                drugSearchesDTO.setDrugSearchId(ds.getId());
                if (ds.getPatientProfile() != null && ds.getPatientProfile().getId() != null) {
                    drugSearchesDTO.setId(ds.getPatientProfile().getId());
                    drugSearchesDTO.setPatientId(ds.getPatientProfile().getId());
                }
//                if (ds.getDrug() != null && ds.getDrug().getDrugId() != null) {
//                    drugSearchesDTO.setDrugId(ds.getDrug().getDrugId());
//                }
                if (ds.getDrugDetail() != null && ds.getDrugDetail().getDrugDetailId() != null) {//ds.getDrugDetail().getDrugNDC() != null) {
                    // drugSearchesDTO.setDrugId(ds.getDrug().getDrugId());
                    drugSearchesDTO.setDrugNDC(ds.getDrugDetail().getDrugDetailId().toString());//.getDrugNDC().toString());
                }
                drugSearchesDTO.setGenericName(ds.getGenericName());
                drugSearchesDTO.setDrugName(ds.getDrugBrandName());
                drugSearchesDTO.setDrugType(ds.getDrugType());
                drugSearchesDTO.setStrength(ds.getStrength());
                drugSearchesDTO.setDrugQty(ds.getQty());
                drugSearchesDTO.setDrugPrice(ds.getDrugPrice());
                String date = DateUtil.dateToString(ds.getCreatedOn(), "MMM dd, yyyy");
                String time = DateUtil.dateToString(ds.getCreatedOn(), "HH:mm a");
                drugSearchesDTO.setCreatedOn(date + " at " + time);
                drugSearchesDTO.setTotalRecord(drugSearcheses.size());
                list.add(drugSearchesDTO);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getSearchesRecordList", e);
        }
        return list;
    }

    public boolean deleteDrugSearchesById(Integer id) {
        boolean isDelete = false;
        try {
            isDelete = patientProfileDAO.deleteDrugSearchesRecordById(id);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> deleteDrugSearchesById", e);
        }
        return isDelete;
    }

    public boolean deleteAllDrugSearchesRecordByProfileId(Integer profileId) {
        boolean isDelete = false;
        try {
            isDelete = patientProfileDAO.deleteAllDrugSearchesRecordByProfileId(profileId);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> deleteAllDrugSearchesRecordByProfileId", e);
        }
        return isDelete;
    }

    public boolean saveBloodGlucoseResult(Integer profileId, String glucoseLevel, String readingTime, String type) {
        boolean isSaved = false;
        try {
            PatientGlucoseResults patientGlucoseResults = new PatientGlucoseResults();
            patientGlucoseResults.setPatientProfile(new PatientProfile(profileId));
            patientGlucoseResults.setGlucoseLevel(glucoseLevel);
            patientGlucoseResults.setReadingTime(readingTime);
            patientGlucoseResults.setType(type);
            patientGlucoseResults.setCreatedOn(new Date());
            patientProfileDAO.save(patientGlucoseResults);
            isSaved = true;
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception: PatientProfileService -> saveBloodGlucoseResultsaveBloodGlucoseResult", e);
        }
        return isSaved;
    }

    /**
     *
     * @param insuranceDetailsId
     * @return
     */
    public PatientInsuranceDetailsDTO getPatientInsuranceDetailsById(Integer insuranceDetailsId) {
        PatientInsuranceDetailsDTO patientInsuranceDetailsDTO = null;
        try {
            if (insuranceDetailsId != null) {

                PatientInsuranceDetails patientInsuranceDetails = patientProfileDAO.getPatientInsuranceDetailById(insuranceDetailsId);
                patientInsuranceDetailsDTO = new PatientInsuranceDetailsDTO();
                modelToDtoPatientInsuranceDetails(patientInsuranceDetailsDTO, patientInsuranceDetails);
                logger.info("Getting Patient Insurance : " + patientInsuranceDetails.getId());
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientInsurance Detail ", e);
        }
        return patientInsuranceDetailsDTO;
    }

    /**
     *
     * @param patientInsuranceDetailsDTO
     * @return
     */
    public boolean saveUpdatePatientInsuranceDetails(PatientInsuranceDetailsDTO patientInsuranceDetailsDTO) {
        boolean isUpdate = false;

        /**
         * check record save or update
         */
        boolean b_recordUpdate = updatePatientInsuranceDetail(patientInsuranceDetailsDTO);
        if (b_recordUpdate) {
            return true;
        }
        try {
            if (patientInsuranceDetailsDTO != null) {

                PatientProfile patientProfile = new PatientProfile();
                patientProfile.setId(patientInsuranceDetailsDTO.getPatientProfileId());

                PatientInsuranceDetails patientInsuranceDetails = new PatientInsuranceDetails();
                patientInsuranceDetails.setPatientProfile(patientProfile);
                dtoToModelPatientInsuranceDetails(patientInsuranceDetailsDTO, patientInsuranceDetails);

                patientProfileDAO.saveOrUpdate(patientInsuranceDetails);
                logger.info("Save Ptient Insurance : " + patientInsuranceDetails.getId());
                isUpdate = true;
            }
        } catch (Exception e) {
            logger.error("Exception -> updateDeliveryAddressWs", e);
        }
        return isUpdate;
    }

    /**
     *
     * @param patientInsuranceDetailsDTO
     * @return
     */
    private boolean updatePatientInsuranceDetail(PatientInsuranceDetailsDTO patientInsuranceDetailsDTO) {

        boolean isUpdate = false;
        try {
            PatientInsuranceDetails patientInsuranceDetails = patientProfileDAO.getPatientInsuranceDetailById(patientInsuranceDetailsDTO.getPatientProfileId());
            if (patientInsuranceDetails != null && patientInsuranceDetails.getId() != 0) {
                dtoToModelPatientInsuranceDetails(patientInsuranceDetailsDTO, patientInsuranceDetails);
                patientProfileDAO.merge(patientInsuranceDetails);
                return true;
            }
            logger.info("Update Ptient Insurance : " + patientInsuranceDetails.getId());
        } catch (Exception ex) {
            logger.info("Patient Insurnace Card Detail not exist");
        }
        return isUpdate;

    }

    private void dtoToModelPatientInsuranceDetails(PatientInsuranceDetailsDTO patientInsuranceDetailsDTO, PatientInsuranceDetails patientInsuranceDetails) throws Exception {

        Date fromdate = DateUtil.stringToDate(patientInsuranceDetailsDTO.getExpiryDate(), "MM/dd/yyyy");
        patientInsuranceDetails.setExpiryDate(fromdate);
        patientInsuranceDetails.setGroupNumber(patientInsuranceDetailsDTO.getGroupNumber());
        patientInsuranceDetails.setInsuranceProvider(patientInsuranceDetailsDTO.getInsuranceProvider());
        patientInsuranceDetails.setMemberID(patientInsuranceDetailsDTO.getMemberID());
        patientInsuranceDetails.setPlanId(patientInsuranceDetailsDTO.getPlanId());
        patientInsuranceDetails.setProviderAddress(patientInsuranceDetailsDTO.getProviderAddress());
        patientInsuranceDetails.setProviderPhone(patientInsuranceDetailsDTO.getProviderPhone());
        Date currentDate = DateUtil.formatDate(new Date(), "MM/dd/yyyy");
        patientInsuranceDetails.setCreatedOn(currentDate);
        patientInsuranceDetails.setUpdatedOn(currentDate);

    }

    private void modelToDtoPatientInsuranceDetails(PatientInsuranceDetailsDTO patientInsuranceDetailsDTO, PatientInsuranceDetails patientInsuranceDetails) throws Exception {

        patientInsuranceDetailsDTO.setId(patientInsuranceDetails.getId());
        patientInsuranceDetailsDTO.setGroupNumber(patientInsuranceDetails.getGroupNumber());
        patientInsuranceDetailsDTO.setInsuranceProvider(patientInsuranceDetails.getInsuranceProvider());
        patientInsuranceDetailsDTO.setMemberID(patientInsuranceDetails.getMemberID());
        patientInsuranceDetailsDTO.setPlanId(patientInsuranceDetails.getPlanId());
        patientInsuranceDetailsDTO.setProviderAddress(patientInsuranceDetails.getProviderAddress());
        patientInsuranceDetailsDTO.setProviderPhone(patientInsuranceDetails.getProviderPhone());
        String str_expireDate = DateUtil.dateToString(patientInsuranceDetails.getExpiryDate(), "MM/dd/yyyy");
        patientInsuranceDetailsDTO.setExpiryDate(str_expireDate);

    }

    public boolean isDefaultPatientDeliveryAddress(Integer profileId) {
        boolean isExist = false;
        try {
            isExist = patientProfileDAO.isDefaultPatientDeliveryAddress(profileId);
            logger.info("isDefaultPatientDeliveryAddress: " + isExist);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> isDefaultPatientDeliveryAddress", e);
        }
        return isExist;
    }

    public String getZipCodeCalculations(String zip, Integer profileId, Integer prefId) {
        ZipCodeCalculation object = null;
        try {
            object = this.patientProfileDAO.getZipCodeCalculations(zip, profileId, prefId);
        } catch (Exception ex) {
            Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (object != null) {
            return object.getDeliveryFee().toString();
        }
        return "0";
    }

    public List<DeliveryDistanceFeeDTO> getZipCodeCalculationsList(String zip, PatientProfile patientProfile) {
        List<DeliveryDistanceFeeDTO> deliveryDistanceFeeDTOs = new ArrayList<>();
        try {
            List<ZipCodeCalculation> zipCodeCalculationsList = patientProfileDAO.getZipCodeCalculationsList(zip, patientProfile.getId());
            if (!zipCodeCalculationsList.isEmpty() && zipCodeCalculationsList.size() > 0) {
                ZipCodeCalculation calculation = zipCodeCalculationsList.get(0);
                PharmacyZipCodes pharmacyZipCodes = this.getPharmacyZipCodes();
                for (DeliveryDistanceFee deliveryDistanceFee : pharmacyZipCodes.getDeliveryDistanceFeesList()) {
                    DeliveryDistanceFeeDTO distanceFeeDTO = new DeliveryDistanceFeeDTO();
                    if (deliveryDistanceFee != null && deliveryDistanceFee.getId() != null) {
                        if (calculation.getMiles() >= deliveryDistanceFee.getDeliveryDistances().getDistanceFrom() && calculation.getMiles() <= deliveryDistanceFee.getDeliveryDistances().getDistanceTo()) {
                            if (calculation.getDeliveryFee() != null) {
                                logger.info("Delivery fee is: " + deliveryDistanceFee.getDeliveryFee());
                                distanceFeeDTO.setDeliveryFee(deliveryDistanceFee.getDeliveryFee());
                            }
                            distanceFeeDTO.setMiles(calculation.getMiles());
                            if (deliveryDistanceFee.getDeliveryPreferenceses() != null && deliveryDistanceFee.getDeliveryPreferenceses().getId() != null) {
                                logger.info("DeliveryPreferenceId is:: " + deliveryDistanceFee.getDeliveryPreferenceses().getId());
                                distanceFeeDTO.setDprefaId(deliveryDistanceFee.getDeliveryPreferenceses().getId());
                                distanceFeeDTO.setName(deliveryDistanceFee.getDeliveryPreferenceses().getName());
                                String name = AppUtil.getSafeStr(deliveryDistanceFee.getDeliveryPreferenceses().getName(), "").toLowerCase();
                                if (name.contains("2nd day")) {
                                    distanceFeeDTO.setDeliveryFee(BigDecimal.valueOf(0));
                                    distanceFeeDTO.setIncludedStr("Included");
                                } else {
                                    distanceFeeDTO.setIncludedStr("");
                                }
                                distanceFeeDTO.setDescription(deliveryDistanceFee.getDeliveryPreferenceses().getDescription());
                                distanceFeeDTO.setPickedFromPharmacy(deliveryDistanceFee.getDeliveryPreferenceses().isPickedFromPharmacy());
                            }
                            if (!checkDuplicateRecordInList(deliveryDistanceFeeDTOs, distanceFeeDTO.getDprefaId())) {
                                deliveryDistanceFeeDTOs.add(distanceFeeDTO);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getZipCodeCalculationsList", e);
        }
        return deliveryDistanceFeeDTOs;
    }

    public boolean checkDuplicateRecordInList(List<DeliveryDistanceFeeDTO> list, Integer prefId) {
        boolean isExist = false;
        try {
            for (DeliveryDistanceFeeDTO dTO : list) {
                if (dTO.getDprefaId() != null && prefId != null) {
                    if (dTO.getDprefaId().equals(prefId)) {
                        isExist = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> checkDuplicateRecordInList", e);
        }
        return isExist;
    }

    public PharmacyZipCodes getPharmacyZipCodes() {
        PharmacyZipCodes pharmacyZipCodes = new PharmacyZipCodes();
        try {
            List<PharmacyZipCodes> list = patientProfileDAO.getPharmacyZipCodesList();
            if (!list.isEmpty() && list.size() > 0) {
                pharmacyZipCodes = list.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getPharmacyZipCodes", e);
        }
        return pharmacyZipCodes;
    }

    public List<OrderDetailDTO> getTransferRxList(Integer profileId, Integer transFerId) {
        List<OrderDetailDTO> list = new ArrayList<>();
        try {
            List<TransferRequest> transferRequestList = patientProfileDAO.geTransferRequestsList(profileId, transFerId);
            setRxTransFerList(transferRequestList, list, profileId);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getTransferRxList", e);
        }
        return list;
    }

    public List<OrderDetailDTO> getTransferRxList(Integer profileId, Integer transFerId, String comments, String paymentType) {
        List<OrderDetailDTO> list = new ArrayList<>();
        try {
            List<TransferRequest> transferRequestList = patientProfileDAO.geTransferRequestsList(profileId, transFerId);
            setRxTransFerList(transferRequestList, list, profileId, comments, paymentType);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getTransferRxList", e);
        }
        return list;
    }

    public List<OrderDetailDTO> getTransferRxList(Integer profileId, Integer transFerId, String comments, String paymentType, String source, Boolean addCopyCard) {
        List<OrderDetailDTO> list = new ArrayList<>();
        try {
            List<TransferRequest> transferRequestList = patientProfileDAO.geTransferRequestsList(profileId, transFerId);
            setRxTransFerList(transferRequestList, list, profileId, comments, paymentType, source, addCopyCard);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getTransferRxList", e);
        }
        return list;
    }

    private void setRxTransFerList(List<TransferRequest> transferRequestList, List<OrderDetailDTO> list, Integer profileId) throws Exception {
        if (!transferRequestList.isEmpty() && transferRequestList.size() > 0) {
            for (TransferRequest transferRequest : transferRequestList) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                if (transferRequest.getDeliveryPreferences() != null && transferRequest.getDeliveryPreferences().getId() != null) {
                    DeliveryPreferences deliveryPreferences = this.getDeliveryPreferenceById(transferRequest.getDeliveryPreferences().getId());
                    String fee = this.getZipCodeCalculations(transferRequest.getZip(), profileId, transferRequest.getDeliveryPreferences().getId());
                    orderDetailDTO.setName(deliveryPreferences.getName());
                    orderDetailDTO.setDescription(deliveryPreferences.getDescription());
                    orderDetailDTO.setFee(CommonUtil.getStrToBigDecimal(fee));
                }
                orderDetailDTO.setTransferRxId(transferRequest.getId());
                orderDetailDTO.setPatientName(transferRequest.getPatientName());
                orderDetailDTO.setDrugName(transferRequest.getDrug());
                if (transferRequest.getQuantity() != null) {
                    orderDetailDTO.setQty(transferRequest.getQuantity().toString());
                }
                if (transferRequest.getRequestedOn() != null) {
                    orderDetailDTO.setOrderDate(DateUtil.dateToString(transferRequest.getRequestedOn(), Constants.USA_DATE_TIME_FORMATE));
                }
                if (transferRequest.getPatientPaymentInfo() != null && transferRequest.getPatientPaymentInfo().getId() != null) {
                    orderDetailDTO.setCardType(transferRequest.getPatientPaymentInfo().getCardType());
                    String cardNumber = transferRequest.getPatientPaymentInfo().getCardNumber();
                    logger.info("Card Number is: " + cardNumber);
                    if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                        logger.info("card number is: " + cardNumber.substring(cardNumber.length() - 4));
                        orderDetailDTO.setCardNumber(cardNumber.substring(cardNumber.length() - 4));
                    } else {
                        logger.info("Card Number length is less than 4: " + cardNumber);
                        orderDetailDTO.setCardNumber(cardNumber);
                    }
                }
                if (transferRequest.getPatientDeliveryAddress() != null && transferRequest.getPatientDeliveryAddress().getId() != null) {
                    State state = transferRequest.getPatientDeliveryAddress().getState();
                    if (transferRequest.getPatientDeliveryAddress().getApartment() != null && !"".equals(transferRequest.getPatientDeliveryAddress().getApartment())) {
                        logger.info("Appartmnet is: " + transferRequest.getPatientDeliveryAddress().getApartment());
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getApartment() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    } else {
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    }
                }
                orderDetailDTO.setPayment(transferRequest.getDeliveryFee().doubleValue());
                orderDetailDTO.setOrderType(1);
                orderDetailDTO.setOrderStatusName(Constants.PENDING);
                getOrderRewardDetail(profileId, orderDetailDTO);
                list.add(orderDetailDTO);
            }
        }
    }

    private void setRxTransFerList(List<TransferRequest> transferRequestList, List<OrderDetailDTO> list, Integer profileId, String comments, String paymentType) throws Exception {
        if (!transferRequestList.isEmpty() && transferRequestList.size() > 0) {
            for (TransferRequest transferRequest : transferRequestList) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                if (transferRequest.getDeliveryPreferences() != null && transferRequest.getDeliveryPreferences().getId() != null) {
                    DeliveryPreferences deliveryPreferences = this.getDeliveryPreferenceById(transferRequest.getDeliveryPreferences().getId());
                    String fee = this.getZipCodeCalculations(transferRequest.getZip(), profileId, transferRequest.getDeliveryPreferences().getId());
                    orderDetailDTO.setName(deliveryPreferences.getName());
                    orderDetailDTO.setDescription(deliveryPreferences.getDescription());
                    orderDetailDTO.setFee(CommonUtil.getStrToBigDecimal(fee));
                }
                orderDetailDTO.setTransferRxId(transferRequest.getId());
                orderDetailDTO.setPatientName(transferRequest.getPatientName());
                orderDetailDTO.setDrugName(transferRequest.getDrug());
                orderDetailDTO.setComments(AppUtil.getSafeStr(comments, ""));//transferRequest.getComments());
                orderDetailDTO.setPaymentType(AppUtil.getSafeStr(paymentType, ""));//transferRequest.getPaymentType());
                if (transferRequest.getQuantity() != null) {
                    orderDetailDTO.setQty(transferRequest.getQuantity().toString());
                }
                if (transferRequest.getRequestedOn() != null) {
                    orderDetailDTO.setOrderDate(DateUtil.dateToString(transferRequest.getRequestedOn(), Constants.USA_DATE_TIME_FORMATE));
                }
                if (transferRequest.getPatientPaymentInfo() != null && transferRequest.getPatientPaymentInfo().getId() != null) {
                    orderDetailDTO.setCardType(transferRequest.getPatientPaymentInfo().getCardType());
                    String cardNumber = transferRequest.getPatientPaymentInfo().getCardNumber();
                    logger.info("Card Number is: " + cardNumber);
                    if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                        logger.info("card number is: " + cardNumber.substring(cardNumber.length() - 4));
                        orderDetailDTO.setCardNumber(cardNumber.substring(cardNumber.length() - 4));
                    } else {
                        logger.info("Card Number length is less than 4: " + cardNumber);
                        orderDetailDTO.setCardNumber(cardNumber);
                    }
                }
                if (transferRequest.getPatientDeliveryAddress() != null && transferRequest.getPatientDeliveryAddress().getId() != null) {
                    State state = transferRequest.getPatientDeliveryAddress().getState();
                    if (transferRequest.getPatientDeliveryAddress().getApartment() != null && !"".equals(transferRequest.getPatientDeliveryAddress().getApartment())) {
                        logger.info("Appartmnet is: " + transferRequest.getPatientDeliveryAddress().getApartment());
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getApartment() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    } else {
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    }
                }
                orderDetailDTO.setPayment(transferRequest.getDeliveryFee().doubleValue());
                orderDetailDTO.setOrderType(1);
                orderDetailDTO.setOrderStatusName(Constants.PENDING);
                getOrderRewardDetail(profileId, orderDetailDTO);
                list.add(orderDetailDTO);
            }
        }
    }

    private void setRxTransFerList(List<TransferRequest> transferRequestList, List<OrderDetailDTO> list, Integer profileId, String comments, String paymentType, String source, Boolean addCopyCard) throws Exception {
        if (!transferRequestList.isEmpty() && transferRequestList.size() > 0) {
            for (TransferRequest transferRequest : transferRequestList) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                orderDetailDTO.setIsCopayCard(addCopyCard);
                if (transferRequest.getDeliveryPreferences() != null && transferRequest.getDeliveryPreferences().getId() != null) {
                    DeliveryPreferences deliveryPreferences = this.getDeliveryPreferenceById(transferRequest.getDeliveryPreferences().getId());
                    String fee = this.getZipCodeCalculations(transferRequest.getZip(), profileId, transferRequest.getDeliveryPreferences().getId());
                    orderDetailDTO.setName(deliveryPreferences.getName());
                    orderDetailDTO.setDescription(deliveryPreferences.getDescription());
                    orderDetailDTO.setFee(CommonUtil.getStrToBigDecimal(fee));
                }
                orderDetailDTO.setTransferRxId(transferRequest.getId());
                orderDetailDTO.setPatientName(transferRequest.getPatientName());
                orderDetailDTO.setDrugName(transferRequest.getDrug());
                orderDetailDTO.setComments(AppUtil.getSafeStr(comments, ""));//transferRequest.getComments());
                orderDetailDTO.setPaymentType(AppUtil.getSafeStr(paymentType, ""));//transferRequest.getPaymentType());
                if (transferRequest.getQuantity() != null) {
                    orderDetailDTO.setQty(transferRequest.getQuantity().toString());
                }
                if (transferRequest.getRequestedOn() != null) {
                    orderDetailDTO.setOrderDate(DateUtil.dateToString(transferRequest.getRequestedOn(), Constants.USA_DATE_TIME_FORMATE));
                }
                if (transferRequest.getPatientPaymentInfo() != null && transferRequest.getPatientPaymentInfo().getId() != null) {
                    orderDetailDTO.setCardType(transferRequest.getPatientPaymentInfo().getCardType());
                    String cardNumber = transferRequest.getPatientPaymentInfo().getCardNumber();
                    logger.info("Card Number is: " + cardNumber);
                    if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                        logger.info("card number is: " + cardNumber.substring(cardNumber.length() - 4));
                        orderDetailDTO.setCardNumber(cardNumber.substring(cardNumber.length() - 4));
                    } else {
                        logger.info("Card Number length is less than 4: " + cardNumber);
                        orderDetailDTO.setCardNumber(cardNumber);
                    }
                }
                if (transferRequest.getPatientDeliveryAddress() != null && transferRequest.getPatientDeliveryAddress().getId() != null) {
                    State state = transferRequest.getPatientDeliveryAddress().getState();
                    if (transferRequest.getPatientDeliveryAddress().getApartment() != null && !"".equals(transferRequest.getPatientDeliveryAddress().getApartment())) {
                        logger.info("Appartmnet is: " + transferRequest.getPatientDeliveryAddress().getApartment());
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getApartment() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    } else {
                        orderDetailDTO.setShippingAddress(transferRequest.getPatientDeliveryAddress().getAddress() + " " + transferRequest.getPatientDeliveryAddress().getCity() + ", " + state.getName() + " " + transferRequest.getPatientDeliveryAddress().getZip());
                    }
                }
                orderDetailDTO.setSource(source);
                orderDetailDTO.setPayment(transferRequest.getDeliveryFee().doubleValue());
                orderDetailDTO.setOrderType(1);
                orderDetailDTO.setOrderStatusName(Constants.PENDING);
                getOrderRewardDetail(profileId, orderDetailDTO);
                list.add(orderDetailDTO);
            }
        }
    }

    public PatientProfile getPatientProfileBySecurityToken(String securityToken) {
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileDAO.getPatientProfileBySecurityToken(securityToken);
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getPatientProfileBySecurityToken", e);
        }
        return patientProfile;
    }

    public boolean savePatientProfileNotes(PatientProfileNotes patientProfileNotes) {
        boolean success;
        try {
            patientProfileNotes.setCreatedOn(new Date());
            patientProfileDAO.save(patientProfileNotes);
            success = true;
        } catch (Exception e) {
            logger.error("Exception::", e);
            success = false;
        }
        return success;
    }

    public List<PatientProfileNotes> getPatientProfileNotesList(Integer profileId) {
        List<PatientProfileNotes> list = new ArrayList<>();
        try {
            list = patientProfileDAO.getPatientProfileNotesListByPtProfileId(profileId);
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return list;
    }

    public boolean isDefaultDeliveryAddress(Integer profileId) {
        boolean isExist = false;
        try {
            logger.info("ProfileId is:: " + profileId);
            isExist = patientProfileDAO.isDefaultDeliveryAddress(profileId);
            logger.info("isDefaultDeliveryAddress:: " + isExist);
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return isExist;
    }

    public boolean isDefaultPaymentInfo(Integer profileId) {
        boolean isExist = false;
        try {
            logger.info("ProfileId is:: " + profileId);
            isExist = patientProfileDAO.isDefaultPaymentInfo(profileId);
            logger.info("isDefaultDeliveryAddress:: " + isExist);
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return isExist;
    }

    public TransferDetail getTransferDetailByTranferRequestId(int requestId) {

        try {
            TransferDetail trnsfer = patientProfileDAO.getTransferDetailByTranferRequestId(requestId);
            return trnsfer;
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return null;
    }

    public boolean deleteRxTransferRecord(Integer transferId, Integer profileId) {
        boolean isDelete = false;
        try {
            isDelete = patientProfileDAO.deleteRxTransferRecord(transferId, profileId);
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return isDelete;
    }

    public NotificationMessages updateOrdersPtMessage(Integer id) {
        NotificationMessages notificationMessages = new NotificationMessages();
        try {
            OrdersPtMessage ordersPtMessage = (OrdersPtMessage) patientProfileDAO.getObjectById(new OrdersPtMessage(), id);
            if (ordersPtMessage != null && ordersPtMessage.getId() != null) {
                ordersPtMessage.setIsRead(Boolean.TRUE);
                patientProfileDAO.update(ordersPtMessage);
                notificationMessages = getPharmacyNotification(ordersPtMessage);
            }
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return notificationMessages;
    }

    public PatientInsuranceDetails getPatientInsuranceDetailsByProfileId(Integer profileId) {
        PatientInsuranceDetails patientInsuranceDetails = new PatientInsuranceDetails();
        try {
            patientInsuranceDetails = patientProfileDAO.getPatientInsuranceDetailById(profileId);
        } catch (Exception e) {
            logger.error("Exception::", e);
        }
        return patientInsuranceDetails;
    }

    //////////////////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> viewOrderReceiptWs(Integer patientId, String orderId, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.viewOrderReceiptList(patientId, orderId);
            list = setOrderList(dbOrders, null, handlingFee, pref, cardNumber, cardType, awardedPoints, clearNameFlds);
            logger.info("New Order list size: " + list.size());
        } catch (Exception e) {
            logger.error("Exception -> viewOrderReceiptWs", e);
        }
        return list;
    }

    //////////////////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> viewOrderReceiptWs(Integer patientId, String orderId, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds, String paymentType, String comments) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.viewOrderReceiptList(patientId, orderId);
            list = setOrderList(dbOrders, null, handlingFee, pref, cardNumber, cardType, awardedPoints, clearNameFlds, paymentType, comments);
            logger.info("New Order list size: " + list.size());
        } catch (Exception e) {
            logger.error("Exception -> viewOrderReceiptWs", e);
        }
        return list;
    }

    //////////////////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> viewOrderReceiptWs(Integer patientId, String orderId, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds, String paymentType, String comments, String source) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            List<Order> dbOrders = patientProfileDAO.viewOrderReceiptList(patientId, orderId);
            if (!CommonUtil.isNullOrEmpty(dbOrders)) {
                list = setOrderList(dbOrders, null, handlingFee, pref, cardNumber, cardType, awardedPoints, clearNameFlds, paymentType, comments, source);
            }
            logger.info("New Order list size: " + list.size());
        } catch (Exception e) {
            logger.error("Exception -> viewOrderReceiptWs", e);
        }
        return list;
    }

    private Set<OrderDetailDTO> setOrderList(List<Order> dbOrders, String type, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds) throws Exception {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        for (Order order : dbOrders) {
            OrderDetailDTO newOrder = new OrderDetailDTO();
            newOrder.setId(order.getId());
            if (AppUtil.getSafeStr(clearNameFlds, "").equals("1")) {
                newOrder.setFirstName("");
                newOrder.setLastName("");
            } else {
                CommonUtil.populateDecryptedOrderData(newOrder, order);
            }
            if (order.getCreatedOn() != null) {
                newOrder.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
                newOrder.setOrderTime(DateUtil.dateToString(order.getCreatedOn(), "hh:mm:ss"));
            }
            if (order.getOrderStatus() != null && order.getOrderStatus().getId() != null) {
                newOrder.setOrderStatusName(order.getOrderStatus().getName());
                if (order.getOrderStatus().getId() == 8) {
                    newOrder.setOrderType(1);
                } else {
                    newOrder.setOrderType(0);
                }
            }
            newOrder.setStateTax(0f);
            newOrder.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "").length() > 0
                    ? AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "") : "");
            newOrder.setStrength(order.getStrength());
            if (CommonUtil.isNotEmpty(order.getDrugType()) && order.getDrugType().equalsIgnoreCase(Constants.TABLET)) {
                newOrder.setDrugType("TAB");
            } else if (order.getDrugType().equalsIgnoreCase(Constants.CAPSULE)) {
                newOrder.setDrugType("CAP");
            } else {
                newOrder.setDrugType(order.getDrugType());
            }
            newOrder.setQty(order.getQty());
            newOrder.setDrugPrice(order.getDrugPrice());
            newOrder.setRedeemPoints(order.getRedeemPoints());
            newOrder.setRedeemPointsCost(order.getRedeemPointsCost());
            newOrder.setHandLingFee(order.getHandLingFee());
            //String 
            cardNumber = order.getCardNumber();
            logger.info("Card Number is: " + cardNumber);
            if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                logger.info("card number is: " + cardNumber.substring(order.getCardNumber().length() - 4));
                newOrder.setCardNumber(cardNumber.substring(order.getCardNumber().length() - 4));
            } else {
                logger.info("Card Number length is less than 4: " + cardNumber);
                newOrder.setCardNumber(cardNumber);
            }
            if (order.getPayment() != null) {
                if (order.getPayment() > 0d) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    String formatPayment = decimalFormat.format(order.getPayment());
                    logger.info("formatPayment" + formatPayment);
                    newOrder.setPayment(Double.parseDouble(formatPayment));
                } else {
                    newOrder.setPayment(0.00d);
                }
            }

            if (order.getApartment() != null && !"".equals(order.getApartment())) {
                logger.info("Appartmnet is: " + order.getApartment());
                newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getApartment() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
            } else {
                newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
            }

            newOrder.setDeliveryPreferencesName(pref);
            newOrder.setAwardedPoints(awardedPoints);

            getOrderRewardDetail(order.getPatientProfile().getId(), newOrder);
            list.add(newOrder);
        }
        return list;
    }

    private Set<OrderDetailDTO> setOrderList(List<Order> dbOrders, String type, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds, String paymentType, String comments) throws Exception {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        for (Order order : dbOrders) {
            OrderDetailDTO newOrder = new OrderDetailDTO();
            newOrder.setId(order.getId());
            if (AppUtil.getSafeStr(clearNameFlds, "").equals("1")) {
                newOrder.setFirstName("");
                newOrder.setLastName("");
            } else {
                newOrder.setFirstName(order.getFirstName());
                newOrder.setLastName(order.getLastName());
            }
            CommonUtil.populateDecryptedOrderData(newOrder, order);
            if (order.getCreatedOn() != null) {
                newOrder.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
                newOrder.setOrderTime(DateUtil.dateToString(order.getCreatedOn(), "hh:mm:ss"));
            }
            if (order.getOrderStatus() != null && order.getOrderStatus().getId() != null) {
                newOrder.setOrderStatusName(order.getOrderStatus().getName());
                if (order.getOrderStatus().getId() == 8) {
                    newOrder.setOrderType(1);
                } else {
                    newOrder.setOrderType(0);
                }
            }
            newOrder.setStateTax(0f);
            newOrder.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "").length() > 0
                    ? AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "") : "");
            newOrder.setStrength(order.getStrength());
            newOrder.setDrugName(order.getDrugName());
            if (order.getDrugType().equalsIgnoreCase(Constants.TABLET)) {
                newOrder.setDrugType("TAB");
            } else if (order.getDrugType().equalsIgnoreCase(Constants.CAPSULE)) {
                newOrder.setDrugType("CAP");
            } else {
                newOrder.setDrugType(order.getDrugType());
            }
            newOrder.setQty(order.getQty());
            newOrder.setDrugPrice(order.getDrugPrice());
            newOrder.setRedeemPoints(order.getRedeemPoints());
            newOrder.setRedeemPointsCost(order.getRedeemPointsCost());
            newOrder.setHandLingFee(order.getHandLingFee());
            newOrder.setCardType(order.getCardType());
            newOrder.setPaymentType(AppUtil.getSafeStr(paymentType, ""));
            newOrder.setComments(AppUtil.getSafeStr(comments, ""));
            //String 
            cardNumber = order.getCardNumber();
            logger.info("Card Number is: " + cardNumber);
            if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                logger.info("card number is: " + cardNumber.substring(order.getCardNumber().length() - 4));
                newOrder.setCardNumber(cardNumber.substring(order.getCardNumber().length() - 4));
            } else {
                logger.info("Card Number length is less than 4: " + cardNumber);
                newOrder.setCardNumber(cardNumber);
            }
            if (order.getPayment() != null) {
                if (order.getPayment() > 0d) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    String formatPayment = decimalFormat.format(order.getPayment());
                    logger.info("formatPayment" + formatPayment);
                    newOrder.setPayment(Double.parseDouble(formatPayment));
                } else {
                    newOrder.setPayment(0.00d);
                }
            }

            if (order.getApartment() != null && !"".equals(order.getApartment())) {
                logger.info("Appartmnet is: " + order.getApartment());
                newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getApartment() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
            } else {
                newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
            }

            newOrder.setDeliveryPreferencesName(pref);
            newOrder.setAwardedPoints(awardedPoints);

            getOrderRewardDetail(order.getPatientProfile().getId(), newOrder);
            list.add(newOrder);
        }
        return list;
    }

    private Set<OrderDetailDTO> setOrderList(List<Order> dbOrders, String type, String handlingFee, String pref,
            String cardNumber, String cardType, String awardedPoints, String clearNameFlds, String paymentType, String comments, String source) throws Exception {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            logger.info("clearNameFlds: " + clearNameFlds + " pref:: " + pref + " cardNumber:: " + cardNumber + " awardedPoints:: " + awardedPoints + " paymentType:: " + paymentType + " comments:: " + comments + " source: " + source);
            for (Order order : dbOrders) {
                OrderDetailDTO newOrder = new OrderDetailDTO();
                newOrder.setId(order.getId());
                newOrder.setIsCopayCard(order.getAddCopyCard());
                if (AppUtil.getSafeStr(clearNameFlds, "").equals("1")) {
                    newOrder.setFirstName("");
                    newOrder.setLastName("");
                } else {
                    if (order.getPatientProfileMembers() != null) {
                        newOrder.setFirstName(AppUtil.getSafeStr(order.getPatientProfileMembers().getFirstName(), ""));
                        newOrder.setLastName(AppUtil.getSafeStr(order.getPatientProfileMembers().getLastName(), ""));

                    } else {
                        if (order.getPatientProfile() != null) {
                            newOrder.setFirstName(AppUtil.getSafeStr(order.getPatientProfile().getFirstName(), ""));
                            newOrder.setLastName(AppUtil.getSafeStr(order.getPatientProfile().getLastName(), ""));
                        } else {
                            newOrder.setFirstName("");
                            newOrder.setLastName("");
                        }
                    }
                }
                CommonUtil.populateDecryptedOrderData(newOrder, order);
                if (order.getCreatedOn() != null) {
                    newOrder.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
                    newOrder.setOrderTime(DateUtil.dateToString(order.getCreatedOn(), "hh:mm:ss"));
                }
                if (order.getOrderStatus() != null && order.getOrderStatus().getId() != null) {
                    newOrder.setOrderStatusName(order.getOrderStatus().getName());
                    if (order.getOrderStatus().getId() == 8) {
                        newOrder.setOrderType(1);
                    } else {
                        newOrder.setOrderType(0);
                    }
                }
                newOrder.setStateTax(0f);
                newOrder.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "").length() > 0
                        ? AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "") : "");
                newOrder.setStrength(order.getStrength());
                newOrder.setDrugName(order.getDrugName());
                if (CommonUtil.isNotEmpty(order.getDrugType())) {
                    if (order.getDrugType().equalsIgnoreCase(Constants.TABLET)) {
                        newOrder.setDrugType("TAB");
                    } else if (order.getDrugType().equalsIgnoreCase(Constants.CAPSULE)) {
                        newOrder.setDrugType("CAP");
                    }
                } else {
                    newOrder.setDrugType(order.getDrugType());
                }
                newOrder.setQty(order.getQty());
                newOrder.setDrugPrice(order.getDrugPrice());
                newOrder.setRedeemPoints(order.getRedeemPoints());
                String redeemCostStr = "0.0";
                if (order.getRedeemPointsCost() != null) {
                    redeemCostStr = AppUtil.roundOffNumberToTwoDecimalPlaces(order.getRedeemPointsCost());
                }
                newOrder.setRedeemPointsCost(AppUtil.getSafeDouble(redeemCostStr, 0d));//order.getRedeemPointsCost());
                newOrder.setHandLingFee(order.getHandLingFee());
                newOrder.setCardType(order.getCardType());
                newOrder.setPaymentType(AppUtil.getSafeStr(paymentType, ""));
                newOrder.setComments(AppUtil.getSafeStr(comments, ""));
                newOrder.setSource(AppUtil.getSafeStr(source, ""));
                //String 
                cardNumber = order.getCardNumber();
                logger.info("Card Number is: " + cardNumber);
                if (cardNumber != null && !"".equals(cardNumber) && cardNumber.length() > 4) {
                    logger.info("card number is: " + cardNumber.substring(order.getCardNumber().length() - 4));
                    newOrder.setCardNumber(cardNumber.substring(order.getCardNumber().length() - 4));
                } else {
                    logger.info("Card Number length is less than 4: " + cardNumber);
                    newOrder.setCardNumber(cardNumber);
                }
                if (order.getPayment() != null) {
                    if (order.getPayment() > 0d) {
                        DecimalFormat decimalFormat = new DecimalFormat("#.###");
                        String formatPayment = decimalFormat.format(order.getPayment());
                        logger.info("formatPayment" + formatPayment);
                        newOrder.setPayment(Double.parseDouble(formatPayment));
                    } else {
                        newOrder.setPayment(0.00d);
                    }
                }

                if (order.getApartment() != null && !"".equals(order.getApartment())) {
                    logger.info("Appartmnet is: " + order.getApartment());
                    newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getApartment() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
                } else {
                    newOrder.setShippingAddress(order.getStreetAddress() + " " + order.getCity() + ", " + order.getState() + " " + order.getZip());
                }
                if (CommonUtil.isNotEmpty(pref)) {
                    String name = AppUtil.getSafeStr(pref, "").toLowerCase();
                    if (name.contains("2nd day")) {
                        newOrder.setHandLingFee(0.0d);
                        newOrder.setIncludedStr("Included");
                    } else {
                        newOrder.setIncludedStr("");
                    }
                }

                newOrder.setDeliveryPreferencesName(pref);
                if (CommonUtil.isNotEmpty(awardedPoints)) {
                    newOrder.setAwardedPoints(awardedPoints);
                }
                if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
                    newOrder.setOrderChainId(order.getOrderChain().getId());
                }
                DrugDetail drugDetail = order.getDrugDetail();
                if (drugDetail != null && drugDetail.getDrugDetailId() != null && drugDetail.getDrugDetailId() > 0) {
                    DrugBasic drugBasic = drugDetail.getDrugBasic();
                    DrugGeneric drugGeneric = drugBasic.getDrugGeneric();
                    if (drugGeneric != null && drugGeneric.getDrugGenericID() != null) {
                        newOrder.setBrandName(drugBasic.getBrandName());
                    }
                    if (drugGeneric != null && drugGeneric.getDrugGenericID() != null) {
                        newOrder.setGenericName(drugGeneric.getGenericName());
                    }
                }
                newOrder.setDaysToRefill("No Refillable yet.");
                if (order.getPatientProfile() != null && order.getPatientProfile().getId() != null) {
                    getOrderRewardDetail(order.getPatientProfile().getId(), newOrder);
                }
                newOrder.setMiles(order.getMiles());
                newOrder.setPharmacyName(AppUtil.getSafeStr(order.getPharmacyName(), ""));
                newOrder.setPharmacyPhone(AppUtil.getSafeStr(order.getPharmacyPhone(), ""));
                newOrder.setPrescriberName(AppUtil.getSafeStr(order.getPrescriberLastName(), ""));
                newOrder.setPrescriberPhone(AppUtil.getSafeStr(order.getPrescriberPhone(), ""));
                List<OrderTransferImagesDTO> orderTransferImagesDTOs = populateOrderTransferImages(order);
                newOrder.setOrderTransferImages(orderTransferImagesDTOs);
                newOrder.setOrigRx(AppUtil.getSafeStr(order.getOldRxNumber(), ""));
                newOrder.setIsBottleAvailable(order.getIsBottleAvailable() == null ? Boolean.FALSE : order.getIsBottleAvailable());
                System.out.println("Pharmacy phone " + AppUtil.getSafeStr(newOrder.getPharmacyPhone(), ""));
                System.out.println("ORIG RX " + AppUtil.getSafeStr(newOrder.getOrigRx(), ""));
                DrugDetail detail = order.getDrugDetail();
                if (detail != null) {
                    newOrder.setImageURL(AppUtil.getSafeStr(detail.getImgUrl(), ""));
                    newOrder.setDrugDocURL(AppUtil.getSafeStr(detail.getDrugDocUrl(), ""));
                    newOrder.setPatientDocURL(AppUtil.getSafeStr(detail.getPdfDocUrl(), ""));
                    System.out.println("IMAGE " + detail.getImgUrl() + " drug doc " + detail.getDrugDocUrl() + " patient doc" + detail.getPdfDocUrl());
                }
                list.add(newOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception::setOrderList:: ", e);
        }
        return list;
    }

    public PatientAddress getPatientAddressById(int addressId) {
        PatientAddress address = null;
        try {
            address = this.patientProfileDAO.getPatientAddressById(addressId);
        } catch (Exception ex) {
            Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }

    public PatientDeliveryAddress getPatientDeliveryAddressById(int addressId) {
        PatientDeliveryAddress address = null;
        try {
            address = this.patientProfileDAO.getPatientDeliveryAddressById(addressId);
        } catch (Exception ex) {
            Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }

    public DeliveryPreferences getDeliveryPreferenceById(int prefId) {
        DeliveryPreferences pref = null;
        try {
            pref = this.patientProfileDAO.getDeliveryPreferenceById(prefId);
        } catch (Exception ex) {
            Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pref;
    }

    public PatientDeliveryAddress getDefaultDeliveryPreference(int patientId) {
        PatientDeliveryAddress address = null;
        try {
            address = this.patientProfileDAO.getDefaultPatientDeliveryAddress(patientId);
        } catch (Exception ex) {
            Logger.getLogger(PatientProfileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }

    /////////////////////////////////////////////////////////////////////////////
    private void populateOrderDetail(String patientId, List lst, List<OrderDetailDTO> orderDetailLst) throws Exception {
        if (orderDetailLst == null) {
            orderDetailLst = new ArrayList();
        }
        for (Object obj : lst) {
            OrderDetailDTO dto = new OrderDetailDTO();
            Order order = (Order) obj;

            String type = AppUtil.getSafeStr(order.getDrugType(), "");
            if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.TABLET)) {
                type = "TAB";
            } else if (AppUtil.getSafeStr(order.getDrugType(), "").equalsIgnoreCase(Constants.CAPSULE)) {
                type = "CAP";
            }
            dto.setId(order.getId());
            dto.setPatientId(patientId);
            dto.setDrugPrice(order.getDrugPrice() != null ? order.getDrugPrice() : 0d);
            dto.setOrderDate(DateUtil.dateToString(order.getCreatedOn(), "MM/dd/yyyy"));
            dto.setStatusCreatedOn(order.getUpdatedOn());//.getStatusCreatedOn());
            Hibernate.initialize(order.getTransferDetail());
            dto.setRxNumber(AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "").length() > 0
                    ? AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), "") : "");
            dto.setRedeemPoints(order.getRedeemPoints());
            dto.setRedeemPointsCost(order.getRedeemPointsCost());
            dto.setDrugName(AppUtil.getSafeStr(order.getDrugName(), "") + " " + AppUtil.getSafeStr(order.getStrength(), ""));
            dto.setRefillsRemaining(order.getOrderChain() != null ? order.getOrderChain().getRefillRemaing() + "" : "0");
            dto.setDaysSupply(order.getOrderChain() != null && order.getOrderChain().getDaysSupply() != null ? order.getOrderChain().getDaysSupply() : 0);//order.getDaysSupply() != null && order.getDaysSupply() > 0 ? order.getDaysSupply() : 0);
            dto.setQty(order.getQty());
            dto.setHandLingFee(order.getHandLingFee());
            dto.setPayment(order.getPayment());
            dto.setFinalPayment(order.getFinalPayment() != null ? order.getFinalPayment() : 0d);
            dto.setPointsEarned(order.getRewardHistorySet() != null && order.getRewardHistorySet().size() > 0 && order.getRewardHistorySet().get(0) != null && order.getRewardHistorySet().get(0).getPoint() != null ? order.getRewardHistorySet().get(0).getPoint() : 0);
            dto.setRxAcqCost(order.getRxAcqCost() != null ? order.getRxAcqCost() : 0d);
            dto.setRxReimbCost(order.getRxReimbCost() != null ? order.getRxReimbCost() : 0d);
            dto.setOriginalPtCopay(order.getOriginalPtCopay() != null ? order.getOriginalPtCopay() : 0d);
            dto.setAdditionalMargin(order.getAdditionalMargin() != null ? order.getAdditionalMargin() : 0d);
            dto.setProfitMargin(order.getProfitMargin() != null ? order.getProfitMargin() : 0d);
            if (dto.getDrugPrice() != null && dto.getAdditionalMargin() != null) {
                dto.setAmountWithoutMargin(dto.getDrugPrice() - dto.getAdditionalMargin());
            } else {
                dto.setAmountWithoutMargin(0d);
            }
//            if (order.getFilledDate() != null && order.getRefillsRemaining() != null && order.getRefillsRemaining() > 0) {
//                dto.setNextRefillDate(DateUtil.addDaysToDate(order.getFilledDate(), dto.getDaysSupply()));
//            }
            if (order.getNextRefillDate() != null) {
                dto.setNextRefillDate(order.getNextRefillDate());//DateUtil.addDaysToDate(order.getFilledDate(), dto.getDaysSupply()));
            }
            dto.setFilledDate(order.getFilledDate());
            dto.setOrderStatusName(order.getOrderStatus().getName());
            //////////////////////////////////////////////
            try {
                String videoStr = AppUtil.getSafeStr(order.getVideo(), "").replace("localhost", Constants.APP_PATH_NON_SECURE);//"rxdirectdev.ssasoft.com");
                if (videoStr.length() > 0 && !videoStr.contains("http://") && !videoStr.contains("https://")) {
                    videoStr = "http://" + videoStr;
                }
                dto.setPtVideo(videoStr);
                String imgStr = AppUtil.getSafeStr(order.getImagePath(), "").replace("localhost", Constants.APP_PATH_NON_SECURE);// "rxdirectdev.ssasoft.com");
                if (imgStr.length() > 0 && !imgStr.contains("http://") && !imgStr.contains("https://")) {
                    imgStr = "http://" + imgStr;
                }
                dto.setTransferImage(imgStr);

                ////////////////////////////////////////////////////////
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Exception:: populateOrderDetail:: ", e);
            }
            /////////////////////////////////////////////

            orderDetailLst.add(dto);
            //dto.setAwardedPoints(order.getAwardedPoints());

        }
    }

    public List getPatientProfilesHistoryOtherThanPending(int patientId) throws Exception {
        List lst = this.patientProfileDAO.getPatientProfilesHistoryOtherThanPending(patientId);
        List<OrderDetailDTO> orderDetailLst = new ArrayList();
        populateOrderDetail("" + patientId, lst, orderDetailLst);
        return orderDetailLst;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public List getPatientProfilesHistory(int patientId, String status) throws Exception {
        List<Order> lst = this.patientProfileDAO.getPatientProfilesHistory(patientId, status);
        List<Order> lst2 = new ArrayList<>();
        for (Order order : lst) {
            try {
                Hibernate.initialize(order.getPatientProfileMembers());
            } catch (ObjectNotFoundException oe) {
                order.setPatientProfileMembers(null);
            }
            if (order.getPatientProfileMembers() != null && order.getPatientProfileMembers().getIsAdult() && !order.getPatientProfileMembers().getIsApproved()) {
//                lst.remove(order);
            } else {
                lst2.add(order);
            }
        }
        List<OrderDetailDTO> orderDetailLst = new ArrayList();
        populateOrderDetail("" + patientId, lst2, orderDetailLst);
        return orderDetailLst;
    }

    public String getRequestVideo(String id) {
        TransferRequest req = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(id, 0));
        return (req != null ? req.getVideo() : "");

    }

    public TransferRxQueueDTO getRequestVideoAndImage(String id) {
        TransferRequest req = (TransferRequest) this.patientProfileDAO.getObjectById(new TransferRequest(), AppUtil.getSafeInt(id, 0));
        TransferRxQueueDTO transfer = null;
        if (req != null) {
            transfer = new TransferRxQueueDTO();
            transfer.setTransferImage(AppUtil.getSafeStr(req.getDrugImg(), ""));
            transfer.setPtVideo(AppUtil.getSafeStr(req.getVideo(), ""));
        }
        return transfer;

    }

    ////////////////////////////////////////////////////////////////////////////////////
    public List<NotificationMessages> getNotificationForWaitingResponsesWs(Integer profileId) throws Exception {
        List<NotificationMessages> list = new ArrayList<>();
        List<NotificationMessages> listFromDb = patientProfileDAO.
                getNotificationMessagesListForWaitingResponses(profileId);
        for (NotificationMessages messages : listFromDb) {
            NotificationMessages notificationMessages = getNotificationMessages(messages);
//            notificationMessages.setMessageText(null);
            notificationMessages.setIsCritical(messages.getIsCritical());
            list.add(notificationMessages);
        }
        return list;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    public List<NotificationMessages> getNotificationWs(Integer profileId) {
        List<NotificationMessages> list = new ArrayList<>();
        try {
            List<NotificationMessages> lstDb = patientProfileDAO.getNotificationMessagesByProfileId(profileId);
            for (NotificationMessages messages : lstDb) {
                NotificationMessages notificationMessages = getNotificationMessages(messages);
                notificationMessages.setMessageText(null);
                notificationMessages.setIsCritical(messages.getIsCritical());
                list.add(notificationMessages);
            }

            //////Pharmacy Notification Messages///////////////////
            for (OrdersPtMessage messages : patientProfileDAO.getPharmacyNotificationMessagesByProfileId(profileId)) {
                NotificationMessages notificationMessages = getPharmacyNotification(messages);
                notificationMessages.setMessageText(null);
                notificationMessages.setIsCritical(messages.getIsCritical());
                list.add(notificationMessages);
            }
            Collections.sort(list, CommonUtil.byDate);
        } catch (Exception e) {
            logger.error("Exception -> getNotificationWs", e);
            e.printStackTrace();
        }
        return list;
    }

    public JSONObject getNotificationMessagesTextById(Integer id) {
        JSONObject messagesJSON = new JSONObject();
        try {
            if (id != null) {
                List<NotificationMessages> dbList = patientProfileDAO.getNotificationMessagesListById(id);
                RewardPoints cRewardPoints = (RewardPoints) this.patientProfileDAO.findByPropertyUnique(new RewardPoints(), "type", "Read Critical Message", Constants.HIBERNATE_EQ_OPERATOR, "", 0);
                RewardPoints sRewardPoints = (RewardPoints) this.patientProfileDAO.findByPropertyUnique(new RewardPoints(), "type", "Read Standard Message", Constants.HIBERNATE_EQ_OPERATOR, "", 0);
                if (dbList != null && dbList.size() > 0) {

                    for (NotificationMessages messages : dbList) {
                        RewardHistory rewardHistory = new RewardHistory();
                        if (!messages.getIsRead()) {
                            if (messages.getIsCritical() != null && messages.getIsCritical() == 1) {
                                messages.setPointsAwarded(cRewardPoints.getPoint().intValue());
                                rewardHistory.setPatientId(messages.getPatientProfile().getId());
                                rewardHistory.setDescription(cRewardPoints.getType());
                                rewardHistory.setType("PLUS");
                                rewardHistory.setPoint(cRewardPoints.getPoint().intValue());
                                rewardHistory.setOrder(messages.getOrders());
                            } else {
                                messages.setPointsAwarded(sRewardPoints.getPoint().intValue());
                                rewardHistory.setPatientId(messages.getPatientProfile().getId());
                                rewardHistory.setDescription(sRewardPoints.getType());
                                rewardHistory.setType("PLUS");
                                rewardHistory.setPoint(sRewardPoints.getPoint().intValue());
                                rewardHistory.setOrder(messages.getOrders());
                            }
                        }
                        messages.setIsRead(Boolean.TRUE);
                        this.patientProfileDAO.save(rewardHistory);
                        this.patientProfileDAO.update(messages);
                        NotificationMessages notificationMessages = getNotificationMessages(messages);
                        messagesJSON = getMessageJsonObject(notificationMessages);
                    }
                } else {
                    //////Pharmacy Notification Messages///////////////////
                    for (OrdersPtMessage messages : patientProfileDAO.getPharmacyNotificationMessagesById(id)) {
                        RewardHistory rewardHistory = new RewardHistory();
                        if (!messages.getIsRead()) {
                            if (messages.getIsCritical() != null && messages.getIsCritical() == 1) {
                                messages.setPointsAwarded(cRewardPoints.getPoint().intValue());
                                rewardHistory.setPatientId(messages.getPatientProfile().getId());
                                rewardHistory.setDescription(cRewardPoints.getType());
                                rewardHistory.setType("PLUS");
                                rewardHistory.setPoint(cRewardPoints.getPoint().intValue());
                                rewardHistory.setOrder(messages.getOrder());
                            } else {
                                messages.setPointsAwarded(sRewardPoints.getPoint().intValue());
                                rewardHistory.setPatientId(messages.getPatientProfile().getId());
                                rewardHistory.setDescription(sRewardPoints.getType());
                                rewardHistory.setType("PLUS");
                                rewardHistory.setPoint(sRewardPoints.getPoint().intValue());
                                rewardHistory.setOrder(messages.getOrder());
                            }
                        }
                        messages.setIsRead(Boolean.TRUE);
                        this.patientProfileDAO.save(rewardHistory);
                        this.patientProfileDAO.update(messages);//.saveOrUpdate(messages);
                        NotificationMessages notificationMessages = getPharmacyNotification(messages);
                        messagesJSON = getMessageJsonObject(notificationMessages);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getNotificationMessagesTextById", e);
        }
        return messagesJSON;
    }

    private JSONObject getMessageJsonObject(NotificationMessages notificationMessages) {
        JSONObject messagesJSON = new JSONObject();
        messagesJSON.put("id", notificationMessages.getId());
        messagesJSON.put("messageCategory", notificationMessages.getMessageCategory());
        messagesJSON.put("subject", EncryptionHandlerUtil.getDecryptedString(notificationMessages.getSubject()));
        messagesJSON.put("messageText", EncryptionHandlerUtil.getDecryptedString(notificationMessages.getMessageText()));
        messagesJSON.put("isRead", notificationMessages.getIsRead());
        messagesJSON.put("messageTypeId", notificationMessages.getMessageTypeId());
        messagesJSON.put("profileId", notificationMessages.getProfileId());
        messagesJSON.put("orderId", notificationMessages.getOrderId());
        messagesJSON.put("createdOn", notificationMessages.getCreatedOn());
        return messagesJSON;
    }

    public OrderChain saveOrderChain(Integer drugDetailId) {
        OrderChain orderChain = new OrderChain();
        try {
            orderChain.setRefillAllow(0);
            orderChain.setRefillDayes(0);
            orderChain.setRefillRemaing(0);
            DrugDetail drugDetail = (DrugDetail) patientProfileDAO.findRecordById(new DrugDetail(), drugDetailId);
            orderChain.setDrugDetail(drugDetail);
            orderChain.setLastRefillDate(new Date());
            orderChain.setNextRefillDate(new Date());
            orderChain.setCreatedBy(0);
            orderChain.setCreatedOn(new Date());
            orderChain.setUpdatedBy(0);
            orderChain.setUpdatedOn(new Date());
            patientProfileDAO.save(orderChain);
        } catch (Exception e) {
        }
        return orderChain;
    }

    public String getCalculateFinalPtPay(String redeemPoints, Integer patinetId, String orderId, String copay) {
        String json = "";
        try {
            Order order = (Order) getObjectById(new Order(), orderId);
            if (CommonUtil.isNotEmpty(redeemPoints)) {
                saveRewardHistory("Redeemed Compliance Rewards", Integer.parseInt(redeemPoints), patinetId, Constants.PLUS, order);
            }
            DrugDetail drugDetail = new DrugDetail();
            PatientProfile patientProfile = getPatientProfileById(patinetId);
            RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());

            FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
            Double totalCopayPoints = (1 / feeSettings.getFee().doubleValue()) * AppUtil.getSafeDouble(copay, 0.0d);
            logger.info("totalCopayPoints:: " + totalCopayPoints);
            if (rewardPoints.getAvailablePoints() >= totalCopayPoints) {
                saveRewardHistory("Redeemed Compliance Rewards", totalCopayPoints.intValue(), patinetId, Constants.MINUS, order);
                drugDetail.setRedeemedPoints(totalCopayPoints.longValue());
                totalCopayPoints = feeSettings.getFee().doubleValue() * totalCopayPoints;
                drugDetail.setRedeemedPointsPrice(Float.parseFloat(CommonUtil.getDecimalFormat(totalCopayPoints)));
            } else {
                saveRewardHistory("Redeemed Compliance Rewards", rewardPoints.getAvailablePoints().intValue(), patinetId, Constants.MINUS, order);
                drugDetail.setRedeemedPoints(rewardPoints.getAvailablePoints());
                Double rewardPointCost = feeSettings.getFee().doubleValue() * rewardPoints.getAvailablePoints();
                drugDetail.setRedeemedPointsPrice(Float.parseFloat(CommonUtil.getDecimalFormat(rewardPointCost)));
            }
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(drugDetail);
        } catch (Exception e) {
            logger.error("Exception:: getCalculateFinalPtPay", e);
        }
        return json;
    }

    public Long getTotalRewardHistoryPoint(String type, String orderId, Integer patientId) {
        Long totalPoints = 0L;
        try {
            if (patientId != null) {
                totalPoints = patientProfileDAO.getTotalRewardHistoryPointByType(type, patientId);
            } else if (CommonUtil.isNotEmpty(orderId)) {
                totalPoints = patientProfileDAO.getTotalRewardHistoryPointByOrderId(type, orderId);
            }
        } catch (Exception e) {
            logger.error("Exception:: getTotalRewardHistoryPoint", e);
        }
        return totalPoints;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean saveNotificationMessages(CampaignMessages campaignMessages, Integer profileId, String orderId, Integer isCritical) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(AppUtil.getSafeStr(campaignMessages.getSubject(), ""));
                notificationMessages.setMessageText(campaignMessages.getSmstext());

                if (isCritical > 0) {
                    isCritical = 1;
                }
                notificationMessages.setIsCritical(isCritical);
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            //////////////////////////////////
            Order order = (Order) this.getObjectById(new Order(), orderId);
            notificationMessages.setOrders(order);
            /////////////////////////////////
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setPushSubject(AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""));
            notificationMessages.setIsEventFire(false);
            patientProfileDAO.save(notificationMessages);
            this.sendPushNotifications(profileId, orderId, "" + notificationMessages.getId(),
                    AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), "RXD");
            isSaved = true;
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendPushNotifications(Integer profileId, String orderId, String id, String subject, String prefix) {
        try {
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null) {
                System.out.println("Profile for sending push " + profile.getFirstName());
                String osType = AppUtil.getSafeStr(profile.getOsType(), "20");
                if (osType.equals("20"))//android
                {
                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), orderId, id, prefix, subject, profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), orderId, id, prefix, subject, profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> saveRefillModule(Long orderChainId) throws Exception {
        Set<OrderDetailDTO> detailDTOs = new LinkedHashSet<>();

        RewardPoints rewardPoints = (RewardPoints) this.patientProfileDAO.findByPropertyUnique(new RewardPoints(), "type", "Rx Refill", Constants.HIBERNATE_EQ_OPERATOR, "", 0);

        List<Order> orderLst = this.orderDAO.getDeliveredOrderList(orderChainId);
        if (CommonUtil.isNullOrEmpty(orderLst)) {
            throw new Exception("No  orders has been DELIVERED/SHIPPED for this prescription.");
        }
        Order dbOrder = orderLst.get(0);//(Order) patientProfileDAO.findByPropertyUnique(new Order(), "orderChain.id", orderChainId, Constants.HIBERNATE_EQ_OPERATOR, "createdOn", Constants.HIBERNATE_DESC_ORDER);
        if (dbOrder.getRefillsAllowed() == null || dbOrder.getRefillsAllowed() == 0) {
            throw new Exception("Refill is not allowed.");
        }
        if (dbOrder.getRefillsRemaining() == null || dbOrder.getRefillsRemaining() == 0) {
            throw new Exception("No Refills remaining.");
        }
        if (dbOrder != null && dbOrder.getId() != null) {
            //dbOrder.setOrderType(Constants.REFILL);
            Order ord = new Order();
            BeanUtils.copyProperties(dbOrder, ord);
            OrderStatus status = new OrderStatus();
            status.setId(1);
            ord.setOrderStatus(status);
//            ord.setOrderHistory(null);
//            ord.setOrdersPtMessagesList(null);
//            ord.setRewardHistorySet(null);
            //                if (ord.getRefillsAllowed() != null && ord.getRefillsAllowed() > 0) {
//                    int refillRemining = ord.getRefillsAllowed() - 1;
//                    ord.setRefillsRemaining(refillRemining);
//                }
//            ord.setCoPayCardDetails(null);
            ord.setOrderType(Constants.REFILL);
            ord.setCreatedOn(new Date());
            ord.setRefillDone(1);
            patientProfileDAO.save(ord);

            OrderChain orderChain = ord.getOrderChain();
            if (orderChain != null) {
                orderChain.setRefillAllow(ord.getRefillsAllowed());
                orderChain.setRefillRemaing(ord.getRefillsRemaining() != null && ord.getRefillsRemaining() > 0 ? ord.getRefillsRemaining() - 1 : 0);
            }

            OrderHistory orderHistory = new OrderHistory();
            OrderHistory dbOrderHistory = (OrderHistory) patientProfileDAO.findByPropertyUnique(new OrderHistory(), "order.id", dbOrder.getId(), Constants.HIBERNATE_EQ_OPERATOR);
            if (dbOrderHistory != null && dbOrderHistory.getId() != null) {
                BeanUtils.copyProperties(dbOrderHistory, orderHistory);
                orderHistory.setOrder(ord);
                patientProfileDAO.save(orderHistory);
            }
            List<Order> orderlist = new ArrayList<>();
            orderlist.add(ord);
            detailDTOs = setOrderList(orderlist, null);
        }

        return detailDTOs;
    }

    public String saveRefillModuleFromWeb(String commaSeparatedOrderIds, int createdBy) throws Exception {
        String[] arr = commaSeparatedOrderIds.split(",");
        boolean flag = false;
        String id = "0";
        for (int i = 0; arr != null && i < arr.length; i++) {
            Order dbOrder = (Order) this.patientProfileDAO.findRecordById(new Order(), arr[i]);
            Set<OrderDetailDTO> dtos = this.saveRefillModule(dbOrder, false, createdBy);
            if (!flag) {
                id = dtos.stream().findFirst().get().getId();
                flag = true;
            }
        }
        return id;
    }

    public Set<OrderDetailDTO> saveRefillModule(List<OrderDetailDTO> lstOrder, Integer patientId) throws Exception {
        Set orderSet = new HashSet();
        OrderBatch orderBatch = new OrderBatch();
        orderBatch.setCreatedOn(new Date());
        PatientProfile profile = new PatientProfile(patientId);
        orderBatch.setPatientProfile(profile);
        this.patientProfileDAO.saveOrUpdate(orderBatch);
        for (OrderDetailDTO dto : lstOrder) {
//            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(
//                    new PatientDeliveryAddress(), dto.getDeliveryAddressId());
            Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), dto.getId());
            if (order != null) {
//                order.setQty(dto.getQty());
//                order.setOrderBatch(orderBatch);
//                if (dto.getDeliveryPreferenceId() != null && dto.getDeliveryPreferenceId() > 0) {
//                    order.setDeliveryPreference(new DeliveryPreferences(dto.getDeliveryPreferenceId()));
//                }
//                if (dto.getPaymentId() > 0) {
//                    order.setPaymentId(dto.getPaymentId());
//                }
//                /////////////////////////////////////////////////////////////
//                if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null
//                        && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
//                    logger.info("Set shipping address info. ");
//                    order.setStreetAddress(patientDeliveryAddress.getAddress());
//                    order.setCity(patientDeliveryAddress.getCity());
//                    order.setZip(patientDeliveryAddress.getZip());
//                    order.setAddressLine2(patientDeliveryAddress.getDescription());
//                    order.setApartment(patientDeliveryAddress.getApartment());
//                    if (patientDeliveryAddress.getState() != null
//                            && patientDeliveryAddress.getState().getId() != null) {
//                        order.setState(patientDeliveryAddress.getState().getName());
//                    }
//                }
                ////////////////////////////////////////////////////////////
                Set<OrderDetailDTO> set = this.saveRefillModule(order, dto, patientId, orderBatch);
                if (set != null && set.size() > 0) {
                    orderSet.add(set.iterator().next());
                }

//                order.setPaymentId(dto.);
                //this
            }
        }
        return orderSet;

    }

    public Set<OrderDetailDTO> saveRefillModule(Order dbOrder, OrderDetailDTO dto, Integer patientId,
            OrderBatch orderBatch) throws Exception {

        //RewardPoints rewardPoints =(RewardPoints)this.patientProfileDAO.findByPropertyUnique(new RewardPoints(),"type","Rx Refill",Constants.HIBERNATE_EQ_OPERATOR,"",0);
        //List<Order> orderLst=this.orderDAO.getDeliveredOrderList(orderChainId);
        if (dbOrder == null || (!dbOrder.getOrderStatus().getName().equalsIgnoreCase("DELIVERY")
                && !dbOrder.getOrderStatus().getName().equalsIgnoreCase("Shipped")
                && !dbOrder.getOrderStatus().getName().equalsIgnoreCase("Pickup At Pharmacy")
                && !dbOrder.getOrderStatus().getName().equalsIgnoreCase("Filled"))) {
            throw new Exception("Only delivered/shipped orders can be Refilled.");
        }

        if (dbOrder.getRefillsAllowed() == null || dbOrder.getRefillsAllowed() == 0) {
            throw new Exception("Refill is not allowed.");
        }
        if (dbOrder.getRefillsRemaining() == null || dbOrder.getRefillsRemaining() == 0) {
            throw new Exception("No Refills remaining.");
        }
        return this.saveRefillModule(dbOrder, dto, true, 0, patientId, orderBatch);

    }

    public Set<OrderDetailDTO> saveRefillModule(Long orderChainId, String orderId) throws Exception {

        //RewardPoints rewardPoints =(RewardPoints)this.patientProfileDAO.findByPropertyUnique(new RewardPoints(),"type","Rx Refill",Constants.HIBERNATE_EQ_OPERATOR,"",0);
        //List<Order> orderLst=this.orderDAO.getDeliveredOrderList(orderChainId);
        Order dbOrder = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);

        if (dbOrder == null || (!dbOrder.getOrderStatus().getName().equalsIgnoreCase("DELIVERY")
                && !dbOrder.getOrderStatus().getName().equalsIgnoreCase("Shipped"))) {
            throw new Exception("Only delivered/shipped orders can be Refilled.");
        }

        if (dbOrder.getRefillsAllowed() == null || dbOrder.getRefillsAllowed() == 0) {
            throw new Exception("Refill is not allowed.");
        }
        if (dbOrder.getRefillsRemaining() == null || dbOrder.getRefillsRemaining() == 0) {
            throw new Exception("No Refills remaining.");
        }
        return this.saveRefillModule(dbOrder, true, 0);

    }

    /**
     *
     * @param dbOrder order for which refill has to be carried out
     * @param refillViaApp true in case of placing order from app, false
     * otherwise.
     * @param createdBy user going to create refill, it will be 0 when refill
     * request is made from app
     * @return
     * @throws Exception
     */
    public Set<OrderDetailDTO> saveRefillModule(Order dbOrder, boolean refillViaApp, int createdBy) throws Exception {
        Set<OrderDetailDTO> detailDTOs = new LinkedHashSet<>();
        DrugDetail detail = null;
        if (dbOrder != null && dbOrder.getId() != null) {
            //dbOrder.setOrderType(Constants.REFILL);
            dbOrder.setRefillDone(1);
            this.patientProfileDAO.saveOrUpdate(dbOrder);//.update(dbOrder);
//            dbOrder.setMessageResponseses(null);
//            dbOrder.setOrderTransferImages(null);
//            dbOrder.setNotificationMessageses(null);
//            dbOrder.setOrderCustomDocumentses(null);
            Order ord = new Order();
            BeanUtils.copyProperties(dbOrder, ord);
            if (dbOrder.getDrugDetail() != null) {
                detail = getGenericBasedDrugDetailInfo(dbOrder.getDrugDetail().getDrugDetailId(), AppUtil.getSafeInt(dbOrder.getQty(), 0), dbOrder.getPatientProfile());
                if (detail != null) {
                    ord.setDrugPrice(1d * detail.getDrugCost());//detail.getBasePrice() * AppUtil.getSafeInt(ord.getQty(),0));
                    ord.setPriceIncludingMargins(1d * detail.getTotalPrice());
                    if (!CommonUtil.isNullOrEmpty(detail.getRedeemedPoints())) {
                        ord.setRedeemPoints(detail.getRedeemedPoints().toString());
                    }
                    if (detail.getRedeemedPointsPrice() != null) {
                        ord.setRedeemPointsCost(1d * detail.getRedeemedPointsPrice());
                    }
                }
            }
            OrderStatus status = new OrderStatus();
            status.setId(1);
            ord.setOrderStatus(status);
//          ord.setMessageResponseses(null);
            ord.setOrderTransferImages(null);
//            ord.setNotificationMessageses(null);
//            ord.setOrderCustomDocumentses(null);
            ord.setOrdersPtMessagesList(null);
            ord.setRewardHistorySet(null);
            ord.setCreatedBy(createdBy);
//                if (ord.getRefillsAllowed() != null && ord.getRefillsAllowed() > 0) {
//                    int refillRemining = ord.getRefillsAllowed() - 1;
//                    ord.setRefillsRemaining(refillRemining);
//                }
            ord.setCoPayCardDetails(null);
            ord.setOrderType(Constants.REFILL);
            ord.setCreatedOn(new Date());
            ord.setShippedOn(null);
            ord.setShippedBy(null);
            ord.setTraclingno("");
            ord.setClerk("");
            ord.setDeliverycarrier("");
            ord.setRefillDone(0);

            OrderChain orderChain = ord.getOrderChain();
            if (orderChain != null) {
//                orderChain.setRefillAllow(ord.getRefillsAllowed());
                orderChain.setRefillRemaing(orderChain.getRefillRemaing() > 0 ? orderChain.getRefillRemaing() - 1 : 0);
            }
            List<OrderHistory> orderHistorys = populateOrderHistory(dbOrder, ord, status);
            if (!CommonUtil.isNullOrEmpty(orderHistorys)) {
                ord.setOrderHistory(orderHistorys);
            } else {
                ord.setOrderHistory(null);
            }
            patientProfileDAO.save(ord);
            patientProfileDAO.saveOrUpdate(orderChain);
            /////////////////////////////////
            if (refillViaApp) {
                ord.setAwardedPoints(saveRewardOrderHistory(ord.getRedeemPoints(), ord.getPatientProfile(), ord, "Rx Refill", 6));
            }
            /////////////////////////////////
            List<Order> orderlist = new ArrayList<>();
            orderlist.add(ord);
            detailDTOs = setOrderList2(orderlist, null, "");
        }

        return detailDTOs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public Set<OrderDetailDTO> saveRefillModule(Order dbOrder, OrderDetailDTO dto, boolean refillViaApp,
            int createdBy, Integer patientId, OrderBatch orderBatch) throws Exception {
        Set<OrderDetailDTO> detailDTOs = new LinkedHashSet<>();
        DrugDetail detail = null;
//        OrderBatch orderBatch = new OrderBatch();
//        orderBatch.setCreatedOn(new Date());
//        PatientProfile profile = new PatientProfile(patientId);
//        orderBatch.setPatientProfile(profile);
//        this.patientProfileDAO.saveOrUpdate(orderBatch);
        if (dbOrder != null && dbOrder.getId() != null) {
            //dbOrder.setOrderType(Constants.REFILL);
            dbOrder.setRefillDone(1);
            this.patientProfileDAO.saveOrUpdate(dbOrder);//.update(dbOrder);
//            dbOrder.setMessageResponseses(null);
//            dbOrder.setOrderTransferImages(null);
//            dbOrder.setNotificationMessageses(null);
//            dbOrder.setOrderCustomDocumentses(null);
            Order ord = new Order();
            BeanUtils.copyProperties(dbOrder, ord);
            if (dbOrder.getDrugDetail() != null) {
                detail = getGenericBasedDrugDetailInfo(dbOrder.getDrugDetail().getDrugDetailId(), AppUtil.getSafeInt(dto.getQty(), 0), dbOrder.getPatientProfile());
                if (detail != null) {
                    ord.setRxAcqCost(detail.getRxAcqCost() != null ? detail.getRxAcqCost().doubleValue() : 0f);
                    ord.setDrugPrice(1d * detail.getDrugCost());//detail.getBasePrice() * AppUtil.getSafeInt(ord.getQty(),0));
                    ord.setPriceIncludingMargins(1d * detail.getTotalPrice());
                    if (!CommonUtil.isNullOrEmpty(detail.getRedeemedPoints())) {
                        ord.setRedeemPoints(detail.getRedeemedPoints().toString());
                    }
                    if (detail.getRedeemedPointsPrice() != null) {
                        ord.setRedeemPointsCost(1d * detail.getRedeemedPointsPrice());
                    }
                    ord.setPayment(detail.getFinalDrugCost());
                    ord.setFinalPayment(detail.getFinalDrugCost());
                    ord.setPaymentExcludingDelivery(detail.getFinalDrugCost());
                    ord.setEstimatedPrice(detail.getFinalDrugCost());
                }
            }
            //////////////////////////////////////////////////////////////
            ord.setRefillsRemaining(dbOrder != null && dbOrder.getRefillsRemaining() != null
                    && dbOrder.getRefillsRemaining() > 0 ? dbOrder.getRefillsRemaining() - 1 : 0);
            ord.setQty(dto.getQty());
            ord.setOrderBatch(orderBatch);
            if (dto.getDeliveryPreferenceId() != null && dto.getDeliveryPreferenceId() > 0) {
                ord.setDeliveryPreference(new DeliveryPreferences(dto.getDeliveryPreferenceId()));
            }
            if (dto.getPaymentId() > 0) {
                ord.setPaymentId(dto.getPaymentId());
            }
            ord.setHandLingFee(dto.getHandLingFee() != null ? dto.getHandLingFee() : 0.0d);
            /////////////////////////////////////////////////////////////
            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(
                    new PatientDeliveryAddress(), dto.getDeliveryAddressId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null) {
//                        && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                ord.setStreetAddress(patientDeliveryAddress.getAddress());
                ord.setCity(patientDeliveryAddress.getCity());
                ord.setZip(patientDeliveryAddress.getZip());
                ord.setAddressLine2(patientDeliveryAddress.getDescription());
                ord.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null
                        && patientDeliveryAddress.getState().getId() != null) {
                    ord.setState(patientDeliveryAddress.getState().getName());
                }
            }
            /////////////////////////////////////////////////////////////
            OrderStatus status = new OrderStatus();
            status.setId(1);
            ord.setOrderStatus(status);
//          ord.setMessageResponseses(null);
            ord.setOrderTransferImages(null);
//            ord.setNotificationMessageses(null);
//            ord.setOrderCustomDocumentses(null);
            ord.setOrdersPtMessagesList(null);
            ord.setRewardHistorySet(null);
            ord.setCreatedBy(createdBy);
//                if (ord.getRefillsAllowed() != null && ord.getRefillsAllowed() > 0) {
//                    int refillRemining = ord.getRefillsAllowed() - 1;
//                    ord.setRefillsRemaining(refillRemining);
//                }
            ord.setCoPayCardDetails(null);
            ord.setOrderType(Constants.REFILL);
            ord.setCreatedOn(new Date());
            ord.setShippedOn(null);
            ord.setShippedBy(null);
            ord.setTraclingno("");
            ord.setClerk("");
            ord.setDeliverycarrier("");
            ord.setRefillDone(0);

            OrderChain orderChain = ord.getOrderChain();
            if (orderChain != null) {
//                orderChain.setRefillAllow(ord.getRefillsAllowed());
                orderChain.setRefillRemaing(orderChain.getRefillRemaing() > 0 ? orderChain.getRefillRemaing() - 1 : 0);
            }
            List<OrderHistory> orderHistorys = populateOrderHistory(dbOrder, ord, status);
            if (!CommonUtil.isNullOrEmpty(orderHistorys)) {
                ord.setOrderHistory(orderHistorys);
            } else {
                ord.setOrderHistory(null);
            }
            patientProfileDAO.save(ord);
            patientProfileDAO.saveOrUpdate(orderChain);
            /////////////////////////////////
            if (refillViaApp) {
                ord.setAwardedPoints(saveRewardOrderHistory(ord.getRedeemPoints(), ord.getPatientProfile(), ord, "Rx Refill", 6));
            }
            /////////////////////////////////
            List<Order> orderlist = new ArrayList<>();
            orderlist.add(ord);
            detailDTOs = setOrderList2(orderlist, null, "");
        }

        return detailDTOs;
    }

    private List<OrderHistory> populateOrderHistory(Order dbOrder, Order ord, OrderStatus status) {
        List<OrderHistory> orderHistorys = new ArrayList<>();
        OrderHistory dbOrderHistory = (OrderHistory) patientProfileDAO.findByPropertyUnique(new OrderHistory(), "order.id", dbOrder.getId(), Constants.HIBERNATE_EQ_OPERATOR);
        if (dbOrderHistory != null && dbOrderHistory.getId() != null) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(ord);
            orderHistory.setComments(dbOrderHistory.getComments());
            orderHistory.setOrderStatus(status);
            orderHistory.setPatientResponseLog(dbOrderHistory.getPatientResponseLog());
            orderHistory.setResponseRequiredLog(dbOrderHistory.getResponseRequiredLog());
            orderHistory.setCreatedOn(new Date());
            orderHistory.setUpdatedOn(new Date());
            orderHistorys.add(orderHistory);
        }
        return orderHistorys;
    }

    public boolean saveQuestion(String orderId, String question, PatientProfile patientProfile, Integer notificationMsgId) {
        boolean isSaved = false;

        try {
            Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
            if (order != null) {
                QuestionAnswer questionAnswer = new QuestionAnswer();
                questionAnswer.setOrder(order);
                questionAnswer.setQuestion(question);
                questionAnswer.setQuestionTime(new Date());
                questionAnswer.setPatientProfile(patientProfile);

                if (!CommonUtil.isNullOrEmpty(notificationMsgId)) {
                    NotificationMessages notificationMessages = new NotificationMessages();
                    notificationMessages.setId(notificationMsgId);
                    questionAnswer.setNotificationMessages(notificationMessages);
                }

                patientProfileDAO.save(questionAnswer);
                resetAutoDeletionValues(order, order.getOrderStatus().getId());
                order.setPatientResponse(Constants.PATIENT_RESPONSES.PLACE_QUESTION);
                order.setResponseFullFilled("0");
                OrderStatus status = new OrderStatus();
                //status.setId(19);
                //Cleint change the order status when patient place any Question 
                status.setId(Constants.ORDER_STATUS.PENDING_ID);
                order.setOrderStatus(status);
                patientProfileDAO.saveOrUpdate(order);
                isSaved = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        return isSaved;
    }

    public boolean saveDependentQuestion(Integer dependentId, String question, PatientProfile patientProfile) {
        boolean isSaved = false;

        try {
            PatientProfileMembers member = (PatientProfileMembers) this.patientProfileDAO.findRecordById(new PatientProfileMembers(), dependentId);
            if (member != null) {
                QuestionAnswer questionAnswer = new QuestionAnswer();
                questionAnswer.setMember(member);
                questionAnswer.setQuestion(question);
                questionAnswer.setQuestionTime(new Date());
                questionAnswer.setPatientProfile(patientProfile);
                patientProfileDAO.save(questionAnswer);
                isSaved = true;

            }
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        return isSaved;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    public boolean saveAnswer(String orderId, int questionId, String answer, int userId) {
        boolean isSaved = false;

        try {
            Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
            if (order != null) {
                QuestionAnswer questionAnswer = (QuestionAnswer) this.patientProfileDAO.findRecordById(new QuestionAnswer(), questionId);
                if (questionAnswer != null) {
//                    questionAnswer.setOrder(order);
                    questionAnswer.setAnswer(answer);
                    questionAnswer.setAnswerTime(new Date());
                    patientProfileDAO.save(questionAnswer);
                    resetAutoDeletionValues(order, order.getOrderStatus().getId());
//                    order.setPatientResponse(Constants.PATIENT_RESPONSES.PLACE_QUESTION);
                    order.setResponseFullFilled("1");
                    OrderStatus status = new OrderStatus();
                    status.setId(1);
                    order.setOrderStatus(status);
                    patientProfileDAO.saveOrUpdate(order);
                    isSaved = true;
                }
            }
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        return isSaved;
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public PatientProfileDTO getProfileListWs(String mobileNumber, Integer verificationCode) {
        PatientProfileDTO patientProfileDTO = new PatientProfileDTO();
        try {
            List<PatientProfile> patientProfilesList = patientProfileDAO.getPatientProfilesList(EncryptionHandlerUtil.getEncryptedString(mobileNumber), verificationCode);
            if (!CommonUtil.isNullOrEmpty(patientProfilesList)) {
                PatientProfile patientProfile = patientProfilesList.get(0);

                LoginDTO profile = CommonUtil.populateProfileUserData(patientProfile);
                patientProfileDTO.setUser(profile);
                List<PatientInsuranceDetails> patientInsuranceDetailses = patientProfileDAO.findByNestedProperty(new PatientInsuranceDetails(), "patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR, "", Constants.HIBERNATE_ASC_ORDER);
                patientProfileDTO.setInsurancecards(populatePatientInsuranceDetails(patientInsuranceDetailses));

                patientProfileDTO.setSaveSearches(getSearchesRecordList(patientProfile.getId()));

                InsuranceCardDataDTO insuranceCardDataDTO = populateInsuranceCardData(patientProfile);
                patientProfileDTO.setInsuranceCardData(insuranceCardDataDTO);

                List<PatientProfileMembers> patientProfileMemberses = populatePatientProfileMembers(patientProfile.getId());
                patientProfileDTO.setDependents(patientProfileMemberses);
                patientProfileDTO.setAddress(getPatientDeliveryAddressesByProfileId(patientProfile.getId()));
                populatePatientPaymentDetail(patientProfile, patientProfileDTO);
                RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
                patientProfileDTO.setAvailableRewardPoints(rewardPoints.getAvailablePoints());
                patientProfileDTO.setLifetimeRewardPoints(rewardPoints.getLifeTimePoints());
                patientProfileDTO.setAvailedRewardPoints(rewardPoints.getAvailedRewardPoints());
                patientProfileDTO.setTotalSavings(patientProfileDAO.getTotalRecords(new Order(), "patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_SUM_FUNCTION, "redeemPointsCost"));
            }
        } catch (Exception e) {
            logger.error("Exception:: getProfileListWs", e);
            e.printStackTrace();
        }
        return patientProfileDTO;
    }

    private List<PatientProfileMembers> populatePatientProfileMembers(Integer patientProfileId) {
        List<PatientProfileMembers> patientProfileMemberses = new ArrayList<>();
        try {
            List<PatientProfileMembers> profileMembersList = patientProfileDAO.getPatientProfileMembersListById(patientProfileId);
            profileMembersList.stream().map((patientProfileMembers) -> {
                PatientProfileMembers members = new PatientProfileMembers();
                members.setId(patientProfileMembers.getId());
                members.setPatientId(patientProfileMembers.getPatientId());
                members.setFirstName(patientProfileMembers.getFirstName());
                members.setLastName(patientProfileMembers.getLastName());
                members.setDob(patientProfileMembers.getDob());
                members.setGender(patientProfileMembers.getGender());
                members.setAllergies(patientProfileMembers.getAllergies());
                members.setDermatologist(patientProfileMembers.getDermatologist());
                members.setCreatedOn(patientProfileMembers.getCreatedOn());
                members.setFrontPOAImage(patientProfileMembers.getFrontPOAImage());
                members.setBackPOAImage(patientProfileMembers.getBackPOAImage());
                members.setState(patientProfileMembers.getState());
                members.setExpiryDate(patientProfileMembers.getExpiryDate());
                members.setIsAdult(patientProfileMembers.getIsAdult());
                members.setIsApproved(patientProfileMembers.getIsApproved());
                members.setPoaApprovalDate(patientProfileMembers.getPoaApprovalDate());
                members.setDisApproveDate(patientProfileMembers.getDisApproveDate());
                members.setDisapprove(patientProfileMembers.getDisapprove());
                members.setComments(patientProfileMembers.getComments());
                members.setPoaExpirationDate(patientProfileMembers.getPoaExpirationDate());
                members.setArchived(patientProfileMembers.getArchived());
                members.setOptOut(patientProfileMembers.getOptOut());
                members.setEmail(patientProfileMembers.getEmail());
                return members;
            }).forEach((members) -> {
                patientProfileMemberses.add(members);
            });

        } catch (Exception e) {
            logger.error("Exception# populatePatientProfileMembers# ", e);
        }
        return patientProfileMemberses;
    }

    public PatientProfileDTO getProfileListWs(String mobileNumber) {
        PatientProfileDTO patientProfileDTO = new PatientProfileDTO();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByMobileNumber(EncryptionHandlerUtil.getEncryptedString(mobileNumber));
            if (patientProfile != null) {

                LoginDTO profile = CommonUtil.populateProfileUserData(patientProfile);
                patientProfileDTO.setUser(profile);
                List<PatientInsuranceDetails> patientInsuranceDetailses = patientProfileDAO.findByNestedProperty(new PatientInsuranceDetails(), "patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR, "", Constants.HIBERNATE_ASC_ORDER);
                patientProfileDTO.setInsurancecards(populatePatientInsuranceDetails(patientInsuranceDetailses));

                patientProfileDTO.setSaveSearches(getSearchesRecordList(patientProfile.getId()));

                InsuranceCardDataDTO insuranceCardDataDTO = populateInsuranceCardData(patientProfile);
                patientProfileDTO.setInsuranceCardData(insuranceCardDataDTO);

                List<PatientProfileMembers> profileMembersList = patientProfileDAO.getPatientProfileMembersListById(patientProfile.getId());
                patientProfileDTO.setDependents(profileMembersList);
                patientProfileDTO.setAddress(getPatientDeliveryAddressesByProfileId(patientProfile.getId()));
                populatePatientPaymentDetail(patientProfile, patientProfileDTO);
                RewardPoints rewardPoints = getMyRewardsPoints(patientProfile.getId());
                patientProfileDTO.setAvailableRewardPoints(rewardPoints.getAvailablePoints());
                patientProfileDTO.setLifetimeRewardPoints(rewardPoints.getLifeTimePoints());
                patientProfileDTO.setAvailedRewardPoints(rewardPoints.getAvailedRewardPoints());
                patientProfileDTO.setTotalSavings(patientProfileDAO.getTotalRecords(new Order(), "patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_SUM_FUNCTION, "redeemPointsCost"));
            }
        } catch (Exception e) {
            logger.error("Exception:: getProfileListWs", e);
        }
        return patientProfileDTO;
    }

    public LoginDTO getProfileListWs(String mobileNumber, String status) {
        LoginDTO profile = new LoginDTO();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByPhoneAndStatus(EncryptionHandlerUtil.getEncryptedString(mobileNumber), status);
            if (patientProfile != null && !CommonUtil.isNullOrEmpty(patientProfile.getId())) {
                profile = CommonUtil.populateProfileUserData(patientProfile);
            }
        } catch (Exception e) {
            logger.error("Exception:: getProfileListWs", e);
        }
        return profile;
    }

    public void populatePatientPaymentDetail(PatientProfile patientProfile, PatientProfileDTO patientProfileDTO) throws Exception {
        List<PatientPaymentInfo> patientPaymentInfos = patientProfileDAO.getPatientPaymentInfoListByProfileId(patientProfile.getId());
        List<PatientPaymentDTO> creditCardsList = new ArrayList<>();
        if (!CommonUtil.isNullOrEmpty(patientPaymentInfos)) {
            patientPaymentInfos.stream().map((patientPaymentInfo) -> {
                PatientPaymentDTO patientPaymentDTO = new PatientPaymentDTO();
                patientPaymentDTO.setId(patientPaymentInfo.getId());
                patientPaymentDTO.setPatientId(patientPaymentInfo.getPatientProfile().getId());
                patientPaymentDTO.setCardHolderName(patientPaymentInfo.getCardHolderName());
                patientPaymentDTO.setCardType(patientPaymentInfo.getCardType());
                patientPaymentDTO.setCvvNumber(patientPaymentInfo.getCvvNumber());
                patientPaymentDTO.setCardNumber(patientPaymentInfo.getCardNumber());
                patientPaymentDTO.setExpiryDate(patientPaymentInfo.getExpiryDate());
                patientPaymentDTO.setDefaultCard(patientPaymentInfo.getDefaultCard());
                if (patientPaymentInfo.getBillingAddress() != null && patientPaymentInfo.getBillingAddress().getId() != null) {
                    patientPaymentDTO.setAddress(patientPaymentInfo.getBillingAddress().getAddress());
                    patientPaymentDTO.setZip(patientPaymentInfo.getBillingAddress().getZip());
                    patientPaymentDTO.setStateId(patientPaymentInfo.getBillingAddress().getStateId());
                    patientPaymentDTO.setCity(patientPaymentInfo.getBillingAddress().getCity());
                    patientPaymentDTO.setAddressType(patientPaymentInfo.getBillingAddress().getAddressType());
                    patientPaymentDTO.setAddressdescription(patientPaymentInfo.getBillingAddress().getAddressDescription());
                    patientPaymentDTO.setApartment(patientPaymentInfo.getBillingAddress().getApartment());
                }
                return patientPaymentDTO;
            }).forEach((patientPaymentDTO) -> {
                creditCardsList.add(patientPaymentDTO);
            });
        }
        patientProfileDTO.setCreditcards(creditCardsList);
    }

    public InsuranceCardDataDTO populateInsuranceCardData(PatientProfile patientProfile) {
        InsuranceCardDataDTO insuranceCardDataDTO = new InsuranceCardDataDTO();
        insuranceCardDataDTO.setCardHolderRelationship(patientProfile.getCardHolderRelation());
        insuranceCardDataDTO.setInsuranceFrontCardPath(patientProfile.getInsuranceFrontCardPath());
        insuranceCardDataDTO.setInsuranceBackCardPath(patientProfile.getInsuranceBackCardPath());
        return insuranceCardDataDTO;
    }

    public OrderCountDTO getOrderCount(Integer patientId) {
        OrderCountDTO orderCountDTO = new OrderCountDTO();
        try {
            orderCountDTO.setNewMsgCount(patientProfileDAO.getTotalUnReadNotificationMessages(patientId));
            orderCountDTO.setOldMsgCount(patientProfileDAO.getTotalReadNotificationMessages(patientId));
            //lstObj for CoPayCardDetails
//            List<BusinessObject> lstObj = new ArrayList();
//            BusinessObject obj = new BusinessObject("patientProfile.id", patientId, Constants.HIBERNATE_EQ_OPERATOR);
//            lstObj.add(obj);
//            obj = new BusinessObject("isArchive", "0", Constants.HIBERNATE_EQ_OPERATOR);
//            lstObj.add(obj);
//            //bos for PatientInsuranceDetails
//            List<BusinessObject> bos = new ArrayList();
//            BusinessObject bo = new BusinessObject("patientProfile.id", patientId, Constants.HIBERNATE_EQ_OPERATOR);
//            bos.add(bo);
//            bo = new BusinessObject("isArchived", 0, Constants.HIBERNATE_EQ_OPERATOR);
//            bos.add(bo);

            orderCountDTO.setTotalCopyCardsCount(patientProfileDAO.getCopayCardsCount(patientId));
            orderCountDTO.setTotalDependentsCount(patientProfileDAO.getTotalPatientMembersByProfileId(patientId));
            orderCountDTO.setTotalSavedSearchesCount(patientProfileDAO.getTotalRecords(new DrugSearches(), "patientProfile.id", patientId, Constants.HIBERNATE_COUNT_FUNCTION, "*"));
            orderCountDTO.setInsuranceCardCount(patientProfileDAO.getInsuranceCardsCount(patientId));
            orderCountDTO.setAllergiesCount(patientProfileDAO.getTotalAllergiesCount(patientId));
//            orderCountDTO.setAllergiesCount(patientProfileDAO.getAllPatientAllergiesCount(patientId));
            orderCountDTO.setTotalRefillRemaing(patientProfileDAO.getTotalRecords(new OrderChain(), "patientProfile.id", patientId, Constants.HIBERNATE_SUM_FUNCTION, "refillRemaing"));
            orderCountDTO.setTotalRefillAvailable(patientProfileDAO.getTotalRecords(new OrderChain(), "patientProfile.id", patientId, Constants.HIBERNATE_SUM_FUNCTION, "refillAllow"));
            orderCountDTO.setTotalOrderCount(patientProfileDAO.getTotalRecords(new Order(), "patientProfile.id", patientId, Constants.HIBERNATE_COUNT_FUNCTION, "*"));
            //lstObj for Ready to Deliver Rx
            List<BusinessObject> lstObj = new ArrayList();
            BusinessObject obj = new BusinessObject("patientProfile.id", patientId, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            obj = new BusinessObject("orderStatus.id", Constants.ORDER_STATUS.FILLED_ID, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            obj = new BusinessObject("deliveryPreference.id", Constants.DELIVERY_PREfFERENCES.PICK_UP_AT_PHARMACY_ID, Constants.HIBERNATE_NE_OPERATOR);
            lstObj.add(obj);
            orderCountDTO.setTotalReadyToDeliverRxOrders(patientProfileDAO.getTotalRecordsByNestedProperty(new Order(), lstObj, "Count(*)"));
        } catch (Exception e) {
            logger.error("Exception:: getOrderCount", e);
        }
        return orderCountDTO;
    }

    public List<CoPayCardDetailsDTO> getCoPayCardsListByOrderId(String orderId, Integer patientProfileId) {
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = new ArrayList();
        try {
            List<CoPayCardDetails> coPayCardDetailslist = patientProfileDAO.getCoPayCardDetailsWithOrderIdOrWithOutOrderId(orderId, patientProfileId);
            coPayCardDetailsDTOList = populateCoPayCardDetails(coPayCardDetailslist);
        } catch (Exception e) {
            logger.error("Exception:: getCoPayCardsListByOrderId", e);
        }
        return coPayCardDetailsDTOList;
    }

    public List<CoPayCardDetailsDTO> getAvailableCoPayCards(Integer patientProfileId) {
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = new ArrayList();
        try {
            List<CoPayCardDetails> coPayCardDetailslist = patientProfileDAO.getAvailableCoPayCardDetails(patientProfileId);
            coPayCardDetailsDTOList = populateCoPayCardDetails(coPayCardDetailslist);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: getCoPayCardsListByOrderId", e);
        }
        return coPayCardDetailsDTOList;
    }

    public boolean removeMFRCardById(Long id) {
        boolean isRemove = false;
        try {
            CoPayCardDetails coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), id);
            if (coPayCardDetails != null && coPayCardDetails.getId() != null) {
                coPayCardDetails.setOrder(null);
                coPayCardDetails.setUpdatedOn(new Date());
                patientProfileDAO.update(coPayCardDetails);
                isRemove = true;
            }
        } catch (Exception e) {
            logger.error("Exception:: removeMFRById", e);
            isRemove = false;
        }
        return isRemove;
    }

    public boolean updateMFRWithOrderWs(String orderId, String copayCardDictionary) {
        boolean isUpdate = false;
        try {
            if (patientProfileDAO.updateCoPayCardDetailsByOrderId(orderId) > 0) {
                isUpdate = true;
            }
            if (CommonUtil.isNotEmpty(copayCardDictionary)) {
                String[] copayCardIds = copayCardDictionary.split(",");
                for (String copayCardId : copayCardIds) {
                    CoPayCardDetails coPayCardDetails = (CoPayCardDetails) patientProfileDAO.findRecordById(new CoPayCardDetails(), Long.parseLong(copayCardId));
                    Order order = (Order) patientProfileDAO.findRecordById(new Order(), orderId);
                    coPayCardDetails.setOrder(order);
                    coPayCardDetails.setUpdatedOn(new Date());
                    patientProfileDAO.update(coPayCardDetails);
                    isUpdate = true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception:: updateMFRWithOrderWs", e);
        }
        return isUpdate;
    }

    public int updateDeleiveryPreferance(String orderId, String fee, String addressId, String deliveryPrefId) {
        int isUpdate = 0;
        try {
            Order order = this.getOrderById("" + orderId);
            DeliveryPreferences deliveryPreference = this.getDeliveryPreferenceById(AppUtil.getSafeInt(deliveryPrefId, 0));
            order.setHandLingFee(Double.parseDouble(fee));
            order.setDeliveryPreference(deliveryPreference);

            PatientDeliveryAddress patientDeliveryAddress = (PatientDeliveryAddress) patientProfileDAO.getObjectById(new PatientDeliveryAddress(), AppUtil.getSafeInt(addressId, 0));//getDefaultPatientDeliveryAddress(patientProfile.getId());
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {
                logger.info("Set shipping address info. ");
                order.setStreetAddress(patientDeliveryAddress.getAddress());
                order.setCity(patientDeliveryAddress.getCity());
                order.setZip(patientDeliveryAddress.getZip());
                order.setAddressLine2(patientDeliveryAddress.getDescription());
                order.setApartment(patientDeliveryAddress.getApartment());
                if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                    order.setState(patientDeliveryAddress.getState().getName());
                    isUpdate = 1;
                }
                patientProfileDAO.update(order);
            }
        } catch (Exception e) {
            logger.error("Exception:: updateDeleiveryPreferance", e);
        }
        return isUpdate;
    }

    public int updateDateTime(String orderId, Integer dPrefId, String date, String deliveryTo, String deliveryFrom) throws ParseException, Exception {

        Order order = this.getOrderById("" + orderId);

        Date toTime = DateUtil.stringToDate(deliveryTo, "HH:mm");
        Date fromTime = DateUtil.stringToDate(deliveryFrom, "HH:mm");

        order.setDeliveryTo(toTime);
        order.setDeliveryFrom(fromTime);
        order.setCustomerShippingDate(DateUtil.stringToDate(date, "mm-dd-yyyy"));
        if (dPrefId != null) {
            DeliveryPreferences pref = (DeliveryPreferences) this.patientProfileDAO.findRecordById(
                    new DeliveryPreferences(), dPrefId);
            if (pref != null) {
                order.setDeliveryPreference(pref);
            }
        }
        order.setPatientResponse("Preferred delivery time is between " + deliveryFrom + " and " + deliveryTo);
        patientProfileDAO.update(order);
//        this.saveQuestion(order, "Preferred delivery time is between " + deliveryFrom + " and " + deliveryTo + ".", order.getPatientProfile());
        return 1;
    }

    public int fillAsCash(int Id, int createdBy, int statusVal, String comments, String paymentMode) throws Exception {
        Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), "" + Id);
        if (order != null) {
//            order.setPatientResponse(Constants.PATIENT_RESPONSES.FILL_AS_CASH);
            order.setUpdatedOn(new Date());
            order.setFinalPaymentMode("SELF PAY");
            order.setPatientResponse(Constants.PATIENT_RESPONSES.FILL_AS_CASH);
            order.setResponseFullFilled("0");
            OrderStatus status = new OrderStatus();
            status.setId(19);
            order.setOrderStatus(status);
//            this.saveQuestion(order, "Customer requested to fill as cash.", order.getPatientProfile());
            OrderHistory history = new OrderHistory();
            history.setOrder(order);
            history.setOrderStatus(order.getOrderStatus());
            history.setCreatedBy(createdBy);
            history.setUpdatedBy(createdBy);
            history.setCreatedOn(new Date());
            history.setUpdatedOn(new Date());
            history.setPatientResponseLog(Constants.PATIENT_RESPONSES.FILL_AS_CASH);
            patientProfileDAO.saveOrUpdate(history);
            order = resetAutoDeletionValues(order, statusVal);
            if (order != null) {
                patientProfileDAO.saveOrUpdate(order);
            }
        }
        return 1;
    }

    public Order resetAutoDeletionValues(Order order, Integer status) throws Exception {
//        Order order = this.findOrderById(orderId);

        if (order != null) {
//            OrderStatus orderStatus = new OrderStatus();
//            orderStatus = (OrderStatus) patientProfileDAO.findRecordById(new OrderStatus(), status);
//            order.setOrderStatus(orderStatus);
            order.setAutoDeletionDate(null);
            order.setAutoDeletionFlag(0);
//            patientProfileDAO.saveOrUpdate(order);

        }
        return order;
    }

    public int updatestopReminder(String orderId, Integer optOutRefillReminder) throws ParseException, Exception {
        //OrderChain orderChain = (OrderChain) patientProfileDAO.findRecordById(new OrderChain(),orderChainId );     
        Order order = this.getOrderById("" + orderId);
        if (order != null) {
            OrderChain orderChain = order.getOrderChain();
            orderChain.setOptOutRefillReminder(optOutRefillReminder);
            patientProfileDAO.update(orderChain);
            return 1;

        } else {

            return 0;
        }

    }

    public void saveYearEndStatementInfo(Integer patientProfileId, String fileName) {
        try {
            YearEndStatementInfo yearEndStatementInfo = new YearEndStatementInfo();
            yearEndStatementInfo.setPatientProfile(new PatientProfile(patientProfileId));
            yearEndStatementInfo.setFileName(fileName);
            yearEndStatementInfo.setYearOn(DateUtil.dateToString(new Date(), "yyyy"));
            yearEndStatementInfo.setCreatedOn(new Date());
            patientProfileDAO.save(yearEndStatementInfo);
        } catch (Exception e) {
            logger.error("Exception:: saveYearEndStatementInfo", e);
        }
    }

    public YearEndStatementInfo getYearEndStatementInfo(Integer patientProfileId) {
        YearEndStatementInfo yearEndStatementInfo = new YearEndStatementInfo();
        try {
            List<YearEndStatementInfo> endStatementInfos = patientProfileDAO.findByNestedProperty(new YearEndStatementInfo(), "patientProfile.id", patientProfileId, Constants.HIBERNATE_EQ_OPERATOR, null, 0);
            if (!CommonUtil.isNullOrEmpty(endStatementInfos)) {
                yearEndStatementInfo = endStatementInfos.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception:: getYearEndStatementInfo", e);
        }
        return yearEndStatementInfo;
    }

    public Set<DrugBrandDTO> getDrugBrandsOnlyList(String name) {

        Set<DrugBrandDTO> list = new HashSet<>();
        try {

            List<DrugBasic> drugBrandList = patientProfileDAO.retrieveDrugWithoutGeneric(name);

            for (DrugBasic drugBrand : drugBrandList) {
                DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                drugBrandDTO.setId(drugBrand.getDrugBasicId());

                drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), ""));

                list.add(drugBrandDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getDrugBrandsList", e);
        }
        return list;
    }

    public String getDrugDetailByBrandName(String brandName, String genericName, Long drugGcn) {
        String response = "";
        try {
            List<BusinessObject> lstObj = new ArrayList();
            BusinessObject obj = new BusinessObject("drugBasic.brandName", brandName, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            obj = new BusinessObject("drugBasic.drugGeneric.genericName", genericName, Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
            if (!CommonUtil.isNullOrEmpty(drugGcn)) {
                obj = new BusinessObject("drugGCN", drugGcn, Constants.HIBERNATE_EQ_OPERATOR);
                lstObj.add(obj);
            }

            List<DrugDetail> drugDetails = patientProfileDAO.findByNestedProperty(new DrugDetail(), lstObj, "", 0);
            List<DrugDetailDTO> drugDetailList = new ArrayList<>();
            for (DrugDetail dd : drugDetails) {
                DrugDetailDTO drugDetail = new DrugDetailDTO();
                drugDetail.setDrugDetailId(dd.getDrugDetailId());
                drugDetail.setDrugGCN(dd.getDrugGCN());
                drugDetail.setStrength(dd.getStrength());
                drugDetail.setFormDesc(dd.getDrugForm().getFormDescr());
                drugDetail.setDefQty(dd.getDefQty());
                drugDetailList.add(drugDetail);
            }
            response = new ObjectMapper().writeValueAsString(drugDetailList);
        } catch (Exception e) {
            logger.error("Exception:: getDrugDetailByBrandName", e);
        }
        return response;
    }

    public Set<DrugBrandDTO> getDrugBrandsListByBrandOrGenericName(String name) {

        Set<DrugBrandDTO> list = new HashSet<>();
        try {

            List<DrugBasic> drugGenericList = patientProfileDAO.retrieveDrugWithGeneric(name);
            List<DrugBasic> drugBrandList = patientProfileDAO.retrieveDrugWithoutGeneric(name);

            if ((drugGenericList == null && drugBrandList == null) || (CommonUtil.isNullOrEmpty(drugGenericList) && CommonUtil.isNullOrEmpty(drugBrandList))) {
                DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                drugBrandDTO.setId(0);
                drugBrandDTO.setDrugBrandName(Constants.EMPTY_MESSAGE);
                list.add(drugBrandDTO);
                return list;
            }

            if (drugGenericList.size() > 0) {
                drugGenericList.stream().map((drugBrand) -> {
                    DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                    drugBrandDTO.setId(drugBrand.getDrugBasicId());
                    //                    drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "") + "("
//                            + AppUtil.getSafeStr(drugBrand.getBrandName(), "") + ")");
                    if (AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").equalsIgnoreCase("* BRAND NAME ONLY *")) {
                        // if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N"))
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + " "
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), ""));
                    } else {
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + "{"
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "") + "}");

                    }
                    return drugBrandDTO;
                }).forEach((drugBrandDTO) -> {
                    list.add(drugBrandDTO);
                });
            }

            if (drugBrandList.size() > 0) {
                logger.info("Drug Brand list size: " + drugBrandList.size());
                drugBrandList.stream().map((drugBrand) -> {
                    DrugBrandDTO drugBrandDTO = new DrugBrandDTO();
                    drugBrandDTO.setId(drugBrand.getDrugBasicId());
                    //drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), ""));
                    if (AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "").equalsIgnoreCase("* BRAND NAME ONLY *")) {
                        // if (AppUtil.getSafeStr(d.getArchived(), "").equalsIgnoreCase("N")) 
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + " "
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), ""));
                    } else {
                        drugBrandDTO.setDrugBrandName(AppUtil.getSafeStr(drugBrand.getBrandName(), "") + "{"
                                + AppUtil.getSafeStr(drugBrand.getDrugGeneric().getGenericName(), "") + "}");

                    }
                    return drugBrandDTO;
                }).forEach((drugBrandDTO) -> {
                    list.add(drugBrandDTO);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: getDrugBrandsListByBrandOrGenericName", e);
        }
        return list;
    }

    public List<NotificationMessagesDTO> getInAppNotificationReport(BaseDTO baseDTO, boolean isPdf) {
        List<NotificationMessagesDTO> list = new ArrayList<>();
        try {
            String phoneNumber = baseDTO.getPhoneNumber();
            baseDTO.setPhoneNumber(EncryptionHandlerUtil.getEncryptedString(phoneNumber));
            List<NotificationMessages> notificationMessageses = patientProfileDAO.getInAppNotificationReport(baseDTO);
            if (!CommonUtil.isNullOrEmpty(notificationMessageses)) {
                notificationMessageses.stream().map((messages) -> getNotificationMessagesDTO(messages, isPdf)).forEach((notificationMessages) -> {
                    list.add(notificationMessages);
                });
            }

            //////Pharmacy Notification Messages///////////////////
            List<OrdersPtMessage> ordersPtMessages = patientProfileDAO.getPharmacyNotificationMessagesByDate(baseDTO);
            if (!CommonUtil.isNullOrEmpty(ordersPtMessages)) {
                ordersPtMessages.stream().map((messages) -> getPharmacyNotificationDTO(messages)).forEach((notificationMessages) -> {
                    list.add(notificationMessages);
                });
            }
            baseDTO.setPhoneNumber(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: getInAppNotificationReport", e);
        }
        return list;
    }

    public List<PatientPreferencesDTO> loadPatientPreferences(String headingName, Integer patientId) {
        List<PatientPreferencesDTO> retVal = null;
        try {
            retVal = patientProfileDAO.loadPatientPreferences(headingName, patientId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: loadPatientPreferences", e);
        }
        return retVal;
    }

    public boolean savePatientPreference(Integer patientId, Integer preferenceSettingId, boolean preferenceValue) {
        boolean retVal = false;
        try {
            PatientProfilePreferences preference = null;
            List<BusinessObject> businessObjects = new ArrayList<>();
            businessObjects.add(new BusinessObject("patient.id", patientId, Constants.HIBERNATE_EQ_OPERATOR));
            businessObjects.add(new BusinessObject("preferenceSetting.id", preferenceSettingId, Constants.HIBERNATE_EQ_OPERATOR));
            List<PatientProfilePreferences> preferences = patientProfileDAO.findByProperty(new PatientProfilePreferences(), businessObjects, "", 0);

            if (preferences != null && preferences.size() > 0) {
                preference = preferences.get(0);
            } else {
                preference = new PatientProfilePreferences(new PatientProfile(patientId), new PreferencesSetting(preferenceSettingId));
            }
            preference.setPreferenceValue(preferenceValue);
            patientProfileDAO.savePatientPreference(preference);
            retVal = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: savePatientPreference", e);
        }
        return retVal;
    }

    public void deletePendingOrder(Integer patientId, Integer orderChainId, JsonResponse jsonResponse) throws Exception {
        try {
            OrderChain orderChain = (OrderChain) patientProfileDAO.findByPropertyUnique(new OrderChain(), "id", Long.parseLong(orderChainId.toString()), Constants.HIBERNATE_EQ_OPERATOR);
            if (orderChain != null) {
                List<Order> orders = orderChain.getOrderList();
                if (orders != null && orderChain.getPatientProfile().getId().equals(patientId)) {
                    if (orders.size() == 1 && orders.get(0).getOrderStatus().getName().equals("Pending")) {
                        patientProfileDAO.delete(orders.get(0));
                        orderChain.getOrderList().clear();
                        patientProfileDAO.delete(orderChain);
                        jsonResponse.setData(true);
                        jsonResponse.setErrorCode(1);
                    } else if (orders.size() == 1 && !orders.get(0).getOrderStatus().getName().equals("Pending")) {
                        jsonResponse.setData(false);
                        jsonResponse.setErrorMessage("Order cannot be deleted because it is in processing.");
                    } else if (orders.size() > 1) {
                        jsonResponse.setData(false);
                        jsonResponse.setErrorMessage("Order cannot be deleted because you have muliple orders.");
                    }
                } else {
                    jsonResponse.setData(false);
                    jsonResponse.setErrorMessage("Order cannot be deleted because you didn't have any order.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: deletePendingOrder", e);
        }
    }

    public boolean addResponseToNotificationMessage(String responseDescription, Integer profileId, Integer messageId, String orderId) {
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            notificationMessages.setId(messageId);
            Order order = new Order();
            order.setId(orderId);
            MessageResponses messageResponses = new MessageResponses(responseDescription, new PatientProfile(profileId), notificationMessages, order);
            patientProfileDAO.save(messageResponses);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception :: addResponseToNotificationMessage", e);
        }
        return false;
    }

    private NotificationMessagesDTO getNotificationMessagesDTO(NotificationMessages messages, boolean isPdf) {
        NotificationMessagesDTO nm = new NotificationMessagesDTO();
        nm.setMessageId(messages.getId());
        if (messages.getOrders() != null && messages.getOrders().getId() != null) {
            nm.setOrderId(messages.getOrders().getId());
        }
        if (messages.getMessageType() != null && messages.getMessageType().getId() != null) {
            nm.setMessageTypeId(messages.getMessageType().getId().getMessageTypeId());
        }
        if (messages.getPatientProfile() != null && messages.getPatientProfile().getId() != null) {
            nm.setProfileId(messages.getPatientProfile().getId());
            nm.setMobileNumber(messages.getPatientProfile().getMobileNumber());
        }
        if (messages.getMessageResponses() != null && messages.getMessageResponses().size() > 0) {
            StringBuilder sb = new StringBuilder("");
            messages.getMessageResponses().forEach((mr) -> {
                if (!isPdf) {
                    sb.append(AppUtil.getSafeStr(mr.getResponseDescription(), "")).append("<br>");
                } else {
                    sb.append(AppUtil.getSafeStr(mr.getResponseDescription(), "")).append("\n");
                }
            });
            nm.setMessageResponses(sb.toString());
        }

        nm.setSubject(messages.getSubject());
        nm.setMessageText(CommonUtil.replaceNotificationMessagesPlaceHolder(messages.getMessageText(), messages.getCreatedOn()));
        nm.setCreatedOn(messages.getCreatedOn());
        nm.setTimeAgo(DateUtil.getDateDiffInSecondsFromCurrentDate(messages.getCreatedOn()));
        nm.setIsRead(messages.getIsRead());
        nm.setIsCritical(messages.getIsCritical() != null ? messages.getIsCritical() : 0);
        nm.setMessageCategory(Constants.ORDER_NOTIFICATION);
        return nm;
    }

    private NotificationMessagesDTO getPharmacyNotificationDTO(OrdersPtMessage messages) {
        NotificationMessagesDTO notificationMessages = new NotificationMessagesDTO();
        notificationMessages.setMessageId(messages.getId());
        notificationMessages.setMessageTypeId(0);
        notificationMessages.setProfileId(messages.getOrder().getPatientProfile().getId());
        notificationMessages.setSubject(messages.getSubject());
        if (messages.getOrder() != null && messages.getOrder().getId() != null) {
            notificationMessages.setOrderId(messages.getOrder().getId());
        }
        notificationMessages.setMessageText(CommonUtil.replaceNotificationMessagesPlaceHolder(messages.getMessage(), messages.getCreatedOn()));
        notificationMessages.setAttachmentName(messages.getAttachmentName());
        notificationMessages.setAttachmentPath(messages.getAttachmentPath());
        if (CommonUtil.isNotEmpty(messages.getAttachmentPath())) {
            String attachment = notificationMessages.getMessageText() + "<br/> <a href='" + messages.getAttachmentPath() + "' style='font-weight:bold;' target=\"_blank\">View Attachment</a>";
            notificationMessages.setMessageText(attachment);
        }
        notificationMessages.setContentType(messages.getContentType());
        notificationMessages.setCreatedOn(messages.getCreatedOn());
        notificationMessages.setTimeAgo(DateUtil.getDateDiffInSecondsFromCurrentDate(messages.getCreatedOn()));
        notificationMessages.setIsRead(messages.getIsRead());
        notificationMessages.setIsCritical(messages.getIsCritical());
        notificationMessages.setMessageCategory(messages.getSubject());
        return notificationMessages;
    }

    public List<DeliveryDistanceFeeDTO> getZipCodeCalculationsList(String zip, PatientProfile patientProfile, boolean pickedFromPharmacy) {
        List<DeliveryDistanceFeeDTO> deliveryDistanceFeeDTOs = new ArrayList<>();
        try {
            List<ZipCodeCalculation> zipCodeCalculationsList = patientProfileDAO.getZipCodeCalculationsList(zip, patientProfile.getId());
            if (!zipCodeCalculationsList.isEmpty() && zipCodeCalculationsList.size() > 0) {
                ZipCodeCalculation calculation = zipCodeCalculationsList.get(0);
                PharmacyZipCodes pharmacyZipCodes = this.getPharmacyZipCodes(pickedFromPharmacy);
                for (DeliveryDistanceFee deliveryDistanceFee : pharmacyZipCodes.getDeliveryDistanceFeesList()) {
                    DeliveryDistanceFeeDTO distanceFeeDTO = new DeliveryDistanceFeeDTO();
                    if (deliveryDistanceFee != null && deliveryDistanceFee.getId() != null) {
                        if (calculation.getMiles() >= deliveryDistanceFee.getDeliveryDistances().getDistanceFrom() && calculation.getMiles() <= deliveryDistanceFee.getDeliveryDistances().getDistanceTo()) {
                            if (calculation.getDeliveryFee() != null) {
                                logger.info("Delivery fee is: " + deliveryDistanceFee.getDeliveryFee());
                                distanceFeeDTO.setDeliveryFee(deliveryDistanceFee.getDeliveryFee());
                            }
                            if (deliveryDistanceFee.getDeliveryPreferenceses() != null && deliveryDistanceFee.getDeliveryPreferenceses().getId() != null) {
                                logger.info("DeliveryPreferenceId is:: " + deliveryDistanceFee.getDeliveryPreferenceses().getId());
                                distanceFeeDTO.setDprefaId(deliveryDistanceFee.getDeliveryPreferenceses().getId());
                                distanceFeeDTO.setName(deliveryDistanceFee.getDeliveryPreferenceses().getName());
                                String name = AppUtil.getSafeStr(deliveryDistanceFee.getDeliveryPreferenceses().getName(), "").toLowerCase();
                                if (name.contains("2nd day")) {
                                    distanceFeeDTO.setDeliveryFee(BigDecimal.valueOf(0));
                                    distanceFeeDTO.setIncludedStr("Included");
                                } else {
                                    distanceFeeDTO.setIncludedStr("");
                                }
                                distanceFeeDTO.setDescription(deliveryDistanceFee.getDeliveryPreferenceses().getDescription());
                                distanceFeeDTO.setPickedFromPharmacy(deliveryDistanceFee.getDeliveryPreferenceses().isPickedFromPharmacy());
                                distanceFeeDTO.setSeqNo(deliveryDistanceFee.getDeliveryPreferenceses().getSeqNo());
                            }

                            distanceFeeDTO.setMiles(calculation.getMiles());

                            if (!checkDuplicateRecordInList(deliveryDistanceFeeDTOs, distanceFeeDTO.getDprefaId())) {
                                deliveryDistanceFeeDTOs.add(distanceFeeDTO);
                            }

                        }
                    }
                }
            }

            Comparator<DeliveryDistanceFeeDTO> byName
                    = (DeliveryDistanceFeeDTO o1, DeliveryDistanceFeeDTO o2) -> Integer.compare(o1.getSeqNo(), o2.getSeqNo());

            Collections.sort(deliveryDistanceFeeDTOs, byName);
            //////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception: PatientProfileService -> getZipCodeCalculationsList", e);
        }
        return deliveryDistanceFeeDTOs;
    }

    public PharmacyZipCodes getPharmacyZipCodes(boolean pickedFromPharmacy) {
        PharmacyZipCodes pharmacyZipCodes = new PharmacyZipCodes();
        try {
            List<PharmacyZipCodes> list = patientProfileDAO.getPharmacyZipCodesList(pickedFromPharmacy);
            if (!list.isEmpty() && list.size() > 0) {
                pharmacyZipCodes = list.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getPharmacyZipCodes", e);
        }
        return pharmacyZipCodes;
    }

    public PatientDependantDTO addCareTaker(Integer id, String adultFirstName, String adultLastName,
            String expiryDate, String state, String frontPOAUrl, String backPOAUrl, Boolean isApproved,
            String gender, String dob, String allergies, String email) {
        PatientDependantDTO patientDependantDTO = new PatientDependantDTO();
        PatientProfileMembers patientProfileMembers = new PatientProfileMembers();
        try {
            patientProfileMembers.setPatientId(id);
            patientProfileMembers.setFirstName(adultFirstName);
            patientProfileMembers.setLastName(adultLastName);
            patientProfileMembers.setExpiryDate(expiryDate);
            if (CommonUtil.isNotEmpty(expiryDate)) {
                patientProfileMembers.setPoaExpirationDate(DateUtil.stringToDate(expiryDate, Constants.USA_DATE_FORMATE));
            }
            patientProfileMembers.setState(state);
            patientProfileMembers.setFrontPOAImage(frontPOAUrl);
            patientProfileMembers.setBackPOAImage(backPOAUrl);
            patientProfileMembers.setIsAdult(Boolean.TRUE);
            patientProfileMembers.setEmail(AppUtil.getSafeStr(email, ""));
            patientProfileMembers.setIsApproved(isApproved);
            patientProfileMembers.setCreatedOn(new Date());
            patientProfileMembers.setGender(gender);
            if (CommonUtil.isNotEmpty(dob)) {
                patientProfileMembers.setDob(DateUtil.stringToDate(dob, Constants.USA_DATE_FORMATE));
            }
            patientProfileMembers.setAllergies(allergies);
            //Populate data in patient de
            patientDependantDTO = CommonUtil.populatePatientDependant(patientProfileMembers);
            patientProfileDAO.save(patientProfileMembers);
            patientDependantDTO.setId(patientProfileMembers.getId());
        } catch (Exception e) {
            logger.error("Exception:: PatientProfileService:: addCareTaker", e);
            e.printStackTrace();
        }
        return patientDependantDTO;
    }

    public boolean saveMessageResponses(PatientProfile patientProfile, String orderId, Integer notificationMsgId, String msgResponse) {
        boolean isSaved = false;
        try {
            MessageResponses messageResponses = new MessageResponses();
            messageResponses.setPatientProfile(patientProfile);
            if (CommonUtil.isNotEmpty(orderId)) {
                Order order = getOrderById(orderId);
                messageResponses.setOrder(order);
                if (CommonUtil.isNotEmpty(msgResponse) && msgResponse.equalsIgnoreCase("Requested to cancel request")) {
                    OrderHistory history = new OrderHistory();
                    history.setOrder(order);
                    history.setComments(msgResponse);
                    OrderStatus orderStatus = (OrderStatus) patientProfileDAO.findByPropertyUnique(new OrderStatus(), "name", Constants.ORDER_STATUS.CANCELLED, Constants.HIBERNATE_EQ_OPERATOR);
                    history.setOrderStatus(orderStatus);
                    saveOrderHistory(history);
                }
            }
            if (!CommonUtil.isNullOrEmpty(notificationMsgId)) {
                NotificationMessages notificationMessages = (NotificationMessages) getObjectById(new NotificationMessages(), notificationMsgId);
                messageResponses.setNotificationMessages(notificationMessages);
            }
            messageResponses.setResponseDescription(msgResponse);
            messageResponses.setCreatedOn(new Date());
            patientProfileDAO.save(messageResponses);
            isSaved = true;
        } catch (Exception e) {
            isSaved = false;
            logger.error("Exception:: PatientProfileService:: saveMessageResponses", e);
        }
        return isSaved;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getMessageSubjectWithprocessedPlaceHolders(String messageSubject, String orderId) {
        DrugDetail detail = null;
        DrugBasic basic = null;
        DrugGeneric generic = null;
        Order order = this.findOrderById(orderId);
//        if (order == null) {
//            return "No order found";
//        }
        if (order != null && order.getDrugDetail() != null) {
            detail = order.getDrugDetail();
            if (order.getDrugDetail().getDrugBasic() != null) {
                basic = order.getDrugDetail().getDrugBasic();
                if (order.getDrugDetail().getDrugBasic().getDrugGeneric() != null) {
                    generic = order.getDrugDetail().getDrugBasic().getDrugGeneric();
                }
            }

        }
        String patientName = "";
        if (order != null) {
            if (order.getPatientProfileMembers() != null) {
                patientName = order.getPatientProfileMembers().getFirstName();
            } else if (order.getPatientProfile() != null) {
                patientName = order.getPatientProfile().getFirstName();
            }
        }
        patientName = EncryptionHandlerUtil.getDecryptedString(patientName);
        return CommonUtil.getMessageSubjectWithprocessedPlaceHolders(messageSubject, order, detail, basic,
                generic, order != null ? order.getDeliveryPreference() : null, patientName);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public Order findOrderById(String id) {
        Order order = (Order) this.patientProfileDAO.findRecordById(new Order(), id);
        if (order != null) {
            Hibernate.initialize(order.getOrderStatus());
            Hibernate.initialize(order.getPatientProfile());
        }
        return order;
    }

    public Object findRecordById(Object obj, Integer id) {
        return this.patientProfileDAO.findRecordById(obj, id);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public PatientDependantDTO poaApproval(PatientProfileMembers patientProfileMembers) {
        PatientDependantDTO dependantDTO = null;
        try {
            PatientProfileMembers profileMembers = (PatientProfileMembers) patientProfileDAO.findRecordById(new PatientProfileMembers(), patientProfileMembers.getId());
            if (!CommonUtil.isNullOrEmpty(patientProfileMembers.getDisapprove())) {
                profileMembers.setDisapprove(patientProfileMembers.getDisapprove());
                profileMembers.setDisApproveDate(new Date());
            } else {
                profileMembers.setIsApproved(patientProfileMembers.getIsApproved());
                profileMembers.setPoaApprovalDate(new Date());
            }
            profileMembers.setComments(patientProfileMembers.getComments());
            profileMembers.setPoaExpirationDate(patientProfileMembers.getPoaExpirationDate());

            dependantDTO = CommonUtil.populatePatientDependant(profileMembers);
            patientProfileDAO.update(profileMembers);
            //PatientProfile patient=(PatientProfile) this.patientProfileDAO.findRecordById(new PatientProfile(), patientId);

            return dependantDTO;
        } catch (Exception e) {
            //isUpdate = false;
            e.printStackTrace();
            logger.error("Exception:: poaApproval", e);
            return null;
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean saveNotificationMessages(CampaignMessages campaignMessages, Integer profileId, String orderId,
            Integer isCritical, Integer patientProfileMembersId) {
        logger.info("Start->Save NotificationMessages");
        boolean isSaved;
        try {
            NotificationMessages notificationMessages = new NotificationMessages();
            MessageType messageType = new MessageType();
            MessageTypeId messageTypeId = new MessageTypeId();
            if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                if (campaignMessages.getMessageType() != null) {
                    messageTypeId.setFolderId(campaignMessages.getMessageType().getId().getFolderId());
                    messageTypeId.setMessageTypeId(campaignMessages.getMessageType().getId().getMessageTypeId());
                } else {
                    logger.info("MessageType is null");
                }
                messageType.setId(messageTypeId);
                notificationMessages.setMessageType(messageType);
                notificationMessages.setSubject(AppUtil.getSafeStr(campaignMessages.getSubject(), ""));
                notificationMessages.setMessageText(campaignMessages.getSmstext());
                PatientProfileMembers member = new PatientProfileMembers();
                member.setId(patientProfileMembersId);
                notificationMessages.setPatientProfileMembers(member);

                if (isCritical > 0) {
                    isCritical = 1;
                }
                notificationMessages.setIsCritical(isCritical);
            } else {
                logger.info("CampaignMessages is null.");
            }
            notificationMessages.setPatientProfile(new PatientProfile(profileId));
            //////////////////////////////////
//            Order order = (Order) this.getObjectById(new Order(), orderId);
//            notificationMessages.setOrders(order);
            /////////////////////////////////
            notificationMessages.setStatus("Success");
            notificationMessages.setIsRead(Boolean.FALSE);
            notificationMessages.setCreatedOn(new Date());
            notificationMessages.setPushSubject(AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""));
            patientProfileDAO.save(notificationMessages);
            isSaved = true;
            PatientProfile profile = (PatientProfile) this.findRecordById(new PatientProfile(), profileId);
            if (profile != null && notificationMessages != null) {
                if (AppUtil.getSafeStr(profile.getOsType(), "20").equals("20")) {

                    CommonUtil.pushFCMNotification(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                } else {
                    CommonUtil.pushFCMNotificationIOS(profile.getDeviceToken(), "0", "" + notificationMessages.getId(), "",
                            AppUtil.getSafeStr(notificationMessages.getPushSubject(), ""), profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
            logger.error("Exception -> NotificationMessages", e);
        }
        logger.info("End->Save NotificationMessages: " + isSaved);
        return isSaved;
    }

    public CampaignMessages getNotificationMsgsForDisapprove(String message, String eventName) {
        CampaignMessages campaignMessages = new CampaignMessages();
        try {
            //proccessing Y,YES,YEP ect.
            ValidResponse validResponse = textFlowDAO.getValidResponse(message);
            if (validResponse != null && validResponse.getVresponseId() != null) {
                Event event = textFlowDAO.getEventByStaticValue(eventName.trim());
                if (event == null) {
                    logger.info("No such event defined (System will return)");
                    return null;
                }
                EventHasFolderHasCampaigns eventHasFolderHasCampaigns = textFlowDAO.getEventHasFolderHasCampaign(Integer.parseInt(Constants.campaignId), event.getEventId(), Constants.SMS);
                if (eventHasFolderHasCampaigns == null) {
                    logger.info("No folder associated to this campaign (System will return)");
                    return null;
                }
                int folderId = eventHasFolderHasCampaigns.getFolderId();
                logger.info("Folder Id : " + folderId);
                List<CampaignMessages> campaignMessagesList = textFlowDAO.getCampaignMessagesByCommunicationType(Integer.parseInt(Constants.campaignId), folderId);
                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    logger.info("No messages found for (System will return)");
                    return null;
                }

                CampaignMessagesResponse campaignMessagesResponse = textFlowDAO.getCampaignMessagesResponseByResComm(Integer.parseInt(Constants.campaignId), folderId, validResponse.getResponse().getResponseTitle());
                if (campaignMessagesResponse == null) {
                    logger.info("No Campaign Messages Response.");
                    return null;
                }
                campaignMessages = campaignMessagesResponse.getCampaignMessages();
            }
        } catch (Exception e) {
            logger.error("Exception -> getNotificationMsgs", e);
        }
        return campaignMessages;
    }

    public int archivePatientProfileMember(Integer dependentId) throws Exception {
        int ret = 0;
        PatientProfileMembers members = (PatientProfileMembers) this.patientProfileDAO.
                findRecordById(new PatientProfileMembers(), dependentId);
        if (members != null) {
            members.setDisapprove(1);
            members.setDisApproveDate(new Date());
            members.setArchived(1);
            this.patientProfileDAO.saveOrUpdate(members);
            ret = 1;
        }

        return ret;
    }

    public String updatePatientAllergies(int pid, String allergies) throws Exception {
        PatientProfile patientProfile = (PatientProfile) this.patientProfileDAO.findRecordById(new PatientProfile(), pid);
        if (patientProfile != null) {
            if (AppUtil.getSafeStr(allergies, "").length() > Constants.ALLERGIES_MAX_LENGTH) {
                allergies = AppUtil.getSafeStr(allergies, "").substring(0, Constants.ALLERGIES_MAX_LENGTH);
            }
            patientProfile.setAllergies(allergies);
            this.patientProfileDAO.saveOrUpdate(patientProfile);
            return "Patient allergies have been updated successfully.";
        }
        return "Some error occurred while saving the record.";
    }

    /////////////////////////////////////////////////////////////////
    public String updateDependentAllergies(int pid, String allergies) throws Exception {
        PatientProfileMembers dependent = (PatientProfileMembers) this.patientProfileDAO.findRecordById(new PatientProfileMembers(), pid);
        if (dependent != null) {
            if (AppUtil.getSafeStr(allergies, "").length() > Constants.ALLERGIES_MAX_LENGTH) {
                allergies = AppUtil.getSafeStr(allergies, "").substring(0, Constants.ALLERGIES_MAX_LENGTH);
            }
            dependent.setAllergies(allergies);
            this.patientProfileDAO.saveOrUpdate(dependent);
            return "Dependent allergies have been updated successfully.";
        }
        return "Some error occurred while saving the record.";
    }
    ////////////////////////////////////////////////////////////////

    public PatientProfileMembers retrieveDependentDetail(Integer id) throws Exception {
        PatientProfileMembers member = (PatientProfileMembers) this.patientProfileDAO.findRecordById(new PatientProfileMembers(), id);
        if (member.getIsAdult() == null) {
            member.setIsAdult(false);
        }
        if (member.getIsAdult()) {
            if (member.getIsApproved() != null && member.getIsApproved()) {
                member.setApprovalDateStr(DateUtil.dateToString(member.getPoaApprovalDate(), "MM/dd/YYYY"));
                member.setExpiryDateStr(DateUtil.dateToString(member.getPoaExpirationDate(), "MM/dd/YYYY"));
                member.setApprovalStr("Yes");
            } else {
                member.setApprovalDateStr("Not Approved");
                member.setExpiryDateStr("Not Approved");
                member.setApprovalStr("NO");
            }
        } else {
            member.setApprovalDateStr("N/A");
            member.setExpiryDateStr("N/A");
            member.setApprovalStr("N/A");
        }
        long age = DateUtil.dateDiffInDays(member.getDob(), new Date());
        long ageYear = age / 365;
        long ageDays = age % 365;
        String ageStr = ageYear + " years " + ageDays + " days";
        member.setAge(ageStr);
        member.setAllergies(AppUtil.getSafeStr(member.getAllergies(), ""));
        return member;
    }

    public String saveRefill(String orderId, Double rxAcqCost, Double originalPtCopay, Double rxReimbCost,
            Integer profitSharePoint, Double actualProfitShare, Double paymentExcludingDelivery,
            int createdBy, int orderStatusId, Integer refillOverriden) throws Exception {
        String id = "0";
//        int refilloverriden = 0;
//        try {
        if (CommonUtil.isNotEmpty(orderId)) {
            Order dbOrder = (Order) this.patientProfileDAO.findRecordById(new Order(), orderId);
            if (dbOrder.getOrderChain() != null) {
                if (refillOverriden != 1) {
                    Date nextDate = dbOrder.getOrderChain().getNextRefillDate();
                    if (nextDate != null) {

                        if (DateUtil.dateDiffInDays(new Date(), nextDate) > 0) {
                            long days = DateUtil.dateDiffInDays(new Date(), nextDate);
                            throw new Exception("Still " + Math.abs(days) + " days remaining for next Refill.");

                        }

                    }
                }
            }

            if (dbOrder != null && dbOrder.getId() != null) {
                if (dbOrder.getOrderStatus() != null && dbOrder.getOrderStatus().getId() != null && (dbOrder.getOrderStatus().getId() == 5 || dbOrder.getOrderStatus().getId() == 6 || dbOrder.getOrderStatus().getId() == 8)) {
                    Set<OrderDetailDTO> dtos = this.saveRefillModule(dbOrder, rxAcqCost, originalPtCopay, rxReimbCost, profitSharePoint, actualProfitShare, paymentExcludingDelivery, false, createdBy, 1);
                    List<OrderDetailDTO> orderBatchList = orderDAO.getOrderBatchsList(dbOrder.getPatientProfile().getId(), orderId);
                    if (!CommonUtil.isNullOrEmpty(orderBatchList)) {
                        id = orderBatchList.stream().findFirst().get().getId();
                    } else {
                        id = dtos.stream().findFirst().get().getId();
                    }
                }
            }
        }

//        } catch (Exception e) {
//            logger.error("Exception:: saveFillRx:: ", e);
//        }
        return id;
    }

    private void populateOrderData(Order order, Double rxAcqCost, Double originalPtCopay, Double rxReimbCost, Integer profitSharePoint, Double actualProfitShare, Double paymentExcludingDelivery) {
        if (rxAcqCost != null) {
            order.setRxAcqCost(rxAcqCost);
        }
        if (originalPtCopay != null) {
            order.setOriginalPtCopay(originalPtCopay);
        }
        if (rxReimbCost != null) {
            order.setRxReimbCost(rxReimbCost);
        }
        if (profitSharePoint != null) {
            order.setProfitSharePoint(profitSharePoint);
        }
        if (actualProfitShare != null) {
            order.setActualProfitShare(actualProfitShare.floatValue());
        }
        if (paymentExcludingDelivery != null) {
            order.setPaymentExcludingDelivery(paymentExcludingDelivery);
        }
    }

    private Set<OrderDetailDTO> saveRefillModule(Order dbOrder, Double rxAcqCost, Double originalPtCopay, Double rxReimbCost, Integer profitSharePoint, Double actualProfitShare, Double paymentExcludingDelivery, boolean refillViaApp, int createdBy, int orderStatusId) throws Exception {
        Set<OrderDetailDTO> detailDTOs = new LinkedHashSet<>();
        DrugDetail detail = null;
        if (dbOrder != null && dbOrder.getId() != null) {
            //dbOrder.setOrderType(Constants.REFILL);
            dbOrder.setRefillDone(1);
            this.patientProfileDAO.update(dbOrder);
            Order ord = new Order();
            BeanUtils.copyProperties(dbOrder, ord);
            if (dbOrder.getDrugDetail() != null) {
                detail = getGenericBasedDrugDetailInfo(dbOrder.getDrugDetail().getDrugDetailId(), AppUtil.getSafeInt(dbOrder.getQty(), 0), dbOrder.getPatientProfile());
                if (detail != null) {
                    ord.setDrugPrice(1d * detail.getDrugCost());//detail.getBasePrice() * AppUtil.getSafeInt(ord.getQty(),0));
                    ord.setPriceIncludingMargins(1d * detail.getTotalPrice());
                    ord.setRedeemPoints(detail.getRedeemedPoints().toString());
                    ord.setRedeemPointsCost(1d * detail.getRedeemedPointsPrice());
                }
            }
            populateOrderData(ord, rxAcqCost, originalPtCopay, rxReimbCost, profitSharePoint, actualProfitShare, paymentExcludingDelivery);
            OrderStatus status = new OrderStatus();
            status.setId(orderStatusId);
            ord.setOrderStatus(status);
            ord.setOrdersPtMessagesList(null);
            ord.setRewardHistorySet(null);
            ord.setOrderTransferImages(null);
            ord.setCreatedBy(createdBy);
            if (ord.getRefillsAllowed() != null && ord.getRefillsAllowed() > 0) {
                int refillRemining = ord.getRefillsAllowed() - 1;
                ord.setRefillsRemaining(refillRemining);
            }
            ord.setCoPayCardDetails(null);
            ord.setOrderType(Constants.REFILL);
            ord.setCreatedOn(new Date());
            ord.setRefillDone(0);

            OrderChain orderChain = ord.getOrderChain();
            if (orderChain != null) {
                orderChain.setRefillAllow(ord.getRefillsAllowed());
                orderChain.setRefillRemaing(ord.getRefillsRemaining() != null && ord.getRefillsRemaining() > 0 ? ord.getRefillsRemaining() - 1 : 0);
            }

            //OrderHistory orderHistory = new OrderHistory();
//            OrderHistory dbOrderHistory = (OrderHistory) patientProfileDAO.findByPropertyUnique(new OrderHistory(), "order.id", dbOrder.getId(), Constants.HIBERNATE_EQ_OPERATOR);
//            if (dbOrderHistory != null && dbOrderHistory.getId() != null) {
//                BeanUtils.copyProperties(dbOrderHistory, orderHistory);
//                orderHistory.setId(null);
            //orderHistory.setOrder(ord);
            //OrderStatus statusHistory = new OrderStatus();
            //statusHistory.setId(1);
            //orderHistory.setOrderStatus(statusHistory);
            //orderHistory.setCreatedBy(0);
            //orderHistory.setCreatedOn(new Date());
            //orderHistory.setUpdatedBy(0);
            //orderHistory.setUpdatedOn(new Date());
            //patientProfileDAO.save(orderHistory);
//            }
            List<OrderHistory> orderHistorys = populateOrderHistory(dbOrder, ord, status);
            if (!CommonUtil.isNullOrEmpty(orderHistorys)) {
                ord.setOrderHistory(orderHistorys);
            } else {
                ord.setOrderHistory(null);
            }
            patientProfileDAO.save(ord);
            /////////////////////////////////
            if (refillViaApp) {
                ord.setAwardedPoints(saveRewardOrderHistory(ord.getRedeemPoints(), ord.getPatientProfile(), ord, "Rx Refill", 6));
            }
            /////////////////////////////////
            List<Order> orderlist = new ArrayList<>();
            orderlist.add(ord);
            detailDTOs = setOrderList(orderlist, null);
        }

        return detailDTOs;
    }

    public int savePrimeryOrders(String orderId, Integer paymentId, Integer deliveryPreferenceId) throws Exception {
//        Order order = new Order();
        Order order = this.orderDAO.getOrdersById("" + orderId);
        DeliveryPreferences deliveryPreference = new DeliveryPreferences();
        deliveryPreference = (DeliveryPreferences) patientProfileDAO.findRecordById(new DeliveryPreferences(), deliveryPreferenceId);
        try {
            order.setPaymentId(paymentId);
            order.setDeliveryPreference(deliveryPreference);
            patientProfileDAO.saveOrUpdate(order);
        } catch (Exception e) {
            logger.error("Exception -> saveRxOrderChainForTransfer", e);
        }
        return 1;
    }

    public long getOrdersCount(int orderStatus, Integer pharmacyId) throws Exception {

        return this.orderDAO.getCountAllOrders(orderStatus, pharmacyId);

    }

    public boolean deleteDrugImagesByOrderId(String orderId) {
        boolean isDelete = false;
        try {
            isDelete = patientProfileDAO.deleteDrugImages(orderId);
        } catch (Exception e) {

            logger.error("Exception: PatientProfileService -> deleteDrugImagesSearchesByOrderId", e);
        }
        return isDelete;
    }

//     public int Reminder(int seconds) throws Exception {
////        Order order = new Order();
//
//        try {
//
////            Timer timer = new Timer();
////            timer.schedule(new RemindTask(), seconds * 1000);
////            timer.cancel(); //Terminate the timer thread
//          Timer timer = new Timer();
//          timer.schedule(new RemindTask(), seconds*1000);
//	}
//
//         public void run() {
//            System.out.println("Time's up!");
//            timer.cancel(); //Terminate the timer thread
//        }
//    }
//
//     }
//       catch (Exception e) {
//            logger.error(e);
//        }
//        return 1;
//    }
    public boolean saveAllPreference(Integer patientId) throws Exception {
        boolean retVal = false;
        PatientProfilePreferences preference = null;
        PatientProfile patientProfile = (PatientProfile) patientProfileDAO.findRecordById(new PatientProfile(), patientId);
        List<PreferencesSetting> preferen = patientProfileDAO.getPreferenceSettingId();

        if (preferen != null && preferen.size() > 0) {
            for (PreferencesSetting ps : preferen) {
                preference = new PatientProfilePreferences();
                preference.setPatient(patientProfile);
                preference.setPreferenceValue(true);
                preference.setPreferenceSetting(ps);
                patientProfileDAO.save(preference);
                retVal = true;
            }
        }

        return retVal;
    }

    private DrugDetail populateDrugDetailCalculation(DrugDetail newDrug, int qty) {
        DrugDetail drug = new DrugDetail();
        if (newDrug != null && newDrug.getDrugDetailId() != null) {
            float drugProfitPercent = newDrug.getMarginPercent();
            float additionalFee = newDrug.getAdditionalFee();
            float basePrice = AppUtil.getSafeFloat(AppUtil.roundOffNumberToTwoDecimalPlaces(newDrug.getBasePrice()), 0f);
            float drugCost = basePrice * qty; //newDrug.getBasePrice() * qty;
            float drugProfit = drugCost * (drugProfitPercent / 100);
            float mktSurcharge = newDrug.getMktSurcharge() != null ? newDrug.getMktSurcharge() : 0.0f;
            float totalFee = drugCost + drugProfit + additionalFee + newDrug.getMktSurcharge();
//                drug.setTotalPrice(totalFee);
            float profitValue = totalFee - drugCost;
            float profitShare = profitValue * Constants.PROFIT_SHARE_PERCENT / 100;
            drug.setAdditionalFee(additionalFee);
            drug.setProfitValue(profitValue);
            drug.setProfitShare(profitShare);
            drug.setTotalPrice(totalFee);
            drug.setMktSurcharge(mktSurcharge);
            drug.setBasePrice(basePrice);
            drug.setDrugCost(drugCost);
            drug.setDrugProfit(drugProfit);
        }

        return drug;
    }

    public Set<OrderDetailDTO> getRefillRx(Integer profileId, String orderId, Integer dependentId) {
        Set<OrderDetailDTO> list = new LinkedHashSet<>();
        try {
            RewardPoints rewardPoints = getMyRewardsPoints(profileId);
            List<Order> dbOrders = patientProfileDAO.getRefillableOrdersListByProfileId(profileId, dependentId);
            Long availablePoints = rewardPoints.getAvailablePoints();
            for (Order order : dbOrders) {

//                OrderDetailDTO newOrder=populateRefillRxDataWithCalculations(order, orderId,true);
                OrderDetailDTO newOrder = populateRefillRxData(order, orderId);
                if (order.getDrugDetail() != null) {
                    newOrder.setDrugId(order.getDrugDetail().getDrugDetailId() != null
                            ? order.getDrugDetail().getDrugDetailId() : 0L);

                    DrugDetail drug = populateDrugDetailCalculation(order.getDrugDetail(), AppUtil.getSafeInt(order.getQty(), 0));
                    availablePoints = getDrugsLookUpCalculation(drug, rewardPoints.getLifeTimePoints(), availablePoints);

                    newOrder.setAvailablePoints(availablePoints);
                    newOrder.setLifeTimePoints(rewardPoints.getLifeTimePoints());
                    newOrder.setFinalPayment(drug.getFinalPrice() != null ? drug.getFinalPrice().doubleValue() : 0d);
                    newOrder.setFinalPaymentStr(AppUtil.roundOffNumberToCurrencyFormat(newOrder.getFinalPayment(), "en", "us"));
                    newOrder.setTotalPayment(drug.getTotalPrice() != null ? drug.getTotalPrice().doubleValue() : 0d);
                    newOrder.setTotalPaymentStr(AppUtil.roundOffNumberToCurrencyFormat(newOrder.getTotalPayment(), "en", "us"));
                    newOrder.setRedeemPoints(drug.getRedeemedPoints() != null ? drug.getRedeemedPoints().toString() : "0");
                    newOrder.setRedeemPointsCost(drug.getRedeemedPointsPrice() != null ? drug.getRedeemedPointsPrice().doubleValue() : 0d);
                    newOrder.setBasePrice(drug.getBasePrice());
                    newOrder.setAdditionalMargin(AppUtil.getSafeDouble("" + drug.getAdditionalFee(), 0d));
                    newOrder.setDrugProfit(drug.getDrugProfit());
                    newOrder.setMktSurcharge(drug.getMktSurcharge() != null ? drug.getMktSurcharge() : 0d);
                    newOrder.setAcqCost(drug.getDrugCost());
                    double handlingFee = order.getHandLingFee() != null ? order.getHandLingFee() : 0d;
                    double finalPayment = newOrder.getFinalPayment() != null ? newOrder.getFinalPayment() : 0d;
                    double paymentIncludingShipping = finalPayment + handlingFee;
                    newOrder.setPaymentIncludingShipping(AppUtil.roundOffNumberToCurrencyFormat(paymentIncludingShipping,
                            "en", "Us"));
                    newOrder.setPaymentIncludingRedmeenCost(AppUtil.roundOffNumberToCurrencyFormat(finalPayment + newOrder.getRedeemPointsCost(),
                            "en", "Us"));
                    double drugCost = drug.getDrugCost() - newOrder.getRedeemPointsCost();
                    newOrder.setFinalDrugCost(AppUtil.getSafeDouble(AppUtil.roundOffNumberToTwoDecimalPlaces(drugCost + handlingFee), 0d));
                }
                newOrder.setRefillAbleCount(dbOrders.size());
                list.add(newOrder);
            }
//            dbOrders.stream().map((order) -> populateRefillRxDataWithCalculations(order, orderId,true)).map((newOrder) -> {
//                newOrder.setRefillAbleCount(dbOrders.size());
//                return newOrder;
//            }).forEach((newOrder) -> {
//                list.add(newOrder);
//            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getRefillRx", e);
        }
        return list;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getDrugsLookUpCalculation(PatientProfile patientProfile, DrugDetail drug, RewardPoints rewardPoints) throws Exception {
        try {

            //calculatePointsFromProfitShare(drug);
            OrderDetailDTO detailDTO = getRedeemPointsWs(rewardPoints.getAvailablePoints().toString());
            //Double dp = drug.getTotalPrice() * 0.9;
            Double dp = drug.getTotalPrice().doubleValue();
            //logger.info("90% of Drug Price:: " + dp);
            if (detailDTO.getRedeemPointsCost() <= dp) {
                logger.info("Drug total Price:: " + drug.getTotalPrice() + " Total RedeemPointsCost:: " + detailDTO.getRedeemPointsCost());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, rewardPoints.getAvailablePoints(), dp);
            } else {
                FeeSettings feeSettings = (FeeSettings) patientProfileDAO.getRecordByType(new FeeSettings(), Constants.PERPOINTVALUE);
                Double p = dp / feeSettings.getFee().doubleValue();
                Long redemainPoints = Math.round(p);
                logger.info("Round value:: " + redemainPoints);
                detailDTO = getRedeemPointsWs(redemainPoints.toString());
                getDrugLookUpCalculation(drug, detailDTO, rewardPoints, redemainPoints, dp);
            }
        } catch (Exception e) {
            logger.error("Exception:: getDrugsLookUpCalculation", e);
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public List<OrderPlaceEmail> getOrderPlaceEmails() {
        List<OrderPlaceEmail> orderPlaceEmails = new ArrayList<>();
        try {
            orderPlaceEmails = patientProfileDAO.findByNestedProperty(new OrderPlaceEmail(), "isActive", Constants.YES, Constants.HIBERNATE_EQ_OPERATOR, "", 0);
        } catch (Exception e) {
            logger.error("Exception# getOrderPlaceEmails", e);
        }
        return orderPlaceEmails;
    }

    private void updatePreviousPaymentDefaultCard(List<PatientPaymentInfo> list, String defaultCard) {
        try {
            if (list != null && list.size() > 0) {
                if (defaultCard.equalsIgnoreCase("Yes")) {
                    for (PatientPaymentInfo info : list) {
                        logger.info("Previous Default Crad " + info.getDefaultCard() + " Id is: " + info.getId());
                        if (info.getDefaultCard().equalsIgnoreCase("Yes")) {
                            info.setDefaultCard("No");
                            patientProfileDAO.merge(info);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception# updatePreviousPaymentDefaultCard# ", e);
        }

    }

    public void updateExistingPOA(String expiryDate, String frontPOAImage, String backPOAImage, Integer dependetId) throws Exception {

        PatientProfileMembers patientProfileMember = (PatientProfileMembers) patientProfileDAO.getObjectById(new PatientProfileMembers(), dependetId);

        if (dependetId != null) {
            patientProfileMember.setExpiryDate(expiryDate);
            patientProfileMember.setFrontPOAImage(frontPOAImage);
            patientProfileMember.setBackPOAImage(backPOAImage);
            patientProfileDAO.saveOrUpdate(patientProfileMember);
        }

    }

    public String isPOAExpire(String orderId) {
        logger.info("Start method isPOAExpire# orderId " + orderId);
        String response = JsonResponseComposer.composeSuccessResponse();
        try {
            Order order = (Order) patientProfileDAO.findRecordById(new Order(), orderId);
            if (order == null || CommonUtil.isNullOrEmpty(order.getId())) {
                logger.info("There is no order found.");
                return response;
            }
            if (order.getPatientProfileMembers() == null || CommonUtil.isNullOrEmpty(order.getPatientProfileMembers().getId())) {
                logger.info("There is no dependent exist against this order id " + orderId);
                return response;
            }
            Date aboutToExpiry = new Date();
            List<PatientProfileMembers> dependentList = orderDAO.getPOAExpiresRecod(aboutToExpiry, order.getPatientProfileMembers().getId());
            if (CommonUtil.isNullOrEmpty(dependentList)) {
                logger.info("There are no POA expiry record exist.");
                return response;
            }

            CampaignMessages campaignMessages = this.getNotificationMsgs("Update Caretaker POA", Constants.PHARMACY_NOTIFICATION);
            if (campaignMessages == null || CommonUtil.isNullOrEmpty(campaignMessages.getMessageId())) {
                logger.info("There are no caretaker msg exist.");
                return response;
            }

            for (PatientProfileMembers patientProfileMembers : dependentList) {
                String dateExpiry = patientProfileMembers.getExpiryDate();
                int result = DateUtil.formatDate(aboutToExpiry, Constants.USA_DATE_FORMATE).compareTo(DateUtil.stringToDate(dateExpiry, Constants.USA_DATE_FORMATE));
                if (result >= 0) {
                    String message = campaignMessages.getSmstext();
                    campaignMessages.setSmstext(message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                            .replace("[dependent_name]", EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getFirstName()) + " " + EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getLastName()))
                            .replace("[poa_expiration_date]", patientProfileMembers.getPoaExpirationDate() != null
                                    ? DateUtil.dateToString(patientProfileMembers.getPoaExpirationDate(), Constants.USA_DATE_FORMATE) : "-")
                            .replace("[POA_ID]", AppUtil.getSafeStr(patientProfileMembers.getId().toString(), ""))
                            .replace("[request_no]", AppUtil.getSafeStr(patientProfileMembers.getId().toString(), "")));
                    this.saveNotificationMessages(campaignMessages, Constants.NO, patientProfileMembers.getPatientId(), orderId);
                    logger.info("Reminder Message : Your Dependent will be exppired on  : " + dateExpiry);
                    logger.info("Record has been added successfully.");
                    response = JsonResponseComposer.composeFailureResponse("Your POA has been expired on this " + dateExpiry + " date.");
                }
            }
        } catch (Exception e) {
            response = JsonResponseComposer.composeFailureResponse(e.getMessage());
            logger.error("Exception# isPOAExpire# ", e);
            e.printStackTrace();
        }
        return response;
    }

    public void updateNotificationMessages(Integer notificationMsgId, Boolean isEventFire) {
        try {
            NotificationMessages notificationMessages = (NotificationMessages) patientProfileDAO.getObjectById(new NotificationMessages(), notificationMsgId);
            if (notificationMessages != null && notificationMessages.getId() != null) {
                notificationMessages.setIsEventFire(isEventFire);
                patientProfileDAO.update(notificationMessages);
            }
        } catch (Exception e) {
            logger.error("Exception# updateNotificationMessages# ", e);
        }
    }

    public String getQuestionAnswerById(Integer id) {
        String json = "";
        try {
            List<QuestionAnswerDTO> questionDTOLst = populateQuestionAnswerDetail(id);
            json = JsonResponseComposer.composeSuccessResponse(questionDTOLst);
        } catch (Exception e) {
            logger.error("Exception#", e);
        }
        return json;
    }

    public List<QuestionAnswerDTO> populateQuestionAnswerDetail(Integer questionId) {
        List<QuestionAnswerDTO> questionDTOLst = new ArrayList<>();
        try {
            List<QuestionAnswer> questionsList = this.patientProfileDAO.findByNestedProperty(new QuestionAnswer(), "id", questionId, Constants.HIBERNATE_EQ_OPERATOR, "id", Constants.HIBERNATE_DESC_ORDER);
            for (QuestionAnswer answer : questionsList) {
                QuestionAnswerDTO dto = new QuestionAnswerDTO();
                dto.setId(answer.getId());
                dto.setQuestion(answer.getQuestion());
                if (answer.getPatientProfile() != null && !CommonUtil.isNullOrEmpty(answer.getPatientProfile().getId())) {
                    dto.setPatientName((answer.getPatientProfile().getFirstName() + " " + answer.getPatientProfile().getLastName()).toUpperCase());
                    String queuePatientDetailPage = "<a href=\"/ConsumerPortal/queuePatientDetailPage/" + answer.getPatientProfile().getId() + "/0/tab1\" target='_blank'>" + dto.getPatientName() + "</a>";
                    dto.setPatientName(queuePatientDetailPage);
                    dto.setPatientPhoneNumber(answer.getPatientProfile().getMobileNumber());
                    dto.setPatientEmail(answer.getPatientProfile().getEmailAddress());

                    if (Integer.toString(Constants.OS_TYPE.IOS).equals(answer.getPatientProfile().getOsType())) {
                        dto.setOsType(Constants.APP_TYPE.IOS);
                    }
                    if (Integer.toString(Constants.OS_TYPE.ANDROID).equals(answer.getPatientProfile().getOsType())) {
                        dto.setOsType(Constants.APP_TYPE.ANDROID);
                    }

                }
                if (answer.getNotificationMessages() != null && !CommonUtil.isNullOrEmpty(answer.getNotificationMessages().getId())) {
                    String subject = answer.getNotificationMessages().getSubject();
                    logger.info("Subject# " + subject);
                    if (CommonUtil.isNotEmpty(subject) && !subject.contains("<<") && !subject.contains("div")) {
                        dto.setMsgTitle(subject);
                    }

                }

                if (CommonUtil.isNullOrEmpty(dto.getMsgTitle())) {
                    if (answer.getOrder() != null && CommonUtil.isNotEmpty(answer.getOrder().getId())) {
                        dto.setMsgTitle(answer.getOrder().getOrderType());
                    }
                }
                dto.setQuestionTime(answer.getQuestionTime());
                String hourMint = answer.getQuestionTime() != null ? DateUtil.dateToString(answer.getQuestionTime(), "HH:mm") : "";
                String sec = answer.getQuestionTime() != null ? DateUtil.dateToString(answer.getQuestionTime(), "ss") : "";
                if (CommonUtil.isNotEmpty(hourMint) && CommonUtil.isNotEmpty(sec)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(hourMint);
                    sb.append(":");
                    sb.append("<small>");
                    sb.append(sec);
                    sb.append("</small>");
                    sb.append("<a href=\"#\">");
                    sb.append(dto.getPatientName());
                    sb.append("</a>");
                    dto.setQuestionTimeStr(sb.toString());
                }
                if (answer.getOrder() != null) {
                    dto.setSystemGeneratedRxNumber(AppUtil.getSafeStr(answer.getOrder().getSystemGeneratedRxNumber(), "").length() > 0 ? AppUtil.getSafeStr(answer.getOrder().getSystemGeneratedRxNumber(), "") : "" + answer.getOrder().getId());
                    dto.setSystemRxNumberLabel(AppUtil.getSafeStr(answer.getOrder().getSystemGeneratedRxNumber(), "").length() > 0 ? "Rx#" : "Req#");
                }
                questionDTOLst.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception# populateQuestionAnswerDetail", e);
        }
        return questionDTOLst;
    }

    public LoginDTO getPatientProfileDetailByToken(String token) {
        LoginDTO patientProfileDTO = new LoginDTO();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null && !CommonUtil.isNullOrEmpty(patientProfile.getId())) {
                patientProfileDTO = CommonUtil.populateProfileUserData(patientProfile);
                this.populateDependentAndIsuranceCountsForPatient(patientProfile.getId(), patientProfileDTO, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientProfileDetailByToken", e);
        }
        return patientProfileDTO;
    }

    public LoginDTO getPatientProfileDetailByToken(String token, Integer dependentId) {
        LoginDTO patientProfileDTO = new LoginDTO();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfileByToken(token);
            if (patientProfile != null && !CommonUtil.isNullOrEmpty(patientProfile.getId())) {
                //populate PatientProfile User Data
                patientProfileDTO = CommonUtil.populateProfileUserData(patientProfile);
                patientProfileDTO.setFirstName(patientProfileDTO.getFirstName() + " " + patientProfileDTO.getLastName());
                //populate Dependent and Isurance Counts For Patient
                this.populateDependentAndIsuranceCountsForPatient(patientProfile.getId(), patientProfileDTO, dependentId);
                //populate DeliveryPreference data
                if (patientProfile.getDeliveryPreferenceId() != null && patientProfile.getDeliveryPreferenceId().getId() != null) {
                    patientProfileDTO.setDprefaId(patientProfile.getDeliveryPreferenceId().getId());
                    patientProfileDTO.setMiles(patientProfile.getMiles());
                    patientProfileDTO.setDeliveryFee(patientProfile.getDeliveryFee());
                }
                //populate Billing Address
                patientProfileDTO.setBillingAddress(patientProfile.getBillingAddress());
                //populate patient insurance card details
                patientProfileDTO.setInsuranceBackCardPath(patientProfile.getInsuranceBackCardPath());
                patientProfileDTO.setInsuranceFrontCardPath(patientProfile.getInsuranceFrontCardPath());
                patientProfileDTO.setCardHolderRelation(patientProfile.getCardHolderRelation());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception # getPatientProfileDetailByToken(String token, Integer dependentId) ", e);
        }
        return patientProfileDTO;
    }

    private void populateDependentAndIsuranceCountsForPatient(Integer patientProfileId, LoginDTO patientProfileDTO, Integer dependentId) throws Exception {
        Long count = this.patientProfileDAO.populateDependentsCount(patientProfileId);
        patientProfileDTO.setDependentCount(count != null ? count : 0L);
        if (!CommonUtil.isNullOrEmpty(dependentId)) {
            Long insCount = this.patientProfileDAO.populateInsCardCountForDependent(dependentId);
            patientProfileDTO.setDependentInsCardCount(insCount != null ? insCount : 0L);
        } else {
            Long insCount = this.patientProfileDAO.populateInsCardCount(patientProfileId);
            patientProfileDTO.setInsCardCount(insCount != null ? insCount : 0L);
        }
    }

    public List<PatientDependantDTO> getPatientDependantList(Integer profileId) {
        List<PatientDependantDTO> list = new ArrayList<>();
        try {
            List<PatientProfileMembers> dbList = patientProfileDAO.getPatientProfileMembersListById(profileId);
            for (PatientProfileMembers members : dbList) {
                PatientDependantDTO dependantDTO = CommonUtil.populatePatientDependant(members);
                list.add(dependantDTO);
            }
        } catch (Exception e) {
            logger.error("Exception -> getProfileMemberses", e);
        }
        return list;
    }

    public PatientDependantDTO getPatientProfileMembersById(Integer id) {
        PatientDependantDTO dependantDTO = null;
        try {
            PatientProfileMembers patientProfileMembers = (PatientProfileMembers) patientProfileDAO.getObjectById(new PatientProfileMembers(), id);
            dependantDTO = CommonUtil.populatePatientDependant(patientProfileMembers);
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileMembersWs", e);
        }
        return dependantDTO;
    }

    public LoginDTO getPatientProfileDataById(Integer id) {
        LoginDTO profile = new LoginDTO();
        try {
            PatientProfile patientProfile = patientProfileDAO.getPatientProfile(id);
            if (patientProfile != null && patientProfile.getId() != null) {
                profile = CommonUtil.populateProfileUserData(patientProfile);
                if (patientProfile.getBillingAddress() != null) {
                    profile.setBillingAddress(patientProfile.getBillingAddress());
                    State state = (State) getObjectById(new State(), patientProfile.getBillingAddress().getStateId());
                    profile.getBillingAddress().setState(state);
                    List lstTaxes = this.patientProfileDAO.findByNestedProperty(
                            new StateRewardTaxes(),
                            "state.id", state.getId(), Constants.HIBERNATE_EQ_OPERATOR, "", 0);
                    if (lstTaxes == null || lstTaxes.isEmpty()) {
                        profile.setStateTaxPercent(0f);
                    } else {
                        StateRewardTaxes rewardTaxes = (StateRewardTaxes) lstTaxes.get(0);
                        profile.setStateTaxPercent(rewardTaxes.getTaxPercentage() == null ? 0f : rewardTaxes.getTaxPercentage());
                    }
                }

                Long totalRewardPoint = patientProfileDAO.getTotalRewardHistoryPoints(id);
                if (totalRewardPoint != null) {
                    profile.setTotalRewardPoints(totalRewardPoint);
                } else {
                    profile.setTotalRewardPoints(0L);
                }

                List<PatientProfileMembers> profileMemberses = patientProfileDAO.getPatientProfileMembersListById(id);
                if (profileMemberses != null && profileMemberses.size() > 0) {
                    List<PatientDependantDTO> listOfDependant = new ArrayList<>();
                    for (PatientProfileMembers members : profileMemberses) {
                        //PatientDependantDTO dependantDTO=populatePatientDependant(members);
                        listOfDependant.add(CommonUtil.populatePatientDependant(members));
                    }
                    profile.setPatientProfileMembersList(listOfDependant);
                }

                List<PatientDeliveryAddress> deliveryAddressList = patientProfile.getPatientDeliveryAddresses();
                profile.setPatientDeliveryAddresses(deliveryAddressList);
                if (deliveryAddressList != null && deliveryAddressList.size() > 0) {
                    PatientDeliveryAddress address = deliveryAddressList.get(0);
                    profile.setDefaultAddress(AppUtil.getSafeStr(address.getDescription(), "") + " " + AppUtil.getSafeStr(address.getAddress(), "") + ":"
                            + AppUtil.getSafeStr(address.getZip(), ""));
                    profile.setDefaultAddresszip(AppUtil.getSafeStr(address.getZip(), ""));
                }
                List<Order> ordersList = patientProfileDAO.getOrdersListByProfileId(id);
                if (ordersList.size() > 0) {
                    profile.setOrders(ordersList);
                } else {
                    profile.setOrders(ordersList);
                }
                profile.setPatientProfileHealthsList(patientProfile.getPatientProfileHealthsList());
            } else {
                logger.info("PatientProfile id is null: " + id);
            }
        } catch (Exception e) {
            logger.error("Exception: PatientProfileService -> getPatientProfileDataById", e);
        }
        return profile;
    }

    public List<PatientDependantDTO> getCareGiverList(Integer id) {
        List<PatientDependantDTO> careGiverList = new ArrayList<>();
        try {
            List<BusinessObject> list = new ArrayList<>();
            list.add(new BusinessObject("isAdult", Boolean.TRUE, Constants.HIBERNATE_EQ_OPERATOR));
            //list.add(new BusinessObject("isApproved", Boolean.FALSE, Constants.HIBERNATE_EQ_OPERATOR));
            if (!CommonUtil.isNullOrEmpty(id)) {
                list.add(new BusinessObject("id", id, Constants.HIBERNATE_EQ_OPERATOR));
            }
            List<PatientProfileMembers> dbMembersList = patientProfileDAO.findByNestedProperty(new PatientProfileMembers(), list, "createdOn", Constants.HIBERNATE_DESC_ORDER);
            for (PatientProfileMembers members : dbMembersList) {
                PatientDependantDTO careGiverM = CommonUtil.populatePatientDependant(members);
                Long rxCount = (Long) patientProfileDAO.getTotalRecords(new Order(), "patientProfileMembers.id", members.getId(), Constants.HIBERNATE_COUNT_FUNCTION, "*");
                careGiverM.setRxCount(rxCount);
                if (!CommonUtil.isNullOrEmpty(members.getPatientId())) {
                    PatientProfile patientProfile = (PatientProfile) patientProfileDAO.findRecordById(new PatientProfile(), members.getPatientId());
                    if (patientProfile == null) {
                        continue;
                    }
                    careGiverM.setFullPatientName(EncryptionHandlerUtil.getDecryptedString(patientProfile.getFirstName()) + " " + EncryptionHandlerUtil.getDecryptedString(patientProfile.getLastName()));

                    if (members.getDisapprove() != null && members.getDisapprove() == 1) {
                        careGiverM.setApprovalStr("Rejected");
                        careGiverM.setDisabledStr("1");
                    } else if (members.getIsApproved() != null && members.getIsApproved()) {
                        careGiverM.setApprovalStr("Approved");
                        careGiverM.setDisabledStr("1");
                    } else {
                        careGiverM.setApprovalStr("Pending");
                        careGiverM.setDisabledStr("0");
                    }
                }

                careGiverList.add(careGiverM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: getCareGiverList", e);
        }
        return careGiverList;
    }

    public void updateCareTaker(Integer id, String frontPOAImage, String backPOAImage) {
        PatientProfileMembers patientProfileMembers = (PatientProfileMembers) patientProfileDAO.findRecordById(new PatientProfileMembers(), id);
        try {
            if (patientProfileMembers != null) {
                patientProfileMembers.setFrontPOAImage(frontPOAImage);
                patientProfileMembers.setBackPOAImage(backPOAImage);
                patientProfileMembers.setDisapprove(0);
                patientProfileMembers.setDisApproveDate(null);
                patientProfileDAO.saveOrUpdate(patientProfileMembers);
//            patientProfileDAO.update(patientProfileMembers);
            } else {
                logger.info("patientProfileMembers id is null: " + patientProfileMembers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: updateCareTaker", e);
        }

    }

    public void updateCareTakerExpiry(Integer id, String expiryDate) {
        PatientProfileMembers patientProfileMembers = (PatientProfileMembers) patientProfileDAO.findRecordById(new PatientProfileMembers(), id);
        try {
            if (patientProfileMembers != null) {
                patientProfileMembers.setExpiryDate(expiryDate);
                patientProfileMembers.setPoaExpirationDate(DateUtil.stringToDate(expiryDate, "MM/dd/yyyy"));
                patientProfileDAO.saveOrUpdate(patientProfileMembers);
//            patientProfileDAO.update(patientProfileMembers);
            } else {
                logger.info("patientProfileMembers id is null: " + patientProfileMembers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: updateCareTaker", e);
        }

    }

    public String processSameDayShippingRxOrders(String orderIds) {
        String json = "";
        try {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            List<String> lst = Arrays.asList(orderIds.split(","));
            Double handLingFee = 0.0d, finalPayment = 0.0d, totalAmount = 0.0d;
            List<Order> listOfOrders = patientProfileDAO.getOrderListByIds(lst);
            for (Order order : listOfOrders) {
                //(Order) patientProfileDAO.findRecordById(new Order(), id);
                Double handlingFee = order.getHandLingFee() != null ? order.getHandLingFee() : 0d;
                handLingFee = handLingFee + handlingFee;
                orderDetailDTO.setHandLingFee(handLingFee);

                orderDetailDTO.setHandlingFeeStr(AppUtil.roundOffNumberToCurrencyFormat(handlingFee, "en", "Us"));

                Double finalPyment = order.getFinalPayment() != null ? order.getFinalPayment() : 0d;
                finalPayment = finalPayment + finalPyment;
                orderDetailDTO.setPaymentExcludingDeliveryStr(AppUtil.roundOffNumberToCurrencyFormat(finalPayment, "en", "Us"));

                totalAmount = finalPyment + totalAmount; //finalPyment + handLingFee + totalAmount;
                orderDetailDTO.setFinalPayment(totalAmount);
                orderDetailDTO.setFinalPaymentStr(AppUtil.roundOffNumberToCurrencyFormat(totalAmount, "en", "Us"));
            }
            orderDetailDTO.setTotalRxShipped((long) lst.size());
            json = JsonResponseComposer.composeSuccessResponse(orderDetailDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: processSameDayShippingRxOrders", e);
        }
        return json;
    }

    public List<Order> getProcessedOrdersByPatientId(Integer patientId) {
        List<Order> listOfOrderDetail = new ArrayList<>();
        try {
            List<Integer> lstStatus = CommonUtil.generateStatusIdsList();
            listOfOrderDetail = patientProfileDAO.getRxProgramOrdersByPatientId(patientId, lstStatus);
        } catch (Exception e) {
            logger.error("Exception:: getProcessedOrdersByPatientId", e);
        }
        return listOfOrderDetail;
    }

    public boolean isConfirmReadyToDeliveryRxOrders(List<ReadyToDeliveryRxDTO> lstReadyToDeliveryRx, MultipartFile signature) {
        logger.info("Start ReadyToDeliveryRxOrders process " + lstReadyToDeliveryRx.size());
        boolean isSaved = false;
        try {
            for (ReadyToDeliveryRxDTO readyToDeliveryRx : lstReadyToDeliveryRx) {
                logger.info("Order Id# " + readyToDeliveryRx.getOrderId() + " readyToDeliveryRx.getIsDelivered()# " + readyToDeliveryRx.getIsDelivered());
                Order order = (Order) patientProfileDAO.findByPropertyUnique(new Order(), "id", readyToDeliveryRx.getOrderId(), Constants.HIBERNATE_EQ_OPERATOR);
                order.setReadyToDeliverRxDate(new Date());
                if (CommonUtil.isNotEmpty(readyToDeliveryRx.getHandlingFee())) {
                    logger.info("Handling Fee# " + readyToDeliveryRx.getHandlingFee());
                    order.setHandLingFee(AppUtil.getSafeDouble(readyToDeliveryRx.getHandlingFee(), 0d));
                }
                if (CommonUtil.isNotEmpty(readyToDeliveryRx.getFinalPayment())) {
                    logger.info("Prescription Out Of Pocket# " + readyToDeliveryRx.getFinalPayment());
                    double finalPayment = AppUtil.getSafeDouble(readyToDeliveryRx.getFinalPayment(), 0d);
                    order.setPaymentExcludingDelivery(finalPayment);
                    //finalPayment = finalPayment + AppUtil.getSafeDouble(readyToDeliveryRx.getHandlingFee(), 0d);
                    logger.info("Final Payment# " + finalPayment);
                    order.setFinalPayment(finalPayment);
                }
                OrderStatus os = new OrderStatus();
                if (readyToDeliveryRx.getIsDelivered()) {
                    os.setId(Constants.ORDER_STATUS.READY_TO_DELIVER_ID);
                } else {
                    //os.setId(Constants.ORDER_STATUS.CANCEL_ORDER_ID);
                }
                logger.info("OrderStatus is# " + os.getId());
                order.setOrderStatus(os);
                PatientDeliveryAddress patientDeliveryAddress = null;
                if (!CommonUtil.isNullOrEmpty(readyToDeliveryRx.getDelievryAddressId())) {
                    patientDeliveryAddress = patientProfileDAO.getPatientDeliveryAddressById(readyToDeliveryRx.getDelievryAddressId());
                    if (patientDeliveryAddress != null && !CommonUtil.isNullOrEmpty(patientDeliveryAddress.getId())) {
                        order.setStreetAddress(patientDeliveryAddress.getAddress());
                        order.setCity(patientDeliveryAddress.getCity());
                        order.setZip(patientDeliveryAddress.getZip());
                        order.setAddressLine2(patientDeliveryAddress.getDescription());
                        order.setApartment(patientDeliveryAddress.getApartment());
                        if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                            order.setState(patientDeliveryAddress.getState().getName());
                        }
                    }
                }
                if (!CommonUtil.isNullOrEmpty(readyToDeliveryRx.getDeliveryPreferenceId())) {
                    DeliveryPreferences deliveryPreferences = patientProfileDAO.getDeliveryPreferenceById(readyToDeliveryRx.getDeliveryPreferenceId());
                    if (deliveryPreferences != null && !CommonUtil.isNullOrEmpty(readyToDeliveryRx.getDeliveryPreferenceId())) {
                        order.setDeliveryPreference(deliveryPreferences);
                    }
                }

                order.setReadyToDeliverRxOrders(this.saveReadyToDeliverRxOrders(order, patientDeliveryAddress, readyToDeliveryRx));
                patientProfileDAO.saveOrUpdate(order);
                if (signature != null && (!signature.isEmpty())) {
                    Integer patientId = 0;
                    PatientProfile patientProfile = order.getPatientProfile();
                    if (Objects.equals(patientProfile.getId(), patientId)) {
                        logger.info("Patient Signature already saved against this " + patientId);
                        break;
                    }
                    patientId = patientProfile.getId();
                    String dateStr = DateUtil.dateToString(new Date(), "yy-MM-dd hh:mm:ss");
                    dateStr = dateStr.replace(":", "-");
                    dateStr = dateStr.replace(" ", "-");
                    String ext = FileUtil.determineImageFormat(signature.getBytes());
                    String completeName = "Sign_" + patientId + "_" + dateStr + "." + ext;
                    String signatureImagPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
                    logger.info("Complete signature Image Path: " + signatureImagPath);

                    FileCopyUtils.copy(signature.getBytes(),
                            new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
                    CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND"));
                    patientProfile.setSignature(signatureImagPath);

                    patientProfileDAO.saveOrUpdate(patientProfile);
                }
                isSaved = true;
            }
        } catch (Exception e) {
            isSaved = false;
            e.printStackTrace();
            logger.error("Exception:: isConfirmReadyToDeliveryRxOrders", e);
        }
        return isSaved;
    }

    private List<ReadyToDeliverRxOrders> listOfReadyToDeliverRxOrders(Order order, ReadyToDeliveryRxDTO readyToDeliveryRx) {
        List<BusinessObject> lstObj = new ArrayList();
        BusinessObject obj = new BusinessObject("patientProfile.id", order.getPatientProfile().getId(), Constants.HIBERNATE_EQ_OPERATOR);
        lstObj.add(obj);
        obj = new BusinessObject("deliveryPreferences.id", order.getDeliveryPreference().getId(), Constants.HIBERNATE_EQ_OPERATOR);
        lstObj.add(obj);
        if (!CommonUtil.isNullOrEmpty(readyToDeliveryRx.getDelievryAddressId())) {
            obj = new BusinessObject("patientDeliveryAddress.id", readyToDeliveryRx.getDelievryAddressId(), Constants.HIBERNATE_EQ_OPERATOR);
            lstObj.add(obj);
        }
        obj = new BusinessObject("isShipped", false, Constants.HIBERNATE_EQ_OPERATOR);
        lstObj.add(obj);
        List<ReadyToDeliverRxOrders> listOfReadyToDeliverRxOrders = patientProfileDAO.findByNestedProperty(new ReadyToDeliverRxOrders(), lstObj, "", 0);
        return listOfReadyToDeliverRxOrders;
    }

    private ReadyToDeliverRxOrders saveReadyToDeliverRxOrders(Order order, PatientDeliveryAddress patientDeliveryAddress, ReadyToDeliveryRxDTO readyToDeliveryRx) {
        logger.info("Start saving saveReadyToDeliverRxOrders");
        ReadyToDeliverRxOrders readyToDeliverRxOrders = new ReadyToDeliverRxOrders();
        try {
            readyToDeliverRxOrders.setPatientProfile(order.getPatientProfile());
            readyToDeliverRxOrders.setDeliveryPreferences(order.getDeliveryPreference());
            if (patientDeliveryAddress != null && !CommonUtil.isNullOrEmpty(patientDeliveryAddress.getId())) {
                readyToDeliverRxOrders.setPatientDeliveryAddress(patientDeliveryAddress);
            }
            readyToDeliverRxOrders.setIsShipped(false);
            readyToDeliverRxOrders.setPostedDate(new Date());
            List<ReadyToDeliverRxOrders> listOfReadyToDeliverRxOrders = this.listOfReadyToDeliverRxOrders(order, readyToDeliveryRx);
            if (CommonUtil.isNullOrEmpty(listOfReadyToDeliverRxOrders)) {
                patientProfileDAO.save(readyToDeliverRxOrders);
            } else {
                readyToDeliverRxOrders = listOfReadyToDeliverRxOrders.get(0);
            }
            //order.setReadyToDeliverRxOrders(readyToDeliverRxOrders);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: saveReadyToDeliverRxOrders", e);
        }
        logger.info("End saving saveReadyToDeliverRxOrders " + readyToDeliverRxOrders.getId());
        return readyToDeliverRxOrders;
    }
}
