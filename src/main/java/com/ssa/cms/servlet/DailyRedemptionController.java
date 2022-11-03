package com.ssa.cms.servlet;

import com.ssa.cms.common.Constants;
import com.ssa.cms.enumeration.NotificationStatusEnum;
import com.ssa.cms.enumeration.StatusEnum;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CampaignTrigger;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.CustomerRequest;
import com.ssa.cms.model.DailyRedemption;
import com.ssa.cms.model.Drug;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.Enrollment;
import com.ssa.cms.model.EventDetail;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRequest;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.InstantRedemptionId;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.IvroptInOut;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.RedemptionChannel;
import com.ssa.cms.model.RedemptionIngredient;
import com.ssa.cms.model.RedemptionLog;
import com.ssa.cms.model.ShortCodes;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.GatewayService;
import com.ssa.cms.service.PMSEmailFlowService;
import com.ssa.cms.service.PMSTextFlowService;
import com.ssa.cms.service.PhoneValidationService;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.thread.EmailRedemptionSendingThread;
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EmailFlowUtil;
import com.ssa.cms.util.EmailSender;
import com.ssa.cms.util.FileUtil;
import com.ssa.cms.util.IVRCallDialerUtil;
import com.ssa.cms.util.RedemptionUtil;
import com.ssa.cms.util.TextFlowUtil;
import com.ssa.cms.validation.DailyValidationUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping
public class DailyRedemptionController {

    private static final Logger log = Logger.getLogger(DailyRedemptionController.class);

    @Autowired
    private RedemptionService redemptionDAO;

    @Autowired
    private PMSTextFlowService pMSTextFlowService;

    String fileSeparator = File.separator;

    @Autowired
    private PMSEmailFlowService emailFlowService;

    @RequestMapping(value = "/DailyRedemption", method = RequestMethod.GET)
    public void process() {
        //process compound file
        //processDrfCompoundFile();

        //process main file
        processDrfMainFile();
    }

    @RequestMapping(value = "/dailyRedemption", method = RequestMethod.GET)
    public void processDRF(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //process main file
        processDrfFile(request, response);
    }

    @RequestMapping(value = "/redemptionIngredient", method = RequestMethod.GET)
    public void processDrfCompoundFile() {

        log.info("DailyRedemptionController -> processCompoundFile Starts : " + new Date());

        try {

            /* Redemption file reading */
            File sourceFiles[] = FileUtil.readDRFiles(Constants.DRF_FILE_PATH);

            if (sourceFiles == null || sourceFiles.length == 0) {
                log.info("No file found for reading");
                return;
            } else {
                log.info("Files Found for processing");
            }

            /* Starting reading files */
            for (File file : sourceFiles) {
                if (file.isDirectory()) {
                    log.info("It has been directory");
                    continue;
                } else if (!file.getName().contains("companion")) {
                    log.info("not main file");
                    continue;
                }

                BufferedReader bufferedReader;
                try (FileReader fileReader = new FileReader(file)) {
                    bufferedReader = new BufferedReader(fileReader);
                    String line;

                    //skipping header line
                    bufferedReader.readLine();

                    /* Starting reading each file line by line */
                    while ((line = bufferedReader.readLine()) != null) {

                        try {

                            if (line.startsWith("$T")) {
                                continue;
                            }

                            //extracting compound
                            int startIndex = 6;
                            String transactionNumber = line.substring(startIndex, startIndex + 20);
                            startIndex += 20;
                            int compoundCount = Integer.parseInt(line.substring(startIndex, startIndex + 2));
                            startIndex += 2;
                            String compound = line.substring(startIndex, startIndex + 107);
                            startIndex += 107;
                            saveIngredient(compound, transactionNumber);

                            //reading next ingredients
                            for (int i = 1; i < compoundCount; i++) {
                                compound = line.substring(startIndex, startIndex + 107);
                                startIndex += 107;
                                saveIngredient(compound, transactionNumber);
                            }

                        } catch (NumberFormatException e) {
                            log.error("NumberFormatException :: " + e);
                        }
                    }
                }

                //delete file
                file.renameTo(new File(Constants.DRF_FILE_PATH + fileSeparator + "backup" + fileSeparator + file.getName()));

                bufferedReader.close();
            }
        } catch (IOException e) {
            log.error("DRF File processing has been completed" + e);
        }

        log.info("DailyRedemptionController -> processCompoundFile Ends : " + new Date());
    }

