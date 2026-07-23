package SalesCRM;

import utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import groovy.util.logging.Slf4j;
import temp.UpdateSheet;

public class LeadCreation extends RestExecution {


    int iterationCounter = 0;
    private static final Logger log = LoggerFactory.getLogger(LeadCreation.class);

    private static String logFileName = "Lead.log";
    private static String logModuleName = "CreateLeadCustomer";
    private UpdateSheet updateSheet = new UpdateSheet();

    int thread_size = Constant.THREAD_POOL_SIZE;
    int batchSize = Constant.BATCH_SIZE;
    int retryLimit = Constant.RETRY_LIMIT; // remove  -->31 dec
    int retryDelayMS = Constant.RETRY_DELAY_MS; //remove --> 31 dec

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);

    private static XSSFWorkbook workbook = null;
    //				ExecutorService executor = Executors.newCachedThreadPool();
    ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Use fixed thread pool
//rajnish


    public ArrayList<Object> createPrepaidCustomer(Map<String, String> customerDetailsMap, Map<String, Integer> serviceAreaIdAll) {
        ArrayList<Object> objects = new ArrayList<>();
        try {
            String rowIndex = customerDetailsMap.get("RowIndex");
            String apiURL = getAPIURL("SavbillSalesCrmsBss/leadMaster/save");

            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            String apiBody = getPrepaidCustomerJson(customerDetailsMap, serviceAreaIdAll);
            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

            if (apiBody != null && !apiBody.isEmpty()) {
                StopWatch sw = new StopWatch();
                sw.start();
                JSONObject JSONResponseBody = null;
                boolean success = false;
                int attempts = 0;

                // Retry logic with exponential backoff
                while (attempts < retryLimit && !success) {
                    try {
                        JSONResponseBody = httpPost(apiURL, apiBody);
                        success = true;

                        String response = JSONResponseBody.toString(4);
                        Utility.printLog(logFileName, logModuleName, "Response", response);

                    } catch (Exception e) {
                        attempts++;
                        if (attempts == retryLimit) {
                            Utility.printLog("execution.log", logModuleName, "ERROR", "API call failed after retries: " + e.getMessage());
                        }
                        try {
                            Thread.sleep(retryDelayMS * (long) Math.pow(2, attempts)); // Exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

//                String leadName = customerDetailsMap.get("Name");
//                ProductUtility.printResponse(JSONResponseBody, logModuleName, leadName);
//                handleResponse(JSONResponseBody, leadName, customerDetailsMap.get("RowIndex"));

                objects.add(JSONResponseBody);
                objects.add(rowIndex);
                objects.add(sw);
                objects.add(customerDetailsMap);
                return objects;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (createPrepaidCustomer)...... " + e.getMessage());
            failureCount.incrementAndGet();
        }
        return objects;
    }

    private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime,
                                   Map<String, String> customerDetailsMap, Map<String, Integer> serviceAreaIdAll) {
        try {
            int status = response.getInt("status");
            String userName = customerDetailsMap.get("Username") + " - " + elapsedTime;
            String migrationStatus = "Initial";
            String migrationDetail = "Initial";
            int leadMasterId = -1;

            if (!response.has("ERROR")) {
                if (status == 200) {
                    successCount.incrementAndGet();
                    String message = "New Lead added successfully - " + elapsedTime;
                    System.out.println(message);
                    Utility.printLog("execution.log", logModuleName, "Success", message);

                    migrationStatus = "Success";
                    migrationDetail = message;

                    // Extract the customer object
                    JSONObject customer = response.getJSONObject("leadMaster");
                    String leadNo = customer.get("leadNo").toString();
                    leadMasterId = customer.getInt("id");

                    String columnAndValue = "LeadNo:" + leadNo + "#" + "MigrationStatus:Success";

                    // Approve Lead if staff is valid
//                    if (isValidStaff(customerDetailsMap)) {
//                        try {
//                            // 1. Perform Lead approval synchronously
//                            approveLead(response, customerDetailsMap, leadNo, serviceAreaIdAll);
//
//                            // 2. Assign Staff asynchronously using executorService
//                            executorService.submit(() -> {
//                                try {
//                                    assignStaff(response, customerDetailsMap, leadNo, serviceAreaIdAll);
//                                } catch (Exception e) {
//                                    log.error("Error while assigning staff for Lead No: {}: {}", leadNo, e.getMessage(), e);
//                                }
//                            });
//
//                        } catch (Exception e) {
//                            log.error("Error occurred while approving Lead for Lead No: {}: {}", leadNo, e.getMessage(), e);
//                        }
//
//                    } else {
//                        System.out.println("Staff is empty, Lead will not be approved. LeadNo: " + leadNo);
//                    }

                    UpdateSheet us = new UpdateSheet();
                    us.setRowList(rowIndex, columnAndValue);

                } else if (status == 406) {
                    String error = response.getString("responseMessage") + " - " + elapsedTime;
                    System.out.println(error);
                    Utility.printLog("execution.log", logModuleName, "Already Exists", error);
                    failureCount.incrementAndGet();

                    migrationStatus = "Already Exists";
                    migrationDetail = error;

                } else {
                    failureCount.incrementAndGet();
                    String message = response.get("ERROR") + " - " + elapsedTime;
                    Utility.printLog("execution.log", logModuleName, "ERROR", message);

                    migrationStatus = "Error";
                    migrationDetail = message;

                }
            } else {
                failureCount.incrementAndGet();
                String message = response.get("ERROR") + " - " + elapsedTime;
                Utility.printLog("execution.log", logModuleName, "ERROR", message);
                migrationStatus = "Error";
                migrationDetail = message;
            }

            String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail + "#" + "leadMasterId::" + leadMasterId;
            //UpdateSheet us = new UpdateSheet();
            updateSheet.setRowList(rowIndex, columnAndValue);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in handleAPIResponse(): " + e.getMessage());
        }
    }


//    private void handleResponse(JSONObject response, String leadName, String rowIndex) {
//        int status = response.getInt("status");
//        String migrationStatus = "Initial";
//        String migrationDetail = "Initial";
//
//        if (!response.has("ERROR")) {
//            if (status == 200) {
//                String message = response.getString("message") + " - " + leadName;
//                migrationStatus = "Success";
//                migrationDetail = message;
//            } else if (status == 406) {
//                String error = response.getString("message") + " - " + leadName;
//                migrationStatus = "Already Exists";
//                migrationDetail = error;
//            } else {
//                String message = response.get("ERROR") + " - " + leadName;
//                migrationStatus = "Error";
//                migrationDetail = message;
//            }
//        } else {
//            String message = response.get("ERROR") + " - " + leadName;
//            migrationStatus = "Error";
//            migrationDetail = message;
//        }
//
//        String columnAndValue = "MigrationStatus:" + migrationStatus + "#" + "MigrationDetail:" + migrationDetail;
//        updateSheet.setRowList(rowIndex, columnAndValue);
//    }

    public void createPrepaidCustomer(List<Map<String, String>> customerMapList, Map<String, Integer> serviceAreaIdAll) {
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Thread pool size is dynamically set
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        updateSheet.setActiveSheetName("Lead");

        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
        CommonGetAPI commonGetAPI = new CommonGetAPI();

        List<Future<ArrayList<Object>>> futures = new ArrayList<>();

        // Submit tasks for concurrent processing
        for (Map<String, String> customerDetails : customerMapList) {
            String userName = customerDetails.get("Name");
            String row = customerDetails.get("RowIndex");

            futures.add(executorService.submit(() -> {
                StopWatch sw = new StopWatch();
                sw.start();
                ArrayList<Object> result = new ArrayList<>();

                try {
                    Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, customerDetails.toString());
                    result = createPrepaidCustomer(customerDetails, serviceAreaIdAll); // Call API to create customer
                    // Batch Excel updates
                    batchToWrite.add(customerDetails);

                    // Write to Excel in batches
                    if (batchToWrite.size() >= batchSize) {
                        // rw.setMultipleColumnInActiveSheet(batchToWrite);
                        batchToWrite.clear(); // Clear the batch after writing
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("Error processing customer " + userName + ": " + e.getMessage());
                }

                return result;
            }));
        }

        // Wait for all tasks to complete
        for (Future<ArrayList<Object>> future : futures) {
            try {
                ArrayList<Object> objects = future.get(); // Retrieve the result of the task
                JSONObject jsonObject = (JSONObject) objects.get(0);
                String rowIndex = (String) objects.get(1);
                StopWatch stopWatch = (StopWatch) objects.get(2);
                Map<String, String> customerDetailsMap = (Map<String, String>) objects.get(3);
                handleAPIResponse(jsonObject, rowIndex, stopWatch.getTime(), customerDetailsMap, serviceAreaIdAll);
                System.out.println("Task Completed Successfully: " + future.isDone());
            } catch (Exception e) {
                System.err.println("Error retrieving task result: " + e.getMessage());
            }
        }

//        // Update Excel after all tasks complete
//        ReadWriteExcelFile rw = new ReadWriteExcelFile();
//        rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SALES_CRM_DATA_FILE);

        // Gracefully shut down executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(90, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        } finally {
            System.out.println("---------->   Started to write status in sheet. <-----------------");
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SALES_CRM_DATA_FILE);
            System.out.println("---------->   Stopped to write status in sheet. <-----------------");
        }

        // Write remaining batch to Excel
        if (!batchToWrite.isEmpty()) {
            // ReadWriteExcelFile rw = new ReadWriteExcelFile();
            // rw.setMultipleColumnInActiveSheet(batchToWrite);
        }

        System.out.println("Final migration step completed.");
        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
    }


    public List<Map<String, String>> readUniquePrepaidCustomerList() {

        String sheetName = "Lead";  // This is sheet name.
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getSalesCRMDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String userName = cellValue.get("Name");
            String mStatus = cellValue.get("MigrationStatus");
            String rowIndex = cellValue.get("Sno");

            if ((!userName.isEmpty()) && (!"Success".equalsIgnoreCase(mStatus)) && (!"Already Exists".equalsIgnoreCase(mStatus))) {

                if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {


                    valuemap.put("RowIndex", safeTrim(cellValue.get("Sno")));

                    valuemap.put("Name", safeTrim(cellValue.get("Name")));
                    valuemap.put("LeadCustomerSector", safeTrim(cellValue.get("LeadCustomerSector")));
                    valuemap.put("LeadSource", safeTrim(cellValue.get("LeadSource")));

                    valuemap.put("PrimaryMobile", safeTrim(cellValue.get("Phone")));
                    //	valuemap.put("SecondryPhone", cellValue.get("Phone"));
                    valuemap.put("Email", safeTrim(cellValue.get("Email")));
                    valuemap.put("Servicearea", safeTrim(cellValue.get("Servicearea")));


                    valuemap.put("Plan", safeTrim(cellValue.get("Plan")));
                    valuemap.put("Branch", safeTrim(cellValue.get("Branch")));
                    valuemap.put("Address", safeTrim(cellValue.get("Address")));
                    valuemap.put("Municipality", safeTrim(cellValue.get("Pincode")));
                    valuemap.put("Ward", safeTrim(cellValue.get("Area")));
                    valuemap.put("Gender", safeTrim(cellValue.get("CustomerGender")));

                    valuemap.put("Landmark", safeTrim(cellValue.get("Landmark")));
                    valuemap.put("Service", safeTrim(cellValue.get("Service")));
                    valuemap.put("Staff", safeTrim(cellValue.get("AssigneeStaff")));
                    valuemap.put("Teams", safeTrim(cellValue.get("Teams")));

                    valuemap.put("Createdby", safeTrim(cellValue.get("Createdby")));
                    valuemap.put("LeadNo", safeTrim(cellValue.get("LeadNo")));
                    valuemap.put("primaryIndex", safeTrim(cellValue.get("PrimaryIndex")));
                    customerMapList.add(valuemap);
                }
            }
        }
        return customerMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // @SuppressWarnings("unchecked")
    private String getPrepaidCustomerJson(Map<String, String> customerDetails, Map<String, Integer> serviceAreaDetails) {

        String jsonString = null;

        try {

            JSONObject customerJsonObject = new JSONObject();
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            //CommonList commonList = new CommonList();


            // prepaid customer
            customerJsonObject.put("custtype", "Prepaid");
            // name = first name and last name
            // Extract the full name from the Map
            String fullName = customerDetails.get("Name");
            String firstName = "";
            String lastName = "";
            // Check if fullName is null or empty
            if (fullName == null || fullName.trim().isEmpty()) {
                // Handle the case where the full name is missing or empty
                firstName = ""; // Or some default value
                lastName = "";  // Or some default value
            } else {
                // Split the full name into parts based on space
                String[] nameParts = fullName.split("\\s+");

                // Extract the first name (first part)
                firstName = nameParts[0];

                // Join the rest of the parts as the last name (everything after the first part)
                lastName = (nameParts.length > 1)
                        ? String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length))
                        : firstName; // If no last name exists, use first name as last name

                // If lastName is still empty or null, fallback to firstName
                if (lastName == null || lastName.isEmpty()) {
                    lastName = firstName;
                }
            }


            customerJsonObject.put("title", "");
            customerJsonObject.put("firstname", firstName);
            customerJsonObject.put("lastname", lastName);
//            customerJsonObject.put("username", JSONObject.NULL);

//            customerJsonObject.put("password", JSONObject.NULL);

            // ---------------------- Mobile number ----------------------
            String mobNo = customerDetails.get("PrimaryMobile");
            String updatedNumber = "99999999"; // Default fallback
            String countryCode = "+256"; // Default static code if extraction fails

            if (mobNo != null && !mobNo.trim().isEmpty()) {
                try {
                    // Handle numbers in scientific notation (e.g., 2.567E9)
                    if (mobNo.contains("E") || mobNo.contains("e")) {
                        mobNo = String.format("%.0f", Double.parseDouble(mobNo));
                    }
                    String numericOnly = mobNo.replaceAll("[^0-9]", "");

                    // Extract first 3 digits for dynamic country code
                    if (numericOnly.length() >= 3) {
                        countryCode = "+" + numericOnly.substring(0, 3);
                    }

                    // Remove first 3 digits for local number if possible
                    if (numericOnly.length() > 3) {
                        updatedNumber = numericOnly.substring(3);
                    } else {
                        updatedNumber = numericOnly; // use as-is if shorter
                    }

                } catch (Exception e) {
                    // fallback in case of parsing or format error
                    updatedNumber = "99999999";
                    countryCode = "+256";
                }
            }

// Put values into JSON safely
            customerJsonObject.put("countryCode", countryCode);

            try {
                Long mobileNumber = Long.parseLong(updatedNumber);
                customerJsonObject.put("mobile", mobileNumber);
                customerJsonObject.put("mobilenumber", mobileNumber);
            } catch (NumberFormatException e) {
                customerJsonObject.put("mobile", 99999999L); // fallback if invalid
            }


            //--------------------------------------------------------------------------------->
            customerJsonObject.put("phone", "");  // new added
            // Handle email
            String email = customerDetails.get("Email");
            customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "dummy@123gmail.com");

            customerJsonObject.put("pan", "");
            customerJsonObject.put("contactperson", JSONObject.NULL);

            customerJsonObject.put("calendarType", "English");

            customerJsonObject.put("dunningCategory", "Gold");

            customerJsonObject.put("cafno", JSONObject.NULL);

            customerJsonObject.put("currency", JSONObject.NULL);


            customerJsonObject.put("leadStaffId", JSONObject.NULL); // -->sar  here put logic for creted by

            // Handle status

            customerJsonObject.put("status", JSONObject.NULL);


