package com.atait.assignment.api.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility for generating unique, collision-proof test data for live GoREST API runs.
 */
public class GoRestTestDataGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1);

    /**
     * Generates a unique, descriptive test name avoiding collisions across parallel test runs.
     */
    public static String generateUniqueName(String baseName) {
        String sanitized = (baseName == null || baseName.trim().isEmpty()) ? "Test User" : baseName.trim();
        long timestamp = System.currentTimeMillis() % 1000000;
        int count = counter.getAndIncrement();
        return String.format("%s (QA %d-%d)", sanitized, timestamp, count);
    }

    /**
     * Generates a unique, valid email address guaranteed not to collide on live service reruns.
     */
    public static String generateUniqueEmail(String prefix) {
        String cleanPrefix = (prefix == null || prefix.trim().isEmpty()) ? "user" : prefix.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (cleanPrefix.isEmpty()) {
            cleanPrefix = "user";
        }
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 8);
        long timestamp = System.currentTimeMillis();
        return String.format("%s.%d.%s@automation-test.io", cleanPrefix, timestamp, uuidSuffix);
    }
}
