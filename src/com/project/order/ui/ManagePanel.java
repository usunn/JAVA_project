package com.project.order.ui;

import com.project.order.model.Menu;
import com.project.order.service.InventoryService;
import com.project.order.service.OrderLogService;
import com.project.order.service.SalesService;

import javax.swing.*;
import javax.swing.table.TableColumn;     
import javax.swing.table.DefaultTableModel;  
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;


public class ManagePanel extends JPanel {
    private InventoryService invSvc;
    private OrderLogService logSvc;
    private SalesService salesSvc;
    private Runnable onLogout;

    public ManagePanel(Runnable onLogout) {
        this.onLogout = onLogout;
        invSvc   = new InventoryService();
        logSvc   = new OrderLogService();
        salesSvc = new SalesService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단: 로그아웃 버튼
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);
        RoundedButton logoutBtn = new RoundedButton("로그아웃");
        logoutBtn.setBackground(new Color(200, 200, 200));
        logoutBtn.addActionListener(e -> onLogout.run());
        topBar.add(logoutBtn);
        add(topBar, BorderLayout.NORTH);

        // 중앙: 탭 패널
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("재고 관리", createInventoryTab());
        tabs.addTab("매출 확인", createSalesTab());
        tabs.addTab("주문 내역", createOrderLogTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ─────────────────── “재고 관리” 탭 UI ───────────────────
    private JPanel createInventoryTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // ─── 테이블(기존) ───────────────────────────────────────────────────────
        DefaultTableModel model = new DefaultTableModel(new Object[] {
                "메뉴명", "가격", "재고"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 모든 열(0,1,2) 편집 가능
                return true;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // 메뉴 데이터를 불러와서 테이블에 채우기
        try {
            List<Menu> all = invSvc.getAllMenus();
            for (Menu m : all) {
                model.addRow(new Object[]{ m.getName(), m.getPrice(), m.getStock() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "재고 목록을 불러오는 중 오류 발생:\n" + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        }

        // ── 편집 전 데이터를 저장할 변수 ───────────────────────────────────────────
        final String[] beforeName  = new String[1];
        final int[]    beforePrice = new int[1];
        final int[]    beforeStock = new int[1];
        final int[]    editRow     = new int[1];
        editRow[0] = -1;
        beforeName[0] = null;

        // ── 1) “메뉴명” 열(컬럼 0)에 대한 CellEditor 설정 ─────────────────────────────
        TableColumn nameCol = table.getColumnModel().getColumn(0);
        nameCol.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                beforeName[0] = value.toString();
                beforePrice[0] = Integer.parseInt(model.getValueAt(row, 1).toString());
                beforeStock[0] = Integer.parseInt(model.getValueAt(row, 2).toString());
                editRow[0] = row;
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                int row = editRow[0];
                if (row < 0) {
                    beforeName[0] = null;
                    return super.stopCellEditing();
                }

                String newName = ((JTextField)getComponent()).getText().trim();
                String oldName = beforeName[0];
                int oldPrice   = beforePrice[0];
                int oldStock   = beforeStock[0];

                String priceObj = model.getValueAt(row, 1).toString().trim();
                String stockObj = model.getValueAt(row, 2).toString().trim();
                int newPrice, newStock;
                try {
                    newPrice = Integer.parseInt(priceObj);
                    newStock = Integer.parseInt(stockObj);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "가격과 재고는 숫자만 입력 가능합니다.",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(oldPrice, row, 1);
                    model.setValueAt(oldStock, row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                try {
                    boolean ok = invSvc.updateMenu(oldName, newName, newPrice, newStock);
                    if (!ok) {
                        throw new Exception("해당 메뉴를 찾을 수 없습니다: " + oldName);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "메뉴 정보 업데이트 중 오류 발생:\n" + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(oldPrice, row, 1);
                    model.setValueAt(oldStock, row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                beforeName[0] = null;
                editRow[0] = -1;
                return super.stopCellEditing();
            }
        });

        // ── 2) “가격” 열(컬럼 1)에 대한 CellEditor 설정 ───────────────────────────────
        TableColumn priceCol = table.getColumnModel().getColumn(1);
        priceCol.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                beforeName[0] = model.getValueAt(row, 0).toString();
                try {
                    beforePrice[0] = Integer.parseInt(value.toString());
                } catch (NumberFormatException ex) {
                    beforePrice[0] = 0;
                }
                beforeStock[0] = Integer.parseInt(model.getValueAt(row, 2).toString());
                editRow[0] = row;
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                int row = editRow[0];
                if (row < 0) {
                    beforeName[0] = null;
                    return super.stopCellEditing();
                }

                String oldName = beforeName[0];
                String priceObj = ((JTextField)getComponent()).getText().trim();
                String stockObj = model.getValueAt(row, 2).toString().trim();
                int newPrice, newStock;
                try {
                    newPrice = Integer.parseInt(priceObj);
                    newStock = Integer.parseInt(stockObj);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "가격과 재고는 숫자만 입력 가능합니다.",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(beforePrice[0], row, 1);
                    model.setValueAt(beforeStock[0], row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                try {
                    boolean ok = invSvc.updateMenu(oldName, oldName, newPrice, newStock);
                    if (!ok) {
                        throw new Exception("해당 메뉴를 찾을 수 없습니다: " + oldName);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "메뉴 정보 업데이트 중 오류 발생:\n" + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(beforePrice[0], row, 1);
                    model.setValueAt(beforeStock[0], row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                beforeName[0] = null;
                editRow[0] = -1;
                return super.stopCellEditing();
            }
        });

        // ── 3) “재고” 열(컬럼 2)에 대한 CellEditor 설정 ───────────────────────────────
        TableColumn stockCol = table.getColumnModel().getColumn(2);
        stockCol.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                beforeName[0]  = model.getValueAt(row, 0).toString();
                beforePrice[0] = Integer.parseInt(model.getValueAt(row, 1).toString());
                try {
                    beforeStock[0] = Integer.parseInt(value.toString());
                } catch (NumberFormatException ex) {
                    beforeStock[0] = 0;
                }
                editRow[0] = row;
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                int row = editRow[0];
                if (row < 0) {
                    beforeName[0] = null;
                    return super.stopCellEditing();
                }

                String oldName = beforeName[0];
                String priceObj = model.getValueAt(row, 1).toString().trim();
                String stockObj = ((JTextField)getComponent()).getText().trim();
                int newPrice, newStock;
                try {
                    newPrice = Integer.parseInt(priceObj);
                    newStock = Integer.parseInt(stockObj);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "가격과 재고는 숫자만 입력 가능합니다.",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(beforePrice[0], row, 1);
                    model.setValueAt(beforeStock[0], row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                try {
                    boolean ok = invSvc.updateMenu(oldName, oldName, newPrice, newStock);
                    if (!ok) {
                        throw new Exception("해당 메뉴를 찾을 수 없습니다: " + oldName);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManagePanel.this,
                        "메뉴 정보 업데이트 중 오류 발생:\n" + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(oldName,  row, 0);
                    model.setValueAt(beforePrice[0], row, 1);
                    model.setValueAt(beforeStock[0], row, 2);
                    beforeName[0] = null;
                    editRow[0] = -1;
                    return super.stopCellEditing();
                }

                beforeName[0] = null;
                editRow[0] = -1;
                return super.stopCellEditing();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        p.add(scroll, BorderLayout.CENTER);

        // ── 하단: 메뉴 추가 입력란 + 버튼 ─────────────────────────────────────────────
        JPanel addPanel = new JPanel(null);
        addPanel.setPreferredSize(new Dimension(360, 100));
        addPanel.setBackground(Color.WHITE);

        JLabel nameLbl = new JLabel("메뉴명:");
        nameLbl.setBounds(10, 10, 60, 25);
        addPanel.add(nameLbl);
        JTextField nameField = new JTextField();
        nameField.setBounds(70, 10, 120, 25);
        addPanel.add(nameField);

        JLabel priceLbl = new JLabel("가격:");
        priceLbl.setBounds(10, 45, 60, 25);
        addPanel.add(priceLbl);
        JTextField priceField = new JTextField();
        priceField.setBounds(70, 45, 80, 25);
        addPanel.add(priceField);

        JLabel stockLbl = new JLabel("재고:");
        stockLbl.setBounds(160, 45, 40, 25);
        addPanel.add(stockLbl);
        JTextField stockField = new JTextField();
        stockField.setBounds(200, 45, 80, 25);
        addPanel.add(stockField);

        RoundedButton addBtn = new RoundedButton("추가");
        addBtn.setBounds(290, 27, 60, 30);
        addBtn.setBackground(new Color(102, 204, 102));
        addBtn.setForeground(Color.WHITE);
        addPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "모든 입력란을 채워주세요.",
                    "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int price, stock;
            try {
                price = Integer.parseInt(priceText);
                stock = Integer.parseInt(stockText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "가격과 재고는 숫자만 입력 가능합니다.",
                    "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = invSvc.addMenu(name, price, stock);
                if (success) {
                    model.addRow(new Object[]{ name, price, stock });
                    nameField.setText("");
                    priceField.setText("");
                    stockField.setText("");
                    JOptionPane.showMessageDialog(this,
                        "새 메뉴가 추가되었습니다.",
                        "추가 완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "메뉴 추가에 실패했습니다. 이미 존재하는 메뉴인지 확인하세요.",
                        "추가 실패", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "메뉴 추가 중 오류 발생:\n" + ex.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(addPanel, BorderLayout.SOUTH);

        return p;
    }

    // ─────────────────── “매출 확인” 탭 UI ───────────────────
    private JPanel createSalesTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // (1) 주문 로그 전체를 가져와서
        List<String> logs;
        try {
            logs = logSvc.getOrderLogs();
        } catch (Exception ex) {
            logs = List.of();
            JOptionPane.showMessageDialog(this,
                    "주문 로그를 불러오는 중 오류 발생:\n" + ex.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        }

        // (2) 메뉴별 판매 개수 집계 및 전체 매출 계산
        Map<String, Integer> qtyByMenu = salesSvc.computeQuantityByMenu(logs);
        int totalRevenue = salesSvc.computeTotalRevenue(logs);

        // (3) 테이블 모델 생성: {메뉴명, 판매 개수}
        DefaultTableModel model = new DefaultTableModel(new Object[]{"메뉴명", "판매 개수"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 절대로 수정 불가
            }
        };

        for (Map.Entry<String, Integer> entry : qtyByMenu.entrySet()) {
            model.addRow(new Object[] {
                entry.getKey(), entry.getValue()
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(table);

        // (4) 전체 매출 라벨
        String fmtRevenue = NumberFormat.getNumberInstance(Locale.KOREA)
                .format(totalRevenue);
        JLabel revenueLbl = new JLabel("총 매출: " + fmtRevenue + "원");
        revenueLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        revenueLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(revenueLbl, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────── “주문 내역” 탭 UI ───────────────────
    private JPanel createOrderLogTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // (1) 주문 로그 읽어서 “텍스트 에어리어”에 출력
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        try {
            List<String> logs = logSvc.getOrderLogs();
            if (logs.isEmpty()) {
                area.setText("주문 로그가 없습니다.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String line : logs) {
                    sb.append(line).append("\n");
                }
                area.setText(sb.toString());
            }
        } catch (Exception ex) {
            area.setText("주문 로그를 불러오는 중 오류 발생:\n" + ex.getMessage());
        }

        JScrollPane scroll = new JScrollPane(area);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }
}
