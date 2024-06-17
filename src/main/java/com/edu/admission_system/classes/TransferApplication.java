package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.db.DB;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransferApplication extends Application {
    public TransferApplication(Student student, University desiredUniversity) {
        super(student, desiredUniversity);
    }

    @Override
    public void submitApplication() {
        try {
            String q = "(SELECT university_department.id AS university_depId FROM university_department WHERE universityId=? AND departmentId=?)";
            PreparedStatement stmt = DB.stmt("INSERT INTO application (studentId, university_depId, status, transfer) VALUES (?, " + q + ", ?, 1)");
            stmt.setInt(1, getStudent().getUserId());
            stmt.setInt(2, getUniversity().getId());
            stmt.setInt(3, getStudent().getFieldOfStudy().getId());
            stmt.setInt(4, Status.RECEIVED.ordinal());
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public University getDesiredUniversity() {
        return super.getUniversity();
    }
}
