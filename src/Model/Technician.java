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
    private String status;

    public static boolean checkEmailDuplicates(String email, String technicianID) {//used when editing a technician
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    technicians ");
        query.append(" WHERE   email = ? AND technicianID != ?");//Exempts the record of the technician we are editing
        Boolean duplicateResult = false;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, email);
            statement.setString(2, technicianID);

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

    public static boolean checkEmailDuplicates(String email) {//used when adding a new technician, no ID yet
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

    public static boolean checkMatchCurrentPassword(String technicianID, String passwordFromInput) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  password               ");
        query.append(" FROM    technicians ");
        query.append(" WHERE   technicianID = ? ");
        Boolean matchResult = false;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, technicianID);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//This command will return true when AT LEAST 1 record is found.
                String passwordFromDB = rs.getString("password");//We get the password from the database
                if (passwordFromDB.equals(passwordFromInput)) {//We compare them and ensure they match
                    matchResult = true;
                }
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return matchResult;
    }

    public static boolean login(String technicianID, char[] password) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    technicians ");
        query.append(" WHERE   technicianID = ? ");
        Boolean result = false;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, technicianID);
            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//"if (rs.next)" will return true when AT LEAST 1 record is found. Hence, the technician is found
                if (rs.getString("password").equals(password)) {//Checks if the right password was inputted
                    result = true;
                } else {
                    result = false;
                }
            }

            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            result = false;
        }
        return result;
    }

    public static String add(String firstName, String lastName, String email, String position, String password) {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO technicians (technicianID, firstName, lastName, position, email, password) ");
        query.append(" VALUES (?, ?, ?, ?, ?, ?)");

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

                String incrementedID = HelperFunctions.incrementID("technicians");
                statement.setString(1, incrementedID);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, position);
                statement.setString(5, email);
                statement.setString(6, password);

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

    public static String edit(String technicianID, String firstName, String lastName, String email, String position, String currentPassword, String newPassword) {
        StringBuilder query1 = new StringBuilder();//This command is used when the user wants to keep the password the same
        query1.append("UPDATE technicians ");
        query1.append("SET firstName = ?, lastName = ?, position = ?, email = ? ");
        query1.append("WHERE technicianID = ?");

        StringBuilder query2 = new StringBuilder();//This command is used when the user want to change their password
        query2.append("UPDATE technicians ");
        query2.append("SET firstName = ?, lastName = ?, position = ?, email = ?, password = ? ");
        query2.append("WHERE technicianID = ?");

        //Checker for when the User leaves a Field blank
        //We allow the currentPassword and newPassword field to BOTH be blank, but we do not allow ONLY 1 of them to be blank
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()
                || (currentPassword.isBlank() && !newPassword.isBlank()) || (!currentPassword.isBlank() && newPassword.isBlank())) {
            return "Empty";
        } else if (checkEmailDuplicates(email, technicianID) == true) {//Checker for email duplicates
            return "Duplicate Email";
        } else if (currentPassword.isBlank() && newPassword.isBlank()) {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement1 = conn.prepareStatement(query1.toString());

                statement1.setString(1, firstName);
                statement1.setString(2, lastName);
                statement1.setString(3, position);
                statement1.setString(4, email);
                statement1.setString(5, technicianID);

                statement1.executeUpdate();

                statement1.close();
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        } else if (checkMatchCurrentPassword(technicianID, currentPassword) == true) {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement2 = conn.prepareStatement(query2.toString());

                statement2.setString(1, firstName);
                statement2.setString(2, lastName);
                statement2.setString(3, position);
                statement2.setString(4, email);
                statement2.setString(5, newPassword);
                statement2.setString(6, technicianID);

                statement2.executeUpdate();

                statement2.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        } else if (checkMatchCurrentPassword(technicianID, currentPassword) == false) {
            return "Wrong password";
        }
        return "Valid";
    }

    public static String delete(String technicianID) {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE technicians               ");
        query.append(" SET   status = 'Inactive' ");
        query.append(" WHERE   technicianID = ? ");

        String result = "Invalid";

        if (technicianID.isBlank()) {
            result = "Empty";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                statement.setString(1, technicianID);
                int rowAffected = statement.executeUpdate();
                System.out.println(rowAffected);

                if (rowAffected == 0) {
                    result = "Missing";
                } else if (rowAffected > 0) {
                    result = "Valid";
                }
                statement.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                result = "Invalid";
            }
        }
        return result;
    }

    public static String activate(String technicianID) {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE technicians               ");
        query.append(" SET   status = 'Available' ");
        query.append(" WHERE   technicianID = ? ");

        String result = "Invalid";

        if (technicianID.isBlank()) {
            result = "Empty";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                statement.setString(1, technicianID);
                int rowAffected = statement.executeUpdate();
                System.out.println(rowAffected);

                if (rowAffected == 0) {
                    result = "Missing";
                } else if (rowAffected > 0) {
                    result = "Valid";
                }
                statement.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                result = "Invalid";
            }
        }
        return result;
    }

    public static Technician getInfo(String technicianID) {
        //TODO edit email part to get the first part and remove @ptrackerdb.com

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
                resultTechnician.setStatus(rs.getString("status"));
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

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

    public String getStatus() {
        return status;
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

    public void setStatus(String status) {
        this.status = status;
    }

}
