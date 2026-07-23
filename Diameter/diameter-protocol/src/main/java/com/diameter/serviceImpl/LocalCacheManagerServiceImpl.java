package com.diameter.serviceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.diameter.service.CacheManagerService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class LocalCacheManagerServiceImpl implements CacheManagerService {
	
    private static final Map<String, CacheEntry> CACHE =new ConcurrentHashMap<>(1000000);


    @Override
    public void setValue(String key, Object value) {
        CACHE.put(key, new CacheEntry(value, -1)
        );
    }

    @Override
    public void setValueWithExpiry(String key,Object value,long time,TimeUnit unit) {
        long expiry =System.currentTimeMillis()+unit.toMillis(time);

        CACHE.put( key, new CacheEntry(value, expiry));
    }

    @Override
    public <T> T getValue(String key,Class<T> clazz) {
        CacheEntry entry =CACHE.get(key);

        if(entry == null) {
            return null;
        }

        if(entry.isExpired()) {
            CACHE.remove(key);
            return null;
        }

        return clazz.cast(entry.value);
    }


    @Override
    public void deleteKey(String key) {
        CACHE.remove(key);
    }

    @Override
    public boolean exists(String key) {
        CacheEntry entry = CACHE.get(key);

        if(entry == null) {
            return false;
        }

        if(entry.isExpired()) {
            CACHE.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public void setExpiry( String key,long time,TimeUnit unit) {
    	CacheEntry entry =CACHE.get(key);

        if(entry != null) {
            entry.expiryTimeMillis = System.currentTimeMillis()+unit.toMillis(time);
        }
    }

    @Override
    public Long increment(String key) {
        CacheEntry entry =
                CACHE.compute(
                    key,
                    (k,v)->{


                        if(v == null || v.isExpired()) {
                            return new CacheEntry( new AtomicLong(1),-1);
                        }

                        ((AtomicLong)v.value).incrementAndGet();

                        return v;
                    });

        return ((AtomicLong)entry.value).get();
    }


    @Override
    public Long decrement(String key) {
        CacheEntry entry =CACHE.compute(
                    key,
                    (k,v)->{


                        if(v == null || v.isExpired()) {
                            return new CacheEntry(new AtomicLong(-1), -1);
                        }

                        ((AtomicLong)v.value).decrementAndGet();
                        return v;

                    });

        return ((AtomicLong)entry.value).get();
    }

    @Scheduled(fixedDelayString = "${info.app.cacheCleanupInterval:300000}")
    public void cleanupExpiredCache() {
        long now =System.currentTimeMillis();

        int removed = 0;

        for(Map.Entry<String,CacheEntry> entry : CACHE.entrySet()) {

            CacheEntry cacheEntry =entry.getValue();

            if(cacheEntry.expiryTimeMillis > 0 && now > cacheEntry.expiryTimeMillis) {
                CACHE.remove(entry.getKey());
                removed++;
            }
        }

        if(removed > 0) {
            log.info("Cache cleanup removed {} entries, remaining={}",removed,CACHE.size());
        }

    }

    private static class CacheEntry {
        private Object value;
        private volatile long expiryTimeMillis;

        CacheEntry(Object value,long expiryTimeMillis) {
            this.value = value;
            this.expiryTimeMillis =
                    expiryTimeMillis;
        }

        boolean isExpired() {
            return expiryTimeMillis > 0 && System.currentTimeMillis() > expiryTimeMillis;

        }
    }

}