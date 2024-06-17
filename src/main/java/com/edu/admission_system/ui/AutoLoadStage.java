package com.edu.admission_system.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AutoLoadStage extends Stage {
    protected final FXMLLoader fxmlLoader;
    protected final Scene scene;
    protected StageController controller;

    public AutoLoadStage(String uiFile, String title) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(uiFile));
        scene = new Scene(fxmlLoader.load());

        controller = fxmlLoader.getController();
        controller.setStage(this);

        setTitle(title);
        setScene(scene);
    }

    public AutoLoadStage setAsDialog(Stage owner) {
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        return this;
    }
}
