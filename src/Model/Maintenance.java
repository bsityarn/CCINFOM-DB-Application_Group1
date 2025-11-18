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
public class Maintenance {

    private String maintenanceID;
    private String workType;
    private String patchID;
    private String technicianIDassigned;
    private String dateAssigned;
    private String targetDeadline;
    private String dateFinished;
    private String status;
    private String description;

    //CHECKER for Technician's availability
    public static int checkTechAvailability(String technicianIDassigned) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT COUNT(technicianIDassigned) AS noOfTasks  ");
        query.append(" FROM maintenance ");
        query.append(" WHERE technicianIDassigned = ? AND status != 'Done'");
        int noOfTasks = -1;
        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, technicianIDassigned);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                noOfTasks = rs.getInt("noOfTasks");
            }
            System.out.println("NoOfTasks for Technician " + technicianIDassigned + ":" + " " + noOfTasks);

            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return -1;
        }

        return noOfTasks;
    }

    public static String checkTechCompatibility(String technicianIDassigned, String patchID) {
        StringBuilder query1 = new StringBuilder();
        query1.append(" SELECT position FROM technicians              ");
        query1.append(" WHERE technicianID = ?");
        //Get the position of the Technician assigned

        StringBuilder query2 = new StringBuilder();
        query2.append(" SELECT type FROM patch              ");
        query2.append(" WHERE patchID = ?");
        //Get the type of the Patch Assigned

        String techPosition = "";
        String patchType = "";
        String result = "Invalid";

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement1 = conn.prepareStatement(query1.toString());
            statement1.setString(1, technicianIDassigned);

            PreparedStatement statement2 = conn.prepareStatement(query2.toString());
            statement2.setString(1, patchID);

            ResultSet rsTechnician = statement1.executeQuery();
            ResultSet rsPatch = statement2.executeQuery();

            if (rsTechnician.next()) {
                techPosition = rsTechnician.getString("position");//Retrieve and store technician's postion
            }
            if (rsPatch.next()) {
                patchType = rsPatch.getString("type");//Retrieve and store patch's type
            }

            rsTechnician.close();
            rsPatch.close();
            statement1.close();
            statement2.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "Invalid";
        }

        if (techPosition.equals("Desktop Support")) {
            if (patchType.equals("Application") || patchType.equals("System") || patchType.equals("Programming")) {
                result = "Valid";
            } else {
                result = techPosition + " cannot work with " + patchType + " patch type";
            }
        } else if (techPosition.equals("Network Admin")) {
            if (patchType.equals("Network")) {
                result = "Valid";
            } else {
                result = techPosition + " cannot work with " + patchType + " patch type";
            }
        } else if (techPosition.equals("System Admin")) {
            if (patchType.equals("Server")) {
                result = "Valid";
            } else {
                result = techPosition + " cannot work with " + patchType + " patch type";
            }
        }
        return result;
    }

    public static String checkDupliMaintenance(String technicianIDassigned, String patchID, String workType) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT *  ");
        query.append(" FROM maintenance ");
        query.append(" WHERE technicianIDassigned = ? AND patchID = ? AND workType = ?");
        String result = "Invalid";
        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, technicianIDassigned);
            statement.setString(2, patchID);
            statement.setString(3, workType);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = "Duplicate found";
            }

            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "Invalid";
        }
        return result;
    }

    public static String checkExist(String technicianIDassigned, String patchID) {
        StringBuilder query1 = new StringBuilder();
        query1.append(" SELECT * FROM technicians              ");
        query1.append(" WHERE technicianID = ?");

        StringBuilder query2 = new StringBuilder();
        query2.append(" SELECT * FROM patch              ");
        query2.append(" WHERE patchID = ?");

        Boolean foundTechnician = false;
        Boolean foundPatch = false;
        String result = "";

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement1 = conn.prepareStatement(query1.toString());
            statement1.setString(1, technicianIDassigned);

            PreparedStatement statement2 = conn.prepareStatement(query2.toString());
            statement2.setString(1, patchID);

            ResultSet rsTechnician = statement1.executeQuery();
            ResultSet rsPatch = statement2.executeQuery();

            if (rsTechnician.next()) {
                foundTechnician = true;//Technician exists
            }
            if (rsPatch.next()) {
                foundPatch = true;//Patch exists
            }

            rsTechnician.close();
            rsPatch.close();
            statement1.close();
            statement2.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "Invalid";
        }

        if (foundTechnician && foundPatch) {
            result = "Valid";
        } else {
            result = "Invalid";
        }

        return result;
    }

    public static void updateTechnicianStatus(String technicianIDassigned, String status) {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE technicians  ");
        query.append(" SET status =  ? ");
        query.append(" WHERE technicianID = ?");
        String result = "Invalid";
        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, status);
            statement.setString(2, technicianIDassigned);

            statement.executeUpdate();

            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void updateMachineStatus(String patchID, String status) {
        StringBuilder query1 = new StringBuilder();
        query1.append(" SELECT machineID  ");
        query1.append(" FROM patch ");
        query1.append(" WHERE patchID = ?");

        StringBuilder query2 = new StringBuilder();
        query2.append(" UPDATE machines  ");
        query2.append(" SET status = ? ");
        query2.append(" WHERE machineID = ?");
        String machineID = "";

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement1 = conn.prepareStatement(query1.toString());

            statement1.setString(1, patchID);

            ResultSet rsPatch = statement1.executeQuery();

            if (rsPatch.next()) {
                machineID = rsPatch.getString("machineID");
            }

            statement1.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            // Prepare SQL statement to be executed
            PreparedStatement statement2 = conn.prepareStatement(query2.toString());
            
            statement2.setString(1, status);
            statement2.setString(2, machineID);

            statement2.executeUpdate();

            statement2.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String transac3(String workType, String patchID, String technicianIDassigned,
            String targetDeadline, String description) {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO maintenance               ");
        query.append(" (maintenanceID, workType, patchID, technicianIDassigned, targetDeadline, description)");
        query.append(" VALUES (?, ?, ?, ?, ?, ?)");
        String incrementedID = "";
        String compatibilityResult = checkTechCompatibility(technicianIDassigned, patchID);

        if (workType.isBlank() || patchID.isBlank() || technicianIDassigned.isBlank() || targetDeadline.isBlank() || description.isBlank() || targetDeadline.isBlank()) {//Checker for when the User leaves a Field blank
            return "Empty";
        } else if (checkTechAvailability(technicianIDassigned) >= 3) {//Checker for email duplicates
            return "Unavailable Technician";
        } else if (compatibilityResult != "Valid") {
            return compatibilityResult;
        } else if (checkDupliMaintenance(technicianIDassigned, patchID, workType) == "Duplicate found") {
            return "Duplicate found";
        } else if (checkExist(technicianIDassigned, patchID) == "Invalid") {
            return "Invalid";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement = conn.prepareStatement(query.toString());

                incrementedID = HelperFunctions.incrementID("maintenance");
                statement.setString(1, incrementedID);
                statement.setString(2, workType);
                statement.setString(3, patchID);
                statement.setString(4, technicianIDassigned);
                statement.setString(5, targetDeadline);
                statement.setString(6, description);

                statement.executeUpdate();

                if (checkTechAvailability(technicianIDassigned) >= 3) {
                    //after adding to the Maintenance record, it checks the noOfTasks and updates the technician's status accordingly
                    updateTechnicianStatus(technicianIDassigned, "Unavailable");
                }

                updateMachineStatus(patchID, "Scheduled for patch");

                statement.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        }

        return incrementedID;
    }

    public static String delete(String maintenanceID) {
        StringBuilder query1 = new StringBuilder();
        query1.append(" DELETE FROM maintenance               ");
        query1.append(" WHERE   maintenanceID = ? AND status = 'Done'");
        String result = "Invalid";

        if (maintenanceID.isBlank()) {
            result = "Empty";
        } else {
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                // Prepare SQL statement to be executed
                PreparedStatement statement1 = conn.prepareStatement(query1.toString());

                statement1.setString(1, maintenanceID);
                int rowAffected = statement1.executeUpdate();

                if (rowAffected == 0) {
                    result = "Missing/Wrong Status";
                } else if (rowAffected > 0) {
                    result = "Valid";
                }

                statement1.close();
                conn.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                result = "Invalid";
            }
        }
        return result;
    }

    public static Maintenance getInfo(String maintenanceID) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    maintenance ");
        query.append(" WHERE   maintenanceID = ? ");
        Maintenance resultMaintenance = new Maintenance();

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, maintenanceID);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//"if (rs.next)" will return true when AT LEAST 1 record is found. Hence, the maintenance is found
                resultMaintenance.setMaintenanceID(rs.getString("maintenanceID"));
                resultMaintenance.setWorkType(rs.getString("workType"));
                resultMaintenance.setPatchID(rs.getString("patchID"));
                resultMaintenance.setTechnicianIDassigned(rs.getString("technicianIDassigned"));
                resultMaintenance.setDateAssigned(rs.getString("dateAssigned"));
                resultMaintenance.setTargetDeadline(rs.getString("targetDeadline"));
                resultMaintenance.setDateFinished("dateFinished");
                resultMaintenance.setStatus(rs.getString("status"));
                resultMaintenance.setDescription(rs.getString("description"));
            }

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return resultMaintenance;
    }

    //Getters
    public String getMaintenanceID() {
        return maintenanceID;
    }

    public String getWorkType() {
        return workType;
    }

    public String getPatchID() {
        return patchID;
    }

    public String getTechnicianIDassigned() {
        return technicianIDassigned;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public String getTargetDeadline() {
        return targetDeadline;
    }

    public String getDateFinished() {
        return dateFinished;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    //Setters
    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public void setPatchID(String patchID) {
        this.patchID = patchID;
    }

    public void setTechnicianIDassigned(String technicianIDassigned) {
        this.technicianIDassigned = technicianIDassigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public void setTargetDeadline(String targetDeadline) {
        this.targetDeadline = targetDeadline;
    }

    public void setDateFinished(String dateFinished) {
        this.dateFinished = dateFinished;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