//            customerJsonObject.put("parentCustomerId", JSONObject.NULL);
            customerJsonObject.put("custlabel", "customer");
            customerJsonObject.put("salesremark", "");


            // *********** Service Area Details *****************
            String ServiceArea = customerDetails.get("Servicearea").toLowerCase();


            Integer serviceareaId = serviceAreaDetails.get(ServiceArea);


            customerJsonObject.put("serviceareaid", serviceareaId);
            customerJsonObject.put("serviceareaName", ServiceArea);

//            customerJsonObject.put("branch", JSONObject.NULL);
            customerJsonObject.put("partnerid", 1);

            String branchName = customerDetails.get("Branch");


            if (!"".equals(branchName)) {
                int branchId = commonGetAPI.getBranchIdList(branchName).get(0);
//                int BranchName = commonGetAPI.getBranchIdList(branchName).get(1);
                customerJsonObject.put("branch", JSONObject.NULL);
                customerJsonObject.put("branchId", branchId);
            }


            // -- Customer Address Details --

            List<JSONObject> addressJsonObjectList = new ArrayList<JSONObject>();

            customerJsonObject.put("addressList", addressJsonObjectList);

//            customerJsonObject.put("valleyType", JSONObject.NULL);
            customerJsonObject.put("latitude", JSONObject.NULL);
            customerJsonObject.put("longitude", JSONObject.NULL);
            customerJsonObject.put("loginPassword", JSONObject.NULL);
            customerJsonObject.put("loginUsername", JSONObject.NULL);
            //Latitude	longitude

