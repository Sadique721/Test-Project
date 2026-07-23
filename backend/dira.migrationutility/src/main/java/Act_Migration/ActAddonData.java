package Act_Migration;

import java.util.List;
import java.util.Map;

import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class ActAddonData {
	  private void RenewPlan() {
	        if (ModuleControlConstant.ADDON) {
	            ReadWriteExcelFile rk = new ReadWriteExcelFile(); // Make sure this is always accessible in finally block
	            try {
	                RenewCustomerPlanService add = new RenewCustomerPlanService();
	                List<Map<String, String>> customerMapList = add.readRenewPlanCustomerList();
	                add.renewCustomerPlans(customerMapList);

	                Utility.printLog("execution.log", "Act Addon Data", "Customer plans renewed successfully.", "");

	            } catch (Exception e) {
	                Utility.printLog("execution.log", "Act Addon Data", "Error during renewing customer plans", e.getMessage());
	                e.printStackTrace();
	            } finally {
	                // Ensure data is updated in the Excel sheet no matter what
	                try {
	                    rk.setMultipleColumnInActiveSheetACTAddon(); // Update the Excel sheet
	                    Utility.printLog("execution.log", "Act Addon Data", "Excel sheet updated successfully.", "");
	                } catch (Exception e) {
	                    Utility.printLog("execution.log", "Act Addon Data", "Error updating the Excel sheet", e.getMessage());
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	   
	//Add Addon method to create data
	public void generateAddonData() throws Exception {

		System.out.println("Started Generting Act Addon Customer Data...!");
		Utility.printLog("execution.log", "Act Addon Customer", "Started Generting Act Addon Customer Data...!", "");

		String fileName = Constant.ACTCUSTOMER_ADDON_DATA_FILE;
		ReadWriteExcelFile rwe = new ReadWriteExcelFile();
		rwe.isExcelFileOpen(fileName);
		
		RenewPlan();

		System.out.println("Ended Generting Act Customer Data...!");
		Utility.printLog("execution.log", "Addon Customer", "Ended Generting Act Addon Customer Data...!", "");

	}

}
