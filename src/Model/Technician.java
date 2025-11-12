/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.*;

/**
 *
 * @author marcquizon
 */
public class Technician {

    private String technicianID;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private String currentPassword;
    private String newPassword;

    public static boolean checkEmailDuplicates(String email) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    technicians ");
        query.append(" WHERE   email = ? ");
        Boolean duplicateResult = false;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, email);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//This command will return true when AT LEAST 1 record is found. Hence, duplicate is true
                duplicateResult = true;
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return duplicateResult;
    }

    public static String add(String firstName, String lastName, String email, String position, String password) {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO technician (firstName, lastName, position, email, password) ");
        query.append(" VALUES (?, ?, ?, ?, ?)");

        //TODO - Check for email duplicates
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {//Checker for when the User leaves a Field blank
            return "Empty";
        } else if (checkEmailDuplicates(email) == true) {//Checker for email duplicates
            return "Duplicate Email";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, position);
                statement.setString(4, email);
                statement.setString(5, password);

                statement.executeUpdate();

                statement.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        }
        return "Valid";
    }
    
     public static String edit(String firstName, String lastName, String email, String position, String currentPassword, String newPassword) {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO technician (firstName, lastName, position, email, password) ");
        query.append(" VALUES (?, ?, ?, ?, ?)");

        
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {//Checker for when the User leaves a Field blank
            return "Empty";
        } else if (checkEmailDuplicates(email) == true) {//Checker for email duplicates
            return "Duplicate Email";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, position);
                statement.setString(4, email);
                statement.setString(5, password);

                statement.executeUpdate();

                statement.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        }
        return "Valid";
    }

    public static String delete(String technicianID) {

        if (technicianID.isBlank()) {
            return "Empty";
        }
        return "Valid";
    }

    public static Technician getInfo(String technicianID) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    technicians ");
        query.append(" WHERE   technicianID = ? ");
        Technician resultTechnician = new Technician();

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, technicianID);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//"if (rs.next)" will return true when AT LEAST 1 record is found. Hence, the technician is found
                resultTechnician.setID(rs.getString("technicianID"));
                resultTechnician.setFirstName(rs.getString("firstName"));
                resultTechnician.setLastName(rs.getString("lastName"));
                resultTechnician.setEmail(rs.getString("email"));
                resultTechnician.setPosition(rs.getString("position"));
            } else {//this is the resultTechnician's value when no technician is found
                resultTechnician.setID("Not found");
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        //TODO: Code logic for retrieving a record
        //TODO: Code setting of attributes for technician to be returned
        return resultTechnician;
    }

    // --- Getters ---
    public String getTechnicianID() {
        return technicianID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // --- Setters ---
    public void setID(String technicianID) {
        this.technicianID = technicianID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