//            customerJsonObject.put("latitude", customerDetails.get("Latitude"));
//            customerJsonObject.put("longitude", customerDetails.get("longitude"));

            customerJsonObject.put("failcount", 0);
            customerJsonObject.put("isCustCaf", "yes");
            customerJsonObject.put("servicetype", "");
            customerJsonObject.put("billday", JSONObject.NULL);
            customerJsonObject.put("gender", customerDetails.get("Gender"));
            // lead 1;
            customerJsonObject.put("billableCustomerId", JSONObject.NULL);
            customerJsonObject.put("altmobile1", JSONObject.NULL);
            customerJsonObject.put("altmobile2", JSONObject.NULL);
            customerJsonObject.put("altmobile3", JSONObject.NULL);
            customerJsonObject.put("altmobile4", JSONObject.NULL);
            customerJsonObject.put("amount", JSONObject.NULL);
            customerJsonObject.put("assigneeName", JSONObject.NULL);
            customerJsonObject.put("blockNo", JSONObject.NULL);
            customerJsonObject.put("addparam1", customerDetails.get("primaryIndex"));


            customerJsonObject.put("gst", "");
            customerJsonObject.put("aadhar", "");
            customerJsonObject.put("passportNo", "");
//            customerJsonObject.put("tinNo", JSONObject.NULL);

            customerJsonObject.put("amount", JSONObject.NULL);
            customerJsonObject.put("paymentDetails", JSONObject.NULL);

            List<JSONObject> planJsonObjectList = new ArrayList<JSONObject>();

            String planCategory = "Individual";
            // --Individual Plan

            String billableCustomerId = null;
            float discountPercentage = 0;
            String discountType = null;
            String discountExpiryDate = null;
            boolean invoiceToOrg = false;
            boolean istrialplan = false;
            if (planCategory.equalsIgnoreCase("Individual")) {
                customerJsonObject.put("flatAmount", JSONObject.NULL);
                customerJsonObject.put("billableCustomerId", JSONObject.NULL);
                customerJsonObject.put("billTo", "CUSTOMER");
                customerJsonObject.put("discount", 0);
                customerJsonObject.put("discountType", "One-time");
                customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
                customerJsonObject.put("istrialplan", istrialplan);
            }
            if (customerDetails.get("Plan") != null && !customerDetails.get("Plan").toString().isEmpty()) {
                String service = customerDetails.get("Service");
                String plan = customerDetails.get("Plan");
                discountType = "";
                discountExpiryDate = "";

                JSONObject planDetailJsonObject = new JSONObject();
                int planId = commonGetAPI.getPlanId(plan);
                String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");
                float offerPrice = Float.valueOf(planDetails[1]);
                int validity = Integer.parseInt(planDetails[2]);
                float flatAmount = offerPrice;
                flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));

                planDetailJsonObject.put("newAmount", flatAmount);
                planDetailJsonObject.put("planId", planId);
                planDetailJsonObject.put("service", service);
                planDetailJsonObject.put("validity", validity);
                planDetailJsonObject.put("discount", 0);
                planDetailJsonObject.put("billTo", "CUSTOMER");
                planDetailJsonObject.put("billableCustomerId", JSONObject.NULL);
                planDetailJsonObject.put("offerPrice", offerPrice);
                planDetailJsonObject.put("isInvoiceToOrg", false);
                planDetailJsonObject.put("istrialplan", false);
                planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
                planDetailJsonObject.put("discountType", "One-time");
                planJsonObjectList.add(planDetailJsonObject);

            }
            customerJsonObject.put("planMappingList", planJsonObjectList);
            customerJsonObject.put("isInvoiceToOrg", false);

            // --Plan Group
            customerJsonObject.put("plangroupid", JSONObject.NULL);

            customerJsonObject.put("voicesrvtype", "");
            customerJsonObject.put("didno", "");

            // a add key value by sarfraz -->
            customerJsonObject.put("leadCustomerSector", customerDetails.get("LeadCustomerSector"));
