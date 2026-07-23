package inventory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.DBOperations;
import utility.ProductUtility;
import utility.Utility;

public class Outward extends RestExecution {

	private static String logFileName = "inventory.log";
	private static String logModuleName = "CreateOutward";
	
	private void createOutward(Map<String, String> outwardDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/outwards/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getOutwardJson(outwardDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);
		
		StopWatch sw = new StopWatch(); sw.start();
		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("responseCode");

		if (status == 200) {
			
			String outwardNumber = JSONResponseBody .getJSONObject("data").getString("outwardNumber");
			
			String message = "New Outward is added successfully - " + outwardNumber + " | " + sw.getTime();;
			System.out.println(message);
			Utility.printLog("execution.log", logModuleName, "Success", message);

			int outwardId = JSONResponseBody.getJSONObject("data").getInt("id");
			int outwardsInwardId = JSONResponseBody.getJSONObject("data").getInt("outwardsInwardId");
			String productName = outwardDetails.get("Product");
			
			sw.reset(); sw.start();
			outwardMacMapping(outwardNumber,outwardId, outwardDetails); 
			message = "New Outward Serial mapping time - " + outwardNumber + " | " + sw.getTime();
			System.out.println(message);
			
			sw.reset(); sw.start();			
			outwardGeneratedInwardApproval(outwardsInwardId,productName); 
			message = "New Outward approval time - " + outwardNumber + " | " + sw.getTime();
			System.out.println(message);
			
		} else if (status == 406) {
			String error = JSONResponseBody.getString("responseMessage") + " - " + outwardDetails.get("Product");
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "Already Exist", error);
		}
	}

	public void createOutward(List<Map<String, String>> outwardMapList) {

		for (int i = 0; i < outwardMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = outwardMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createOutward(map);
		}
	}

//    public void createOutward(List<Map<String, String>> outwardMapList) {
//
//        // Create a fixed thread pool, adjust size as needed
//        ExecutorService executor = Executors.newFixedThreadPool(5); // e.g., 5 threads
//
//        for (Map<String, String> map : outwardMapList) {
//            executor.submit(() -> {
//                try {
//                    Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//                    createOutward(map); // original processing
//                } catch (Exception e) {
//                    Utility.printLog(logFileName, logModuleName, "Error processing map", e.getMessage());
//                }
//            });
//        }
//
//        // Shutdown executor gracefully and wait for all tasks to complete
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

	public List<Map<String, String>> readUniqueOutwardList() {

		String sheetName = "Outward";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);
		Utility.printLog(logFileName, logModuleName, "Sheet Data", sheetMap.toString());

		String sheetName1 = "Outward_MAC_Serial_Mapping";
		List<Map<String, String>> macSerialMapping = new ArrayList<Map<String, String>>();
		macSerialMapping = readData.getInventoryDataSheet(sheetName1);
		
		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> outwardMapList = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String product = safeTrim(cellValue.get("Product *"));
			if ((!"".equals(product)) && (product != null)) {

				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("Product", safeTrim(cellValue.get("Product *")));
				valuemap.put("SourceType", safeTrim(cellValue.get("SourceType *")));
				valuemap.put("SelectSource", safeTrim(cellValue.get("SelectSource *")));

				valuemap.put("DestinationType", safeTrim(cellValue.get("DestinationType *")));
				valuemap.put("SelectDestination", safeTrim(cellValue.get("SelectDestination *")));

				String createOutwardId = safeTrim(cellValue.get("Outward_ID"));
				String macList = "";
				String serialList = "";
				
				for (int j = 0; j < macSerialMapping.size(); j++) {

					Map<String, String> map = new HashMap<String, String>();
					map = macSerialMapping.get(j);

					String outwardId = map.get("Outward_ID");
					if (createOutwardId.equalsIgnoreCase(outwardId)) {
						macList = macList + map.get("OutwardMAC") + ",";
						serialList = serialList + map.get("OutwardSerialNumber") + ",";
					}
				}
				
				macList = macList.substring(0,macList.length()-1);
				serialList = serialList.substring(0,serialList.length()-1);				
				
				valuemap.put("outwardMAC", macList);
				valuemap.put("outwardSerialNumber", serialList);

				valuemap.put("QuantityOut", safeTrim(cellValue.get("QuantityOut *")));
				valuemap.put("OutwardDate", safeTrim(cellValue.get("OutwardDate *")));
				valuemap.put("Status", safeTrim(cellValue.get("Status *")));
				valuemap.put("Description", safeTrim(cellValue.get("Description *")));

				outwardMapList.add(valuemap);
			}
		}
