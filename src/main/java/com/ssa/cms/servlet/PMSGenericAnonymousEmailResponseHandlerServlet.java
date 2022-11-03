package com.ssa.cms.servlet;

import com.ssa.cms.common.Constants;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.Campaigns;
import com.ssa.cms.model.EmailOptInOut;
import com.ssa.cms.model.EmailRequest;
import com.ssa.cms.model.InstantRedemption;
import com.ssa.cms.model.Intervals;
import com.ssa.cms.model.IntervalsType;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.Raryesreceived;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.model.ValidResponse;
import com.ssa.cms.service.DoDirectPayment;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.service.RefillReminderService;
import com.ssa.cms.thread.EmailRefillSendingThread;
import com.ssa.cms.util.EmailFlowUtil;
import com.ssa.cms.util.EmailSender;
import com.ssa.cms.util.RedemptionUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urn.ebay.api.PayPalAPI.DoDirectPaymentResponseType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;

@Controller
@RequestMapping
public class PMSGenericAnonymousEmailResponseHandlerServlet {

    private static final Logger log = Logger.getLogger(PMSGenericAnonymousEmailResponseHandlerServlet.class);
    @Autowired
    RefillReminderService refillReminderDAO;
    @Autowired
    RedemptionService redemptionDAO;

    @RequestMapping(value = "/PMSGenericAnonymousEmailResponseHandler", method = RequestMethod.GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @RequestMapping(value = "/PMSGenericAnonymousEmailResponseHandler", method = RequestMethod.POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Object emailReqNo = request.getAttribute("emailReqNo");
        String message = request.getParameter("message");

        PrintWriter out = response.getWriter();

        log.info("email request number :" + emailReqNo);
        log.info("Message :" + message);

        try {
            int emailReqNum = 0;

            if (emailReqNo != null) {
                log.info("Email Request Number : " + emailReqNo.toString());
                emailReqNum = Integer.valueOf(emailReqNo.toString());
                log.info("Email Request Integer Value : " + emailReqNo);

                if (emailReqNum == 0) {
                    log.info("Email Req No number empty");
                    request.setAttribute("message", "Email Req No number empty.");
                    request.getRequestDispatcher("/displayMessage").forward(request, response);
                    return;
                }
            } else {
                log.info("Email Req No number empty");
                request.setAttribute("message", "Email Req No number empty.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            message = message.trim();

            EmailRequest emailRequest = refillReminderDAO.getEmailRequestById(emailReqNum);

            if (emailRequest == null) {
                log.info("Email Req No entry not found in EmailRequest");
                request.setAttribute("message", "Email Req No entry not found in EmailRequest.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            int campaignId = emailRequest.getCampaignId();
            String messageContext = emailRequest.getMessageContext();
            String email = emailRequest.getEmail();
            int folderId = emailRequest.getFolderId();

            log.info("Campaign Id : " + campaignId);
            log.info("Message Context : " + messageContext);

            //Processing of Y,YES,YEP e.t.c
            ValidResponse validResponse = refillReminderDAO.getValidResponse(message);

            if (validResponse == null) {
                log.info("No valid response found. (System will return)");
                request.setAttribute("message", "No valid response found.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            InstantRedemption irf = refillReminderDAO.getInstantRedemptionDetailByRedemptionId(emailRequest.getRedemptionId());
            if (irf == null) {
                log.info("IRF record not found (System will continue)");
                request.setAttribute("message", "IRF record not found.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            if (messageContext == null || messageContext.trim().length() == 0) {
                log.info("Message Context is null. (Return)");
                request.setAttribute("message", "Message Context is null.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            messageContext = messageContext.trim();

            String communicatoinMethod = irf.getCommunicationMethod();

            if (communicatoinMethod == null || communicatoinMethod.trim().length() == 0) {
                log.info("No communicaiton method defined (System will continue)");
                request.setAttribute("message", "No communicaiton method defined.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            communicatoinMethod = communicatoinMethod.trim();

            if (!(communicatoinMethod.equalsIgnoreCase("T") || communicatoinMethod.equalsIgnoreCase("E"))) {
                log.info("Communication method has not value of E or T (System will continue)");
                request.setAttribute("message", "Communication method has not value of E or T.");
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }

            Campaigns campaigns = refillReminderDAO.getCampaignsById(campaignId);

            if (campaigns == null) {
                log.info("Campaign detail not found against CampaignId: " + campaignId);
                log.info("(System will continue)");

                request.setAttribute("message", "Campaign detail not found against CampaignId: " + campaignId);
                request.getRequestDispatcher("/displayMessage").forward(request, response);
                return;
            }
            String campaignName = campaigns.getCampaignName();
            String communicationPath = Constants.EMAIL;
            if (messageContext.equalsIgnoreCase(Constants.REDEMPTION) || messageContext.equalsIgnoreCase(Constants.MSG_CONTEXT_REFILL) || messageContext.equalsIgnoreCase(Constants.MSG_CONTEXT_REPEAT_REFILL)) {

                String refillFlag = campaigns.getIsRefill();
                log.info("Refill Flag at comapgn level : " + refillFlag);

                if (!messageContext.equalsIgnoreCase(Constants.REDEMPTION)) {
                    if (refillFlag == null || refillFlag.trim().length() == 0) {
                        log.info("No refill configured at campaign level. (System will return)");
                        request.setAttribute("message", "No refill configured at campaign level.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }

                    refillFlag = refillFlag.trim();

                    if (!refillFlag.equalsIgnoreCase("YES")) {
                        log.info("Refill flag value not equal to YES. (System will return)");
                        request.setAttribute("message", "Refill flag value not equal to YES.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }
                }

                EmailOptInOut emailOptInOut = refillReminderDAO.getEmailOptInOut(email, campaignId);
                String optInOutFlag = null;
                if (emailOptInOut != null) {
                    optInOutFlag = emailOptInOut.getOptInOut();
                    log.info("Email OptInOut value: " + optInOutFlag);
                }
                if (optInOutFlag != null && optInOutFlag.trim().equalsIgnoreCase("O")) {
                    log.info(email + " has opted out from Campaign : " + campaignId);
                    log.info("System will continue");
                    request.setAttribute("message", email + " has been opted out from this program.");
                    request.getRequestDispatcher("/displayMessage").forward(request, response);
                    return;
                }
                if (messageContext.equalsIgnoreCase(Constants.REDEMPTION) && communicatoinMethod.equalsIgnoreCase("E")) {
                    CampaignMessagesResponse campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(campaignId, emailRequest.getFolderId(), emailRequest.getMessageTypeId(), Constants.EMAIL);

                    if (campaignMessageResponses == null) {
                        log.info("CampaignMessagesResponse is null in thread");
                        request.setAttribute("message", "CampaignMessagesResponse is null in thread.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }

                    Integer nextMessageTypeId = campaignMessageResponses.getNextMessage();

                    log.info("Next Message Type Id : " + nextMessageTypeId);

                    Raryesreceived raryesreceived = new Raryesreceived();
                    raryesreceived.setCommunicationId(emailRequest.getEmail());
                    raryesreceived.setCommunicationMethod(communicatoinMethod);
                    raryesreceived.setFolderId(emailRequest.getFolderId());
                    raryesreceived.setCampaignId(emailRequest.getCampaignId());
                    raryesreceived.setShortCode(campaigns.getShortCodes().getShortCode());
                    raryesreceived.setMessageTypeId(emailRequest.getMessageTypeId());
                    raryesreceived.setRedemptionId(emailRequest.getRedemptionId());
                    raryesreceived.setMessageResponse(message);
                    raryesreceived.setCommunicationPath(Constants.EMAIL);
                    raryesreceived.setEffectiveDate(new Date());
                    raryesreceived.setMessageTypeId(nextMessageTypeId);
                    refillReminderDAO.save(raryesreceived);

                    if (nextMessageTypeId == null || nextMessageTypeId == 0) {
                        log.info("Next message type id is 0");
                        request.setAttribute("message", "Message already sent. No pending message for this email.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }

                    campaignMessageResponses = refillReminderDAO.getCampaignMessagesResponsebyCommunicationPath(emailRequest.getCampaignId(), emailRequest.getFolderId(), nextMessageTypeId, Constants.EMAIL);

                    if (campaignMessageResponses == null) {
                        log.info("CampaignMessagesResponse is null in thread for next message");
                        request.setAttribute("message", "CampaignMessagesResponse is null in thread for next message.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }

                    sendEmail(campaignMessageResponses, campaignId, emailRequest, nextMessageTypeId, messageContext, email, irf, null, campaigns, campaignName, request, response, Boolean.FALSE);

                } else if (communicatoinMethod.equalsIgnoreCase("E")) {
                    Integer messageTypeId = 0;
                    Order order = refillReminderDAO.getOrderDetailByTransactionNumber(irf.getCardholderFirstName(), irf.getCardholderLastName(), irf.getCampaignId(), irf.getCardholderDob());
                    if (order == null) {
                        log.info("Order is null in thread");
                        request.setAttribute("message", "Order is null in thread.");
                        request.getRequestDispatcher("/displayMessage").forward(request, response);
                        return;
                    }
                    Order ord = new Order();
                    BeanUtils.copyProperties(order, ord, new String[]{"id"});

                    DoDirectPayment dopayment = new DoDirectPayment();
                    DoDirectPaymentResponseType res = dopayment.doDirectPayment(ord.getCardType(), ord.getCardNumber(), ord.getCardExpiry(),
                            ord.getCardHolderName(), ord.getPayment().toString(), ord.getCardCvv());
                    boolean doDirectRes = false;
                    CampaignMessagesResponse campaignMessagesResponse;
                    if (res.getAck().getValue().equalsIgnoreCase("success")) {
                            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.MSG_CONTEXT_REFILL_SUCCESS, communicationPath);
                            int sentEmailCount = refillReminderDAO.sentEmailCountRefill(email, campaignId, emailRequest.getFolderId(), campaignMessagesResponse.getMessageTypeId());
                            if (sentEmailCount > 0) {
                                log.info("Email already sent to Email : " + email);
                                request.setAttribute("message", "Order has already been placed against this email.");
                                request.getRequestDispatcher("/displayMessage").forward(request, response);
                                return;
                            }
                            doDirectRes = redemptionDAO.saveAutoOrderPlace(irf, ord);
                        } else {
                        List<ErrorType> errorList = res.getErrors();
                            //denied message
                            campaignMessagesResponse = refillReminderDAO.getCampaignMessagesResponseByResComm(campaignId, folderId, Constants.MSG_CONTEXT_REFILL_DENIED, communicationPath);
                        //ord = null;
                        }
                    messageTypeId = campaignMessagesResponse.getMessageTypeId();
                    sendEmail(campaignMessagesResponse, campaignId, emailRequest, messageTypeId, messageContext, email, irf, ord, campaigns, campaignName, request, response, doDirectRes);
                }
            }

        } catch (NumberFormatException | BeansException e) {
            log.error("Exception: ", e);
        }
    }

    private boolean sendEmail(CampaignMessagesResponse campaignMessageResponses, int campaignId, EmailRequest emailRequest, int nextMessageTypeId, String messageContext, String email, InstantRedemption irf, Order order, Campaigns campaigns, String campaignName, HttpServletRequest request, HttpServletResponse response, Boolean doDirectRes) throws ServletException, IOException {
        if (campaignMessageResponses == null) {
            log.info("CampaignMessagesResponse is null in thread");
            request.setAttribute("message", "CampaignMessagesResponse is null in thread.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }
        Intervals interval = campaignMessageResponses.getIntervals();
        IntervalsType intervalsType = campaignMessageResponses.getIntervals().getIntervalsType();

        int intervalId = interval.getIntervalId();
        String intervalDesc = interval.getIntervalValue() + " " + intervalsType.getIntervalsTypeTitle();
        String eventDesc = redemptionDAO.getEventsDescription(campaignId, emailRequest.getFolderId(), Constants.EMAIL);

        log.info("Event Description: " + eventDesc);

        int intervalVal = interval.getIntervalValue();
        int intervalUnitInSecond = intervalsType.getUnitInSecond();
        long secondsDelay = intervalVal * intervalUnitInSecond;

        List<CampaignMessages> campaignMessagesList = refillReminderDAO.getCampaignMessagesByCommunicationPath(emailRequest.getCampaignId(), emailRequest.getFolderId(), nextMessageTypeId, Constants.EMAIL);

        if (campaignMessagesList == null || campaignMessagesList.isEmpty()) {
            log.info("No messages found for (System will return)");
            request.setAttribute("message", "No messages found.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }

        CampaignMessages campaignMessages = campaignMessagesList.get(0);

        if (campaignMessages == null) {
            log.info("CampaignMessages object not found. (System will return)");
            request.setAttribute("message", "CampaignMessages object not found.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }

        int messageTypeId = campaignMessages.getMessageType().getId().getMessageTypeId();

        String emailFrom = null;
        String emailSubject = null;
        String emailbody = null;

        SmtpServerInfo smtpServerInfo = campaigns.getSmtpServerInfo();

        log.info("Email From : " + emailFrom);
        log.info("Email Body : " + emailbody);
        log.info("Email Subject : " + emailSubject);

        if (emailbody == null || emailbody.equalsIgnoreCase("")) {
            log.info("No Email body found System will continue");
            log.info("Message Type ID: " + messageTypeId);
            request.setAttribute("message", "No Email body found.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }

        if (smtpServerInfo == null) {
            log.info("No SMTP serve found System will continue");
            log.info("Message Type ID: " + messageTypeId);
            request.setAttribute("message", "No SMTP serve found.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }

        int sentEmailCount = refillReminderDAO.sentEmailCountRefill(email, campaignId, emailRequest.getFolderId(), messageTypeId);

        if (sentEmailCount > 0) {
            log.info("Email already sent to Email : " + email);
            request.setAttribute("message", "Order has already been placed against this email.");
            request.getRequestDispatcher("/displayMessage").forward(request, response);
            return true;
        }

        log.error("Order value is: " + order);

        String unsubUrl = RedemptionUtil.prepareUnsubscribeURL(email, smtpServerInfo.getFromEmail());
        String placeOrderUrl = RedemptionUtil.preparePlaceOrderURL(irf.getId().getTransactionNumber());
        String orderStatusUrl = null;
        emailbody = EmailSender.makeEmailBody(emailbody);

        Double payment = null;
        String cardType = null;
        String cardNumber = null;
        String pharmacyName = null;
        String pharmacyPhone = null;
        if (order != null && Objects.equals(doDirectRes, Boolean.TRUE)) {
            payment = order.getPayment();
            cardType = order.getCardType();
            cardNumber = order.getCardNumber();
            orderStatusUrl = RedemptionUtil.prepareOrderStatusURL(order.getId());
            pharmacyName = order.getPharmacy().getTitle();
            pharmacyPhone = order.getPharmacy().getPhone();
        } else {
            pharmacyName = irf.getPharmacyName();
            pharmacyPhone = irf.getPharmacyPhone();
            log.error("Order value is null");
        }
        emailbody = EmailFlowUtil.prepareMessage(null, null, pharmacyName, pharmacyPhone, emailbody, null, null, null, payment, unsubUrl, null, placeOrderUrl, orderStatusUrl, cardType, cardNumber, null);

        if (campaignName != null && !campaignName.equalsIgnoreCase("")) {
            emailbody = emailbody.replace("[_brand_]", campaignName);
        }

        log.info("Final Email :" + emailbody);

        EmailRequest emailRequestNext = SetEmailRequest(emailRequest, campaigns, email, emailbody, emailSubject, messageContext, messageTypeId, intervalDesc, intervalId, eventDesc);

        startEmailRefillSendingThread(campaigns, email, emailbody, emailRequestNext, emailSubject, secondsDelay, smtpServerInfo);

        emailRequest.setEndDate(new Date());
        refillReminderDAO.update(emailRequest);

        String responseMessage = null;
        if (messageContext.equalsIgnoreCase(Constants.REDEMPTION)) {
            responseMessage = "Thank You! for your interest in API Rx Saving & Support Program. Please check your inbox.";
        } else {
            responseMessage = "Thank You! for your interest in API Rx Saving & Support Program. You will receive an email once order placed.";
        }

        boolean endFlag = EmailFlowUtil.isHierarchyEnd(campaignMessageResponses);
        if (endFlag) {
            request.setAttribute("message", responseMessage);
            request.getRequestDispatcher("/displayMessage").forward(request, response);
        }

        return false;
    }

    private EmailRequest SetEmailRequest(EmailRequest emailRequest, Campaigns campaigns, String email, String emailbody, String emailSubject, String messageContext, int messageTypeId, String intervalDesc, int intervalId, String eventDesc) {
        EmailRequest emailRequestNext = new EmailRequest();
        emailRequestNext.setCampaignId(campaigns.getCampaignId());
        emailRequestNext.setCardholderDob(emailRequest.getCardholderDob());
        emailRequestNext.setClaimStatus(emailRequest.getClaimStatus());
        emailRequestNext.setEmail(email);
        emailRequestNext.setEmailBody(emailbody.getBytes());
        emailRequestNext.setEmailSubject(emailSubject);
        emailRequestNext.setFileName(emailRequest.getFileName());
        emailRequestNext.setFileTypeCode(emailRequest.getFileTypeCode());
        emailRequestNext.setFillDate(emailRequest.getFillDate());
        emailRequestNext.setFolderId(emailRequest.getFolderId());
        emailRequestNext.setMessageContext(messageContext);
        emailRequestNext.setMessageTypeId(messageTypeId);
        emailRequestNext.setNdcNumber(emailRequest.getNdcNumber());
        emailRequestNext.setRedemptionId(emailRequest.getRedemptionId());
        emailRequestNext.setRxGroupNumber(emailRequest.getRxGroupNumber());
        emailRequestNext.setSmtpId(campaigns.getSmtpServerInfo().getSmtpId());
        emailRequestNext.setIntervalDescription(intervalDesc);
        emailRequestNext.setIntervalId(intervalId);
        emailRequestNext.setEventDetail(eventDesc);

        return emailRequestNext;
    }

    private void startEmailRefillSendingThread(Campaigns campaigns, String email, String emailbody, EmailRequest emailRequestNext, String emailSubject, long secondsDelay, SmtpServerInfo smtpServerInfo) {
        EmailRefillSendingThread emailRefillSendingThread = new EmailRefillSendingThread();

        emailRefillSendingThread.setCampaign(campaigns);
        emailRefillSendingThread.setEmail(email);
        emailRefillSendingThread.setEmailBody(emailbody);
        emailRefillSendingThread.setEmailRequest(emailRequestNext);
        emailRefillSendingThread.setEmailSubject(emailSubject);
        emailRefillSendingThread.setRefillReminderDAO(refillReminderDAO);
        emailRefillSendingThread.setSecondsDelay(secondsDelay);
        emailRefillSendingThread.setSmtpServerInfo(smtpServerInfo);

        Thread thread = new Thread(emailRefillSendingThread);
        thread.start();
    }

}
