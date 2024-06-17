package com.edu.admission_system.ui;

import com.edu.admission_system.classes.ApplicationManager;

import java.io.IOException;

public class ManagerStage extends AutoLoadStage {
    public ManagerStage(ApplicationManager manager) throws IOException {
        super("manager.fxml", "Welcome, @" + manager.getUsername() + " - Manager");
        ((ManagerController) controller).setManager(manager);

        setMaximized(true);
    }
}
