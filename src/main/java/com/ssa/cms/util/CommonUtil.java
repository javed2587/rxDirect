/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.ssa.cms.common.Constants;
import com.ssa.cms.dao.PatientProfileDAO;
import com.ssa.cms.dto.DataTablesTO;
import com.ssa.cms.dto.LoginDTO;
import com.ssa.cms.dto.OrderDetailDTO;
import com.ssa.cms.dto.PatientDependantDTO;
import com.ssa.cms.enums.ProcessedOrderStatusEnum;
import com.ssa.cms.model.DeliveryPreferences;
import com.ssa.cms.model.DrugBasic;
import com.ssa.cms.model.DrugDetail;
import com.ssa.cms.model.DrugGeneric;
import com.ssa.cms.model.JsonResponse;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.OrdersPtMessage;
import com.ssa.cms.model.PatientProfile;
import com.ssa.cms.model.PatientProfileMembers;
import com.ssa.cms.model.PharmacyUser;
import com.ssa.cms.service.DrugService;
import com.ssa.cms.service.PatientProfileService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author mzubair
 */
public class CommonUtil {
    
    private static final Log logger = LogFactory.getLog(CommonUtil.class.getName());
    
    public static boolean isNullOrEmpty(String value) {
        boolean isNullOrEmpty = false;
        if (value == null || value.trim().equalsIgnoreCase("") || value.trim().equalsIgnoreCase("null") || value.equalsIgnoreCase(Constants.UNDEFINED)) {
            isNullOrEmpty = true;
        }
        return isNullOrEmpty;
    }
    
    public static boolean isNotEmpty(String value) {
        return value != null && value.trim().length() > 0 && !value.equalsIgnoreCase(Constants.UNDEFINED) && !value.equalsIgnoreCase("null");
    }
    
    public static boolean isNullDate(Date date) {
        boolean isNullDate = false;
        try {
            if (date == null) {
                isNullDate = true;
            }
            return isNullDate;
        } catch (Exception e) {
            logger.error("#Exception" + e.getMessage());
        }
        return false;
    }
    
    public static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }
    
    public static String executeCommand(String cmd) {
        String line = null;
        try {
            List<String> command = new ArrayList<>();
            command.add("/bin/bash");
            command.add("-c");
            command.add(cmd);
            logger.info("Decrypting Files");
            ProcessBuilder p = new ProcessBuilder(command);
            Process process = p.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            line = br.readLine();
            logger.info("line:: " + line);
        } catch (Exception e) {
            logger.error("Exception:: ", e);
        }
        return line;
    }
    
    public static String getDecimalFormat(Double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
    public static final Comparator<NotificationMessages> byDate = (NotificationMessages ord1, NotificationMessages ord2) -> {
        Date d1 = ord1.getCreatedOn();
        Date d2 = ord2.getCreatedOn();
        return (d1.getTime() > d2.getTime() ? -1 : 1);     //descending
        //  return (d1.getTime() > d2.getTime() ? 1 : -1);     //ascending
    };
    
    public static String toJson(DataTablesTO<?> dataTable) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataTable);
    }
    
    public static BigDecimal getStrToBigDecimal(String val) {
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
        
    }
    
    public static void inValidSecurityToken(JsonResponse jsonResponse) {
        logger.info("In valid user token.");
        jsonResponse.setErrorCode(0);
        jsonResponse.setErrorMessage(Constants.INVALID_SECURITY_TOKEN);
    }

    ///////////////////////////////////////////////////////////////////////////////
    public static String postDataToPhone(String msg, String json, int patientId, String urlStr, boolean isSecure) throws IOException {
        URL url = new URL(urlStr);
        //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        //conn.setRequestProperty("Authorization","Bearer "+token);

//    	   String json = "{\"from\": \"12489108874\",\"to\": [\"78600\"],\"body\": \"Hi\"}";
        //String json ="{\"from\": \"78600\",\"to\": [\"17345642666\"],\"body\": \"deployed\",\"type\": \"mt_text\",\"delivery_report\": \"none\"}"; //"{from\": \"78600\",\"to\": [\"12489108874\"],\"body\": \"Hi\"}";
        //String json ="{\"from\": \""+shortCode+"\",\"to\": [\""+phone+"\"],\"body\": \""+msg+"\",\"type\": \"mt_text\",\"delivery_report\": \"none\"}"; 
        System.out.println("JSON INPUT " + json);
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        System.out.println("Send Message Request came at " + new Date().toString());
        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            System.out.println(conn.getResponseCode());
            throw new RuntimeException("MESSAGE SENDING MTS Failed : HTTP error code : "
                    + conn.getResponseCode() + " Description " + conn.getResponseMessage());
        }
