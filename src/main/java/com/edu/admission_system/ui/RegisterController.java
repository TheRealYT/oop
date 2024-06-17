package com.edu.admission_system.ui;

import com.edu.admission_system.classes.Constants;
import com.edu.admission_system.classes.Department;
import com.edu.admission_system.classes.Student;
import com.edu.admission_system.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Map;

public class RegisterController extends StageController {
    static String[] ImageExt = {"*.png", "*.jpg", "*.jpeg"};

    public TextField txtFullName;
    public TextField txtEUEE;
    public ComboBox<String> fieldOfStudy;
    public Button btnProfile;
    public Button btnDoc;
    public ImageView imgProfile;
    public ImageView imgDoc;

    public TextField txtUsername;
    public TextField txtEmail;
    public PasswordField txtPassword;

    public Label labelErr;
    Map<String, Integer> studyFields = Department.getAll();

    @FXML
    void initialize() {
        labelErr.setText("");
        fieldOfStudy.getItems().addAll(studyFields.keySet());
    }

    @FXML
    void register() {
        labelErr.setText("");

        String fullName = txtFullName.getText(),
                euee = txtEUEE.getText(),
                txtFieldOfStudy = fieldOfStudy.getValue();

        if (fullName.isEmpty() || !fullName.matches("^[A-Z][a-z]+ [A-Z][a-z]+ [A-Z][a-z]+$")) {
            labelErr.setText("Please enter valid name");
            txtFullName.requestFocus();
            return;
        }

        int eueeVal;
        if (euee.isEmpty() || !euee.matches("^[0-9]+$") || (eueeVal = parseInt(euee)) < 100 || eueeVal > 700) {
            labelErr.setText("Please enter valid EUEE result [100-700]");
            txtEUEE.requestFocus();
            return;
        }

        if (txtFieldOfStudy == null || txtFieldOfStudy.isEmpty()) {
            labelErr.setText("Please select field of study");
            return;
        }

        Image profileImg = imgProfile.getImage(),
                docImg = imgDoc.getImage();

        if (profileImg == null) {
            labelErr.setText("Please upload your profile photo");
            btnProfile.requestFocus();
            return;
        }

        if (docImg == null) {
            labelErr.setText("Please upload your transcript photo");
            btnDoc.requestFocus();
            return;
        }

        String username = txtUsername.getText(),
                email = txtEmail.getText(),
                password = txtPassword.getText();

        if (username.isEmpty() || !username.matches("^[A-Za-z]{6,}$")) {
            labelErr.setText("Please enter valid username (min 6)");
            txtUsername.requestFocus();
            return;
        } else if (DB.exists("username", username, Constants.ROLE_STU)) {
            labelErr.setText("Username already taken");
            txtUsername.requestFocus();
            return;
        }

        if (email.isEmpty() || !email.matches("^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]{3,}\\.[A-Za-z]{2,6}$")) {
            labelErr.setText("Please enter valid email");
            txtEmail.requestFocus();
            return;
        } else if (DB.exists("email", email, Constants.ROLE_STU)) {
            labelErr.setText("Email already registered");
            txtEmail.requestFocus();
            return;
        }

        if (password.length() < 8) {
            labelErr.setText("Please enter valid password (min 8)");
            txtPassword.requestFocus();
            return;
        }

        boolean added = Student.add(username, password, fullName, email, studyFields.get(txtFieldOfStudy), eueeVal);

        if (!added) {
            labelErr.setText("Failed to register");
            return;
        }

        if (!saveFile("profile_" + username, profileImg)) {
            labelErr.setText("Failed to upload profile photo");
            return;
        }

        if (!saveFile("doc_" + username, docImg)) {
            labelErr.setText("Failed to upload transcript");
            return;
        }

        new Alert(Alert.AlertType.INFORMATION, "Registered successfully").showAndWait();
        stage.close();
    }

    boolean saveFile(String filename, Image img) {
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        boolean saved = false;

        try {
            String pathname = img.getUrl();
            File file = new File(pathname.substring("file:".length()));
            String extension = ""; // pathname.substring(pathname.lastIndexOf("."));

            outputStream = new FileOutputStream(Constants.UPLOAD_DIR + "/" + filename + extension, false);
            inputStream = new FileInputStream(file);
            outputStream.write(inputStream.readAllBytes());
            saved = true;
        } catch (Exception ignored) {
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception ignored) {
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (Exception ignored) {
            }
        }

        return saved;
    }

    int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception ignored) {
        }

        return 0;
    }

    @FXML
    void preview(MouseEvent event) {
        Util.imagePreview(event, stage);
    }

    @FXML
    void uploadProfile() {
        File file = selectFile("Select Profile Photo");
        if (file == null)
            return;

        imgProfile.setImage(new Image("file:" + file.getAbsolutePath()));
    }

    @FXML
    void uploadTranscript() {
        File file = selectFile("Select Transcript Photo");
        if (file == null)
            return;

        imgDoc.setImage(new Image("file:" + file.getAbsolutePath()));
    }

    File selectFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", ImageExt),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String name = file.getName().toLowerCase();
            for (String ext : ImageExt)
                if (name.endsWith(ext.substring(1)))
                    return file;

            new Alert(Alert.AlertType.ERROR, "Only images are allowed").showAndWait();
        }

        return null;
    }
}