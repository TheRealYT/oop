package com.edu.admission_system.ui;

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
import java.util.Map;

public class ManagerController extends StageController {
    public Label txtName;
    public Label txtInfo;
    public TableView<Department> tableDept;

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

        final VBox container = new VBox();
        Accordion accordion = new Accordion();
        container.getChildren().add(accordion);
        container.setPadding(new Insets(10));

        tableDept.setRowFactory(tv -> new TableRow<>() {
            boolean visible = false;

            {
                setOnMouseClicked(mouseEvent -> {
                    visible = !visible;

                    if (visible) {
                        Department department = getItem();
                        if (department == null)
                            return;

                        accordion.getPanes().clear();
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
    }

    @FXML
    void addDepartment() throws IOException {
        new DepartmentStage().setAsDialog(stage).showAndWait();

        Map<String, Integer> all = Department.getAll();
        ArrayList<Department> departments = manager.getUniversity().getDepartments();
        for (Department department : departments)
            all.remove(department.getName());

//        if (all.containsKey(department)) {
//            Department.addFor(all.get(department), manager.getUniversity().getId());
//        }
    }
}