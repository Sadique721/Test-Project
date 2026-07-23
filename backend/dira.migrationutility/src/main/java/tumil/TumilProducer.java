package tumil;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;
import DatabaseUpdationProducer.BatchExecutionCustPackage;
import DatabaseUpdationProducer.CuiLimitBatch;
import DatabaseUpdationProducer.InsertIpAddress;
import DatabaseUpdationProducer.InvoiceProducer;
import DatabaseUpdationProducer.QuotaUpdatedWithLimit;
public class TumilProducer {
	
	


	

	    public void callProducer() {
	        // Log the initial check to ensure DATABASEUPDATION is true
	        Utility.printLog("Database.log", "Updation Database", "Checking DATABASEUPDATION flag: " + ModuleControlConstant.CUSTOMERDATABASE, "");

	        if (ModuleControlConstant.CUSTOMERDATABASE) {
	            try {
	                // Log before starting the batch executions
	                Utility.printLog("Database.log", "Updation Database", "Starting batch executions.", "");

	                BatchExecutionCustPackage baseusages = new BatchExecutionCustPackage();
	                CuiLimitBatch customer = new CuiLimitBatch();
	            InsertIpAddress ip=new InsertIpAddress();
	                InvoiceProducer invoice =new InvoiceProducer(); 
                   QuotaUpdatedWithLimit quota=new QuotaUpdatedWithLimit();
	              
	                // customer updation
	                customer.executeCustomersUpdate();
	                
	                // custpackage updation
	                baseusages.executeCustpackageUpdation();
	            
	                
	                // updation invoice 
	             //   invoice.executeInvoiceUpdation();
	                quota.executeQuotaUpdation();
ip.insertip();
	                // Log successful execution
	                Utility.printLog("Database.log", "Updation Database", "Data updation successfully.", "");

	            } catch (Exception e) {
	                // Log error if any exception occurs
	                Utility.printLog("Database.log", "Updation Database", "Error during producer run", e.getMessage());
	                e.printStackTrace();
	            } finally {
	                // Log completion
	                try {
	                    Utility.printLog("Database.log", "Updation Database", "Execution complete", "");
	                } catch (Exception e) {
	                    Utility.printLog("Database.log", "Updation Database", "Error execution", e.getMessage());
	                    e.printStackTrace();
	                }
	            }
	        } else {
	            Utility.printLog("Database.log", "Updation Database", "DATABASEUPDATION is false. Skipping update.", "");
	        }
	    }

	


}
