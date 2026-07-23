package com.savbill.cpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.savbill.cpm.service.common.CustomersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;



@Service
public class CacheService<T> {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

  /*  private final Cache<String, T> localCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();*/

    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public T getFromCache(String key, Class<T> clazz) {
        // First, check the local cache
//        T cachedObject = localCache.getIfPresent(key);
        T cachedObject = (T) localCache.getIfPresent(key);
        if (cachedObject != null) {
            return cachedObject;
        }

        // Then check Redis
        try {
            String cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                T object = objectMapper.readValue(cachedData, clazz);
                localCache.put(key, object); // Store in local cache
                return object;
            }
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
            log.error("Redis cache read failed for key {}. Proceeding without cache.", key, e);
        }
        return null;
    }



    public void putInCacheWithExpire(String key, T object) {
        try {
            localCache.put(key, object); // Store in local cache
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(object)); // Store in Redis
            redisTemplate.expire(key, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
        }
    }
    public void putInCacheWithdynamicExpire(String key, T object,long timeout, TimeUnit timeUnit) {
        try {
            localCache.put(key, object); // Store in local cache
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(object)); // Store in Redis
            redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
        }
    }

    public void putInCache(String key, T object) {
        try {
            localCache.put(key, object); // Store in local cache
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(object)); // Store in Redis
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
        }
    }

    public <T> List<T> getListFromCache(String key, Class<T> clazz) {
        // First, check the local cache
        List<T> cachedObject = (List<T>) localCache.getIfPresent(key); // Get List<T> from the cache
        if (cachedObject != null) {
            return cachedObject;
        }

        // Then check Redis
        try {
            String cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                // Deserialize the data as a List of the specified type
                try {
                    List<T> list = objectMapper.readValue(cachedData, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
                    localCache.put(key, list); // Store List<T> in local cache
                    return list;
                } catch (Exception e) {
                    // Handle errors during deserialization
                    log.error("Raddit Error "+e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
        }
        return null;
    }

    // Method to store lists in cache
    public <T> void putListInCache(String key, List<T> list) {
        try {
            localCache.put(key, list); // Store List<T> in local cache
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list)); // Store List<T> in Redis
        } catch (Exception e) {
            log.error("Raddit Error "+e.getMessage());
        }
    }
    @Async
    public <T> CompletableFuture<Void> saveOrUpdateInCacheAsync(T obj, String cacheKey) {
        try {
            localCache.put(cacheKey, obj);
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(obj));

            System.out.println(obj.getClass().getSimpleName() + " added to cache with key: " + cacheKey);
        } catch (Exception e) {
            System.err.println("Failed to add " + obj.getClass().getSimpleName() + " to cache: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public <T> CompletableFuture<Void> saveOrUpdateInCacheAsync(List<T> objects, String cacheKey) {
        try {
            for (T obj : objects) {
                localCache.put(cacheKey, obj);
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(obj));
                System.out.println(obj.getClass().getSimpleName() + " added to cache with key: " + cacheKey);
            }
        } catch (Exception e) {
            System.err.println("Failed to add objects to cache: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> removeCacheAsync(String key) {
        try {
            // Remove from local cache (Caffeine)
            localCache.invalidate(key);

            // Remove from Redis
            redisTemplate.delete(key);

            log.info("Async cache removed for key: " + key);
        } catch (Exception e) {
            log.error("Async cache removal failed for key: " + key + " | Error: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }


}
