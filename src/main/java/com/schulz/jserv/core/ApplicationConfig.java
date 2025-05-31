package com.schulz.jserv.core;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class ApplicationConfig {

    private static volatile Map<String, Object> config;

    public static Map<String, Object> get() {
        if (config == null) {
            synchronized (ApplicationConfig.class) {
                if (config == null) {
                    Map<String, Object> temp = null;

                    Yaml yaml = new Yaml();
                    try (InputStream inputStream = ApplicationConfig.class.getClassLoader()
                            .getResourceAsStream("application.yaml")) {
                        if (inputStream == null) {
                            throw new RuntimeException("application.yaml not found in resources folder");
                        }
                        temp = yaml.load(inputStream);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to load application.yaml", e);
                    }

                    config = java.util.Collections.unmodifiableMap(temp);
                }
            }
        }
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String keyPath) {
        String[] parts = keyPath.split("\\.");
        Object current = get();

        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null; // path broken
            }
            current = ((Map<?, ?>) current).get(part);
        }

        try {
            // Attempt to cast the current object to the expected type
            return (T) current;
        } catch (ClassCastException e) {
            // If casting fails, return null
            return null;
        }
    }

}
