package temp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuiLimit {
	private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=120000";  // Increased socketTimeout


    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create the Usedquota stored procedure
        	createCustomerProcedure(conn);

            // Execute the Usedquota stored procedure with a dynamic LIMIT value
            int dynamicLimit = 40000;  // You can change this value dynamically
            executeUsedCustomerProcedure(conn, dynamicLimit);

        } catch (SQLException e) {
            Logger.getLogger(CuiLimit.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }

	
	 private static void createCustomerProcedure(Connection conn) throws SQLException {
	        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `Customer`";
	        String createProcedureSQL = 
	                "CREATE PROCEDURE `Customer`(IN dynamic_limit INT) " +
	                "BEGIN " +
	                "    DECLARE done INT DEFAULT FALSE; " +
	                "    DECLARE v_cui VARCHAR(200); " +
	                "    DECLARE v_createdate DATETIME; " +
	                "    DECLARE v_firstactivationdate DATETIME; " +
	                "    DECLARE v_custid INT; " +
	                "    DECLARE cur CURSOR FOR " +
	                "        SELECT accountnumber, custid, firstactivationdate, createdate " +
	                "        FROM Savbillcpm.tblcustomers " +
	                "        ORDER BY custid DESC " +
	                "        LIMIT dynamic_limit; " +
	                "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; " +
	                "    OPEN cur; " +
	                "    read_loop: LOOP " +
	                "        FETCH cur INTO v_cui, v_custid, v_firstactivationdate, v_createdate; " +
	                "        IF done THEN " +
	                "            LEAVE read_loop; " +
	                "        END IF; " +
	                "        UPDATE savbillradius.tblcustomers " +
	                "        SET accountnumber = v_cui, " +
	                "            firstactivationdate = v_firstactivationdate, " +
	                "            createdate = v_createdate " +
	                "        WHERE custid = v_custid; " +
	                "    END LOOP; " +
	                "    CLOSE cur; " +
	                "END";

	        try (Statement stmt = conn.createStatement()) {
	            // Drop the procedure if it exists
	            stmt.executeUpdate(dropProcedureSQL);
	            // Create the procedure
	            stmt.executeUpdate(createProcedureSQL);
	            System.out.println("Stored procedure 'Customer' created successfully.");
	        } catch (SQLException e) {
	            Logger.getLogger(CuiLimit.class.getName()).log(Level.SEVERE, "Error creating stored procedure", e);
	        }
	    }
	 // Method to execute the Usedquota stored procedure with dynamic LIMIT
	    private static void executeUsedCustomerProcedure(Connection conn, int dynamicLimit) {
	        String callProcedureSQL = "{CALL Customer(?)}";  // Pass the dynamic limit as a parameter
	        
	        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
	            // Set the dynamic limit value as the parameter
	            stmt.setInt(1, dynamicLimit);

	            // Execute the stored procedure
	            stmt.execute();
	            System.out.println("Stored procedure 'Customer' executed successfully with dynamic LIMIT: " + dynamicLimit);
	        } catch (SQLException e) {
	            Logger.getLogger(CuiLimit.class.getName()).log(Level.SEVERE, "Error executing stored procedure", e);
	        }
	    }
}
