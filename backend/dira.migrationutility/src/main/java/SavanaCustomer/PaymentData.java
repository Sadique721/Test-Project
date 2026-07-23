package SavanaCustomer;

import java.util.List;
import java.util.Map;

import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class PaymentData {

	public void generatePrepaidCustomerData() throws Exception {
try {
			System.out.println("Started Generting Savanna Customer Data...!");
			Utility.printLog("execution.log", "SavannaCustomer", "Started Generting Expired SavannaCustomer Data...!", "");

			String fileName = Constant.PAYMENTFILE;
			ReadWriteExcelFile rwe = new ReadWriteExcelFile();
			rwe.isExcelFileOpen(fileName);

		
			payment();
			
			System.out.println("Ended Generting SavannaCustomer Data...!");
			Utility.printLog("execution.log", "SavannaCustomer", "Ended Generting Expired SavannaCustomer Data...!", "");
}
catch(Exception e){
    e.printStackTrace();
    System.out.println("getting error in this method (generateExpiredSavanaCustomerData).... " + e.getMessage());
}
	}
	// Payment 
	
	private void payment() {
		 try {
		if (ModuleControlConstant.SAVANNA_PAYMENT) {
			
				 ReadWriteExcelFile rw = new ReadWriteExcelFile();
			 
				
				 Payment prepaidCustomerNew=new Payment();
				// 
				 List<Map<String, String>> customerData = prepaidCustomerNew.readRenewPlanCustomerList();
		System.out.println(customerData);


			prepaidCustomerNew.renewCustomerPlan(customerData);
		
		    rw.setMultipleColumnInActiveSheetSavanaPayment();
		    
			 }
	        }
		catch (Exception e){
	            e.printStackTrace();
	            System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
	        }
		 finally {
				ReadWriteExcelFile rw = new ReadWriteExcelFile();//act migration 
				rw.setMultipleColumnInActiveSheetSavanaPayment();  //it is commenct by now after add addon
		}
		}
	
	
}
