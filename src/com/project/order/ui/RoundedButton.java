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
        // 기본 패딩 (상하 10, 좌우 20)은 필요에 따라 조절
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

        // 기본 텍스트/아이콘 등은 부모에게 맡겨서 그리도록
        super.paintComponent(g);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // UI가 업데이트될 때마다 불필요한 설정이 초기화될 수 있으므로,
        // 위젯 생성 시점 이후에 다시 아래 옵션을 꺼 줍니다.
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }
}
