package DatabaseUpdationProducer;

import utility.Constant;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InsertIpAddress {

    private static final String DB_URL = Constant.URLCONVERGE;
    private static final String DB_USER = Constant.USERNAME;
    private static final String DB_PASSWORD = Constant.PASSWORD;

    private static final Logger LOGGER = Logger.getLogger(InsertIpAddress.class.getName());

    public static void insertip() {
        // Establishing connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            insertIpAddresses(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }

    // Method to insert IP addresses with INSERT IGNORE to avoid duplicates
    private static void insertIpAddresses(Connection conn) throws SQLException {
        String insertSQL =
                "INSERT IGNORE INTO savbillradius.tblcustipmapping (" +
                        "id, custid, ip_address, ip_type, createdate, lastmodificationdate," +
                        "createdby, lastmodifiedby, custsermappingid" +
                        ") " +
                        "SELECT " +
                        "dd.id, " +
                        "dd.custid, " +
                        "dd.ip_address, " +
                        "dd.ip_type, " +
                        "dd.createdate, " +
                        "dd.lastmodifieddate, " +
                        "dd.createbyname, " +
                        "dd.updatebyname, " +
                        "dd.custsermappingid " +
                        "FROM " +
                        "Savbillcpm.tblcustipmapping dd " +
                        "WHERE NOT EXISTS ( " +
                        "    SELECT 1 " +
                        "    FROM savbillradius.tblcustipmapping t " +
                        "    WHERE t.id = dd.id " +
                        ")";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            int rowsInserted = pstmt.executeUpdate();
            LOGGER.info(rowsInserted + " rows inserted.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting IP addresses", e);
        }
    }
}
