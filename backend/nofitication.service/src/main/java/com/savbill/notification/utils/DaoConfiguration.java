package com.savbill.notification.utils;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DaoConfiguration 
{

	@Bean
	public NamedParameterJdbcTemplate getTemplate( DataSource datasource)
	{
		return new NamedParameterJdbcTemplate(datasource);
	}

	@Bean
	public ModelMapper getModelMapper(){
		return new ModelMapper();
	}
}
