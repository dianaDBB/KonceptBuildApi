package com.konceptbuild.core.util;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FilterHelper {

    public record RangeFilter<T extends Comparable<? super T>>(T min, T max) {
    }

    public static boolean matchesString(String value, String query) {
        return query == null
                || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    public static boolean matchesString(List<String> values, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);

        return values.stream()
                .filter(Objects::nonNull)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(normalizedQuery));
    }

    public static <T extends Comparable<? super T>> boolean isWithinRange(T value, RangeFilter<T> range) {
        if (range == null) {
            return true;
        }

        if (value == null) {
            return false;
        }

        validateRange(range);

        return (range.min() == null || value.compareTo(range.min()) >= 0)
                && (range.max() == null || value.compareTo(range.max()) <= 0);
    }

    public static <T extends Comparable<? super T>> void validateRange(RangeFilter<T> range) {
        if (range == null) {
            return;
        }

        if (range.min() != null
                && range.max() != null
                && range.min().compareTo(range.max()) > 0) {
            throw new IllegalArgumentException("MIN must not exceed MAX");
        }
    }

    public static <T> boolean matchesEnum(T value, T query) {
        return query == null || value == query;
    }
}