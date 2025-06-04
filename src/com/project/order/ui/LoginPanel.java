package com.project.order.ui;

import com.project.order.service.AdminService;
import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private JTextField idField;
    private JPasswordField pwField;

    public LoginPanel(AdminService adminSvc, Runnable onLoginSuccess, Runnable onBack) {
        setLayout(null);
        setBackground(Color.WHITE);

        // 타이틀 라벨
        JLabel title = new JLabel("관리자 로그인");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBounds(100, 50, 200, 30);
        add(title);

        // ID 입력 라벨 + 필드
        JLabel idLbl = new JLabel("ID:");
        idLbl.setBounds(50, 120, 80, 25);
        add(idLbl);

        idField = new JTextField();
        idField.setBounds(120, 120, 180, 25);
        add(idField);

        // Password 입력 라벨 + 필드
        JLabel pwLbl = new JLabel("Password:");
        pwLbl.setBounds(50, 160, 80, 25);
        add(pwLbl);

        pwField = new JPasswordField();
        pwField.setBounds(120, 160, 180, 25);
        add(pwField);

        // “로그인” 버튼
        RoundedButton loginBtn = new RoundedButton("로그인");
        loginBtn.setBounds(120, 210, 160, 35);
        loginBtn.setBackground(new Color(102, 153, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();

            boolean success = adminSvc.authenticate(id, pw);
            if (success) {
                // 로그인 성공 시 실행할 콜백
                onLoginSuccess.run();
                // 입력 필드 초기화
                idField.setText("");
                pwField.setText("");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "아이디 또는 비밀번호가 올바르지 않습니다.",
                    "로그인 실패",
                    JOptionPane.ERROR_MESSAGE
                );
                idField.setText("");
                pwField.setText("");
            }
        });
        add(loginBtn);

        // “뒤로가기” 버튼
        RoundedButton backBtn = new RoundedButton("←뒤로");
        backBtn.setPreferredSize(new Dimension(100, 50));
        backBtn.setBounds(10, 10, 100, 35);
        backBtn.setBackground(new Color(200, 200, 200));
        backBtn.addActionListener(e -> {
            // 뒤로가기 시 실행할 콜백
            onBack.run();
            // 입력 필드 초기화
            idField.setText("");
            pwField.setText("");
        });
        add(backBtn);
    }
}
