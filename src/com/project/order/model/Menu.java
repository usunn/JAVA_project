package com.project.order.model;

import java.util.Comparator;

public class Menu extends BaseObject implements Comparable<Menu>{
    protected String name;
    protected int price;
    protected int stock;

    public Menu(String name, int price, int stock) {
        super(); this.name=name; this.price=price; this.stock=stock;
    }
    public String getName(){return name;} public int getPrice(){return price;} public int getStock(){return stock;}
    public void setStock(int stock){this.stock=stock;}
    @Override public String toRecord() {
        return super.toRecord()+"|"+name+"|"+price+"|"+stock;
    }
    public static Menu fromRecord(String r) {
        String[] f = r.split("\\|");
        // f[0]=id, f[1]=ts, f[2]=name, f[3]=price, f[4]=stock
        return new Menu(f[2], Integer.parseInt(f[3]), Integer.parseInt(f[4]));
    }
    
	class MenuComparator implements Comparator<Menu>
	{

		@Override
		public int compare(Menu o1, Menu o2) {
			if(o1.stock == o2.stock)
			{
				return o1.price - o2.price;
			}
			return o1.stock - o2.stock;
		}
		
	}

	@Override
    public int compareTo(Menu other) {
        return Integer.compare(this.stock, other.stock); 
    }	
	
}