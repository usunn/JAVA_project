package com.project.order.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class OrderLogService {
    private static final String ORDER_LOG_PATH = "src/com/project/order/logs/orders.txt";

    /**
     * order.txt 파일이 존재하면 모든 줄을 읽어 List<String>으로 반환한다.
     * (각 줄이 “한 주문기록” 한 줄로 간주된다.)
     */
    public List<String> getOrderLogs() throws IOException {
        Path path = Paths.get(ORDER_LOG_PATH);
        if (!Files.exists(path)) {
            // 파일이 없으면 빈 리스트
            return List.of();
        }
        // UTF-8로 읽어서 반환
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
}
