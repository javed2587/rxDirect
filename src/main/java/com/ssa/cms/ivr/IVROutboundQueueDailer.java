/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.ivr;

import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.IvrcallTiming;
import com.ssa.cms.model.IvroptInOut;
import com.ssa.cms.model.IvroutboundQueue;
import com.ssa.cms.model.IvrrequestResponse;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.util.OutboundUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Abid
 */
public class IVROutboundQueueDailer {
    
    public void processQueue(RedemptionService redemptionDAO) {
        IvrcallTiming timing = redemptionDAO.getOutBoundCallTiming();
        if (timing == null) {
            System.out.println("Timing detail not found. system will return.");
            return;
        }
        int startHour = timing.getStartHour();
        int endHour = timing.getEndHour();
        String responseXml = "";
        GregorianCalendar calendar = new GregorianCalendar();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (currentHour < startHour || currentHour >= endHour) {
            System.out.println("We can't call from : " + startHour + " to " + endHour + " only");
            System.out.println("Current hour is : " + currentHour);
            System.out.println("System will return");
            return;
        }
        List<IvroutboundQueue> list = redemptionDAO.getOutboundQueueRecords();
        int recordCount = list.size();
        System.out.println("Record Count : " + recordCount);
        IvrrequestResponse outboundReqRes = null;
        for (IvroutboundQueue ivroutboundQueue : list) {
            boolean isMobile = false;
            boolean isLandLine = false;
            ivroutboundQueue.setEndDate(new Date());
            int campaignId = ivroutboundQueue.getCampaignId();
            String optInOutFlag = null;
            String phoneNumber = ivroutboundQueue.getPhoneNumber();
            if (ivroutboundQueue.getIsMobile().equalsIgnoreCase("Yes")) {
                isMobile = true;
            }
            if (ivroutboundQueue.getIsLandLine().equalsIgnoreCase("Yes")) {
                isLandLine = true;
            }
            if (isLandLine == false && isMobile == false) {
                System.out.println(phoneNumber + " PhoneNumber neither Mobile nor Landline : " + campaignId);
                System.out.println("System will return");
                redemptionDAO.update(ivroutboundQueue);
                return;
            }
            IvroptInOut optInOut = redemptionDAO.getIVROptInOut(campaignId, phoneNumber);
            
            if (optInOut != null) {
                optInOutFlag = optInOut.getOptInOut();
            }
            
            if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                System.out.println(phoneNumber + " has opted out from Campaign : " + campaignId);
                System.out.println("System will return");
                redemptionDAO.update(ivroutboundQueue);
                return;
            }
            InstantRedemption instantRedemption = redemptionDAO.getInstantRedemptionDetailByRedemptionId(ivroutboundQueue.getRedemptionId());
            IvrrequestResponse ivrrequestResponse = new IvrrequestResponse();
            ivrrequestResponse.setCardholderDob(instantRedemption.getCardholderDob());
            ivrrequestResponse.setClaimStatus(instantRedemption.getId().getClaimStatus());
            ivrrequestResponse.setFileTypeCode(instantRedemption.getFileTypeCode());
            ivrrequestResponse.setFolderId(ivroutboundQueue.getFolderId());
            ivrrequestResponse.setMessageTypeId((ivroutboundQueue.getMessageTypeId()));
            ivrrequestResponse.setCampaignId(campaignId);
            ivrrequestResponse.setNdcNumber(instantRedemption.getNdcNumber());
            ivrrequestResponse.setPhoneNumber(instantRedemption.getCommunicationId());
            ivrrequestResponse.setRedemptionId(instantRedemption.getRedemptionId());
            ivrrequestResponse.setRxGroupNumber(instantRedemption.getRxGroupNumber());
            ivrrequestResponse.setEffectiveDate(new Date());
            ivrrequestResponse.setFillDate(instantRedemption.getFillDate());
            ivrrequestResponse.setMessageContext(ivroutboundQueue.getMessageContext());
            ivrrequestResponse.setInputReferenceNo((long) 0);
            ivrrequestResponse.setIntervalDescription(ivroutboundQueue.getIntervalDescription());
            ivrrequestResponse.setIntervalId(ivroutboundQueue.getIntervalId());
            ivrrequestResponse.setEventDetail(ivroutboundQueue.getEventDetail());
            ivrrequestResponse.setServoId(ivroutboundQueue.getServoId());
            ivrrequestResponse.setMessageContext(ivroutboundQueue.getMessageContext());
            ivrrequestResponse.setIsLandLine(false);
            ivrrequestResponse.setIsMobile(false);
            ivrrequestResponse.setRequestUrl(ivroutboundQueue.getRequestUrl());
            if (isLandLine) {
                ivrrequestResponse.setIsLandLine(true);
            }
            if (isMobile) {
                ivrrequestResponse.setIsMobile(true);
            }
            int sentMessageCount = 0;
            sentMessageCount = redemptionDAO.retrieveIVRMessageCountForRedemption(ivrrequestResponse);
            if (sentMessageCount > 0) {
                System.out.println("Message already sent for phone number: " + phoneNumber);
                redemptionDAO.update(ivroutboundQueue);
                return;
            }
            redemptionDAO.save(ivrrequestResponse);
            OutboundUtil outboundUtil = new OutboundUtil();
            responseXml = outboundUtil.sendOutboundRequest(ivroutboundQueue.getRequestUrl());
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
            ivroutboundQueue.setCallDailed("Yes");
            redemptionDAO.update(ivroutboundQueue);
            
        }
        
    }
}
