package com.savbill.radius.aaa.data;

import java.util.HashMap;
import java.util.Map;

public class CacheService {
    private static CacheService instance;
    private final Map<String, CustomerDetails> cache = new HashMap<>();

    // Private constructor to prevent instantiation
    public CacheService() {}

    // Public method to provide access to the instance
    public static synchronized CacheService getInstance() {
        if (instance == null) {
            instance = new CacheService();
        }
        return instance;
    }

    public void put(String key, CustomerDetails value) {
        cache.put(key, value);
    }

    public CustomerDetails get(String key) {
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}

