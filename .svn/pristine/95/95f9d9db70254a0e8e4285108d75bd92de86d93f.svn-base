package com.ssa.cms.thread;

import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.EmailResponse;
import com.ssa.cms.model.RedemptionLog;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.util.EmailSender;
import java.util.Date;
import org.apache.log4j.Logger;

public class EmailRedemptionSendingThread implements Runnable {
    
    private static final Logger logger = Logger.getLogger(EmailRedemptionSendingThread.class);
    private String email;
    private String emailBody;
    private String emailSubject;
    private SmtpServerInfo smtpServerInfo;
    private Campaigns campaign;
    private long secondsDelay;
    private RedemptionService redemptionDAO;
    private int messageSent;
    private boolean isEm = false;
    private RedemptionLog redemptionLog;
    private EmailRequest emailRequest;
    
    @Override
    public void run() {
        logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
        try {
            
            Thread.sleep(1000 * secondsDelay);
            
            int claimStatus = emailRequest.getClaimStatus();
            
            if (claimStatus == 1) {
                long seconds = redemptionDAO.getIRFLastRedemptionSecondsByPhone(email, campaign.getCampaignId());
                
                if (seconds <= secondsDelay && seconds != 0) {
                    logger.info("Paid received within " + secondsDelay + " Seconds");
                    return;
                }
            }
            
            emailRequest.setEffectiveDate(new Date());
            redemptionDAO.save(emailRequest);
            
            boolean flag = EmailSender.sendEmail(email, emailSubject, emailBody, smtpServerInfo, campaign);
            String emailStatus = "Success";
            
            if (!flag) {
                emailStatus = "Failure";
            }
            
            EmailResponse emailResponse = new EmailResponse();
            emailResponse.setEffectiveDate(new Date());
            emailResponse.setEmailReqNo(emailRequest.getEmailReqNo());
            emailResponse.setEmailStatus(emailStatus);
            
            redemptionDAO.save(emailResponse);
            
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
    
    public RedemptionService getRedemptionDAO() {
        return redemptionDAO;
    }
    
    public void setRedemptionDAO(RedemptionService redemptionDAO) {
        this.redemptionDAO = redemptionDAO;
    }
    
    public int getMessageSent() {
        return messageSent;
    }
    
    public void setMessageSent(int messageSent) {
        this.messageSent = messageSent;
    }
    
    public boolean isIsEm() {
        return isEm;
    }
    
    public void setIsEm(boolean isEm) {
        this.isEm = isEm;
    }
    
    public EmailRequest getEmailRequest() {
        return emailRequest;
    }
    
    public void setEmailRequest(EmailRequest emailRequest) {
        this.emailRequest = emailRequest;
    }
    
    public RedemptionLog getRedemptionLog() {
        return redemptionLog;
    }
    
    public void setRedemptionLog(RedemptionLog redemptionLog) {
        this.redemptionLog = redemptionLog;
    }
}
