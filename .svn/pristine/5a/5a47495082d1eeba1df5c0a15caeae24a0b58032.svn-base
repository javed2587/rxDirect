package com.ssa.cms.remainder;

import com.ssa.cms.common.Constants;
import com.ssa.cms.model.CampaignEmailRequest;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.ReminderPOJO;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.PMSEmailFlowService;
import com.ssa.cms.thread.EmailOptinSendingThread;
import com.ssa.cms.util.RedemptionUtil;
import java.util.List;

public class PMSGenericEmailCouponReminder {

    public void processCouponReminder(PMSEmailFlowService emailFlowDAO) {

        try {

            List<ReminderPOJO> list = emailFlowDAO.getCouponRemindeRecords();

            if (list == null || list.isEmpty()) {
                System.out.println("No campaign reminder record found. (System will return)");
                return;
            }

            System.out.println("List Size : " + list.size());

            int campaignId = 0;
            int folderId = 0;
            int messageTypeId = 0;
            int smtpId = 0;
            String campaignName = "";
            int ecrSeqNo = 0;
            int messageId = 0;
            String email = "";
            int count = 0;

            CampaignMessagesResponse campaignMessagesResponseSent = null;
            CampaignMessagesResponse campaignMessagesResponse = null;

            CampaignEmailRequest campaignEmailRequest = null;
            SmtpServerInfo smtpServerInfo = null;

            long seconds = 0;

            int irfCount = 0;
            long irfLastRedemptionSeconds = 0;

            for (ReminderPOJO reminderPOJO : list) {

                campaignId = reminderPOJO.getCampaignId();
                folderId = reminderPOJO.getFolderId();
                messageTypeId = reminderPOJO.getMessageTypeId();
                email = reminderPOJO.getEmail();
                seconds = reminderPOJO.getSeconds();
                count = 0;

                Campaigns campaigns = emailFlowDAO.getCampaignsById(campaignId);

                if (campaigns == null) {
                    System.out.println("Campaign doesnt exists");
                    continue;
                }

                System.out.println("Email : " + email);

                if (email == null || email.equalsIgnoreCase("")) {
                    System.out.println("Email is empty. System will continue");
                    continue;
                }

                irfCount = emailFlowDAO.getIRFRedemptionCountByEmail(email, campaignId);
                System.out.println("IRF Redemption Count : " + irfCount);

                if (irfCount > 0) {
                    irfLastRedemptionSeconds = emailFlowDAO.getIRFLastRedemptionSecondsByEmail(email, campaignId);

                    System.out.println("IRF last redemption seconds passed : " + irfLastRedemptionSeconds);

                    if (irfLastRedemptionSeconds > 0 && irfLastRedemptionSeconds <= (seconds + (24 * 3600))) {
                        System.out.println("IRF Redemption already there in last " + irfLastRedemptionSeconds + " System will continue");
                        continue;
                    }
                }

                campaignMessagesResponseSent = emailFlowDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, messageTypeId, Constants.EMAIL);

                if (campaignMessagesResponseSent == null) {
                    System.out.println("campaignMessagesResponseSent is null (System will continue.)");
                    continue;
                }

                int repeatMessageTypeId = campaignMessagesResponseSent.getRepeatMessageId();

                System.out.println("Repeat Message Type Id to be sent : " + repeatMessageTypeId);

                campaignMessagesResponse = emailFlowDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, repeatMessageTypeId, Constants.EMAIL);

                if (campaignMessagesResponse == null) {
                    System.out.println("campaignMessagesResponse for repeat not found.");
                    continue;
                }

                Intervals interval = campaignMessagesResponse.getIntervals();
                IntervalsType intervalsType = campaignMessagesResponse.getIntervals().getIntervalsType();

                int intervalId = reminderPOJO.getIntervalId();
                String intervalDesc = +interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
                String eventDesc = emailFlowDAO.getEventsDescription(campaignId, folderId);

                int intervalVal = interval.getIntervalValue();
                int intervalUnitInSecond = intervalsType.getUnitInSecond();
                long secondsDelay = intervalVal * intervalUnitInSecond;

                List<CampaignMessages> campaignMessagesList = emailFlowDAO.getCampaignMessagesByCommunicationPath(campaignId, folderId, repeatMessageTypeId, Constants.EMAIL);

                if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
                    System.out.println("No messages found for (System will return)");
                    continue;
                }

