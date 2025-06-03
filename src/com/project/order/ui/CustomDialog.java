package com.project.order.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.AbstractButton;
import javax.swing.JComponent;

public class CustomDialog extends JDialog {
    public CustomDialog(JFrame owner, String titleText, String messageText) {
        super(owner, true);
        setUndecorated(true);
        RoundedPanel content = new RoundedPanel(20, new Color(242,216,216));
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        title.setForeground(new Color(165,33,33));
        content.add(title, BorderLayout.NORTH);

        JLabel msg = new JLabel("<html><center>"+messageText+"</center></html>", SwingConstants.CENTER);
        msg.setFont(msg.getFont().deriveFont(Font.PLAIN,14f));
        msg.setForeground(Color.DARK_GRAY);
        content.add(msg, BorderLayout.CENTER);

        JButton ok = new JButton("확인");
        ok.setFont(ok.getFont().deriveFont(Font.PLAIN,14f));
        ok.setBackground(Color.BLACK);
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));
        ok.addActionListener(_ -> dispose());
        ok.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override protected void paintButtonPressed(Graphics g, AbstractButton b) {}
            @Override public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton)c;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(b.getBackground());
                g2.fillRoundRect(0,0,b.getWidth(),b.getHeight(),20,20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        JPanel btnBar = new JPanel(); btnBar.setOpaque(false); btnBar.add(ok);
        content.add(btnBar, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        public RoundedPanel(int radius, Color bg) { this.radius=radius; this.bg=bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
