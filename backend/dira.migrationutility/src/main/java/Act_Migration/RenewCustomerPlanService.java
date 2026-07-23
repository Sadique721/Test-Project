package Act_Migration;

import java.util.*;
import java.util.concurrent.*;
import org.json.*;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class RenewCustomerPlanService extends RestExecution{

    private final String logFileName = "execution.log";
    private final String logModuleName = "Addon";
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    //private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public void renewCustomerPlans(List<Map<String, String>> customerMapList) {
    	
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        //here updating sheet
        UpdateSheet us = new UpdateSheet();
        us.setActiveSheetName("MigrationCustomerWithAddonUsaeg");

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
         
            rw.setMultipleColumnInActiveSheetACTAddon();// Batch update after all tasks
        }

        System.out.println("All tasks completed.");
    }

    private void processCustomerData(Map<String, String> customerData, ReadWriteExcelFile rw, CommonGetAPI commonGetAPI) {
        try {
            String userName = customerData.get("Username");

            if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
            	Utility.printLog("execution.log", logModuleName, "Processing Customer", userName);
            	
                renewCustomerPlan(customerData);
            } else {
            	Utility.printLog("execution.log", logModuleName, "Customer Username NOT found", userName);
            	System.out.println("Customer UserName is NOT found - " + userName);
            }
        } catch (Exception e) {
            logError("Error processing customer data", e);
        }
    }

    private void renewCustomerPlan(Map<String, String> changePlanMap) {
    	
    	
    	
        String apiURL = getAPIURL("cpm/subscriber/changePlan01");
        Utility.printLog("execution.log", logModuleName,"Request URL", apiURL);

        String apiBody = getRenewPlanJson(changePlanMap);
        Utility.printLog("execution.log", logModuleName, "Request Body", apiBody);

        if (apiBody != null) {
            JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
            Utility.printLog("execution.log", logModuleName, "Response", JSONResponseBody.toString(4));

            handleApiResponse(JSONResponseBody, changePlanMap);
        }
    }

    private void handleApiResponse(JSONObject responseBody, Map<String, String> changePlanMap) {
        int status = responseBody.getInt("responseCode");
        String userName = changePlanMap.get("Username");
        String planName = changePlanMap.get("Addon");
        String rowIndex = changePlanMap.get("RowIndex");

        if (status == 200) {
            int planMapId = extractPlanMapId(responseBody);

            synchronized (this) { // Minimize critical section
                UpdateSheet us = new UpdateSheet();
                String columnAndValue = "cprid:" + planMapId + "#" + "migrationstatus:Success";
                us.setRowList(rowIndex, columnAndValue);
            }

            Utility.printLog("execution.log", logModuleName, "Success", "Customer Plan renewed successfully - " + userName + " | " + planName);
            System.out.println("Success :"+ "Customer Top-Up successfully - " + userName + " | " + planName);
        } else if (status == 406) {
            String error = responseBody.getString("responseMessage");
            Utility.printLog("execution.log", logModuleName, "Error", error + " - " + userName);
        }
    }

   
    public List<Map<String, String>> readRenewPlanCustomerList() {

		String sheetName = "MigrationCustomerWithAddonUsaeg";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getAddonDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			// Sno	username	startdate	enddate	existingplanname	addon	usedquota	migrationstatus	cprid

			
			String userName = cellValue.get("username");
			String mStatus = cellValue.get("migrationstatus");

			if ((!"".equals(userName)) && (!"success".equalsIgnoreCase(mStatus))) {

				valuemap.put("RowIndex", cellValue.get("Sno"));
			//	valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
			 // 	valuemap.put("Operation", cellValue.get("Operation"));  
				valuemap.put("Username", userName);
				valuemap.put("existingplanname", cellValue.get("existingplanname"));
				valuemap.put("Addon", cellValue.get("addon"));
				valuemap.put("Enddate", cellValue.get("enddate"));
				//valuemap.put("Remark", cellValue.get("Remark"));

				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}
    private int extractPlanMapId(JSONObject responseBody) {
        JSONArray planList = responseBody.getJSONObject("data").getJSONArray("planList");
        return planList.getJSONObject(1).getInt("planmapid"); // Assuming first plan is required here i ave change custplanmapping id  to planmapid--->
    }
    
    
   //<-------------------------------------------
    
    private String getRenewPlanJson(Map<String, String> renewPlanDetails) {
        String jsonString = null;
        int maxRetries = 3; // Maximum number of retries
        int retryCount = 0;

        try {
            JSONObject custRenewPlanJson = new JSONObject();
            JSONObject renewPlanJson = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            String customerType = "Postpaid";
            String customerUsername = renewPlanDetails.get("Username");
            int customerId = 0;
            
            // Retry logic for fetching customerId
            while (retryCount < maxRetries) {
                customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
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
            
            renewPlanJson.put("custId", customerId);

            String existingPlanName = renewPlanDetails.get("existingplanname");
            String details = null;

            // Retry logic for fetching plan details
            retryCount = 0;
            while (retryCount < maxRetries) {
                details = getPlanByCustService(customerId, existingPlanName);
                if (details != null && !details.isEmpty()) {
                    break; // Exit loop if details are valid
                } else {
                    retryCount++;
                    Utility.printLog("execution.log", logModuleName, "Retry", "Failed to fetch plan details, retrying...");
                    try {
                        Thread.sleep(2000); // Wait for 2 seconds before retry
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (details == null || details.isEmpty()) {
                Utility.printLog("execution.log", logModuleName, "Error", "Exceeded retry attempts for fetching plan details.");
                return null; // Return null if retries failed
            }

            // Parse the details string
            String[] temp = details.split(":");
            int serviceId = Integer.parseInt(temp[0]);
            int custServiceMapId = Integer.parseInt(temp[4]);

            String changePlanName = renewPlanDetails.get("Addon");
            int planId = commonGetAPI.getPlanId(changePlanName);

            String operationType = "Addon";
            if (operationType.equalsIgnoreCase("Addon")) {
                String endDate = Utility.formatDateChangeaddon(renewPlanDetails.get("Enddate"));
                String currentDate = Utility.getCurrentDateTimeByProvidedFormat("YYYY-MM-dd");
                String currentTime = Utility.getCurrentDateTimeByProvidedFormat("HH:mm:ss.SSS");
                
                String currentTimeStamp = currentDate + "T" + currentTime + "Z";
                
                renewPlanJson.put("purchaseType", "Addon");
                renewPlanJson.put("addonStartDate", currentTimeStamp);
                renewPlanJson.put("addonEndDate", endDate);  // static end date
            }

            renewPlanJson.put("isPaymentReceived", "false");
            renewPlanJson.put("remarks", "Act Top-Up");

            int staffId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
            renewPlanJson.put("paymentOwnerId", staffId);
            renewPlanJson.put("billableCustomerId", customerId);

            renewPlanJson.put("newPlanList", JSONObject.NULL);
            renewPlanJson.put("planMappingList", JSONObject.NULL);
            renewPlanJson.put("ChangePlanCategory", "");
            renewPlanJson.put("isAdvRenewal", false);

            JSONObject recordPaymentDTO = new JSONObject();
            renewPlanJson.put("recordPaymentDTO", recordPaymentDTO);

            List<String> planBindWithOldPlans = new ArrayList<String>();
            renewPlanJson.put("planBindWithOldPlans", planBindWithOldPlans);

            renewPlanJson.put("isRefund", false);
            renewPlanJson.put("isParent", true);
            renewPlanJson.put("custServiceMappingId", custServiceMapId);
            renewPlanJson.put("discount", 0);
            renewPlanJson.put("planId", planId);

            List<JSONObject> changePlanRequestDTOList = new ArrayList<JSONObject>();
            changePlanRequestDTOList.add(renewPlanJson);

            custRenewPlanJson.put("changePlanRequestDTOList", changePlanRequestDTOList);
            custRenewPlanJson.put("recordPayment", JSONObject.NULL);

            // Add addon
            custRenewPlanJson.put("isTriggerCoaDm", true);

            jsonString = custRenewPlanJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }

        return jsonString;
    }

   
    
    private String getPlanByCustService(int custId, String planName) {
        String queryParam = "cpm/subscriber/getPlanByCustService/" + custId + "?isAllRequired=true&isNotChangePlan=true";
        String apiURL = getAPIURL(queryParam);

        // Initialize retry parameters
        int maxRetries = 1;
        int retryCount = 0;
        String planDetails = null;

        while (retryCount < maxRetries) {
            try {
                // Log the API request details
                Utility.printLog(logFileName, logModuleName, "API Request", "URL: " + apiURL);

                // Make the API call
                JSONObject responseBody = httpGet(apiURL);

                // Check the response status code
                if (responseBody.getInt("responseCode") == 0) {
                    JSONArray jsonArray = responseBody.getJSONArray("dataList");

                    // Log the number of plans returned
                    Utility.printLog(logFileName, logModuleName, "API Response", "Found " + jsonArray.length() + " plans for Customer ID: " + custId);

                    // Search for the plan by name
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject planData = jsonArray.getJSONObject(i);
                        if (planData.getString("planName").equalsIgnoreCase(planName)) {
                            planDetails = String.format("%d:%s:%d:%d:%d",
                                    planData.getInt("serviceId"),
                                    planData.getString("connection_no"),
                                    planData.getInt("custPlanMapppingId"),
                                    planData.getInt("planId"),
                                    planData.getInt("customerServiceMappingId"));
                            break;
                        }
                    }

                    // If we found the plan, break the loop
                    if (planDetails != null) {
                        Utility.printLog(logFileName, logModuleName, "Plan Found", "Plan details for " + planName + ": " + planDetails);
                        return planDetails;
                    } else {
                        Utility.printLog(logFileName, logModuleName, "Plan Not Found", "No matching plan found for " + planName + " in the response.");
                    }
                } else {
                    // If responseCode is not 0, log the error message
                    String errorMessage = responseBody.getString("responseMessage");
                    Utility.printLog(logFileName, logModuleName, "API Error", "Error fetching plan details for Customer ID: " + custId + " - " + errorMessage);
                }
            } catch (Exception e) {
                // Log the exception and increment the retry count
                Utility.printLog(logFileName, logModuleName, "API Error", "Exception occurred while fetching plan details for Customer ID: " + custId + " - " + e.getMessage());
            }

            // Increment retry count and sleep before retrying
            retryCount++;
            Utility.printLog(logFileName, logModuleName, "Retry", "Retrying... Attempt " + retryCount + " of " + maxRetries);
            try {
                Thread.sleep(2000); // Wait for 2 seconds before retrying
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // If we reached here, all retries failed, so return null
        Utility.printLog(logFileName, logModuleName, "Failed After Retries", "Failed to fetch plan details after " + maxRetries + " attempts for Customer ID: " + custId);
        return null;
    }

    //<-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
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

    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        Utility.printLog(logFileName, logModuleName, message, e.getMessage());
        e.printStackTrace();
    }
}