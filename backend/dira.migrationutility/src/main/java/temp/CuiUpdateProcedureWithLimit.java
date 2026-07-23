package temp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuiUpdateProcedureWithLimit {

    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=120000";  // Increased socketTimeout
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create the Cuiupdate stored procedure
            createCuiUpdateProcedure(conn);

            // Execute the Cuiupdate stored procedure with a dynamic LIMIT value
            int dynamicLimit = 1000;  // You can change this value dynamically
            executeCuiUpdateProcedure(conn, dynamicLimit);

        } catch (SQLException e) {
            Logger.getLogger(CuiUpdateProcedureWithLimit.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }

    // Method to create the Cuiupdate stored procedure
    private static void createCuiUpdateProcedure(Connection conn) throws SQLException {
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `Cuiupdate`";
        String createProcedureSQL = 
                "CREATE PROCEDURE `Cuiupdate`(IN dynamic_limit INT) " +
                "BEGIN " +
                "    DECLARE done INT DEFAULT FALSE; " +
                "    DECLARE v_username VARCHAR(100); " +
                "    DECLARE v_cui VARCHAR(100); " +
                "    DECLARE v_custid INT; " +
                "    DECLARE v_firstactivationdate DATETIME; " +
                "    DECLARE v_createdate DATETIME; " +
                "    DECLARE cur CURSOR FOR " +
                "        SELECT username, accountnumber, custid, firstactivationdate, createdate " +
                "        FROM Savbillcpm.tblcustomers " +
                "        LIMIT dynamic_limit; " +
                "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; " +
                "    OPEN cur; " +
                "    read_loop: LOOP " +
                "        FETCH cur INTO v_username, v_cui, v_custid, v_firstactivationdate, v_createdate; " +
                "        IF done THEN " +
                "            LEAVE read_loop; " +
                "        END IF; " +
                "        UPDATE savbillradius.tblcustomers " +
                "        SET accountnumber = COALESCE(v_cui, v_username), " +
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
            System.out.println("Stored procedure 'Cuiupdate' created successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CuiUpdateProcedureWithLimit.class.getName()).log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    // Method to execute the Cuiupdate stored procedure with dynamic LIMIT
    private static void executeCuiUpdateProcedure(Connection conn, int dynamicLimit) {
        String callProcedureSQL = "{CALL Cuiupdate(?)}";  // Pass the dynamic limit as a parameter
        
        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            // Set the dynamic limit value as the parameter
            stmt.setInt(1, dynamicLimit);

            // Execute the stored procedure
            stmt.execute();
            System.out.println("Stored procedure 'Cuiupdate' executed successfully with dynamic LIMIT: " + dynamicLimit);
        } catch (SQLException e) {
            Logger.getLogger(CuiUpdateProcedureWithLimit.class.getName()).log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}
