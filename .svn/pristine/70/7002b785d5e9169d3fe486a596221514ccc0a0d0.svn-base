/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.util;

import com.ssa.cms.model.IvroutboundQueue;
import com.ssa.cms.model.IvrrequestResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Abid
 */
public class OutboundUtil {
    
    public String prepareFinalOutbooundCallUrl(String url, String phoneNumber, String servoId) {
        try {
            url = url.replaceAll("_phone_to_call_", phoneNumber);
            url = url.replaceAll("_survoId_", servoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    
    public String sendOutboundRequest(String outboundUrl) {
        String responseString = "";
        try {
            URL url = new URL(outboundUrl);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = rd.readLine()) != null) {
                responseString = responseString.concat(line);
            }
            rd.close();
            
        } catch (Exception e) {
            responseString = "";
            e.printStackTrace();
        }
        
        return responseString;
    }
    
    public IvroutboundQueue prepareQueueRecord(IvrrequestResponse outboundReqRes) {
        IvroutboundQueue queue = new IvroutboundQueue();
        queue.setPhoneNumber(outboundReqRes.getPhoneNumber());
        queue.setServoId(outboundReqRes.getServoId());
        queue.setRequestUrl(outboundReqRes.getRequestUrl());
        queue.setSessionId(0);
        queue.setIntervalId(outboundReqRes.getIntervalId());
        queue.setEventDetail(outboundReqRes.getEventDetail());
        queue.setIntervalDescription(outboundReqRes.getIntervalDescription());
        queue.setInputReferenceNumber(outboundReqRes.getInputReferenceNo());
        queue.setMessageContext(outboundReqRes.getMessageContext());
        
        if (outboundReqRes.getIsMobile()) {
            queue.setIsMobile("YES");
        } else {
            queue.setIsMobile("NO");
        }
        if (outboundReqRes.getIsLandLine()) {
            queue.setIsLandLine("YES");
        } else {
            queue.setIsLandLine("NO");
        }
        queue.setCallDailed("NO");
        queue.setOutBoundRequestId((long) 0);
        return queue;
    }
}
