package api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utility.Constant;
import utility.ReadWriteExcelFile;

public class ReadData {
	
	public List<Map<String, String>> getSalesCRMDataSheet(String sheetName) {
		String fileName = Constant.SALES_CRM_DATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	
	public List<Map<String, String>> getTicketDataSheet(String sheetName) {
		String fileName = Constant.TICKETDATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	public List<Map<String, String>> getCustomerDataSheet(String sheetName) {
		String fileName = Constant.CUSTOMER_DATA_FILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	public List<Map<String, String>> getInventoryDataSheet(String sheetName) {
		String fileName = Constant.INVENTORY_DATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	public List<Map<String, String>> getMaterDataSheet(String sheetName) {
		String fileName = Constant.MASTERDATA_FILE;

		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	public List<Map<String, String>> getLocationDataSheet(String sheetName) {
		String fileName = Constant.LOCATION_DATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	//read data from sheet
	public List<Map<String, String>> getNetConfDataSheet(String sheetName) {
		String fileName = Constant.NETCONF_DATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	
	public List<Map<String, String>> getPlanDataSheet(String sheetName) {
		String fileName = Constant.PLANDATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	public List<Map<String, String>> getPartnerDataSheet(String sheetName) {
		String fileName = Constant.PARTNERDATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	//Network sheet read 
	public List<Map<String, String>> getNetworkDataSheet(String sheetName) {
		String fileName = Constant.NETWORK_DATA_FILE;
		
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		// return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	//savana customer file
	public List<Map<String, String>> getSavanaCustomerDataSheet(String sheetName) {
		String fileName = Constant.SAVANACUSTOMER_FILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName); //here change add 3
		return sheetMap;
	}

	//savana customer file
	public List<Map<String, String>> getDailyPaymentDataSheet(String sheetName) {
		String fileName = Constant.DAILY_PAYMENTS_CHECK;

		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName); //here change add 3
		return sheetMap;
	}
	
	/*ACT plan And customer creation sheet will read from below method-->*/
	//ACT 
	public List<Map<String, String>> getActCustomerDataSheet(String sheetName) {
		String fileName = Constant.ACTCUSTOMER_DATA_FILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName); //here change add 3
		return sheetMap;
	}
	
	
	public List<Map<String, String>> getAddonDataSheet(String sheetName) {
		String fileName = Constant.ACTCUSTOMER_ADDON_DATA_FILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	
	public List<Map<String, String>> getActPlan(String sheetName) {
		String fileName = Constant.ACTPLAN_DATA_FILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	// savanna file payment 
	public List<Map<String, String>> getPaymentSheet(String sheetName) {
		String fileName = Constant.PAYMENTFILE;
	
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
		return sheetMap;
	}
	
	
	// Tumil customer file with sheet
		public List<Map<String, String>> getTumilCustomerDataSheet(String sheetName) {
			String fileName = Constant.TUMIL_FILE;
		
			List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
			sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName); //here change add 3
			return sheetMap;
		}

    //DEPARTMENT_MANAGEMENT
    public List<Map<String, String>> getDepartmentManagementDataSheet(String sheetName) {
        String fileName = Constant.DEPARTMENT_MANAGEMENT_DATA_FILE;

        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        // return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
        sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
        return sheetMap;
    }

    //OLT
    public List<Map<String, String>> getOLTDataSheet(String sheetName) {
        String fileName = Constant.OLT;

        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        // return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
        sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
        return sheetMap;
    }

    public List<Map<String, String>> getWorkflowDataSheet(String sheetName) {
        String fileName = Constant.WORKFLOWFILE;

        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        // return sheetMap = ReadWriteExcelFile.getSheet(sheetName,startRow,endRow);
        sheetMap = ReadWriteExcelFile.getSheetNew(fileName, sheetName);
        return sheetMap;
    }
		
	/*------->*/
	
}
