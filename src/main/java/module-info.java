module com.edu.admission_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.edu.admission_system to javafx.fxml;
    exports com.edu.admission_system;
    exports com.edu.admission_system.ui;
    opens com.edu.admission_system.ui to javafx.fxml;
    exports com.edu.admission_system.classes;
    opens com.edu.admission_system.classes to javafx.fxml;
}