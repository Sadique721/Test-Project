package SavanaCustomer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import org.json.JSONObject;
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DirectCharge extends RestExecution {


    private final String logFileName = "DirectCharge";
    private final String logModuleName = "Create_Direct_Charge";
    int thread_size = Constant.THREAD_POOL_SIZE;
    private final ExecutorService executorService = Executors.newFixedThreadPool(thread_size);
    private UpdateSheet updateSheet = new UpdateSheet();

    public void addCustomerdirectcharge(List<Map<String, String>> customerMapList) {
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
            System.out.println("---------->   Started to write status in Fast Excel sheet. <-----------------");
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
            System.out.println("---------->   Stopped to write status in Fast Excel sheet. <-----------------");
//            rw.setMultipleColumnInActiveSheetTumil();// Batch update after all tasks
        }


        System.out.println("All tasks completed.");

    }


    public List<Map<String, String>> readCustomerDirectChargeDatalist() {
        String sheetName = "CustomerDirect";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < sheetMap.size(); i++) {
            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);
            // Sno  username   startdate   migrationstatus
            String userName = cellValue.get("USERNAME");
            String mStatus = cellValue.get("MIGRATIONSTATUS");
            if ((!"".equals(userName)) && (!"success".equalsIgnoreCase(mStatus))) {

                valuemap.put("RowIndex", safeTrim(cellValue.get("SNO")));

                valuemap.put("Username", userName);
                valuemap.put("service", safeTrim(cellValue.get("SERVICE")));
                valuemap.put("planname", safeTrim(cellValue.get("EXISTINGPLANNAME")));
                valuemap.put("DirectCharge", safeTrim(cellValue.get("CHARGENAME")));
                valuemap.put("price", safeTrim(cellValue.get("PRICE")));
                valuemap.put("staff", safeTrim(cellValue.get("STAFF")));
                valuemap.put("primaryIndex", safeTrim(cellValue.get("PrimaryIndex")));
                customerMapList.add(valuemap);
            }
        }
        return customerMapList;

    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private void processCustomerData(Map<String, String> customerData, ReadWriteExcelFile rw, CommonGetAPI commonGetAPI) {

        updateSheet.setActiveSheetName("CustomerDirect");
        try {
            String userName = customerData.get("Username");
            String row = customerData.get("RowIndex");

            if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
                Utility.printLog(logFileName, logModuleName, "Processing Customer", userName);

                addCustomerCharge(customerData);
                //  System.out.println("direct charge");

            } else {
                Utility.printLog("execution.log", logModuleName, "Customer Username NOT found", userName);
//                System.out.println("Customer UserName is NOT found - " + userName);
                String message = "Customer Usename is Not Found - " + userName;
                String migrationStatus = "UserName Not Found";
                String migrationDetail = message;
                String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
                updateSheet.setRowList(row, columnAndValue);
                System.out.println(message);
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
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        String apiBody = getCustomerDirectChargeJson(CustomerDirectMap);
        // System.out.println("json Body +"+ apiBody);
        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

        if (apiBody != null) {
            JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
            // System.out.println("response body "+ JSONResponseBody);
            Utility.printLog("execution.log", logModuleName, "Response", JSONResponseBody.toString(4));

            handleApiResponse(JSONResponseBody, CustomerDirectMap);
        }
    }

    private void handleApiResponse(JSONObject responseBody, Map<String, String> CustomerDirectMap) {
        try {


            int status = responseBody.getInt("status");
            String userName = CustomerDirectMap.get("Username");
            String rowIndex = CustomerDirectMap.get("RowIndex");
            String migrationStatus = "Initial";
            String migrationDetail = "Initial";

            if (status == 200) {

                // System.out.println("added customer direct charge ");
//                synchronized (this) { // Minimize critical section
//                    UpdateSheet us = new UpdateSheet();
//                    String columnAndValue = "MIGRATIONSTATUS:Success";
//                    us.setRowList(rowIndex, columnAndValue);
//                }
                String message = "Direct Charge added successfully - " + CustomerDirectMap.get("Username");
                System.out.println(message);

                migrationStatus = "Success";
                migrationDetail = message;

                Utility.printLog("execution.log", logModuleName, "Success", "Customer Customer Direct Charge Added successfully - " + userName + " | ");
                System.out.println("Success :" + "Customer Direct Charge successfully - " + userName + " | ");
            } else if (status == 406) {
                String error = responseBody.getString("responseMessage");
                Utility.printLog("execution.log", logModuleName, "Error", error + " - " + userName);

                migrationStatus = "Error";
                migrationDetail = error;
            }

            String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
            updateSheet.setRowList(rowIndex, columnAndValue);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (handleAPIResponse)...." + e.getMessage());
        }
    }

//		    private String getCustomerDirectChargeJson(Map<String, String> customerDirectChargeDetails)
//		    {
//		        String jsonString = null;
//		        int maxRetries = 3;
//		        int retryCount = 0;
//
//		        try{
//		            JSONObject custDirectChargeJson = new JSONObject();
//                    List<JSONObject> customerFilterJsonObjectList = new ArrayList<JSONObject>();
//		            JSONObject filterObject = new JSONObject();
//		            CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//		            String customerType = "Prepaid";
//		            String customerUsername = customerDirectChargeDetails.get("Username");
//		            int customerId=0;
//
//		            while (retryCount < maxRetries) {
//		                customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
//		                System.out.println(customerId);
//		                if (customerId != 0) {
//		                    break;
//		                } else {
//		                    retryCount++;
//		                    Utility.printLog(logFileName, logModuleName, "Retry", "Failed to fetch customerId, retrying...");
//		                    try {
//		                        Thread.sleep(2000); // Wait for 2 seconds before retry
//		                    } catch (InterruptedException e) {
//		                        e.printStackTrace();
//		                    }
//		                }
//		            }
//
//		            if (customerId == 0) {
//		                Utility.printLog(logFileName, logModuleName, "Error", "Exceeded retry attempts for fetching customerId.");
//		                return null; // Return null if retries failed
//		            }
//
//		            String service= customerDirectChargeDetails.get("service");
//
//		            String basePlanDetail = commonGetAPI.getPlandetailsForCustoemrDirectChareg(customerDirectChargeDetails.get("planname"),customerId,service);
//
//		            String[] detail = basePlanDetail.split(":");
//		            //     ans = basePlan + ":" + planId + ":" + connection_no + ":" + service +":" + serviceId+ ":" + endDate;
//
//		            String serviceId = detail[4];
//
//
//		            String date= detail[6];
////                    String startDate = date.substring(0, 9);
//		            String endDate = date.substring(0, 10);
////		              System.out.println(endDate); // Output: 01-06-2025
//
//		            SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy");
//		            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");

    /// /                    Date date2 = fromFormat.parse(startDate);
    /// /                    String startDate = toFormat.format(date2);
//		            Date date1 = fromFormat.parse(endDate);
//		            String expirydate = toFormat.format(date1);
//
//		            LocalDate today = LocalDate.now();  // Gets current date
//		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		            String chargeDate = today.format(formatter);
//		            //  System.out.println("Charge date "+chargeDate);
//
//		            String directChargeDetail = commonGetAPI.getChargedetailsForCustoemrDirectChareg(customerDirectChargeDetails.get("DirectCharge"),serviceId);
//		            String[] detail1= directChargeDetail.split(":");
//		            int staffid= commonGetAPI.getStaffId(customerDirectChargeDetails.get("staff"));
//
//		            filterObject.put("type","Recurring");
//		            filterObject.put("chargeid",detail1[0]);
//		            filterObject.put("validity",30);
//		            filterObject.put("price",customerDirectChargeDetails.get("price"));  //dynamic from sheet
//		            filterObject.put("actualprice",detail1[1]);
//		            filterObject.put("charge_date",chargeDate);
//		            filterObject.put("planid",detail[1]);
//		            filterObject.put("planName",detail[0]);
//		            filterObject.put("unitsOfValidity","Days");
//		            filterObject.put("billingCycle",1);
//		            filterObject.put("paymentOwnerId",staffid);
//		            filterObject.put("discount",JSONObject.NULL);
//		            filterObject.put("staticIPAdrress",JSONObject.NULL);
//		            filterObject.put("expiry",expirydate);
//		            filterObject.put("expiryDate",endDate);
//		            filterObject.put("connection_no",detail[2]);
//
//		            customerFilterJsonObjectList.add(filterObject);
//		            custDirectChargeJson.put("custChargeDetailsPojoList",customerFilterJsonObjectList);
//		            custDirectChargeJson.put("custid",customerId);
//		            custDirectChargeJson.put("billableCustomerId",JSONObject.NULL);
//		            custDirectChargeJson.put("paymentOwnerId",staffid);
//
//		            jsonString = custDirectChargeJson.toString();
//		            // System.out.println(jsonString);
//
//		        }
//		        catch (Exception e) {
//		            jsonString = null;
//		            e.printStackTrace();
//		        }
//		        return jsonString;
//		    }
    private String getCustomerDirectChargeJson(Map<String, String> customerDirectChargeDetails) {
        String jsonString = null;
        int maxRetries = 3;
        int retryCount = 0;

        try {
            JSONObject custDirectChargeJson = new JSONObject();
            List<JSONObject> custChargeDetailsList = new ArrayList<>();
            JSONObject filterObject = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            String customerType = "Prepaid";
            String customerUsername = customerDirectChargeDetails.get("Username");
            int customerId = 0;

            // Retry fetching customerId from primary source
            while (retryCount < maxRetries) {
                customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
                if (customerId != 0) {
                    break; // found in primary source
                }
                retryCount++;
                Utility.printLog(logFileName, logModuleName, "Retry", "Failed to fetch customerId, retrying...");
                Thread.sleep(2000); // wait before retry
            }

            // Fallback: if not found in primary source after retries, get from CAF
            if (customerId == 0) {
                customerId = commonGetAPI.getCAFCustomerId(customerUsername);
                if (customerId != 0) {
                    Utility.printLog(logFileName, logModuleName, "Info", "Fetched customerId from CAF");
                }
            }

//            // Retry fetching customerId
//            while (retryCount < maxRetries) {
//                customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
//                if (customerId != 0) break;
//                retryCount++;
//                Utility.printLog(logFileName, logModuleName, "Retry", "Failed to fetch customerId, retrying...");
//                Thread.sleep(2000);
//            }

            if (customerId == 0) {
                Utility.printLog(logFileName, logModuleName, "Error", "Exceeded retry attempts for fetching customerId.");
                return null;
            }

            String service = customerDirectChargeDetails.get("service");
            String basePlanDetail = commonGetAPI.getPlandetailsForCustoemrDirectChareg(
                    customerDirectChargeDetails.get("planname"), customerId, service);

            // basePlanDetail format: ["Twiga", "1", "SERV2025-0000004", "FTTH", "2", "25-11-2025 07", "10 PM", "26-12-2025 06", "29 PM"]
            String[] detail = basePlanDetail.split(":");
            String serviceId = detail[4];

            // Combine end date parts
            String endDateStr = detail[7] + " " + detail[8]; // "26-12-2025 06 29 PM"

            SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy hh mm a");
            SimpleDateFormat expiryFormat = new SimpleDateFormat("yyyy-MM-dd");       // For "expiry"
            SimpleDateFormat expiryDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // For "expiryDate"

            Date endDate = fromFormat.parse(endDateStr);
            String expiry = expiryFormat.format(endDate);           // yyyy-MM-dd
            String expiryDate = expiryDateFormat.format(endDate);  // dd-MM-yyyy HH:mm

            // Current charge date
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String chargeDate = today.format(formatter);

            // Get direct charge details
            String directChargeDetail = commonGetAPI.getChargedetailsForCustoemrDirectChareg(
                    customerDirectChargeDetails.get("DirectCharge"), serviceId);
            String[] detail1 = directChargeDetail.split(":");
            int staffId = commonGetAPI.getStaffId(customerDirectChargeDetails.get("staff"));

            // Build JSON object
            filterObject.put("type", "Recurring");
            filterObject.put("chargeid", Integer.parseInt(detail1[0]));
            filterObject.put("validity", 1);
//            filterObject.put("price", Integer.parseInt(customerDirectChargeDetails.get("price")));
            filterObject.put("price", Double.parseDouble(customerDirectChargeDetails.get("price")));
//            filterObject.put("actualprice", Integer.parseInt(detail1[1]));
            filterObject.put("actualprice", Double.parseDouble(detail1[1]));
            filterObject.put("charge_date", chargeDate);
            filterObject.put("planid", detail[1]);
            filterObject.put("planName", detail[0]);
            filterObject.put("unitsOfValidity", "Months");
            filterObject.put("billingCycle", 1);
            filterObject.put("paymentOwnerId", staffId);
            filterObject.put("discount", JSONObject.NULL);
            filterObject.put("staticIPAdrress", JSONObject.NULL);
            filterObject.put("expiry", expiry);
            filterObject.put("expiryDate", expiryDate);
            filterObject.put("connection_no", detail[2]);
            filterObject.put("installment_no", 1);
            filterObject.put("installmentFrequency", JSONObject.NULL);
            filterObject.put("totalInstallments", JSONObject.NULL);
            filterObject.put("remarks", customerDirectChargeDetails.get("primaryIndex"));

            custChargeDetailsList.add(filterObject);

            custDirectChargeJson.put("custChargeDetailsPojoList", custChargeDetailsList);
            custDirectChargeJson.put("custid", customerId);
            custDirectChargeJson.put("billableCustomerId", JSONObject.NULL);
            custDirectChargeJson.put("paymentOwnerId", staffId);

            jsonString = custDirectChargeJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }

        return jsonString;
    }


}


