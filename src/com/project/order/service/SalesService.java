package com.project.order.service;

import java.util.*;
import java.util.regex.*;

import com.project.order.model.OrderLog;

public class SalesService {
    public Map<String, Integer> computeQuantityByMenu(List<OrderLog> logs) {
        Map<String, Integer> qtyMap = new LinkedHashMap<>();
        // "메뉴명 <수량>" 패턴
        Pattern itemPattern = Pattern.compile("([^<>]+)\\s*<([0-9]+)>");

        for (OrderLog log : logs) {
            String itemsStr = log.getItems();
            Matcher m = itemPattern.matcher(itemsStr);
            while (m.find()) {
                String menuName = m.group(1).trim();
                int count = Integer.parseInt(m.group(2));
                qtyMap.put(menuName, qtyMap.getOrDefault(menuName, 0) + count);
            }
        }
        return qtyMap;
    }

    // order.txt의 마지막 필드(총금액)을 합산하여 전체 매출을 계산한다.
    public int computeTotalRevenue(List<OrderLog> logs) {
        int total = 0;
        for (OrderLog log : logs) {
            total += log.getTotal();
        }
        return total;
    }
}

