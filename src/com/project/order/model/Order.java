package com.project.order.model;

import com.project.order.exception.SoldOutException;

public class Order extends BaseObject {
    private int menuId;
    private int qty;
    private String payMethod;
    public Order(int menuId,int qty,String payMethod) throws SoldOutException {
        super(); this.menuId=menuId; this.qty=qty; this.payMethod=payMethod;
    }
    public int getMenuId(){return menuId;} public int getQty(){return qty;} public String getPayMethod(){return payMethod;}
    @Override public String toRecord() {
        return super.toRecord()+"|"+menuId+"|"+qty+"|"+payMethod;
    }
    public static Order fromRecord(String r) throws Exception {
        String[] f = r.split("\\|");
        return new Order(Integer.parseInt(f[2]), Integer.parseInt(f[3]), f[4]);
    }

}