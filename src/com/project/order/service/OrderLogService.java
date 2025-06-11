package com.project.order.service;

import com.project.order.model.OrderLog;
import com.project.order.storage.FileManager;
import java.io.IOException;
import java.util.List;

public class OrderLogService {
    private static final String ORDER_LOG_PATH = "src/com/project/order/logs/orders.txt";

    public List<OrderLog> getOrderLogs() {
        try {
            return FileManager.loadAll(ORDER_LOG_PATH, OrderLog::fromRecord);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
