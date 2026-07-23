package com.savbill.radius.aaa.db;

public class DataBaseUtility {
	/*
	private static final String JDBC_APACHE_COMMONS_DBCP = "jdbc:apache:commons:dbcp:";
	private static final Logger log = LoggerFactory.getLogger(DataBaseUtility.class);

    public static void setupDriver(String connectURI,String strUsername,String strPassword) throws RuntimeException {
       try {
		ConnectionFactory connectionFactory =
		        new DriverManagerConnectionFactory(connectURI,strUsername,strPassword);
		    PoolableConnectionFactory poolableConnectionFactory =
		        new PoolableConnectionFactory(connectionFactory, null);
		    ObjectPool<PoolableConnection> connectionPool =
		        new GenericObjectPool<>(poolableConnectionFactory);
		    poolableConnectionFactory.setPool(connectionPool);
		    poolableConnectionFactory.setMaxOpenPreparedStatements(200);
		    poolableConnectionFactory.setDefaultQueryTimeout(1000);
		    Class.forName("org.apache.commons.dbcp2.PoolingDriver");
		    PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(JDBC_APACHE_COMMONS_DBCP);
		    driver.registerPool(AAAConstant.POOLNAME,connectionPool);
	} catch (ClassNotFoundException e) {
		
		log.error(e.getMessage());
	} catch (SQLException e) {
		
		log.error(e.getMessage());
	}
    }

    public static void printDriverStats() throws RuntimeException {
        try {
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(JDBC_APACHE_COMMONS_DBCP);
			ObjectPool<? extends Connection> connectionPool = driver.getConnectionPool(AAAConstant.POOLNAME);
			if (log.isDebugEnabled()) {
				log.debug(String.format("NumActive: %s",connectionPool.getNumActive()));
				log.debug(String.format("NumIdle: %s",connectionPool.getNumIdle()));
			}
		} catch (SQLException e) {
			
			log.error(e.getMessage());
		}
    }

    public static void shutdownDriver() throws RuntimeException {
        try {
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(JDBC_APACHE_COMMONS_DBCP);
			driver.closePool(AAAConstant.POOLNAME);
		} catch (SQLException e) {
			
			log.error(e.getMessage());
		}
		
    }*/
}
