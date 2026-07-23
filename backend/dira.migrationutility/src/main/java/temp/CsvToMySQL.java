package temp;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import utility.Constant;
/*
public class CsvToMySQL {

    static String fs = Constant.FILE_SEPERATOR;
    static String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

    // Database connection details
    private static final String URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=30000&socketTimeout=30000";
    private static final String USER = "root";  // MySQL username
    private static final String PASSWORD = "root@qa";  // MySQL password
    private static final String CSV_FILE_PATH = filePath + "MigrationCustomerWithBaseUsaegs.csv";  // Path to your CSV file

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement psmt = null;
        Statement stmt = null;

        // Updated SQL insert query for 26 columns
        String insertQuery = "INSERT INTO migrationcustomerwithbaseusaegs (" +
                "sno, username, password, status, concurrentloginpolicy, radiuspolicy, " +
                "additionalpolicy, param1, param2, param4, customeraltemailid, callingstationid, " +
                "cui, macvalidation, msisdn, geolocation, param6, primarydns, secondarydns, " +
                "primaryipv6dns, secondaryipv6dns, usedquota, startdate, enddate, cprid, migrationstatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // SQL for creating the table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS migrationcustomerwithbaseusaegs (" +
                "sno INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255), " +
                "password VARCHAR(255), " +
                "status VARCHAR(50), " +
                "concurrentloginpolicy VARCHAR(50), " +
                "radiuspolicy VARCHAR(50), " +
                "additionalpolicy VARCHAR(50), " +
                "param1 VARCHAR(255), " +
                "param2 VARCHAR(255), " +
                "param4 VARCHAR(255), " +
                "customeraltemailid VARCHAR(255), " +
                "callingstationid VARCHAR(255), " +
                "cui VARCHAR(255), " +
                "macvalidation VARCHAR(50), " +
                "msisdn VARCHAR(50), " +
                "geolocation VARCHAR(255), " +
                "param6 VARCHAR(255), " +
                "primarydns VARCHAR(50), " +
                "secondarydns VARCHAR(50), " +
                "primaryipv6dns VARCHAR(50), " +
                "secondaryipv6dns VARCHAR(50), " +
                "usedquota INT, " +
                "startdate DATETIME, " +
                "enddate DATETIME, " +
                "cprid INT, " +
                "migrationstatus VARCHAR(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try {
            // Establish the connection to the database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);  // Disable auto-commit for batch insertion

            // Create the table if it doesn't exist
            stmt = conn.createStatement();
            stmt.executeUpdate(createTableQuery);

            // Prepare the insert statement
            psmt = conn.prepareStatement(insertQuery);

            // Open the CSV file using OpenCSV
            CSVReader csvReader = new CSVReader(new FileReader(CSV_FILE_PATH));
            String[] fields;
            int lineNumber = 0;

            // Read each line from the CSV
            while ((fields = csvReader.readNext()) != null) {
                lineNumber++;

                // Skip the header line
                if (lineNumber == 1) {
                    continue;
                }

                // Ensure the CSV has the correct number of columns (26 in this case)
                if (fields.length != 26) {
                    System.out.println("Skipping invalid line " + lineNumber + " (incorrect number of columns): " + String.join(",", fields));
                    continue;
                }

                // Map CSV data to the SQL statement, converting empty fields to NULL
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].isEmpty()) {
                        psmt.setNull(i + 1, java.sql.Types.NULL);
                    } else {
                        psmt.setString(i + 1, fields[i]);
                    }
                }

                // Add to batch
                psmt.addBatch();

                // Execute batch every 1000 records to improve performance
                if (lineNumber % 1000 == 0) {
                    psmt.executeBatch();
                }
            }

            // Execute any remaining records
            psmt.executeBatch();

            // Commit transaction
            conn.commit();

            System.out.println("Data has been successfully inserted into the database.");

        } catch (SQLException | IOException e) {  // Handle SQLException and IOException
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();  // Rollback in case of error
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}  */




public class CsvToMySQL {

    static String fs = Constant.FILE_SEPERATOR;
    static String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

    // Database connection details with local_infile enabled
    private static final String URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=30000&socketTimeout=30000&allowLoadLocalInfile=true";
    private static final String USER = "root";  // MySQL username
    private static final String PASSWORD = "root@qa";  // MySQL password
    private static final String CSV_FILE_PATH = filePath + "MigrationCustomerWithBaseUsaegs.csv";  // Path to your CSV file

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        // SQL for creating the table (if necessary)
        String createTableQuery = "CREATE TABLE IF NOT EXISTS migrationcustomerwithbaseusaegs (" +
                "sno INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255), " +
                "password VARCHAR(255), " +
                "status VARCHAR(50), " +
                "concurrentloginpolicy VARCHAR(50), " +
                "radiuspolicy VARCHAR(50), " +
                "additionalpolicy VARCHAR(50), " +
                "param1 VARCHAR(255), " +
                "param2 VARCHAR(255), " +
                "param4 VARCHAR(255), " +
                "customeraltemailid VARCHAR(255), " +
                "callingstationid VARCHAR(255), " +
                "cui VARCHAR(255), " +
                "macvalidation VARCHAR(50), " +
                "msisdn VARCHAR(50), " +
                "geolocation VARCHAR(255), " +
                "param6 VARCHAR(255), " +
                "primarydns VARCHAR(50), " +
                "secondarydns VARCHAR(50), " +
                "primaryipv6dns VARCHAR(50), " +
                "secondaryipv6dns VARCHAR(50), " +
                "usedquota INT, " +
                "startdate DATETIME, " +
                "enddate DATETIME, " +
                "cprid INT, " +
                "migrationstatus VARCHAR(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try {
            // Establish the connection to the database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);  // Disable auto-commit for batch insertion

            // Create the table if it doesn't exist
            stmt = conn.createStatement();
            stmt.executeUpdate(createTableQuery);
            System.out.println("Table migrationcustomerwithbaseusaegs created or already exists.");

            // Using LOAD DATA INFILE to insert the CSV data into MySQL
            String loadDataQuery = "LOAD DATA LOCAL INFILE '" + CSV_FILE_PATH + "' " +
                    "INTO TABLE migrationcustomerwithbaseusaegs " +
                    "FIELDS TERMINATED BY ',' " +
                    "OPTIONALLY ENCLOSED BY '\"' " +
                    "LINES TERMINATED BY '\\n' " +
                    "IGNORE 1 ROWS;";  // Skips header row

            // Execute the LOAD DATA INFILE query
            stmt.executeUpdate(loadDataQuery);
            System.out.println("Data has been successfully loaded into the database using LOAD DATA INFILE.");

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();  // Rollback in case of error
                    System.out.println("Transaction rolled back.");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