                CampaignMessages campaignMessages = campaignMessagesList.get(0);

                if (campaignMessages == null) {
                    System.out.println("CampaignMessages object not found. (System will return)");
                    continue;
                }

                ecrSeqNo = reminderPOJO.getEcrSeqNo();
                campaignName = reminderPOJO.getCampaignName();
                smtpId = reminderPOJO.getSmtpId();
                messageId = campaignMessages.getMessageId();

                smtpServerInfo = emailFlowDAO.getSmtpServerInfo(smtpId);

                if (smtpServerInfo == null) {
                    System.out.println("smtpServerInfo object not found. (System will return)");
                    continue;
                }

                String emailBody = null;
                String emailSubject = null;
                String emailFrom = null;

                if (emailFrom == null || emailFrom.equalsIgnoreCase("")) {
                    System.out.println("No Email from found System will continue");
                    System.out.println("Message Type ID: " + messageTypeId);
                    continue;
                }

                if (emailBody == null || emailBody.equalsIgnoreCase("")) {
                    System.out.println("No Email body found System will continue");
                    System.out.println("Message Type ID: " + messageTypeId);
                    continue;
                }

                if (messageTypeId == repeatMessageTypeId) {
                    count = 1;
                }

                int sentMessageCount = emailFlowDAO.sentEmailCountCampaign(email, campaignId, folderId, repeatMessageTypeId);

                if (sentMessageCount > count) {
                    System.out.println("Message already sent to Email : " + email);
                    continue;
                }

                String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(email, smtpServerInfo.getFromEmail());
                String actionUrl = RedemptionUtil.prepareActionURL(email, smtpServerInfo.getFromEmail());

                emailBody = RedemptionUtil.placeHolders(emailBody, unsubUrl, actionUrl);

                emailBody = emailBody.replace("XXXXXXXXXXX", reminderPOJO.getCardNumber());

                campaignName = reminderPOJO.getCampaignName();

                if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
                    emailBody = emailBody.replace("[_brand_]", campaignName);
                }

                System.out.println("Final Email :" + emailBody);

                campaignEmailRequest = new CampaignEmailRequest();
                campaignEmailRequest.setCampaignId(campaignId);
                campaignEmailRequest.setCampaignName(campaignName);
                campaignEmailRequest.setEcrSeqNo(ecrSeqNo);
                campaignEmailRequest.setEmail(email);
                campaignEmailRequest.setEmailBody(emailBody.getBytes());
                campaignEmailRequest.setEmailSubject(emailSubject);
                campaignEmailRequest.setFolderId(folderId);
                campaignEmailRequest.setMessageId(messageId);
                campaignEmailRequest.setMessageTypeId(repeatMessageTypeId);
                campaignEmailRequest.setSmtpId(smtpId);
                campaignEmailRequest.setIntervalDescription(intervalDesc);
                campaignEmailRequest.setEventDetail(eventDesc);
                campaignEmailRequest.setIntervalId(intervalId);

                EmailOptinSendingThread emailOptinSendingThread = new EmailOptinSendingThread();

                emailOptinSendingThread.setCampaign(campaigns);
                emailOptinSendingThread.setEmail(email);
                emailOptinSendingThread.setEmailBody(emailBody);
                emailOptinSendingThread.setEmailFlowDAO(emailFlowDAO);
                emailOptinSendingThread.setEmailRequest(campaignEmailRequest);
                emailOptinSendingThread.setEmailSubject(emailSubject);
                emailOptinSendingThread.setSecondsDelay(secondsDelay);
                emailOptinSendingThread.setSmtpServerInfo(smtpServerInfo);

                Thread thread = new Thread(emailOptinSendingThread);
                thread.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
