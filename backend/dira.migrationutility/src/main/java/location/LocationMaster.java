package location;

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

public class LocationMaster extends RestExecution {

	private static String logFileName = "Location.log";
	private static String logModuleName = "Location";

	public void createLocation(Map<String, String> locationDetails) {

		String apiURL = getAPIURL("cpm/LocationMaster/addLocationMaster"); //here solve issue of id
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getLocationJson(locationDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String location = locationDetails.get("LocationMasterName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, location);

	}

	public void createLocation(List<Map<String, String>> LocationMapList) {

		for (int i = 0; i < LocationMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = LocationMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createLocation(map);
		}
	}

	public List<Map<String, String>> readLocationList() {

		String sheetName = "Location";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getLocationDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> locationMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String location = cellValue.get("LocationMasterName");
			if ((!"".equals(location)) && (location != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));

				valuemap.put("LocationMasterName", cellValue.get("LocationMasterName"));
				valuemap.put("locationIdentifyValue", cellValue.get("locationIdentifyValue"));
				valuemap.put("locationIdentifyAttribute", cellValue.get("locationIdentifyAttribute"));
				valuemap.put("checkItem", cellValue.get("checkItem"));
				valuemap.put("Status", cellValue.get("Status"));
				locationMapList.add(valuemap);
			}
		}
		return locationMapList;
	}

	private String getLocationJson(Map<String, String> locationDetails) {

		String jsonString = null;

		try {
			//CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject locationPojoObject = new JSONObject();
		//	JSONObject countryPojoJsonObject = new JSONObject();

			String locationName = locationDetails.get("LocationMasterName");
			String locationIdentifyVakue = locationDetails.get("locationIdentifyValue");
			String locationIdentifyAttribute = locationDetails.get("locationIdentifyAttribute");
			String checkItem = locationDetails.get("checkItem");
			
			
		//	int countryId = commonGetAPI.getCountryId(countryName);

			String status = ProductUtility.getStatus(locationDetails.get("Status"));
			//String latitude= provinceDetails.get("Latitude");
			locationPojoObject.put("name", locationName);
			locationPojoObject.put("status", status);

			locationPojoObject.put("locationIdentifyValue", locationIdentifyVakue);
			locationPojoObject.put("locationIdentifyAttribute", locationIdentifyAttribute);
		    locationPojoObject.put("mvnoName", "");
			//statePojoJsonObject.put("latitude", latitude);
			locationPojoObject.put("checkItem", checkItem);
			
			List<String> locationMasterMappingJsonObject = new ArrayList<String>();
			
			locationPojoObject.put("locationMasterMapping", locationMasterMappingJsonObject);

			jsonString = locationPojoObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}