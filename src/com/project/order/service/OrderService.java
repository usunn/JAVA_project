package com.project.order.service;

import com.project.order.model.Menu;
import com.project.order.model.OrderLog;
import com.project.order.util.DateTime;
import com.project.order.persistence.FileManager;
import com.project.order.persistence.Persistable;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public class OrderService {
    private static final String ORDERS_FILE = "src/com/project/order/logs/orders.txt";
    private final MenuService menuService;

    public OrderService() {
        this.menuService = new MenuService();
    }


    public void placeOrder(Map<Menu, Integer> cart) throws Exception {
        menuService.deductStockAndSave(cart);
        appendOrderLog(cart);
    }

    private void appendOrderLog(Map<Menu, Integer> cart) throws IOException {
        List<OrderLog> logs = FileManager.loadAll(ORDERS_FILE, OrderLog::fromRecord);
        int nextId = logs.stream().mapToInt(OrderLog::getId).max().orElse(0) + 1;

        StringBuilder sb = new StringBuilder();
        int total = 0;
        boolean first = true;

        for (var e : cart.entrySet()) {
            if (!first) sb.append(" ");
            sb.append(e.getKey().getName())
              .append(" <").append(e.getValue()).append(">");
            total += e.getKey().getPrice() * e.getValue();
            first = false;
        }
        String items = sb.toString();

        String record = String.join("|", String.valueOf(nextId), DateTime.now().toRecord(), items,String.valueOf(total));

        FileManager.append(ORDERS_FILE, new Persistable() {
            @Override
            public String toRecord() {
                return record;
            }
        });
    }
}
