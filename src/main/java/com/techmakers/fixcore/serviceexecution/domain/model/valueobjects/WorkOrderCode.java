package com.techmakers.fixcore.serviceexecution.domain.model.valueobjects;

import java.util.regex.Pattern;

public record WorkOrderCode(String value) {

    private static final Pattern FORMAT = Pattern.compile("^OT-(\\d{4})-(\\d{4,})$");

    public WorkOrderCode {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Work order codes follow OT-<year>-<sequence>");
        }
    }

    public static WorkOrderCode of(int year, int sequence) {
        return new WorkOrderCode("OT-%d-%04d".formatted(year, sequence));
    }

    public static String prefixFor(int year) {
        return "OT-%d-".formatted(year);
    }

    public int sequence() {
        var matcher = FORMAT.matcher(value);
        matcher.matches();
        return Integer.parseInt(matcher.group(2));
    }
}
