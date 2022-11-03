package com.ssa.cms.dao;

import com.ssa.cms.common.Constants;
import com.ssa.cms.dto.BaseDTO;
import com.ssa.cms.dto.PatientPreferencesDTO;
import com.ssa.cms.enumeration.StatusEnum;
import com.ssa.cms.model.CampaignMessages;
import com.ssa.cms.model.CampaignMessagesResponse;
import com.ssa.cms.model.CoPayCardDetails;
import com.ssa.cms.model.ContactUs;
import com.ssa.cms.model.DeliveryPreferences;
import com.ssa.cms.model.Drug;
import com.ssa.cms.model.DrugAdditionalMarginPrices;
import com.ssa.cms.model.DrugBrand;
import com.ssa.cms.model.DrugCategory;
import com.ssa.cms.model.DrugDetail;
import com.ssa.cms.model.DrugGenericTypes;
import com.ssa.cms.model.DrugSearches;
import com.ssa.cms.model.DrugTherapyClass;
import com.ssa.cms.model.NotificationMessages;
import com.ssa.cms.model.Order;
import com.ssa.cms.model.OrdersPtMessage;
import com.ssa.cms.model.PatientAddress;
import com.ssa.cms.model.PatientDeliveryAddress;
import com.ssa.cms.model.PatientInsuranceDetails;
import com.ssa.cms.model.PatientPaymentInfo;
import com.ssa.cms.model.PatientProfile;
import com.ssa.cms.model.PatientProfileHealth;
import com.ssa.cms.model.PatientProfileMembers;
import com.ssa.cms.model.PatientProfileNotes;
import com.ssa.cms.model.PatientProfilePreferences;
import com.ssa.cms.model.PharmacyZipCodes;
import com.ssa.cms.model.PreferencesSetting;
import com.ssa.cms.model.RewardHistory;
import com.ssa.cms.model.RewardPoints;
import com.ssa.cms.model.SmtpServerInfo;
import com.ssa.cms.model.TransferDetail;
import com.ssa.cms.model.TransferRequest;
import com.ssa.cms.model.Url;
import com.ssa.cms.model.ZipCodeCalculation;
import com.ssa.cms.util.AppUtil;
import com.ssa.cms.util.CommonUtil;
import com.ssa.cms.util.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author msheraz
 */
@Repository
@Transactional
public class PatientProfileDAO extends BaseDAO {

    public Object getObjectById(Object clz, int id) {
        return this.getCurrentSession().get(clz.getClass(), id);
    }

    public Object getObjectById(Class clz, int id) {
        return this.getCurrentSession().get(clz, id);
    }

    public List getAllRecords(Object type) throws Exception {
        return getCurrentSession().createQuery("from " + type.getClass().getName()).list();
    }

    public List getAllRecords(Class entity) throws Exception {
        return getCurrentSession().createQuery("from " + entity.getName()).list();
    }

    public List<PatientProfile> getPatientProfilesList() throws Exception {
        return getCurrentSession().createQuery("From PatientProfile patientProfile "
                + " left join fetch patientProfile.billingAddress billingAddress order by patientProfile.createdOn desc").list();
    }

