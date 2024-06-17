package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Program {
    private final int id;
    private String name;
    private final ArrayList<Integer> prerequisiteIds;
    private final ArrayList<Program> prerequisites = new ArrayList<>();

    public Program(int programId, int universityProgramId) {
        this.id = programId;
        PreparedStatement stmt = DB.stmt("SELECT name FROM program WHERE id=?");
        try {
            stmt.setInt(1, programId);
            ResultSet resultSet = stmt.executeQuery();
            name = resultSet.getString("name");

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
        prerequisiteIds = loadPrerequisites(universityProgramId);
    }

    private ArrayList<Integer> loadPrerequisites(int universityProgramId) {
        PreparedStatement stmt = DB.stmt("SELECT university_programId2 FROM university_program, prerequisite WHERE university_program.id=? AND prerequisite.university_programId=?");
        ArrayList<Integer> programIds = new ArrayList<>();

        try {
            stmt.setInt(1, universityProgramId);
            stmt.setInt(2, universityProgramId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                programIds.add(resultSet.getInt("university_programId2"));
            }

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return programIds;
    }

    public static ArrayList<Program> getDepPrograms(int universityId, int departmentId) {
        PreparedStatement stmt = DB.stmt("SELECT programId, university_program.id AS universityProgramId FROM university_department, university_program WHERE universityId=? AND departmentId=? AND university_program.university_depId=university_department.id");
        Map<Integer, Program> programs = new HashMap<>();

        try {
            Map<Integer, Integer> programIds = new HashMap<>();

            stmt.setInt(1, universityId);
            stmt.setInt(2, departmentId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                programIds.put(resultSet.getInt("programId"), resultSet.getInt("universityProgramId"));
            }

            resultSet.close();
            stmt.close();
            DB.close();

            programIds.forEach((programId, universityProgramId) -> {
                programs.put(programId, new Program(programId, universityProgramId));
            });
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        programs.forEach((programId, program) -> {
            for (Integer prerequisiteId : program.prerequisiteIds) {
                if (!programs.containsKey(prerequisiteId)) continue;

                program.prerequisites.add(programs.get(prerequisiteId));
            }
        });

        return new ArrayList<>(programs.values());
    }

    public ArrayList<Program> getPrerequisites() {
        return prerequisites;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
