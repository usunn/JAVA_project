package com.project.order.service;

import com.project.order.model.Menu;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventoryService {
    private static final String MENU_FILE = "src/com/project/order/logs/menu.txt";

    /**
     * menu.txt를 읽어서 메뉴 객체(List<Menu>)만 뽑아서 돌려준다.
     * 파일 포맷: ID|타임스탬프|메뉴명|가격|재고
     */
    public List<Menu> getAllMenus() throws IOException {
        List<Menu> result = new ArrayList<>();
        Path path = Paths.get(MENU_FILE);
        if (!Files.exists(path)) {
            return result;
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            // 예시: "2|2025-05-23 14:31:12|김치알밥|5000|24"
            String[] parts = line.split("\\|");
            if (parts.length < 5) continue;

            String name = parts[2];
            int price;
            int stock;
            try {
                price = Integer.parseInt(parts[3].trim());
                stock = Integer.parseInt(parts[4].trim());
            } catch (NumberFormatException ex) {
                continue;
            }
            result.add(new Menu(name, price, stock));
        }
        return result;
    }

    /**
     * menu.txt에서 “메뉴명”이 menuName인 줄의 재고(stock)만 newStock으로 바꾼 뒤 전체를 덮어쓴다.
     * @param menuName 수정할 메뉴명
     * @param newStock 새 재고값
     * @return true: 수정 성공, false: 해당 메뉴를 못 찾음
     */
    public boolean updateStock(String menuName, int newStock) throws IOException {
        Path path = Paths.get(MENU_FILE);
        if (!Files.exists(path)) {
            return false;
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        boolean found = false;
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 5) {
                // 형식이 맞지 않으면 그대로 유지
                updated.add(line);
                continue;
            }
            String name = parts[2];
            if (name.equals(menuName)) {
                // 재고만 바꾼다
                parts[4] = String.valueOf(newStock);
                found = true;
            }
            updated.add(String.join("|", parts));
        }

        if (!found) {
            return false;
        }

        // 변경된 전체 내용으로 덮어쓰기
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String outLine : updated) {
                writer.write(outLine);
                writer.newLine();
            }
        }
        return true;
    }

    public boolean updateMenu(String oldName, String newName, int newPrice, int newStock) throws IOException {
        Path path = Paths.get(MENU_FILE);
        if (!Files.exists(path)) return false;
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        boolean found = false;
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 5) {
                updated.add(line);
                continue;
            }
            if (parts[2].equals(oldName)) {
                // 메뉴명, 가격, 재고를 새 값으로 교체
                parts[2] = newName;
                parts[3] = String.valueOf(newPrice);
                parts[4] = String.valueOf(newStock);
                found = true;
            }
            updated.add(String.join("|", parts));
        }
        if (!found) return false;

        // 전체 내용을 덮어쓰기로 저장
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String out : updated) {
                writer.write(out);
                writer.newLine();
            }
        }
        return true;
    }



    public boolean addMenu(String name, int price, int stock) throws IOException {
        Path path = Paths.get(MENU_FILE);

        // 1) 파일이 존재하면, 기존 줄 전부 읽어서 가장 큰 ID 찾기
        int maxId = 0;
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) { }
            }
        }

        int newId = maxId + 1;

        // 2) 현재 시각을 "yyyy-MM-dd HH:mm:ss" 형식으로 얻기
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 3) "ID|타임스탬프|메뉴명|가격|재고" 한 줄로 조합
        String newLine = String.format("%d|%s|%s|%d|%d",
                newId, timestamp, name, price, stock);

        // 4) menu.txt에 append
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(newLine);
            writer.newLine();
        }

        return true;
    }
}
