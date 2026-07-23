package com.diameter.service;

import java.util.concurrent.TimeUnit;

public interface CacheManagerService {

    void setValue(String key, Object value);

    void setValueWithExpiry(String key, Object value, long time, TimeUnit unit);

    <T> T getValue(String key, Class<T> clazz);

    void deleteKey(String key);

    boolean exists(String key);

    void setExpiry(String key, long time, TimeUnit unit);

    Long increment(String key);

    Long decrement(String key);
}
