package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IProgramListing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Department implements IProgramListing {
    private final int id;
    private String name;
    private int availableSlots = -1;
    private ArrayList<Program> programs = null;

    public Department(int depId, int universityId) {
        this.id = depId;
        loadDep();
        loadAvailableSlots(universityId);
        programs = Program.getDepPrograms(universityId, depId);
    }

    public Department(int depId) {
        this.id = depId;
        loadDep();
    }

    public static void addFor(int depId, int universityId, int slots) {
        PreparedStatement stmt = DB.stmt("INSERT INTO university_department (universityId, departmentId, availableSlots) VALUES (?, ?, ?)");
        try {
            stmt.setInt(1, universityId);
            stmt.setInt(2, depId);
            stmt.setInt(3, slots);
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    private void loadDep() {
        PreparedStatement stmt = DB.stmt("SELECT name FROM department WHERE id=?");
        try {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            name = resultSet.getString("name");

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    private void loadAvailableSlots(int universityId) {
        PreparedStatement stmt = DB.stmt("SELECT availableSlots FROM university_department WHERE departmentId=? AND universityId=?");
        try {
            stmt.setInt(1, id);
            stmt.setInt(2, universityId);
            ResultSet resultSet = stmt.executeQuery();
            availableSlots = resultSet.getInt("availableSlots");

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public static Map<String, Integer> getAll() {
        Map<String, Integer> department = new HashMap<>();
        PreparedStatement stmt = DB.stmt("SELECT * FROM department");
        try {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                department.put(resultSet.getString("name"), resultSet.getInt("id"));
            }

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return department;
    }

    public static ArrayList<Department> getUniversityDep(int universityId) {
        PreparedStatement stmt = DB.stmt("SELECT departmentId FROM university_department WHERE universityId=?");
        ArrayList<Department> departments = new ArrayList<>();

        try {
            ArrayList<Integer> programIds = new ArrayList<>();

            stmt.setInt(1, universityId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                programIds.add(resultSet.getInt("departmentId"));
            }

            resultSet.close();
            stmt.close();
            DB.close();

            for (Integer programId : programIds) {
                departments.add(new Department(programId, universityId));
            }
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
        return departments;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public ArrayList<Program> getPrograms() {
        return programs;
    }

    public int getStudentsCount(int universityId) {
        int cnt = 0;
        try {
            PreparedStatement stmt = DB.stmt("SELECT COUNT(studentId) AS cnt FROM university_department, application WHERE universityId=? AND departmentId=? AND status=? AND university_depId = university_department.id GROUP BY studentId");
            stmt.setInt(1, universityId);
            stmt.setInt(2, id);
            stmt.setInt(3, Status.ACCEPTED.ordinal());
            ResultSet resultSet = stmt.executeQuery();
            cnt = resultSet.getInt("cnt");
            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return cnt;
    }
}
