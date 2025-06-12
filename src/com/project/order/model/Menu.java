package com.project.order.model;

import java.util.Comparator;
import java.util.Objects;
import com.project.order.util.DateTime;

public class Menu extends BaseObject implements Comparable<Menu> {
    protected String name;
    protected int price;
    protected int stock;

    public Menu(String name, int price, int stock) {
        super(); 
        this.name = name; 
        this.price = price; 
        this.stock = stock;
    }

    public Menu(int id, DateTime ts, String name, int price, int stock) {
        super(id, ts);
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
    
    public int getStock() {
        return stock;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setPrice (int price) {
        this.price = price;
    }

    public void setStock (int stock) {
        this.stock = stock;
    }

    public int getId() {
        return super.id;
    }
    
    @Override
    public String toRecord() {
        return super.toRecord()+"|"+name+"|"+price+"|"+stock;
    }

    public static Menu fromRecord(String r) {
        String[] f = r.split("\\|");
        int id = Integer.parseInt(f[0].trim());
        DateTime ts = DateTime.parse(f[1].trim());
        String name = f[2];
        int price = Integer.parseInt(f[3].trim());
        int stock = Integer.parseInt(f[4].trim());

        return new Menu(id, ts, name, price, stock);
    }
    
	class MenuComparator implements Comparator<Menu> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;
        Menu other = (Menu) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}