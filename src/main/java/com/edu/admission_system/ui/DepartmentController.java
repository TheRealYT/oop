package com.edu.admission_system.ui;

import com.edu.admission_system.classes.ApplicationManager;
import com.edu.admission_system.classes.Department;
import com.edu.admission_system.classes.Program;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DepartmentController extends StageController {
    private Map<String, Integer> deps;
    private Map<String, Integer> programs;
    private ApplicationManager manager;

    public ComboBox<String> selectPro;
    public ComboBox<String> selectDept;
    public TextField txtSlots;
    public Button btnAdd;

    public void setManager(ApplicationManager manager) {
        this.manager = manager;
        deps = Department.getAll();
        ArrayList<Department> departments = manager.getUniversity().getDepartments();
        for (Department department : departments)
            deps.remove(department.getName());

        selectDept.getItems().addAll(deps.keySet());
    }

    public void setManager(ApplicationManager manager, boolean program) {
        this.manager = manager;

        deps = new HashMap<>();
        programs = new HashMap<>();

        Map<String, Integer> allDeps = Department.getAll();
        for (Department department : manager.getUniversity().getDepartments())
            if (allDeps.containsKey(department.getName()))
                deps.put(department.getName(), department.getId());

        selectDept.getItems().addAll(deps.keySet());
        selectPro.getItems().addAll(programs.keySet());

        selectDept.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            programs = Program.getAll(manager.getUniversity().getId());

            Map<String, Integer> oldPrograms = Program.getAll(deps.get(newValue), manager.getUniversity().getId());
            oldPrograms.forEach((name, id) -> {
                programs.remove(name);
            });

            selectPro.getItems().clear();
            selectPro.getItems().addAll(programs.keySet());
        });
    }

    @FXML
    void addDept() {
        String department = selectDept.getValue();
        String slots = txtSlots.getText();
        int slotsN;

        if (department == null || slots == null || !slots.matches("^[0-9]+$") || (slotsN = Util.parseInt(slots)) < 5)
            return;

        if (deps.containsKey(department)) {
            Department.addFor(deps.get(department), manager.getUniversity().getId(), slotsN);
        }

        stage.close();
    }

    @FXML
    void addProgram() {
        String department = selectDept.getValue();
        String program = selectPro.getValue();

        if (department == null || program == null)
            return;

        if (deps.containsKey(department) && programs.containsKey(program)) {
            Program.addFor(programs.get(program), deps.get(department), manager.getUniversity().getId());
        }

        stage.close();
    }
}