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

public class SubBusinessVertical extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "SubBusinessVertical";

	private void createSubBusinessVertical(Map<String, String> subBusinessVertical) {

		String apiURL = getAPIURL("SavbillCommonGateway/subbusinessvertical/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getSubBusinessVerticalJson(subBusinessVertical);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String subBusinessVerticalName = subBusinessVertical.get("SubBusinessVerticalName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, subBusinessVerticalName);
		
	}

	public void createSubBusinessVertical(List<Map<String, String>> subBusinessVerticalMapList) {

		for (int i = 0; i < subBusinessVerticalMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = subBusinessVerticalMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createSubBusinessVertical(map);
		}
	}

	public List<Map<String, String>> readSubBusinessVerticalList() {
		
		String sheetName = "SubBusinessVertical";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> subBusinessVerticalMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String subBV = cellValue.get("SubBusinessVerticalName");
			if (!"".equals(subBV)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("SubBusinessVerticalName", cellValue.get("SubBusinessVerticalName"));
				valuemap.put("BusinessVertical", cellValue.get("BusinessVertical"));
				valuemap.put("Status", cellValue.get("Status"));
				subBusinessVerticalMapList.add(valuemap);
			}
		}
		return subBusinessVerticalMapList;
	}

	private String getSubBusinessVerticalJson(Map<String, String> subBusinessVertical) {

		String jsonString = null;

		try {

			JSONObject subBusinessVericalJson = new JSONObject();
			String status = ProductUtility.getStatus(subBusinessVertical.get("Status"));
			
			subBusinessVericalJson.put("sbvname", subBusinessVertical.get("SubBusinessVerticalName"));
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			int businessVerticalId = commonGetAPI.getBusinessVerticalId(subBusinessVertical.get("BusinessVertical"));
			subBusinessVericalJson.put("buVerticalsId", businessVerticalId);
			subBusinessVericalJson.put("status", status);

			jsonString = subBusinessVericalJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}
