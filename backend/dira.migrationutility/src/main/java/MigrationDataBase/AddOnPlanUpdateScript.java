package MigrationDataBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;


public class AddOnPlanUpdateScript {

    private static final Logger logger = LoggerFactory.getLogger(AddOnPlanUpdateScript.class);

    public void updateAddOnPlanInDataBase(Connection converge, Connection radius, String planMapId, Map<String, String> chagePlanMap) throws SQLException {


        long startTime = System.currentTimeMillis();
        updatePlanUpdateForConverge(converge, radius, planMapId, chagePlanMap);

        updateQuotaDetailsForRadius(converge, radius, planMapId, chagePlanMap);

        long endTime = System.currentTimeMillis();
        logger.info("Update Records successfully all table With (UserName : {}) Taken Time (ms : {})",chagePlanMap.get("username"), endTime-startTime);

    }

    public void updatePlanUpdateForConverge(Connection converge, Connection radius, String planMapId, Map<String, String> changePlanMap) {

        // Query for converge add on plan
        String insertAddOnPlanData = "UPDATE tblcustpackagerel SET " + "createdate = ?, startdate = ?, expirydate = ?, enddate = ? " + "WHERE custpackageid = ?";

        try {
            converge.setAutoCommit(false);

            try (PreparedStatement convergeStatement = converge.prepareStatement(insertAddOnPlanData)) {

                // Set parameters for Converge database
                String createDate = changePlanMap.get("Startdate");
                String endDate = changePlanMap.get("Enddate");
                addOnPlanQueryStatement(convergeStatement, createDate, endDate, planMapId);

                int convergeResult = convergeStatement.executeUpdate();
                logger.info("Plan Update Query Execute Success For Database Converge {}", convergeResult);

                if (convergeResult > 0) {
                    converge.commit();
                } else {
                    converge.rollback();
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error In SQL Query For Updating AddOnPlan {}", e.getMessage());
        }
    }


    public void updateQuotaDetailsForRadius(Connection converge, Connection radius, String planMapId, Map<String, String> changePlanMap) {

        String addOnQuotaDetails = "UPDATE tblcustquotadtls SET " + "createdate = ?, usedquota = ?, quotaunit = ? " + "WHERE custpackageid = ?";

        try {
            converge.setAutoCommit(false);

            try (PreparedStatement convergeStatement = converge.prepareStatement(addOnQuotaDetails)) {

                // Set parameters for Converge database
                String createDate = changePlanMap.get("Startdate");
                double usedQuota = convertBytesToGB(changePlanMap.get("usedquota"));
                addOnQuotaDetailsQueryStatement(convergeStatement, createDate, String.valueOf(usedQuota), planMapId);

                int convergeResult = convergeStatement.executeUpdate();
                logger.info("Quota Update Query Execute Success ConvergeResult ..... {}",  convergeResult);


                if (convergeResult > 0) {
                    converge.commit();
                } else {
                    converge.rollback();
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error In SQL Query For Updating QuotaDetails {}", e.getMessage());
        }
    }

    private void addOnPlanQueryStatement(PreparedStatement convergeStatement, String createDate, String endDate, String planMapId) throws SQLException {

        convergeStatement.setString(1, createDate);
        convergeStatement.setString(2, createDate);
        convergeStatement.setString(3, endDate);
        convergeStatement.setString(4, endDate);
        convergeStatement.setString(5, planMapId);

    }

    private void addOnQuotaDetailsQueryStatement(PreparedStatement convergeStatement, String createDate, String usedQuota, String planMapId) throws SQLException {

        convergeStatement.setString(1, createDate);
        convergeStatement.setString(2, usedQuota);
        convergeStatement.setString(3, "GB");
        convergeStatement.setString(4, planMapId);

    }

    private double convertBytesToGB(String usedQuota) {
        if (usedQuota == null || usedQuota.trim().isEmpty()) {
            logger.error("usedQuota is null or empty");
            return 0.0;
        }
        try {
            long bytes = Long.parseLong(usedQuota.trim());
            return bytes / (1024.0 * 1024.0 * 1024.0);
        } catch (NumberFormatException e) {
            logger.error("Invalid number format for usedQuota: " + usedQuota, e);
            return 0.0;
        }
    }
}