    public PatientProfile getPatientProfile(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile "
                //+ " left join fetch patientProfile.billingAddress billingAddress "
                + "left join fetch patientProfile.patientProfileHealthsList patientProfileHealthsList where patientProfile.id=:id");
        query.setParameter("id", id);
        return (PatientProfile) query.uniqueResult();
    }

    public List<Object[]> getPatientProfilesListWithDefaultAddress() throws Exception {
//        return getCurrentSession().createQuery("From PatientProfile p,PatientDeliveryAddress addr "
//                + "  where p.id=addr.patientProfile.id and addr.defaultAddress='Yes' order by p.firstName  ").list();

        return getCurrentSession().createQuery("From PatientProfile p left join p.patientDeliveryAddresses addr "
                + "  where p.id=addr.patientProfile.id and (addr=null or  addr.defaultAddress='Yes' )order by p.firstName  ").list();
    }

    public String getURL(String urlCode) throws Exception {
        Url url = null;
        String urlString = "";

        String hql = "select url from Url url "
                + "where url.code = :urlCode "
                + "and url.endDate is null";

        List<Url> list = (List<Url>) this.getCurrentSession().createQuery(hql)
                .setParameter("urlCode", urlCode)
                .list();

        if (list != null && (!list.isEmpty())) {
            url = new Url();
            url = list.get(0);
            urlString = url.getUrl();
        }

        return urlString;
    }

    public boolean updatePatientInfo(PatientProfile patientProfile) throws Exception {
        Query query = getCurrentSession().createQuery("Update PatientProfile set updatedOn=:currentDate,status=:patientStatus,comments=:comments,insuranceProvider=:insuranceProvider,planId=:planId,groupNumber=:groupNumber,memberId=:memberId,insuranceExpiryDate=:expiryDate,providerPhoneNumber=:providerPhoneNo,providerAddress=:providerAddress where id=:id");
        query.setParameter("currentDate", new Date());
        query.setParameter("patientStatus", patientProfile.getStatus());
        query.setParameter("comments", patientProfile.getComments());
        query.setParameter("insuranceProvider", patientProfile.getInsuranceProvider());
        query.setParameter("planId", patientProfile.getPlanId());
        query.setParameter("groupNumber", patientProfile.getGroupNumber());
        query.setParameter("memberId", patientProfile.getMemberId());
        query.setParameter("expiryDate", patientProfile.getInsuranceExpiryDate());
        query.setParameter("providerPhoneNo", patientProfile.getProviderPhoneNumber());
        query.setParameter("providerAddress", patientProfile.getProviderAddress());
        query.setParameter("id", patientProfile.getId());
        query.executeUpdate();
        if (query.executeUpdate() != 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public String isCommunicationIdExist(String communicationId) throws Exception {
        String statusCode = "";
        SQLQuery sQLQuery = null;
        if (communicationId.contains("@")) {
            sQLQuery = getCurrentSession().createSQLQuery("SELECT DISTINCT EmailCustomerRequest.`Status_Code` "
                    + "                FROM "
                    + "                `PtProfile_Info` "
                    + "                INNER JOIN `EmailOptInOut` "
                    + "                    ON EmailOptInOut.`Email` = PtProfile_Info.`CommunicationID` "
                    + "                  INNER JOIN `EmailCustomerRequest` "
                    + "                    ON EmailCustomerRequest.`Email` = EmailOptInOut.`Email` "
                    + "                 WHERE PtProfile_Info.`CommunicationID` = ? AND PtProfile_Info.`Status`='Pending'"
                    + "                 ORDER BY EmailCustomerRequest.`Effective_Date` DESC");
        } else {
            sQLQuery = getCurrentSession().createSQLQuery("SELECT "
                    + "  DISTINCT CustomerRequest.`Status_Code` "
                    + "FROM "
                    + "  `PtProfile_Info` "
                    + "  INNER JOIN `OptInOut` "
                    + "    ON OptInOut.`Phone_Number` = PtProfile_Info.`CommunicationID` "
                    + "  INNER JOIN `CustomerRequest` "
                    + "    ON CustomerRequest.`Phone_Number` = OptInOut.`Phone_Number` "
                    + " WHERE PtProfile_Info.`CommunicationID` = ? AND PtProfile_Info.`Status`='Pending'"
                    + " ORDER BY CustomerRequest.`Effective_Date` DESC");
        }

        sQLQuery.setParameter(0, communicationId);
        List<Object[]> list = sQLQuery.list();
        if (list != null && !list.isEmpty()) {
            Object object = list.get(0);
            if (object.toString().equalsIgnoreCase(StatusEnum.COMPLETED.getValue()) || object.toString().equalsIgnoreCase(StatusEnum.IN_PROGRESS.getValue()) || object.toString().equalsIgnoreCase(StatusEnum.STOPPED.getValue())) {
                statusCode = object.toString();
            }
        }
        return statusCode;
    }

    public SmtpServerInfo getSmtpServerInfo(String campaignId) {
        Query query = getCurrentSession().createQuery("From SmtpServerInfo smtpServerInfo left join fetch smtpServerInfo.campaignses campaign where campaign.campaignId=:campaignId");
        query.setParameter("campaignId", Integer.parseInt(campaignId));
        return (SmtpServerInfo) query.list().get(0);
    }

    public boolean isVerificationCodeExist(String phoneNumber, Integer code) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile  where mobileNumber=:phoneNumber and verificationCode=:code");
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("code", code);
        if (query.list().size() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public List retrieveatientByPhoneAndCode(String phoneNumber, Integer code) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where mobileNumber=:phoneNumber and verificationCode=:code");
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("code", code);
        return query.list();
    }

    public boolean isPatientPhoneNumberExist(String phoneNumber) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where mobileNumber=:phoneNumber");
        query.setParameter("phoneNumber", phoneNumber);
        //query.setParameter("status", Constants.COMPLETED);
        if (query.list().size() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public RewardPoints getRewardPoints(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From RewardPoints rewardPoints where rewardPoints.id=:id");
        query.setParameter("id", id);
        return (RewardPoints) query.uniqueResult();
    }

    public boolean updateVerificationCode(Integer verificationCode, String mobileNumber) throws Exception {
        Query query = getCurrentSession().createQuery("Update PatientProfile set updatedOn=:currentDate,verificationCode=:verificationCode where mobileNumber=:mobileNumber");
        query.setParameter("currentDate", new Date());
        query.setParameter("verificationCode", verificationCode);
        query.setParameter("mobileNumber", mobileNumber);
        if (query.executeUpdate() != 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public PatientProfile getPatientProfileByMobileNumber(String mobileNumber) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where patientProfile.mobileNumber=:mobileNumber and status=:status");
        query.setParameter("mobileNumber", mobileNumber);
        query.setParameter("status", Constants.COMPLETED);
        return (PatientProfile) query.uniqueResult();
    }

    public Long populateDependentsCount(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientProfileMembers pm where pm.patientId=:patientId");
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public List populateInsCardList(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery(" From PatientInsuranceDetails pm where pm.patientProfile.id=:patientId and pm.dependent is null  and isArchived=0");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    public Long populateInsCardCount(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientInsuranceDetails pm where pm.patientProfile.id=:patientId and pm.dependent is null  and isArchived=0");
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public Long populateInsCardCountForDependent(Integer dependentId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientInsuranceDetails pm where pm.dependent.id=:dependentId and isArchived=0");
        query.setParameter("dependentId", dependentId);
        return (Long) query.uniqueResult();
    }

    //////////////////////////////////////////////////////////////////////////////////
    public List populateInsCardListForDependent(Integer dependentId) throws Exception {
        Query query = getCurrentSession().createQuery(" From PatientInsuranceDetails pm where pm.dependent.id=:dependentId and isArchived=0");
        query.setParameter("dependentId", dependentId);
        return query.list();
    }
    //////////////////////////////////////////////////////////////////////////////////

    public PatientProfile getPatientProfileByToken(String securityToken) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile "
                + " left join fetch patientProfile.billingAddress billingAddress left join fetch patientProfile.deliveryPreferenceId deliveryPreferenceId where patientProfile.securityToken=:securityToken and status=:status");
        query.setParameter("securityToken", securityToken);
        query.setParameter("status", Constants.COMPLETED);
        return (PatientProfile) query.uniqueResult();
    }

    public PatientAddress getPatientAddressById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientAddress patientAddress where patientAddress.id=:id");
        query.setParameter("id", id);
        return (PatientAddress) query.uniqueResult();
    }

    public PatientDeliveryAddress getPatientDeliveryAddressById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress where patientDeliveryAddress.id=:id");
        query.setParameter("id", id);
        return (PatientDeliveryAddress) query.uniqueResult();
    }

    public DeliveryPreferences getDeliveryPreferenceById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From DeliveryPreferences deliveryPreferences where deliveryPreferences.id=:id");
        query.setParameter("id", id);
        return (DeliveryPreferences) query.uniqueResult();
    }

    public PatientProfile getPatientProfileByPhoneNumber(String mobileNumber) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where patientProfile.mobileNumber=:mobileNumber and status=:status");
        query.setParameter("mobileNumber", mobileNumber);
        query.setParameter("status", Constants.PENDING);
        return (PatientProfile) query.uniqueResult();
    }

    public List<PatientProfileMembers> getPatientProfileMembersListById(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfileMembers profileMembers where profileMembers.patientId=:patientId and (profileMembers.archived is null or profileMembers.archived=0) "
                + "and (profileMembers.isAdult =0 or (profileMembers.isAdult =1 and profileMembers.isApproved =1))order by firstName,lastName");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    public Long getTotalRewardHistoryPoints(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("SELECT SUM(point) FROM RewardHistory WHERE patientId=:patientId AND type='PLUS'");
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public ContactUs getContactUsByProfileId(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From ContactUs contactUs left join fetch contactUs.patientProfile patientProfile where patientProfile.id=:id");
        query.setParameter("id", id);
        return (ContactUs) query.uniqueResult();
    }

    public List<DrugBrand> getDrugBrandsList(String name) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugBrand drugBrand where drugBrand.drugBrandName like :name or drugBrand.drugGenericTypes.drugGenericName like :name");
        query.setParameter("name", name + "%");
        return query.list();
    }

    public List<Drug> getAllDrug(Integer drugBrandId) throws Exception {
        Query query = getCurrentSession().createQuery("from Drug drug left join fetch drug.drugUnits drugUnits where drug.drugBrand.id=:drugBrandId");
        query.setParameter("drugBrandId", drugBrandId);
        return query.list();
    }

    public List<DrugAdditionalMarginPrices> getDrugAdditionalMarginPrices(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugAdditionalMarginPrices drugAdditionalMarginPrices left join fetch drugAdditionalMarginPrices.drugAdditionalMargin drugAdditionalMargin where drugAdditionalMargin.drugCategory.id=:id");
        query.setParameter("id", id);
        return query.list();
    }

    public Drug getDrugById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From Drug drug left join fetch drug.drugBrand drugBrand where drug.drugId=:drugId");
        query.setParameter("drugId", id);
        return (Drug) query.uniqueResult();
    }

    public List<NotificationMessages> getNotificationMessagesByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From NotificationMessages notificationMessages left join fetch notificationMessages.patientProfile patientProfile left join fetch notificationMessages.messageType messageType where patientProfile.id=:id and notificationMessages.status=:status order by notificationMessages.createdOn desc");
        query.setParameter("id", profileId);
        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    //////////////////////////////////////////////////////////////
    public List<OrdersPtMessage> getPharmacyNotificationMessagesByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From OrdersPtMessage ordPtMsg left join fetch ordPtMsg.order ord left join fetch ord.patientProfileMembers patientProfileMembers where ord.patientProfile.id=:id  order by ordPtMsg.createdOn desc");
        query.setParameter("id", profileId);
