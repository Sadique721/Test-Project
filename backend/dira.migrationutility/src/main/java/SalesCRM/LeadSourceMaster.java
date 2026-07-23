package SalesCRM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class LeadSourceMaster extends RestExecution {
	
	private static String logFileName = "SalesCRM.log";
	private static String logModuleName = "LeadSourceMaster";

	private void createLeadSourceMaster(Map<String, String> leadSourceMaster) {

		String apiURL = getAPIURL("SavbillSalesCrmsBss/leadSource/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getLeadSourceMasterJson(leadSourceMaster);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String leadSourceMasterName = leadSourceMaster.get("LeadSourceMasterName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, leadSourceMasterName);
		
	}
		
	public void createLeadSourceMaster(List<Map<String, String>> leadSourceMasterMapList) {
		
		for (int i = 0; i < leadSourceMasterMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = leadSourceMasterMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createLeadSourceMaster(map);
		}
	}
	
	public List<Map<String, String>> readLeadSourceMasterList() {
		
		String sheetName = "LeadSourceMaster";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getSalesCRMDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> leadSourceMasterMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String leadSourceMasterName = cellValue.get("LeadSourceMasterName");
			if (!"".equals(leadSourceMasterName)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("LeadSourceMasterName", cellValue.get("LeadSourceMasterName"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("LeadSubSource", cellValue.get("LeadSubSource"));
				leadSourceMasterMapList.add(valuemap);
			}
		}
		return leadSourceMasterMapList;
	}


	private String getLeadSourceMasterJson(Map<String, String> leadSourceMaster) {

		String jsonString = null;

		try {

			JSONObject leadSourceMasterJson = new JSONObject();
			String status = ProductUtility.getStatus(leadSourceMaster.get("Status"));
			
			leadSourceMasterJson.put("leadSourceName", leadSourceMaster.get("LeadSourceMasterName"));
			leadSourceMasterJson.put("status", status);
			
			List<JSONObject> leadSubSourceDtoList = new ArrayList<JSONObject>();
			String subLeadSourceName = leadSourceMaster.get("LeadSubSource");
			
			if(!"".equals(subLeadSourceName)) {
				
				String totalSubleadSource[] = subLeadSourceName.split(",");
				
				for(int i=0;i<totalSubleadSource.length;i++) {
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("id", JSONObject.NULL);
					jsonObject.put("name", totalSubleadSource[i]);
					jsonObject.put("leadSourceId", JSONObject.NULL);					
					leadSubSourceDtoList.add(jsonObject);
				}
			}
			
			leadSourceMasterJson.put("leadSubSourceDtoList", leadSubSourceDtoList);			
			jsonString = leadSourceMasterJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}


