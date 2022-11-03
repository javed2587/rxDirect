/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.util;

import com.ssa.cms.controller.ConsumerRegistrationController;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author arsalan.ahmad
 */
public class PropertiesUtil {
    
    private final static Log logger = LogFactory.getLog(PropertiesUtil.class);
    
    private static Properties load(){
        Properties prop = new Properties();
        try{
            InputStream inputStream = PropertiesUtil.class.getResourceAsStream("/application.properties");
            prop.load(inputStream);
        }catch(IOException ioe){
            System.err.println(ioe.getStackTrace());
            logger.error(ioe.getMessage());
        }
        return prop;
    }
    
    public static String getProperty(String name){
        try
        {
        System.out.println("Reading Property ############################# " +load().getProperty(name));
        return load().getProperty(name);
        }
        catch(Exception e)
        {
            
        }
        return "";
    }
    
}
