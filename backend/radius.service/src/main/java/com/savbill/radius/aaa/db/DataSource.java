package com.savbill.radius.aaa.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.savbill.radius.config.DbConfig;
import com.savbill.radius.utils.RadiusUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    
    static {

    }

    public static void setupDataSource() {
	    try {
	        String dbURL=null,dbUsername=null,dbPassword=null;
            
	        int dbMinPool=0,dbMaxPool=0,dbIdleTime=0;
	        
			try {
				dbMinPool=Integer.parseInt(RadiusUtils.readValueFromProperties("dbMinPool"));
				dbMaxPool=Integer.parseInt(RadiusUtils.readValueFromProperties("dbMaxPool"));
				dbIdleTime=Integer.parseInt(RadiusUtils.readValueFromProperties("dbIdleTime"));
			}
			catch(Exception e) {
				
			}
			
			if(dbMinPool==0) {
				dbMinPool=250;
			}
			if(dbMaxPool==0) {
				dbMaxPool=250;
			}
			if(dbIdleTime==0) {
				dbIdleTime=1000;
			}
	        
	        dbURL=System.getenv("spring.datasource.url");
	        dbUsername=System.getenv("spring.datasource.username");
            dbPassword=System.getenv("spring.datasource.password");
            if(dbURL==null) {
	        	dbURL=DbConfig.properties.getProperty("spring.datasource.url");
	        }
            
            if(dbUsername==null) {
            	dbUsername=DbConfig.properties.getProperty("spring.datasource.username");
	        }
            
            if(dbPassword==null) {
            	dbPassword=DbConfig.properties.getProperty("spring.datasource.password");
	        }
			System.out.println("DB POOL Intializaed:dbMinPool"+dbMinPool+":dbMaxPool:"+dbMaxPool+":dbIdleTime:"+dbIdleTime+"URL:"+dbURL+":USername:"+dbUsername+":Password:"+dbPassword);
	         config.setJdbcUrl(dbURL);
	         config.setUsername(dbUsername);
	         config.setPassword(dbPassword);
	         config.addDataSourceProperty("cachePrepStmts", true);
	         config.addDataSourceProperty("prepStmtCacheSize", 50240);
	         config.addDataSourceProperty("prepStmtCacheSqlLimit", 50240);
	         config.setMinimumIdle(dbMinPool);
	         config.setMaximumPoolSize(dbMaxPool);
	         config.setConnectionTimeout(dbIdleTime);
	         ds = new HikariDataSource(config);
	    }
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private DataSource() {
    	
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
