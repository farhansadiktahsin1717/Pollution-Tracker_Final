package com.example.pollutiontracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        DatabaseInitializer.initialize();
        return openApplicationConnection();
    }

    static Connection openApplicationConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getJdbcUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

    static Connection openAdminConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getAdminJdbcUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }
}
