package com.project.order.ui;

public class Main {
    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new OrderSystemUIFrame().setVisible(true);
            } catch(Exception e) { e.printStackTrace(); }
        });
    }
}