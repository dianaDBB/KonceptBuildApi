package com.konceptbuild.core.util;

import com.konceptbuild.core.filter.SortDirection;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Generic comparator builder for sorting with support for nested properties.
 * Uses reflection to dynamically access nested fields using dot notation (e.g., "client.companyName").
 */
public class ComparatorBuilder {

    /**
     * Build a comparator for a given property path and sort direction.
     * Supports nested properties using dot notation (e.g., "client.companyName").
     */
    public static <T> Comparator<T> buildComparator(String propertyPath, SortDirection sortDirection, Class<T> clazz) {
        return (obj1, obj2) -> {
            Object value1 = getNestedProperty(obj1, propertyPath);
            Object value2 = getNestedProperty(obj2, propertyPath);

            return compareValues(value1, value2, sortDirection);
        };
    }

    /**
     * Get nested property value using dot notation.
     * Example: "client.companyName" will call getClient().getCompanyName()
     */
    private static Object getNestedProperty(Object obj, String propertyPath) {
        if (obj == null || propertyPath == null) {
            return null;
        }

        String[] parts = propertyPath.split("\\.");
        Object current = obj;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            try {
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                current = field.get(current);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }

        return current;
    }

    /**
     * Compare two values with proper null handling and type support.
     */
    private static int compareValues(Object value1, Object value2, SortDirection sortDirection) {
        // Handle nulls
        if (value1 == null && value2 == null) {
            return 0;
        }
        if (value1 == null) {
            return sortDirection == SortDirection.DESC ? -1 : 1;
        }
        if (value2 == null) {
            return sortDirection == SortDirection.DESC ? 1 : -1;
        }

        // Compare strings (case-insensitive)
        if (value1 instanceof String str1 && value2 instanceof String str2) {
            int result = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            return sortDirection == SortDirection.DESC ? -result : result;
        }

        // Compare numbers
        if (value1 instanceof Number num1 && value2 instanceof Number num2) {
            double d1 = num1.doubleValue();
            double d2 = num2.doubleValue();
            int result = Double.compare(d1, d2);
            return sortDirection == SortDirection.DESC ? -result : result;
        }

        // Compare dates
        if (value1 instanceof LocalDate date1 && value2 instanceof LocalDate date2) {
            int result = date1.compareTo(date2);
            return sortDirection == SortDirection.DESC ? -result : result;
        }

        // Compare comparables
        if (value1 instanceof Comparable<?> comp1 && value2 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked")
            int result = ((Comparable<Object>) comp1).compareTo(value2);
            return sortDirection == SortDirection.DESC ? -result : result;
        }

        return 0;
    }
}
