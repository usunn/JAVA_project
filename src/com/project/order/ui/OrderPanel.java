// File: OrderPanel.java
package com.project.order.ui;

import javax.swing.*;
import javax.swing.table.*;

import com.project.order.model.Menu;
import com.project.order.service.MenuService;
import com.project.order.service.OrderService;
import com.project.order.service.RecommendService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class OrderPanel extends JPanel {
    private MenuService menuSvc;
    private List<Menu> menus;
    private Map<Menu, Integer> cart = new LinkedHashMap<>();

    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JLabel totalLbl;

    public OrderPanel() throws Exception {
        menuSvc = new MenuService();
        menus = menuSvc.getAll();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));

        // 상단: 추천 메뉴 버튼
        add(createRecommendPanel(() -> {
            // 뒤로가기 버튼 클릭 시 메인 화면으로 돌아가기
            Container parent = this.getParent();
            if (parent instanceof JPanel && parent.getLayout() instanceof CardLayout) {
                CardLayout cl = (CardLayout) parent.getLayout();
                cl.show(parent, "MAIN"); 
            }
        }), BorderLayout.NORTH);


        // 중앙: 메뉴 리스트
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        for (Menu m : menus) {
            listPanel.add(itemComp(m));
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        JScrollPane listScroll = new JScrollPane(listPanel);
        listScroll.getVerticalScrollBar().setUnitIncrement(16);
        listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(listScroll, BorderLayout.CENTER);

        // 하단: 장바구니 + 결제
        JPanel bottom = new JPanel(null);
        bottom.setBackground(Color.WHITE);
        bottom.setPreferredSize(new Dimension(360, 240));

        cartModel = new DefaultTableModel(new Object[]{"메뉴명", "수량", "금액", ""}, 0);
        cartTable = new JTable(cartModel);
        cartTable.setShowGrid(false);
        cartTable.setRowHeight(25);

        // “삭제” 버튼 렌더러/에디터
        TableColumn col = cartTable.getColumnModel().getColumn(3);
        col.setCellRenderer(new ButtonRenderer());
        col.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBounds(0, 0, 360, 100);
        bottom.add(cartScroll);

        totalLbl = new JLabel("합계: 0 원");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLbl.setBounds(200, 110, 160, 30);
        bottom.add(totalLbl);

        RoundedButton checkout = new RoundedButton("결제하기");
        checkout.setPreferredSize(new Dimension(250, 40));
        checkout.setBackground(Color.black);
        checkout.setBounds(50, 150, 250, 40);
        checkout.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "메뉴를 선택 후 결제해주세요", "경고!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showOrderSummary();
        });
        bottom.add(checkout);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createRecommendPanel(Runnable onBack) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(360, 40));

        // ← 뒤로가기 버튼 (WEST)
        RoundedButton backBtn = new RoundedButton("←뒤로");
        backBtn.setPreferredSize(new Dimension(100, 40));
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.setBackground(new Color(200, 200, 200));
        backBtn.addActionListener(e -> onBack.run());
        p.add(backBtn, BorderLayout.WEST);

        RoundedButton btn = new RoundedButton("오늘의 추천 메뉴");
        btn.setPreferredSize(new Dimension(170, 40));
        btn.setBounds(10, 10, 80, 30);
        btn.setBackground(new Color(102, 153, 255));
    
        btn.addActionListener(e -> {
            try {
                RecommendService recommender = new RecommendService();
                Menu rec = recommender.getRecommend(menuSvc.getAll());
                if (rec != null) {
                    JOptionPane.showMessageDialog(this,
                        "오늘의 추천 메뉴는 " + rec.getName() + " 입니다.",
                        "추천 메뉴", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "추천 가능한 메뉴가 부족합니다.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "추천 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(btn, BorderLayout.EAST);

        return p;
    }


    private JPanel itemComp(Menu m) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.setPreferredSize(new Dimension(360, 80));

        // 이미지 또는 기본 배경
        JLabel img = new JLabel();
        URL imgUrl = getClass().getResource("/com/project/order/image/" + m.getName() + ".png");

        if (imgUrl != null) {
            ImageIcon ico = new ImageIcon(imgUrl);
            img.setIcon(new ImageIcon(ico.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else {
            img = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(220, 240, 220));
                    g.fillOval(0, 0, getWidth(), getHeight());
                }
            };
        }
        img.setBounds(10, 10, 60, 60);
        p.add(img);

        JLabel name = new JLabel(m.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setBounds(80, 10, 150, 20);
        p.add(name);

        JLabel price = new JLabel(String.format("%d 원", m.getPrice()));
        price.setBounds(80, 35, 100, 20);
        p.add(price);

        JButton add = new JButton();
        add.setBounds(260, 25, 60, 30);
        if (m.getStock() <= 0) {
            add.setText("품절");
            add.setEnabled(false);
            add.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "현재 메뉴는 품절입니다!\n다른 메뉴를 선택해주세요.", "품절", JOptionPane.INFORMATION_MESSAGE)
            );
        } else {
            add.setText("추가");
            add.setEnabled(true);
            add.addActionListener(e -> addToCart(m));
        }
        p.add(add);

        return p;
    }

    private void addToCart(Menu m) {
        int cur = cart.getOrDefault(m, 0);
        if (cur >= m.getStock()) {
            JOptionPane.showMessageDialog(this,
                m.getName() + "의 남은 재고는 " + m.getStock() + "개 입니다.",
                "재고 부족", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cart.put(m, cur + 1);
        refreshCart();
    }

    private void refreshCart() {
        if (cartTable.isEditing()) {
            cartTable.getCellEditor().stopCellEditing();
        }
        cartModel.setRowCount(0);
        int total = 0;
        for (Map.Entry<Menu, Integer> e : cart.entrySet()) {
            int amt = e.getKey().getPrice() * e.getValue();
            cartModel.addRow(new Object[]{ e.getKey().getName(), e.getValue(), amt, "삭제" });
            total += amt;
        }
        totalLbl.setText("합계: " + total + " 원");
    }

    private void showOrderSummary() {
        Map<Menu, Integer> cartCopy = new LinkedHashMap<>(cart);
        StringBuilder sb = new StringBuilder();
        int total = 0;
        for (Map.Entry<Menu, Integer> entry : cart.entrySet()) {
            String name = entry.getKey().getName();
            int qty = entry.getValue();
            int price = entry.getKey().getPrice();
            int amt = price * qty;
            sb.append(name).append(" | ").append(qty).append("개 | ").append(amt).append("원<br>");
            total += amt;
        }
        sb.append("<br> 합계 : ").append(total).append("원");
        String html = "<html>" + sb.toString() + "</html>";

        try {
            OrderService orderSvc = new OrderService();
            orderSvc.placeOrder(cart);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "주문 처리 중 오류 발생:\n" + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, html, "주문 완료!", JOptionPane.INFORMATION_MESSAGE);

        // 재고 차감
        for (Map.Entry<Menu, Integer> entry : cartCopy.entrySet()) {
            Menu orderedMenu = entry.getKey();
            int orderedQty = entry.getValue();
            for (Menu m : menus) {
                if (m.getName().equals(orderedMenu.getName())) {
                    int newStock = m.getStock() - orderedQty;
                    m.setStock(Math.max(newStock, 0));
                    break;
                }
            }
        }
        cart.clear();
        refreshCart();
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("삭제");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("삭제");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = (String) cartModel.getValueAt(row, 0);
                    Menu key = null;
                    for (Menu m : cart.keySet()) {
                        if (m.getName().equals(name)) {
                            key = m;
                            break;
                        }
                    }
                    if (key != null) {
                        cart.remove(key);
                        refreshCart();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "삭제";
        }
    }
}
