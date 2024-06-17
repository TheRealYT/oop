package com.edu.admission_system.ui;

import com.edu.admission_system.Status;
import com.edu.admission_system.classes.Application;
import com.edu.admission_system.classes.ApplicationManager;
import com.edu.admission_system.classes.Department;
import com.edu.admission_system.classes.Program;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ManagerController extends StageController {
    public Label txtName;
    public Label txtInfo;
    public TableView<Department> tableDept;

    public Button btnAddReq;
    public Button btnAddPro;
    public Accordion list;

    private ApplicationManager manager;

    @FXML
    void initialize() {
        txtName.setText("");
        txtInfo.setText("");
    }

    protected void setManager(ApplicationManager manager) {
        this.manager = manager;

        txtName.setText("@" + manager.getUsername());
        txtInfo.setText(manager.getUniversity().getName());

        tableDept.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tableDept.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("availableSlots"));
        tableDept.getItems().addAll(manager.getUniversity().getDepartments());

        tableDept.setRowFactory(tv -> new TableRow<>() {
            boolean visible = false;
            final VBox container = new VBox();

            {
                Accordion accordion = new Accordion();
                container.getChildren().add(accordion);
                container.setPadding(new Insets(10));

                setOnMouseClicked(mouseEvent -> {
                    visible = !visible;

                    if (visible) {
                        Department department = getItem();
                        if (department == null)
                            return;

                        accordion.getPanes().clear();

                        if (department.getPrograms().isEmpty()) {
                            Label label = new Label("No program added");
                            container.getChildren().clear();
                            container.getChildren().add(label);
                        } else {
                            ArrayList<TitledPane> titledPanes = new ArrayList<>();
                            for (Program program : department.getPrograms()) {
                                VBox node = new VBox();
                                Label label = new Label("");

                                if (program.getPrerequisites().isEmpty()) {
                                    label.setText("No requirements");
                                } else {
                                    label.setFont(new Font(20));
                                    label.setText("Prerequisites");
                                }

                                node.getChildren().add(label);

                                ArrayList<Program> prerequisites = program.getPrerequisites();
                                for (int i = 0; i < prerequisites.size(); i++) {
                                    Program prerequisite = prerequisites.get(i);
                                    node.getChildren().add(new Label((i + 1) + ". " + prerequisite.getName()));
                                }

                                titledPanes.add(new TitledPane(program.getName(), node));
                            }
                            accordion.getPanes().addAll(titledPanes);
                        }

                        getChildren().add(container);
                    } else {
                        getChildren().remove(container);
                    }

                    this.requestLayout();
                });
            }

            @Override
            protected double computePrefHeight(double width) {
                if (visible) {
                    return super.computePrefHeight(width) + container.prefHeight(getWidth());
                } else {
                    return super.computePrefHeight(width);
                }
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (visible) {
                    double width = getWidth();
                    double height = container.prefHeight(getWidth());
                    container.resizeRelocate(0, super.computePrefHeight(width), width, height);
                }
            }
        });

        list.getPanes().clear();
        ArrayList<Application> applications = manager.viewIncomingApplications();
        for (Application application : applications) {
            addList(application);
        }
    }

    @FXML
    void addDepartment() throws IOException {
        new DepartmentStage(manager).setAsDialog(stage).showAndWait();
    }

    @FXML
    void addProgram() throws IOException {
        new DepartmentStage(manager, true).setAsDialog(stage).showAndWait();
    }

    @FXML
    void addReq() throws IOException {
        new DepartmentStage(manager, 0).setAsDialog(stage).showAndWait();
    }

    @FXML
    void refresh() {
        tableDept.getItems().clear();
        tableDept.getItems().addAll(manager.getUniversity().getDepartments());
    }

    private void addList(Application applications) {
        VBox node = new VBox();
        StringBuilder captionText = new StringBuilder();

        Department department = new Department(applications.getStudent().getFieldOfStudy().getId(), applications.getUniversity().getId());
        for (Program program : department.getPrograms()) {
            captionText.append("• %s\n".formatted(program.getName()));

            for (Program prerequisite : program.getPrerequisites()) {
                captionText.append("\t→ %s\n".formatted(prerequisite.getName()));
            }
        }

        String title = applications.getStudent().getFullName() + " (EUEE = " + applications.getStudent().getEuee() + ") " + " - " + applications.getApplicationStatus().toString();
        Label label = new Label("%s\nSlots - %s\nStudents - %s\n\n%s".formatted(department.getName(), department.getAvailableSlots(), department.getStudentsCount(applications.getUniversity().getId()), captionText.toString()));

        node.setPadding(new Insets(10));
        node.setSpacing(10);
        node.getChildren().add(label);

        if (applications.getApplicationStatus() != Status.REJECTED && applications.getApplicationStatus() != Status.ACCEPTED) {
            Button btn = new Button("Commit");
            btn.setOnMouseClicked(mouseEvent -> {
                ChoiceDialog<String> dialog = new ChoiceDialog<>("Select Response");
                dialog.setTitle("Update Application");
                ArrayList<String> strings = new ArrayList<>();
                for (Status value : Status.values()) {
                    if (value == Status.RECEIVED || applications.getApplicationStatus() == Status.UNDER_REVIEW && value == Status.UNDER_REVIEW)
                        continue;
                    strings.add(value.toString());
                }
                strings.add("Auto");

                dialog.getItems().addAll(strings);
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals("Auto"))
                        manager.autoCommitApplication(applications);
                    else
                        manager.commitApplication(applications, Status.valueOf(result.get()));

                    applications.saveStatus();
                    setManager(manager);
                }
            });
            node.getChildren().add(btn);
        }

        list.getPanes().add(new TitledPane(title, node));
    }
}