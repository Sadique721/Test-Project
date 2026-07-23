package tumil;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import org.json.JSONObject;
import temp.UpdateSheet;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
public class TumilCustomerDirectData extends RestExecution{

	    private final String logFileName = "execution.log";
	    private final String logModuleName = "CUSTOMER_DIRECT";
	    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

	    public void addCustomerdirectcharge(List<Map<String, String>> customerMapList)
	    {
	        ReadWriteExcelFile rw = new ReadWriteExcelFile();
	        //here updating sheet
	        UpdateSheet us = new UpdateSheet();
	        us.setActiveSheetName("CustomerDirect");

	        CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);

	        try {
	            CommonGetAPI commonGetAPI = new CommonGetAPI();

	            for (Map<String, String> customerData : customerMapList) {
	                completionService.submit(() -> {
	                    processCustomerData(customerData, rw, commonGetAPI);
	                    return null;
	                });
	            }

	            for (int i = 0; i < customerMapList.size(); i++) {
	                try {
	                    completionService.take().get(); // Wait for each task to complete
	                } catch (Exception e) {
	                    logError("Error waiting for task completion", e);
	                }
	            }
	        } finally {
	            shutdownExecutor();

	            rw.setMultipleColumnInActiveSheetTumil();// Batch update after all tasks
	        }

