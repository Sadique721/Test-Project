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

public class RejectedReasonMaster extends RestExecution {
	
	private static String logFileName = "SalesCRM.log";
	private static String logModuleName = "RejectedReasonMaster";

	private void createRejectedReason(Map<String, String> rejectedReasonMaster) {

		String apiURL = getAPIURL("SavbillSalesCrmsBss/rejectReason/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getRejectedReasonJson(rejectedReasonMaster);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String rejectedReasonMasterName = rejectedReasonMaster.get("RejectedReasonMasterName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, rejectedReasonMasterName);
		
	}

	
	public void createRejectedReason(List<Map<String, String>> rejectedReasonMasterMapList) {
		
		for (int i = 0; i < rejectedReasonMasterMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = rejectedReasonMasterMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createRejectedReason(map);
		}
	}
	
	public List<Map<String, String>> readRejectedReasonMasterList() {
		
		String sheetName = "RejectedReasonMaster";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getSalesCRMDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> rejectedReasonMasterMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String rejectedReasonMasterName = cellValue.get("RejectedReasonMasterName");
			if (!"".equals(rejectedReasonMasterName)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("RejectedReasonMasterName", cellValue.get("RejectedReasonMasterName"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("LeadRejectedSubReason", cellValue.get("LeadRejectedSubReason"));
				rejectedReasonMasterMapList.add(valuemap);
			}
		}
		return rejectedReasonMasterMapList;
	}

	private String getRejectedReasonJson(Map<String, String> rejectedReason) {

		String jsonString = null;

		try {

			JSONObject rejectedReasonJson = new JSONObject();
			String status = ProductUtility.getStatus(rejectedReason.get("Status"));
			
			rejectedReasonJson.put("name", rejectedReason.get("RejectedReasonMasterName"));
			rejectedReasonJson.put("status", status);
			
			List<JSONObject> rejectSubReasonDtoList = new ArrayList<JSONObject>();
			String rejectedSubReasonName = rejectedReason.get("LeadRejectedSubReason");
			
			if(!"".equals(rejectedSubReasonName)) {
				
				String totalSubleadSource[] = rejectedSubReasonName.split(",");
				
				for(int i=0;i<totalSubleadSource.length;i++) {
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("id", JSONObject.NULL);
					jsonObject.put("name", totalSubleadSource[i]);
					jsonObject.put("rejectReasonId", JSONObject.NULL);					
					rejectSubReasonDtoList.add(jsonObject);
				}
			}
			
			rejectedReasonJson.put("rejectSubReasonDtoList", rejectSubReasonDtoList);		
			jsonString = rejectedReasonJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}


}


