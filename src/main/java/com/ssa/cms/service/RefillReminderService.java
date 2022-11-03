/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.service;

import com.ssa.cms.bean.BusinessObject;
import com.ssa.cms.common.Constants;
import com.ssa.cms.dao.RefillReminderDAO;
import com.ssa.cms.dao.PatientProfileDAO;
import com.ssa.cms.model.AggregatorMessageRequest;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CampaignTrigger;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.CustomerRequest;
import com.ssa.cms.model.DailyRedemption;
import com.ssa.cms.model.DailyRedemptionId;
import com.ssa.cms.model.Drug;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.EventDetail;
import com.ssa.cms.model.EventHasFolderHasCampaigns;
import com.ssa.cms.model.GatewayInfo;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IvrcallTiming;
import com.ssa.cms.model.IvroptInOut;
import com.ssa.cms.model.IvrrequestResponse;
import com.ssa.cms.model.MessagePriority;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.OptInOut;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.PatientInsuranceDetails;
import com.ssa.cms.model.PatientProfile;
import com.ssa.cms.model.RefillReminderPOJO;
import com.ssa.cms.model.RewardPoints;
import com.ssa.cms.model.RxMMSRedemptionReqRes;
import com.ssa.cms.model.ValidResponse;
import com.ssa.cms.util.AppUtil;
import com.ssa.cms.util.CommonUtil;
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EncryptionHandlerUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RefillReminderService {

    @Autowired
    private RefillReminderDAO refillReminderDAO;

    @Autowired
    private PatientProfileDAO patientProfileDAO;

    @Autowired
    private PatientProfileService patientProfileService;

    private final Log logger = LogFactory.getLog(getClass());

    public RefillReminderDAO getRefillReminderDAO() {
        return refillReminderDAO;
    }

    public void setRefillReminderDAO(RefillReminderDAO refillReminderDAO) {
        this.refillReminderDAO = refillReminderDAO;
    }

    public OptInOut getTextOptInOut(int campaignId, String phoneNumber) {
        OptInOut optInOut = null;
        try {
            optInOut = refillReminderDAO.getTextOptInOut(campaignId, phoneNumber);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getTextOptInOut", e);
        }
        return optInOut;
    }

    public void save(Object bean) {
        try {
            refillReminderDAO.save(bean);
        } catch (Exception ex) {
            logger.error("Exception: RefillReminderService -> save", ex);
        }
    }

    public void update(Object bean) {
        try {
            refillReminderDAO.update(bean);
        } catch (Exception ex) {
            logger.error("Exception: RefillReminderService -> update", ex);
        }
    }

    public void delete(Object bean) {
        try {
            refillReminderDAO.delete(bean);
        } catch (Exception ex) {
            logger.error("Exception: RefillReminderService -> delete", ex);
        }
    }

    public List<Campaigns> getAllRefillCandidateActiveCampaign() {
        List<Campaigns> list = null;
        try {
            list = refillReminderDAO.getAllRefillCandidateActiveCampaign();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAllRefillCandidateActiveCampaign", e);
        }
        return list;
    }

    public List<Campaigns> getAllRepeatRefillCandidateActiveCampaign() {
        List<Campaigns> list = null;
        try {
            list = refillReminderDAO.getAllRepeatRefillCandidateActiveCampaign();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAllRepeatRefillCandidateActiveCampaign", e);
        }
        return list;
    }

    public List<Campaigns> getAllRefillFailureCandidateActiveCampaign() {
        List<Campaigns> list = null;
        try {
            list = refillReminderDAO.getAllRefillFailureCandidateActiveCampaign();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAllRefillFailureCandidateActiveCampaign", e);
        }
        return list;
    }

    public List<Campaigns> getAllRefillOrderReminderCandidateActiveCampaign() {
        List<Campaigns> list = null;
        try {
            list = refillReminderDAO.getAllRefillOrderReminderCandidateActiveCampaign();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAllRefillOrderReminderCandidateActiveCampaign", e);
        }
        return list;
    }

    public List<Campaigns> getAllPACandidateActiveCampaign() {
        List<Campaigns> list = null;
        try {
            list = refillReminderDAO.getAllPACandidateActiveCampaign();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAllPACandidateActiveCampaign", e);
        }
        return list;
    }

    public List<DailyRedemption> getDRFRefillReminderRecordsByCampaignId(int campaignId, int intervalValue) {
        return refillReminderDAO.getDRFRefillReminderRecordsByCampaignId(campaignId, intervalValue);
    }

    public List<EventHasFolderHasCampaigns> getEventHasFolderHasCampaign(int campaignId) {
        List<EventHasFolderHasCampaigns> list = new ArrayList<>();
        try {
            list = refillReminderDAO.getEventHasFolderHasCampaign(campaignId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEventHasFolderHasCampaign", e);
        }
        return list;
    }

    public List<EventHasFolderHasCampaigns> getEventHasFolderHasCampaign(int campaignId, String dynamicValue) {
        List<EventHasFolderHasCampaigns> list = new ArrayList<>();
        try {
            list = refillReminderDAO.getEventHasFolderHasCampaign(campaignId, dynamicValue);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEventHasFolderHasCampaign", e);
        }
        return list;
    }

    public List<CampaignMessages> getCampaignMessages(int campaignId, int folderId) {
        List<CampaignMessages> list = null;
        try {
            list = refillReminderDAO.getCampaignMessages(campaignId, folderId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessages", e);
        }
        return list;
    }

    public List<CampaignMessages> getCampaignMessagesByResponseTitle(int campaignId, int folderId) {
        List<CampaignMessages> list = null;
        try {
            list = refillReminderDAO.getCampaignMessagesByResponseTitle(campaignId, folderId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesByResponseTitle", e);
        }
        return list;
    }

    public List<CampaignMessages> getCampaignMessages(int campaignId, int folderId, int messageTypeId) {
        List<CampaignMessages> list = null;
        try {
            list = refillReminderDAO.getCampaignMessages(campaignId, folderId, messageTypeId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessages", e);
        }
        return list;
    }

    public CustomerRequest getCustomerRequestIPorCM(String phoneNumber) {
        CustomerRequest customerRequest = null;
        try {
            customerRequest = refillReminderDAO.getCustomerRequestIPorCM(phoneNumber);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCustomerRequestIPorCM", e);
        }
        return customerRequest;
    }

    public OptInOut checkOptInOut(String phoneNumber, int campaignId, int shortCode) {
        OptInOut optInOut = null;
        try {
            optInOut = refillReminderDAO.checkOptInOut(phoneNumber, campaignId, shortCode);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> checkOptInOut", e);
        }
        return optInOut;
    }

    public CampaignMessagesResponse getCampaignMessagesResponse(int campaignId, int folderId, int messageTypeId) {
        CampaignMessagesResponse campaignMessagesResponse = null;
        try {
            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponse(campaignId, folderId, messageTypeId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesResponse", e);
        }
        return campaignMessagesResponse;
    }

    public CampaignMessagesResponse getCampaignMessagesResponse(int campaignId, int folderId, String communicationPath) {
        CampaignMessagesResponse campaignMessagesResponse = null;
        try {
            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponse(campaignId, folderId, communicationPath);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesResponse", e);
        }
        return campaignMessagesResponse;
    }

    public CampaignMessagesResponse getCampaignMessagesResponsebyCommunicationPath(int campaignId, int folderId, int messageTypeId, String communicationPath) {
        CampaignMessagesResponse campaignMessagesResponse = null;
        try {
            if (communicationPath.equalsIgnoreCase("T")) {
                communicationPath = Constants.SMS;
            }
            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, folderId, messageTypeId, communicationPath);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesResponsebyCommunicationPath", e);
        }
        return campaignMessagesResponse;
    }

    public OptInOut getOptInOut(long crSeqNo) {
        OptInOut optInOut = null;
        try {
            optInOut = refillReminderDAO.getOptInOut(crSeqNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getOptInOut", e);
        }
        return optInOut;
    }

    public boolean redemptionExistsInIRF(DailyRedemption drf, int daysBack, int campaignId) {
        boolean redemptionExists = false;
        try {
            redemptionExists = refillReminderDAO.redemptionExistsInIRF(drf, daysBack, campaignId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> redemptionExistsInIRF", e);
        }
        return redemptionExists;
    }

    public List<RefillReminderPOJO> getTextRepeatRefillRecords(int campId) {
        List<RefillReminderPOJO> list = new ArrayList<>();
        try {
            list = refillReminderDAO.getTextRepeatRefillRecords(campId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getTextRepeatRefillRecords", e);
        }
        return list;
    }

    public List<RefillReminderPOJO> getTextRefill2Records(int campId) {
        List<RefillReminderPOJO> list = new ArrayList<>();
        try {
            list = refillReminderDAO.getTextRefill2Records(campId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getTextRefill2Records", e);
        }
        return list;
    }

    public List<RefillReminderPOJO> getEmailRepeatRefillRecords(int campId) {
        List<RefillReminderPOJO> list = new ArrayList<>();
        try {
            list = refillReminderDAO.getEmailRepeatRefillRecords(campId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEmailRepeatRefillRecords", e);
        }
        return list;
    }

    public DailyRedemption getDailyRedemptionDetailByRedemptionId(int redemptionId) {
        DailyRedemption drf = null;
        try {
            drf = refillReminderDAO.getDailyRedemptionDetailByRedemptionId(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getDailyRedemptionDetailByRedemptionId", e);
        }
        return drf;
    }

    public InstantRedemption getInstantRedemptionDetailByRedemptionId(int redemptionId) {
        InstantRedemption irf = null;
        try {
            irf = refillReminderDAO.getInstantRedemptionDetailByRedemptionId(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getInstantRedemptionDetailByRedemptionId", e);
        }
        return irf;
    }

    public Campaigns getCampaignsById(Integer campaignsId) {
        Campaigns campaigns = null;
        try {
            campaigns = refillReminderDAO.getCampaignsById(campaignsId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignsById", e);
        }
        return campaigns;
    }

    public boolean isPatientEligibleForReminder(int messageRequestId, long secondsDelay) {
        boolean isPatientEligibleForReminder = false;
        try {
            isPatientEligibleForReminder = refillReminderDAO.isPatientEligibleForReminder(messageRequestId, secondsDelay);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> isPatientEligibleForReminder", e);
        }
        return isPatientEligibleForReminder;
    }

    public GatewayInfo getGatewayInfo(int shortCode) {
        GatewayInfo gatewayInfo = null;
        try {
            gatewayInfo = refillReminderDAO.getGatewayInfo(shortCode);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getGatewayInfo", e);
        }
        return gatewayInfo;
    }

    public CustomerRequest getCustomerRequestIP(String phoneNumber) {
        CustomerRequest customerRequest = null;
        try {
            customerRequest = refillReminderDAO.getCustomerRequestIP(phoneNumber);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCustomerRequestIP", e);
        }
        return customerRequest;
    }

    public ValidResponse getValidResponse(String validWord) {
        ValidResponse validResponse = null;
        try {
            validResponse = refillReminderDAO.getValidResponse(validWord);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getValidResponse", e);
        }
        return validResponse;
    }

    public AggregatorMessageRequest getAggregatorMessageRequestById(int messageReqNo) {
        AggregatorMessageRequest aggregatorMessageRequest = null;
        try {
            aggregatorMessageRequest = refillReminderDAO.getAggregatorMessageRequestById(messageReqNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getAggregatorMessageRequestById", e);
        }
        return aggregatorMessageRequest;
    }

    public AggregatorMessageRequest getRefillReminderAggregatorMessageRequestByRedemptionId(int redemptionId) {
        AggregatorMessageRequest aggregatorMessageRequest = null;
        try {
            aggregatorMessageRequest = refillReminderDAO.getRefillReminderAggregatorMessageRequestByRedemptionId(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getRefillReminderAggregatorMessageRequestByRedemptionId", e);
        }
        return aggregatorMessageRequest;
    }

    public RxMMSRedemptionReqRes getRxMMSRedemptionReqResById(int messageReqNo) {
        RxMMSRedemptionReqRes rxMMSRedemptionReqRes = null;
        try {
            rxMMSRedemptionReqRes = refillReminderDAO.getRxMMSRedemptionReqResById(messageReqNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getRxMMSRedemptionReqResById", e);
        }
        return rxMMSRedemptionReqRes;
    }

    public void populateNpiValues(InstantRedemption irf) {
        try {
            refillReminderDAO.populateNpiValues(irf);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> populateNpiValues", e);
        }
    }

    public Drug getProductByNdc(String ndcNo) {
        Drug drug = null;
        try {
            drug = refillReminderDAO.getProductByNdc(ndcNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getProductByNdc", e);
        }
        return drug;
    }

    public int retrieveMessageCountByTypeForRedemption(AggregatorMessageRequest aggregatorMessageRequest) {
        int count = 0;
        try {
            count = refillReminderDAO.retrieveMessageCountByTypeForRedemption(aggregatorMessageRequest);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> retrieveMessageCountByTypeForRedemption", e);
        }
        return count;
    }

    public String getURL(String urlCode) {
        String urlString = "";
        try {
            urlString = refillReminderDAO.getURL(urlCode);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getURL", e);
        }
        return urlString;
    }

    public CampaignMessagesResponse getCampaignMessagesResponseByResponseName(int campaignId, int folderId, String responseTitle) {
        CampaignMessagesResponse campaignMessagesResponse = null;
        try {
            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponseByResponseName(campaignId, folderId, responseTitle);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesResponseByResponseName", e);
        }
        return campaignMessagesResponse;
    }

    public CampaignTrigger getTriggerByCampaignId(int campaignId) {
        CampaignTrigger trigger = null;
        try {
            trigger = refillReminderDAO.getTriggerByCampaignId(campaignId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getTriggerByCampaignId", e);
        }
        return trigger;
    }

    public Intervals getIntervalById(int intervalId) {
        Intervals interval = null;
        try {
            interval = refillReminderDAO.getIntervalById(intervalId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getIntervalById", e);
        }
        return interval;
    }

    public int markAggregatorMessageRequestEndDate(int messageReqNo) {
        int count = 0;
        try {
            count = refillReminderDAO.markAggregatorMessageRequestEndDate(messageReqNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> markAggregatorMessageRequestEndDate", e);
        }
        return count;
    }

    public int markRARYESReceivedEndDate(int redemptionId) {
        int count = 0;
        try {
            count = refillReminderDAO.markRARYESReceivedEndDate(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> markRARYESReceivedEndDate", e);
        }
        return count;
    }

    public EmailOptInOut getEmailOptInOut(String email, int campaignId) {
        EmailOptInOut optInOut = null;
        try {
            optInOut = refillReminderDAO.getEmailOptInOut(email, campaignId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEmailOptInOut", e);
        }
        return optInOut;
    }

    public int sentEmailCountRefill(String email, int campaignId, int folderId, int messageTypeid) {
        int count = 0;
        try {
            count = refillReminderDAO.sentEmailCountRefill(email, campaignId, folderId, messageTypeid);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> sentEmailCountRefill", e);
        }
        return count;
    }

    public int sentEmailCountPAPending(String email, int campaignId, int folderId, int messageTypeid) {
        int count = 0;
        try {
            count = refillReminderDAO.sentEmailCountPAPending(email, campaignId, folderId, messageTypeid);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> sentEmailCountPAPending", e);
        }
        return count;
    }

    public EmailRequest getEmailRequestById(int emailReqNo) {
        EmailRequest emailRequest = null;
        try {
            emailRequest = refillReminderDAO.getEmailRequestById(emailReqNo);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEmailRequestById", e);
        }
        return emailRequest;
    }

    public CampaignMessagesResponse getCampaignMessagesResponseByResComm(int campaignId, int folderId, String responseTitle, String communicationPath) {
        CampaignMessagesResponse campaignMessagesResponse = null;
        try {
            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponseByResComm(campaignId, folderId, responseTitle, communicationPath);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesResponseByResComm", e);
        }
        return campaignMessagesResponse;
    }

    public List<CampaignMessages> getCampaignMessagesByCommunicationPath(int campaignId, int folderId, int messageTypeId, String communicationPath) {
        List<CampaignMessages> list = null;
        try {
            if (communicationPath.equalsIgnoreCase("T")) {
                communicationPath = Constants.SMS;
            }
            list = refillReminderDAO.getCampaignMessagesByCommunicationPath(campaignId, folderId, messageTypeId, communicationPath);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getCampaignMessagesByCommunicationPath", e);
        }
        return list;
    }

    public String getEventsDescription(int campaignId, int folderId) {
        String events = "";
        try {
            events = refillReminderDAO.getEventsDescription(campaignId, folderId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEventsDescription", e);
        }
        return events;
    }

    public int sentMMSCountCampaign(String phoneNumber, int campaignId, int folderId, int messageTypeid) {
        int count = 0;
        try {
            count = refillReminderDAO.sentMMSCountCampaign(phoneNumber, campaignId, folderId, messageTypeid);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> sentMMSCountCampaign", e);
        }
        return count;
    }

    public IvroptInOut getIVROptInOut(int campaignId, String phoneNumber) {
        IvroptInOut optInOut = null;
        try {
            optInOut = refillReminderDAO.getIVROptInOut(campaignId, phoneNumber);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getIVROptInOut", e);
        }
        return optInOut;
    }

    public int retrieveIVRMessageCountForRedemption(IvrrequestResponse request) {
        int count = 0;
        try {
            count = refillReminderDAO.retrieveIVRMessageCountForRedemption(request);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> retrieveIVRMessageCountForRedemption", e);
        }
        return count;
    }

    public IvrcallTiming getOutBoundCallTiming() {
        IvrcallTiming timing = null;
        try {
            timing = refillReminderDAO.getOutBoundCallTiming();
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getOutBoundCallTiming", e);
        }
        return timing;
    }

    public Object findById(Class clazz, Integer id) {
        Object o = null;
        try {
            o = refillReminderDAO.findById(clazz, id);
        } catch (Exception ex) {
            logger.error("Exception: RefillReminderService -> findById", ex);
        }
        return o;
    }

    public List<EventDetail> getEventDetail(String eventsIds, String dataSet) {
        List<EventDetail> list = null;
        try {
            list = refillReminderDAO.getEventDetail(eventsIds, dataSet);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getEventDetail", e);
        }
        return list;
    }

    public Order getOrderDetailByTransactionNumber(String cardHolderFirstName, String cardHolderLastname, Integer campaignId, Date cardHolderDOB) {
        Order order = null;
        try {
            logger.info("CardHolderFirstName : " + cardHolderFirstName + " CardHolderLastname: " + cardHolderLastname + " CampaignId: " + campaignId + " CardHolderDOB: " + cardHolderDOB);
            String transactionNumber = refillReminderDAO.getDailyRedemptions(cardHolderFirstName, cardHolderLastname, campaignId, cardHolderDOB);
            logger.info("Travsction number is :" + transactionNumber);
            List<Order> list = refillReminderDAO.getOrderByTransactionNumber(transactionNumber);
            if (!list.isEmpty()) {
                order = list.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getOrderDetailByTransactionNumber", e);
        }
        return order;
    }

    public boolean isEventDetailVerifiedForDailyRedemption(List<EventDetail> details, DailyRedemptionId drfId) {
        boolean flag = false;
        flag = refillReminderDAO.isEventDetailVerifiedForDailyRedemption(details, drfId);
        return flag;
    }

    public boolean isMmsExists(int campaignId, int folderId) {
        boolean flag = false;
        try {
            flag = refillReminderDAO.isMmsExists(campaignId, folderId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> isMmsExists", e);
        }
        return flag;
    }

    public MessagePriority getMessagePriority(String phoneNumber, int shortCode) {
        MessagePriority messagePriority = null;
        try {
            messagePriority = refillReminderDAO.getMessagePriority(phoneNumber, shortCode);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getMessagePriority", e);
        }

        return messagePriority;
    }

    public List<InstantRedemption> getDRFInLast30Mints(int campaignId) {
        List<InstantRedemption> list = null;
        try {
            list = refillReminderDAO.getDRFInLast30Mints(campaignId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getDRFInLast30Mints", e);
        }

        return list;
    }

    public int getPreviousRedemptionId(InstantRedemption instantRedemption) {
        int redemptionId = 0;
        try {
            redemptionId = refillReminderDAO.getPreviousRedemptionId(instantRedemption);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getPreviousRedemptionId", e);
        }

        return redemptionId;
    }

    public AggregatorMessageRequest getLastestAggregatorMessageRequest(int redemptionId) {
        AggregatorMessageRequest aggregatorMessageRequest = null;
        try {
            aggregatorMessageRequest = refillReminderDAO.getLastestAggregatorMessageRequest(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getLastestAggregatorMessageRequest", e);
        }

        return aggregatorMessageRequest;
    }

    public AggregatorMessageRequest getRefillSuccessfullMessage(int redemptionId) {
        AggregatorMessageRequest aggregatorMessageRequest = null;
        try {
            aggregatorMessageRequest = refillReminderDAO.getRefillSuccessfullMessage(redemptionId);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getRefillSuccessfullMessage", e);
        }

        return aggregatorMessageRequest;
    }

    public String getDrugName(String ndc) {
        String drugName = null;
        try {
            drugName = refillReminderDAO.getDrugName(ndc);
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getDrugName", e);
        }

        return drugName;
    }

    public Order getOrderByTransactionNumber(String transactionNumber) {
        Order order = null;
        try {
            List<Order> list = refillReminderDAO.getOrderByTransactionNumber(transactionNumber);
            if (!list.isEmpty()) {
                order = list.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception: RefillReminderService -> getOrderByTransactionNumber", e);
        }
        return order;
    }

    public boolean sendNextRefillReminder(Date date, Logger log, Logger successed, Logger failed) {
        boolean nextRefillflag = false;

        try {
            List<String> orderStatusList = new ArrayList<>();
            orderStatusList.add(Constants.ORDER_STATUS.DELIVERY);
            orderStatusList.add(Constants.ORDER_STATUS.PICKUP_AT_PHARMACY);
            orderStatusList.add(Constants.ORDER_STATUS.SHIPPED);
            List<Order> orders = patientProfileDAO.getRefillReminderOrdersList(date, orderStatusList);
            if (CommonUtil.isNullOrEmpty(orders)) {
                log.info("There are no orders availabe which need next refill reminder (System will return).");
                return nextRefillflag;
            }

            CampaignMessages campign = this.patientProfileService.getNotificationMsgs(Constants.MSG_CONTEXT_REFILL, Constants.RX_ORDER);
            if (campign == null || CommonUtil.isNullOrEmpty(campign.getMessageId())) {
                log.info("CampaignMessages is null (System will return).");
                return nextRefillflag;
            }

            for (Order order : orders) {
                boolean successReminder = true;
                try {
                    String pharmacyComments = "", genericName = "";
                    pharmacyComments = this.patientProfileService.loadLastMessageFromPharmacyQueue(order.getId());
                    pharmacyComments = AppUtil.truncateStringToSpecificLength(pharmacyComments, 100);
                    if (order.getDrugDetail() != null && order.getDrugDetail().getDrugDetailId() != null) {
                        genericName = order.getDrugDetail().getDrugBasic().getDrugGeneric().getGenericName();
                    }
                    campign.setSmstext(AppUtil.getSafeStr(campign.getSmstext(), ""));
                    if (CommonUtil.isNotEmpty(campign.getSmstext())) {
                        RewardPoints rewardPoints = (RewardPoints) patientProfileDAO.findByPropertyUnique(new RewardPoints(), "type", "Rx Refill", Constants.HIBERNATE_EQ_OPERATOR);
                        CampaignMessages campaignMessages = replaceRefillMsgPlaceHolders(campign, order, genericName, rewardPoints, pharmacyComments);
                        String subject = patientProfileService.getMessageSubjectWithprocessedPlaceHolders(campaignMessages.getSubject(), order.getId());
                        campaignMessages.setSubject(subject);
                        String pushNot = AppUtil.getSafeStr(campaignMessages.getPushNotification(), "");
                        campaignMessages.setPushNotification(patientProfileService.getMessageSubjectWithprocessedPlaceHolders(pushNot, order.getId()));
                        patientProfileService.saveNotificationMessages(campaignMessages, Constants.NO, order.getPatientProfile().getId(), order.getId(),
                                                                        campaignMessages.getSmstext(),campaignMessages.getSubject());
                        order.setNextRefillFlag("1");
                        patientProfileDAO.saveOrUpdate(order);
                        nextRefillflag = true;
                    } else {
                        log.info("Msg test is empty.");
                        nextRefillflag = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    failed.error("order Id =" + order.getId() + " refill reminder is failed due to " + e);
                    log.error("Exception: Next refill reminder -> ", e);
                    successReminder = false;
                }
                if (successReminder) {
                    successed.info("Successfully send next refill reminder to Order Id=" + order.getId());
                }
            }
            return nextRefillflag;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception: Next refill reminder -> ", e);
            return nextRefillflag;
        }
    }

    public CampaignMessages replaceRefillMsgPlaceHolders(CampaignMessages campign, Order order, String genericName, RewardPoints rewardPoints, String pharmacyComments) throws Exception {
        CampaignMessages campaignMessages = populateCampaignMessages(campign);
        campaignMessages.setSmstext(campign.getSmstext()
                .replace("[DRUG_NAME]", AppUtil.getSafeStr(EncryptionHandlerUtil.getDecryptedString(order.getDrugName()),""))
                .replace("[DRUG_STRENGTH]", order.getStrength())
                .replace("[DRUG_TYPE]", order.getDrugType())
                .replace("[DRUG_QTY]", order.getQty())
                .replace("[generic_name]", genericName)
                .replace("[date_time]", DateUtil.dateToString(new Date(), "MM/dd/YYYY hh:mm a"))
                .replace("[request_no]", order.getId())
                .replace("[REFILL_POINTS]", rewardPoints != null && rewardPoints.getId() != null ? rewardPoints.getPoint().intValueExact() + "" : "0")
                .replace("[PHARMACY_COMMENTS]", CommonUtil.isNotEmpty(pharmacyComments) ? pharmacyComments : "")
                .replace("[RX_NUMBER]", AppUtil.getSafeStr(order.getRxNumber(), ""))
                .replace("[DAYS_SUPPLY]", order.getDaysSupply() != null ? order.getDaysSupply().toString() : "0")
                .replace("[REFILLS_REMAIN]", order.getRefillsRemaining() != null ? order.getRefillsRemaining().toString() : "0"));
        if (order.getOrderChain() != null && order.getOrderChain().getId() != null) {
            if (order.getOrderChain().getRxExpiredDate() != null) {
                campaignMessages.setSmstext(campaignMessages.getSmstext().replace("Rx_Expiration", DateUtil.dateToString(order.getOrderChain().getRxExpiredDate(), Constants.DD_MM_YYYY)));
            }
        }
        return campaignMessages;
    }

    public boolean sendEndOfTheYearJob() {
        boolean nextRefillflag = false;
        try {

            List<PatientProfile> patientProfilesList = patientProfileDAO.findByNestedProperty(new PatientProfile(), "Status", Constants.COMPLETED, Constants.HIBERNATE_EQ_OPERATOR, null, 0);
            if (CommonUtil.isNullOrEmpty(patientProfilesList)) {
                logger.info("There are no orders availabe which need next refill reminder.");
                return nextRefillflag;
            }
            CampaignMessages campign = this.patientProfileService.getNotificationMsgs(Constants.END_OF_THE_YEAR, Constants.Pharmacy_Notification);
            for (PatientProfile patientProfile : patientProfilesList) {
                List<PatientInsuranceDetails> patientInsuranceDetailsList = patientProfileDAO.findByNestedProperty(new PatientInsuranceDetails(), "patientProfile.id", patientProfile.getId(), Constants.HIBERNATE_EQ_OPERATOR, null, 0);

                if (!CommonUtil.isNullOrEmpty(patientInsuranceDetailsList)) {

                    if (campign != null && campign.getMessageId() != null) {
                        if (CommonUtil.isNotEmpty(campign.getSmstext())) {
                            patientProfileService.saveNotificationMessages(campign, Constants.NO, patientProfile.getId());
                            nextRefillflag = true;
                        } else {
                            logger.info("Msg test is empty.");
                            nextRefillflag = false;
                        }
                        return nextRefillflag;
                    }
                } else {
                    logger.info("Patient Insurance detail is null.");
                    nextRefillflag = false;
                }
            }
            return nextRefillflag;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception: Next refill reminder -> ", e);
            return nextRefillflag;
        }
    }

    public boolean sendAnnualStatements() {
        boolean nextAnnualflag = false;
        try {
            String currentYear = DateUtil.dateToString(new Date(), "yyyy");
            logger.info("Current Year is:: " + currentYear);
            List<String> orderStatusList = new ArrayList<>();
            orderStatusList.add(Constants.ORDER_STATUS.DELIVERY);
            orderStatusList.add(Constants.ORDER_STATUS.SHIPPED);
            List<Order> ordersList = patientProfileDAO.getOrdersForAnnualStatement(Integer.parseInt(currentYear), orderStatusList);
            if (CommonUtil.isNullOrEmpty(ordersList)) {
                logger.info("Order list is empty.");
                return nextAnnualflag;
            }
            CampaignMessages campignMessages = this.patientProfileService.getNotificationMsgs(Constants.ANNUAL_STATEMENT, Constants.RX_ORDER);

            for (Order order : ordersList) {
                if (order.getPatientProfile() == null || order.getPatientProfile().getId() == null) {
                    logger.info("Patient Profile info is null.");
                    continue;
                }
                Long totalOrder = patientProfileDAO.getTotalOrders(orderStatusList, order.getPatientProfile().getId());
                logger.info("Total Orders:: " + totalOrder);
                if (campignMessages != null && campignMessages.getMessageId() != null) {
                    List<BusinessObject> bosList = new ArrayList<>();

                    BusinessObject bo = new BusinessObject("patientProfile.id", order.getPatientProfile().getId(), Constants.HIBERNATE_EQ_OPERATOR);
                    bosList.add(bo);
                    bo = new BusinessObject("messageType.id.messageTypeId", campignMessages.getMessageType().getId().getMessageTypeId(), Constants.HIBERNATE_EQ_OPERATOR);
                    bosList.add(bo);
                    bo = new BusinessObject("subject", campignMessages.getSubject(), Constants.HIBERNATE_EQ_OPERATOR);
                    bosList.add(bo);

                    List<NotificationMessages> isNotificationMessagesesExist = patientProfileDAO.findByNestedProperty(new NotificationMessages(), bosList, null, 0);
                    if (!CommonUtil.isNullOrEmpty(isNotificationMessagesesExist)) {
                        logger.info(Constants.ANNUAL_STATEMENT + " already send against this order " + order.getId());
                        continue;
                    }

                    String message = campignMessages.getSmstext();
                    if (CommonUtil.isNotEmpty(message)) {
                        CampaignMessages campaignMessages = populateCampaignMessages(campignMessages);
                        campaignMessages.setSmstext(message.replace("[date_time]", DateUtil.dateToString(new Date(), "MM/dd/YYYY hh:mm a"))
                                .replace("[YEAR]", currentYear)
                                .replace("[TOTAL_ORDER]", (totalOrder != null ? totalOrder.toString() : ""))
                                .replace("[patient_Id]", order.getPatientProfile().getId().toString()));
                        patientProfileService.saveNotificationMessages(campaignMessages, order.getPatientProfile().getId(), order.getId());
                        nextAnnualflag = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception:: sendAnnualStatements ", e);
        }
        return nextAnnualflag;
    }

    public CampaignMessages populateCampaignMessages(CampaignMessages campignMessages) {
        CampaignMessages campaignMessages = new CampaignMessages();
        campaignMessages.setMessageId(campignMessages.getMessageId());
        campaignMessages.setMessageType(campignMessages.getMessageType());
        campaignMessages.setSubject(campignMessages.getSubject());
        campaignMessages.setPushNotification(campignMessages.getPushNotification());
        return campaignMessages;
    }
}
//..................................
//public boolean sendNextRefillReminder(Date date, Logger log) {
//        boolean nextRefillflag = false;
//        try {
//            List<String> orderStatusList=new ArrayList<>();
//            orderStatusList.add(Constants.ORDER_STATUS.DELIVERY);
//            orderStatusList.add(Constants.ORDER_STATUS.SHIPPED);
//            List<Order> orders = patientProfileDAO.getRefillReminderOrdersList(date,orderStatusList);
//            if (CommonUtil.isNullOrEmpty(orders)) {
//                log.info("There are no orders availabe which need next refill reminder.");
//                return nextRefillflag;
//            }
//            CampaignMessages campign = this.patientProfileService.getNotificationMsgs(Constants.MSG_CONTEXT_REFILL, Constants.RX_ORDER);
//            for (Order order : orders) {
//                if (campign != null && campign.getMessageId() != null) {
//                    String pharmacyComments = "", genericName = "";
//                    pharmacyComments = this.patientProfileService.loadLastMessageFromPharmacyQueue(order.getId());
//                    pharmacyComments = AppUtil.truncateStringToSpecificLength(pharmacyComments, 100);
//                    if (order.getDrugDetail() != null && order.getDrugDetail().getDrugDetailId() != null) {
//                        genericName = order.getDrugDetail().getDrugBasic().getDrugGeneric().getGenericName();
//                    }
//                    campign.setSmstext(AppUtil.getSafeStr(campign.getSmstext(), ""));
//                    if (CommonUtil.isNotEmpty(campign.getSmstext())) {
//                        RewardPoints rewardPoints = (RewardPoints) patientProfileDAO.findByPropertyUnique(new RewardPoints(), "type", "Rx Refill", Constants.HIBERNATE_EQ_OPERATOR);
//                        CampaignMessages campaignMessages = replaceRefillMsgPlaceHolders(campign, order, genericName, rewardPoints, pharmacyComments);
//                        patientProfileService.saveNotificationMessages(campaignMessages, order.getPatientProfile().getId(), order.getId());
//                        order.setNextRefillFlag("1");
//                        patientProfileDAO.saveOrUpdate(order);
//                        nextRefillflag = true;
//                    } else {
//                        log.info("Msg test is empty.");
//                        nextRefillflag = false;
//                    }
//                } else {
//                    log.info("CampaignMessages is null.");
//                    nextRefillflag = false;
//                }
//            }
//            return nextRefillflag;
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Exception: Next refill reminder -> ", e);
//            return nextRefillflag;
//        }
//    }