	        System.out.println("All tasks completed.");

	    }



	    public List<Map<String, String>> readCustomerDirectChargeDatalist()
	    {
	        String sheetName = "CustomerDirect";
	        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
	        ReadData readData = new ReadData();
	        sheetMap = readData.getTumilCustomerDataSheet(sheetName);

	        Map<String, String> cellValue = new HashMap<String, String>();
	        List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();
	        for (int i = 0; i < sheetMap.size(); i++) {
	            Map<String, String> valuemap = new HashMap<String, String>();
	            cellValue = sheetMap.get(i);
	            // Sno  username   startdate   migrationstatus
	            String userName = cellValue.get("USERNAME");
	            String mStatus = cellValue.get("MIGRATIONSTATUS");
	            if ((!"".equals(userName)) && (!"success".equalsIgnoreCase(mStatus))) {

	                valuemap.put("RowIndex", cellValue.get("SNO"));

	                valuemap.put("Username", userName);
	                valuemap.put("service",cellValue.get("SERVICE"));
	                valuemap.put("planname", cellValue.get("EXISTINGPLANNAME"));
	                valuemap.put("DirectCharge", cellValue.get("CHARGENAME"));
	                valuemap.put("price", cellValue.get("PRICE"));
	                valuemap.put("staff", cellValue.get("STAFF"));
	                customerMapList.add(valuemap);
	            }
	        }
	        return customerMapList;

	    }

	    private void processCustomerData(Map<String, String> customerData, ReadWriteExcelFile rw, CommonGetAPI commonGetAPI) {
	        try {
	            String userName = customerData.get("Username");

	            if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
	                Utility.printLog("execution.log", logModuleName, "Processing Customer", userName);

	                addCustomerCharge(customerData);
	                //  System.out.println("direct charge");

	            } else {
	                Utility.printLog("execution.log", logModuleName, "Customer Username NOT found", userName);
	                System.out.println("Customer UserName is NOT found - " + userName);
	            }
	        } catch (Exception e) {
	            logError("Error processing customer data", e);
	        }
	    }
	    private void logError(String message, Exception e) {
	        System.err.println(message + ": " + e.getMessage());
	        Utility.printLog(logFileName, logModuleName, message, e.getMessage());
	        e.printStackTrace();
	    }

	    private void shutdownExecutor() {
	        executorService.shutdown();
	        try {
	            if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
	                log("Warning", "Forcing shutdown due to timeout...");
	                executorService.shutdownNow();
	            }
	        } catch (InterruptedException e) {
	            logError("Error during executor shutdown", e);
	            executorService.shutdownNow();
	        }
	    }

	    private void log(String message, String detail) {
	        System.out.println(message + ": " + detail);
	        Utility.printLog(logFileName, logModuleName, message, detail);
	    }

	    private void addCustomerCharge(Map<String, String> CustomerDirectMap) {



	        String apiURL = getAPIURL("cpm/createCustChargeOverride");
	        Utility.printLog("execution.log", logModuleName,"Request URL", apiURL);

	        String apiBody = getCustomerDirectChargeJson(CustomerDirectMap);
	        // System.out.println("json Body +"+ apiBody);
	        Utility.printLog("execution.log", logModuleName, "Request Body", apiBody);

	        if (apiBody != null) {
	            JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
	            // System.out.println("response body "+ JSONResponseBody);
	            Utility.printLog("execution.log", logModuleName, "Response", JSONResponseBody.toString(4));

	            handleApiResponse(JSONResponseBody, CustomerDirectMap);
	        }
	    }
	    private void handleApiResponse(JSONObject responseBody, Map<String, String> CustomerDirectMap) {
	        int status = responseBody.getInt("status");
	        String userName = CustomerDirectMap.get("Username");
	        String rowIndex = CustomerDirectMap.get("RowIndex");

	        if (status == 200) {

	            // System.out.println("added customer direct charge ");
	            synchronized (this) { // Minimize critical section
	                UpdateSheet us = new UpdateSheet();
	                String columnAndValue = "MIGRATIONSTATUS:Success";
	                us.setRowList(rowIndex, columnAndValue);
	            }

	            Utility.printLog("execution.log", logModuleName, "Success", "Customer Customer Direct Charge Added successfully - " + userName + " | " );
	            System.out.println("Success :"+ "Customer Direct Charge successfully - " + userName + " | " );
	        } else if (status == 406) {
	            String error = responseBody.getString("responseMessage");
	            Utility.printLog("execution.log", logModuleName, "Error", error + " - " + userName);
	        }
	    }

	    private String getCustomerDirectChargeJson(Map<String, String> customerDirectChargeDetails)
	    {
	        String jsonString = null;
	        int maxRetries = 3;
	        int retryCount = 0;

	        try{
	            JSONObject custDirectChargeJson = new JSONObject();
	            List<JSONObject> customerFilterJsonObjectList = new ArrayList<JSONObject>();
	            JSONObject filterObject = new JSONObject();
	            CommonGetAPI commonGetAPI = new CommonGetAPI();

	            String customerType = "Postpaid";
	            String customerUsername = customerDirectChargeDetails.get("Username");
	            int customerId=0;

	            while (retryCount < maxRetries) {
	                customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
	                System.out.println(customerId);
	                if (customerId != 0) {
	                    break;
	                } else {
	                    retryCount++;
	                    Utility.printLog("execution.log", logModuleName, "Retry", "Failed to fetch customerId, retrying...");
	                    try {
	                        Thread.sleep(2000); // Wait for 2 seconds before retry
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }

	            if (customerId == 0) {
	                Utility.printLog("execution.log", logModuleName, "Error", "Exceeded retry attempts for fetching customerId.");
	                return null; // Return null if retries failed
	            }

	            String service= customerDirectChargeDetails.get("service");

	            String basePlanDetail = commonGetAPI.getPlandetailsForCustoemrDirectChareg(customerDirectChargeDetails.get("planname"),customerId,service);

	            String[] detail = basePlanDetail.split(":");
	            //     ans = basePlan + ":" + planId + ":" + connection_no + ":" + service +":" + serviceId+ ":" + endDate;

	            String serviceId = detail[4];


	            String date= detail[5];
	            String endDate = date.substring(0, 10);
	            //  System.out.println(endDate); // Output: 01-06-2025

	            SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy");
	            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
	            Date date1 = fromFormat.parse(endDate);
	            String expirydate = toFormat.format(date1);

	            LocalDate today = LocalDate.now();  // Gets current date
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            String chargeDate = today.format(formatter);
	            //  System.out.println("Charge date "+chargeDate);

	            String directChargeDetail = commonGetAPI.getChargedetailsForCustoemrDirectChareg(customerDirectChargeDetails.get("DirectCharge"),serviceId);
	            String[] detail1= directChargeDetail.split(":");
	            int staffid= commonGetAPI.getStaffId(customerDirectChargeDetails.get("staff"));

	            filterObject.put("type","Recurring");
	            filterObject.put("chargeid",detail1[0]);
	            filterObject.put("validity",30);
	            filterObject.put("price",customerDirectChargeDetails.get("price"));  //dynamic from sheet
	            filterObject.put("actualprice",detail1[1]);
	            filterObject.put("charge_date",chargeDate);
	            filterObject.put("planid",detail[1]);
	            filterObject.put("planName",detail[0]);
	            filterObject.put("unitsOfValidity","Days");
	            filterObject.put("billingCycle",1);
	            filterObject.put("paymentOwnerId",staffid);
	            filterObject.put("discount",JSONObject.NULL);
	            filterObject.put("staticIPAdrress",JSONObject.NULL);
	            filterObject.put("expiry",expirydate);
	            filterObject.put("expiryDate",endDate);
	            filterObject.put("connection_no",detail[2]);

	            customerFilterJsonObjectList.add(filterObject);
	            custDirectChargeJson.put("custChargeDetailsPojoList",customerFilterJsonObjectList);
	            custDirectChargeJson.put("custid",customerId);
	            custDirectChargeJson.put("billableCustomerId",JSONObject.NULL);
	            custDirectChargeJson.put("paymentOwnerId",staffid);

	            jsonString = custDirectChargeJson.toString();
	            // System.out.println(jsonString);

	        }
	        catch (Exception e) {
	            jsonString = null;
	            e.printStackTrace();
	        }
	        return jsonString;
	    }


	}

