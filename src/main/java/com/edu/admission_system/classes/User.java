package com.edu.admission_system.classes;

import com.edu.admission_system.db.DB;
import com.edu.admission_system.interfaces.IUserAuthorization;

public class User implements IUserAuthorization {
    protected int userId;
    protected String username;
    protected String password;
    protected String role;
    protected String email;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean authorize() {
        int userId = DB.login(username, password, role);
        this.userId = userId;
        return userId > 0;
    }
}
