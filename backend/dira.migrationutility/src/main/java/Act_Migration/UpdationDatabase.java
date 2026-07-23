package Act_Migration;

import DatabaseUpdationProducer.*;
import utility.ModuleControlConstant;

import utility.Utility;



public class UpdationDatabase {

    public void callProducer() {
        // Log the initial check to ensure DATABASEUPDATION is true
        Utility.printLog("Database.log", "Updation Database", "Checking DATABASEUPDATION flag: " + ModuleControlConstant.CUSTOMERDATABASE, "");

        if (ModuleControlConstant.CUSTOMERDATABASE) {
            try {
                // Log before starting the batch executions
                Utility.printLog("Database.log", "Updation Database", "Starting batch executions.", "");

            //    BatchExecutionCustPackage baseusages = new BatchExecutionCustPackage();
            //    CuiLimitBatch cui = new CuiLimitBatch();
                InsertIpAddress ip = new InsertIpAddress();
                InsertMacAddress mac = new InsertMacAddress();
                QuotaUpdatedWithLimit quota = new QuotaUpdatedWithLimit();

                // ip insertion
                ip.insertip();
                // insert mac
                mac.insertMac();
                // customer updation
              //  cui.executeCustomersUpdate();
                // custpackage updation
             //   baseusages.executeCustpackageUpdation();
                // quota updation
                quota.executeQuotaUpdation();

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
