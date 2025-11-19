/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;


import java.sql.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.time.LocalDate;

/**
 *
 * @author anton
 */
public class Patch {
    
    private String patchID;
    private String technicianID;
    private String machineID;
    private String softwareID;
    private String description;
    private String patchName;
    private String status;
    private String type;
    
    public static DefaultTableModel displayRecord(String patchID) {
        DefaultTableModel model = new DefaultTableModel();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT * FROM patch ");
        query.append(" WHERE patchID = ? ");

        try (Connection conn = MySQLConnector.connectDB();
             PreparedStatement statement = conn.prepareStatement(query.toString())) {

            statement.setString(1, patchID);

            // Execute query inside the try block
            try (ResultSet rs = statement.executeQuery()) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Vector<String> columnNames = new Vector<>();

                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }


                model.setColumnIdentifiers(columnNames); 

                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    Vector<Object> rowData = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.add(rs.getObject(i));
                    }
                    model.addRow(rowData);
                }

                System.out.println("Patch Rows found: " + rowCount);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return model;
    }
    
    public static DefaultTableModel displayTable() {
        DefaultTableModel model = new DefaultTableModel();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT * FROM patch ");

        try (Connection conn = MySQLConnector.connectDB();
             PreparedStatement statement = conn.prepareStatement(query.toString())) {


            // Execute query inside the try block
            try (ResultSet rs = statement.executeQuery()) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Vector<String> columnNames = new Vector<>();

                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }


                model.setColumnIdentifiers(columnNames); 

                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    Vector<Object> rowData = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.add(rs.getObject(i));
                    }
                    model.addRow(rowData);
                }

                System.out.println("Patch Rows found: " + rowCount);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return model;
    }
    
   public static DefaultTableModel displayPatchReport(String year, String month) {
        DefaultTableModel model = new DefaultTableModel();
    Vector<String> columnNames = new Vector<>();
    columnNames.add("Patch Name");
    columnNames.add("Patch Type");
    columnNames.add("Success Deployments");
    columnNames.add("Failed/Pending");
    columnNames.add("Success Rate (%)");
    columnNames.add("Fail Rate (%)");
    model.setColumnIdentifiers(columnNames);
    
    int rowCount = 0;
    
    String query = "SELECT p.patchID, p.name AS patchName, p.type AS patchType, " +
                    "SUM(CASE WHEN m.status = 'Done' THEN 1 ELSE 0 END) AS successCount, " +
                    "SUM(CASE WHEN m.status IN ('Not Started', 'In progress') THEN 1 ELSE 0 END) AS failureCount " +
                    "FROM patch p " +
                    "INNER JOIN maintenance m ON p.patchID = m.patchID " +
                    "WHERE YEAR(m.dateAssigned) = ? AND MONTH(m.dateAssigned) = ? " +
                    "GROUP BY p.patchID, p.name, p.type " +
                    "HAVING COUNT(m.maintenanceID) > 0 " + 
                    "ORDER BY p.name";

    try (Connection conn = MySQLConnector.connectDB();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        // 3. Set Year/Month parameters (Using setInt for safety, as YEAR() and MONTH() return integers)
        
        // This line attempts to convert the strings. If the combo box values are clean, it works.
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);
        
        stmt.setInt(1, yearInt);
        stmt.setInt(2, monthInt);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // ... (rest of your result set processing is correct) ...
                String patchName = rs.getString("patchName");
                String patchType = rs.getString("patchType");

                int successCount = rs.getInt("successCount"); 
                int failureCount = rs.getInt("failureCount");
                int total = successCount + failureCount;

                double successRate = 0.0;
                double failureRate = 0.0;

                if (total > 0) {
                    successRate = ((double) successCount / total) * 100;
                    failureRate = ((double) failureCount / total) * 100;
                }

                Vector<Object> rowData = new Vector<>();
                rowData.add(patchName);
                rowData.add(patchType);
                rowData.add(successCount);
                rowData.add(failureCount);
                rowData.add(String.format("%.2f", successRate));
                rowData.add(String.format("%.2f", failureRate));

                model.addRow(rowData);
                rowCount++;
            }
            System.out.println("Patch Report Rows found: " + rowCount);
        }

    } catch (NumberFormatException e) {
        // Catch if year/month were somehow passed as non-numeric strings
        throw new RuntimeException("Invalid number format for year or month parameter.", e);
    } catch (SQLException ex) {
        System.err.println("Error generating report: " + ex.getMessage());
        ex.printStackTrace();
        throw new RuntimeException("Database Error in displayPatchReport: " + ex.getMessage(), ex);
    }

    return model;
   
    }


    
    public static String deletePatch(String patchID) {

        if (patchID.isBlank()) {
            return "Empty";
        } else {
            
            String checkSQL = "SELECT COUNT(*) FROM patch WHERE patchID = ?";
            String updateSQL = "UPDATE patch SET status = 'Inactive' WHERE patchID = ?";
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();
                
                PreparedStatement checkStatement = conn.prepareStatement(checkSQL);
                checkStatement.setString(1, patchID);
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                rs.close();
                checkStatement.close();

                if (count == 0) {
                    conn.close();
                    return "Not Found";
                }
                
                PreparedStatement updateStatement = conn.prepareStatement(updateSQL);
                updateStatement.setString(1, patchID);
                int rowsAffected = updateStatement.executeUpdate();

                updateStatement.close();
               
                if (rowsAffected == 0) {
                    return "Update Failed";
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        }
        return "Valid";
    }
    
    public static String editPatch(String patchID, String technicianID, String softwareID, String machineID, String description, String patchName, String status, String type) {
        // Check for empty fields
        // Check for empty fields
        if (patchID.isBlank() || technicianID.isBlank() || machineID.isBlank() ||
             description.isBlank() || patchName.isBlank() || softwareID.isBlank() ||
             status == null || type == null) {
             return "Empty";
        }

        // Trim values
        status = status.trim();
        type = type.trim();

        // Validate type
        List<String> allowedTypes = Arrays.asList("Application", "System", "Programming", "Network", "Server");
        if (!allowedTypes.contains(type)) return "Invalid Type";

        // Validate status
        List<String> allowedStatuses = Arrays.asList("New", "Working", "Not Working", "Inactive");
        if (!allowedStatuses.contains(status)) return "Invalid Status";

        try (Connection conn = MySQLConnector.connectDB()) {
            // Check if patch exists and get its current status
            String checkSQL = "SELECT status FROM patch WHERE patchID = ?";
            String currentStatus;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                 checkStmt.setString(1, patchID);
                 ResultSet rs = checkStmt.executeQuery();
                 if (!rs.next()) return "Not Found";
                 currentStatus = rs.getString("status");
             }

             // Prevent changing from Inactive to Working/Not Working
             if ("Inactive".equalsIgnoreCase(currentStatus) &&
                 ("Working".equalsIgnoreCase(status) || "Not Working".equalsIgnoreCase(status))) {
                 return "Cannot change status from Inactive to Working/Not Working";
             }

             // Update patch
             String query = "UPDATE patch SET technicianID = ?, machineID = ?, description = ?, name = ?, softwareID = ?, status = ?, type = ? WHERE patchID = ?";
             try (PreparedStatement stmt = conn.prepareStatement(query)) {
                 stmt.setString(1, technicianID);
                 stmt.setString(2, machineID);
                 stmt.setString(3, description);
                 stmt.setString(4, patchName);
                 stmt.setString(5, softwareID);
                 stmt.setString(6, status);
                 stmt.setString(7, type);
                 stmt.setString(8, patchID);

                 int rows = stmt.executeUpdate();
                 if (rows == 0) return "Update Failed";
             }

         } catch (SQLException ex) {
             System.out.println("DEBUG: SQL Exception: " + ex.getMessage());
             return "Invalid";
         }

         return "Success";
    }
    
    public static String activate(String patchID) {
        if (patchID.isBlank()) {
            return "Empty";
        }

        try (Connection conn = MySQLConnector.connectDB()) {

            // 1. Check current status
            String checkSQL = "SELECT status FROM patch WHERE patchID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, patchID);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    return "Not Found";
                }

                String currentStatus = rs.getString("status");

                // 2. If it's already active then block
                if (currentStatus.equals("New") ||
                    currentStatus.equals("Working") ||
                    currentStatus.equals("Not Working")) {
                    return "Already Active";
                }

                // 3. Only allow activation from Inactive
                if (!currentStatus.equals("Inactive")) {
                    return "Invalid";
                }
            }

            // 4. Activate
            String query = "UPDATE patch SET status = 'New' WHERE patchID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, patchID);

                int rows = stmt.executeUpdate();
                return rows > 0 ? "Valid" : "Not Found";
            }

        } catch (SQLException ex) {
            System.out.println("SQL Exception: " + ex.getMessage());
            return "Invalid";
        }
    }
    
    public static ArrayList<String[]> searchPatch(String patchID) {
        ArrayList<String[]> results = new ArrayList<>();
        
        if (patchID.isBlank()) {
            return results; // empty
        }
        
        try {
        Connection conn = MySQLConnector.connectDB();
        
        String query = "SELECT patchID, technicianID, machineID, description, patchName, status, type FROM tester WHERE patchID = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        
        statement.setString(1, patchID);

        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {

            String id = rs.getString("patchID");
            String tech = rs.getString("technicianID");
            String machine = rs.getString("machineID");
            String desc = rs.getString("description");
            String name = rs.getString("patchName");
            String status = rs.getString("status");
            String type = rs.getString("type");

            // Add to results
            results.add(new String[]{id, tech, machine, desc, name, status, type});
        }
        
        rs.close();
        statement.close();
        conn.close();
        
        } catch (Exception Ex) {
            System.out.println("Search error: " + Ex.getMessage());
        }
        return results;
    }
    public static Patch getPatchByID(String patchID) {
         String query = "SELECT * FROM patch WHERE patchID = ?";
    Patch resultPatch = null;

    try (Connection conn = MySQLConnector.connectDB();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, patchID);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            resultPatch = new Patch();
            resultPatch.setPatchID(rs.getString("patchID"));
            resultPatch.setTechnicianID(rs.getString("technicianID"));
            resultPatch.setMachineID(rs.getString("machineID"));
            resultPatch.setDescription(rs.getString("description"));
            resultPatch.setPatchName(rs.getString("name"));
            resultPatch.setSoftwareID(rs.getString("softwareID"));
            resultPatch.setStatus(rs.getString("status"));
            resultPatch.setType(rs.getString("type"));
        }

    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
    }

    return resultPatch;
    }
    
    public static String releasePatch(String patchName, String patchType, String softwareID, String machineID, String technicianID, String description) {
    
        // 1. Basic input validation
        if (patchName.isBlank() || patchType.isBlank() || softwareID.isBlank() ||
            machineID.isBlank() || technicianID.isBlank() || description.isBlank()) {
            return "Empty Fields";
        }

        // 2. Validate patch type
        List<String> allowedPatchTypes = Arrays.asList("Application", "System", "Programming", "Network", "Server");
        if (!allowedPatchTypes.contains(patchType)) {
            return "Invalid Patch Type";
        }

        try {
            Connection conn = MySQLConnector.connectDB();

            // 3. Verify the software exists and get its type
            String softwareQuery = "SELECT type, status FROM software WHERE softwareID = ?";
            PreparedStatement softwareStmt = conn.prepareStatement(softwareQuery);
            softwareStmt.setString(1, softwareID);
            ResultSet rsSoftware = softwareStmt.executeQuery();

            if (!rsSoftware.next()) {
                rsSoftware.close();
                softwareStmt.close();
                conn.close();
                return "Software Not Found";
            }

            String softwareType = rsSoftware.getString("type");
            String softwareStatus = rsSoftware.getString("status");

            if (!softwareStatus.equals("Active")) {
                rsSoftware.close();
                softwareStmt.close();
                conn.close();
                return "Software Not Active";
            }

            // 4. Verify the machine exists, its type, and status
            String machineQuery = "SELECT deviceType, status FROM machines WHERE machineID = ?";
            PreparedStatement machineStmt = conn.prepareStatement(machineQuery);
            machineStmt.setString(1, machineID);
            ResultSet rsMachine = machineStmt.executeQuery();

            if (!rsMachine.next()) {
                rsMachine.close();
                machineStmt.close();
                conn.close();
                return "Machine Not Found";
            }

            String machineType = rsMachine.getString("deviceType");
            String machineStatus = rsMachine.getString("status");

            if (!machineStatus.equals("Vulnerable")) {
                rsMachine.close();
                machineStmt.close();
                conn.close();
                return "Machine Not Ready for Patch";
            }

            // 5. Verify the technician exists, role matches machine, and availability
            String techQuery = "SELECT position, status FROM technicians WHERE technicianID = ?";
            PreparedStatement techStmt = conn.prepareStatement(techQuery);
            techStmt.setString(1, technicianID);
            ResultSet rsTech = techStmt.executeQuery();

            if (!rsTech.next()) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return "Technician Not Found";
            }

            String techPosition = rsTech.getString("position");
            String techStatus = rsTech.getString("status");

            // Role compatibility
            if ((techPosition.equals("Desktop Support") && !machineType.equals("PC")) ||
                (techPosition.equals("Network Admin") && !(machineType.equals("Switch") || machineType.equals("Router"))) ||
                (techPosition.equals("System Admin") && !machineType.equals("Server"))) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return "Technician Role Mismatch";
            }

            // Availability
            if (!techStatus.equals("Available")) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return "Technician Not Available";
            }

            // 6. Check that patch type matches software type
            if (!patchType.equals(softwareType)) {
                conn.close();
                return "Patch Type Does Not Match Software Type";
            }

            // 7. Check software type compatibility with machine type
            if ((softwareType.equals("Application") || softwareType.equals("System") || softwareType.equals("Programming")) && !machineType.equals("PC")) {
                conn.close();
                return "Software Not Compatible with Machine Type";
            } else if (softwareType.equals("Network") && !(machineType.equals("Router") || machineType.equals("Switch"))) {
                conn.close();
                return "Software Not Compatible with Machine Type";
            } else if (softwareType.equals("Server") && !machineType.equals("Server")) {
                conn.close();
                return "Software Not Compatible with Machine Type";
            }

            // 8. Generate next patchID
            String nextPatchID = generateNextPatchID();

            // 9. Get current date for releaseDate
            java.sql.Date releaseDate = new java.sql.Date(System.currentTimeMillis());

            // 10. Insert the patch record with default status "New"
            String insertQuery = "INSERT INTO patch (patchID, name, type, description, releaseDate, status, softwareID, machineID, technicianID) " +
                                 "VALUES (?, ?, ?, ?, ?, 'New', ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, nextPatchID);
            insertStmt.setString(2, patchName);
            insertStmt.setString(3, patchType);
            insertStmt.setString(4, description);
            insertStmt.setDate(5, releaseDate);
            insertStmt.setString(6, softwareID);
            insertStmt.setString(7, machineID);
            insertStmt.setString(8, technicianID);

            int rows = insertStmt.executeUpdate();

            insertStmt.close();
            rsTech.close();
            techStmt.close();
            rsMachine.close();
            machineStmt.close();
            rsSoftware.close();
            softwareStmt.close();
            conn.close();

            return (rows > 0) ? "Valid" : "Insert Failed";

        } catch (SQLException ex) {
            return "Database Error: " + ex.getMessage();
        }
    }
    
    public static String generateNextPatchID() {
        // Default base if there are no records or something goes wrong
        String nextID = "PT0001";

        // SQL to get the latest patchID ordered descending (so the top row is the highest ID)
        String query = "SELECT patchID FROM patch ORDER BY patchID DESC LIMIT 1";

        try {
            // Open DB connection
            Connection conn = MySQLConnector.connectDB();

            // Prepare and execute the select statement
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // If a row exists, rs.next() will be true and we can read the last patchID
            if (rs.next()) {
                String lastID = rs.getString("patchID"); // e.g., "PT0005"

                try {
                    // Remove the "PT" prefix and parse the remaining numeric part
                    String numericPart = lastID.substring(2); // e.g., "0005"
                    int num = Integer.parseInt(numericPart);  // parse to int
                    int nextNum = num + 1;                    // increment
                    nextID = String.format("PT%04d", nextNum);                  // build new ID, e.g., "PT0006"
                } catch (Exception parseEx) {
                    // If parsing fails for any reason (unexpected format), fall back to default nextID
                    System.out.println("ID parse error: " + parseEx.getMessage());
                    nextID = "PT0001";
                }
            }

            // Close result set and statement, then connection
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            // On DB error, print the message and fall back to default nextID
            System.out.println(e.getMessage());
            nextID = "PT0001";
        }

        // Return the computed or fallback patchID
        return nextID;
    }
    
    public String getPatchID() {
        return patchID;
    }
    
    public String getTechnicianID() {
        return technicianID;
    }
    
    public String getMachineID() {
        return machineID;
    }
    
    public String getSoftwareID() {
        return softwareID;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPatchName() {
        return patchName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getType() {
        return type;
    }
    
    public void setPatchID(String patchID) {
        this.patchID = patchID;
    }
    
    public void setSoftwareID(String softwareID) {
        this.softwareID = softwareID;
    }
    public void setTechnicianID(String technicianID) {
        this.technicianID = technicianID;
    }
    
    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPatchName(String patchName) {
        this.patchName = patchName;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
