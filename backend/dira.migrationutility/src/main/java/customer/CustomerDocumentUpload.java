package customer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Constant;
import utility.Utility;

public class CustomerDocumentUpload extends RestExecution {

	private static String logFileName = "prepaidcustomer.log";
	private static String logModuleName = "UploadDocument";
	private static String basePath =  Constant.BASE_PATH + "\\TestData\\input\\uploads\\customerdoc\\";

	private void uploadCustomerDocumentDetails(Map<String, String> customerDetailsMap) {

		String apiURL = getAPIURL("cpm/custDoc/uploadDoc");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getUploadDocumentJson(customerDetailsMap);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		if (!APIBody.equals(null)) {

			String fileName = customerDetailsMap.get("FileNameToAttach");
			if ((fileName != null) && (!"".equals(fileName))) {
				//String accountNumber = customerDetailsMap.get("AccountNumber");
				//fileName = basePath + accountNumber + "\\" + fileName;
				fileName = basePath + "\\" + fileName;
			}

			JSONObject JSONResponseBody = httpPostFormData1(apiURL, APIBody, fileName);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");
			String userName = customerDetailsMap.get("CustomerUsername");
			//float amount = Float.valueOf(customerDetailsMap.get("Amount"));

			if (status == 200) {
				String message = "Document is uploaded successfully for - " + userName;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);
				
				int docId = JSONResponseBody.getJSONArray("dataList").getJSONObject(0).getInt("docId");
				approveCustomerUploadDocument(docId);
				
			} else if (status == 406) {
				String error = JSONResponseBody.getString("responseMessage") + " - " + userName;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Already Exist", error);
			} else {
				String error = "Error: " + JSONResponseBody.get("ERROR") + " - " + userName;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "ERROR", error);
			}
		}
	}

	public void uploadCustomerDocumentDetails(List<Map<String, String>> customerMapList) {

		CommonGetAPI commonGetAPI = new CommonGetAPI();
		
		for (int i = 0; i < customerMapList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map = customerMapList.get(i);

			String userName = map.get("CustomerUsername");
			if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
				Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
				uploadCustomerDocumentDetails(map);
			} else {
				System.out.println("Customer UserName is not Exists! - " + userName);
			}
		}
	}
	
	public void uploadCustomerDocumentDetailsOLD(List<Map<String, String>> customerMapList) {

		CommonGetAPI commonGetAPI = new CommonGetAPI();
		
		for (int i = 0; i < customerMapList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map = customerMapList.get(i);

			String userName = map.get("CustomerUsername");
			if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
				Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
				
				String accountNumber = map.get("AccountNumber");
				List<String> filelist = listFiles(accountNumber);
				if(filelist.size() > 0) {
					for(int j=0;j<filelist.size();j++) {
						String fileName = filelist.get(j);
						map.put("FileNameToAttach", fileName);
						uploadCustomerDocumentDetails(map);
					}
				} else {
					System.out.println("Document directory or files in directory may not exist - " + userName);
				}
				
			} else {
				System.out.println("Customer UserName is not Exists! - " + userName);
			}
		}
	}

	public List<Map<String, String>> readUploadDocumentList() {

		String sheetName = "UploadDocument";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("CustomerUsername");
			String mStatus = cellValue.get("MigrationStatus");

			if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
				valuemap.put("CustomerUsername", cellValue.get("CustomerUsername"));
				valuemap.put("FileNameToAttach", cellValue.get("FileNameToAttach"));
				valuemap.put("AccountNumber", cellValue.get("AccountNumber"));
				valuemap.put("remark", cellValue.get("Remark"));
				
				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}

	private String getUploadDocumentJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			List<JSONObject> docDetailsList = new ArrayList<JSONObject>(); 
			JSONObject documentJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			String customerType = customerDetails.get("SubscriberType");
			String userName = customerDetails.get("CustomerUsername");
			String remark = customerDetails.get("Remark");
			String fileName = customerDetails.get("FileNameToAttach");
			
			
			if ((userName != null) && (!"".equals(userName))) {
				int customerId = commonGetAPI.getCustomerId(userName,customerType);
				if (customerId != 0) {
					
					String cid = String.valueOf(customerId);
					documentJson.put("custId", cid);
					documentJson.put("docType", "Migration");
					documentJson.put("docSubType", "Migration");
					documentJson.put("docStatus", "verified");
					
					documentJson.put("remark", remark);
					documentJson.put("startDate", "");
					documentJson.put("mode", "Offline");
					documentJson.put("endDate", "");
					documentJson.put("filename", fileName);
				}
			}
			
			docDetailsList.add(documentJson);
			jsonString = docDetailsList.toString();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	private void approveCustomerUploadDocument(int docId) {
		
		try {
			String remarks =  "Approved by migration";
			String apiFind = "?docId=" + docId + "&remarks=" + remarks + "&isApproveRequest=true";
			
			//?docId=6&remarks=approved&isApproveRequest=true
			String apiURL = "cpm/custDoc/approveUploadCustomerDoc" + apiFind;
			apiURL = getAPIURL(apiURL);
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			JSONObject documentJson = new JSONObject();
			
			String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));
			documentJson.put("nextStaffId", "");
			documentJson.put("flag", "approved");
			documentJson.put("remark", "");
			documentJson.put("staffId", staffId);
			String apiBody = documentJson.toString();
			
			JSONObject JSONResponseBody =  httpPut(apiURL,apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");

			if (status == 200 || status == 0) {
				String message = "Uploaded document is approved successfully.";
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);
			} else {
				String error = "Error: " + JSONResponseBody.get("responseMessage");
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "ERROR", error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> listFiles(String accountNumber) {
	   
		List<String> fileNames = new ArrayList<String>();
		try {
			
			String newPath = basePath + "\\"+ accountNumber;
			File directoryPath1 = new File(newPath);
	       
			if(directoryPath1.exists()) {
				File filesList[] = directoryPath1.listFiles();
	 	       	//System.out.println("Number of files : "+filesList.length);
	 	       	for(File file : filesList) {
	 	       		String fileName = file.getName();	 	    	   
	 	       		fileNames.add(fileName);
	 	       		//System.out.println("File name: "+fileName);
	 	       	}
			} else {
				System.out.println("Specified directory does not exist");
			}
	       
	      
		} catch(NullPointerException ne) {
			System.out.println("catch Specified directory does not exist");
		}
		   return fileNames;
	}	
	
}
