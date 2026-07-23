package com.savbill.radius;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class InitializeSpringProperties 
{
	@Value("${spring.datasource.url}")
	private String dbUrl;
	@Value("${spring.datasource.username}")
	private String dbUserName;
	@Value("${spring.datasource.password}")
	private String dbPassword;
	
	
	/*
	public void setPropertiesInNonSpringClass()
	{
		JpaEntityManagerFactory jpaEntity = new JpaEntityManagerFactory();
		jpaEntity.setValueFromEnvironments(dbUrl,dbUserName,dbPassword);
		
		DBAccountingDriver dbAcctDriver = new DBAccountingDriver();
		dbAcctDriver.setValueFromEnvironments(dbUrl, dbUserName, dbPassword);
		
		CachingConfigurations cachingConfigurations = new CachingConfigurations(true);
		cachingConfigurations.setValueFromEnvironments(dbUrl, dbUserName, dbPassword);
		
		DBAuthenticationDriver dbAuthenticationDriver = new DBAuthenticationDriver();
		dbAuthenticationDriver.setValueFromEnvironments(dbUrl, dbUserName, dbPassword);
	}
	*/
}
