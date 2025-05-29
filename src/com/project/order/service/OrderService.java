// ===== service/OrderService.java =====
package com.project.order.service;

import com.project.order.model.Order;
import com.project.order.persistence.FileManager;
import java.util.*;

public class OrderService {
    private List<Order> orders;
    private final String file="order.txt";
    public OrderService() throws Exception {
        orders = FileManager.loadAll(file, s->{
            try{ return Order.fromRecord(s);}catch(Exception e){return null;}
        });
    }
    public void place(int menuId,int qty,String pay) throws Exception {
        Order o = new Order(menuId,qty,pay);
        orders.add(o);
        FileManager.saveAll(file, orders);
    }
}