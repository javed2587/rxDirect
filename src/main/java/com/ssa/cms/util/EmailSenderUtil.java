/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mzubair
 */
public class EmailSenderUtil {

    private static final Log logger = LogFactory.getLog(EmailSenderUtil.class.getName());

    public static boolean send(String email, String subject, String body) {
        logger.info("Email# "+email);
        String port = PropertiesUtil.getProperty("SMTP_PORT");
        System.out.println("SMTP PORT " + port);
        String host = PropertiesUtil.getProperty("SMTP_HOST");
        System.out.println("SMTP HOST " + host);
        String smtpFEmail = PropertiesUtil.getProperty("SMTP_FROM_EMAIL");
        System.out.println("SMTP_FROM_EMAIL " + smtpFEmail);
        String userName = PropertiesUtil.getProperty("SMTP_USER_NAME");
        System.out.println("SMTP_USER_NAME " + userName);
        String key = PropertiesUtil.getProperty("SMTP_KEY");
        System.out.println("SMTP_KEY " + key);

        boolean flag = true;
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", smtpFEmail);
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
        ArrayList<String> toList = new ArrayList<>();
        toList.add(email);
        ArrayList<String> ccList = new ArrayList<>();
        try {
            body = body.replace("Ã‚", "");
            body = body.replace("Ã", "");

            Message message = new MimeMessage(session);
            InternetAddress fromAddress = new InternetAddress(smtpFEmail, "RxDirect");
            String toAddress = toList.toString();
            String ccAddress = ccList.toString();

            for (String toList1 : toList) {
                toAddress += String.valueOf(toList1);
                toAddress += ",";
            }

            for (String ccList1 : ccList) {
                ccAddress += String.valueOf(ccList1);
                ccAddress += ",";
            }
            message.setSubject(subject);
            message.setFrom(fromAddress);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress));
            message.setContent(body, "text/html; charset=utf-8");
            Transport tr = session.getTransport("smtp");
            tr.connect(host, userName, key);
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException# ", e);
            flag = false;
        } catch (MessagingException ex) {
            logger.error("MessagingException#EmailSenderUtil# ", ex);
            flag = false;
        }
        return flag;
    }
}
