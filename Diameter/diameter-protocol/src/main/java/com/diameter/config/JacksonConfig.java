package com.diameter.config;

import com.diameter.util.BooleanToShortDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class JacksonConfig {

    @Bean
    public Module booleanToShortModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Short.class, new BooleanToShortDeserializer());
        module.addDeserializer(short.class, new BooleanToShortDeserializer());
        return module;
    }
 
@Bean
@Primary
public ObjectMapper objectMapper(List<Module> modules) {
    ObjectMapper mapper = new ObjectMapper();

    // register default modules
    mapper.registerModule(new JavaTimeModule());

    //  IMPORTANT: register all custom modules (including BooleanToShort)
    for (Module module : modules) {
        mapper.registerModule(module);
    }

    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return mapper;
 }
}