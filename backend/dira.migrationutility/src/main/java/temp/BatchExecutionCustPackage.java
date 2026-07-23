package temp;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchExecutionCustPackage {

    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";
    
    private static final Logger LOGGER = Logger.getLogger(BatchExecutionCustPackage.class.getName());

    public static void main(String[] args) {
        // Establishing connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // First, create the stored procedure
            createStoredProcedure(conn);

            // Then, execute the stored procedure with dynamic LIMIT and batch size
            int dynamicLimit = 50001;  // You can change this value dynamically based on your requirement
            int batchSize = 25000;  // Processing in batches of 10,000
            executeStoredProcedure(conn, dynamicLimit, batchSize);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }

    // Method to create the stored procedure
    private static void createStoredProcedure(Connection conn) throws SQLException {
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS ProcessCustPackage";
        String createProcedureSQL =
                "CREATE PROCEDURE `ProcessCustPackage`(IN dynamic_limit INT, IN batch_size INT)\n" +
                "BEGIN\n" +
                "    DECLARE offset INT DEFAULT 0;\n" +
                "    -- Loop through the table in batches of 'batch_size' rows\n" +
                "    WHILE offset < dynamic_limit DO\n" +
                "        -- Create temporary table for batch processing\n" +
                "        CREATE TEMPORARY TABLE tmp_update AS\n" +
                "        SELECT t2.custpackageid, t2.createdate, t2.startdate, t2.enddate, t2.expirydate\n" +
                "        FROM Savbillcpm.tblcustpackagerel AS t2\n" +
                "        ORDER BY t2.custpackageid DESC\n" +
                "        LIMIT batch_size OFFSET offset;\n" +
                "\n" +
                "        -- Perform the update using the temporary table\n" +
                "        UPDATE savbillradius.tblcustpackagerel AS t1\n" +
                "        JOIN tmp_update AS tmp\n" +
                "        ON t1.custpackageid = tmp.custpackageid\n" +
                "        SET t1.createdate = tmp.createdate,\n" +
                "            t1.startdate = tmp.startdate,\n" +
                "            t1.enddate = tmp.enddate,\n" +
                "            t1.expirydate = tmp.expirydate;\n" +
                "\n" +
                "        -- Drop the temporary table after processing\n" +
                "        DROP TEMPORARY TABLE IF EXISTS tmp_update;\n" +
                "\n" +
                "        -- Increment the offset by the batch size for the next batch\n" +
                "        SET offset = offset + batch_size;\n" +
                "    END WHILE;\n" +
                "END;";

        try (Statement stmt = conn.createStatement()) {
            // Drop the procedure if it exists
            stmt.executeUpdate(dropProcedureSQL);
            // Create the procedure
            stmt.executeUpdate(createProcedureSQL);
            LOGGER.info("Stored procedure 'ProcessCustPackage' created successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    // Method to execute the stored procedure with dynamic LIMIT and batch size
    private static void executeStoredProcedure(Connection conn, int dynamicLimit, int batchSize) {
        String callProcedureSQL = "{CALL ProcessCustPackage(?, ?)}";  // Stored procedure call with dynamic limit and batch size
        
        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            // Set the dynamic limit and batch size for the stored procedure parameters
            stmt.setInt(1, dynamicLimit);
            stmt.setInt(2, batchSize);

            // Execute the stored procedure
            stmt.execute();
            LOGGER.info("Stored procedure 'ProcessCustPackage' executed successfully with dynamic LIMIT: " + dynamicLimit + " and batch size: " + batchSize);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}
