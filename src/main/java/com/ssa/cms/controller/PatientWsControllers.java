package com.ssa.cms.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssa.cms.common.Constants;
import com.ssa.cms.delegate.CMSService;
import com.ssa.cms.delegate.SetupService;
import com.ssa.cms.dto.CoPayCardDetailsDTO;
import com.ssa.cms.dto.DeliveryDistanceFeeDTO;
import com.ssa.cms.dto.DrugBrandDTO;
import com.ssa.cms.dto.DrugCategoryDTO;
import com.ssa.cms.dto.DrugDTO;
import com.ssa.cms.dto.DrugModelDtoConversion;
import com.ssa.cms.dto.DrugPagingDTO;
import com.ssa.cms.dto.LoginDTO;
import com.ssa.cms.dto.OrderDetailDTO;
import com.ssa.cms.dto.OrderStatusDTO;
import com.ssa.cms.dto.PatientDeliveryAddressDTO;
import com.ssa.cms.dto.PatientDependantDTO;
import com.ssa.cms.dto.PatientInsuranceDetailsDTO;
import com.ssa.cms.dto.PatientPaymentDTO;
import com.ssa.cms.dto.PatientPreferencesDTO;
import com.ssa.cms.dto.PatientProfileDTO;
import com.ssa.cms.dto.ReadyToDeliveryRxDTO;
import com.ssa.cms.dto.TransferRxQueueDTO;
import com.ssa.cms.model.CMSEmailContent;
import com.ssa.cms.model.CMSPageContent;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CoPayCardDetails;
import com.ssa.cms.model.ContactUs;
import com.ssa.cms.model.DeliveryPreferences;
import com.ssa.cms.model.Drug;
import com.ssa.cms.model.DrugDetail;
import com.ssa.cms.model.JsonResponse;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.OrderChain;
import com.ssa.cms.model.OrderHistory;
import com.ssa.cms.model.OrderPlaceEmail;
import com.ssa.cms.model.OrderTransferImages;
import com.ssa.cms.model.PatientAddress;
import com.ssa.cms.model.PatientDeliveryAddress;
import com.ssa.cms.model.PatientInsuranceDetails;
import com.ssa.cms.model.PatientPaymentInfo;
import com.ssa.cms.model.PatientProfile;
import com.ssa.cms.model.PatientProfileMembers;
import com.ssa.cms.model.RewardPoints;
import com.ssa.cms.model.State;
import com.ssa.cms.model.TransferRequest;
import com.ssa.cms.service.ConsumerRegistrationService;
import com.ssa.cms.service.DoDirectPayment;
import com.ssa.cms.service.PatientProfileService;
import com.ssa.cms.thread.SMSAndEmailSendThread;
import com.ssa.cms.util.AppUtil;
import com.ssa.cms.util.CommonUtil;
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EmailSenderUtil;
import com.ssa.cms.util.EncryptionHandlerUtil;
import com.ssa.cms.util.ExportToPdf;
import com.ssa.cms.util.FileUtil;
import com.ssa.cms.util.PropertiesUtil;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.TextFlowUtil;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import urn.ebay.api.PayPalAPI.DoDirectPaymentResponseType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;

/**
 * This web service is used to handle all profile related functionalities i.e.
 * create profile, send verification code ... etc.
 *
 * @author Saber
 */
@RestController
public class PatientWsControllers {

    private static final Log logger = LogFactory.getLog(PatientWsControllers.class.getName());

    @Autowired
    private PatientProfileService patientProfileService;
    @Autowired
    private SetupService setupService;
    @Autowired
    private CMSService cMSService;
    private Pattern pattern;
    private Matcher matcher;
    String[] str = new String[0];
    @Autowired
    ServletContext servletContext;
    @Autowired
    private ConsumerRegistrationService consumerRegistrationService;

