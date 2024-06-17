package com.edu.admission_system.ui;

import com.edu.admission_system.Status;
import com.edu.admission_system.classes.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class StudentController extends StageController {
    public ImageView imgProfile;
    public Label caption;
    public Label txtName;
    public Label txtStudy;
    public Accordion uList;

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

        student.loadApps();
        txtStudy.setText("%s • %s".formatted(student.getFieldOfStudy().getName(), student.hasJoined() ? student.getApplication().getUniversity().getName() : "Yet to join university"));

        int depId = student.getFieldOfStudy().getId();
        Map<String, Integer> byDept = University.getByDept(depId);
        ArrayList<University> universities = new ArrayList<>();

        uList.getPanes().clear();
        ArrayList<Application> application = student.getApplications();

        for (int id : byDept.values()) {
            University university = new University(id);

            universities.add(university);

            addList(university, application);
        }

        caption.setText("Found " + universities.size() + " universities");
    }

    private void addList(University university, ArrayList<Application> applications) {
        VBox node = new VBox();
        StringBuilder captionText = new StringBuilder();

        Department department = new Department(student.getFieldOfStudy().getId(), university.getId());
        for (Program program : department.getPrograms()) {
            captionText.append("• %s\n".formatted(program.getName()));

            for (Program prerequisite : program.getPrerequisites()) {
                captionText.append("\t→ %s\n".formatted(prerequisite.getName()));
            }
        }

        String title = university.getName();
        Label label = new Label("%s\n\n%s".formatted(department.getName(), captionText.toString()));

        Application application = null;
        for (Application application1 : applications) {
            if (university.getId() == application1.getUniversity().getId()) {
                application = application1;
                break;
            }
        }

        boolean isSame = application != null;
        if (isSame) {
            title += " - " + application.getApplicationStatus().toString();
        }

        node.setPadding(new Insets(10));
        node.setSpacing(10);
        node.getChildren().add(label);

        if (!isSame && !student.hasApplied()) {
            boolean joined = student.hasJoined();
            Button btn = new Button(joined ? "Request Transfer" : "Apply Now");
            btn.setOnMouseClicked(mouseEvent -> {
                Application newApplication = joined ? new TransferApplication(student, university) : new Application(0, student, university);
                Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Continue applying?").showAndWait();

                if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                    newApplication.submitApplication();
                    setStudent(student);
                }
            });
            node.getChildren().add(btn);
        }

        uList.getPanes().add(new TitledPane(title, node));
    }

    @FXML
    void preview(MouseEvent event) {
        Util.imagePreview(event, stage);
    }

    @FXML
    void refresh() {
        setStudent(student);
    }
}