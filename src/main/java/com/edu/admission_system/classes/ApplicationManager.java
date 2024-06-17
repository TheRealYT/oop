package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IApplicationManagement;
import com.edu.admission_system.interfaces.IApplicationStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApplicationManager extends User implements IApplicationManagement {
    private University university;

    public ApplicationManager(String username, String password) {
        super(username, password, Constants.ROLE_MGR);
    }

    public ApplicationManager(int userId) {
        super(userId, Constants.ROLE_MGR);
        try {
            PreparedStatement stmt = DB.stmt("SELECT * FROM manager WHERE id=?");
            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();

            username = resultSet.getString("username");
            email = resultSet.getString("email");
            university = new University(resultSet.getInt("universityId"));

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public University getUniversity() {
        return university;
    }

    @Override
    public ArrayList<Application> viewIncomingApplications() {
        ArrayList<Application> applications = new ArrayList<>();
        int universityId = university.getId();

        try {
            ArrayList<Integer> appIds = new ArrayList<>();
            ArrayList<Integer> stuIds = new ArrayList<>();
            ArrayList<Integer> statuses = new ArrayList<>();

            PreparedStatement stmt = DB.stmt("SELECT application.id AS appId, studentId, status FROM application, university_department WHERE universityId=? AND university_department.id=application.university_depId ORDER BY application.id DESC");
            stmt.setInt(1, universityId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                appIds.add(resultSet.getInt("appId"));
                stuIds.add(resultSet.getInt("studentId"));
                statuses.add(resultSet.getInt("status"));
            }

            resultSet.close();
            stmt.close();
            DB.close();

            for (int i = 0; i < stuIds.size(); i++) {
                int appId = appIds.get(i);
                int stuId = stuIds.get(i);
                int status = statuses.get(i);

                Application application = new Application(appId, new Student(stuId), new University(universityId));
                application.setApplicationStatus(Status.values()[status]);
                applications.add(application);
            }
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return applications;
    }

    @Override
    public void commitApplication(IApplicationStatus application, Status status) {
        application.setApplicationStatus(status);
    }

    @Override
    public void autoCommitApplication(Application application, Department department) {
        if (department.getStudentsCount(application.getUniversity().getId()) < department.getAvailableSlots()) {
            application.setApplicationStatus(Status.ACCEPTED);
        } else {
            application.setApplicationStatus(Status.RECEIVED);
        }
    }
}
