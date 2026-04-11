package com.bi.models;

import com.bi.util.Dataset;

public class User {
    private final int userId;
    private String username;
    private String password;
    private final String role;
    private final String department;

    public User(int userId, String username, String password, String role, String department) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public void viewDashboard() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Dataset runQuery() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void generateReport() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
