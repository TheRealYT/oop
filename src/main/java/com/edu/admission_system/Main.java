package com.edu.admission_system;

import com.edu.admission_system.classes.ApplicationManager;
import com.edu.admission_system.classes.Student;
import com.edu.admission_system.db.DB;

public class Main {
    public static void main(String[] args) {
        String username = "abe";
        String password = "123";

        DB.connect();
        DB.init();

        Student student = new Student(username, password);
        ApplicationManager manager = new ApplicationManager("admin", password);
        if (manager.authorize()) {
            System.out.println("Login successful: " + manager.getUserId());
        } else
            System.out.println("Incorrect username or password");
    }
}
