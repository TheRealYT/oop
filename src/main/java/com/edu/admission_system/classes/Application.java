package com.edu.admission_system.classes;

import com.edu.admission_system.Status;
import com.edu.admission_system.interfaces.IApplicationStatus;
import com.edu.admission_system.interfaces.IApplicationSubmission;

public class Application implements IApplicationStatus, IApplicationSubmission {
    private final Student student;
    private final University university;
    private Status status;

    public Application(Student student, University university) {
        this.student = student;
        this.university = university;
    }

    @Override
    public Status getApplicationStatus() {
        return status;
    }

    @Override
    public void setApplicationStatus(Status status) {
        this.status = status;
    }

    @Override
    public void submitApplication() {
        // TODO: submit application
    }

    public University getUniversity() {
        return university;
    }

    public Student getStudent() {
        return student;
    }
}
