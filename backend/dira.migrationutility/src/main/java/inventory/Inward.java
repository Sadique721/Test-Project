package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Utility;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Inward extends RestExecution {

	private static String logFileName = "inventory.log";
	private static String logModuleName = "CreateInward";

	private void createInward(Map<String, String> inwardDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/inwards/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getInwardJson(inwardDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		StopWatch sw = new StopWatch();
		sw.start();
		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("responseCode");

		if (status == 200) {

			String inwardNumber = JSONResponseBody.getJSONObject("data").getString("inwardNumber");

			String message = "New Inward is added successfully - " + inwardNumber + " | " + sw.getTime();
			System.out.println(message);
			Utility.printLog("execution.log", logModuleName, "Success", message);

			int inwardId = JSONResponseBody.getJSONObject("data").getInt("id");
			String product = inwardDetails.get("Product");
			int inTransitQty = Integer.parseInt(inwardDetails.get("QuantityIn"));
			String macList = inwardDetails.get("InwardMAC");
			String serialList = inwardDetails.get("InwardSerialNumber");
			sw.reset();
			sw.start();
			inwardMacMappingNew(inwardId, inTransitQty,macList,serialList);
			message = "New MAC/Serial addming Time - " + inwardNumber + " | " + sw.getTime();

			sw.reset();
			sw.start();
			inwardApproval(inwardId, product);
			message = "New Inward approval Time - " + inwardNumber + " | " + sw.getTime();
			System.out.println(message);
		} else if (status == 406) {
			String error = JSONResponseBody.getString("responseMessage") + " - " + inwardDetails.get("Product");
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "Already Exist", error);
		}
	}

	public void createInward(List<Map<String, String>> inwardMapList) {

		for (int i = 0; i < inwardMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = inwardMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createInward(map);
		}
	}

//    public void createInward(List<Map<String, String>> inwardMapList) {
//
//        // Create a thread pool, size can be tuned based on system
//        ExecutorService executor = Executors.newFixedThreadPool(1); // for example, 5 threads
//
//        for (Map<String, String> map : inwardMapList) {
//            // submit each map processing as a separate task
//            executor.submit(() -> {
//                try {
//                    Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//                    createInward(map); // call your original method
//                } catch (Exception e) {
//                    Utility.printLog(logFileName, logModuleName, "Error processing map", e.getMessage());
//                }
//            });
//        }

        // Shutdown executor gracefully
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//            try {
//                Thread.sleep(100); // wait for all tasks to finish
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

	public List<Map<String, String>> readUniqueInwardList() {

		String sheetName = "Inward";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);
		Utility.printLog(logFileName, logModuleName, "Sheet Data", sheetMap.toString());

		String sheetName1 = "Inward_MAC_Serial_Mapping";
		List<Map<String, String>> macSerialMapping = new ArrayList<Map<String, String>>();
		macSerialMapping = readData.getInventoryDataSheet(sheetName1);
		// Utility.printLog(logFileName, logModuleName, "Sheet Data",
		// macSerialMapping.toString());

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> inwardMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String product = cellValue.get("Product *");
			if ((!"".equals(product)) && (product != null)) {

				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("Inward_MapID", safeTrim(cellValue.get("Inward_ID")));
				valuemap.put("Product", safeTrim(cellValue.get("Product *")));
				valuemap.put("Warehouse", safeTrim(cellValue.get("Warehouse *")));
				valuemap.put("QuantityIn", safeTrim(cellValue.get("QuantityIn *")));
				valuemap.put("InwardDate", safeTrim(cellValue.get("InwardDate *")));
				
				String parameter1 = cellValue.get("Parameter1");
				if ((!"".equals(parameter1)) && (parameter1 != null)) {
				valuemap.put("Parameter1", safeTrim(cellValue.get("Parameter1")));
				valuemap.put("Value1", safeTrim(cellValue.get("Value1")));
				}
				
				String parameter2 = cellValue.get("Parameter2");
				if ((!"".equals(parameter2)) && (parameter2 != null)) {
				valuemap.put("Parameter2", safeTrim(cellValue.get("Parameter2")));
				valuemap.put("Value2", safeTrim(cellValue.get("Value2")));
				}
				
				String parameter3 = cellValue.get("Parameter3");
				if ((!"".equals(parameter3)) && (parameter3 != null)) {
				valuemap.put("Parameter3", safeTrim(cellValue.get("Parameter3")));
				valuemap.put("Value3", safeTrim(cellValue.get("Value3")));
				}
				
				String parameter4 = cellValue.get("Parameter4");
				if ((!"".equals(parameter4)) && (parameter4 != null)) {
				valuemap.put("Parameter4", safeTrim(cellValue.get("Parameter4")));
				valuemap.put("Value4", safeTrim(cellValue.get("Value4")));
				}
				
				String parameter5 = cellValue.get("Parameter5");
				if ((!"".equals(parameter5)) && (parameter5 != null)) {
				valuemap.put("Parameter5", safeTrim(cellValue.get("Parameter5")));
				valuemap.put("Value5", safeTrim(cellValue.get("Value5")));
				}
				
				String parameter6 = cellValue.get("Parameter6");
				if ((!"".equals(parameter6)) && (parameter6 != null)) {
				valuemap.put("Parameter6", safeTrim(cellValue.get("Parameter6")));
				valuemap.put("Value6", safeTrim(cellValue.get("Value6")));
				}

				String createInwardId = safeTrim(cellValue.get("Inward_ID"));
				String macList = "";
				String serialList = "";

                for (int j = 0; j < macSerialMapping.size(); j++) {
                    Map<String, String> map = macSerialMapping.get(j);

                    String inwardId = map.get("Inward_ID");
                    if (createInwardId.equalsIgnoreCase(inwardId)) {
                        macList = macList + map.get("InwardMAC") + ",";
                        serialList = serialList + map.get("InwardSerialNumber") + ",";
                    }
                }

                if (!macList.isEmpty()) {
                    macList = macList.substring(0, macList.length() - 1);
                }
                if (!serialList.isEmpty()) {
                    serialList = serialList.substring(0, serialList.length() - 1);
                }

                valuemap.put("InwardMAC", macList);
                valuemap.put("InwardSerialNumber", serialList);
                valuemap.put("Type", cellValue.get("Type *"));
                valuemap.put("Status", cellValue.get("Status *"));
                valuemap.put("Description", cellValue.get("Description *"));


                inwardMapList.add(valuemap);

			}
		}
