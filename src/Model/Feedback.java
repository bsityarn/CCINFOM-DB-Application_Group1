/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Bernard Llagas
 */
public class Feedback {

    private String feedbackID;
    private String testerID;
    private String patchID;
    private String description;
    private int rating;

    // ---------------------------------------------------------
    // CHECKERS
    // ---------------------------------------------------------

    public static boolean checkTesterExists(String testerID) {
        String query = "SELECT 1 FROM tester WHERE testerID = ?";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, testerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) result = true;

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static boolean checkPatchExists(String patchID) {
        String query = "SELECT 1 FROM patch WHERE patchID = ?";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patchID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) result = true;

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static boolean checkPatchHasDoneMaintenance(String patchID) {
        String query = "SELECT 1 FROM maintenance WHERE patchID = ? AND status = 'Done'";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patchID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) result = true;

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static boolean checkFeedbackExists(String feedbackID) {
        String query = "SELECT 1 FROM feedback WHERE feedbackID = ?";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, feedbackID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) result = true;

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    // ---------------------------------------------------------
    // ADD FEEDBACK
    // ---------------------------------------------------------

    public static String add(String testerID, String patchID, String description, int rating) {

        if (testerID.isBlank() || patchID.isBlank() || description.isBlank()) {
            return "Empty";
        }

        if (rating < 1 || rating > 10) {
            return "Invalid Rating";
        }

        if (!checkTesterExists(testerID)) return "Tester Missing";
        if (!checkPatchExists(patchID)) return "Patch Missing";
        if (!checkPatchHasDoneMaintenance(patchID)) return "Patch Not Done";

        String query = "INSERT INTO feedback (feedbackID, testerID, patchID, description, rating) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);

            // ✔ Use your centralized helper
            String nextID = HelperFunctions.incrementID("feedback");

            stmt.setString(1, nextID);
            stmt.setString(2, testerID);
            stmt.setString(3, patchID);
            stmt.setString(4, description);
            stmt.setInt(5, rating);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // ---------------------------------------------------------
    // EDIT FEEDBACK
    // ---------------------------------------------------------

    public static String edit(String feedbackID, String description, int rating) {

        if (feedbackID.isBlank() || description.isBlank()) {
            return "Empty";
        }

        if (rating < 1 || rating > 10) {
            return "Invalid Rating";
        }

        if (!checkFeedbackExists(feedbackID)) return "Missing";

        // Get patchID to revalidate maintenance
        String patchQuery = "SELECT patchID FROM feedback WHERE feedbackID = ?";
        String associatedPatchID = null;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(patchQuery);
            stmt.setString(1, feedbackID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                associatedPatchID = rs.getString("patchID");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        if (associatedPatchID == null) return "Missing";

        if (!checkPatchHasDoneMaintenance(associatedPatchID)) {
            return "Patch Not Done";
        }

        String updateQuery = "UPDATE feedback SET description = ?, rating = ? WHERE feedbackID = ?";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(updateQuery);

            stmt.setString(1, description);
            stmt.setInt(2, rating);
            stmt.setString(3, feedbackID);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // ---------------------------------------------------------
    // DELETE FEEDBACK
    // ---------------------------------------------------------

    public static String delete(String feedbackID) {

        if (feedbackID.isBlank()) {
            return "Empty";
        }

        if (!checkFeedbackExists(feedbackID)) {
            return "Missing";
        }

        String query = "DELETE FROM feedback WHERE feedbackID = ?";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, feedbackID);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // ---------------------------------------------------------
    // GET FEEDBACK INFO
    // ---------------------------------------------------------

    public static Feedback getInfo(String feedbackID) {

        String query = "SELECT * FROM feedback WHERE feedbackID = ?";
        Feedback fb = new Feedback();

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, feedbackID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                fb.setFeedbackID(rs.getString("feedbackID"));
                fb.setTesterID(rs.getString("testerID"));
                fb.setPatchID(rs.getString("patchID"));
                fb.setDescription(rs.getString("description"));
                fb.setRating(rs.getInt("rating"));
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fb;
    }

    // ---------------------------------------------------------
    // SEARCH FEEDBACK (TABLE LOAD)
    // ---------------------------------------------------------

    public static ArrayList<String[]> searchFeedback(String feedbackID) {

        ArrayList<String[]> results = new ArrayList<>();

        if (feedbackID.isBlank()) return results;

        String query = "SELECT feedbackID, testerID, patchID, description, rating FROM feedback WHERE feedbackID = ?";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, feedbackID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new String[]{
                        rs.getString("feedbackID"),
                        rs.getString("testerID"),
                        rs.getString("patchID"),
                        rs.getString("description"),
                        Integer.toString(rs.getInt("rating"))
                });
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

    // ---------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------

    public String getFeedbackID() { return feedbackID; }
    public String getTesterID() { return testerID; }
    public String getPatchID() { return patchID; }
    public String getDescription() { return description; }
    public int getRating() { return rating; }

    public void setFeedbackID(String feedbackID) { this.feedbackID = feedbackID; }
    public void setTesterID(String testerID) { this.testerID = testerID; }
    public void setPatchID(String patchID) { this.patchID = patchID; }
    public void setDescription(String description) { this.description = description; }
    public void setRating(int rating) { this.rating = rating; }
}