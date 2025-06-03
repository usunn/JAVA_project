package com.project.order.model;

import java.util.*;

public class Recommend{
	
	Random random = new Random();
	int index1,index2;
	
	private List<Menu> realmenu;
	public Menu recommend(Sort o)
	{
		realmenu = o.getMenuList();
		int size = realmenu.size();
		index1 = random.nextInt(size);
	
		Menu result = realmenu.get(index1);
		
		return result;
		
		
	}

}
