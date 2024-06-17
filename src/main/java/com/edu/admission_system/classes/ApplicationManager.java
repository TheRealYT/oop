package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IApplicationManagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
