package com.techmakers.fixcore.analytics.domain.model.valueobjects;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public record Period(LocalDate from, LocalDate to) {

    public Period {
        if (from != null && to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("The period ends before it starts");
        }
    }

    public static Period unbounded() {
        return new Period(null, null);
    }

    public boolean includes(Instant at) {
        var day = at.atZone(ZoneId.systemDefault()).toLocalDate();
        return (from == null || !day.isBefore(from)) && (to == null || !day.isAfter(to));
    }
}
