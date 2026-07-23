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

public class AssignInventoryToPOP extends RestExecution {

	private String logFileName = "inventory.log";
	private String logModuleName = "AssignInventoryPOP";

	private void assignInventory(Map<String, String> assigningInventoryDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/inwards/assignToEndOwner");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getAssignInventoryToPOPJson(assigningInventoryDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String popName = assigningInventoryDetails.get("POPName");
			String mac = assigningInventoryDetails.get("ProductMAC");
			String serial = assigningInventoryDetails.get("ProductSerial");
			String message = popName +":" + mac + ":" + serial;
			ProductUtility.printResponse(JSONResponseBody, logModuleName, message);

			
			assignedInventoryApproval(popName, mac, serial);
		}
	}

	public void assignInventory(List<Map<String, String>> vendorMapList) {

		for (int i = 0; i < vendorMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = vendorMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			assignInventory(map);
		}
	}

	public List<Map<String, String>> readAssignInventoryToPopList() {

		String sheetName = "Assign_Inventory_To_POP";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> assignInventoryMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String popName = cellValue.get("POPName *");
			if ((!"".equals(popName)) && (popName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("POPName", cellValue.get("POPName *"));
				valuemap.put("SerializedProductName", cellValue.get("SerializedProductName"));
				valuemap.put("ProductMAC", cellValue.get("ProductMAC"));
				valuemap.put("ProductSerial", cellValue.get("ProductSerial"));
				valuemap.put("AssignDate", cellValue.get("AssignDate"));
				assignInventoryMapList.add(valuemap);
			}
		}
		return assignInventoryMapList;
	}

	private String getAssignInventoryToPOPJson(Map<String, String> assignInventoryDetails) {

		String jsonString = null;

		try {

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject assignPopJson = new JSONObject();

			int productId = commonGetAPI.getProductId(assignInventoryDetails.get("SerializedProductName"));
			int staffId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);

			assignPopJson.put("id", "");
			assignPopJson.put("qty", "1");
			assignPopJson.put("productId", productId);
			assignPopJson.put("staffId", staffId);

			assignPopJson.put("inwardId", "");

			String assignDate = assignInventoryDetails.get("AssignDate");
			if (!"".equals(assignDate)) {
				assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy",
						"yyyy-MM-dd'T'HH:mm:ss");
				assignPopJson.put("assignedDateTime", assignDate);
			}

			assignPopJson.put("status", "");
			assignPopJson.put("mvnoId", "");

			String popName = assignInventoryDetails.get("POPName");
			int popId = commonGetAPI.getPopId(popName);
			assignPopJson.put("ownerId", popId);
			assignPopJson.put("ownerType", "pop");

			assignPopJson.put("itemTypeFlag", "Serialized Item");
			assignPopJson.put("nonSerializedQty", "");

			String mac = assignInventoryDetails.get("ProductMAC");
			String serial = assignInventoryDetails.get("ProductSerial");

			List<JSONObject> inOutWardMACMapping = getItemDetailsToAssign(popName, productId, staffId, mac, serial);

			assignPopJson.put("inOutWardMACMapping", inOutWardMACMapping);

			String itemId = inOutWardMACMapping.get(0).get("itemId").toString();
			assignPopJson.put("itemId", itemId);

			jsonString = assignPopJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	private List<JSONObject> getItemDetailsToAssign(String popName, int productId, int staffId, String mac,
			String serial) throws Exception {

		String apiFind = "?productId=" + productId + "&ownerId=" + staffId + "&ownerType=Staff";

		String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct" + apiFind;
		apiURL = getAPIURL(apiURL);

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		List<JSONObject> list = new ArrayList<JSONObject>();

		if (status == 200) {

			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			Utility.printLog(logFileName, logModuleName, "dataList", jsonArray.toString());

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject macMappingJsonObject = new JSONObject();
				macMappingJsonObject = jsonArray.getJSONObject(i);

				String inwardedSerial = macMappingJsonObject.getString("serialNumber");
				String inwardedMac = macMappingJsonObject.getString("macAddress");

				if (serial.equalsIgnoreCase(inwardedSerial) && mac.equalsIgnoreCase(inwardedMac)) {

				//	System.out.println("inwardedSerial = " + inwardedSerial);
				//	System.out.println("inwardedMac = " + inwardedMac);

					list.add(macMappingJsonObject);
					break;
				}
			}
		}

		if (list.size() == 0 || list == null) {
			String message = "Provided MAC/serial not found to assign, " + "MAC : " + mac + ", Serial : " + serial;
			ProductUtility.stopExecution(logFileName, logModuleName, message, "POP Name : " + popName);
			list = null;
		}
		return list;
	}

	private String assignedInventoryApproval(String popName, String mac, String serial) {

		String jsonString = null;

		try {

			int inventoryMappingId = getInventoryAssignmentId(popName, mac, serial);
			
			String approvalRemark = "Approved by migration";
			String apiFind = "?inventoryApprovalRemark=" + approvalRemark + "&inventoryMappingId=" + inventoryMappingId
					+ "&isApproveRequest=" + true;

			String apiURL = "SavbillInventoryManagement/inwards/approveInventoryFromOwner" + apiFind;
			apiURL = getAPIURL(apiURL);
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			
			JSONObject JSONResponseBody = httpGet(apiURL);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");

			if (status == 200) {

				String productName = JSONResponseBody.getJSONObject("data").getString("productName");
				String message = "New assigned inventory in Pop is approved successfully - " + productName + ":" +mac+":"+serial;

				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else {
				String error = JSONResponseBody.getString("responseMessage") + "MAC/Serail : " +mac+":"+serial;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Inward Approval", error);
			}

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	public int getInventoryAssignmentId(String popName, String mac, String serial) {

		String jsonString = null;
		JSONObject searchCustomerJson = new JSONObject();
		CommonGetAPI commonGetAPI = new CommonGetAPI();

		List<JSONObject> assignedInventoryFilterJsonObjectList = new ArrayList<JSONObject>();
		JSONObject filterObject = new JSONObject();

		int popId = commonGetAPI.getPopId(popName);

		filterObject.put("filterValue", popId);
		filterObject.put("filterColumn", "pop");

		assignedInventoryFilterJsonObjectList.add(filterObject);
		searchCustomerJson.put("filters", assignedInventoryFilterJsonObjectList);

		searchCustomerJson.put("page", 1);
		searchCustomerJson.put("pageSize", 20);
		searchCustomerJson.put("sortBy", "createdate");
		searchCustomerJson.put("sortOrder", 0);

		jsonString = searchCustomerJson.toString();

		String apiURL = getAPIURL("SavbillInventoryManagement/inwards/getByOwnerIdAndType");
		String APIBody = jsonString;

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		int status = JSONResponseBody.getInt("responseCode");
		int inventoryMappingId = 0;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray inOutWardMACMapping = jsonArray.getJSONObject(i).getJSONArray("inOutWardMACMapping");
				String macAddress = inOutWardMACMapping.getJSONObject(0).getString("macAddress");
				String serialNumber = inOutWardMACMapping.getJSONObject(0).getString("serialNumber");

				if (mac.equalsIgnoreCase(macAddress) || serial.equalsIgnoreCase(serialNumber)) {
					inventoryMappingId = inOutWardMACMapping.getJSONObject(0).getInt("inventoryMappingId");
					break;
				}
			}
		}

		if (inventoryMappingId == 0) {
			String message = "Assigned Inventory details not found for MAC:Serial- " + mac + ":" + serial;
			System.out.println(message);
			Utility.printLog(logFileName, logModuleName, message, "");
		}

		return inventoryMappingId;
	}

}
