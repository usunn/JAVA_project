package com.project.order.service;

public class AdminService {
    // 예시: 실제 환경에서는 파일이나 DB에서 꺼내도록 변경 필요
    private static final String ADMIN_ID  = "admin";
    private static final String ADMIN_PW  = "password";

    /**
     * 주어진 아이디·비밀번호가 관리자 계정과 일치하는지 검사한다.
     */
    public boolean authenticate(String id, String pw) {
        if (id == null || pw == null) return false;
        return ADMIN_ID.equals(id.trim()) && ADMIN_PW.equals(pw.trim());
    }
}
