/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.*;
/**
 *
 * @author Bernard Llagas
 */
public class Machine {

    private String machineID;
    private String machineName;
    private String deviceType;
    private String status;

    // -----------------------------------------------------
    // CHECKERS
    // -----------------------------------------------------

    public static boolean checkMachineExists(String machineID) {
        String query = "SELECT 1 FROM machines WHERE machineID = ?";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, machineID);

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

    public static boolean checkNameDuplicate(String machineName) {
        String query = "SELECT 1 FROM machines WHERE machineName = ?";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, machineName);

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

    // Checks if a machine has active maintenance records
    public static boolean hasActiveMaintenance(String machineID) {
        String query = "SELECT 1 FROM maintenance WHERE machineID = ? AND status IN ('Not Started', 'In progress')";
        boolean result = false;

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, machineID);

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

    // -----------------------------------------------------
    // ADD MACHINE
    // -----------------------------------------------------

    public static String add(String machineName, String deviceType, String status) {

        if (machineName.isBlank() || deviceType.isBlank() || status.isBlank()) {
            return "Empty";
        }

        if (checkNameDuplicate(machineName)) {
            return "Duplicate Name";
        }

        // Only Healthy/Vulnerable can be manually set
        if (!(status.equals("Healthy") || status.equals("Vulnerable"))) {
            return "Invalid Status";
        }

        String query = "INSERT INTO machines (machineID, machineName, deviceType, status) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);

            String incrementedID = HelperFunctions.incrementID("machines");

            stmt.setString(1, incrementedID);
            stmt.setString(2, machineName);
            stmt.setString(3, deviceType);
            stmt.setString(4, status);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // -----------------------------------------------------
    // EDIT MACHINE
    // -----------------------------------------------------

    public static String edit(String machineID, String machineName, String deviceType, String status) {

        if (!checkMachineExists(machineID)) {
            return "Missing";
        }

        if (machineName.isBlank() || deviceType.isBlank() || status.isBlank()) {
            return "Empty";
        }

        // Only Healthy/Vulnerable can be edited manually
        if (!(status.equals("Healthy") || status.equals("Vulnerable"))) {
            return "Invalid Status";
        }

        // Duplicate name check
        if (checkNameDuplicate(machineName)) {
            String checkQuery = "SELECT machineID FROM machines WHERE machineName = ?";
            try {
                Connection conn = MySQLConnector.connectDB();
                PreparedStatement stmt = conn.prepareStatement(checkQuery);
                stmt.setString(1, machineName);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String duplicateID = rs.getString("machineID");
                    if (!duplicateID.equals(machineID)) return "Duplicate Name";
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        String updateQuery = "UPDATE machines SET machineName = ?, deviceType = ?, status = ? WHERE machineID = ?";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(updateQuery);

            stmt.setString(1, machineName);
            stmt.setString(2, deviceType);
            stmt.setString(3, status);
            stmt.setString(4, machineID);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // -----------------------------------------------------
    // DELETE MACHINE (Soft Delete → set Inactive)
    // -----------------------------------------------------

    public static String delete(String machineID) {

        if (machineID.isBlank()) return "Empty";

        if (!checkMachineExists(machineID)) return "Missing";

        // Business rule: can only remove machine if no active maintenance
        if (hasActiveMaintenance(machineID)) {
            return "Blocked";
        }

        String query = "UPDATE machines SET status = 'Inactive' WHERE machineID = ?";

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, machineID);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Invalid";
        }

        return "Valid";
    }

    // -----------------------------------------------------
    // RETRIEVE MACHINE INFO
    // -----------------------------------------------------

    public static Machine getInfo(String machineID) {

        String query = "SELECT * FROM machines WHERE machineID = ?";
        Machine result = new Machine();

        try {
            Connection conn = MySQLConnector.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, machineID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                result.setMachineID(rs.getString("machineID"));
                result.setMachineName(rs.getString("machineName"));
                result.setDeviceType(rs.getString("deviceType"));
                result.setStatus(rs.getString("status"));
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    // -----------------------------------------------------
    // GETTERS
    // -----------------------------------------------------

    public String getMachineID() { 
        return machineID; 
    }
    public String getMachineName() { 
        return machineName; 
    }
    public String getDeviceType() { 
        return deviceType; 
    }
    public String getStatus() { 
        return status; 
    }

    // -----------------------------------------------------
    // SETTERS
    // -----------------------------------------------------

    public void setMachineID(String machineID) { 
        this.machineID = machineID; 
    }
    public void setMachineName(String machineName) { 
        this.machineName = machineName; 
    }
    public void setDeviceType(String deviceType) { 
        this.deviceType = deviceType; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
}
