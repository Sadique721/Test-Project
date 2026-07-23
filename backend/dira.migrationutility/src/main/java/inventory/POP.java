package inventory;

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

public class POP extends RestExecution {

	private String logFileName = "inventory.log";
	private String logModuleName = "Pop";

	private void createPop(Map<String, String> popDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/popmanagement/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getPopJson(popDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String popName = popDetails.get("Name");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, popName);
		}
	}

	public void createPop(List<Map<String, String>> popMapList) {

		for (int i = 0; i < popMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = popMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPop(map);
		}
	}

	public List<Map<String, String>> readUniquePopList() {

		String sheetName = "POP";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> popMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = cellValue.get("Name *");
			if ((!"".equals(name)) && (name != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("Name", cellValue.get("Name *"));
				valuemap.put("PopCode", cellValue.get("PopCode"));
				valuemap.put("Latitude", cellValue.get("Latitude *"));
				valuemap.put("Longitude", cellValue.get("Longitude *"));
				valuemap.put("ServiceArea", cellValue.get("ServiceArea *"));
				valuemap.put("Status", cellValue.get("Status *"));
				popMapList.add(valuemap);
			}
		}
		return popMapList;
	}

	private String getPopJson(Map<String, String> popDetails) {

		String jsonString = null;

		try {

			JSONObject popJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(popDetails.get("Status"));
			
			popJsonObject.put("name", popDetails.get("Name"));
			popJsonObject.put("popCode", popDetails.get("PopCode"));
			popJsonObject.put("latitude", popDetails.get("Latitude"));
			popJsonObject.put("longitude", popDetails.get("Longitude"));
			
			CommonGetAPI commonGetAPI = new CommonGetAPI();			
			popJsonObject.put("serviceAreaIdsList", commonGetAPI.getServiceAreaIdList(popDetails.get("ServiceArea")));
			popJsonObject.put("status", status);
			
			popJsonObject.put("id", JSONObject.NULL);
			popJsonObject.put("isDeleted", false);
			popJsonObject.put("createdById", "");
			popJsonObject.put("lastModifiedById", "");
			popJsonObject.put("mvnoId", 2);
			
			jsonString = popJsonObject.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}
