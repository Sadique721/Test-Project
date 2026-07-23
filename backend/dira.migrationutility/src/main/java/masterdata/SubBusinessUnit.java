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

public class SubBusinessUnit extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "SubBusinessUnit";

	private void createSubBusinessUnit(Map<String, String> subBusinessUnit) {

		String apiURL = getAPIURL("SavbillCommonGateway/subbusinessunit/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getSubBusinessUnitJson(subBusinessUnit);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String subBUName = subBusinessUnit.get("SubBusinessUnitName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, subBUName);
		
	}

	public void createSubBusinessUnit(List<Map<String, String>> SubBUMapList) {

		for (int i = 0; i < SubBUMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = SubBUMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createSubBusinessUnit(map);
		}
	}

	public List<Map<String, String>> readSubBusinessUnitList() {
		
		String sheetName = "SubBusinessUnit";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> SubBUMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String subBusinessUnit = cellValue.get("SubBusinessUnitName");
			if ((!"".equals(subBusinessUnit)) && (subBusinessUnit != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("SubBusinessUnitName", cellValue.get("SubBusinessUnitName"));
				valuemap.put("SubBusinessUnitCode", cellValue.get("SubBusinessUnitCode"));
				valuemap.put("BusinessUnitName", cellValue.get("BusinessUnitName"));
				valuemap.put("Status", cellValue.get("Status"));
				SubBUMapList.add(valuemap);
			}
		}
		return SubBUMapList;
	}

	private String getSubBusinessUnitJson(Map<String, String> subBusinessUnit) {

		String jsonString = null;

		try {

			JSONObject businessVericalJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(subBusinessUnit.get("Status"));
			
			businessVericalJsonObject.put("subbuname", subBusinessUnit.get("SubBusinessUnitName"));
			businessVericalJsonObject.put("subbucode", subBusinessUnit.get("SubBusinessUnitCode"));
			
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			int businessUnitId = commonGetAPI.getBusinessUnitIdList(subBusinessUnit.get("BusinessUnitName")).get(0);
			businessVericalJsonObject.put("businessunitid", businessUnitId);
			businessVericalJsonObject.put("status", status);

			jsonString = businessVericalJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}
