package com.edu.admission_system.ui;

import com.edu.admission_system.classes.Student;

import java.io.IOException;

public class StudentStage extends AutoLoadStage {
    public StudentStage(Student student) throws IOException {
        super("student.fxml", "Welcome, " + student.getFullName() + " - Student");
        ((StudentController) controller).setStudent(student);

        setMaximized(true);
    }
}
