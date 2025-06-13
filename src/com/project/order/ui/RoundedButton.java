package com.project.order.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private static final long serialVersionUID = 1L;
    private int arcWidth  = 50;
    private int arcHeight = 50;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);  // 기본 사각형 배경을 지움
        setFocusPainted(false);       // 포커스 표시(점선 등)를 지움
        setBorderPainted(false);      // 기본 테두리를 지움
        setOpaque(false);
        setForeground(Color.WHITE);    // 텍스트 색은 흰색으로 예시
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public void setArc(int w, int h) {
        this.arcWidth  = w;
        this.arcHeight = h;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 부드럽게 처리하기 위한 Anti‐Aliasing 설정
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 색 지정 (버튼의 배경색을 getBackground()로 받습니다)
        g2.setColor(getBackground());

        // 둥근 사각형으로 배경을 그림
        Shape round = new RoundRectangle2D.Float(
            0, 0,
            getWidth(), getHeight(),
            arcWidth, arcHeight
        );
        g2.fill(round);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }
}
