package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.db.DB;
import javafx.scene.image.Image;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Student extends User {
    ArrayList<Application> applications = new ArrayList<>();
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

    public boolean hasJoined() {
        boolean joined = false;
        try {
            PreparedStatement stmt = DB.stmt("SELECT COUNT(*) AS cnt FROM application WHERE studentId=? AND status=?");
            stmt.setInt(1, userId);
            stmt.setInt(2, Status.ACCEPTED.ordinal());

            ResultSet resultSet = stmt.executeQuery();

            joined = resultSet.getInt("cnt") > 0;

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return joined;
    }

    public boolean hasApplied() {
        boolean applied = false;
        try {
            PreparedStatement stmt = DB.stmt("SELECT COUNT(*) AS cnt FROM application WHERE studentId=? AND status IN (?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, Status.UNDER_REVIEW.ordinal());
            stmt.setInt(3, Status.RECEIVED.ordinal());

            ResultSet resultSet = stmt.executeQuery();

            applied = resultSet.getInt("cnt") > 0;

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return applied;
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public void loadApps() {
        try {
            ArrayList<Integer> appIds = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<Integer> statuses = new ArrayList<>();

            PreparedStatement stmt = DB.stmt("SELECT  application.id AS appId, universityId, status FROM application, university_department WHERE studentId=? AND university_department.id=application.university_depId ORDER BY application.id DESC");
            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int appId = resultSet.getInt("appId");
                int universityId = resultSet.getInt("universityId");
                int status = resultSet.getInt("status");
                appIds.add(appId);
                ids.add(universityId);
                statuses.add(status);
            }

            resultSet.close();
            stmt.close();
            DB.close();

            for (int i = 0; i < ids.size(); i++) {
                int appId = appIds.get(i);
                int id = ids.get(i);
                int status = statuses.get(i);

                Application application = new Application(appId, this, new University(id));
                application.setApplicationStatus(Status.values()[status]);
                applications.add(application);
            }
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public Image getProfile() {
        return new Image("file:" + Constants.UPLOAD_DIR + "/profile_" + username);
    }

    public Image getTranscript() {
        return new Image("file:" + Constants.UPLOAD_DIR + "/doc_" + username);
    }

    public Application getApplication() {
        for (Application application : applications) {
            if (application.getApplicationStatus() == Status.ACCEPTED)
                return application;
        }

        return null;
    }
}