//            customerJsonObject.put("leadCustomerSubSector", JSONObject.NULL);
            customerJsonObject.put("leadCustomerType", "Prepaid"); // take from sheet pospat

            customerJsonObject.put("leadDepartment", JSONObject.NULL);
            customerJsonObject.put("discount", JSONObject.NULL);
            customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
            customerJsonObject.put("discountType", JSONObject.NULL);

            customerJsonObject.put("planType", JSONObject.NULL);
            customerJsonObject.put("popManagementId", JSONObject.NULL);
            customerJsonObject.put("presentCheckForPayment", false);
            customerJsonObject.put("presentCheckForPermanent", false);


            customerJsonObject.put("rejectReasonName", JSONObject.NULL);
            customerJsonObject.put("previousAmount", JSONObject.NULL);
            customerJsonObject.put("previousMonth", JSONObject.NULL);
            customerJsonObject.put("previousVendor", JSONObject.NULL);
            customerJsonObject.put("discountType", JSONObject.NULL);
            customerJsonObject.put("leadDepartment", JSONObject.NULL);
            customerJsonObject.put("rejectReasonId", JSONObject.NULL);
            customerJsonObject.put("rejectSubReasonId", JSONObject.NULL);
            customerJsonObject.put("rejectSubReasonName", JSONObject.NULL);

            customerJsonObject.put("renewPlanLimit", JSONObject.NULL);
            customerJsonObject.put("requireServiceType", JSONObject.NULL);


            customerJsonObject.put("secondaryContactDetails", JSONObject.NULL);
            customerJsonObject.put("secondaryEmail", JSONObject.NULL);
            customerJsonObject.put("secondaryPhone", JSONObject.NULL);
            customerJsonObject.put("servicerType", JSONObject.NULL);
            customerJsonObject.put("nextApproveStaffId", JSONObject.NULL);
            customerJsonObject.put("nextTeamMappingId", JSONObject.NULL);