    protected void processDrfMainFile() {
        File sourceFiles[] = FileUtil.readDRFiles(Constants.DRF_FILE_PATH);
        if (sourceFiles == null || sourceFiles.length == 0) {
            log.info("No file to process");
            return;
        }

        log.info("Files Found for processing");
        for (File file : sourceFiles) {
            if (file.isDirectory()) {
                log.info("It has been directory");
                continue;
            } else if (!file.getName().contains("main")) {
                log.info("not main file");
                continue;
            }
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                bufferedReader.readLine();
                /* Reading each file line by line */
                while ((line = bufferedReader.readLine()) != null) {
                    try {

                        if (line.startsWith("$T")) {
                            log.info("Trailer Record (System will continue)");
                            continue;
                        }

                        String[] splitedSegments = line.split("\\>");
                        int segmentCount = splitedSegments.length;
                        if (segmentCount != 24 && segmentCount > 1) {
                            log.info("Invalid number of segments in current reocrd segment : " + splitedSegments.length + " Line: " + line);
                            continue;
                        }

                        RedemptionLog redemptionLog = saveRequest(line);
                        DailyRedemption dailyRedemption = DailyValidationUtil.getDailyRedemption(splitedSegments);
                        dailyRedemption.setMemberId(dailyRedemption.getSubmittedId());

                        List<RedemptionIngredient> ingredient = redemptionDAO.getRedemptionIngredientByTransactionNumber(dailyRedemption.getId().getTransactionNumber());
                        dailyRedemption.setIngredientList(ingredient);

                        String communicationId = dailyRedemption.getCommunicationId();
                        Campaigns campaign = redemptionDAO.getCampaignById(Constants.campaignId);
                        if (campaign == null) {
                            log.info("No campaign found...");
                            continue;
                        }

                        GatewayInfo gatewayInfo = null;
                        int shortCodeValue = 0;
                        int campaignId = campaign.getCampaignId();
                        String campaignName = campaign.getCampaignName();
                        RedemptionChannel redemptionChannel =null; //campaign.getRedemptionChannel();
                        ShortCodes shortCode = campaign.getShortCodes();
                        redemptionLog.setCampaignId(campaign.getCampaignId());
                        redemptionDAO.update(redemptionLog);

                        if (redemptionChannel == null) {
                            log.info("No redemption channel attached to campaign " + campaignId + " (System will return)");
                            continue;
                        }

                        String redemptionChannelTitle = redemptionChannel.getRchannelTitle();
                        log.info("Redemption channel title : " + redemptionChannelTitle);

                        if (redemptionChannelTitle == null || (!redemptionChannelTitle.trim().equalsIgnoreCase("EMDEON"))) {
                            log.info("Redemption channel titel doesn't have value EMDEON. (System will return)");
                            continue;
                        }

                        dailyRedemption.setRedemptionChannelId(redemptionChannel.getRchannelId());
                        dailyRedemption.setRedemptionChannelTitle(redemptionChannelTitle);
                        dailyRedemption.setCampaignId(campaignId);
                        dailyRedemption.setProductName(campaignName);

                        InstantRequest instantRequest = redemptionDAO.getInstantRequestByMemberId(communicationId, campaign.getCampaignId());
                        boolean isEnrolled = false;
                        String communicationMethod = null;
                        if (instantRequest != null) {
                            String messageFlag = instantRequest.getFlagMessage();

                            if (messageFlag != null) {
                                messageFlag = messageFlag.trim();

                                if (messageFlag.equalsIgnoreCase("T")) {
                                    communicationMethod = "T";
                                    communicationId = instantRequest.getText();
                                } else if (messageFlag.equalsIgnoreCase("E")) {
                                    communicationMethod = "E";
                                    communicationId = instantRequest.getEmail();
                                } else if (messageFlag.equalsIgnoreCase("I")) {
                                    communicationMethod = "I";
                                    communicationId = instantRequest.getText();
                                }
                            } // if(messageFlag != null)
                            dailyRedemption.setCommunicationId(communicationId);
                            dailyRedemption.setCommunicationMethod(communicationMethod);
                        } else {
                            isEnrolled = true;
                            communicationMethod = "T";
                            dailyRedemption.setCommunicationMethod(communicationMethod);
                            CustomerRequest customerRequest = pMSTextFlowService.setCustomerRequest(communicationId, campaignId, campaignName, dailyRedemption.getProgramCode(),
                                    shortCode.getShortCode(), "0002", null, 0, 0, null, null, StatusEnum.COMPLETED.getValue());
                            OptInOut optInOut = setOptinOut(communicationId, campaignId, campaignName, shortCode.getShortCode());
                            redemptionDAO.saveOrUpdate(customerRequest);
                            optInOut.setCrSeqNo(customerRequest.getCrSeqNo());
                            redemptionDAO.saveOrUpdate(optInOut);
                            TextFlowUtil.markCampaignEnd(customerRequest, campaign, null, pMSTextFlowService);
                        }

                        boolean phoneValidity = false;
                        CampaignTrigger campaignTrigger = redemptionDAO.getTriggerByCampaignId(campaignId);
                        String programCode = null;

                        if (campaignTrigger != null) {
                            programCode = campaignTrigger.getId().getKeyword();
                        }

                        Enrollment enrollment = null;
                        if (communicationMethod != null && communicationMethod.equalsIgnoreCase("T")) {
                            enrollment = redemptionDAO.getTextEnrollment(communicationId, campaignId);
                            if (shortCode != null) {
                                shortCodeValue = shortCode.getShortCode();
                            }

                            gatewayInfo = redemptionDAO.getGatewayInfo(shortCodeValue);

                            String appCode = "App000";
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
                            dailyRedemption.setEnrollmentId(new Long(enrollment.getEnrollmentId()).intValue());
                            dailyRedemption.setEnrollmentDate(enrollment.getEffectiveDate());
                            dailyRedemption.setCommunicationSourceCode(enrollment.getCommunicationSourceCode());
                            dailyRedemption.setEnrollmentPath(enrollment.getEnrollmentPath());
                            isEnrolled = true;
                        }

                        dailyRedemption.setProgramCode(programCode);

                        dailyRedemption.setIsValidPhone("NO");
                        if (phoneValidity) {
                            dailyRedemption.setIsValidPhone("YES");
                        }

                        if (communicationMethod != null && communicationMethod.equalsIgnoreCase("I")) {
                            if ("Yes".equalsIgnoreCase(instantRequest.getIsValidPhone())) {
                                dailyRedemption.setIsValidPhone("YES");
                            }
                            if ("L".equalsIgnoreCase(instantRequest.getIsValidPhone())) {
                                dailyRedemption.setIsValidPhone("L");
                            }
                        }

                        if (saveDrf(dailyRedemption, redemptionLog)) {
                            continue;
                        }

                        InstantRedemptionId id = new InstantRedemptionId();
                        id.setClaimStatus(dailyRedemption.getId().getClaimStatus());
                        id.setTransactionNumber(dailyRedemption.getId().getTransactionNumber());
                        int recordCount = redemptionDAO.getIRFCountById(id);

                        InstantRedemption instantRedemption = saveIrf(dailyRedemption, id, recordCount);
                        if (recordCount >= 1) {
                            log.info("Messgase already send via IRF");
                            continue;
                        }

                        if (!isEnrolled) {
                            log.info("Not enrolled patient. (System will return)");
                            continue;
                        }

                        if (communicationMethod == null || communicationMethod.trim().length() == 0) {
                            log.info("Communication method not define (System will return)");
                            continue;
                        }

                        if (!(communicationMethod.equalsIgnoreCase("T") || communicationMethod.equalsIgnoreCase("E") || communicationMethod.equalsIgnoreCase("I"))) {
                            log.info("Communication method not valid (System will return)");
                            continue;
                        }

                        if (communicationMethod.equalsIgnoreCase("T")) {
                            sendTextMessage(instantRedemption, shortCode, gatewayInfo, shortCodeValue, programCode, dailyRedemption, campaign, redemptionLog);
                        } else if (communicationMethod.equalsIgnoreCase("E")) {
                            sendEmailMessage(dailyRedemption, instantRedemption, campaign, redemptionLog);
                        } else if (communicationMethod.equalsIgnoreCase("I")) {
                            initiateIvrOutboundCall(dailyRedemption, instantRequest, campaignId, campaign, instantRedemption);
                        }
                    } catch (Exception exp) {
                        log.error(exp);
                    }
                }

                //delete file
                file.renameTo(new File(Constants.DRF_FILE_PATH + fileSeparator + "backup" + fileSeparator + file.getName()));

            } catch (Exception exp) {
                log.error(exp);
            }
        }

    }

    private boolean initiateIvrOutboundCall(DailyRedemption dailyRedemption, InstantRequest instantRequest, int campaignId, Campaigns campaign, InstantRedemption instantRedemption) {
        //initiate IVR call
        boolean isMobile = false;
        boolean isLandLine = false;
        String phoneNumber = dailyRedemption.getCommunicationId();
        if (instantRequest.getIsValidPhone().equalsIgnoreCase("Yes")) {
            isMobile = true;
        }
        if (instantRequest.getIsValidPhone().equalsIgnoreCase("L")) {
            isLandLine = true;
        }
        if (isLandLine == false && isMobile == false) {
            log.info(phoneNumber + " PhoneNumber neither Mobile nor Landline : " + campaignId);
            log.info("System will return");
            return true;
        }
        IvroptInOut optInOut = redemptionDAO.getIVROptInOut(campaignId, phoneNumber);
        String optInOutFlag = null;
        if (optInOut != null) {
            optInOutFlag = optInOut.getOptInOut();
        }
        if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
            log.info(phoneNumber + " has opted out from Campaign : " + campaignId);
            log.info("System will return");
            return true;
        }
        List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, Constants.OIVR);
        if (eventHasFolderHasCampaignses == null
                || eventHasFolderHasCampaignses.isEmpty()) {
            log.info("No Redemption folder associated to this campaign (System will continue)");
            return true;
        }
        int redemptionFolderId = 0;
        for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

            String eventsIds = "";

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
            List<EventDetail> eventDetails = redemptionDAO.getEventDetail(eventsIds, "DailyRedemption");
            if (eventDetails != null && eventDetails.size() > 0) {
                boolean send = redemptionDAO.isEventDetailVerifiedForDailyRedemption(eventDetails, dailyRedemption.getId(), dailyRedemption.getRedemptionId());
                if (!send) {
                    log.info("Event Detail Criteria doesnt match for Daily Redemption.");
                    continue;
                }
            }

            String responseTile = Constants.THANK_YOU;
            long redemptionCount = redemptionDAO.getTotalRedemptionCountDRF(dailyRedemption);
            String isMaxBenifit = campaign.getIsMaxBenefit();

            long maxbenifitAmount = 0;
            if (isMaxBenifit.equalsIgnoreCase("YES")) {
                if (campaign.getMaxBenefitAmount() != null) {
                    maxbenifitAmount = campaign.getMaxBenefitAmount().intValue();
                }
                long redemptionLimit = campaign.getRedemptionLimit();
                BigDecimal benifitAmount = redemptionDAO.getTotalBenefitAmountDRF(dailyRedemption);
                if ((redemptionCount >= redemptionLimit)
                        || (benifitAmount != null && (benifitAmount.doubleValue() >= maxbenifitAmount && maxbenifitAmount != 0))) {
                    responseTile = Constants.MAX_BENEFIT;
                }
            }
            Double copay = 0.0;
            //calculate copay
            List<RedemptionIngredient> list = dailyRedemption.getIngredientList();
            if (list != null && !list.isEmpty()) {
                for (RedemptionIngredient ingredient : list) {
                    Drug drug = redemptionDAO.getMaxOfferByNdc(ingredient.getNdc());
//                    if (drug != null) {
//                        copay += drug.getMaxOffer();
//                    }
                }
            }
            if (copay > 0) {
                boolean isPharmacyMember = redemptionDAO.getPharmacyByNpiNo(dailyRedemption.getPharmacyNpi());

                if (redemptionCount == 1) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                } else if (!isPharmacyMember) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                }
            }

            CampaignMessagesResponse campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, Constants.OIVR);

            if (campaignMessagesResponse == null) {
                log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, Constants.OIVR);

                if (campaignMessagesResponse == null) {
                    log.info("Redemption campaignMessagesResponse is null.");
                    continue;
                }

            }

            Intervals interval = campaignMessagesResponse.getIntervals();
            IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

            int intervalId = interval.getIntervalId();
            String intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
            String eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, Constants.OIVR);

            int intervalValue = interval.getIntervalValue();
            int intervalUnitInSecond = intervalsType.getUnitInSecond();
            long secondsDelay = intervalValue * intervalUnitInSecond;

            log.info("Thread going to sleep for " + secondsDelay + " Seconds.");
            Integer redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

            List<CampaignMessages> campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, Constants.OIVR);

            if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                log.info("No Redemption messages found for (System will continue)");
                continue;
            }
            CampaignMessages campaignMessages = campaignMessagesList.get(0);

            Integer messageId = campaignMessages.getMessageId();
            int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
            String servoId = null;
            if (servoId == null || servoId.length() == 0) {
                log.info("No Survo Id found for (System will continue)");
                continue;
            }

            IVRCallDialerUtil dialerUtil = new IVRCallDialerUtil();
            dialerUtil.setInstantRedemption(instantRedemption);
            dialerUtil.setCampaignId(campaignId);
            dialerUtil.setEventDesc(eventDesc);
            dialerUtil.setFolderId(redemptionFolderId);
            dialerUtil.setIntervalDesc(intervalDesc);
            dialerUtil.setIntervalId(intervalId);
            dialerUtil.setIsLandLine(isLandLine);
            dialerUtil.setIsMobile(isMobile);
            dialerUtil.setMessageContext(Constants.REDEMPTION);
            dialerUtil.setMessageTypeId(messageTypeId);
            dialerUtil.setServoId(servoId);
            dialerUtil.setCopay(copay);
            dialerUtil.ivrCall(redemptionDAO);
        }
        return false;
    }

    private InstantRedemption saveIrf(DailyRedemption dailyRedemption, InstantRedemptionId id, int recordCount) {
        InstantRedemption instantRedemptionFile = new InstantRedemption();
        instantRedemptionFile.setId(id);

        if (recordCount > 0) {
            InstantRedemption irf = redemptionDAO.getIRFById(id);
            if (irf != null && irf.getOtherPayerRejectionCode() != null) {
                dailyRedemption.setOtherPayerRejectionCode(irf.getOtherPayerRejectionCode());
                dailyRedemption.setRedemptionId(irf.getRedemptionId());
            }
            redemptionDAO.update(dailyRedemption);

        } else {
//            redemptionDAO.populateNpiValues(instantRedemptionFile);
            BeanUtils.copyProperties(dailyRedemption, instantRedemptionFile, new String[]{"id"});
            instantRedemptionFile.setOtherPayerId1(dailyRedemption.getOtherPayerId());
            instantRedemptionFile.setTimeStamp(new Date());
            instantRedemptionFile.setRedemptionId(dailyRedemption.getRedemptionId());
            String otherPayerRejectionCode = dailyRedemption.getOtherPayerRejectionCode();
            log.info("otherPayerRejectionCode is :" + otherPayerRejectionCode);

            boolean irfSaveFalg = redemptionDAO.save(instantRedemptionFile);

            if (otherPayerRejectionCode != null && otherPayerRejectionCode.trim().length() > 0) {

                otherPayerRejectionCode = otherPayerRejectionCode.trim();

                if (otherPayerRejectionCode.equalsIgnoreCase("03")) {
                    instantRedemptionFile.setOtherPayerRejectionCode("75");
                } else {
                    instantRedemptionFile.setOtherPayerRejectionCode(null);
                }
            }

            if (irfSaveFalg) {
                InstantRedemption irf = redemptionDAO.getIRFById(id);
                dailyRedemption.setOtherPayerRejectionCode(instantRedemptionFile.getOtherPayerRejectionCode());
                dailyRedemption.setRedemptionId(irf.getRedemptionId());
                instantRedemptionFile.setRedemptionId(irf.getRedemptionId());
                redemptionDAO.update(dailyRedemption);
            }
        }
        return instantRedemptionFile;
    }

    private boolean saveDrf(DailyRedemption dailyRedemption, RedemptionLog redemptionLog) {
        int recordCount = redemptionDAO.getDRFCountById(dailyRedemption.getId());
        if (recordCount > 0) {
            int statusCode = NotificationStatusEnum.DUPLICATE.getValue();
            String notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
            redemptionLog.setStatusCode(statusCode);
            redemptionLog.setDescription(notificationMessage);
            redemptionDAO.update(redemptionLog);
            log.info("DUPLICATE returning....");
            return true;
        }
//        redemptionDAO.populatePrescriberNpiValues(dailyRedemption);
        boolean drfSaved = redemptionDAO.save(dailyRedemption);
        if (!drfSaved) {
            int statusCode = NotificationStatusEnum.UN_KNOWN.getValue();
            String notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
            redemptionLog.setStatusCode(statusCode);
            redemptionLog.setDescription(notificationMessage);
            redemptionDAO.update(redemptionLog);
            log.info("UN_KNOWN returning....");
            return true;
        }
        int statusCode = NotificationStatusEnum.SUCCESS.getValue();
        String notificationMessage = redemptionDAO.getNotificationDescription(statusCode);
        redemptionLog.setStatusCode(statusCode);
        redemptionLog.setDescription(notificationMessage);
        redemptionDAO.update(redemptionLog);
        log.info("UN_KNOWN returning....");
        return false;
    }

    private boolean sendTextMessage(InstantRedemption instantRedemptionFile, ShortCodes shortCode, GatewayInfo gatewayInfo, int shortCodeValue, String programCode, DailyRedemption dailyRedemption, Campaigns campaign, RedemptionLog redemptionLog) throws Exception {
        int campaignId = instantRedemptionFile.getCampaignId();
        String phoneNumber = instantRedemptionFile.getCommunicationId();

        OptInOut optInOut = redemptionDAO.getTextOptInOut(campaignId, phoneNumber);
        String optInOutFlag = null;
        if (optInOut != null) {
            optInOutFlag = optInOut.getOptInOut();
        }
        if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
            log.info(phoneNumber + " has opted out from Campaign : " + campaignId);
            log.info("System will return");
            return true;
        }
        if (shortCode == null) {
            log.info("No short code defined for campaign : " + campaignId);
            log.info("System will return");
            return true;
        }
        String gatewayStatusServiceURL = gatewayInfo.getGkurl();
        GatewayService gatewayService = new GatewayService(gatewayStatusServiceURL);
        if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("I")) {
            if (shortCodeValue != 21200) {
                gatewayService.informToGatewayForAnonymousYES(phoneNumber, programCode, "NO", "0004");
            }
        } else if (shortCodeValue != 21200) {
            gatewayService.informToGatewayForAnonymousYES(phoneNumber, programCode, "YES", "0004");
        }
        List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, Constants.SMS);
        if (eventHasFolderHasCampaignses == null
                || eventHasFolderHasCampaignses.isEmpty()) {
            log.info("No Redemption folder associated to this campaign (System will continue)");
            return true;
        }
        int redemptionFolderId = 0;
        for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

            String eventsIds = "";
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

            List<EventDetail> eventDetails = redemptionDAO.getEventDetail(eventsIds, "DailyRedemption");

            if (eventDetails != null && eventDetails.size() > 0) {
                boolean send = redemptionDAO.isEventDetailVerifiedForDailyRedemption(eventDetails, dailyRedemption.getId(), dailyRedemption.getRedemptionId());
                if (!send) {
                    log.info("Event Detail Criteria doesnt match for Daily Redemption.");
                    continue;
                }
            }

            String messageContext = Constants.REDEMPTION;
            String responseTile = Constants.THANK_YOU;
            long redemptionCount = redemptionDAO.getTotalRedemptionCountDRF(dailyRedemption);
            String isMaxBenifit = campaign.getIsMaxBenefit();

            long maxbenifitAmount = 0;
            if (isMaxBenifit.equalsIgnoreCase("YES")) {
                if (campaign.getMaxBenefitAmount() != null) {
                    maxbenifitAmount = campaign.getMaxBenefitAmount().intValue();
                }
                long redemptionLimit = campaign.getRedemptionLimit();
                BigDecimal benifitAmount = redemptionDAO.getTotalBenefitAmountDRF(dailyRedemption);
                if ((redemptionCount >= redemptionLimit)
                        || (benifitAmount != null && (benifitAmount.doubleValue() >= maxbenifitAmount && maxbenifitAmount != 0))) {
                    responseTile = Constants.MAX_BENEFIT;
                    messageContext = Constants.MAX_BENEFIT;
                }
            }
            Double copay = 0.0;
            //calculate copay
            List<RedemptionIngredient> list = dailyRedemption.getIngredientList();
            if (list != null && !list.isEmpty()) {
                for (RedemptionIngredient ingredient : list) {
                    Drug drug = redemptionDAO.getMaxOfferByNdc(ingredient.getNdc());
//                    if (drug != null) {
//                        copay += drug.getMaxOffer();
//                    }
                }
            }
            if (copay > 0) {
                boolean isPharmacyMember = redemptionDAO.getPharmacyByNpiNo(dailyRedemption.getPharmacyNpi());

                if (redemptionCount == 1) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                } else if (!isPharmacyMember) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                }
            }
            gatewayService = new GatewayService(gatewayInfo.getWebQueryURL());

            boolean isWebEnabled = gatewayService.webEnabledQuery(phoneNumber, "PMS EMGS Daily Redemption Servlet", 0, gatewayInfo);

            String communicationPath;
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

            CampaignMessagesResponse campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, communicationPath);

            if (campaignMessagesResponse == null) {
                log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, communicationPath);

                if (campaignMessagesResponse == null) {
                    log.info("Redemption campaignMessagesResponse is null.");
                    continue;
                }

            }

            Intervals interval = campaignMessagesResponse.getIntervals();
            IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

            int intervalId = interval.getIntervalId();
            String intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
            String eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, communicationPath);

            int intervalValue = interval.getIntervalValue();
            int intervalUnitInSecond = intervalsType.getUnitInSecond();
            long secondsDelay = intervalValue * intervalUnitInSecond;

            int redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

            List<CampaignMessages> campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, communicationPath);

            if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                log.info("No Redemption messages found for (System will continue)");
                continue;
            }
            CampaignMessages campaignMessages = campaignMessagesList.get(0);

            int messageId = campaignMessages.getMessageId();
            int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();
            String messageText = campaignMessages.getSmstext();
            log.info("Thread going to sleep for " + secondsDelay + " Seconds.");

            AggregatorMessageRequest messageRequest = new AggregatorMessageRequest();
            if (messageText != null && messageText.trim().length() > 0) {
                //check and make sure patient has not recieved within next 24 hours
                int sentMessageCount = redemptionDAO.retrieveMessageCountByTypeForRedemption(phoneNumber, instantRedemptionFile.getRxGroupNumber(), instantRedemptionFile.getCardholderDob(), instantRedemptionFile.getFillDate(), instantRedemptionFile.getNdcNumber(), instantRedemptionFile.getId().getClaimStatus(), ehfhc.getFolderId(), messageTypeId);
                if (sentMessageCount > 0) {
                    log.info("Message already sent for phone number: " + phoneNumber);
                    continue;
                }
                log.info("Message Id : " + messageId + "Message Type Id : " + messageTypeId + "Redemption Message : " + messageText);

                int refillAllowed = dailyRedemption.getRefillsAllowed();
                int refillUsed = dailyRedemption.getRefillsUsed();
                int remainingRefill = refillAllowed - refillUsed;
                Double paymentAmount = calculateCopay(dailyRedemption, copay);

                messageText = TextFlowUtil.prepareMessage(copay, dailyRedemption.getPtOutOfPocket(),
                        dailyRedemption.getPharmacyName(),
                        dailyRedemption.getPharmacyPhone(), messageText, dailyRedemption.getPrescriptionNumber(), remainingRefill, null, paymentAmount, null, null, null);

                campaignMessages.setSmstext(messageText);

                messageRequest = setMessageRequest(instantRedemptionFile, ehfhc.getFolderId(),
                        messageText, messageTypeId, messageContext, intervalId, intervalDesc, eventDesc);
            }

            TextFlowUtil.startMessageThread(gatewayInfo, messageRequest, campaignMessages, programCode, secondsDelay, campaign, ehfhc, messageTypeId,
                    intervalId, intervalDesc, eventDesc, instantRedemptionFile, dailyRedemption, redemptionDAO);

        }
        return false;
    }

    private Double calculateCopay(DailyRedemption dailyRedemption, Double copay) {
        Double paymentAmount = dailyRedemption.getPtOutOfPocket().doubleValue() - copay;
        if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1 && paymentAmount.intValue() < 8) {
            paymentAmount = 8.0;
        }
        return paymentAmount;
    }

    private boolean sendEmailMessage(DailyRedemption dailyRedemption, InstantRedemption instantRedemptionFile, Campaigns campaign, RedemptionLog redemptionLog) throws Exception {
        int campaignId = instantRedemptionFile.getCampaignId();
        String email = instantRedemptionFile.getCommunicationId();

        EmailOptInOut optInOut = redemptionDAO.getEmailOptInOut(email, campaignId);
        String optInOutFlag = null;
        if (optInOut != null) {
            optInOutFlag = optInOut.getOptInOut();
        }
        if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
            log.info(email + " has opted out from Campaign : " + campaignId);
            log.info("System will return");
            return true;
        }

        List<EventHasFolderHasCampaigns> eventHasFolderHasCampaignses = redemptionDAO.getEventHasFolderHasCampaign(campaignId, Constants.REDEMPTION, Constants.EMAIL);
        if (eventHasFolderHasCampaignses == null
                || eventHasFolderHasCampaignses.isEmpty()) {
            log.info("No Redemption folder associated to this campaign (System will continue)");
            return true;
        }
        int redemptionFolderId = 0;
        int messageSent = 0;
        for (EventHasFolderHasCampaigns ehfhc : eventHasFolderHasCampaignses) {

            String eventsIds = "";
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

            List<EventDetail> eventDetails = redemptionDAO.getEventDetail(eventsIds, "DailyRedemption");

            if (eventDetails != null && eventDetails.size() > 0) {
                boolean send = redemptionDAO.isEventDetailVerifiedForDailyRedemption(eventDetails, dailyRedemption.getId(), dailyRedemption.getRedemptionId());
                if (!send) {
                    log.info("Event Detail Criteria doesnt match for Daily Redemption.");
                    continue;
                }
            }

            String messageContext = Constants.REDEMPTION;
            String responseTile = Constants.THANK_YOU;
            long redemptionCount = redemptionDAO.getTotalRedemptionCountDRF(dailyRedemption);
            String isMaxBenifit = campaign.getIsMaxBenefit();

            long maxbenifitAmount = 0;
            if (isMaxBenifit.equalsIgnoreCase("YES")) {
                if (campaign.getMaxBenefitAmount() != null) {
                    maxbenifitAmount = campaign.getMaxBenefitAmount().intValue();
                }
                long redemptionLimit = campaign.getRedemptionLimit();
                BigDecimal benifitAmount = redemptionDAO.getTotalBenefitAmountDRF(dailyRedemption);
                if ((redemptionCount >= redemptionLimit)
                        || (benifitAmount != null && (benifitAmount.doubleValue() >= maxbenifitAmount && maxbenifitAmount != 0))) {
                    responseTile = Constants.MAX_BENEFIT;
                    messageContext = Constants.MAX_BENEFIT;
                }
            }
            Double copay = 0.0;
            //calculate copay
            List<RedemptionIngredient> list = dailyRedemption.getIngredientList();
            if (list != null && !list.isEmpty()) {
                for (RedemptionIngredient ingredient : list) {
                    Drug drug = redemptionDAO.getMaxOfferByNdc(ingredient.getNdc());
//                    if (drug != null) {
//                        copay += drug.getMaxOffer();
//                    }
                }
            }
            if (copay > 0) {
                boolean isPharmacyMember = redemptionDAO.getPharmacyByNpiNo(dailyRedemption.getPharmacyNpi());

                if (redemptionCount == 1) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                } else if (!isPharmacyMember) {
                    if (dailyRedemption.getPtOutOfPocket().compareTo(BigDecimal.valueOf(8)) == 1) {
                        responseTile = Constants.PROGRAM_OFFER_SENT; //program offer
                    }
                }
            }
            String communicationPath = Constants.EMAIL;

            CampaignMessagesResponse campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponseByResComm(campaignId, redemptionFolderId, responseTile, communicationPath);

            if (campaignMessagesResponse == null) {
                log.info("Redemption Thank you and Max Benefit campaignMessagesResponse is null.");

                campaignMessagesResponse = redemptionDAO.getCampaignMessagesResponse(campaignId, redemptionFolderId, communicationPath);

                if (campaignMessagesResponse == null) {
                    log.info("Redemption campaignMessagesResponse is null.");
                    continue;
                }

            }

            Intervals interval = campaignMessagesResponse.getIntervals();
            IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

            int intervalId = interval.getIntervalId();
            String intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
            String eventDesc = redemptionDAO.getEventsDescription(campaignId, redemptionFolderId, communicationPath);

            int intervalValue = interval.getIntervalValue();
            int intervalUnitInSecond = intervalsType.getUnitInSecond();
            long secondsDelay = intervalValue * intervalUnitInSecond;

            int redemptionMsgTypeId = campaignMessagesResponse.getMessageTypeId();

            List<CampaignMessages> campaignMessagesList = redemptionDAO.getCampaignMessagesByCommunicationPath(campaignId, redemptionFolderId, redemptionMsgTypeId, Constants.EMAIL);

            if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                log.info("No Redemption messages found for (System will continue)");
                continue;
            }
            CampaignMessages campaignMessages = campaignMessagesList.get(0);

            int messageId = campaignMessages.getMessageId();
            int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

            String emailFrom = null;
            String emailbody = null;
            emailbody = EmailSender.makeEmailBody(emailbody);
            int refillAllowed = dailyRedemption.getRefillsAllowed();
            int refillUsed = dailyRedemption.getRefillsUsed();
            int remainingRefill = refillAllowed - refillUsed;
            String emailSubject = null;

            SmtpServerInfo smtpServerInfo = campaign.getSmtpServerInfo();

            log.info("Message Id : " + messageId);
            log.info("Message Type Id : " + messageTypeId);
            log.info("Email From : " + emailFrom);
            log.info("Email Body : " + emailbody);
            log.info("Email Subject : " + emailSubject);

            if (emailbody == null || emailbody.equalsIgnoreCase("")) {
                log.info("No Email body found System will continue");
                log.info("Message Type ID: " + messageTypeId);
                continue;
            }

            if (smtpServerInfo == null) {
                log.info("No SMTP server found System will continue");
                log.info("Message Type ID: " + messageTypeId);
                continue;
            }

            int sentEmail = redemptionDAO.getSendEmailCount(email, campaignId, redemptionFolderId, messageTypeId, instantRedemptionFile.getFillDate());

            if (sentEmail > 0) {
                log.info("Redemption Email already send for " + email);
                continue;
            }

            String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(email, smtpServerInfo.getFromEmail());
            String acceptanceUrl = RedemptionUtil.prepareAcceptanceURL(email, smtpServerInfo.getFromEmail());
            String placeOrderUrl = RedemptionUtil.preparePlaceOrderURL(dailyRedemption.getId().getTransactionNumber());

            BigDecimal outOfPocket = dailyRedemption.getPtOutOfPocket();
            if (outOfPocket == null) {
                outOfPocket = BigDecimal.ZERO;
            }
            log.info("OutOfPocket value is: " + outOfPocket + " Copy value is: " + copay);
            Double payment = calculateCopay(dailyRedemption, copay);
            log.info("OutOfPocket-copy: " + payment);
            emailbody = EmailFlowUtil.prepareMessage(copay, outOfPocket, dailyRedemption.getPharmacyName(), dailyRedemption.getPharmacyPhone(), emailbody, dailyRedemption.getPrescriptionNumber(), remainingRefill, acceptanceUrl, payment, unsubUrl, null, placeOrderUrl, null, null, null, null);

            String campaignName = campaign.getCampaignName();
            if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
                emailbody = emailbody.replace("[_brand_]", campaignName);
            }

            double totalDrugCostToPharmacy = 0;
            String patientTotalDrugCost = "";
            NumberFormat numberFormat = new DecimalFormat(".00");

            if (instantRedemptionFile.getTotDrugCostPaidToPharmacy() != null) {
                totalDrugCostToPharmacy = instantRedemptionFile.getTotDrugCostPaidToPharmacy().doubleValue();
                patientTotalDrugCost = numberFormat.format(totalDrugCostToPharmacy);

            }

            if (patientTotalDrugCost != null || !patientTotalDrugCost.equalsIgnoreCase("")) {
                emailbody = emailbody.replace("XX.XX", patientTotalDrugCost);
            }

            log.info("Final Email :" + emailbody);

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setCampaignId(campaignId);
            emailRequest.setCardholderDob(instantRedemptionFile.getCardholderDob());
            emailRequest.setClaimStatus(instantRedemptionFile.getId().getClaimStatus());
            emailRequest.setEmail(email);
            emailRequest.setEmailBody(emailbody.getBytes());
            emailRequest.setEmailSubject(emailSubject);
            emailRequest.setFileName(instantRedemptionFile.getFileName());
            emailRequest.setFileTypeCode(instantRedemptionFile.getFileTypeCode());
            emailRequest.setFillDate(instantRedemptionFile.getFillDate());
            emailRequest.setFolderId(redemptionFolderId);
            emailRequest.setMessageContext(messageContext);
            emailRequest.setMessageTypeId(messageTypeId);
            emailRequest.setNdcNumber(instantRedemptionFile.getNdcNumber());
            emailRequest.setRedemptionId(dailyRedemption.getRedemptionId());
            emailRequest.setRxGroupNumber(instantRedemptionFile.getRxGroupNumber());
            emailRequest.setSmtpId(campaign.getSmtpServerInfo().getSmtpId());
            emailRequest.setIntervalDescription(intervalDesc);
            emailRequest.setEventDetail(eventDesc);
            emailRequest.setIntervalId(intervalId);

            messageSent++;

            EmailRedemptionSendingThread emailRedemptionSendingThread = new EmailRedemptionSendingThread();

            emailRedemptionSendingThread.setCampaign(campaign);
            emailRedemptionSendingThread.setEmail(email);
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
        return false;
    }

    private AggregatorMessageRequest setMessageRequest(InstantRedemption instantRedemption,
            int folderId, String messageText,
            int messageTypeId,
            String messageContext,
            int intervalId, String intervalDesc, String eventDesc) {

        AggregatorMessageRequest messageRequest = new AggregatorMessageRequest();
        if (instantRedemption.getCardholderDob() != null) {
            messageRequest.setCardholderDob(instantRedemption.getCardholderDob());
        } else {
            messageRequest.setCardholderDob(null);
        }
        messageRequest.setClaimStatus(instantRedemption.getId().getClaimStatus());
        messageRequest.setFileTypeCode(Constants.DRF);
        messageRequest.setFolderId(folderId);
        messageRequest.setMessageText(messageText);
        messageRequest.setMessageTypeId((messageTypeId));
        messageRequest.setNdcNumber(instantRedemption.getNdcNumber());
        messageRequest.setPhoneNumber(instantRedemption.getCommunicationId());
        messageRequest.setRedemptionId(instantRedemption.getRedemptionId());
        messageRequest.setRxGroupNumber(instantRedemption.getRxGroupNumber());
        messageRequest.setFillDate(instantRedemption.getFillDate());
        messageRequest.setCampaignId(instantRedemption.getCampaignId());
        messageRequest.setMessageContext(messageContext);
        messageRequest.setIntervalId(intervalId);
        messageRequest.setIntervalDescription(intervalDesc);
        messageRequest.setEventDetail(eventDesc);
        if (instantRedemption.getCommunicationMethod().equalsIgnoreCase("T")) {
            messageRequest.setCommunicationPath(Constants.SMS);
        } else if (instantRedemption.getCommunicationMethod().equalsIgnoreCase("E")) {
            messageRequest.setCommunicationPath(Constants.EMAIL);
        } else {
            messageRequest.setCommunicationPath(instantRedemption.getCommunicationMethod());
        }

        return messageRequest;
    }

    private RedemptionLog saveRequest(String line) {
        RedemptionLog redemptionLog = new RedemptionLog();
        try {
            redemptionLog.setRequestXml(line);
            redemptionLog.setClientIp("");
            redemptionLog.setMessageSent(0);
            redemptionLog.setFileTypeCode("DRF");
            redemptionLog.setEffectiveDate(new Date());
            redemptionDAO.save(redemptionLog);

        } catch (Exception e) {
            log.error("SaveRequest -> RedemptionLog: ", e);
        }
        return redemptionLog;
    }

    private boolean saveIngredient(String ingredient, String transactionNumber) {
        try {
            RedemptionIngredient redemptionIngredient = new RedemptionIngredient();
            int startIndex = 0;
            String ndc = ingredient.substring(startIndex, 19).trim();
            startIndex += 19;
            String name = ingredient.substring(startIndex, startIndex + 35).trim();
            startIndex += 35;
            String strength = ingredient.substring(startIndex, startIndex + 10).trim();
            startIndex += 10;

            String type = ingredient.substring(startIndex, startIndex + 10).trim();
            startIndex += 10;

            String processedSig = ingredient.substring(startIndex, startIndex + 1).trim();
            startIndex += 1;

            double quantity = Double.parseDouble(ingredient.substring(startIndex, startIndex + 10).trim());
            startIndex += 10;
            double planCost = Double.parseDouble(ingredient.substring(startIndex, startIndex + 10).trim());
            startIndex += 10;
            double pharmacyCost = Double.parseDouble(ingredient.substring(startIndex, startIndex + 10).trim());
            startIndex += 10;
            String covered = ingredient.substring(startIndex, startIndex + 1).trim();
            startIndex += 1;
            String formulary = ingredient.substring(startIndex, startIndex + 1).trim();

            redemptionIngredient.setName(name);
            redemptionIngredient.setNdc(ndc);
            redemptionIngredient.setPharmacyCost(pharmacyCost);
            redemptionIngredient.setStrength(strength);
            redemptionIngredient.setCovered(Boolean.parseBoolean(covered));
            redemptionIngredient.setFormulary(Boolean.parseBoolean(formulary));
            redemptionIngredient.setPlanCost(planCost);
            redemptionIngredient.setProcessedSig(processedSig);
            redemptionIngredient.setQuantity(quantity);
            redemptionIngredient.setTransactionNumber(transactionNumber);
            redemptionIngredient.setType(type);
            redemptionDAO.save(redemptionIngredient);
        } catch (NumberFormatException exp) {
            log.info("DailyRedemptionController -> saveIngredient : " + exp);
            return false;
        }
        return true;
    }

    private OptInOut setOptinOut(String phoneNumber, int campaignId, String campaignName, int sCode) {
        OptInOut optInOut = new OptInOut();
        optInOut.setPhoneNumber(phoneNumber);
        optInOut.setCampaignId(campaignId);
        optInOut.setCampaignName(campaignName);
        optInOut.setEffectiveDate(new Date());
        optInOut.setStatusCode(StatusEnum.COMPLETED.getValue());
        optInOut.setOptInOut("I");
        optInOut.setShortCode(sCode);
        return optInOut;
    }

    private CustomerRequest setCustomerRequest(DailyRedemption dailyRedemption, Integer shortCode, String source) {
        CustomerRequest customerRequest;
        customerRequest = new CustomerRequest();
        customerRequest.setPhoneNumber(dailyRedemption.getCommunicationId());
        customerRequest.setCampaignId(dailyRedemption.getCampaignId());
        customerRequest.setCampaignName(dailyRedemption.getProductName());
        customerRequest.setYcount(0);
        customerRequest.setKeywordCode(dailyRedemption.getProgramCode());
        customerRequest.setShortCode(shortCode);
        customerRequest.setEffectiveDate(new Date());
        customerRequest.setLastUpdatedOn(new Date());
        customerRequest.setCommunicationSourceCode(source);
        customerRequest.setWidgetName(null);
        customerRequest.setCardNumber(dailyRedemption.getCardholderId());
        customerRequest.setStatusCode(StatusEnum.COMPLETED.getValue());
        customerRequest.setInputReferenceNumber(null);
        customerRequest.setIvrId(null);
        customerRequest.setIvrPath(null);
        customerRequest.setYearOfBirth(null);
        return customerRequest;
    }

    @RequestMapping(value = "/dailyRedemption", method = RequestMethod.POST)
    private void processDrfFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ndcNumber = request.getParameter("ndcNumber");
        String phoneNO = request.getParameter("cardHolderId");
        String transactionNum = request.getParameter("transactionNum");
        String claimStatus = request.getParameter("claimStatus");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String fillDate = request.getParameter("fillDate");
        String cardHolderDob = request.getParameter("postingDate");
        String outofpocket = request.getParameter("outofpocket");
        File sourceFiles[] = FileUtil.readDRFiles(Constants.DRF_FILE_PATH);
        if (sourceFiles == null || sourceFiles.length == 0) {
            log.info("No file to process");
            return;
        }

        log.info("Files Found for processing");
        for (File file : sourceFiles) {
            if (file.isDirectory()) {
                log.info(file.isDirectory() + " It has been directory " + file.getName());
                continue;
            } else if (!file.getName().contains("main")) {
                log.info("not main file " + file.getName());
                continue;
            }
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                bufferedReader.readLine(); //moving to next line

                /* Reading each file line by line */
                while ((line = bufferedReader.readLine()) != null) {
                    try {

                        if (line.startsWith("$T")) {
                            log.info("Trailer Record (System will continue)");
                            continue;
                        }

                        String[] splitedSegments = line.split("\\>");
                        int segmentCount = splitedSegments.length;
                        if (segmentCount != 24 && segmentCount > 1) {
                            log.info("Invalid number of segments in current reocrd segment : " + splitedSegments.length + " Line: " + line);
                            continue;
                        }

                        RedemptionLog redemptionLog = saveRequest(line);
                        DailyRedemption dailyRedemption = DailyValidationUtil.getDailyRedemption(splitedSegments);
                        dailyRedemption.setMemberId(dailyRedemption.getSubmittedId());

                        dailyRedemption.setNdcNumber(ndcNumber);
                        dailyRedemption.getId().setTransactionNumber(transactionNum);
                        dailyRedemption.getId().setClaimStatus(Integer.parseInt(claimStatus));
                        dailyRedemption.setCardholderFirstName(firstName);
                        dailyRedemption.setCardholderLastName(lastName);
                        dailyRedemption.setFillDate(DateUtil.stringToDate(fillDate, "yyyy-MM-dd"));
                        dailyRedemption.setCardholderDob(DateUtil.stringToDate(cardHolderDob, "yyyy-MM-dd"));
                        BigDecimal ptOutOfPocket = new BigDecimal(outofpocket);
                        dailyRedemption.setPtOutOfPocket(ptOutOfPocket);

                        List<RedemptionIngredient> ingredient = redemptionDAO.getRedemptionIngredientByTransactionNumber(dailyRedemption.getId().getTransactionNumber());
                        dailyRedemption.setIngredientList(ingredient);

                        //String cardHolderId = dailyRedemption.getCardholderId();
                        Campaigns campaign = redemptionDAO.getCampaignByNDCNumber(ndcNumber);

                        int campaignId = 0;
                        String campaignName = "";
                        RedemptionChannel redemptionChannel = null;
                        GatewayInfo gatewayInfo = null;
                        int shortCodeValue = 0;
                        ShortCodes shortCode = null;

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
                            continue;
                        }

                        String redemptionChannelTitle = redemptionChannel.getRchannelTitle();
                        log.info("Redemption channel title : " + redemptionChannelTitle);

                        if (redemptionChannelTitle == null || (!redemptionChannelTitle.trim().equalsIgnoreCase("EMDEON"))) {
                            log.info("Redemption channel titel doesn't have value EMDEON. (System will return)");
                            continue;
                        }

                        dailyRedemption.setRedemptionChannelId(redemptionChannel.getRchannelId());
                        dailyRedemption.setRedemptionChannelTitle(redemptionChannelTitle);
                        dailyRedemption.setCampaignId(campaignId);
                        dailyRedemption.setProductName(campaignName);

                        InstantRequest instantRequest = redemptionDAO.getInstantRequestByMemberId(phoneNO, campaignId);
                        boolean isEnrolled = false;
                        String communicationMethod = null;
                        String communicationId = dailyRedemption.getCommunicationId();
                        log.info("CommunicationId is : " + communicationId);
                        if (instantRequest != null) {
                            String messageFlag = instantRequest.getFlagMessage();

                            if (messageFlag != null) {
                                messageFlag = messageFlag.trim();

                                if (messageFlag.equalsIgnoreCase("T")) {
                                    communicationMethod = "T";
                                    communicationId = instantRequest.getText();
                                } else if (messageFlag.equalsIgnoreCase("E")) {
                                    communicationMethod = "E";
                                    communicationId = instantRequest.getEmail();
                                } else if (messageFlag.equalsIgnoreCase("I")) {
                                    communicationMethod = "I";
                                    communicationId = instantRequest.getText();
                                }
                            } // if(messageFlag != null)
                            dailyRedemption.setCommunicationId(communicationId);
                            dailyRedemption.setCommunicationMethod(communicationMethod);
                        } else {
                            isEnrolled = true;
                            communicationMethod = "T";
                            dailyRedemption.setCommunicationMethod(communicationMethod);
                            CustomerRequest customerRequest = setCustomerRequest(dailyRedemption, shortCode.getShortCode(), "0002");
                            OptInOut optInOut = setOptinOut(communicationId, campaignId, campaignName, shortCode.getShortCode());
                            redemptionDAO.saveOrUpdate(customerRequest);
                            optInOut.setCrSeqNo(customerRequest.getCrSeqNo());
                            redemptionDAO.saveOrUpdate(optInOut);
                            TextFlowUtil.markCampaignEnd(customerRequest, campaign, null, pMSTextFlowService);
                        }

                        boolean phoneValidity = false;
                        CampaignTrigger campaignTrigger = redemptionDAO.getTriggerByCampaignId(campaignId);
                        String programCode = null;

                        if (campaignTrigger != null) {
                            programCode = campaignTrigger.getId().getKeyword();
                        }

                        Enrollment enrollment = null;
                        if (communicationMethod != null && communicationMethod.equalsIgnoreCase("T")) {
                            enrollment = redemptionDAO.getTextEnrollment(communicationId, campaignId);
                            if (shortCode != null) {
                                shortCodeValue = shortCode.getShortCode();
                            }

                            gatewayInfo = redemptionDAO.getGatewayInfo(shortCodeValue);

                            String appCode = "App000";
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
                            dailyRedemption.setEnrollmentId(new Long(enrollment.getEnrollmentId()).intValue());
                            dailyRedemption.setEnrollmentDate(enrollment.getEffectiveDate());
                            dailyRedemption.setCommunicationSourceCode(enrollment.getCommunicationSourceCode());
                            dailyRedemption.setEnrollmentPath(enrollment.getEnrollmentPath());
                            isEnrolled = true;
                        }

                        dailyRedemption.setProgramCode(programCode);

                        dailyRedemption.setIsValidPhone("NO");
                        if (phoneValidity) {
                            dailyRedemption.setIsValidPhone("YES");
                        }

                        if (communicationMethod != null && communicationMethod.equalsIgnoreCase("I")) {
                            if ("Yes".equalsIgnoreCase(instantRequest.getIsValidPhone())) {
                                dailyRedemption.setIsValidPhone("YES");
                            }
                            if ("L".equalsIgnoreCase(instantRequest.getIsValidPhone())) {
                                dailyRedemption.setIsValidPhone("L");
                            }
                        }

                        if (saveDrf(dailyRedemption, redemptionLog)) {
                            continue;
                        }

                        InstantRedemptionId id = new InstantRedemptionId();
                        id.setClaimStatus(dailyRedemption.getId().getClaimStatus());
                        id.setTransactionNumber(dailyRedemption.getId().getTransactionNumber());
                        int recordCount = redemptionDAO.getIRFCountById(id);

                        InstantRedemption instantRedemption = saveIrf(dailyRedemption, id, recordCount);
                        if (recordCount >= 1) {
                            log.info("Messgase already send via IRF");
                            continue;
                        }

                        if (!isEnrolled) {
                            log.info("Not enrolled patient. (System will return)");
                            continue;
                        }

                        if (communicationMethod == null || communicationMethod.trim().length() == 0) {
                            log.info("Communication method not define (System will return)");
                            continue;
                        }

                        if (!(communicationMethod.equalsIgnoreCase("T") || communicationMethod.equalsIgnoreCase("E") || communicationMethod.equalsIgnoreCase("I"))) {
                            log.info("Communication method not valid (System will return)");
                            continue;
                        }

                        if (communicationMethod.equalsIgnoreCase("T")) {
                            sendTextMessage(instantRedemption, shortCode, gatewayInfo, shortCodeValue, programCode, dailyRedemption, campaign, redemptionLog);
                        } else if (communicationMethod.equalsIgnoreCase("E")) {
                            sendEmailMessage(dailyRedemption, instantRedemption, campaign, redemptionLog);
                        } else if (communicationMethod.equalsIgnoreCase("I")) {
                            initiateIvrOutboundCall(dailyRedemption, instantRequest, campaignId, campaign, instantRedemption);
                        }
                    } catch (Exception exp) {
                        log.error(exp);
                    }
                }

                //delete file
                file.renameTo(new File(Constants.DRF_FILE_PATH + fileSeparator + "backup" + fileSeparator + file.getName()));

            } catch (IOException exp) {
                log.error(exp);
            }
        }
    }
}
