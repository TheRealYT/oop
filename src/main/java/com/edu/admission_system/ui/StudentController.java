package com.edu.admission_system.ui;

import com.edu.admission_system.classes.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class StudentController extends StageController {
    public ImageView imgProfile;
    public Label txtName;
    public Label txtStudy;
    public Button btnApply;
    public Button btnTransfer;

    private Student student;

    @FXML
    void initialize() {
        txtName.setText("");
        txtStudy.setText("");
    }

    protected void setStudent(Student student) {
        this.student = student;
        imgProfile.setImage(student.getProfile());
        txtName.setText(student.getFullName());

        boolean joined = !true;
        btnApply.setDisable(joined);
        btnTransfer.setDisable(!joined);
        txtStudy.setText("%s • %s".formatted(student.getFieldOfStudy().getName(), joined ? "Addis Ababa University" : "Yet to join university"));
    }

    @FXML
    void preview(MouseEvent event) {
        Util.imagePreview(event, stage);
    }

    @FXML
    void transfer() {

    }

    @FXML
    void apply() {
    }
}