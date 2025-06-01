package com.project.order.model;

import java.util.*;

public class Recommend{
	
	Random random = new Random();
	int index1,index2;
	
	private List<Menu> realmenu;
	public List<Menu> recommend(Sort o)
	{
		realmenu = o.getMenuList();
		int size = realmenu.size();
		index1 = random.nextInt(size);
		index2 = random.nextInt(size);
		
		while(index1 == index2)
		{
			index2 = random.nextInt(5);
		}
		
		List<Menu> result = new ArrayList<>();
		result.add(realmenu.get(index1));
		result.add(realmenu.get(index2));
		return result;
		
		
	}

}
