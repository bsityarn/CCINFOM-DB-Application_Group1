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
            System.out.println("Unknown table name: " + tableName);
            return "Invalid";
        }

        String incrementedID = "";
        String query = "SELECT MAX(CAST(SUBSTRING(" + IDFieldName + ", 3) AS UNSIGNED)) AS lastID FROM " + tableName;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();
            
            int lastID = 0;//Constant value for when the table is empty
            if (rs.next()) {//Will run if the machine has records. If empty, it will not run
                lastID = rs.getInt("lastID");//will get the last max ID from the records
            }
            //The increment part of the ID
            incrementedID = IDPrefix.concat(String.format("%04d", lastID + 1));

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
