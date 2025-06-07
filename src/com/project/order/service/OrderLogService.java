package com.project.order.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class OrderLogService {
    private static final String ORDER_LOG_PATH = "src/com/project/order/logs/orders.txt";

    public List<String> getOrderLogs() throws IOException {
        Path path = Paths.get(ORDER_LOG_PATH);
        if (!Files.exists(path)) {
            return List.of();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
}
