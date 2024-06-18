package com.edu.admission_system;

import com.edu.admission_system.db.DB;

public class Reset {
    public static void main(String[] args) {
        DB.connect();
        DB.clear();
    }
}
