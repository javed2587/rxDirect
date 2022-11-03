/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.controller;

import com.ssa.cms.service.NotificationPharmacyService;
import com.ssa.cms.service.PatientProfileService;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author mzubair
 */
@RestController
public class PharamcyNotifictionController {
    
    @Autowired
    NotificationPharmacyService notificationPharmacyService;
    @Autowired
    private PatientProfileService patientProfileService;
    
    @RequestMapping(value = "/viewHistoryMessageWs/{id}", method = RequestMethod.GET)
    public ModelAndView listingPageHandler(@PathVariable("id") String id) throws Exception {
        Map<String, String> tableData = new LinkedHashMap<>();
        tableData.put("Sent By", "sentBy");
        tableData.put("Sent On", "sentOn");
        tableData.put("Message", "message");
        tableData.put("View Attachment", "attachmentPath");
        
        String col = new String();
        for (String key : tableData.keySet()) {
            String temp = "  {\n";
            temp += "   \"sTitle\": '" + key + "',\n";
            temp += "   \"mDataProp\": '" + tableData.get(key) + "'\n";
            if (key.equalsIgnoreCase("attachmentPath")) {
                temp += "   }\n";
            } else {
                temp += "   },\n";
            }
            col = col.concat(temp);
        }
        ModelAndView modelAndView = new ModelAndView("portal/viewmessagehistorydialog");
        modelAndView.addObject("type", "messageHistoryListingWs");
        modelAndView.addObject("col", col);
        modelAndView.addObject("id", id);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewPatientHistoryMessageWs/{id}", method = RequestMethod.GET)
    public ModelAndView listingPatientHistoryPageHandler(@PathVariable("id") String id) throws Exception {
        Map<String, String> tableData = new LinkedHashMap<>();
        tableData.put("Sent By", "sentBy");
        tableData.put("Sent On", "sentOn");
        tableData.put("Message", "message");
        
        String col = new String();
        for (String key : tableData.keySet()) {
            String temp = "  {\n";
            temp += "   \"sTitle\": '" + key + "',\n";
            temp += "   \"mDataProp\": '" + tableData.get(key) + "'\n";
            if (key.equalsIgnoreCase("message")) {
                temp += "   }\n";
            } else {
                temp += "   },\n";
            }
            col = col.concat(temp);
        }
        ModelAndView modelAndView = new ModelAndView("portal/viewmessagehistorydialog");
        modelAndView.addObject("type", "patientMessageHistoryListingWs");
        modelAndView.addObject("col", col);
        modelAndView.addObject("id", id);
        return modelAndView;
    }
    
    @RequestMapping(value = "/patientMessageHistoryListingWs/{filter}", produces = "application/json")
    public @ResponseBody
    String filterPatientRecordListing(@RequestParam int iDisplayStart,
            @RequestParam int iDisplayLength, @RequestParam int sEcho,
            @PathVariable String filter, HttpServletRequest request) throws Exception {
        
        String filteredUserResponse;
        String sSearch_1 = request.getParameter("sSearch_1");
        String search;

        //Sort parameters
        String sCol = request.getParameter("iSortCol_0");
        String sdir = request.getParameter("sSortDir_0");
        
        String colName;
        //Set Search criteria

        filteredUserResponse = notificationPharmacyService.getPatientwiseFilteredMessageHistory(iDisplayStart, iDisplayLength, filter, sEcho, sdir);
        return filteredUserResponse;
    }
    
    @RequestMapping(value = "/messageHistoryListingWs/{filter}", produces = "application/json")
    public @ResponseBody
    String filterRecordListing(@RequestParam int iDisplayStart,
            @RequestParam int iDisplayLength, @RequestParam int sEcho,
            @PathVariable String filter, HttpServletRequest request) throws Exception {
        
        String filteredUserResponse;
        String sSearch_1 = request.getParameter("sSearch_1");
        String search;

        //Sort parameters
        String sCol = request.getParameter("iSortCol_0");
        String sdir = request.getParameter("sSortDir_0");
        
        String colName;
        //Set Search criteria

        filteredUserResponse = notificationPharmacyService.getFilteredMessageHistory(iDisplayStart, iDisplayLength, filter, sEcho, sdir);
        return filteredUserResponse;
    }
    
    @RequestMapping(value = "/viewDependentWs/{id}", method = RequestMethod.GET)
    public ModelAndView viewDependentPageHandler(@PathVariable("id") String id) throws Exception {
        Map<String, String> tableData = new LinkedHashMap<>();
        tableData.put("Name", "fullPatientName");
        tableData.put("DOB", "selectedDob");
        tableData.put("Allergies", "allergies");
        
        String col = new String();
        for (String key : tableData.keySet()) {
            String temp = "  {\n";
            temp += "   \"sTitle\": '" + key + "',\n";
            temp += "   \"mDataProp\": '" + tableData.get(key) + "'\n";
            if (key.equalsIgnoreCase("Allergies")) {
                temp += "   }\n";
            } else {
                temp += "   },\n";
            }
            col = col.concat(temp);
        }
        ModelAndView modelAndView = new ModelAndView("portal/dependentdialog");
        modelAndView.addObject("type", "filterDependentRecordListingWs");
        modelAndView.addObject("col", col);
        modelAndView.addObject("id", id);
        return modelAndView;
    }
    
    @RequestMapping(value = "/filterDependentRecordListingWs/{filter}", produces = "application/json")
    public @ResponseBody
    String filterDependentRecordListing(@RequestParam int iDisplayStart,
            @RequestParam int iDisplayLength, @RequestParam int sEcho,
            @PathVariable Integer filter, HttpServletRequest request) throws Exception {
        
        String filteredUserResponse;
        String sSearch_1 = request.getParameter("sSearch_1");
        String search;

        //Sort parameters
        String sCol = request.getParameter("iSortCol_0");
        String sdir = request.getParameter("sSortDir_0");
        
        String colName;
        //Set Search criteria

        filteredUserResponse = notificationPharmacyService.getFilteredDependentRecordListing(iDisplayStart, iDisplayLength, filter, sEcho);
        return filteredUserResponse;
    }
    
    @RequestMapping(value = "/viewPatientHistoryMessage/{id}", method = RequestMethod.GET)
    public ModelAndView viewPatientHistoryMessage(@PathVariable("id") String id) throws Exception {
        Map<String, String> tableData = new LinkedHashMap<>();
        tableData.put("Sent By", "sentBy");
        tableData.put("Sent On", "dateSent");
        tableData.put("Message", "message");
        
        String col = new String();
        for (String key : tableData.keySet()) {
            String temp = "  {\n";
            temp += "   \"sTitle\": '" + key + "',\n";
            temp += "   \"mDataProp\": '" + tableData.get(key) + "'\n";
            if (key.equalsIgnoreCase("message")) {
                temp += "   }\n";
            } else {
                temp += "   },\n";
            }
            col = col.concat(temp);
        }
        ModelAndView modelAndView = new ModelAndView("portal/viewmessagehistorydialog");
        modelAndView.addObject("type", "patientMessageHistoryListing");
        modelAndView.addObject("col", col);
        modelAndView.addObject("id", id);
        return modelAndView;
    }
    
    @RequestMapping(value = "/patientMessageHistoryListing/{filter}", produces = "application/json")
    public @ResponseBody
    String filterPatientHistoryRecordListing(@RequestParam int iDisplayStart,
            @RequestParam int iDisplayLength, @RequestParam int sEcho,
            @PathVariable String filter, HttpServletRequest request) throws Exception {
        
        String filteredUserResponse;
        String sSearch_1 = request.getParameter("sSearch_1");
        String search;

        //Sort parameters
        String sCol = request.getParameter("iSortCol_0");
        String sdir = request.getParameter("sSortDir_0");
        
        String colName;
        //Set Search criteria

        filteredUserResponse = notificationPharmacyService.getFilteredMessageHistory(iDisplayStart, iDisplayLength, filter, sEcho, sdir);
        JSONObject jsonObject = new JSONObject(filteredUserResponse);
        JSONArray jSONArray = new JSONArray(jsonObject.getString("aaData"));
        if (jSONArray.length() == 0) {
            filteredUserResponse = notificationPharmacyService.getFilteredPatientMessageHistory(iDisplayStart, iDisplayLength, Integer.parseInt(filter), sEcho);
        }
        return filteredUserResponse;
    }
    
    @RequestMapping(value = "/viewProcessedOrdersByPatientIdWs/{id}", method = RequestMethod.GET)
    public ModelAndView viewProcessedOrdersByPatientId(@PathVariable("id") Integer id) throws Exception {
        Map<String, String> tableData = new LinkedHashMap<>();
        tableData.put("RX<br/> NUMBER", "systemGeneratedRxNumber");
        tableData.put("DATE<br/>COMPLETED", "orderDate");
        tableData.put("MEDICATION", "drugName");
        tableData.put("QTY", "qty");
        tableData.put("INS", "selfPayCheck");
        tableData.put("PT OUT OF<br/>PCKT", "finalPaymentStr");
        tableData.put("SERVICE", "deliveryPreferencesName");
        tableData.put("DELIVERY<br/>ZIP CODE", "zipCode");
        tableData.put("RX ORIGIN", "requestType");
        
        String col = new String();
        for (String key : tableData.keySet()) {
            String temp = "  {\n";
            temp += "   \"sTitle\": '" + key + "',\n";
            temp += "   \"mDataProp\": '" + tableData.get(key) + "'\n";
            if (key.equalsIgnoreCase("RX ORIGIN")) {
                temp += "   }\n";
            } else {
                temp += "   },\n";
            }
            col = col.concat(temp);
        }
        ModelAndView modelAndView = new ModelAndView("programRxdialog");
        modelAndView.addObject("type", "processedOrderListByPatientId");
        modelAndView.addObject("col", col);
        modelAndView.addObject("id", id);
        modelAndView.addObject("totalRxProgram", patientProfileService.getProcessedOrdersByPatientId(id));
        return modelAndView;
    }
    
    @RequestMapping(value = "/processedOrderListByPatientId/{filter}", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String processedOrderListByPatientId(@RequestParam int iDisplayStart,
            @RequestParam int iDisplayLength, @RequestParam int sEcho,
            @PathVariable Integer filter, HttpServletRequest request) throws Exception {
        
        String filteredUserResponse;
        String sSearch_1 = request.getParameter("sSearch_1");
        String search;

        //Sort parameters
        String sCol = request.getParameter("iSortCol_0");
        String sdir = request.getParameter("sSortDir_0");
        
        String colName;
        //Set Search criteria

        filteredUserResponse = notificationPharmacyService.getProcessedOrdersByPatientId(iDisplayStart, iDisplayLength, filter, sEcho, sdir);
        return filteredUserResponse;
    }
}