//    	   BufferedReader br = new BufferedReader(new InputStreamReader(
//    	     (conn.getInputStream())));
//
//    	   String output,jsonStr="";
//    	   System.out.println("Output from MTS SERVICE .... \nResponse Code "+conn.getResponseCode()+" status "+conn.getResponseMessage() );
//    	   jsonStr="Response Code "+conn.getResponseCode()+" status "+conn.getResponseMessage();
//    	   while ((output = br.readLine()) != null) {
//    	    System.out.println(output);
//    	    jsonStr=jsonStr+output;
//    	   }

        conn.disconnect();
        
        System.out.println("********************************************************");
        System.out.println("Successfully sent PUSH message at " + new Date().toString());
        System.out.println("EXITING FROM MTS");
        System.out.println("********************************************************");
        return "Success";
        
    }
    //////////////////////////////////////////////////////////////////////////////

    public static String replaceNotificationMessagesPlaceHolder(String message, Date createdDate) {
        try {
            if (CommonUtil.isNotEmpty(message) && message.contains("RECEIVED_DATE")) {
                message = message.replace("RECEIVED_DATE", CommonUtil.getReceiveDate(createdDate));
            }
        } catch (Exception e) {
            logger.error("Exception:: ", e);
        }
        return message;
    }
    
    public static String getReceiveDate(Date createdDate) throws ParseException {
        String date = DateUtil.getDateDiffFromCurrentDate(createdDate) + " ago (" + DateUtil.formatDate(createdDate, "HH:mma " + Constants.USA_DATE_FORMATE) + ")";
        logger.info("Date:: " + date);
        return date;
    }
    
    public static String replaceOrdersReceiptPlaceHolder(String message, Integer id, Order order, String brandName, String strength, String type, String qty, String ptCopay) throws Exception {
        if (message.contains("ORDER_NUMBER")) {
            message = message.replace("ORDER_NUMBER", "" + id);
        }
        if (message.contains("ORDER_DATE")) {
            message = message.replace("ORDER_DATE", DateUtil.dateToString(order.getCreatedOn(), Constants.USA_DATE_FORMATE));
        }
        if (message.contains("DRUGNAME_STRENGTH_DRUGTYPE")) {
            message = message.replace("DRUGNAME_STRENGTH_DRUGTYPE", brandName + " " + strength + " " + type);
        }
        if (message.contains("[DRUG_QTY]")) {
            message = message.replace("[DRUG_QTY]", qty);
        }
        if (message.contains("INSURANCE_COPAY")) {
            message = message.replace("INSURANCE_COPAY", ptCopay);
        }
        if (message.contains("CARDTYPE")) {
            message = message.replace("CARDTYPE", AppUtil.getSafeStr(order.getCardType(), "").toUpperCase());
        }
        if (message.contains("CARDNUMBER")) {
            String cardNumber = AppUtil.getSafeStr(order.getCardNumber(), "");
            if (cardNumber.length() > 5) {
                message = message.replace("CARDNUMBER", cardNumber.substring(cardNumber.length() - 4));
            }
        }
        if (message.contains("PAYMENT")) {
            if (order.getPayment() != null) {
                message = message.replace("PAYMENT", order.getPayment().toString());
            }
        }
        if (message.contains("DELIVERY_ADDRESS")) {
            message = message.replace("DELIVERY_ADDRESS", AppUtil.getSafeStr(order.getZip(), "") + " " + AppUtil.getSafeStr(order.getCity(), "")
                    + " " + AppUtil.getSafeStr(order.getState(), ""));
        }
        if (message.contains("HANDLING_FEE")) {
            if (order.getHandLingFee() != null) {
                message = message.replace("HANDLING_FEE", order.getHandLingFee().toString());
            }
        }
        if (message.contains("REDEEM_POINTS_COST")) {
            if (order.getRedeemPointsCost() != null) {
                message = message.replace("REDEEM_POINTS_COST", order.getRedeemPointsCost().toString());
            }
        }
        if (message.contains("ADDRESS")) {
            message = message.replace("ADDRESS", AppUtil.getSafeStr(order.getStreetAddress(), ""));
        }
        if (message.contains("APPART")) {
            message = message.replace("APPART", AppUtil.getSafeStr(order.getApartment(), ""));
        }
        if (message.contains("CITY")) {
            message = message.replace("CITY", AppUtil.getSafeStr(order.getCity(), ""));
        }
        if (message.contains("STATE")) {
            message = message.replace("STATE", AppUtil.getSafeStr(order.getState(), ""));
        }
        if (message.contains("ZIP")) {
            message = message.replace("ZIP", AppUtil.getSafeStr(order.getZip(), ""));
        }
        if (message.contains("AUTHORIZE_BTN_NAME")) {
            message = message.replace("AUTHORIZE_BTN_NAME", "AUTHORIZE PAYMENT");
        }
        return message;
    }
    
    public static String getLastIndexOfStr(String format, String chr) {
        return format.substring(format.lastIndexOf(chr) + 1);
    }
    
    public static boolean isEditableOrder(String orderStatusName) {
        return !(orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.SHIPPED) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.FILLED) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.DELIVERY) || orderStatusName.equalsIgnoreCase(Constants.ORDER_STATUS.CANCELLED));
    }
    
    public static boolean isNullOrEmpty(Long value) {
        return value == null || value == 0;
    }
    
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("File does not exist:: " + filePath);
            return false;
        }
        return true;
    }
    
    public static void downloadFile(String filePath, final OutputStream out) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] outputByte = new byte[4096];
            //copy binary contect to output stream
            while (fileIn.read(outputByte, 0, 4096) != -1) {
                out.write(outputByte, 0, 4096);
            }
        }
    }
    
    public static String encoderUrl(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
    }
    
    public static String replaceRxOrderPlaceHolder(String message, Order order) {
        try {
            if (message.contains("[date_time]")) {
                message = message.replace("[date_time]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE));
            }
            if (message.contains("[DRUG_NAME]")) {
                message = message.replace("[DRUG_NAME]", EncryptionHandlerUtil.getDecryptedString(AppUtil.getSafeStr(order.getDrugName(), "")));
            }
            if (message.contains("[DRUG_TYPE]")) {
                message = message.replace("[DRUG_TYPE]", order.getDrugType());
            }
            if (message.contains("[DRUG_STRENGTH]")) {
                message = message.replace("[DRUG_STRENGTH]", order.getStrength());
            }
            if (message.contains("[DRUG_QTY]")) {
                message = message.replace("[DRUG_QTY]", order.getQty());
            }
            if (message.contains("[PAYMENT_TYPE]")) {
                message = message.replace("[PAYMENT_TYPE]", order.getPaymentType());
            }
            if (message.contains("[DELIVERY_DAYS]")) {
                message = message.replace("[DELIVERY_DAYS]", (order.getDeliveryPreference() != null && order.getDeliveryPreference().getId() != null ? order.getDeliveryPreference().getName() : ""));
            }
            if (message.contains("[REQUEST_MADE]")) {
                message = message.replace("[REQUEST_MADE]", DateUtil.dateToString(new Date(), Constants.USA_DATE_TIME_FORMATE));
            }
            if (message.contains("[SERVICE_REQUESTED]")) {
                message = message.replace("[SERVICE_REQUESTED]", order.getServiceRequested());
            }
            if (message.contains("[DRUG_IMAGE]")) {
                message = message.replace("[DRUG_IMAGE]", (CommonUtil.isNotEmpty(order.getImagePath()) ? "<img src=\'" + order.getImagePath() + "/' width='30' height='30'/>" : ""));
            }
            if (message.contains("[RX_NUMBER]")) {
                message = message.replace("[RX_NUMBER]", (CommonUtil.isNotEmpty(order.getRxNumber()) ? order.getRxNumber() : ""));
            }
            
            if (message.contains("[PHARMACY_PHONE]")) {
                message = message.replace("[PHARMACY_PHONE]", (CommonUtil.isNotEmpty(order.getPharmacyPhone()) ? subStringPhone(order.getPharmacyPhone(), "") : ""));
            }
        } catch (Exception e) {
            logger.error("Exception:: CommonUtil:: replaceRxOrderPlaceHolder", e);
        }
        
        return message;
    }
    
    public static String subStringPhone(String phone, String alt) {
        logger.info("Before substring phone number:: " + phone);
        if (phone.length() >= 10) {
            phone = phone.substring(0, 3) + alt + phone.substring(3, 6) + alt + phone.substring(6, 10);
        }
        logger.info("After substring phone number:: " + phone);
        return phone;
    }
    
    public static String getDocumentPath(String docTypeOpt) {
        //        String content=files.get(0).getContentType();
        String path = "";
        switch (AppUtil.getSafeStr(docTypeOpt, "")) {
            case "1":
                path = PropertiesUtil.getProperty("PATIENT_GUIDE_ATTACHMENT");//Constants.PATIENT_GUIDE_ATTACHMENT;
                break;
            case "2":
                path = PropertiesUtil.getProperty("MEDICATION_GUIDE_ATTACHMENT");//Constants.MEDICATION_GUIDE_ATTACHMENT;
                break;
            case "3":
                path = PropertiesUtil.getProperty("DRUG_IMAGE");//Constants.DRUG_IMAGE;
                break;
            default:
                break;
        }
        return path;
    }
    
    public static String getDocumentDestinationPath(String docTypeOpt) {
        String destPath = "";
        switch (AppUtil.getSafeStr(docTypeOpt, "")) {
            case "1":
                destPath = PropertiesUtil.getProperty("DRUG_ATTACHMENT_PATH") + PropertiesUtil.getProperty("PATIENT_GUIDE_ATTACHMENT");//Constants.DRUG_ATTACHMENT_PATH + Constants.PATIENT_GUIDE_ATTACHMENT;
                break;
            case "2":
                destPath = PropertiesUtil.getProperty("DRUG_ATTACHMENT_PATH") + PropertiesUtil.getProperty("MEDICATION_GUIDE_ATTACHMENT");//Constants.DRUG_ATTACHMENT_PATH + Constants.MEDICATION_GUIDE_ATTACHMENT;
                break;
            case "3":
                destPath = PropertiesUtil.getProperty("DRUG_ATTACHMENT_PATH") + PropertiesUtil.getProperty("DRUG_IMAGE");//Constants.DRUG_ATTACHMENT_PATH + Constants.DRUG_IMAGE;
                break;
            default:
                break;
        }
        return destPath;
    }
    
    public static void saveDrugDocPath(MultipartFile f, String path, String destPath, String docTypeOpt, DrugService drugService) throws Exception {
        FileCopyUtils.copy(f.getBytes(), new File(destPath + f.getOriginalFilename()));
        CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND") + path);
        String fileNameWithOutExt = FilenameUtils.removeExtension(f.getOriginalFilename());
        logger.info("File Name WithOut Ext:: " + fileNameWithOutExt);
        if (CommonUtil.isNotEmpty(fileNameWithOutExt)) {
            drugService.saveDrugDocPath(PropertiesUtil.getProperty("INSURANCE_CARD_URL") + path + f.getOriginalFilename(),
                    Long.parseLong(fileNameWithOutExt), AppUtil.getSafeInt(docTypeOpt, -1));
        }
    }
    
    public static boolean urlAuthorization(String urlStr) {
        boolean isSuccesResponse = false;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Method", "Get");
            
            int responseCode = conn.getResponseCode();
            logger.info("Response code: " + responseCode);
            isSuccesResponse = responseCode < 400;
            logger.info("isSuccesResponse:: " + isSuccesResponse);
        } catch (Exception e) {
            logger.error("Exception:: CommonUtil:: urlAuthorization", e);
        }
        return isSuccesResponse;
    }
    
    public static String getFloatFormat(Float value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }
    
    public static void delPatientProfileData(PatientProfile patientProfile, PatientProfileDAO patientProfileDAO) throws Exception {
        patientProfileDAO.delete("TransferRequest", "PatientId", patientProfile.getId());
        patientProfileDAO.delete("YearEndStatementInfo", "PatientProfileId", patientProfile.getId());
        patientProfileDAO.delete("RewardHistory", "PatientId", patientProfile.getId());
        patientProfileDAO.delete("NotificationMessages", "ProfileId", patientProfile.getId());
        patientProfileDAO.delete("Pharmacy_PtNotifications", "PatientId", patientProfile.getId());
        patientProfileDAO.deleteOrderHistory(patientProfile.getId());
        patientProfileDAO.deleteMultirxByPatientId(patientProfile.getId());
        patientProfileDAO.delete("Orders", "PatientId", patientProfile.getId());
        patientProfileDAO.delete("OrderChain", "Patient_Id", patientProfile.getId());
        patientProfileDAO.delete("PatientInsuranceDetails", "PatientId", patientProfile.getId());
        patientProfileDAO.delete("PatientNotification", "PatientProfileInfoId", patientProfile.getId());
        patientProfileDAO.delete("PatientAllergies", "PatientId", patientProfile.getId());
        patientProfileDAO.delete("PatientProfileMembers", "PatientId", patientProfile.getId());
        patientProfileDAO.delete(patientProfile);
    }
    
    public static String saveInsuranceCard(MultipartFile insuranceCardFront, Integer profileId, String cardName) {
        String imageUrl = "";
        try {
            String dateStr = DateUtil.dateToString(new Date(), Constants.DATE_FORMATE);
            dateStr = dateStr.replace(":", "-");
            dateStr = dateStr.replace(" ", "-");
            dateStr = dateStr.replace("_", ":");
            
            String imageFormat = FileUtil.determineImageFormat(insuranceCardFront.getBytes());
            imageUrl = PropertiesUtil.getProperty("INSURANCE_CARD_URL")/*Constants.INSURANCE_CARD_URL*/ + cardName + profileId + "-" + dateStr + "." + imageFormat;
            FileCopyUtils.copy(insuranceCardFront.getBytes(),
                    new File(PropertiesUtil.getProperty("INSURANCE_CARD_PATH")/*Constants.INSURANCE_CARD_PATH */ + cardName + profileId + "-" + dateStr + "." + imageFormat));
            CommonUtil.executeCommand(PropertiesUtil.getProperty("COMMAND"));
            
        } catch (Exception e) {
            logger.error("Exception:: saveInsuranceCard", e);
        }
        return imageUrl;
    }
    
    public static boolean isNullOrEmpty(Integer value) {
        return value == null || value == 0;
    }
    
    public static void loadPageData(ModelAndView modelAndView, int pid, String id, PatientProfileService patientProfileService) {
        /**
         * Haider Ali Date : 12-08-2016 patient info
         */
        LoginDTO patientProfile = patientProfileService.getPatientProfileDataById(pid);
        long days = DateUtil.dateDiffInDays(patientProfile.getCreatedDate(), new Date());
        patientProfile.setIsOldPatient(days > 30);
        List lstInsuranceCards = patientProfileService.getPatientInsuranceCards(pid);
        modelAndView.addObject("patientProfile", patientProfile);//patientProfileService.getPatientProfileById(pid));
        modelAndView.addObject("paymentInfoList", patientProfileService.getPatientPaymentInfoListByProfileId(pid));
        //modelAndView.addObject("notificationHistoryList", notificationPharmacyService.getPharmacyNotificationList(id));
        //modelAndView.addObject("dependentsList", patientProfileService.getProfileMemberses(pid));
        //modelAndView.addObject("patientInsuranceDetails", patientProfileService.getPatientInsuranceDetailsByProfileId(pid));
        modelAndView.addObject("lstInsuranceCards", lstInsuranceCards);
        modelAndView.addObject("lstInsuranceCardsSize", lstInsuranceCards != null ? lstInsuranceCards.size() : 0);
        modelAndView.addObject("rewardPoints", patientProfileService.getMyRewardsPoints(pid));
        modelAndView.addObject("rewardLevelList", patientProfileService.getPatientRewardLevelList());
    }
    
    public static String relpacePlaceHolderForCredentials(String emailBody, String userName, String password, String pharmacyName, String userEmail) {
        if (userName != null) {
            emailBody = emailBody.replace("_USERNAME_", userName);
            emailBody = emailBody.replace("USERNAME_", userName);
        }
        if (password != null) {
            emailBody = emailBody.replace("_PASSWORD_", password);
            emailBody = emailBody.replace("PASSWORD_", password);
        }
        if (pharmacyName != null) {
            emailBody = emailBody.replace("PHARMACY_NAME", pharmacyName);
        }
        if (userEmail != null) {
            emailBody = emailBody.replace("_EMAIL_", userEmail);
        }
        emailBody = emailBody.replace("_URL_", PropertiesUtil.getProperty("APP_LOGIN"));
        return emailBody;
    }
    
    public static void testService() {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://dev.iomni-llc.com/iomniapi");

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("clientCompanyID", "4566"));
            params.add(new BasicNameValuePair("businessLineID", "1010"));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line = "";
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    br = new BufferedReader(new InputStreamReader(instream));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // do something useful
                } finally {
                    //instream.close();
                }
            }
            System.out.println(" " + sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getMessageSubjectWithprocessedPlaceHolders(String smsSubject, Order order, DrugDetail detail,
            DrugBasic basic, DrugGeneric drugGeneric, DeliveryPreferences preference, String patientName) {
        int maxLength = 0;
        try {
            if (order != null) {
                double finalPayment = order.getFinalPayment() != null ? order.getFinalPayment() : 0d;//transferRxQueueDTO.getFinalPayment() != null ? transferRxQueueDTO.getFinalPayment() : 0d;
                double handlingFee = order.getHandLingFee() != null ? order.getHandLingFee() : 0d;
                double total = finalPayment + handlingFee;
                smsSubject = smsSubject.replace("[request_no]", order.getId())
                        .replace("[RX_NUMBER]", AppUtil.getSafeStr(order.getRxPrefix(), "") + AppUtil.getSafeStr(order.getSystemGeneratedRxNumber(), ""))
                        .replace("[patient_name]",
                                AppUtil.getSafeStr(patientName, "").toUpperCase());
                smsSubject = smsSubject.replace(
                        "[total_amount]", AppUtil.roundOffNumberToCurrencyFormat(total, "en", "US"))
                        .replace("[parcel_qty]", "1")
                        .replace("[tracking_no]", AppUtil.getSafeStr(order.getTraclingno(), "").toUpperCase())
                        .replace("[courier]", AppUtil.getSafeStr(order.getDeliverycarrier(), ""));
                smsSubject = smsSubject.replace("[request_no]", order.getId()).replace("[patient_name]",
                        AppUtil.getSafeStr(patientName, "").toUpperCase());
                System.out.println("DETAIL   " + detail);
                
                System.out.println("Before DRUG " + AppUtil.getSafeStr(order.getDrugName(), ""));
                String drugName = EncryptionHandlerUtil.getDecryptedString(AppUtil.getSafeStr(order.getDrugName(), ""));
                System.out.println("After DrugName   " + drugName);
                if (detail != null) {
                    
                    System.out.println("BASIC   " + basic);
                    if (basic != null) {
                        if (AppUtil.getSafeStr(basic.getBrandIndicator(), "").equalsIgnoreCase("BRAND")) {
                            System.out.println("BRAND");
                            //                        smsSubject=smsSubject.replace("[DRUG_NAME]" ,AppUtil.getSafeStr( basic.getBrandName(),"")+ " *BRAND NAME ONLY*");
                            smsSubject = smsSubject.replace("[DRUG_NAME]", AppUtil.getSafeStr(basic.getBrandName(), ""));//+ " *BRAND NAME ONLY*");
                            smsSubject = smsSubject.replace("[BRAND_NAME]", "BRAND NAME ONLY");//+ " *BRAND NAME ONLY*");
                        } else {
                            System.out.println("GENERIC " + drugGeneric);
                            if (drugGeneric != null) {
                                //                            smsSubject=smsSubject.replace("[DRUG_NAME]" , AppUtil.getSafeStr(drugGeneric.getGenericName(),"")+ " generic for "+ basic.getBrandName());
                                smsSubject = smsSubject.replace("[DRUG_NAME]", AppUtil.getSafeStr(drugGeneric.getGenericName(), ""));
                                smsSubject = smsSubject.replace("[BRAND_NAME]", AppUtil.getSafeStr(basic.getBrandName().toUpperCase(), ""));//+ " *BRAND NAME ONLY*");
                            } else {
                                System.out.println("GENERIC=NULL " + AppUtil.getSafeStr(drugName, ""));
                                smsSubject = smsSubject.replace("[DRUG_NAME]", AppUtil.getSafeStr(drugName, ""));
                                smsSubject = smsSubject.replace("[BRAND_NAME]", "User Inputted");//+ " *BRAND NAME ONLY*");
                            }
                        }
                    } else {
                        System.out.println("BASIC=NULL " + AppUtil.getSafeStr(drugName, ""));
                        smsSubject = smsSubject.replace("[DRUG_NAME]", AppUtil.getSafeStr(drugName, ""));
                        smsSubject = smsSubject.replace("[BRAND_NAME]", "User Inputted");
                    }
                } else {
                    System.out.println("DRUG " + AppUtil.getSafeStr(drugName, ""));
                    smsSubject = smsSubject.replace("[DRUG_NAME]", AppUtil.getSafeStr(drugName, ""));
                    smsSubject = smsSubject.replace("[BRAND_NAME]", "User Inputted");
                }
                smsSubject = smsSubject.replace("[DRUG_TYPE]", AppUtil.getSafeStr(order.getType(), ""))
                        .replace("[DRUG_STRENGTH]", AppUtil.getSafeStr(order.getStrength(), ""))
                        .replace("[DRUG_QTY]", AppUtil.getSafeStr(order.getQty(), ""));
                ////////////////////////////////////////////////////////////////////////
                String pref = preference != null ? AppUtil.getSafeStr(preference.getName(), "") : "N/A";
                if (AppUtil.getSafeStr(pref, "").length() > 0 && smsSubject.indexOf("[delivery_method]") >= 0) {
                    smsSubject = smsSubject.replace("[delivery_method]", pref);
                    if (AppUtil.getSafeStr(pref, "").indexOf("2nd Day") >= 0) {
                        smsSubject = smsSubject.replace("[handling_fee]", "included");
                    } else {
                        smsSubject = smsSubject.replace("[handling_fee]", order.getHandLingFee() != null ? AppUtil.roundOffNumberToCurrencyFormat(order.getHandLingFee(), "en", "US") : "$0.00");
                    }
                    
                }
                double redeemPointsCost = order.getRedeemPointsCost() != null ? order.getRedeemPointsCost() : 0d;
                logger.info("redeemPointsCost# " + redeemPointsCost);
                smsSubject = smsSubject.replace("[points_cost]", AppUtil.roundOffNumberToCurrencyFormat(redeemPointsCost, "en", "US"));
            }
            smsSubject = smsSubject.replace("[qty]", order != null ? AppUtil.getSafeStr(order.getQty(), "") : "-");
//            if (AppUtil.getSafeStr(smsSubject, "").toUpperCase().contains("BRAND NAME ONLY")) {
//                smsSubject = AppUtil.getSafeStr(smsSubject, "").replace("generic for", "");
//            }
            ///////////////////////////////////////////////////////////////////////
            String len = PropertiesUtil.getProperty("MAX_SUB_LEN");
            maxLength = AppUtil.getSafeInt(len, 397);
            return AppUtil.getStringPortion(smsSubject, maxLength);
        } catch (Exception ex) {
            System.out.println("EXCEPTION OCURRED WHILE PLACING PALCEHOLDER VALUES IN MESSAGE SUBJECT");
            ex.printStackTrace();
            return AppUtil.getStringPortion(smsSubject, maxLength);
        }
    }
    
    public static void pushFCMNotification(String userDeviceIdKey, String orderId, String id, String prefix,
            String message, PatientProfile profile) throws Exception {
        
        try {
            System.out.println("SENDING NOTIFICATION TO ANDROID");
            String authKey = PropertiesUtil.getProperty("APP_KEY"); // You FCM AUTH key
            String urlFCM = PropertiesUtil.getProperty("FCM_URL");
            System.out.println("URL " + urlFCM + " KEY " + authKey);
            URL url = new URL(urlFCM);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");
            //First decrypt message
            message = EncryptionHandlerUtil.getDecryptedString(message);
            
            JSONObject json = new JSONObject();
            json.put("to", AppUtil.getSafeStr(userDeviceIdKey, ""));
            JSONObject info = new JSONObject();
            info.put("title", "Rx Direct Message"); // Notification title
            info.put("message", message); // Notification body
            info.put("alert", message);
//            info.put("alert", message); 
            info.put("sound", "default");
            info.put("id", id);
            info.put("orderId", orderId);
            info.put("orderPrefix", prefix);
            info.put("patientId", profile.getId());
            info.put("securityToken", profile.getSecurityToken());
            info.put("firstName", profile.getFirstName());
            info.put("lastName", profile.getLastName());
            info.put("emailAddress", AppUtil.getSafeStr(profile.getEmailAddress(), ""));
            info.put("email", AppUtil.getSafeStr(profile.getEmailAddress(), ""));
            info.put("dob", DateUtil.dateToString(profile.getDob(), "MM/dd/yyyy"));
            info.put("gender", profile.getGender());
            info.put("allergies", profile.getAllergies());
            info.put("mobileNumber", profile.getMobileNumber());
            json.put("data", info);
            System.out.println("JSON " + json.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();
            System.out.println("PUSH NOTICATION SENT TO ANDROID DEVICE WITH TOKEN ID " + userDeviceIdKey + " at " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String parseMessageSubject(String s) {
        try {
            System.out.println("INCOMING MESSAGE " + s);
            String[] arr = AppUtil.getSafeStr(s, "").split("\\<<");
            if (arr != null && arr.length >= 1) {
                System.out.println("arr[0] " + arr[0]);
                String tmp = arr[0];
                if (AppUtil.getSafeStr(tmp, "").length() == 0 && arr.length > 1) {
                    tmp = arr[1];
                }
                String token = tmp.replace(">>", "");
                System.out.println("TOKEN " + token);
                String[] tokenArr = token.split("_");
                if (tokenArr != null && tokenArr.length >= 1) {
                    String subToken = tokenArr[0];
                    System.out.println("SUB TOKEN " + subToken);
                    return subToken;
                }
                return token;
            }
            System.out.println("returning actual " + s);
            return s;
        } catch (Exception e) {
            return s;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static void pushFCMNotificationIOS(String userDeviceIdKey, String orderId, String id, String prefix,
            String message, PatientProfile profile) throws Exception {
        
        try {
            System.out.println("SENDING NOTIFICATION TO IOS");
            String authKey = PropertiesUtil.getProperty("APP_KEY"); // You FCM AUTH key
            String urlFCM = PropertiesUtil.getProperty("FCM_URL");
            System.out.println("URL " + urlFCM + " KEY " + authKey);
            URL url = new URL(urlFCM);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");
            //First decrypt messages
            message = EncryptionHandlerUtil.getDecryptedString(message);
            message = parseMessageSubject(message);
            
            JSONObject json = new JSONObject();
            json.put("to", AppUtil.getSafeStr(userDeviceIdKey, ""));
            JSONObject info = new JSONObject();
            info.put("title", "Rx Direct Message"); // Notification title
            info.put("message", message); // Notification body
            info.put("alert", message);
            info.put("body", message);
//            info.put("alert", message); 
            info.put("sound", "default");
            info.put("id", id);
            info.put("orderId", orderId);
            info.put("orderPrefix", prefix);
            /////////////////////////////////////////////////////////
            info.put("patientId", profile.getId());
            info.put("securityToken", profile.getSecurityToken());
            info.put("firstName", profile.getFirstName());
            info.put("lastName", profile.getLastName());
            info.put("emailAddress", AppUtil.getSafeStr(profile.getEmailAddress(), ""));
            info.put("email", AppUtil.getSafeStr(profile.getEmailAddress(), ""));
            info.put("dob", DateUtil.dateToString(profile.getDob(), "MM/dd/yyyy"));
            info.put("gender", profile.getGender());
            info.put("allergies", profile.getAllergies());
            info.put("mobileNumber", profile.getMobileNumber());
            ////////////////////////////////////////////////////////
            json.put("notification", info);
            System.out.println("JSON " + json.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();
            System.out.println("PUSH NOTICATION SENT TO IOS DEVICE WITH TOKEN ID " + userDeviceIdKey + " at " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Integer getPharmacyId(PharmacyUser pharmacyUser) {
        return pharmacyUser != null && pharmacyUser.getPharmacy() != null && pharmacyUser.getPharmacy().getId() != null ? pharmacyUser.getPharmacy().getId() : 0;
    }
    
    public static List extractStatusIdsFrmArray(int[] arr) {
        return Arrays.stream(arr).boxed().collect(Collectors.toList());
    }

    /**
     * Input: , separated String of numbers idStr Output: returns numbers list
     * after processing input string
     *
     */
    public static List extractIdsListFromCommaSeparatedString(String idStr) {
        try {
            List<String> lst = Arrays.asList(idStr.split(","));
            List<Number> statusList = Lists.transform(lst, new Function<String, Number>() {
                public Number apply(String s) {
                    try {
                        return NumberFormat.getInstance().parse(s); //(Number) classType.cast(s);
                        //           return Integer.valueOf(s);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                }
            });
            return statusList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    ////////////////////////////////////////////////////////
//    public static List extractIdsListFromCommaSeparatedString2(String idStr,List<? extends Number> statusList)
//    {
//        try
//        {
//            List<String> lst=Arrays.asList(idStr.split(","));
//            statusList = Lists.transform(lst, new Function<String, Number>() {
//            public Number apply(String s) 
//            {
//                try {
//                    return NumberFormat.getInstance().parse(s); //(Number) classType.cast(s);
//                    //           return Integer.valueOf(s);
//                } catch (ParseException ex) {
//                    ex.printStackTrace();
//                    return 0;
//                }
//            }
//            });
//            return statusList;
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        
//    }
    ///////////////////////////////////////////////////////
    public static List generateStatusIdsList() {
        List lst = new ArrayList();
        for (ProcessedOrderStatusEnum statusEnum : ProcessedOrderStatusEnum.values()) {
            lst.add(statusEnum.getValue());
        }
        return lst;
    }
    
    public static void delPatientProfileData(PatientProfile patientProfile, PatientProfileDAO patientProfileDAO, String tblNames) throws Exception {
        List<String> listOfTables = Stream.of(tblNames.split(",")).collect(Collectors.toList());
        for (String tblName : listOfTables) {
            if (tblName.equalsIgnoreCase("All")) {
                delPatientProfileData(patientProfile, patientProfileDAO);
            } else if (tblName.equalsIgnoreCase("TransferRequest")) {
                patientProfileDAO.delete("TransferRequest", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("YearEndStatementInfo")) {
                patientProfileDAO.delete("YearEndStatementInfo", "PatientProfileId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("RewardHistory")) {
                patientProfileDAO.delete("RewardHistory", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("NotificationMessages")) {
                patientProfileDAO.delete("PatientNotification", "PatientProfileInfoId", patientProfile.getId());
                patientProfileDAO.delete("Pharmacy_PtNotifications", "PatientId", patientProfile.getId());
                patientProfileDAO.delete("MessageResponses", "PatientId", patientProfile.getId());
                patientProfileDAO.delete("NotificationMessages", "ProfileId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("Pharmacy_PtNotifications")) {
                patientProfileDAO.delete("Pharmacy_PtNotifications", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("Orders")) {
                patientProfileDAO.deleteOrderHistory(patientProfile.getId());
                patientProfileDAO.deleteRewardHistoryByPatientId(patientProfile.getId());
                patientProfileDAO.deleteNotificationMessagesByPatientId(patientProfile.getId());
                patientProfileDAO.deleteMultirxByPatientId(patientProfile.getId());
                patientProfileDAO.delete("OrderBatch", "PatientId", patientProfile.getId());
                patientProfileDAO.delete("OrderChain", "Patient_Id", patientProfile.getId());
                patientProfileDAO.delete("Orders", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientInsuranceDetails")) {
                patientProfileDAO.delete("PatientInsuranceDetails", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientNotification")) {
                patientProfileDAO.delete("PatientNotification", "PatientProfileInfoId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("DrugSearches")) {
                patientProfileDAO.delete("DrugSearches", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientPaymentInfo")) {
                patientProfileDAO.delete("PatientProfilePaymentInfo", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientDeliveryAddress")) {
                patientProfileDAO.delete("PatientDeliveryAddress", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientProfileMembers")) {
                patientProfileDAO.removePatientAllergiesByPatientIdAndDependentExist(patientProfile.getId());
                patientProfileDAO.removePatientInsuranceDetailsByPatientIdAndDependentExist(patientProfile.getId());
                patientProfileDAO.removeNotificationMessagesByPatientIdAndDependentExist(patientProfile.getId());
                patientProfileDAO.removeOrdersByPatientIdAndDependentExist(patientProfile.getId());
                patientProfileDAO.delete("PatientProfileMembers", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientProfileHealth")) {
                patientProfileDAO.delete("PatientProfileHealth", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientGlucoseResults")) {
                patientProfileDAO.delete("PatientGlucoseResults", "PatientId", patientProfile.getId());
            } else if (tblName.equalsIgnoreCase("PatientProfileNotes")) {
                patientProfileDAO.delete("PatientProfileNotes", "PtProfileId", patientProfile.getId());
            }
        }
    }
    
    public static final Comparator<OrdersPtMessage> ordersPtMsgSortByDate = (OrdersPtMessage ord1, OrdersPtMessage ord2) -> {
        Date d1 = ord1.getCreatedOn();
        Date d2 = ord2.getCreatedOn();
        if (ord1.getSort().equalsIgnoreCase("desc")) {
            return (d1.getTime() > d2.getTime() ? -1 : 1);     //descending
        } else {
            return (d1.getTime() > d2.getTime() ? 1 : -1);     //ascending
        }
        
    };
    
    public static String replaceAllStr(String s, String orgchar, String alt) {
        return s.replaceAll(orgchar, alt);
    }
    
    public static String bCryptPasswordEncoder(String psw) {
        return new BCryptPasswordEncoder().encode(psw);
    }
    
    public static boolean isPswExist(String psw, String bcryptPsw) {
        return new BCryptPasswordEncoder().matches(psw, bcryptPsw);
    }
    
    public static PatientDependantDTO populatePatientDependant(PatientProfileMembers patientProfileMembers) throws Exception {
        PatientDependantDTO patientProfileDependantDTO = new PatientDependantDTO();
        patientProfileDependantDTO.setId(patientProfileMembers.getId());
        patientProfileDependantDTO.setPatientId(patientProfileMembers.getPatientId());
        patientProfileDependantDTO.setFirstName(EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getFirstName()));
        patientProfileDependantDTO.setLastName(EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getLastName()));
        patientProfileDependantDTO.setGender(patientProfileMembers.getGender());
        if (patientProfileMembers.getDob() != null) {
            patientProfileDependantDTO.setDob(DateUtil.dateToString(patientProfileMembers.getDob(), Constants.USA_DATE_FORMATE));
        }
        patientProfileDependantDTO.setAllergies(patientProfileMembers.getAllergies());
        patientProfileDependantDTO.setDermatologist(patientProfileMembers.getDermatologist());
        patientProfileDependantDTO.setEmail(EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getEmail()));
        patientProfileDependantDTO.setIsAdult(patientProfileMembers.getIsAdult());
        patientProfileDependantDTO.setIsApproved(patientProfileMembers.getIsApproved());
        patientProfileDependantDTO.setCreatedOn(DateUtil.dateToString(patientProfileMembers.getCreatedOn(), Constants.USA_DATE_TIME_SECOND_FORMATE));
        patientProfileDependantDTO.setFullName(patientProfileMembers.getFullName());
        patientProfileDependantDTO.setComments(patientProfileMembers.getComments());
        patientProfileDependantDTO.setBackPOAImage(EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getBackPOAImage()));
        patientProfileDependantDTO.setFrontPOAImage(EncryptionHandlerUtil.getDecryptedString(patientProfileMembers.getFrontPOAImage()));
        patientProfileDependantDTO.setBirthDate(patientProfileMembers.getDob());
        patientProfileDependantDTO.setCreatedDate(patientProfileMembers.getCreatedOn());
        patientProfileDependantDTO.setPoaApprovalDate(patientProfileMembers.getPoaApprovalDate());
        patientProfileDependantDTO.setPoaExpirationDate(patientProfileMembers.getPoaExpirationDate());
        patientProfileDependantDTO.setExpiryDate(patientProfileMembers.getExpiryDate());
        patientProfileDependantDTO.setDisapprove(patientProfileMembers.getDisapprove());
        patientProfileDependantDTO.setArchived(patientProfileMembers.getArchived() != null
                ? patientProfileMembers.getArchived() : 0);
        
        return patientProfileDependantDTO;
    }
    
    public static LoginDTO populateProfileUserData(PatientProfile patientProfile) throws Exception {
        LoginDTO profile = new LoginDTO();
        profile.setId(patientProfile.getId());
        profile.setFirstName(patientProfile.getFirstName());
        profile.setLastName(patientProfile.getLastName());
        profile.setMobileNumber(patientProfile.getMobileNumber());
        if (patientProfile.getDob() != null) {
            profile.setDob(DateUtil.dateToString(patientProfile.getDob(), Constants.USA_DATE_FORMATE));
        }
        profile.setBirthDate(patientProfile.getDob());
        profile.setVerificationCode(patientProfile.getVerificationCode());
        profile.setSecurityToken(patientProfile.getSecurityToken());
        profile.setGroupNumber(patientProfile.getGroupNumber());
        profile.setGender(patientProfile.getGender());
        profile.setAllergies(patientProfile.getAllergies());
        profile.setEmailAddress(patientProfile.getEmailAddress());
        profile.setPatientId(patientProfile.getId());
        profile.setProviderAddress(patientProfile.getProviderAddress());
        profile.setMemberID(patientProfile.getMemberId());
        profile.setProviderPhone(patientProfile.getProviderPhoneNumber());
        profile.setAlternatePhoneNumber(patientProfile.getAlternatePhoneNumber());
        profile.setCreatedOn(DateUtil.dateToString(patientProfile.getCreatedOn(), Constants.USA_DATE_TIME_SECOND_FORMATE));
        profile.setUpdatedOn(DateUtil.dateToString(patientProfile.getUpdatedOn(), Constants.USA_DATE_TIME_SECOND_FORMATE));
        profile.setCreatedDate(patientProfile.getCreatedOn());
        profile.setUpdatedDate(patientProfile.getUpdatedOn());
        profile.setOsType(patientProfile.getOsType());
        profile.setStatus(patientProfile.getStatus());
        profile.setInsuranceBackCardPath(patientProfile.getInsuranceBackCardPath());
        profile.setInsuranceFrontCardPath(patientProfile.getInsuranceFrontCardPath());
        profile.setCardHolderRelation(patientProfile.getCardHolderRelation());
        return profile;
    }
    
    public static void populateDecryptedOrderData(OrderDetailDTO newOrder, Order order) {
        //Populate decrypted data in OrderDetailDTO
        if (newOrder != null) {
            newOrder.setFirstName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getFirstName()), ""));
            newOrder.setLastName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getLastName()), ""));
            newOrder.setDrugName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getDrugName()), ""));
            newOrder.setCardNumber(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardNumber()), ""));
            newOrder.setCardType(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardType()), ""));
            newOrder.setCardHolderName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardHolderName()), ""));
        }

        //Populate decrypted data in order
        order.setFirstName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getFirstName()), ""));
        order.setLastName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getLastName()), ""));
        order.setDrugName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getDrugName()), ""));
        order.setImagePath(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getImagePath()), ""));
        order.setCardNumber(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardNumber()), ""));
        order.setCardType(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardType()), ""));
        order.setCardCvv(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardCvv()), ""));
        order.setCardHolderName(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getCardHolderName()), ""));
        order.setVideo(AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getVideo()), ""));
    }
    
    public static String subStrCardNumber(String cardNumber) {
        logger.info("Before sub string Card Number is: " + cardNumber);
        if (isNotEmpty(cardNumber) && cardNumber.length() > 4) {
            cardNumber = cardNumber.substring(cardNumber.length() - 4);
            logger.info("After sub string Card Number is: " + cardNumber);
        }
        return cardNumber;
    }
}
