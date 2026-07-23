package SalesCRM;

import java.util.List;
import java.util.Map;

import commons.CommonAPI;
import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateSalesCRMData {

	
	private void createLeadSourceMaster() {
		if(ModuleControlConstant.LEADSOURCEMASTER) {
			LeadSourceMaster leadSourceMaster = new LeadSourceMaster();
			List<Map<String, String>> leadSourceMasterMapList = leadSourceMaster.readLeadSourceMasterList();
			leadSourceMaster.createLeadSourceMaster(leadSourceMasterMapList);
		}
	}

	private void createRejectedReasonMaster() {
		if(ModuleControlConstant.REJECTEDREASONMASTER) {
			RejectedReasonMaster rejectedReasonMaster = new RejectedReasonMaster();
			List<Map<String, String>> rejectedReasonMasterMapList = rejectedReasonMaster.readRejectedReasonMasterList();
			rejectedReasonMaster.createRejectedReason(rejectedReasonMasterMapList);
		}
	}
	
	private void createLeadCustomer() {
		try {
			if (ModuleControlConstant.LEADCREATE) {

				ReadWriteExcelFile rw = new ReadWriteExcelFile();

				LeadCreation lead = new LeadCreation();
				CommonAPI common = new CommonAPI();
				List<Map<String, String>> customerData = lead.readUniquePrepaidCustomerList();

				Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();

				lead.createPrepaidCustomer(customerData, serviceAreaIdAll);
//				rw.setMultipleColumnInActiveSheetTumilLead();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
		} finally {

//			ReadWriteExcelFile rw = new ReadWriteExcelFile();// act migration
//			rw.setMultipleColumnInActiveSheetTumilLead(); // it is commenct by now after add addon
		}
	}
	
	public void generateSalesCRMData() throws Exception {
		System.out.println("Started Generting SalesCRM Data...!");
		Utility.printLog("execution.log", "SalesCRM", "Started Generting SalesCRM Data...!","");
		String fileName = Constant.SALES_CRM_DATA_FILE;
		ReadWriteExcelFile rwe = new ReadWriteExcelFile();
		rwe.isExcelFileOpen(fileName);
		createLeadSourceMaster();
		createRejectedReasonMaster();
		createLeadCustomer();
		
		System.out.println("Ended Generting SalesCRM Data...!");
		Utility.printLog("execution.log", "SalesCRM", "Ended Generting SalesCRM Data...!","");
	}

}
