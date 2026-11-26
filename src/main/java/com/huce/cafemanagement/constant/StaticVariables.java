package com.huce.cafemanagement.constant;

import java.util.Map;
import java.util.Set;

public final class StaticVariables {
    public static final String PRODUCT_STATUS_INACTIVE = "inactive";

    public static final Map<String, Set<String>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            "pending", Set.of("confirmed", "cancelled"),
            "confirmed", Set.of("processing", "cancelled"),
            "processing", Set.of("shipped", "cancelled"),
            "shipped", Set.of("completed"),
            "completed", Set.of(),
            "cancelled", Set.of());

    private StaticVariables() {
    }
}