package customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class AssignInventory extends RestExecution {

	private static String logFileName = "PrepaidCustomer.log";
	private static String logModuleName = "AssignInventory";

	private int customerId1 = 0;

	private void AssignInventoryToCustomer(Map<String, String> customerDetailsMap) {

		String apiURL = null;
		String apiBody = null;

		String itemType = customerDetailsMap.get("ItemType");

		if (itemType.equalsIgnoreCase("Serialized Item")) {
			String assemblyType = customerDetailsMap.get("AssemblyType");
			if (assemblyType.equalsIgnoreCase("Single Item")) {
				apiURL = getAPIURL("SavbillInventoryManagement/inwards/assignToCustomer");
				apiBody = getAssignInventoryJson(customerDetailsMap);

			} else if (assemblyType.equalsIgnoreCase("Pair Item")) {
				apiURL = getAPIURL("SavbillInventoryManagement/inwards/assignToCustomer");
				apiBody = getAssignPairInventoryJson(customerDetailsMap);
			}

		} else if (itemType.equalsIgnoreCase("Non Serialized Item")) {
			apiURL = getAPIURL("SavbillInventoryManagement/inwards/assignNonSerializedItemToCustomer");
			apiBody = getAssignNonSerializedInventoryJson(customerDetailsMap);
		}

		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			// Fetching the desired value of a parameter
			int status = JSONResponseBody.getInt("responseCode");
			String userName = customerDetailsMap.get("CustomerUsername");

			if (status == 200) {
				
				String macAddress = customerDetailsMap.get("MAC");
				String serialNumber = customerDetailsMap.get("SerialNumber");
				String productName = customerDetailsMap.get("Product");
				String qty = customerDetailsMap.get("NonSerializedQty");
				
				String detail = userName +"|"+ productName +"|"+ macAddress +"|"+ serialNumber;
				String message = "An inventory is assigned to customer successfully - " + detail;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

				// Approval of customer inventory is done here.

				int custInventoryMappingId = getAllCustomerInventoryList(itemType, customerId1, macAddress,
						serialNumber,productName,qty);
				approveAssignedCustomerInventory(custInventoryMappingId);

			} /*else if (status == 406) {
				String error = JSONResponseBody.getString("responseMessage");
				System.out.println(error + " - " + userName);
			}  */
			else if (status == 406) {
			    String error = JSONResponseBody.isNull("responseMessage") ? "No response message" : JSONResponseBody.getString("responseMessage");
			    System.out.println(error + " - " + userName);
			}
			
		}
	}

	public void AssignInventoryToCustomer(List<Map<String, String>> customerMapList) {
		
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		
		for (int i = 0; i < customerMapList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map = customerMapList.get(i);

			String userName = map.get("CustomerUsername");
			if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
				Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
				AssignInventoryToCustomer(map);
			} else {
				System.out.println("Customer UserName is NOT found - " + userName);
			}
		}
	}

	public List<Map<String, String>> readAssignInventoryCustomerList() {

		String sheetName = "AssignCustomerInventory";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("CustomerUsername");
			String mStatus = cellValue.get("MigrationStatus");

			if ((!"".equals(userName)) && (!"success".equalsIgnoreCase(mStatus))) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
				valuemap.put("CustomerUsername", cellValue.get("CustomerUsername"));
				valuemap.put("Service", cellValue.get("Service"));
				valuemap.put("PlanName", cellValue.get("PlanName"));
				valuemap.put("ItemType", cellValue.get("ItemType"));
				valuemap.put("NonSerializedQty", cellValue.get("NonSerializedQty"));
				valuemap.put("AssemblyType", cellValue.get("AssemblyType"));
				valuemap.put("AssemblyName", cellValue.get("AssemblyName"));
				valuemap.put("Service", cellValue.get("Service"));
				valuemap.put("Product", cellValue.get("Product"));
				valuemap.put("SerialNumber", cellValue.get("SerialNumber"));
				valuemap.put("MAC", cellValue.get("MAC"));
				valuemap.put("AssignDate", cellValue.get("AssignDate"));

				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}

	private String getAssignInventoryJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			JSONObject customerJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			String customerType = customerDetails.get("SubscriberType");
			String customerUsername = customerDetails.get("CustomerUsername");
			int customerId = commonGetAPI.getCustomerId(customerUsername,customerType);
			customerId1 = customerId;
			if (customerId != 0) {
				customerJson.put("customerId", customerId);
			}

			String planName = customerDetails.get("PlanName");
			String details = getPlanByCustService(customerId, planName);
			String temp[] = details.split(":");

			int serviceId = Integer.valueOf(temp[0]);
			String connectionNumber = temp[1];
			int custServiceMapId = Integer.valueOf(temp[2]);

			customerJson.put("serviceId", serviceId);
			customerJson.put("connectionNo", connectionNumber);
			customerJson.put("custServiceMapId", custServiceMapId);

			String product = customerDetails.get("Product");
			int productId = commonGetAPI.getProductId(product);
			customerJson.put("productId", productId);

			String itemType = customerDetails.get("ItemType");
			if (!"".equals(itemType)) {
				if (itemType.equalsIgnoreCase("Serialized Item")) {
					customerJson.put("qty", 1);
					customerJson.put("nonSerializedQty", "");
				} else if (itemType.equalsIgnoreCase("Non Serialized Item")) {
					int qty = Integer.valueOf(customerDetails.get("NonSerializedQty"));
					customerJson.put("qty", "");
					customerJson.put("nonSerializedQty", qty);
				}
			}

			String assignDate = customerDetails.get("AssignDate");
			if (!"".equals(assignDate)) {
				assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy",
						"yyyy-MM-dd'T'HH:mm:ss");
				customerJson.put("assignedDateTime", assignDate);
			}

			customerJson.put("status", JSONObject.NULL);

			String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));

			customerJson.put("billabecustId", JSONObject.NULL);
			customerJson.put("billTo", "CUSTOMER");
			customerJson.put("chargeId", JSONObject.NULL);

			customerJson.put("id", "");
			customerJson.put("itemAssemblyId", "");
			customerJson.put("mvnoId", "");
			customerJson.put("externalItemId", "");
			customerJson.put("inventoryType", "");
			customerJson.put("paymentOwnerId", staffId);
			customerJson.put("staffId", staffId);
			customerJson.put("inwardId", JSONObject.NULL);
			customerJson.put("itemAssemblyflag", false);
			customerJson.put("itemTypeFlag", itemType);

			customerJson.put("parentCustomerId", JSONObject.NULL);
			customerJson.put("isFree", JSONObject.NULL);
			customerJson.put("isInvoiceToOrg", false);
			customerJson.put("isRequiredApproval", false);
			customerJson.put("itemAssemblyStatus", "Pending");

			String tempNewProductAmount = getSerializedInventoryProductDetails(serviceId, product);
		//	int newProductAmount = Integer.parseInt(tempNewProductAmount);
			// sarfraz  chnges
			double newProductAmountDouble = 0;
			try {
			    newProductAmountDouble = Double.parseDouble(tempNewProductAmount);
			} catch (NumberFormatException e) {
			    // Handle invalid format, e.g., if tempNewProductAmount is not a valid number
			    newProductAmountDouble = 0;
			}
			
			
			// Convert to int (this will truncate any decimal part)  
			int newProductAmount = (int) newProductAmountDouble;
			// If price is 0 then set null 
			if(newProductAmount==0) {
				customerJson.put("newAmount", JSONObject.NULL);
				customerJson.put("offerPrice", JSONObject.NULL);
			}
			else {
			customerJson.put("newAmount", newProductAmount);
			customerJson.put("offerPrice", newProductAmount);
			}
			customerJson.put("discount", JSONObject.NULL);

			String macAddress = customerDetails.get("MAC");
			String serialNumber = customerDetails.get("SerialNumber");

			JSONObject itemHistory = getItemHistoryByProduct(productId, staffId, macAddress, serialNumber);
			int itemId = itemHistory.getInt("itemId");
			String itemCondition = itemHistory.getString("condition");
			String itemMacAddress = itemHistory.getString("macAddress");
			String itemSerialNumber = itemHistory.getString("serialNumber");

			customerJson.put("itemId", itemId);
			customerJson.put("itemType", itemCondition);

			itemHistory.put("macAddress", itemMacAddress);
			itemHistory.put("serialNumber", itemSerialNumber);
			int status = updateMacMapping(itemHistory);

			if (status == 200) {

				List<JSONObject> itemHistoryList = new ArrayList<JSONObject>();
				itemHistoryList.add(itemHistory);
				customerJson.put("inOutWardMACMapping", itemHistoryList);

				JSONObject custInvParamsJson = new JSONObject();
				String paramName = product + " SN";
				custInvParamsJson.put("paramName", paramName);
				custInvParamsJson.put("paramValue", serialNumber);

				List<JSONObject> custInvParams = new ArrayList<JSONObject>();
				custInvParams.add(custInvParamsJson);
				customerJson.put("custInvParams", custInvParams);

			}

			jsonString = customerJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private String getAssignNonSerializedInventoryJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			JSONObject customerJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			String customerType = customerDetails.get("SubscriberType");
			String customerUsername = customerDetails.get("CustomerUsername");
			int customerId = commonGetAPI.getCustomerId(customerUsername,customerType);
			customerId1 = customerId;
			if (customerId != 0) {
				customerJson.put("customerId", customerId);
			}

			String planName = customerDetails.get("PlanName");
			String details = getPlanByCustService(customerId, planName);
			String temp[] = details.split(":");

			int serviceId = Integer.valueOf(temp[0]);
			String connectionNumber = temp[1];

			customerJson.put("serviceId", serviceId);
			customerJson.put("connectionNo", connectionNumber);

			String product = customerDetails.get("Product");
			int productId = commonGetAPI.getProductId(product);
			customerJson.put("productId", productId);

			String assignDate = customerDetails.get("AssignDate");
			if (!"".equals(assignDate)) {
				assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy",
						"yyyy-MM-dd'T'HH:mm:ss");
				customerJson.put("assignedDateTime", assignDate);
			}

			customerJson.put("status", JSONObject.NULL);

			String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));

			customerJson.put("billabecustId", JSONObject.NULL);
			customerJson.put("billTo", "CUSTOMER");
			customerJson.put("chargeId", JSONObject.NULL);

			customerJson.put("inwardId", JSONObject.NULL);
			customerJson.put("parentCustomerId", JSONObject.NULL);
			customerJson.put("isFree", JSONObject.NULL);
			customerJson.put("isInvoiceToOrg", false);
			customerJson.put("isRequiredApproval", false);
			customerJson.put("nonSerializedItemRemark", "This Non-Serialized inventory is assigned by Migration.");

			customerJson.put("id", JSONObject.NULL);
			customerJson.put("itemAssemblyId", JSONObject.NULL);
			customerJson.put("mvnoId", JSONObject.NULL);
			customerJson.put("externalItemId", JSONObject.NULL);
			customerJson.put("inventoryType", JSONObject.NULL);
			customerJson.put("inwardId", JSONObject.NULL);
			customerJson.put("paymentOwnerId", staffId);
			customerJson.put("staffId", staffId);
			customerJson.put("itemAssemblyStatus", "Pending");

			String tempNewProductAmount = getNonSerializedInventoryProductDetails(product);
			int newProductAmount = Integer.parseInt(tempNewProductAmount);
			customerJson.put("newAmount", newProductAmount);
			customerJson.put("offerPrice", newProductAmount);
			customerJson.put("discount", JSONObject.NULL);

			JSONObject nonTrackableProduct = getNonTrackableProductQty(productId, staffId);
			int productId1 = nonTrackableProduct.getInt("productId");
			customerJson.put("itemId", productId1);

			int unusedQty = nonTrackableProduct.getInt("unusedQty");

			String itemType = customerDetails.get("ItemType");
			if (!"".equals(itemType)) {
				if (itemType.equalsIgnoreCase("Serialized Item")) {
					customerJson.put("qty", 1);
					customerJson.put("nonSerializedQty",  JSONObject.NULL);
					customerJson.put("itemAssemblyflag", false);
					customerJson.put("itemTypeFlag", "Serialized Item");

				} else if (itemType.equalsIgnoreCase("Non Serialized Item")) {
					int qty = Integer.valueOf(customerDetails.get("NonSerializedQty"));
					if (qty < unusedQty) {
						customerJson.put("qty", qty);
						customerJson.put("nonSerializedQty", qty);
						customerJson.put("itemTypeFlag", "Non Serialized Item");
					} else {
						jsonString = null;
						System.out.println(
								"Non Serialized Product provided qty is higher than unusedQty" + customerUsername);
						Utility.printLog(logFileName, logModuleName,
								"Non Serialized Product provided qty is higher than unusedQty ", customerUsername);
					}
				}
			}

			jsonString = customerJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private String getAssignPairInventoryJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			JSONObject customerJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			String customerType = customerDetails.get("SubscriberType");
			String customerUsername = customerDetails.get("CustomerUsername");
			int customerId = commonGetAPI.getCustomerId(customerUsername,customerType);
			customerId1 = customerId;
			if (customerId != 0) {
				customerJson.put("customerId", customerId);
			}

			String planName = customerDetails.get("PlanName");
			String details = getPlanByCustService(customerId, planName);
			String temp[] = details.split(":");

			int serviceId = Integer.valueOf(temp[0]);
			String connectionNumber = temp[1];

			customerJson.put("serviceId", serviceId);
			customerJson.put("connectionNo", connectionNumber);

			String product1 = null;
			String product2 = null;
			String product = customerDetails.get("Product");
			if (product.contains(",")) {
				String temp1[] = product.split(",");
				product1 = temp1[0];
				product2 = temp1[1];
			}

			int productId = commonGetAPI.getProductId(product1);
			customerJson.put("productId", productId);

			String assignDate = customerDetails.get("AssignDate");
			if (!"".equals(assignDate)) {
				assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy",
						"yyyy-MM-dd'T'HH:mm:ss");
				customerJson.put("assignedDateTime", assignDate);
			}

			customerJson.put("status", JSONObject.NULL);

			String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));

			customerJson.put("id", JSONObject.NULL);
			customerJson.put("itemAssemblyId", JSONObject.NULL);
			customerJson.put("mvnoId", JSONObject.NULL);
			customerJson.put("externalItemId", JSONObject.NULL);
			customerJson.put("inventoryType", JSONObject.NULL);
			customerJson.put("inwardId", JSONObject.NULL);
			customerJson.put("staffId", staffId);

			String serialNumber1 = null;
			String serialNumber2 = null;
			String serial = customerDetails.get("SerialNumber");
			if (serial.contains(",")) {
				String temp1[] = serial.split(",");
				serialNumber1 = temp1[0];
				serialNumber2 = temp1[1];
			}

			String macAddress = customerDetails.get("MAC");

			JSONObject itemHistory = getItemHistoryByProduct(productId, staffId, macAddress, serial);
			int itemId = itemHistory.getInt("itemId");
			customerJson.put("itemId", itemId);

			// itemHistory.put("serialNumber", serialNumber1);
			// int status = updateMacMapping(itemHistory);

			List<JSONObject> itemHistoryList = new ArrayList<JSONObject>();
			itemHistoryList.add(itemHistory);

			String assemblyType = customerDetails.get("AssemblyType");
			if (assemblyType.equalsIgnoreCase("Pair Item")) {

				int productId2 = commonGetAPI.getProductId(product2);
				JSONObject itemHistory1 = getItemHistoryByProduct(productId2, staffId, macAddress, serial);

				// itemHistory.put("serialNumber", serialNumber2);
				// status = updateMacMapping(itemHistory1);
				itemHistoryList.add(itemHistory1);
			}

			customerJson.put("inOutWardMACMapping", itemHistoryList);

			String itemType = customerDetails.get("ItemType");
			if (!"".equals(itemType)) {
				if (itemType.equalsIgnoreCase("Serialized Item")) {

					// Serialized single Item.
					// String assemblyType = customerDetails.get("AssemblyType");
					if (assemblyType.equalsIgnoreCase("Single Item")) {
						customerJson.put("qty", 1);
						customerJson.put("itemAssemblyflag", false);

						// Serialized Pair Item.
					} else if (assemblyType.equalsIgnoreCase("Pair Item")) {
						customerJson.put("qty", 2);
						customerJson.put("itemAssemblyflag", true);
						customerJson.put("itemAssemblyName", customerDetails.get("AssemblyName"));
					}

					customerJson.put("itemTypeFlag", "Serialized Item");
					customerJson.put("nonSerializedQty", JSONObject.NULL);

				} else if (itemType.equalsIgnoreCase("Non Serialized Item")) {

					JSONObject nonTrackableProduct = getNonTrackableProductQty(productId, staffId);
					int productId1 = nonTrackableProduct.getInt("productId");
					customerJson.put("itemId", productId1);

					int unusedQty = nonTrackableProduct.getInt("unusedQty");

					int qty = Integer.valueOf(customerDetails.get("NonSerializedQty"));
					if (qty < unusedQty) {
						customerJson.put("qty", qty);
						customerJson.put("nonSerializedQty", qty);
						customerJson.put("itemTypeFlag", "Non Serialized Item");
					} else {
						jsonString = null;
						System.out.println(
								"Non Serialized Product provided qty is higher than unusedQty" + customerUsername);
						Utility.printLog(logFileName, logModuleName,
								"Non Serialized Product provided qty is higher than unusedQty ", customerUsername);
					}
				}
			}

			customerJson.put("itemAssemblyStatus", "Pending");
			jsonString = customerJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private String getPlanByCustService(int custId, String planName) {

		String queryParam = "cpm/subscriber/getPlanByCustService/" + custId
				+ "?isAllRequired=true&isNotChangePlan=true";
		String apiURL = getAPIURL(queryParam);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		String result = null;

		if (status == 0) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedPlanName = jsonArray.getJSONObject(i).getString("planName");
				if (receivedPlanName.equalsIgnoreCase(planName)) {
					int serviceId = jsonArray.getJSONObject(i).getInt("serviceId");
					String connectionNumber = jsonArray.getJSONObject(i).getString("connection_no");
					int custPlanMapppingId = jsonArray.getJSONObject(i).getInt("custPlanMapppingId");
					result = serviceId + ":" + connectionNumber + ":" + custPlanMapppingId;
					break;
				}
			}
		}

		if (result == null) {
			System.out.println("Customer Plan details not found - " + planName);
			Utility.printLog(logFileName, logModuleName, "Customer Plan details not found - ", planName);
		}

		return result;
	}

	private String getSerializedInventoryProductDetails(int serviceId, String productName) {

		String queryParam = "SavbillInventoryManagement/product/getAllProductByServiceId?serviceId=" + serviceId;
		String apiURL = getAPIURL(queryParam);
System.out.println(serviceId);
		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		String result = null;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedProductName = jsonArray.getJSONObject(i).getString("name");
				if (receivedProductName.equalsIgnoreCase(productName)) {
				//	int newProductAmount = jsonArray.getJSONObject(i).getInt("newProductAmount");
				//	result = String.valueOf(newProductAmount);
				//	break;
					String newProductAmount = jsonArray.getJSONObject(i).optString("newProductAmount", ""); // Will return empty string if null or not found
					result = newProductAmount;
					break;


				}
			}
		}

		if (result == null) {
			System.out.println("Inventory details not found - " + productName);
			Utility.printLog(logFileName, logModuleName, "Inventory details not found - ", productName);
		}

		return result;
	}

	private String getNonSerializedInventoryProductDetails(String productName) {

		String queryParam = "SavbillInventoryManagement/product/getAllProductForNonTrackableProductCategory";
		String apiURL = getAPIURL(queryParam);
	//	Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		String result = null;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedProductName = jsonArray.getJSONObject(i).getString("name");
				if (receivedProductName.equalsIgnoreCase(productName)) {
					//int newProductAmount = jsonArray.getJSONObject(i).getInt("newProductAmount");   //--sar 1 feb
					//result = String.valueOf(newProductAmount);
					
					String newProductAmount = jsonArray.getJSONObject(i).getString("newProductAmount");
					result = newProductAmount;
					break;

					
				}
			}
		}

		if (result == null) {
			System.out.println("Inventory details not found - " + productName);
			Utility.printLog(logFileName, logModuleName, "Inventory details not found - ", productName);
			
		}

		return result;
	}

	private JSONObject getItemHistoryByProduct(int productId, String ownerId, String assignedMacAddress,
			String assignedSerialNumber) {

		String queryParam = "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=Staff";
		String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct" + queryParam;
		apiURL = getAPIURL(apiURL);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		JSONObject itemJson = null;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			if (jsonArray.length() > 0) {

				for (int i = 0; i < jsonArray.length(); i++) {
					String serialNumber = jsonArray.getJSONObject(i).getString("serialNumber");
					String macAddress = jsonArray.getJSONObject(i).getString("macAddress");

					if (assignedMacAddress.equalsIgnoreCase(macAddress)
							&& assignedSerialNumber.equalsIgnoreCase(serialNumber)) {
						itemJson = jsonArray.getJSONObject(i);
						break;
					}
				}
			}
		}

		if (itemJson == null) {
			String message = "Item history of product id " + productId + " with MAC=" + assignedMacAddress
					+ " and Serial=" + assignedSerialNumber + " not found";
			System.out.println(message);
			Utility.printLog(logFileName, logModuleName, message, "");
		}

		return itemJson;
	}

	public int updateMacMapping(JSONObject itemMacSerialMapping) {

		int status = 0;
		try {

			int itemId = itemMacSerialMapping.getInt("itemId");
			String mac = itemMacSerialMapping.getString("macAddress");
			String serialNumber = itemMacSerialMapping.getString("serialNumber");

			String queryParam = "?itemId=" + itemId + "&macAddress=" + mac + "&serialNumber=" + serialNumber;
			String apiURL = "SavbillInventoryManagement/item/updateItemMacAndSerial" + queryParam;
			apiURL = getAPIURL(apiURL);
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			// Initializing payload or API body
			String apiBody = itemMacSerialMapping.toString();
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			status = JSONResponseBody.getInt("responseCode");

			if (status == 200) {
				String message = "New MAC/Serials are updated in item successfully - itemID-" + itemId;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);
			} else {
				String error = JSONResponseBody.getString("responseMessage") + " - " + itemId;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Error", error);
			}

		} catch (Exception e) {
			status = 0;
			e.printStackTrace();
		}

		return status;
	}

	private JSONObject getNonTrackableProductQty(int productId, String ownerId) {

		String queryParam = "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=Staff";
		String apiURL = "SavbillInventoryManagement/outwards/getNonTrackableProductQty" + queryParam;
		apiURL = getAPIURL(apiURL);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		JSONObject itemJson = null;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");

			int receivedProductId = jsonArray.getJSONObject(0).getInt("productId");
			if (receivedProductId == productId) {
				itemJson = jsonArray.getJSONObject(0);
			}
		}

		if (itemJson == null) {
			System.out.println("Non serialized product not found - " + productId);
			Utility.printLog(logFileName, logModuleName, "Non serialized product not found - ",
					String.valueOf(productId));
		}

		return itemJson;
	}

	private int getAllCustomerInventoryList(String itemType, int custId, String macAddress,
			String serialNumber,String productName,String qty) {

		String queryParam = "SavbillInventoryManagement/inwards/getAllCustomerInventoryList?custId=" + custId;
		String apiURL = getAPIURL(queryParam);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("responseCode");
		int assemblyId = 0;
		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
			//Utility.printLog(logFileName, logModuleName, "Response", jsonArray.toString());

			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedStatus = jsonArray.getJSONObject(i).getString("status");
				if (receivedStatus.equalsIgnoreCase("PENDING")) {
					JSONArray inOutWardMACMapping = jsonArray.getJSONObject(i).getJSONArray("inOutWardMACMapping");

					if (itemType.equalsIgnoreCase("Serialized Item")) {
						String receivedSerialNumber = inOutWardMACMapping.getJSONObject(0).getString("serialNumber");
						String receivedMACAddress = inOutWardMACMapping.getJSONObject(0).getString("macAddress");

						if (macAddress.equalsIgnoreCase(receivedMACAddress)
								&& serialNumber.equalsIgnoreCase(receivedSerialNumber)) {
							assemblyId = jsonArray.getJSONObject(i).getInt("id");
							break;
						}

					} else if (itemType.equalsIgnoreCase("Non Serialized Item")) {
						String receivedProductName =  jsonArray.getJSONObject(i).getString("productName");
						int receivedQty =  jsonArray.getJSONObject(i).getInt("qty");
						int tempQty = Integer.parseInt(qty);
						
						if (productName.equalsIgnoreCase(receivedProductName) && (tempQty == receivedQty)) {
							assemblyId = jsonArray.getJSONObject(i).getInt("id");
							break;
						}
					}
				}
			}
		}

		if (assemblyId == 0) {
			System.out.println("Customer inventory mapping details not found - " + custId);
			Utility.printLog(logFileName, logModuleName, "Customer inventory mapping details not found",
					String.valueOf(custId));
		}

		return assemblyId;
	}


	private void approveAssignedCustomerInventory(int custInventoryMappingId) {
		
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));

		String apiURL = "SavbillInventoryManagement/inwards/approveInventory?isApproveRequest=true&nextstaff=" + staffId
				+ "&remark=Approved by Migration";
		
		// SavbillInventoryManagement/inwards/approveInventory?isApproveRequest=true&nextstaff=2&remark=ok
		
		apiURL = getAPIURL(apiURL);

		List<Integer> assemblyId = new ArrayList<Integer>();
		assemblyId.add(custInventoryMappingId);
		// JSONObject approveJson = new JSONObject();
		// approveJson.put("", assemblyId);
		String apiBody = assemblyId.toString();

		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
		Utility.printLog(logFileName, logModuleName, "apiBody", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);
		int status = JSONResponseBody.getInt("responseCode");

		if (status == 200) {
			
			System.out.println("Assigned inventory is approved successfully = " + custInventoryMappingId);
			Utility.printLog(logFileName, logModuleName, "Assigned inventory is approved successfully = ",
					String.valueOf(custInventoryMappingId));
		}

	}

}
