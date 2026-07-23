package SavanaCustomer;

import staff.Staff;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MigrationDataBase.DataBaseUpdateScript;
import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import groovy.util.logging.Slf4j;
import temp.UpdateSheet;

public class CafCustomer extends RestExecution {

    private final String jdbcUrl1 = Constant.URLCONVERGE;
    private final String jdbcUrl2 = Constant.URLREVENUE;
    private final String dbUser = Constant.USERNAME;
    private final String dbPassword = Constant.PASSWORD;

    int iterationCounter = 0;
    private static final Logger log = LoggerFactory.getLogger(Savana_Thread_Customer.class);

    private static String logFileName = "savannaCaf.log";
    private static String logModuleName = "CreatesavanaCafCustomer";

    int thread_size = Constant.THREAD_POOL_SIZE;
    int batchSize = Constant.BATCH_SIZE;
    int retryLimit = Constant.RETRY_LIMIT; // remove  -->31 dec
    int retryDelayMS = Constant.RETRY_DELAY_MS; //remove --> 31 dec

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);

    private static XSSFWorkbook workbook = null;
    //		ExecutorService executor = Executors.newCachedThreadPool();
    ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Use fixed thread pool

    public ArrayList<Object> createPrepaidCustomer(Map<String, String> customerDetailsMap, Map<String, Integer> serviceAreaIdAll) {
        ArrayList<Object> objects = new ArrayList<>();
        try {
            String rowIndex = customerDetailsMap.get("RowIndex");
            String apiURL = getAPIURL("cpm/customers");

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
                                   Map<String, String> customerDetailsMap) {
        try {
            int status = response.getInt("status");
            String userName = customerDetailsMap.get("Username") + " - " + elapsedTime;

            if (!response.has("ERROR")) {
                if (status == 200) {
                    successCount.incrementAndGet();
                    String message = "New Customer added successfully - " + userName;
                    System.out.println(message);
                    Utility.printLog("execution.log", logModuleName, "Success", message);

                    // enhanced
                    // Extract the customer object once
                    JSONObject customer = response.getJSONObject("customer");

                    // Extract the first element from planMappingList
                    JSONArray planMappingList = customer.getJSONArray("planMappingList");
                    JSONObject firstPlanMapping = planMappingList.getJSONObject(0);

                    // Extract the required values
                    int cprId = firstPlanMapping.getInt("id");
                    int planMappingId = firstPlanMapping.getInt("custServiceMappingId");
                    String customerId = customer.get("id").toString();
                    String createdbyname = customer.get("createdByName").toString();
                    String createdbyid = customer.get("createdById").toString();

                    // Create the column and value string
                    String columnAndValue = "cprid:" + cprId + "#" + "MigrationStatus:Success";
                    // approve
                    if (isValidStaff(customerDetailsMap)) {
                        try {
                            // Perform CAF approval
//                            approveCaf(customerDetailsMap, customerId);

                        } catch (Exception e) {
                            log.error("Error occurred while approving CAF for Customer ID: {}: {}", customerId, e.getMessage(), e);
                        }

                    } else {
                        System.out.println("Staff is empty caf will not approved and username is : " + userName);
                    }


                    try (Connection converge = DriverManager.getConnection(jdbcUrl1, dbUser, dbPassword);
                         Connection radius = DriverManager.getConnection(jdbcUrl2, dbUser, dbPassword)) {
                        DatabaseUpdationCaf dataBaseUpdateScript = new DatabaseUpdationCaf();
                        dataBaseUpdateScript.updateCustomerDataInDatabases(converge, radius, customerId, String.valueOf(cprId), String.valueOf(planMappingId), customerDetailsMap, createdbyname, createdbyid);
                        log.info("****************************************Success With Count :::: {}", successCount.get());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        log.error("Error During Database Update: " + e.getMessage());
                    }

                    UpdateSheet us = new UpdateSheet();
                    us.setRowList(rowIndex, columnAndValue);
                } else if (status == 406) {
                    String error = response.getString("responseMessage") + " - " + userName;
                    System.out.println(error);
                    Utility.printLog("execution.log", logModuleName, "Already Exists", error);
                    failureCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                    String message = response.get("ERROR") + " - " + userName;
                    Utility.printLog("execution.log", logModuleName, "ERROR", message);
                }
            } else {
                failureCount.incrementAndGet();
                String message = response.get("ERROR") + " - " + userName;
                Utility.printLog("execution.log", logModuleName, "ERROR", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (handleAPIResponse)...." + e.getMessage());
        }
    }


    public void createPrepaidCustomer(List<Map<String, String>> customerMapList, Map<String, Integer> serviceAreaIdAll) {
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Thread pool size is dynamically set
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        UpdateSheet us = new UpdateSheet();

        us.setActiveSheetName("CafCustomer");
        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
        CommonGetAPI commonGetAPI = new CommonGetAPI();

        List<Future<ArrayList<Object>>> futures = new ArrayList<>();

        // Submit tasks for concurrent processing
        for (Map<String, String> customerDetails : customerMapList) {
            String userName = customerDetails.get("Username");
            String row = customerDetails.get("RowIndex");

            futures.add(executorService.submit(() -> {
                StopWatch sw = new StopWatch();
                sw.start();
                ArrayList<Object> result = new ArrayList<>();

                try {
                    if (!commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
                        Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, customerDetails.toString());
                        result = createPrepaidCustomer(customerDetails, serviceAreaIdAll); // Call API to create customer
                    } else {
                        System.out.println("Savanna Customer UserName already exists! - " + userName + " | " + sw.getTime());
                    }

                    // Batch Excel updates
                    batchToWrite.add(customerDetails);

                    // Write to Excel in batches
                    if (batchToWrite.size() >= batchSize) {
                        //rw.setMultipleColumnInActiveSheet(batchToWrite);
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
                handleAPIResponse(jsonObject, rowIndex, stopWatch.getTime(), customerDetailsMap);
                System.out.println("Task Completed Successfully: " + future.isDone());
            } catch (Exception e) {
                System.err.println("Error retrieving task result: " + e.getMessage());
            }
        }

        // Gracefully shut down executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(90, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // Write remaining batch to Excel
        if (!batchToWrite.isEmpty()) {
            //  ReadWriteExcelFile rw = new ReadWriteExcelFile();
            //  rw.setMultipleColumnInActiveSheet(batchToWrite);
        }

        System.out.println("Final migration step completed.");
        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
    }


    public List<Map<String, String>> readUniquePrepaidCustomerList() {

        String sheetName = "CafCustomer";  // This is sheet name.
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>(); //create a empty list that later will hold the customer data

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String userName = cellValue.get("UserName");
            String mStatus = cellValue.get("MigrationStatus");

            if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {
                String resiteredDate = Utility.convertDate(cellValue.get("Registered"));// Test with a valid dat
                String startDatetemp = Utility.convertDate(cellValue.get("Renewed"));
                String lastLogingDate = Utility.convertDate(cellValue.get("LastLogin"));

                String endDate = "";

                // Check if the "Expires" key exists in the map and if the value is not null or empty
                if (StringUtils.isNotEmpty(safeTrim(cellValue.get("Expires")))) {
                    // If it's not empty or null, convert the date format using the Utility method
                    endDate = Utility.convertEndDateFormat(safeTrim(cellValue.get("Expires")));
                }

                valuemap.put("RowIndex", safeTrim(cellValue.get("Sno")));
                valuemap.put("Title", safeTrim(cellValue.get("Title")));
                valuemap.put("Name", safeTrim(cellValue.get("Name")));
                valuemap.put("Username", safeTrim(cellValue.get("UserName")));
                valuemap.put("Password", safeTrim(cellValue.get("Password")));
                valuemap.put("PrimaryMobile", safeTrim(cellValue.get("Phone")));
                valuemap.put("Email", safeTrim(cellValue.get("Email")));
                valuemap.put("Servicearea", safeTrim(cellValue.get("Servicearea")));
                valuemap.put("UnitNo", safeTrim(cellValue.get("UnitNo")));
                valuemap.put("status", safeTrim(cellValue.get("Status")));
                valuemap.put("Plan", safeTrim(cellValue.get("Plan")));
                valuemap.put("Staff", safeTrim(cellValue.get("Staff")));
                valuemap.put("primaryIndex", safeTrim(cellValue.get("PrimaryIndex")));
                // Updates
                valuemap.put("Notes", safeTrim(cellValue.get("Updates")));
                // After Add ACt project  -->
                valuemap.put("IpAddress", safeTrim(cellValue.get("IpAddress")));
                valuemap.put("Branch", safeTrim(cellValue.get("Branch")));
                valuemap.put("Address", safeTrim(cellValue.get("Address")));
                valuemap.put("Municipality", safeTrim(cellValue.get("Road")));
                valuemap.put("Ward", safeTrim(cellValue.get("Building")));
                valuemap.put("Landmark", safeTrim(cellValue.get("Landmark")));
                valuemap.put("OLT", safeTrim(cellValue.get("OLT")));
                valuemap.put("Service", safeTrim(cellValue.get("Service")));
                valuemap.put("Location", safeTrim(cellValue.get("Location")));  // decription
                valuemap.put("LastLogin", lastLogingDate);
                valuemap.put("Updates", safeTrim(cellValue.get("Updates"))); // note new feature
                valuemap.put("AccountNo", safeTrim(cellValue.get("AccountNo")));
                valuemap.put("Registered", safeTrim(resiteredDate));
                valuemap.put("startdate", safeTrim(startDatetemp));
                valuemap.put("enddate", safeTrim(endDate));
                valuemap.put("cprid", safeTrim(cellValue.get("cprid")));
                customerMapList.add(valuemap);
            }
        }
//         System.out.println(customerMapList);
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

//                log.info("Generated Prepaid Customer JSON for row {}: {}",
//                        customerDetails.getOrDefault("RowIndex", "?"),
//                        customerJsonObject.toString(2));


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
                firstName = "Unknown"; // Or some default value
                lastName = "Unknown";  // Or some default value
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


            customerJsonObject.put("title", customerDetails.get("Title"));
            customerJsonObject.put("firstname", firstName);
            customerJsonObject.put("lastname", lastName);
            customerJsonObject.put("username", customerDetails.get("Username"));

            customerJsonObject.put("password", customerDetails.get("Password"));

            // mobile number------------------------------------------------->
            customerJsonObject.put("countryCode", "+256");//Static
            // Handle mobile number
            String mobNo = customerDetails.get("PrimaryMobile");
            String updatedNumber = "";

            // Check if mobNo is not null or empty before proceeding
            if (mobNo != null && !mobNo.trim().isEmpty()) {
                // Skip the first 3 characters (country code 256)
                // Handle number
                if (mobNo.contains("E")) {
                    // Convert the scientific notation to a regular number string
                    String regularNumber = String.format("%.0f", Double.parseDouble(mobNo));
                    // Remove the first 3 digits (country code 256)
                    updatedNumber = regularNumber.substring(3);
                } else {
                    // If not in scientific notation, directly remove the first 3 digits (country code 256)
                    updatedNumber = mobNo.replaceAll("[^0-9]", "").substring(3);
                }
            } else {
                // If mobNo is null or empty, set default value
                updatedNumber = "99999999";
            }

            // Ensure that updatedNumber is not null or empty
            customerJsonObject.put("mobile", (updatedNumber != null && !updatedNumber.isEmpty()) ? updatedNumber : "99999999");

            //--------------------------------------------------------------------------------->
            customerJsonObject.put("phone", "");

            customerJsonObject.put("fax", ""); // --changes
            // Handle email
            String email = customerDetails.get("Email");
            customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "savanna@123gmail.com");

            customerJsonObject.put("pan", "");
            customerJsonObject.put("contactperson", firstName);

            customerJsonObject.put("calendarType", "English");
            if (customerDetails.get("UnitNo") != null && !customerDetails.get("UnitNo").isEmpty()) {
                customerJsonObject.put("blockNo", customerDetails.get("UnitNo")); // here block no will get from sheet .
            }
            customerJsonObject.put("dunningCategory", "Gold");

            customerJsonObject.put("cafno", "");

            customerJsonObject.put("birthDate", JSONObject.NULL); // -->here we need to change date format.

            customerJsonObject.put("staffId", ""); // -->sar

            // Handle status
            String status = customerDetails.get("status");
            switch (status != null ? status : "") {
                case "NewActivation":
                    //    customerJsonObject.put("status", "Active");  // for Caf status should be NewActivation
                    customerJsonObject.put("status", "NewActivation");
                    break;
                case "In Active":
                    customerJsonObject.put("status", "In Active");
                    break;
                case "Suspend":
                    customerJsonObject.put("status", "Suspend");
                    break;
                default:
                    customerJsonObject.put("status", "Terminate");
                    break;
            }

            customerJsonObject.put("parentCustomerId", "");
            customerJsonObject.put("invoiceType", JSONObject.NULL);


            customerJsonObject.put("custlabel", "customer");
            customerJsonObject.put("salesremark", "");


            // *********** Service Area Details *****************
            String ServiceArea = customerDetails.get("Servicearea").toLowerCase();

            // System.out.println(ServiceArea+"--------------------------------");
            //int serviceAreaId = commonGetAPI.getServiceAreaIdList(ServiceArea).get(0);
            Integer serviceareaId = serviceAreaDetails.get(ServiceArea);
            //	System.out.println(serviceAreaDetails);
            //System.out.println(serviceAreaDetails.get("Serv01"));


            customerJsonObject.put("serviceareaid", serviceareaId);

            customerJsonObject.put("Branch", JSONObject.NULL);
            customerJsonObject.put("partnerid", 1);

            String branchName = customerDetails.get("Branch");


            if (!"".equals(branchName)) {
                int branchId = commonGetAPI.getBranchIdList(branchName).get(0);
                customerJsonObject.put("Branch", branchId);
            }


            // -- Customer Address Details --

            List<JSONObject> addressJsonObjectList = new ArrayList<JSONObject>();
            JSONObject presentAddressDetail = getCustomerAddressJson("Present", customerDetails);
            if (presentAddressDetail != null) {
                addressJsonObjectList.add(presentAddressDetail);
            }

            customerJsonObject.put("addressList", addressJsonObjectList);

            customerJsonObject.put("valleyType", "");
            customerJsonObject.put("customerArea", "");
            customerJsonObject.put("latitude", "");
            customerJsonObject.put("longitude", "");

            customerJsonObject.put("valleyType", "");

            customerJsonObject.put("customerArea", "");


            customerJsonObject.put("latitude", "");


            customerJsonObject.put("longitude", "");


//				// ************ Network Location Details *********************
//
//
//
//				customerJsonObject.put("oltid", "");
//				customerJsonObject.put("masterdbid", "");
//				customerJsonObject.put("splitterid", "");
//				/*********** ACT **************************/
//				customerJsonObject.put("primaryDNS", "");
//				customerJsonObject.put("primaryIPv6DNS", "");
//				customerJsonObject.put("secondaryDNS", "");
//				customerJsonObject.put("secondaryIPv6DNS", "");
//				/*************************************************/


            // -- Radius Service Details

            customerJsonObject.put("framedIp", customerDetails.get("IpAddress"));  // -->it will take fromm sheet
            customerJsonObject.put("framedIpBind", "");
            customerJsonObject.put("nasPort", JSONObject.NULL);
            customerJsonObject.put("ipPoolNameBind", "");

            customerJsonObject.put("failcount", 0);
            customerJsonObject.put("isCustCaf", "yes");
            customerJsonObject.put("servicetype", "");

            customerJsonObject.put("isParentLocation", "");
            customerJsonObject.put("locations", JSONObject.NULL);
            customerJsonObject.put("maxconcurrentsession", "");
            customerJsonObject.put("nasIpAddress", "");
            customerJsonObject.put("nasPort", "");

            customerJsonObject.put("altmobile", "");
            customerJsonObject.put("billableCustomerId", "");


            customerJsonObject.put("gst", "");
            customerJsonObject.put("aadhar", "");
            customerJsonObject.put("addparam1", "");
            customerJsonObject.put("addparam2", "");
            customerJsonObject.put("addparam3", customerDetails.get("primaryIndex"));
            customerJsonObject.put("addparam4", "");

            customerJsonObject.put("passportNo", "");
            customerJsonObject.put("tinNo", "");
            customerJsonObject.put("parentQuotaType", "");
            JSONObject paymentJson = new JSONObject();

            paymentJson.put("amount", 0);
            paymentJson.put("paymode", "");
            paymentJson.put("referenceno", "");
            paymentJson.put("paymentdate", "");
            customerJsonObject.put("paymentDetails", paymentJson);

            // --PlanMappingDetails
            customerJsonObject.put("istrialplan", false);

            List<JSONObject> planJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("planMappingList", planJsonObjectList);

            String planCategory = "Individual";

            // --Individual Plan
            if (planCategory.equalsIgnoreCase("Individual")) {


                String billableCustomerId = null;
                float discountPercentage = 0;
                String discountType = null;
                String discountExpiryDate = null;
                boolean invoiceToOrg = false;
                boolean istrialplan = false;
                customerJsonObject.put("billableCustomerId", "");
                customerJsonObject.put("billTo", "CUSTOMER");

                customerJsonObject.put("discount", 0);
                customerJsonObject.put("discountType", "One-time");
                customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
                customerJsonObject.put("planPurchaseType", "individual");
                customerJsonObject.put("vlan_id", "");
                customerJsonObject.put("istrialplan", istrialplan);


                String service = customerDetails.get("Service");


                String plan = customerDetails.get("Plan");


                discountType = "";
                discountExpiryDate = "";
                String tempDiscountPercentage = "";

                JSONObject planDetailJsonObject = new JSONObject();

                int planId = commonGetAPI.getPlanId(plan);
                int serviceId = commonGetAPI.getServiceId(service);

                String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

                String serviceName = planDetails[0];
                float offerPrice = Float.valueOf(planDetails[1]);

                int validity = Integer.parseInt(planDetails[2]);

                float flatAmount = offerPrice;


                flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
                // customerJsonObject.put("flatAmount", flatAmount);
                customerJsonObject.put("flatAmount", "");

                planDetailJsonObject.put("newAmount", "");
                float newAmount = offerPrice;


                planDetailJsonObject.put("planId", planId);
                planDetailJsonObject.put("service", serviceName);
                planDetailJsonObject.put("validity", validity);
                planDetailJsonObject.put("discount", discountPercentage);
                planDetailJsonObject.put("billTo", "CUSTOMER");
                planDetailJsonObject.put("billableCustomerId", "");

                planDetailJsonObject.put("newAmount", "");
                planDetailJsonObject.put("offerPrice", offerPrice);
                planDetailJsonObject.put("invoiceType", "");
                planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);
                planDetailJsonObject.put("istrialplan", JSONObject.NULL);
                planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
                planDetailJsonObject.put("discountType", discountType);
                planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
                planDetailJsonObject.put("serviceId", serviceId);

                planDetailJsonObject.put("serialNumber", "");
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
            customerJsonObject.put("customerSector", "");
            customerJsonObject.put("customerSubSector", "");
            customerJsonObject.put("customerSubType", "");
            customerJsonObject.put("customerType", "");
            customerJsonObject.put("department", "");
            customerJsonObject.put("discount", JSONObject.NULL);
            customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
            customerJsonObject.put("discountType", JSONObject.NULL);
            customerJsonObject.put("framedIpv6Address", "");

            // Add pojo for Act-->
//				customerJsonObject.put("delegatedprefix", "");
//				customerJsonObject.put("earlybilldate", "");
//				customerJsonObject.put("framedIPNetmask", "");
//				customerJsonObject.put("framedIPv6Prefix", "");
//				customerJsonObject.put("framedroute", "");
//				customerJsonObject.put("gatewayIP", ""); // gateway
//
//				customerJsonObject.put("macRetentionPeriod", "");  //static
//				customerJsonObject.put("macRetentionUnit", "");  //static
//


//				customerJsonObject.put("mac_auth_enable", true);
//				customerJsonObject.put("mac_provision", true);

            // ------>

            // -- Over Direct Charge Mapping

            List<JSONObject> chargeJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("overChargeList", chargeJsonObjectList);

            // sarfraz -->
            List<JSONObject> customerLocationsJsonObject = new ArrayList<JSONObject>();
            customerJsonObject.put("customerLocations", customerLocationsJsonObject);

            // --Customer MAC Addresses Mapping

            List<JSONObject> macJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("custMacMapppingList", macJsonObjectList);

            List<JSONObject> mappingJsonObjectList = new ArrayList<JSONObject>();
            customerJsonObject.put("custIpMappingList", mappingJsonObjectList);
            jsonString = customerJsonObject.toString();

        } // take care of this brace
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }


    private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {
        CommonGetAPI commonGetAPI = new CommonGetAPI();
        JSONObject addressDetailJsonObject = new JSONObject();

        if ("Present".equalsIgnoreCase(addressType)) {
            String address = customerDetails.get("Address");
            String landmark = customerDetails.get("Landmark");
            String ward = customerDetails.get("Ward");
            String municipality = customerDetails.get("Municipality");

            if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() && !municipality.isEmpty()) {

//                int pincodeid = commonGetAPI.getPincodeId(municipality);
//                int areaId = commonGetAPI.getAreaId(ward);

                String wardHeirarchyDetail = commonGetAPI.getMasterDetailsByAreaName(ward);

                String[] detail = wardHeirarchyDetail.split(":");


                addressDetailJsonObject.put("addressType", addressType);
                addressDetailJsonObject.put("landmark", address);
                // addressDetailJsonObject.put("landmark1", JSONObject.NULL);
                addressDetailJsonObject.put("areaId", Integer.parseInt(detail[0]));
                addressDetailJsonObject.put("pincodeId", Integer.parseInt(detail[4]));
                addressDetailJsonObject.put("cityId", Integer.parseInt(detail[1]));
                addressDetailJsonObject.put("stateId", Integer.parseInt(detail[3]));
                addressDetailJsonObject.put("countryId", Integer.parseInt(detail[2]));
                addressDetailJsonObject.put("version", "NEW");
            } else {
                return null;
            }
        }

        return addressDetailJsonObject;
    }
    /*
    private String getCafApprove(Map<String, String> customerDetails, String custid) {
        // String jsonString1 = null;
        JSONObject approved = new JSONObject();
        String staff=customerDetails.get("Staff");
        CommonGetAPI commonGetAPI = new CommonGetAPI();
        int staffId=commonGetAPI.getStaffId(staff);

        approved.put("custcafId",custid );
        approved.put("nextStaffId", JSONObject.NULL);
        approved.put("flag", "approved");
        approved.put("remark", "Approved Caf");
        approved.put("staffId", staffId);
        return approved.toString();

    }

     */

    private Map<String, Object> getCafApprove(Map<String, String> customerDetails, String custid) {
        Map<String, Object> approved = new HashMap<>();

        String staff = customerDetails.get("Staff");
        CommonGetAPI commonGetAPI = new CommonGetAPI();
        int staffId = commonGetAPI.getStaffId(staff);

        approved.put("custcafId", custid);
        approved.put("nextStaffId", ""); // keep empty instead of null for form-data
        approved.put("flag", "approved");
        approved.put("remark", "Approved Caf");
        approved.put("staffId", staffId);

        return approved;
    }


    private boolean isValidStaff(Map<String, String> customerDetailsMap) {
        String staff = customerDetailsMap.get("Staff");
        return staff != null && !staff.trim().isEmpty();
    }

    private void approveCaf(Map<String, String> customerDetailsMap, String customerId) throws Exception {
        String apiURLPut = getAPIURL("cpm/approveCaf");
        Map<String, Object> apiBodyPut = getCafApprove(customerDetailsMap, customerId);

        // Make the HTTP PUT request
        JSONObject JSONResponseBody1 = httpPutcaf(apiURLPut, apiBodyPut);
        int status1 = JSONResponseBody1.optInt("status", -1); // safer: won't throw if missing

        if (status1 == 200) {
            String message = "CAF approved successfully for Customer ID: " +
                    customerDetailsMap.get("Username") + " : " + customerId;
            System.out.println(message);
            Utility.printLog("execution.log", logModuleName, "CAF Approved", message);
        } else {
            String errorMessage = "Failed to approve CAF for Customer ID: " +
                    customerId + ". Status: " + status1;
            log.error(errorMessage);
            Utility.printLog("execution.log", logModuleName, "CAF Approval Failed", errorMessage);
        }
    }


}
