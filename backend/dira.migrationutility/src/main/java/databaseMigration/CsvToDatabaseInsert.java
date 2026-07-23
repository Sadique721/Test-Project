package databaseMigration;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import utility.Constant;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CsvToDatabaseInsert {

    private static final Logger logger = Logger.getLogger(CsvToDatabaseInsert.class.getName());

    public static void main(String[] args) {
        // MySQL connection details
        String url = Constant.URLCONVERGE;
        String user = Constant.USERNAME;
        String password = Constant.PASSWORD;

        // Path to CSV file
        String fs = Constant.FILE_SEPERATOR;
        String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
        String csvFile = filePath + Constant.ACTCUSTOMERCSV;

        // SQL query for inserting data
        String insertQuery = "INSERT INTO Savbillcpm.tblcustomers "
                + "(username, password, email, accountnumber, cstatus, failcount, expirydate, NEXTBILLDATE, LASTBILLDATE, BILLDAY, "
                + "outstandingbalance, partnerid, ASNNumber, IPPrefixes, IPV6Prefixes, LANIP, WANIP, customertype, gender, title, phone, subscriberpackage, subscriberpackageid) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Declare connection and prepared statement
        Connection conn = null;
        PreparedStatement stmt = null;

        // Batch size to commit periodically (e.g., 1000)
        int batchSize = 1000;
        int count = 0;

        try {
            // Setup MySQL connection
            logger.info("Connecting to database...");
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false); // Disable auto-commit for batch processing
            logger.info("Database connection established.");

            // Prepare statement
            stmt = conn.prepareStatement(insertQuery);

            // Open CSV reader
            logger.info("Opening CSV file: " + csvFile);
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            reader.skip(1); // Skip header row

            // Read all rows from the CSV
            logger.info("Reading rows from CSV file...");
            List<String[]> rows = reader.readAll();

            // Process each row and add to batch
            for (String[] row : rows) {
                logger.fine("Processing row: " + String.join(", ", row));

                // Check for missing or empty columns and handle accordingly
                stmt.setString(1, row.length > 1 ? row[1] : null); // username
                stmt.setString(2, row.length > 2 ? row[2] : null); // password
                stmt.setString(3, row.length > 10 ? row[10] : null); // email
                stmt.setString(4, row.length > 1 ? row[1] : null); // accountnumber
                stmt.setString(5, "Active"); // cstatus
                stmt.setInt(6, 0); // failcount
                stmt.setString(7, "2025-12-31"); // expirydate
                stmt.setString(8, "2025-04-01 00:00:00"); // NEXTBILLDATE
                stmt.setString(9, "2025-03-01 00:00:00"); // LASTBILLDATE
                stmt.setString(10, "1"); // BILLDAY
                stmt.setDouble(11, 0.0); // outstandingbalance
                stmt.setInt(12, 1); // partnerid (empty string as static value)
                stmt.setString(13, ""); // ASNNumber (empty string as static value)
                stmt.setString(14, ""); // IPPrefixes (empty string as static value)
                stmt.setString(15, ""); // IPV6Prefixes (empty string as static value)
                stmt.setString(16, ""); // LANIP (empty string as static value)
                stmt.setString(17, ""); // WANIP (empty string as static value)
                stmt.setString(18, "individual"); // customertype
                stmt.setString(19, "Male"); // gender
                stmt.setString(20, "Mr"); // title
                stmt.setString(21, row.length > 1 ? row[14] : null); // phone
                stmt.setString(22, "Silver"); // subscriberpackage
                stmt.setInt(23, 1); // subscriberpackageid

                // Add to batch
                stmt.addBatch();
                count++;

                // Execute the batch every batchSize records
                if (count % batchSize == 0) {
                    logger.info("Executing batch insert for " + batchSize + " records.");
                    stmt.executeBatch();
                    conn.commit(); // Commit the transaction
                    logger.info("Batch insert completed.");
                }
            }

            // Execute the remaining batch (if any)
            if (count % batchSize != 0) {
                stmt.executeBatch();
                conn.commit();
                logger.info("Remaining records inserted.");
            }

            logger.info("Data inserted successfully!");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred: " + e.getMessage(), e);
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                    logger.info("Transaction rolled back.");
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during rollback: " + ex.getMessage(), ex);
            }
        } catch (IOException | CsvException e) {
            logger.log(Level.SEVERE, "Error reading the CSV file: " + e.getMessage(), e);
        } finally {
            // Close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                    logger.info("Database connection closed.");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing resources: " + e.getMessage(), e);
            }
        }
    }
}
