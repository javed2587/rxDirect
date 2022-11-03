package com.ssa.cms.thread;

import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.EmailResponse;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.service.RefillReminderService;
import com.ssa.cms.util.EmailSender;
import java.util.Date;
import org.apache.log4j.Logger;

public class EmailRefillSendingThread implements Runnable {

    private String email;
    private String emailBody;
    private String emailSubject;
    private SmtpServerInfo smtpServerInfo;
    private Campaigns campaign;
    private long secondsDelay;
    private RefillReminderService refillReminderDAO;
    private EmailRequest emailRequest;
    private static final Logger logger = Logger.getLogger(EmailRefillSendingThread.class);

    @Override
    public void run() {
        logger.info("Thread going to sleep for " + secondsDelay + " Seconds.");
        try {

            Thread.sleep(1000 * secondsDelay);

            emailRequest.setEffectiveDate(new Date());
            refillReminderDAO.save(emailRequest);

            boolean flag = EmailSender.sendEmail(email, emailSubject, emailBody, smtpServerInfo, campaign);

            String emailStatus = "Success";

            if (!flag) {
                emailStatus = "Failure";
            }

            EmailResponse emailResponse = new EmailResponse();
            emailResponse.setEffectiveDate(new Date());
            emailResponse.setEmailReqNo(emailRequest.getEmailReqNo());
            emailResponse.setEmailStatus(emailStatus);

            refillReminderDAO.save(emailResponse);
        } catch (InterruptedException e) {
            logger.error("Exception->EmailRefillSendingThread ThreadRun: ", e);
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

    public RefillReminderService getRefillReminderDAO() {
        return refillReminderDAO;
    }

    public void setRefillReminderDAO(RefillReminderService refillReminderDAO) {
        this.refillReminderDAO = refillReminderDAO;
    }

    public EmailRequest getEmailRequest() {
        return emailRequest;
    }

    public void setEmailRequest(EmailRequest emailRequest) {
        this.emailRequest = emailRequest;
    }
}
