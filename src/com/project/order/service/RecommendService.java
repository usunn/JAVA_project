package com.project.order.service;

import com.project.order.model.*;
import java.util.*;

public class RecommendService
{
	
	public Menu getRecommend (List<Menu> menuList) throws Exception
	{
		MenuService service = new MenuService();
        List<Menu> rmenu = menuList;

		Sort sorted = new Sort();
		sorted.sortList(rmenu);
		
		Recommend recomm = new Recommend();
		
		Menu rec = recomm.recommend(sorted);

       return rec;

	}
	
	
}
