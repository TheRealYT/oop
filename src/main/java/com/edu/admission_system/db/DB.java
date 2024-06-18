package com.edu.admission_system.db;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB {
    private static Connection connection = null;
    private static final String DatabaseName = "db.db";

    public static void handleSqlException(SQLException e) {
        System.err.println(e.getMessage());

        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.setTitle("Fetal Error");
        alert.setHeaderText("Fatal Error\n:( Sorry exiting now");
        alert.showAndWait();

        System.exit(1);
    }

    public static void connect() {
        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection("jdbc:sqlite:" + DatabaseName);
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static PreparedStatement stmt(String sql) {
        try {
            connect();
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            handleSqlException(e);
        }

        return null;
    }

    public static void clear() {
        try {
            stmt("DELETE FROM application").executeUpdate();
            stmt("DELETE FROM prerequisite").executeUpdate();
            stmt("DELETE FROM university_department").executeUpdate();
            stmt("DELETE FROM university_program").executeUpdate();
            close();
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static int login(String username, String password, String role) {
        int id = 0;
        PreparedStatement stmt = stmt("SELECT id FROM " + role + " WHERE username=? AND password=?");
        try {
            stmt.setString(1, username);
            stmt.setString(2, password); // TODO: hash

            id = stmt.executeQuery().getInt("id");
            stmt.close();
            close();
        } catch (SQLException e) {
            handleSqlException(e);
        }

        return id;
    }

    public static boolean exists(String fieldName, String fieldValue, String role) {
        PreparedStatement stmt = stmt("SELECT COUNT(*) AS cnt FROM " + role + " WHERE " + fieldName + "=?");
        int cnt = 0;
        try {
            stmt.setString(1, fieldValue);

            cnt = stmt.executeQuery().getInt("cnt");
            stmt.close();
            close();
        } catch (SQLException e) {
            handleSqlException(e);
        }

        return cnt > 0;
    }
}
