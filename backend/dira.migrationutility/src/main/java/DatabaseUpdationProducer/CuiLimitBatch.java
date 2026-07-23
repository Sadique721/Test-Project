package DatabaseUpdationProducer;
import utility.Constant;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuiLimitBatch {

    private static final String DB_URL = Constant.URLCONVERGE;
    private static final String DB_USER = Constant.USERNAME;
    private static final String DB_PASSWORD = Constant.PASSWORD;

    private static final Logger LOGGER = Logger.getLogger(CuiLimitBatch.class.getName());

    public static void executeCustomersUpdate() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create the Customer stored procedure
            createCustomerProcedure(conn);

            // Execute the Customer stored procedure with a dynamic LIMIT value
            int dynamicLimit = Constant.LIMIT+1;  // You can change this value dynamically
            int batchSize = Constant.BATCH; // Set the batch size dynamically
            executeCustomerProcedure(conn, dynamicLimit, batchSize);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }

    // Method to create the Customer stored procedure
    private static void createCustomerProcedure(Connection conn) throws SQLException {
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `Customer`";
        String createProcedureSQL =
        		"CREATE PROCEDURE `Customer`(IN dynamic_limit INT, IN batch_size INT) " +
                        "BEGIN " +
                        "    DECLARE offset INT DEFAULT 0; " +
                        "    DECLARE done INT DEFAULT FALSE; " +
                        "    DECLARE v_cui VARCHAR(200); " +
                        "    DECLARE v_createdate DATETIME; " +
						"    DECLARE v_lastlogin DATETIME; " +
                        "    DECLARE v_firstactivationdate DATETIME; " +
                        "    DECLARE v_custid INT; " +
                        "    DECLARE cur CURSOR FOR " +
                        "        SELECT accountnumber, custid, firstactivationdate, createdate, last_login_time " +
                        "        FROM Savbillcpm.tblcustomers " +
                        "        ORDER BY custid DESC " +
                        "        LIMIT dynamic_limit OFFSET offset; " +
                        "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; " +
                        "    OPEN cur; " +
                        "    read_loop: LOOP " +
                        "        FETCH cur INTO v_cui, v_custid, v_firstactivationdate, v_createdate, v_lastlogin; " +
                        "        IF done THEN " +
                        "            LEAVE read_loop; " +
                        "        END IF; " +
                        "        UPDATE savbillradius.tblcustomers " +
                        "        SET accountnumber = v_cui, " +
                        "            firstactivationdate = v_firstactivationdate, " +
						"            last_login_time = v_lastlogin, " +
                        "            createdate = v_createdate " +
                        "        WHERE custid = v_custid; " +
                        "        SET offset = offset + batch_size; " +
                        "    END LOOP; " +
                        "    CLOSE cur; " +
                        "END";

        try (Statement stmt = conn.createStatement()) {
            // Drop the procedure if it exists
            stmt.executeUpdate(dropProcedureSQL);
            // Create the procedure
            stmt.executeUpdate(createProcedureSQL);
            LOGGER.info("Stored procedure 'Customer' created successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    // Method to execute the Customer stored procedure with dynamic LIMIT and batch size
    private static void executeCustomerProcedure(Connection conn, int dynamicLimit, int batchSize) {
        String callProcedureSQL = "{CALL Customer(?, ?)}";  // Pass the dynamic limit and batch size as parameters

        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            // Set the dynamic limit and batch size values as the parameters
            stmt.setInt(1, dynamicLimit);
            stmt.setInt(2, batchSize);

            // Execute the stored procedure
            stmt.execute();
            LOGGER.info("Stored procedure 'Customer' executed successfully with dynamic LIMIT: " + dynamicLimit + " and batch size: " + batchSize);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}

