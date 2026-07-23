package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;

public class Warehouse extends RestExecution {

	private static String logFileName = "inventory.log";
	private static String logModuleName = "Warehouse";

	private void createWarehouse(Map<String, String> warehouseDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/warehouseManagement/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getWarehouseJson(warehouseDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String warehouseName = warehouseDetails.get("Name");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, warehouseName);
		
	}

	public void createWarehouse(List<Map<String, String>> warehouseMapList) {

		for (int i = 0; i < warehouseMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = warehouseMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createWarehouse(map);
		}
	}

	public List<Map<String, String>> readUniqueWarehouseList() {

		String sheetName = "Warehouse";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> warehouseMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = safeTrim(cellValue.get("Name *"));
			if ((!"".equals(name)) && (name != null)) {

				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("Name", safeTrim(cellValue.get("Name *")));
				valuemap.put("Type", safeTrim(cellValue.get("Type *")));
				valuemap.put("ParentServiceArea", safeTrim(cellValue.get("ParentServiceArea")));

				valuemap.put("WareHouseCode", safeTrim(cellValue.get("WareHouseCode")));
				valuemap.put("Teams", safeTrim(cellValue.get("Teams *")));
				valuemap.put("Description", safeTrim(cellValue.get("Description *")));
				valuemap.put("Status", safeTrim(cellValue.get("Status *")));

				valuemap.put("Latitude", safeTrim(cellValue.get("Latitude")));
				valuemap.put("Longitude", safeTrim(cellValue.get("Longitude")));

				valuemap.put("ServiceArea", safeTrim(cellValue.get("ServiceArea *")));
				valuemap.put("Branch", safeTrim(cellValue.get("Branch")));
				valuemap.put("Address1", safeTrim(cellValue.get("Address1 *")));
				valuemap.put("Address2", safeTrim(cellValue.get("Address2 *")));
				valuemap.put("Municipality", safeTrim(cellValue.get("Municipality *")));
				valuemap.put("ProductName", safeTrim(cellValue.get("ProductName")));
				valuemap.put("ThresholdQty", safeTrim(cellValue.get("ThresholdQty")));
				valuemap.put("Unit", safeTrim(cellValue.get("Unit")));

				warehouseMapList.add(valuemap);

			}
		}
		return warehouseMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

	private String getWarehouseJson(Map<String, String> warehouseDetails) {

	       String jsonString = null;

	       try {

	          JSONObject warehouseJsonObject = new JSONObject();
	          CommonGetAPI commonGetAPI = new CommonGetAPI();
	          String status = ProductUtility.getStatus(warehouseDetails.get("Status")).toUpperCase();

	          warehouseJsonObject.put("name", warehouseDetails.get("Name"));
	          warehouseJsonObject.put("warehouseType", warehouseDetails.get("Type"));
	          
	          warehouseJsonObject.put("parentServiceAreaIdsList", JSONObject.NULL);
	          String parentServiceArea = warehouseDetails.get("ParentServiceArea");
	          if (!"".equals(parentServiceArea)) {
	             warehouseJsonObject.put("parentServiceAreaIdsList",
	                   commonGetAPI.getServiceAreaIdList(parentServiceArea));
	          }

	          warehouseJsonObject.put("warehouseCode", JSONObject.NULL);
	          String warehouseCode = warehouseDetails.get("WareHouseCode");
	          if (!"".equals(warehouseCode)) {
	             warehouseJsonObject.put("warehouseCode", warehouseCode);
	          }
	          
	          String teams = warehouseDetails.get("Teams");
	          warehouseJsonObject.put("teamsIdsList", commonGetAPI.getTeamIdListBasedOnAttchedStaff(teams));

	          warehouseJsonObject.put("status", status);
	          warehouseJsonObject.put("description", warehouseDetails.get("Description"));
	          
	          warehouseJsonObject.put("latitude", JSONObject.NULL);
	          String latitude = warehouseDetails.get("Latitude");
	          if (!"".equals(warehouseCode)) {
	             warehouseJsonObject.put("latitude", latitude);
	          }
	          
	          warehouseJsonObject.put("longitude", JSONObject.NULL);
	          String longitude = warehouseDetails.get("Longitude");
	          if (!"".equals(warehouseCode)) {
	             warehouseJsonObject.put("longitude", longitude);
	          }

	          warehouseJsonObject.put("serviceAreaIdsList",
	                commonGetAPI.getServiceAreaIdList(warehouseDetails.get("ServiceArea")));
	          
	          warehouseJsonObject.put("branchId", JSONObject.NULL);
	          String branch = warehouseDetails.get("Branch");
	          if (!"".equals(branch)) {
	             int branchId = commonGetAPI.getBranchIdList(branch).get(0);
	             warehouseJsonObject.put("branchId", branchId);
	          }

	          warehouseJsonObject.put("address1", warehouseDetails.get("Address1"));
	          warehouseJsonObject.put("address2", warehouseDetails.get("Address2"));

	          String municipality = warehouseDetails.get("Municipality");

	          if (!"".equals(municipality)) {

	             String temp = commonGetAPI.getMasterDetailsByMunicipalityName(municipality);

	             String data[] = temp.split(":");

	             int pincodeId = Integer.parseInt(data[0]);
	             int cityId = Integer.parseInt(data[1]);
	             int stateId = Integer.parseInt(data[2]);
	             int countryId = Integer.parseInt(data[3]);

	             warehouseJsonObject.put("pincode", pincodeId);
	             warehouseJsonObject.put("city", cityId);
	             warehouseJsonObject.put("state", stateId);
	             warehouseJsonObject.put("country", countryId);
	          }

	          warehouseJsonObject.put("id", "");
	          int staffId =commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
	          warehouseJsonObject.put("mvnoId", staffId);

	          //for threesoldvalue
	          String productName = warehouseDetails.get("ProductName");
	          JSONArray thresholdArray = new JSONArray(); // Always initialize the array

	          if (productName != null && !productName.isEmpty()) {
	              int productId = commonGetAPI.getProductId(productName);

	              JSONObject thresholdJson = new JSONObject();
	              thresholdJson.put("mvnoId", staffId);
	              thresholdJson.put("productId", productId);
	              thresholdJson.put("thresholdQty", warehouseDetails.get("ThresholdQty"));
	              thresholdJson.put("unit", warehouseDetails.get("Unit"));
	              thresholdJson.put("warehouseId", "");  // or JSONObject.NULL if needed

	              thresholdArray.put(thresholdJson); // Only add the object if productName is valid
	          }

	          // Add the array (either empty or with the object) to the main JSON object
	          warehouseJsonObject.put("productWarehouseMappingDTOS", thresholdArray);

	          jsonString = warehouseJsonObject.toString();
	       } catch (Exception e) {
	          jsonString = null;
	          e.printStackTrace();
	       }

	       return jsonString;
	    }
	
	public List<Integer> getTeamIdListBasedOnAttchedStaff_OLD(String teamName) {

		String apiURL = getAPIURL("teams/getAllTeamBasedOnAttchedStaff");

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		List<Integer> list = new ArrayList<Integer>();

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedTeamName = jsonArray.getJSONObject(i).getString("name");
				if (teamName.equalsIgnoreCase(receivedTeamName)) {
					list.add(jsonArray.getJSONObject(i).getInt("id"));
				}
			}
		}

		return list;
	}

}
