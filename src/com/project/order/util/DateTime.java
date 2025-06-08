package com.project.order.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private LocalDateTime dt;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTime(LocalDateTime dt) {
        this.dt = dt; 
    }

    public static DateTime now() { 
        return new DateTime(LocalDateTime.now());
    }

    public static DateTime parse(String s) {
        return new DateTime(LocalDateTime.parse(s, FMT));
    }

    public String toRecord() {
        return dt.format(FMT);
    }
}