package temp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateAndCallProcedureOptimized {

    // JDBC connection details
    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";

    public static void main(String[] args) {
        // Establish connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Step 1: Drop the stored procedure if it exists
            dropCustPackageProcedure(conn);
            
            // Step 2: Create the stored procedure
            createCustPackageProcedure(conn);
            
            // Step 3: Use batch updates to process the data in bulk
            processInBulk(conn);

            // Step 4: Optionally, call the procedure if needed
            // callCustPackageProcedure(conn);

        } catch (SQLException e) {
            Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }

    /**
     * Method to drop the 'custpackage' stored procedure if it exists.
     */
    private static void dropCustPackageProcedure(Connection conn) {
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `custpackage`";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dropProcedureSQL);
            System.out.println("Stored procedure 'custpackage' dropped if it existed.");
        } catch (SQLException e) {
            Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Error dropping stored procedure", e);
        }
    }

    /**
     * Method to create the 'custpackage' stored procedure.
     */
    private static void createCustPackageProcedure(Connection conn) {
        String createProcedureSQL = "CREATE DEFINER=`root`@`%` PROCEDURE `custpackage`()\n" +
                "BEGIN\n" +
                "    DECLARE done INT DEFAULT FALSE;\n" +
                "    DECLARE v_createdate DATETIME;\n" +
                "    DECLARE v_startdate DATETIME;\n" +
                "    DECLARE v_enddate DATETIME;\n" +
                "    DECLARE v_cprid INT;\n" +
                "\n" +
                "    DECLARE cur CURSOR FOR \n" +
                "        SELECT createdate, startdate, enddate, custpackageid \n" +
                "        FROM Savbillcpm.tblcustpackagerel;\n" +
                "\n" +
                "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;\n" +
                "\n" +
                "    OPEN cur;\n" +
                "\n" +
                "    read_loop: LOOP\n" +
                "        FETCH cur INTO v_createdate, v_startdate, v_enddate, v_cprid;\n" +
                "        IF done THEN\n" +
                "            LEAVE read_loop;\n" +
                "        END IF;\n" +
                "        UPDATE savbillradius.tblcustpackagerel\n" +
                "        SET createdate = v_createdate,\n" +
                "            startdate = v_startdate,\n" +
                "            expirydate = v_enddate,\n" +
                "            enddate = v_enddate\n" +
                "        WHERE custpackageid = v_cprid;\n" +
                "    END LOOP;\n" +
                "    CLOSE cur;\n" +
                "END;";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createProcedureSQL);
            System.out.println("Stored procedure 'custpackage' created successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    /**
     * Method to process the updates in bulk.
     * This will fetch data and perform batch updates.
     */
    private static void processInBulk(Connection conn) {
        // SQL to fetch the necessary data
        String fetchQuery = "SELECT createdate, startdate, enddate, custpackageid FROM Savbillcpm.tblcustpackagerel";
        String updateQuery = "UPDATE savbillradius.tblcustpackagerel SET createdate = ?, startdate = ?, expirydate = ?, enddate = ? WHERE custpackageid = ?";

        try (PreparedStatement fetchStmt = conn.prepareStatement(fetchQuery);
             ResultSet rs = fetchStmt.executeQuery();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Disable auto-commit to improve performance
            conn.setAutoCommit(false);

            // Loop through the result set and batch updates
            int batchCount = 0;
            while (rs.next()) {
                Timestamp createdate = rs.getTimestamp("createdate");
                Timestamp startdate = rs.getTimestamp("startdate");
                Timestamp enddate = rs.getTimestamp("enddate");
                int custpackageid = rs.getInt("custpackageid");

                // Set parameters for batch update
                updateStmt.setTimestamp(1, createdate);
                updateStmt.setTimestamp(2, startdate);
                updateStmt.setTimestamp(3, enddate);
                updateStmt.setTimestamp(4, enddate); // expirydate = enddate
                updateStmt.setInt(5, custpackageid);

                // Add the update to the batch
                updateStmt.addBatch();

                // Execute the batch every 1000 records (can adjust based on performance)
                if (++batchCount % 100000 == 0) {
                    updateStmt.executeBatch();
                    conn.commit();  // Commit the batch
                }
            }

            // Execute any remaining updates in the batch
            updateStmt.executeBatch();
            conn.commit();

            System.out.println("Bulk updates completed successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Error processing batch updates", e);
        } finally {
            try {
                // Restore auto-commit to true
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Error restoring auto-commit", e);
            }
        }
    }

    /**
     * Method to call the 'custpackage' stored procedure (optional).
     */
    private static void callCustPackageProcedure(Connection conn) {
        String callProcedureSQL = "{CALL custpackage()}";  // Using curly braces to call the stored procedure

        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            stmt.execute();
            System.out.println("Stored procedure 'custpackage' executed successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CreateAndCallProcedureOptimized.class.getName()).log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}
