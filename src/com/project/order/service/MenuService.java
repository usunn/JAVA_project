package com.project.order.service;

import com.project.order.model.Menu;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;


public class MenuService {
    private static final String MENU_FILE = "src/com/project/order/logs/menu.txt";
    private static final String DELIMITER = "\\|";  
    private static final String JOINER    = "|";


    // menu.txt에서 모든 메뉴를 읽어 List<Menu> 으로 반환
    public List<Menu> getAll() throws IOException {
        List<Menu> list = new ArrayList<>();
        Path path = Paths.get(MENU_FILE);
        if (!Files.exists(path)) {
            System.out.println("[MenuService] menu.txt 파일을 찾을 수 없습니다: " + path.toAbsolutePath());
            return list;
        }

        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            if (parts.length < 5) continue;

            String name  = parts[2];
            int    price = Integer.parseInt(parts[3]);
            int    stock = Integer.parseInt(parts[4]);
            Menu m = new Menu(name, price, stock);
            list.add(m);
            
        }
        return list;
    }


    public void deductStockAndSave(Map<Menu, Integer> cart) throws IOException {
        Path path = Paths.get(MENU_FILE);
        if (!Files.exists(path)) {
            throw new IOException("menu.txt를 찾을 수 없습니다: " + path.toAbsolutePath());
        }

        // 1) menu.txt 모든 줄 읽기
        List<String> lines = Files.readAllLines(path);
        List<String> output = new ArrayList<>();

        // 2) 각 줄마다 “이름(name)”을 찾아서 재고 차감
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            if (parts.length < 5) {
                output.add(line);
                continue;
            }
            String name   = parts[2];
            int    stock  = Integer.parseInt(parts[4]);
            int    newStock = stock;

            // cart에 들어있는 Menu.keySet()에서 이름이 같은 걸 찾는다
            for (Menu m : cart.keySet()) {
                if (m.getName().equals(name)) {
                    int deductQty = cart.get(m);
                    newStock = stock - deductQty;
                    if (newStock < 0) newStock = 0;
                    System.out.printf("[MenuService] 메뉴 '%s' 재고: %d -> %d (주문 수량 %d)%n",
                                      name, stock, newStock, deductQty);
                    break;
                }
            }

            // id와 timestamp(=parts[0],parts[1])는 그대로 두고 stock만 바꾼 뒤 재조립
            String rebuilt = String.join(JOINER,
                    parts[0], 
                    parts[1], 
                    parts[2], 
                    parts[3], 
                    String.valueOf(newStock)
            );
            output.add(rebuilt);
        }

        Files.write(path,
                    output,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);

        System.out.println("[MenuService] menu.txt가 갱신되었습니다.");
    }
}
