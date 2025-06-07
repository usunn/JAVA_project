package com.project.order.service;

public class AdminService {
    private static final String ADMIN_ID  = "admin";
    private static final String ADMIN_PW  = "password";

    public boolean authenticate(String id, String pw) {
        if (id == null || pw == null) return false;
        return ADMIN_ID.equals(id.trim()) && ADMIN_PW.equals(pw.trim());
    }
}
