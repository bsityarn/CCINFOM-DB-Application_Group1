/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author marcquizon
 */
public class HelperFunctions {

    public static String incrementID(String tableName) {
        String IDFieldName = "";
        String IDPrefix = "";
        if (tableName.equals("technicians")) {
            IDFieldName = "technicianID";
            IDPrefix = "TH";
        } else if (tableName.equals("machines")) {
            IDFieldName = "machineID";
            IDPrefix = "MC";
        } else if (tableName.equals("software")) {
            IDFieldName = "softwareID";
            IDPrefix = "SF";
        } else if (tableName.equals("tester")) {
            IDFieldName = "testerID";
            IDPrefix = "TS";
        } else if (tableName.equals("patch")) {
            IDFieldName = "patchID";
            IDPrefix = "PT";
        } else if (tableName.equals("maintenance")) {
            IDFieldName = "maintenanceID";
            IDPrefix = "MT";
        } else if (tableName.equals("feedback")) {
            IDFieldName = "feedbackID";
            IDPrefix = "FB";
        } else {
            System.out.println("⚠️ Unknown table name: " + tableName);
            return "Invalid";
        }
        
        String incrementedID = "";
        StringBuilder query = new StringBuilder();
        query.append("SELECT MAX(CAST(SUBSTRING(?, 3) AS UNSIGNED)) AS lastID");
        query.append("FROM ?");
        
        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, IDFieldName);
            statement.setString(2, tableName);


            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//This command will return true when AT LEAST 1 record is found.
                String lastID = rs.getString("lastID");//Issue here, check what lastID returns
                incrementedID = IDPrefix + String.format("%04d", lastID);
                System.out.println(lastID);
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return incrementedID;
    }

}
