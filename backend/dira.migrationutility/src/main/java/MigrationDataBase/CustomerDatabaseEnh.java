package MigrationDataBase;
import java.sql.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class CustomerDatabaseEnh {

        private static final Logger logger = LoggerFactory.getLogger(DataBaseUpdateScript.class);

        public void updateCustomerDataInDatabases(Connection converge, Connection radius, String customerId,
                                                  String cprId, String planMappingId, Map<String, String> customerDetailsMap)
                throws SQLException {
            long startTime = System.currentTimeMillis();
          //  updateCustomerInfo(converge, customerId, customerDetailsMap);
         //   updatePackageInfo(converge, cprId, customerDetailsMap);
            updateQuotaInfo(converge, cprId, customerDetailsMap);
            updateIpMapping(converge, customerId, planMappingId, customerDetailsMap);
            updateMacMapping(converge, customerId, planMappingId, customerDetailsMap);

            long endTime = System.currentTimeMillis();
            logger.info("Update Records successfully in both database in all tables (UserName : {} ) Took Time (ms : {})",
                    customerDetailsMap.get("Username"), endTime - startTime);
        }

        private void updateCustomerInfo(Connection conn1, String customerId, Map<String, String> customerDetailsMap) throws SQLException {
            String updateCustomerQuery = "UPDATE tblcustomers SET createdate = ?, firstactivationdate = ?, " +
                    "lastmodifieddate = ?, accountnumber = ? WHERE custid = ?";

            try (PreparedStatement stmt1 = conn1.prepareStatement(updateCustomerQuery)) {
                conn1.setAutoCommit(false);  // Disable auto-commit for this transaction

                String createDate = customerDetailsMap.get("startdate");
                String cuiId = customerDetailsMap.get("cui").isEmpty() ? customerDetailsMap.get("Username") : customerDetailsMap.get("cui");

                setPreparedStatementParameters(stmt1, createDate, cuiId, customerId);

                int result1 = stmt1.executeUpdate();
                logger.info("Customer Info Update Execution Success: {}", result1);

                if (result1 > 0) {
                    conn1.commit();
                } else {
                    conn1.rollback();
                }
            } catch (SQLException e) {
                logger.error("Error Updating Customer Info: {}", e.getMessage(), e);
                if (conn1 != null) {
                    try {
                        conn1.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Error Rolling Back: {}", rollbackEx.getMessage(), rollbackEx);
                    }
                }
            } finally {
                try {
                    conn1.setAutoCommit(true);  // Reset auto-commit back to true
                } catch (SQLException e) {
                    logger.error("Error Resetting AutoCommit: {}", e.getMessage(), e);
                }
            }
        }

        private void updatePackageInfo(Connection conn1, String cprId, Map<String, String> customerDetailsMap) throws SQLException {
            String updatePackageQuery = "UPDATE tblcustpackagerel SET createdate = ?, startdate = ?, lastmodifieddate = ?, " +
                    "enddate = ?, expirydate = ? WHERE custpackageid = ?";

            try (PreparedStatement stmt1 = conn1.prepareStatement(updatePackageQuery)) {
                conn1.setAutoCommit(false);  // Disable auto-commit for this transaction

                String createDate = customerDetailsMap.get("startdate");
                String endDate = customerDetailsMap.get("enddate");

                setPackagePreparedStatementParameters(stmt1, createDate, endDate, cprId);

                int result1 = stmt1.executeUpdate();
                logger.info("Package Info Update Execution Success: {}", result1);

                if (result1 > 0) {
                    conn1.commit();
                } else {
                    conn1.rollback();
                }
            } catch (SQLException e) {
                logger.error("Error Updating Package Info: {}", e.getMessage(), e);
                if (conn1 != null) {
                    try {
                        conn1.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Error Rolling Back: {}", rollbackEx.getMessage(), rollbackEx);
                    }
                }
            } finally {
                try {
                    conn1.setAutoCommit(true);  // Reset auto-commit back to true
                } catch (SQLException e) {
                    logger.error("Error Resetting AutoCommit: {}", e.getMessage(), e);
                }
            }
        }

        private void updateQuotaInfo(Connection conn1, String cprId, Map<String, String> customerDetailsMap) throws SQLException {
            String updateQuotaDetails = "UPDATE tblcustquotadtls SET createdate = ?, usedquota = ?, quotaunit = ? WHERE custpackageid = ?";

            try (PreparedStatement stmt1 = conn1.prepareStatement(updateQuotaDetails)) {
                conn1.setAutoCommit(false);  // Disable auto-commit for this transaction

                String createDate = customerDetailsMap.get("startdate");
                double intoGb = convertBytesToGB(customerDetailsMap.get("usedquota"));
                setQuotaDetailsPreparedStatementParameters(stmt1, createDate, intoGb, cprId);

                int result1 = stmt1.executeUpdate();
                logger.info("Quota Update Execution Success: {}", result1);

                if (result1 > 0) {
                    conn1.commit();
                } else {
                    conn1.rollback();
                }
            } catch (SQLException e) {
                logger.error("Error Updating Quota Info: {}", e.getMessage(), e);
                if (conn1 != null) {
                    try {
                        conn1.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Error Rolling Back: {}", rollbackEx.getMessage(), rollbackEx);
                    }
                }
            } finally {
                try {
                    conn1.setAutoCommit(true);  // Reset auto-commit back to true
                } catch (SQLException e) {
                    logger.error("Error Resetting AutoCommit: {}", e.getMessage(), e);
                }
            }
        }

        private void updateIpMapping(Connection conn1, String customerId, String planMappingId, Map<String, String> customerDetailsMap) throws SQLException {
            String ipAddress = customerDetailsMap.get("FramedIPAddress");
            if (ipAddress == null || ipAddress.isEmpty()) {
                logger.info("IP Not available for UserName: {} and skipping tblcustipmapping.", customerDetailsMap.get("Username"));
                return; // Skip if no IP is provided
            }

            String insertIpMappingQueryConverge = "INSERT INTO tblcustipmapping (custid, ip_address, ip_type, custsermappingid, " +
                    "createdate, lastmodifieddate, createbyname, updatebyname, CREATEDBYSTAFFID, " +
                    "LASTMODIFIEDBYSTAFFID, service) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt1 = conn1.prepareStatement(insertIpMappingQueryConverge)) {
                conn1.setAutoCommit(false);  // Disable auto-commit for this transaction

                String createDate = customerDetailsMap.get("startdate");
                insertDataIntoCustomerIpMappingConverge(stmt1, customerId, ipAddress, "Ipv4", planMappingId,
                        createDate, createDate, "admin", "admin", "2", "2", "BroadBand");

                int result1 = stmt1.executeUpdate();
                logger.info("IP Address Insert Success for UserName: {}", customerDetailsMap.get("Username"));

                if (result1 > 0) {
                    conn1.commit();
                } else {
                    conn1.rollback();
                }
            } catch (SQLException e) {
                logger.error("Error Updating IP Mapping: {}", e.getMessage(), e);
                if (conn1 != null) {
                    try {
                        conn1.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Error Rolling Back: {}", rollbackEx.getMessage(), rollbackEx);
                    }
                }
            } finally {
                try {
                    conn1.setAutoCommit(true);  // Reset auto-commit back to true
                } catch (SQLException e) {
                    logger.error("Error Resetting AutoCommit: {}", e.getMessage(), e);
                }
            }
        }

        private void updateMacMapping(Connection conn1, String customerId, String planMappingId, Map<String, String> customerDetailsMap) throws SQLException {
            String macAddress = customerDetailsMap.get("callingstationid");
            if (macAddress == null || macAddress.isEmpty() || macAddress.contains(".")) {
                logger.info("MAC Address Not available for UserName: {} and skipping tblcustmacmapping.", customerDetailsMap.get("Username"));
                return; // Skip if no MAC address is provided
            }

            String insertMacMappingQueryConverge = "INSERT INTO tblcustmacmapping (custid, macaddress, is_deleted, createbyname, " +
                    "updatebyname, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, createdate, " +
                    "lastmodifieddate, custsermappingid, service) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt1 = conn1.prepareStatement(insertMacMappingQueryConverge)) {
                conn1.setAutoCommit(false);  // Disable auto-commit for this transaction

                String createDate = customerDetailsMap.get("startdate");
                insertDataIntoCustomerMacMappingConverge(stmt1, customerId, macAddress, "admin", "admin", "2", "2", createDate, createDate, planMappingId, "BroadBand");

                int result1 = stmt1.executeUpdate();
                logger.info("MAC Address Insert Success for UserName: {}", customerDetailsMap.get("Username"));

                if (result1 > 0) {
                    conn1.commit();
                } else {
                    conn1.rollback();
                }
            } catch (SQLException e) {
                logger.error("Error Updating MAC Mapping: {}", e.getMessage(), e);
                if (conn1 != null) {
                    try {
                        conn1.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Error Rolling Back: {}", rollbackEx.getMessage(), rollbackEx);
                    }
                }
            } finally {
                try {
                    conn1.setAutoCommit(true);  // Reset auto-commit back to true
                } catch (SQLException e) {
                    logger.error("Error Resetting AutoCommit: {}", e.getMessage(), e);
                }
            }
        }

        private void setPreparedStatementParameters(PreparedStatement preparedStatement, String createDate, String cuiId, String customerId) throws SQLException {
            preparedStatement.setString(1, createDate);
            preparedStatement.setString(2, createDate);
            preparedStatement.setString(3, createDate);
            preparedStatement.setString(4, cuiId);
            preparedStatement.setString(5, customerId);
        }

        private void setPackagePreparedStatementParameters(PreparedStatement preparedStatement, String createDate, String endDate, String cprId) throws SQLException {
            preparedStatement.setString(1, createDate);
            preparedStatement.setString(2, createDate);
            preparedStatement.setString(3, createDate);
            preparedStatement.setString(4, endDate);
            preparedStatement.setString(5, endDate);
            preparedStatement.setString(6, cprId);
        }

        private void setQuotaDetailsPreparedStatementParameters(PreparedStatement preparedStatement, String createDate, double intoGb, String cprId) throws SQLException {
            preparedStatement.setString(1, createDate);
            preparedStatement.setString(2, String.valueOf(intoGb));
            preparedStatement.setString(3, "GB");
            preparedStatement.setString(4, cprId);
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

        private void insertDataIntoCustomerIpMappingConverge(PreparedStatement preparedStatement, String customerId, String ipAddress,
                                                             String ipType, String planMappingId, String createDate, String lastModifiedDate,
                                                             String createByName, String updateByName, String createStaffId, String updateStaffId,
                                                             String service) throws SQLException {
            preparedStatement.setString(1, customerId);
            preparedStatement.setString(2, ipAddress);
            preparedStatement.setString(3, ipType);
            preparedStatement.setString(4, planMappingId);
            preparedStatement.setString(5, createDate);
            preparedStatement.setString(6, lastModifiedDate);
            preparedStatement.setString(7, createByName);
            preparedStatement.setString(8, updateByName);
            preparedStatement.setString(9, createStaffId);
            preparedStatement.setString(10, updateStaffId);
            preparedStatement.setString(11, service);
        }

        private void insertDataIntoCustomerMacMappingConverge(PreparedStatement preparedStatement, String customerId, String macAddress,
                                                              String createByName, String updateByName, String createStaffId, String updateStaffId,
                                                              String createDate, String lastModifiedDate, String planMappingId, String service) throws SQLException {
            preparedStatement.setString(1, customerId);
            preparedStatement.setString(2, macAddress);
            preparedStatement.setString(3,"1");
            preparedStatement.setString(4, createByName);
            preparedStatement.setString(5, updateByName);
            preparedStatement.setString(6, createStaffId);
            preparedStatement.setString(7, updateStaffId);
            preparedStatement.setString(8, createDate);
            preparedStatement.setString(9, lastModifiedDate);
            preparedStatement.setString(10, planMappingId);
            preparedStatement.setString(11, service);
        }
    }


