package com.project.order.model;

import com.project.order.persistence.Persistable;
import com.project.order.util.DateTime;

public abstract class BaseObject implements Persistable {
    protected int id;
    protected DateTime ts;
    private static int idCounter = 0;

    public BaseObject() { 
        this.id = ++idCounter; 
        this.ts = DateTime.now(); 
    }

    protected BaseObject(int id, DateTime ts) {
        this.id = id;
        this.ts = ts;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toRecord() {
        return id + "|" + ts.toRecord(); 
    }
}