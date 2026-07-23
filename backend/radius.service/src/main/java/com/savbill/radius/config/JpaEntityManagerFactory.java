package com.savbill.radius.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class JpaEntityManagerFactory {

	private static final Logger log = LoggerFactory.getLogger(JpaEntityManagerFactory.class);
    private static final String ERROR_MESSAGE = "Error while performing operation";

    private Class[] entityClasses;
    
    public JpaEntityManagerFactory(Class[] entityClasses) {
        this.entityClasses = entityClasses;
    }
            
    public EntityManager getEntityManager() {
    	return getEntityManagerFactory().createEntityManager();
    }
    
    public static void close(EntityManager em) {
    	if(em != null) {em.close();}
    }
    
    protected EntityManagerFactory getEntityManagerFactory() {
    	PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
        Map<String, Object> configuration = new HashMap<>();
        try{
            
            return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
                    .build();
        } catch (Exception ex){
            log.error(ERROR_MESSAGE,ex);
        }
        return null;
    }
    
    protected HibernatePersistenceUnitInfo getPersistenceUnitInfo(String name) {
        return new HibernatePersistenceUnitInfo(name, getEntityClassNames(), getProperties());
    }
    
    protected List<String> getEntityClassNames() {
        return Arrays.asList(getEntities())
          .stream()
          .map(Class::getName)
          .collect(Collectors.toList());
    }
    
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", dataSource());
        return properties;
    }
    
    @Bean
    @Primary
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        String dbURL=null;
        dbURL=System.getenv("spring.datasource.url");
        if(dbURL==null) {
        	dbURL=DbConfig.properties.getProperty("spring.datasource.url");
        }
        dataSource.setDriverClassName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(dbURL);
        dataSource.setUsername(DbConfig.properties.getProperty("spring.datasource.username"));
        dataSource.setPassword(DbConfig.properties.getProperty("spring.datasource.password"));
        return dataSource;
    }

    protected Class[] getEntities() {
        return entityClasses;
    }
    
}


