package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class University {
    private int id;
    private String abbr;
    private String name;
    private ArrayList<Department> departments;

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

    public ArrayList<Department> getDepartments() {
        return departments = Department.getUniversityDep(id);
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

    public void setId(int id) {
        this.id = id;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public void setName(String name) {
        this.name = name;
    }
}
