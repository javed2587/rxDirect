package com.ssa.cms.thread;

import com.ssa.cms.model.ClinicalMessageLog;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.util.SMSUtil;
import com.ssa.decorator.MTDecorator;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClinicalMessageThread implements Runnable {

    private final Log logger = LogFactory.getLog(getClass());

    private ClinicalMessageLog clinicalMessageLog;
    private RedemptionService redemptionDAO;
    private int secondsDelay;
    private Thread t;

    @Override
    public void run() {

        try {

            String message = clinicalMessageLog.getMessageText();
            String phoneNumber = clinicalMessageLog.getCommunicationId();
            logger.info("SMS Text : " + message + "Sleep interval: " + secondsDelay);

            Thread.sleep(1000 * secondsDelay);

            if (message != null && message.trim().length() > 0) {
                MTDecorator decorator = SMSUtil.sendSmsMessage(phoneNumber, message);
                clinicalMessageLog.setStatus(decorator.getErrorDescription());
                saveLog();
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    private boolean saveLog() {
        ClinicalMessageLog messageLog = new ClinicalMessageLog();
        messageLog.setCampaignId(clinicalMessageLog.getCampaignId());
        messageLog.setCommunicationId(clinicalMessageLog.getCommunicationId());
        messageLog.setCommunicationMethod(clinicalMessageLog.getCommunicationMethod());
        messageLog.setCreatedOn(new Date());
        messageLog.setMessageText(clinicalMessageLog.getMessageText());
        messageLog.setStatus(clinicalMessageLog.getStatus());
        messageLog.setNdc(clinicalMessageLog.getNdc());
        messageLog.setMessageCategoryId(clinicalMessageLog.getMessageCategoryId());
        messageLog.setMessageOrder(clinicalMessageLog.getMessageOrder());
        messageLog.setCreatedBy("Clinical Message Thread");
        redemptionDAO.save(messageLog);

        return true;
    }

    public ClinicalMessageLog getClinicalMessageLog() {
        return clinicalMessageLog;
    }

    public void setClinicalMessageLog(ClinicalMessageLog clinicalMessageLog) {
        this.clinicalMessageLog = clinicalMessageLog;
    }

    public int getSecondsDelay() {
        return secondsDelay;
    }

    public void setSecondsDelay(int secondsDelay) {
        this.secondsDelay = secondsDelay;
    }

    public RedemptionService getRedemptionDAO() {
        return redemptionDAO;
    }

    public void setRedemptionDAO(RedemptionService redemptionDAO) {
        this.redemptionDAO = redemptionDAO;
    }

}
