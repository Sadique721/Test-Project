package temp;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class CreateAndExecuteUsedQuotaProcedurewithoutlimit {


	

	//this class for used quota 

	    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=120000";  // Increased socketTimeout

	    private static final String DB_USER = "root";
	    private static final String DB_PASSWORD = "root@qa";

	    public static void main(String[] args) {
	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            // Create the Usedquota stored procedure
	            createUsedQuotaProcedure(conn);

	            // Execute the Usedquota stored procedure
	            executeUsedQuotaProcedure(conn);

	        } catch (SQLException e) {
	            Logger.getLogger(CreateAndExecuteUsedQuotaProcedurewithoutlimit.class.getName()).log(Level.SEVERE, "Database connection error", e);
	        }
	    }

	    // Method to create the Usedquota stored procedure
	    private static void createUsedQuotaProcedure(Connection conn) throws SQLException {
	        String dropProcedureSQL = "DROP PROCEDURE IF EXISTS `Usedquota`";
	        String createProcedureSQL = 
	                "CREATE PROCEDURE `Usedquota`() " +  // Removed dynamic LIMIT parameter
	                "BEGIN " +
	                "    DECLARE done INT DEFAULT FALSE; " +
	                "    DECLARE v_usedquota DECIMAL(20,8); " +
	                "    DECLARE v_quotaunit VARCHAR(10); " +
	                "    DECLARE v_createdate DATETIME; " +
	                "    DECLARE v_custpackageid INT; " +
	                "    DECLARE cur CURSOR FOR " +
	                "        SELECT usedquota, quotaunit, createdate, custpackageid " +
	                "        FROM Savbillcpm.tblcustquotadtls " +  // No LIMIT clause here
	                "        ORDER BY custid DESC; " +
	                "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; " +
	                "    OPEN cur; " +
	                "    read_loop: LOOP " +
	                "        FETCH cur INTO v_usedquota, v_quotaunit, v_createdate, v_custpackageid; " +
	                "        IF done THEN " +
	                "            LEAVE read_loop; " +
	                "        END IF; " +
	                "        UPDATE savbillradius.tblcustquotadtls " +
	                "        SET usedquota = v_usedquota, " +
	                "            quotaunit = v_quotaunit, " +
	                "            createdate = v_createdate " +
	                "        WHERE custpackageid = v_custpackageid; " +
	                "    END LOOP; " +
	                "    CLOSE cur; " +
	                "END";

	        try (Statement stmt = conn.createStatement()) {
	            // Drop the procedure if it exists
	            stmt.executeUpdate(dropProcedureSQL);
	            // Create the procedure
	            stmt.executeUpdate(createProcedureSQL);
	            System.out.println("Stored procedure 'Usedquota' created successfully.");
	        } catch (SQLException e) {
	            Logger.getLogger(CreateAndExecuteUsedQuotaProcedurewithoutlimit.class.getName()).log(Level.SEVERE, "Error creating stored procedure", e);
	        }
	    }

	    // Method to execute the Usedquota stored procedure
	    private static void executeUsedQuotaProcedure(Connection conn) {
	        String callProcedureSQL = "{CALL Usedquota()}";  // No parameters passed now

	        try (CallableStatement stmt = conn.prepareCall(callProcedureSQL)) {
	            // Execute the stored procedure
	            stmt.execute();
	            System.out.println("Stored procedure 'Usedquota' executed successfully.");
	        } catch (SQLException e) {
	            Logger.getLogger(CreateAndExecuteUsedQuotaProcedurewithoutlimit.class.getName()).log(Level.SEVERE, "Error executing stored procedure", e);
	        }
	    }
	}


