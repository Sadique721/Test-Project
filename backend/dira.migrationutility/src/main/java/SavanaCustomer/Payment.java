package SavanaCustomer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class Payment extends RestExecution{
	private static String logFileName = "PaymentLink.log";
	private static String logModuleName = "PaymentLink";
// https://common.savannafibre.com:30080/api/v1/cpm/generatePaymentLink/958
	// http://192.168.24.18:30080/api/v1/cpm/generatePaymentLinkForRenew/17503


	private void renewCustomerPlan(Map<String, String> changePlanMap) {
        String custid=changePlanMap.get("custid");
		String apiURL = getAPIURL("cpm/generatePaymentLinkForRenew/"+custid);
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		
		Utility.printLog(logFileName, logModuleName,custid,apiURL );

		if (!custid.equals(null)) {

			JSONObject JSONResponseBody = httpPostS(apiURL,null);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			// Fetching the desired value of a parameter
			int status = JSONResponseBody.getInt("responseCode");
			String userName = changePlanMap.get("username");
			
			String rowIndex = changePlanMap.get("RowIndex");
			

		
			if (status == 200) {
				
				JSONObject jsonResponse = new JSONObject(response);
				String data = jsonResponse.getString("data");

				UpdateSheet us = new UpdateSheet();
				String columnAndValue = "code:" + data + "#" + "MigrationStatus:Success";
				us.setRowList(rowIndex, columnAndValue);

				String message = "Customer payment code is successfully - " + userName ;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else if (status == 405) {
				String error = JSONResponseBody.getString("responseMessage");
				System.out.println(error + " - " + userName);
			}
		}
	}
	
	


	

	
	

	public void renewCustomerPlan(List<Map<String, String>> customerMapList) {
		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		UpdateSheet us = new UpdateSheet();
		us.setActiveSheetName("ExpiredCustomer");
		try {
			//CommonGetAPI commonGetAPI = new CommonGetAPI();

			for (int i = 0; i < customerMapList.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				map = customerMapList.get(i);

				//String userName = map.get("username");
				//if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
					Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
					renewCustomerPlan(map);
					if (i % 10 == 1) {
						rw.setMultipleColumnInActiveSheetSavanaPayment();
					}
				} 
			
		} finally {
			rw.setMultipleColumnInActiveSheetSavanaPayment();
		}

	}

	public List<Map<String, String>> readRenewPlanCustomerList() {

		String sheetName = "ExpiredCustomer";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPaymentSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("username");
			String code = cellValue.get("code");

		

				valuemap.put("RowIndex", cellValue.get("No"));
			
				valuemap.put("username", cellValue.get("username"));
				valuemap.put("custid", cellValue.get("custid"));
				
				valuemap.put("code", cellValue.get("code"));
			

				customerMapList.add(valuemap);
			}
		
		return customerMapList;
	}

	

	
	

}
