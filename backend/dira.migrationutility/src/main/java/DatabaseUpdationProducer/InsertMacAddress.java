package DatabaseUpdationProducer;

import utility.Constant;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InsertMacAddress {

    private static final String DB_URL = Constant.URLCONVERGE;
    private static final String DB_USER = Constant.USERNAME;
    private static final String DB_PASSWORD = Constant.PASSWORD;

    private static final Logger LOGGER = Logger.getLogger(InsertMacAddress.class.getName());

    // Method to insert MAC addresses with INSERT IGNORE to avoid duplicates

    public static void insertMac() {
        // Establishing connection to the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            insertMacAddresses(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }
    private static void insertMacAddresses(Connection conn) throws SQLException {
        String insertSQL =
                "INSERT IGNORE INTO Savbillcpmiusbss.tbltmacaddressmapping (" +
                        "macaddressid, custid, macaddress, createdate, lastmodificationdate," +
                        "createdby, lastmodifiedby, custsermappingid, macretentiondate, normalizemac" +
                        ") " +
                        "SELECT " +
                        "dd.custmacmapid, " +
                        "dd.custid, " +
                        "dd.macaddress, " +
                        "dd.createdate, " +
                        "dd.lastmodifieddate, " +
                        "dd.createbyname, " +
                        "dd.updatebyname, " +
                        "dd.custsermappingid, " +
                        "dd.macretentiondate, " +
                        "REPLACE(dd.macaddress, ':', '') AS normalizemac " +
                        "FROM " +
                        "Savbillcpm.tblcustmacmapping dd";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            int rowsInserted = pstmt.executeUpdate();
            LOGGER.info(rowsInserted + " rows inserted.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting MAC addresses", e);
        }
    }
}
