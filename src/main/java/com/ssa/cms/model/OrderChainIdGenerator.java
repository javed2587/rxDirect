/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssa.cms.model;

import com.ssa.cms.util.AppUtil;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mzubair
 */
public class OrderChainIdGenerator implements IdentifierGenerator {

    private static final Log logger = LogFactory.getLog(OrderChainIdGenerator.class.getName());

    @Override
    public Serializable generate(SessionImplementor session, Object o) throws HibernateException {
        Long id = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(new Date());
            Connection connection = session.connection();
            long count = 0;
            String sql="SELECT MAX(id) FROM OrderChain WHERE DATE(CreatedOn)=?";
            PreparedStatement ps = connection.prepareStatement(sql);//"SELECT count(*) from OrderChain where Id like ?");
            ps.setString(1, date);// + "%");
            logger.info("Query generated: " + ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
                logger.info("Count: " + count);
            }
            //String result = (count + 1);
//            logger.info("Generated Order Id: " + result);
            String idStr="",nextId="";
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMMdd");
            String dateToAppend = simpleDateFormat2.format(new Date());
            if(count>0)
            {
                idStr=count+"";
                String idPart=idStr.substring(8);
                int nextNum= AppUtil.getSafeInt(idPart, 0)+1;
                nextId=dateToAppend+nextNum;
            }
            else
            {
                nextId=dateToAppend+"1";
            }
            id =AppUtil.getSafeLong(nextId, 0L); //Long.parseLong(result);
            if(id<=0L)
            {
                throw new HibernateException("Unable to generate OrderChainId");
            }
            logger.info("Generated Order Id: " + id);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            logger.error(e);
            throw new HibernateException("Unable to generate Order Chain Id Sequence");
        }
        return id;
    }

}
