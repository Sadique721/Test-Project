package masterdata;

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

public class Region extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Region";

	private void createRegion(Map<String, String> region) {

		String apiURL = getAPIURL("SavbillCommonGateway/region/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getRegionJson(region);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String regionName = region.get("RegionName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, regionName);
		
	}

	public void createRegion(List<Map<String, String>> regionMapList) {
		
		for (int i = 0; i < regionMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = regionMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createRegion(map);
		}
	}

	public List<Map<String, String>> readRegionList() {
		
		String sheetName = "Region";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> regionMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String regionName = cellValue.get("RegionName");
			if ((!"".equals(regionName)) && (regionName != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("RegionName", cellValue.get("RegionName"));
				valuemap.put("Branch", cellValue.get("Branch"));
				valuemap.put("Status", cellValue.get("Status"));
				regionMapList.add(valuemap);
			}
		}
		return regionMapList;
	}

	private String getRegionJson(Map<String, String> region) {

		String jsonString = null;

		try {

			JSONObject regionJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(region.get("Status"));
			
			regionJsonObject.put("rname", region.get("RegionName"));
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			regionJsonObject.put("branchid", commonGetAPI.getBranchIdList(region.get("Branch")));
			regionJsonObject.put("status", status);
			regionJsonObject.put("id", JSONObject.NULL);

			jsonString = regionJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}
