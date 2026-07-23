package temp;
/*
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustPackageUpdater {
	//private static final String URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=30000&socketTimeout=30000";
    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=30000&socketTimeout=30000";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";

    public static void main(String[] args) {
        // Establish connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Create the query to fetch data from the tblcustpackagerel table
            String fetchQuery = "SELECT createdate, startdate, enddate, custpackageid FROM Savbillcpm.tblcustpackagerel";
            
            // Create a prepared statement to execute the query
            try (PreparedStatement fetchStatement = conn.prepareStatement(fetchQuery);
                 ResultSet rs = fetchStatement.executeQuery()) {
                
                // Loop through the results
                while (rs.next()) {
                    // Retrieve values for each row
                    Timestamp createdate = rs.getTimestamp("createdate");
                    Timestamp startdate = rs.getTimestamp("startdate");
                    Timestamp enddate = rs.getTimestamp("enddate");
                    int custpackageid = rs.getInt("custpackageid");

                    // Call the method to update the tblcustpackagerel table in the other database
                    updateCustPackage(conn, createdate, startdate, enddate, custpackageid);
                }
            } catch (SQLException e) {
                Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Error processing the result set", e);
            }
        } catch (SQLException e) {
            Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }

    private static void updateCustPackage(Connection conn, Timestamp createdate, Timestamp startdate, Timestamp enddate, int custpackageid) {
        // SQL statement to update the tblcustpackagerel in the savbillradius database
        String updateQuery = "UPDATE savbillradius.tblcustpackagerel SET createdate = ?, startdate = ?, expirydate = ?, enddate = ? WHERE custpackageid = ?";

        try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
            // Set the values to the prepared statement
            updateStatement.setTimestamp(1, createdate);
            updateStatement.setTimestamp(2, startdate);
            updateStatement.setTimestamp(3, enddate);
            updateStatement.setTimestamp(4, enddate); // expirydate and enddate are set to the same value as per the original procedure
            updateStatement.setInt(5, custpackageid);

            // Execute the update query
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated record for custpackageid: " + custpackageid);
            } else {
                System.out.println("No record updated for custpackageid: " + custpackageid);
            }
        } catch (SQLException e) {
            Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Error updating record for custpackageid: " + custpackageid, e);
        }
    }
}  */





import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustPackageUpdater {

    // JDBC connection URL with timeout and auto-reconnect settings
    private static final String DB_URL = "jdbc:mysql://192.168.24.7:3306/Savbillcpm?useSSL=false&serverTimezone=UTC&connectTimeout=60000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root@qa";
    private static final int BATCH_SIZE = 1000;  // Process in batches of 1000 rows (can be tuned)

    public static void main(String[] args) {
        // Establish connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Disable auto-commit for batch processing
            conn.setAutoCommit(false);

            // Create the query to fetch data from the tblcustpackagerel table
            String fetchQuery = "SELECT createdate, startdate, enddate, custpackageid FROM Savbillcpm.tblcustpackagerel";

            // Create a prepared statement to execute the query
            try (PreparedStatement fetchStatement = conn.prepareStatement(fetchQuery);
                 ResultSet rs = fetchStatement.executeQuery()) {

                // Prepare the batch update statement
                String updateQuery = "UPDATE savbillradius.tblcustpackagerel SET createdate = ?, startdate = ?, expirydate = ?, enddate = ? WHERE custpackageid = ?";
                try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {

                    int count = 0;  // To keep track of the batch size

                    // Loop through the results
                    while (rs.next()) {
                        // Retrieve values for each row
                        Timestamp createdate = rs.getTimestamp("createdate");
                        Timestamp startdate = rs.getTimestamp("startdate");
                        Timestamp enddate = rs.getTimestamp("enddate");
                        int custpackageid = rs.getInt("custpackageid");

                        // Set values for the update statement
                        updateStatement.setTimestamp(1, createdate);
                        updateStatement.setTimestamp(2, startdate);
                        updateStatement.setTimestamp(3, enddate);
                        updateStatement.setTimestamp(4, enddate);  // expirydate and enddate are set to the same value
                        updateStatement.setInt(5, custpackageid);

                        // Add to the batch
                        updateStatement.addBatch();
                        count++;

                        // If batch size is reached, execute the batch and reset the counter
                        if (count % BATCH_SIZE == 0) {
                            updateStatement.executeBatch();
                            conn.commit();  // Commit the batch
                            System.out.println("Processed " + count + " records.");
                        }
                    }

                    // Execute any remaining updates in the batch after finishing the loop
                    if (count % BATCH_SIZE != 0) {
                        updateStatement.executeBatch();
                        conn.commit();
                        System.out.println("Processed " + count + " records.");
                    }
                } catch (SQLException e) {
                    conn.rollback();  // Rollback if an error occurs
                    Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Error during batch update", e);
                }
            } catch (SQLException e) {
                Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Error processing the result set", e);
            }
        } catch (SQLException e) {
            Logger.getLogger(CustPackageUpdater.class.getName()).log(Level.SEVERE, "Database connection error", e);
        }
    }
}