//            customerJsonObject.put("outsideValley", JSONObject.NULL);

            customerJsonObject.put("leadStatus", JSONObject.NULL);
            customerJsonObject.put("leadSubSourceId", JSONObject.NULL);
            customerJsonObject.put("leadSubSourceName", JSONObject.NULL);
            customerJsonObject.put("leadType", "Warm");                      // value
            customerJsonObject.put("leadvariety", JSONObject.NULL);


            customerJsonObject.put("competitorDuration", JSONObject.NULL);
            customerJsonObject.put("customerId", JSONObject.NULL);


            customerJsonObject.put("dateOfBirth", JSONObject.NULL);
            customerJsonObject.put("department", JSONObject.NULL);
            customerJsonObject.put("durationUnits", "Days");
            customerJsonObject.put("existingCustomerId", JSONObject.NULL);                      // value
            customerJsonObject.put("expiry", JSONObject.NULL);


            customerJsonObject.put("feasibility", JSONObject.NULL);
            customerJsonObject.put("feasibilityRemark", JSONObject.NULL);
            customerJsonObject.put("heardAboutSubisuFrom", JSONObject.NULL);
            customerJsonObject.put("id", JSONObject.NULL);
            customerJsonObject.put("isCredentialMatchWithAccountNo", JSONObject.NULL);
            customerJsonObject.put("landlineNumber", JSONObject.NULL);


            customerJsonObject.put("feedback", JSONObject.NULL);
            customerJsonObject.put("isLeadFromCWSC", false);
            customerJsonObject.put("isLeadQuickInv", false);

            int leasSourceId = commonGetAPI.getLeadSourceMasterId(customerDetails.get("LeadSource"));
            // here imp ---> give logic no wise
            customerJsonObject.put("leadSourceId", leasSourceId);

            // here change if lead agent,branch id
            customerJsonObject.put("leadAgentId", JSONObject.NULL);                      // value
            customerJsonObject.put("leadBranchId", JSONObject.NULL);
            customerJsonObject.put("leadPartnerId", JSONObject.NULL);
            customerJsonObject.put("leadCustomerId", JSONObject.NULL);


            customerJsonObject.put("leadCategory", "New Lead");
            customerJsonObject.put("leadCustomerCategory", JSONObject.NULL);

            customerJsonObject.put("leadIdentity", "retail");
            customerJsonObject.put("leadNo", JSONObject.NULL);

            customerJsonObject.put("leadOriginType", JSONObject.NULL); // take from sheet

            customerJsonObject.put("leadOriginTypes", JSONObject.NULL);
            customerJsonObject.put("leadServiceAreaId", JSONObject.NULL);
            customerJsonObject.put("leadSourceName", JSONObject.NULL);

            customerJsonObject.put("existingCustomerId", JSONObject.NULL);                      // value
            customerJsonObject.put("expiry", JSONObject.NULL);


            // Add pojo for Act-->


            //earlybillday
            customerJsonObject.put("earlybillday", JSONObject.NULL);

            // ------>

            // -- Over Direct Charge Mapping

            List<JSONObject> chargeJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("overChargeList", chargeJsonObjectList);


            // --Customer MAC Addresses Mapping

            List<JSONObject> macJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("custMacMapppingList", macJsonObjectList);

            jsonString = customerJsonObject.toString();

        } // take care of this brace
        catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(jsonString);
        return jsonString;
    }

