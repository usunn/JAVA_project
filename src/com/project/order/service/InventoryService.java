package com.project.order.service;

import com.project.order.model.Menu;
import com.project.order.persistence.FileManager;
import com.project.order.persistence.Persistable;
import com.project.order.util.DateTime;

import java.io.IOException;
import java.util.List;

public class InventoryService {
    private static final String MENU_FILE = "src/com/project/order/logs/menu.txt";

     // menu.txt를 읽어서 메뉴 객체(List<Menu>)만 뽑아서 돌려준다.
     // 파일 포맷: ID|타임스탬프|메뉴명|가격|재고
    public List<Menu> getAllMenus() {
        try {
            return FileManager.loadAll(MENU_FILE, Menu::fromRecord);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }


    // menu.txt에서 “메뉴명”이 menuName인 줄의 재고(stock)만 newStock으로 바꾼 뒤 전체를 덮어쓴다.
    public boolean updateStock(String menuName, int newStock) {
        try {
            List<Menu> menus = FileManager.loadAll(MENU_FILE, Menu::fromRecord);
            boolean found = false;

            for (Menu m : menus) {
                if (m.getName().equals(menuName)) {
                    m.setStock(newStock);
                    found = true;
                    break;
                }
            }
            if (!found) return false;

            FileManager.saveAll(MENU_FILE, menus);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 메뉴, 재고, 가격 수정시 사용
    public boolean updateMenu(String oldName, String newName, int newPrice, int newStock) {
        try {
            List<Menu> menus = getAllMenus();
            boolean found = false;
            for (Menu m : menus) {
                if (m.getName().equals(oldName)) {
                    m.setName(newName);
                    m.setPrice(newPrice);
                    m.setStock(newStock);
                    found = true;
                    break;
                }
            }
            if (!found) return false;

            FileManager.saveAll(MENU_FILE, menus);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean addMenu(String name, int price, int stock) {
        try {
            List<Menu> menus = getAllMenus();
            int maxId = menus.stream().mapToInt(Menu::getId).max().orElse(0);
            int newId = maxId + 1;

            String newMenu = String.join("|", String.valueOf(newId), DateTime.now().toRecord(), name, String.valueOf(price), String.valueOf(stock));
            FileManager.append(MENU_FILE, new Persistable() {
                @Override
                public String toRecord() {
                    return newMenu;
                }   
            });

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
