package com.edu.admission_system.ui;

import com.edu.admission_system.classes.ApplicationManager;

import java.io.IOException;

public class DepartmentStage extends AutoLoadStage {
    public DepartmentStage(ApplicationManager manager) throws IOException {
        super("department.fxml", "Add Department");
        ((DepartmentController) controller).setManager(manager);
    }

    public DepartmentStage(ApplicationManager manager, boolean program) throws IOException {
        super("program.fxml", "Add Program");
        ((DepartmentController) controller).setManager(manager, program);
    }
}
