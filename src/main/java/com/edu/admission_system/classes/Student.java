package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;
import javafx.scene.image.Image;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student extends User {
    private String fullName;
    private Department fieldOfStudy;
    private int euee;

    public Student(String username, String password) {
        super(username, password, Constants.ROLE_STU);
    }

    public Student(int userId) {
        super(userId, Constants.ROLE_STU);
        try {
            PreparedStatement stmt = DB.stmt("SELECT * FROM student WHERE id=?");
            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();

            username = resultSet.getString("username");
            fullName = resultSet.getString("fullName");
            email = resultSet.getString("email");
            euee = resultSet.getInt("euee");
            fieldOfStudy = new Department(resultSet.getInt("fieldOfStudy"));

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

    }

    public static boolean add(String username, String password, String fullName, String email, Integer fieldOfStudy, int eueeVal) {
        try {
            PreparedStatement stmt = DB.stmt("INSERT INTO student (username, password, fullName, email, fieldOfStudy, euee) VALUES (?, ?, ?, ?, ?, ?);");
            stmt.setString(1, username);
            stmt.setString(2, password); // TODO: hash
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            stmt.setInt(5, fieldOfStudy);
            stmt.setInt(6, eueeVal);

            int insert = stmt.executeUpdate();
            stmt.close();
            DB.close();

            return insert > 0;
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return false;
    }

    public String getFullName() {
        return fullName;
    }

    public int getEuee() {
        return euee;
    }

    public Department getFieldOfStudy() {
        return fieldOfStudy;
    }

    public Image getProfile() {
        return new Image("file:" + Constants.UPLOAD_DIR + "/profile_" + username);
    }

    public Image getTranscript() {
        return new Image("file:" + Constants.UPLOAD_DIR + "/doc_" + username);
    }
}
