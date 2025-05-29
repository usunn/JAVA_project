package com.project.order.model;

public class Review extends BaseObject {
    private int menuId; private int rating; private String comment;
    public Review(int menuId,int rating,String comment) {
        super(); this.menuId=menuId; this.rating=rating; this.comment=comment;
    }
    public int getMenuId(){return menuId;} public int getRating(){return rating;} public String getComment(){return comment;}
    @Override public String toRecord() {
        return super.toRecord()+"|"+menuId+"|"+rating+"|"+comment;
    }
    public static Review fromRecord(String r) {
        String[] f = r.split("\\|");
        return new Review(Integer.parseInt(f[2]), Integer.parseInt(f[3]), f[4]);
    }
}