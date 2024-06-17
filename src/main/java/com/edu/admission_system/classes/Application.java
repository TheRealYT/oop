package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IApplicationStatus;
import com.edu.admission_system.interfaces.IApplicationSubmission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Application implements IApplicationStatus, IApplicationSubmission {
    private final int id;
    private final Student student;
    private final University university;
    private Status status;

    public Application(int id, Student student, University university) {
        this.id = id;
        this.student = student;
        this.university = university;
    }

    @Override
    public Status getApplicationStatus() {
        return status;
    }

    @Override
    public void setApplicationStatus(Status status) {
        this.status = status;
    }

    @Override
    public void submitApplication() {
        try {
            String q = "(SELECT university_department.id AS university_depId FROM university_department WHERE universityId=? AND departmentId=?)";
            PreparedStatement stmt = DB.stmt("INSERT INTO application (studentId, university_depId, status, transfer) VALUES (?, " + q + ", ?, 0)");
            stmt.setInt(1, student.getUserId());
            stmt.setInt(2, university.getId());
            stmt.setInt(3, student.getFieldOfStudy().getId());
            stmt.setInt(4, Status.RECEIVED.ordinal());
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public University getUniversity() {
        return university;
    }

    public Student getStudent() {
        return student;
    }

    public void saveStatus() {
        PreparedStatement stmt = DB.stmt("UPDATE application SET status=? WHERE id=?");
        try {
            stmt.setInt(1, status.ordinal());
            stmt.setInt(2, id);
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }
}
