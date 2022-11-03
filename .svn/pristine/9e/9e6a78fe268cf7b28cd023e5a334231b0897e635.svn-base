/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.service;

import com.ssa.cms.common.Constants;
import com.ssa.cms.dao.NotificationPharmacyDAO;
import com.ssa.cms.dao.OrderDAO;
import com.ssa.cms.dto.DataTablesTO;
import com.ssa.cms.dto.OrderDetailDTO;
import com.ssa.cms.dto.PatientNotificationDTO;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.OrdersPtMessage;
import com.ssa.cms.model.PatientNotification;
import com.ssa.cms.model.PatientProfileMembers;
import com.ssa.cms.model.PharmacyUser;
import com.ssa.cms.model.Users;
import com.ssa.cms.util.AppUtil;
import com.ssa.cms.util.CommonUtil;
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EncryptionHandlerUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Haider Ali
 */
@Service
@Transactional
public class NotificationPharmacyService {

    @Autowired
    private NotificationPharmacyDAO notificationPharmacyDAO;
    @Autowired
    private OrderDAO orderDAO;

    private static final Log logger = LogFactory.getLog(NotificationPharmacyService.class.getName());

    public OrdersPtMessage saveOrdersPtMessage(OrdersPtMessage ordersMsg) {
        try {
            ordersMsg.setIsRead(Boolean.FALSE);
            ordersMsg.setCreatedBy(0);
            ordersMsg.setCreatedOn(new Date());
            notificationPharmacyDAO.save(ordersMsg);
        } catch (Exception e) {
            logger.error("Exception: NotificationPharmacyService -> saveNotificationPharmacy", e);
            e.printStackTrace();
            return null;
        }
        return ordersMsg;
    }

    public List<OrdersPtMessage> getPharmacyNotificationList(String orderId) {
        List<OrdersPtMessage> newlist = new ArrayList<>();
        try {
            List<OrdersPtMessage> dbList = notificationPharmacyDAO.getPharmacyNotificationList(orderId);
            for (OrdersPtMessage message : dbList) {
                if (message.getCreatedBy() != null && message.getCreatedBy() > 0) {
                    PharmacyUser pharmacyUser = (PharmacyUser) notificationPharmacyDAO.getObjectById(new PharmacyUser(), message.getCreatedBy());
                    if (pharmacyUser != null && pharmacyUser.getId() != null) {
                        message.setSentBy(pharmacyUser.getFirstName() + " " + pharmacyUser.getLastName());
                    }
                }
                newlist.add(message);
            }
        } catch (Exception e) {
            logger.error("Exception: getPharmacyNotificationList:: ", e);
        }
        return newlist;
    }