//        System.out.print(outwardMapList);
		return outwardMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

	private String getOutwardJson(Map<String, String> outwardDetails) {

		String jsonString = null;

		try {

			JSONObject outwardJson = new JSONObject();

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			String warehouse = outwardDetails.get("SelectSource");
			String productName = outwardDetails.get("Product");

			int productId = commonGetAPI.getProductId(productName);
			if (productId != 0) {
				outwardJson.put("productId", productId);

			}

			int warehouseId = commonGetAPI.getWarehouseId(warehouse);
			outwardJson.put("sourceId", warehouseId);
			outwardJson.put("sourceType", "Warehouse");

			outwardJson.put("outwardNumber", "");

			String destinationType = outwardDetails.get("DestinationType");
			if (!"".equals(destinationType)) {
				if (destinationType.equalsIgnoreCase("staff")) {

					outwardJson.put("destinationType", "Staff");
					int staffId = commonGetAPI.getStaffId(outwardDetails.get("SelectDestination"));
					outwardJson.put("destinationId", staffId);

				} else if (destinationType.equalsIgnoreCase("partner")) {

					outwardJson.put("destinationType", "Partner");
					int partnerId = commonGetAPI.getPartnerId(outwardDetails.get("SelectDestination"));
					outwardJson.put("destinationId", partnerId);

				} else if (destinationType.equalsIgnoreCase("warehouse")) {

					outwardJson.put("destinationType", "Warehouse");
					int warehouseId1 = commonGetAPI.getWarehouseId(outwardDetails.get("SelectDestination"));
					outwardJson.put("destinationId", warehouseId1);
				}
			}

			int qtyInward = Integer.parseInt(outwardDetails.get("QuantityOut"));
			if (qtyInward > 0) {
				outwardJson.put("inTransitQty", qtyInward);

			}

			String outwardDate = outwardDetails.get("OutwardDate");
			if (!"".equals(outwardDate)) {
				outwardDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(outwardDate, "dd-MMM-yyyy",
						"yyyy-MM-dd'T'HH:mm:ss");
				outwardJson.put("outwardDateTime", outwardDate);
			}
			
			outwardJson.put("status", outwardDetails.get("Status").toUpperCase());
			outwardJson.put("description", outwardDetails.get("Description"));
			
			outwardJson.put("id", JSONObject.NULL);
			outwardJson.put("qty", JSONObject.NULL);
			outwardJson.put("usedQty", JSONObject.NULL);
			outwardJson.put("unusedQty", JSONObject.NULL);
			outwardJson.put("outTransitQty", JSONObject.NULL);
			outwardJson.put("rejectedQty", JSONObject.NULL);
			outwardJson.put("requestInventoryId", JSONObject.NULL);

			outwardJson.put("mvnoId", JSONObject.NULL);
			outwardJson.put("requestInventoryProductId", JSONObject.NULL);
			outwardJson.put("selectedItems", JSONObject.NULL);
			
			jsonString = outwardJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//        System.out.println(jsonString);
		return jsonString;
	}

	private void outwardMacMapping(String outwardNumber, int outwardId, Map<String, String> outwardDetails) {

		/* 
		 * Temporary for outward mac-serial generation only Not required for actual migration.
					
		int qty = Integer.parseInt(outwardDetails.get("QuantityOut"));
		String ans = macSerialGenerate(qty,"SR-");		
		String randomSerialMAC[] = ans.split("##");
		String serial = randomSerialMAC[0];
		String mac = randomSerialMAC[1];		
		//***************************************
			
		
		StopWatch sw = new StopWatch();
		sw.start();
		String prefix = outwardDetails.get("outwardSerialNumber");
		int qty = Integer.parseInt(outwardDetails.get("QuantityOut"));
		String serial = generateSequencialSerialNumber(prefix,qty);		
		System.out.println("Serial Generation Time : " +  sw.getTime());
		 */
		
		//String mac = outwardDetails.get("OutwardMAC");
		//String serial = outwardDetails.get("OutwardSerialNumber");
		
		String productName = outwardDetails.get("Product");
		String warehouseName = outwardDetails.get("SelectSource");
		
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		String details = commonGetAPI.getProductCategoryMACSerialTrackDetailsAll(productName);
		String productCategoryDetails[] = details.split(":");
		boolean hasTrackable = Boolean.valueOf(productCategoryDetails[0]);
		boolean hasSerial = Boolean.valueOf(productCategoryDetails[1]);
		boolean hasMac = Boolean.valueOf(productCategoryDetails[2]);
		
		if((hasTrackable) || (hasSerial)) {
			
			List<String> macSerialList = new ArrayList<String>();
			String serial = outwardDetails.get("outwardSerialNumber");
			if (!"".equals(serial)) {

				String serialArray[] = serial.split(",");
				String mac = outwardDetails.get("outwardMAC");
				//String mac = serial;
				if (!"".equals(mac) && hasMac) {
					String macArray[] = mac.split(",");

					for (int i = 0; i < serialArray.length; i++) {

						String result = serialArray[i] + "#" + macArray[i];
						macSerialList.add(result);
					}
				} else {
					for (int i = 0; i < serialArray.length; i++) {

						String result = serialArray[i];
						macSerialList.add(result);
					}
				}
				
				updateMacMapping(outwardNumber,productName, warehouseName, outwardId, macSerialList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String outwardGeneratedInwardApproval(int outwardsInwardId,String productName) {

		String jsonString = null;
		Inward inward = new Inward();
		
		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject inwardApprovalJson = new JSONObject();

			if (outwardsInwardId != 0) {

				inwardApprovalJson.put("id", outwardsInwardId);
				inwardApprovalJson.put("approvalStatus", "Approve");
				inwardApprovalJson.put("approvalRemark", "Approved by migration");
				//inwardApprovalJson.put("productId", inward.getProductIdAndProductCategoryIdJSON(productName));
				inwardApprovalJson.put("productId", "");
				
				int productId = commonGetAPI.getProductId(productName);
				if (productId != 0) {
					inwardApprovalJson.put("productId", productId);
				}
				
				
				jsonString = inwardApprovalJson.toString();

				String apiURL = getAPIURL("SavbillInventoryManagement/inwards/inwardApproval");
				Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

				// Initializing payload or API body
				String apiBody = jsonString;
				Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

				JSONObject JSONResponseBody = httpPut(apiURL, apiBody);
				String response = JSONResponseBody.toString(4);
				Utility.printLog(logFileName, logModuleName, "Response", response);

				int status = JSONResponseBody.getInt("responseCode");

				if (status == 200) {

					// String inwardNumber1 = JSONResponseBody
					// .getJSONObject("data").getString("inwardNumber");
					String message = "New Outward Generated-Inward is approved successfully - IN-" + outwardsInwardId;

					System.out.println(message);
					Utility.printLog("execution.log", logModuleName, "Success", message);

				} else {
					String error = JSONResponseBody.getString("responseMessage") + " - IN-" + outwardsInwardId;
					System.out.println(error);
					Utility.printLog("execution.log", logModuleName, "Inward Approval", error);
				}
			}
		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}
	
	// new developemnt
	public String getJsonGetItemPagination(int page, int pageSize) {
	    try {
	        JSONObject getOutwardItem = new JSONObject();
	        getOutwardItem.put("page", page);
	        getOutwardItem.put("pageSize", pageSize);

	        return getOutwardItem.toString();
	    } catch (Exception e) {
	        // Log the error or handle it as needed
	        System.err.println("Error creating pagination JSON: " + e.getMessage());
	        return "{}"; // return empty JSON if something goes wrong
	    }
	}


//	public List<JSONObject> getItemForOutward(String productName, String warehouseName, int outwardId,
//			List<String> macSerialList) throws Exception {
//
//		CommonGetAPI CommonGetAPI = new CommonGetAPI();
//
//		int productId = CommonGetAPI.getProductId(productName);
//		int warehouseId = CommonGetAPI.getWarehouseId(warehouseName);
//		String apiFind = "?productId=" + productId + "&ownerId=" + warehouseId + "&ownerType=Warehouse";
//
//		String apiURL = "SavbillInventoryManagement/outwards/getItemForOutward" + apiFind;
////		System.out.println("get url "+apiURL);
//
//		apiURL = getAPIURL(apiURL);
//	String apiBody=getJsonGetItemPagination(1,5000);
//
//		JSONObject jsonResponse = httpPost(apiURL,apiBody);
//
//	//	System.out.println("json"+jsonResponse);
//
//		int status = jsonResponse.getInt("responseCode");
//
//	//System.out.println("statuscode"+status);
//
//		List<JSONObject> list = new ArrayList<JSONObject>();
//
//		if (status == 200) {
//
//			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
//			Utility.printLog(logFileName, logModuleName, "dataList", jsonArray.toString());
//			if(macSerialList.size() <= jsonArray.length()) {
//
//				for (int i = 0; i < macSerialList.size(); i++) {
//
//					String serial = macSerialList.get(i);
//					String mac = null;
//					if (serial.contains("#")) {
//						String macSerial[] = serial.split("#");
//						serial = macSerial[0];
//						mac = macSerial[1];
//					}
//
//
//					for(int j=0;j<jsonArray.length();j++) {
//
//						JSONObject macMappingJsonObject = new JSONObject();
//						macMappingJsonObject = jsonArray.getJSONObject(j);
//
//						String inwardedSerial = macMappingJsonObject.getString("serialNumber");
//						String inwardedMac = macMappingJsonObject.getString("macAddress");
//
//
//						if(serial.equalsIgnoreCase(inwardedSerial) && mac.equalsIgnoreCase(inwardedMac)) {
//
//							System.out.println("inwardedSerial = " + inwardedSerial);
//							System.out.println("inwardedMac = " + inwardedMac);
//
//							macMappingJsonObject.put("outwardId", outwardId);
//							//macMappingJsonObject.put("serialNumber", serial);
//							//macMappingJsonObject.put("macAddress", JSONObject.NULL);
//							if (mac != null) {
//								//macMappingJsonObject.put("macAddress", mac);
//							}
//							list.add(macMappingJsonObject);
//							break;
//						}
//					}
//				}
//			} else {
//				String message = "Available products are less than provided mac/serial lists";
//				ProductUtility.stopExecution(logFileName, logModuleName, message, "OUT-"+outwardId);
//				list = null;
//			}
//		}
//        System.out.println(list);
//		return list;
//	}

//public List<JSONObject> getItemForOutward(String productName, String warehouseName, int outwardId,
//                                          List<String> macSerialList) throws Exception {
//
//    CommonGetAPI CommonGetAPI = new CommonGetAPI();
//
//    int productId = CommonGetAPI.getProductId(productName);
//    int warehouseId = CommonGetAPI.getWarehouseId(warehouseName);
//    String apiFind = "?productId=" + productId + "&ownerId=" + warehouseId + "&ownerType=Warehouse";
//
//    String apiURL = "SavbillInventoryManagement/outwards/getItemForOutward" + apiFind;
//    apiURL = getAPIURL(apiURL);
//
//    int page = 1;
//    int pageSize = 5000;
//
//    // Will store ALL pages here
//    List<JSONObject> allItemsList = new ArrayList<JSONObject>();
//
//    // ---------------- PAGINATION LOOP ----------------
//    while (true) {
//
//        String apiBody = getJsonGetItemPagination(page, pageSize);
//        JSONObject jsonResponse = httpPost(apiURL, apiBody);
//
//        int status = jsonResponse.getInt("responseCode");
//        if (status != 200) {
//            break;
//        }
//
//        JSONArray dataList = jsonResponse.getJSONArray("dataList");
//
//        for (int i = 0; i < dataList.length(); i++) {
//            allItemsList.add(dataList.getJSONObject(i));
//        }
//
//        // If records returned < page size ⇒ no more pages
//        if (dataList.length() < pageSize) {
//            break;
//        }
//
//        page++;
//    }
//
//    System.out.println("Total Items Fetched: " + allItemsList.size());
//
//    // ------------ SORT ASCENDING BY serialNumber (Java 8 friendly) ------------
//    Collections.sort(allItemsList, new Comparator<JSONObject>() {
//        @Override
//        public int compare(JSONObject o1, JSONObject o2) {
//            return o1.getString("serialNumber").compareTo(o2.getString("serialNumber"));
//        }
//    });
//    // ---------------------------------------------------------------------------
//
//    List<JSONObject> outputList = new ArrayList<JSONObject>();
//
//    // Your original matching logic
//    if (macSerialList.size() <= allItemsList.size()) {
//
//        for (int i = 0; i < macSerialList.size(); i++) {
//
//            String serial = macSerialList.get(i);
//            String mac = null;
//
//            if (serial.contains("#")) {
//                String[] macSerial = serial.split("#");
//                serial = macSerial[0];
//                mac = macSerial[1];
//            }
//
//            // Loop sorted items
//            for (int j = 0; j < allItemsList.size(); j++) {
//
//                JSONObject obj = allItemsList.get(j);
//
//                String inwardedSerial = obj.getString("serialNumber");
//                String inwardedMac = obj.getString("macAddress");
//
//                if (serial.equalsIgnoreCase(inwardedSerial) &&
//                        mac.equalsIgnoreCase(inwardedMac)) {
//
//                    System.out.println("MATCH serial = " + inwardedSerial +
//                            ", mac = " + inwardedMac);
//
//                    obj.put("outwardId", outwardId);
//                    outputList.add(obj);
//                    break;
//                }
//            }
//        }
//
//    } else {
//        String message = "Available products are less than provided mac/serial lists";
//        ProductUtility.stopExecution(logFileName, logModuleName, message, "OUT-" + outwardId);
//        return null;
//    }
//
//    System.out.println("Total Matched Items: " + outputList.size());
//    return outputList;
//}


// Cache map: key is combination of productId + warehouseId, value is allItemsList
private static Map<String, List<JSONObject>> itemsCache = new HashMap<>();

    public List<JSONObject> getItemForOutward(String productName, String warehouseName, int outwardId,
                                              List<String> macSerialList) throws Exception {

        CommonGetAPI commonGetAPI = new CommonGetAPI();

        int productId = commonGetAPI.getProductId(productName);
        int warehouseId = commonGetAPI.getWarehouseId(warehouseName);

        String cacheKey = productId + "-" + warehouseId;

        List<JSONObject> allItemsList;

        // Check if data is already cached
        if (itemsCache.containsKey(cacheKey)) {
            allItemsList = itemsCache.get(cacheKey);
            System.out.println("Using cached data for productId=" + productId + ", warehouseId=" + warehouseId);
        } else {
            String apiFind = "?productId=" + productId + "&ownerId=" + warehouseId + "&ownerType=Warehouse";
            String apiURL = "SavbillInventoryManagement/outwards/getItemForOutward" + apiFind;
            apiURL = getAPIURL(apiURL);

            int page = 1;
            int pageSize = 5000;

            allItemsList = new ArrayList<>();

            // ---------------- PAGINATION LOOP ----------------
            while (true) {
                String apiBody = getJsonGetItemPagination(page, pageSize);
                JSONObject jsonResponse = httpPost(apiURL, apiBody);

                int status = jsonResponse.getInt("responseCode");
                if (status != 200) break;

                JSONArray dataList = jsonResponse.getJSONArray("dataList");
                for (int i = 0; i < dataList.length(); i++) {
                    allItemsList.add(dataList.getJSONObject(i));
                }

                if (dataList.length() < pageSize) break;
                page++;
            }

            System.out.println("Total Items Fetched: " + allItemsList.size());

            // ------------ SORT ASCENDING BY serialNumber (Java 8 friendly) ------------
            allItemsList.sort(Comparator.comparing(o -> o.getString("serialNumber")));
            // ---------------------------------------------------------------------------

            // Store in cache
            itemsCache.put(cacheKey, allItemsList);
        }

        // Build a lookup map for fast matching (serial#mac -> JSONObject)
        Map<String, JSONObject> lookup = allItemsList.stream()
                .collect(Collectors.toMap(
                        obj -> obj.getString("serialNumber").toLowerCase() + "#" + obj.getString("macAddress").toLowerCase(),
                        obj -> obj,
                        (existing, replacement) -> existing // in case of duplicate keys
                ));

        List<JSONObject> outputList = macSerialList.stream()
                .map(serial -> {
                    String originalSerial = serial;
                    String mac = null;

                    if (serial.contains("#")) {
                        String[] parts = serial.split("#");
                        serial = parts[0];
                        mac = parts[1];
                    }

                    String key = serial.toLowerCase() + "#" + (mac != null ? mac.toLowerCase() : "null");
                    JSONObject matched = lookup.get(key);

                    if (matched != null) {
                        JSONObject objCopy = new JSONObject(matched.toString()); // make copy to avoid mutation issues
                        objCopy.put("outwardId", outwardId);
                        System.out.println("MATCH serial = " + serial + ", mac = " + mac);
                        return objCopy;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (outputList.size() < macSerialList.size()) {
            String message = "Available products are less than provided mac/serial lists";
            ProductUtility.stopExecution(logFileName, logModuleName, message, "OUT-" + outwardId);
            return null;
        }

        System.out.println("Total Matched Items: " + outputList.size());
        return outputList;
    }



	public String updateMacMapping(String outwardNumber, String productName, String warehouseName,int outwardId, List<String> macSerialList) {

		String jsonString = null;

		try {
			String apiURL = getAPIURL("SavbillInventoryManagement/inoutWardMacMapping/updateMACMappingList");
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
			
			// Initializing payload or API body
			String apiBody = getItemForOutward(productName, warehouseName,outwardId, macSerialList).toString();
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);
			
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");

			if (status == 200) {

				String message = "New MAC/Serials are added in outward successfully - OUT-" + outwardNumber;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else {
				String error = JSONResponseBody.getString("responseMessage") + " - " + outwardNumber;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Error", error);
			}

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}
	
	private List<JSONObject> getAllMACListByInwardId_OLD(int inwardId, int outwardId, List<String> macSerialList) {

		String apiURL = "inoutWardMacMapping/getbyinwardid?id=" + inwardId;
		apiURL = getAPIURL(apiURL);

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		List<JSONObject> list = new ArrayList<JSONObject>();

		if (status == 200) {

			for (int i = 0; i < macSerialList.size(); i++) {

				String serial = macSerialList.get(i);
				String mac = null;
				if (serial.contains("#")) {
					String macSerial[] = serial.split("#");
					serial = macSerial[0];
					mac = macSerial[1];
				}

				JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
				// org.json.simple.JSONArray jsonArray = jsonResponse.getJSONArray("dataList");

				for (int j = 0; j < jsonArray.length(); j++) {

					String receivedSerial = jsonArray.getJSONObject(j).getString("serialNumber");

					if (receivedSerial.equalsIgnoreCase(serial)) {

						String receivedMAC = jsonArray.getJSONObject(j).getString("macAddress");
						if (!receivedMAC.equals("null")) {
							if (receivedMAC.equalsIgnoreCase(mac)) {

								JSONObject macMappingJsonObject = new JSONObject();

								macMappingJsonObject = jsonArray.getJSONObject(j);
								macMappingJsonObject.put("outwardId", outwardId);
								list.add(macMappingJsonObject);
								break;
							}
						} else {
							JSONObject macMappingJsonObject = new JSONObject();

							macMappingJsonObject = jsonArray.getJSONObject(j);
							macMappingJsonObject.put("outwardId", outwardId);
							list.add(macMappingJsonObject);
							break;
						}
					}
				}
			}
		}

		return list;
	}

	public String updateMacMapping_OLD(int inwardId, int outwardId, List<String> macSerialList) {

		String jsonString = null;

		try {

			String apiURL = getAPIURL("inoutWardMacMapping/updateMACMappingList");
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			// Initializing payload or API body
			String apiBody = getAllMACListByInwardId_OLD(inwardId, outwardId, macSerialList).toString();
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");

			if (status == 200) {

				String message = "New MAC/Serials are added in outward successfully - OUT-" + outwardId;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else {
				String error = JSONResponseBody.getString("responseMessage") + " - " + outwardId;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Error", error);
			}

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private static String macSerialGenerate(int no,String serialInit) {
		
		String result = null;
		String mac = "";
		String serial = "";
		for(int i=0;i<no;i++) {
			mac =  mac  + "," + Utility.getRandomMacAddress();
			serial = serial + "," + Utility.getRandomSerialNumber(serialInit, 4);
		}
		
		mac = mac.substring(1);
		serial = serial.substring(1);
		
		result = serial +"##"+mac;
		
		return result;
	}
	
	public static String generateSequencialSerialNumber(String prefix,int qty) {
		
		DBOperations dbo = new DBOperations();
		String query = "select id from status where entitytype='outward' and name='"+prefix+"'";
		String ans = dbo.getSingleData(query);
		int start = 1;boolean update=false;
		if(ans!=null) {
			start= Integer.parseInt(ans) + 1;
			update=true;
		}
		
		String result = null;
		String serial = "";
		int lastNumber = 0;
		int end = start + qty;
		for(int i=start;i<end;i++) {
			String temp = prefix +"_"+ i;
			serial = serial + "," + temp;
			lastNumber = i;
		}
		
		result = serial.substring(1);		
		
		String type = "outward";
		String name = prefix;
		int id = lastNumber;
		int status = 1;
		
		if(update) {
			dbo.updateAPIData(type, name, id, status);
		}else {
			dbo.setAPIData(type, name, id, status);
		}
			
		return result;
	}
	
}
