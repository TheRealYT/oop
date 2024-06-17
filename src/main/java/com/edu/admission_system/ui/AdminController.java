package com.edu.admission_system.ui;

import com.edu.admission_system.classes.University;
import com.edu.admission_system.db.DB;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class AdminController extends StageController {
    public TableView<University> table;
    public TableColumn<University, String> colAbbr;
    public TableColumn<University, String> colName;

    private final ObservableList<University> data = DB.universities();

    @FXML
    void initialize() {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);

        colAbbr.setCellFactory(universityIntegerTableColumn -> new TextFieldTableCell<>(new StringConverter<>() {
            @Override
            public String toString(String s) {
                return s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        }));
        colName.setCellFactory(universityIntegerTableColumn -> new TextFieldTableCell<>(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        }));

        colAbbr.setCellValueFactory(new PropertyValueFactory<>("abbr"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        colAbbr.setOnEditCommit(universityIntegerCellEditEvent -> universityIntegerCellEditEvent.getRowValue().setAbbr(universityIntegerCellEditEvent.getNewValue()));
        colName.setOnEditCommit(universityIntegerCellEditEvent -> universityIntegerCellEditEvent.getRowValue().setName(universityIntegerCellEditEvent.getNewValue()));

        table.setItems(null);
        table.setItems(data);
    }

    @FXML
    void add() {
        data.add(new University(0, "", ""));
        table.requestFocus();
        table.edit(data.size() - 1, table.getColumns().getFirst());
    }

    @FXML
    void delete() {
        University selectedItem = table.getSelectionModel().getSelectedItem();
        data.remove(selectedItem);
    }
}
