package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/employee_management";
    private static final String USER = "root";
    private static final String PASSWORD = "MyNewPassword"; // Keep your actual password here
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            // Check if connection is null OR if it has been closed
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
        }
        return connection;
    }
}