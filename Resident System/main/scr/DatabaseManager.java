// Database management class
// private static final String USER = "root";           // Matches my Workbench user
// private static final String PASS = "R@566432"; // My real password you use for root on SQL
//This section handles database connection and disconnection for MySQL.

package edu.rmit.cosc1295.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Database Connection details 
public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/residentdb";
    private static final String USER = "root";
    private static final String PASS = "R@566432";

    private static Connection connection;

        //Single Shared connection instance 

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println(" Connected to MySQL successfully!");
        }
        return connection;
    }
       //Establishes connection to the MySQL DB if not already done 
       //Load MySQL driver and establish connection with an exception handler if not available properly 


    public static void closeConnection() {
        try { 
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connection closed.");
            } //Exception to show an print the full stack track of an exception to help with debugging
        } catch (SQLException e) {
            e.printStackTrace(); // Prints details of the exception
        }
    }
} //Closes the active database connection safely and shows error if there are any while debugging