//    private void approveLead(JSONObject response, Map<String, String> customerDetails, String leadNo, Map<String, Integer> serviceAreaDetails) {
//        String jsonString = null;
//        String apiURLPut = getAPIURL("cpm/teamHierarchy/approveLead");
//        Map<String, Object> approved = new HashMap<>();
//        CommonGetAPI commonGetAPI = new CommonGetAPI();
//        String staffUsername = customerDetails.get("Staff");
//        int staffId = commonGetAPI.getStaffId(staffUsername);
//        int mvnoId = commonGetAPI.getMvnoId(Constant.STAFF_USERNAME);
//
//        // *********** Service Area Details *****************
//        String ServiceArea = customerDetails.get("Servicearea").toLowerCase();
//        Integer serviceareaId = serviceAreaDetails.get(ServiceArea);
//
//        // Safe read
//        JSONObject leadMaster = response.optJSONObject("leadMaster");
//        int leadId = (leadMaster != null) ? leadMaster.optInt("id", 0) : 0;
//
//        approved.put("approveRequest", true);
//        approved.put("buId", JSONObject.NULL);
//        approved.put("currentLoggedInStaffId", staffId);
//        JSONObject firstname = response.optJSONObject("leadMaster");
//        String fName = (firstname != null) ? firstname.optString("firstname", "") : "";
//        approved.put("firstname", fName);

    /// /        int id = response.getJSONObject("leadMaster").getInt("id");
