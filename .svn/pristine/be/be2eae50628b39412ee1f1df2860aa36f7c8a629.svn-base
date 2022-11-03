package com.ssa.cms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    public static Connection getCommDBConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://216.139.211.23/o2orxssa_Common", "o2orxssa_comm78u", "dbu4commonDB"); // Produciton Server
            connection = DriverManager.getConnection("jdbc:mysql://engagedmedia.com/engagedm_CommonDB", "engagedm_Common", "GN8G06RET2sS"); // Produciton Server
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        return connection;
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static Connection getRouter21200Connection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://192.168.100.68/Text2ExtremeDB", "root", "root"); // 68 Server
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/Text2ExtremeDB", "root", "root"); // Loal machine
            connection = DriverManager.getConnection("jdbc:mysql://qa.ssasoft.com/Text2ExtremeDB", "root", "passw00rd123Qa"); // QA Server
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        return connection;
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static Connection getRouter37500Connection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://192.168.100.68/o2orxssa_Router37500", "root", "root"); // 68 Server
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/o2orxssa_Router37500", "root", "root"); // Local machine
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/o2orxssa_Router37500", "root", "passw0rd"); // QA Server
            connection = DriverManager.getConnection("jdbc:mysql://216.139.211.23/o2orxssa_Router37500", "o2orxssa_Router3", "Router37500"); //Text2rx server
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        return connection;
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static Connection getRouter55065Connection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://192.168.100.68/o2orxssa_Router55065", "root", "root"); // 68 Server
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/o2orxssa_Router55065", "root", "root"); // Local machine
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/o2orxssa_Router55065", "root", "passw0rd"); // QA Server            
            connection = DriverManager.getConnection("jdbc:mysql://216.139.211.23/o2orxssa_Router55065", "o2orxssa_Ro55065", "router@00!%%0^%"); //Text2rx server
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        return connection;
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static Connection getPAConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://192.168.100.68/o2orxssa_PA", "root", "root"); // Local Linux Machine
            //connection = DriverManager.getConnection("jdbc:mysql://qa.ssasoft.com/o2orxssa_PA", "root", "passw00rd123Qa"); // QA Server
            connection = DriverManager.getConnection("jdbc:mysql://216.139.211.23/o2orxssa_PA", "o2orxssa_pa", "un]yuvJ[W2t1125"); // Production Server

            System.out.println("Opening the PA connection");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        return connection;
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static void closeStatement(Statement preparedStatement) {

        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /*
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************
     */
    public static void closeConnection(Connection connection) {

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
