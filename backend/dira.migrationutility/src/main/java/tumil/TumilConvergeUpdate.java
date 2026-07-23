package tumil;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TumilConvergeUpdate {

	    private static final Logger logger = LoggerFactory.getLogger(TumilConvergeUpdate.class);

	    public synchronized void updateCustomerDataInDatabases(Connection converge,Connection radius,String customerId, String cprId, String planMappingId,
	                                              Map<String, String> customerDetailsMap, String createdbyname, String createdbyid) throws SQLException, InterruptedException, ExecutionException {
	        long startTime = System.currentTimeMillis();
	        //custmer update 
	        updateCustomerInfo(converge, radius,customerId, customerDetailsMap);
	        // cust package rel updtion
	        updatePackageInfo(converge, radius,cprId,customerDetailsMap);
	    //    updateInvoiceInfo(converge,radius, cprId, customerDetailsMap);
	        insertNotesMapping(converge,radius,customerId, createdbyname,createdbyid, customerDetailsMap);
	        
	        updateQuotaInfo(converge, cprId, customerDetailsMap);
	        updateIpMapping(converge,customerId, planMappingId, customerDetailsMap);

	            long endTime = System.currentTimeMillis();
	            logger.info("Update Records successfully in both database in all table With (UserName : {} ) And Taking Time (ms : {})",customerDetailsMap.get("Username"),endTime-startTime);
	    }

	    private synchronized void updateCustomerInfo(Connection conn1, Connection conn2,String customerId, Map<String, String> customerDetailsMap) throws SQLException {
	        String updateCustomerQuery = "UPDATE tblcustomers SET " +
	                "createdate = ?, firstactivationdate = ?, lastmodifieddate = ?, last_login_time=?" +
	                "WHERE custid = ?";

	        try {
	            conn1.setAutoCommit(false);


	            try (PreparedStatement stmt1 = conn1.prepareStatement(updateCustomerQuery)) {

	                String createDate = customerDetailsMap.get("Registered"); // registered
	                String lastLogin=customerDetailsMap.get("LastLogin");
//	                String cuiId = customerDetailsMap.get("AccountNo").isEmpty() ?
//	                        customerDetailsMap.get("Username") : customerDetailsMap.get("AccountNo");

	               
	                setPreparedStatementParameters(stmt1, createDate,lastLogin, customerId);


	                int result1 = stmt1.executeUpdate();
	                logger.info("?????? (CustomerInfoUpdate Execute Success in) (tblcustomers) ?????? {}", result1);

	                if (result1 > 0) {
	                    conn1.commit();

	                } else {
	                    conn1.rollback();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            logger.error("Error Getting In Method updateCustomerInfo: {}", e.getMessage());
	        }
	    }
	  
	    
	    // added for this method null check-->
	    private synchronized void updatePackageInfo(Connection conn1, Connection conn2, String cprId, Map<String, String> customerDetailsMap) throws SQLException {
	        String updatePackageQuery = "UPDATE tblcustpackagerel SET " +
	                "createdate = ?, startdate = ?" +
	                "WHERE custpackageid = ?";

	        try {
	            conn1.setAutoCommit(false);

	            try (PreparedStatement stmt1 = conn1.prepareStatement(updatePackageQuery)) {

	                String createDate = customerDetailsMap.get("startdate");
	             //   String endDate = customerDetailsMap.get("enddate");

	                setPackagePreparedStatementParameters(stmt1, createDate, cprId);

	                int result1 = stmt1.executeUpdate();
	                logger.info("PackageInfoUpdate Execute Success in (tblcustpackagerel) {}", result1);

	                if (result1 > 0) {
	                    conn1.commit();
	                } else {
	                    conn1.rollback();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            logger.error("Error in Method updatePackageInfo: {}", e.getMessage());
	        }
	    
	    }
	    
	    private synchronized void updateQuotaInfo(Connection conn1,String cprId, Map<String, String> customerDetailsMap) throws SQLException {
	        String updateQuotaDetails = "UPDATE tblcustquotadtls SET " +
	                "createdate = ?, usedquota = ?, quotaunit = ? WHERE custpackageid = ?";

	        try {
	            conn1.setAutoCommit(false);
//	            conn2.setAutoCommit(false);

	            try (PreparedStatement stmt1 = conn1.prepareStatement(updateQuotaDetails)) {

	                String createDate = customerDetailsMap.get("startdate");
	                String quota=customerDetailsMap.get("Usedquota");

	                synchronized (this) {
	               
	                	double quotaValue = 0.0;
	                	if (quota != null && !quota.isEmpty()) {
	                	    try {
	                	        quotaValue = Double.parseDouble(quota);
	                	    } catch (NumberFormatException e) {
	                	    	quotaValue = 0.0;
	                	        System.out.println("Invalid number format: " + quota);
	                	    }
	                	}

	                  
	                    setQuotaDetailsPreparedStatementParameters(stmt1, createDate, quotaValue, cprId);
//	                    setQuotaDetailsPreparedStatementParameters(stmt2, createDate, intoGb, cprId);
	                }
	                int result1 = stmt1.executeUpdate();
	                logger.info("??????? (QuotaUpdate Query Success in) (tblcustquotadtls) ??????? {}", result1);
//	             //   int result2 = stmt2.executeUpdate();
//	             //   logger.info("Result for converge conn1 in tblcustquotadtls  : {}", result1);
//	             //   logger.info("Result for radius conn2 in tblcustquotadtls  : {}", result2);

	                if (result1 > 0) {
	                    conn1.commit();
//	               //     conn2.commit();
	                } else {
	                    conn1.rollback();
//	                //    conn2.rollback();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            logger.error("Error Getting In Method updateQuotaInfo: {}", e.getMessage());
	        }
	    }

	    private synchronized void updateIpMapping(Connection conn1,String customerId, String planMappingId,
	            Map<String, String> customerDetailsMap) throws SQLException {
	String ipAddress = customerDetailsMap.get("IpAddress");
	if (ipAddress == null || ipAddress.isEmpty()) {
	logger.info("IP Not available In Sheet for this (UserName : {} ) And skip entry for table : (tblcustipmapping) ", customerDetailsMap.get("Username"));
	return; // Not having an IP is not considered a failure
	}

	String insertIpMappingQueryConverge = "INSERT INTO tblcustipmapping " +
	"(custid, ip_address, ip_type, custsermappingid, createdate, lastmodifieddate, " +
	"createbyname, updatebyname, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, service) " +
	"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";




	try {
	conn1.setAutoCommit(false);
	//conn2.setAutoCommit(false);

	try (PreparedStatement stmt1 = conn1.prepareStatement(insertIpMappingQueryConverge)) {

	// Set parameters for Converge database
	String createDate = customerDetailsMap.get("startdate");
	String service = customerDetailsMap.get("Service");
	insertDataIntoCustomerIpMappingConverge(stmt1, customerId, ipAddress, "Ipv4",
	planMappingId, createDate, createDate, "admin", "admin", "2", "2", service);

	int result1 = stmt1.executeUpdate();
	logger.info("****** (IpAddress Query Execute Success For Database Converge in) (tblcustipmapping) ****** {}", result1);


	// Get generated ID
	//long generatedId = -1;
	//ResultSet rs = stmt1.getGeneratedKeys();
	//if (rs.next()) {
	//generatedId = rs.getLong(1);
	//}

	//if (generatedId != -1) {
	// Set parameters for Radius database
	//insertDataIntoCustomerIpMappingRadius(stmt2, generatedId, customerId, ipAddress,
	//"Ipv4", createDate, createDate, "admin", "admin", planMappingId);

	//int result2 = stmt2.executeUpdate();
	//logger.info("******* (IpAddress Query Execute Success For Database Radius in) (tblcustipmapping) *******{}", result2);
	if (result1 > 0) {
	conn1.commit();
	//conn2.commit();
	return;
	} else {
	conn1.rollback();
	//conn2.rollback();
	return;
	}
	//}
	}
	} catch (SQLException e) {
	e.printStackTrace();
	logger.error("Error Getting In Method updateIpMapping: {}", e.getMessage());
	}
	}
	   // invoice   billdate,createdate,startdate,enddate,duedate,latepaymentdate
	    private synchronized void updateInvoiceInfo(Connection conn1, Connection conn2, String cprId, Map<String, String> customerDetailsMap) throws SQLException {
	        String updateInvoiceDetails = "UPDATE tbltdebitdocument SET " +
	                "billdate = ?, createdate = ?, startdate = ?, " +
	                "enddate = COALESCE(?, enddate), duedate = ?, latepaymentdate = ? " +
	                "WHERE custpackrelid = ?";

	        try {
	            conn1.setAutoCommit(false);

	            try (PreparedStatement stmt1 = conn2.prepareStatement(updateInvoiceDetails)) {

	                String createDate = customerDetailsMap.get("startdate");
	                String endDate = customerDetailsMap.get("enddate");

	                synchronized (this) {
	                    // Set parameters for the prepared statement
	                    setInvoiceDetailsPreparedStatementParameters(stmt1, createDate, endDate, cprId);
	                }

	                // Execute the update
	                int result1 = stmt1.executeUpdate();
	                logger.info("Invoice Query Success in tbltdebitdocument, Result: {}", result1);

	                if (result1 > 0) {
	                    conn1.commit();
	                } else {
	                    conn1.rollback();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            logger.error("Error in Method updateInvoiceInfo: {}", e.getMessage());
	        }
	    }

	    // inserting customer notes 
	    private synchronized void insertNotesMapping(Connection conn1,Connection conn2,String customerId, String createdbyname,String createdbyid,
	            Map<String, String> customerDetailsMap) throws SQLException {
	String note = customerDetailsMap.get("Updates");
	if (note == null || note.isEmpty()) {
	logger.info("Notes Not available In Sheet for this (UserName : {} ) And skip entry for table : (tblcustipmapping) ", customerDetailsMap.get("Username"));
	return; // Not having an IP is not considered a failure
	}

	// customer_notes_id,notes,custid,created_on,created_by,created_by_name
	String insertNotesQueryConverge = "INSERT INTO tbltcustomernotes " +
	"(notes, custid, created_on, created_by, created_by_name)" +
	"VALUES (?, ?, ?, ?, ?)";
	   
	try {
	    conn1.setAutoCommit(false);

	   try (PreparedStatement stmt1 = conn1.prepareStatement(insertNotesQueryConverge)) {

	       // Set parameters for Converge database
	       String createDate = customerDetailsMap.get("Registered");
	       insertDataIntoCustomerNotesConverge(stmt1, note,customerId, createDate,
	    		   createdbyid, createdbyname );

	       int result1 = stmt1.executeUpdate();
	       logger.info("****** (Notes Query Execute Success For Database Converge in) (tbltcustomernotes) ****** {}", result1);
	       if (result1 > 0) {
	           conn1.commit();
	           return;
	       } else {
	           conn1.rollback();

	           return;
	       }

	}
	} catch (SQLException e) {
	e.printStackTrace();
	logger.error("Error Getting In Method InsertNotes: {}", e.getMessage());
	}
	}

	    // Existing helper methods remain unchanged
	    private void setPreparedStatementParameters(PreparedStatement preparedStatement,
	                                                String createDate, String lastLoginDate, String customerId) throws SQLException {
	        preparedStatement.setString(1, createDate);
	        preparedStatement.setString(2, createDate);
	        preparedStatement.setString(3, createDate);
	        preparedStatement.setString(4, lastLoginDate);
	        preparedStatement.setString(5, customerId);
	    }

	    // custpackage updation 
	    private void setPackagePreparedStatementParameters(PreparedStatement preparedStatement,
	            String createDate, String cprId) throws SQLException {
	preparedStatement.setString(1, createDate);
	preparedStatement.setString(2, createDate);

	//// If enddate is not null or empty, set it in the prepared statement
	//if (enddate != null && !enddate.isEmpty()) {
	//preparedStatement.setString(4, enddate);
	//preparedStatement.setString(5, enddate);
	//} else {
	//
	//preparedStatement.setNull(4, java.sql.Types.NULL); 
	//preparedStatement.setNull(5, java.sql.Types.NULL); 
	//}

	preparedStatement.setString(3, cprId);
	}
	    
	    // Invoice 
	    private void setInvoiceDetailsPreparedStatementParameters(PreparedStatement preparedStatement,
	            String createDate, String enddate, String cprId) throws SQLException {
	preparedStatement.setString(1, createDate); // Set billdate
	preparedStatement.setString(2, createDate); // Set createdate
	preparedStatement.setString(3, createDate); // Set startdate

	// Check if enddate is null or empty before setting it
	if (enddate != null && !enddate.isEmpty()) {
	preparedStatement.setString(4, enddate);  // Set enddate if it's not null or empty
	} else {
	preparedStatement.setNull(4, java.sql.Types.NULL); // Set it as NULL if enddate is null or empty
	}

	preparedStatement.setString(5, createDate); // Set duedate
	preparedStatement.setString(6, createDate); // Set latepaymentdate
	preparedStatement.setString(7, cprId);      // Set custpackrelid
	}
	    
	    
	    private void setQuotaDetailsPreparedStatementParameters(PreparedStatement preparedStatement,
	            String createDate, double intoGb, String cprId) throws SQLException {
	preparedStatement.setString(1, createDate);
	preparedStatement.setString(2, String.valueOf(intoGb));
	preparedStatement.setString(3, "GB");
	preparedStatement.setString(4, cprId);
	}

	    
	//stmt1, note,customerId, createDate,
		   
	    private void insertDataIntoCustomerNotesConverge(PreparedStatement preparedStatement,
	            String note, String customerId, String createDate, String createdbyid,
	            String createdbyname)  throws SQLException {
	preparedStatement.setString(1, note);
	preparedStatement.setString(2, customerId);
	preparedStatement.setString(3, createDate);
	preparedStatement.setString(4, createdbyid);
	preparedStatement.setString(5, createdbyname);

	}   
	    private void insertDataIntoCustomerIpMappingConverge(PreparedStatement preparedStatement,
	            String customerId, String ipAddress, String ipType, String custsermappingid,
	            String createDate, String lastModifiedDate, String createByName, String updateByName,
	            String createStaffId, String lastModifiedStaffId, String service) throws SQLException {
	preparedStatement.setString(1, customerId);
	preparedStatement.setString(2, ipAddress);
	preparedStatement.setString(3, ipType);
	preparedStatement.setString(4, custsermappingid);
	preparedStatement.setString(5, createDate);
	preparedStatement.setString(6, lastModifiedDate);
	preparedStatement.setString(7, createByName);
	preparedStatement.setString(8, updateByName);
	preparedStatement.setString(9, createStaffId);
	preparedStatement.setString(10, lastModifiedStaffId);
	preparedStatement.setString(11, service);
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
