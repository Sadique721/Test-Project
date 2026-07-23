package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import utility.ProductUtility;
import utility.Utility;

public class PlanCharge extends RestExecution {

	private String logFileName = "prepaidplan.log";
	private String logModuleName = "Charge";

	private void createPlanCharge(Map<String, String> charge) {

		String apiURL = getAPIURL("cpm/charge");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPlanChargeJson(charge);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String chargeName = charge.get("Name");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, chargeName);
		
	}

	public void createPlanCharge(List<Map<String, String>> chargeMapList) {

		for (int i = 0; i < chargeMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = chargeMapList.get(i);
			createPlanCharge(map);
		}
	}

	public List<Map<String, String>> readUniquePlanChargeList() {

		String sheetName = "Charge";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> chargeMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);
			
			String chargeName = cellValue.get("Name");
			if ((!"".equals(chargeName)) && (chargeName != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("Name", cellValue.get("Name"));
				valuemap.put("Category", cellValue.get("Category"));
				valuemap.put("Type", cellValue.get("Type"));
				valuemap.put("Service", cellValue.get("Service"));
				valuemap.put("Currency", cellValue.get("Currency"));  // new added currency support
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("LedgerID", cellValue.get("LedgerID"));
				valuemap.put("Description", cellValue.get("Description"));
				valuemap.put("GroupLedgerID", cellValue.get("GroupLedgerID"));
				valuemap.put("RoyaltyPayable", cellValue.get("RoyaltyPayable"));			
				valuemap.put("ActualPrice", cellValue.get("ActualPrice"));
				valuemap.put("SACCode", cellValue.get("SACCode"));
				valuemap.put("Tax", cellValue.get("Tax"));
				chargeMapList.add(valuemap);
			}
		}
		return chargeMapList;
	}

	private String getPlanChargeJson(Map<String, String> charge) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			CommonList commonList = new CommonList();
			
			JSONObject chargeJson = new JSONObject();

			chargeJson.put("name", charge.get("Name"));
			String commonChargeCategory = commonList.getCommonChargeCategory(charge.get("Category"));
			chargeJson.put("chargecategory", commonChargeCategory);

			String commonChargeType = commonList.getCommonChargeType(charge.get("Type"));
			chargeJson.put("chargetype", commonChargeType);
			
			List<Integer> serviceIdList = commonGetAPI.getServiceIdList(charge.get("Service"));
			chargeJson.put("serviceid", serviceIdList);
			chargeJson.put("status", charge.get("Status"));
			
			String ledgerId = charge.get("LedgerID");
			chargeJson.put("ledgerId", JSONObject.NULL);
			if (!"".equals(ledgerId)) { chargeJson.put("ledgerId", ledgerId); }

			String groupLedgerID = charge.get("GroupLedgerID");
			chargeJson.put("pushableLedgerId", JSONObject.NULL);
			if (!"".equals(groupLedgerID)) { chargeJson.put("pushableLedgerId", groupLedgerID); }
						
			chargeJson.put("royalty_payable", false);
			if(commonChargeType.equalsIgnoreCase("recurring prepaid") || commonChargeType.equalsIgnoreCase("recurring postpaid")) {
				chargeJson.put("royalty_payable", Boolean.valueOf(charge.get("RoyaltyPayable")));
			}
			
			chargeJson.put("desc", charge.get("Description"));
			chargeJson.put("currency", charge.get("Currency"));
			
//			float actualPrice = Float.valueOf(charge.get("ActualPrice"));

            double actualPrice = Double.valueOf(charge.get("ActualPrice"));


            chargeJson.put("actualprice", actualPrice);
			chargeJson.put("price", actualPrice);
			
			String saccode = charge.get("SACCode");
			chargeJson.put("saccode", JSONObject.NULL);
			if (!"".equals(saccode)) { chargeJson.put("saccode", saccode); }
			
			int taxId = commonGetAPI.getTaxId(charge.get("Tax"));
			chargeJson.put("taxid", taxId);
			
			chargeJson.put("serviceNameList", JSONObject.NULL);
			
			jsonString = chargeJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}
