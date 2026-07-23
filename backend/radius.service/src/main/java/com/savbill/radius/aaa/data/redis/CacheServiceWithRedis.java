package com.savbill.radius.aaa.data.redis;

import com.savbill.radius.aaa.data.CustomerDetails;
import com.savbill.radius.utils.ApplicationContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class CacheServiceWithRedis {

    private static final Logger log = LoggerFactory.getLogger(CacheServiceWithRedis.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;

    // Local cache (in-memory HashMap)
//    private final Map<String, CustomerDetails> localCache = new HashMap<>();

    // Constructor-based Dependency Injection
    @Autowired
    public CacheServiceWithRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    // Put data in both local and Redis caches with TTL
    public void put(String key, CustomerDetails value) {
        // Store in local cache
//        localCache.put(key, value);

        // Store in Redis with TTL
        try {
            valueOperations.set(key, value);
            log.info("Add cache in redis for mac: " + key);
            // To set a TTL (time-to-live), use redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("Exception to add cache in redis for mac: " + key + " ,exception: " + e.getMessage());
            System.err.println("Failed to store data in Redis: " + e.getMessage());
        }
    }

    // Get data, first check local cache, then Redis
    public CustomerDetails get1(String key) {
        // Check local cache
//        CustomerDetails value = localCache.get(key);
//        if (value != null) {//TODO: Need to handle this for performance
//            return value;
//        }

        // If not in local cache, check Redis
        try {
            Object redisValue = valueOperations.get(key);
            if (redisValue != null) {
                CustomerDetails value = (CustomerDetails) redisValue;

                // Store the retrieved data in local cache for future requests
//                localCache.put(key, value);
                log.info("get cache from redis for mac: " + key);
                return value;
            }
        } catch (Exception e) {
            log.info("Exception to get cache in redis for mac: " + key + " ,exception: " + e.getMessage());
            System.err.println("Failed to retrieve data from Redis: " + e.getMessage());
        }
        return null;
    }

    public CustomerDetails get(String key) {
        // Check local cache first (if implemented)
        // CustomerDetails value = localCache.get(key);
        // if (value != null) {
        //     return value;
        // }

        // If not in local cache, check Redis
        try {
            // Fetch the value from Redis
            Object redisValue = valueOperations.get(key);

            if (redisValue != null) {
                // Handle deserialization manually if needed
                if (redisValue instanceof LinkedHashMap) {
                    // Convert LinkedHashMap to CustomerDetails (use Jackson or any other deserialization method)
                    CustomerDetails value = convertMapToCustomerDetails((LinkedHashMap) redisValue);

                    // Store the retrieved data in local cache for future requests (if implemented)
                    // localCache.put(key, value);

                    log.info("get cache from redis for mac: " + key);
                    return value;
                } else {
                    // If the object is already a CustomerDetails instance
                    CustomerDetails value = (CustomerDetails) redisValue;
                    log.info("get cache from redis for mac: " + key);
                    return value;
                }
            }
        } catch (Exception e) {
            log.error("Exception to get cache in redis for mac: " + key + " ,exception: " + e.getMessage());
            System.err.println("Failed to retrieve data from Redis: " + e.getMessage());
        }
        return null;
    }

    private CustomerDetails convertMapToCustomerDetails(LinkedHashMap map) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, CustomerDetails.class);
    }


    // Remove data from both local and Redis caches
    public void remove(String key) {
        // Remove from local cache
//        localCache.remove(key);

        // Remove from Redis
        try {
            redisTemplate.delete(key);
            log.info("Remove cache in redis for mac: " + key);
        } catch (Exception e) {
            log.info("Exception to remove cache in redis for mac: " + key + " ,exception: " + e.getMessage());
            System.err.println("Failed to remove data from Redis: " + e.getMessage());
        }
    }

    // Clear both local and Redis caches
    public void clear() {
        // Clear local cache
//        localCache.clear();

        // Clear Redis cache
        try {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
        } catch (Exception e) {
            System.err.println("Failed to clear Redis cache: " + e.getMessage());
        }
    }

    public static CacheServiceWithRedis getInstance() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring ApplicationContext is not initialized.");
        }
        return context.getBean(CacheServiceWithRedis.class);
    }
}