//        System.out.println(inwardMapList);
		return inwardMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getInwardJson(Map<String, String> inwardDetails) {
        String jsonString = null;

        try {
            JSONObject inwardJson = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            // 1️⃣ Core fields
            inwardJson.put("id", JSONObject.NULL);

            int productId = commonGetAPI.getProductId(inwardDetails.get("Product"));
            inwardJson.put("productId", productId != 0 ? productId : JSONObject.NULL);

            inwardJson.put("qty", JSONObject.NULL);

            // 2️⃣ Date fields
            String inwardDate = inwardDetails.get("InwardDate");
            if (inwardDate != null && !inwardDate.isEmpty()) {
                inwardDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(
                        inwardDate, "dd-MMM-yyyy", "yyyy-MM-dd'T'HH:mm:ss");
                inwardJson.put("inwardDateTime", inwardDate);
            } else {
                inwardJson.put("inwardDateTime", JSONObject.NULL);
            }

//            // 2️⃣ Date fields
//            String inwardDate = inwardDetails.get("InwardDate");
//            if (inwardDate != null && !inwardDate.isEmpty()) {
//                // Use your convertDate method
//                inwardDate = convertDate(inwardDate);
//                inwardJson.put("inwardDateTime", inwardDate);
//            } else {
//                inwardJson.put("inwardDateTime", JSONObject.NULL);
//            }


            inwardJson.put("startDateTime", JSONObject.NULL);
            inwardJson.put("expiryDateTime", JSONObject.NULL);

            // 3️⃣ Destination info
            int warehouseId = commonGetAPI.getWarehouseId(inwardDetails.get("Warehouse"));
            inwardJson.put("destinationId", warehouseId);
            inwardJson.put("destinationType", "Warehouse");

            // 4️⃣ Type & Description
            inwardJson.put("type", inwardDetails.getOrDefault("Type", "New"));
            inwardJson.put("description", inwardDetails.getOrDefault("Description", ""));

            // 5️⃣ Status
            inwardJson.put("status", inwardDetails.getOrDefault("Status", "ACTIVE").toUpperCase());

            // 6️⃣ Other fields
            inwardJson.put("inwardNumber", "");
            inwardJson.put("inTransitQty", Integer.parseInt(inwardDetails.getOrDefault("QuantityIn", "0")));
            inwardJson.put("mvnoId", JSONObject.NULL);
            inwardJson.put("usedQty", JSONObject.NULL);
            inwardJson.put("unusedQty", JSONObject.NULL);
            inwardJson.put("outTransitQty", JSONObject.NULL);
            inwardJson.put("rejectedQty", JSONObject.NULL);
            inwardJson.put("totalMacSerial", JSONObject.NULL);

            // 7️⃣ Specification parameters
            List<JSONObject> specificationParametersDTOList = new ArrayList<>();

            String[] params = {"Parameter1", "Parameter2", "Parameter3", "Parameter4", "Parameter5", "Parameter6"};
            Map<String, String> paramNameValueMap = new HashMap<>();

            for (int i = 0; i < params.length; i++) {
                String paramName = inwardDetails.get(params[i]);
                String paramValue = inwardDetails.get("Value" + (i + 1));
                if (paramName != null && !paramName.isEmpty()) {
                    paramNameValueMap.put(paramName, paramValue);
                }
            }

            if (!paramNameValueMap.isEmpty()) {
                JSONArray extractedDataArray = getSpecificParametersByid(inwardDetails.get("Product"));

                for (int i = 0; i < extractedDataArray.length(); i++) {
                    JSONObject extractedData = extractedDataArray.getJSONObject(i);
                    JSONObject specJson = new JSONObject();

                    String paramName = extractedData.optString("paramName", "");
                    String paramValue = paramNameValueMap.getOrDefault(paramName, null);

                    specJson.put("id", extractedData.optInt("id", -1));
                    specJson.put("identityKey", extractedData.optInt("identityKey", -1));
                    specJson.put("mvnoId", extractedData.optInt("mvnoId", -1));
                    specJson.put("paramName", paramName);
                    specJson.put("paramValue", paramValue);
                    specJson.put("defaultValue", extractedData.optString("defaultValue", null));
                    specJson.put("isMandatory", extractedData.optBoolean("isMandatory", false));
                    specJson.put("isMultiValueParam", extractedData.optBoolean("isMultiValueParam", false));
                    specJson.put("pcid", JSONObject.NULL);

                    specificationParametersDTOList.add(specJson);
                }
            }

            inwardJson.put("specificationParametersDTOList", specificationParametersDTOList);
            jsonString = inwardJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }

//        System.out.println(jsonString);
        return jsonString;
    }


