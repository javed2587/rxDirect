package com.ssa.cms.controller;

import com.ssa.cms.remainder.PMSGenericCouponReminder;
import com.ssa.cms.remainder.PMSGenericEmailCouponReminder;
import com.ssa.cms.remainder.PMSGenericTextFlowReminder;
import com.ssa.cms.remainder.refill.RefillReminderJob;
import com.ssa.cms.remainder.refill.PMSGenericRepeatRefillReminder;
import com.ssa.cms.service.PMSEmailFlowService;
import com.ssa.cms.service.PMSTextFlowService;
import com.ssa.cms.service.RedemptionService;
import com.ssa.cms.service.RefillReminderService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller("/crc.rem")
public class CustomeReminderController extends MultiActionController {

    @Autowired
    private PMSTextFlowService textFlowDAO;
    @Autowired
    private PMSEmailFlowService emailFlowDAO;
    @Autowired
    private RefillReminderService refillReminderDAO;
    @Autowired
    private RedemptionService redemptionDAO;

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("reminders");
        mav.addObject("message", "Provide userid/password select process and click execute button");

        return mav;
    }

    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {

            String userName = request.getParameter("userName");
            String password = request.getParameter("password");
            String remidner = request.getParameter("remidner");

            if (remidner == null) {
                remidner = "Text";
            }

            ModelAndView mav = new ModelAndView("reminders");

            mav.addObject("userName", userName);
            mav.addObject("password", password);
            mav.addObject("message", "Process executed");

            /*
             *********************************************************************************************
             Text Flow Reminders (WM, TCM etc)
             *********************************************************************************************
             */
            if (remidner.equalsIgnoreCase("Text")) {
                PMSGenericTextFlowReminder pmsGenericTextFlowReminder = new PMSGenericTextFlowReminder();

                for (int i = 0; i < 5; i++) {
                    pmsGenericTextFlowReminder.processTextFlowReminder(i, textFlowDAO);
                }
                return mav;
            }

            /*
             *********************************************************************************************
             Text Flow Coupon Reminder
             *********************************************************************************************
             */
            if (remidner.equalsIgnoreCase("Coupon")) {
                PMSGenericCouponReminder couponReminder = new PMSGenericCouponReminder();
                couponReminder.processCouponReminder(textFlowDAO);
                return mav;
            }

            /*
             *********************************************************************************************
             Email Flow Coupon Reminder
             *********************************************************************************************
             */
            if (remidner.equalsIgnoreCase("EmailCoupon")) {
                PMSGenericEmailCouponReminder couponReminder = new PMSGenericEmailCouponReminder();
                couponReminder.processCouponReminder(emailFlowDAO);
                return mav;
            }

            /*
             *********************************************************************************************
             Refill Reminder
             *********************************************************************************************
             */
            if (remidner.equalsIgnoreCase("Refill")) {
                RefillReminderJob refillReminder = new RefillReminderJob();
                refillReminder.sendRefillAlerts(refillReminderDAO);
                return mav;
            }

            /*
             *********************************************************************************************
             Repeat Refill Reminder
             *********************************************************************************************
             */
            if (remidner.equalsIgnoreCase("RepeatRefill")) {
                PMSGenericRepeatRefillReminder repeatRefillReminder = new PMSGenericRepeatRefillReminder();
                repeatRefillReminder.sendRepeatRefillReminder(refillReminderDAO);
                return mav;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