    public String getPatientwiseFilteredMessageHistory(int iDisplayStart, int iDisplayLength, String patientId, int sEcho, String sort) {
        String json = "";
        try {
            List<OrdersPtMessage> newlist = new ArrayList<>();
            List<OrdersPtMessage> dbList = notificationPharmacyDAO.getPharmacyNotificationListByPatientId(iDisplayStart, iDisplayLength, patientId, sort);
            List<NotificationMessages> notificationList = notificationPharmacyDAO.getMedificationNotificationListByOrderId(iDisplayStart, iDisplayLength, patientId, sort);
            for (OrdersPtMessage message : dbList) {
                OrdersPtMessage newMessage = new OrdersPtMessage();
                if (message.getCreatedBy() != null && message.getCreatedBy() > 0) {
                    PharmacyUser pharmacyUser = (PharmacyUser) notificationPharmacyDAO.getObjectById(new PharmacyUser(), message.getCreatedBy());
                    if (pharmacyUser != null && pharmacyUser.getId() != null) {
                        newMessage.setSentBy(pharmacyUser.getFirstName() + " " + pharmacyUser.getLastName());
                    }
                }
                newMessage.setSentOn(DateUtil.dateToString(message.getCreatedOn(), "MM/dd/yyyy HH:mm"));
                newMessage.setMessage(message.getMessage());
                if (CommonUtil.isNotEmpty(message.getAttachmentPath())) {
                    newMessage.setAttachmentPath("<a href='" + message.getAttachmentPath() + "'>Click Here</a>");
                }
                newMessage.setCreatedOn(message.getCreatedOn());
                newMessage.setSort(sort);
                newlist.add(newMessage);
            }
            ////////////////////////////////////////////////////////////////
            for (NotificationMessages message : notificationList) {
                OrdersPtMessage newMessage = new OrdersPtMessage();
                //if (message.getCreatedBy() != null && message.getCreatedBy() > 0) {
                //PharmacyUser pharmacyUser = (PharmacyUser) notificationPharmacyDAO.getObjectById(new PharmacyUser(), message.getCreatedBy());
                //if (pharmacyUser != null && pharmacyUser.getId() != null) {
                newMessage.setSentBy("");//(pharmacyUser.getFirstName() + " " + pharmacyUser.getLastName());
                //}
                //  }
                newMessage.setSentOn(DateUtil.dateToString(message.getCreatedOn(), "MM/dd/yyyy HH:mm"));
                newMessage.setMessage(message.getMessageText());
                newMessage.setCreatedOn(message.getCreatedOn());
                newMessage.setSort(sort);
                newlist.add(newMessage);
            }
            ///////////////////////////////////////////////////////////////
            DataTablesTO<OrdersPtMessage> dataTableTo = new DataTablesTO<>();
            Integer totalRecords = dbList.size();
            if (newlist.size() > 0) {
                dataTableTo.setiTotalDisplayRecords(notificationPharmacyDAO.getTotalPharmacyNotificationRecords(patientId));
            } else {
                dataTableTo.setiTotalDisplayRecords(totalRecords.longValue());
            }
            Collections.sort(newlist, CommonUtil.ordersPtMsgSortByDate);
            dataTableTo.setAaData(newlist);
            dataTableTo.setiTotalRecords(totalRecords);
            dataTableTo.setsEcho(sEcho);
            json = CommonUtil.toJson(dataTableTo);
        } catch (Exception e) {
            logger.error("Exception: getFilteredMessageHistory:: ", e);
        }
        return json;
    }

