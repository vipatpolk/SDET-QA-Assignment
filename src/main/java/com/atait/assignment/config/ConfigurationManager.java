package com.atait.assignment.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_FILE = "config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            System.err.println("Failed to load " + DEFAULT_CONFIG_FILE + ": " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.trim().isEmpty()) {
            return sysValue.trim();
        }

        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static String getBaseUrl() {
        String baseUrl = getProperty("app.base.url", "https://www.saucedemo.com");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public static String getUrl(String path) {
        String base = getBaseUrl();
        if (path == null || path.isEmpty()) {
            return base + "/";
        }
        if (path.startsWith("/")) {
            return base + path;
        }
        return base + "/" + path;
    }
}
