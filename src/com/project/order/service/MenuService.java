package com.project.order.service;

import com.project.order.model.Menu;
import com.project.order.persistence.FileManager;


import java.io.IOException;
import java.util.List;
import java.util.Map;


public class MenuService {
    private static final String MENU_FILE = "src/com/project/order/logs/menu.txt";

    // menu.txt에서 모든 메뉴를 읽어 List<Menu> 으로 반환
    public List<Menu> getAll() {
        try {
            return FileManager.loadAll(MENU_FILE, Menu::fromRecord);
        } catch (IOException e) {
            System.err.println("[MenuService] 메뉴 로드 실패: " + e.getMessage());
            return List.of();
        }
    }


    public void deductStockAndSave(Map<Menu, Integer> cart) throws IOException {
        List<Menu> menus = getAll();

        boolean modified = false;

        for (Menu m : menus) {
            Integer qty = cart.get(m);
            if (qty != null) {
                int oldStock = m.getStock();
                int newStock = oldStock - qty;
                if (newStock < 0) newStock = 0;

                System.out.printf(
                    "[MenuService] 메뉴 '%s' 재고: %d -> %d (주문 수량 %d)%n",
                    m.getName(), oldStock, newStock, qty
                );

                m.setStock(newStock);
                modified = true;
            }
        }

        if (modified) {
            FileManager.saveAll(MENU_FILE, menus);
            System.out.println("[MenuService] menu.txt가 갱신되었습니다.");
        }
    }
}
