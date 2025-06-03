package com.project.order.ui;

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
import javax.swing.*;
import javax.swing.table.*;

public class OrderSystemUIFrame extends JFrame {
    private MenuService menuSvc;
    private List<Menu> menus;
    private Map<Menu, Integer> cart = new LinkedHashMap<>();


    private JPanel mainPanel, orderPanel;
    private JLabel totalLbl, totalCartLbl;
    private DefaultTableModel cartModel;

    public OrderSystemUIFrame() throws Exception {
        super("Order System");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuSvc = new MenuService();
        menus = menuSvc.getAll();

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel(null);
        setContentPane(content);

        mainPanel = createMainPanel();
        mainPanel.setBounds(0, 0, 360, 640);
        content.add(mainPanel);

        orderPanel = createOrderPanel();
        orderPanel.setBounds(0, 0, 360, 640);
        orderPanel.setVisible(false);
        content.add(orderPanel);
    }

    private JPanel createMainPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        JLabel icon = new JLabel();
        URL url = getClass().getResource("com/project/order/model");
        if (url != null) icon.setIcon(new ImageIcon(url));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(icon);
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel w = new JLabel("Welcome to");
        w.setFont(new Font("SansSerif", Font.BOLD, 24));
        w.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel t = new JLabel("Order System");
        t.setFont(new Font("SansSerif", Font.BOLD, 24));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(w);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(t);
        p.add(Box.createVerticalGlue());

