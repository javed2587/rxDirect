package com.ssa.cms.servlet;

import com.ssa.cms.common.Constants;
import com.ssa.cms.enumeration.NotificationStatusEnum;
import com.ssa.cms.model.RedemptionLog;
import com.ssa.cms.util.NotificationUtil;
import com.ssa.cms.decorator.ValidationDecorator;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.AggregatorMessageResponse;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CampaignTrigger;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.CustomerRequest;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.EmailResponse;
import com.ssa.cms.model.Enrollment;
import com.ssa.cms.model.EventDetail;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRequest;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.IvroptInOut;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.RedemptionChannel;
import com.ssa.cms.model.ShortCodes;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.GatewayService;
import com.ssa.cms.service.PhoneValidationService;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.thread.EmailRedemptionSendingThread;
import com.ssa.cms.thread.TextIntervalReminderThread;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.SMSUtil;
import com.ssa.cms.util.IVRCallDialerUtil;
import com.ssa.cms.validation.InstantValidationUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class EMGSInstantRedemptionServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(EMGSInstantRedemptionServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * ************************************************************************************************
     * ************************************************************************************************
     * ***********************************************************************************************
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String xml = request.getParameter("xml");
        String secKey = request.getParameter("key");
        String sendMessage = request.getParameter("sendMessage");

        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        PrintWriter out = response.getWriter();

        SAXBuilder builder = null;
        Document document = null;
        Element rootElement = null;
        int messageSent = 0;

        RedemptionLog redemptionLog = null;
        ValidationDecorator validationDecorator = null;
        InstantValidationUtil validationUtil = new InstantValidationUtil();
        NotificationUtil notificationUtil = new NotificationUtil();
        AggregatorMessageRequest messageRequest = null;
        AggregatorMessageResponse messageResponse = null;
        SMSUtil smsUtil = new SMSUtil();
        GatewayInfo gatewayInfo = null;
        int shortCodeValue = 0;
        ShortCodes shortCode = null;

        long requetLogSeqNo = 0;
        int statusCode = 0;
        String notificationMessage = "";
        String notification = "";
        String memberId = "";

        CustomerRequest customerRequest = null;

        if (xml == null || xml.trim().length() == 0) {
            xml = "";
            BufferedReader bufferedReader = request.getReader();
            int i = 0;

            while ((i = bufferedReader.read()) != 1) {
                xml = xml + (char) i;
            }
        }
        try {
            ServletContext servletContext = this.getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            RedemptionService redemptionDAO = (RedemptionService) applicationContext.getBean("redemptionService");

            String remortIP = request.getRemoteAddr();

            redemptionLog = new RedemptionLog();
            redemptionLog.setRequestXml(xml);
            redemptionLog.setClientIp(remortIP);
            redemptionLog.setMessageSent(messageSent);
            redemptionLog.setEffectiveDate(new Date());
            redemptionLog.setFileTypeCode(Constants.IRF);

            redemptionDAO.save(redemptionLog);

            requetLogSeqNo = redemptionLog.getSeqNo();

            /*Validationg Security Key*/
            if (secKey == null || secKey.trim().length() == 0) {
                statusCode = NotificationStatusEnum.KEY_MISSING.getValue();
                notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
                notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);

                redemptionLog.setStatusCode(statusCode);
                redemptionLog.setNotification(notification);
                redemptionLog.setDescription(notificationMessage);

                redemptionDAO.update(redemptionLog);

                out.print(notification);
                return;
            }

            secKey = secKey.trim();

            /*XML validation*/
            if (xml == null || xml.trim().length() == 0) {
                statusCode = NotificationStatusEnum.NO_XML.getValue();
                notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
                notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);

                redemptionLog.setStatusCode(statusCode);
                redemptionLog.setNotification(notification);
                redemptionLog.setDescription(notificationMessage);

                redemptionDAO.update(redemptionLog);

                out.print(notification);
                return;
            }

            xml = xml.trim();

            try {
                builder = new SAXBuilder();
                document = builder.build(new ByteArrayInputStream(xml.getBytes()));
                rootElement = document.getRootElement();
            } catch (JDOMException jdome) {
                statusCode = NotificationStatusEnum.XML_STRUCTURE_INVALID.getValue();
                notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
                notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);
                redemptionLog.setStatusCode(statusCode);
                redemptionLog.setNotification(notification);
                redemptionLog.setDescription(notificationMessage);

                redemptionDAO.update(redemptionLog);

                out.print(notification);
                return;
            }

            validationDecorator = validationUtil.validateIRFRecord(rootElement);

            if (!validationDecorator.isValid()) {

                statusCode = NotificationStatusEnum.REQUEST_FIELD.getValue();
                notificationMessage = validationDecorator.getMessage();
                notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);

                redemptionLog.setStatusCode(statusCode);
                redemptionLog.setNotification(notification);
                redemptionLog.setDescription(notificationMessage);

                redemptionDAO.update(redemptionLog);

                out.print(notification);
                return;
            }

            InstantRedemption instantRedemption = (InstantRedemption) validationDecorator.getResult();

            instantRedemption.setFileTypeCode(Constants.IRF);
            instantRedemption.setInputReferenceNumber(redemptionLog.getSeqNo());
            memberId = RedemptionUtil.calculateMemberId(instantRedemption);
            instantRedemption.setMemberId(memberId);
            instantRedemption.setEffectiveDate(new Date());
            instantRedemption.setTimeStamp(new Date());


            /*Populating prescriber and pharmacy values   */
            redemptionDAO.populateNpiValues(instantRedemption);

            Campaigns campaign = redemptionDAO.getCampaignByNDCNumber(instantRedemption.getNdcNumber());

            int campaignId = 0;
            String campaignName = "";
            RedemptionChannel redemptionChannel = null;
            boolean enrollmentFlag = false;

            if (campaign != null) {
                campaignId = campaign.getCampaignId();
                campaignName = campaign.getCampaignName();
                //redemptionChannel = campaign.getRedemptionChannel();
                shortCode = campaign.getShortCodes();

                redemptionLog.setCampaignId(campaign.getCampaignId());
                redemptionDAO.update(redemptionLog);
            }

            if (redemptionChannel == null) {
                log.info("No redemption channel attached to campaign " + campaignId + " (System will return)");
                return;
            }

            String redemptionChannelTitle = redemptionChannel.getRchannelTitle();
            log.info("Redemption channel title : " + redemptionChannelTitle);

            if (redemptionChannelTitle == null || (!redemptionChannelTitle.trim().equalsIgnoreCase("EMDEON"))) {
                log.info("Redemption channel titel doesn't have value EMDEON. (System will return)");
                return;
            }

            instantRedemption.setRedemptionChannelId(redemptionChannel.getRchannelId());
            instantRedemption.setRedemptionChannelTitle(redemptionChannelTitle);
            instantRedemption.setCampaignId(campaignId);

            String cardHolderId = instantRedemption.getCardholderId();

            InstantRequest instantRequest = redemptionDAO.getInstantRequestByMemberId(cardHolderId, campaignId);

            String communicationMethod = null;
            String communicationId = null;

            if (instantRequest != null) {
                String messageFlag = instantRequest.getFlagMessage();

                if (messageFlag != null) {
                    messageFlag = messageFlag.trim();

                    if (messageFlag.equalsIgnoreCase("T")) {
                        communicationMethod = "T";
                        communicationId = instantRequest.getText();
                    } else if (messageFlag.equalsIgnoreCase("I")) {
                        communicationMethod = "I";
                        communicationId = instantRequest.getText();
                    } else if (messageFlag.equalsIgnoreCase("E")) {
                        communicationMethod = "E";
                        communicationId = instantRequest.getEmail();
                    }
                } // if(messageFlag != null)
            }// if(instantRequest != null)

            instantRedemption.setCommunicationMethod(communicationMethod);
            instantRedemption.setCommunicationId(communicationId);

            boolean isEnrolled = false;

            Enrollment enrollment = null;
            String appCode = "App0000";
            boolean phoneValidity = false;

            CampaignTrigger campaignTrigger = redemptionDAO.getTriggerByCampaignId(campaignId);
            String programCode = null;

            if (campaignTrigger != null) {
                programCode = campaignTrigger.getId().getKeyword();
            }

            if (communicationMethod != null && communicationMethod.equalsIgnoreCase("T")) {
                enrollment = redemptionDAO.getTextEnrollment(communicationId, campaignId);
                if (shortCode != null) {
                    shortCodeValue = shortCode.getShortCode();
                }

                gatewayInfo = redemptionDAO.getGatewayInfo(shortCodeValue);

                appCode = "App000";
                if (gatewayInfo != null) {
                    appCode = gatewayInfo.getAppCode();
                }

                String phonevalidationUrl = redemptionDAO.getURL(Constants.PVURL);
                PhoneValidationService phoneValidationService = new PhoneValidationService(phonevalidationUrl);

                phoneValidity = phoneValidationService.checkPhoneValidity(communicationId, appCode, programCode);
            }
            if (communicationMethod != null && communicationMethod.equalsIgnoreCase("I")) {
                enrollment = new Enrollment();
                if (instantRequest.getId() != null) {
                    enrollment.setEnrollmentId(instantRequest.getId());
                }
                enrollment.setEnrollmentPath("I");
                enrollment.setCommunicationSourceCode("0003");
                enrollment.setEffectiveDate(instantRequest.getEffectiveDate());
                enrollment.setCommunicationMethod("I");
                enrollment.setCommunicationId(instantRequest.getText());
            }
            if (communicationMethod != null && communicationMethod.equalsIgnoreCase("E")) {
                enrollment = redemptionDAO.getEmailEnrollment(communicationId, campaignId);
            }

            if (enrollment != null) {
                instantRedemption.setEnrollmentId(new Long(enrollment.getEnrollmentId()).intValue());
                instantRedemption.setEnrollmentDate(enrollment.getEffectiveDate());
                instantRedemption.setCommunicationSourceCode(enrollment.getCommunicationSourceCode());
                instantRedemption.setEnrollmentPath(enrollment.getEnrollmentPath());
                isEnrolled = true;
                enrollmentFlag = true;
            }
            instantRedemption.setIsValidPhone("NO");
            if (phoneValidity) {
                instantRedemption.setIsValidPhone("YES");
            }
            if (communicationMethod != null && communicationMethod.equalsIgnoreCase("I")) {
                if (instantRequest.getIsValidPhone().equalsIgnoreCase("Yes")) {
                    instantRedemption.setIsValidPhone("YES");
                }
                if (instantRequest.getIsValidPhone().equalsIgnoreCase("L")) {
                    instantRedemption.setIsValidPhone("L");
                }
            }
            instantRedemption.setProgramCode(programCode);

            int recordCount = redemptionDAO.getIRFCountById(instantRedemption.getId());

            if (recordCount > 0) {
                statusCode = NotificationStatusEnum.DUPLICATE.getValue();
                notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
                notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);

                redemptionLog.setStatusCode(statusCode);
                redemptionLog.setNotification(notification);
                redemptionLog.setDescription(notificationMessage);

                redemptionDAO.update(redemptionLog);

                out.print(notification);
                return;
            }

            redemptionDAO.save(instantRedemption);

            statusCode = NotificationStatusEnum.SUCCESS.getValue();
            notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
            notification = notificationUtil.prepareErrorNotification(statusCode, requetLogSeqNo, notificationMessage);
            redemptionLog.setStatusCode(statusCode);
            redemptionLog.setNotification(notification);
            redemptionLog.setDescription(notificationMessage);

            redemptionDAO.update(redemptionLog);

            int claimStatus = instantRedemption.getId().getClaimStatus();

            if (communicationMethod == null || communicationMethod.trim().length() == 0) {
                log.info("Communication method not define (System will return)");
                return;
            }

            communicationMethod = communicationMethod.trim();

            if (!(communicationMethod.equalsIgnoreCase("T") || communicationMethod.equalsIgnoreCase("I") || communicationMethod.equalsIgnoreCase("E"))) {
                log.info("Communication method not valid (System will return)");
                return;
            }

            log.info("Enrollment flag value : " + enrollmentFlag);
            if (!enrollmentFlag) {
                log.info("Patient has not completed the enrollment. (System will return)");
                return;
            }

            if (!isEnrolled) {
                log.info("Not enrolled patient. (System will return)");
                return;
            }

            if (campaignTrigger == null) {
                log.info("Campaign trigger is null. (System will continue)");
                return;
            }

            instantRedemption.setRedemptionId(redemptionDAO.getIRFById(instantRedemption.getId()).getRedemptionId());

            String optInOutFlag = null;
            List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses = null;
            String eventsIds = "";
            List<EventDetail> eventDetails = null;
            CampaignMessagesResponse campaignMessagesResponse = null;
            List<CampaignMessages> campaignMessagesList = null;
            CampaignMessages campaignMessages = null;
            int redemptionFolderId = 0;
            int redemptionMsgTypeId = 0;

            String communicationPath = "";
            boolean isWebEnabled = false;
            int messageId = 0;
            int messageTypeId = 0;

            Intervals interval = null;
            IntervalsType intervalsType = null;

            int intervalId = 0;
            String intervalDesc = "";
            String eventDesc = "";
            String messageText = "";
            int sentMessageCount = 0;
            int paFolderId = 0;

            byte[] mmsImage = null;

            if (communicationMethod.equalsIgnoreCase("T")) {

                String phoneNumber = instantRedemption.getCommunicationId();

                OptInOut optInOut = redemptionDAO.getTextOptInOut(campaignId, phoneNumber);

                if (optInOut != null) {
                    optInOutFlag = optInOut.getOptInOut();
                }

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    log.info(phoneNumber + " has opted out from Campaign : " + campaignId);
                    log.info("System will return");
                    return;
                }

                if (shortCode == null) {
                    log.info("No short code defined for campaign : " + campaignId);
                    log.info("System will return");
                    return;
                }

                String gatewayStatusServiceURL = gatewayInfo.getGkurl();
                GatewayService gatewayService = new GatewayService(gatewayStatusServiceURL);

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("I")) {
                    if (shortCodeValue != 21200) {
                        gatewayService.informToGatewayForAnonymousYES(phoneNumber, programCode, "NO", "0004");
                    }
                } else {
                    if (shortCodeValue != 21200) {
                        gatewayService.informToGatewayForAnonymousYES(phoneNumber, programCode, "YES", "0004");
                    }
                }

                eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, Constants.SMS);

                if (eventHasFolderHasCampaignses == null
                        || eventHasFolderHasCampaignses.isEmpty()) {
                    log.info("No Redemption folder associated to this campaign (System will continue)");
                    return;
                }

                for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

                    eventsIds = "";

                    if (redemptionFolderId == ehfhc.getFolderId()) {
                        continue;
                    }

                    redemptionFolderId = ehfhc.getFolderId();

                    for (EventHasFolderHasCampaigns ehfhc1 : eventHasFolderHasCampaignses) {

                        if (ehfhc1.getFolderId() == ehfhc.getFolderId()) {
                            eventsIds += ehfhc1.getEventId() + ",";
                        }
                    }
                    eventsIds = eventsIds.substring(0, eventsIds.length() - 1);
                    eventDetails = redemptionDAO.getEventDetail(eventsIds, "InstantRedemption");
                    if (eventDetails != null && eventDetails.size() > 0) {
                        boolean send = redemptionDAO.isEventDetailVerifiedForInstantRedemption(eventDetails, instantRedemption.getId(), instantRedemption.getRedemptionId());
                        if (!send) {
                            log.info("Event Detail Criteria doesnt match for Instant Redemption.");
                            continue;
                        }
                    }

                    String messageContext = Constants.REDEMPTION;
                    String responseTile = Constants.THANK_YOU;

                    gatewayService = new GatewayService(gatewayInfo.getWebQueryURL());

                    isWebEnabled = gatewayService.webEnabledQuery(phoneNumber, "PMS EMGS Instant Redemption Servlet", 0, gatewayInfo);

                    if (isWebEnabled) {
                        boolean isMmsExist = redemptionDAO.isMmsExists(campaignId, redemptionFolderId);
                        if (isMmsExist) {
                            communicationPath = Constants.MMS;
                        } else {
                            communicationPath = Constants.SMS;
                        }
                    } else {
                        communicationPath = Constants.SMS;
                    }

                    campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, communicationPath);

                    if (campaignMessagesResponse == null) {
                        log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                        campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, communicationPath);

                        if (campaignMessagesResponse == null) {
                            log.info("Redemption campaignMessagesResponse is null.");
                            continue;
                        }

                    }

                    interval = campaignMessagesResponse.getIntervals();
                    intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                    intervalId = interval.getIntervalId();
                    intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                    eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, communicationPath);

                    int intervalValue = interval.getIntervalValue();
                    int intervalUnitInSecond = intervalsType.getUnitInSecond();
                    long secondsDelay = intervalValue * intervalUnitInSecond;

                    redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

                    campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, communicationPath);

                    if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                        log.info("No Redemption messages found for (System will continue)");
                        continue;
                    }
                    campaignMessages = campaignMessagesList.get(0);

                    messageId = campaignMessages.getMessageId();
                    messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
                    messageText = campaignMessages.getSmstext();

                    //check and make sure patient has not recieved within next 24 hours
                    sentMessageCount = redemptionDAO.retrieveMessageCountByTypeForRedemption(phoneNumber, instantRedemption.getRxGroupNumber(), instantRedemption.getCardholderDob(), instantRedemption.getFillDate(), instantRedemption.getNdcNumber(), instantRedemption.getId().getClaimStatus(), ehfhc.getFolderId(), messageTypeId);

                    if (sentMessageCount > 0) {

                        log.info("Message already sent for phone number: " + phoneNumber);
                        continue;
                    }
                    log.info("Message Id : " + messageId);
                    log.info("Message Type Id : " + messageTypeId);
                    log.info("Redemption Message : " + messageText);

                    log.info("Thread going to sleep for " + secondsDelay + " Seconds.");

                    if (messageText != null && messageText.trim().length() > 0) {
                        double totalDrugCostToPharmacy = 0;
                        String patientTotalDrugCostWithoutDecimal = "";
                        String patientTotalDrugCost = "";
                        NumberFormat numberFormat = new DecimalFormat(".00");
                        NumberFormat numberFormatWithoutDecimal = new DecimalFormat("###");
                        if (instantRedemption.getTotDrugCostPaidToPharmacy() != null) {
                            totalDrugCostToPharmacy = instantRedemption.getTotDrugCostPaidToPharmacy().doubleValue();
                            patientTotalDrugCost = numberFormat.format(totalDrugCostToPharmacy);
                            patientTotalDrugCostWithoutDecimal = numberFormatWithoutDecimal.format(totalDrugCostToPharmacy);
                        }

                        int refillAllowed = instantRedemption.getRefillAuthorized();
                        int remaining = refillAllowed - instantRedemption.getRefillsUsed();
                        messageText = messageText.replace("ZZ", remaining + "");

                        BigDecimal copay = instantRedemption.getPtOutOfPocket();
                        if (copay != null) {
                            copay = copay.setScale(2, RoundingMode.CEILING);
                            messageText = messageText.replace("YYY.YY", copay + "");
                        }

                        messageText = messageText.replace("XX.XX", patientTotalDrugCost);
                        messageText = messageText.replace("XXX", patientTotalDrugCostWithoutDecimal); //for soolantra

                        String pharmacyName = instantRedemption.getPharmacyName();
                        if (pharmacyName != null) {
                            if (campaignId >= 10) {
                                if (pharmacyName.trim().length() > 30) {
                                    pharmacyName = pharmacyName.substring(0, 29);
                                }
                                pharmacyName = pharmacyName.toUpperCase();
                            } else {
                                if (pharmacyName.trim().length() > 10) {
                                    pharmacyName = pharmacyName.substring(0, 9);
                                }
                            }
                            messageText = messageText.replaceAll("_PHARMACY_", pharmacyName);
                        }
                        String pharmacyPhone = instantRedemption.getPharmacyPhone();
                        if (pharmacyPhone != null) {
                            String formatedPharmacyPhone = RedemptionUtil.formatPhone(pharmacyPhone);
                            messageText = messageText.replaceAll("###-###-####", formatedPharmacyPhone);
                        }
                        campaignMessages.setSmstext(messageText);

                        messageRequest = new AggregatorMessageRequest();
                        messageRequest.setCardholderDob(instantRedemption.getCardholderDob());
                        messageRequest.setClaimStatus(claimStatus);
                        messageRequest.setFileTypeCode(Constants.IRF);
                        messageRequest.setFolderId((ehfhc.getFolderId()));
//                        messageRequest.setMessageRequest(mTDecorator.getRequestXML());
                        messageRequest.setMessageText(messageText);
                        messageRequest.setMessageTypeId((messageTypeId));
                        messageRequest.setNdcNumber(instantRedemption.getNdcNumber());
                        messageRequest.setPhoneNumber(phoneNumber);
                        messageRequest.setRedemptionId(instantRedemption.getRedemptionId());
                        messageRequest.setRxGroupNumber(instantRedemption.getRxGroupNumber());
                        messageRequest.setFillDate(instantRedemption.getFillDate());
                        messageRequest.setCampaignId(campaign.getCampaignId());
                        messageRequest.setMessageContext(messageContext);
                        messageRequest.setIntervalId(intervalId);
                        messageRequest.setIntervalDescription(intervalDesc);
                        messageRequest.setEventDetail(eventDesc);
                        messageRequest.setCommunicationPath(communicationPath);
                        messageSent++;
                    }

                    mmsImage = null;

                    if (mmsImage != null && communicationPath.equalsIgnoreCase(Constants.MMS)) {
                        messageSent++;
                    }

                    campaignMessages.setSmstext(messageText);

                    TextIntervalReminderThread intervalMessageThread = new TextIntervalReminderThread();
                    intervalMessageThread.setGatewayInfo(gatewayInfo);
                    intervalMessageThread.setAggregatorMessageRequest(messageRequest);
                    intervalMessageThread.setCampaignMessages(campaignMessages);
                    intervalMessageThread.setAppName("PMS Campaign " + programCode + " IRF Processor");
                    intervalMessageThread.setProgramCode(programCode);
                    intervalMessageThread.setPhoneNumber(phoneNumber);
                    intervalMessageThread.setInputReferenceNumber(0);
                    intervalMessageThread.setSecondsDelay(secondsDelay);
                    intervalMessageThread.setCampaign(campaign);
                    intervalMessageThread.setRedemptionDAO(redemptionDAO);
                    intervalMessageThread.setFolderId(ehfhc.getFolderId());
                    intervalMessageThread.setMessageTypeId(messageTypeId);
                    intervalMessageThread.setCommunicationType(communicationPath);
                    intervalMessageThread.setIntervalId(intervalId);
                    intervalMessageThread.setIntervalDesc(intervalDesc);
                    intervalMessageThread.setEventDesc(eventDesc);
                    intervalMessageThread.setInstantRedemption(instantRedemption);

                    intervalMessageThread.setMessageSent(messageSent);
                    intervalMessageThread.setThreadType("redemption");
                    Thread smsIntervalMessageThread = new Thread(intervalMessageThread);
                    smsIntervalMessageThread.start();

                }

            } else if (communicationMethod.equalsIgnoreCase("I")) {
                //initiate IVR call
                boolean isMobile = false;
                boolean isLandLine = false;
                boolean isThankYouCallIntitiated = false;
                String phoneNumber = instantRedemption.getCommunicationId();
                if (instantRequest.getIsValidPhone().equalsIgnoreCase("Yes")) {
                    isMobile = true;
                }
                if (instantRequest.getIsValidPhone().equalsIgnoreCase("L")) {
                    isLandLine = true;
                }
                if (isLandLine == false && isMobile == false) {
                    log.info(phoneNumber + " PhoneNumber neither Mobile nor Landline : " + campaignId);
                    log.info("System will return");
                    return;
                }
                IvroptInOut optInOut = redemptionDAO.getIVROptInOut(campaignId, phoneNumber);

                if (optInOut != null) {
                    optInOutFlag = optInOut.getOptInOut();
                }

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    log.info(phoneNumber + " has opted out from Campaign : " + campaignId);
                    log.info("System will return");
                    return;
                }
                eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, communicationPath);

                if (eventHasFolderHasCampaignses == null
                        || eventHasFolderHasCampaignses.isEmpty()) {
                    log.info("No Redemption folder associated to this campaign (System will continue)");
                    return;
                }

                String servoId = "";
                IVRCallDialerUtil dialerUtil = null;

                for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

                    eventsIds = "";

                    if (redemptionFolderId == ehfhc.getFolderId()) {
                        continue;
                    }

                    redemptionFolderId = ehfhc.getFolderId();

                    for (EventHasFolderHasCampaigns ehfhc1 : eventHasFolderHasCampaignses) {
                        if (ehfhc1.getFolderId() == ehfhc.getFolderId()) {
                            eventsIds += ehfhc1.getEventId() + ",";
                        }
                    }
                    eventsIds = eventsIds.substring(0, eventsIds.length() - 1);
                    eventDetails = redemptionDAO.getEventDetail(eventsIds, "InstantRedemption");
                    if (eventDetails != null && eventDetails.size() > 0) {
                        boolean send = redemptionDAO.isEventDetailVerifiedForInstantRedemption(eventDetails, instantRedemption.getId(), instantRedemption.getRedemptionId());
                        if (!send) {
                            log.info("Event Detail Criteria doesnt match for Instant Redemption.");
                            continue;
                        }

                    }

                    String messageContext = Constants.REDEMPTION;
                    String responseTile = Constants.THANK_YOU;
                    String isMaxBenifit = campaign.getIsMaxBenefit();
                    long maxbenifitAmount = 0;

                    if (isMaxBenifit.equalsIgnoreCase("YES")) {

                        if (campaign.getMaxBenefitAmount() != null) {
                            maxbenifitAmount = campaign.getMaxBenefitAmount().intValue();
                        }
                        long redemptionLimit = campaign.getRedemptionLimit();

                        BigDecimal benifitAmount = redemptionDAO.getTotalBenefitAmountIRF(instantRedemption);
                        long redemptionCount = redemptionDAO.getTotalRedemptionCountIRF(instantRedemption);

                        if (redemptionCount >= redemptionLimit) {
                            responseTile = Constants.MAX_BENEFIT;
                            messageContext = Constants.MAX_BENEFIT;
                        }

                        if (benifitAmount != null && (benifitAmount.doubleValue() >= maxbenifitAmount && maxbenifitAmount != 0)) {
                            responseTile = Constants.MAX_BENEFIT;
                            messageContext = Constants.MAX_BENEFIT;
                        }
                    }

                    campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, Constants.OIVR);

                    if (campaignMessagesResponse == null) {
                        log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                        campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, Constants.OIVR);

                        if (campaignMessagesResponse == null) {
                            log.info("Redemption campaignMessagesResponse is null.");
                            continue;
                        }

                    }

                    interval = campaignMessagesResponse.getIntervals();
                    intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                    intervalId = interval.getIntervalId();
                    intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                    eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, communicationPath);

                    int intervalValue = interval.getIntervalValue();
                    int intervalUnitInSecond = intervalsType.getUnitInSecond();
                    long secondsDelay = intervalValue * intervalUnitInSecond;

                    Thread.sleep(1000 * secondsDelay);

                    redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

                    campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, Constants.OIVR);

                    if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                        log.info("No Redemption messages found for (System will continue)");
                        continue;
                    }
                    campaignMessages = campaignMessagesList.get(0);

                    messageId = campaignMessages.getMessageId();
                    messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
                    servoId = null;
                    if (servoId == null || servoId.length() == 0) {
                        log.info("No Survo Id found for (System will continue)");
                        continue;
                    }

                    dialerUtil = new IVRCallDialerUtil();
                    dialerUtil.setInstantRedemption(instantRedemption);
                    dialerUtil.setCampaignId(campaignId);
                    dialerUtil.setEventDesc(eventDesc);
                    dialerUtil.setFolderId(redemptionFolderId);
                    dialerUtil.setIntervalDesc(intervalDesc);
                    dialerUtil.setIntervalId(intervalId);
                    dialerUtil.setIsLandLine(isLandLine);
                    dialerUtil.setIsMobile(isMobile);
                    dialerUtil.setMessageContext(messageContext);
                    dialerUtil.setMessageTypeId(messageTypeId);
                    dialerUtil.setServoId(servoId);
                    BigDecimal copay = instantRedemption.getPtOutOfPocket();
                    if (copay != null) {
                        dialerUtil.setCopay(copay.setScale(2, RoundingMode.CEILING).doubleValue());
                    }
                    dialerUtil.ivrCall(redemptionDAO);
                    isThankYouCallIntitiated = dialerUtil.isCallInitiated();

                }

            } else if (communicationMethod.equalsIgnoreCase("E")) {

                String patientEmail = instantRedemption.getCommunicationId();

                EmailOptInOut emailOptInOut = redemptionDAO.getEmailOptInOut(patientEmail, campaignId);

                if (emailOptInOut != null) {
                    optInOutFlag = emailOptInOut.getOptInOut();
                }

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    log.info(patientEmail + " has opted out from Campaign : " + campaignId);
                    log.info("System will continue");
                    return;
                }

                eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, Constants.EMAIL);

                if (eventHasFolderHasCampaignses == null
                        || eventHasFolderHasCampaignses.isEmpty()) {
                    log.info("No Redemption folder associated to this campaign (System will continue)");
                    return;
                }

                EmailRequest emailRequest = null;
                EmailResponse emailResponse = null;
                String emailFrom = "";
                String emailbody = "";
                String emailSubject = "";
                boolean flag = false;

                String unsubUrl = "";
                String actionUrl = "";
                String emailStatus = "";

                SmtpServerInfo smtpServerInfo = null;

                for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

                    eventsIds = "";

                    if (redemptionFolderId == ehfhc.getFolderId()) {
                        continue;
                    }

                    redemptionFolderId = ehfhc.getFolderId();

                    for (EventHasFolderHasCampaigns ehfhc1 : eventHasFolderHasCampaignses) {
                        if (ehfhc1.getFolderId() == ehfhc.getFolderId()) {
                            eventsIds += ehfhc1.getEventId() + ",";
                        }
                    }
                    eventsIds = eventsIds.substring(0, eventsIds.length() - 1);
                    eventDetails = redemptionDAO.getEventDetail(eventsIds, "InstantRedemption");
                    if (eventDetails != null && eventDetails.size() > 0) {
                        boolean send = redemptionDAO.isEventDetailVerifiedForInstantRedemption(eventDetails, instantRedemption.getId(), instantRedemption.getRedemptionId());

                        if (!send) {
                            log.info("Event Detail Criteria doesnt match for Redemption.");
                            continue;
                        }

                    }

                    String messageContext = Constants.REDEMPTION;
                    String responseTile = Constants.THANK_YOU;

                    campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, Constants.EMAIL);

                    if (campaignMessagesResponse == null) {
                        log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                        campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, Constants.EMAIL);

                        if (campaignMessagesResponse == null) {
                            log.info("Redemption campaignMessagesResponse is null.");
                            continue;
                        }

                    }

                    interval = campaignMessagesResponse.getIntervals();
                    intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                    intervalId = interval.getIntervalId();
                    intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                    eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, Constants.EMAIL);

                    int intervalValue = interval.getIntervalValue();
                    int intervalUnitInSecond = intervalsType.getUnitInSecond();
                    long secondsDelay = intervalValue * intervalUnitInSecond;

                    redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

                    campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, Constants.EMAIL);

                    if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                        log.info("No Redemption messages found for (System will continue)");
                        continue;
                    }
                    campaignMessages = campaignMessagesList.get(0);

                    messageId = campaignMessages.getMessageId();
                    messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

                    emailFrom = null;
                    emailbody = null;
                    emailSubject = null;

                    smtpServerInfo = campaign.getSmtpServerInfo();

                    log.info("Message Id : " + messageId);
                    log.info("Message Type Id : " + messageTypeId);
                    log.info("Email From : " + emailFrom);
                    log.info("Email Body : " + emailbody);
                    log.info("Email Subject : " + emailSubject);

                    if (emailbody == null || emailbody.equalsIgnoreCase("")) {
                        log.info("No Email body found System will continue");
                        log.info("Message Type ID: " + messageTypeId);
                        return;
                    }

                    if (smtpServerInfo == null) {
                        log.info("No SMTP found System will continue");
                        log.info("Message Type ID: " + messageTypeId);
                        return;
                    }

                    int sentEmail = redemptionDAO.getSendEmailCount(patientEmail, campaignId, redemptionFolderId, messageTypeId, instantRedemption.getFillDate());

                    if (sentEmail > 0) {
                        log.info("Redemption Email already send for " + patientEmail);
                        return;
                    }

                    unsubUrl = RedemptionUtil.prepareUnsubscribeURL(patientEmail, smtpServerInfo.getFromEmail());
                    actionUrl = RedemptionUtil.prepareActionURL(patientEmail, smtpServerInfo.getFromEmail());

                    emailbody = RedemptionUtil.placeHolders(emailbody, unsubUrl, actionUrl);

                    if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
                        emailbody = emailbody.replace("[_brand_]", campaignName);
                    }

                    double totalDrugCostToPharmacy = 0;
                    String patientTotalDrugCost = "";
                    NumberFormat numberFormat = new DecimalFormat(".00");

                    if (instantRedemption.getTotDrugCostPaidToPharmacy() != null) {
                        totalDrugCostToPharmacy = instantRedemption.getTotDrugCostPaidToPharmacy().doubleValue();
                        patientTotalDrugCost = numberFormat.format(totalDrugCostToPharmacy);

                    }

                    if (patientTotalDrugCost != null || !patientTotalDrugCost.equalsIgnoreCase("")) {
                        emailbody = emailbody.replace("XX.XX", patientTotalDrugCost);
                    }

                    log.info("Final Email :" + emailbody);

                    emailRequest = new EmailRequest();
                    emailRequest.setCampaignId(campaignId);
                    emailRequest.setCardholderDob(instantRedemption.getCardholderDob());
                    emailRequest.setClaimStatus(instantRedemption.getId().getClaimStatus());
                    emailRequest.setEmail(patientEmail);
                    emailRequest.setEmailBody(emailbody.getBytes());
                    emailRequest.setEmailSubject(emailSubject);
                    emailRequest.setFileName(instantRedemption.getFileName());
                    emailRequest.setFileTypeCode(instantRedemption.getFileTypeCode());
                    emailRequest.setFillDate(instantRedemption.getFillDate());
                    emailRequest.setFolderId(redemptionFolderId);
                    emailRequest.setMessageContext(messageContext);
                    emailRequest.setMessageTypeId(messageTypeId);
                    emailRequest.setNdcNumber(instantRedemption.getNdcNumber());
                    emailRequest.setRedemptionId(instantRedemption.getRedemptionId());
                    emailRequest.setRxGroupNumber(instantRedemption.getRxGroupNumber());
                    emailRequest.setSmtpId(campaign.getSmtpServerInfo().getSmtpId());
                    emailRequest.setIntervalId(intervalId);
                    emailRequest.setIntervalDescription(intervalDesc);
                    emailRequest.setEventDetail(eventDesc);

                    messageSent++;

                    EmailRedemptionSendingThread emailRedemptionSendingThread = new EmailRedemptionSendingThread();

                    emailRedemptionSendingThread.setCampaign(campaign);
                    emailRedemptionSendingThread.setEmail(patientEmail);
                    emailRedemptionSendingThread.setEmailBody(emailbody);
                    emailRedemptionSendingThread.setEmailRequest(emailRequest);
                    emailRedemptionSendingThread.setEmailSubject(emailSubject);
                    emailRedemptionSendingThread.setMessageSent(messageSent);
                    emailRedemptionSendingThread.setRedemptionDAO(redemptionDAO);
                    emailRedemptionSendingThread.setRedemptionLog(redemptionLog);
                    emailRedemptionSendingThread.setSecondsDelay(secondsDelay);
                    emailRedemptionSendingThread.setSmtpServerInfo(smtpServerInfo);
                    emailRedemptionSendingThread.setIsEm(true);

                    Thread redemptionThread = new Thread(emailRedemptionSendingThread);
                    redemptionThread.start();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
