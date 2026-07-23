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
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class RenewPlan extends RestExecution {

	private static String logFileName = "PrepaidCustomer.log";
	private static String logModuleName = "Addon";

	private void renewCustomerPlan(Map<String, String> changePlanMap) {

		String apiURL = getAPIURL("cpm/subscriber/changePlan01");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getRenewPlanJson(changePlanMap);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			// Fetching the desired value of a parameter
			int status = JSONResponseBody.getInt("responseCode");
			String userName = changePlanMap.get("Username");
			String planName = changePlanMap.get("Addon");
			String rowIndex = changePlanMap.get("RowIndex");

			int planmapid = -1;
			if (status == 200) {
				JSONArray jsonArray = JSONResponseBody.getJSONObject("data").getJSONArray("planList");
				for (int i = 0; i < jsonArray.length(); i++) {
					planmapid = jsonArray.getJSONObject(i).getInt("planmapid");

				}

				UpdateSheet us = new UpdateSheet();
				String columnAndValue = "CPRID:" + planmapid + "#" + "MigrationStatus:Success";
				us.setRowList(rowIndex, columnAndValue);

				String message = "Customer Plan is renewed successfully - " + userName + "|" + planName;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else if (status == 406) {
				String error = JSONResponseBody.getString("responseMessage");
				System.out.println(error + " - " + userName);
			}
		}
	}

	public void renewCustomerPlan(List<Map<String, String>> customerMapList) {
		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		UpdateSheet us = new UpdateSheet();
		us.setActiveSheetName("Addon");
		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			for (int i = 0; i < customerMapList.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				map = customerMapList.get(i);

				String userName = map.get("Username");
				if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
					Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
					renewCustomerPlan(map);
					if (i % 10 == 1) {
						rw.setMultipleColumnInActiveSheet();
					}
				} else {
					System.out.println("Customer UserName is NOT found - " + userName);
				}
			}
		} finally {
			rw.setMultipleColumnInActiveSheet();
		}

	}

	public List<Map<String, String>> readRenewPlanCustomerList() {

		String sheetName = "Addon";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("Username");
			String mStatus = cellValue.get("MigrationStatus");

			if ((!"".equals(userName)) && (!"success".equalsIgnoreCase(mStatus))) {

				valuemap.put("RowIndex", cellValue.get("No"));
			//	valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
			 // 	valuemap.put("Operation", cellValue.get("Operation"));  
				valuemap.put("Username", cellValue.get("Username"));
				valuemap.put("ExistingPlanName", cellValue.get("ExistingPlanName"));
				valuemap.put("Addon", cellValue.get("Addon"));
				valuemap.put("Enddate", cellValue.get("Enddate"));
				//valuemap.put("Remark", cellValue.get("Remark"));

				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}

	private String getRenewPlanJson(Map<String, String> renewPlanDetails) {

		String jsonString = null;

		try {
			JSONObject custRenewPlanJson = new JSONObject();
			JSONObject renewPlanJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			String customerType = "Postpaid";  //renewPlanDetails.get("SubscriberType");
			String customerUsername = renewPlanDetails.get("Username");
			int customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
			if (customerId != 0) {
				renewPlanJson.put("custId", customerId);
			}

			String existingPlanName = renewPlanDetails.get("ExistingPlanName");
			String details = getPlanByCustService(customerId, existingPlanName);
			String temp[] = details.split(":");

			int serviceId = Integer.valueOf(temp[0]);
			int custServiceMapId = Integer.valueOf(temp[4]);
			// int planId = Integer.valueOf(temp[3]);

			String changePlanName = renewPlanDetails.get("Addon");
			// int planId = getPlanIdByPlanFilters("changeplan", customerId, serviceId,
			// custServiceMapId, changePlanName);
			int planId = commonGetAPI.getPlanId(changePlanName);

			String operationType = "Addon";

			if (operationType.equalsIgnoreCase("Renew")) {
				renewPlanJson.put("purchaseType", "Renew");
				renewPlanJson.put("addonStartDate", JSONObject.NULL);

				List<JSONObject> custChargeDetailsList = new ArrayList<JSONObject>();
				custRenewPlanJson.put("custChargeDetailsList", custChargeDetailsList);

			} 
			else if (operationType.equalsIgnoreCase("Addon")) {
                String endDate=Utility.formatDateChange(renewPlanDetails.get("Enddate"));
				String currentDate = Utility.getCurrentDateTimeByProvidedFormat("YYYY-MM-dd");
				String currentTime = Utility.getCurrentDateTimeByProvidedFormat("HH:mm:ss.SSS");
				
				
				String currentTimeStamp = currentDate + "T" + currentTime + "Z";

				renewPlanJson.put("purchaseType", "Addon");
				renewPlanJson.put("addonStartDate", currentTimeStamp);
				renewPlanJson.put("addonEndDate", endDate);  //static end date
				
			}

			renewPlanJson.put("isPaymentReceived", "false");
		//	renewPlanJson.put("remarks", renewPlanDetails.get("Remark"));
			renewPlanJson.put("remarks", "Act Top-Up");

			int staffId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
			renewPlanJson.put("paymentOwnerId", staffId);
			renewPlanJson.put("billableCustomerId", customerId);

			renewPlanJson.put("newPlanList", JSONObject.NULL);
			renewPlanJson.put("planMappingList", JSONObject.NULL);
			renewPlanJson.put("ChangePlanCategory", "");
			renewPlanJson.put("isAdvRenewal", false);

			JSONObject recordPaymentDTO = new JSONObject();
			renewPlanJson.put("recordPaymentDTO", recordPaymentDTO);

			List<String> planBindWithOldPlans = new ArrayList<String>();
			renewPlanJson.put("planBindWithOldPlans", planBindWithOldPlans);

			renewPlanJson.put("isRefund", false);
			renewPlanJson.put("isParent", true);
			renewPlanJson.put("custServiceMappingId", custServiceMapId);
			renewPlanJson.put("discount", 0);
			renewPlanJson.put("planId", planId);

			List<JSONObject> changePlanRequestDTOList = new ArrayList<JSONObject>();
			changePlanRequestDTOList.add(renewPlanJson);

			custRenewPlanJson.put("changePlanRequestDTOList", changePlanRequestDTOList);
			custRenewPlanJson.put("recordPayment", JSONObject.NULL);
			
			//Add addon
			custRenewPlanJson.put("isTriggerCoaDm", true);
			

			jsonString = custRenewPlanJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}
	
	
/*
	private int getPlanIdByPlanFilters(String changePlanType, int custId, int serviceId, int customerServiceMappingID,
			String planName) {

		String queryParam = "cpm/getPlansByFilters";
		String apiURL = getAPIURL(queryParam);
		// String apiBody = null;

		JSONObject changePlan = new JSONObject();

		changePlan.put("changePlanType", changePlanType);
		changePlan.put("customerServiceMappingID", 20906);
		changePlan.put("custId", 20904);
		//changePlan.put("serviceId", 3);-->cmt  by sarfraz 28 oct

		String apiBody = changePlan.toString();

		// apiBody = apiBody.substring(1, apiBody.length() - 1);

		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);
		int status = 200; // JSONResponseBody.getInt("responseCode");
		int planId = -1;

		if (status == 200) {
			JSONObject jsonObject = JSONResponseBody.getJSONObject("");
			for (int i = 0; i < jsonObject.length(); i++) {
				// String receivedPlanName = jsonObject.getJSONObject(i).getString("name");
				// if (receivedPlanName.equalsIgnoreCase(planName)) {
				// planId = jsonObject.getJSONObject(i).getInt("id");
				break;
			}
		}
		// }

		if (planId == -1) {
			System.out.println("Customer Plan details not found - " + planName);
			Utility.printLog(logFileName, logModuleName, "Customer Plan details not found - ", planName);
		}

		return planId;
	}
*/
	
	
	private String getPlanByCustService(int custId, String planName) {

		String queryParam = "cpm/subscriber/getPlanByCustService/" + custId
				+ "?isAllRequired=true&isNotChangePlan=true";
		String apiURL = getAPIURL(queryParam);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		String result = null;

		if (status == 0) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedPlanName = jsonArray.getJSONObject(i).getString("planName");
				if (receivedPlanName.equalsIgnoreCase(planName)) {
					int serviceId = jsonArray.getJSONObject(i).getInt("serviceId");
					String connectionNumber = jsonArray.getJSONObject(i).getString("connection_no");
					int custPlanMapppingId = jsonArray.getJSONObject(i).getInt("custPlanMapppingId");
					int planId = jsonArray.getJSONObject(i).getInt("planId");
					int customerServiceMappingId = jsonArray.getJSONObject(i).getInt("customerServiceMappingId");

					result = serviceId + ":" + connectionNumber + ":" + custPlanMapppingId + ":" + planId + ":"
							+ customerServiceMappingId;
					break;
				}
			}
		}

		if (result == null) {
			System.out.println("Customer Plan details not found - " + planName);
			Utility.printLog(logFileName, logModuleName, "Customer Plan details not found - ", planName);
		}

		return result;
	}

}
