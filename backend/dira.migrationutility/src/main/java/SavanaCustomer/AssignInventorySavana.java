package SavanaCustomer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import commons.CommonAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class AssignInventorySavana extends RestExecution {


    private static String logFileName = "AssignInventorySavanna.log";
    private static String logModuleName = "AssignInventory";
    private UpdateSheet updateSheet = new UpdateSheet();
    int thread_size = Constant.THREAD_POOL_SIZE;

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

            JSONObject JSONResponseBody = httpPostAI(apiURL, apiBody);
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
                JSONArray dataList = JSONResponseBody.optJSONArray("dataList");
                int custId = 0;
                if (dataList != null && dataList.length() > 0) {
                    JSONObject firstObj = dataList.getJSONObject(0);
                    custId = firstObj.optInt("customerId", 0);
                }

                String detail = userName + "|" + productName + "|" + macAddress + "|" + serialNumber;
                String message = "An inventory is assigned to customer successfully - " + detail;
                System.out.println(message);
                Utility.printLog("execution.log", logModuleName, "Success", message);

                // Approval of customer inventory is done here.
                CommonGetAPI commonGetAPI = new CommonGetAPI();
                int custInventoryMappingId = commonGetAPI.getAllCustomerInventoryList(itemType, custId, macAddress, serialNumber, productName, qty);
                approveAssignedCustomerInventory(custInventoryMappingId);

            } /*else if (status == 406) {
					String error = JSONResponseBody.getString("responseMessage");
					System.out.println(error + " - " + userName);
				}  */ else if (status == 406) {
                String error = JSONResponseBody.isNull("responseMessage") ? "No response message" : JSONResponseBody.getString("responseMessage");
                System.out.println(error + " - " + userName);
            }

            ProductUtility.printResponse(JSONResponseBody, logModuleName, userName);

            handleResponse(JSONResponseBody, userName, customerDetailsMap.get("RowIndex"));

        }

    }

    private void handleResponse(JSONObject response, String usernameName, String rowIndex) {

        int status = response.optInt("responseCode", -1);
        String message = response.optString("responseMessage", "No message");

        String migrationStatus = "Initial";
        String migrationDetail = "Initial";

        if (status == 200) {
            // SUCCESS
            migrationStatus = "Success";
            migrationDetail = message + " - " + usernameName;

        } else if (status == 406 || status == 417) {
            // ALREADY EXISTS
            migrationStatus = "Already Exists";
            migrationDetail = message + " - " + usernameName;

        } else if (status == 400) {
            // PINCODE or VALIDATION ERROR
            migrationStatus = "Validation Error";
            migrationDetail = message + " - " + usernameName;

        } else {
            // ANY OTHER ERROR
            migrationStatus = "Error";
            migrationDetail = message + " - " + usernameName;
        }

        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" +
                "MigrationDetail::" + migrationDetail;

        updateSheet.setRowList(rowIndex, columnAndValue);

    }

    public void AssignInventoryToCustomer(List<Map<String, String>> customerMapList) {

        CommonGetAPI commonGetAPI = new CommonGetAPI();
//        CommonAPI commonAPI = new CommonAPI();
//        // Pre-calculate staffId
//        String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));
//
//        // 🔥 Step 1: Build product cache BEFORE starting threads
//        Map<String, Map<String, Integer>> historyCache = new HashMap<>();
//
//        for (Map<String, String> row : customerMapList) {
//
//            String product = row.get("Product");
//            int productId = commonGetAPI.getProductId(product);
//
//            String mac = row.get("MAC");
//            String serial = row.get("SerialNumber");
//
//            String key = mac.trim().toLowerCase() + "|" + serial.trim().toLowerCase();
//
//            // Call API only if not already cached
//            if (!historyCache.containsKey(key)) {
//                Map<String, Integer> history =
//                        commonAPI.getItemHistoryByProduct(productId, staffId);
//
//                historyCache.put(key, history);
//            }
//        }
        // Create a fixed thread pool - adjust size based on your CPU and API load
        int threadCount = thread_size; // example
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        updateSheet.setActiveSheetName("AssignCustomerInventory");
        for (Map<String, String> map : customerMapList) {
            executor.submit(() -> {
                try {
                    String userName = map.get("CustomerUsername");
                    String row = map.get("RowIndex");
                    if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
                        Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
                        AssignInventoryToCustomer(map); // your existing single-item method
                    } else {
//                        System.out.println("Customer UserName is NOT found - " + userName);
                        String message = "Customer Usename is Not Found - " + userName;
                        String migrationStatus = "UserName Not Found";
                        String migrationDetail = message;
                        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
                        updateSheet.setRowList(row, columnAndValue);
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown executor and wait for all tasks to finish
        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) { // adjust timeout as needed
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Update Excel after all tasks complete
        rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);

    }


//    public void AssignInventoryToCustomer(List<Map<String, String>> customerMapList) {
//
//        CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//        for (int i = 0; i < customerMapList.size(); i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map = customerMapList.get(i);
//
//            String userName = map.get("CustomerUsername");
//            if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
//                Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//                AssignInventoryToCustomer(map);
//            } else {
//                System.out.println("Customer UserName is NOT found - " + userName);
//            }
//        }
//    }

//    public void AssignInventoryToCustomer(List<Map<String, String>> customerMapList) {
//
//        CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//        // Thread pool (choose size depending on your load)
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//
//        for (Map<String, String> map : customerMapList) {
//
//            executor.submit(() -> {
//                try {
//                    String userName = map.get("CustomerUsername");
//
//                    if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
//                        Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//                        AssignInventoryToCustomer(map); // your existing method
//                    } else {
//                        System.out.println("Customer UserName is NOT found - " + userName);
//                    }
//
//                } catch (Exception e) {
//                    Utility.printLog(logFileName, logModuleName,
//                            "Error processing customer map", e.getMessage());
//                }
//            });
//        }
//
//        // Shutdown and wait for all tasks
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

    public List<Map<String, String>> readAssignInventoryCustomerList() {

        String sheetName = "AssignCustomerInventory";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String userName = cellValue.get("CustomerUsername");
            String mStatus = cellValue.get("MigrationStatus");

            if ((!"Success".equalsIgnoreCase(mStatus)) && (!"Already Exists".equalsIgnoreCase(mStatus))) {
                if (userName != null && !userName.isEmpty()) {
                    valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                    valuemap.put("SubscriberType", safeTrim(cellValue.get("SubscriberType")));
                    valuemap.put("CustomerUsername", safeTrim(cellValue.get("CustomerUsername")));
                    valuemap.put("Service", safeTrim(cellValue.get("Service")));
                    valuemap.put("PlanName", safeTrim(cellValue.get("PlanName")));
                    valuemap.put("ItemType", safeTrim(cellValue.get("ItemType")));
                    valuemap.put("NonSerializedQty", safeTrim(cellValue.get("NonSerializedQty")));
                    valuemap.put("AssemblyType", safeTrim(cellValue.get("AssemblyType")));
                    valuemap.put("AssemblyName", safeTrim(cellValue.get("AssemblyName")));
                    valuemap.put("Product", safeTrim(cellValue.get("Product")));
                    valuemap.put("SerialNumber", safeTrim(cellValue.get("SerialNumber")));
                    valuemap.put("MAC", safeTrim(cellValue.get("MAC")));
                    valuemap.put("AssignDate", safeTrim(cellValue.get("AssignDate")));
                    customerMapList.add(valuemap);
                }else {
                    System.out.println("Username Not Available");
                }
            }
        }
//        System.out.println(customerMapList.toString());
        return customerMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getAssignInventoryJson(Map<String, String> customerDetails) {

        try {
            JSONObject json = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            // ------------------- BASIC DETAIL FETCHING -------------------
            String customerType = customerDetails.get("SubscriberType");
            String customerUsername = customerDetails.get("CustomerUsername");
            int customerId = commonGetAPI.getCustomerId(customerUsername, customerType);

            if (customerId == 0) {
                customerId = commonGetAPI.getCAFCustomerId(customerUsername);
            }

            String planName = customerDetails.get("PlanName");
            String details = commonGetAPI.getPlanByCustService(customerId, planName);
            String[] temp = details.split(":");

            int serviceId = Integer.parseInt(temp[0]);
            String connectionNo = temp[1];
            int custServiceMapId = Integer.parseInt(temp[2]);

            String product = customerDetails.get("Product");
            int productId = commonGetAPI.getProductId(product);

            String itemType = customerDetails.get("ItemType");
            String macAddress = customerDetails.get("MAC");
            String serialNumber = customerDetails.get("SerialNumber");

            String staffId = String.valueOf(commonGetAPI.staffUserNameIdList(Constant.STAFF_USERNAME));

            // ------------------- ITEM HISTORY LOOKUP -------------------
            JSONObject itemHistory = commonGetAPI.getItemHistoryByProduct(productId, staffId, macAddress, serialNumber);

            int itemId = itemHistory.getInt("itemId");
            String condition = itemHistory.getString("condition");
            String mac = itemHistory.getString("mac");
            String serial = itemHistory.getString("serial");

            // Update mapping
            itemHistory.put("macAddress", mac);
            itemHistory.put("serialNumber", serial);
            updateMacMapping(itemHistory);

            // ------------------- PRICE FETCHING -------------------
            String itemPriceStr = commonGetAPI.getSerializedInventoryProductDetails(serviceId, product);
            double tempPrice = 0;
            try {
                tempPrice = Double.parseDouble(itemPriceStr);
            } catch (Exception e) {
            }

            Integer finalPrice = (tempPrice == 0 ? null : (int) tempPrice);

            // ------------------- NOW BUILD JSON EXACTLY LIKE PAYLOAD -------------------
            json.put("id", "");
            json.put("qty", 1);
            json.put("productId", productId);
            json.put("customerId", customerId);
            json.put("serviceId", serviceId);
            json.put("inventoryType", "");
            json.put("staffId", staffId);
            json.put("inwardId", "");

            // assignedDateTime
            String assignDate = customerDetails.get("AssignDate");
            if (assignDate != null && !assignDate.isEmpty()) {
                assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy", "yyyy-MM-dd'T'HH:mm:ss");
                json.put("assignedDateTime", assignDate);
            } else {
                json.put("assignedDateTime", JSONObject.NULL);
            }

            json.put("status", "");
            json.put("paymentOwnerId", Integer.parseInt(staffId));
            json.put("mvnoId", "");
            json.put("externalItemId", "");
            json.put("itemId", itemId);
            json.put("itemAssemblyId", "");
            json.put("itemAssemblyflag", false);
            json.put("itemTypeFlag", itemType);
            json.put("nonSerializedQty", "");
            json.put("connectionNo", connectionNo);
            json.put("isInvoiceToOrg", false);
            json.put("billTo", "CUSTOMER");
            json.put("discount", "");
            json.put("inventoryJobType", "New Installation");
            json.put("nature", "Sales Conversion");

            // offerPrice & newAmount
            if (finalPrice == null) {
                json.put("offerPrice", "");
                json.put("newAmount", "");
            } else {
                json.put("offerPrice", finalPrice);
                json.put("newAmount", finalPrice);
            }

            json.put("chargeId", "");
            json.put("isRequiredApproval", false);
            json.put("isFree", false);
            json.put("itemType", condition);
            json.put("billabecustId", "");
            json.put("parentCustomerId", "");

            // ------------------- inOutWardMACMapping ARRAY -------------------
            JSONArray macArray = new JSONArray();

            JSONObject mapObj = new JSONObject();
            mapObj.put("id", itemHistory.getInt("id"));
            // Assuming itemHistory is a JSONObject
            int inwardId = itemHistory.optInt("inwardId", 0);  // 0 is default if key is missing
            int outwardId = itemHistory.optInt("outwardId", 0);

            mapObj.put("inwardId", inwardId);
            mapObj.put("outwardId", outwardId);

//            mapObj.put("inwardId", itemHistory.opt("inwardId"));
//            mapObj.put("outwardId", itemHistory.opt("outwardId"));
            mapObj.put("status", itemHistory.optString("status"));
            mapObj.put("macAddress", mac);
            mapObj.put("isDeleted", false);
            mapObj.put("custInventoryMappingId", JSONObject.NULL);
            mapObj.put("serialNumber", serial);
            mapObj.put("mvnoId", JSONObject.NULL);
            mapObj.put("currentApproverId", JSONObject.NULL);
            mapObj.put("previousApproverId", JSONObject.NULL);
            mapObj.put("teamHierarchyMappingId", JSONObject.NULL);
            mapObj.put("inwardIdOfOutward", JSONObject.NULL);
            mapObj.put("isForwarded", 0);
            mapObj.put("remark", JSONObject.NULL);
            mapObj.put("externalItemId", JSONObject.NULL);
            mapObj.put("itemId", itemId);
            mapObj.put("inventoryMappingId", JSONObject.NULL);
            mapObj.put("bulkConsumptionId", JSONObject.NULL);
            mapObj.put("itemRemaingDays", JSONObject.NULL);
            mapObj.put("isReturned", 0);
            mapObj.put("nonSerializedItemId", JSONObject.NULL);
            mapObj.put("condition", condition);
            mapObj.put("productName", product);
            mapObj.put("productId", productId);
            mapObj.put("hasMac", true);
            mapObj.put("hasSerial", true);
            mapObj.put("ownerShip", "Staff");
            mapObj.put("inReplacementProcess", JSONObject.NULL);
            mapObj.put("identityKey", itemHistory.getInt("id"));

            macArray.put(mapObj);
            json.put("inOutWardMACMapping", macArray);

            // ------------------- custInvParams ARRAY -------------------
            JSONArray params = new JSONArray();
            JSONObject p = new JSONObject();
            p.put("paramName", product + " SN");
            p.put("paramValue", serialNumber);
            params.put(p);

            json.put("custInvParams", params);

            json.put("itemAssemblyStatus", "Pending");
            json.put("custServiceMapId", custServiceMapId);

            return json.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getAssignNonSerializedInventoryJson(Map<String, String> customerDetails) {

        String jsonString = null;

        try {

            JSONObject customerJson = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            String customerType = customerDetails.get("SubscriberType");
            String customerUsername = customerDetails.get("CustomerUsername");
            int customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
            customerId1 = customerId;
            if (customerId != 0) {
                customerJson.put("customerId", customerId);
            }

            String planName = customerDetails.get("PlanName");
            String details = commonGetAPI.getPlanByCustService(customerId, planName);
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
                assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy", "yyyy-MM-dd'T'HH:mm:ss");
                customerJson.put("assignedDateTime", assignDate);
            }

            customerJson.put("status", JSONObject.NULL);

            String staffId = String.valueOf(commonGetAPI.staffUserNameIdList(Constant.STAFF_USERNAME));

            customerJson.put("billabecustId", "");
            customerJson.put("billTo", "CUSTOMER");
            customerJson.put("chargeId", "");

            customerJson.put("inwardId", JSONObject.NULL);
            customerJson.put("parentCustomerId", JSONObject.NULL);
            customerJson.put("isFree", JSONObject.NULL);
            customerJson.put("isInvoiceToOrg", false);
            customerJson.put("isRequiredApproval", false);
            customerJson.put("nonSerializedItemRemark", "This Non-Serialized inventory is assigned by Migration.");

            customerJson.put("id", JSONObject.NULL);
            customerJson.put("itemAssemblyId", JSONObject.NULL);
            customerJson.put("mvnoId", "");
            customerJson.put("externalItemId", JSONObject.NULL);
            customerJson.put("inventoryJobType", "New Installation");
            customerJson.put("inventoryType", "");
            customerJson.put("inwardId", JSONObject.NULL);
            customerJson.put("paymentOwnerId", staffId);
            customerJson.put("staffId", staffId);
            customerJson.put("itemAssemblyStatus", "Pending");

            String tempNewProductAmount = commonGetAPI.getNonSerializedInventoryProductDetails(product);
            int newProductAmount = Integer.parseInt(tempNewProductAmount);
            customerJson.put("newAmount", newProductAmount);
            customerJson.put("offerPrice", newProductAmount);
            customerJson.put("discount", JSONObject.NULL);

            JSONObject nonTrackableProduct = commonGetAPI.getNonTrackableProductQty(productId, staffId);
            int productId1 = nonTrackableProduct.getInt("productId");
            customerJson.put("itemId", productId1);

            int unusedQty = nonTrackableProduct.getInt("unusedQty");

            String itemType = customerDetails.get("ItemType");
            if (!"".equals(itemType)) {
                if (itemType.equalsIgnoreCase("Serialized Item")) {
                    customerJson.put("qty", 1);
                    customerJson.put("nonSerializedQty", JSONObject.NULL);
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
                        System.out.println("Non Serialized Product provided qty is higher than unusedQty" + customerUsername);
                        Utility.printLog(logFileName, logModuleName, "Non Serialized Product provided qty is higher than unusedQty ", customerUsername);
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
            int customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
            customerId1 = customerId;
            if (customerId != 0) {
                customerJson.put("customerId", customerId);
            }

            String planName = customerDetails.get("PlanName");
            String details = commonGetAPI.getPlanByCustService(customerId, planName);
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
                assignDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(assignDate, "dd-MMM-yyyy", "yyyy-MM-dd'T'HH:mm:ss");
                customerJson.put("assignedDateTime", assignDate);
            }

            customerJson.put("status", "");

            String staffId = String.valueOf(commonGetAPI.staffUserNameIdList(Constant.STAFF_USERNAME));

            customerJson.put("id", JSONObject.NULL);
            customerJson.put("itemAssemblyId", JSONObject.NULL);
            customerJson.put("mvnoId", "");

            customerJson.put("externalItemId", JSONObject.NULL);
            customerJson.put("inventoryJobType", "New Installation");
            customerJson.put("inventoryType", "");
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

            JSONObject itemHistory = commonGetAPI.getItemHistoryByProduct(productId, staffId, macAddress, serial);
            int itemId = itemHistory.getInt("itemId");
            customerJson.put("itemId", itemId);

            // itemHistory.put("serialNumber", serialNumber1);
            // int status = updateMacMapping(itemHistory);

            List<JSONObject> itemHistoryList = new ArrayList<JSONObject>();
            itemHistoryList.add(itemHistory);

            String assemblyType = customerDetails.get("AssemblyType");
            if (assemblyType.equalsIgnoreCase("Pair Item")) {

                int productId2 = commonGetAPI.getProductId(product2);
                JSONObject itemHistory1 = commonGetAPI.getItemHistoryByProduct(productId2, staffId, macAddress, serial);

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

                    JSONObject nonTrackableProduct = commonGetAPI.getNonTrackableProductQty(productId, staffId);
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
                        System.out.println("Non Serialized Product provided qty is higher than unusedQty" + customerUsername);
                        Utility.printLog(logFileName, logModuleName, "Non Serialized Product provided qty is higher than unusedQty ", customerUsername);
                    }
                }
            }

            customerJson.put("itemAssemblyStatus", "Pending");
            jsonString = customerJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }
        System.out.println("jsonString = " + jsonString);
        return jsonString;
    }

//    private String getPlanByCustService(int custId, String planName) {
//
//        String queryParam = "cpm/subscriber/getPlanByCustService/" + custId
//                + "?isAllRequired=true&isNotChangePlan=true";
//        String apiURL = getAPIURL(queryParam);
//
//        JSONObject JSONResponseBody = httpGet(apiURL);
//        int status = JSONResponseBody.getInt("responseCode");
//        String result = null;
//
//        if (status == 0) {
//            JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String receivedPlanName = jsonArray.getJSONObject(i).getString("planName");
//                if (receivedPlanName.equalsIgnoreCase(planName)) {
//                    int serviceId = jsonArray.getJSONObject(i).getInt("serviceId");
//                    String connectionNumber = jsonArray.getJSONObject(i).getString("connection_no");
//                    int custPlanMapppingId = jsonArray.getJSONObject(i).getInt("custPlanMapppingId");
//                    result = serviceId + ":" + connectionNumber + ":" + custPlanMapppingId;
//                    break;
//                }
//            }
//        }
//
//        if (result == null) {
//            System.out.println("Customer Plan details not found - " + planName);
//            Utility.printLog(logFileName, logModuleName, "Customer Plan details not found - ", planName);
//        }
//
//        return result;
//    }

//    private String getSerializedInventoryProductDetails(int serviceId, String productName) {
//
//        String queryParam = "SavbillInventoryManagement/product/getAllProductByServiceId?serviceId=" + serviceId;
//        String apiURL = getAPIURL(queryParam);
//        System.out.println(serviceId);
//        JSONObject JSONResponseBody = httpGet(apiURL);
//        int status = JSONResponseBody.getInt("responseCode");
//        String result = null;
//
//        if (status == 200) {
//            JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String receivedProductName = jsonArray.getJSONObject(i).getString("name");
//                if (receivedProductName.equalsIgnoreCase(productName)) {
//                    //	int newProductAmount = jsonArray.getJSONObject(i).getInt("newProductAmount");
//                    //	result = String.valueOf(newProductAmount);
//                    //	break;
//                    String newProductAmount = jsonArray.getJSONObject(i).optString("newProductAmount", ""); // Will return empty string if null or not found
//                    result = newProductAmount;
//                    break;
//
//
//                }
//            }
//        }
//
//        if (result == null) {
//            System.out.println("Inventory details not found - " + productName);
//            Utility.printLog(logFileName, logModuleName, "Inventory details not found - ", productName);
//        }
//
//        return result;
//    }


//    private String getNonSerializedInventoryProductDetails(String productName) {
//
//        String queryParam = "SavbillInventoryManagement/product/getAllProductForNonTrackableProductCategory";
//        String apiURL = getAPIURL(queryParam);
//        //	Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
//        JSONObject JSONResponseBody = httpGet(apiURL);
//        int status = JSONResponseBody.getInt("responseCode");
//        String result = null;
//
//        if (status == 200) {
//            JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String receivedProductName = jsonArray.getJSONObject(i).getString("name");
//                if (receivedProductName.equalsIgnoreCase(productName)) {
//                    //int newProductAmount = jsonArray.getJSONObject(i).getInt("newProductAmount");   //--sar 1 feb
//                    //result = String.valueOf(newProductAmount);
//
//                    String newProductAmount = jsonArray.getJSONObject(i).getString("newProductAmount");
//                    result = newProductAmount;
//                    break;
//
//
//                }
//            }
//        }
//
//        if (result == null) {
//            System.out.println("Inventory details not found - " + productName);
//            Utility.printLog(logFileName, logModuleName, "Inventory details not found - ", productName);
//
//        }
//
//        return result;
//    }

//		private JSONObject getItemHistoryByProduct(int productId, String ownerId, String assignedMacAddress,
//				String assignedSerialNumber) {
//
//			String queryParam = "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=Staff";
//			String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct" + queryParam;
//			apiURL = getAPIURL(apiURL);
//
//			JSONObject JSONResponseBody = httpGet(apiURL);
//			int status = JSONResponseBody.getInt("responseCode");
//			JSONObject itemJson = null;
//
//			if (status == 200) {
//				JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//				if (jsonArray.length() > 0) {
//
//					for (int i = 0; i < jsonArray.length(); i++) {
//						String serialNumber = jsonArray.getJSONObject(i).getString("serialNumber");
//						String macAddress = jsonArray.getJSONObject(i).getString("macAddress");
//
//						if (assignedMacAddress.equalsIgnoreCase(macAddress)
//								&& assignedSerialNumber.equalsIgnoreCase(serialNumber)) {
//							itemJson = jsonArray.getJSONObject(i);
//							break;
//						}
//					}
//				}
//			}
//
//            if (itemJson == null) {
//				String message = "Item history of product id " + productId + " with MAC=" + assignedMacAddress
//						+ " and Serial=" + assignedSerialNumber + " not found";
//				System.out.println(message);
//				Utility.printLog(logFileName, logModuleName, message, "");
//			}
//
//			return itemJson;
//		}

//    private JSONObject getItemHistoryByProduct(
//            int productId,
//            String ownerId,
//            String assignedMacAddress,
//            String assignedSerialNumber
//    ) {
//
//        // --- Define endpoint (no query params — POST) ---
//        String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct" + "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=staff";
//        apiURL = getAPIURL(apiURL);
//
//        // --- Build payload ---
//        JSONObject payload = new JSONObject();
//        payload.put("productId", productId);
//        payload.put("ownerId", ownerId);
//        payload.put("ownerType", "staff");
//
//
//        // Optional: Add pagination support if needed
//        JSONObject pagination = new JSONObject();
//        pagination.put("page", 1);
//        pagination.put("pageSize", 50);
//        payload.put("paginationRequestDTO", pagination);
//
//        // --- Make POST call ---
//        JSONObject JSONResponseBody = httpPost(apiURL, String.valueOf(payload));
//
//        int status = JSONResponseBody.optInt("responseCode", 0);
//        JSONObject itemJson = null;
//
//        if (status == 200) {
//            JSONArray jsonArray = JSONResponseBody.optJSONArray("dataList");
//
//            if (jsonArray != null && jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject item = jsonArray.getJSONObject(i);
//
//                    String serialNumber = item.optString("serialNumber", "");
//                    String macAddress = item.optString("macAddress", "");
//
//                    if (assignedMacAddress.equalsIgnoreCase(macAddress)
//                            && assignedSerialNumber.equalsIgnoreCase(serialNumber)) {
//                        itemJson = item;
//                        break;
//                    }
//                }
//            }
//        }
//
//        // --- Log if item not found ---
//        if (itemJson == null) {
//            String message = "Item history of product id " + productId
//                    + " with MAC=" + assignedMacAddress
//                    + " and Serial=" + assignedSerialNumber + " not found";
//            System.out.println(message);
//            Utility.printLog(logFileName, logModuleName, message, "");
//        }
//
//        return itemJson;
//    }


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

//    private JSONObject getNonTrackableProductQty(int productId, String ownerId) {
//
//        String queryParam = "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=Staff";
//        String apiURL = "SavbillInventoryManagement/outwards/getNonTrackableProductQty" + queryParam;
//        apiURL = getAPIURL(apiURL);
//
//        JSONObject JSONResponseBody = httpGet(apiURL);
//        int status = JSONResponseBody.getInt("responseCode");
//        JSONObject itemJson = null;
//
//        if (status == 200) {
//            JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//
//            int receivedProductId = jsonArray.getJSONObject(0).getInt("productId");
//            if (receivedProductId == productId) {
//                itemJson = jsonArray.getJSONObject(0);
//            }
//        }
//
//        if (itemJson == null) {
//            System.out.println("Non serialized product not found - " + productId);
//            Utility.printLog(logFileName, logModuleName, "Non serialized product not found - ",
//                    String.valueOf(productId));
//        }
//
//        return itemJson;
//    }


    //================================================

//    private int getAllCustomerInventoryList(String itemType, int custId, String macAddress,
//                                            String serialNumber, String productName, String qty) {
//
//        String queryParam = "SavbillInventoryManagement/inwards/getAllCustomerInventoryList?custId=" + custId;
//        String apiURL = getAPIURL(queryParam);
//
//        JSONObject JSONResponseBody = httpGet(apiURL);
//        int status = JSONResponseBody.getInt("responseCode");
//        int assemblyId = 0;
//        if (status == 200) {
//            JSONArray jsonArray = JSONResponseBody.getJSONArray("dataList");
//            //Utility.printLog(logFileName, logModuleName, "Response", jsonArray.toString());
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String receivedStatus = jsonArray.getJSONObject(i).getString("status");
//                if (receivedStatus.equalsIgnoreCase("PENDING")) {
//                    JSONArray inOutWardMACMapping = jsonArray.getJSONObject(i).getJSONArray("inOutWardMACMapping");
//
//                    if (itemType.equalsIgnoreCase("Serialized Item")) {
//                        String receivedSerialNumber = inOutWardMACMapping.getJSONObject(0).getString("serialNumber");
//                        String receivedMACAddress = inOutWardMACMapping.getJSONObject(0).getString("macAddress");
//
//                        if (macAddress.equalsIgnoreCase(receivedMACAddress)
//                                && serialNumber.equalsIgnoreCase(receivedSerialNumber)) {
//                            assemblyId = jsonArray.getJSONObject(i).getInt("id");
//                            break;
//                        }
//
//                    } else if (itemType.equalsIgnoreCase("Non Serialized Item")) {
//                        String receivedProductName = jsonArray.getJSONObject(i).getString("productName");
//                        int receivedQty = jsonArray.getJSONObject(i).getInt("qty");
//                        int tempQty = Integer.parseInt(qty);
//
//                        if (productName.equalsIgnoreCase(receivedProductName) && (tempQty == receivedQty)) {
//                            assemblyId = jsonArray.getJSONObject(i).getInt("id");
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        if (assemblyId == 0) {
//            System.out.println("Customer inventory mapping details not found - " + custId);
//            Utility.printLog(logFileName, logModuleName, "Customer inventory mapping details not found",
//                    String.valueOf(custId));
//        }
//
//        return assemblyId;
//    }


    private void approveAssignedCustomerInventory(int custInventoryMappingId) {

        CommonGetAPI commonGetAPI = new CommonGetAPI();
        String staffId = String.valueOf(commonGetAPI.staffUserNameIdList(Constant.STAFF_USERNAME));

        String apiURL = "SavbillInventoryManagement/inwards/approveInventory?isApproveRequest=true&nextstaff=" + staffId + "&remark=Approved by Migration";

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
            Utility.printLog(logFileName, logModuleName, "Assigned inventory is approved successfully = ", String.valueOf(custInventoryMappingId));

        }

    }
}
	


