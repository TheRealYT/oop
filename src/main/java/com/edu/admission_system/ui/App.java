package com.edu.admission_system.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new LoginStage().show();
    }

    public static void main(String[] args) {
        launch();
    }
}