//        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    //////////////////////////////////////////////////////////////
    public List<OrdersPtMessage> getPharmacyNotificationMessagesByOrderId(Integer orderId) throws Exception {
        Query query = getCurrentSession().createQuery("From OrdersPtMessage ord where ord.order.id=:id  order by ord.createdOn desc");
        query.setParameter("id", "" + orderId);
//        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    public List<NotificationMessages> getNotificationMessagesListById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From NotificationMessages notificationMessages left join fetch notificationMessages.patientProfile patientProfile left join fetch notificationMessages.messageType messageType where notificationMessages.id=:id and notificationMessages.status=:status order by notificationMessages.createdOn desc");
        query.setParameter("id", id);
        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    ////////////////////////////////////////////////////////////////
    public List<NotificationMessages> getNotificationMessagesListForWaitingResponses(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From NotificationMessages notificationMessages left join fetch "
                + " notificationMessages.patientProfile patientProfile "
                + " left join fetch notificationMessages.messageType messageType "
                + " left join fetch notificationMessages.orders ord "
                + " where notificationMessages.isEventFire=0 and messageType.responseRequired=1 "
                + " and patientProfile.id=:patientId and notificationMessages.status=:status and "
                + " ord is not null and ord.orderStatus.id=16  "
                + " order by notificationMessages.id asc");
        query.setParameter("patientId", patientId);
        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    ///////////////////////////////////////////////////////////////
    public Long getTotalReadNotificationMessages(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From NotificationMessages notificationMessages where notificationMessages.isRead=:isRead and patientProfile.id=:id");
        query.setParameter("isRead", Boolean.TRUE);
        query.setParameter("id", profileId);
        return (Long) query.uniqueResult();
    }

    public Long getTotalUnReadNotificationMessages(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From NotificationMessages notificationMessages where notificationMessages.isRead=:isRead and patientProfile.id=:id");
        query.setParameter("isRead", Boolean.FALSE);
        query.setParameter("id", profileId);
        return (Long) query.uniqueResult();
    }

    /////////////////////////////////////////////////////////////////////////
    public Long getInsuranceCardsCount(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientInsuranceDetails details where details.isArchived=0 and details.patientProfile.id=:id and details.dependent is null ");
        query.setParameter("id", profileId);
        return (Long) query.uniqueResult();
    }

    public Long getCopayCardsCount(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From CoPayCardDetails details where details.isArchive=0 and details.patientProfile.id=:id and details.order is NULL ");
        query.setParameter("id", profileId);
        return (Long) query.uniqueResult();
    }

    ////////////////////////////////////////////////////////////////////////
    public Object getRecordByType(Object type, String title) throws Exception {
        Criteria searchCriteria = getCurrentSession().createCriteria(type.getClass());
        return searchCriteria.add(Restrictions.and(Restrictions.eq("type", title))).uniqueResult();
    }

    public Long getTotalRewardHistoryPointByType(String type, Integer profileId) throws Exception {
        try {
            Query query = getCurrentSession().createQuery("Select sum(rewardHistory.point) From RewardHistory rewardHistory where rewardHistory.type=:type and rewardHistory.patientId=:profileId");
            query.setParameter("type", type);
            query.setParameter("profileId", profileId);
            return (Long) query.uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public List getAllRecords(Class entity, Integer offset, Integer maxResults) throws Exception {
        Query query = getCurrentSession().createQuery("from " + entity.getName());
        query.setMaxResults(maxResults);
        query.setFirstResult(offset);
        return query.list();
    }

    public Long getTotalRecords(Class aClass) throws Exception {
        String queryCount = "Select count(*) from " + aClass.getName();
        Query query = getCurrentSession().createQuery(queryCount);
        return (Long) query.uniqueResult();
    }

    public List getDrugSearch(Class entity, String searchParameter) throws Exception {
        //String str_query = "from DrugCategory drugCategory left join fetch drugCategory.drugTherapyClass drugTherapyClass left join drugTherapyClass.drugGenericTypes drugGenericTypes left join drugGenericTypes.drugBrand";
        String str_queryDrugCategory = "from DrugCategory drugCategory inner join fetch drugCategory.drugTherapyClass drugTherapyClass inner join fetch drugTherapyClass.drugGenericTypes drugGenericTypes"
                + " inner join fetch drugGenericTypes.drugBrand drugBrand inner join fetch drugBrand.drugs drugs where drugCategory.drugCategoryName like '%" + searchParameter + "%' and drugs.drugMacPrice=:macPrice";
        String str_queryDrugTherapyClass = "from DrugTherapyClass drugTherapyClass inner join fetch drugTherapyClass.drugGenericTypes drugGenericTypes "
                + "inner join fetch drugGenericTypes.drugBrand drugBrand inner join fetch drugBrand.drugs drugs where drugTherapyClass.drugTherapyClassName like '%" + searchParameter + "%' and drugs.drugMacPrice=:macPrice";
        String str_queryDrugGenericTypes = "from DrugGenericTypes drugGenericTypes inner join fetch drugGenericTypes.drugBrand drugBrand inner join fetch drugBrand.drugs drugs where drugGenericTypes.drugGenericName like '%" + searchParameter + "%' and drugs.drugMacPrice=:macPrice";
        String str_queryDrugBrand = "from DrugBrand drugBrand inner join fetch drugBrand.drugs drugs where drugBrand.drugBrandName like '%" + searchParameter + "%' and drugs.drugMacPrice=:macPrice";

        List lst_DrugCategory = new ArrayList();

        /**
         * records exist in DrugCategory
         */
        Query query = getCurrentSession().createQuery(str_queryDrugCategory);
        query.setParameter("macPrice", 0d);
        lst_DrugCategory = query.list();
        if (lst_DrugCategory.size() > 0) {
            return lst_DrugCategory;
        }

        /**
         * record exist in Drug Therapy Class
         */
        query = getCurrentSession().createQuery(str_queryDrugTherapyClass);
        lst_DrugCategory = query.list();
        if (lst_DrugCategory.size() > 0) {

            return lst_DrugCategory;
        }

        /**
         * search in Generic types
         */
        query = getCurrentSession().createQuery(str_queryDrugGenericTypes);
        lst_DrugCategory = query.list();
        if (lst_DrugCategory.size() > 0) {

            return lst_DrugCategory;
        }

        /**
         * search in drug brand
         */
        query = getCurrentSession().createQuery(str_queryDrugBrand);
        lst_DrugCategory = query.list();
        if (lst_DrugCategory.size() > 0) {

            return lst_DrugCategory;
        }

        return lst_DrugCategory;
    }

    public CampaignMessagesResponse getCampaignMessagesResponseByCampaignMessageId(Integer messageId) throws Exception {
        Query query = getCurrentSession().createQuery("From CampaignMessagesResponse campaignMessagesResponse left join fetch campaignMessagesResponse.campaignMessages campaignMessages where campaignMessages.messageType.id.messageTypeId=:messageId");
        query.setParameter("messageId", messageId);
        return (CampaignMessagesResponse) query.uniqueResult();
    }

    public List getDrugCategoryList(Integer offset, Integer maxResults) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugCategory drugCategory inner join fetch drugCategory.drugTherapyClass drugTherapyClass inner join fetch drugTherapyClass.drugGenericTypes drugGenericTypes "
                + "inner join fetch drugGenericTypes.drugBrand drugBrand inner join fetch drugBrand.drugs drugs where drugs.drugMacPrice=:macPrice");
        query.setParameter("macPrice", 0d);
        query.setMaxResults(maxResults);
        query.setFirstResult(offset);
        return query.list();
    }

    public Long getTotalRxOrderStatusRecordByPatientId(Integer patientId, Integer orderStatusId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From Order ord join ord.orderStatus orderStatus join ord.patientProfile patientProfile where patientProfile.id=:patientId and orderStatus.id=:orderStatusId");
        query.setParameter("patientId", patientId);
        query.setParameter("orderStatusId", orderStatusId);
        return (Long) query.uniqueResult();
    }

    public Double getTotalRxOrderSaving(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select Sum(ord.redeemPointsCost) From Order ord join ord.patientProfile patientProfile where patientProfile.id=:patientId");
        query.setParameter("patientId", patientId);
        return (Double) query.uniqueResult();
    }

    public List<Order> getOrdersListByProfileId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord left join fetch ord.patientProfile patientProfile left join fetch ord.orderStatus orderStatus left join fetch ord.orderChain left join fetch ord.deliveryPreference where patientProfile.id=:patientId and orderStatus.id not in(11,18) order by ord.createdOn desc");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    //////////////////////////////////////////////////////////
    public List<Order> getActiveOrdersListByProfileId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord left join fetch ord.patientProfile patientProfile left join fetch ord.orderStatus orderStatus left join fetch ord.orderChain left join fetch ord.deliveryPreference where patientProfile.id=:patientId and orderStatus.id not in(11,16,18) order by ord.createdOn desc");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    //////////////////////////////////////////////////////////
    public List<Order> getRefillableOrdersListByProfileId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord left join fetch "
                + "  ord.patientProfile patientProfile left join fetch ord.orderStatus orderStatus where "
                + " patientProfile.id=:patientId and orderStatus.id  in(5,6,15) "
                + " and ord.orderChain.nextRefillDate<=:nextRefillDate and ord.orderChain.refillRemaing>0 "
                + " and ord.orderChain.rxExpiredDate>:nextRefillDate and ord.patientProfileMembers is null "
                + " and (ord.refillDone is null or ord.refillDone=0) "
                + " order by ord.createdOn desc ");
        query.setParameter("patientId", patientId);
        query.setDate("nextRefillDate", new Date());
        return query.list();
    }
    /////////////////////////////////////////////////////////

    public List<RewardHistory> getRewardHistoryByProfileId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From RewardHistory reward where reward.patientId=:patientId order by reward.id asc");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    public List<Order> viewOrderReceiptList(Integer patientId, String orderId) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord left join fetch ord.patientProfile patientProfile left join fetch ord.orderStatus orderStatus left join fetch ord.drugDetail drugDetail left join fetch ord.orderTransferImages orderTransferImages where patientProfile.id=:patientId and ord.id=:orderId order by ord.createdOn desc");
        query.setParameter("patientId", patientId);
        query.setParameter("orderId", orderId);
        return query.list();
    }

    public PatientProfileHealth getPatientProfileHealthByPatientId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfileHealth patientProfileHealth left join fetch patientProfileHealth.patientProfile patientProfile where patientProfile.id=:patientId");
        query.setParameter("patientId", patientId);
        return (PatientProfileHealth) query.uniqueResult();
    }

    public boolean deletePatientDependent(Integer profileId, Integer memberId) throws Exception {
        Query query = getCurrentSession().createQuery("Delete From PatientProfileMembers patientProfileMembers where patientProfileMembers.patientId=:profileId and patientProfileMembers.id=:memberId");
        query.setParameter("profileId", profileId);
        query.setParameter("memberId", memberId);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Long getTotalPatientMembersByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientProfileMembers patientProfileMembers where patientProfileMembers.patientId=:profileId and (patientProfileMembers.archived is null or patientProfileMembers.archived=0) ");
        query.setParameter("profileId", profileId);
        return (Long) query.uniqueResult();
    }

    public Long getTotalPlaceOrdersRecordsByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From Order ord join ord.patientProfile patientProfile where patientProfile.id=:profileId and ord.orderStatus.id in (1,2,3,5,6,8,15,16,17,19)");
        query.setParameter("profileId", profileId);
        return (Long) query.uniqueResult();
    }

    public Long getTotalActiveOrdersRecordsByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From Order ord join ord.patientProfile patientProfile where patientProfile.id=:profileId and ord.orderStatus.id in (1,2,3,5,6,8,15,17,19)");
        query.setParameter("profileId", profileId);
        return (Long) query.uniqueResult();
    }

    public List<PatientDeliveryAddress> getPatientDeliveryAddressesByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile left join fetch patientDeliveryAddress.state state where patientProfile.id=:profileId order by patientDeliveryAddress.createdOn desc");
        query.setParameter("profileId", profileId);
        return query.list();
    }

    public PatientDeliveryAddress getPatientDeliveryAddressById(Integer profileId, Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile left join fetch patientDeliveryAddress.state state where patientProfile.id=:profileId and patientDeliveryAddress.id=:id");
        query.setParameter("profileId", profileId);
        query.setParameter("id", id);
        return (PatientDeliveryAddress) query.uniqueResult();
    }

    public PatientDeliveryAddress getDefaultPatientDeliveryAddress(Integer profileId) throws Exception {
        PatientDeliveryAddress patientDeliveryAddress = new PatientDeliveryAddress();
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile left join fetch patientDeliveryAddress.state state left join fetch patientDeliveryAddress.deliveryPreferences deliveryPreferences where patientProfile.id=:profileId and patientDeliveryAddress.defaultAddress='Yes' Order by patientDeliveryAddress.createdOn desc");
        query.setParameter("profileId", profileId);
        if (!CommonUtil.isNullOrEmpty(query.list())) {
            patientDeliveryAddress = (PatientDeliveryAddress) query.list().get(0);
        }
        return patientDeliveryAddress;
    }

    public List<PatientPaymentInfo> getPatientPaymentInfoListByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile left join fetch patientPaymentInfo.billingAddress billingAddress where patientProfile.id=:profileId order by patientPaymentInfo.createdOn desc");
        query.setParameter("profileId", profileId);
        return query.list();
    }

    public PatientPaymentInfo getPatientPaymentInfoDefaultCardByProfileId(Integer profileId, String defaultCard) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile where patientProfile.id=:profileId and patientPaymentInfo.defaultCard=:defaultCard");
        query.setParameter("profileId", profileId);
        query.setParameter("defaultCard", defaultCard);
        return (PatientPaymentInfo) query.uniqueResult();
    }

    public PatientPaymentInfo getPatientPaymentInfoById(Integer id, Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile left join fetch patientPaymentInfo.billingAddress billingAddress where patientPaymentInfo.id=:id and patientProfile.id=:profileId");
        query.setParameter("id", id);
        query.setParameter("profileId", profileId);
        return (PatientPaymentInfo) query.uniqueResult();
    }

    public List<PatientDeliveryAddress> getPatientDeliveryAddressListById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile left join fetch patientDeliveryAddress.state state where patientDeliveryAddress.id !=:id and patientDeliveryAddress.defaultAddress='Yes'");
        query.setParameter("id", id);
        return query.list();
    }

    public List<PatientPaymentInfo> getPatientPaymentInfoListByPatientId(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile where patientProfile.id=:patientId and patientPaymentInfo.defaultCard='Yes'");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    public List<PharmacyZipCodes> getPharmacyZipCodesList() throws Exception {
        return getCurrentSession().createQuery("select distinct pharmacyZipCodes From PharmacyZipCodes pharmacyZipCodes left join fetch pharmacyZipCodes.deliveryDistanceFeesList deliveryDistanceFeesList left join fetch deliveryDistanceFeesList.deliveryDistances deliveryDistances left join fetch deliveryDistanceFeesList.deliveryPreferenceses deliveryPreferenceses ").list();
    }

    public void updateDeliveryPreferencesByProfileId(Integer profileId, Integer dprefId, String status, String deliveryFee, String distance) throws Exception {
        SQLQuery sQLQuery = getCurrentSession().createSQLQuery("Update PatientProfileInfo SET DeliveryPreferenceId=:dprefId,Status=:status,DeliveryFee=:deliveryFee,Distance=:distance where Id=:profileId");
        sQLQuery.setParameter("dprefId", dprefId);
        sQLQuery.setParameter("status", status);
        sQLQuery.setParameter("deliveryFee", deliveryFee);
        sQLQuery.setParameter("distance", distance);
        sQLQuery.setParameter("profileId", profileId);
        sQLQuery.executeUpdate();
    }

    public Long getTotalDeliveryAddressByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientDeliveryAddress patientDeliveryAddress join patientDeliveryAddress.patientProfile patientProfile where patientProfile.id=:profileId");
        query.setParameter("profileId", profileId);
        return (Long) query.uniqueResult();
    }

    public Long getTotalPaymentCardsByProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From PatientPaymentInfo patientPaymentInfo join patientPaymentInfo.patientProfile patientProfile where patientProfile.id=:profileId");
        query.setParameter("profileId", profileId);
        return (Long) query.uniqueResult();
    }

    public boolean updateTransferRequest(Integer profileId, Integer transferRxId, Integer devliveryAddressId, Integer paymentId, Integer dprefId, String zip, String miles, String deliveryFee) throws Exception {
        SQLQuery sQLQuery = getCurrentSession().createSQLQuery("Update TransferRequest SET PatientDeliveryAddressId=:devliveryAddressId,PatientPaymentInfoId=:paymentId,DeliveryPreferencesId=:dprefId,Zip=:zip,Miles=:miles,DeliveryFee=:deliveryFee where Id=:transferRxId and PatientId=:profileId");
        sQLQuery.setParameter("devliveryAddressId", devliveryAddressId);
        sQLQuery.setParameter("paymentId", paymentId);
        sQLQuery.setParameter("dprefId", dprefId);
        sQLQuery.setParameter("zip", zip);
        sQLQuery.setParameter("miles", miles);
        sQLQuery.setParameter("deliveryFee", deliveryFee);
        sQLQuery.setParameter("transferRxId", transferRxId);
        sQLQuery.setParameter("profileId", profileId);

        if (sQLQuery.executeUpdate() != 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public List getDrugCategoryListAll(Integer offset, Integer maxResults) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugCategory drugCategory inner join fetch drugCategory.drugTherapyClass drugTherapyClass inner join fetch drugTherapyClass.drugGenericTypes drugGenericTypes "
                + "inner join fetch drugGenericTypes.drugBrand drugBrand inner join fetch drugBrand.drugs drugs inner join fetch drugs.drugUnits drugUnits");
        query.setMaxResults(maxResults);
        query.setFirstResult(offset);
        return query.list();
    }

    /**
     * Update Allergies
     *
     * @param patientProfile
     * @return
     * @throws Exception
     */
    public boolean updatePatientInfoAllergies(PatientProfile patientProfile) throws Exception {
        Query query = getCurrentSession().createQuery("Update PatientProfile set updatedOn=:currentDate,allergies=:allergies where id=:id");
        query.setParameter("currentDate", new Date());
        query.setParameter("allergies", patientProfile.getAllergies());
        query.setParameter("id", patientProfile.getId());
        query.executeUpdate();
        if (query.executeUpdate() != 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public List<RewardHistory> getRewardHistorysByPatientId(Integer patientId) {
        Query query = getCurrentSession().createQuery("From RewardHistory rewardHistory where rewardHistory.patientId=:patientId order by rewardHistory.id desc");
        query.setParameter("patientId", patientId);
        return query.list();
    }

    public List<DrugCategory> searchDrugCategoryListByParameter(String name) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugCategory drugCategory where drugCategory.drugCategoryName like :name");
        query.setParameter("name", name + "%");
        query.setMaxResults(Constants.PAGING_CONSTANT.RECORDS_PER_AUTO_COMPLETE);
        return query.list();
    }

    public List<DrugTherapyClass> searchDrugTherapyClassListByParameter(Integer drugCatId, String drugTherapyClassname) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugTherapyClass drugTherapyClass where drugTherapyClass.drugTherapyClassName like :name and drugTherapyClass.drugCategory.id = :catId");
        query.setParameter("name", drugTherapyClassname + "%");
        query.setParameter("catId", drugCatId);
        query.setMaxResults(Constants.PAGING_CONSTANT.RECORDS_PER_AUTO_COMPLETE);
        return query.list();
    }

    public List<DrugGenericTypes> searchDrugGenericTypesListByParameter(Integer therapyClassId, String drugGenericType) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugGenericTypes drugGenericTypes where drugGenericTypes.drugGenericName like :name and drugGenericTypes.drugTherapyClass.id = :catId");
        query.setParameter("name", drugGenericType + "%");
        query.setParameter("catId", therapyClassId);
        query.setMaxResults(Constants.PAGING_CONSTANT.RECORDS_PER_AUTO_COMPLETE);
        return query.list();
    }

    public List<DrugBrand> searchDrugBrandNameListByParameter(Integer genericTypeId, String drugBrandName) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugBrand drugBrand where drugBrand.drugBrandName like :name and drugBrand.drugGenericTypes.id = :catId");
        query.setParameter("name", drugBrandName + "%");
        query.setParameter("catId", genericTypeId);
        query.setMaxResults(Constants.PAGING_CONSTANT.RECORDS_PER_AUTO_COMPLETE);
        return query.list();
    }

    public List<DrugSearches> getDrugSearchesList(Integer profileId) throws Exception {
        //Query query = getCurrentSession().createQuery("From DrugSearches drugSearches left join fetch drugSearches.patientProfile patientProfile left join fetch drugSearches.drug drug where patientProfile.id=:profileId order by drugSearches.createdOn desc");
        Query query = getCurrentSession().createQuery("From DrugSearches drugSearches left join fetch drugSearches.patientProfile patientProfile left join fetch drugSearches.drugDetail drug where patientProfile.id=:profileId order by drugSearches.createdOn desc");
        query.setParameter("profileId", profileId);
        return query.list();
    }

    public boolean deleteDrugSearchesRecordById(Integer id) throws Exception {
        SQLQuery query = getCurrentSession().createSQLQuery("DELETE FROM DrugSearches WHERE Id=:id");
        query.setParameter("id", id);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean deleteDrugImages(String orderId) throws Exception {
        String sql = "DELETE FROM OrderTransferImages WHERE OrderId = :orderId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setString("orderId", orderId);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean deleteAllDrugSearchesRecordByProfileId(Integer profileId) throws Exception {
        SQLQuery query = getCurrentSession().createSQLQuery("Delete from DrugSearches where PatientId=:profileId");
        query.setParameter("profileId", profileId);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean isCardNumberExist(String cardNumber, Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("from PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile where patientPaymentInfo.cardNumber=:cardNumber and patientProfile.id=:profileId");
        query.setParameter("cardNumber", cardNumber);
        query.setParameter("profileId", profileId);
        if (!CommonUtil.isNullOrEmpty(query.list())) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public PatientInsuranceDetails getPatientInsuranceDetailById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientInsuranceDetails patientInsuranceDetails where patientInsuranceDetails.patientProfile.id=:id");
        query.setParameter("id", id);
        return (PatientInsuranceDetails) query.uniqueResult();
    }

    public boolean isDefaultPatientDeliveryAddress(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile where patientProfile.id=:id and patientDeliveryAddress.defaultAddress=:defaultAddress");
        query.setParameter("id", profileId);
        query.setParameter("defaultAddress", "Yes");
        if (!CommonUtil.isNullOrEmpty(query.list())) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public ZipCodeCalculation getZipCodeCalculationByProfileId(Integer profileId, Integer deliveryPreferencesId, String zipCode) throws Exception {
        ZipCodeCalculation zipCodeCalculation = new ZipCodeCalculation();
        Query query = getCurrentSession().createQuery("From ZipCodeCalculation zipCodeCalculation where zipCodeCalculation.patientId=:patientId and zipCodeCalculation.deliveryPreferencesId=:deliveryPreferencesId and zipCodeCalculation.zip=:zipCode order by zipCodeCalculation.createdOn desc");
        query.setParameter("patientId", profileId);
        query.setParameter("deliveryPreferencesId", deliveryPreferencesId);
        query.setParameter("zipCode", zipCode);
        if (!CommonUtil.isNullOrEmpty(query.list())) {
            zipCodeCalculation = (ZipCodeCalculation) query.list().get(0);
        }
        return zipCodeCalculation;
    }

    public List<ZipCodeCalculation> getZipCodeCalculationsList(String zip, Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("From ZipCodeCalculation zipCodeCalculation where zipCodeCalculation.zip=:zip and zipCodeCalculation.patientId=:profileId");
        query.setParameter("zip", zip);
        query.setParameter("profileId", profileId);
        return query.list();
    }

    public ZipCodeCalculation getZipCodeCalculations(String zip, Integer profileId, Integer prefId) throws Exception {
        Query query = getCurrentSession().createQuery("From ZipCodeCalculation zipCodeCalculation where zipCodeCalculation.zip=:zip and zipCodeCalculation.patientId=:profileId and zipCodeCalculation.deliveryPreferencesId=:prefId");
        query.setParameter("zip", zip);
        query.setParameter("profileId", profileId);
        query.setParameter("prefId", prefId);
        List lst = query.list();
        if (lst != null && lst.size() > 0) {
            return (ZipCodeCalculation) lst.get(0);
        }
        return null;

    }

    public List<TransferRequest> geTransferRequestsList(Integer profileId, Integer transferId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(TransferRequest.class);
        criteria.createCriteria("patientDeliveryAddress", "patientDeliveryAddress", JoinType.LEFT_OUTER_JOIN);
        criteria.createCriteria("patientPaymentInfo", "patientPaymentInfo", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("patientId", profileId));
        criteria.add(Restrictions.eq("id", transferId));
        return criteria.list();
    }

    public boolean updatePreviousDefaultAddress(Integer profileId, String defaultAddress) throws Exception {
        Query query = getCurrentSession().createQuery("Update PatientDeliveryAddress set updatedOn=:currentDate,defaultAddress=:defaultAddress where patientProfile.id=:profileId and defaultAddress='Yes'");
        query.setParameter("currentDate", new Date());
        query.setParameter("defaultAddress", defaultAddress);
        query.setParameter("profileId", profileId);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public PatientProfile getPatientProfileBySecurityToken(String securityToken) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where patientProfile.securityToken=:securityToken and patientProfile.status=:status");
        query.setParameter("securityToken", securityToken);
        query.setParameter("status", Constants.COMPLETED);
        return (PatientProfile) query.uniqueResult();
    }

    public List<PatientProfileNotes> getPatientProfileNotesListByPtProfileId(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("FROM PatientProfileNotes c left join fetch c.patientProfile patientProfile WHERE patientProfile.id =:profileId ORDER BY c.createdOn desc");
        query.setParameter("profileId", profileId);
        return query.list();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public List getPatientProfilesHistory(int patientId, String status) throws Exception {
        String sql = " select distinct o From Order o  "
                + " left join fetch o.patientProfile   "
                + " left join fetch o.rewardHistorySet r  "
                + " where o.patientProfile.id=:patientId and (r.type='PLUS' OR r.type=NULL)";
        if (AppUtil.getSafeStr(status, "").length() > 0) {
            sql += " and o.orderStatus.name=:status";
        }
        sql += " order by o.createdOn desc";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameter("patientId", patientId);
        if (AppUtil.getSafeStr(status, "").length() > 0) {
            query.setParameter("status", status);
        }
        return query.list();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public List getPatientProfilesHistoryOtherThanPending(int patientId) throws Exception {
        String sql = " select distinct o From Order o  "
                + " left join fetch o.patientProfile "
                + " left join fetch o.rewardHistorySet r  "
                + " where o.patientProfile.id=:patientId and (r.type='PLUS' OR r.type=NULL)"
                + " and o.orderStatus.name!='Pending'"
                + " and o.orderStatus.name!='Cancelled'"
                + " order by o.createdOn desc ";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameter("patientId", patientId);

        return query.list();
    }
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isDefaultDeliveryAddress(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("FROM PatientDeliveryAddress patientDeliveryAddress left join fetch patientDeliveryAddress.patientProfile patientProfile where patientDeliveryAddress.defaultAddress='Yes' and patientProfile.id=:profileId");
        query.setParameter("profileId", profileId);
        if (query.list() != null && !query.list().isEmpty() && query.list().size() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean isDefaultPaymentInfo(Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("FROM PatientPaymentInfo patientPaymentInfo left join fetch patientPaymentInfo.patientProfile patientProfile where patientPaymentInfo.defaultCard='Yes' and patientProfile.id=:profileId");
        query.setParameter("profileId", profileId);
        if (query.list() != null && !query.list().isEmpty() && query.list().size() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public TransferDetail getTransferDetailByTranferRequestId(int requestId) {
        TransferDetail transferdetail = null;
        Query query = getCurrentSession().createQuery(
                "FROM TransferDetail td  where  td.requsetId=:requestId");
        query.setParameter("requestId", requestId);

        List<TransferDetail> transferDetailList = query.list();

        if (!CommonUtil.isNullOrEmpty(transferDetailList)) {
            transferdetail = (TransferDetail) transferDetailList.get(0);
        }
        return transferdetail;

    }

    public boolean deleteRxTransferRecord(Integer transferId, Integer profileId) throws Exception {
        Query query = getCurrentSession().createQuery("Delete From TransferRequest transferRequest where transferRequest.id=:transferId and transferRequest.patientId=:profileId");
        query.setParameter("transferId", transferId);
        query.setParameter("profileId", profileId);
        if (query.executeUpdate() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<OrdersPtMessage> getPharmacyNotificationMessagesById(Integer id) throws Exception {
        Query query = getCurrentSession().createQuery("From OrdersPtMessage ord where ord.id=:id  order by ord.createdOn desc");
        query.setParameter("id", id);
//        query.setParameter("status", Constants.SUCCESS);
        return query.list();
    }

    public DrugDetail getDrugDetailById(long id) throws Exception {
        Query query = getCurrentSession().createQuery("From DrugDetail drugDetail left join fetch drugDetail.drugForm drugForm left join fetch drugDetail.drugBasic drugBasic where drugDetail.drugNDC=:drugNDC");
        query.setParameter("drugNDC", id);
        return (DrugDetail) query.uniqueResult();
    }

    public List retrieveDrugWithoutGeneric(String drugName) {
//        return getCurrentSession().createQuery("From PatientProfile p,PatientDeliveryAddress addr "
//                + "  where p.id=addr.patientProfile.id and addr.defaultAddress='Yes' order by p.firstName  ").list();

        return getCurrentSession().createQuery("From DrugBasic b  "
                + "  where   b.brandName like :drugName and b.drugGeneric.brandNameOnly=1"
                ////////////////////////////////////////////////////
                + " and b.drugBasicId in(select d.drugBasic.drugBasicId from DrugDetail d) "
                ///////////////////////////////////////////////////
                + " order by b.brandName  ").setParameter("drugName", drugName + "%").list();
    }

    public List retrieveDrugWithGeneric(String drugName) {
//        return getCurrentSession().createQuery("From PatientProfile p,PatientDeliveryAddress addr "
//                + "  where p.id=addr.patientProfile.id and addr.defaultAddress='Yes' order by p.firstName  ").list();

        return getCurrentSession().createQuery("From DrugBasic b  "
                + "  where  ( b.drugGeneric.genericName like :drugName or (b.brandName like :drugName and b.drugGeneric.genericName!='* BRAND NAME ONLY *')) "
                ////////////////////////////////////////////////////
                + " and b.drugBasicId in(select d.drugBasic.drugBasicId from DrugDetail d) "
                ///////////////////////////////////////////////////
                + " order by  b.brandName,b.drugGeneric.genericName ").setParameter("drugName", drugName + "%").list();
    }

    public Long getTotalRewardHistoryPointByOrderId(String type, String orderId) throws Exception {
        Query query = getCurrentSession().createQuery("Select sum(rewardHistory.point) From RewardHistory rewardHistory "
                + "where rewardHistory.type=:type and rewardHistory.order.id=:orderId");
        query.setParameter("type", type);
        query.setParameter("orderId", orderId);
        return (Long) query.uniqueResult();
    }

    public List<PatientProfile> getPatientProfileWithNoOrder(Date date) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile left join fetch patientProfile.orders ord where ord.patientProfile.id is null and patientProfile.createdOn<=:queryDate ");
        query.setParameter("queryDate", date);
        return query.list();
    }

    public List<PatientProfile> getPatientProfilesList(String mobileNumber, Integer verificationCode) throws Exception {
        Query query = getCurrentSession().createQuery("FROM PatientProfile patientProfile where patientProfile.mobileNumber=:mobileNumber and patientProfile.verificationCode=:verificationCode and status=:status");
        query.setParameter("mobileNumber", mobileNumber);
        query.setParameter("verificationCode", verificationCode);
        query.setParameter("status", Constants.COMPLETED);
        return query.list();
    }

    public List getCoPayCardWithNoOrder(Integer patientId, String isArchive, String orderId) throws Exception {
        Query query = getCurrentSession().createQuery("FROM CoPayCardDetails WHERE patientProfile.id = :patientId AND orderid = :orderId AND isArchive != :isArchive order by  id desc ")
                .setParameter("patientId", patientId)
                .setParameter("orderId", orderId)
                .setParameter("isArchive", isArchive);
//        Query query = getCurrentSession().createQuery("FROM patientprofileinfo p LEFT JOIN orders"
//                + " ON p.Id = o.PatientId WHERE o.PatientId IS NULL AND p.CreatedOn <= '" + date + "'" );
        List list = query.list();
        return list;
    }

    public List getCoPayCardForAnOrder(Integer patientId, String orderId) throws Exception {
        Query query = getCurrentSession().createQuery("FROM CoPayCardDetails WHERE patientProfile.id = :patientId AND order.id = :orderId  order by  id desc ")
                .setParameter("patientId", patientId)
                .setParameter("orderId", orderId);

//        Query query = getCurrentSession().createQuery("FROM patientprofileinfo p LEFT JOIN orders"
//                + " ON p.Id = o.PatientId WHERE o.PatientId IS NULL AND p.CreatedOn <= '" + date + "'" );
        List list = query.list();
        return list;
    }

    public List getYearEndStatment(Integer patientId, Date startDate, Date endDate) throws Exception {

        Query query = getCurrentSession().createQuery("FROM Order WHERE patientProfile.id = :patientId "
                + " AND createdOn >= :startDate AND createdOn <= :endDate "
                + " AND orderStatus.id  in(5,8,6,15) ")
                .setParameter("patientId", patientId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        List list = query.list();
        return list;
    }

    public Long getTotalAllergiesCount(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) from PatientProfile patientProfile where patientProfile.id=:patientId and patientProfile.allergies is not null and patientProfile.allergies<>''");
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public Long getAllPatientAllergiesCount(Integer patientId) {
        Query query = getCurrentSession().createQuery("Select count(*) from PatientAllergies patientAllergies where patientAllergies.patientProfile.id=:patientId and patientAllergies.allergies is not null and patientAllergies.allergies<>'' and patientAllergies.patientProfileMembers is null");
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public int updateCoPayCardDetailsByOrderId(String orderId) throws Exception {
        Query query = getCurrentSession().createQuery("Update CoPayCardDetails Set OrderId=:orderIds where order.id=:orderId");
        query.setParameter("orderIds", null);
        query.setParameter("orderId", orderId);
        return query.executeUpdate();
    }

    public List<CoPayCardDetails> getCoPayCardDetailsWithOrderIdOrWithOutOrderId(String orderId, Integer patientProfileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select coPayCardDetails From CoPayCardDetails coPayCardDetails where (coPayCardDetails.order.id=:orderId or coPayCardDetails.order.id=:orderIdZero or coPayCardDetails.order.id is null) and (coPayCardDetails.isArchive=:isArchive and coPayCardDetails.patientProfile.id=:patientProfileId) order by coPayCardDetails.id desc");
        query.setParameter("orderId", orderId);
        query.setParameter("orderIdZero", "0");
        query.setParameter("patientProfileId", patientProfileId);
        query.setParameter("isArchive", "0");
        return query.list();
    }

    public List<CoPayCardDetails> getAvailableCoPayCardDetails(Integer patientProfileId) throws Exception {
        Query query = getCurrentSession().createQuery("Select coPayCardDetails From CoPayCardDetails coPayCardDetails where coPayCardDetails.isArchive=0 and coPayCardDetails.patientProfile.id=:id and coPayCardDetails.order is NULL order by coPayCardDetails.id desc");

//        query.setParameter("orderId", orderId);
//        query.setParameter("orderIdZero", "0");
        query.setParameter("id", patientProfileId);
//        query.setParameter("isArchive", "0");
        return query.list();
    }

    public List<Order> getRefillReminderOrdersList(Date date, List<String> orderStatusList) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord left join fetch ord.orderChain orderChain left join fetch ord.orderStatus orderStatus where ord.nextRefillFlag=:nextRefillFlag and (orderChain.optOutRefillReminder=:optOutRefillReminder or orderChain.optOutRefillReminder is null) and orderChain.nextRefillDate<=:nextRefillDate and orderStatus.name in (:orderStatusList)");
        query.setParameter("nextRefillFlag", "0");
        query.setParameter("optOutRefillReminder", 0);
        query.setParameter("nextRefillDate", date);
        query.setParameterList("orderStatusList", orderStatusList);
        return query.list();
    }

    public List<Order> getOrdersForAnnualStatement(Integer currentYear, List<String> orderStatusList) throws Exception {
        Query query = getCurrentSession().createQuery("From Order ord where Year(ord.createdOn)=:createdOn and ord.orderStatus.name in (:orderStatusList)");
        query.setParameter("createdOn", currentYear);
        query.setParameterList("orderStatusList", orderStatusList);
        return query.list();
    }

    public Long getTotalOrders(List<String> orderStatusList, Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) From Order ord where ord.orderStatus.name in (:orderStatusList) and ord.patientProfile.id=:patientId");
        query.setParameterList("orderStatusList", orderStatusList);
        query.setParameter("patientId", patientId);
        return (Long) query.uniqueResult();
    }

    public Long getTotalCoPayCardDetails(Integer patientId) throws Exception {
        Query query = getCurrentSession().createQuery("Select count(*) FROM CoPayCardDetails coPayCardDetails where coPayCardDetails.patientProfile.id=:patientId and coPayCardDetails.order.id is null and coPayCardDetails.isArchive=:isArchive");
        query.setParameter("patientId", patientId);
        query.setParameter("isArchive", "0");
        return (Long) query.uniqueResult();
    }

    public List<NotificationMessages> getInAppNotificationReport(BaseDTO baseDTO) throws Exception {
        String hql = "From NotificationMessages notificationMessages left join fetch notificationMessages.patientProfile patientProfile ";
        if (baseDTO.getFromDate() != null && baseDTO.getToDate() != null && CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            hql += "where notificationMessages.createdOn>=:fromDate and notificationMessages.createdOn<=:toDate and patientProfile.mobileNumber=:phoneNumber order by notificationMessages.createdOn desc";
        } else if (baseDTO.getFromDate() != null && baseDTO.getToDate() != null) {
            hql += "where notificationMessages.createdOn>=:fromDate and notificationMessages.createdOn<=:toDate order by notificationMessages.createdOn desc";
        } else if (CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            hql += "where patientProfile.mobileNumber=:phoneNumber order by notificationMessages.createdOn desc";
        }
        Query query = getCurrentSession().createQuery(hql);
        if (baseDTO.getFromDate() != null) {
            query.setParameter("fromDate", baseDTO.getFromDate());
        }
        if (baseDTO.getToDate() != null) {
            query.setParameter("toDate", DateUtil.addDays(baseDTO.getToDate(), 1));
        }
        if (CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            query.setParameter("phoneNumber", baseDTO.getPhoneNumber());
        }
        return query.list();
    }

    //////////////////////////////////////////////////////////////
    public List<OrdersPtMessage> getPharmacyNotificationMessagesByDate(BaseDTO baseDTO) throws Exception {
        String hql = "From OrdersPtMessage ord left join fetch ord.patientProfile patientProfile ";
        if (baseDTO.getFromDate() != null && baseDTO.getToDate() != null && CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            hql += "where ord.createdOn>=:fromDate and ord.createdOn<=:toDate and patientProfile.mobileNumber=:phoneNumber order by ord.createdOn desc";
        } else if (baseDTO.getFromDate() != null && baseDTO.getToDate() != null) {
            hql += "where ord.createdOn>=:fromDate and ord.createdOn<=:toDate order by ord.createdOn desc";
        } else if (CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            hql += "where patientProfile.mobileNumber=:phoneNumber order by ord.createdOn desc";
        }
        Query query = getCurrentSession().createQuery(hql);
        if (baseDTO.getFromDate() != null) {
            query.setParameter("fromDate", baseDTO.getFromDate());
        }
        if (baseDTO.getToDate() != null) {
            query.setParameter("toDate", DateUtil.addDays(baseDTO.getToDate(), 1));
        }
        if (CommonUtil.isNotEmpty(baseDTO.getPhoneNumber())) {
            query.setParameter("phoneNumber", baseDTO.getPhoneNumber());
        }
        return query.list();
    }

    public List<PatientPreferencesDTO> loadPatientPreferences(String heading, Integer patientId) {

        String queryStr = "Select ps.id as id, ps.settingName as settingName, COALESCE(pp.preferenceValue,false) as preferenceValue "
                + "From PreferencesSetting ps LEFT OUTER JOIN "
                + "ps.patientProfilePreferenceses pp with pp.patient.id = :patientId "
                + "where ps.heading = :heading ";

        Query query = getCurrentSession().createQuery(queryStr);
        query.setParameter("heading", heading);
        query.setParameter("patientId", patientId);
        query.setResultTransformer(Transformers.aliasToBean(PatientPreferencesDTO.class));
        return query.list();
    }

    public void savePatientPreference(PatientProfilePreferences preferences) {
        super.saveOrUpdate(preferences);
    }

    public List<PharmacyZipCodes> getPharmacyZipCodesList(boolean pickedFromPharmacy) throws Exception {
        Query query = getCurrentSession().createQuery("select distinct pharmacyZipCodes From PharmacyZipCodes pharmacyZipCodes "
                + "left join fetch pharmacyZipCodes.deliveryDistanceFeesList deliveryDistanceFeesList "
                + "left join fetch deliveryDistanceFeesList.deliveryDistances deliveryDistances "
                + "left join fetch deliveryDistanceFeesList.deliveryPreferenceses deliveryPreferenceses "
                + " order by deliveryPreferenceses.seqNo");
//                + "where deliveryPreferenceses.pickedFromPharmacy=:pickedFromPharmacy");
//        query.setBoolean("pickedFromPharmacy", pickedFromPharmacy);
        return query.list();
    }

    public List<CampaignMessages> getCampaignMessagesList() throws Exception {
        Query query = getCurrentSession().createQuery("From CampaignMessages campaignMessages left join fetch campaignMessages.messageType messageType");
        return query.list();
    }

    public List<Order> getOrdersListByStatus(String mobileNumber, List<Integer> orderStatusList) throws Exception {
        Query query = getCurrentSession().createQuery("From Order o left join fetch o.patientProfile patientProfile left join fetch o.orderStatus orderStatus where patientProfile.mobileNumber=:mobileNumber and orderStatus.id in (:orderStatusList)");
        query.setParameter("mobileNumber", mobileNumber);
        query.setParameterList("orderStatusList", orderStatusList);
        return query.list();
    }

    public Object getTotalMsgCountByPatientId(Integer patientId, String testMsg) throws Exception {
        SQLQuery query = getCurrentSession().createSQLQuery("SELECT Count(*) FROM NotificationMessages WHERE ProfileId=:patientId AND IsTestMsg=:testMsg");
        query.setParameter("patientId", patientId);
        query.setParameter("testMsg", testMsg);
        return query.uniqueResult();
    }

    public Object getTotalMsgCountByPatientId(Integer patientId) throws Exception {
        SQLQuery query = getCurrentSession().createSQLQuery("SELECT Count(*) FROM NotificationMessages WHERE ProfileId=:patientId ");
        query.setParameter("patientId", patientId);
        return query.uniqueResult();
    }

    public List<PreferencesSetting> getPreferenceSettingId() throws Exception {
        Query query = getCurrentSession().createQuery("From PreferencesSetting ");
        return query.list();
    }

    public List<Order> getRefillableOrdersListByProfileId(Integer patientId, Integer dependentId) throws Exception {
        StringBuilder hql = new StringBuilder();
        hql.append("From Order ord left join fetch ");
        hql.append(" ord.patientProfile patientProfile left join fetch ord.orderStatus orderStatus where ");
        hql.append(" patientProfile.id=:patientId and orderStatus.id  in(5,6,15) ");
        hql.append(" and ord.orderChain.nextRefillDate<=:nextRefillDate and ord.orderChain.refillRemaing>0 ");
        hql.append(" and ord.orderChain.rxExpiredDate>:nextRefillDate ");
        hql.append(" and (ord.refillDone is null or ord.refillDone=0) ");
        if (!CommonUtil.isNullOrEmpty(dependentId)) {
            hql.append(" and ord.patientProfileMembers.id=:dependentId ");
        } else {
            hql.append(" and ord.patientProfileMembers is null ");
        }
        hql.append(" order by ord.createdOn desc ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameter("patientId", patientId);
        query.setDate("nextRefillDate", new Date());
        if (!CommonUtil.isNullOrEmpty(dependentId)) {
            query.setParameter("dependentId", dependentId);
        }
        return query.list();
    }

    public PatientProfile getPatientProfileByPhoneAndStatus(String mobileNumber, String status) throws Exception {
        Query query = getCurrentSession().createQuery("From PatientProfile patientProfile where patientProfile.mobileNumber=:mobileNumber and status=:status");
        query.setParameter("mobileNumber", mobileNumber);
        query.setParameter("status", status);
        return (PatientProfile) query.uniqueResult();
    }

    public void removePatientAllergiesByPatientIdAndDependentExist(Integer patientId) throws Exception {
        Query query = getCurrentSession().createSQLQuery("Delete From PatientAllergies Where PatientId In (:patientId) and DependentId is not null");
        query.setParameter("patientId", patientId);
        query.executeUpdate();
    }

    public void removePatientInsuranceDetailsByPatientIdAndDependentExist(Integer patientId) throws Exception {
        Query query = getCurrentSession().createSQLQuery("Delete From PatientInsuranceDetails Where PatientId In (:patientId) and DependentID is not null");
        query.setParameter("patientId", patientId);
        query.executeUpdate();
    }

    public void removeOrdersByPatientIdAndDependentExist(Integer patientId) throws Exception {
        Query query = getCurrentSession().createSQLQuery("Delete From Orders Where PatientId In (:patientId) and DependentId is not null");
        query.setParameter("patientId", patientId);
        query.executeUpdate();
    }
    
    public void removeNotificationMessagesByPatientIdAndDependentExist(Integer patientId) throws Exception {
        Query query = getCurrentSession().createSQLQuery("Delete From NotificationMessages Where ProfileId In (:patientId) and DependentId is not null");
        query.setParameter("patientId", patientId);
        query.executeUpdate();
    }
}
