package com.edu.admission_system.ui;

import com.edu.admission_system.classes.ApplicationManager;
import com.edu.admission_system.classes.Constants;
import com.edu.admission_system.classes.Student;
import com.edu.admission_system.classes.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController extends StageController {
    public TextField textUsername;
    public PasswordField txtPsw;
    public ComboBox<String> comboRoles;
    public Button btnLogin;

    @FXML
    void initialize() {
        comboRoles.getItems().addAll(Constants.ROLE_STU, Constants.ROLE_MGR);
    }

    @FXML
    void login() throws IOException {
        String role = comboRoles.getValue();
        String username = textUsername.getText();
        String psw = txtPsw.getText();

        if (role == null || role.isEmpty() || username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill out all fields").showAndWait();
            return;
        }

        User user = new User(username, psw, role);
        if (user.authorize()) {
            new Alert(Alert.AlertType.INFORMATION, "Login successful").showAndWait();
            stage.close();

            if (user.getRole().equals(Constants.ROLE_STU)) {
                new StudentStage(new Student(user.getUserId())).show();
            } else if (user.getRole().equals(Constants.ROLE_MGR)) {
                new ManagerStage(new ApplicationManager(user.getUserId())).show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Incorrect username or password").show();
        }
    }

    @FXML
    void register() throws IOException {
        new RegisterStage()
                .setAsDialog(stage)
                .show();
    }
}