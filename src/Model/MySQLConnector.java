/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author marcquizon
 */
public class MySQLConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CCINFOMS24_GROUP1_DB";//TODO: Add url of database
    private static final String USER = "root";//TODO: Add username for database
    private static final String PASSWORD = "m)mmy0604";//TODO: Add password for database
    //edited
     public static Connection connectDB() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    
}
