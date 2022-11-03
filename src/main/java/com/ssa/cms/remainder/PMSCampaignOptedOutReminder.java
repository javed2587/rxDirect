package com.ssa.cms.remainder;

import com.ssa.cms.common.Constants;
import com.ssa.cms.enumeration.StatusEnum;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.CampaignEmailRequest;
import com.ssa.cms.model.CampaignMessageRequest;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.CustomerRequest;
import com.ssa.cms.model.DailyRedemption;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.Event;
import com.ssa.cms.model.EventDetail;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.InstantRedemptionId;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.PMSEmailFlowService;
import com.ssa.cms.service.PMSTextFlowService;
import com.ssa.cms.service.PhoneValidationService;
import com.ssa.cms.service.RefillReminderService;
import com.ssa.cms.thread.EmailOptinSendingThread;
import com.ssa.cms.thread.EmailRefillSendingThread;
import com.ssa.cms.thread.TextIntervalMessageRequestThread;
import com.ssa.cms.thread.TextIntervalReminderThread;
import com.ssa.cms.util.EmailFlowUtil;
import com.ssa.cms.util.EmailSender;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.RefillUtil;
import com.ssa.cms.util.TextFlowUtil;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PMSCampaignOptedOutReminder {

    private static final Logger logger = Logger.getLogger(PMSCampaignOptedOutReminder.class);
    @Autowired
    RefillReminderService refillReminderService;
    @Autowired
    PMSEmailFlowService emailFlowService;
    @Autowired
    PMSTextFlowService textFlowService;

    public void sendCampaignOptedOutAlerts() {
        try {
            List<Campaigns> campaignList = emailFlowService.getAllOptedOutCampaigns();

            if (campaignList == null || campaignList.isEmpty()) {
                logger.info("No Opteded out campaigns found. (System will return)");
                return;
            }

//            String PVURL = refillReminderService.getURL(Constants.PVURL);
//            PhoneValidationService phoneValidationService = new PhoneValidationService(PVURL);
            for (Campaigns campaign : campaignList) {

                int campaignId = campaign.getCampaignId();
                String campaignName = campaign.getCampaignName();

                List<EmailOptInOut> list = emailFlowService.getCampaignOptedOutReminderRecords(campaignId);
                if (list == null || list.isEmpty()) {
                    logger.info("No active campaign member found for Campaign ID: " + campaignId);
                    continue;
                }

                int totalEligibleRecords = list.size();
                logger.info("Campaign active members count: " + totalEligibleRecords + " for campaign : " + campaignId);

                logger.info("Text Flow start ");
                //TextFlow
                List<OptInOut> optInOuts = textFlowService.getCampaignOptedOutReminderRecords(campaignId);
                if (optInOuts == null || optInOuts.isEmpty()) {
                    logger.info("No active campaign member found for Campaign ID: " + campaignId);
                    continue;
                }

                int totalRecords = optInOuts.size();
                logger.info("Campaign active members count: " + totalRecords + " for campaign : " + campaignId);

                List<CustomerRequest> customerRequestList = textFlowService.getAllCustomerRequestIPOrCMByCampaignId(campaignId);
                for (CustomerRequest customerRequest : customerRequestList) {
                    if (customerRequest != null) {
                        String phoneNo = customerRequest.getPhoneNumber();

                        customerRequest.setStatusCode(StatusEnum.STOPPED.getValue());
                        customerRequest.setLastUpdatedOn(new Date());
                        textFlowService.update(customerRequest);

                        OptInOut optInOut = textFlowService.getOptInOut(customerRequest.getCrSeqNo());

                        if (optInOut != null) {
                            optInOut.setStatusCode(StatusEnum.STOPPED.getValue());
                            optInOut.setOptInOut("O");
                            textFlowService.update(optInOut);
                            campaignId = optInOut.getCampaignId();
                        }

                        //send email
                        if (campaignId == 0) {
                            logger.info("No valid campaign found (System will return)");
                            return;
                        }

                        Event event = textFlowService.getEventByStaticValue(Constants.STANDARD);

                        if (event == null) {
                            logger.info("No such event defined (System will return)");
                            return;
                        }

                        int eventId = event.getEventId();

                        EventHasFolderHasCampaigns eventHasFolderHasCampaigns = emailFlowService.getEventHasFolderHasCampaign(campaignId, eventId, Constants.SMS);

                        if (eventHasFolderHasCampaigns == null) {
                            logger.info("No folder associated to this campaign (System will return)");
                            return;
                        }

                        int folderId = eventHasFolderHasCampaigns.getFolderId();

                        logger.info("Folder Id : " + folderId);

                        CampaignMessagesResponse campaignMessagesResponse = emailFlowService.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.END, Constants.SMS);

                        if (campaignMessagesResponse == null) {
                            logger.info("Stop Response message not found.");
                            return;
                        }

                        Intervals interval = campaignMessagesResponse.getIntervals();
                        IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                        int intervalId = interval.getIntervalId();
                        String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();

                        int intervalVal = interval.getIntervalValue();
                        int intervalUnitInSecond = intervalsType.getUnitInSecond();
                        long secondsDelay = intervalVal * intervalUnitInSecond;
                        int messageTypeId = campaignMessagesResponse.getMessageTypeId();
                        List<CampaignMessages> campaignMessagesList = emailFlowService.getCampaignMessagesByCommunicationPath(campaignId, folderId, messageTypeId, Constants.SMS);
                        if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                            logger.info("No messages found for (System will continue)");
                            logger.info("Folder ID: " + folderId);
                            return;
                        }

                        CampaignMessages campaignMessages = campaignMessagesList.get(0);
                        int messageId = campaignMessages.getMessageId();
                        messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
                        String eventDesc = textFlowService.getEventsDescription(campaignId, folderId, Constants.SMS);
                        String messageTitle = campaignMessages.getSmstext();
                        logger.info("Message Title is: " + messageTitle);
                        if (messageTitle == null || messageTitle.isEmpty()) {
                            logger.info("SMS Text is null System will continue");
                            logger.info("Message Type ID: " + messageTypeId);
                            return;
                        }
                        long inputReferenceNumber = 0;
                        CampaignMessageRequest campaignMessageRequest = setCampaignMessageRequest(campaignId, campaignName, customerRequest, folderId, messageId, messageTypeId, intervalId, intervalDesc, eventDesc, inputReferenceNumber);

                        setAndStartMessageThread(campaignMessages, customerRequest, phoneNo, inputReferenceNumber, secondsDelay, campaign, folderId, messageTypeId, intervalId, intervalDesc, eventDesc, campaignMessageRequest);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("PMSCampaignOptedOutReminder -> sendCampaignOptedOutAlerts", e);
        }

    }

    private void setAndStartMessageThread(CampaignMessages campaignMessages, CustomerRequest customerRequest, String phoneNo, long inputReferenceNumber, long secondsDelay, Campaigns campaign, int folderId, int messageTypeId, int intervalId, String intervalDesc, String eventDesc, CampaignMessageRequest campaignMessageRequest) {
        TextIntervalMessageRequestThread intervalMessageThread = new TextIntervalMessageRequestThread();
        intervalMessageThread.setGatewayInfo(null);
        intervalMessageThread.setCampaignMessages(campaignMessages);
        intervalMessageThread.setAppName("PMS text flow thread");
        intervalMessageThread.setCustomerRequest(customerRequest);
        intervalMessageThread.setPhoneNumber(phoneNo);
        intervalMessageThread.setInputReferenceNumber(inputReferenceNumber);
        intervalMessageThread.setSecondsDelay(secondsDelay);
        intervalMessageThread.setCampaign(campaign);
        intervalMessageThread.setTextFlowDAO(textFlowService);
        intervalMessageThread.setFolderId(folderId);
        intervalMessageThread.setMessageTypeId(messageTypeId);
        intervalMessageThread.setCommunicationType(Constants.SMS);
        intervalMessageThread.setIsWebEnabled(Boolean.FALSE);
        intervalMessageThread.setIntervalId(intervalId);
        intervalMessageThread.setIntervalDesc(intervalDesc);
        intervalMessageThread.setEventDesc(eventDesc);
        intervalMessageThread.setGroupNumber(null);
        intervalMessageThread.setThreadType("text_flow");
        intervalMessageThread.setCampaignMessageRequest(campaignMessageRequest);
        Thread smsIntervalMessageThread = new Thread(intervalMessageThread);
        smsIntervalMessageThread.start();
    }

    private CampaignMessageRequest setCampaignMessageRequest(int campaignId, String campaignName, CustomerRequest customerRequest, int folderId, int messageId, int messageTypeId, int intervalId, String intervalDesc, String eventDesc, long inputReferenceNumber) {
        CampaignMessageRequest campaignMessageRequest = new CampaignMessageRequest();
        campaignMessageRequest.setCampaignId(campaignId);
        campaignMessageRequest.setCampaignName(campaignName);
        campaignMessageRequest.setCrSeqNo(customerRequest.getCrSeqNo());
        campaignMessageRequest.setFolderId(folderId);
        campaignMessageRequest.setMessageId(messageId);
        campaignMessageRequest.setMessageTypeId(messageTypeId);
        campaignMessageRequest.setIntervalId(intervalId);
        campaignMessageRequest.setIntervalDescription(intervalDesc);
        campaignMessageRequest.setEventDetail(eventDesc);
        campaignMessageRequest.setCommunicationPath(Constants.SMS);
        campaignMessageRequest.setShortCode(customerRequest.getShortCode());
        campaignMessageRequest.setInputReferenceNumber(inputReferenceNumber);
        return campaignMessageRequest;
    }

    private boolean processFolder(int folderId, EventHasFolderHasCampaigns ehfhc, List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses, List<DailyRedemption> list, RefillReminderService refillReminderService1, int campaignId, PhoneValidationService phoneValidationService, String appCode, String trigger, int shortCode, GatewayInfo gatewayInfo, String appName, Campaigns campaign) throws Exception {
        Intervals interval;
        int intervalId;
        if (folderId == ehfhc.getFolderId()) {
            return true;
        }
        folderId = ehfhc.getFolderId();
        List<CampaignMessages> campaignMessagesList = null;
        String eventsIds = "";
        for (EventHasFolderHasCampaigns ehfhc1 : eventHasFolderHasCampaignses) {
            if (ehfhc1.getFolderId() == ehfhc.getFolderId()) {
                eventsIds += ehfhc1.getEventId() + ",";
            }
        }
        eventsIds = eventsIds.substring(0, eventsIds.length() - 1);
        for (DailyRedemption drf : list) {
            String communicationId = drf.getCommunicationId();
            String communicatoinMethod = drf.getCommunicationMethod();

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

            if (communicatoinMethod.equalsIgnoreCase("T")) {
                String communicationPath = Constants.SMS;
                CampaignMessagesResponse campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.SURVEY, communicationPath);
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

                Order order = refillReminderService.getOrderByTransactionNumber(drf.getId().getTransactionNumber());
                Double payment = null;
                if (order != null) {
                    payment = order.getPayment();
                }

                patientMessage = TextFlowUtil.prepareMessage(null, null, drf.getPharmacyName(), drf.getPharmacyPhone(),
                        patientMessage, drf.getPrescriptionNumber(), null, null, payment, null, null, null);

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
                CampaignMessagesResponse campaignMessagesResponse;

                logger.info("Campaign Id: " + campaignId);
                logger.info("Folder Id: " + folderId);
                logger.info("Response Title: " + Constants.SURVEY);
                logger.info("Communication Path: " + Constants.EMAIL);

                campaignMessagesResponse = refillReminderService1.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.SURVEY, Constants.EMAIL);
                if (campaignMessagesResponse == null) {
                    logger.info("Survey Response message not found.");
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

                int surveyMessageTypeId = campaignMessagesResponse.getMessageTypeId();

                campaignMessagesList = refillReminderService1.getCampaignMessagesByCommunicationPath(campaignId, folderId, surveyMessageTypeId, Constants.EMAIL);
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

                String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(communicationId, smtpServerInfo.getFromEmail());
                String surveyUrl = RedemptionUtil.prepareSurveyURL(campaign.getSurvey().getId(), communicationId);

                emailBody = EmailFlowUtil.prepareMessage(null, null, null, null, emailBody, null, null, null, null, unsubUrl, null, null, null, null, null, surveyUrl);

                logger.info("Final Email :" + emailBody);

                EmailRequest emailRequest = setEmailRequest(campaignId, drf, communicationId, emailBody, emailSubject, ehfhc, messageTypeId, campaign, intervalDesc, intervalId, eventDesc);

                startEmailRefillSendingThread(campaign, communicationId, emailBody, emailRequest, emailSubject, refillReminderService1, secondsDelay, smtpServerInfo);

            } else if (communicatoinMethod.equalsIgnoreCase("I")) {
            }
        } //
        //
        //just process first event only
        //just process first event only
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
        intervalMessageThread.setThreadType(Constants.REDEMPTION);
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
        aggregatorMessageRequest.setMessageContext(Constants.REDEMPTION);
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
        emailRequest.setMessageContext(Constants.REDEMPTION);
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
