package com.triloco.ehrmachine.applicationManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final String DEFAULT_FILE = "application.properties";
    private static final String CONFIG_FOLDER = "/config/";

    private static Properties properties = new Properties();

    static {
        try (InputStream input = PropertiesLoader.class.getResourceAsStream(CONFIG_FOLDER + DEFAULT_FILE)) {
            if (input == null) {
                System.err.println("❌ Cannot find file: " + CONFIG_FOLDER + DEFAULT_FILE);
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load application.properties");
            e.printStackTrace();
        }
    }

    // Get value from application.properties
    public static String get(String key) {
        return properties.getProperty(key);
    }

    // Get value with default
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
