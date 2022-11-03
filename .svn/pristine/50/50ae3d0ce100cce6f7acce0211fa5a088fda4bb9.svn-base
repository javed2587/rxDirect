package com.ssa.cms.thread;

import com.ssa.cms.model.CampaignEmailRequest;
import com.ssa.cms.model.CampaignEmailResponse;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.PMSEmailFlowService;
import com.ssa.cms.util.EmailSender;
import java.util.Date;
import org.apache.log4j.Logger;

public class EmailOptinSendingThread implements Runnable {

    private String email;
    private String emailBody;
    private String emailSubject;
    private SmtpServerInfo smtpServerInfo;
    private Campaigns campaign;
    private long secondsDelay;
    private PMSEmailFlowService emailFlowDAO;
    private CampaignEmailRequest emailRequest;
    private static final Logger logger = Logger.getLogger(EmailOptinSendingThread.class);

    @Override
    public void run() {
        logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
        try {

            Thread.sleep(1000 * secondsDelay);

            emailRequest.setEffectiveDate(new Date());
            emailFlowDAO.save(emailRequest);

            boolean flag = EmailSender.sendEmail(email, emailSubject, emailBody, smtpServerInfo, campaign);
            String emailStatus = "Success";

            if (!flag) {
                emailStatus = "Failure";
            }

            CampaignEmailResponse campaignEmailResponse = new CampaignEmailResponse();
            campaignEmailResponse.setEffectiveDate(new Date());
            campaignEmailResponse.setEmailReqNo(emailRequest.getEmailReqNo());
            campaignEmailResponse.setEmailStatus(emailStatus);

            emailFlowDAO.save(campaignEmailResponse);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public SmtpServerInfo getSmtpServerInfo() {
        return smtpServerInfo;
    }

    public void setSmtpServerInfo(SmtpServerInfo smtpServerInfo) {
        this.smtpServerInfo = smtpServerInfo;
    }

    public Campaigns getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaigns campaign) {
        this.campaign = campaign;
    }

    public long getSecondsDelay() {
        return secondsDelay;
    }

    public void setSecondsDelay(long secondsDelay) {
        this.secondsDelay = secondsDelay;
    }

    public PMSEmailFlowService getEmailFlowDAO() {
        return emailFlowDAO;
    }

    public void setEmailFlowDAO(PMSEmailFlowService emailFlowDAO) {
        this.emailFlowDAO = emailFlowDAO;
    }

    public CampaignEmailRequest getEmailRequest() {
        return emailRequest;
    }

    public void setEmailRequest(CampaignEmailRequest emailRequest) {
        this.emailRequest = emailRequest;
    }

}
