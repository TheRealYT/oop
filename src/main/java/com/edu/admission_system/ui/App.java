package com.edu.admission_system.ui;

import com.edu.admission_system.classes.ApplicationManager;
import com.edu.admission_system.classes.Student;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new ManagerStage(new ApplicationManager(1)).show();
//        new LoginStage().show();
//        new StudentStage(new Student(1)).show();
    }

    public static void main(String[] args) {
        launch();
    }
}