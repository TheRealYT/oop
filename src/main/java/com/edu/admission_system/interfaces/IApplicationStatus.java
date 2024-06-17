package com.edu.admission_system.interfaces;

import com.edu.admission_system.Status;

public interface IApplicationStatus {
    Status getApplicationStatus();

    void setApplicationStatus(Status status);
}