//    private String getInwardJson(Map<String, String> inwardDetails) {
//
//		String jsonString = null;
//
//		try {
//
//			JSONObject inwardJson = new JSONObject();
//
//
//			CommonGetAPI commonGetAPI = new CommonGetAPI();
//			int productId = commonGetAPI.getProductId(inwardDetails.get("Product"));
//			if (productId != 0) {
//				inwardJson.put("productId", productId);
//			}
//
//			int warehouseId = commonGetAPI.getWarehouseId(inwardDetails.get("Warehouse"));
//			inwardJson.put("destinationId", warehouseId);
//			inwardJson.put("destinationType", "Warehouse");
//
//			int qtyInward = Integer.parseInt(inwardDetails.get("QuantityIn"));
//			if (qtyInward > 0) {
//				inwardJson.put("inTransitQty", qtyInward);
//			}
//
//			String inwardDate = inwardDetails.get("InwardDate");
//			if (!"".equals(inwardDate)) {
//				inwardDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(inwardDate, "dd-MMM-yyyy",
//						"yyyy-MM-dd'T'HH:mm:ss");
//				inwardJson.put("inwardDateTime", inwardDate);
//			}
//
//			inwardJson.put("type", inwardDetails.get("Type"));
//			inwardJson.put("status", inwardDetails.get("Status").toUpperCase());
//			inwardJson.put("description", inwardDetails.get("Description"));
//
//			inwardJson.put("inwardNumber", "");
//			inwardJson.put("id", JSONObject.NULL);
//			inwardJson.put("qty", JSONObject.NULL);
//			inwardJson.put("usedQty", JSONObject.NULL);
//			inwardJson.put("unusedQty", JSONObject.NULL);
//			inwardJson.put("outTransitQty", JSONObject.NULL);
//			inwardJson.put("rejectedQty", JSONObject.NULL);
//			inwardJson.put("totalMacSerial", JSONObject.NULL);
//
//			inwardJson.put("mvnoId", JSONObject.NULL);
//
//
//
//
//
//
//			// added by sarfraz till from
//			List<JSONObject> specificationParametersDTOList = new ArrayList<JSONObject>();
//
//			String parameter1=inwardDetails.get("Parameter1");
//			String parameter2=inwardDetails.get("Parameter2");
//			String parameter3=inwardDetails.get("Parameter3");
//			String parameter4=inwardDetails.get("Parameter4");
//			String parameter5=inwardDetails.get("Parameter5");
//			String parameter6=inwardDetails.get("Parameter6");
//
//			if((parameter1 != null && !parameter1.isEmpty() || parameter2 != null && !parameter2.isEmpty()) || (parameter3 != null && !parameter3.isEmpty())  || parameter4 != null && !parameter4.isEmpty() || parameter5 != null && !parameter5.isEmpty() || parameter6 != null && !parameter6.isEmpty() ) {
//
//			JSONArray extractedDataArray = getSpecificParametersByid(inwardDetails.get("Product"));
//
//
//			// Define a map or logic to assign paramValue based on paramName
//			Map<String, String> paramNameValueMap = new HashMap<>();
//			 // Check for null and non-empty parameters before adding to the map
//		    if (parameter1 != null && !parameter1.isEmpty()) {
//		        paramNameValueMap.put(parameter1, inwardDetails.get("Value1"));
//		    }
//		    if (parameter2 != null && !parameter2.isEmpty()) {
//		        paramNameValueMap.put(parameter2, inwardDetails.get("Value2"));
//		    }
//		    if (parameter3 != null && !parameter3.isEmpty()) {
//		        paramNameValueMap.put(parameter3, inwardDetails.get("Value3"));
//		    }
//		    if (parameter4 != null && !parameter4.isEmpty()) {
//		        paramNameValueMap.put(parameter4, inwardDetails.get("Value4"));
//		    }
//		    if (parameter5 != null && !parameter5.isEmpty()) {
//		        paramNameValueMap.put(parameter5, inwardDetails.get("Value5"));
//		    }
//		    if (parameter6 != null && !parameter6.isEmpty()) {
//		        paramNameValueMap.put(parameter6, inwardDetails.get("Value6"));
//		    }
//
//			// Loop through all extracted data items
//			for (int i = 0; i < extractedDataArray.length(); i++) {
//			    JSONObject inwardsecfication = new JSONObject();
//
//			    // Extract data from the JSONArray element
//			    JSONObject extractedData = extractedDataArray.getJSONObject(i);
//
//			    int id = extractedData.optInt("id", -1);
//			    int keyId = extractedData.optInt("identityKey", -1);
//			    int mvnoid = extractedData.optInt("mvnoId", -1);
//			    String paramName = extractedData.optString("paramName", "");
//			    String defaultValue = extractedData.optString("defaultValue", "");
//			    boolean isMandatory = extractedData.optBoolean("isMandatory", false);
//			    boolean isMultiValueParam = extractedData.optBoolean("isMultiValueParam", false);
//
//			    // Check if the paramName exists in the map and set the paramValue accordingly
//			    String paramValue = paramNameValueMap.getOrDefault(paramName, null);  // Default value is null if paramName not found in the map
//
//			    // Populate inwardsecfication JSONObject for each entry
//			    inwardsecfication.put("defaultValue", defaultValue);
//			    inwardsecfication.put("id", id);
//			    inwardsecfication.put("identityKey", keyId);
//			    inwardsecfication.put("isMandatory", isMandatory);
//			    inwardsecfication.put("isMultiValueParam", isMultiValueParam);
//			    inwardsecfication.put("mvnoId", mvnoid);
//			    inwardsecfication.put("paramValue", paramValue);  // Set the paramValue based on paramName
//			    inwardsecfication.put("paramName", paramName);
//			    inwardsecfication.put("pcid", JSONObject.NULL);
//
//			    // Add each populated object to the list
//			    specificationParametersDTOList.add(inwardsecfication);
//			}
//			}
//			// After loop, add the list to the final inwardJson
//			inwardJson.put("specificationParametersDTOList", specificationParametersDTOList);
//
//			// Till Here --------------->
//			jsonString = inwardJson.toString();
//
//		} catch (Exception e) {
//			jsonString = null;
//			e.printStackTrace();
//		}
//        System.out.println(jsonString);
//
//		return jsonString;
//	}

	private String inwardApproval(int inwardNumber, String productName) {

		String jsonString = null;

		try {

			JSONObject inwardApprovalJson = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			if (inwardNumber != 0) {

				inwardApprovalJson.put("id", inwardNumber);
				inwardApprovalJson.put("approvalStatus", "Approve");
				inwardApprovalJson.put("approvalRemark", "Approved by migration");
				// inwardApprovalJson.put("productId",
				// getProductIdAndProductCategoryIdJSON(productName));
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

					String inwardNumber1 = JSONResponseBody.getJSONObject("data").getString("inwardNumber");
					String message = "New Inward is approved successfully - " + inwardNumber1;

					System.out.println(message);
					Utility.printLog("execution.log", logModuleName, "Success", message);

				} else {
					String error = JSONResponseBody.getString("responseMessage") + " - IN-" + inwardNumber;
					System.out.println(error);
					Utility.printLog("execution.log", logModuleName, "Inward Approval", error);
				}
			}
		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//        System.out.println(jsonString);
		return jsonString;
	}

	public JSONObject getProductIdAndProductCategoryIdJSON(String productName) {

		String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllActiveProduct");

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		JSONObject productIdJson = null;

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedProductName = jsonArray.getJSONObject(i).getString("name");
				if (productName.equalsIgnoreCase(receivedProductName)) {
					productIdJson = jsonArray.getJSONObject(i);
					break;
				}
			}
		}
		
		

		if (productIdJson == null) {
			System.out.println("Product details not found - " + productName);
			Utility.printLog(logFileName, logModuleName, "Product details not found - ", productName);
		}

		return productIdJson;
	}

	private String inwardMacMappingTemp123(int inwardNumber, Map<String, String> macDetails) {

		String jsonString = null;

		try {

			JSONObject macMappingJsonObject = new JSONObject();

			String tempMAC = macDetails.get("InwardMAC");
			String tempSerialNumber = macDetails.get("InwardSerialNumber");

			if ((!"".equals(tempMAC)) && (!"".equals(tempSerialNumber))) {

				String macList[] = tempMAC.split(",");
				String snList[] = tempSerialNumber.split(",");

				for (int i = 0; i < macList.length; i++) {
					// String mac = macList[i];
					// String serial = snList[i];

					String mac = Utility.getRandomMacAddress();
					String serial = Utility.getRandomSerialNumber("A", 4);

					macMappingJsonObject.put("macAddress", mac);
					macMappingJsonObject.put("serialNumber", serial);
					macMappingJsonObject.put("id", JSONObject.NULL);
					macMappingJsonObject.put("inwardId", inwardNumber);
					macMappingJsonObject.put("status", macDetails.get("Status").toUpperCase());

					jsonString = macMappingJsonObject.toString();

					String apiURL = getAPIURL("SavbillInventoryManagement/inoutWardMacMapping/save");
					Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

					// Initializing payload or API body
					String apiBody = jsonString;
					Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

					JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
					String response = JSONResponseBody.toString(4);
					Utility.printLog(logFileName, logModuleName, "Response", response);

					int status = JSONResponseBody.getInt("responseCode");

					if (status == 200) {

						String message = "New MAC/Serial is added successfully - " + mac + "/" + serial;
						System.out.println(message);
						Utility.printLog("execution.log", logModuleName, "Success", message);

					} else if (status == 406) {
						String error = JSONResponseBody.getString("responseMessage") + " - " + mac + "/" + serial;
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "Already Exist", error);
					}
				}
			}

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private String inwardMacMappingNew(int inwardNumber,int qty, String macList, String serialList) {

		String jsonString = null;

		try {

			List<JSONObject> macSerialListDTOList = new ArrayList<JSONObject>();
			JSONObject macMappingJsonObject = new JSONObject();
			
			String mac[] = macList.split(",");
			String serial[] = serialList.split(",");
			
//			for (int i = 0; i < qty; i++) {
//
//				JSONObject macSerialJson = new JSONObject();
//
//				//String mac = Utility.getRandomMacAddress();
//				//String serial = Utility.getRandomSerialNumber("A", 4);
//
//				macSerialJson.put("macAddress", mac[i]);
//				macSerialJson.put("serialNumber", serial[i]);
//				macSerialListDTOList.add(macSerialJson);
//
//			}

            int count = Math.min(qty, Math.min(mac.length, serial.length));

            if (qty != mac.length || qty != serial.length) {
                Utility.printLog(logFileName, logModuleName, "Warning",
                        "Mismatch between Quantity (" + qty + "), MAC count (" + mac.length + "), and Serial count (" + serial.length + ") for inwardId: " + inwardNumber);
            }

            for (int i = 0; i < count; i++) {
                JSONObject macSerialJson = new JSONObject();
                macSerialJson.put("macAddress", mac[i].trim());
                macSerialJson.put("serialNumber", serial[i].trim());
                macSerialListDTOList.add(macSerialJson);
            }


            macMappingJsonObject.put("inwardId", inwardNumber);
			macMappingJsonObject.put("macSerialListDTOList", macSerialListDTOList);
			
			jsonString = macMappingJsonObject.toString();

			String apiURL = getAPIURL("SavbillInventoryManagement/inwards/saveManualMacSerial");
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			// Initializing payload or API body
			String apiBody = jsonString;
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			int status = JSONResponseBody.getInt("responseCode");

			if (status == 200) {

				String message = "New MAC/Serials are added successfully";
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);

			} else if (status == 406) {
				String error = JSONResponseBody.getString("responseMessage");
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Already Exist", error);
			}

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//        System.out.println(jsonString);
		return jsonString;
	}
	
	
	public JSONArray getSpecificParametersByid(String productName) {
	    // Assuming CommonGetAPI and httpGet methods are defined elsewhere

	    CommonGetAPI commonGetAPI = new CommonGetAPI();
	    int productId = commonGetAPI.getProductId(productName);

	    String apiURL = getAPIURL("SavbillInventoryManagement/specificationParameters/getSpecificParametersByid?product_id=" + productId);
	    JSONObject jsonResponse = httpGet(apiURL);

	    int status = jsonResponse.getInt("responseCode");
	    JSONArray extractedDataArray = new JSONArray(); // To store all the extracted data for each product

	    if (status == 200) {
	        JSONArray jsonArray = jsonResponse.getJSONArray("dataList");

	        // Loop through the dataList to process all products
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject productJson = jsonArray.getJSONObject(i);
	            
	            // Extracting all relevant details for each product
	            int id = productJson.optInt("id", -1);
	            int keyId = productJson.optInt("identityKey", -1);
	            int mvnoId = productJson.optInt("mvnoId", -1); // Default to -1 if not found
	            String paramName = productJson.optString("paramName", "");
	            String defaultValue = productJson.optString("defaultValue", "");
	            boolean isMandatory = productJson.optBoolean("isMandatory", false);
	            boolean isMultiValueParam = productJson.optBoolean("isMultiValueParam", false);

	            // Create a new JSONObject for each entry in the dataList
	            JSONObject extractedData = new JSONObject();
	            extractedData.put("id", id);
	            extractedData.put("paramName", paramName);
	            extractedData.put("defaultValue", defaultValue);
	            extractedData.put("isMandatory", isMandatory);
	            extractedData.put("isMultiValueParam", isMultiValueParam);
	            extractedData.put("mvnoId", mvnoId);
	            extractedData.put("identityKey", keyId);

	            // Add the extracted data for this product to the result array
	            extractedDataArray.put(extractedData);
	            System.out.println(extractedData);
	            
	        }
	    }

	    // Return the array containing all the extracted data objects
	    return extractedDataArray;
	}



    private String convertDate(String date) {

        DateTimeFormatter outFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        DateTimeFormatter[] inFormats = {
                DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
        };

        for (DateTimeFormatter formatter : inFormats) {
            try {
                return LocalDateTime.parse(date, formatter).format(outFormat);
            } catch (Exception ignored) {
            }
        }

        // fallback
        return "2025-06-12 00:00:00";
    }



}
