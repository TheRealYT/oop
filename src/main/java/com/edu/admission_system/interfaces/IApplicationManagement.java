package com.edu.admission_system.interfaces;

import com.edu.admission_system.Status;
import com.edu.admission_system.classes.Application;
import com.edu.admission_system.classes.Department;

import java.util.ArrayList;

public interface IApplicationManagement {
    ArrayList<Application> viewIncomingApplications();

    void commitApplication(IApplicationStatus application, Status status);

    void autoCommitApplication(Application application, Department department);
}