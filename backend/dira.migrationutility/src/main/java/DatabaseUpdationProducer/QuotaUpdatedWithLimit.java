package DatabaseUpdationProducer;

import utility.Constant;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
// this producer work fine limit and batch
public class QuotaUpdatedWithLimit {

    private static final String DB_URL = Constant.URLCONVERGE;
    private static final String DB_USER = Constant.USERNAME;
    private static final String DB_PASSWORD = Constant.PASSWORD;

    private static final Logger LOGGER = Logger.getLogger(QuotaUpdatedWithLimit.class.getName());

    public static void executeQuotaUpdation() {
        // Establishing connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // First, create the stored procedure
            createUsedQuotaProcedure(conn);

            // Then, execute the stored procedure with dynamic LIMIT and batch size
            int dynamicLimit = Constant.LIMIT+1;  // You can change this value dynamically based on your requirement
            int batchSize = Constant.BATCH;  // Processing in batches of 10,000
            executeUsedQuotaProcedure(conn, dynamicLimit, batchSize);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }

    // Method to create the Usedquota stored procedure
    private static void createUsedQuotaProcedure(Connection conn) throws SQLException {
        // SQL for creating the stored procedure with batch_size as parameter
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS Usedquota";
        String createProcedureSQL =
                "CREATE PROCEDURE `Usedquota`(IN dynamic_limit INT, IN batch_size INT)\n" +
                        "BEGIN\n" +
                        "    DECLARE offset INT DEFAULT 0;\n" +
                        "\n" +
                        "    -- Loop through the table in batches of 'batch_size' rows\n" +
                        "    WHILE offset < dynamic_limit DO\n" +
                        "        -- Create a temporary table to store the ordered data for the current batch\n" +
                        "        CREATE TEMPORARY TABLE tmp_update AS \n" +
                        "        SELECT t2.quotadtlsid, t2.usedquota, t2.quotaunit, t2.createdate\n" +
                        "        FROM Savbillcpm.tblcustquotadtls AS t2\n" +
                        "        ORDER BY t2.quotadtlsid DESC\n" +
                        "        LIMIT batch_size OFFSET offset;\n" +
                        "\n" +
                        "        -- Update savbillradius.tblcustquotadtls using the temporary table\n" +
                        "        UPDATE savbillradius.tblcustquotadtls AS t1\n" +
                        "        JOIN tmp_update AS tmp \n" +
                        "            ON t1.quotadtlsid = tmp.quotadtlsid\n" +
                        "        SET t1.usedquota = tmp.usedquota,\n" +
                        "            t1.quotaunit = tmp.quotaunit,\n" +
                        "            t1.createdate = tmp.createdate;\n" +
                        "\n" +
                        "        -- Drop the temporary table after the update\n" +
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
            LOGGER.info("Stored procedure 'Usedquota' created successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    // Method to execute the Usedquota stored procedure with dynamic LIMIT and batch size
    private static void executeUsedQuotaProcedure(Connection conn, int dynamicLimit, int batchSize) {
        String callProcedureSQL = "{CALL Usedquota(?, ?)}";  // Stored procedure call with dynamic limit and batch size

        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            // Set the dynamic limit and batch size for the stored procedure parameters
            stmt.setInt(1, dynamicLimit);
            stmt.setInt(2, batchSize);

            // Execute the stored procedure
            stmt.execute();
            LOGGER.info("Stored procedure 'Usedquota' executed successfully with dynamic LIMIT: " + dynamicLimit + " and batch size: " + batchSize);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}

