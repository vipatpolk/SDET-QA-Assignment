package com.atait.assignment.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager {

    private static final Properties properties = new Properties();
    private static final Map<String, String> dotEnvVariables = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE = "config.properties";

    static {
        loadDotEnv();
        loadProperties();
    }

    private static void loadDotEnv() {
        File dotEnvFile = new File(".env");
        if (dotEnvFile.exists() && dotEnvFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dotEnvFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int equalsIndex = line.indexOf('=');
                    if (equalsIndex > 0) {
                        String key = line.substring(0, equalsIndex).trim();
                        String value = line.substring(equalsIndex + 1).trim();
                        // Strip optional enclosing quotes
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        dotEnvVariables.put(key, value);
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read .env file: " + e.getMessage());
            }
        }
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
        
        // 1. OS Environment Variable
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        // 2. JVM System Property
        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.trim().isEmpty()) {
            return sysValue.trim();
        }

        // 3. Local .env File
        if (dotEnvVariables.containsKey(envKey) && !dotEnvVariables.get(envKey).trim().isEmpty()) {
            return dotEnvVariables.get(envKey).trim();
        }
        if (dotEnvVariables.containsKey(key) && !dotEnvVariables.get(key).trim().isEmpty()) {
            return dotEnvVariables.get(key).trim();
        }

        // 4. Bundled config.properties
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            return propValue.trim();
        }

        return null;
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

    public static String getGoRestBaseUrl() {
        String baseUrl = getProperty("gorest.api.base.url", "https://gorest.co.in/public/v2");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public static String getGoRestToken() {
        return getProperty("gorest.api.token");
    }

    /**
     * Validates that the GoREST API token is configured.
     * Fails clearly with an informative error rather than silently skipping or producing vague 401s.
     */
    public static String validateGoRestTokenPresent() {
        String token = getGoRestToken();
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException(
                "CRITICAL: GoREST API Token is not configured!\n" +
                "Please configure GOREST_API_TOKEN in your local .env file or as an environment variable.\n" +
                "Example: GOREST_API_TOKEN=f70f0827fca46867982578bcdb90a6bf7b29f560088fa81366a5e7fd0ed6290a"
            );
        }
        return token;
    }
}
