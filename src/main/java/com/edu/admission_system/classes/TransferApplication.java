package com.edu.admission_system.classes;

public class TransferApplication extends Application {
    public TransferApplication(Student student, University desiredUniversity) {
        super(student, desiredUniversity);
    }

    @Override
    public void submitApplication() {
        // TODO: submit transfer
    }

    public University getDesiredUniversity() {
        return super.getUniversity();
    }
}
