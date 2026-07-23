package com.savbill.cpm.spring;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    @Value("${redis.sentinel.master:}")
    private String master;

    @Value("${redis.sentinel.nodes:}")
    private String sentinelNodes;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${redis.sentinel.enabled:false}")     // IMPORTANT
    private boolean sentinelEnabled;


    /**
     * AUTO SWITCH:
     * - If sentinelEnabled = true → Sentinel mode
     * - Else → Standalone mode
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        try{LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .readFrom(ReadFrom.MASTER_PREFERRED)
                        .build();

        System.out.println("========= REDIS CONNECTION DEBUG =========");
        System.out.println("Redis Sentinel Enabled : " + sentinelEnabled);
        System.out.println("Redis Host              : " + host);
        System.out.println("Redis Port              : " + port);
        System.out.println("Redis Password Provided : " + (password != null && !password.isEmpty() ? "YES (masked)" : "NO"));
        System.out.println("Redis Sentinel Master   : " + master);
        System.out.println("Redis Sentinel Nodes    : " + sentinelNodes);
        System.out.println("==========================================");

        // --------- SENTINEL MODE ---------
        if (sentinelEnabled) {
            System.out.println(">>> INITIALIZING REDIS IN SENTINEL MODE");

            RedisSentinelConfiguration sentinelConfig =
                    new RedisSentinelConfiguration().master(master);

            for (String node : sentinelNodes.split(",")) {
                String[] hp = node.split(":");
                System.out.println("Adding Sentinel Node: " + hp[0] + ":" + hp[1]);
                sentinelConfig.sentinel(hp[0], Integer.parseInt(hp[1]));
            }

            if (password != null && !password.isEmpty()) {
                sentinelConfig.setPassword(RedisPassword.of(password));
                System.out.println("Sentinel Auth Enabled: YES (masked)");

            }

            return new LettuceConnectionFactory(sentinelConfig, clientConfig);
        }

        // --------- STANDALONE MODE ---------
        System.out.println(">>> INITIALIZING REDIS IN STANDALONE MODE");
        RedisStandaloneConfiguration standaloneConfig =
                new RedisStandaloneConfiguration(host, port);

        if (password != null && !password.isEmpty()) {
            standaloneConfig.setPassword(RedisPassword.of(password));
        }

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }catch (Exception ex) {
            System.err.println("***** REDIS INIT FAILED – FALLING BACK TO NO-CACHE MODE *****");
            System.err.println("Reason: " + ex.getMessage());
            // Return a NO-OP connection that never throws error
            return new LettuceConnectionFactory(new RedisStandaloneConfiguration("invalid-host", 0));
        }}

    @Bean
    public RedisTemplate<String, Object> redisTemplates(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }
}