    /**
     * This function is used to validate the phone number, send verification
     * code and create basic profile
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param securityToken
     * @return Its return JSON of profile
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @RequestMapping(value = "/updateProfileWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateProfile(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException, ParseException {
        //public Object getObjectById(Object obj, String id)
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            profile.setEmailAddress(email);
            boolean isSaved = updateprofile(patientProfile, profile);
            if (isSaved) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Profile Updated successfully");
                profile.setSuccessOrFailure(Constants.SUCCESS);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }

        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);

    }

    /**
     * This function is used to used to process payments authorized by patient
     *
     * @param cardId
     * @param securityToken
     * @param orderId
     * @param paymentValue
     * @param notificationMsgId
     * @return Its return JSON of profile
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @RequestMapping(value = "/paymentAutorizationWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object authorizePayment(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "cardId", required = false) String cardId,
            @RequestParam(value = "paymentValue", required = false) String paymentValue,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException {
        //public Object getObjectById(Object obj, String id)
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            CommonUtil.inValidSecurityToken(jsonResponse);
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        Order order = (Order) patientProfileService.getObjectById(new Order(), orderId);
        if (!CommonUtil.isNullOrEmpty(order.getPaymentId())) {
            cardId = order.getPaymentId().toString();
        }
        int isSaved = 0;
        int oderIdSafeInteger = AppUtil.getSafeInt(order.getId(), 0);
        String paymentAuthorization = "1";
        if (patientProfile.getId() != null && order.getId() != null && Objects.equals((order.getPatientProfile()).getId(), patientProfile.getId())) {
            try {
                PatientPaymentInfo patientPaymentInfo = patientProfileService.getPatientPaymentInfoByCardId(cardId);
                if ("1".equals(order.getPaymentAuthorization())) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("This payment has already been completed!");
                    return objectMapper(objectMapper, jsonResponse);
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////
                if (!AppUtil.getSafeStr(order.getPaymentAuthorization(), "").equals("1")) {
                    if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                        logger.info("is Default Card: " + patientPaymentInfo.getDefaultCard());
                        logger.info(" Card NO: " + patientPaymentInfo.getCardNumber());
                        if (!validCardTypeLength(patientPaymentInfo.getCardType(), patientPaymentInfo.getCvvNumber(), jsonResponse)) {
                            return objectMapper(objectMapper, jsonResponse);
                        }
                        if (CommonUtil.isNotEmpty(paymentValue)
                                && CommonUtil.getStrToBigDecimal(paymentValue).compareTo(BigDecimal.ZERO) > 0) {
                            //BigDecimal finalPaymentPlusHandlingFee = CommonUtil.getStrToBigDecimal(finalPayment).add(feeNumeric);
//                            DoDirectPayment payment = new DoDirectPayment();
//                            DoDirectPaymentResponseType response = payment.doDirectPayment(patientPaymentInfo.getCardType(),
//                                    patientPaymentInfo.getCardNumber(), patientPaymentInfo.getExpiryDate(),
//                                    patientPaymentInfo.getCardHolderName(), "" + paymentValue, patientPaymentInfo.getCvvNumber());
//                            logger.info("DoDirectPaymentResponseType: " + response);
//                            if (!response.getAck().getValue().equalsIgnoreCase("success")) {
//                                List<ErrorType> errorList = response.getErrors();
//                                jsonResponse.setErrorCode(0);
//                                jsonResponse.setErrorMessage(errorList.get(0).getLongMessage());
//                                return objectMapper(objectMapper, jsonResponse);
//                            }
                            isSaved = consumerRegistrationService.updateOrderStatusAndAuthorization(
                                    oderIdSafeInteger, 0, Constants.ORDER_STATUS.RESPONSE_RECEIVED_ID,
                                    paymentAuthorization, 0, Constants.PATIENT_RESPONSES.PAYMENT_AUTHORIZATION);
                        } else {
                            jsonResponse.setErrorCode(0);
                            jsonResponse.setErrorMessage("Payment amount is zero & can't be authorized.");
                            return objectMapper(objectMapper, jsonResponse);
                        }
                    } else {
                        logger.info("Payment info is null");
                        jsonResponse.setErrorCode(0);
                        jsonResponse.setErrorMessage("Payment info is null");
                        return objectMapper(objectMapper, jsonResponse);
                    }
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Payment has already been completed!");
                    return objectMapper(objectMapper, jsonResponse);
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////

                //int isSaved =  consumerRegistrationService.updateOrderStatusAndAuthorization(oderIdSafeInteger, 0, 10, paymentAuthorization);
                if (isSaved == 1) {
                    isPatientMsgResponse(notificationMsgId);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Payment Authorization successful");
                    profile.setSuccessOrFailure(Constants.SUCCESS);
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                    return objectMapper(objectMapper, jsonResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Exception:: ", e);
            }
        } else {

            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);

    }

    /**
     * This function is used to store questions asked by patients code and
     * create basic profile
     *
     * @param securityToken
     * @param orderId
     * @param question
     * @param notificationMsgId
     * @return Its return JSON of profile
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @RequestMapping(value = "/placeQuestionWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object questionPlace(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "question", required = false) String question,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException {
        //public Object getObjectById(Object obj, String id)

        try {
            PatientProfile profile = new PatientProfile();
            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
//            Order order = (Order) patientProfileService.getObjectById(new Order(), orderId);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            boolean isSaved = patientProfileService.saveQuestion(orderId, question, patientProfile, notificationMsgId);

            if (isSaved) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Question  has been saved successfully.");
                profile.setSuccessOrFailure(Constants.SUCCESS);
                return objectMapper(objectMapper, jsonResponse);

            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    ////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/placePOAQuestionWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object questionPlaceByDependent(
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "dependentId", required = false) Integer dependentId,
            @RequestParam(value = "question", required = false) String question,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException {
        //public Object getObjectById(Object obj, String id)

        try {
            PatientProfile profile = new PatientProfile();
            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
//            Order order = (Order) patientProfileService.getObjectById(new Order(), orderId);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            boolean isSaved = patientProfileService.saveDependentQuestion(dependentId, question, patientProfile);

            if (isSaved) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Question  has been saved successfully.");
                profile.setSuccessOrFailure(Constants.SUCCESS);
                return objectMapper(objectMapper, jsonResponse);

            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * This function is used to cancel an order if its status is not filled,
     * shipped or delivered code and create basic profile
     *
     * @param securityToken
     * @param orderId
     * @param notificationMsgId
     * @return Its return JSON of profile
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @RequestMapping(value = "/orderCancelledWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object cancelOrder(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException {
        //public Object getObjectById(Object obj, String id)
        try {
            PatientProfile profile = new PatientProfile();
            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            Order order = (Order) patientProfileService.findOrderById(orderId);

            int isSaved = 0;
            int oderIdSafeInteger = AppUtil.getSafeInt(order.getId(), 0);
            String orderStatusName = order.getOrderStatus().getName();//this.patientProfileService.getOrderStatusName(orderId);
            if (patientProfile.getId() != null && order.getId() != null && Objects.equals((order.getPatientProfile()).getId(), patientProfile.getId())) {
                if (orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.SHIPPED) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.PICKUP_AT_PHARMACY) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.DELIVERY) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.PAYMENT_AUTHORIZED)) {
                    jsonResponse.setErrorMessage("Your Order can't be cancelled now since its status is " + orderStatusName);
                    return objectMapper(objectMapper, jsonResponse);
                }
                if (orderStatusName.equals(Constants.ORDER_STATUS.CANCELLED)) {
                    jsonResponse.setErrorMessage("Your order already has been cancelled. ");
                    return objectMapper(objectMapper, jsonResponse);
                }
//                if (orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.WAITING_PT_RESPONSE)
//                        && order.getRequiresDeletion() == 1) {
//                    jsonResponse.setErrorMessage("Your cancel request has already been submitted. ");
//                    return objectMapper(objectMapper, jsonResponse);
//                }
                // isSaved = consumerRegistrationService.updateOrderStatus(oderIdSafeInteger, 0, 11, "");
                isSaved = consumerRegistrationService.updateOrderStatusByPatient(oderIdSafeInteger, 11);
            }
            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Cancel request has been submitted successfully.");
                profile.setSuccessOrFailure(Constants.SUCCESS);
                return objectMapper(objectMapper, jsonResponse);

            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private boolean updateprofile(PatientProfile patientProfile, PatientProfile profile) {
        boolean isSaved;
        if (patientProfile != null && patientProfile.getId() != null) {
            logger.info("Update Pending Profile: " + patientProfile.getId());
            profile.setId(patientProfile.getId());
            patientProfile.setFirstName(profile.getFirstName());
            patientProfile.setLastName(profile.getLastName());
            patientProfile.setEmailAddress(profile.getEmailAddress());
            isSaved = patientProfileService.update(patientProfile);
        } else {
            isSaved = patientProfileService.savePatientInfo(profile);
        }
        return isSaved;
    }

    /**
     * This function is used to create patient profile code and create basic
     * profile
     *
     * @param firstName
     * @param lastName
     * @param mobileNumber
     * @param email
     * @param dob
     * @param gender
     * @param osType
     * @param deviceToken
     * @return Its return JSON of profile
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @RequestMapping(value = "/createProfileWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object createProfile(@RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "osType", required = false) String osType,
            @RequestParam(value = "deviceToken", required = false) String deviceToken) throws IOException, ParseException {
        System.out.println("Mobile Number: " + mobileNumber);
        logger.info("MobileNumber: " + mobileNumber);
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (!validateCreateProfileField(firstName, jsonResponse, lastName, mobileNumber, email, dob, gender)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            mobileNumber = CommonUtil.replaceAllStr(mobileNumber, "-", "");
            PatientProfile patientProfile = patientProfileService.getPatientProfileByPhoneNumber(mobileNumber);
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            profile.setMobileNumber(mobileNumber);
            profile.setEmailAddress(email);
            profile.setGender(gender);
            profile.setCreatedOn(new Date());
            profile.setUpdatedOn(new Date());
            profile.setOsType(osType);
            profile.setDeviceToken(deviceToken);
            Integer verificationCode = RedemptionUtil.getRandomNumber();
            logger.info("VerificationCode is: " + verificationCode);
            System.out.println("VerificationCode is: " + verificationCode);
            profile.setVerificationCode(verificationCode);
            if (!validateDOB(dob, profile, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String message = getWelcomeMessage(verificationCode);
            boolean isSaved = saveorupdateprofile(patientProfile, profile);
            //success
            if (isSaved) {
                if (patientProfileService.sendVerificationCode(mobileNumber, message)) {
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.VERIFICATION_CODE_SENT);
                    profile.setSuccessOrFailure(Constants.SUCCESS);
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.INVALID_PHONE_MESSAGE);
                    return objectMapper(objectMapper, jsonResponse);
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }
            jsonResponse.setData(patientProfileService.getProfileListWs(mobileNumber, profile.getStatus()));
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private String getWelcomeMessage(Integer verificationCode) {
        String message = "Your Rx-Direct verification code: " + verificationCode + " to complete your registration.";
        logger.info("Message:: " + message);
        System.out.println("Message:: " + message);
        return message;
    }

    private boolean validateDOB(String dob, PatientProfile profile, JsonResponse jsonResponse) throws ParseException, IOException {
        if (dob != null && !"".equals(dob)) {
            Date dateOfBirth = DateUtil.stringToDate(dob, "MM/dd/yyyy");
            logger.info("After Format Date Of Birth is: " + dateOfBirth);
            System.out.println("After Format Date Of Birth is: " + dateOfBirth);
            profile.setDob(dateOfBirth);
            if (!validateAge(dateOfBirth, profile, jsonResponse)) {
                return false;
            }
        }
        return true;
    }

    private boolean saveorupdateprofile(PatientProfile patientProfile, PatientProfile profile) {
        boolean isSaved;
        if (patientProfile != null && patientProfile.getId() != null) {
            logger.info("Update Pending Profile: " + patientProfile.getId());
            System.out.println("Update Pending Profile: " + patientProfile.getId());
            profile.setId(patientProfile.getId());
            patientProfile.setMobileNumber(profile.getMobileNumber());
            patientProfile.setFirstName(profile.getFirstName());
            patientProfile.setLastName(profile.getLastName());
            patientProfile.setVerificationCode(profile.getVerificationCode());
            patientProfile.setEmailAddress(profile.getEmailAddress());
            patientProfile.setDob(profile.getDob());
            patientProfile.setGender(profile.getGender());
            patientProfile.setUpdatedOn(new Date());
            isSaved = patientProfileService.update(patientProfile);
        } else {
            logger.info("save if mobile number not exist: ");
            System.out.println("save if mobile number not exist: ");
            isSaved = patientProfileService.savePatientInfo(profile);
        }
        return isSaved;
    }

    private boolean validateCreateProfileField(String firstName, JsonResponse jsonResponse, String lastName, String mobileNumber, String email, String dob, String gender) {
        if (CommonUtil.isNullOrEmpty(firstName)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_FIRST_NAME);
            return false;
        }
        if (CommonUtil.isNullOrEmpty(lastName)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_LAST_NAME);
            return false;
        }
        if (CommonUtil.isNullOrEmpty(mobileNumber)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_MOBILE_NO);
            return false;
        }
        if (CommonUtil.isNotEmpty(mobileNumber)) {
            mobileNumber = CommonUtil.replaceAllStr(mobileNumber, "-", "");
            if (patientProfileService.isPatientPhoneNumberExist(mobileNumber)) {
                jsonResponse.setData(mobileNumber);
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.PHONE_NUMBER_EXIST);
                return false;
            }
        }
        if (CommonUtil.isNullOrEmpty(email)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.EMAIL_REQ);
            return false;
        }
        if (CommonUtil.isNotEmpty(email)) {
            pattern = Pattern.compile(Constants.EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_EMAIL);
                return false;
            }
        }
        if (CommonUtil.isNullOrEmpty(dob)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.DOB_REQ);
            return false;
        }
        if (CommonUtil.isNullOrEmpty(gender)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.GENDER_TYPE_REQ);
            return false;
        }
        return true;
    }

    /**
     * This function is used to verify account
     *
     * @param mobileNumber
     * @param code
     * @param firebaseToken
     * @return Its return JSON of profile
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/accountVerificationWs", produces = "application/json")
    public @ResponseBody
    Object accountVerification(
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "firebaseToken", required = false) String firebaseToken) throws IOException {
        PatientProfileDTO profile = new PatientProfileDTO();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Verification Code is: " + code + " Phone number is: " + mobileNumber);
        if (CommonUtil.isNullOrEmpty(code)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.VERIFICATION_CODE_REQ);
            return objectMapper(objectMapper, jsonResponse);
        }
        profile.setVerificationCode(Integer.parseInt(code));
        profile.setMobileNumber(mobileNumber);
        if (patientProfileService.isVerificationCodeExist(mobileNumber, AppUtil.getSafeInt(code, 0), firebaseToken)) {
            logger.info("valid code: " + code);
            jsonResponse.setData(profile);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("valid code");
        } else {
            logger.info("Invalid verification code: " + code);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_CODE);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is to used updated profile email on the base of give
     * profile id
     *
     * @param profileId
     * @param email
     * @return Its return JSON of profile
     * @throws java.io.IOException
     *
     * This API is not used
     */
    @RequestMapping(value = "/emailVerificationWs", produces = "application/json")
    public @ResponseBody
    Object emailVerification(
            @RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "email", required = false) String email) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("Emial Address is: " + email + " Profile Id is: " + profileId);
            if (!validateEmailField(profileId, jsonResponse, email)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            PatientProfile profile = patientProfileService.getPatientProfileById(profileId);
            if (profile != null && profile.getId() != null) {
                profile.setEmailAddress(email);
                patientProfileService.savePatientInfo(profile);
                profile.setDescription(Constants.UPDATE_MESSAGE);
                profile.setSuccessOrFailure(Constants.SUCCESS);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.UPDATE_MESSAGE);
                setPatientChild(profile);
                jsonResponse.setData(profile);
            } else {
                logger.info("Profile data is null.");
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.NO_RECORD_AGAINST_EMAIL);
                setEmptyData(jsonResponse);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception ex) {
            jsonResponse.setData(null);
            logger.error("Exception", ex);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_UPDATE_RECORD);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void setPatientChild(PatientProfile profile) {
        profile.setPaymentInfoList(null);
        profile.setBillingAddress(null);
        profile.setDeliveryPreferenceId(null);
        profile.setContactUsList(null);
        profile.setNotificationMessagesList(null);
        profile.setOrders(null);
        profile.setPatientProfileHealthsList(null);
        profile.setPatientDeliveryAddresses(null);
        profile.setDrugSearchesList(null);
        profile.setPatientGlucoseResultsList(null);
        profile.setPatientProfileNotesList(null);
        profile.setYearEndStatementInfoList(null);
        profile.setOrderBatchs(null);
        profile.setMessageResponseses(null);
        profile.setOrdersPtMessagesList(null);
    }

    private boolean validateEmailField(Integer profileId, JsonResponse jsonResponse, String email) {
        if (CommonUtil.isNullOrEmpty(profileId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.PROFILE_ID_REQ);
            return false;
        }
        if (CommonUtil.isNullOrEmpty(email)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.EMAIL_REQ);
            return false;
        }
        if (!email.trim().isEmpty()) {
            pattern = Pattern.compile(Constants.EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_EMAIL);
                return false;
            }
        }
        return true;
    }

    /**
     * This function is used to verify 18+ age and updated profile if age is
     * validated. It will return success when age updated successfully in
     * profile otherwise failure.
     *
     * @param profileId
     * @param dob
     * @param gender
     * @param allergies
     * @return Its return JSON of profile
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/ageVerificationWs", produces = "application/json")
    public @ResponseBody
    Object ageVerification(
            @RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "allergies", required = false) String allergies) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("Date of Birth is : " + dob + " Profile id is :" + profileId + " Gender type is: " + gender + " Allergies: " + allergies);
            if (!validateAgeVerification(profileId, jsonResponse, dob, gender)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            PatientProfile patientProfile = patientProfileService.getPatientProfileById(profileId);
            if (!isProfileInfoEmpty(patientProfile, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            patientProfile.setGender(gender);
            if (allergies != null && Constants.UNDEFINED.equals(allergies)) {
                allergies = null;
            }
            patientProfile.setAllergies(allergies);
            if (!"".equals(dob)) {
                Date dateOfBirth = DateUtil.stringToDate(dob, "MM/dd/yyyy");
                logger.info("After Format Date Of Birth is: " + dateOfBirth);
                patientProfile.setDob(dateOfBirth);
                if (!validateAge(dateOfBirth, patientProfile, jsonResponse)) {
                    return objectMapper(objectMapper, jsonResponse);
                }
            }
            PatientProfile profile = patientProfile;
            patientProfileService.savePatientInfo(patientProfile);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.UPDATE_MESSAGE);
            setPatientChild(profile);
            jsonResponse.setData(profile);
        } catch (IOException | ParseException ex) {
            logger.error("Exception", ex);
            jsonResponse.setData(null);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_UPDATE_RECORD);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private boolean validateAge(Date dateOfBirth, PatientProfile profile, JsonResponse jsonResponse) throws IOException {
        int years = DateUtil.getDiffYears(dateOfBirth, new Date());
        if (years < 18) {
            profile.setDescription(Constants.INVALID_AGE);
            profile.setSuccessOrFailure(Constants.FAILURE);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_AGE);
            return false;
        }
        return true;
    }

    private boolean isProfileInfoEmpty(PatientProfile profile, JsonResponse jsonResponse) throws IOException {
        if (profile == null || profile.getId() == null) {
            logger.info("Profile info is null.");
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.PROFILE_ID_REQ);
            setEmptyData(jsonResponse);
            return false;
        }
        return true;
    }

    private boolean validateAgeVerification(Integer profileId, JsonResponse jsonResponse, String dob, String gender) {
        boolean valid = true;
        if (CommonUtil.isNullOrEmpty(profileId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.PROFILE_ID_REQ);
            valid = false;
        }
        if (CommonUtil.isNullOrEmpty(dob)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.DOB_REQ);
            valid = false;
        }
        if (CommonUtil.isNullOrEmpty(gender)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.GENDER_TYPE_REQ);
            valid = false;
        }
        return valid;
    }

    /**
     * This function is used to save profile payment information and billing
     * address
     *
     * @param profileId
     * @param securityToken
     * @param cardHolderName
     * @param cardNumber
     * @param cvv
     * @param addressLine
     * @param appt
     * @param city
     * @param stateId
     * @param zip
     * @param expiry
     * @param cardType
     * @param defaultCard
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/paymentInformationWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object paymentInfo(
            @RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardHolderName", required = false) String cardHolderName,
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @RequestParam(value = "cvvNumber", required = false) String cvv,
            @RequestParam(value = "address", required = false) String addressLine,
            @RequestParam(value = "apartment", required = false) String appt,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stateId", required = false) String stateId,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "expiry", required = false) String expiry,
            @RequestParam(value = "cardType", required = false) String cardType,
            @RequestParam(value = "defaultCard", required = false) String defaultCard
    ) throws IOException {
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info("profileId: " + profileId + " Expiry date is: " + expiry + " cardType: " + cardType + " CardHolderName: " + cardHolderName + " CardNumber: " + cardNumber + " Cvv number: " + cvv + " Address: " + addressLine + " appt: " + appt + " DefaultCard: " + defaultCard + " securityToken is: " + securityToken);
            logger.info(" City " + city + " stateId " + stateId + " zip: " + zip);
            System.out.println("profileId: " + profileId + " Expiry date is: " + expiry + " cardType: " + cardType + " CardHolderName: " + cardHolderName + " CardNumber: " + cardNumber + " Cvv number: " + cvv + " Address: " + addressLine + " appt: " + appt + " DefaultCard: " + defaultCard + " securityToken is: " + securityToken);
            System.out.println(" City " + city + " stateId " + stateId + " zip: " + zip);
            if (!validatePaymentInfo(profileId, jsonResponse, cardHolderName, cardNumber, cvv, expiry, cardType, securityToken)) {
                return jsonResponse;
            }
            if (securityToken != null) {
                profile = patientProfileService.getPatientProfileByToken(securityToken);
            } else {
                profile = patientProfileService.getPatientProfileById(profileId);
            }
            if (!isProfileInfoEmpty(profile, jsonResponse)) {
                return jsonResponse;
            }
            if (!validCardTypeLength(cardType, cvv, jsonResponse)) {
                return jsonResponse;
            }
            if (!validateDoDirectPayment(cardType, cardNumber, expiry, cardHolderName, cvv, jsonResponse)) {
                return jsonResponse;
            }
            if (!validateBillingAddressField(addressLine, jsonResponse, appt, city, stateId, zip)) {
                return jsonResponse;
            }
            if (defaultCard.equalsIgnoreCase(Constants.NO)) {
                if (!patientProfileService.isDefaultPaymentInfo(profile.getId())) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Please Make it your default payment info.");
                    return jsonResponse;
                }
            }

            PatientAddress billingAddress = new PatientAddress();
            //save billing address
            if (addressLine != null && !"undefined".equals(addressLine)) {
                logger.info("save address: " + addressLine);
                billingAddress = patientProfileService.saveAddress(addressLine, appt, "", city, stateId, zip, null, null);
                profile.setBillingAddress(billingAddress);
            } else {
                PatientDeliveryAddress patientDeliveryAddress = patientProfileService.getPatientDeliveryDefaultAddress(profileId);
                if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {

                    billingAddress.setAddress(patientDeliveryAddress.getAddress());
                    billingAddress.setApartment(patientDeliveryAddress.getApartment());
                    billingAddress.setAddressDescription(patientDeliveryAddress.getDescription());
                    billingAddress.setCity(patientDeliveryAddress.getCity());
                    if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                        billingAddress.setStateId(patientDeliveryAddress.getState().getId().shortValue());
                    }
                    billingAddress.setZip(patientDeliveryAddress.getZip());
                    billingAddress.setAddressType(patientDeliveryAddress.getAddressType());
                    profile.setBillingAddress(billingAddress);
                }
            }
            //save payment information
            PatientPaymentInfo payment = patientProfileService.savePaymentInfo(profile.getId(), cardHolderName, cardNumber, cvv, expiry, cardType, defaultCard);
            payment.setBillingAddress(billingAddress);
            patientProfileService.update(payment);
            logger.info("Expiry date is: " + payment.getExpiryDate());
            logger.info("save profile address");
            //save profile
            patientProfileService.savePatientInfo(profile);
            jsonResponse.setData(patientProfileService.getPatientPaymentInfo(payment.getId(), profile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            logger.error("Exception", ex);
            profile.setDescription("There is problem with saving payment information.");
            profile.setSuccessOrFailure(Constants.FAILURE);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem with saving payment information. " + ex.getMessage());
        }
        return jsonResponse;
    }

    private boolean validateBillingAddressField(String addressLine, JsonResponse jsonResponse, String appt, String city, String stateId, String zip) {
        if (CommonUtil.isNullOrEmpty(addressLine)) {
            jsonResponse.setErrorMessage("Address is required");
            jsonResponse.setErrorCode(0);
            return false;
        }

//        if (CommonUtil.isNullOrEmpty(city)) {
//            jsonResponse.setErrorMessage("City is required");
//            jsonResponse.setErrorCode(0);
//            return false;
//        }
        if (CommonUtil.isNullOrEmpty(stateId)) {
            jsonResponse.setErrorMessage("State is required");
            jsonResponse.setErrorCode(0);
            return false;
        }
        if (CommonUtil.isNullOrEmpty(zip)) {
            jsonResponse.setErrorMessage("Zip code is required");
            jsonResponse.setErrorCode(0);
            return false;
        }
        return true;
    }

    private boolean validateDoDirectPayment(String cardType, String cardNumber, String expiry, String cardHolderName, String cvv, JsonResponse jsonResponse) throws IOException {
        if (!validCardTypeLength(cardType, cvv, jsonResponse)) {
            return false;
        }
//            //validating...
        DoDirectPayment doDirectPayment = new DoDirectPayment();
        DoDirectPaymentResponseType response = doDirectPayment.authorizationRequest(cardType, cardNumber, expiry, cardHolderName, cvv);
        if (response == null) {
            System.out.println("RESPONSE IS NULL");
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Connection timeout while authorizing payment info");
            return false;
        }
        logger.info("DoDirectPaymentResponseType: " + response);
        System.out.println("DoDirectPaymentResponseType: " + response + " STRING VLAUE " + response.getAck().getValue());
        if (!(response.getAck().getValue().equalsIgnoreCase(Constants.SUCCESS) || response.getAck().getValue().equalsIgnoreCase(Constants.SUCCESS_WITH_WARNING))) {
            if (response.getErrors() != null) {
                List<ErrorType> errorList = response.getErrors();
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(errorList.get(0).getLongMessage());
                logger.info("DoDirectPaymentResponseType error: " + errorList.get(0).getLongMessage());
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Error while authorizing payment info.");
                logger.info("Error while authorizing payment info.");
            }
            return false;
        }
        return true;
    }

    private boolean validCardTypeLength(String cardType, String cvv, JsonResponse jsonResponse) {
        if (cardType != null && cvv != null) {
            logger.info("Cvv number length is: " + cvv.length());
            if ((cardType.equalsIgnoreCase(Constants.VISA) || cardType.equalsIgnoreCase(Constants.MASTERCARD) || cardType.equalsIgnoreCase(Constants.DISCOVER))) {
                if (cvv.length() < 3 || cvv.length() > 3) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.INVALID_CVVNO_VMD);
                    logger.info("Please enter valid 3 digit CVV code against this card " + cardType);
                    return false;
                }
            } else if (cardType.equalsIgnoreCase(Constants.AMEX) && (cvv.length() < 4 || cvv.length() > 4)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_CVVNO_AMEX);
                logger.info("Please enter valid 4 digit CVV code against this card " + cardType);
                return false;
            }
        }
        return true;
    }

    private boolean validatePaymentInfo(Integer profileId, JsonResponse jsonResponse, String cardHolderName, String cardNumber, String cvv, String expiry, String cardType, String token) {
        if (CommonUtil.isNullOrEmpty(cardHolderName)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("CardHolderName is required.");
            return false;
        }
        if (CommonUtil.isNullOrEmpty(cardNumber)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("CardNumber is required.");
            return false;
        }
        if (CommonUtil.isNullOrEmpty(cvv)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("CVV is required.");
            return false;
        }
        if (CommonUtil.isNullOrEmpty(expiry)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Expiry date is required.");
            return false;
        }
        if (CommonUtil.isNullOrEmpty(cardType)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("CardType is required.");
            return false;
        }
        return true;
    }

    /**
     * This function is used to get list of all delivery preferences.
     *
     * @return list of delivery preferences in JSON format
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/getDeliveryPrefrencesWs", produces = "application/json")
    public @ResponseBody
    Object getDeliveryPrefrences() throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        if (patientProfileService.getDeliveryPreferences().size() > 0) {
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            jsonResponse.setData(patientProfileService.getDeliveryPreferences());
        } else {
            jsonResponse.setData(null);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is no record in DeliveryPrefrences table.");
            logger.info("Empty DeliveryPrefrences list::getDeliveryPrefrences()");
        }
        return objectMapper(mapper, jsonResponse);
    }

    /**
     * This function is used to save insuranceCardInfo
     *
     * @param profileId
     * @param cardHolderRelation
     * @param insuranceCardFront
     * @param insuranceCardBack
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/insuranceCardInfoWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object insuranceCardInfo(@RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "cardHolderRelation", required = false) String cardHolderRelation,
            @RequestParam(value = "insuranceFrontCardPath", required = false) String insuranceCardFront,
            @RequestParam(value = "insuranceBackCardPath", required = false) String insuranceCardBack, HttpServletRequest request, HttpServletResponse response) throws IOException {
        PatientProfile patientProfile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info("ProfileId: " + profileId + " CardHolderRelation: " + cardHolderRelation + " insuranceCardFront: " + insuranceCardFront.length() + " insuranceCardBack: " + insuranceCardBack.length());
            if (!validateCreditCardInfo(cardHolderRelation, jsonResponse, insuranceCardFront, insuranceCardBack)) {
                return jsonResponse;
            }
            byte[] decodedFrontCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardFront);
            String imgFcPath = saveInsuranceCard(decodedFrontCard, profileId, "InsuranceFrontCard");
            logger.info("insuranceCardFront Path: " + imgFcPath);
            if (!CommonUtil.urlAuthorization(imgFcPath)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Error in saving card please try again.");
                return objectMapper(mapper, jsonResponse);
            }
            String imgBcPath = "";
            if (CommonUtil.isNotEmpty(insuranceCardBack)) {
                byte[] decodedBackCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardBack);
                imgBcPath = saveInsuranceCard(decodedBackCard, profileId, "InsuranceBackCard");
                logger.info("decodedBackCard: " + imgBcPath);
            }
            patientProfile.setId(profileId);
            patientProfile.setCardHolderRelation(cardHolderRelation);
            //patientProfile.setInsuranceCardFront(decodedFrontCard);
            //patientProfile.setInsuranceCardBack(decodedBackCard);
            String securityToken = RedemptionUtil.getRandomToken();
            logger.info("Token is: " + securityToken);
            patientProfile.setSecurityToken(securityToken);
            patientProfile.setInsuranceFrontCardPath(imgFcPath);
            patientProfile.setInsuranceBackCardPath(imgBcPath);
            if (patientProfileService.savePatientInsuranceCardInfo(patientProfile)) {
                RewardPoints rewardPoints = patientProfileService.getRewardPoints(Constants.Reward_Points_Id);
                if (rewardPoints != null) {
                    //setRewardPoints(rewardPoints, profileId);
                    RewardPoints rp = patientProfileService.getMyRewardsPoints(profileId);
                    logger.info("RewardPoints: " + rewardPoints.getPoint() + " save reward history." + " Points are: " + rewardPoints.getPoint());
                    Integer points = rewardPoints.getPoint().intValueExact();
                    logger.info("Points with out decimal is: " + points);
                    rewardPoints.setPoints(rp.getLifeTimePoints().intValue());
                    rewardPoints.setSecurityToken(securityToken);
                    rewardPoints.setInsuranceFrontCardPath(imgFcPath);
                    rewardPoints.setInsuranceBackCardPath(imgBcPath);
                    jsonResponse.setData(rewardPoints);
                }
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
                return objectMapper(mapper, jsonResponse);
            }
        } catch (Exception e) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
            logger.error("Exception -> insuranceCardInfo", e);
        }
        return jsonResponse;
    }

    private void setRewardPoints(RewardPoints rewardPoints, Integer profileId) {
        patientProfileService.saveRewardHistory(rewardPoints.getType(), rewardPoints.getPoint().intValueExact(), profileId, Constants.PLUS);
        CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs(Constants.WELCOME, Constants.EVENTNAME);
        if (campaignMessages != null) {
            String messageText = TextFlowUtil.setMessagePlaceHolder(campaignMessages.getSmstext(), rewardPoints.getPoint().intValueExact());
            logger.info("Message Text is: " + messageText);
            campaignMessages.setSmstext(messageText);
            if (patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, profileId)) {
                logger.info("Notification messages sent.");
                patientProfileService.sendNextMessages(campaignMessages, profileId);
            } else {
                logger.info("Notification messages cannot sent.");
            }
        } else {
            logger.info("CampaignMessages cannot found.");
        }
    }

    private String saveInsuranceCard(byte[] decodedBackCard, Integer profileId, String cardName) throws IOException {
        String imageFormat = FileUtil.determineImageFormat(decodedBackCard);
        logger.info("imageFormat: " + imageFormat + " Card Name is: " + cardName);
        File file;
        file = setFilePath(profileId, imageFormat, cardName);
        logger.info("Complete Image Path: " + file.getPath());
        patientProfileService.saveImageOrVideo(decodedBackCard, file, imageFormat, Constants.COMMAND);
        String imageUrl = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + cardName + profileId + "." + imageFormat;
        logger.info("Image Url:: " + imageUrl);
        try {
            Date date = new Date();
            String dateStr = DateUtil.dateToString(date, Constants.DATE_FORMATE);
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");
            dateStr = dateStr.replace("_", ":");
            imageUrl = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + cardName + profileId + "-" + dateStr + "." + imageFormat;
            logger.info("-------------- IMAGE URL2 -------------- " + imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: saveInsuranceCard", e);
        }
        return imageUrl;
    }

    private boolean validateCreditCardInfo(String cardHolderRelation, JsonResponse jsonResponse, String insuranceCardFront, String insuranceCardBack) throws IOException {
        if (CommonUtil.isNullOrEmpty(cardHolderRelation)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Cardholder relation required.");
            return false;
        }
        if (CommonUtil.isNullOrEmpty(insuranceCardFront)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("insurance card front side required.");
            return false;
        }
//        if (insuranceCardBack == null || insuranceCardBack.isEmpty() || Constants.UNDEFINED.equals(insuranceCardBack)) {
//            jsonResponse.setErrorCode(0);
//            jsonResponse.setErrorMessage("insurance card back side required.");
//            return false;
//        }
        return true;
    }

    /**
     * This function is used to get list of all states of US.
     *
     * @return list of states in JSON format
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/getStatesWs", produces = "application/json")
    public @ResponseBody
    Object getStates() throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        List<State> list = patientProfileService.getStates();
        ObjectMapper mapper = new ObjectMapper();
        if (list.size() > 0) {
            jsonResponse.setData(list);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Empty state list.");
        }
        return objectMapper(mapper, jsonResponse);
    }

    /**
     * This function is used to get page contents
     *
     * @param type
     * @return page contents
     * @throws IOException
     */
    @RequestMapping(value = "/getContentsWs", produces = "application/json; charset=utf-8")
    public @ResponseBody
    Object getPageContents(@RequestParam(value = "type", required = false) String type) throws IOException {
        CMSPageContent pageContent = new CMSPageContent();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        logger.info("Page Type is: " + type);
        if (type == null || type.trim().isEmpty()) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Page type required.");
            return objectMapper(mapper, jsonResponse);
        }
        if (type.equalsIgnoreCase("TermsOfUse")) {
            pageContent = cMSService.getCMSPageContentById(1);
        }
        if (type.equalsIgnoreCase("PrivacyPolicy")) {
            pageContent = cMSService.getCMSPageContentById(2);
        }
        if (type.equalsIgnoreCase("WhyAllergyDetailsRequired")) {
            pageContent = cMSService.getCMSPageContentById(3);
            // String btnLink = "<div style='text-align: center; padding-top: 10px;'><a href='inapp://capture'><button style='background: rgb(35, 104, 178) none repeat scroll 0px 0px; border: 0px none; height: 33px; width: 100px; color: white;'>Click me</button></a></div>";
            // pageContent.setContent(pageContent.getContent().replace("[URL]", btnLink));
        }
        if (type.equalsIgnoreCase("LearnAboutTransferRx")) {
            pageContent = cMSService.getCMSPageContentById(4);
        }
        if (type.equalsIgnoreCase("HowEarnPoints")) {
            pageContent = cMSService.getCMSPageContentById(5);
        }
        if (type.equalsIgnoreCase("HowSpendPoints")) {
            pageContent = cMSService.getCMSPageContentById(6);
        }
        if (type.equalsIgnoreCase("ContactUs")) {
            pageContent = cMSService.getCMSPageContentById(7);
        }
        jsonResponse.setData(pageContent);
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(Constants.SUCCESS);
        return objectMapper(mapper, jsonResponse);
    }

    /**
     * This function is used to resend verification code to patient
     *
     * @param mobileNumber
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "reSendVerificationCodeWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object resendVerificationCode(@RequestParam(value = "mobileNumber", required = false) String mobileNumber) throws IOException {
        logger.info("Phone number is: " + mobileNumber);
        PatientProfile patientProfile = new PatientProfile();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        try {
            if (mobileNumber == null || mobileNumber.trim().isEmpty() || "undefined".equals(mobileNumber)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Mobile number required.");
                return objectMapper(objectMapper, jsonResponse);
            }
            mobileNumber = CommonUtil.replaceAllStr(mobileNumber, "-", "");
            int verificationCode = RedemptionUtil.getRandomNumber();
            logger.info("VerificationCode is: " + verificationCode);
            patientProfile.setVerificationCode(verificationCode);
            String message = getWelcomeMessage(verificationCode);
            if (patientProfileService.sendVerificationCode(mobileNumber, message)) {
                if (patientProfileService.updateVerificationCode(verificationCode, mobileNumber)) {
                    patientProfile.setDescription(Constants.RESEND_VERIFICATIONCODE_MESSAGE);
                    jsonResponse.setData(patientProfile);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.VERIFICATION_CODE_SENT);
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("This phone# is not registered.");
                    return objectMapper(objectMapper, jsonResponse);
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("This phone# is not registered.");
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);

        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to send verification code.
     *
     * @param mobileNumber
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/verifyMobileNoWs", produces = "application/json")
    public @ResponseBody
    Object sendVerificationCode(@RequestParam(value = "mobileNumber", required = false) String mobileNumber) throws IOException {
        logger.info("mobileNumber is #" + mobileNumber);
        ObjectMapper mapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        PatientProfile patientProfile = new PatientProfile();
        if (mobileNumber == null || mobileNumber.trim().isEmpty() || Constants.UNDEFINED.equals(mobileNumber)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_MOBILE_NO);
            return objectMapper(mapper, jsonResponse);
        }
        mobileNumber = CommonUtil.replaceAllStr(mobileNumber, "-", "");
        if (patientProfileService.isPatientPhoneNumberExist(mobileNumber)) {
            int verificationCode = RedemptionUtil.getRandomNumber();
            logger.info("verificationCode: " + verificationCode);
            String message = getWelcomeMessage(verificationCode);
            try {
                if (patientProfileService.sendVerificationCode(mobileNumber, message)) {
                    if (patientProfileService.updateVerificationCode(verificationCode, mobileNumber)) {
                        patientProfile.setMobileNumber(mobileNumber);
                        patientProfile.setVerificationCode(verificationCode);
                        jsonResponse.setData(patientProfile);
                        jsonResponse.setErrorCode(1);
                        jsonResponse.setErrorMessage(Constants.VERIFICATION_CODE_SENT);
                    } else {
                        jsonResponse.setErrorCode(0);
                        jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
                        return objectMapper(mapper, jsonResponse);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PatientWsControllers.class.getName()).log(Level.SEVERE, null, ex);
                jsonResponse.setErrorCode(2);
                jsonResponse.setErrorMessage("Phone # is not registered.");
            }
        } else {
            jsonResponse.setErrorCode(2);
            jsonResponse.setErrorMessage("Phone # is not registered.");
            return objectMapper(mapper, jsonResponse);
        }
        return objectMapper(mapper, jsonResponse);
    }

    /**
     * This function is used to login to the system from mobile device
     *
     * @param mobileNumber
     * @param verificationCode
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/loginWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object login(@RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "verificationCode", required = false) Integer verificationCode,
            @RequestParam(value = "firebaseToken", required = false) String firebaseToken,
            @RequestParam(value = "osType", required = false) String osType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        PatientProfile patientProfile = new PatientProfile();
        try {
            if (!validateLoginField(mobileNumber, jsonResponse, mapper, verificationCode)) {
                return objectMapper(mapper, jsonResponse);
            }
            String secureToken = RedemptionUtil.getRandomToken();
            logger.info("Secure Token is: " + secureToken);
            System.out.println("Secure Token is: " + secureToken);
            if (patientProfileService.isVerificationCodeExist(mobileNumber, verificationCode, firebaseToken, osType)) {
                PatientProfile dbProfile = patientProfileService.getPatientProfileByMobileNumber(mobileNumber);
                dbProfile.setSecurityToken(secureToken);
                if (patientProfileService.update(dbProfile)) {
//                    patientProfile.setId(dbProfile.getId());
//                    patientProfile.setSecurityToken(secureToken);
//                    patientProfile.setMobileNumber(mobileNumber);
                    jsonResponse.setData(patientProfileService.getProfileListWs(mobileNumber, verificationCode));
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("valid code");
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.ERROR_UPDATE_RECORD);
                }

            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("ERROR: " + e.getMessage());
        }
        return objectMapper(mapper, jsonResponse);
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is used to login to the system from mobile device
     *
     * @param securityToken
     * @param mobileNumber
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/loginTokenBasedWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object loginTokenBased(@RequestHeader(value = "securityToken") String securityToken,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        PatientProfile patientProfile = new PatientProfile();
        if (!validateLoginField(mobileNumber, jsonResponse, mapper, 0)) {
            return objectMapper(mapper, jsonResponse);
        }
//        String secureToken = RedemptionUtil.getRandomToken();
        System.out.println("Parameter Token is: " + securityToken);
//        if (patientProfileService.isVerificationCodeExist(mobileNumber, verificationCode,firebaseToken,osType)) {
        PatientProfile dbProfile = patientProfileService.getPatientProfileByMobileNumber(mobileNumber);
        System.out.println("DB TOKEN " + AppUtil.getSafeStr(dbProfile.getSecurityToken(), ""));
        if (!AppUtil.getSafeStr(dbProfile.getSecurityToken(), "").equals(securityToken)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_CODE);
        } else {
            jsonResponse.setErrorCode(1);
            jsonResponse.setData(patientProfileService.getProfileListWs(mobileNumber));
            jsonResponse.setErrorMessage("valid code");
        }
//            dbProfile.setSecurityToken(securityToken);
//            if (patientProfileService.update(dbProfile)) {
//                patientProfile.setId(dbProfile.getId());
//                patientProfile.setSecurityToken(securityToken);
//                patientProfile.setMobileNumber(mobileNumber);
//                jsonResponse.setData(patientProfileService.getProfileListWs(mobileNumber, verificationCode));
//                jsonResponse.setErrorCode(1);
//                jsonResponse.setErrorMessage("valid code");
//            } else {
//                jsonResponse.setErrorCode(0);
//                jsonResponse.setErrorMessage(Constants.ERROR_UPDATE_RECORD);
//            }

//        } else {
//            jsonResponse.setErrorCode(0);
//            jsonResponse.setErrorMessage(Constants.INVALID_CODE);
//        }
        return objectMapper(mapper, jsonResponse);
    }

    ////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/logoutWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object logout(@RequestHeader(value = "securityToken") String securityToken,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        PatientProfile patientProfile = new PatientProfile();
        try {
            patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                boolean isSaved = this.patientProfileService.clearDeviceTokenFromProfile(patientProfile);
                if (isSaved) {
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Loggedout successfully.");

                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error occurred while logging out.");
                    return objectMapper(objectMapper, jsonResponse);
                }
            } else {
                CommonUtil.inValidSecurityToken(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(null);
            logger.error("Exception", e);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
        }
        return objectMapper(objectMapper, jsonResponse);
    }
    ////////////////////////////////////////////////////////////////////////////////

    private boolean validateLoginField(String mobileNumber, JsonResponse jsonResponse, ObjectMapper mapper, Integer verificationCode) throws IOException {
        if (mobileNumber == null || mobileNumber.trim().isEmpty() || "undefined".equals(mobileNumber)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Mobile number is required.");
            return false;
        }
        if (verificationCode == null) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Verification code is required.");
            return false;
        }
        if (!patientProfileService.isPatientPhoneNumberExist(mobileNumber)) {
            logger.info("Phone # is not exist. " + mobileNumber);
            jsonResponse.setErrorCode(2);
            jsonResponse.setErrorMessage("Phone # does not exist.");
            return false;
        }
        return true;
    }

    /**
     * this function is used to create Profile Members
     *
     * @param securityToken
     * @param firstName
     * @param lastName
     * @param dob
     * @param gender
     * @param allergies
     * @param dermatologist
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping(value = "/createProfileMembersWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object savePatientMembers(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "allergies", required = false) String allergies,
            @RequestParam(value = "dermatologist", required = false) String dermatologist,
            @RequestParam(value = "emailAddr", required = false) String emailAddr) throws IOException, ParseException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("SecurityToken is: " + securityToken + " FirstName is: " + firstName + " lastName is: " + lastName + " DOB is: " + dob + " Gender is: " + gender + " allergies: " + allergies + " dermatologist: " + dermatologist);
            System.out.println("SecurityToken is: " + securityToken + " FirstName is: " + firstName + " lastName is: " + lastName + " DOB is: " + dob + " Gender is: " + gender + " allergies: " + allergies + " dermatologist: " + dermatologist);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                PatientDependantDTO patientProfileMembers = patientProfileService.savePatientProfileMembers(patientProfile.getId(), firstName, lastName, dob, gender, allergies, dermatologist, emailAddr);
                logger.info("After saved Patient members: " + patientProfileMembers);
                System.out.println("After saved Patient members: " + patientProfileMembers);
                if (patientProfileMembers != null && patientProfileMembers.getId() != null) {
                    logger.info("After saved Patient members: " + patientProfileMembers.getId());
                    System.out.println("After saved Patient members: " + patientProfileMembers.getId());
                    jsonResponse.setData(patientProfileMembers);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
                } else {
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
                }
            } else {
                logger.info("Invalid security token. " + securityToken);
                System.out.println("Invalid security token. " + securityToken);
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Invalid security token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            logger.error("Exception -> getPatientProfileByToken", e);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get patient profile
     *
     * @param profileId
     * @return patient profile
     * @throws IOException
     */
    @RequestMapping(value = "/getProfileWs", produces = "application/json")
    public @ResponseBody
    Object getPatientProfile(@RequestParam(value = "profileId", required = false) Integer profileId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        jsonResponse.setData(patientProfileService.getPatientProfileDataById(profileId));
        jsonResponse.setErrorCode(1);
        objectMapper.setVisibilityChecker(objectMapper.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update Allergies (add, update)
     *
     * @param securityToken
     * @param allergies
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateAllergiesWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateAllergies(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "allergies", required = false) String allergies) throws IOException {
        logger.info("SecurityToken is : " + securityToken + " Allergies is: " + allergies);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (allergies != null && Constants.UNDEFINED.equals(allergies)) {
            allergies = "";
        }
        if (patientProfileService.updateAllergies(securityToken, allergies)) {
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Record has been updated successfully.");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update delivery address
     *
     * @param securityToken
     * @param id
     * @param address
     * @param apt
     * @param city
     * @param stateId
     * @param zip
     * @param description
     * @param addressType
     * @param defaultAddress
     * @param dprefId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateDeliveryAddressWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateDeliveryAddressWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "apartment", required = false) String apt,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stateId", required = false) String stateId,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "addressType", required = false) String addressType,
            @RequestParam(value = "defaultAddress", required = false) String defaultAddress,
            @RequestParam(value = "dprefaId", required = false) Integer dprefId,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken is :" + securityToken + " Delivery Address id is: " + id + " Default Address as: " + defaultAddress + " DeliveryPreferenceId is: " + dprefId);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (!isValidZipCode(zip, patientProfile, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (defaultAddress == null || defaultAddress.isEmpty() || defaultAddress.equalsIgnoreCase("No")) {
            boolean isExist = patientProfileService.isDefaultPatientDeliveryAddress(patientProfile.getId());
            PatientDeliveryAddressDTO deliveryAddressDTO = patientProfileService.getPatientDeliveryAddressById(patientProfile.getId(), id);
            if ((deliveryAddressDTO != null && deliveryAddressDTO.getDefaultAddress().equalsIgnoreCase("Yes")) || (!isExist)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("At least one record should set as default.");
                return objectMapper(objectMapper, jsonResponse);
            }
        }
        if (patientProfileService.updateDeliveryAddressWs(patientProfile, id, address, apt, city, stateId, zip, description, addressType, defaultAddress, dprefId)) {
            isPatientMsgResponse(notificationMsgId);
            jsonResponse.setData(patientProfileService.getPatientDeliveryAddressById(patientProfile.getId(), id));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Record has been updated successfully.");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update insurance card.
     *
     * @param securityToken
     * @param cardHolderRelation
     * @param insuranceCardFront
     * @param insuranceCardBack
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateInsuranceCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateInsuranceCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardHolderRelation", required = false) String cardHolderRelation,
            @RequestParam(value = "insuranceFrontCardPath", required = false) String insuranceCardFront,
            @RequestParam(value = "insuranceBackCardPath", required = false) String insuranceCardBack) throws IOException {
        logger.info("SecurityToken: " + securityToken + " CardHolderRelation: " + cardHolderRelation + " insuranceCardFront: " + insuranceCardFront.length() + " insuranceCardBack: " + insuranceCardBack.length());
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile profile = new PatientProfile();
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

        byte[] decodedFrontCard = null, decodedBackCard = null;
        String imgFcPath = null, imgBcPath = null;
        if (!insuranceCardFront.contains(Constants.APP_PATH)) {
            decodedFrontCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardFront.getBytes());
            imgFcPath = saveInsuranceCard(decodedFrontCard, patientProfile.getId(), "InsuranceFrontCard");
            logger.info("decodedFrontCard Path: " + imgFcPath);
        } else {
            logger.info("update exist decodedFrontCard Path: " + imgFcPath);
            imgFcPath = insuranceCardFront;
            //decodedFrontCard = patientProfile.getInsuranceCardFront();
        }
        if (!CommonUtil.urlAuthorization(imgFcPath)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Error in saving card please try again.");
            return jsonResponse;
        }
        if (CommonUtil.isNotEmpty(insuranceCardBack)) {
            if (!insuranceCardBack.contains(Constants.APP_PATH)) {
                decodedBackCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardBack.getBytes());
                imgBcPath = saveInsuranceCard(decodedBackCard, patientProfile.getId(), "InsuranceBackCard");
                logger.info("decodedBackCard path: " + imgBcPath);
            } else {
                logger.info("update exist decodedBackCard path: " + imgBcPath);
                imgBcPath = insuranceCardBack;
                //  decodedBackCard = patientProfile.getInsuranceCardBack();
            }
        }
        if (patientProfileService.updateInsuranceCardWs(securityToken, cardHolderRelation, decodedFrontCard, decodedBackCard, imgFcPath, imgBcPath)) {
            logger.info("Record has been updated successfully.");
            profile.setCardHolderRelation(cardHolderRelation);
            profile.setInsuranceFrontCardPath(imgFcPath);
            profile.setInsuranceBackCardPath(imgBcPath);
            jsonResponse.setData(profile);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Record has been updated successfully.");
        } else {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
        }
        return jsonResponse;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is used to add new Insurance card
     *
     * @param securityToken
     * @param cardHolderRelation
     * @param memberId
     * @param cardId
     * @param insuranceProvider
     * @param groupNumber
     * @param planId
     * @param providerPhone
     * @param providerAddress
     * @param expiryDate
     * @param createdOn
     * @param updatedOn
     * @param isPramiry
     * @param effectiveDate
     * @param insuranceCardFront
     * @param insuranceCardBack
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addInsuranceCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addInsuranceCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardHolderRelation", required = false) String cardHolderRelation,
            @RequestParam(value = "memberId", required = false) String memberId,
            @RequestParam(value = "cardId", required = false) Long cardId,
            @RequestParam(value = "insuranceProvider", required = false) String insuranceProvider,
            @RequestParam(value = "groupNumber", required = false) String groupNumber,
            @RequestParam(value = "planId", required = false) String planId,
            @RequestParam(value = "providerPhone", required = false) String providerPhone,
            @RequestParam(value = "providerAddress", required = false) String providerAddress,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "createdOn", required = false) String createdOn,
            @RequestParam(value = "updatedOn", required = false) String updatedOn,
            @RequestParam(value = "isPramiry", required = false) Integer isPramiry,
            @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
            @RequestParam(value = "insuranceFrontCardPath", required = false) String insuranceCardFront,
            @RequestParam(value = "insuranceBackCardPath", required = false) String insuranceCardBack) throws IOException {
        logger.info("SecurityToken: " + securityToken + " CardHolderRelation: " + cardHolderRelation + " insuranceCardFront: " + insuranceCardFront.length() + " insuranceCardBack: " + insuranceCardBack.length());
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientInsuranceDetails insuranceDetails = new PatientInsuranceDetails();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            String[] eDate = effectiveDate.split("/");
            effectiveDate = eDate[1] + "-" + eDate[0] + "-01";
            //Long longCardId = Long.parseLong(cardId);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            byte[] decodedFrontCard = null, decodedBackCard = null;
            String imgFcPath = null, imgBcPath = null;
            if (!insuranceCardFront.contains(PropertiesUtil.getProperty("INSURANCE_CARD_URL"))) {
                decodedFrontCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardFront.getBytes());
                imgFcPath = saveInsuranceCard(decodedFrontCard, patientProfile.getId(), "InsuranceFrontCard");
                logger.info("decodedFrontCard Path: " + imgFcPath);
            } else {
                logger.info("update exist decodedFrontCard Path: " + imgFcPath);
                imgFcPath = insuranceCardFront;
            }
            if (!CommonUtil.urlAuthorization(imgFcPath)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Error in saving card please try again.");
                return jsonResponse;
            }
            if (CommonUtil.isNotEmpty(insuranceCardBack)) {
                if (!insuranceCardBack.contains(PropertiesUtil.getProperty("INSURANCE_CARD_URL"))) {
                    decodedBackCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardBack.getBytes());
                    imgBcPath = saveInsuranceCard(decodedBackCard, patientProfile.getId(), "InsuranceBackCard");
                    logger.info("decodedBackCard path: " + imgBcPath);
                } else {
                    logger.info("update exist decodedBackCard path: " + imgBcPath);
                    imgBcPath = insuranceCardBack;
                }
            }
            if (patientProfileService.addInsuranceCard(patientProfile, cardHolderRelation,
                    decodedFrontCard, decodedBackCard, imgFcPath, imgBcPath, memberId, cardId, planId,
                    effectiveDate, expiryDate, createdOn, updatedOn, insuranceProvider,
                    groupNumber, providerPhone, providerAddress, isPramiry, 0)) {
                logger.info("Record has been added successfully.");
                insuranceDetails.setCardHolderRelationship(cardHolderRelation);
                insuranceDetails.setInsuranceFrontCardPath(imgFcPath);
                insuranceDetails.setInsuranceBackCardPath(imgBcPath);
                jsonResponse.setData(insuranceDetails);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
        }

        return jsonResponse;
    }

    /**
     * This function is used to delete insurance card
     *
     * @param securityToken
     * @param cardId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/archiveInsuranceCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object archiveInsuranceCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardId", required = false) Long cardId) throws IOException {
        logger.info("SecurityToken: " + securityToken);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile profile = new PatientProfile();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            if (patientProfileService.archiveInsuranceCard(patientProfile, cardId)) {
                logger.info("Record has been deleted successfully.");
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been deleted successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to delete record.");
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
        }

        return jsonResponse;
    }

    /**
     * This function is use to update Primary insurance Card
     *
     * @param securityToken
     * @param cardId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updatPrimaryInsuranceCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatPrimaryInsuranceCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardId", required = false) Integer cardId) throws IOException {
        logger.info("SecurityToken: " + securityToken);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile profile = new PatientProfile();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            if (patientProfileService.updatePrimaryInsuranceCard(patientProfile, cardId)) {
                logger.info("Record has been updated successfully.");
                jsonResponse.setData(profile);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
        }

        return jsonResponse;
    }

    /**
     * This function is used to get all the insurance card
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getInsuranceCardsWs", produces = "application/json")
    public @ResponseBody
    Object getInsuranceCardsWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "dependentId", required = false) Integer dependentId) throws IOException {
        logger.info("SecurityToken: " + securityToken);

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            if (patientProfile != null && patientProfile.getId() != null) {
                List<PatientInsuranceDetailsDTO> patientInsuranceDetailsDTOList = patientProfileService.getInsuranceCard(patientProfile, dependentId);
                if (!CommonUtil.isNullOrEmpty(patientInsuranceDetailsDTOList)) {
                    jsonResponse.setData(patientInsuranceDetailsDTOList);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.SUCCESS);
                    logger.info("Success");
                    return objectMapper(objectMapper, jsonResponse);
                } else {
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("No Insurance Card Avalible.Please add new.");
                    logger.info("Empty list");
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
                logger.info(Constants.INVALID_SECURITY_TOKEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(e.getMessage());
            logger.error("Exception -> getPatientProfileByToken", e);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //  Co-Pay Card Start                                                                                        //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /**
     * This function is used to add Co-Pay card one at a time.
     *
     * @param securityToken
     * @param manufacturerName
     * @param drugId
     * @param expiryDate
     * @param effectiveDate
     * @param copayFrontCardPath
     * @param copayBackCardPath
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addCoPayCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addCoPayCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "manufacturerName", required = false) String manufacturerName,
            @RequestParam(value = "drugId", required = false) Integer drugId,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
            @RequestParam(value = "copayFrontCardPath", required = false) String copayFrontCardPath,
            @RequestParam(value = "copayBackCardPath", required = false) String copayBackCardPath) throws IOException {
        logger.info("SecurityToken: " + securityToken + " copayFrontCardPath: " + copayFrontCardPath.length() + " copayBackCardPath: " + copayBackCardPath.length());
        JsonResponse jsonResponse = new JsonResponse();
        CoPayCardDetails coPayCardDetails = new CoPayCardDetails();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            byte[] decodedFrontCard = null, decodedBackCard = null;
            String imgFcPath = null, imgBcPath = null;
            if (!copayFrontCardPath.contains(PropertiesUtil.getProperty("INSURANCE_CARD_URL"))) {
                decodedFrontCard = org.apache.commons.codec.binary.Base64.decodeBase64(copayFrontCardPath.getBytes());
                imgFcPath = saveInsuranceCard(decodedFrontCard, patientProfile.getId(), "copayFrontCardPath");
                logger.info("decodedFrontCard Path: " + imgFcPath);
            } else {
                logger.info("update exist decodedFrontCard Path: " + imgFcPath);
                imgFcPath = copayFrontCardPath;
                //decodedFrontCard = patientProfile.getInsuranceCardFront();
            }
            if (!CommonUtil.urlAuthorization(imgFcPath)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Error in saving card please try again.");
                return jsonResponse;
            }
            if (CommonUtil.isNotEmpty(copayBackCardPath)) {
                if (!copayBackCardPath.contains(PropertiesUtil.getProperty("INSURANCE_CARD_URL"))) {
                    decodedBackCard = org.apache.commons.codec.binary.Base64.decodeBase64(copayBackCardPath.getBytes());
                    imgBcPath = saveInsuranceCard(decodedBackCard, patientProfile.getId(), "copayBackCardPath");
                    logger.info("decodedBackCard path: " + imgBcPath);
                } else {
                    logger.info("update exist decodedBackCard path: " + imgBcPath);
                    imgBcPath = copayBackCardPath;
                    //  decodedBackCard = patientProfile.getInsuranceCardBack();
                }
            }
            if (patientProfileService.addCopayCard(patientProfile, decodedFrontCard, decodedBackCard,
                    imgFcPath, imgBcPath, effectiveDate, expiryDate, manufacturerName, drugId)) {
                logger.info("Record has been added successfully.");
                coPayCardDetails.setCopayFrontCardPath(imgFcPath);
                coPayCardDetails.setCopayBackCardPath(imgBcPath);
                jsonResponse.setData(coPayCardDetails);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
            logger.error("Exception -> getPatientProfileByToken", e);
        }

        return jsonResponse;
    }

    /**
     * This function is used to get the list of Co-Pay Cards.
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getCoPayCardsWs", produces = "application/json")
    public @ResponseBody
    Object getCoPayCardsWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        logger.info("SecurityToken: " + securityToken);

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            if (patientProfile != null && patientProfile.getId() != null) {
                List<CoPayCardDetailsDTO> coPayCardDetailsDTOList = patientProfileService.getCoPayCards(patientProfile);
                if (!CommonUtil.isNullOrEmpty(coPayCardDetailsDTOList)) {
                    jsonResponse.setData(coPayCardDetailsDTOList);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(PropertiesUtil.getProperty("SUCCESS"));
                    logger.info("Success");
                } else {
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(PropertiesUtil.getProperty("EMPTY_MESSAGE"));
                    logger.info("Empty list");
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(PropertiesUtil.getProperty("INVALID_SECURITY_TOKEN"));
                logger.info(Constants.INVALID_SECURITY_TOKEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            logger.error("Exception -> getPatientProfileByToken", e);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to delete Co-Pay card
     *
     * @param securityToken
     * @param cardId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/archiveCoPayCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object archiveCoPayCardWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "cardId", required = false) Long cardId) throws IOException {
        logger.info("SecurityToken: " + securityToken + " cardId: " + cardId);
        JsonResponse jsonResponse = new JsonResponse();
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            if (patientProfileService.deleteCopayCard(patientProfile, cardId)) {
                logger.info("Record has been deleted successfully.");
//                jsonResponse.setData(patientProfileService.getCoPayCardsListByOrderId(null, patientProfile.getId()));
                jsonResponse.setData(new ArrayList<CoPayCardDetailsDTO>());
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been deleted successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            logger.error("Exception -> getPatientProfileByToken", e);
        }

        return jsonResponse;
    }

    /**
     * This function is used to get the brand information of a drug
     *
     * @param drugBasicId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getDrugInfoWs", produces = "application/json")
    public @ResponseBody
    Object getDrugLookUpList(@RequestParam(value = "drugBasicId", required = false) Integer drugBasicId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Drug id is: " + drugBasicId);
        if (drugBasicId == 0) {
            logger.info("Drug Name is empty: " + drugBasicId);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Drug Name is empty.");
            return jsonResponse;
        }
        DrugDTO drugDTO = (DrugDTO) patientProfileService.getDrugInfo(drugBasicId);
        if (drugDTO != null) {
            jsonResponse.setData(drugDTO);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info("Success");
        } else {
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.EMPTY_MESSAGE);
            logger.info("Empty list");
        }
        logger.info("return json");
        return jsonResponse;
    }

    /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //  Co-Pay Card End                                                                                          //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

 /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // Refill Reminder Start                                                                                     //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /**
     * This function is used to stop refill reminders.
     *
     * @param securityToken
     * @param orderChainId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/optOutRefillReminderWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePaymentWsDetails(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "orderChainId", required = false) Integer orderChainId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileBySecurityToken(securityToken);
            //patientProfileService.optOutRefillReminder(patientProfile, orderChainId);

            if (patientProfileService.optOutRefillReminder(patientProfile, orderChainId)) {
                logger.info("Record has been updated successfully.");
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // Refill Reminder End                                                                                       //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
 /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // Year End Statment Start                                                                                   //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /**
     * This function is used to get the detail of orders placed in a period of
     * time.
     *
     * @param securityToken
     * @param startDate
     * @param endDate
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/yearEndStatmentWs", produces = "application/json")
    public @ResponseBody
    Object yearEndStatmentWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate, HttpServletRequest request) throws IOException {
        logger.info("SecurityToken: " + securityToken + " sDate: " + startDate + " eDate: " + endDate);
        JsonResponse jsonResponse = new JsonResponse();
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);

            List<OrderDetailDTO> orderDetailDTOList = patientProfileService.getYearEndStatment(patientProfile, startDate, endDate);
            if (!CommonUtil.isNullOrEmpty(orderDetailDTOList)) {
                logger.info("Record for the Patient Id " + patientProfile.getId() + "Orders count is " + orderDetailDTOList.size());
                jsonResponse.setData(orderDetailDTOList);
                //ExportToPdf file for year end statement
                ExportToPdf exportToPdf = new ExportToPdf();
//                YearEndStatementInfo yearEndStatementInfo = patientProfileService.getYearEndStatementInfo(patientProfile.getId());
//                if (yearEndStatementInfo != null && yearEndStatementInfo.getId() != null) {
//                    jsonResponse.setYearEndStatementPdfFile(yearEndStatementInfo.getFileName());
//                    jsonResponse.setYearEndStatementLink(Constants.INSURANCE_CARD_URL + yearEndStatementInfo.getFileName());
//                } else {
                String yearEndStatementPdfFile = exportToPdf.downlaodYearEndStatement(orderDetailDTOList, patientProfile.getId(), request, logger);
                patientProfileService.saveYearEndStatementInfo(patientProfile.getId(), yearEndStatementPdfFile);
                jsonResponse.setYearEndStatementPdfFile(yearEndStatementPdfFile);
                jsonResponse.setYearEndStatementLink(PropertiesUtil.getProperty("INSURANCE_CARD_URL") + yearEndStatementPdfFile);
//                }
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setErrorCode(1);
                jsonResponse.setData(orderDetailDTOList);
                jsonResponse.setErrorMessage("There is no record found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: yearEndStatmentWs", e);
        }

        return jsonResponse;
    }

    /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // Year End Statment end                                                                                     //
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /**
     * This function is used to update billing address
     *
     * @param securityToken
     * @param address
     * @param apt
     * @param city
     * @param stateId
     * @param zip
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateBillingAddressWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateBillingAddressWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "apartment", required = false) String apt,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stateId", required = false) String stateId,
            @RequestParam(value = "zip", required = false) String zip) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken + " Address is: " + address + " Apt is: " + apt + " cityis: " + city + " StateId is: " + stateId + " zip is: " + zip);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null) {
            if (patientProfileService.updateBillingAddress(securityToken, address, apt, city, stateId, zip)) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } else {
            logger.info("Invalid token. " + securityToken);
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Invalid token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update payment details
     *
     * @param securityToken
     * @param id
     * @param cardHolderName
     * @param cardNumber
     * @param cvv
     * @param expiry
     * @param cardType
     * @param defaultCard
     * @param addressLine
     * @param appt
     * @param city
     * @param stateId
     * @param zip
     * @param billingAddressId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updatePaymentDetailsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePaymentWsDetails(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "cardHolderName", required = false) String cardHolderName,
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @RequestParam(value = "cvvNumber", required = false) String cvv,
            @RequestParam(value = "expiry", required = false) String expiry,
            @RequestParam(value = "cardType", required = false) String cardType,
            @RequestParam(value = "defaultCard", required = false) String defaultCard,
            @RequestParam(value = "address", required = false) String addressLine,
            @RequestParam(value = "apartment", required = false) String appt,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stateId", required = false) String stateId,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "billingAddressId", required = false) Integer billingAddressId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (!validCardTypeLength(cardType, cvv, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (!validateDoDirectPayment(cardType, cardNumber, expiry, cardHolderName, cvv, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (!validateBillingAddressField(addressLine, jsonResponse, appt, city, stateId, zip)) {
                return jsonResponse;
            }
            logger.info("billingAddressId# " + billingAddressId);
            PatientProfile patientProfile = patientProfileService.getPatientProfileBySecurityToken(securityToken);
            PatientAddress address = patientProfileService.saveAddress(addressLine, appt, "", city, stateId, zip, null, new PatientAddress(billingAddressId));
            patientProfile.setBillingAddress(address);
            patientProfileService.savePatientInfo(patientProfile);

            PatientPaymentInfo patientPaymentInfo = patientProfileService.updatePaymentWsDetails(securityToken, id, cardHolderName, cardNumber, cardType, expiry, cvv, defaultCard, address);
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                logger.info("Record has been updated successfully.");
                jsonResponse.setData(patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private boolean validateToken(String securityToken, JsonResponse jsonResponse) throws IOException {
        if (CommonUtil.isNullOrEmpty(securityToken)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("User token is null.");
            return false;
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile == null || CommonUtil.isNullOrEmpty(patientProfile.getId())) {
            logger.info("Invalid token: " + securityToken);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            return false;
        }
        jsonResponse.setPatientId(patientProfile.getId());
        return true;
    }

    /**
     * this function is used to update Family Members
     *
     * @param securityToken
     * @param id
     * @param firstName
     * @param lastName
     * @param dob
     * @param gender
     * @param allergies
     * @param dermatologist
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateFamilyMembersWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateFamilyMembers(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "allergies", required = false) String allergies,
            @RequestParam(value = "dermatologist", required = false) String dermatologist,
            @RequestParam(value = "emailAddr", required = false) String emailAddr) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("Memeber id is: " + id + "securityToken: " + securityToken + " FirstName: " + firstName + " lastName: " + lastName + " dob: " + dob + " gender: " + gender + " allergies: " + allergies + " dermatologist: " + dermatologist);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            PatientProfileMembers patientProfileMembers = patientProfileService.updateFamilyMembers(id, firstName, lastName, gender, dob, allergies, dermatologist, emailAddr);
            if (patientProfileMembers != null && patientProfileMembers.getId() != null) {
                jsonResponse.setData(dob);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            logger.error("Exception:: updateCareTakerImageWs", e);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * this function is used to get Profile details
     *
     * @param securityToken
     * @param type
     * @param dependentId
     * @param notificationMsgId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getProfileDetailsWs", produces = "application/json")
    public @ResponseBody
    Object getProfileDetails(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "dependentId", required = false) Integer dependentId,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException {
        PatientProfile patientProfile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        getProfileDetails(securityToken, type, patientProfile, dependentId, jsonResponse);
        if (!CommonUtil.isNullOrEmpty(jsonResponse.getErrorCode()) && jsonResponse.getErrorCode() == 1) {
            isPatientMsgResponse(notificationMsgId);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void getProfileDetails(String securityToken, String type, PatientProfile patientProfile,
            Integer dependentId, JsonResponse jsonResponse) throws IOException {
        PatientAddress patientAddress;
        //PatientProfile profile = patientProfileService.getPatientProfileByToken(securityToken, dependentId);
        LoginDTO profile = patientProfileService.getPatientProfileDetailByToken(securityToken, dependentId);
        if (profile != null && profile.getId() != null) {
            if (type.equalsIgnoreCase("PersonalDetails")) {
//                patientProfile.setId(profile.getId());
//                patientProfile.setFirstName(profile.getFirstName() + " " + profile.getLastName());
//                patientProfile.setMobileNumber(profile.getMobileNumber());
//                patientProfile.setEmailAddress(profile.getEmailAddress());
//                patientProfile.setDob(profile.getDob());
//                patientProfile.setAlternatePhoneNumber(profile.getAlternatePhoneNumber());
//                if (profile.getDeliveryPreferenceId() != null && profile.getDeliveryPreferenceId().getId() != null) {
//                    patientProfile.setDprefaId(profile.getDeliveryPreferenceId().getId());
//                    patientProfile.setMiles(profile.getMiles());
//                    patientProfile.setDeliveryFee(profile.getDeliveryFee());
//                }
                setJsonResponse(jsonResponse, type, profile);
            } else if (type.equalsIgnoreCase("Allergies")) {
                patientProfile.setAllergies(profile.getAllergies());
                setJsonResponse(jsonResponse, type, patientProfile);
            } else if (type.equalsIgnoreCase("DeliveryAddress")) {
                List<PatientDeliveryAddressDTO> list = patientProfileService.getPatientDeliveryAddressesByProfileId(profile.getId());
                if (list != null && list.size() > 0) {
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(type);
                    jsonResponse.setData(list);
                } else {
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("DeliveryAddress is null.");
                }
            } else if (type.equalsIgnoreCase("BillingAddress")) {
                patientAddress = profile.getBillingAddress();
                if (patientAddress != null) {
                    setPatientAddress(patientAddress);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(type);
                    jsonResponse.setData(patientAddress);
                } else {
                    logger.info("BillingAddress is null.");
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("BillingAddress is null.");
                }

            } else if (type.equalsIgnoreCase("InsuranceCard")) {
                patientProfile.setInsuranceFrontCardPath(profile.getInsuranceFrontCardPath());
                patientProfile.setInsuranceBackCardPath(profile.getInsuranceBackCardPath());
//                if (profile.getInsuranceCardFront() != null) {
//                    //String insuranceFrontCard = saveInsuranceCard(profile.getInsuranceCardFront(), profile.getId(), "InsuranceFrontCard");
//                    logger.info("Insurance Front card path: " + profile.getInsuranceFrontCardPath());
//                    
//                } else {
//                    patientProfile.setInsuranceFrontCardPath(profile.getInsuranceFrontCardPath());
//                }
//                if (profile.getInsuranceCardBack() != null) {
//                    //String insuranceBackCardPath = saveInsuranceCard(profile.getInsuranceCardBack(), profile.getId(), "InsuranceBackCard");
//                    logger.info("Insurance Back card path: " + profile.getInsuranceBackCardPath());
//                    patientProfile.setInsuranceBackCardPath(profile.getInsuranceBackCardPath());
//                } else {
//                    
//                }
                patientProfile.setCardHolderRelation(profile.getCardHolderRelation());
                setJsonResponse(jsonResponse, type, patientProfile);
            } else if (type.equalsIgnoreCase("PaymentDetails")) {
                List<PatientPaymentInfo> list = patientProfileService.getPatientPaymentInfoListByProfileId(profile.getId());
                if (list != null && list.size() > 0) {
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(type);
                    jsonResponse.setData(list);
                } else {
                    logger.info("Payment info is null.");
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Payment info is null.");
                }
            } else if (type.equalsIgnoreCase("FamilyMembers")) {
                List<PatientDependantDTO> memberses = patientProfileService.getPatientDependantList(profile.getId());
                if (memberses != null && memberses.size() > 0) {
                    logger.info("PatientProfileMembers list size is: " + memberses.size());
                    jsonResponse.setData(memberses);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(type);
                } else {
                    logger.info("Family Members doect not exist.");
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Family Members doect not exist.");
                }
            } else if (type.equalsIgnoreCase("ContactUs")) {
                ContactUs contactUs = patientProfileService.getContactUs(profile.getId());
                if (contactUs != null) {
                    contactUs.setPatientProfile(null);
                    contactUs.setCreatedOn(null);
                    contactUs.setUpdatedOn(null);
                    jsonResponse.setData(contactUs);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(type);
                } else {
                    logger.info("Contact us info is null.");
                    setEmptyData(jsonResponse);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Contact us info is null.");
                }
            } else if (type.equalsIgnoreCase(Constants.DRUG_SEARCHES)) {
                jsonResponse.setData(patientProfileService.getSearchesRecordList(profile.getId()));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(type);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is no record found against completed status.");
        }
    }

    private void setPatientAddress(PatientAddress patientAddress) {
        patientAddress.setUpdatedOn(null);
        patientAddress.setCreatedOn(null);
        patientAddress.setPatientProfileInfoList1(null);
        patientAddress.setState(null);
    }

    private void setEmptyData(JsonResponse jsonResponse) {
        jsonResponse.setData(str);
    }

    /**
     * This function is used to update Personal Details
     *
     * @param securityToken
     * @param alternatePhoneNumber
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updatePersonalDetailsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePersonalDetails(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "alternatePhoneNumber", required = false) String alternatePhoneNumber) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " Alternate PhoneNumber is: " + alternatePhoneNumber);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile profile = patientProfileService.getPatientProfileByToken(securityToken);
        if (profile != null) {
            profile.setAlternatePhoneNumber(alternatePhoneNumber);
            profile.setUpdatedOn(new Date());
            if (patientProfileService.update(profile)) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is no record found against this security token.");
            logger.info("There is no record found against this security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to save complaints or feedback shared by patient
     *
     * @param securityToken
     * @param complainType
     * @param message
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/saveContactUsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object saveContactUs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "complainType", required = false) String complainType,
            @RequestParam(value = "message", required = false) String message) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken is: " + securityToken + " Complain Type is: " + complainType + " message is: " + message);
        if (!validateContactUsPortalField(securityToken, jsonResponse, complainType, message)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null) {
            if (patientProfileService.saveContactUs(patientProfile, complainType, message)) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Invalid security token.");
            logger.info("Invalid security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private boolean validateContactUsPortalField(String securityToken, JsonResponse jsonResponse, String complainType, String message) throws IOException {
        if (securityToken == null) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Security Token is required.");
            return false;
        }
        if (complainType == null) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("ComplainType is required.");
            return false;
        }
        if (message == null) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Message is required.");
            return false;
        }
        return true;
    }

    /**
     * This function is used to get list of drugs
     *
     * @param drugName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getDrugLookUpWs", produces = "application/json")
    public @ResponseBody
    Object getDrugLookUpList(@RequestParam(value = "drugBrandName", required = false) String drugName) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Drug Name is: " + drugName);
        if (drugName == null || drugName.isEmpty()) {
            logger.info("Drug Name is empty: " + drugName);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Drug Name is empty.");
            return jsonResponse;
        }
        Set<DrugBrandDTO> list = patientProfileService.getDrugBrandsList(drugName);
        if (list.size() > 0) {
            jsonResponse.setData(list);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info("Success");
        } else {
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.EMPTY_MESSAGE);
            logger.info("Empty list");
        }
        logger.info("return json");
        return jsonResponse;
    }

    /**
     * This function is used to get specific drugs of same brand
     *
     * @param securityToken
     * @param drugBrandId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getDrugRecordWs", produces = "application/json")
    public Object getDrugRecord(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "drugBrandId", required = false) Integer drugBrandId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is; " + securityToken + " DrugBrandId: " + drugBrandId);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            Drug drug = patientProfileService.getDrugList(drugBrandId);
            if (drug != null) {//&& drug.getDrugNdc() != null) {
                jsonResponse.setData(drug);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
                logger.info("Success");
            } else {
                setEmptyData(jsonResponse);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.EMPTY_MESSAGE);
                logger.info("Empty list");
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get all the details of a drug
     *
     * @param securityToken
     * @param drugId
     * @param drugType
     * @param strength
     * @param qty
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getDrugRecordDetailWs", produces = "application/json")
    public Object getDrugRecordDetail(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "drugId", required = false) Long drugId,
            @RequestParam(value = "dType", required = false) String drugType,
            @RequestParam(value = "strength", required = false) String strength,
            @RequestParam(value = "qty", required = false) String qty,
            @RequestParam(value = "orderId", required = false) String orderId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken is: " + securityToken + " Drug Id is: " + drugId + " drug Type: " + drugType + " Strength is: " + strength + " Qty is: " + qty);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {

            DrugDetail drug = patientProfileService.getGenericBasedDrugDetailInfoHandler(drugId, AppUtil.getSafeInt(qty, 0), patientProfile, orderId);//.getDrugDetailInfo(drugId, AppUtil.getSafeInt(qty,0), patientProfile);
            //drug.setStrength(strength);
            drug.setFormDesc(drugType);
            //drug.setType(drugType);
            drug.setDrugQty(AppUtil.getSafeInt(qty, 0));
            drug.setStrength(strength);
            jsonResponse.setData(drug);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorCodeType(0);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info("Sucess");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void inValidZipCodeMessage(JsonResponse jsonResponse) {
        jsonResponse.setErrorCode(0);
        jsonResponse.setErrorMessage("InValid zip code");
    }

    /**
     * This function is used to get all the notification messages
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getNotificationMessagesWs", produces = "application/json")
    public Object getAllNotificationMessages(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            logger.info("Profile Id is: " + patientProfile.getId());
            jsonResponse.setData(patientProfileService.getNotificationMessagesByProfileId(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info("Sucess");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to save new transfer Rx
     *
     * @param securityToken
     * @param patientName
     * @param rxNumber
     * @param firstName
     * @param lastName
     * @param pharmacyName
     * @param pharmacyPhone
     * @param drug
     * @param quantity
     * @param transferId
     * @param drugImg
     * @param drugImgList
     * @param video
     * @param prescriberName
     * @param prescriberPhone
     * @param orderType
     * @param req
     * @param dependentId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/saveRxTransferVideoWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object saveRxTransferVideoWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "rxNumber", required = false) String rxNumber,
            @RequestParam(value = "drug", required = false) String drug,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "transferId", required = false) String transferId,
            @RequestParam(value = "drugImg", required = false) MultipartFile drugImg,
            @RequestParam(value = "drugImgs", required = false) List<MultipartFile> drugImgList,
            @RequestParam(value = "pharmacyName", required = false) String pharmacyName,
            @RequestParam(value = "pharmacyPhone", required = false) String pharmacyPhone,
            @RequestParam(value = "prescriberName", required = false) String prescriberName,
            @RequestParam(value = "prescriberPhone", required = false) String prescriberPhone,
            @RequestParam(value = "dependentId", required = false) String dependentId,
            @RequestParam(value = "strength", required = false) String strength,
            HttpServletRequest req) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " PatientName is: " + patientName + " pharmacyName: " + pharmacyName + " pharmacyPhone: " + pharmacyPhone + " Drug is: " + drug + " Quantity is: " + quantity + " video: " + video + " TransferId:: " + transferId);
        logger.info("DependentId:: " + dependentId + " drugImgList Size is:: " + drugImgList.size());
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        String dateStr = "";
        String completeName = "";
        try {
            Date date = new Date();
            dateStr = DateUtil.dateToString(date, "yy-MM-dd hh:mm:ss");
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");
        } catch (Exception e) {

        }
        if (patientProfile != null && patientProfile.getId() != null) {
            String videoPath = null, drugImagPath = null;
//            String sRootPath = new File("").getAbsolutePath();
            String contextPath = req.getServerName();
            int port = req.getServerPort();
            String webappRoot = servletContext.getRealPath("/");
            String relativeFolder = File.separator + "resources" + File.separator
                    + "noinsurancecard" + File.separator;
            List<OrderTransferImages> orderTransferImagesList = new ArrayList<>();
            if (video != null && !video.isEmpty()) {
                if (video.getBytes() != null) {
                    logger.info("video Format: " + video.getContentType());
//                    String webappRoot = servletContext.getRealPath("/");
//                    String relativeFolder = File.separator + "resources" + File.separator
//                            + "noinsurancecard" + File.separator;
                    String contentType = video.getContentType();
                    String[] contentArr = contentType.split("/");
                    String ext = "mp4"; //FileUtil.determineImageFormat(video.getBytes());
                    if (contentArr != null && contentArr.length > 1) {
                        ext = contentArr[1];
                    }
                    completeName = "Vid_" + patientProfile.getId() + "_" + dateStr + "." + ext;
                    File file = new File(webappRoot + relativeFolder + completeName);
                    videoPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
//                    videoPath = Constants.INSURANCE_CARD_PATH + video.getOriginalFilename() + patientProfile.getId() + "." + video.getContentType();
                    logger.info("Complete video Path: " + videoPath);
//                    FileCopyUtils.copy(video.getBytes(), new File(Constants.INSURANCE_CARD_PATH + video.getOriginalFilename()));
                    FileCopyUtils.copy(video.getBytes(),
                            new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
                    CommonUtil.executeCommand(Constants.COMMAND);
                    completeName = videoPath;
                }
            } else if (!CommonUtil.isNullOrEmpty(drugImgList)) {
                completeName = uploadOrderTransferImages(drugImgList, completeName, patientProfile, dateStr, orderTransferImagesList);
            } else if (drugImg != null && drugImg.getBytes() != null) {
                logger.info("Drug Image Format: " + drugImg.getContentType());

                String ext = FileUtil.determineImageFormat(drugImg.getBytes());
                completeName = "Img_" + patientProfile.getId() + "_" + dateStr + "." + ext;
                drugImagPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
                logger.info("Complete Drug Image Path: " + drugImagPath);
                //FileCopyUtils.copy(drugImg.getBytes(), new File(Constants.INSURANCE_CARD_PATH + drugImg.getOriginalFilename()));
//                    FileCopyUtils.copy(drugImg.getBytes(),
//                            new File(Constants.INSURANCE_CARD_PATH + drugImg.getOriginalFilename()));
                FileCopyUtils.copy(drugImg.getBytes(),
                        new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
                CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND"));
                completeName = drugImagPath;
            }
//            saveRxTransfer(patientProfile, patientName, pharmacyName, pharmacyPhone, drug, quantity, videoPath, jsonResponse,
//                    rxNumber, transferId, drugImagPath, AppUtil.getSafeStr(prescriberName, ""),
//                    AppUtil.getSafeStr(prescriberPhone, ""), AppUtil.getSafeStr(orderType, ""));
//            drug=drug+" "+AppUtil.getSafeStr(strength, "");
            logger.info("DRUG INFO FOR TRANSFER-----------> DRUG:: " + drug + " Strength:: " + strength);
            if (video != null) {
                saveRxTransfer(patientProfile, patientName, firstName, lastName, pharmacyName, pharmacyPhone, drug, strength, quantity,
                        completeName, jsonResponse,
                        rxNumber, transferId,
                        "", AppUtil.getSafeStr(prescriberName, ""),
                        AppUtil.getSafeStr(prescriberPhone, ""), AppUtil.getSafeStr(orderType, ""), dependentId, orderTransferImagesList);
            } else {
                saveRxTransfer(patientProfile, patientName, firstName, lastName, pharmacyName, pharmacyPhone, drug, strength, quantity,
                        "", jsonResponse,
                        rxNumber, transferId,
                        completeName, AppUtil.getSafeStr(prescriberName, ""),
                        AppUtil.getSafeStr(prescriberPhone, ""), AppUtil.getSafeStr(orderType, ""), dependentId, orderTransferImagesList
                );
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(PropertiesUtil.getProperty("INVALID_SECURITY_TOKEN"));
            logger.info(PropertiesUtil.getProperty("INVALID_SECURITY_TOKEN"));
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private String uploadOrderTransferImages(List<MultipartFile> drugImgList, String completeName, PatientProfile patientProfile, String dateStr, List<OrderTransferImages> orderTransferImagesList) throws IOException {
        String drugImagPath;
        int count = 1;
        for (MultipartFile drugImg : drugImgList) {
            OrderTransferImages transferImages = new OrderTransferImages();
            logger.info("Drug Image Format: " + drugImg.getContentType());
            String ext = FileUtil.determineImageFormat(drugImg.getBytes());
            completeName = "Img_" + patientProfile.getId() + "_" + dateStr + "_" + count + "." + ext;
            drugImagPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
            logger.info("Complete Drug Image Path: " + drugImagPath);
            FileCopyUtils.copy(drugImg.getBytes(),
                    new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
            CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND"));
            completeName = drugImagPath;
            transferImages.setDrugImg(drugImagPath);
            orderTransferImagesList.add(transferImages);
            count++;
        }
        return completeName;
    }

    private void saveRxTransfer(PatientProfile patientProfile, String patientName, String pharmacyName, String pharmacyPhone,
            String drug, Integer quantity, String videoPath, JsonResponse jsonResponse, String rxNumber, String transferId,
            String drugImg, String prescriberName, String prescriberPhone, String orderType) {

        OrderChain orderChain = patientProfileService.saveRxOrderChainForTransfer(patientProfile.getId(), patientName,
                pharmacyName, pharmacyPhone, drug, quantity, videoPath, rxNumber, transferId, drugImg,
                prescriberName, prescriberPhone, orderType);
        TransferRequest transferRequest = patientProfileService.saveRxTransferRequest(patientProfile.getId(), patientName,
                pharmacyName, pharmacyPhone, drug, quantity, videoPath, rxNumber, transferId, drugImg,
                prescriberName, prescriberPhone, orderType);
        if (transferRequest != null) {
            logger.info("Transfer Id is:: " + transferRequest.getId());
            RewardPoints rewardPoints = patientProfileService.getRxTransferPoints();
            if (rewardPoints != null && rewardPoints.getId() != null) {
                jsonResponse.setErrorCode(1);
                rewardPoints.setTransferId(transferRequest.getId());
                rewardPoints.setOrderChainId(orderChain.getId());
                jsonResponse.setData(rewardPoints);
                jsonResponse.setErrorMessage("Rx Transfer successful");
            } else {
                jsonResponse.setErrorCode(1);
                transferRequest.setTransferId(transferRequest.getId());
                jsonResponse.setData(transferRequest);
                jsonResponse.setErrorMessage("Rx Transfer successful");
                logger.info("Rx Transfer successful: " + transferRequest.getId());
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
            logger.info(Constants.ERROR_SAVE_RECORD);
        }
    }

    private void saveRxTransfer(PatientProfile patientProfile, String patientName, String firstName, String lastName, String pharmacyName, String pharmacyPhone,
            String drug, String strength, Integer quantity, String videoPath, JsonResponse jsonResponse, String rxNumber, String transferId,
            String drugImg, String prescriberName, String prescriberPhone, String orderType, String dependentId, List<OrderTransferImages> orderTransferImageses) {

        OrderChain orderChain = patientProfileService.saveRxOrderChainForTransfer(patientProfile.getId(), patientName, firstName, lastName,
                pharmacyName, pharmacyPhone, drug, strength, quantity, videoPath, rxNumber, transferId, drugImg,
                prescriberName, prescriberPhone, orderType, dependentId, orderTransferImageses);
        TransferRequest transferRequest = patientProfileService.saveRxTransferRequest(patientProfile.getId(), patientName,
                pharmacyName, pharmacyPhone, drug, quantity, videoPath, rxNumber, transferId, drugImg,
                prescriberName, prescriberPhone, orderType);
        if (transferRequest != null) {
            logger.info("Transfer Id is:: " + transferRequest.getId());
            RewardPoints rewardPoints = new RewardPoints(); //patientProfileService.getRxTransferPoints();
//            if (rewardPoints != null && rewardPoints.getId() != null) {
            jsonResponse.setErrorCode(1);
            rewardPoints.setTransferId(transferRequest.getId());
            rewardPoints.setOrderChainId(orderChain.getId());
            jsonResponse.setData(rewardPoints);
            jsonResponse.setErrorMessage("Rx Transfer successful");
//            } else {
//                jsonResponse.setErrorCode(1);
//                transferRequest.setTransferId(transferRequest.getId());
//                jsonResponse.setData(transferRequest);
//                jsonResponse.setErrorMessage("Rx Transfer successful");
//                logger.info("Rx Transfer successful: " + transferRequest.getId());
//            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
            logger.info(Constants.ERROR_SAVE_RECORD);
        }
    }

    /**
     * This function is used to get all the dependents of a profile
     *
     * @param securityToken
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getPatientProfileMembersWs", produces = "application/json")
    public @ResponseBody
    Object getPatientProfileMembers(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " PatientProfileMembers is: " + id);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            PatientDependantDTO patientProfileMembers = patientProfileService.getPatientProfileMembersById(id);
            if (patientProfileMembers != null && patientProfileMembers.getId() != null) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setData(patientProfileMembers);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            } else {
                setEmptyData(jsonResponse);
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Empty list.");
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get notification detail view
     *
     * @param securityToken
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getNotificationDeatilViewWs", produces = "application/json")
    public @ResponseBody
    Object getNotificationDeatilViewWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " id is: " + id);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfileService.getNotificationMessagesByProfileId(patientProfile.getId()).size() > 0) {
            jsonResponse.setData(patientProfileService.getNotificationMessagesListById(id));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Sucess");
        } else {
            logger.info("NotificationMessagesList cannot found against this profile id or invalid token");
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Empty List");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update notification messages
     *
     * @param securityToken
     * @param id
     * @param messageCategory
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateNotificationMessagesWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateNotificationMessagesWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "messageCategory", required = false) String messageCategory) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " id is: " + id + " Message Category:: " + messageCategory);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(messageCategory)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Message Category is null.");
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (messageCategory.equalsIgnoreCase(Constants.ORDER_NOTIFICATION)) {
            if (patientProfileService.getNotificationMessagesByProfileId(patientProfile.getId()).size() > 0) {
                NotificationMessages notificationMessages = patientProfileService.updateNotificationMessages(id);
                if (notificationMessages != null) {
                    notificationMessages.setMessageType(null);
                    notificationMessages.setPatientProfile(null);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setData(notificationMessages);
                    jsonResponse.setErrorMessage("Success");
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("There is some problem to save record.");
                    logger.info("There is some problem to save record.");
                }
            } else {
                logger.info("NotificationMessagesList cannot found against this profile id or invalid token");
                setEmptyData(jsonResponse);
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Empty List");
            }
        } else if (messageCategory.equalsIgnoreCase(Constants.PHARMACY_NOTIFICATION)) {
            NotificationMessages notificationMessages = patientProfileService.updateOrdersPtMessage(id);
            if (notificationMessages != null) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setData(notificationMessages);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is some problem to save record.");
                logger.info("There is some problem to save record.");
            }
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get the message read count for in-app messages
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getIsReadNotificationMessagesCountWs", produces = "application/json")
    public @ResponseBody
    Object getIsReadNotificationMessagesCountWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfileService.getNotificationMessagesByProfileId(patientProfile.getId()).size() > 0) {
            NotificationMessages notificationMessages = patientProfileService.getIsReadNotificationMessagesCount(patientProfile.getId());
            if (notificationMessages != null) {
                jsonResponse.setData(notificationMessages);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Success");
            } else {
                setEmptyData(jsonResponse);
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Failure");
            }
        } else {
            logger.info("NotificationMessagesList cannot found against this profile id or invalid token");
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Empty List");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to save transfer rx points
     *
     * @param securityToken
     * @param points
     * @param description
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/saveTransferRxPointsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object saveTransferRxPoints(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "points", required = false) String points,
            @RequestParam(value = "type", required = false) String description) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            if (patientProfileService.saveRewardHistory(description, Integer.parseInt(points), patientProfile.getId(), Constants.PLUS)) {
                //sendRxTransferNotification(patientProfile, jsonResponse);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is some problem to save record.");
                logger.info("There is some problem to save record.");
            }
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void sendRxTransferNotification(PatientProfile patientProfile, Order order, TransferRequest transferRequest, JsonResponse jsonResponse) throws Exception {
        //send notification
        CampaignMessages campaignMessages = null;
        if (order != null && order.getIsBottleAvailable() != null && order.getIsBottleAvailable()) {
            campaignMessages = patientProfileService.getNotificationMsgs(Constants.RX_TRANSFER_RESPONSE, Constants.RX_TRANSFER);
        } else {
            campaignMessages = patientProfileService.getNotificationMsgs("Rx Scan Request", Constants.RX_TRANSFER);
        }
        if (campaignMessages != null) {
            if (CommonUtil.isNotEmpty(transferRequest.getPharmacyPhone())) {
                transferRequest.setPharmacyPhone(CommonUtil.replaceAllStr(transferRequest.getPharmacyPhone(), "-", ""));
            }
            String messageSubject = campaignMessages.getSubject();
            String messageText = campaignMessages.getSmstext();
            String userInputStr = "<h5> <small>Generic For</small> <span class=\"blue\"> User Inputted " + "</span></h5>";
            campaignMessages.setPushNotification(patientProfileService.getMessageSubjectWithprocessedPlaceHolders(
                    AppUtil.getSafeStr(campaignMessages.getPushNotification(), ""), order != null ? order.getId() : "0"));
            campaignMessages.setSubject(patientProfileService.getMessageSubjectWithprocessedPlaceHolders(messageSubject, order != null ? order.getId() : "0"));
            campaignMessages.setSmstext(messageText.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                    .replace("[SERVICE_REQUESTED]", "X-FER RX via PHARMACY LABEL")
                    .replace("[request_no]", AppUtil.getSafeStr(transferRequest.getOrderId(), ""))
                    .replace("[REQUEST_MADE]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                    .replace("[DRUG_NAME]", order != null ? AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getDrugName()), "") + userInputStr : "")
                    .replace("[DRUG_STRENGTH]", order != null ? AppUtil.getSafeStr(order.getStrength(), "") : "")
                    .replace("[DRUG_QTY]", order != null ? AppUtil.getSafeStr(order.getQty(), "") : "")
                    .replace("[DRUG_IMAGE]", (CommonUtil.isNotEmpty(transferRequest.getDrugImg()) && CommonUtil.urlAuthorization(transferRequest.getDrugImg()) ? "<img id='drugImg' src=\'" + transferRequest.getDrugImg() + "\' width='30' height='30'/>" : ""))
                    .replace("[RX_NUMBER]", AppUtil.getSafeStr(transferRequest.getRxNumber(), ""))
                    .replace("[PAYMENT_TYPE]", AppUtil.getSafeStr(transferRequest.getPaymentType(), ""))
                    .replace("[DELIVERY_DAYS]", AppUtil.getSafeStr(transferRequest.getDeliveryDay(), ""))
                    .replace("[PHARMACY_PHONE]", (CommonUtil.isNotEmpty(transferRequest.getPharmacyPhone()) ? CommonUtil.subStringPhone(transferRequest.getPharmacyPhone(), " ") : "")));
            if (patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId(), order.getId())) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Rx Transfer Notification send.");
                logger.info("Rx Transfer Notification send.");
            } else {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Rx Transfer Notification send.");
                logger.info("Rx Transfer Notification send.");
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Rx Transfer Notification cannot send.");
            logger.info("Rx Transfer Notification cannot send.");
        }
    }

    /**
     * This function is used to get the reward points of a patient
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getMyRewardsPointWs", produces = "application/json")
    public @ResponseBody
    Object getMyRewardsPointWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.getMyRewardsPoints(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Success");
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private File setFilePath(Integer profileId, String imageFormat, String name) {
        logger.info("Get file path");
        String webappRoot = servletContext.getRealPath("/");
        String relativeFolder = File.separator + "resources" + File.separator
                + "noinsurancecard" + File.separator;
        String filename = PropertiesUtil.getProperty("INSURANCE_CARD_PATH") + name + profileId + "." + imageFormat;
        try {
            Date date = new Date();
            String dateStr = DateUtil.dateToString(date, Constants.DATE_FORMATE); //DateUtil.dateToString(date, "yy-mm-dd hh:mm:ss");
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");
            dateStr = dateStr.replace("_", ":");
            filename = PropertiesUtil.getProperty("INSURANCE_CARD_PATH") + name + profileId + "-" + dateStr + "." + imageFormat;
            logger.info("-------------- IMAGE URL1 -------------- " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:: setFilePath", e);
        }
        File file = new File(filename);
        return file;
    }

    private void setJsonResponse(JsonResponse jsonResponse, String type, Object patientProfile) {
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(type);
        jsonResponse.setData(patientProfile);
    }

    private Object objectMapper(ObjectMapper mapper, JsonResponse jsonResponse) throws IOException {
        logger.info("make json response");
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        logger.info("return json..");
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse);
    }

    /**
     * This function is to get drugs
     *
     * @param securityToken
     * @param offSet
     * @return Its return JSON of profile
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/drugCategoryWs", produces = "application/json")
    public @ResponseBody
    Object drugCategoryWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "offSet", required = false) Integer offSet) throws IOException {
        logger.info("Json response: " + offSet);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
//            DrugPagingDTO drugPagingDTO = objectMapper.readValue(json, DrugPagingDTO.class);
            DrugPagingDTO drugPagingDTO = new DrugPagingDTO();
            drugPagingDTO.setOffSet(offSet);
            Long l_total = patientProfileService.getTotalDrugCategory();

            List drugCategories = patientProfileService.getDrugCategory(drugPagingDTO.getOffSet());
            int offsetResponse = drugPagingDTO.getOffSet() + Constants.PAGING_CONSTANT.RECORDS_PER_PAGE;
            if (drugCategories != null && drugCategories.size() > 0) {
                logger.info("List size is: " + drugCategories.size());
                DrugModelDtoConversion drugModelDtoConversion = new DrugModelDtoConversion();
                List<DrugCategoryDTO> lst_DrugCategoryDTO = drugModelDtoConversion.modelToDtoDrugCategory(drugCategories);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Getting drug category");
                jsonResponse.setData(lst_DrugCategoryDTO);
                jsonResponse.setTotalRecords(l_total);
                jsonResponse.setOffset(offsetResponse);
            } else {
                logger.info("Drug category is null.");
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is no category.");
                setEmptyData(jsonResponse);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception ex) {
            jsonResponse.setData(null);
            logger.error("Exception", ex);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem with getting category.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get the categories of the drugs
     *
     * @param securityToken
     * @param offSet
     * @param searchParameter
     * @return @throws IOException
     */
    @RequestMapping(value = "/drugCategorySearchWs", produces = "application/json")
    public @ResponseBody
    Object drugCategorySearchWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "offSet", required = false) Integer offSet,
            @RequestParam(value = "searchParameter", required = false) String searchParameter) throws IOException {
        logger.info("Json response: " + securityToken);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
//            DrugPagingDTO drugPagingDTO = objectMapper.readValue(json, DrugPagingDTO.class);

            Long l_total = patientProfileService.getTotalDrugCategory();
            List drugCategories = patientProfileService.getDrugsByParameter(searchParameter);
            int offsetResponse = offSet + Constants.PAGING_CONSTANT.RECORDS_PER_PAGE;
            if (drugCategories != null && drugCategories.size() > 0) {

                DrugModelDtoConversion drugModelDtoConversion = new DrugModelDtoConversion();
                List<DrugCategoryDTO> lst_DrugCategoryDTO = drugModelDtoConversion.modelToDtoDrugCategorySearch(drugCategories);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Getting drug category");
                jsonResponse.setData(lst_DrugCategoryDTO);
                jsonResponse.setTotalRecords(l_total);
                jsonResponse.setOffset(offsetResponse);
            } else {
                logger.info("Drug category is null.");
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("No drug found.");
                setEmptyData(jsonResponse);
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception ex) {
            jsonResponse.setData(null);
            logger.error("Exception", ex);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem with getting category.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get all the details of the drugs ordered by a
     * particular patient
     *
     * @param securityToken
     * @param drugId
     * @param qty
     * @param drugType
     * @param strength
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getPlaceRxOrderDetailsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object getPlaceRxOrderDetailsWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "drugId", required = false) Integer drugId,
            @RequestParam(value = "qty", required = false) Integer qty,
            @RequestParam(value = "dType", required = false) String drugType,
            @RequestParam(value = "strength", required = false) String strength) throws IOException {
        logger.info("Json response: " + securityToken);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            OrderDetailDTO orderDetailDTO = patientProfileService.getPlaceRxOrderDetailsWs(patientProfile, drugId, qty, drugType, strength);
            if (orderDetailDTO != null) {
                jsonResponse.setData(orderDetailDTO);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Sucess");
            } else {
                setEmptyData(jsonResponse);
                logger.info("Empty list.");
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.EMPTY_MESSAGE);
            }
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void payed(JsonResponse jsonResponse) {
        logger.info("Payment has already processed!");
        jsonResponse.setErrorCode(0);
        jsonResponse.setErrorMessage("Payment has already processed!");
    }

    /**
     * This function is used to compete the order by processing the payment
     * required to get the drug
     *
     * @param securityToken
     * @param drugId
     * @param finalPayment
     * @param redeemPoints
     * @param redeemPointsCost
     * @param handLingFee
     * @param strength
     * @param drugName
     * @param drugType
     * @param qty
     * @param drugPrice
     * @param cardId
     * @param addressId
     * @param orderId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/placeRxOrderDetailsWs", produces = "application/json")
    public @ResponseBody
    Object placeRxOrderDetailsWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "drugId", required = false) Integer drugId,
            @RequestParam(value = "payment", required = false) String finalPayment,
            @RequestParam(value = "redeemPoints", required = false) String redeemPoints,
            @RequestParam(value = "redeemPointsCost", required = false) String redeemPointsCost,
            @RequestParam(value = "handLingFee", required = false) String handLingFee,
            @RequestParam(value = "strength", required = false) String strength,
            @RequestParam(value = "drugName", required = false) String drugName,
            @RequestParam(value = "drugType", required = false) String drugType,
            @RequestParam(value = "qty", required = false) String qty,
            @RequestParam(value = "drugPrice", required = false) String drugPrice,
            @RequestParam(value = "cardId", required = false) String cardId,
            @RequestParam(value = "addressId", required = false) String addressId,
            @RequestParam(value = "orderId", required = false) String orderId) throws IOException, Exception {
        logger.info("Json response: " + securityToken + " Drug Id: " + drugId + " Final Payment is: " + finalPayment + " Redeem Points: " + redeemPoints + " Redeem Points Cost: " + redeemPointsCost);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            logger.info("Profile id is: " + patientProfile.getId());
            PatientPaymentInfo patientPaymentInfo = patientProfileService.getPatientPaymentInfoByCardId(cardId);//getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null && patientPaymentInfo.getDefaultCard().equalsIgnoreCase("Yes")) {
                logger.info("is Default Card: " + patientPaymentInfo.getDefaultCard());
                logger.info(" Card NO: " + patientPaymentInfo.getCardNumber());
                if (!validCardTypeLength(patientPaymentInfo.getCardType(), patientPaymentInfo.getCvvNumber(), jsonResponse)) {
                    return objectMapper(objectMapper, jsonResponse);
                }
                //To Do
//                if (CommonUtil.isNotEmpty(finalPayment) && CommonUtil.getStrToBigDecimal(finalPayment).compareTo(BigDecimal.ZERO) > 0) {
//                    DoDirectPayment payment = new DoDirectPayment();
//                    DoDirectPaymentResponseType response = payment.doDirectPayment(patientPaymentInfo.getCardType(),
//                            patientPaymentInfo.getCardNumber(), patientPaymentInfo.getExpiryDate(),
//                            patientPaymentInfo.getCardHolderName(), finalPayment, patientPaymentInfo.getCvvNumber());
//                    logger.info("DoDirectPaymentResponseType: " + response);
//                    if (!response.getAck().getValue().equalsIgnoreCase("success")) {
//                        List<ErrorType> errorList = response.getErrors();
//                        jsonResponse.setErrorCode(0);
//                        jsonResponse.setErrorMessage(errorList.get(0).getLongMessage());
//                        return objectMapper(objectMapper, jsonResponse);
//                    }
//                }
            } else {
                logger.info("Payment info is null");
            }
            Order order = patientProfileService.saveRxOrder(patientProfile, drugId, finalPayment, redeemPoints, redeemPointsCost,
                    handLingFee, strength, drugName, drugType, qty, drugPrice, patientPaymentInfo, addressId);
            OrderHistory history = new OrderHistory();
            history.setOrder(order);
            history.setOrderStatus(order.getOrderStatus());
            history = patientProfileService.saveOrderHistory(history);
            if (order != null && order.getId() != null) {
                CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs("Order Placed", "Rx Order");
                if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                    order.setServiceRequested("WRITTEN RX via APP");
                    String message = CommonUtil.replaceRxOrderPlaceHolder(campaignMessages.getSmstext(), order);
                    campaignMessages.setSmstext(message);
                    logger.info("Notification send");
                    patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId(), order.getId());
                    logger.info("Order id is: " + order.getId() + " Patient Profile id is: " + patientProfile.getId());
                    jsonResponse.setData(patientProfileService.viewOrderReceiptWs(patientProfile.getId(), order.getId()));
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.SUCCESS);
                    logger.info(Constants.SUCCESS);
                    //start sending place order email and sms
                    //startSMSAndEmailSendThread(order.getId(), drugName, order.getStrength(), order.getQty(), order.getDrugType(), "Order Placed");
                } else {
                    logger.info("Notification send fail or campaign messages is not defined.");
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Notification send fail or campaign messages is not defined.");
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.FAILURE);
            }
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return jsonResponse;
    }

    private void startSMSAndEmailSendThread(String orderId, String drugName, String drugStrength, String drugQty, String drugType, String requestMode) {
        logger.info("DrugName is# " + drugName);
        try {
            List<OrderPlaceEmail> orderPlaceEmails = patientProfileService.getOrderPlaceEmails();
            if (CommonUtil.isNullOrEmpty(orderPlaceEmails)) {
                logger.info("No email address exist for sending place order msg.");
                return;
            }

            CMSEmailContent cMSEmailContent = cMSService.getCMSEmailByPageId(Integer.parseInt(PropertiesUtil.getProperty("PLACE_ORDER_CMS_EMAIL_CONTENT_ID")));
            String emailBody = cMSEmailContent.getEmailBody();
            SMSAndEmailSendThread sMSAndEmailSendThread = new SMSAndEmailSendThread(logger);
            sMSAndEmailSendThread.setPatientProfileService(patientProfileService);
            sMSAndEmailSendThread.setOrderPlaceEmails(orderPlaceEmails);
            sMSAndEmailSendThread.setSubject(cMSEmailContent.getSubject());
            sMSAndEmailSendThread.setEmailBody(emailBody.replace("[request_no]", orderId)
                    .replace("[DRUG_NAME]", AppUtil.getProperDrugName(drugName, drugType, drugStrength))
                    .replace("[date]", DateUtil.dateToString(new Date(), Constants.USA_DATE_FORMATE))
                    .replace("[time]", DateUtil.dateToString(new Date(), "HH:mm"))
                    .replace("[PHARMACY_URL]", PropertiesUtil.getProperty("APP_PATH") + "/pharmacyqueue/login"));
            Thread thread = new Thread(sMSAndEmailSendThread);
            thread.start();
        } catch (Exception e) {
            logger.error("startSMSAndEmailSendThread# Exception# ", e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is used to update details of rx order.
     *
     * @param securityToken
     * @param drugId
     * @param finalPayment
     * @param redeemPoints
     * @param redeemPointsCost
     * @param priceWithMargin
     * @param handLingFee
     * @param strength
     * @param drugName
     * @param drugType
     * @param qty
     * @param drugPrice
     * @param cardId
     * @param addressId
     * @param orderId
     * @param copayCardDic
     * @param deliveryPrefId
     * @param clearNameFlds
     * @param additionalDrugMargin
     * @param comments
     * @param orderChainId
     * @param isPrescriptionHardCopy
     * @param addCopyCard
     * @param coPayCardIdList
     * @param paymentType
     * @param rxAcqCost
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateRxOrderDetailsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object placeRxOrderDetailsWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "drugId", required = false) Long drugId,
            @RequestParam(value = "payment", required = false) String finalPayment,
            @RequestParam(value = "priceWithMargin", required = false) String priceWithMargin,
            @RequestParam(value = "redeemPoints", required = false) String redeemPoints,
            @RequestParam(value = "redeemPointsCost", required = false) String redeemPointsCost,
            @RequestParam(value = "handLingFee", required = false) String handLingFee,
            @RequestParam(value = "strength", required = false) String strength,
            @RequestParam(value = "drugName", required = false) String drugName,
            @RequestParam(value = "drugType", required = false) String drugType,
            @RequestParam(value = "qty", required = false) String qty,
            @RequestParam(value = "drugPrice", required = false) String drugPrice,
            @RequestParam(value = "cardId", required = false) String cardId,
            @RequestParam(value = "copayCardDictionary", required = false) String copayCardDic,
            @RequestParam(value = "addressId", required = false) String addressId,
            @RequestParam(value = "deliveryPrefId", required = false) String deliveryPrefId,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "clearNameFlds", required = false) String clearNameFlds,
            @RequestParam(value = "additionalMargin", required = false) String additionalDrugMargin,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "orderChainId", required = false) String orderChainId,
            @RequestParam(value = "isPrescriptionHardCopy", required = false) Boolean isPrescriptionHardCopy,
            @RequestParam(value = "addCopyCard", required = false) Boolean addCopyCard,
            @RequestParam(value = "coPayCardIdList", required = false) Long[] coPayCardIdList,
            @RequestParam(value = "paymentType", required = false) String paymentType,
            @RequestParam(value = "miles", required = false) String miles,
            @RequestParam(value = "rxAcqCost", required = false) String rxAcqCost) throws IOException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        try {
            logger.info("Json response: " + securityToken + " Drug Id: " + drugId + " Final Payment is: " + finalPayment
                    + " Redeem Points: " + redeemPoints + " Redeem Points Cost: " + redeemPointsCost + " rxAcqCost: " + rxAcqCost);

            ObjectMapper objectMapper = new ObjectMapper();
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("Profile id is: " + patientProfile.getId());
                ///////////////////////////////////////////////////////////////////
                PatientDeliveryAddress address = patientProfileService.getPatientDeliveryAddressById(AppUtil.getSafeInt(addressId, 0));
                String fee = "0.0";
                BigDecimal feeNumeric = BigDecimal.ZERO;
                String prefName = "";
                DeliveryPreferences prefDetails = this.patientProfileService.getDeliveryPreferenceById(AppUtil.getSafeInt(deliveryPrefId, 0));
                if (prefDetails != null) {
                    prefName = prefDetails.getName();
                }
                if (AppUtil.getSafeStr(prefName, "").contains("2nd Day")) {
                    fee = "0.0";
                } else if (address != null) {
                    fee = patientProfileService.getZipCodeCalculations(address.getZip(), patientProfile.getId(), AppUtil.getSafeInt(deliveryPrefId, 0));
                    feeNumeric = CommonUtil.getStrToBigDecimal(fee);
                }

//                List<CoPayCardDetails> coPayCardDetailsList =null;
//                if(copayCardDic!=null)
//                {
//                 //coPayCardDetailsList = objectMapper.readValue(copayCardDic, TypeFactory.collectionType(List.class, CoPayCardDetails.class));
//                }
                //CoPayCardDetails coPayCardDetails = objectMapper.readValue(copayCardDic, CoPayCardDetails.class);
                //////////////////////////////////////////////////////////////////
                PatientPaymentInfo patientPaymentInfo = patientProfileService.getPatientPaymentInfoByCardId(cardId);//getPatientPaymentInfoDefaultCardByProfileId(patientProfile.getId(), "Yes");
                //            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null && patientPaymentInfo.getDefaultCard().equalsIgnoreCase("Yes")) {
                //                logger.info("is Default Card: " + patientPaymentInfo.getDefaultCard());
                //                logger.info(" Card NO: " + patientPaymentInfo.getCardNumber());
                //                if (!validCardTypeLength(patientPaymentInfo.getCardType(), patientPaymentInfo.getCvvNumber(), jsonResponse)) {
                //                    return objectMapper(objectMapper, jsonResponse);
                //                }
                //                if (CommonUtil.isNotEmpty(finalPayment)
                //                        && CommonUtil.getStrToBigDecimal(finalPayment).compareTo(BigDecimal.ZERO) > 0) {
                //                    BigDecimal finalPaymentPlusHandlingFee = CommonUtil.getStrToBigDecimal(finalPayment).add(feeNumeric);
                //                    DoDirectPayment payment = new DoDirectPayment();
                //                    DoDirectPaymentResponseType response = payment.doDirectPayment(patientPaymentInfo.getCardType(),
                //                            patientPaymentInfo.getCardNumber(), patientPaymentInfo.getExpiryDate(),
                //                            patientPaymentInfo.getCardHolderName(), "" + finalPaymentPlusHandlingFee, patientPaymentInfo.getCvvNumber());
                //                    logger.info("DoDirectPaymentResponseType: " + response);
                //                    if (!response.getAck().getValue().equalsIgnoreCase("success")) {
                //                        List<ErrorType> errorList = response.getErrors();
                //                        jsonResponse.setErrorCode(0);
                //                        jsonResponse.setErrorMessage(errorList.get(0).getLongMessage());
                //                        return objectMapper(objectMapper, jsonResponse);
                //                    }
                //                }
                //            } else {
                //                logger.info("Payment info is null");
                //            }
                //////////////////////////////////////////////////////////////
                //            saveRxTransfer(patientProfile, patientName, pharmacyName, pharmacyPhone, drug, quantity, null, jsonResponse, rxNumber, transferId, null,prescriberName,  prescriberPhone);
                /////////////////////////////////////////////////////////////
                String video = "";
                String imagePath = "";
                String source = "";
                if (AppUtil.getSafeStr(orderId, "").length() > 0) {
                    TransferRxQueueDTO transfer = this.patientProfileService.getRequestVideoAndImage(orderId);
                    if (transfer != null) {
                        video = AppUtil.getSafeStr(transfer.getPtVideo(), "");
                        imagePath = AppUtil.getSafeStr(transfer.getTransferImage(), "");
                        if (video.length() > 0) {
                            source = "Video";
                        } else if (imagePath.length() > 0) {
                            source = "Image";
                        }
                    }

                }

                Order order = patientProfileService.saveRxOrder(patientProfile, drugId, finalPayment, priceWithMargin, redeemPoints, redeemPointsCost,
                        fee, strength, drugName, drugType, qty, drugPrice, additionalDrugMargin, patientPaymentInfo, addressId,
                        orderChainId, video, imagePath, comments, false, isPrescriptionHardCopy, addCopyCard, paymentType, prefDetails, cardId, copayCardDic, miles, rxAcqCost);
                OrderHistory history = new OrderHistory();
                history.setOrder(order);
                history.setOrderStatus(order.getOrderStatus());
                history = patientProfileService.saveOrderHistory(history);
                if (order != null && order.getId() != null) {
                    CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs("Order Placed", "Rx Order");
                    if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                        order.setServiceRequested("WRITTEN RX via APP");
                        String messageSubject = campaignMessages.getSubject();
                        String message = campaignMessages.getSmstext();
                        String pushNot = AppUtil.getSafeStr(campaignMessages.getPushNotification(), "");
                        System.out.println("GOING TO CALL getMessageSubjectWithprocessedPlaceHolders PUSH NOT is " + pushNot);
                        String brandName = "";
                        String genericName = "";
                        campaignMessages.setSubject(patientProfileService.getMessageSubjectWithprocessedPlaceHolders(messageSubject, order != null ? order.getId() : "0"));
                        campaignMessages.setPushNotification(
                                patientProfileService.getMessageSubjectWithprocessedPlaceHolders(pushNot, order != null ? order.getId() : "0"));
                        //////////////////////////////////////////
//                        String[] arr = AppUtil.getBrandAndGenericFromDrugName(order.getDrugName());
//                        System.out.println("DRUG NAME -----------> " + order.getDrugName());
//                        if (arr != null) {
//                            if (arr.length == 2) {
//                                brandName = arr[0];
//                                genericName = arr[1];
//                            } else {
//                                brandName = arr[0] + "(" + Constants.BRAND_NAME_ONLY + ")";
//                            }
//                        }
                        String drugQualifiedName = AppUtil.getProperDrugName(order.getDrugName(),
                                AppUtil.getSafeStr(drugType, ""), AppUtil.getSafeStr(strength, ""));
                        System.out.println("BRAND NAME -----------> " + brandName + " GENERIC NAME ----> " + genericName);
                        logger.info("Drug replace name# " + drugQualifiedName);
                        /////////////////////////////////////////
                        campaignMessages.setSmstext(message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                                .replace("[request_no]", AppUtil.getSafeStr(order.getId(), ""))
                                .replace("[DRUG_NAME]", AppUtil.getSafeStr(drugQualifiedName, ""))
                                .replace("[generic_name]", AppUtil.getSafeStr(genericName, ""))
                                .replace("[DRUG_TYPE]", AppUtil.getSafeStr(drugType, ""))
                                .replace("[DRUG_STRENGTH]", AppUtil.getSafeStr(strength, ""))
                                .replace("[DRUG_QTY]", AppUtil.getSafeStr(qty, ""))
                                .replace("[PAYMENT_TYPE]", AppUtil.getSafeStr(paymentType, ""))
                                .replace("[DELIVERY_DAYS]", AppUtil.getSafeStr(prefName, ""))
                                .replace("[SERVICE_REQUESTED]", "WRITTEN RX via APP")
                                .replace("[INS_TYPE]", AppUtil.getSafeStr(paymentType, "").equalsIgnoreCase("INSURANCE") ? "Y" : "N")
                                .replace("[RX_ESTIMATE]", order.getEstimatedPrice() != null
                                        && order.getEstimatedPrice() > 0d
                                        ? AppUtil.roundOffNumberToCurrencyFormat(order.getEstimatedPrice(), "en", "US")
                                        : AppUtil.roundOffNumberToCurrencyFormat(0d, "en", "US"))
                                .replace("[REQUEST_MADE]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE)));
                        logger.info("Notification sending.....");
                        /////////////////////////////////////////////
                        /**
                         * if (AppUtil.getSafeStr(genericName, "").length() == 0
                         * && campaignMessages != null &&
                         * AppUtil.getSafeStr(campaignMessages.getSmstext(),
                         * "").length() > 0) {
                         * campaignMessages.setSmstext(campaignMessages.getSmstext().replace("Generic
                         * For", "")); }*
                         */
                        /////////////////////////////////////////////
                        patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId(), order.getId());
                        logger.info("Order id is: " + order.getId() + " Patient Profile id is: " + patientProfile.getId());
                        jsonResponse.setData(patientProfileService.viewOrderReceiptWs(patientProfile.getId(), order.getId(), fee, prefName, "", "", order.getAwardedPoints() != null ? "" + order.getAwardedPoints() : "0", clearNameFlds, paymentType, comments, source));
                        jsonResponse.setErrorCode(1);
                        jsonResponse.setErrorMessage(Constants.SUCCESS);
                        logger.info(Constants.SUCCESS);
                        startSMSAndEmailSendThread(order.getId(), order.getDrugName(), order.getStrength(), order.getQty(), order.getDrugType(), "Order Placed");
                    } else {
                        logger.info("Notification send fail or campaign messages is not defined.");
                        jsonResponse.setErrorCode(0);
                        jsonResponse.setErrorMessage("Notification send fail or campaign messages is not defined.");
                    }
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.FAILURE);
                }
            } else {
                CommonUtil.inValidSecurityToken(jsonResponse);
            }
            return jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return jsonResponse;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function is used to get redeem points
     *
     * @param securityToken
     * @param availablePoints
     * @param redeemPoints
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getRedeemPointsWs", produces = "application/json")
    public @ResponseBody
    Object getRedeemPointsWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "availablePoints", required = false) String availablePoints,
            @RequestParam(value = "redeemPoints", required = false) String redeemPoints) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken + " Available Points: " + availablePoints + " Redeem Points: " + redeemPoints);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            OrderDetailDTO orderDetailDTO = patientProfileService.getRedeemPointsWs(redeemPoints);
            if (orderDetailDTO.getRedeemPoints() != null) {
                jsonResponse.setData(orderDetailDTO);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            } else {
                setEmptyData(jsonResponse);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.EMPTY_MESSAGE);
            }
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get Quick Order Statistics
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getQuickStatsWs", produces = "application/json")
    public @ResponseBody
    Object getQuickStatsWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.getQuickStatsWs(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get filled rx history
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getFilledRxHistoryWs", produces = "application/json")
    public @ResponseBody
    Object getFilledRxHistory(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.getFilledRxHistory(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/getRefillRxWs", produces = "application/json")
    public @ResponseBody
    Object getRefillRxList(@RequestHeader(value = "securityToken") String securityToken,
            @RequestParam(value = "orderId", required = false) String orderId, @RequestParam(value = "dependentId", required = false) Integer dependentId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.getRefillRx(patientProfile.getId(), orderId, dependentId));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }
    //////////////////////////////////////////////////////////////////////////

    /**
     * This function is used to view order receipt
     *
     * @param securityToken
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/viewOrderReceiptWs", produces = "application/json")
    public @ResponseBody
    Object viewOrderReceiptWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) String id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.viewOrderReceiptWs(patientProfile.getId(), id));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to send campaign messages who have no insurance
     *
     * @param profileId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getNoInsurancesWs", produces = "application/json")
    public @ResponseBody
    Object getNoInsurancesWs(@RequestParam(value = "profileId", required = false) Integer profileId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("profile id is: " + profileId);
        PatientProfile patientProfile = patientProfileService.getPatientProfileById(profileId);
        if (patientProfile != null && patientProfile.getId() != null) {
            CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs(Constants.NO_INSURANCE, Constants.EVENTNAME);
            saveNoInsuranceMsgs(campaignMessages, patientProfile, profileId, jsonResponse);
        } else {
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Profile id does not exist.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void saveNoInsuranceMsgs(CampaignMessages campaignMessages, PatientProfile patientProfile, Integer profileId, JsonResponse jsonResponse) {
        if (campaignMessages != null && campaignMessages.getMessageId() != null) {
            OrderDetailDTO orderDetailDTO = patientProfileService.getNoInsurancesWs(patientProfile.getMobileNumber(), patientProfile.getId());
            RewardPoints rewardPoints = patientProfileService.getRewardPoints(Constants.Reward_Points_Id);
            String securityToken = RedemptionUtil.getRandomToken();
            logger.info("Token is: " + securityToken);
            if (rewardPoints != null) {
                logger.info("Rewards Points: " + rewardPoints.getPoint().intValueExact());
                setRewardPoints(rewardPoints, profileId);
                orderDetailDTO.setPoints(rewardPoints.getPoint().intValueExact());
                orderDetailDTO.setSecurityToken(securityToken);
            }
            orderDetailDTO.setSubject(campaignMessages.getSubject());
            if (campaignMessages.getSmstext().contains("cid:No_Insurance")) {
                logger.info("Set image path");
                String mess = campaignMessages.getSmstext();
                mess = mess.replaceAll("cid:No_Insurance", orderDetailDTO.getNoInsuranceCard());
                orderDetailDTO.setNoInsuranceCard(mess);
                campaignMessages.setSmstext(mess);
            }
            patientProfile.setDiscountPercentage(orderDetailDTO.getLocalDisCount());
            patientProfile.setSecurityToken(securityToken);
            patientProfile.setStatus(Constants.COMPLETED);
            patientProfileService.update(patientProfile);
            patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId());
            jsonResponse.setData(orderDetailDTO);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Empty Message");
        }
    }

    /**
     * This function is used to delete patient dependents
     *
     * @param securityToken
     * @param profileId
     * @param memberId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/deletePatientDependentWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object deletePatientDependentWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "id", required = false) Integer memberId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("Memeber id is: " + memberId + " securityToken: " + securityToken + " ProfileId is: " + profileId);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (profileId == null) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("ProfileId is null.");
                return objectMapper(objectMapper, jsonResponse);
            }
            if (memberId == null) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("MemberId is null.");
                return objectMapper(objectMapper, jsonResponse);
            }
//            if (patientProfileService.dependentHasOrders(memberId)) {
//                jsonResponse.setErrorCode(0);
//                jsonResponse.setErrorMessage("There exist order(s) for this dependent. So it can't be deleted.");
//                return objectMapper(objectMapper, jsonResponse);
//            }
            if (patientProfileService.deletePatientDependentWs(profileId, memberId)) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been deleted successfully.");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is some problem to delete record.");
            }
            return objectMapper(objectMapper, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is some problem to delete record. " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }
    }

    /**
     * This function is used to updated shipping address
     *
     * @param securityToken
     * @param profileId
     * @param dprefId
     * @param distance
     * @param deliveryFee
     * @param addressLine
     * @param appt
     * @param description
     * @param city
     * @param stateId
     * @param zip
     * @param addressType
     * @param defaultAddress
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/shippingAddressWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object deliveryAddressesWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "dprefaId", required = false) Integer dprefId,
            @RequestParam(value = "miles", required = false) String distance,
            @RequestParam(value = "deliveryFee", required = false) String deliveryFee,
            @RequestParam(value = "address", required = false) String addressLine,
            @RequestParam(value = "apartment", required = false) String appt,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stateId", required = false) Integer stateId,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "addressType", required = false) String addressType,
            @RequestParam(value = "defaultAddress", required = false) String defaultAddress) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("ProfileId is: " + profileId + " DeliveryPreferences id: " + dprefId + " Address is: " + addressLine + " Appt: " + appt + " Description is: " + description + " City: " + city + " State id: " + stateId + " Zip Code: " + zip + " AddressType is: " + addressType + " Default address is: " + defaultAddress);
        PatientProfile profile;
        if (securityToken != null) {
            profile = patientProfileService.getPatientProfileByToken(securityToken);
        } else {
            profile = patientProfileService.getPatientProfileById(profileId);
        }
        if (!isValidZipCode(zip, profile, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (profile != null && profile.getId() != null) {
            if (dprefId != null) {
                String status = Constants.PENDING;
                if (securityToken != null || CommonUtil.isNotEmpty(profile.getSecurityToken())) {
                    status = Constants.COMPLETED;
                }
                patientProfileService.updateDeliveryPreferencesByProfileId(profile.getId(), dprefId, status, deliveryFee, distance);
            }
            if (defaultAddress.equalsIgnoreCase(Constants.NO)) {
                if (!patientProfileService.isDefaultDeliveryAddress(profile.getId())) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Please Make it your default shipping address.");
                    return objectMapper(objectMapper, jsonResponse);
                }
            }
            PatientDeliveryAddress patientDeliveryAddress = patientProfileService.savePatientDeliveryAddress(profile.getId(), addressLine, appt, description, city, stateId, zip, addressType, defaultAddress, dprefId);
            if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null) {
                jsonResponse.setData(patientProfileService.getPatientDeliveryAddressById(profile.getId(), patientDeliveryAddress.getId()));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
            }
        } else {
            logger.info("Profile info is: ");
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.PROFILE_ID_REQ);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private boolean isValidZipCode(String zip, PatientProfile profile, JsonResponse jsonResponse) throws IOException {
        List<DeliveryDistanceFeeDTO> list = patientProfileService.getZipCodeCalculationsList(zip, profile);
        if (list.isEmpty()) {
            if (!validateZipCode(zip, jsonResponse, profile.getId())) {
                return false;
            }
        }
        return true;
    }

    private boolean validateZipCode(String zip, JsonResponse jsonResponse, Integer profileId) throws IOException {
        logger.info("Validate zip code -> zip code is: " + zip);
        DeliveryDistanceFeeDTO deliveryDistanceFeeDTO = patientProfileService.validateDistanceFeeDTO(zip, profileId);
        if (deliveryDistanceFeeDTO != null && deliveryDistanceFeeDTO.getError_code() != null) {
            if (deliveryDistanceFeeDTO.getError_code() == 400) {
                inValidZipCodeMessage(jsonResponse);
                return false;
            }
            if (deliveryDistanceFeeDTO.getError_code() == 429) {
                zipCodeLimitError(jsonResponse);
                return false;
            }
            if (deliveryDistanceFeeDTO.getError_code() == 404) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(deliveryDistanceFeeDTO.getErrorMessage());
                return false;
            }
        }
        return true;
    }

    private void zipCodeLimitError(JsonResponse jsonResponse) {
        jsonResponse.setErrorCode(0);
        jsonResponse.setErrorMessage(Constants.LIMIT_EXCEEDED);
    }

    /**
     * This function is used to get delivery address
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getDeliveryAddressWs", produces = "application/json")
    public @ResponseBody
    Object getDeliveryAddressWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "id", required = false) Integer id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken: " + securityToken + " Id is: " + id);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile profile = patientProfileService.getPatientProfileByToken(securityToken);
        PatientDeliveryAddressDTO patientDeliveryAddressDTO = patientProfileService.getPatientDeliveryAddressById(profile.getId(), id);
        if (patientDeliveryAddressDTO != null && patientDeliveryAddressDTO.getId() != null) {
            jsonResponse.setData(patientDeliveryAddressDTO);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("DeliveryAddress is null.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get patient payment information
     *
     * @param securityToken
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getPatientPaymentInfoWs", produces = "application/json")
    public @ResponseBody
    Object getPatientPaymentInfoWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "id", required = false) Integer id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken: " + securityToken + " Id is: " + id);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile profile = patientProfileService.getPatientProfileByToken(securityToken);
        PatientPaymentInfo patientPaymentInfo = patientProfileService.getPatientPaymentInfo(id, profile.getId());
        if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
            jsonResponse.setData(patientPaymentInfo);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            setEmptyData(jsonResponse);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("PatientPaymentInfo is null.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to save rx transfer for future order
     *
     * @param securityToken
     * @param patientName
     * @param rxNumber
     * @param pharmacyName
     * @param pharmacyPhone
     * @param drug
     * @param quantity
     * @param transferId
     * @param prescriberName
     * @param prescriberPhone
     * @param orderType
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/saveRxTransferWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object saveRxTransferRequest(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "rxNumber", required = false) String rxNumber,
            @RequestParam(value = "pharmacyName", required = false) String pharmacyName,
            @RequestParam(value = "pharmacyPhone", required = false) String pharmacyPhone,
            @RequestParam(value = "drug", required = false) String drug,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "transferId", required = false) String transferId,
            @RequestParam(value = "prescriberName", required = false) String prescriberName,
            @RequestParam(value = "prescriberPhone", required = false) String prescriberPhone,
            @RequestParam(value = "orderType", required = false) String orderType) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " PatientName is: " + patientName + " pharmacyName: " + pharmacyName + " pharmacyPhone: " + pharmacyPhone + " Drug is: " + drug + " Quantity is: " + quantity + " TransferId:: " + transferId);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            saveRxTransfer(patientProfile, patientName, pharmacyName, pharmacyPhone, drug, quantity, null, jsonResponse, rxNumber, transferId, null, prescriberName, prescriberPhone, orderType);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to calculate distance between different geographic
     * location by zip codes
     *
     * @param securityToken
     * @param profileId
     * @param zip
     * @param pickedFromPharmacy
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/calculateZipCodeDistanceWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object calculateZipCodeDistance(@RequestHeader(value = "profileId", required = false) Integer profileId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "zip", required = false) String zip, @RequestParam(value = "pickedFromPharmacy", required = false) boolean pickedFromPharmacy) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " Zip Code: " + zip + " ProfileId: " + profileId + " pickedFromPharmacy: " + pickedFromPharmacy);
        PatientProfile patientProfile;
        if (profileId != null) {
            patientProfile = patientProfileService.getPatientProfileById(profileId);
        } else {
            patientProfile = patientProfileService.getPatientProfileBySecurityToken(securityToken);
        }
        if (patientProfile != null && patientProfile.getId() != null) {
            List<DeliveryDistanceFeeDTO> list = patientProfileService.getZipCodeCalculationsList(zip, patientProfile, pickedFromPharmacy);
            if (list.isEmpty()) {
                if (!validateZipCode(zip, jsonResponse, patientProfile.getId())) {
                    return objectMapper(objectMapper, jsonResponse);
                }
                list = patientProfileService.getDistanceFeeDTO(zip, patientProfile.getId());
            }
            jsonResponse.setErrorCode(1);
            jsonResponse.setData(list);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Invalid security or profileId");
            logger.info("Invalid security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to confirm payment from patient in case the payment
     * is more than the quoted price
     *
     * @param securityToken
     * @param transferId
     * @param paymentId
     * @param devliveryAddressId
     * @param dprefId
     * @param zip
     * @param miles
     * @param deliveryFee
     * @param comments
     * @param addCopyCard
     * @param paymentType
     * @param orderChainId
     * @param coPayCardIdList
     * @param copayCardDic
     * @param isBottleAvailable
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/confirmPaymentWs", produces = "application/json", method = RequestMethod.POST)
    public Object confirmRxTransferPayment(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "transferId", required = false) Integer transferId,
            @RequestParam(value = "paymentId", required = false) Integer paymentId,
            @RequestParam(value = "devliveryAddressId", required = false) Integer devliveryAddressId,
            @RequestParam(value = "dprefId", required = false) Integer dprefId,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "miles", required = false) String miles,
            @RequestParam(value = "deliveryFee", required = false) String deliveryFee,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "addCopyCard", required = false) Boolean addCopyCard,
            @RequestParam(value = "paymentType", required = false) String paymentType,
            @RequestParam(value = "orderChainId", required = false) String orderChainId,
            @RequestParam(value = "coPayCardIdList", required = false) Long[] coPayCardIdList,
            @RequestParam(value = "copayCardDictionary", required = false) String copayCardDic,
            @RequestParam(value = "isBottleAvailable", required = false) Boolean isBottleAvailable) throws IOException, Exception {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " transferId: " + transferId + " paymentId: " + paymentId + " devliveryAddressId: " + devliveryAddressId + " dprefId: " + dprefId + " zip: " + zip + " miles: " + miles + " deliveryFee: " + deliveryFee);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            TransferRequest transferRequest = (TransferRequest) patientProfileService.getObjectById(new TransferRequest(), transferId);
            if (transferRequest != null && transferRequest.getId() != null) {
                Order order = patientProfileService.updateTransferOrder(patientProfile.getId(), transferId, devliveryAddressId,
                        paymentId, dprefId, zip, miles, deliveryFee, AppUtil.getSafeLong(orderChainId, 0L), comments, paymentType,
                        addCopyCard, coPayCardIdList, dprefId, copayCardDic, isBottleAvailable);
                if (order != null) {
                    String source = "";
                    String video = AppUtil.getSafeStr(order.getVideo(), "");
                    String image = AppUtil.getSafeStr(order.getImagePath(), "");
                    if (video.length() > 0) {
                        source = "Video";
                    } else if (image.length() > 0) {
                        source = "Image";
                    }
                    order.setServiceRequested("X-FER RX via PHARMACY LABEL");
                    order.setRxNumber(transferRequest.getRxNumber());
                    order.setPharmacyPhone(transferRequest.getPharmacyPhone());
                    logger.info("Order Number:: " + order.getId());
                    transferRequest.setOrderId(order.getId());
                    transferRequest.setPaymentType(paymentType);
                    if (order.getDeliveryPreference() != null && order.getDeliveryPreference().getId() != null) {
                        String deliveryDay = order.getDeliveryPreference().getName();
                        if (CommonUtil.isNotEmpty(deliveryDay)) {
                            transferRequest.setDeliveryDay(deliveryDay.replace("*", " ") + "Delivery");
                        } else {
                            transferRequest.setDeliveryDay("");
                        }

                    }
                    sendRxTransferNotification(patientProfile, order, transferRequest, jsonResponse);
                    jsonResponse.setData(patientProfileService.viewOrderReceiptWs(patientProfile.getId(), order.getId()));
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Rx Transfer successful");
                    startSMSAndEmailSendThread(order.getId(), order.getDrugName(), order.getStrength(), order.getQty(), order.getDrugType(), Constants.RX_TRANSFER);
                } else {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("TransferRequest is null against this id." + transferId);
                logger.info("TransferRequest is null against this id." + transferId);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info("Invalid security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to save searches made by customer
     *
     * @param securityToken
     * @param profileId
     * @param drugId
     * @param drugName
     * @param drugType
     * @param qty
     * @param drugPrice
     * @param strength
     * @param genericName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/saveDrugSearchesWs", produces = "application/json", method = RequestMethod.POST)
    public Object saveDrugSearchesWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "profileId", required = false) String profileId,
            @RequestParam(value = "drugId", required = false) String drugId,
            @RequestParam(value = "drugName", required = false) String drugName,
            @RequestParam(value = "drugType", required = false) String drugType,
            @RequestParam(value = "drugQty", required = false) String qty,
            @RequestParam(value = "drugPrice", required = false) String drugPrice,
            @RequestParam(value = "strength", required = false) String strength,
            @RequestParam(value = "genericName", required = false) String genericName) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (CommonUtil.isNullOrEmpty(profileId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.PROFILE_ID_REQ);
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(drugId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.DRUG_ID_REQ);
            return objectMapper(objectMapper, jsonResponse);
        }
        logger.info("Security Token is: " + securityToken + " drugName is: " + drugName + " drugType is: " + drugType + " qty is: " + qty + " drugPrice is: " + drugPrice + " strength is: " + strength + " genericName is: " + genericName);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            if (patientProfileService.saveDrugSearchesWs(Integer.parseInt(profileId),
                    AppUtil.getSafeLong(drugId, 0L), drugName, drugType, qty, drugPrice, strength, genericName)) {
                jsonResponse.setData(patientProfileService.getSearchesRecordList(patientProfile.getId()));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info("Invalid security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to delete saved searches made by patient
     *
     * @param securityToken
     * @param drugSearchId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/clearDrugSearchesWs", produces = "application/json", method = RequestMethod.POST)
    public Object deleteDrugSearchesById(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "drugSearchId", required = false) Integer drugSearchId) throws IOException {
        logger.info("Security Token is: " + securityToken + " DrugSearchId is: " + drugSearchId);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            if (patientProfileService.deleteDrugSearchesById(drugSearchId)) {
                jsonResponse.setData(patientProfileService.getSearchesRecordList(patientProfile.getId()));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Saved search deleted successfully.");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_DELETE_MEGGAGE);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to delete all the saved searches
     *
     * @param securityToken
     * @param profileId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/clearAllDrugSearchesWs", produces = "application/json", method = RequestMethod.POST)
    public Object clearAllDrugSearchesWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "id", required = false) Integer profileId) throws IOException {
        logger.info("Security Token is: " + securityToken + " ProfileId is: " + profileId);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            if (patientProfileService.deleteAllDrugSearchesRecordByProfileId(profileId)) {
                jsonResponse.setData(patientProfileService.getSearchesRecordList(profileId));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Saved search deleted successfully.");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_DELETE_MEGGAGE);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info("Invalid security token.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to record blood glucose level of patient
     *
     * @param securityToken
     * @param glucoseLevel
     * @param readingTime
     * @param type
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addBloodGlucoseResultWs", produces = "application/json", method = RequestMethod.POST)
    public Object saveBloodGlucoseResultWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "glucoseLevel", required = false) String glucoseLevel,
            @RequestParam(value = "readingTime", required = false) String readingTime,
            @RequestParam(value = "type", required = false) String type) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken + " glucoseLevel is: " + glucoseLevel + " glucoseTime is: " + readingTime + " Type is: " + type);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            if (patientProfileService.saveBloodGlucoseResult(patientProfile.getId(), glucoseLevel, readingTime, type)) {
                RewardPoints rewardPoints = patientProfileService.getRewardPoints(Constants.BLOOD_GLUCOSE_ID);
                if (rewardPoints != null && rewardPoints.getId() != null && rewardPoints.getPoint() != null) {
                    logger.info("Reward Points is: " + rewardPoints.getPoint());
                    rewardPoints.setPoints(rewardPoints.getPoint().intValueExact());
                    jsonResponse.setData(rewardPoints);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
                } else {
                    logger.info("Blood Glucose point not available against this id: " + Constants.BLOOD_GLUCOSE_ID);
                    jsonResponse.setData(rewardPoints);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
                }
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_DELETE_MEGGAGE);
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     *
     * @param profileId
     * @param mobileNumber
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/noThanksWs", produces = "application/json")
    public Object noThanksWs(@RequestParam(value = "profileId", required = false) Integer profileId,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Profile Id is: " + profileId + " mobileNumber is: " + mobileNumber);

        PatientProfile patientProfile = patientProfileService.getPatientProfileById(profileId);
        if (!isProfileInfoEmpty(patientProfile, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        logger.info("Mobile# " + patientProfile.getMobileNumber());
        if (!patientProfile.getMobileNumber().equals(EncryptionHandlerUtil.getEncryptedString(mobileNumber))) {
            logger.info("Mobile number does not match");
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.MOBILE_NUMBER_NOT_EXIST);
            return objectMapper(objectMapper, jsonResponse);
        }
        patientProfile.setSecurityToken(RedemptionUtil.getRandomToken());
        patientProfile.setStatus(Constants.COMPLETED);
        if (patientProfileService.update(patientProfile)) {
            RewardPoints rewardPoints = patientProfileService.getRewardPoints(Constants.Reward_Points_Id);
            setRewardPoints(rewardPoints, profileId);
            PatientProfileDTO profileDTO = getPatientProfileDTO(patientProfile);
            profileDTO.setPoints(rewardPoints.getPoint().intValueExact());
            jsonResponse.setData(profileDTO);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_UPDATE_RECORD);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private PatientProfileDTO getPatientProfileDTO(PatientProfile patientProfile) {
        PatientProfileDTO profileDTO = new PatientProfileDTO();
        profileDTO.setProfileId(patientProfile.getId());
        profileDTO.setMobileNumber(EncryptionHandlerUtil.getDecryptedString(patientProfile.getMobileNumber()));
        profileDTO.setSecurityToken(patientProfile.getSecurityToken());
        return profileDTO;
    }

    /**
     * This function is used to delete rx transfer record
     *
     * @param securityToken
     * @param transferId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/deleteRxTransferRecordWs", produces = "application/json", method = RequestMethod.POST)
    public Object deleteRxTransferRecordWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "transferId", required = false) String transferId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken is:: " + securityToken + " TransferId is:: " + transferId);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile == null || patientProfile.getId() == null) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(transferId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("TransferId is null");
            logger.info("TransferId is null");
            return objectMapper(objectMapper, jsonResponse);
        }
        logger.info("Profile Id is:: " + patientProfile.getId());
        if (patientProfileService.deleteRxTransferRecord(Integer.parseInt(transferId), patientProfile.getId())) {
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info(Constants.SUCCESS);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.FAILURE);
            logger.info(Constants.FAILURE);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to set reward points for a shared message with some
     * one.
     *
     * @param securityToken
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping(value = "/shareWs", produces = "application/json", method = RequestMethod.POST)
    public Object share(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException, Exception {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("SecurityToken is:: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            RewardPoints rp = patientProfileService.getRewardPoints(Constants.SHARE_WITH_FRIEND_RP_ID);
            if (patientProfileService.saveRewardHistory(rp.getType(), rp.getPoint().intValueExact(), patientProfile.getId(), Constants.PLUS)) {
                jsonResponse.setErrorCode(1);
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                orderDetailDTO.setSharePoints(rp.getPoint().intValueExact());
                patientProfileService.getOrderRewardDetail(patientProfile.getId(), orderDetailDTO);
                jsonResponse.setData(orderDetailDTO);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
                logger.info(Constants.SUCCESS);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.FAILURE);
                logger.info(Constants.FAILURE);
            }
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to confirm rx order
     *
     * @param transferno
     * @param securityToken
     * @return
     */
    @RequestMapping(value = "/confirmOrderWs/{transferno}", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object confirmOrderWs(@PathVariable("transferno") String transferno,
            @RequestHeader(value = "securityToken", required = false) String securityToken) {
        JsonResponse jsonResponse = new JsonResponse();
        //ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token is: " + securityToken);
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            Order order = this.setupService.saveOrderByTransferDetail(transferno, patientProfile);
            if (order != null) {
                jsonResponse.setData("Order ID " + order.getId());
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            }

        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return jsonResponse;
    }

    /**
     * This function is used to get Notifications
     *
     * @param securityToken
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getNotificationWs", produces = "application/json")
    public @ResponseBody
    Object getNotificationWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("Security Token is: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            logger.info("Profile Id is: " + patientProfile.getId());
            jsonResponse.setData(patientProfileService.getNotificationWs(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            logger.info("Sucess");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
            logger.info(Constants.INVALID_SECURITY_TOKEN);
        }
        return jsonResponse;
    }

    ////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/getWaitingResponseNotificationWs", produces = "application/json; charset=utf-8")
    public @ResponseBody
    Object getWaitingResponseNotificationWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        try {
            logger.info("Security Token is: " + securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return jsonResponse;
            }
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("Profile Id is: " + patientProfile.getId());
                jsonResponse.setData(patientProfileService.getNotificationForWaitingResponsesWs(patientProfile.getId()));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
                logger.info("Sucess");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
                logger.info(Constants.INVALID_SECURITY_TOKEN);
            }
        } catch (Exception e) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return jsonResponse;
    }
    ///////////////////////////////////////////////////////////////////

    /**
     * This is function is used to get specific message
     *
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getNotificationMessagesTextByIdWs", produces = "application/json; charset=utf-8")
    public @ResponseBody
    Object getNotificationMessagesTextByIdWs(@RequestParam(value = "id", required = false) Integer id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("Id: " + id);
        jsonResponse.setData(patientProfileService.getNotificationMessagesTextById(id));
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(Constants.SUCCESS);
        logger.info("Sucess");
        return jsonResponse;
    }

    @RequestMapping(value = "/getAtDoctorTextWs", produces = "application/json; charset=utf-8")
    public @ResponseBody
    Object getAtDoctorTextWs() throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        //  jsonResponse.setData(Constants.AT_DOCTOR_TEXT);
        Calendar now = Calendar.getInstance();
        int cruntYear = now.get(Calendar.YEAR);
        String yearInString = "" + cruntYear;
        String drText = (Constants.AT_DOCTOR_TEXT);
        String replaceString = drText.replace("[year]", yearInString);
        jsonResponse.setData(replaceString);
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(Constants.SUCCESS);
        logger.info("Sucess");

        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * String s1="my name is khan my name is java"; String
     * replaceString=s1.replace("is","was");//replaces all occurrences of "is"
     * to "was" System.out.println(replaceString); Calendar now =
     * Calendar.getInstance(); int year = now.get(Calendar.YEAR); String
     * yearInString = String.valueOf(year); This function is used to send email
     * to patient by request
     *
     * @param id
     * @param email
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/sendEmailWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object sendEmailWs(@RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "email", required = false) String email) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (CommonUtil.isNullOrEmpty(email)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.EMAIL_REQ);
            return false;
        }
        if (!validateEmail(email, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        List<NotificationMessages> messagesesList = patientProfileService.getNotificationMessagesListById(id);
        if (CommonUtil.isNullOrEmpty(messagesesList)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is not record found.");
            return objectMapper(objectMapper, jsonResponse);
        }
        NotificationMessages notificationMessages = messagesesList.get(0);
        if (AppUtil.getSafeStr(notificationMessages.getSubject(), "").equalsIgnoreCase(Constants.MSG_ORDER_RECEIPT)) {
            String message = "Please <a href=\"https://rxdirectdev.ssasoft.com/ConsumerPortal/orderReceipt/" + id + "\">Click here</a> to view your order receipt.";
            notificationMessages.setMessageText(message);
            notificationMessages.setSubject("Order Recevied From Order No " + notificationMessages.getOrderId());
        }
        if (EmailSenderUtil.send(email, notificationMessages.getSubject(), notificationMessages.getMessageText())) {
            isPatientMsgResponse(id);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.EMAIL_SEND_SUCCESS_MESSEGE);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.EMAIL_SEND_FAILURE_MESSEGE);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private boolean validateEmail(String email, JsonResponse jsonResponse) throws IOException {
        if (CommonUtil.isNotEmpty(email)) {
            pattern = Pattern.compile(Constants.EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_EMAIL + " " + email);
                return false;
            }
        }
        return true;
    }

    /**
     * This function is used to test the application for payment through credit
     * card
     *
     * @param id
     * @param cardtype
     * @param cardnumber
     * @param expiry
     * @param cardholder
     * @param cvv
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/sendcardtestWs2", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object testCardPageLoader(@RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "cardtype", required = false) String cardtype,
            @RequestParam(value = "cardnumber", required = false) String cardnumber,
            @RequestParam(value = "expiry", required = false) String expiry,
            @RequestParam(value = "cardholder", required = false) String cardholder,
            @RequestParam(value = "cvv", required = false) String cvv)
            throws IOException {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        PatientProfile patientProfile = this.patientProfileService.getPatientProfileById(id);

        patientProfile.setCardHolderRelation("Cardholder");
        patientProfile.setAllergyStatus("None");
        //  request.getSession().setAttribute("patientProfile", patientProfile);

        //validate credit card info
        PatientPaymentInfo paymentInfo = new PatientPaymentInfo();
        logger.info("CardType: " + paymentInfo.getCardType());
        logger.info("CardNumber: " + paymentInfo.getCardNumber());
        logger.info("CardExpiry: " + paymentInfo.getExpiryDate());
        logger.info("CardHoderName: " + paymentInfo.getCardHolderName());
        logger.info("CardCvv: " + paymentInfo.getCvvNumber());

        //validating...
        DoDirectPayment payment = new DoDirectPayment();
        DoDirectPaymentResponseType response = payment.authorizationRequest(cardtype,
                cardnumber, expiry,
                cardholder, cvv);

        logger.info("DoDirectPaymentResponseType: " + response);
        System.out.println("RESPONSE VALUE " + response.getAck().getValue());
        if (!(response.getAck().getValue().equalsIgnoreCase("success"))) {
            List<ErrorType> errorList = response.getErrors();
            for (ErrorType error : errorList) {
                jsonResponse.setErrorCode(AppUtil.getSafeInt(error.getErrorCode(), 0));
                jsonResponse.setErrorMessage(error.getLongMessage());
            }

//            modelAndView.addObject("errorMessage", errorList.get(0).getLongMessage());
//            modelAndView.setViewName("patientprofile/creditcardinfo");
        }
        if (response.getAck().getValue().equalsIgnoreCase("SuccessWithWarning")) {
            List<ErrorType> errorList = response.getErrors();
            for (ErrorType error : errorList) {
                jsonResponse.setErrorCode(AppUtil.getSafeInt(error.getErrorCode(), 0));
                jsonResponse.setErrorMessage(error.getLongMessage());
            }
        } else {
            jsonResponse.setErrorCode(2);
            jsonResponse.setErrorMessage("Success");
        }
        return objectMapper(objectMapper, jsonResponse);
        //validation ends...

        //return modelAndView;
    }

    /**
     * This function is used to process refill for a drug
     *
     * @param securityToken
     * @param orderChainId
     * @param orderId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/refillModuleWs", produces = "application/json")
    public @ResponseBody
    Object saveRefillModule(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "orderChainId", required = false) Long orderChainId,
            @RequestParam(value = "orderId", required = false) String orderId) throws IOException {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            logger.info("Security Token is: " + securityToken);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile == null) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
                logger.info(Constants.INVALID_SECURITY_TOKEN);
                return objectMapper(objectMapper, jsonResponse);
            }
            if (CommonUtil.isNullOrEmpty(orderChainId)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order ChainId is null");
                logger.info("Order ChainId is null" + orderChainId);
                return objectMapper(objectMapper, jsonResponse);
            }
            jsonResponse.setData(patientProfileService.saveRefillModule(orderChainId, orderId));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            return objectMapper(objectMapper, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/batchRefillModuleWs", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Object saveBatchRefillModule(@RequestHeader(value = "securityToken") String securityToken,
            @RequestBody String lstOrder) throws IOException {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            /////////////////////////////////////////////////////////////////////
            JSONObject jsonObj = new JSONObject(lstOrder);

            JSONArray jsonArray = jsonObj.getJSONArray("lstOrder");

            //then get the type for list and parse using gson as
            Type listType = new TypeToken<List<OrderDetailDTO>>() {
            }.getType();
            List<OrderDetailDTO> lstOrder2 = new Gson().fromJson(jsonArray.toString(), listType);
            ////////////////////////////////////////////////////////////////////
            logger.info("Security Token is: " + securityToken);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile == null) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
                logger.info(Constants.INVALID_SECURITY_TOKEN);
                return objectMapper(objectMapper, jsonResponse);
            }
            if (lstOrder == null) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order list is null");
                logger.info("Order list is null");
                return objectMapper(objectMapper, jsonResponse);
            }
            Set<OrderDetailDTO> batchOrders = patientProfileService.saveRefillModule(lstOrder2, patientProfile.getId());
            for (OrderDetailDTO order : batchOrders) {
                String pharmacyComments = "", brandName = "", genericName = "";
                CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs(Constants.MSG_CONTEXT_REFILL_REQUEST_RECEIVED, Constants.RX_ORDER);
                if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                    pharmacyComments = patientProfileService.loadLastMessageFromPharmacyQueue(order.getId());
                    pharmacyComments = AppUtil.truncateStringToSpecificLength(pharmacyComments, 100);
                    logger.info("Order Drug Name is:: " + order.getDrugName());
//                    String[] arr = AppUtil.getBrandAndGenericFromDrugName(order.getDrugName());
//                    if (arr != null) {
//                        if (arr.length == 2) {
//                            brandName = arr[0];
//                            genericName = arr[1];
//                        } else {
//                            brandName = arr[0] + "(" + Constants.BRAND_NAME_ONLY + ")";
//                        }
//                    }
                    String drugName = AppUtil.getProperDrugName(order.getDrugName(), AppUtil.getSafeStr(
                            order.getDrugType(), ""), AppUtil.getSafeStr(order.getStrength(), ""));
                    String subject = campaignMessages.getSubject();
                    campaignMessages.setSubject(subject);
                    String message = campaignMessages.getSmstext();
                    campaignMessages.setSmstext(message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                            .replace("[request_no]", AppUtil.getSafeStr(order.getId(), ""))
                            .replace("[generic_name]", genericName)
                            .replace("[DRUG_NAME]", drugName).replace("[DRUG_STRENGTH]", order.getStrength())
                            .replace("[DRUG_TYPE]", order.getDrugType()).replace("[DRUG_QTY]", order.getQty())
                            .replace("[DAYS_SUPPLY]", order.getDaysSupply() > 0 ? "" + order.getDaysSupply() : "Not mentioned")
                            .replace("[REFILLS_REMAIN]", order.getRefillsRemaining() != null ? order.getRefillsRemaining() : "Not mentioned")
                            .replace("[PHARMACY_COMMENTS]", pharmacyComments));
                    String pushNot = AppUtil.getSafeStr(campaignMessages.getPushNotification(), "");
                    System.out.println("GOING TO CALL getMessageSubjectWithprocessedPlaceHolders PUSH NOT is " + pushNot);
                    campaignMessages.setPushNotification(patientProfileService.getMessageSubjectWithprocessedPlaceHolders(pushNot, order.getId()));
                    patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId(), order.getId());
                }
                //startSMSAndEmailSendThread(order.getId(), order.getDrugName(), order.getStrength(), order.getQty(), order.getDrugType(), "");
            }
            jsonResponse.setData(batchOrders);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
            return objectMapper(objectMapper, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function is used to get total order count
     *
     * @param securityToken
     * @param patientId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getTotalCountWs", produces = "application/json")
    public @ResponseBody
    Object getActiveOrderWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "patientID", required = false) Integer patientId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(patientId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("patientId is null");
            return objectMapper(objectMapper, jsonResponse);
        }
        jsonResponse.setData(patientProfileService.getOrderCount(patientId));
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(Constants.SUCCESS);
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to update payment card
     *
     * @param orderId
     * @param paymentId
     * @param securityToken
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/updatePaymentCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePCard(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {
        //public Object getObjectById(Object obj, String id)

        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            PatientPaymentInfo patientPaymentInfo = this.patientProfileService.getPatientPaymentInfoByCardId(paymentId);
            if (patientPaymentInfo != null && patientPaymentInfo.getId() != null) {
                if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")) {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("Payment card can't be update now since its status is " + orderStatusName);
                    return objectMapper(objectMapper, jsonResponse);
                }
                //int isSaved = 0;
                isSaved = consumerRegistrationService.updatePaymentCard(paymentId, orderId, orderStatusName,
                        Constants.PATIENT_RESPONSES.UPDATE_PAYMENT_CARD);
            }
            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * This function is used to update prescriber
     *
     * @param orderId
     * @param prescriberName
     * @param prescriberPhone
     * @param prescriberNPI
     * @param securityToken
     * @param notificationMsgId
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/updatePrescriberWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePrescriber(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "prescriberName", required = false) String prescriberName,
            @RequestParam(value = "prescriberPhone", required = false) String prescriberPhone,
            @RequestParam(value = "prescriberNPI", required = false) String prescriberNPI,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {

        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            Order order = (Order) patientProfileService.getObjectById(new Order(), orderId);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Prescriber can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.updatePrescriber(orderId, prescriberName, prescriberPhone, prescriberNPI);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/acceptLowerCostWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object acceptLowerCost(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {
        logger.info("orderId# " + orderId + " securityToken# " + securityToken + " notificationMsgId# " + notificationMsgId);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            //PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                    Constants.PATIENT_RESPONSES.ACCEPTED_LOWER_COST);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Problem Occurred " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

//        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/acceptLowerCostLaterWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object acceptLowerCostLater(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                    Constants.PATIENT_RESPONSES.ACCEPT_LOWER_COST_LATER);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Problem Occurred " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

//        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/preAuthorizeRefillWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object preAuthorizeRefill(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId
    ) throws IOException, ParseException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            //PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                    Constants.PATIENT_RESPONSES.PRE_AUTHORIZE_REFILL);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Problem Occurred " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

//        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/distcontinueFutureMessagesWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object distcontinueFutureMessages(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            //PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                    Constants.PATIENT_RESPONSES.DISCONTINUE_FUTURE_MESSAGES);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Problem Occurred " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

//        return null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/continueWaitWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object continueWait(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            //PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
            if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                    || orderStatusName.equalsIgnoreCase("Cancelled")) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
            isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                    Constants.PATIENT_RESPONSES.CONTINUE_WAIT);

            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Problem Occurred " + e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

//        return null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value = "/changeOrderStatusWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object changeOrderStatus(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "orderStatusId", required = false) Integer orderStatusId,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {

        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;

            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (CommonUtil.isNullOrEmpty(orderId)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order id is null");
                return objectMapper(objectMapper, jsonResponse);
            }
            int oderIdSafeInteger = AppUtil.getSafeInt(orderId, 0);
            String commentsSafeString = AppUtil.getSafeStr(comments, "");
            OrderStatusDTO dto = this.patientProfileService.getOrderStatusDTO(orderId);
            String orderStatusName = dto != null ? AppUtil.getSafeStr(dto.getName(), "") : "";
            if (dto != null) {
                if (Objects.equals(dto.getId(), orderStatusId)) {
                    jsonResponse.setErrorMessage("Status is alreay " + dto.getName());
                    return objectMapper(objectMapper, jsonResponse);
                }
            }
            boolean IsEditableOrder = CommonUtil.isEditableOrder(orderStatusName);
            if (!IsEditableOrder) {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Status can't be update now since its status is " + orderStatusName);
                return objectMapper(objectMapper, jsonResponse);
            }
//            if (orderStatusName.equalsIgnoreCase("Fill As Cash")) {
//                jsonResponse.setErrorMessage("Status already " + orderStatusName);
//                return objectMapper(objectMapper, jsonResponse);
//            }
            if (orderStatusId == 12) {
                isSaved = patientProfileService.fillAsCash(oderIdSafeInteger, 0, orderStatusId, commentsSafeString, paymentMode);
            } else {
                isSaved = consumerRegistrationService.updateOrderStatusByPatient(oderIdSafeInteger, orderStatusId);
            }
            if (isSaved == 1) {
                isPatientMsgResponse(notificationMsgId);
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been updated successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/getCopayCardsByOrderIdWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object getCopayCardsByOrderIdWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "orderId", required = false) String orderId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOs = patientProfileService.getCoPayCardsListByOrderId(orderId, patientProfile.getId());
        if (!CommonUtil.isNullOrEmpty(coPayCardDetailsDTOs)) {
            jsonResponse.setData(coPayCardDetailsDTOs);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            jsonResponse.setData(coPayCardDetailsDTOs);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("There is no record found.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    ///////////////////////////////////////////////
    @RequestMapping(value = "/getAvailableCopayCardsWs", produces = "application/json")
    public @ResponseBody
    Object getAvailableCopayCardsWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "orderId", required = false) String orderId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        List<CoPayCardDetailsDTO> coPayCardDetailsDTOs = patientProfileService.getAvailableCoPayCards(patientProfile.getId());
        if (!CommonUtil.isNullOrEmpty(coPayCardDetailsDTOs)) {
            jsonResponse.setData(coPayCardDetailsDTOs);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            jsonResponse.setData(coPayCardDetailsDTOs);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("There is no record found.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    //////////////////////////////////////////////
    @RequestMapping(value = "/updateMFRWithOrderWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateMFRWithOrder(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "orderId", required = false) String orderId, @RequestParam(value = "copayCardDictionary", required = false) String copayCardDictionary) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("securityToken is:: " + securityToken + " orderId is :: " + orderId + " copayCardDictionary is :: " + copayCardDictionary);
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(orderId)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("OrderId is null or empty.");
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        logger.info("PatientProfile id is:: " + patientProfile.getId());
        if (patientProfileService.updateMFRWithOrderWs(orderId, copayCardDictionary)) {
            jsonResponse.setData(patientProfileService.getCoPayCardsListByOrderId(orderId, patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Record has been updated successfully.");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Please attach copay card.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    @RequestMapping(value = "/removeMFRCardWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object removeMFRWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "mfrId", required = false) Long id) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(id)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Mfr Id is null");
            return objectMapper(objectMapper, jsonResponse);
        }
        if (patientProfileService.removeMFRCardById(id)) {
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("Record has been removed successfully.");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is some problem to remove record.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    @RequestMapping(value = "/updateDeliveryPerformanceWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateDeliveryPerformance(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "addressId", required = false) String addressId,
            @RequestParam(value = "preferenceId", required = false) String deliveryPrefId,
            @RequestHeader(value = "securityToken", required = false) String securityToken, String paymentMode) throws IOException, ParseException, Exception {
        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("Profile id is: " + patientProfile.getId());
                ///////////////////////////////////////////////////////////////////
                PatientDeliveryAddress address = patientProfileService.getPatientDeliveryAddressById(AppUtil.getSafeInt(addressId, 0));
                String fee = "0.0";
                BigDecimal feeNumeric = BigDecimal.ZERO;
                String prefName = "";
                DeliveryPreferences prefDetails = this.patientProfileService.getDeliveryPreferenceById(AppUtil.getSafeInt(deliveryPrefId, 0));
                if (prefDetails != null) {
                    prefName = prefDetails.getName();
                }
                if (AppUtil.getSafeStr(prefName, "").contains("2nd Day")) {
                    fee = "0.0";
                } else if (address != null) {
                    fee = patientProfileService.getZipCodeCalculations(address.getZip(), patientProfile.getId(), AppUtil.getSafeInt(deliveryPrefId, 0));
                    feeNumeric = CommonUtil.getStrToBigDecimal(fee);
                }

                String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);
                boolean IsEditableOrder = CommonUtil.isEditableOrder(orderStatusName);
                if (IsEditableOrder) {
                    jsonResponse.setErrorMessage("Status can't be update now since its status is " + orderStatusName);
                    return objectMapper(objectMapper, jsonResponse);
                }

                isSaved = patientProfileService.updateDeleiveryPreferance(orderId, fee, addressId, deliveryPrefId);

                if (isSaved == 1) {
                    jsonResponse.setData(1);//patientPaymentInfo);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Record has been updated successfully.");
                } else {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("There is problem to update record.");
                }
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Exception:: updateDeliveryPerformance:: ", e);
        }
        return null;

    }

    @RequestMapping(value = "/updateOrderDeliveryTimeWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateDeliveryTime(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "dprefId", required = false) Integer dprefId,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "toTime", required = false) String deliveryTo,
            @RequestParam(value = "fromTime", required = false) String deliveryFrom,
            @RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException, ParseException, Exception {
        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;

            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("Profile id is: " + patientProfile.getId());
                ///////////////////////////////////////////////////////////////////

                String orderStatusName = this.patientProfileService.getOrderStatusName(orderId);

                if (!orderStatusName.equalsIgnoreCase("Ready to Fill") && !orderStatusName.equalsIgnoreCase("Waiting Pt Response")) {
                    jsonResponse.setErrorMessage("Status can't be update now since its status is " + orderStatusName);
                    return objectMapper(objectMapper, jsonResponse);
                }

                isSaved = patientProfileService.updateDateTime(orderId, dprefId, date, deliveryTo, deliveryFrom);

                if (isSaved == 1) {
                    jsonResponse.setData(1);//patientPaymentInfo);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Record has been updated successfully.");
                } else {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("There is problem to update record.");
                }
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Exception::updateDeliveryTime ", e);
        }
        return null;

    }

    @RequestMapping(value = "/stopRefillReminderWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object stockReminder(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException, ParseException, Exception {
        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;

            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                logger.info("Profile id is: " + patientProfile.getId());
                ///////////////////////////////////////////////////////////////////

                isSaved = patientProfileService.updatestopReminder(orderId, 1);

                if (isSaved == 1) {
                    isPatientMsgResponse(notificationMsgId);
                    jsonResponse.setData(1);//patientPaymentInfo);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Record has been updated successfully.");
                } else {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("There is problem to update record.");
                }
                return objectMapper(objectMapper, jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Exception::stockReminder ", e);
        }
        return null;

    }

    @RequestMapping(value = "/downloadYearEndStatementWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object downloadYearEndStatement(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "yearEndStatementPdfFile", required = false) String yearEndStatementPdfFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("securityToken:: " + securityToken + " yearEndStatementPdfFile:: " + yearEndStatementPdfFile);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        if (CommonUtil.isNullOrEmpty(yearEndStatementPdfFile)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("File name is null.");
            return objectMapper(objectMapper, jsonResponse);
        }
        if (!CommonUtil.isFileExist(PropertiesUtil.getProperty("INSURANCE_CARD_PATH") + yearEndStatementPdfFile)) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("File doest not exist.");
            return objectMapper(objectMapper, jsonResponse);
        }
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + yearEndStatementPdfFile + "\"");
            CommonUtil.downloadFile(PropertiesUtil.getProperty("INSURANCE_CARD_PATH") + yearEndStatementPdfFile, out);
            out.flush();
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * this function is used to send email to patient
     *
     * @param securityToken
     * @param subject
     * @param message
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/sendEmailsWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object sendEmailsWs(@RequestHeader(value = "securityToken", required = false) String securityToken, @RequestParam(value = "subject", required = false) String subject, @RequestParam(value = "message", required = false) String message) throws IOException {
        logger.info("securityToken:: " + securityToken + " subject:: " + subject + " message:: " + message);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!validateToken(securityToken, jsonResponse)) {
            return objectMapper(objectMapper, jsonResponse);
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (CommonUtil.isNotEmpty(patientProfile.getEmailAddress())) {
            String email = EncryptionHandlerUtil.getDecryptedString(patientProfile.getEmailAddress());
            logger.info("Email Address:: " + email);
            if (!validateEmail(email, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            boolean isEmailSend = EmailSenderUtil.send(email, subject, message);
            logger.info("isEmailSend# " + isEmailSend);
            if (isEmailSend) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.EMAIL_SEND_SUCCESS_MESSEGE);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.EMAIL_SEND_FAILURE_MESSEGE);
            }

        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Patient email address is empty.");
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    /**
     * This function is used to get drug information by drugBrandName
     *
     * @param drugName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getCopyDrugLookUpWs", produces = "application/json")
    public @ResponseBody
    Object getCopyDrugLookUpList(@RequestParam(value = "drugBrandName", required = false) String drugName) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("Drug Name is: " + drugName);
        if (CommonUtil.isNullOrEmpty(drugName)) {
            logger.info("Drug Name is empty: " + drugName);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Drug Name is empty.");
            return jsonResponse;
        }
        Set<DrugBrandDTO> list = patientProfileService.getDrugBrandsOnlyList(drugName);
        jsonResponse.setData(list);
        jsonResponse.setErrorCode(1);
        jsonResponse.setErrorMessage(Constants.SUCCESS);
        logger.info("Success");
        return jsonResponse;
    }

    /**
     * This function is used to add Insurance Card information
     *
     * @param securityToken
     * @param cardHolderRelation
     * @param memberId
     * @param cardId
     * @param insuranceProvider
     * @param groupNumber
     * @param planId
     * @param providerPhone
     * @param providerAddress
     * @param expiryDate
     * @param createdOn
     * @param updatedOn
     * @param isPramiry
     * @param effectiveDate
     * @param insuranceCardFront
     * @param insuranceCardBack
     * @param notificationMsgId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addInsuranceCard1Ws", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addInsuranceCard1Ws(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "dependentId", required = false) Integer dependentId,
            @RequestParam(value = "cardHolderRelation", required = false) String cardHolderRelation,
            @RequestParam(value = "memberId", required = false) String memberId,
            @RequestParam(value = "cardId", required = false) Long cardId,
            @RequestParam(value = "insuranceProvider", required = false) String insuranceProvider,
            @RequestParam(value = "groupNumber", required = false) String groupNumber,
            @RequestParam(value = "planId", required = false) String planId,
            @RequestParam(value = "providerPhone", required = false) String providerPhone,
            @RequestParam(value = "providerAddress", required = false) String providerAddress,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "createdOn", required = false) String createdOn,
            @RequestParam(value = "updatedOn", required = false) String updatedOn,
            @RequestParam(value = "isPramiry", required = false) Integer isPramiry,
            @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
            @RequestParam(value = "insuranceFrontCardPath", required = false) MultipartFile insuranceCardFront,
            @RequestParam(value = "insuranceBackCardPath", required = false) MultipartFile insuranceCardBack,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId) throws IOException {
        logger.info("SecurityToken: " + securityToken + " CardHolderRelation: " + cardHolderRelation + " insuranceCardFront: " + insuranceCardFront + " insuranceCardBack: " + insuranceCardBack);
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientInsuranceDetails insuranceDetails = new PatientInsuranceDetails();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            String[] eDate = effectiveDate.split("/");
            effectiveDate = eDate[1] + "-" + eDate[0] + "-01";
            //Long longCardId = Long.parseLong(cardId);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            byte[] decodedFrontCard = null, decodedBackCard = null;
            String imgFcPath = null, imgBcPath = null;
            if (insuranceCardFront != null && (!insuranceCardFront.isEmpty())) {
                //decodedFrontCard = org.apache.commons.codec.binary.Base64.decodeBase64(insuranceCardFront.getBytes());
                imgFcPath = CommonUtil.saveInsuranceCard(insuranceCardFront, patientProfile.getId(), "InsuranceFrontCard");
                logger.info("decodedFrontCard Path: " + imgFcPath);
                if (!CommonUtil.urlAuthorization(imgFcPath)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving card please try again.");
                    return jsonResponse;
                }
            }

            if (insuranceCardBack != null && (!insuranceCardBack.isEmpty())) {
                imgBcPath = CommonUtil.saveInsuranceCard(insuranceCardBack, patientProfile.getId(), "InsuranceBackCard");
                logger.info("decodedBackCard path: " + imgBcPath);
                if (!CommonUtil.urlAuthorization(imgFcPath)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving card please try again.");
                    return jsonResponse;
                }
            }
            if (patientProfileService.addInsuranceCard(patientProfile, cardHolderRelation,
                    decodedFrontCard, decodedBackCard, imgFcPath, imgBcPath, memberId, cardId, planId,
                    effectiveDate, expiryDate, createdOn, updatedOn, insuranceProvider,
                    groupNumber, providerPhone, providerAddress, isPramiry, dependentId)) {
                logger.info("Record has been added successfully.");
                isPatientMsgResponse(notificationMsgId);
                insuranceDetails.setCardHolderRelationship(cardHolderRelation);
                insuranceDetails.setInsuranceFrontCardPath(imgFcPath);
                insuranceDetails.setInsuranceBackCardPath(imgBcPath);
                jsonResponse.setData(insuranceDetails);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception -> getPatientProfileByToken", e);
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update record." + e.getMessage());
        }

        return jsonResponse;
    }

    /**
     *
     * This function is used to add CoPay card information
     *
     * @param securityToken
     * @param manufacturerName
     * @param drugId
     * @param expiryDate
     * @param effectiveDate
     * @param copayFrontCardPath
     * @param copayBackCardPath
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addCoPayCard1Ws", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addCoPayCard1Ws(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "manufacturerName", required = false) String manufacturerName,
            @RequestParam(value = "drugId", required = false) Integer drugId,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
            @RequestParam(value = "copayFrontCardPath", required = false) MultipartFile copayFrontCardPath,
            @RequestParam(value = "copayBackCardPath", required = false) MultipartFile copayBackCardPath) throws IOException {
        logger.info("SecurityToken: " + securityToken + " copayFrontCardPath: " + copayFrontCardPath + " copayBackCardPath: " + copayBackCardPath);
        JsonResponse jsonResponse = new JsonResponse();
        CoPayCardDetails coPayCardDetails = new CoPayCardDetails();

        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        try {
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            byte[] decodedFrontCard = null, decodedBackCard = null;
            String imgFcPath = null, imgBcPath = null;
            if (copayFrontCardPath != null && (!copayFrontCardPath.isEmpty())) {
                imgFcPath = CommonUtil.saveInsuranceCard(copayFrontCardPath, patientProfile.getId(), "copayFrontCardPath");
                logger.info("decodedFrontCard Path: " + imgFcPath);
                if (!CommonUtil.urlAuthorization(imgFcPath)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving card please try again.");
                    return jsonResponse;
                }
            }

            if (copayBackCardPath != null && (!copayBackCardPath.isEmpty())) {
                imgBcPath = CommonUtil.saveInsuranceCard(copayBackCardPath, patientProfile.getId(), "copayBackCardPath");
                logger.info("decodedBackCard path: " + imgBcPath);
                if (!CommonUtil.urlAuthorization(imgBcPath)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving card please try again.");
                    return jsonResponse;
                }
            }
            if (patientProfileService.addCopayCard(patientProfile, decodedFrontCard, decodedBackCard,
                    imgFcPath, imgBcPath, effectiveDate, expiryDate, manufacturerName, drugId)) {
                logger.info("Record has been added successfully.");
                coPayCardDetails.setCopayFrontCardPath(imgFcPath);
                coPayCardDetails.setCopayBackCardPath(imgBcPath);
                jsonResponse.setData(coPayCardDetails);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
            logger.error("Exception -> getPatientProfileByToken", e);
        }

        return jsonResponse;
    }

    /**
     * @author arsalan.ahmad
     * @param securityToken
     * @param heading
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/loadPatientPreferencesWs", produces = "application/json")
    public @ResponseBody
    Object loadPatientPreferences(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "heading", required = false) String heading) throws IOException {
        logger.info("SecurityToken: " + securityToken + " heading: " + heading);
        JsonResponse jsonResponse = new JsonResponse();
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return jsonResponse;
            }
            List<PatientPreferencesDTO> preferences = patientProfileService.loadPatientPreferences(heading,
                    patientProfileService.getPatientProfileByToken(securityToken).getId());
            if (preferences != null && preferences.size() > 0) {
                jsonResponse.setData(preferences);
                jsonResponse.setErrorCode(1);
                jsonResponse.setTotalRecords(preferences.size());
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to find preferences.");
            }

        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to fetch preferences.");
            logger.error("Exception -> loadPatientPreferences", e);
        }
        return jsonResponse;
    }

    /**
     * @author arsalan.ahmad
     * @param securityToken
     * @param preferenceSettingId
     * @param preferenceValue
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/savePatientPreferenceWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object savePatientPreference(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "preferenceSettingId", required = false) Integer preferenceSettingId, @RequestParam(value = "preferenceValue", required = false) boolean preferenceValue) throws IOException {
        logger.info("SecurityToken: " + securityToken + " PreferenceSettingId: " + preferenceSettingId + " PreferenceValue: " + preferenceValue);
        JsonResponse jsonResponse = new JsonResponse();
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return jsonResponse;
            }
            boolean response = patientProfileService.savePatientPreference(patientProfileService.getPatientProfileByToken(securityToken).getId(), preferenceSettingId, preferenceValue);
            if (response) {
                jsonResponse.setData(response);
                jsonResponse.setErrorCode(1);
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to save preference.");
            }

        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to save preferences.");
            logger.error("Exception -> savePatientPreference", e);
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/deletePendingOrderWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object deletePendingOder(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "orderChainId", required = false) Integer orderChainId) {
        logger.info("SecurityToken: " + securityToken + " orderChainId: " + orderChainId);
        JsonResponse jsonResponse = new JsonResponse();
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return jsonResponse;
            }
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            patientProfileService.deletePendingOrder(patientProfile.getId(), orderChainId, jsonResponse);
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to delete the order record.");
            logger.error("Exception -> deletePendingOrderWs", e);
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/addResponseToNotificationMessageWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addResponseToNotificationMessage(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "responseText", required = false) String responseText, @RequestParam(value = "messageId", required = false) Integer messageId, @RequestParam(value = "orderId", required = false) String orderId) {
        JsonResponse jsonResponse = new JsonResponse();
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                return jsonResponse;
            }
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            boolean result = patientProfileService.addResponseToNotificationMessage(responseText, patientProfile.getId(), messageId, orderId);

            if (result) {
                jsonResponse.setData(result);
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("Response to Notification Messages cannot be added.");
            }
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("Response to Notification Messages cannot be added.");
            logger.error("Exception -> addResponseToNotificationMessage", e);
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/updatePOAWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePOAWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "dependentId", required = false) Integer dependentId,
            @RequestParam(value = "frontPOAImage", required = false) MultipartFile frontPOAImage,
            @RequestParam(value = "backPOAImage", required = false) MultipartFile backPOAImage,
            @RequestParam(value = "expiryDate", required = false) String expiryDate) {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("securityToken is: " + securityToken);
        System.out.println("securityToken is: " + securityToken);
        String dateStr = "";
        String frontPOAImageUrl = "";
        String backPOAImageUrl = "";
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }
            Date date = new Date();
            dateStr = DateUtil.dateToString(date, "yy-MM-dd hh:mm:ss");
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");
            PatientProfile patientProfile = patientProfileService.getPatientProfileBySecurityToken(securityToken);

            String ext = FileUtil.determineImageFormat(frontPOAImage.getBytes());
            frontPOAImageUrl = "Img_" + patientProfile.getId() + "_" + dateStr + "." + ext;

            String extt = FileUtil.determineImageFormat(frontPOAImage.getBytes());
            backPOAImageUrl = "Img_" + patientProfile.getId() + "_" + dateStr + "." + extt;

            if (patientProfile != null && patientProfile.getId() != null) {
                patientProfileService.updateExistingPOA(expiryDate, frontPOAImageUrl, backPOAImageUrl, dependentId);
                jsonResponse.setData(1);
                jsonResponse.setErrorMessage("Data has been updated.");
            }
        } catch (Exception e) {
            jsonResponse.setData(0);
            jsonResponse.setErrorMessage("There is problem to update data.");
            logger.error("Exception -> updatePOAWs", e);
        }
        return jsonResponse;

    }

    @RequestMapping(value = "/addCareTakerWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object addCareTakerWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "adultFirstName", required = false) String adultFirstName,
            @RequestParam(value = "adultLastName", required = false) String adultLastName,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "isApproved", required = false) Boolean isApproved,
            @RequestParam(value = "frontPOAImage", required = false) MultipartFile frontPOAImage,
            @RequestParam(value = "backPOAImage", required = false) MultipartFile backPOAImage,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "allergies", required = false) String allergies,
            @RequestParam(value = "emailAddr", required = false) String emailAddr) {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("securityToken is: " + securityToken + " Gender is:  " + gender);
        System.out.println("securityToken is: " + securityToken);
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }
            if (CommonUtil.isNullOrEmpty(gender)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.GENDER_TYPE_REQ);
                return jsonResponse;
            }
            Date dateOfBirth = DateUtil.stringToDate(dob, "MM/dd/yyyy");
            logger.info("After Format Date Of Birth is: " + dateOfBirth);
            int years = DateUtil.getDiffYears(dateOfBirth, new Date());
            if (years < 18) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.INVALID_AGE);
                return jsonResponse;
            }
            String frontPOAUrl = null, backPOAUrl = null;
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (frontPOAImage != null && (!frontPOAImage.isEmpty())) {
                frontPOAUrl = CommonUtil.saveInsuranceCard(frontPOAImage, patientProfile.getId(), "FrontPOAImage");
                logger.info("decodedFrontCard Path: " + frontPOAUrl);
                if (!CommonUtil.urlAuthorization(frontPOAUrl)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving page 1 power of attorney.please try again.");
                    return jsonResponse;
                }
            }
            if (backPOAImage != null && (!backPOAImage.isEmpty())) {
                backPOAUrl = CommonUtil.saveInsuranceCard(backPOAImage, patientProfile.getId(), "BackPOAImage");
                logger.info("decodedFrontCard Path: " + backPOAUrl);
                if (!CommonUtil.urlAuthorization(backPOAUrl)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving page 2 power of attorney/signature.please try again.");
                    return jsonResponse;
                }
            }
            PatientDependantDTO patientProfileMembers = patientProfileService.addCareTaker(patientProfile.getId(), adultFirstName, adultLastName, expiryDate, state, frontPOAUrl, backPOAUrl, isApproved, gender, dob, allergies, emailAddr);
            if (patientProfileMembers != null && patientProfileMembers.getId() != null) {
                logger.info("patientProfileMembers.getId() " + patientProfileMembers.getId());
                CampaignMessages campaignMessages = patientProfileService.getNotificationMsgs("CareTaker", Constants.PHARMACY_NOTIFICATION);
                if (campaignMessages != null && campaignMessages.getMessageId() != null) {
                    String message = campaignMessages.getSmstext();
                    if (CommonUtil.isNotEmpty(message)) {
                        campaignMessages.setSmstext(message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE))
                                .replace("[request_no]", patientProfileMembers.getId() != null ? AppUtil.getSafeStr(patientProfileMembers.getId().toString(), "") : "")
                                .replace("[SERVICE_REQUESTED]", "CAREGIVER via APP")
                                .replace("[PATIENT_NAME]", patientProfileMembers.getFirstName() + " " + patientProfileMembers.getLastName())
                                .replace("[EXP_POA]", AppUtil.getSafeStr(patientProfileMembers.getExpiryDate(), ""))
                                .replace("[REQUEST_MADE]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE)));
                        patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, patientProfile.getId());
                    } else {
                        logger.info("Msg text is empty.");
                    }
                }
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Record has been added successfully.");
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is problem to add record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to save record.");
            logger.error("Exception:: addCareTakerWs", e);
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/getPatientProfileWs", produces = "application/json")
    public @ResponseBody
    Object getPatientProfileWs(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        logger.info("securityToken is: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }

        LoginDTO patientProfile = patientProfileService.getPatientProfileDetailByToken(securityToken);
        if (patientProfile == null || CommonUtil.isNullOrEmpty(patientProfile.getId())) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Patient info is null.");
            return objectMapper(mapper, jsonResponse);
        }
        jsonResponse.setErrorCode(1);
        jsonResponse.setData(patientProfile);

        return objectMapper(mapper, jsonResponse);
    }

    @RequestMapping(value = "/getPatientProfile2Ws", produces = "application/json")
    public @ResponseBody
    Object getPatientProfile2Ws(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        logger.info("securityToken is: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            PatientProfile profile = new PatientProfile();
            profile.setId(patientProfile.getId());
            profile.setFirstName(patientProfile.getFirstName());
            profile.setLastName(patientProfile.getLastName());
            profile.setMobileNumber(patientProfile.getMobileNumber());
            profile.setEmailAddress(patientProfile.getEmailAddress());
            profile.setSecurityToken(securityToken);
            profile.setDependentCount(patientProfile.getDependentCount());
            profile.setInsCardCount(patientProfile.getInsCardCount());
            jsonResponse.setErrorCode(1);
            jsonResponse.setData(profile);
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Patient info is null.");
        }
        return objectMapper(mapper, jsonResponse);
    }

    @RequestMapping(value = "/msgResponseWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object msgResponseWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "response", required = false) String response,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId,
            @RequestParam(value = "orderId", required = false) String orderId) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper mapper = new ObjectMapper();
        logger.info("securityToken is: " + securityToken);
        if (!validateToken(securityToken, jsonResponse)) {
            return jsonResponse;
        }
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            patientProfileService.saveMessageResponses(patientProfile, orderId, notificationMsgId, response);
            jsonResponse.setErrorCode(1);
            //jsonResponse.setErrorMessage("Record has been added successfully.");
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("Patient info is null.");
        }
        return objectMapper(mapper, jsonResponse);
    }

    @RequestMapping(value = "/archiveCaregiverWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object archiveCaregiverWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "dependentId", required = false) String dependentId) throws IOException, ParseException, Exception {
        logger.info("DependentId is# " + dependentId);
        try {

            JsonResponse jsonResponse = new JsonResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            int isSaved = 0;

            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (CommonUtil.isNullOrEmpty(dependentId)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Dependent id is null");
                return objectMapper(objectMapper, jsonResponse);
            }
            int dependendentIdInt = AppUtil.getSafeInt(dependentId, 0);
            PatientDependantDTO patientDependantDTO = patientProfileService.getPatientProfileMembersById(dependendentIdInt);
            if (patientDependantDTO == null && CommonUtil.isNullOrEmpty(patientDependantDTO.getId())) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("No record found.");
                return objectMapper(objectMapper, jsonResponse);
            }
            logger.info("patientDependantDTO.getArchived()# " + patientDependantDTO.getArchived());
            if (patientDependantDTO.getArchived() != null && patientDependantDTO.getArchived() == 1) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("This POA request has already been cancelled.");
                return objectMapper(objectMapper, jsonResponse);
            }

//            String orderStatusName = dto != null ? AppUtil.getSafeStr(dto.getName(), "") : "";
//            if (dto != null) {
//                if (Objects.equals(dto.getId(), orderStatusId)) {
//                    jsonResponse.setErrorMessage("Status is alreay " + dto.getName());
//                    return objectMapper(objectMapper, jsonResponse);
//                }
//            }
            isSaved = patientProfileService.archivePatientProfileMember(dependendentIdInt);

            if (isSaved == 1) {
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Request has been cancelled successfully.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @RequestMapping(value = "/updateHandDeliveryWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateHandDelivery(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException, ParseException, Exception {

        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            int isSaved = 0;
            if (!validateToken(securityToken, jsonResponse)) {
                return objectMapper(objectMapper, jsonResponse);
            }
            if (CommonUtil.isNullOrEmpty(orderId)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order id is null");
                return objectMapper(objectMapper, jsonResponse);
            }

//            if (orderStatusName.equalsIgnoreCase("Fill As Cash")) {
//                jsonResponse.setErrorMessage("Status already " + orderStatusName);
//                return objectMapper(objectMapper, jsonResponse);
//            }
            isSaved = this.consumerRegistrationService.updateHandDeliveryFlag(orderId);
            if (isSaved == 1) {
                jsonResponse.setData(1);//patientPaymentInfo);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Your request for order shipment is accepted.");
            } else {
                jsonResponse.setData(0);
                jsonResponse.setErrorMessage("There is problem to update the record.");
            }
            return objectMapper(objectMapper, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(0);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }

    }

    @RequestMapping(value = "/updateOrderPrimeryWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePrimeryOrder(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "quantity", required = false) String quantity,
            @RequestParam(value = "paymentId", required = false) Integer paymentId,
            @RequestParam(value = "deliveryPrefernceId", required = false) Integer deliveryPrefernceId,
            @RequestParam(value = "insuranceCardId", required = false) String insuranceCardId,
            @RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException, ParseException, Exception {
        //public Object getObjectById(Object obj, String id)
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        int isSaved = 0;
        if (patientProfile != null && patientProfile.getId() != null) {
            isSaved = patientProfileService.savePrimeryOrders(orderId, paymentId, deliveryPrefernceId);

            if (isSaved == 1) {
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage("Order Updated successfully");
                profile.setSuccessOrFailure(Constants.SUCCESS);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(Constants.ERROR_SAVE_MESSAGE);
                return objectMapper(objectMapper, jsonResponse);
            }

        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);

    }

    ////////////////////////////////////////////////////////////
    @RequestMapping(value = "/generateDeviceToken", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object generateDeviceToken(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "deviceToken", required = false) String deviceToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Security Token: " + securityToken);
        PatientProfile patientProfile = patientProfileService.createDeviceTokenForPatient(securityToken, deviceToken);
        if (patientProfile != null && patientProfile.getId() != null) {
            jsonResponse.setData(patientProfileService.getFilledRxHistory(patientProfile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } else {
            CommonUtil.inValidSecurityToken(jsonResponse);
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    @RequestMapping(value = "/getServerTimeWs", produces = "application/json")
    public @ResponseBody
    Object getServerTime(@RequestHeader(value = "securityToken", required = false) String securityToken) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            logger.info("Security Token: " + securityToken);
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (patientProfile != null && patientProfile.getId() != null) {
                jsonResponse.setData(DateUtil.dateToString(new Date(), "MM/dd/yyyy hh:mm:ss"));
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS);
            } else {
                CommonUtil.inValidSecurityToken(jsonResponse);
            }
            return objectMapper(objectMapper, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setData(e.getMessage());
            return objectMapper(objectMapper, jsonResponse);
        }
    }

    ////////////////////////////////////////////////////////////
    @RequestMapping(value = "/updateOrderImagesWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateOrderImagesWs(@RequestHeader(value = "securityToken") String securityToken,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "drugImg", required = false) MultipartFile drugImg,
            @RequestParam(value = "drugImgs", required = false) List<MultipartFile> drugImgList,
            @RequestParam(value = "notificationMsgId", required = false) Integer notificationMsgId,
            HttpServletRequest req) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("OrderId:: " + orderId + "Video" + video + " drugImgList Size is:: " + drugImgList.size());
        System.out.println("OrderId:: " + orderId + "Video" + video + " drugImgList Size is:: " + drugImgList.size());
        PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
        String dateStr = "";
        String completeName = "";
        int isSaved = 0;
        try {
            Date date = new Date();
            dateStr = DateUtil.dateToString(date, "yy-MM-dd hh:mm:ss");
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");

            if (patientProfile != null && patientProfile.getId() != null) {
                /////////////////////////////////////////////////
                String orderStatusName = AppUtil.getSafeStr(this.patientProfileService.getOrderStatusName(orderId), "");
                if (orderStatusName.equalsIgnoreCase("Shipped") || orderStatusName.equalsIgnoreCase("Pickup At Pharmacy") || orderStatusName.equalsIgnoreCase("Filled") || orderStatusName.equalsIgnoreCase("DELIVERY")
                        || orderStatusName.equalsIgnoreCase("Cancelled")) {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("Information can't be updated now since its status is " + orderStatusName);
                    return objectMapper(objectMapper, jsonResponse);
                }
                /////////////////////////////////////////////////
                patientProfileService.deleteDrugImagesByOrderId(orderId);

                String videoPath = null, drugImagPath = null;
                String webappRoot = servletContext.getRealPath("/");
                String relativeFolder = File.separator + "resources" + File.separator
                        + "noinsurancecard" + File.separator;
                List<OrderTransferImages> orderTransferImagesList = new ArrayList<>();
                if (video != null && !video.isEmpty()) {
                    if (video.getBytes() != null) {
                        logger.info("video Format: " + video.getContentType());
                        String contentType = video.getContentType();
                        String[] contentArr = contentType.split("/");
                        String ext = "mp4"; //FileUtil.determineImageFormat(video.getBytes());
                        if (contentArr != null && contentArr.length > 1) {
                            ext = contentArr[1];
                        }
                        completeName = "Vid_" + patientProfile.getId() + "_" + dateStr + "." + ext;
                        File file = new File(webappRoot + relativeFolder + completeName);
                        videoPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
                        //                    videoPath = Constants.INSURANCE_CARD_PATH + video.getOriginalFilename() + patientProfile.getId() + "." + video.getContentType();
                        logger.info("Complete video Path: " + videoPath);
                        //                    FileCopyUtils.copy(video.getBytes(), new File(Constants.INSURANCE_CARD_PATH + video.getOriginalFilename()));
                        FileCopyUtils.copy(video.getBytes(),
                                new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
                        CommonUtil.executeCommand(Constants.COMMAND);
                        completeName = videoPath;
                    }
                } else if (!CommonUtil.isNullOrEmpty(drugImgList)) {
                    completeName = uploadOrderTransferImages(drugImgList, completeName, patientProfile, dateStr, orderTransferImagesList);
                }
                if (drugImg != null && drugImg.getBytes() != null) {
                    logger.info("Drug Image Format: " + drugImg.getContentType());

                    String ext = FileUtil.determineImageFormat(drugImg.getBytes());
                    completeName = "Img_" + patientProfile.getId() + "_" + dateStr + "." + ext;
                    drugImagPath = PropertiesUtil.getProperty("INSURANCE_CARD_URL") + "orderimages/" + completeName;
                    logger.info("Complete Drug Image Path: " + drugImagPath);

                    FileCopyUtils.copy(drugImg.getBytes(),
                            new File(PropertiesUtil.getProperty("ORDER_IMAGES_CARD_PATH") + completeName));
                    CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND"));
                    completeName = drugImagPath;
                }

                if (video != null) {
                    saveRxTransfer(orderId, completeName, jsonResponse, "", orderTransferImagesList);
                } else {
                    saveRxTransfer(orderId, "", jsonResponse, completeName, orderTransferImagesList);
                }

                isSaved = consumerRegistrationService.savePatientResponseAttributesForOrder(orderId,
                        Constants.PATIENT_RESPONSES.UPDATE_IMAGE_VIDEO);

                if (isSaved == 1) {
                    isPatientMsgResponse(notificationMsgId);
                    jsonResponse.setData(1);//patientPaymentInfo);
                    jsonResponse.setErrorCode(1);
                    jsonResponse.setErrorMessage("Record has been updated successfully.");
                } else {
                    jsonResponse.setData(0);
                    jsonResponse.setErrorMessage("There is problem to update record.");
                }

            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage(PropertiesUtil.getProperty("INVALID_SECURITY_TOKEN"));
                logger.info(PropertiesUtil.getProperty("INVALID_SECURITY_TOKEN"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(e.getMessage());
            return jsonResponse;
        }
        return objectMapper(objectMapper, jsonResponse);
    }

    private void isPatientMsgResponse(Integer notificationMsgId) {
        if (!CommonUtil.isNullOrEmpty(notificationMsgId)) {
            patientProfileService.updateNotificationMessages(notificationMsgId, Boolean.TRUE);
        }
    }

    private void saveRxTransfer(String orderId, String videoPath, JsonResponse jsonResponse, String drugImg, List<OrderTransferImages> orderTransferImageses) {

        OrderChain orderChain = patientProfileService.saveRxOrderChainForTransferReplaceImages(orderId, videoPath, drugImg, orderTransferImageses);
        //        TransferRequest transferRequest = patientProfileService.saveRxTransferRequestForReplaceImages( orderId,  videoPath, drugImg);
        if (orderChain != null) {
            logger.info("chain Id is:: " + orderChain.getId());
            RewardPoints rewardPoints = patientProfileService.getRxTransferPoints();
            if (rewardPoints != null && rewardPoints.getId() != null) {
                jsonResponse.setErrorCode(1);
                rewardPoints.setTransferId(0);
                rewardPoints.setOrderChainId(orderChain.getId());
                jsonResponse.setData(rewardPoints);
                jsonResponse.setErrorMessage("Rx Transfer successful");
            } else {
                jsonResponse.setErrorCode(1);
                //                transferRequest.setTransferId(transferRequest.getId());
                jsonResponse.setData(orderChain);
                jsonResponse.setErrorMessage("Rx Transfer successful");
                //                logger.info("Rx Transfer successful: " + transferRequest.getId());
            }
        } else {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage(Constants.ERROR_SAVE_RECORD);
            logger.info(Constants.ERROR_SAVE_RECORD);
        }
    }

    ////////////////////////////////////////////////////////////
    /**
     *
     * @param securityToken
     * @param paymentInfo
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/paymentInformation1Ws", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object paymentInfo1(@RequestHeader(value = "securityToken") String securityToken, @RequestBody String paymentInfo) throws IOException {
        PatientProfile profile = new PatientProfile();
        JsonResponse jsonResponse = new JsonResponse();
        try {
            PatientPaymentDTO patientPaymentInfo = new Gson().fromJson(paymentInfo, PatientPaymentDTO.class);
            logger.info("profileId: " + patientPaymentInfo.getProfileId() + " Expiry date is: " + patientPaymentInfo.getExpiryDate() + " cardType: " + patientPaymentInfo.getCardType() + " CardHolderName: " + patientPaymentInfo.getCardHolderName() + " CardNumber: " + patientPaymentInfo.getCardNumber() + " Cvv number: " + patientPaymentInfo.getCvvNumber() + " Address: " + patientPaymentInfo.getAddress() + " appt: " + patientPaymentInfo.getApartment() + " DefaultCard: " + patientPaymentInfo.getDefaultCard() + " securityToken is: " + securityToken);
            logger.info(" City " + patientPaymentInfo.getCity() + " stateId " + patientPaymentInfo.getStateId() + " zip: " + patientPaymentInfo.getZip());
            System.out.println("profileId: " + patientPaymentInfo.getProfileId() + " Expiry date is: " + patientPaymentInfo.getExpiryDate() + " cardType: " + patientPaymentInfo.getCardType() + " CardHolderName: " + patientPaymentInfo.getCardHolderName() + " CardNumber: " + patientPaymentInfo.getCardNumber() + " Cvv number: " + patientPaymentInfo.getCvvNumber() + " Address: " + patientPaymentInfo.getAddress() + " appt: " + patientPaymentInfo.getApartment() + " DefaultCard: " + patientPaymentInfo.getDefaultCard() + " securityToken is: " + securityToken);
            System.out.println(" City " + patientPaymentInfo.getCity() + " stateId " + patientPaymentInfo.getStateId() + " zip: " + patientPaymentInfo.getZip());
            if (!validatePaymentInfo(patientPaymentInfo.getProfileId(), jsonResponse, patientPaymentInfo.getCardHolderName(), patientPaymentInfo.getCardNumber(), patientPaymentInfo.getCvvNumber(), patientPaymentInfo.getExpiryDate(), patientPaymentInfo.getCardType(), securityToken)) {
                return jsonResponse;
            }
            if (patientPaymentInfo.getStateId() != null) {
                patientPaymentInfo.setState(String.valueOf(patientPaymentInfo.getStateId()));
            }
            if (securityToken != null) {
                profile = patientProfileService.getPatientProfileByToken(securityToken);
            } else {
                profile = patientProfileService.getPatientProfileById(patientPaymentInfo.getProfileId());
            }
            if (!isProfileInfoEmpty(profile, jsonResponse)) {
                return jsonResponse;
            }
            if (!validCardTypeLength(patientPaymentInfo.getCardType(), patientPaymentInfo.getCvvNumber(), jsonResponse)) {
                return jsonResponse;
            }
            if (!validateDoDirectPayment(patientPaymentInfo.getCardType(), patientPaymentInfo.getCardNumber(), patientPaymentInfo.getExpiryDate(), patientPaymentInfo.getCardHolderName(), patientPaymentInfo.getCvvNumber(), jsonResponse)) {
                return jsonResponse;
            }
            if (!validateBillingAddressField(patientPaymentInfo.getAddress(), jsonResponse, patientPaymentInfo.getApartment(), patientPaymentInfo.getCity(), patientPaymentInfo.getState(), patientPaymentInfo.getZip())) {
                return jsonResponse;
            }
            if (patientPaymentInfo.getDefaultCard().equalsIgnoreCase(Constants.NO)) {
                if (!patientProfileService.isDefaultPaymentInfo(profile.getId())) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Please Make it your default payment info.");
                    return jsonResponse;
                }
            }

            PatientAddress billingAddress = new PatientAddress();
            //save billing address
            if (patientPaymentInfo.getAddress() != null && !"undefined".equals(patientPaymentInfo.getAddress())) {
                logger.info("save address: " + patientPaymentInfo.getAddress());

                billingAddress = patientProfileService.saveAddress(patientPaymentInfo.getAddress(), patientPaymentInfo.getApartment(), "", patientPaymentInfo.getCity(), patientPaymentInfo.getState(), patientPaymentInfo.getZip(), null, null);
                profile.setBillingAddress(billingAddress);
            } else {
                PatientDeliveryAddress patientDeliveryAddress = patientProfileService.getPatientDeliveryDefaultAddress(patientPaymentInfo.getProfileId());
                if (patientDeliveryAddress != null && patientDeliveryAddress.getId() != null && patientDeliveryAddress.getDefaultAddress().equalsIgnoreCase("Yes")) {

                    billingAddress.setAddress(patientDeliveryAddress.getAddress());
                    billingAddress.setApartment(patientDeliveryAddress.getApartment());
                    billingAddress.setAddressDescription(patientDeliveryAddress.getDescription());
                    billingAddress.setCity(patientDeliveryAddress.getCity());
                    if (patientDeliveryAddress.getState() != null && patientDeliveryAddress.getState().getId() != null) {
                        billingAddress.setStateId(patientDeliveryAddress.getState().getId().shortValue());
                    }
                    billingAddress.setZip(patientDeliveryAddress.getZip());
                    billingAddress.setAddressType(patientDeliveryAddress.getAddressType());
                    profile.setBillingAddress(billingAddress);
                }
            }
            //save payment information
            PatientPaymentInfo payment = patientProfileService.savePaymentInfo(profile.getId(), patientPaymentInfo.getCardHolderName(), patientPaymentInfo.getCardNumber(), patientPaymentInfo.getCvvNumber(), patientPaymentInfo.getExpiryDate(), patientPaymentInfo.getCardType(), patientPaymentInfo.getDefaultCard());
            payment.setBillingAddress(billingAddress);
            patientProfileService.update(payment);
            logger.info("Expiry date is: " + payment.getExpiryDate());
            logger.info("save profile address");
            //save profile
            patientProfileService.savePatientInfo(profile);
            jsonResponse.setData(patientProfileService.getPatientPaymentInfo(payment.getId(), profile.getId()));
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            logger.error("Exception", ex);
            profile.setDescription("There is problem with saving payment information.");
            profile.setSuccessOrFailure(Constants.FAILURE);
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem with saving payment information. " + ex.getMessage());
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/updatePasswordWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updatePassword() throws IOException {
//       PharmacyUser phUser = new PharmacyUser();
        JsonResponse jsonResponse = new JsonResponse();
        try {
            consumerRegistrationService.getpasswordByPharmacyUserList();
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } catch (Exception e) {
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("fail");
            jsonResponse.setErrorMessage(e.getMessage());
        }
        return jsonResponse;
    }

    @RequestMapping(value = "/updateCareTakerImageWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateCareTakerImge(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "careTakerId", required = false) Integer careTakerId,
            @RequestParam(value = "frontPOAImage", required = false) MultipartFile frontPOAImage,
            @RequestParam(value = "backPOAImage", required = false) MultipartFile backPOAImage) throws IOException {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("SecurityToken is: " + securityToken + " CareTakerId is: " + careTakerId);
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }
            if (CommonUtil.isNullOrEmpty(careTakerId)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Care Taker ID Required");
                return jsonResponse;
            }
            PatientDependantDTO dependantDTO = patientProfileService.getPatientProfileMembersById(careTakerId);
            if (dependantDTO == null || CommonUtil.isNullOrEmpty(dependantDTO.getId())) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("No care taker record found against this id " + careTakerId);
                return jsonResponse;
            }

            logger.info("patientDependantDTO.getArchived()# " + dependantDTO.getArchived());
            if (dependantDTO.getArchived() != null && dependantDTO.getArchived() == 1) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("This POA request has already been cancelled.");
                return jsonResponse;
            }

            String frontPOAImg = null, backPOAimg = null;
            PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            if (frontPOAImage != null && (!frontPOAImage.isEmpty())) {
                frontPOAImg = CommonUtil.saveInsuranceCard(frontPOAImage, patientProfile.getId(), "FrontPOAImage");
                logger.info("FrontPOAImage Path: " + frontPOAImg);
                if (!CommonUtil.urlAuthorization(frontPOAImg)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving page 1 power of attorney.please try again.");
                    return jsonResponse;
                }
            }
            if (backPOAImage != null && (!backPOAImage.isEmpty())) {
                backPOAimg = CommonUtil.saveInsuranceCard(backPOAImage, patientProfile.getId(), "BackPOAImage");
                logger.info("BackPOAimg Path: " + backPOAimg);
                if (CommonUtil.isNotEmpty(backPOAimg) && !CommonUtil.urlAuthorization(frontPOAImg)) {
                    jsonResponse.setErrorCode(0);
                    jsonResponse.setErrorMessage("Error in saving page 2 power of attorney.please try again.");
                    return jsonResponse;
                }
            }

            patientProfileService.updateCareTaker(careTakerId, frontPOAImg, backPOAimg);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("updtae record Successfully");

        } catch (IOException e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
            logger.error("Exception:: updateCareTakerImageWs", e);

        }
        return jsonResponse;

    }

    /////////////////////////////////////////////////////////////
    @RequestMapping(value = "/updateCareTakerExpiryWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object updateCareTakerExpiryWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "careTakerId", required = false) Integer careTakerId) {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("securityToken is: " + securityToken + " careTakerId " + careTakerId);
//        System.out.println("securityToken is: " + securityToken);
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }

            //PatientProfile patientProfile = patientProfileService.getPatientProfileByToken(securityToken);
            PatientDependantDTO dependantDTO = patientProfileService.getPatientProfileMembersById(careTakerId);
            if (dependantDTO == null || CommonUtil.isNullOrEmpty(dependantDTO.getId())) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("No care taker record found against this id " + careTakerId);
                return jsonResponse;
            }
            if (dependantDTO.getArchived() != null
                    && dependantDTO.getArchived() == 1) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("This POA request has already been cancelled.");
                return jsonResponse;
            }
            patientProfileService.updateCareTakerExpiry(careTakerId, expiryDate);
            jsonResponse.setErrorCode(1);
            jsonResponse.setErrorMessage("updtae record Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to update record.");
            logger.error("Exception:: updateCareTakerImageWs", e);

        }
        return jsonResponse;
    }

    ////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/populateReadyToDeliveryRxWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object populateReadyToDeliveryRxWs(@RequestHeader(value = "securityToken", required = false) String securityToken) {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("securityToken is: " + securityToken);
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }
            List<OrderDetailDTO> listOfReadyToDeliveryOrders = consumerRegistrationService.getSameDayAndFilledStatusOrdersByPatientId(jsonResponse.getPatientId());
            jsonResponse.setErrorCode(1);
            jsonResponse.setData(listOfReadyToDeliveryOrders);
            jsonResponse.setErrorMessage(Constants.SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem....");
            logger.error("Exception:: populateReadyToDeliveryRxWs", e);
        }

        return jsonResponse;
    }

    @RequestMapping(value = "/confirmReadyToDeliveryRxWs", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    Object confirmReadyToDeliveryRxWs(@RequestHeader(value = "securityToken", required = false) String securityToken,
            @RequestParam(value = "data", required = false) String lstOrder, @RequestParam(value = "signature", required = false) MultipartFile signature) {
        JsonResponse jsonResponse = new JsonResponse();
        logger.info("securityToken is: " + securityToken + " lstOrder# " + lstOrder);
        try {
            if (!validateToken(securityToken, jsonResponse)) {
                System.out.println("securityToken not validated");
                return jsonResponse;
            }
            if (CommonUtil.isNullOrEmpty(lstOrder)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order list is empty");
                return jsonResponse;
            }
            JSONObject jsonObj = new JSONObject(lstOrder);

            JSONArray jsonArray = jsonObj.getJSONArray("lstOrder");

            //then get the type for list and parse using gson as
            Type listType = new TypeToken<List<ReadyToDeliveryRxDTO>>() {
            }.getType();
            List<ReadyToDeliveryRxDTO> lstReadyToDeliveryRx = new Gson().fromJson(jsonArray.toString(), listType);
            if (CommonUtil.isNullOrEmpty(lstReadyToDeliveryRx)) {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("Order list is null");
                logger.info("Order list is null");
                return jsonResponse;
            }
            boolean isSaved = patientProfileService.isConfirmReadyToDeliveryRxOrders(lstReadyToDeliveryRx, signature);
            if (isSaved) {
                jsonResponse.setData(1);
                jsonResponse.setErrorCode(1);
                jsonResponse.setErrorMessage(Constants.SUCCESS_MESSEGE);
            } else {
                jsonResponse.setErrorCode(0);
                jsonResponse.setErrorMessage("There is problem to saved record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.setErrorCode(0);
            jsonResponse.setErrorMessage("There is problem to saved record..");
            logger.error("Exception:: confirmReadyToDeliveryRxWs", e);
        }
        return jsonResponse;
    }
}
////////////////////////////////////////////////////////////////
