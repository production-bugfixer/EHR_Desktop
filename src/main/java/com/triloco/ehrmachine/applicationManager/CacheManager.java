package com.triloco.ehrmachine.applicationManager;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private static final String CACHE_FILE = "cache.dat";
    private static Map<String, Object> cache = new HashMap<>();

    
    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static <T> T get(String key, Class<T> type) {
        Object value = cache.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void clear() {
        cache.clear();
    }

    public static void saveCache() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(cache);
        } catch (IOException e) {
            System.err.println("Failed to save cache: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadCache() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) {
            createEmptyCacheFile();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                cache = (Map<String, Object>) obj;
            } else {
                System.err.println("Cache file is corrupted. Resetting.");
                cache = new HashMap<>();
                createEmptyCacheFile();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading cache file: " + e.getMessage());
            cache = new HashMap<>();
            createEmptyCacheFile();
        }
    }

    private static void createEmptyCacheFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(cache); // empty map
        } catch (IOException e) {
            System.err.println("Unable to create empty cache file: " + e.getMessage());
        }
    }
}
