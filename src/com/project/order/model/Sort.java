package com.project.order.model;
import java.util.*;


public class Sort {
	private List<Menu> realmenu = new ArrayList<>();
	int size;
	public PriorityQueue<Menu> pQ = new PriorityQueue<>(Collections.reverseOrder()); //재고많은 순으로 우선순위 결정
	
	 public void sortList(List<Menu> menus) {
        double sum = 0;
        for (Menu m : menus) {
            sum += m.getStock(); 
            }

        double avg = 0;
        if (!menus.isEmpty()) {
            avg = sum / menus.size(); 
            }

        PriorityQueue<Menu> pq = new PriorityQueue<>(
            Comparator.comparing(Menu::getStock).reversed()
        );

        for (Menu m : menus) {
            if (m.getStock() >= avg) {
                pq.offer(m);
            }
        }

        for (int i = 0; i<=5 && !pq.isEmpty(); i++) {
            realmenu.add(pq.poll());
        }
    }

    public List<Menu> getMenuList() {
        return realmenu;
    }
}
