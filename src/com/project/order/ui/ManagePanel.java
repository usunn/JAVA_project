package com.project.order.ui;

import com.project.order.model.Menu;
import com.project.order.model.OrderLog;
import com.project.order.service.InventoryService;
import com.project.order.service.OrderLogService;
import com.project.order.service.SalesService;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


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

    // “재고 관리” 탭 UI
    private JPanel createInventoryTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        //  테이블(기존)
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

        // 편집 전 데이터를 저장할 변수
        final String[] beforeName  = new String[1];
        final int[]    beforePrice = new int[1];
        final int[]    beforeStock = new int[1];
        final int[]    editRow     = new int[1];
        editRow[0] = -1;
        beforeName[0] = null;

        //  1) “메뉴명” 열(컬럼 0)에 대한 CellEditor 설정
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
                    if (!ok) throw new Exception("해당 메뉴를 찾을 수 없습니다: " + oldName);

                    //이미지 이름도 같이 수정
                    java.nio.file.Path oldImg = java.nio.file.Paths.get("src/com/project/order/image/" + oldName + ".png");
                    java.nio.file.Path newImg = java.nio.file.Paths.get("src/com/project/order/image/" + newName + ".png");

                    if (java.nio.file.Files.exists(oldImg)) {
                        java.nio.file.Files.move(oldImg, newImg, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
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

        // 2) “가격” 열(컬럼 1)에 대한 CellEditor 설정 
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

        //  3) “재고” 열(컬럼 2)에 대한 CellEditor 설정 
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

//  하단: 메뉴 추가 입력란 + 버튼
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.setPreferredSize(new Dimension(400, 160));
        addPanel.setBackground(Color.WHITE);

        // 1행: 메뉴명
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row1.setBackground(Color.WHITE);
        row1.add(new JLabel("메뉴명:"));
        JTextField nameField = new JTextField(20);
        row1.add(nameField);
        addPanel.add(row1);

        // 2행: 가격, 재고
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row2.setBackground(Color.WHITE);
        row2.add(new JLabel("가격:"));
        JTextField priceField = new JTextField(8);
        row2.add(priceField);
        row2.add(new JLabel("재고:"));
        JTextField stockField = new JTextField(8);
        row2.add(stockField);
        addPanel.add(row2);

        // 3행: 이미지 경로 + 찾기 버튼
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row3.setBackground(Color.WHITE);
        row3.add(new JLabel("이미지:"));
        JTextField imgPathField = new JTextField(15);
        row3.add(imgPathField);
        JButton browseBtn = new JButton("찾기");
        row3.add(browseBtn);
        addPanel.add(row3);

        // 4행: 추가 버튼
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row4.setBackground(Color.WHITE);
        RoundedButton addBtn = new RoundedButton("추가하기");
        addBtn.setPreferredSize(new Dimension(160, 35));
        addBtn.setBackground(new Color(102, 153, 255));
        addBtn.setForeground(Color.WHITE);
        row4.add(addBtn);
        addPanel.add(row4);

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                imgPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        addBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();
            String imgPath = imgPathField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || imgPath.isEmpty()) {
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
                java.nio.file.Path src = java.nio.file.Paths.get(imgPath);

                // 파일 확장자 검사 추가 
                if (!imgPath.toLowerCase().endsWith(".png")) {
                    JOptionPane.showMessageDialog(this,
                        "이미지 파일은 .png 형식만 지원합니다.",
                        "파일 형식 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 파일 이름 검사: 메뉴명.png와 일치해야 함
                String expectedFilename = name + ".png";
                String actualFilename = src.getFileName().toString();
                if (!actualFilename.equals(expectedFilename)) {
                    JOptionPane.showMessageDialog(this,
                        "이미지 파일 이름은 메뉴명과 동일해야 합니다: " + expectedFilename,
                        "파일 이름 불일치", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.nio.file.Path dest = java.nio.file.Paths.get("src/com/project/order/image/" + name + ".png");
                java.nio.file.Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                boolean success = invSvc.addMenu(name, price, stock);
                if (success) {
                    model.addRow(new Object[]{ name, price, stock });
                    nameField.setText("");
                    priceField.setText("");
                    stockField.setText("");
                    imgPathField.setText("");
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
    // “매출 확인” 탭 UI 
    private JPanel createSalesTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // (1) 주문 로그 전체를 가져와서
        List<OrderLog> logs;
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

    //  “주문 내역” 탭 UI
    private JPanel createOrderLogTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // 테이블 모델 정의
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"시간", "주문 목록", "총액"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // JTable 생성
        JTable table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setReorderingAllowed(false);

        // 셀 렌더러 - 줄바꿈 적용
        table.getColumnModel().getColumn(1).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JTextArea ta = new JTextArea(value.toString());
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setOpaque(true);
            ta.setFont(tbl.getFont());
            ta.setBackground(isSelected ? tbl.getSelectionBackground() : tbl.getBackground());
            return ta;
        });

        JScrollPane tableScroll = new JScrollPane(table);

        // 주문 로그 읽기
        try {
            List<OrderLog> logs = logSvc.getOrderLogs(); // 파일에서 읽는 경우엔 대체 가능

            for (OrderLog log : logs) {
                // 1|2025-06-03 20:14:38|김치알밥 <4> 제육덮밥 <3> 육회비빔밥 <2>|43500
                String time = log.getTs().toRecord();
                String rawItems = log.getItems();  

                // 메뉴 파싱 (정규식으로 메뉴+수량 추출)
                StringBuilder formattedMenu = new StringBuilder();
                Matcher m = Pattern.compile("([^<]+)<(\\d+)>").matcher(rawItems);
                while (m.find()) {
                    formattedMenu
                    .append(m.group(1).trim())
                    .append(" ")
                    .append(m.group(2).trim())
                    .append("개\n");
                }

                tableModel.addRow(new Object[]{
                    time,
                    formattedMenu.toString().trim(),
                    NumberFormat.getNumberInstance(Locale.KOREA).format(log.getTotal()) + "원"
                });
            }

        // 자동 행 높이 조정
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                maxHeight = Math.max(maxHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, maxHeight);
        }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "주문 로그를 불러오는 중 오류 발생:\n" + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        }

        p.add(tableScroll, BorderLayout.CENTER);
        return p;
    }

}
