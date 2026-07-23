package ticketsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import customer.AssignInventory;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
import utility.Utility;
import temp.UpdateSheet;
import MigrationDataBase.DataBaseUpdateScript; // if you have a DB class for tickets, create/replace accordingly

/**
 * Ticket class upgraded to:
 * - fixed thread pool
 * - retry logic with exponential backoff for POST
 * - returns task result to main thread and handles response similarly to CafCustomer
 * - batched excel updates and logging
 *
 * NOTE: implement DatabaseUpdationTicket.updateTicketDataInDatabases(...) if DB updates are needed.
 */
public class TicketCreationwithUpdation extends RestExecution {

    private static final Logger log = LoggerFactory.getLogger(TicketCreationwithUpdation.class);

    private final String jdbcUrl3 = Constant.URLTICKETMANAGEMENT;
    private final String dbUser = Constant.USERNAME;
    private final String dbPassword = Constant.PASSWORD;

    private static String logFileName = "ticketdata.log";
    private static String logModuleName = "Ticket";
    private UpdateSheet updateSheet = new UpdateSheet();
    private static String basePath = Constant.BASE_PATH + "\\TestData\\input\\uploads\\ticket\\";

    // Threading / batch / retry config (use values from Constant)
    int thread_size = Constant.THREAD_POOL_SIZE;
    int batchSize = Constant.BATCH_SIZE;
    int retryLimit = Constant.RETRY_LIMIT;
    int retryDelayMS = Constant.RETRY_DELAY_MS;

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);

    /**
     * Single-ticket creation with retry/exponential backoff. Returns:
     * [0] JSONObject response
     * [1] String rowIndex
     * [2] StopWatch sw
     * [3] Map<String,String> ticketDetails
     */
    private ArrayList<Object> createTicketWithRetry(Map<String, String> ticketDetails) {
        ArrayList<Object> objects = new ArrayList<>();
        try {
            String rowIndex = ticketDetails.get("RowIndex");
            String apiURL = getAPIURL("TicketManagement/case/save");
            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            String apiBody = getTicketJson(ticketDetails);
            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

            if (apiBody != null && !apiBody.isEmpty()) {
                StopWatch sw = new StopWatch();
                sw.start();

                JSONObject JSONResponseBody = null;
                boolean success = false;
                int attempts = 0;

                while (attempts < retryLimit && !success) {
                    try {
                        String fileName = ticketDetails.get("FileNameToAttach");
                        if (fileName != null && !"".equals(fileName)) {
                            fileName = basePath + "\\" + fileName;
                        } else {
                            fileName = null;
                        }

                        JSONResponseBody = httpPostFormData2(apiURL, apiBody, fileName);
                        success = true;

                        String response = JSONResponseBody.toString(4);
                        Utility.printLog(logFileName, logModuleName, "Response", response);

                    } catch (Exception e) {
                        attempts++;
                        Utility.printLog(logFileName, logModuleName, "ERROR", "API call error attempt " + attempts + ": " + e.getMessage());
                        if (attempts >= retryLimit) {
                            Utility.printLog("execution.log", logModuleName, "ERROR", "API call failed after retries: " + e.getMessage());
                        }
                        try {
                            Thread.sleep(retryDelayMS * (long) Math.pow(2, attempts)); // exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                objects.add(JSONResponseBody);
                objects.add(rowIndex);
                objects.add(sw);
                objects.add(ticketDetails);
                return objects;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.printLog("execution.log", logModuleName, "ERROR", "createTicketWithRetry exception: " + e.getMessage());
            failureCount.incrementAndGet();
        }
        return objects;
    }

    /**
     * Handle the API response the same way as CafCustomer.handleAPIResponse:
     * - increments counters
     * - extracts important values (case id etc.)
     * - optionally perform DB update & write to excel sheet
     */
    private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime,
                                   Map<String, String> ticketDetails) {
        try {

            String migrationStatus = "Initial";
            String migrationDetail = "Initial";

            if (response == null) {
                failureCount.incrementAndGet();
                Utility.printLog("execution.log", logModuleName, "ERROR", "Null response for row: " + rowIndex);
                return;
            }

            // Log the full response for debugging
            Utility.printLog(logFileName, logModuleName, "DEBUG", "Full API Response for row " + rowIndex + ": " + response.toString());

            int status = response.optInt("status", -1);
            String userName = ticketDetails.get("Username") + " | row: " + rowIndex + " | elapsed(ms): " + elapsedTime;

            // Extract case/ticket ID from multiple possible keys
            String caseId = null;
            if (response.has("case") && response.optJSONObject("case") != null) {
                caseId = response.optJSONObject("case").optString("id", null);
            } else if (response.has("data") && response.optJSONObject("data") != null) {
                caseId = response.optJSONObject("data").optString("caseId", null);
            } else if (response.has("ticketId")) {
                caseId = response.optString("ticketId", null);
            }

            // Update ticketDetails map with caseId for DB update
            if (caseId != null) {
                ticketDetails.put("CaseId", caseId);
            }
            int responsecode = response.optInt("responseCode", -1);

            if (responsecode == 200) {
                successCount.incrementAndGet();
                String message = "Ticket created successfully - " + userName + " | caseId: " + caseId;
                System.out.println(message);
                Utility.printLog("execution.log", logModuleName, "Success", message);
                migrationStatus = "Success";
                migrationDetail = message;

            } else if (status == 406) {
                String error = response.optString("responseMessage", "Already exists") + " - " + userName;
                System.out.println(error);
                Utility.printLog("execution.log", logModuleName, "Already Exists", error);
                failureCount.incrementAndGet();
                migrationStatus = "Already Exists";
                migrationDetail = error;

            } else {
                failureCount.incrementAndGet();
                String message = response.optString("ERROR", response.toString()) + " - " + userName;
                Utility.printLog("execution.log", logModuleName, "ERROR", message);
                migrationStatus = "Error";
                migrationDetail = message;
            }

            // ----------------- Database Update -----------------
            if (caseId != null) {
                try (Connection converge = DriverManager.getConnection(jdbcUrl3, dbUser, dbPassword)) {
                    DatabaseUpdationTicket databaseUpdationTicket = new DatabaseUpdationTicket();
                    databaseUpdationTicket.updateTicketInfoByCaseId(converge, ticketDetails);
                    log.info("DB update executed for caseId: {} | username: {}", caseId, ticketDetails.get("Username"));
                } catch (SQLException e) {
                    log.error("Error during DB update for row {}: {}", rowIndex, e.getMessage(), e);
                }
            } else {
                log.warn("No caseId found for row {}, DB update skipped.", rowIndex);
            }

            // ----------------- Excel Update -----------------
            UpdateSheet us = new UpdateSheet();
            String columnAndValue = (caseId != null) ? ("caseId:" + caseId + "#Status:Success") : "Status:Success";
            us.setRowList(rowIndex, columnAndValue);

        } catch (Exception e) {
            e.printStackTrace();
            Utility.printLog("execution.log", logModuleName, "ERROR", "handleAPIResponse exception: " + e.getMessage());
        }
    }


    /**
     * Public method to create tickets concurrently. Mirrors CafCustomer.createPrepaidCustomer flow.
     */
    public void createTicketUpdation(List<Map<String, String>> ticketList) {
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size);
        ReadWriteExcelFile rw = new ReadWriteExcelFile(); // Excel write utility
        UpdateSheet us = new UpdateSheet();
        us.setActiveSheetName("Ticket");

        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
        List<Future<ArrayList<Object>>> futures = new ArrayList<>();

        for (Map<String, String> ticketDetails : ticketList) {
            futures.add(executorService.submit(() -> {
                StopWatch sw = new StopWatch();
                sw.start();

                ArrayList<Object> result = new ArrayList<>();
                try {
                    String row = ticketDetails.get("RowIndex");
                    Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, ticketDetails.toString());

                    result = createTicketWithRetry(ticketDetails);

                    // ---------------- Batch Excel updates ----------------
                    batchToWrite.add(ticketDetails);
                        if (batchToWrite.size() >= batchSize) {
                            // Uncomment if write method implemented
                            // rw.setMultipleColumnInActiveSheet(batchToWrite);
                            batchToWrite.clear();
                        }

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    Utility.printLog("execution.log", logModuleName, "ERROR",
                            "Error processing ticket " + ticketDetails.get("Username") + ": " + e.getMessage());
                }

                return result;
            }));
        }

        // Collect results and handle responses
        for (Future<ArrayList<Object>> future : futures) {
            try {
                ArrayList<Object> objects = future.get();
                if (objects == null || objects.isEmpty()) continue;

                JSONObject jsonObject = (JSONObject) objects.get(0);
                String rowIndex = (String) objects.get(1);
                StopWatch stopWatch = (StopWatch) objects.get(2);
                @SuppressWarnings("unchecked")
                Map<String, String> ticketDetailsMap = (Map<String, String>) objects.get(3);

                long elapsed = (stopWatch != null) ? stopWatch.getTime() : 0L;

                // ---------------- Excel + DB update in handleAPIResponse ----------------
                synchronized (us) { // synchronize Excel updates
                    handleAPIResponse(jsonObject, rowIndex, elapsed, ticketDetailsMap);
                }

            } catch (Exception e) {
                Utility.printLog("execution.log", logModuleName, "ERROR", "Error retrieving task result: " + e.getMessage());
            }
        }

        // Shutdown executor
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(90, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        } finally {
            System.out.println("---------->   Started to write status in Fast Excel sheet. <-----------------");
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.TICKETDATA_FILE);
            System.out.println("---------->   Stopped to write status in Fast Excel sheet. <-----------------");
        }

        // Final batch write
        synchronized (batchToWrite) {
            if (!batchToWrite.isEmpty()) {
                // rw.setMultipleColumnInActiveSheet(batchToWrite); // uncomment if implemented
            }
        }

        System.out.println("Final ticket creation step completed.");
        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
    }


    /* ---------------- existing helper methods largely unchanged ---------------- */

    public List<Map<String, String>> readTicketUpdationList() {
        String sheetName = "Ticket";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getTicketDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> problemDomainMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {
            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String problemDomainName = cellValue.get("CaseTitle");
            String mStatus = cellValue.get("MigrationStatus");

            if ((!"".equals(problemDomainName)) && (!"Success" .equalsIgnoreCase(mStatus))) {
                valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                valuemap.put("CaseTitle", safeTrim(cellValue.get("CaseTitle")));
                valuemap.put("SubscriberType", safeTrim(cellValue.get("SubscriberType")));
                valuemap.put("Username", safeTrim(cellValue.get("Username")));
                valuemap.put("Services", safeTrim(cellValue.get("Services")));
                valuemap.put("Type", safeTrim(cellValue.get("Type")));
                valuemap.put("TicketType", safeTrim(cellValue.get("TicketType")));
                valuemap.put("TicketProblemDomain", safeTrim(cellValue.get("TicketProblemDomain")));
                valuemap.put("TicketSubProblemDomain", safeTrim(cellValue.get("TicketSubProblemDomain")));
                valuemap.put("Status", safeTrim(cellValue.get("Status")));
                valuemap.put("FileNameToAttach", safeTrim(cellValue.get("FileNameToAttach")));
                valuemap.put("Remark", safeTrim(cellValue.get("Remark")));
                valuemap.put("primaryIndex", safeTrim(cellValue.get("PrimaryIndex")));
                valuemap.put("caseNumber", safeTrim(cellValue.get("Case no")));
                valuemap.put("startDate", safeTrim(cellValue.get("Created Date")));
                valuemap.put("endDate", safeTrim(cellValue.get("Closed Date")));
                problemDomainMapList.add(valuemap);
            }
        }
//        System.out.println(problemDomainMapList);
        return problemDomainMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getTicketJson(Map<String, String> ticket) {

        String jsonString = null;

        try {

            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject ticketJson = new JSONObject();

            ticketJson.put("caseStatus", ticket.get("Status"));
            ticketJson.put("caseTitle", ticket.get("CaseTitle"));
            ticketJson.put("caseType", ticket.get("Type"));

            String ticketProblemDomain = ticket.get("TicketProblemDomain");
            int ticketReasonCategoryId = commonGetAPI.getReasonCategoryIdList(ticketProblemDomain).get(0);
            ticketJson.put("ticketReasonCategoryId", ticketReasonCategoryId);

            String ticketSubProblemDomain = ticket.get("TicketSubProblemDomain");
            int reasonSubCategoryId = commonGetAPI.getSubReasonCategoryId(ticketSubProblemDomain);
            ticketJson.put("reasonSubCategoryId", reasonSubCategoryId);

            String nextFollowupDate = Utility.getCurrentDateTimeByProvidedFormat("yyyy-MM-dd");
            String nextFollowupTime = Utility.getCurrentDateTimeByProvidedFormat("HH:mm:ss");

            ticketJson.put("nextFollowupDate", nextFollowupDate);
            ticketJson.put("nextFollowupTime", nextFollowupTime);

            AssignInventory assignInventory = new AssignInventory();
            String customerUsername = ticket.get("Username");
            String customerType = ticket.get("SubscriberType");
            int customerId = commonGetAPI.getCustomerId(customerUsername, customerType);
            if (customerId != 0) {
                ticketJson.put("customersId", customerId);
            }

            // Fallback: if not found in primary source after retries, get from CAF
            if (customerId == 0) {
                customerId = commonGetAPI.getCAFCustomerIdNew(customerUsername);
                if (customerId != 0) {
                    ticketJson.put("customersId", customerId);
                }
            }

            String serviceName = ticket.get("Services");
            List<Integer> serviceIds = commonGetAPI.getServiceIdList(serviceName);
            JSONObject serviceIdsJson = new JSONObject();
            for (int i = 0; i < serviceIds.size(); i++) {
                serviceIdsJson.put("serviceid", serviceIds.get(i));
            }

            List<JSONObject> ticketServicemappingList = new ArrayList<JSONObject>();
            ticketServicemappingList.add(serviceIdsJson);
            ticketJson.put("ticketServicemappingList", ticketServicemappingList);

            ticketJson.put("department", ticket.get("TicketType"));
            ticketJson.put("firstRemark", ticket.get("Remark"));

            ticketJson.put("priority", "Low");
            ticketJson.put("caseForPartner", "Customer");
            ticketJson.put("caseFor", "Customer");
            ticketJson.put("caseOrigin", "Phone");
            ticketJson.put("serialNumber", "");
            ticketJson.put("rootCauseReasonId", "");

            ticketJson.put("file", JSONObject.NULL);
            String fileName = ticket.get("FileNameToAttach");
            if ((fileName != null) && (!"".equals(fileName))) {
                ticketJson.put("file", fileName);
            }

            ticketJson.put("groupReasonId", JSONObject.NULL);
            ticketJson.put("currentAssigneeId", JSONObject.NULL);
            ticketJson.put("customerAdditionalEmail", JSONObject.NULL);
            ticketJson.put("customerAdditionalMobileNumber", JSONObject.NULL);
            ticketJson.put("rating", ticket.get("primaryIndex"));
            ticketJson.put("helperName", JSONObject.NULL);

            ticketJson.put("source", JSONObject.NULL);
            ticketJson.put("subSource", JSONObject.NULL);

            jsonString = ticketJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }

        return jsonString;
    }
}
