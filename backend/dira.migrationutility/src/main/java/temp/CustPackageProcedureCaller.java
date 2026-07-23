package temp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustPackageProcedureCaller {
    
    // JDBC connection details
    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";
    
    public static void main(String[] args) {
        // Establish connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Call the stored procedure
            callCustPackageProcedure(conn);
            
        } catch (SQLException e) {
            Logger.getLogger(CustPackageProcedureCaller.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }
    
    /**
     * Method to call the stored procedure
     */
    private static void callCustPackageProcedure(Connection conn) {
        // SQL to call the stored procedure
        String callProcedure = "{ CALL custpackage() }";
        
        try (CallableStatement callableStatement = conn.prepareCall(callProcedure)) {
            // Execute the stored procedure
            callableStatement.execute();
            System.out.println("Stored procedure 'custpackage' executed successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CustPackageProcedureCaller.class.getName()).log(Level.SEVERE, "Error calling stored procedure", e);
        }
    }
}
