/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;
import java.sql.*;
/**
 *
 * @author anton
 */
public class Tester {
    
    private String testerID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String status;
    
//    // Constructor
//    public Tester() {
//         this.testerID = "";
//        this.firstName = "";
//        this.lastName = "";
//        this.email = "";
//        this.password = "";
//        this.status = "";
//    }
    // Fetch all testers from DB
//    public static ArrayList<Tester> getAllTesters() throws SQLException {
//        ArrayList<Tester> testers = new ArrayList<>();
//
//        String url = "jdbc:mysql://localhost:3306/CCINFOMS24_SQL"; // your DB URL
//        String user = "root";
//        String password = "VFXc465vfxC!";
//
//        Connection conn = DriverManager.getConnection(url, user, password);
//        String query = "SELECT testerID, firstName, lastName, email, password, status FROM tester";
//
//        Statement stmt = conn.createStatement();
//        ResultSet rs = stmt.executeQuery(query);
//
//        while (rs.next()) {
//            testers.add(new Tester(
//                rs.getString("testerID"),
//                rs.getString("firstName"),
//                rs.getString("lastName"),
//                rs.getString("email"),
//                rs.getString("password"),
//                rs.getString("status")
//            ));
//        }
//
//        rs.close();
//        stmt.close();
//        conn.close();
//
//        return testers;
//    }

    public static boolean checkEmailDuplicates(String email) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    tester ");
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
    
