// File: MainFrame.java
package com.project.order.ui;

import com.project.order.service.AdminService;
import com.project.order.service.InventoryService;
import com.project.order.service.OrderLogService;
import com.project.order.service.SalesService;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPane;

    public MainFrame() throws Exception {
        super("Order System");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 공용 서비스 인스턴스들
        AdminService adminSvc     = new AdminService();
        InventoryService invSvc   = new InventoryService();
        OrderLogService logSvc    = new OrderLogService();
        SalesService salesSvc     = new SalesService();

        // CardLayout을 가진 contentPane
        cardLayout  = new CardLayout();
        contentPane = new JPanel(cardLayout);
        setContentPane(contentPane);

        // 1) MAIN 화면: 초기 메뉴 패널
        contentPane.add(createMainMenuPanel(), "MAIN");

        // 2) ORDER 화면: OrderPanel
        OrderPanel orderPanel = new OrderPanel();
        contentPane.add(orderPanel, "ORDER");

        // 3) LOGIN 화면: LoginPanel
        LoginPanel loginPanel = new LoginPanel(
            adminSvc,
            () -> {
                // 로그인 성공 시 MANAGE 화면으로 전환
                cardLayout.show(contentPane, "MANAGE");
            },
            () -> {
                // 뒤로가기 시 MAIN 화면으로 전환
                cardLayout.show(contentPane, "MAIN");
            }
        );
        contentPane.add(loginPanel, "LOGIN");

        // 4) MANAGE 화면: ManagePanel
        ManagePanel managePanel = new ManagePanel(
            () -> {
                // 로그아웃 시 MAIN 화면으로 전환
                cardLayout.show(contentPane, "MAIN");
            }
        );
        contentPane.add(managePanel, "MANAGE");

        // 초기 화면을 MAIN으로 설정
        cardLayout.show(contentPane, "MAIN");
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        // 로고나 아이콘 넣을 수 있는 자리
        JLabel icon = new JLabel();
        URL url = getClass().getResource("/com/project/order/image/Logo.png");
        if (url != null) {
            ImageIcon ico = new ImageIcon(url);
            icon.setIcon(new ImageIcon(ico.getImage().getScaledInstance(120,120, Image.SCALE_SMOOTH)));
        }
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(icon);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel welcome1 = new JLabel("Welcome to");
        welcome1.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcome1);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel welcome2 = new JLabel("Order System");
        welcome2.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcome2);

        panel.add(Box.createVerticalGlue());

        // “Order” 버튼
        RoundedButton orderBtn = new RoundedButton("Order");
        orderBtn.setBackground(Color.BLACK);
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderBtn.setMaximumSize(new Dimension(250, 50));
        orderBtn.addActionListener(e -> {
            cardLayout.show(contentPane, "ORDER");
        });
        panel.add(orderBtn);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // “Manage” 버튼
        RoundedButton manageBtn = new RoundedButton("Manage");
        manageBtn.setBackground(new Color(227, 227, 227));
        manageBtn.setForeground(new Color(177, 177, 177));
        manageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageBtn.setMaximumSize(new Dimension(250, 50));
        manageBtn.addActionListener(e -> {
            cardLayout.show(contentPane, "LOGIN");
        });
        panel.add(manageBtn);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "애플리케이션 실행 중 오류 발생:\n" + ex.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
