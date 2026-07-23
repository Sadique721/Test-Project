package com.savbill.commonGateway.spring;


import com.savbill.commonGateway.constants.CacheConstant;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheConstant.COMMONTYPE, CacheConstant.ALL_COMMONTYPE, CacheConstant.CLIENT_SRV);
    }
}