//        approved.put("id", leadId);
//        approved.put("mvnoId", mvnoId);
//        approved.put("serviceareaid", serviceareaId);
//        approved.put("flag", "Approve");
//        approved.put("remark", "Approved Lead");
//        approved.put("nextTeamMappingId", JSONObject.NULL);
//        approved.put("status", "Inquiry");
//        approved.put("username", JSONObject.NULL);
//        approved.put("rejectedReasonMasterId", JSONObject.NULL);
//        approved.put("teamName", JSONObject.NULL);
//
//        // 3. Convert Map to JSON string
//        String body = new JSONObject(approved).toString();
//
//        // 4. Make the POST request using your existing httpPost
//        JSONObject jsonResponse = httpPut(apiURLPut, body);
//
//        // 5. Check the status and log
//        int status = jsonResponse.optInt("responseCode", -1);
//
//        if (status == 200) {
//            String message = "Lead approved successfully for Lead Name: " +
//                    fName + " Lead No : " + leadNo;
//            System.out.println(message);
//            Utility.printLog("execution.log", logModuleName, "Lead Approved", message);
//        } else {
//            String errorMessage = "Failed to approve Lead for LeadNo: " +
//                    leadNo + ". Status: " + status;
//            log.error(errorMessage);
//            Utility.printLog("execution.log", logModuleName, "Lead Approval Failed", errorMessage);
//        }
//
//    }
    private void approveLead(JSONObject response, Map<String, String> customerDetails, String leadNo,
                             Map<String, Integer> serviceAreaDetails) {

        String apiURLPut = getAPIURL("cpm/teamHierarchy/approveLead");
        Map<String, Object> approved = new HashMap<>();
        CommonGetAPI commonGetAPI = new CommonGetAPI();

        String staffUsername = customerDetails.get("Staff");
        int staffId = commonGetAPI.getStaffId(staffUsername);
        int mvnoId = commonGetAPI.getMvnoId(Constant.STAFF_USERNAME);

        // *********** Service Area Details *****************
        String ServiceArea = customerDetails.get("Servicearea").toLowerCase();
        Integer serviceareaId = serviceAreaDetails.get(ServiceArea);

        // Safe read leadMaster
        JSONObject leadMaster = response.optJSONObject("leadMaster");
        int leadId = (leadMaster != null) ? leadMaster.optInt("id", 0) : 0;
        String fName = (leadMaster != null) ? leadMaster.optString("firstname", "") : "";

        // Payload
        approved.put("approveRequest", true);
        approved.put("buId", JSONObject.NULL);
        approved.put("currentLoggedInStaffId", staffId);
        approved.put("firstname", fName);
        approved.put("id", leadId);
        approved.put("mvnoId", mvnoId);
        approved.put("serviceareaid", serviceareaId);
        approved.put("flag", "Approve");
        approved.put("remark", "Approved Lead");
        approved.put("nextTeamMappingId", JSONObject.NULL);
        approved.put("status", "Inquiry");
        approved.put("username", JSONObject.NULL);
        approved.put("rejectedReasonMasterId", JSONObject.NULL);
        approved.put("teamName", JSONObject.NULL);

        String body = new JSONObject(approved).toString();

        // ========== SMART RETRY LOGIC (same as previous code) ==========
        int maxRetries = 5;
        int retryCount = 0;
        long baseDelay = 300; // 300ms

        while (retryCount < maxRetries) {
            long start = System.currentTimeMillis();

            try {
                JSONObject jsonResponse = httpPut(apiURLPut, body);
                int status = jsonResponse.optInt("responseCode", -1);

                long taken = System.currentTimeMillis() - start;

                if (status == 200) {
                    String msg = "Lead approved successfully for Lead Name: " + fName +
                            " Lead No: " + leadNo + " | Time = " + taken + "ms";
                    System.out.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Lead Approved", msg);
                    return;
                } else if (status == 417) {
                    retryCount++;
                    long delay = baseDelay * (long) Math.pow(2, retryCount);

                    String msg = "Lead not ready (417) for LeadNo: " + leadNo +
                            ", retry " + retryCount + " after " + delay +
                            "ms | Taken = " + taken + "ms";
                    System.out.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Lead Retry", msg);

                    Thread.sleep(delay);
                } else {
                    String err = "Failed Lead Approval for LeadNo: " + leadNo +
                            " Status = " + status + " | Taken = " + taken + "ms";
                    log.error(err);
                    Utility.printLog("execution.log", logModuleName, "Lead Approval Failed", err);
                    return;
                }

            } catch (Exception e) {
                retryCount++;
                long delay = baseDelay * (long) Math.pow(2, retryCount);

                String msg = "Exception while approving LeadNo: " + leadNo +
                        ", attempt " + retryCount + ": " + e.getMessage();
                System.err.println(msg);
                Utility.printLog("execution.log", logModuleName, "Lead Retry Error", msg);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // ========== FINAL FAILURE (after all retries) ==========
        String errorMessage = "Lead Approval FAILED after " + maxRetries +
                " retries for LeadNo: " + leadNo;
        log.error(errorMessage);
        Utility.printLog("execution.log", logModuleName, "Lead Approval Failed", errorMessage);
    }


    private boolean isValidStaff(Map<String, String> customerDetailsMap) {
        String staff = customerDetailsMap.get("Staff");
        return staff != null && !staff.trim().isEmpty();
    }


    // Utility method to safely check for null or empty

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    //================Assign Staff===================================

//    private void assignStaff(JSONObject response, Map<String, String> customerDetails, String LeadNo, Map<String, Integer> serviceAreaDetails) throws Exception {
//
//        CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//        // Staff ID from Customer Excel
//        int staffId = commonGetAPI.getStaffId(customerDetails.get("Staff"));
//
//        // mvnoId from constant staff login
//        int mvnoId = commonGetAPI.getMvnoId(Constant.STAFF_USERNAME);
//
//
//        // ***********************
//        // 1. Build API URL dynamically
//        // ***********************
//        String apiURL = getAPIURL(
//                "cpm/teamHierarchy/assignFromStaffListForLead?eventName=LEAD&nextAssignStaff=" + staffId
//        );
//
//        // ***********************
//        // 2. Build API Body
//        // ***********************
//        Map<String, Object> apiBody = new HashMap<>();
//        // *********** Service Area Details *****************
//        String ServiceArea = customerDetails.get("Servicearea").toLowerCase();
//        Integer serviceareaId = serviceAreaDetails.get(ServiceArea);
//
//        JSONObject firstname = response.optJSONObject("leadMaster");
//        String fName = (firstname != null) ? firstname.optString("firstname", "") : "";
//        apiBody.put("firstname", fName);
//
//        apiBody.put("approveRequest", true);
//        apiBody.put("buId", JSONObject.NULL);
//        apiBody.put("currentLoggedInStaffId", staffId);
//
//        // Safe read
//        JSONObject leadMaster = response.optJSONObject("leadMaster");
//        int leadId = (leadMaster != null) ? leadMaster.optInt("id", 0) : 0;
//        apiBody.put("id", leadId);
//        apiBody.put("mvnoId", mvnoId);
//        apiBody.put("remark", "Migration Data");
//        apiBody.put("serviceareaid", serviceareaId);
//        apiBody.put("flag", "Approve");
//        apiBody.put("nextTeamMappingId", JSONObject.NULL);
//        apiBody.put("status", "Inquiry");
//        apiBody.put("teamName", JSONObject.NULL);
//        apiBody.put("username", JSONObject.NULL);
//        apiBody.put("rejectedReasonMasterId", JSONObject.NULL);
//
//        // ***********************
//        // 3. Convert Map to JSON string
//        // ***********************
//        String bodyJson = new JSONObject(apiBody).toString();
//
//        // ***********************
//        // 4. Execute POST call
//        // ***********************
//        JSONObject jsonResponse = httpPost(apiURL, bodyJson);
//
//        // ***********************
//        // 5. Validate Status
//        // ***********************
//        int status = jsonResponse.optInt("responseCode", -1);
//
//        if (status == 200) {
//            System.out.println("Assign Staff Success for Customer: " + LeadNo);
//        } else {
//            System.err.println("Assign Staff FAILED for Customer: " + LeadNo + " Status=" + status);
//        }
//    }


    private void assignStaff(JSONObject response, Map<String, String> customerDetails, String LeadNo,
                             Map<String, Integer> serviceAreaDetails) throws Exception {

        CommonGetAPI commonGetAPI = new CommonGetAPI();

        // Staff ID from Customer Excel
        int staffId = commonGetAPI.getStaffId(customerDetails.get("Staff"));

        // MVNO ID from logged-in staff
        int mvnoId = commonGetAPI.getMvnoId(Constant.STAFF_USERNAME);

        // ***********************
        // Build API URL
        // ***********************
        String apiURL = getAPIURL(
                "cpm/teamHierarchy/assignFromStaffListForLead?eventName=LEAD&nextAssignStaff=" + staffId
        );

        // ***********************
        // Build API Body
        // ***********************
        Map<String, Object> apiBody = new HashMap<>();

        // Service Area ID
        String ServiceArea = customerDetails.get("Servicearea").toLowerCase();
        Integer serviceareaId = serviceAreaDetails.get(ServiceArea);

        JSONObject leadMaster = response.optJSONObject("leadMaster");
        String fName = (leadMaster != null) ? leadMaster.optString("firstname", "") : "";
        int leadId = (leadMaster != null) ? leadMaster.optInt("id", 0) : 0;

        apiBody.put("firstname", fName);
        apiBody.put("approveRequest", true);
        apiBody.put("buId", JSONObject.NULL);
        apiBody.put("currentLoggedInStaffId", staffId);
        apiBody.put("id", leadId);
        apiBody.put("mvnoId", mvnoId);
        apiBody.put("remark", "Migration Data");
        apiBody.put("serviceareaid", serviceareaId);
        apiBody.put("flag", "Approve");
        apiBody.put("nextTeamMappingId", JSONObject.NULL);
        apiBody.put("status", "Inquiry");
        apiBody.put("teamName", JSONObject.NULL);
        apiBody.put("username", JSONObject.NULL);
        apiBody.put("rejectedReasonMasterId", JSONObject.NULL);

        String bodyJson = new JSONObject(apiBody).toString();

        // ==============================================================
        // SMART RETRY LOGIC (same behavior as Payment + Lead Approval)
        // ==============================================================

        int maxRetries = 5;
        int retryCount = 0;
        long baseDelay = 300; // 300ms

        while (retryCount < maxRetries) {
            long start = System.currentTimeMillis();

            try {
                JSONObject jsonResponse = httpPost(apiURL, bodyJson);
                int status = jsonResponse.optInt("responseCode", -1);
                long taken = System.currentTimeMillis() - start;

                if (status == 200) {
                    String msg = "Assign Staff SUCCESS for Customer: " + LeadNo +
                            " | Time = " + taken + "ms";
                    System.out.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Assign Staff Success", msg);
                    return;
                }

                else if (status == 417) {
                    retryCount++;
                    long delay = baseDelay * (long) Math.pow(2, retryCount);

                    String msg = "Assign Staff NOT READY (417) for Customer: " + LeadNo +
                            " Retry " + retryCount + " after " + delay + "ms"
                            + " | Taken = " + taken + "ms";

                    System.out.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Assign Staff Retry", msg);

                    Thread.sleep(delay);
                }

                else {
                    String msg = "Assign Staff FAILED for Customer: " + LeadNo +
                            " Status=" + status + " | Taken = " + taken + "ms";

                    System.err.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Assign Staff Failed", msg);
                    return;
                }

            } catch (Exception e) {

                retryCount++;
                long delay = baseDelay * (long) Math.pow(2, retryCount);

                String msg = "Exception during Assign Staff for Customer: " + LeadNo +
                        ", Attempt " + retryCount + ": " + e.getMessage();

                System.err.println(msg);
                Utility.printLog("execution.log", logModuleName, "Assign Staff Retry Error", msg);

                Thread.sleep(delay);
            }
        }

        // ==============================================================
        // FINAL FAILURE AFTER ALL RETRIES
        // ==============================================================

        String finalMsg = "Assign Staff FAILED after " + maxRetries +
                " retries for Customer: " + LeadNo;

        System.err.println(finalMsg);
        Utility.printLog("execution.log", logModuleName, "Assign Staff Final Failure", finalMsg);
    }



}
