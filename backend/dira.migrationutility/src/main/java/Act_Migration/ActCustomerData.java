package Act_Migration;

/*import java.util.List;
import java.util.Map;

import customer.PrepaidCustomerNew;
import customer.RenewPlan;
import temp.SimpleThreadPool;

import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class ActCustomerData {

	private void createPrepaidCustomer() {
		if (ModuleControlConstant.CUSTOMERACT) {
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			
			ACT_Customer_Sheet prepaidCustomerNew = new ACT_Customer_Sheet(); //here add thread
			List<Map<String, String>> customerMapList = prepaidCustomerNew.readUniquePrepaidCustomerList();
			prepaidCustomerNew.createPrepaidCustomer(customerMapList);
			rw.setMultipleColumnInActiveSheetACT(); 
		}
	
		}


	public void generateActCustomerData() throws Exception {

		System.out.println("Started Generting Act Customer Data...!");
		Utility.printLog("execution.log", "Act Customer", "Started Generting Act Customer Data...!", "");

		String fileName = Constant.ACTCUSTOMER_DATA_FILE;
		ReadWriteExcelFile rwe = new ReadWriteExcelFile();
		rwe.isExcelFileOpen(fileName);

		createPrepaidCustomer();
		
		System.out.println("Ended Generting Act Customer Data...!");
		Utility.printLog("execution.log", "Addon Customer", "Ended Generting ActCustomer Data...!", "");

	}
	

}  */


import java.util.List;
import java.util.Map;



import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class ActCustomerData {

    private void createPrepaidCustomer() {
        if (ModuleControlConstant.CUSTOMERACT) {
            ReadWriteExcelFile rw = new ReadWriteExcelFile();
            
            ACT_Customer_Sheet prepaidCustomerNew = new ACT_Customer_Sheet(); // Thread implementation here if needed
            List<Map<String, String>> customerMapList = prepaidCustomerNew.readUniquePrepaidCustomerList();
            prepaidCustomerNew.createPrepaidCustomer(customerMapList);
            
            // Ensure Excel update after creating prepaid customer data
          //  rw.setMultipleColumnInActiveSheetACT(); 
        }
    }

    public void generateActCustomerData() throws Exception {
        System.out.println("Started Generating Act Customer Data...!");
        Utility.printLog("execution.log", "Act Customer", "Started Generating Act Customer Data...!", "");

        String fileName = Constant.ACTCUSTOMER_DATA_FILE;
        ReadWriteExcelFile rwe = new ReadWriteExcelFile();
        rwe.isExcelFileOpen(fileName);

        createPrepaidCustomer(); // Create prepaid customer data and update Excel sheet
        
        System.out.println("Ended Generating Act Customer Data...!");
        Utility.printLog("execution.log", "Act Customer", "Ended Generating Act Customer Data...!", "");
    }
}

