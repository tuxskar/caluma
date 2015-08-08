package com.tuxskar.caluma.ws.models.users;


public class Student extends User {
    public static String role = "STUD";

    public Student(String username, String password, String name, String last_name, String email) {
        super(username, password, name, last_name, email);
        role = "STUD";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        Student.role = role;
    }
}
