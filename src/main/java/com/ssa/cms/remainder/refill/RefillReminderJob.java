package com.ssa.cms.remainder.refill;

import com.ssa.cms.common.Constants;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CampaignTrigger;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.DailyRedemption;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.EventDetail;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.InstantRedemptionId;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.IvroptInOut;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.PhoneValidationService;
import com.ssa.cms.service.RefillReminderService;
import com.ssa.cms.thread.EmailRefillSendingThread;
import com.ssa.cms.thread.TextIntervalReminderThread;
import com.ssa.cms.util.EmailFlowUtil;
import com.ssa.cms.util.EmailSender;
import com.ssa.cms.util.IVRCallDialerUtil;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.RefillUtil;
import com.ssa.cms.util.TextFlowUtil;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class RefillReminderJob {

    private static final Logger logger = Logger.getLogger(RefillReminderJob.class);

    @Autowired
    RefillReminderService refillReminderService;

//    @Scheduled(cron = "${GENERIC_REFILL_REMINDERS_CRON}")
    protected void executeInternal() {
        sendRefillAlerts(refillReminderService);
    }

    public void sendRefillAlerts(RefillReminderService refillReminderService) {
        try {
            this.refillReminderService = refillReminderService;

            List<Campaigns> campaignList = refillReminderService.getAllRefillCandidateActiveCampaign();

            if (campaignList == null || campaignList.isEmpty()) {
                logger.info("No active campaigns for refill reminder. (System will return)");
                return;
            }

            String PVURL = refillReminderService.getURL(Constants.PVURL);
            PhoneValidationService phoneValidationService = new PhoneValidationService(PVURL);

            for (Campaigns campaign : campaignList) {

                int campaignId = campaign.getCampaignId();
                String campaignName = campaign.getCampaignName();
                int intervalId = campaign.getRefillProcessTiming();
                String isRefill = campaign.getIsRefill();

                if (!isRefill.equalsIgnoreCase("Yes")) {
                    logger.info("Refill is not attached with campaign.");
                    continue;
                }

                Intervals interval = refillReminderService.getIntervalById(intervalId);

                if (interval == null) {
                    logger.info("No interval found by id : " + intervalId);
                    continue;
                }
                int intervalValue = interval.getIntervalValue();
                logger.info("Interval Value : " + intervalValue);

                List<DailyRedemption> list = refillReminderService.getDRFRefillReminderRecordsByCampaignId(campaignId, intervalValue);
                if (list == null || list.isEmpty()) {
                    logger.info("No drf records found (System will continue) Campaign ID: " + campaignId);
                    continue;
                }

                int totalEligibleRefills = list.size();
                logger.info("Remidner record count  : " + totalEligibleRefills + " for campaign : " + campaignId);

                CampaignTrigger campaignTrigger = refillReminderService.getTriggerByCampaignId(campaignId);
                String trigger = campaignTrigger.getId().getKeyword();
                int shortCode = campaign.getShortCodes().getShortCode();

                GatewayInfo gatewayInfo = refillReminderService.getGatewayInfo(shortCode);
                String appName = "PMS " + campaignName + " refill reminder";
                String appCode = gatewayInfo.getAppCode();

                List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses = refillReminderService.getEventHasFolderHasCampaign(campaignId);

                if (eventHasFolderHasCampaignses == null) {
                    logger.info("No folder associated to this campaign (System will continue)");
                    continue;
                }

                int folderId = 0;
                String path = "";
                for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {
                    if (path.equals(ehfhc.getCommunicationPath()) && folderId == ehfhc.getFolderId()) {
                        continue;
                    }

                    if (processFolder(folderId, ehfhc, eventHasFolderHasCampaignses, list, refillReminderService,
                            intervalValue, campaignId, phoneValidationService, appCode, trigger, shortCode, gatewayInfo, appName, campaign)) {
                        continue;
                    }
                    path = ehfhc.getCommunicationPath();
                    folderId = ehfhc.getFolderId();
                }
            } // for (Campaigns campaign : campaignList)

        } catch (Exception e) {
            logger.error("Exception:: -> sendRefillAlerts", e);
        }

    }

    private boolean processFolder(int folderId, EventHasFolderHasCampaigns ehfhc, List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses, List<DailyRedemption> list, RefillReminderService refillReminderService1, int intervalValue, int campaignId, PhoneValidationService phoneValidationService, String appCode, String trigger, int shortCode, GatewayInfo gatewayInfo, String appName, Campaigns campaign) throws Exception {
        Intervals interval;
        int intervalId;
        if (folderId == ehfhc.getFolderId()) {
            return true;
        }
        folderId = ehfhc.getFolderId();
        List<CampaignMessages> campaignMessagesList;
        String eventsIds = "";
        for (EventHasFolderHasCampaigns ehfhc1 : eventHasFolderHasCampaignses) {
            if (ehfhc1.getFolderId() == ehfhc.getFolderId()) {
                eventsIds += ehfhc1.getEventId() + ",";
            }
        }

        eventsIds = eventsIds.substring(0, eventsIds.length() - 1);
        for (DailyRedemption drf : list) {
            logger.info("TransactionNumber is: " + drf.getId().getTransactionNumber());
            String communicationId = drf.getCommunicationId();
            String communicatoinMethod = drf.getCommunicationMethod();
            logger.info("Communicaiton method: " + communicatoinMethod);
            List<EventDetail> eventDetails = refillReminderService.getEventDetail(eventsIds, "DailyRedemption");
            InstantRedemptionId instantRedemptionId = new InstantRedemptionId();
            instantRedemptionId.setTransactionNumber(drf.getId().getTransactionNumber());
            instantRedemptionId.setClaimStatus(drf.getId().getClaimStatus());
            if (eventDetails != null && eventDetails.size() > 0) {
                boolean send = refillReminderService1.isEventDetailVerifiedForDailyRedemption(eventDetails, drf.getId());
                if (!send) {
                    logger.info("Event Detail Criteria doesnt match for Refill.");
                    continue;
                }
            }
            if (communicatoinMethod == null || communicatoinMethod.trim().length() == 0) {
                logger.info("No communicaiton method defined (System will continue)");
                continue;
            }
            communicatoinMethod = communicatoinMethod.trim();
            if (!(communicatoinMethod.equalsIgnoreCase("T") || communicatoinMethod.equalsIgnoreCase("E") || communicatoinMethod.equalsIgnoreCase("I"))) {
                logger.info("Communication method has not value of E,I or T (System will continue)");
                continue;
            }
            int daysSupply = drf.getDaysSupply();
            int daysBack = 1;
            if (daysSupply > 20) {
                daysBack = (daysSupply * (intervalValue / 100)) - 10;
            } else if (daysSupply > 10) {
                daysBack = (daysSupply * (intervalValue / 100)) - 5;
            } else if (daysSupply > 5) {
                daysBack = (daysSupply * (intervalValue / 100)) - 2;
            }
            logger.info("Days Back : " + daysBack);
            boolean redemptionExistDRF = refillReminderService1.redemptionExistsInIRF(drf, daysBack, campaignId);
            if (redemptionExistDRF) {
                logger.info("Redemption already received in DRF for communicationid : " + communicationId);
                continue;
            }
            Order order = refillReminderService.getOrderDetailByTransactionNumber(drf.getCardholderFirstName(), drf.getCardholderLastName(), campaignId, drf.getCardholderDob());
            if (communicatoinMethod.equalsIgnoreCase("T")) {
                String communicationPath = Constants.SMS;
                CampaignMessagesResponse campaignMessagesResponse;
                logger.info("Folder id is: " + folderId);
                if (order == null) {
                    logger.info("Order is null ");
                    campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.DIRECT_REFILLED, communicationPath);

                } else {
                    campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.REFILL, communicationPath);

                }
                if (campaignMessagesResponse == null) {
                    logger.info("Refill campaignMessagesResponse for MMS is null.");
                    campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponse(campaignId, folderId, communicationPath);
                    if (campaignMessagesResponse == null) {
                        logger.info("Refill campaignMessagesResponse is null.");
                        continue;
                    }
                }
                interval = campaignMessagesResponse.getIntervals();
                IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();
                intervalId = interval.getIntervalId();
                String intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                String eventDesc = refillReminderService1.getEventsDescription(campaignId, folderId);
                int intervalVal = interval.getIntervalValue();
                int intervalUnitInSecond = intervalsType.getUnitInSecond();
                long secondsDelay = intervalVal * intervalUnitInSecond;
                int refillMessageTypeId = campaignMessagesResponse.getMessageTypeId();
                campaignMessagesList = refillReminderService1.getCampaignMessagesByCommunicationPath(campaignId, folderId, refillMessageTypeId, communicationPath);
                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    logger.info("No messages found for (System will continue)");
                    logger.info("Folder ID: " + ehfhc.getFolderId());
                    continue;
                }
                CampaignMessages campaignMessages = campaignMessagesList.get(0);
                String refillMessage = campaignMessages.getSmstext();
                int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
                int messageId = campaignMessages.getMessageId();
                String patientMessage = refillMessage;
                if (patientMessage == null || patientMessage.length() == 0) {
                    logger.info("No Message found System will continue");
                    logger.info("Message Type ID: " + messageTypeId);
                    continue;
                }
                boolean phoneValidity = phoneValidationService.checkPhoneValidity(communicationId, appCode, trigger);
                if (!phoneValidity) {
                    logger.info("Phone number : " + communicationId + " not a valid cell phone. (Continue) ");
                    continue;
                }
                OptInOut optInOut = refillReminderService1.checkOptInOut(communicationId, campaignId, shortCode);
                String optInOutFlag = null;
                if (optInOut != null) {
                    optInOutFlag = optInOut.getOptInOut();
                }
                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    logger.info("PhoneNumber : " + communicationId + " has opted opted out from campaign : " + campaignId);
                    continue;
                }

                Double payment;
                String pharmacyName;
                String pharmacyPhone;
                if (order != null) {
                    payment = order.getPayment();
                    pharmacyName = order.getPharmacy().getTitle();
                    pharmacyPhone = order.getPharmacy().getPhone();
                    String orderPlaceUrl = Constants.APP_PATH + "/order/status/" + order.getId();
                    patientMessage = TextFlowUtil.prepareMessage(null, null, pharmacyName, pharmacyPhone,
                            patientMessage, drf.getPrescriptionNumber(), null, orderPlaceUrl, payment, null, order.getCardType(), order.getCardNumber());
                } else {
                    logger.info("Order value null against this TransactionNumber: " + drf.getId().getTransactionNumber());
                    patientMessage = TextFlowUtil.prepareMessage(null, null, drf.getPharmacyName(), drf.getPharmacyPhone(),
                            patientMessage, drf.getPrescriptionNumber(), null, null, null, null, null, null);
                }

                logger.info("Finally Message : " + patientMessage);
                logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
                InstantRedemptionId irfId = new InstantRedemptionId();
                InstantRedemption irf = new InstantRedemption();
                irfId.setClaimStatus(drf.getId().getClaimStatus());
                irf.setId(irfId);
                BeanUtils.copyProperties(drf.getId(), irfId);
                BeanUtils.copyProperties(drf, irf, new String[]{"id"});
                if (patientMessage != null && patientMessage.trim().length() > 0) {
                    campaignMessages.setSmstext(patientMessage);
                    AggregatorMessageRequest aggregatorMessageRequest = setAggregatorMessageRequest(drf, communicationId, ehfhc, campaignId, messageTypeId,
                            refillMessage, intervalDesc, eventDesc, intervalId, communicationPath);
                    int sentMessageCount = refillReminderService1.retrieveMessageCountByTypeForRedemption(aggregatorMessageRequest);
                    logger.info("Sent Message Count : " + sentMessageCount);
                    if (sentMessageCount > 0) {
                        logger.info("Refill Message already sent (for fill date " + RefillUtil.formatDateShort(drf.getFillDate()));
                        continue;
                    }
                    logger.info("Message Id : " + messageId);
                    logger.info("Message Type Id : " + messageTypeId);
                    logger.info("Welcome Message : " + refillMessage);
                    startTextIntervalReminderThread(gatewayInfo, aggregatorMessageRequest, campaignMessages, appName, trigger, communicationId, secondsDelay, campaign, refillReminderService1, ehfhc, messageTypeId, communicationPath, intervalId, intervalDesc, eventDesc, irf, drf);
                }
            } else if (communicatoinMethod.equalsIgnoreCase("E")) {
                logger.info("Transaction Number: " + drf.getId().getTransactionNumber());
                logger.info("Order is : " + order);
                CampaignMessagesResponse campaignMessagesResponse;
                if (order != null) {
                    logger.info("Send Refill Response message.");
                    campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.REFILL, Constants.EMAIL);
                } else {
                    logger.info("Send Direct Refill Response message.");
                    campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.DIRECT_REFILLED, Constants.EMAIL);
                }

                if (campaignMessagesResponse == null) {
                    logger.info("Refill Response message not found.");
                    continue;
                }

                interval = campaignMessagesResponse.getIntervals();
                IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                intervalId = interval.getIntervalId();
                String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                String eventDesc = refillReminderService1.getEventsDescription(campaignId, folderId);

                int intervalVal = interval.getIntervalValue();
                int intervalUnitInSecond = intervalsType.getUnitInSecond();
                long secondsDelay = intervalVal * intervalUnitInSecond;

                int refillMessageTypeId = campaignMessagesResponse.getMessageTypeId();

                campaignMessagesList = refillReminderService1.getCampaignMessagesByCommunicationPath(campaignId, folderId, refillMessageTypeId, Constants.EMAIL);
                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    logger.info("No messages found for (System will continue)");
                    logger.info("Folder ID: " + ehfhc.getFolderId());
                    continue;
                }

                CampaignMessages campaignMessages = campaignMessagesList.get(0);
                int messageId = campaignMessages.getMessageId();
                int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

                String emailFrom = null;
                String emailSubject = null;
                String emailBody = null;
                emailBody = EmailSender.makeEmailBody(emailBody);

                EmailOptInOut emailOptInOut = refillReminderService1.getEmailOptInOut(communicationId, campaignId);

                String optInOutFlag = null;

                if (emailOptInOut != null) {
                    optInOutFlag = emailOptInOut.getOptInOut();
                }

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    logger.info(communicationId + " has opted out from Campaign : " + campaignId);
                    logger.info("System will continue");
                    continue;
                }

                if (emailFrom == null || emailFrom.equalsIgnoreCase("")) {
                    logger.info("No Email from found System will continue");
                    logger.info("Message Type ID: " + messageTypeId);
                    continue;
                }

                if (emailBody == null || emailBody.equalsIgnoreCase("")) {
                    logger.info("No Email body found System will continue");
                    logger.info("Message Type ID: " + messageTypeId);
                    continue;
                }

                SmtpServerInfo smtpServerInfo = campaign.getSmtpServerInfo();

                if (smtpServerInfo == null) {
                    logger.info("No SMTP found System will continue");
                    logger.info("Message Type ID: " + messageTypeId);
                    continue;
                }

                int sentEmailCount = refillReminderService1.sentEmailCountRefill(communicationId, campaignId, ehfhc.getFolderId(), messageTypeId);

                if (sentEmailCount > 0) {
                    logger.info("Email already sent to Email : " + communicationId);
                    continue;
                }

                String pharmacyName;
                String pharmacyPhone;
                Double payment = null;
                Double copay = null;
                String cardType = null;
                String cardNumber=null;
                if (order != null) {
                    payment = order.getPayment();
                    pharmacyName = order.getPharmacy().getTitle();
                    pharmacyPhone = order.getPharmacy().getPhone();
                    //copay = order.getCopay();
                    cardType = order.getCardType();
                    cardNumber=order.getCardNumber();
                    logger.info("Out of Pocket value is: " + drf.getPtOutOfPocket());
                } else {
                    pharmacyName = drf.getPharmacyName();
                    pharmacyPhone = drf.getPharmacyPhone();
                    logger.info("Order value null: " + order);
                }

                String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(communicationId, smtpServerInfo.getFromEmail());
                String acceptanceUrl = RedemptionUtil.prepareAcceptanceURL(communicationId, smtpServerInfo.getFromEmail());
                String placeOrderUrl = RedemptionUtil.preparePlaceOrderURL(drf.getId().getTransactionNumber());
                emailBody = EmailFlowUtil.prepareMessage(copay, drf.getPtOutOfPocket(), pharmacyName, pharmacyPhone, emailBody, drf.getPrescriptionNumber(), null, acceptanceUrl, payment, unsubUrl, null, placeOrderUrl, null, cardType, cardNumber, null);

                String campaignName = campaign.getCampaignName();

                if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
                    emailBody = emailBody.replace("[_brand_]", campaignName);
                }

                logger.info("Final Email :" + emailBody);

                EmailRequest emailRequest = setEmailRequest(campaignId, drf, communicationId, emailBody, emailSubject, ehfhc, messageTypeId, campaign, intervalDesc, intervalId, eventDesc);

                startEmailRefillSendingThread(campaign, communicationId, emailBody, emailRequest, emailSubject, refillReminderService1, secondsDelay, smtpServerInfo);

            } else if (communicatoinMethod.equalsIgnoreCase("I")) {

                boolean isMobile = false;
                boolean isLandLine = false;
                String optInOutFlag = null;
                String phoneNumber = communicationId;
                if (drf.getIsValidPhone().equalsIgnoreCase("Yes")) {
                    isMobile = true;
                }
                if (drf.getIsValidPhone().equalsIgnoreCase("L")) {
                    isLandLine = true;
                }
                if (isLandLine == false && isMobile == false) {
                    logger.info(phoneNumber + " PhoneNumber neither Mobile nor Landline : " + campaignId);
                    logger.info("System will return");
                    continue;
                }
                IvroptInOut optInOut = refillReminderService1.getIVROptInOut(campaignId, phoneNumber);

                if (optInOut != null) {
                    optInOutFlag = optInOut.getOptInOut();
                }

                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    logger.info(phoneNumber + " has opted out from Campaign : " + campaignId);
                    logger.info("System will return");
                    continue;
                }
                CampaignMessagesResponse campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.REFILL, Constants.OIVR);

                if (campaignMessagesResponse == null) {
                    logger.info("Refill Response message not found.");
                    continue;
                }

                interval = campaignMessagesResponse.getIntervals();
                IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                intervalId = interval.getIntervalId();
                String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                String eventDesc = refillReminderService1.getEventsDescription(campaignId, folderId);

                int intervalVal = interval.getIntervalValue();
                int intervalUnitInSecond = intervalsType.getUnitInSecond();
                long secondsDelay = intervalVal * intervalUnitInSecond;
                logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
                Thread.sleep(1000 * secondsDelay);

                int refillMessageTypeId = campaignMessagesResponse.getMessageTypeId();

                campaignMessagesList = refillReminderService1.getCampaignMessagesByCommunicationPath(campaignId, folderId, refillMessageTypeId, Constants.OIVR);
                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    logger.info("No messages found for (System will continue)");
                    logger.info("Folder ID: " + ehfhc.getFolderId());
                    continue;
                }

                CampaignMessages campaignMessages = campaignMessagesList.get(0);
                int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

                String servoId = null;
                if (servoId == null || servoId.length() == 0) {
                    logger.info("No Survo Id found for (System will continue)");
                    continue;
                }
                InstantRedemptionId id = new InstantRedemptionId();
                id.setClaimStatus(drf.getId().getClaimStatus());
                id.setTransactionNumber(drf.getId().getTransactionNumber());
                InstantRedemption instantRedemptionFile = new InstantRedemption();
                instantRedemptionFile.setId(id);
                BeanUtils.copyProperties(drf, instantRedemptionFile, new String[]{"id"});
                instantRedemptionFile.setOtherPayerId1(drf.getOtherPayerId());
                instantRedemptionFile.setTimeStamp(new Date());

                IVRCallDialerUtil dialerUtil = new IVRCallDialerUtil();
                dialerUtil.setInstantRedemption(instantRedemptionFile);
                dialerUtil.setCampaignId(campaignId);
                dialerUtil.setEventDesc(eventDesc);
                dialerUtil.setFolderId(folderId);
                dialerUtil.setIntervalDesc(intervalDesc);
                dialerUtil.setIntervalId(intervalId);
                dialerUtil.setIsLandLine(isLandLine);
                dialerUtil.setIsMobile(isMobile);
                dialerUtil.setMessageContext(Constants.MSG_CONTEXT_REFILL);
                dialerUtil.setMessageTypeId(messageTypeId);
                dialerUtil.setServoId(servoId);
                //dialerUtil.setCopay(order.getCopay());
                dialerUtil.setCardType(order.getCardType());
                dialerUtil.setCardEnding4(order.getCardNumber().substring(order.getCardNumber().length() - 4));
                dialerUtil.ivrCall(refillReminderService1);
            }
        }
        return false;
    }

    private void startTextIntervalReminderThread(GatewayInfo gatewayInfo, AggregatorMessageRequest aggregatorMessageRequest, CampaignMessages campaignMessages, String appName, String trigger, String communicationId, long secondsDelay, Campaigns campaign, RefillReminderService refillReminderService1, EventHasFolderHasCampaigns ehfhc, int messageTypeId, String communicationPath, int intervalId, String intervalDesc, String eventDesc, InstantRedemption irf, DailyRedemption drf) {
        TextIntervalReminderThread intervalMessageThread = new TextIntervalReminderThread();
        intervalMessageThread.setGatewayInfo(gatewayInfo);
        intervalMessageThread.setAggregatorMessageRequest(aggregatorMessageRequest);
        intervalMessageThread.setCampaignMessages(campaignMessages);
        intervalMessageThread.setAppName(appName);
        intervalMessageThread.setProgramCode(trigger);
        intervalMessageThread.setPhoneNumber(communicationId);
        intervalMessageThread.setInputReferenceNumber(0);
        intervalMessageThread.setSecondsDelay(secondsDelay);
        intervalMessageThread.setCampaign(campaign);
        intervalMessageThread.setRefillReminderDAO(refillReminderService1);
        intervalMessageThread.setFolderId(ehfhc.getFolderId());
        intervalMessageThread.setMessageTypeId(messageTypeId);
        intervalMessageThread.setCommunicationType(communicationPath);
        intervalMessageThread.setIntervalId(intervalId);
        intervalMessageThread.setIntervalDesc(intervalDesc);
        intervalMessageThread.setEventDesc(eventDesc);
        intervalMessageThread.setInstantRedemption(irf);
        intervalMessageThread.setThreadType("refill");
        intervalMessageThread.setDailyRedemption(drf);
        Thread smsIntervalMessageThread = new Thread(intervalMessageThread);
        smsIntervalMessageThread.start();
    }

    private AggregatorMessageRequest setAggregatorMessageRequest(DailyRedemption drf, String communicationId, EventHasFolderHasCampaigns ehfhc, int campaignId, int messageTypeId, String refillMessage, String intervalDesc, String eventDesc, int intervalId, String communicationPath) {
        AggregatorMessageRequest aggregatorMessageRequest = new AggregatorMessageRequest();
        aggregatorMessageRequest.setRxGroupNumber(drf.getRxGroupNumber());
        aggregatorMessageRequest.setPhoneNumber(communicationId);
        aggregatorMessageRequest.setCardholderDob(drf.getCardholderDob());
        aggregatorMessageRequest.setFillDate(drf.getFillDate());
        aggregatorMessageRequest.setNdcNumber(drf.getNdcNumber());
        aggregatorMessageRequest.setClaimStatus(drf.getId().getClaimStatus());
        aggregatorMessageRequest.setFolderId((ehfhc.getFolderId()));
        aggregatorMessageRequest.setCampaignId(campaignId);
        aggregatorMessageRequest.setRedemptionId(drf.getRedemptionId());
        aggregatorMessageRequest.setMessageTypeId((messageTypeId));
        aggregatorMessageRequest.setFileTypeCode(drf.getFileTypeCode());
        aggregatorMessageRequest.setMessageText(refillMessage);
        aggregatorMessageRequest.setMessageContext(Constants.MSG_CONTEXT_REFILL);
        aggregatorMessageRequest.setFileName(drf.getFileName());
        aggregatorMessageRequest.setIntervalDescription(intervalDesc);
        aggregatorMessageRequest.setEventDetail(eventDesc);
        aggregatorMessageRequest.setIntervalId(intervalId);
        aggregatorMessageRequest.setCommunicationPath(communicationPath);
        return aggregatorMessageRequest;
    }

    private void startEmailRefillSendingThread(Campaigns campaign, String communicationId, String emailBody, EmailRequest emailRequest, String emailSubject, RefillReminderService refillReminderService1, long secondsDelay, SmtpServerInfo smtpServerInfo) {
        EmailRefillSendingThread emailRefillSendingThread = new EmailRefillSendingThread();
        emailRefillSendingThread.setCampaign(campaign);
        emailRefillSendingThread.setEmail(communicationId);
        emailRefillSendingThread.setEmailBody(emailBody);
        emailRefillSendingThread.setEmailRequest(emailRequest);
        emailRefillSendingThread.setEmailSubject(emailSubject);
        emailRefillSendingThread.setRefillReminderDAO(refillReminderService1);
        emailRefillSendingThread.setSecondsDelay(secondsDelay);
        emailRefillSendingThread.setSmtpServerInfo(smtpServerInfo);

        Thread thread = new Thread(emailRefillSendingThread);
        thread.start();
    }

    private EmailRequest setEmailRequest(int campaignId, DailyRedemption drf, String communicationId, String emailBody, String emailSubject, EventHasFolderHasCampaigns ehfhc, int messageTypeId, Campaigns campaign, String intervalDesc, int intervalId, String eventDesc) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setCampaignId(campaignId);
        emailRequest.setCardholderDob(drf.getCardholderDob());
        emailRequest.setClaimStatus(drf.getId().getClaimStatus());
        emailRequest.setEmail(communicationId);
        emailRequest.setEmailBody(emailBody.getBytes());
        emailRequest.setEmailSubject(emailSubject);
        emailRequest.setFileName(drf.getFileName());
        emailRequest.setFileTypeCode(drf.getFileTypeCode());
        emailRequest.setFillDate(drf.getFillDate());
        emailRequest.setFolderId(ehfhc.getFolderId());
        emailRequest.setMessageContext(Constants.MSG_CONTEXT_REFILL);
        emailRequest.setMessageTypeId(messageTypeId);
        emailRequest.setNdcNumber(drf.getNdcNumber());
        emailRequest.setRedemptionId(drf.getRedemptionId());
        emailRequest.setRxGroupNumber(drf.getRxGroupNumber());
        emailRequest.setSmtpId(campaign.getSmtpServerInfo().getSmtpId());
        emailRequest.setIntervalDescription(intervalDesc);
        emailRequest.setIntervalId(intervalId);
        emailRequest.setEventDetail(eventDesc);

        return emailRequest;
    }
}
