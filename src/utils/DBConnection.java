package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    // JDBC URL, username, and password
    private static final String URL = "jdbc:mysql://localhost:3306/bms_db";  // Change to your database URL
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "1234";  // Replace with your MySQL password

    // Method to get the database connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver (if not loaded automatically)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            System.out.println("Database connected successfully.");
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Connection failed. Check output console.");
        }
        return conn;
    }
    
    // Optionally, you can add a method to close the connection:
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

