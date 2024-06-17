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

    public static Map<String, Integer> getAll(int universityId) {
        Map<String, Integer> programs = new HashMap<>();
        PreparedStatement stmt = DB.stmt("SELECT name, programId FROM program, university_program, university_department WHERE universityId=? AND university_depId=university_department.id AND program.id=programId");
        try {
            stmt.setInt(1, universityId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                programs.put(resultSet.getString("name"), resultSet.getInt("programId"));
            }

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return programs;
    }

    public static Map<String, Integer> getAll(int depId, int universityId) {
        Map<String, Integer> programs = new HashMap<>();
        PreparedStatement stmt = DB.stmt("SELECT name, programId FROM program, university_program, university_department WHERE departmentId=? AND universityId=? AND university_depId=university_department.id AND program.id=university_program.programId");
        try {
            stmt.setInt(1, depId);
            stmt.setInt(2, universityId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                programs.put(resultSet.getString("name"), resultSet.getInt("programId"));
            }

            resultSet.close();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }

        return programs;
    }

    public static void addFor(int programId, int depId, int universityId) {
        PreparedStatement stmt = DB.stmt("INSERT INTO university_program (university_depId, programId) VALUES ((SELECT university_department.id AS university_depId FROM university_department WHERE departmentId=? AND universityId=?), ?)");
        try {
            stmt.setInt(1, depId);
            stmt.setInt(2, universityId);
            stmt.setInt(3, programId);
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    public static void addPreReq(int programId, int reqProgramId, int depId, int universityId) {
        String program1 = "(SELECT university_program.id AS university_programId FROM university_program, university_department WHERE programId=? AND departmentId=? AND universityId=? AND university_depId=university_department.id)";
        String program2 = "(SELECT university_program.id AS university_programId2 FROM university_program, university_department WHERE programId=? AND departmentId=? AND universityId=? AND university_depId=university_department.id)";

        PreparedStatement stmt = DB.stmt("INSERT INTO prerequisite (university_programId, university_programId2) VALUES (" + program1 + ", " + program2 + ")");
        try {
            stmt.setInt(1, programId);
            stmt.setInt(2, depId);
            stmt.setInt(3, universityId);
            stmt.setInt(4, reqProgramId);
            stmt.setInt(5, depId);
            stmt.setInt(6, universityId);
            stmt.executeUpdate();
            stmt.close();
            DB.close();
        } catch (SQLException e) {
            DB.handleSqlException(e);
        }
    }

    private ArrayList<Integer> loadPrerequisites(int universityProgramId) {
        PreparedStatement stmt = DB.stmt("SELECT programId FROM university_program, prerequisite WHERE prerequisite.university_programId=? AND university_program.id=prerequisite.university_programId2");
        ArrayList<Integer> programIds = new ArrayList<>();

        try {
            stmt.setInt(1, universityProgramId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                programIds.add(resultSet.getInt("programId"));
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
