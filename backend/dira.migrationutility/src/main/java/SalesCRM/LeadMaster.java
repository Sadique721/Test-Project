package SalesCRM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class LeadMaster extends RestExecution {
	
	private static String logFileName = "SalesCRM.log";
	private static String logModuleName = "CreateLead";

	private void createLead(Map<String, String> leadSourceMaster) {

		String apiURL = getAPIURL("SavbillSalesCrmsBss/leadMaster/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

	//	String APIBody = getLeadSourceMasterJson(leadSourceMaster);
	//	Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

	//	JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
//		String response = JSONResponseBody.toString(4);
	//	Utility.printLog(logFileName, logModuleName, "Response", response);

		String leadMasterName = leadSourceMaster.get("LeadSourceMasterName");
	//	ProductUtility.printResponse(JSONResponseBody, logModuleName, leadMasterName);
		
	}
		
	public void createLead(List<Map<String, String>> leadMapList) {
		
		for (int i = 0; i < leadMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = leadMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createLead(map);
		}
	}
	
	public List<Map<String, String>> readLeadList() {
		
		String sheetName = "Lead";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getSalesCRMDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> leadMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String leadSourceMasterName = cellValue.get("LeadSourceMasterName");
			if (!"".equals(leadSourceMasterName)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("LeadSourceMasterName", cellValue.get("LeadSourceMasterName"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("LeadSubSource", cellValue.get("LeadSubSource"));
				leadMapList.add(valuemap);
			}
		}
		return leadMapList;
	}


	private String getLeadJson(Map<String, String> leadSourceMaster) {

		String jsonString = null;

		try {

			JSONObject leadJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			String status = ProductUtility.getStatus(leadSourceMaster.get("Status"));
			
			leadJson.put("leadSourceName", leadSourceMaster.get("LeadSourceMasterName"));
			leadJson.put("status", status);
			
			List<JSONObject> overChargeList = new ArrayList<JSONObject>();
			String subLeadSourceName = leadSourceMaster.get("LeadSubSource");
			
			
				
				// *********** Service Area Details *****************
				int serviceAreaId = commonGetAPI.getServiceAreaIdList(leadSourceMaster.get("ServiceArea")).get(0);
				leadJson.put("serviceareaid", serviceAreaId);

				leadJson.put("branch", JSONObject.NULL);
				leadJson.put("partnerid", 1);

				
				
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("id", JSONObject.NULL);
					
					jsonObject.put("leadSourceId", JSONObject.NULL);					
					
				
			
			
					//leadJson.put("leadSubSourceDtoList", leadSubSourceDtoList);			
			jsonString = leadJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}


