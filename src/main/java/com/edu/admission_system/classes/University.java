package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IDepartmentListing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class University implements IDepartmentListing {
    private int id;
    private String abbr;
    private String name;

    public University(int id, String abbr, String name) {
        this.id = id;
        this.abbr = abbr;
        this.name = name;
    }

    public University(int universityId) {
        try {
            this.id = universityId;
            PreparedStatement stmt = DB.stmt("SELECT * FROM university WHERE id=?");
            stmt.setInt(1, universityId);

            ResultSet resultSet = stmt.executeQuery();

            name = resultSet.getString("name");
            abbr = resultSet.getString("abbr");

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public static Map<String, Integer> getByDept(int depId) {
        PreparedStatement stmt = DB.stmt("SELECT university.id AS uId, university.name AS uName FROM department, university_department, university WHERE department.id=? AND departmentId=department.id AND university.id=university_department.universityId");
        Map<String, Integer> universityDeps = new HashMap<>();

        try {
            stmt.setInt(1, depId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                universityDeps.put(resultSet.getString("uName"), resultSet.getInt("uId"));
            }

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return universityDeps;
    }

    public int getId() {
        return id;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getName() {
        return name;
    }

    @Override
    public ArrayList<Department> getDepartments() {
        return Department.getUniversityDep(id);
    }
}