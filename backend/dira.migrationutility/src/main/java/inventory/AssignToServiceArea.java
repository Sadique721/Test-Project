package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class AssignToServiceArea extends RestExecution {

	private String logFileName = "inventory.log";
	private String logModuleName = "Vendor";

	private void createVendor(Map<String, String> vendorDetails) {

		String apiURL = getAPIURL("vendor/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getVendorJson(vendorDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);
			
			String vendorName = vendorDetails.get("VendorName");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, vendorName);
		}
	}

	public void createVendor(List<Map<String, String>> vendorMapList) {

		for (int i = 0; i < vendorMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = vendorMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createVendor(map);
		}
	}

	public List<Map<String, String>> readVendorList() {

		String sheetName = "Vendor";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> vendorMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String vendorName = cellValue.get("VendorName *");
			if ((!"".equals(vendorName)) && (vendorName != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("VendorName", cellValue.get("VendorName *"));
				valuemap.put("Status", cellValue.get("Status *"));
				vendorMapList.add(valuemap);
			}
		}
		return vendorMapList;
	}

	private String getVendorJson(Map<String, String> vendorDetails) {

		String jsonString = null;

		try {

			JSONObject vendorJson = new JSONObject();
			String status = ProductUtility.getStatus(vendorDetails.get("Status"));
			
			vendorJson.put("name", vendorDetails.get("VendorName"));
			vendorJson.put("status", status);
			
			vendorJson.put("id", "");
			vendorJson.put("mvnoId", "");
			vendorJson.put("isDeleted", false);
			vendorJson.put("delete", false);
			
			jsonString = vendorJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}
