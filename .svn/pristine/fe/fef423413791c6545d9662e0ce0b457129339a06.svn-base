package com.ssa.cms.remainder.refill;

import com.ssa.cms.common.Constants;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.AggregatorMessageResponse;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CampaignTrigger;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.DailyRedemption;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.InstantRedemptionId;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.RefillReminderPOJO;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.RefillReminderService;
import com.ssa.cms.thread.EmailRefillSendingThread;
import com.ssa.cms.thread.TextIntervalReminderThread;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.RefillUtil;
import com.ssa.cms.util.TextFlowUtil;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class PMSGenericRepeatRefillReminder {

    private static final Logger logger = Logger.getLogger(PMSGenericRepeatRefillReminder.class);

    public void sendRepeatRefillReminder(RefillReminderService refillReminderDAO) {
        try {

            String startTime = RefillUtil.formatDate(new Date());
            int totalEligibleRefills = 0;

            int daysBack = 0;
            String communicatoinMethod = "T";
            String phoneNumber = "";

            boolean redemptionExistIRF = false;

            int sentMessageCount = 0;
            InstantRedemption irf = null;
            InstantRedemptionId irfId = null;
            CampaignTrigger campaignTrigger = null;
            GatewayInfo gatewayInfo = null;
            String appName = "";
            String trigger = "";
            int campaignId = 0;
            String campaignName = "";
            String appCode = "";
            int messageSeqNo = 0;
            int emailReqNo = 0;
            String email = "";
            String emailFrom = "";
            String emailbody = "";
            String emailSubject = "";
            String isValidPhone = "";
            int count = 0;

            int folderId = 0;
            int messageTypeId = 0;
            int redemptionId = 0;
            int reminderDays = 0;
            int shortCode = 0;
            String communicationPath;
            DailyRedemption dailyRedemption = null;
            AggregatorMessageRequest aggregatorMessageRequest = null;
            AggregatorMessageResponse aggregatorMessageResponse = null;
            CampaignMessagesResponse campaignMessageResponses = null;
            List<CampaignMessages> campaignMessagesList = null;
            CampaignMessages campaignMessages = null;

            System.out.println("##################### Repeat Refill Reminder Starts on " + startTime + "##################################");
            List<Campaigns> campaignList = refillReminderDAO.getAllRepeatRefillCandidateActiveCampaign();
            if (campaignList == null || campaignList.isEmpty()) {
                logger.info("No active campaigns for repeat refill reminder. (System will return)");
                return;
            }
            logger.info("Repeat Refill reminder candidate campaigns : " + campaignList.size());
            for (Campaigns campaign : campaignList) {
                campaignId = campaign.getCampaignId();
                campaignName = campaign.getCampaignName();
                shortCode = campaign.getShortCodes().getShortCode();
                List<RefillReminderPOJO> list = refillReminderDAO.getTextRepeatRefillRecords(campaignId);

                if (list == null || list.isEmpty()) {
                    logger.info("Total Text Eligible Repeat Refills " + totalEligibleRefills);
                    // continue;
                }
                totalEligibleRefills = list.size();
                for (RefillReminderPOJO reminderPOJO : list) {

                    messageSeqNo = reminderPOJO.getMessageReqNo();
                    campaignId = reminderPOJO.getCampaignId();
                    folderId = reminderPOJO.getFolderId();
                    messageTypeId = reminderPOJO.getMessageTypeId();
                    redemptionId = reminderPOJO.getRedemptionId();
                    reminderDays = reminderPOJO.getReminderDays();
                    phoneNumber = reminderPOJO.getPhoneNumber();
                    communicationPath = reminderPOJO.getCommunicationPath();
                    isValidPhone = reminderPOJO.getIsValidPhone();
                    count = 0;
                    if (communicationPath.equalsIgnoreCase(Constants.OIVR)) {
                        communicatoinMethod = "I";
                    }
                    logger.info("Message Seq No : " + messageSeqNo);
                    logger.info("Campaign Id : " + campaignId);
                    logger.info("Folder Id : " + folderId);
                    logger.info("Message Type Id : " + messageTypeId);
                    logger.info("Redemption Id : " + redemptionId);
                    logger.info("Remidner Days : " + reminderDays);
                    logger.info("PhoneNumber : " + phoneNumber);
                    logger.info("communicatoinMethod : " + communicatoinMethod);
                    logger.info("communicationPath : " + communicationPath);

                    campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.REFILL, communicationPath);

                    if (campaignMessageResponses == null) {
                        logger.info("Repeat Refill campaignMessagesResponse is null.");
                        return;
                    }

                    int nextMessageTypeId = campaignMessageResponses.getRepeatMessageId();
                    System.out.println("Next Message Type Id : " + nextMessageTypeId);

                    if (nextMessageTypeId == 0) {
                        logger.info("Next message type id is 0. (Continue)");
                        continue;
                    }

                    if (messageTypeId == nextMessageTypeId) {
                        count = 1;
                    }

                    campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, nextMessageTypeId, communicationPath);

                    if (campaignMessageResponses == null) {
                        logger.info("CampaignMessagesResponse is null for next message. (Continue)");
                        continue;
                    }

                    Intervals interval = campaignMessageResponses.getIntervals();
                    IntervalsType intervalsType = campaignMessageResponses.getIntervals().getIntervalsType();

                    int intervalId = reminderPOJO.getIntervalId();
                    String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                    String eventDesc = refillReminderDAO.getEventsDescription(campaignId, folderId);

                    int intervalVal = interval.getIntervalValue();
                    int intervalUnitInSecond = intervalsType.getUnitInSecond();
                    long secondsDelay = intervalVal * intervalUnitInSecond;

                    campaignMessagesList = refillReminderDAO.getCampaignMessagesByCommunicationPath(campaignId, folderId, nextMessageTypeId, communicationPath);

                    if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                        logger.info("No messages found for (System will continue)");
                        continue;
                    }

                    campaignMessages = campaignMessagesList.get(0);

                    if (campaignMessages == null) {
                        logger.info("CampaignMessages object not found. (System will continue)");
                        continue;
                    }

                    String refillMessage = campaignMessages.getSmstext();
                    int messageId = campaignMessages.getMessageId();
                    messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
                    System.out.println("SMS Text : " + refillMessage);

                    daysBack = reminderDays;
                    dailyRedemption = new DailyRedemption();
                    dailyRedemption.setCardholderFirstName(reminderPOJO.getCardholderFirstName());
                    dailyRedemption.setCardholderLastName(reminderPOJO.getCardholderLastName());
                    dailyRedemption.setCardholderGender(reminderPOJO.getCardholderGender());
                    dailyRedemption.setCardholderDob(reminderPOJO.getCardholderDob());
                    dailyRedemption.setCampaignId(campaignId);

                    redemptionExistIRF = refillReminderDAO.redemptionExistsInIRF(dailyRedemption, daysBack, campaignId);

                    if (redemptionExistIRF) {
                        logger.info("Redemption already received in DRF for Phone Number : " + phoneNumber);
                        continue;
                    }

                    if (communicatoinMethod.equalsIgnoreCase("T")) {

                        if (refillMessage == null || refillMessage.length() == 0) {
                            logger.info("No Message found System will continue");
                            logger.info("Message Type ID: " + messageTypeId);
                            continue;
                        }

                        campaignTrigger = refillReminderDAO.getTriggerByCampaignId(campaignId);
                        trigger = campaignTrigger.getId().getKeyword();
                        appName = "PMS " + campaign.getCampaignName() + " repeat refill reminder";

                        gatewayInfo = refillReminderDAO.getGatewayInfo(shortCode);
                        appCode = gatewayInfo.getAppCode();

                        OptInOut optInOut = refillReminderDAO.checkOptInOut(phoneNumber, campaignId, shortCode);

                        String optInOutFlag = null;

                        if (optInOut != null) {
                            optInOutFlag = optInOut.getOptInOut();
                        }

                        if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                            logger.info("PhoneNumber : " + phoneNumber + " has opted opted out from campaign : " + campaignId);
                            continue;
                        }

                        String pharmacyName = reminderPOJO.getPharmacyName();

                        if (pharmacyName != null) {
                            if (pharmacyName.trim().length() > 10) {
                                pharmacyName = pharmacyName.substring(0, 9);
                            }
                            refillMessage = refillMessage.replaceAll("_PHARMACY_", pharmacyName);
                        }

                        String pharmacyPhone = reminderPOJO.getPharmacyPhone();

                        if (pharmacyPhone != null) {
                            String formatedPharmacyPhone = RedemptionUtil.formatPhone(pharmacyPhone);

                            refillMessage = refillMessage.replaceAll("###-###-####", formatedPharmacyPhone);
                        }
                        Order order = refillReminderDAO.getOrderByTransactionNumber(dailyRedemption.getId().getTransactionNumber());
                        Double payment = null;
                        String orderPlaceUrl = null;
                        Double copay = null;
                        if (order != null) {
                            payment = order.getPayment();
                            pharmacyName = order.getPharmacy().getTitle();
                            pharmacyPhone = order.getPharmacy().getPhone();
                            
                            orderPlaceUrl = Constants.APP_PATH + "/order/status/" + order.getId();
                        } else {
                            logger.info("Order value null against this TransactionNumber: " + dailyRedemption.getId().getTransactionNumber());
                        }
                        refillMessage = TextFlowUtil.prepareMessage(copay, null, pharmacyName, pharmacyPhone,
                                refillMessage, dailyRedemption.getPrescriptionNumber(), null, orderPlaceUrl, payment, null, null, null);
                        logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
                        if (refillMessage != null && refillMessage.trim().length() > 0) {
                            campaignMessages.setSmstext(refillMessage);
                            aggregatorMessageRequest = new AggregatorMessageRequest();

                            aggregatorMessageRequest.setCardholderDob(reminderPOJO.getCardholderDob());
                            aggregatorMessageRequest.setClaimStatus(reminderPOJO.getClaimStatus());
                            aggregatorMessageRequest.setFileTypeCode(reminderPOJO.getFileTypeCode());
                            aggregatorMessageRequest.setFolderId(folderId);
                            aggregatorMessageRequest.setMessageText(refillMessage);
                            aggregatorMessageRequest.setMessageTypeId(messageTypeId);
                            aggregatorMessageRequest.setCampaignId(campaignId);
                            aggregatorMessageRequest.setNdcNumber(reminderPOJO.getNdcNumber());
                            aggregatorMessageRequest.setPhoneNumber(phoneNumber);
                            aggregatorMessageRequest.setRedemptionId(redemptionId);
                            aggregatorMessageRequest.setRxGroupNumber(reminderPOJO.getRxGroupNumber());
                            aggregatorMessageRequest.setFillDate(reminderPOJO.getFillDate());
                            aggregatorMessageRequest.setMessageContext(Constants.MSG_CONTEXT_REPEAT_REFILL);
                            aggregatorMessageRequest.setFileName(reminderPOJO.getFileName());
                            aggregatorMessageRequest.setIntervalDescription(intervalDesc);
                            aggregatorMessageRequest.setIntervalId(intervalId);
                            aggregatorMessageRequest.setEventDetail(eventDesc);
                            aggregatorMessageRequest.setCommunicationPath(communicationPath);
                            aggregatorMessageRequest.setIsRepeat("Yes");

                            sentMessageCount = refillReminderDAO.retrieveMessageCountByTypeForRedemption(aggregatorMessageRequest);
                            logger.info("Sent Message Count : " + sentMessageCount);

                            if (sentMessageCount > 0) {

                                logger.info("Refill Message already sent for fill date " + RedemptionUtil.formatDateShort(reminderPOJO.getFillDate()));
                                logger.info("System will continue");
                                continue;
                            }

                            int endDateMarkCount = refillReminderDAO.markAggregatorMessageRequestEndDate(reminderPOJO.getMessageReqNo());

                            logger.info("End Date mark count : " + endDateMarkCount);

                        }

                        irfId = new InstantRedemptionId();
                        irf = new InstantRedemption();
                        irfId.setClaimStatus(reminderPOJO.getClaimStatus());
                        irf.setId(irfId);
                        irf.setCardholderDob(reminderPOJO.getCardholderDob());
                        irf.setFileTypeCode(reminderPOJO.getFileTypeCode());
                        irf.setNdcNumber(reminderPOJO.getNdcNumber());
                        irf.setFileName(reminderPOJO.getFileName());
                        irf.setRedemptionId(reminderPOJO.getRedemptionId());
                        irf.setFillDate(reminderPOJO.getFillDate());
                        irf.setRedemptionChannelId(reminderPOJO.getRedemptionChannelId());
                        irf.setRxGroupNumber(reminderPOJO.getRxGroupNumber());
                        irf.setCommunicationId(phoneNumber);

                        TextIntervalReminderThread intervalMessageThread = new TextIntervalReminderThread();
                        intervalMessageThread.setGatewayInfo(gatewayInfo);
                        intervalMessageThread.setAggregatorMessageRequest(aggregatorMessageRequest);
                        intervalMessageThread.setCampaignMessages(campaignMessages);
                        intervalMessageThread.setAppName("PMS " + campaign.getCampaignName() + " Repeat Refill Remidner");
                        intervalMessageThread.setProgramCode(trigger);
                        intervalMessageThread.setPhoneNumber(phoneNumber);
                        intervalMessageThread.setInputReferenceNumber(0);
                        intervalMessageThread.setSecondsDelay(secondsDelay);
                        intervalMessageThread.setCampaign(campaign);
                        intervalMessageThread.setRefillReminderDAO(refillReminderDAO);
                        intervalMessageThread.setFolderId(folderId);
                        intervalMessageThread.setMessageTypeId(messageTypeId);
                        intervalMessageThread.setCommunicationType(communicationPath);
                        intervalMessageThread.setIntervalId(intervalId);
                        intervalMessageThread.setIntervalDesc(intervalDesc);
                        intervalMessageThread.setEventDesc(eventDesc);
                        intervalMessageThread.setInstantRedemption(irf);
                        intervalMessageThread.setThreadType("repeat_refill");
                        Thread smsIntervalMessageThread = new Thread(intervalMessageThread);
                        smsIntervalMessageThread.start();

                    } else if (communicatoinMethod.equalsIgnoreCase("I")) {
                    }
                }

                list.clear();

                list = refillReminderDAO.getEmailRepeatRefillRecords(campaignId);

                if (list == null || list.size() == 0) {
                    System.out.println("Total Eligible Email Repeat Refills " + totalEligibleRefills);
                    continue;
                }

                for (RefillReminderPOJO reminderPOJO : list) {

                    emailReqNo = reminderPOJO.getEmailReqNo();
                    campaignId = reminderPOJO.getCampaignId();
                    folderId = reminderPOJO.getFolderId();
                    messageTypeId = reminderPOJO.getMessageTypeId();
                    redemptionId = reminderPOJO.getRedemptionId();
                    reminderDays = reminderPOJO.getReminderDays();
                    email = reminderPOJO.getEmail();
                    count = 0;

                    logger.info("Email Seq No : " + emailReqNo);
                    logger.info("Campaign Id : " + campaignId);
                    logger.info("Folder Id : " + folderId);
                    logger.info("Message Type Id : " + messageTypeId);
                    logger.info("Redemption Id : " + redemptionId);
                    logger.info("Remidner Days : " + reminderDays);
                    logger.info("Email : " + email);

                    EmailOptInOut emailOptInOut = refillReminderDAO.getEmailOptInOut(email, campaignId);

                    String optInOutFlag = null;

                    if (emailOptInOut != null) {
                        optInOutFlag = emailOptInOut.getOptInOut();
                    }

                    if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                        logger.info(email + " has opted out from Campaign : " + campaignId);
                        logger.info("System will continue");
                        continue;
                    }

                    campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, messageTypeId, Constants.EMAIL);
                    if (campaignMessageResponses == null) {
                        logger.info("CampaignMessagesResponse is null");
                        continue;
                    }

                    int nextMessageTypeId = campaignMessageResponses.getRepeatMessageId();
                    logger.info("Next Message Type Id : " + nextMessageTypeId);

                    if (nextMessageTypeId == 0) {
                        logger.info("Next message type id is 0. (Continue)");
                        continue;
                    }

                    if (messageTypeId == nextMessageTypeId) {
                        count = 1;
                    }

                    campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, nextMessageTypeId, Constants.EMAIL);

                    if (campaignMessageResponses == null) {
                        logger.info("CampaignMessagesResponse is null for next message. (Continue)");
                        continue;
                    }

                    Intervals interval = campaignMessageResponses.getIntervals();
                    IntervalsType intervalsType = campaignMessageResponses.getIntervals().getIntervalsType();

                    int intervalId = interval.getIntervalId();
                    String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                    String eventDesc = refillReminderDAO.getEventsDescription(campaignId, folderId);

                    int intervalVal = interval.getIntervalValue();
                    int intervalUnitInSecond = intervalsType.getUnitInSecond();
                    long secondsDelay = intervalVal * intervalUnitInSecond;

                    campaignMessagesList = refillReminderDAO.getCampaignMessagesByCommunicationPath(campaignId, folderId, nextMessageTypeId, Constants.EMAIL);

                    if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                        logger.info("No messages found for (System will continue)");
                        continue;
                    }

                    campaignMessages = campaignMessagesList.get(0);

                    if (campaignMessages == null) {
                        logger.info("CampaignMessages object not found. (System will continue)");
                        continue;
                    }

                    int messageId = campaignMessages.getMessageId();
                    messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

                    emailFrom = null;
                    emailSubject = null;
                    emailbody = null;
                    SmtpServerInfo smtpServerInfo = campaign.getSmtpServerInfo();

                    logger.info("Message Id : " + messageId);
                    logger.info("Message Type Id : " + messageTypeId);
                    logger.info("Email From : " + emailFrom);
                    logger.info("Email Body : " + emailbody);
                    logger.info("Email Subject : " + emailSubject);

                    if (emailbody == null || emailbody.equalsIgnoreCase("")) {
                        logger.info("No Email body found System will continue");
                        logger.info("Message Type ID: " + messageTypeId);
                        continue;
                    }

                    if (smtpServerInfo == null) {
                        logger.info("No SMTP serve found System will continue");
                        logger.info("Message Type ID: " + messageTypeId);
                        continue;
                    }

                    daysBack = reminderDays;
                    dailyRedemption = new DailyRedemption();
                    dailyRedemption.setCardholderFirstName(reminderPOJO.getCardholderFirstName());
                    dailyRedemption.setCardholderLastName(reminderPOJO.getCardholderLastName());
                    dailyRedemption.setCardholderGender(reminderPOJO.getCardholderGender());
                    dailyRedemption.setCardholderDob(reminderPOJO.getCardholderDob());
                    dailyRedemption.setCampaignId(campaignId);

                    redemptionExistIRF = refillReminderDAO.redemptionExistsInIRF(dailyRedemption, daysBack, campaignId);

                    if (redemptionExistIRF) {
                        logger.info("Redemption already received in DRF for Email : " + email);
                        continue;
                    }

                    int sentEmailCount = refillReminderDAO.sentEmailCountRefill(email, campaignId, folderId, messageTypeId);

                    if (sentEmailCount > count) {
                        logger.info("Email already sent to Email : " + email);
                        continue;
                    }

                    String pharmacyName = reminderPOJO.getPharmacyName();

                    if (pharmacyName != null) {
                        if (pharmacyName.trim().length() > 10) {
                            pharmacyName = pharmacyName.substring(0, 9);
                        }
                        emailbody = emailbody.replaceAll("_PHARMACY_", pharmacyName);
                    }

                    String pharmacyPhone = reminderPOJO.getPharmacyPhone();

                    if (pharmacyPhone != null) {
                        String formatedPharmacyPhone = RedemptionUtil.formatPhone(pharmacyPhone);
                        emailbody = emailbody.replaceAll("###-###-####", formatedPharmacyPhone);
                    }

                    String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(email, smtpServerInfo.getFromEmail());
                    String actionUrl = RedemptionUtil.prepareActionURL(email, smtpServerInfo.getFromEmail());

                    emailbody = RedemptionUtil.placeHolders(emailbody, unsubUrl, actionUrl);

                    campaignName = campaign.getCampaignName();

                    if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
                        emailbody = emailbody.replace("[_brand_]", campaignName);
                    }

                    EmailRequest emailRequest = new EmailRequest();
                    emailRequest.setCampaignId(campaignId);
                    emailRequest.setCardholderDob(reminderPOJO.getCardholderDob());
                    emailRequest.setClaimStatus(reminderPOJO.getClaimStatus());
                    emailRequest.setEmail(email);
                    emailRequest.setEmailBody(emailbody.getBytes());
                    emailRequest.setEmailSubject(emailSubject);
                    emailRequest.setFileName(reminderPOJO.getFileName());
                    emailRequest.setFileTypeCode(reminderPOJO.getFileTypeCode());
                    emailRequest.setFillDate(reminderPOJO.getFillDate());
                    emailRequest.setFolderId(folderId);
                    emailRequest.setMessageContext(Constants.MSG_CONTEXT_REPEAT_REFILL);
                    emailRequest.setMessageTypeId(messageTypeId);
                    emailRequest.setNdcNumber(reminderPOJO.getNdcNumber());
                    emailRequest.setRedemptionId(reminderPOJO.getRedemptionId());
                    emailRequest.setRxGroupNumber(reminderPOJO.getRxGroupNumber());
                    emailRequest.setSmtpId(campaign.getSmtpServerInfo().getSmtpId());
                    emailRequest.setIntervalDescription(intervalDesc);
                    emailRequest.setIntervalId(intervalId);
                    emailRequest.setEventDetail(eventDesc);

                    logger.info("Final Email :" + emailbody);

                    EmailRefillSendingThread emailRefillSendingThread = new EmailRefillSendingThread();

                    emailRefillSendingThread.setCampaign(campaign);
                    emailRefillSendingThread.setEmail(email);
                    emailRefillSendingThread.setEmailBody(emailbody);
                    emailRefillSendingThread.setEmailRequest(emailRequest);
                    emailRefillSendingThread.setEmailSubject(emailSubject);
                    emailRefillSendingThread.setRefillReminderDAO(refillReminderDAO);
                    emailRefillSendingThread.setSecondsDelay(secondsDelay);
                    emailRefillSendingThread.setSmtpServerInfo(smtpServerInfo);

                    Thread thread = new Thread(emailRefillSendingThread);
                    thread.start();

                }//email record for loop

            }//campaign loop

            //get all eligible records from Aggreegate Message Request
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
