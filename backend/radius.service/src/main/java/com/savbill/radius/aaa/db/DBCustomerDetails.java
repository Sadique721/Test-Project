package com.savbill.radius.aaa.db;

import com.savbill.radius.config.DbConfig;
import com.savbill.radius.helper.CustomerPlanDataForResetQuota;
import com.savbill.radius.helper.CustomerQuotaDataForReset;
import com.savbill.radius.helper.CustomerQuotaReset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBCustomerDetails {

    private static final Logger log = LoggerFactory.getLogger(DBCustomerDetails.class);

    private static String strRadiusCustomerQuery = "SELECT c.custid, c.NEXTBILLDATE,c.BILLDAY, c.nextquotaresetdate, c.customertype, c.username, c.MVNOID, " +
            "p.custpackageid, p.startdate, p.enddate, p.planid, " +
            "q.quotadtlsid, q.usedquota, q.usedquotakb, q.timequotaused, q.timeusedquotasec, " +
            "q.is_chunk_available, q.reserved_quota_in_per, q.skip_quota_update, q.last_quota_reset, " +
            "pp.POSTPAIDPLANID, pp.NAME, pp.quotarestinterval,pp.unitsofvalidity, pp.validity, l.CDRID " +
            "FROM tblcustomers c " +
            "LEFT JOIN tblcustpackagerel p ON c.custid = p.custid " +
            "LEFT JOIN tblcustquotadtls q ON p.custpackageid = q.custpackageid " +
            "LEFT JOIN tblmpostpaidplan pp ON pp.POSTPAIDPLANID = p.planid " +
            "LEFT JOIN tbltliveuser l ON c.custid = l.custid " +
            "WHERE c.nextquotaresetdate = ? " + // Dynamic date parameter
            "AND c.cstatus IN ('Active', 'Inactive') " +
            "AND p.cust_plan_status != 'STOP' " +
            "AND p.purchase_type = 'NEW' " +
            "ORDER BY c.custid";

    private static String strUpdateCPRendDate = "update tblcustpackagerel t set t.enddate = ? , t.expirydate = ? where t.custpackageid  = ? ";
    private static String strUpdateNextBillDate = "update tblcustomers t set t.NEXTBILLDATE = ?, t.nextquotaresetdate = ? where t.custid = ?";

    private static String strUpdateCustomerQuota = "update tblcustquotadtls cquota set currentsessionusagetime=0,currentsessionusagevolume=0,usedquota=0,timequotaused=0, isquotaupdateskipped = false, last_quota_reset=? where cquota.custpackageid=?";
    private static String strUpdateCustomerQuotaWithSkipQuotaUpdateflag = "update tblcustquotadtls cquota set isquotaupdateskipped = true, last_quota_reset=? where cquota.custpackageid=?";

    public Map<Integer, CustomerQuotaReset> getCustomerPlansDtls(String nextQuotaResetDate) throws SQLException {
        Map<Integer, CustomerQuotaReset> customerMap = new LinkedHashMap<>();
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(strRadiusCustomerQuery)) {
            stmt.setString(1, nextQuotaResetDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer custId = rs.getInt("custid"); // custId
                    // Retrieve or create CustomerQuotaReset object
                    CustomerQuotaReset customer = customerMap.get(custId);
                    if (customer == null) {
                        customer = new CustomerQuotaReset();
                        customer.setCustId(custId);
                        if (rs.getTimestamp("NEXTBILLDATE") != null)
                            customer.setNextBillDate(rs.getDate("NEXTBILLDATE").toLocalDate()); // nextBillDate
                        else
                            customer.setNextBillDate(LocalDate.now());
                        if (rs.getTimestamp("nextquotaresetdate") != null)
                            customer.setNextQuotaResetDate(rs.getDate("nextquotaresetdate").toLocalDate()); // nextquotaresetdate
                        else
                            customer.setNextQuotaResetDate(LocalDate.now());

                        customer.setCustType(rs.getString("customertype")); // custType
                        customer.setUsername(rs.getString("username")); // username
                        customer.setMvnoId(rs.getInt("MVNOID")); // mvnoId
                        customer.setCdrId(rs.getLong("CDRID"));
                        customer.setBillDay(rs.getInt("BILLDAY"));
                        customer.setCustomerPlanData(new ArrayList<>());
                    }

                    // Add Plan Data
                    Long cprId = rs.getLong("custpackageid"); // cprId
                    if (cprId != 0) {
                        CustomerPlanDataForResetQuota planData = new CustomerPlanDataForResetQuota();
                        planData.setCprId(cprId);
                        if (rs.getTimestamp("startdate") != null)
                            planData.setStartDate(rs.getTimestamp("startdate").toLocalDateTime()); // startDate
                        if (rs.getTimestamp("enddate") != null)
                            planData.setEndDate(rs.getTimestamp("enddate").toLocalDateTime()); // endDate
                        planData.setPlanId(rs.getInt("POSTPAIDPLANID")); // planId
                        planData.setPlanName(rs.getString("NAME")); // planName
                        planData.setUnitsofvalidity(rs.getString("unitsofvalidity")); // unitsofvalidity
                        planData.setQuotarestinterval(rs.getString("quotarestinterval")); // quotarestinterval
                        planData.setValidity(rs.getInt("validity")); // validity

                        // Add Quota Data
                        Integer cqdId = rs.getInt("quotadtlsid"); // cqdId
                        if (cqdId != 0) {
                            CustomerQuotaDataForReset quotaData = new CustomerQuotaDataForReset();
                            quotaData.setCqdId(cqdId);
                            quotaData.setUsedQuota(rs.getDouble("usedquota")); // usedQuota
                            quotaData.setUsedQuotaKB(rs.getDouble("usedquotakb")); // usedQuotaKB
                            quotaData.setTimeQuotaUsed(rs.getDouble("timequotaused")); // timeQuotaUsed
                            quotaData.setTimeUsedQuotaSec(rs.getDouble("timeusedquotasec")); // timeUsedQuotaSec
                            quotaData.setChunkAvailable(rs.getBoolean("is_chunk_available")); // isChunkAvailable
                            quotaData.setReservedQuotaInPer(rs.getDouble("reserved_quota_in_per")); // reservedQuotaInPer
                            quotaData.setSkipQuotaUpdate(rs.getBoolean("skip_quota_update")); // skipQuotaUpdate
                            if (rs.getTimestamp("last_quota_reset") != null)
                                quotaData.setLastQuotaReset(rs.getTimestamp("last_quota_reset").toLocalDateTime()); // lastQuotaReset
                            planData.setCustomerQuotaData(quotaData);
                        }
                        customer.getCustomerPlanData().add(planData);
                    }
                    customerMap.put(custId, customer);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error to fetch customer data for reset quota for date: " + nextQuotaResetDate);
        }
        return customerMap;
    }

    public boolean updateCPRendDate(Long custpackageid, LocalDateTime endDate) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update CPR FROM Query : %s : custpackageid %s : endDate %s",
                    strUpdateCPRendDate, custpackageid, endDate));
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // Load DB Driver (only needed if not using DataSource)
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));

            // Get Database Connection
            conn = DataSource.getConnection();

            // Prepare Statement with Correct SQL Query
            stmt = conn.prepareStatement(strUpdateCPRendDate);

            // Set Parameters in the Correct Order
            stmt.setTimestamp(1, Timestamp.valueOf(endDate)); // Set enddate
            stmt.setTimestamp(2, Timestamp.valueOf(endDate)); // Set expirydate
            stmt.setLong(3, custpackageid); // Set custpackageid

            // Execute Update and Return Success Status
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Returns true if at least one row was updated
        } catch (Exception e) {
            log.error("SQLException", e);
            e.printStackTrace();
            return false;
        } finally {
            // Close Resources in `finally` Block to Prevent Leaks
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }


    public boolean updateCustomerNextBillDate(Integer custId, LocalDate nextBillDate, LocalDate nextQuotaResetDate) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update CPR FROM Query : %s : custId %s : nextBillDate %s : nextQuotaResetDate %s",
                    strUpdateNextBillDate, custId, nextBillDate, nextQuotaResetDate));
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // Load DB Driver (not needed if using a DataSource)
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));

            // Get Connection
            conn = DataSource.getConnection();

            // Use Positional Parameters
            stmt = conn.prepareStatement(strUpdateNextBillDate);
            stmt.setDate(1, Date.valueOf(nextBillDate));  // Set NEXTBILLDATE
            stmt.setDate(2, Date.valueOf(nextQuotaResetDate));  // Set nextquotaresetdate
            stmt.setInt(3, custId);  // Set custid

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (Exception e) {
            log.error("SQLException", e);
            e.printStackTrace();
            return false;
        } finally {
            // Close resources in `finally` to prevent memory leaks
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }


    public boolean updateQuota(Long custpackageid, Boolean skipQuotaReset, LocalDateTime lastQuotaReset) throws SQLException {
        if (lastQuotaReset == null)
            lastQuotaReset = LocalDateTime.now();

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            if (skipQuotaReset != null && skipQuotaReset) {
                stmt = conn.prepareStatement(strUpdateCustomerQuotaWithSkipQuotaUpdateflag);
                log.debug("IN Update updateQuota FROM DB" + strUpdateCustomerQuotaWithSkipQuotaUpdateflag);
            } else {
                stmt = conn.prepareStatement(strUpdateCustomerQuota);
                log.debug("IN Update updateQuota FROM DB" + strUpdateCustomerQuota);
            }
            stmt.setTimestamp(1, Timestamp.valueOf(lastQuotaReset));
            stmt.setLong(2, custpackageid);
            stmt.executeUpdate();
            log.info("Update Quota: " + stmt.toString());
        } catch (Exception e) {
            log.error("SQLException: ", e);
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return true;
    }
}
