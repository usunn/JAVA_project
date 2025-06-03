package com.project.order.service;

import com.project.order.model.Menu;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class OrderService {
    private static final String ORDERS_FILE = "src/com/project/order/logs/orders.txt";
    private static final DateTimeFormatter DT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MenuService menuService;

    public OrderService() {
        this.menuService = new MenuService();
    }


    public void placeOrder(Map<Menu, Integer> cart) throws Exception {
        menuService.deductStockAndSave(cart);

        appendOrderLog(cart);
    }

    private void appendOrderLog(Map<Menu, Integer> cart) throws IOException {
        Path path = Paths.get(ORDERS_FILE);
        int nextId = 1;
        if (Files.exists(path)) {
            List<String> existing = Files.readAllLines(path);
            if (!existing.isEmpty()) {
                // 마지막 줄의 ID 부분 파싱
                String lastLine = existing.get(existing.size() - 1);
                String[] parts  = lastLine.split("\\|");
                int lastId = Integer.parseInt(parts[0]);
                nextId = lastId + 1;
                
            }
        }

        String orderId = String.valueOf(nextId);  // 순차 번호로 설정
        String now     = LocalDateTime.now().format(DT_FORMATTER);

        StringBuilder itemsBuilder = new StringBuilder();
        int total = 0;
        boolean first = true;
        for (Map.Entry<Menu, Integer> entry : cart.entrySet()) {
            String name = entry.getKey().getName();
            int    qty  = entry.getValue();
            int    price= entry.getKey().getPrice();
            int    amt  = price * qty;
            total += amt;

            if (!first) itemsBuilder.append(" ");
            itemsBuilder.append(name).append(" <").append(qty).append(">");
            first = false;
        }

        String logLine = orderId
                    + "|" + now
                    + "|" + itemsBuilder.toString()
                    + "|" + total;

        Files.write(path,
                    (logLine + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
    }
}