    public String getFilteredMessageHistory(int iDisplayStart, int iDisplayLength, String orderId, int sEcho, String sort) {
        String json = "";
        try {
            List<OrdersPtMessage> newlist = new ArrayList<>();
            List<OrdersPtMessage> dbList = notificationPharmacyDAO.getPharmacyNotificationListByOrderId(iDisplayStart, iDisplayLength, orderId, sort);
            List<NotificationMessages> notificationList = notificationPharmacyDAO.getMedificationNotificationListByOrderId(iDisplayStart, iDisplayLength, orderId, sort);
            for (OrdersPtMessage message : dbList) {
                OrdersPtMessage newMessage = new OrdersPtMessage();
                if (message.getCreatedBy() != null && message.getCreatedBy() > 0) {
                    PharmacyUser pharmacyUser = (PharmacyUser) notificationPharmacyDAO.getObjectById(new PharmacyUser(), message.getCreatedBy());
                    if (pharmacyUser != null && pharmacyUser.getId() != null) {
                        newMessage.setSentBy(pharmacyUser.getFirstName() + " " + pharmacyUser.getLastName());
                    }
                }
                newMessage.setSentOn(DateUtil.dateToString(message.getCreatedOn(), "MM/dd/yyyy HH:mm"));
                newMessage.setMessage(message.getMessage());
                if (CommonUtil.isNotEmpty(message.getAttachmentPath())) {
                    newMessage.setAttachmentPath("<a href='" + message.getAttachmentPath() + "' style='font-weight:bold;' target=\"_blank\">View Attachment</a>");
                }
                newMessage.setCreatedOn(message.getCreatedOn());
                newMessage.setSort(sort);
                newlist.add(newMessage);
            }
            ////////////////////////////////////////////////////////////////
            for (NotificationMessages message : notificationList) {
                OrdersPtMessage newMessage = new OrdersPtMessage();
                //if (message.getCreatedBy() != null && message.getCreatedBy() > 0) {
                //PharmacyUser pharmacyUser = (PharmacyUser) notificationPharmacyDAO.getObjectById(new PharmacyUser(), message.getCreatedBy());
                //if (pharmacyUser != null && pharmacyUser.getId() != null) {
                newMessage.setSentBy("");//(pharmacyUser.getFirstName() + " " + pharmacyUser.getLastName());
                //}
                //  }
                newMessage.setCreatedOn(message.getCreatedOn());
                newMessage.setSentOn(DateUtil.dateToString(message.getCreatedOn(), "MM/dd/yyyy HH:mm"));
                newMessage.setMessage(message.getMessageText());
                newMessage.setSort(sort);
                newlist.add(newMessage);
            }
            ///////////////////////////////////////////////////////////////
            DataTablesTO<OrdersPtMessage> dataTableTo = new DataTablesTO<>();
            Integer totalRecords = dbList.size();
            if (newlist.size() > 0) {
                dataTableTo.setiTotalDisplayRecords(notificationPharmacyDAO.getTotalPharmacyNotificationRecords(orderId));
            } else {
                dataTableTo.setiTotalDisplayRecords(totalRecords.longValue());
            }
            Collections.sort(newlist, CommonUtil.ordersPtMsgSortByDate);
            dataTableTo.setAaData(newlist);
            dataTableTo.setiTotalRecords(totalRecords);
            dataTableTo.setsEcho(sEcho);
            json = CommonUtil.toJson(dataTableTo);
        } catch (Exception e) {
            logger.error("Exception: getFilteredMessageHistory:: ", e);
        }
        return json;
    }

    public String getFilteredDependentRecordListing(int iDisplayStart, int iDisplayLength, Integer patientId, int sEcho) {
        String json = "";
        try {
            List<PatientProfileMembers> dbList = notificationPharmacyDAO.getPatientProfileMembersListById(patientId, iDisplayStart, iDisplayLength);
            List<PatientProfileMembers> list = getDependentsList(dbList);
            DataTablesTO<PatientProfileMembers> dataTableTo = new DataTablesTO<>();
            Integer totalRecords = dbList.size();
            if (dbList.size() > 0) {
                dataTableTo.setiTotalDisplayRecords(notificationPharmacyDAO.getTotalDependentRecords(patientId));
            } else {
                dataTableTo.setiTotalDisplayRecords(totalRecords.longValue());
            }
            dataTableTo.setAaData(list);
            dataTableTo.setsEcho(sEcho);
            json = CommonUtil.toJson(dataTableTo);
        } catch (Exception e) {
            logger.error("Exception: getFilteredDependentRecordListing:: ", e);
        }
        return json;
    }

    private List<PatientProfileMembers> getDependentsList(List<PatientProfileMembers> dbList) throws Exception {
        List<PatientProfileMembers> list = new ArrayList<>();
        for (PatientProfileMembers profileMembers : dbList) {
            PatientProfileMembers patientProfileMembers = new PatientProfileMembers();
            patientProfileMembers.setFullPatientName(profileMembers.getFirstName() + " " + profileMembers.getLastName() + " (" + profileMembers.getGender() + ")");
            if (profileMembers.getDob() != null) {
                int year = DateUtil.getDiffYears(profileMembers.getDob(), new Date());
                patientProfileMembers.setSelectedDob("(" + DateUtil.dateToString(profileMembers.getDob(), Constants.USA_DATE_FORMATE) + " " + year + ")");
            }
            patientProfileMembers.setAllergies(profileMembers.getAllergies());
            list.add(patientProfileMembers);
        }
        return list;
    }

