package SavanaCustomer;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DatabaseUpdationCaf {
	

	

	
	    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdationCaf.class);

	    public synchronized void updateCustomerDataInDatabases(Connection converge,Connection radius,String customerId, String cprId, String planMappingId,
	                                              Map<String, String> customerDetailsMap, String createdbyname, String createdbyid) throws SQLException, InterruptedException, ExecutionException {
	        long startTime = System.currentTimeMillis();
	        //custmer update 
	        updateCustomerInfo(converge, radius, customerId, customerDetailsMap);
	        // cust package rel updtion
	      //  updatePackageInfo(converge, radius,cprId,customerDetailsMap);
	    
	        
	     //   updateInvoiceInfo(converge,radius, cprId, customerDetailsMap);
	       
	        insertNotesMapping(converge,radius,customerId, createdbyname,createdbyid, customerDetailsMap);

	            long endTime = System.currentTimeMillis();
	            logger.info("Update Records successfully in both database in all table With (UserName : {} ) And Taking Time (ms : {})",customerDetailsMap.get("Username"),endTime-startTime);
	    }

	    private synchronized void updateCustomerInfo(Connection conn1, Connection conn2,String customerId, Map<String, String> customerDetailsMap) throws SQLException {
	        String updateCustomerQuery = "UPDATE tblcustomers SET " +
	                "accountnumber = ?, username = ?, password = ? " +
	                "WHERE custid = ?";

	        try {
	            conn1.setAutoCommit(false);


	            try (PreparedStatement stmt1 = conn1.prepareStatement(updateCustomerQuery)) {

	              
	                String cuiId = customerDetailsMap.get("AccountNo").isEmpty() ?
	                        customerDetailsMap.get("Username") : customerDetailsMap.get("AccountNo");

                    // set 3 fields + custid
                    stmt1.setString(1, cuiId);     // accountnumber
                    stmt1.setString(2, cuiId);     // username
                    stmt1.setString(3, cuiId);     // password
                    stmt1.setString(4, customerId); // WHERE custid = ?
//	                setPreparedStatementParameters(stmt1, cuiId, customerId);


//                    System.out.print("result =" + stmt1);

	                int result1 = stmt1.executeUpdate();
	                logger.info("??????? (CustomerInfoUpdate Execute Success in) (tblcustomers) ??????? {}", result1);

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
	                "createdate = ?, startdate = ?, lastmodifieddate = ?, " +
	                "enddate = COALESCE(?, enddate), expirydate = COALESCE(?, expirydate) " +
	                "WHERE custpackageid = ?";

	        try {
	            conn1.setAutoCommit(false);

	            try (PreparedStatement stmt1 = conn1.prepareStatement(updatePackageQuery)) {

	                String createDate = customerDetailsMap.get("startdate");
	                String endDate = customerDetailsMap.get("enddate");

	                setPackagePreparedStatementParameters(stmt1, createDate, endDate, cprId);

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
	String note = customerDetailsMap.get("Notes");
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
	                                                 String cuiId, String customerId) throws SQLException {
	      
	        preparedStatement.setString(1, cuiId);
	        preparedStatement.setString(2, customerId);
	    }

	    // custpackage updation 
	    private void setPackagePreparedStatementParameters(PreparedStatement preparedStatement,
	            String createDate, String enddate, String cprId) throws SQLException {
	preparedStatement.setString(1, createDate);
	preparedStatement.setString(2, createDate);
	preparedStatement.setString(3, createDate);

	// If enddate is not null or empty, set it in the prepared statement
	if (enddate != null && !enddate.isEmpty()) {
	preparedStatement.setString(4, enddate);
	preparedStatement.setString(5, enddate);
	} else {

	preparedStatement.setNull(4, java.sql.Types.NULL); 
	preparedStatement.setNull(5, java.sql.Types.NULL); 
	}

	preparedStatement.setString(6, cprId);
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
	    
	
}
