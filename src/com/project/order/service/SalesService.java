package com.project.order.service;

import java.util.*;
import java.util.regex.*;

public class SalesService {
    public Map<String, Integer> computeQuantityByMenu(List<String> orderLines) {
        Map<String, Integer> qtyMap = new LinkedHashMap<>();

        // 표현: (메뉴명) <(수량)>
        Pattern itemPattern = Pattern.compile("([^<>]+)\\s*<([0-9]+)>");

        for (String line : orderLines) {
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;

            String itemsStr = parts[2].trim();
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
    public int computeTotalRevenue(List<String> orderLines) {
        int total = 0;
        for (String line : orderLines) {
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;

            try {
                int revenue = Integer.parseInt(parts[3].trim());
                total += revenue;
            } catch (NumberFormatException ex) {
                // 잘못된 숫자 형식이면 무시
            }
        }
        return total;
    }
}
