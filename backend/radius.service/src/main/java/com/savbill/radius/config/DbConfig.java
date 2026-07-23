package com.savbill.radius.config;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private static final Logger log = LoggerFactory.getLogger(DbConfig.class);
    public static final Properties properties;

    static {
        properties = new Properties();

        try {
            ClassLoader classLoader = DbConfig.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            properties.load(applicationPropertiesStream);
        } catch (Exception e) {
            log.error("Error while fetching properties",e);
        }
    }
}
