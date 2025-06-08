package com.project.order.service;

import com.project.order.model.Sort;
import com.project.order.model.Menu;
import com.project.order.model.Recommend;
import java.util.*;

public class RecommendService
{
	
	public Menu getRecommend (List<Menu> menuList) throws Exception {
        List<Menu> rmenu = menuList;

		Sort sorted = new Sort();
		sorted.sortList(rmenu);
		
		Recommend recomm = new Recommend();
		
		Menu rec = recomm.recommend(sorted);

       return rec;

	}
	
	
}
