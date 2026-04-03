package com.example.database;

import com.example.pollutiontracker.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyJDBC {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT username, email FROM registrations ORDER BY id"
             )) {

            while (resultSet.next()) {
                System.out.println(resultSet.getString("username") + " -> " + resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
