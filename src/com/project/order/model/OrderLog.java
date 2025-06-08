package com.project.order.model;

import com.project.order.util.DateTime;

public class OrderLog extends BaseObject {
    private final String items;
    private final int total;

    public OrderLog(int id, DateTime ts, String items, int total) {
        super(id, ts);
        this.items = items;
        this.total = total;
    }

    public OrderLog(String items, int total) {
        super();
        this.items = items;
        this.total = total;
    }

    public String getItems() {
        return items;
    }

    public int getTotal() {
        return total; 
    }

    public static OrderLog fromRecord(String rec) {
        String[] f = rec.split("\\|", 4);
        int id    = Integer.parseInt(f[0].trim());
        DateTime ts    = DateTime.parse(f[1].trim());
        String items = f[2];
        int tot   = Integer.parseInt(f[3].trim());
        
        return new OrderLog(id, ts, items, tot);
    }

    public DateTime getTs() {
        return ts;
    }

    @Override
    public String toRecord() {
        return super.toRecord() + "|" + items + "|" + total;
    }
}
