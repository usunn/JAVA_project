package com.project.order.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 타임스탬프 유틸 */
public class DateTime {
    private LocalDateTime dt;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateTime(LocalDateTime dt) { this.dt = dt; }
    public static DateTime now() { return new DateTime(LocalDateTime.now()); }
    public String toRecord() { return dt.format(FMT); }
}