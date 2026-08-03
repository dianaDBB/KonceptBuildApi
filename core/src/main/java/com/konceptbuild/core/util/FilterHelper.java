package com.konceptbuild.core.util;

import java.util.Locale;

/**
 * Generic utility for filtering operations across all services.
 * Provides common filter predicates to reduce code duplication.
 */
public class FilterHelper {

    /**
     * Case-insensitive string contains filter.
     * Returns true if query is null (no filter) or value contains query.
     */
    public static boolean matchesString(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    /**
     * Range filter for Doubles. Supports open ranges (null = no limit).
     */
    public static boolean isWithinRange(Double value, Double min, Double max) {
        if (value == null) {
            return false;
        }
        return (min == null || value >= min) && (max == null || value <= max);
    }

    /**
     * Range filter for Longs. Supports open ranges (null = no limit).
     */
    public static boolean isWithinRange(Long value, Long min, Long max) {
        if (value == null) {
            return false;
        }
        return (min == null || value >= min) && (max == null || value <= max);
    }

    /**
     * Range filter for Integers. Supports open ranges (null = no limit).
     */
    public static boolean isWithinRange(Integer value, Integer min, Integer max) {
        if (value == null) {
            return false;
        }
        return (min == null || value >= min) && (max == null || value <= max);
    }

    /**
     * Equality filter for nullable values.
     */
    public static <T> boolean matchesEnum(T value, T query) {
        return query == null || value == query;
    }
}
