package customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Utility;

public class CreditNote extends RestExecution {

	private static String logFileName = "PrepaidCustomer.log";
	private static String logModuleName = "CreditNote";

	private void createCreditNote(Map<String, String> customerDetailsMap) {

		String apiURL = getAPIURL("Revenue/record/payment");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getCreditNoteJson(customerDetailsMap);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		if (!APIBody.equals(null)) {
						
			JSONObject JSONResponseBody = httpPostFormData(apiURL, APIBody, "");
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("status");
			String userName = customerDetailsMap.get("CustomerUsername");
			float amount = Float.valueOf(customerDetailsMap.get("Amount"));

			if (status == 200) {
				String message = "New Credit Note of " + amount + " is recorded successfully for - " + userName;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);
				
				approveCreditNote(JSONResponseBody);
				
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

	public void createCreditNote(List<Map<String, String>> customerMapList) {

		CommonGetAPI commonGetAPI = new CommonGetAPI();
		
		for (int i = 0; i < customerMapList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map = customerMapList.get(i);

			String userName = map.get("CustomerUsername");
			if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
				Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
				createCreditNote(map);
			} else {
				System.out.println("Customer UserName is not Exists! - " + userName);
			}
		}
	}

	public List<Map<String, String>> readCreditNoteList() {

		String sheetName = "CreditNote";
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
				valuemap.put("DocumentNumber", cellValue.get("DocumentNumber"));
				valuemap.put("Amount", cellValue.get("Amount"));
				valuemap.put("ReferenceNumber", cellValue.get("ReferenceNumber"));
				valuemap.put("Remark", cellValue.get("Remark"));

				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}

	@SuppressWarnings("unchecked")
	private String getCreditNoteJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			JSONObject paymentJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			String customerType = customerDetails.get("SubscriberType");
			String userName = customerDetails.get("CustomerUsername");
			
			if (!"".equals(userName)) {
				int customerId = commonGetAPI.getCustomerId(userName,customerType);
				if (customerId != 0) {
					paymentJson.put("customerid", customerId);
					paymentJson.put("paymode", "Credit Note");
					
					String documentNumber =  customerDetails.get("DocumentNumber");
					int invoiceId = getCustomerInvoiceId(customerId,documentNumber);
					List<Integer> invoiceList = new ArrayList<Integer>();
					invoiceList.add(invoiceId);
					paymentJson.put("invoiceId", invoiceList);
				}
			}
			
			float amount = Float.valueOf(customerDetails.get("Amount"));			
			paymentJson.put("amount", amount);
			paymentJson.put("referenceno", customerDetails.get("ReferenceNumber"));
			paymentJson.put("remark", customerDetails.get("Remark"));
			paymentJson.put("type", "creditnote");
			paymentJson.put("paytype", "creditnote");
			
			jsonString = paymentJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	public int getCustomerInvoiceId(int customerId,String docNumber) {

		String apiURL = "Revenue/invoiceList/byCustomer/" + customerId;
		apiURL = getAPIURL(apiURL);

		JSONObject jsonResponse = httpGet(apiURL);
		// String ans = jsonResponse.toString(4);

		// Fetching the desired value of a parameter
		int status = jsonResponse.getInt("status");
		int invoiceId = 0;

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("invoiceList");
			
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedDocumnetNumber = jsonArray.getJSONObject(i).getString("docnumber");
				if (receivedDocumnetNumber.equalsIgnoreCase(docNumber)) {
					invoiceId = jsonArray.getJSONObject(i).getInt("id");
					break;
				}
			}
		}

		if (invoiceId == 0) {
			System.out.println("Customer Invoice details not found - " + customerId);
			Utility.printLog(logFileName, logModuleName, "Customer Invoice details not found - ",
					String.valueOf(customerId));
		}

		return invoiceId;
	}

	private void approveCreditNote(JSONObject creditNoteResponse) {
		

		try {

			JSONObject approveCreditNote = new JSONObject();
			creditNoteResponse = creditNoteResponse.getJSONObject("recordpayment");
			float amount =  creditNoteResponse.getFloat("amount");
			
			int customerid =  creditNoteResponse.getInt("customerid");
			int idlist =  creditNoteResponse.getInt("creditDocId");
			String paymode =  creditNoteResponse.getString("paymode");
			String paystatus =  "pending";
			String paytodate =  creditNoteResponse.getString("paymentdate");
			String referenceno =  creditNoteResponse.getString("referenceno");
			String remarks =  "Migration created credit note is approved by migration";
			
			
			approveCreditNote.put("customerid", customerid);
			approveCreditNote.put("idlist", idlist);
			approveCreditNote.put("paymode", paymode);
			approveCreditNote.put("paystatus", paystatus);
			approveCreditNote.put("paytodate", paytodate);
			approveCreditNote.put("referenceno", referenceno);
			approveCreditNote.put("remarks", remarks);
			
			
			String apiURL = getAPIURL("cpm/payment/approve");
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
			
			String apiBody = approveCreditNote.toString();
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);
			
			JSONObject JSONResponseBody =  httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);
			
			int status = JSONResponseBody.getInt("status");

			if (status == 200 || status == 0) {
				String message = "New Credit note of "+amount+" is approved successfully. ";
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else {
				String error = "Error: " + JSONResponseBody.get("ERROR");
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "ERROR", error);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
}
