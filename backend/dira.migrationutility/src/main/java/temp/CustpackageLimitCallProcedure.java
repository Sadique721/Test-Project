package temp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustpackageLimitCallProcedure {
 // it is not working
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
            
            // Step 3: Call the stored procedure with a dynamic limit
            int dynamicLimit = 100;  // Set the dynamic limit value here
            callCustPackageProcedure(conn, dynamicLimit);

        } catch (SQLException e) {
            Logger.getLogger(CustpackageLimitCallProcedure.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }

    /**
     * Method to drop the 'custpackage' stored procedure if it exists.
     */
    private static void dropCustPackageProcedure(Connection conn) {
        // SQL to drop the stored procedure if it exists
        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `custpackage`";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dropProcedureSQL);
            System.out.println("Stored procedure 'custpackage' dropped if it existed.");
        } catch (SQLException e) {
            Logger.getLogger(CustpackageLimitCallProcedure.class.getName()).log(Level.SEVERE, "Error dropping stored procedure", e);
        }
    }

    /**
     * Method to create the 'custpackage' stored procedure.
     */
    private static void createCustPackageProcedure(Connection conn) {
        // Define the SQL to create the stored procedure
        String createProcedureSQL = "CREATE DEFINER=`root`@`%` PROCEDURE `custpackage`(\n" +
                "    IN dynamic_limit INT  -- Add dynamic limit as an input parameter\n" +
                ")\n" +
                "BEGIN\n" +
                "    DECLARE done INT DEFAULT FALSE;\n" +
                "    DECLARE v_createdate DATETIME;\n" +
                "    DECLARE v_startdate DATETIME;\n" +
                "    DECLARE v_enddate DATETIME;\n" +
                "    DECLARE v_cprid INT;\n" +
                "\n" +
                "    -- Declare a cursor for iterating over the latest rows from the tblcustpackagerel table\n" +
                "    DECLARE cur CURSOR FOR \n" +
                "        SELECT createdate, startdate, enddate, custpackageid \n" +
                "        FROM Savbillcpm.tblcustpackagerel\n" +
                "        ORDER BY createdate DESC  -- Assuming you want to order by createdate to get the latest records\n" +
                "        LIMIT dynamic_limit;  -- Use the dynamic limit\n" +
                "\n" +
                "    -- Declare a handler to set the done flag when the cursor finishes\n" +
                "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;\n" +
                "\n" +
                "    -- Open the cursor\n" +
                "    OPEN cur;\n" +
                "\n" +
                "    -- Start the loop to fetch and process each row\n" +
                "    read_loop: LOOP\n" +
                "        -- Fetch data from the cursor\n" +
                "        FETCH cur INTO v_createdate, v_startdate, v_enddate, v_cprid;\n" +
                "        \n" +
                "        -- Exit the loop if there are no more rows\n" +
                "        IF done THEN\n" +
                "            LEAVE read_loop;\n" +
                "        END IF;\n" +
                "\n" +
                "        -- Update the savbillradius.tblcustpackagerel table with the fetched values\n" +
                "        UPDATE savbillradius.tblcustpackagerel \n" +
                "        SET createdate = v_createdate,\n" +
                "            startdate = v_startdate,\n" +
                "            expirydate = v_enddate,  -- expirydate and enddate are set to the same value\n" +
                "            enddate = v_enddate\n" +
                "        WHERE custpackageid = v_cprid;\n" +
                "\n" +
                "    END LOOP;\n" +
                "\n" +
                "    -- Close the cursor\n" +
                "    CLOSE cur;\n" +
                "END;";  // No DELIMITER command is needed in JDBC

        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL to create the procedure
            stmt.executeUpdate(createProcedureSQL);
            System.out.println("Stored procedure 'custpackage' created successfully.");
        } catch (SQLException e) {
            Logger.getLogger(CustpackageLimitCallProcedure.class.getName()).log(Level.SEVERE, "Error creating stored procedure", e);
        }
    }

    /**
     * Method to call the 'custpackage' stored procedure with dynamic LIMIT.
     */
    private static void callCustPackageProcedure(Connection conn, int dynamicLimit) {
        // Define the SQL for calling the procedure with a dynamic parameter
        String callProcedureSQL = "{CALL custpackage(?)}";  // Using ? for dynamic LIMIT
        
        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
            // Set the dynamic limit value as the parameter
            stmt.setInt(1, dynamicLimit);

            // Execute the stored procedure
            stmt.execute();
            System.out.println("Stored procedure 'custpackage' executed successfully with dynamic LIMIT: " + dynamicLimit);
        } catch (SQLException e) {
            Logger.getLogger(CustpackageLimitCallProcedure.class.getName()).log(Level.SEVERE, "Error executing stored procedure", e);
        }
    }
}
