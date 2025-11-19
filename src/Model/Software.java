/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Alberto Descalzo
 */
public class Software {

    public static boolean insert(String softwareID, String name, String type, String version, String releaseDate) {
        String query = "INSERT INTO software (softwareID, softwareName, type, version, releaseDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setString(4, version);
            stmt.setString(5, releaseDate); // formatted as "YYYY-MM-DD"
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static String generateNewID() {
        String newID = "SF0001";
        String query = "SELECT MAX(softwareID) FROM software";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String maxID = rs.getString(1);
                int num = Integer.parseInt(maxID.substring(2));
                num++;
                newID = String.format("SF%04d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return newID;
    }


    private String softwareID, softwareName, type, version;
    private String releaseDate;


    // Constructors, getters, setters
    public Software() {}

    public Software(String softwareID, String softwareName, String type, String version) {
        this.softwareID = softwareID;
        this.softwareName = softwareName;
        this.type = type;
        this.version = version;
    }

    public String getSoftwareID() { return softwareID; }
    public void setSoftwareID(String softwareID) { this.softwareID = softwareID; }

    public String getSoftwareName() { return softwareName; }
    public void setSoftwareName(String softwareName) { this.softwareName = softwareName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getReleaseDate() { 
    return releaseDate; 
    }

    public void setReleaseDate(String releaseDate) { 
        this.releaseDate = releaseDate; 
    }


    // STATIC SERVICES

    // Add to DB
    public static boolean add(String softwareID, String softwareName, String type, String version) {
        String query = "INSERT INTO software (softwareID, softwareName, type, version) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            stmt.setString(2, softwareName);
            stmt.setString(3, type);
            stmt.setString(4, version);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Delete from DB
    public static boolean delete(String softwareID) {
        String query = "UPDATE software SET status = 'Inactive' WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deactivate(String softwareID) {
        String query = "UPDATE software SET status = 'Inactive' WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean activate(String softwareID) {
        String query = "UPDATE software SET status = 'Active' WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Update in DB
    public static boolean update(String softwareID, String softwareName, String type, String version) {
        String query = "UPDATE software SET softwareName = ?, type = ?, version = ? WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareName);
            stmt.setString(2, type);
            stmt.setString(3, version);
            stmt.setString(4, softwareID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get info for one software
    public static Software getInfo(String softwareID) {
        String query = "SELECT * FROM software WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Software(
                    rs.getString("softwareID"),
                    rs.getString("softwareName"),
                    rs.getString("type"),
                    rs.getString("version")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
        
        //Get status
        public static String getStatus(String softwareID) {
        String query = "SELECT status FROM software WHERE softwareID = ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, softwareID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    public static DefaultTableModel displayRecord(String softwareID) {
        DefaultTableModel model = new DefaultTableModel();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT * FROM software ");
        query.append(" WHERE softwareID = ? ");

        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement statement = conn.prepareStatement(query.toString())) {

            statement.setString(1, softwareID);

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

                System.out.println("Software Rows found: " + rowCount);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return model;
    }

    // Search/list all software whose ID or name matches searchText (for table listing)
    public static ArrayList<Software> search(String searchText) {
        ArrayList<Software> list = new ArrayList<>();
        String query = "SELECT * FROM software WHERE softwareID LIKE ? OR softwareName LIKE ?";
        try (Connection conn = MySQLConnector.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Software s = new Software(
                    rs.getString("softwareID"),
                    rs.getString("softwareName"),
                    rs.getString("type"),
                    rs.getString("version")
                );
                s.setReleaseDate(rs.getString("releaseDate"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
