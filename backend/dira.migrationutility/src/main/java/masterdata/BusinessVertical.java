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

public class BusinessVertical extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "BusinessVertical";

	private void createBusinessVertical(Map<String, String> businessVertical) {

		String apiURL = getAPIURL("SavbillCommonGateway/businessverticals/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getBusinessVerticalJson(businessVertical);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String businessVerticalName = businessVertical.get("BusinessVerticalName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, businessVerticalName);
		
	}

	public void createBusinessVertical(List<Map<String, String>> businessVerticalMapList) {

		for (int i = 0; i < businessVerticalMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = businessVerticalMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createBusinessVertical(map);
		}
	}

	public List<Map<String, String>> readBusinessVerticalList() {
		
		String sheetName = "BusinessVertical";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> businessVerticalMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String regionName = cellValue.get("BusinessVerticalName");
			if ((!"".equals(regionName)) && (regionName != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("BusinessVerticalName", cellValue.get("BusinessVerticalName"));
				valuemap.put("Region", cellValue.get("Region"));
				valuemap.put("Status", cellValue.get("Status"));
				businessVerticalMapList.add(valuemap);
			}
		}
		return businessVerticalMapList;
	}

	private String getBusinessVerticalJson(Map<String, String> businessVertical) {

		String jsonString = null;

		try {

			JSONObject businessVericalJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(businessVertical.get("Status"));
			
			String regionName = businessVertical.get("Region");
			businessVericalJsonObject.put("vname", businessVertical.get("BusinessVerticalName"));
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			businessVericalJsonObject.put("region_id", commonGetAPI.getRegionIdList(regionName));
			businessVericalJsonObject.put("status", status);
			//businessVericalJsonObject.put("id", JSONObject.NULL);

			jsonString = businessVericalJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}
