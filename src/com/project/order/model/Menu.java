package com.project.order.model;

public class Menu extends BaseObject {
    private String name;
    private int price;
    private int stock;
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
}