    public String getFilteredPatientMessageHistory(int iDisplayStart, int iDisplayLength, Integer patientId, int sEcho) {
        String json = "";
        List<PatientNotificationDTO> newPatientProfileNotes = new ArrayList<>();
        try {
            List<PatientNotification> dbList = notificationPharmacyDAO.getPatientNotificationListByPatientId(iDisplayStart, iDisplayLength, patientId);
            for (PatientNotification patientNotification : dbList) {
                PatientNotificationDTO notification = new PatientNotificationDTO();
                if (patientNotification.getDateSent() != null) {
                    notification.setDateSent(DateUtil.dateToString(patientNotification.getDateSent(), Constants.USA_DATE_TIME_FORMATE));
                }
                if (patientNotification.getCreatedOn() != null) {
                    notification.setSentOn(DateUtil.dateToString(patientNotification.getCreatedOn(), Constants.USA_DATE_TIME_FORMATE));
                }
                notification.setMessage(EncryptionHandlerUtil.getDecryptedString(patientNotification.getMessage()));
                if (patientNotification.getCreatedBy() != null) {
                    Users users = (Users) notificationPharmacyDAO.getObjectById(new Users(), patientNotification.getCreatedBy());
                    notification.setSentBy(users.getFirstName() + " " + users.getLastName());
                }
                newPatientProfileNotes.add(notification);
            }
            DataTablesTO<PatientNotificationDTO> dataTableTo = new DataTablesTO<>();
            Integer totalRecords = newPatientProfileNotes.size();
            if (dbList.size() > 0) {
                dataTableTo.setiTotalDisplayRecords((Long) notificationPharmacyDAO.getTotalRecords(new PatientNotification(), "PatientProfileInfoId", patientId, Constants.HIBERNATE_COUNT_FUNCTION, "*"));
            } else {
                dataTableTo.setiTotalDisplayRecords(totalRecords.longValue());
            }
            dataTableTo.setAaData(newPatientProfileNotes);
            dataTableTo.setsEcho(sEcho);
            json = CommonUtil.toJson(dataTableTo);
        } catch (Exception e) {
            logger.error("Exception: getFilteredPatientMessageHistory:: ", e);
        }
        return json;
    }

