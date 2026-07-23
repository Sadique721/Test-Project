package SavanaCustomer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

public class DatabaseUpdationPayments {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdationPayments.class);


    public synchronized void updatePayment(
            Connection g1A, String g1ATable,
            Connection g1B, String g1BTable,
            Map<String, String> map
    ) throws SQLException {

        // GROUP 1 : tbltcreditdoc
        updateGroupOneTable(g1A, g1ATable, map);
        updateGroupTwoTable(g1B, g1BTable, map);
    }

    /* ============================================================
       🔵 GROUP 1: tbltcreditdoc (2 tables same structure)
       ============================================================ */
    private synchronized void updateGroupOneTable(Connection conn, String tableName, Map<String, String> map) throws SQLException {

        String sql = "UPDATE " + tableName + " SET " +
                "PAYMENTDATE = ?, CREATEDATE = ?, LASTMODIFIEDDATE = ? " +
                "WHERE CREDITDOCID = ?";

        String creditDocId = map.get("CREDITDOCID");
        if (creditDocId == null) {
            logger.warn("CREDITDOCID is null, skipping DB update for table: {}", tableName);
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            String rawChequeDate = map.get("backdate");
            if (rawChequeDate == null || rawChequeDate.isEmpty()) {
                rawChequeDate = "11/25/25";
            }
            String paymentDate = convertToApiDateTime(rawChequeDate); // convert to yyyy-MM-dd

            // Bind columns
            stmt.setString(1, paymentDate);
            stmt.setString(2, paymentDate);
            stmt.setString(3, paymentDate);

            // WHERE clause
            stmt.setString(4, creditDocId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                conn.commit();
                logger.info("Group1 updated: {} → CREDITDOCID: {}", tableName, creditDocId);
                logger.info("DB update executed → Table: {} | CREDITDOCID: {}", tableName, creditDocId);
            } else {
                conn.rollback();
                logger.warn("Group1 NO update: {} → CREDITDOCID: {}", tableName, creditDocId);
            }

        } catch (SQLException e) {
            conn.rollback();
            logger.error("Group1 ERROR at {} → CREDITDOCID: {} | {}", tableName, creditDocId, e.getMessage(), e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }


    private synchronized void updateGroupTwoTable(Connection conn, String tableName, Map<String, String> map) throws SQLException {

        String sql = "UPDATE " + tableName + " SET " +
                "PAYMENTDATE = ?, CREATEDATE = ?, LASTMODIFIEDDATE = ? " +
                "WHERE CREDITDOCID = ?";

        String creditDocId = map.get("CREDITDOCID");
        if (creditDocId == null) {
            logger.warn("CREDITDOCID is null, skipping DB update for table: {}", tableName);
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            String rawChequeDate = map.get("backdate");
            if (rawChequeDate == null || rawChequeDate.isEmpty()) {
                rawChequeDate = "11/25/25";
            }
            String paymentDate = convertToApiDateTime(rawChequeDate);

                // Bind columns
                stmt.setString(1, paymentDate);
                stmt.setString(2, paymentDate);
                stmt.setString(3, paymentDate);

                // WHERE clause
                stmt.setString(4, creditDocId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                conn.commit();
                logger.info("Group2 updated: {} → CREDITDOCID: {}", tableName, creditDocId);
                logger.info("DB update executed → Table: {} | CREDITDOCID: {}", tableName, creditDocId);
            } else {
                conn.rollback();
                logger.warn("Group2 NO update: {} → CREDITDOCID: {}", tableName, creditDocId);
            }

        } catch (SQLException e) {
            conn.rollback();
            logger.error("Group2 ERROR at {} → CREDITDOCID: {} | {}", tableName, creditDocId, e.getMessage(), e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }


    private void setSafeString(PreparedStatement stmt, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            stmt.setNull(index, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(index, value);
        }
    }

    private String convertToApiDateTime(String date) {
        try {
            // Incoming format: MM/dd/yy
            java.time.format.DateTimeFormatter inFormat = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yy");
            // Desired output format with time
            java.time.format.DateTimeFormatter outFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Parse date
            java.time.LocalDate localDate = java.time.LocalDate.parse(date, inFormat);

            // Convert to LocalDateTime at start of day (00:00:00)
            java.time.LocalDateTime dateTime = localDate.atStartOfDay();

            // Format and return
            return dateTime.format(outFormat);

        } catch (Exception e) {
            // fallback: return fixed date-time
            return "2025-11-25 00:00:00";
        }
    }


}
