package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {

	private static Connection connection;
	//private static String logFileName = "db.log"; //"order-execution.log"; //

	private static Connection getDBConnection() {
		try {
			if (connection == null) {
				connection = getConnection();
				connection.setAutoCommit(true);
				//System.out.println("connection = " + connection);
			}		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;

	}

	private static Connection getConnection() {
		Connection conn = null;
		try {
			
			String dbFileName = "test.db";
			
			// db parameters			
			String dbFilePath = Constant.BASE_PATH + "/TestData/datafiles/" + dbFileName;
			String url = "jdbc:sqlite:" + dbFilePath;
			//System.out.println("DB FilePath = " + dbFilePath);
			
			// create a connection to the database
			conn = DriverManager.getConnection(url);

			//Utility.printLog(logFileName,"DATABASE", "Connection to SQLite has been established.",String.valueOf(conn));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public int executeQuery(String strQuery) {
		//Utility.printLog(logFileName,"DATABASE", "Init Execute Query",strQuery);
		int queryReturnStatus=-1;
		Connection conn = null;
		Statement stmt = null;

		// creating connection to Oracle database using JDBC
		try {

			conn = getDBConnection();

			// creating statement object to execute query
			stmt = conn.createStatement();
			queryReturnStatus = stmt.executeUpdate(strQuery);

		} catch (SQLException se) {
			// Handle errors for JDBC
			System.out.println("Connection Error has occured...!");
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing

			/*
			 * try{ if(conn!=null) conn.close(); }catch(SQLException se){ }// do nothing
			 */
		}
		//Utility.printLog(logFileName,"DATABASE", "Execute Query Result",String.valueOf(queryReturnStatus));
		return queryReturnStatus;
	}

	public String selectData(String strQuery) {
		//Utility.printLog(logFileName,"DATABASE", "Init Select Query",strQuery);
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultset = null;
		String queryResult = null;
		
		// creating connection to Oracle database using JDBC
		try {

			conn = getDBConnection();

			// creating PreparedStatement object to execute query
			preStatement = conn.prepareStatement(strQuery);

			resultset = preStatement.executeQuery();

			if (resultset != null) {
			//if (resultset.next() != false) {
			//	resultset.beforeFirst();
				while (resultset.next()) {
					queryResult = (resultset.getString(1)).trim();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultset != null)
					resultset.close();
			} catch (SQLException se) {
			} // do nothing

			try {
				if (preStatement != null)
					preStatement.close();					
			} catch (SQLException se) {
			} // do nothing
		}
		String printResult = queryResult;
		if(queryResult==null) {
			printResult="null";
		} 
		//Utility.printLog(logFileName,"DATABASE", "Select Query Result",printResult);
		return queryResult;

	}


	



}
