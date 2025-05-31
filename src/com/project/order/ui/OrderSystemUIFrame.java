package com.project.order.ui;

import javax.swing.*;
import javax.swing.table.*;
import com.project.order.model.Menu;
import com.project.order.service.MenuService;
import com.project.order.service.OrderService;
import com.project.order.ui.CustomDialog;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class OrderSystemUIFrame extends JFrame {
    private MenuService menuSvc;
    private OrderService orderSvc;
    private List<Menu> menus;
    private Map<Menu, Integer> cart = new LinkedHashMap<>();

    private JPanel mainPanel, orderPanel, cartPanel;
    private JLabel totalLbl, totalCartLbl;
    private DefaultTableModel cartModel;

    public OrderSystemUIFrame() throws Exception {
        super("Order System");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuSvc = new MenuService();
        orderSvc = new OrderService();
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

        cartPanel = createCartPanel();
        cartPanel.setBounds(0, 640, 360, 640);
        content.add(cartPanel);
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

        JButton orderBtn = createRoundedButton("Order");
        orderBtn.setBackground(Color.BLACK);
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderBtn.addActionListener(e -> switchTo(orderPanel));
        p.add(orderBtn);
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton manageBtn = createRoundedButton("Manage");
        manageBtn.setEnabled(false);
        manageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(manageBtn);

        return p;
    }

    private JPanel createOrderPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248, 248, 248));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);
        for (Menu m : menus) {
            list.add(itemComp(m));
            list.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        JScrollPane sp = new JScrollPane(list);
        sp.setBounds(0, 0, 360, 400);
        sp.setBorder(null);
        p.add(sp);

        JPanel bottom = new JPanel(null);
        bottom.setBackground(Color.WHITE);
        bottom.setBounds(0, 400, 360, 240);

        cartModel = new DefaultTableModel(new Object[] { "메뉴명", "수량", "금액", "" }, 0);
        JTable ct = new JTable(cartModel);
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

        JButton checkout = createRoundedButton("결제하기");
        checkout.setBackground(new Color(236, 102, 85));
        checkout.setForeground(Color.WHITE);
        checkout.setBounds(100, 150, 160, 40);
        checkout.addActionListener(e -> {
            slideCartUp();
            showOrderSummary();
        });
        bottom.add(checkout);

        p.add(bottom);
        return p;
    }

    private JPanel createCartPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel header = new JLabel("Cart");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBounds(10, 10, 200, 30);
        p.add(header);

        JTable table = new JTable(cartModel);
        table.setRowHeight(25);
        TableColumn col = table.getColumnModel().getColumn(3);
        col.setCellRenderer(new ButtonRenderer());
        col.setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane cp = new JScrollPane(table);
        cp.setBounds(0, 50, 360, 260);
        p.add(cp);

        totalCartLbl = new JLabel("합계: 0 원");
        totalCartLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalCartLbl.setBounds(200, 320, 160, 30);
        p.add(totalCartLbl);

        JButton checkout = createRoundedButton("결제하기");
        checkout.setBackground(new Color(236, 102, 85));
        checkout.setForeground(Color.WHITE);
        checkout.setBounds(100, 370, 160, 50);
        checkout.addActionListener(e -> showOrderSummary());
        p.add(checkout);

        return p;
    }

    private void showOrderSummary() {
        StringBuilder sb = new StringBuilder("주문이 완료되었습니다!\n\n");
        int total = 0;
        for (Map.Entry<Menu, Integer> e : cart.entrySet()) {
            int price = e.getKey().getPrice();
            int qty = e.getValue();
            int amt = price * qty;
            sb.append(e.getKey().getName()).append(" - ")
              .append(qty).append("개, ")
              .append(amt).append("원 \n");
            total += amt;
        }
        sb.append("\n총 합계: ").append(total).append("원");
        JOptionPane.showMessageDialog(this, sb.toString(), "주문 내역", JOptionPane.INFORMATION_MESSAGE);
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
            add.addActionListener(e -> {
                CustomDialog dlg = new CustomDialog(this, "현재 메뉴는 품절입니다!", "다른 메뉴 선택 부탁드립니다");
                dlg.setVisible(true);
            });
        } else {
            add.setText("추가");
            add.addActionListener(e -> addToCart(m));
        }
        p.add(add);
        return p;
    }

    private void addToCart(Menu m) {
        int cur = cart.getOrDefault(m, 0);
        if (cur >= m.getStock()) {
            CustomDialog dlg = new CustomDialog(this, "재고 부족",
                    m.getName() + "의 재고가 부족합니다.\n현재 재고: " + m.getStock() + "개");
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
        cartPanel.setVisible(panel == cartPanel);
    }

    private void slideCartUp() {
        cartPanel.setVisible(true);
        Timer t = new Timer(5, null);
        t.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Point loc = cartPanel.getLocation();
                if (loc.y <= 0)
                    ((Timer) e.getSource()).stop();
                else
                    cartPanel.setLocation(0, loc.y - 20);
            }
        });
        t.start();
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return btn;
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
            button.addActionListener(e -> {
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