    public static boolean checkMatchCurrentPassword(String testerID, String passwordFromInput) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  password               ");
        query.append(" FROM    tester ");
        query.append(" WHERE   testerID = ? ");
        Boolean matchResult = false;

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, testerID);

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
    
    public static Tester getTesterByID(String testerID) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    tester ");
        query.append(" WHERE   testerID = ? ");
        Tester resultTester = new Tester();

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, testerID);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//"if (rs.next)" will return true when AT LEAST 1 record is found. Hence, the tester is found
                resultTester.setID(rs.getString("testerID"));
                resultTester.setFirstName(rs.getString("firstName"));
                resultTester.setLastName(rs.getString("lastName"));
                resultTester.setEmail(rs.getString("email"));
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
        return resultTester;
    }
    public static ArrayList<String[]> searchTester(String testerID) {
        ArrayList<String[]> results = new ArrayList<>();
        
        if (testerID.isBlank()) {
            return results; // empty
        }
        
        try {
        Connection conn = MySQLConnector.connectDB();
        
        String query = "SELECT testerID, firstName, lastName, email FROM tester WHERE testerID = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        
        statement.setString(1, testerID);

        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
            String id = rs.getString("testerID");
            String fn = rs.getString("firstName");
            String ln = rs.getString("lastName");
            String email = rs.getString("email");

            String fullName = fn + " " + ln;

            results.add(new String[]{id, fullName, email});
        }
        rs.close();
        statement.close();
        conn.close();
        
        } catch (Exception Ex) {
            System.out.println("Search error: " + Ex.getMessage());
        }
        return results;
    }
    
    public static String addTester(String firstName, String lastName, String email, String password) {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO tester (testerID, firstName, lastName, email, password, status) ");
        query.append(" VALUES (?, ?, ?, ?, ?, 'Active')");

        //TODO - Check for email duplicates
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {//Checker for when the User leaves a Field blank
            return "Empty";
        } else if (checkEmailDuplicates(email) == true) {
            return "Duplicate Email";
        } else {
            String testerID = generateNextTesterID();
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());
                
                statement.setString(1, testerID);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, email);
                statement.setString(5, password);

                statement.executeUpdate();
                
                
                statement.close();
                conn.close();
                return testerID;
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        }
        
    }
    
    public static String deleteTester(String testerID) {
        StringBuilder query = new StringBuilder();
        query.append(" DELETE FROM tester               ");
        query.append(" WHERE   testerID = ? ");

        if (testerID.isBlank()) {
            return "Empty";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                String checkSQL = "SELECT COUNT(*) FROM tester WHERE testerID = ?";
                PreparedStatement checkStatement = conn.prepareStatement(query.toString());

                // replaces ? with testerID itself
                checkStatement.setString(1, testerID);
                
                // executes the query before returning a result set (rows)
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                
                rs.close();
                checkStatement.close();

                // nothing detected so connection is closed and returns not found
                if (count == 0) {
                    conn.close();
                    return "not Found";
                }
                
                 // Soft-delete: update status to Inactive
                   String updateQuery = "UPDATE tester SET status='Inactive' WHERE testerID=?";
                   PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                   
                   updateStmt.setString(1, testerID);
                   int rowsAffected = updateStmt.executeUpdate();
                   
                   updateStmt.close();
                   conn.close();

                // If no rows deleted (shouldn’t happen if count is greater than 0)
                if (rowsAffected == 0) {
                    return "Delete Failed";
                }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                    return "Invalid";
                }
        }
        return "Valid";
    }

    public static String editTester(String testerID, String lastName, String firstName, String email, String currentPassword, String newPassword) {
        StringBuilder query1 = new StringBuilder();//This command is used when the user wants to keep the password the same
        query1.append(" UPDATE tester ");
        query1.append(" SET firstName = ?, lastName = ?, email = ? ");
        query1.append(" WHERE testerID = ? ");
        
        StringBuilder query2 = new StringBuilder();//This command is used when the user want to change their password
        query2.append(" UPDATE tester ");
        query2.append(" SET firstName = ?, lastName = ?, email = ?, password = ? ");
        query2.append(" WHERE testerID = ? "); 

        //Checker for when the User leaves a Field blank
        //We allow the currentPassword and newPassword field to BOTH be blank, but we do not allow ONLY 1 of them to be blank
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()
                || (currentPassword.isBlank() && !newPassword.isBlank()) || (!currentPassword.isBlank() && newPassword.isBlank())) {
            return "Empty";
        } else if (checkEmailDuplicates(email) == true) {//Checker for email duplicates
            return "Duplicate Email";
        } else if (currentPassword.isBlank() && newPassword.isBlank()) {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement1 = conn.prepareStatement(query1.toString());

                statement1.setString(1, firstName);
                statement1.setString(2, lastName);
                statement1.setString(3, email);
                statement1.setString(4, testerID);
                

                statement1.executeUpdate();

                statement1.close();
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        } else if (checkMatchCurrentPassword(testerID, currentPassword) == true) {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement2 = conn.prepareStatement(query2.toString());

                statement2.setString(1, firstName);
                statement2.setString(2, lastName);
                statement2.setString(3, email);
                statement2.setString(4, newPassword);
                statement2.setString(5, testerID);


                statement2.executeUpdate();

                statement2.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        } else if (checkMatchCurrentPassword(testerID, currentPassword) == false){
            return "Wrong password";
        }
        return "Valid";
    }
    
    public static boolean login(String testerID, char[] password) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT password ");
        query.append(" FROM tester ");
        query.append(" WHERE testerID = ? AND status = 'Active' ");

        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, testerID);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String passwordFromDB = rs.getString("password");

                // convert char[] to String for comparison
                String passwordInput = new String(password);

                if (passwordFromDB.equals(passwordInput)) {
                    result = true;
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

    public static String activate(String testerID) {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE tester               ");
        query.append(" SET   status = 'Active' ");
        query.append(" WHERE   testerID = ? ");

        String result = "Invalid";

        if (testerID.isBlank()) {
            result = "Empty";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                statement.setString(1, testerID);
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
    private static String generateNextTesterID() {
        // Default ID if table is empty or something fails
        String nextID = "TS0001";

        // Query to get highest existing tester ID
        String query = "SELECT testerID FROM tester ORDER BY testerID DESC LIMIT 1";

        try {
            // Connect to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare and execute query
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // If table has at least one tester
            if (rs.next()) {
                String lastID = rs.getString("testerID"); // e.g., "TS0033"

                try {
                    // Remove prefix "TS" and get numeric part
                    String numericPart = lastID.substring(2); 
                    int num = Integer.parseInt(numericPart);  
                    int nextNum = num + 1;                    
                    nextID = String.format("TS%04d", nextNum);                 
                } catch (Exception parseEx) {
                    // If format is weird or corrupted, default to TS1001
                    System.out.println("ID parse error: " + parseEx.getMessage());
                    nextID = "TS0001";
                }
            }

            // Cleanup
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            // On SQL error, fallback to TS0001
            System.out.println(e.getMessage());
            nextID = "TS0001";
        }

        return nextID; // Return the final valid ID
    }

    public String getTesterID() {
        return testerID;
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
    
    public String getPassword() {
        return password;
    }
    
    public String getStatus() {
        return status;
    }
    public void setID(String testerID) {
        this.testerID = testerID;
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
    
    public void setPassword(String password) {
        this.password = password;
    }
}