    public String getProcessedOrdersByPatientId(int iDisplayStart, int iDisplayLength, Integer filter, int sEcho, String sdir) {
        String json = "";
        try {
            List<OrderDetailDTO> listOfOrderDetail = new ArrayList<>();
            List<Integer> lstStatus = CommonUtil.generateStatusIdsList();
            List<Order> orders = notificationPharmacyDAO.getRxProgramOrdersByPatientId(filter, lstStatus);
            //populating Processed/Filled orders
            for (Order order : orders) {
                OrderDetailDTO detailDTO = new OrderDetailDTO();
                CommonUtil.populateDecryptedOrderData(detailDTO, order);
                detailDTO.setId(order.getId());
                detailDTO.setSystemGeneratedRxNumber("<span class='bold_new'>" + order.getRxPrefix() + "<br/></span>" + order.getSystemGeneratedRxNumber());
                String year = DateUtil.dateToString(order.getCreatedOn(), "yyyy");
                String month = DateUtil.dateToString(order.getCreatedOn(), "MM");
                String d = DateUtil.dateToString(order.getCreatedOn(), "dd");
                detailDTO.setOrderDate(year + "-" + month + "-<span class='redText'>" + d + "</span>");
                detailDTO.setSelfPayCheck(AppUtil.getSafeStr(order.getFinalPaymentMode(), "").equalsIgnoreCase("SELF PAY") ? "<span class='redText'>N</span>" : "INS");
                detailDTO.setQty(order.getQty());
                String drugName = "", strength = "", drugType = "", deliveryPreferencesName = "";
                if (CommonUtil.isNotEmpty(order.getDrugName())) {
                    drugName = order.getDrugName();
                }
                if (CommonUtil.isNotEmpty(order.getStrength())) {
                    strength = order.getStrength();
                }
                if (CommonUtil.isNotEmpty(order.getDrugType())) {
                    drugType = order.getDrugType();
                }
                detailDTO.setDrugName(drugName + ' ' + drugType + ' ' + strength);
                double handlingFee = order.getHandLingFee() != null ? order.getHandLingFee() : 0d;
                detailDTO.setHandLingFee(handlingFee);
                detailDTO.setHandlingFeeStr(AppUtil.roundOffNumberToCurrencyFormat(detailDTO.getHandLingFee(), "en", "US"));
                double finalPayment = order.getFinalPayment() != null ? order.getFinalPayment() : 0d;
                double paymentExcludingDelivery = finalPayment > handlingFee ? finalPayment - handlingFee : 0d;
                detailDTO.setFinalPayment(finalPayment);
                detailDTO.setFinalPaymentStr(AppUtil.roundOffNumberToCurrencyFormat(detailDTO.getFinalPayment(), "en", "US"));
                if (order.getDeliveryPreference() != null) {
                    detailDTO.setDeliveryPreferenceId(order.getDeliveryPreference().getId());
                    deliveryPreferencesName = order.getDeliveryPreference().getName();
                    detailDTO.setDeliveryPreferencesName(deliveryPreferencesName);
                    if (detailDTO.getDeliveryPreferencesName().equalsIgnoreCase("Same Day")) {
                        deliveryPreferencesName = "<span style='color:red;'>!*Same<br/>DAY *!</span>";

                    } else if (detailDTO.getDeliveryPreferencesName().equalsIgnoreCase("Next Day*")) {
                        deliveryPreferencesName = "<span class='redText'>! *</span>";
                        deliveryPreferencesName += "<span style='color:blue;'>NEXT<br/> DAY</span>";
                        deliveryPreferencesName += "<span class='redText'>*!</span>";
                    } else if (detailDTO.getDeliveryPreferencesName().equalsIgnoreCase("Pick Up at Pharmacy")) {
                        deliveryPreferencesName = "Pick Up<br/>at Pharmacy";
                    }
                    detailDTO.setDeliveryPreferencesName(deliveryPreferencesName);
                } else {
                    detailDTO.setDeliveryPreferenceId(0);
                    detailDTO.setDeliveryPreferencesName("-");
                }
                detailDTO.setZipCode(order.getZip());
                String requestType = AppUtil.getSafeStr(order.getOrderType(), "");
                String requestStr = "", requestStyle = "style=color:red;";
                if (!requestType.equalsIgnoreCase("Refill")) {
                    if (order.getIsBottleAvailable() != null && order.getIsBottleAvailable()) {
                        requestType = "X-FER LABEL SCAN";
                        requestStr = "RX  LABEL";
                        requestStyle = "style=color:blue;";
                    } else {
                        requestType = "X-FER RX SCAN";
                        requestStr = "PAPER RX";
                        requestStyle = "style=color:green;";
                    }
                }
                detailDTO.setRequestType("<span " + requestStyle + ">" + requestType + "</span>");
                detailDTO.setRequestStr(requestStr);
                listOfOrderDetail.add(detailDTO);
            }
            DataTablesTO<OrderDetailDTO> dataTableTo = new DataTablesTO<>();
            Integer totalDisplayCount = listOfOrderDetail.size();
            dataTableTo.setiTotalDisplayRecords(totalDisplayCount.longValue());
            if (!CommonUtil.isNullOrEmpty(listOfOrderDetail)) {
                listOfOrderDetail = listOfOrderDetail.subList(iDisplayStart, Math.min(listOfOrderDetail.size(), iDisplayStart + 10));
            }
            dataTableTo.setAaData(listOfOrderDetail);
            dataTableTo.setsEcho(sEcho);
            json = CommonUtil.toJson(dataTableTo);
        } catch (Exception e) {
            logger.error("Exception: getProcessedOrdersByPatientId:: ", e);
        }
        return json;
    }
}
