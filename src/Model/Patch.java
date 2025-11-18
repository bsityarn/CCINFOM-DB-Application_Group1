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
    
    public static DefaultTableModel displayPatchReport(int year, int month) {
         DefaultTableModel model = new DefaultTableModel();

        // SQL query for patch success/failure stats
        String query = """
        SELECT name AS patchName,
               type AS patchType,
               COUNT(CASE WHEN status='Working' THEN 1 END) AS successCount,
               COUNT(CASE WHEN status<>'Working' THEN 1 END) AS failureCount,
               ROUND(COUNT(CASE WHEN status='Working' THEN 1 END)/COUNT(*)*100, 2) AS successRate,
               ROUND(COUNT(CASE WHEN status<>'Working' THEN 1 END)/COUNT(*)*100, 2) AS failureRate
        FROM patch
        WHERE YEAR(releaseDate) = ? AND MONTH(releaseDate) = ?
        GROUP BY name, type
        """;


        try (Connection conn = MySQLConnector.connectDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters for year and month
            stmt.setInt(1, year);
            stmt.setInt(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                // Build column headers dynamically
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnLabel(i));
                }
                model.setColumnIdentifiers(columnNames);

                // Fill rows
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
            ex.printStackTrace();
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
    
    public static String editPatch(String patchID, String technicianID, String machineID, String description, String patchName, String status, String type) {
        StringBuilder query = new StringBuilder();//This command is used when the user wants to keep the password the same
        query.append(" UPDATE patch ");
        query.append(" SET technicianID = ?, machineID = ?, description = ?, status = ?, type = ? ");
        query.append(" WHERE patchID = ?");
        

        //Checker for when the User leaves a Field blank
        //We allow the currentPassword and newPassword field to BOTH be blank, but we do not allow ONLY 1 of them to be blank
        if (patchID.isBlank() || technicianID.isBlank() || machineID.isBlank() ||
            description.isBlank() || patchName.isBlank() || status.isBlank() || type.isBlank()) {
            return "Empty";
        }
        
        List<String> allowedTypes = Arrays.asList("Application", "System", "Programming", "Network", "Server");
        if (!allowedTypes.contains(type)) {
            return "Invalid Type";
        }
        
        if (!status.equals("Active") && !status.equals("Inactive")) {
            return "Invalid Status";
        }
        
        // Check if patch exists
        
       
            try {
                // Establish connection to DB
                Connection conn = MySQLConnector.connectDB();

                String checkSQL = "SELECT COUNT(*) FROM patch WHERE patchID = ?";
                PreparedStatement checkStatement = conn.prepareStatement(checkSQL);
                checkStatement.setString(1, patchID);
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                rs.close();
                checkStatement.close();
                
                if (count == 0) {
                    conn.close();
                    return "not Found";
                }
                
                PreparedStatement Statement = conn.prepareStatement(query.toString());
                
                Statement.setString(1, technicianID);
                Statement.setString(2, machineID);
                Statement.setString(3, description);
                Statement.setString(4, patchName);
                Statement.setString(5, status);
                Statement.setString(6, type);
                Statement.setString(7, patchID);
                

                int rowsAffected = Statement.executeUpdate();

                Statement.close();
                conn.close();
                
                if (rowsAffected == 0) {
                    return "Update Failed";
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return "Invalid";
            }
        return "Valid";
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
        StringBuilder query = new StringBuilder();
        query.append(" SELECT  *               ");
        query.append(" FROM    patch ");
        query.append(" WHERE   patchID = ? ");
        Patch resultPatch = new Patch();

        try {
            // Establish connection to DB
            Connection conn = MySQLConnector.connectDB();

            PreparedStatement statement = conn.prepareStatement(query.toString());

            statement.setString(1, patchID);

            // 1. Use executeQuery() and get the ResultSet
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {//"if (rs.next)" will return true when AT LEAST 1 record is found. Hence, the tester is found
                resultPatch.setPatchID(rs.getString("patchID"));
                resultPatch.setTechnicianID(rs.getString("technicianID"));
                resultPatch.setMachineID(rs.getString("machineID"));
                resultPatch.setDescription(rs.getString("description"));
                resultPatch.setPatchName(rs.getString("patchName"));
                resultPatch.setStatus(rs.getString("status")); // should be "Active" or "Inactive"
                resultPatch.setType(rs.getString("type")); // must be one of Application, System, Programming, Network, Server
            } 

            //Closing the connections to avoid DB app slow down in performance
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return resultPatch;
    }
    
    public static boolean releasePatch(String patchName, String patchType, String softwareID, String machineID, String technicianID, String description) {
        
        // 1. Basic input validation
        if (patchName.isBlank() || patchType.isBlank() || softwareID.isBlank() ||
            machineID.isBlank() || technicianID.isBlank() || description.isBlank()) {
            return false;
        }

        // 2. Validate patch type
        List<String> allowedPatchTypes = Arrays.asList("Application", "System", "Programming", "Network", "Server");
        if (!allowedPatchTypes.contains(patchType)) {
            return false;
        }

        try {
            Connection conn = MySQLConnector.connectDB();

            // 3. Verify the software exists and get its type
            String softwareQuery = "SELECT type FROM software WHERE softwareID = ?";
            PreparedStatement softwareStmt = conn.prepareStatement(softwareQuery);
            softwareStmt.setString(1, softwareID);
            ResultSet rsSoftware = softwareStmt.executeQuery();
            
            // 
            if (!rsSoftware.next()) {
                rsSoftware.close();
                softwareStmt.close();
                conn.close();
                return false; // Software does not exist
            }

            String softwareType = rsSoftware.getString("type");
            String softwareStatus = rsSoftware.getString("status");
       
            if (!softwareStatus.equals("Active")) {
                rsSoftware.close();
                softwareStmt.close();
                conn.close();
                return false; // Software not active
            }
    
            rsSoftware.close();
            softwareStmt.close();
            
            // 4. Verify the machine exists, its type, and status
            String machineQuery = "SELECT type, status FROM machine WHERE machineID = ?";
            PreparedStatement machineStmt = conn.prepareStatement(machineQuery);
            machineStmt.setString(1, machineID);
            ResultSet rsMachine = machineStmt.executeQuery();

            if (!rsMachine.next()) {
                rsMachine.close();
                machineStmt.close();
                conn.close();
                return false; // Machine does not exist
            }

            String machineType = rsMachine.getString("type");
            String machineStatus = rsMachine.getString("status");
            
            if (!machineStatus.equals("Vulnerable")) {
                rsMachine.close();
                machineStmt.close();
                conn.close();
                return false; // Machine not ready for patch
            }
            
            rsSoftware.close();
            softwareStmt.close();
            
            // 5. Verify the technician exists, role matches machine, and availability
            String techQuery = "SELECT position, status FROM technicians WHERE technicianID = ?";
            PreparedStatement techStmt = conn.prepareStatement(techQuery);
            techStmt.setString(1, technicianID);
            ResultSet rsTech = techStmt.executeQuery();

            if (!rsTech.next()) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return false; // Technician does not exist
            }

            String techPosition = rsTech.getString("position");
            String techStatus = rsTech.getString("status");
            
            // Check role compatibility
            if ((techPosition.equals("Desktop Support") && !machineType.equals("PC")) ||
                (techPosition.equals("Network Admin") && !(machineType.equals("Switch") || machineType.equals("Router"))) ||
                (techPosition.equals("System Admin") && !machineType.equals("Server"))) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return false; // Technician role mismatch
            }
            
             // Check availability (assuming Available means less than 3 assigned patch works)
            if (!techStatus.equals("Available")) {
                rsTech.close();
                techStmt.close();
                conn.close();
                return false; // Technician not available
            }

            rsTech.close();
            techStmt.close();
            
            // 6. Check that patch type matches software type
            if (!patchType.equals(softwareType)) {
                conn.close();
                return false; // Patch type must match software type
            }

            // 7. Check software type compatibility with machine type
            if (softwareType.equals("Application") || softwareType.equals("System") || softwareType.equals("Programming")) {
                if (!machineType.equals("PC")) {
                    conn.close();
                    return false; // PC-only software
                }
            } else if (softwareType.equals("Network")) {
                if (!machineType.equals("Router") && !machineType.equals("Switch")) {
                    conn.close();
                    return false; // Network software only for Router or Switch
                }
            } else if (softwareType.equals("Server")) {
                if (!machineType.equals("Server")) {
                    conn.close();
                    return false; // Server software only for Server
                }
            }

            // 8. Generate next patchID
            String nextPatchID = generateNextPatchID();
            
            // 9. Get current date for releaseDate
            java.sql.Date releaseDate = new java.sql.Date(System.currentTimeMillis());
            
            // 10. Insert the patch record with default status "New"
            String insertQuery = "INSERT INTO patch (patchID, patchName, type, softwareID, machineID, technicianID, description, status, releaseDate) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, 'New', ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, nextPatchID);
            insertStmt.setString(2, patchName);
            insertStmt.setString(3, patchType);
            insertStmt.setString(4, softwareID);
            insertStmt.setString(5, machineID);
            insertStmt.setString(6, technicianID);
            insertStmt.setString(7, description);
            insertStmt.setDate(8, releaseDate);
             
            int rows = insertStmt.executeUpdate();
            insertStmt.close();
            
            
            conn.close();
            return rows > 0; // true if insert succeeded

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
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
