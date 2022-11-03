package com.ssa.cms.util;

import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.IvrcallTiming;
import com.ssa.cms.model.IvroutboundQueue;
import com.ssa.cms.model.IvrrequestResponse;
import com.ssa.cms.model.IvrvendorInfo;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.service.RefillReminderService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class IVRCallDialerUtil {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IVRCallDialerUtil.class);

    private InstantRedemption instantRedemption;
    private int campaignId;
    private int intervalId;
    private String intervalDesc;
    private String eventDesc;
    private String messageContext;
    private String servoId;
    private int folderId;
    private int messageTypeId;
    private boolean isLandLine;
    private boolean isMobile;
    private boolean callInitiated = false;
    private Double copay;
    private String cardType;
    private String cardEnding4;

    public void ivrCall(RedemptionService redemptionDAO) {

        try {
            String phoneNumber = instantRedemption.getCommunicationId();

            IvrrequestResponse ivrrequestResponse = new IvrrequestResponse();
            ivrrequestResponse.setCardholderDob(instantRedemption.getCardholderDob());
            ivrrequestResponse.setClaimStatus(instantRedemption.getId().getClaimStatus());
            ivrrequestResponse.setFileTypeCode(instantRedemption.getFileTypeCode());
            ivrrequestResponse.setFolderId(folderId);
            ivrrequestResponse.setMessageTypeId((messageTypeId));
            ivrrequestResponse.setCampaignId(campaignId);
            ivrrequestResponse.setNdcNumber(instantRedemption.getNdcNumber());
            ivrrequestResponse.setPhoneNumber(instantRedemption.getCommunicationId());
            ivrrequestResponse.setRedemptionId(instantRedemption.getRedemptionId());
            ivrrequestResponse.setRxGroupNumber(instantRedemption.getRxGroupNumber());
            ivrrequestResponse.setEffectiveDate(new Date());
            ivrrequestResponse.setFillDate(instantRedemption.getFillDate());
            ivrrequestResponse.setMessageContext(messageContext);
            ivrrequestResponse.setInputReferenceNo((long) 0);
            ivrrequestResponse.setIntervalDescription(intervalDesc);
            ivrrequestResponse.setIntervalId(intervalId);
            ivrrequestResponse.setEventDetail(eventDesc);
            ivrrequestResponse.setServoId(servoId);
            ivrrequestResponse.setIsLandLine(false);
            ivrrequestResponse.setIsMobile(false);
            ivrrequestResponse.setParameterList(getIvrParameterList(instantRedemption));
            if (isLandLine) {
                ivrrequestResponse.setIsLandLine(true);
            }
            if (isMobile) {
                ivrrequestResponse.setIsMobile(true);
            }
            int sentMessageCount = 0;
            sentMessageCount = redemptionDAO.retrieveIVRMessageCountForRedemption(ivrrequestResponse);
            if (sentMessageCount > 0) {
                log.info("PA Message already sent for phone number: " + phoneNumber);
                return;
            }
            String outboundCallurl = "";
            IvrvendorInfo ivrvendorInfo = (IvrvendorInfo) redemptionDAO.findById(IvrvendorInfo.class, 1);
            if (ivrvendorInfo != null) {
                outboundCallurl = ivrvendorInfo.getUrl();
            }

            OutboundUtil outboundUtil = new OutboundUtil();
            String finalUrl = outboundUtil.prepareFinalOutbooundCallUrl(outboundCallurl, phoneNumber, servoId);
            ivrrequestResponse.setRequestUrl(finalUrl);
            IvrcallTiming timing = redemptionDAO.getOutBoundCallTiming();
            if (timing == null) {
                log.info("Timing detail not found. system will return.");
                return;
            }
            int startHour = timing.getStartHour();
            int endHour = timing.getEndHour();
            String responseXml = "";
            GregorianCalendar calendar = new GregorianCalendar();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            if (currentHour >= startHour && currentHour < endHour && !callInitiated) {
                redemptionDAO.save(ivrrequestResponse);
                responseXml = outboundUtil.sendOutboundRequest(finalUrl);
                responseXml = responseXml.trim();
                // outboundUtil.parseResponse(responseXml, ivrrequestResponse);
                ivrrequestResponse.setErrorCode("307");
                ivrrequestResponse.setErrorDescription(responseXml);
                if (responseXml.contains("Call Connected")) {
                    ivrrequestResponse.setErrorCode("1");
                    ivrrequestResponse.setErrorDescription(responseXml);
                }
                ivrrequestResponse.setResponseXml(responseXml);
                redemptionDAO.update(ivrrequestResponse);
                callInitiated = true;
            } else {
                IvroutboundQueue queue = outboundUtil.prepareQueueRecord(ivrrequestResponse);
                queue.setCampaignId(campaignId);
                queue.setFolderId(folderId);
                queue.setMessageTypeId(messageTypeId);
                queue.setEffectiveDate(new Date());
                queue.setRedemptionId(instantRedemption.getRedemptionId());
                redemptionDAO.save(queue);
                callInitiated = false;
            }
        } catch (Exception e) {
            log.error("Exception in IVRCallDialerUtil-->ivrCall(): " + e.getMessage());
        }
    }

    public void ivrCall(RefillReminderService refillReminderDAO) {

        try {
            String phoneNumber = instantRedemption.getCommunicationId();

            IvrrequestResponse ivrrequestResponse = new IvrrequestResponse();
            ivrrequestResponse.setCardholderDob(instantRedemption.getCardholderDob());
            ivrrequestResponse.setClaimStatus(instantRedemption.getId().getClaimStatus());
            ivrrequestResponse.setFileTypeCode(instantRedemption.getFileTypeCode());
            ivrrequestResponse.setFolderId(folderId);
            ivrrequestResponse.setMessageTypeId((messageTypeId));
            ivrrequestResponse.setCampaignId(campaignId);
            ivrrequestResponse.setNdcNumber(instantRedemption.getNdcNumber());
            ivrrequestResponse.setPhoneNumber(instantRedemption.getCommunicationId());
            ivrrequestResponse.setRedemptionId(instantRedemption.getRedemptionId());
            ivrrequestResponse.setRxGroupNumber(instantRedemption.getRxGroupNumber());
            ivrrequestResponse.setEffectiveDate(new Date());
            ivrrequestResponse.setFillDate(instantRedemption.getFillDate());
            ivrrequestResponse.setMessageContext(messageContext);
            ivrrequestResponse.setInputReferenceNo((long) 0);
            ivrrequestResponse.setIntervalDescription(intervalDesc);
            ivrrequestResponse.setIntervalId(intervalId);
            ivrrequestResponse.setEventDetail(eventDesc);
            ivrrequestResponse.setServoId(servoId);
            ivrrequestResponse.setIsLandLine(false);
            ivrrequestResponse.setIsMobile(false);
            ivrrequestResponse.setParameterList(getIvrParameterList(instantRedemption));
            if (isLandLine) {
                ivrrequestResponse.setIsLandLine(true);
            }
            if (isMobile) {
                ivrrequestResponse.setIsMobile(true);
            }
            int sentMessageCount = 0;
            sentMessageCount = refillReminderDAO.retrieveIVRMessageCountForRedemption(ivrrequestResponse);
            if (sentMessageCount > 0) {
                log.info("PA Message already sent for phone number: " + phoneNumber);
                return;
            }
            String outboundCallurl = "";
            IvrvendorInfo ivrvendorInfo = (IvrvendorInfo) refillReminderDAO.findById(IvrvendorInfo.class, 1);
            if (ivrvendorInfo != null) {
                outboundCallurl = ivrvendorInfo.getUrl();
            }

            OutboundUtil outboundUtil = new OutboundUtil();
            String finalUrl = outboundUtil.prepareFinalOutbooundCallUrl(outboundCallurl, phoneNumber, servoId);
            ivrrequestResponse.setRequestUrl(finalUrl);
            IvrcallTiming timing = refillReminderDAO.getOutBoundCallTiming();
            if (timing == null) {
                log.info("Timing detail not found. system will return.");
                return;
            }
            int startHour = timing.getStartHour();
            int endHour = timing.getEndHour();
            String responseXml = "";
            GregorianCalendar calendar = new GregorianCalendar();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            if (currentHour >= startHour && currentHour < endHour && !callInitiated) {
                refillReminderDAO.save(ivrrequestResponse);
                responseXml = outboundUtil.sendOutboundRequest(finalUrl);
                responseXml = responseXml.trim();
                ivrrequestResponse.setErrorCode("307");
                ivrrequestResponse.setErrorDescription(responseXml);
                if (responseXml.contains("Call Connected")) {
                    ivrrequestResponse.setErrorCode("1");
                    ivrrequestResponse.setErrorDescription(responseXml);
                }
                // outboundUtil.parseResponse(responseXml, ivrrequestResponse);
                ivrrequestResponse.setResponseXml(responseXml);
                refillReminderDAO.update(ivrrequestResponse);
                callInitiated = true;
            } else {
                IvroutboundQueue queue = outboundUtil.prepareQueueRecord(ivrrequestResponse);
                queue.setCampaignId(campaignId);
                queue.setFolderId(folderId);
                queue.setMessageTypeId(messageTypeId);
                queue.setEffectiveDate(new Date());
                queue.setRedemptionId(instantRedemption.getRedemptionId());
                refillReminderDAO.save(queue);
                callInitiated = false;
            }
        } catch (Exception e) {
            log.error("Exception in IVRCallDialerUtil-->ivrCall(RefillReminderService): " + e.getMessage());
        }
    }

    private String getIvrParameterList(InstantRedemption ird) {
        String parameterList = "";
        try {
            if (ird.getPharmacyName() != null && !ird.getPharmacyName().isEmpty()) {
                parameterList += "PharmacyName=" + ird.getPharmacyName();
                parameterList += "&";
            }

            if (ird.getPharmacyPhone() != null && !ird.getPharmacyPhone().isEmpty()) {
                parameterList += "PharmacyPhone=" + ird.getPharmacyPhone();
                parameterList += "&";
            }

            if (getCopay() != null) {
                parameterList += "Copay=" + getCopay();
                parameterList += "&";
            }

            if (ird.getPtOutOfPocket() != null) {
                parameterList += "PtOutOfPocket=" + ird.getPtOutOfPocket();
                parameterList += "&";
            }

            if (ird.getPtOutOfPocket() != null && getCopay() != null) {
                parameterList += "Payment=" + (ird.getPtOutOfPocket().doubleValue() - getCopay());
                parameterList += "&";
            }

            if (getCardType() != null) {
                parameterList += "CardType=" + getCardType();
                parameterList += "&";
            }

            if (getCardEnding4() != null) {
                parameterList += "CardEnding4=" + getCardEnding4();
                parameterList += "&";
            }

            parameterList += "DeliveryDate=" + DateUtil.dateToString(new Date(), "EEEE MMMM d, y");
            parameterList += "&";

            int refillAllowed = ird.getRefillAllowed();
            int refillUsed = ird.getRefillsUsed();
            int remainingRefill = refillAllowed - refillUsed;
            parameterList += "RemainingRefills=" + remainingRefill;
        } catch (Exception e) {
            log.error("Exception in IVRCallDialerUtil-->getParameterList(): " + e.getMessage());
        }
        return parameterList;
    }

    public InstantRedemption getInstantRedemption() {
        return instantRedemption;
    }

    public void setInstantRedemption(InstantRedemption instantRedemption) {
        this.instantRedemption = instantRedemption;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(int intervalId) {
        this.intervalId = intervalId;
    }

    public String getIntervalDesc() {
        return intervalDesc;
    }

    public void setIntervalDesc(String intervalDesc) {
        this.intervalDesc = intervalDesc;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(String messageContext) {
        this.messageContext = messageContext;
    }

    public String getServoId() {
        return servoId;
    }

    public void setServoId(String servoId) {
        this.servoId = servoId;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public boolean isIsMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    public boolean isIsLandLine() {
        return isLandLine;
    }

    public void setIsLandLine(boolean isLandLine) {
        this.isLandLine = isLandLine;
    }

    public boolean isCallInitiated() {
        return callInitiated;
    }

    public void setCallInitiated(boolean callInitiated) {
        this.callInitiated = callInitiated;
    }

    public Double getCopay() {
        return copay;
    }

    public void setCopay(Double copay) {
        this.copay = copay;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardEnding4() {
        return cardEnding4;
    }

    public void setCardEnding4(String cardEnding4) {
        this.cardEnding4 = cardEnding4;
    }

}
