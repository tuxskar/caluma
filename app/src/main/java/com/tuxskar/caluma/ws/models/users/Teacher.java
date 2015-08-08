package com.tuxskar.caluma.ws.models.users;

public class Teacher extends User {
    String dept, description;
    String role = "TEAC";

    public Teacher(String username, String password, String name,
                   String last_name, String email, String dept, String description) {
        super(username, password, name, last_name, email);
        this.dept = dept;
        this.description = description;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