        RoundedButton orderBtn = new RoundedButton("Order");
        orderBtn.setBackground(Color.BLACK);
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderBtn.setMaximumSize(new Dimension(250, 50));  
        orderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTo(orderPanel);
            }
        });
        p.add(orderBtn);

        p.add(Box.createRigidArea(new Dimension(0, 15)));

        RoundedButton manageBtn = new RoundedButton("Manage");
        manageBtn.setBackground(new Color(227, 227, 227));
        manageBtn.setForeground(new Color(177, 177, 177));
        manageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageBtn.setMaximumSize(new Dimension(250, 50));
        p.add(manageBtn);

        p.add(Box.createVerticalGlue()); // 남은 여백

        return p;
    }

    private JPanel createOrderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 248, 248));
        p.add(createRecommendPanel(), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);
        for (Menu m : menus) {
            list.add(itemComp(m));
            list.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 16px만큼 이동
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 좌우 스크롤바 정책 X
        sp.setBounds(0, 50, 360, 350);
        sp.setBorder(null);
        p.add(sp);
        

        JPanel bottom = new JPanel(null);
        bottom.setBackground(Color.WHITE);
        bottom.setPreferredSize(new Dimension(360, 240));

        cartModel = new DefaultTableModel(new Object[] { "메뉴명", "수량", "금액", "" }, 0);
        JTable ct = new JTable(cartModel);
        ct.setShowGrid(false);

        ct.setRowHeight(25);
        TableColumn col = ct.getColumnModel().getColumn(3);
        col.setCellRenderer(new ButtonRenderer());
        col.setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane cp = new JScrollPane(ct);
        cp.setBounds(0, 0, 360, 100);
        bottom.add(cp);

        totalLbl = new JLabel("합계: 0 원");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLbl.setBounds(200, 110, 160, 30);
        bottom.add(totalLbl);

        RoundedButton checkout = new RoundedButton("결제하기");
        checkout.setBackground(new Color(236, 102, 85));
        checkout.setForeground(Color.WHITE);
        checkout.setBounds(100, 150, 160, 40);
        checkout.setArc(20,20);

        checkout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cart.isEmpty()) {
                    CustomDialog dlg = new CustomDialog(OrderSystemUIFrame.this, "경고!", "메뉴를 선택 후 결제해주세요");
                    dlg.setVisible(true);
                    return;
                }
                showOrderSummary();
            }
        });
        
        bottom.add(checkout);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

     private void Recommend() {
        try {
        
            MenuService menuSvc = new MenuService();
            List<Menu> allMenus = menuSvc.getAll();

            RecommendService recommender = new RecommendService();
            Menu rec = recommender.getRecommend(allMenus);
        
        
            if (rec!=null) {
                String menu1 = rec.getName();

                String message = "오늘의 추천 메뉴는 " + menu1 +" 입니다.";

                JOptionPane.showMessageDialog(this, message, "추천 메뉴", JOptionPane.INFORMATION_MESSAGE);

            } else {
        
                JOptionPane.showMessageDialog(this, "추천 가능한 메뉴가 부족합니다.");
            }

        } catch (Exception e) {
        
            JOptionPane.showMessageDialog(this, "추천 실패: " + e.getMessage());
        }
}
  
  private JPanel createRecommendPanel() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel icon = new JLabel();
        URL url = getClass().getResource("/com/project/order/image/Logo.png");
        if (url != null) {
            ImageIcon iconImg = new ImageIcon(url);
            icon.setIcon(new ImageIcon(iconImg.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } 

        JButton btn = new JButton("오늘의 추천");
        btn.setFont(new Font("recommend", Font.PLAIN, 14));
        btn.setBackground(new Color(102, 153, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Recommend();
            }
        });

        p.add(icon, BorderLayout.WEST);
        p.add(btn, BorderLayout.EAST);

        return p;
    }

    private void showOrderSummary() {
        Map<Menu, Integer> cartCopy = new LinkedHashMap<>(cart);

        StringBuilder sb = new StringBuilder();
        int total = 0;
        for (Map.Entry<Menu, Integer> entry : cart.entrySet()) {
            String name = entry.getKey().getName();
            int qty     = entry.getValue();
            int price   = entry.getKey().getPrice();
            int amt     = price * qty;

            sb.append(name)
            .append(" | ")
            .append(qty).append("개")
            .append(" | ")
            .append(amt).append("원")
            .append("<br>");
            total += amt;
        }
        sb.append("<br> 합계 : ").append(total).append("원");
        String raw  = sb.toString();
        String html = "<html>" + raw + "</html>";

        //  재고 차감 & 주문 로그 기록
        try {
            OrderService orderSvc = new OrderService();
            orderSvc.placeOrder(cart);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "주문 처리 중 오류 발생:\n" + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CustomDialog dlg = new CustomDialog(this, "주문 완료!", html);
        dlg.setVisible(true);

        // cartCopy를 이용해 메모리상의 menus 리스트의 stock만큼 차감
        for (Map.Entry<Menu, Integer> entry : cartCopy.entrySet()) {
            Menu orderedMenu = entry.getKey();
            int  orderedQty  = entry.getValue();

            // menus 리스트에서 name으로 동일 메뉴 객체를 찾은 뒤, stock 업데이트
            for (Menu m : menus) {
                if (m.getName().equals(orderedMenu.getName())) {
                    int newStock = m.getStock() - orderedQty;
                    if (newStock < 0) newStock = 0;
                    m.setStock(newStock);
                    break;
                }
            }
        }

        cart.clear();
        refreshCart();
    }

    private JPanel itemComp(Menu m) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.setPreferredSize(new Dimension(360, 80));

        JLabel img = new JLabel();
        URL imgUrl = getClass().getResource("/image/" + m.getName() + ".png");
        if (imgUrl != null) {
            ImageIcon ico = new ImageIcon(imgUrl);
            Image im2 = ico.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(im2));
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
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CustomDialog dlg = new CustomDialog(OrderSystemUIFrame.this, "현재 메뉴는 품절입니다!",
                            "다른 메뉴를 선택해주세요.");
                    dlg.setVisible(true);
                }
            });
        } else {
            add.setText("추가");
            add.setEnabled(true);
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addToCart(m);
                }
            });
        }

        p.add(add);
        return p;
    }

    private void addToCart(Menu m) {
        int cur = cart.getOrDefault(m, 0);
        if (cur >= m.getStock()) {
            CustomDialog dlg = new CustomDialog(this, "재고 부족",
                    m.getName() + "의 남은 재고는" + m.getStock() + "개 입니다.");
            dlg.setVisible(true);
            return;
        }
        cart.put(m, cur + 1);
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        int total = 0;
        for (Map.Entry<Menu, Integer> e : cart.entrySet()) {
            int amt = e.getKey().getPrice() * e.getValue();
            cartModel.addRow(new Object[] { e.getKey().getName(), e.getValue(), amt, "삭제" });
            total += amt;
        }
        totalLbl.setText("합계: " + total + " 원");
        totalCartLbl.setText("합계: " + total + " 원");
    }

    private void switchTo(JPanel panel) {
        mainPanel.setVisible(panel == mainPanel);
        orderPanel.setVisible(panel == orderPanel);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("삭제");
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
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

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            return "삭제";
        }
    }
}
