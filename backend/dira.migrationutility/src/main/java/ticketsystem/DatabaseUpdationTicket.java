package ticketsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DatabaseUpdationTicket {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdationTicket.class);

    /**
     * Updates ticket info in savbillticketmanagement.tblcases table using CaseId as the unique key.
     */
    public void updateTicketInfoByCaseId(Connection conn, Map<String, String> ticketDetailsMap) throws SQLException {
        String caseId = ticketDetailsMap.get("CaseId"); // <-- use CaseId from ticketDetailsMap

        if (caseId == null || caseId.trim().isEmpty()) {
            logger.warn("Skipping update: CaseId missing for user {}", ticketDetailsMap.get("Username"));
            return;
        }

        String updateSql = "UPDATE savbillticketmanagement.tblcases SET " +
                "case_status = ?, case_started_on = ?, first_assigned_on = ?, CREATEDATE = ?, " +
                "LASTMODIFIEDDATE = ?, final_resolution_date = ?, final_closed_date = ? " +
                "WHERE case_id = ?"; // directly use CaseId

        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            conn.setAutoCommit(false);

//            String caseNumber   = ticketDetailsMap.get("caseNumber");
//            String creationDate = ticketDetailsMap.get("startDate");
//            String closedDate   = ticketDetailsMap.get("endDate");
            String creationDate = convertDate(ticketDetailsMap.get("startDate"));
            String closedDate   = convertDate(ticketDetailsMap.get("endDate"));

            String caseStatus   = ticketDetailsMap.get("Status");

            setSafeString(stmt, 1, caseStatus);
            setSafeString(stmt, 2, creationDate);
            setSafeString(stmt, 3, creationDate);
            setSafeString(stmt, 4, creationDate);
            setSafeString(stmt, 5, closedDate);
            setSafeString(stmt, 6, closedDate);
            setSafeString(stmt, 7, closedDate);

            stmt.setString(8, caseId); // <-- assign CaseId directly

            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                conn.commit();
                logger.info("✅ Ticket info updated successfully for case_id={} (user={})",
                        caseId, ticketDetailsMap.get("Username"));
            } else {
                conn.rollback();
                logger.warn("⚠ No ticket updated for case_id={} (user={})",
                        caseId, ticketDetailsMap.get("Username"));
            }

        } catch (SQLException e) {
            conn.rollback();
            logger.error("❌ DB update failed for case_id={}: {}", caseId, e.getMessage(), e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }




    private String convertDate(String date) {

        DateTimeFormatter outFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        DateTimeFormatter[] inFormats = {
                DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
        };

        for (DateTimeFormatter formatter : inFormats) {
            try {
                return LocalDateTime.parse(date, formatter).format(outFormat);
            } catch (Exception ignored) {
            }
        }

        // fallback
        return "2025-06-12 00:00:00";
    }



    private void setSafeString(PreparedStatement stmt, int index, String value) throws SQLException {
        if (value != null && !value.trim().isEmpty()) {
            stmt.setString(index, value);
        } else {
            stmt.setNull(index, Types.VARCHAR);
        }
    }
}
