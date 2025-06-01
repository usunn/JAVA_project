package com.project.order.service;

import com.project.order.model.Menu;
import com.project.order.persistence.FileManager;
import java.util.*;

public class MenuService {
    private List<Menu> menus;
    private final String file="menu.txt";
    public MenuService() throws Exception {
        menus = FileManager.loadAll(file, Menu::fromRecord);
    }
    public List<Menu> getAll(){ return menus; }
    public void save() throws Exception { FileManager.saveAll(file, menus); }
    public void decreaseStock(int menuId,int qty) throws Exception {
        for(Menu m:menus) if(m.getName().hashCode()==menuId) {
            if(m.getStock()<qty) throw new com.project.order.exception.SoldOutException("품절");
            m.setStock(m.getStock()-qty);
            save(); return;
        }
    }
}

