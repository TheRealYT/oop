package com.edu.admission_system.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Util {
    @FXML
    public static void imagePreview(MouseEvent event, Stage stage) {
        ImageView imgView = (ImageView) event.getTarget();
        Image image = imgView.getImage();
        if (image == null) return;

        Scene scene1 = new Scene(new ScrollPane(new ImageView(image)));

        Stage stage1 = new Stage();
        stage1.setTitle("Image Preview");
        stage1.initModality(Modality.WINDOW_MODAL);
        stage1.initOwner(stage);
        stage1.setScene(scene1);
        stage1.setMaximized(true);
        stage1.showAndWait();
    }
}
