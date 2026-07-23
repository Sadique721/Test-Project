package com.savbill.radius.aaa.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IPPoolManagementService {

    private static final String SQL_EXCEPTION = "SQLException";
    private static final Logger log = LoggerFactory.getLogger(IPPoolManagementService.class);
    private static final String strGetFreeIpFromIpPool = "select *  from tblipallocationdtls  where pool_id in ( ? ) and status = 'Free' and is_delete = 0 LIMIT 1 for update";
    private static final String updateIPPoolForAllocation = "update tblipallocationdtls set status=?, block_by_cust_id = ?, block_by_session_id = ?, lastmodificationdate = ?, nas_ip_address = ? where pool_id = ? and ip_address = ?";
    public String allocateFreeIpFromPool(List<Long> poolIdList, int custId, String acctSessionId, String nasIpAddress) throws SQLException {

        String freeIp = null;
        Long poolId = null;

        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET Free Ip FROM IP-Pool DB : %s", strGetFreeIpFromIpPool));
        }

        ResultSet resultSet;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DataSource.getConnection();
            connection.setAutoCommit(false);

            String inClause = poolIdList.stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(","));

            String query = strGetFreeIpFromIpPool.replace("?", inClause);

            stmt = connection.prepareStatement(query);

            for (int i=0; i < poolIdList.size(); i++) {
                stmt.setLong(i+1, poolIdList.get(i));
            }

            resultSet = stmt.executeQuery();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", resultSet));
            }
            while (resultSet.next()) {
                freeIp = resultSet.getString("ip_address");
                poolId = resultSet.getLong("pool_id");
                break;
            }

            if (freeIp != null && !freeIp.trim().isEmpty()) {

                try (PreparedStatement updateStmt
                             = connection.prepareStatement(updateIPPoolForAllocation)) {

                    updateStmt.setString(1, "Reserved");
                    updateStmt.setString(2, String.valueOf(custId));
                    updateStmt.setString(3, String.valueOf(acctSessionId));
                    updateStmt.setTimestamp(4, new Timestamp(new Date().getTime()));
                    updateStmt.setString(5, nasIpAddress);
                    updateStmt.setString(6, String.valueOf(poolId));
                    updateStmt.setString(7, freeIp);

                    int finalResult = updateStmt.executeUpdate();
                    log.debug(String.format("Reserved IP: %s for customer id: %s", freeIp, custId));
                    log.debug(String.format("No of row updated on IP Reserved: %s", finalResult));

                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            connection.setAutoCommit(true);
            log.error(SQL_EXCEPTION, e);
        } finally {
            connection.commit();
            connection.setAutoCommit(true);
            stmt.close();
            connection.close();
        }
        return freeIp;

    }


    public int setIpAllocatedStatusInIpPool(String status, int custid, String acctSessionId, String nasIpAddress) {

        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update Ip Pool FROM DB : %s", ""));
        }
        int result = 0;
        String sqlForStart = "update tblipallocationdtls set status=?, lastmodificationdate = ? where block_by_cust_id = ? and block_by_session_id = ? and nas_ip_address = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlForStart)) {

            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            stmt.setString(3, String.valueOf(custid));
            stmt.setString(4, String.valueOf(acctSessionId));
            stmt.setString(5, String.valueOf(nasIpAddress));

            result = stmt.executeUpdate();
            log.debug(String.format("No of row updated in update IP-Pool operation: %s", result));
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(SQL_EXCEPTION, e);
        }
        return result;
    }

    public int setIpStatusToFreeInIpPool(String status, int custId, String acctSessionId, String nasIpAddress) {

        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update Ip Pool FROM DB : %s", ""));
        }
        int result = 0;
        String sqlForStop = "update tblipallocationdtls set status=?, lastmodificationdate = ?, block_by_cust_id = ?, block_by_session_id = ?, nas_ip_address = ? where block_by_cust_id = ? and block_by_session_id = ? and nas_ip_address = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlForStop)) {

            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            stmt.setString(3, null);
            stmt.setString(4, null);
            stmt.setString(5, null);
            stmt.setString(6, String.valueOf(custId));
            stmt.setString(7, String.valueOf(acctSessionId));
            stmt.setString(8, String.valueOf(nasIpAddress));

            result = stmt.executeUpdate();

            log.debug(String.format("No of row updated in IP-Release on Stop operation: %s", result));
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(SQL_EXCEPTION, e);
        }

        return result;
    }

    public int setLastModifiedTImeIpPool(int custid, String acctSessionId, String nasIpAddress) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update Ip Pool FROM DB : %s", ""));
        }
        int result = 0;
        String sqlForStop = "update tblipallocationdtls set lastmodificationdate = ? where block_by_cust_id = ? and block_by_session_id = ? and nas_ip_address = ? and status = 'Allocated'";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlForStop)) {

            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.setString(2, String.valueOf(custid));
            stmt.setString(3, String.valueOf(acctSessionId));
            stmt.setString(4, String.valueOf(nasIpAddress));

            result = stmt.executeUpdate();

            log.debug(String.format("No of row updated in update IP-Pool operation: %s", result));
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(SQL_EXCEPTION, e);
        }

        return result;

    }

}
