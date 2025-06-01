package com.project.order.service;

import com.project.order.model.*;
import java.util.*;

public class RecommendService
{
	
	public List<Menu> getRecommend (List<Menu> menuList) throws Exception
	{
		MenuService service = new MenuService();
        List<Menu> rmenuList = service.getAll();

		Sort sorted = new Sort();
		sorted.sortList(rmenuList);
		
		Recommend recomm = new Recommend();
		List<Menu> rec = recomm.recommend(sorted);

       return rec;

	}
	
	
}
