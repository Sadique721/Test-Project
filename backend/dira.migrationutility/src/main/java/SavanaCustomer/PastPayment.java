package SavanaCustomer;

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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import temp.UpdateSheet;
import utility.Constant;
import utility.CustomerExecutionSchedulerHelper;
import utility.ReadWriteExcelFile;
import utility.Utility;

/**
 * payment class upgraded to:
 * - fixed thread pool
 * - retry logic with exponential backoff for POST
 * - returns task result to main thread and handles response similarly to CafCustomer
 * - batched excel updates and logging
 * <p>
 * NOTE: implement DatabaseUpdationpayment.updatepaymentDataInDatabases(...) if DB updates are needed.
 */
public class PastPayment extends RestExecution {

    private static final Logger log = LoggerFactory.getLogger(PastPayment.class);

    private final String pcpmurl = Constant.URLPAYMENTCPM;
    private final String prevurl = Constant.URLPAYMENTREV;

    private final String dbUser = Constant.USERNAME;
    private final String dbPassword = Constant.PASSWORD;

    private static String logFileName = "paymentdata.log";
    private static String logModuleName = "Payment";
    private static Integer customerId = 0;

    private static String basePath = Constant.BASE_PATH + "\\TestData\\input\\uploads\\payment\\";

    // Threading / batch / retry config (use values from Constant)
    int thread_size = Constant.THREAD_POOL_SIZE;
    int batchSize = Constant.BATCH_SIZE;
    int retryLimit = Constant.RETRY_LIMIT;
    int retryDelayMS = Constant.RETRY_DELAY_MS;

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);
    private UpdateSheet updateSheet = new UpdateSheet();

    /**
     * Single-payment creation with retry/exponential backoff. Returns:
     * [0] JSONObject response
     * [1] String rowIndex
     * [2] StopWatch sw
     * [3] Map<String,String> paymentDetails
     */
    private ArrayList<Object> createPaymentWithRetry(Map<String, String> paymentDetails) {
        ArrayList<Object> objects = new ArrayList<>();
        try {
            String rowIndex = paymentDetails.get("RowIndex");
            String apiURL = getAPIURL("Revenue/record/payment");
            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            String apiBody = getPaymentJson(paymentDetails);
            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

            if (apiBody != null && !apiBody.isEmpty()) {
                StopWatch sw = new StopWatch();
                sw.start();

                JSONObject JSONResponseBody = null;
                boolean success = false;
                int attempts = 0;

                while (attempts < retryLimit && !success) {
                    try {
                        String fileName = paymentDetails.get("FileNameToAttach");
                        if (fileName != null && !"".equals(fileName)) {
                            fileName = basePath + "\\" + fileName;
                        } else {
                            fileName = null;
                        }

                        JSONResponseBody = httpPostFormData3(apiURL, apiBody, fileName);
                        success = true;

                        String response = JSONResponseBody.toString(4);

                        Utility.printLog(logFileName, logModuleName, "Response", response);

                        int status = JSONResponseBody.getInt("status");
                        String userName = paymentDetails.get("CustomerUsername");
                        float amount = Float.valueOf(paymentDetails.get("Amount"));

                        if (status == 200) {
                            String message = "New Payment of " + amount + " is done successfully for - " + userName + "|" + sw.getTime();
                            System.out.println(message);
                            Utility.printLog("execution.log", logModuleName, "Success", message);

//                            Thread.sleep(500); // 50ms delay after commit
                            //Approve Payment========================================
//                            approvePayment(JSONResponseBody, paymentDetails);
//                            System.out.println("Total time = " + sw.getTime());

                        } else if (status == 406) {
                            String error = JSONResponseBody.getString("responseMessage") + " - " + userName;
                            System.out.println(error);
                            Utility.printLog("execution.log", logModuleName, "Already Exist", error);
                        } else {
                            String error = "Error: " + JSONResponseBody.get("ERROR") + " - " + userName;
                            System.out.println(error);
                            Utility.printLog("execution.log", logModuleName, "ERROR", error);
                        }

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
                objects.add(paymentDetails);
                return objects;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.printLog("execution.log", logModuleName, "ERROR", "createPaymentWithRetry exception: " + e.getMessage());
            failureCount.incrementAndGet();
        }
        return objects;
    }

    /**
     * Handle the API response the same way as CafCustomer.handleAPIResponse:
     * - increments counters
     * - extracts important values ( etc.)
     * - optionally perform DB update & write to excel sheet
     */
//    private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime, Map<String, String> paymentDetails) {
//        try {
//            if (response == null) {
//                failureCount.incrementAndGet();
//                Utility.printLog("execution.log", logModuleName, "ERROR", "Null response for row: " + rowIndex);
//                return;
//            }
//
//            // Log the full response for debugging
//            Utility.printLog(logFileName, logModuleName, "DEBUG", "Full API Response for row " + rowIndex + ": " + response.toString());
//
//            int status = response.optInt("status", -1);
//            String userName = paymentDetails.get("Username") + " | row: " + rowIndex + " | elapsed(ms): " + elapsedTime;
//
//            // Extract CPR ID (customerid) from response
//            String customerId = String.valueOf(response.optInt("customerid", -1));
//            if ("-1".equals(customerId)) {
//                customerId = null;
//            }
//
//            // Update paymentDetails map with extracted customerId
//            if (customerId != null) {
//                paymentDetails.put("customerid", customerId);
//            }
//
//            // Extract response code
//            int responsecode = response.optInt("responseCode", -1);
//
//            if (responsecode == 200) {
//                successCount.incrementAndGet();
//                String message = "payment created successfully - " + userName + " | customerId: " + customerId;
//                System.out.println(message);
//                Utility.printLog("execution.log", logModuleName, "Success", message);
//
//            } else if (status == 406) {
//                String error = response.optString("responseMessage", "Already exists") + " - " + userName;
//                System.out.println(error);
//                Utility.printLog("execution.log", logModuleName, "Already Exists", error);
//                failureCount.incrementAndGet();
//
//            } else {
//                failureCount.incrementAndGet();
//                String message = response.optString("ERROR", response.toString()) + " - " + userName;
//                Utility.printLog("execution.log", logModuleName, "ERROR", message);
//            }
//
//            // ----------------- Database Update -----------------
//            String creditDocId = response.optString("CreditDocId", null); // FIXED case
//
//            if (creditDocId != null) {
//
//                paymentDetails.put("CREDITDOCID", creditDocId); // add to map for DB update
//
//                try (
//                        Connection paymentCpm = DriverManager.getConnection(pcpmurl, dbUser, dbPassword);
//                        Connection paymentRev = DriverManager.getConnection(prevurl, dbUser, dbPassword)
//                ) {
//                    DatabaseUpdationPayments databaseUpdationPayments = new DatabaseUpdationPayments();
//
//                    databaseUpdationPayments.updatePayment(
//                            paymentCpm, "savbillcpm.tbltcreditdoc",
//                            paymentRev, "savbillrevenuemanagement.tbltcreditdoc",
//                            paymentDetails
//                    );
//
////                    log.info("DB update executed for customerId: {} | creditDocId: {} | username: {}",
////                            customerId, creditDocId, paymentDetails.get("Username"));
//
//                } catch (SQLException e) {
//                    log.error("Error during DB update for row {}: {}", rowIndex, e.getMessage(), e);
//                }
//
//            } else {
//                log.warn("No CreditDocId found for row {}, DB update skipped.", rowIndex);
//            }
//
//
//
//            // ----------------- Excel Update -----------------
//            UpdateSheet us = new UpdateSheet();
//            String columnAndValue = (customerId != null) ? ("customerId:" + customerId + "#Status:Success") : "Status:Success";
//            us.setRowList(rowIndex, columnAndValue);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utility.printLog("execution.log", logModuleName, "ERROR", "handleAPIResponse exception: " + e.getMessage());
//        }
//    }
    private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime, Map<String, String> paymentDetails) {
        try {
            if (response == null) {
                failureCount.incrementAndGet();
                Utility.printLog("execution.log", logModuleName, "ERROR", "Null response for row: " + rowIndex);
                return;
            }

            // Debug log
            Utility.printLog(logFileName, logModuleName, "DEBUG",
                    "Full API Response for row " + rowIndex + ": " + response.toString());

            String userName = paymentDetails.get("CustomerUsername")
                    + " | row: " + rowIndex
                    + " | elapsed(ms): " + elapsedTime;

            // Extract customerId (Java-8 friendly)
            String CreditDocId = null;
            if (response.has("CreditDocId")) {
                CreditDocId = response.optString("CreditDocId", null);
            }
            if (CreditDocId != null) {
                paymentDetails.put("CreditDocId", CreditDocId);
            }

            // ---- FIX: Unified code for Java 8 ----
            int code = -1;
            if (response.has("responseCode")) {
                code = response.optInt("responseCode", -1);
            } else if (response.has("status")) {
                code = response.optInt("status", -1);
            }
            // ---------------------------------------

            String migrationStatus = "Initial";
            String migrationDetail = "Initial";

            if (code == 200) {
                successCount.incrementAndGet();

                String msg = "payment created successfully - "
                        + userName + " | CreditDocId: " + CreditDocId;

                System.out.println(msg);
                Utility.printLog("execution.log", logModuleName, "Success", msg);

                migrationStatus = "Success";
                migrationDetail = msg;

            } else if (code == 406) {

                failureCount.incrementAndGet();

                String error = response.optString("responseMessage", "406")
                        + " - " + userName;

                System.out.println(error);
                Utility.printLog("execution.log", logModuleName, "406", error);

                migrationStatus = "406";
                migrationDetail = error;

            } else {

                failureCount.incrementAndGet();

                String msg = response.optString("responseMessage", response.toString());
                Utility.printLog("execution.log", logModuleName, "ERROR", msg + " - " + userName);
                migrationStatus = "Error";
                migrationDetail = msg;
            }

            // ---------------- DATABASE UPDATE (Java-8 safe) ----------------
//            String creditDocId = null;
//            if (response.has("CreditDocId")) {
//                creditDocId = response.optString("CreditDocId", null);
//            }

//            if (creditDocId != null) {
//                paymentDetails.put("CREDITDOCID", creditDocId);
//
//                Connection paymentCpm = null;
//                Connection paymentRev = null;
//
//                try {
//                    paymentCpm = DriverManager.getConnection(pcpmurl, dbUser, dbPassword);
//                    paymentRev = DriverManager.getConnection(prevurl, dbUser, dbPassword);
//
////                    DatabaseUpdationPayments updater = new DatabaseUpdationPayments();
////                    updater.updatePayment(
////                            paymentCpm, "savbillcpm.tbltcreditdoc",
////                            paymentRev, "savbillrevenuemanagement.tbltcreditdoc",
////                            paymentDetails
////                    );
//
//                } catch (SQLException e) {
//                    log.error("DB update error for row " + rowIndex + ": " + e.getMessage(), e);
//                } finally {
//                    try { if (paymentCpm != null) paymentCpm.close(); } catch (SQLException ignored) {}
//                    try { if (paymentRev != null) paymentRev.close(); } catch (SQLException ignored) {}
//                }
//
//            } else {
//                log.warn("No CreditDocId found for row " + rowIndex + ", DB update skipped.");
//            }

            // ---------------- EXCEL UPDATE ----------------
            //UpdateSheet us = new UpdateSheet();
            String columnValue;

            if (CreditDocId != null) {
                columnValue = "CreditDocId:" + CreditDocId + "#Status:Success";
            } else {
                columnValue = "Status:Success";
            }

            String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
            updateSheet.setRowList(rowIndex, columnAndValue);

            //us.setRowList(rowIndex, columnValue);

        } catch (Exception e) {
            failureCount.incrementAndGet();
            Utility.printLog("execution.log", logModuleName, "ERROR",
                    "handleAPIResponse exception: " + e.getMessage());
        }
    }

    /**
     * Public method to create payments concurrently..
     */
    public void createPaymentRecord(List<Map<String, String>> paymentList) {
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size);
        ReadWriteExcelFile rw = new ReadWriteExcelFile(); // Excel write utility
        //UpdateSheet us = new UpdateSheet();
        updateSheet.setActiveSheetName("PaymentDetails");

        CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            customerScheduler.setCustomerExecutionSchedulerData(updateSheet, Constant.RECORD_PAYMENT_SCHEDULER);
        }, 15, 10, TimeUnit.SECONDS);


        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
        List<Future<ArrayList<Object>>> futures = new ArrayList<>();

        for (Map<String, String> paymentDetails : paymentList) {
            futures.add(executorService.submit(() -> {
                StopWatch sw = new StopWatch();
                sw.start();

                ArrayList<Object> result = new ArrayList<>();
                try {
                    String row = paymentDetails.get("RowIndex");
                    Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, paymentDetails.toString());

                    result = createPaymentWithRetry(paymentDetails);

                    // ---------------- Batch Excel updates ----------------
                    synchronized (batchToWrite) { // ensure thread-safe add + write check
                        batchToWrite.add(paymentDetails);
                        if (batchToWrite.size() >= batchSize) {
                            // Uncomment if write method implemented
                            // rw.setMultipleColumnInActiveSheet(batchToWrite);
                            batchToWrite.clear();
                        }
                    }

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    Utility.printLog("execution.log", logModuleName, "ERROR", "Error processing payment " + paymentDetails.get("Username") + ": " + e.getMessage());
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
                @SuppressWarnings("unchecked") Map<String, String> paymentDetailsMap = (Map<String, String>) objects.get(3);

                long elapsed = (stopWatch != null) ? stopWatch.getTime() : 0L;

//                Thread.sleep(2000);
                // ---------------- Excel + DB update in handleAPIResponse ----------------
                //synchronized (us) { // synchronize Excel updates
                handleAPIResponse(jsonObject, rowIndex, elapsed, paymentDetailsMap);

                //}
                System.out.println("Task Completed Successfully: " + future.isDone());
            } catch (Exception e) {
                System.err.println("Error retrieving task result: " + e.getMessage());
                Utility.printLog("execution.log", logModuleName, "ERROR", "Error retrieving task result: " + e.getMessage());
            }
        }

        // Shutdown executor
        executorService.shutdown();
        scheduler.shutdown();
        try {
            if (!executorService.awaitTermination(90, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
            if (!scheduler.awaitTermination(15, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduler.shutdownNow();
        } finally {
            System.out.println("---------->   Started to write status in sheet. <-----------------");
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
            customerScheduler.clearExistingFile(Constant.RECORD_PAYMENT_SCHEDULER);
            System.out.println("---------->   Stopped to write status in sheet. <-----------------");
        }

        // Final batch write
        synchronized (batchToWrite) {
            if (!batchToWrite.isEmpty()) {
                // rw.setMultipleColumnInActiveSheet(batchToWrite); // uncomment if implemented
            }
        }

        System.out.println("Final payment creation step completed.");
        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
    }


    /* ---------------- existing helper methods largely unchanged ---------------- */

    public List<Map<String, String>> readPaymentList() {
        String sheetName = "PaymentDetails";
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

            if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

                valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                valuemap.put("SubscriberType", safeTrim(cellValue.get("SubscriberType")));
                valuemap.put("CustomerUsername", safeTrim(cellValue.get("CustomerUsername")));
                valuemap.put("DocumentNumber", safeTrim(cellValue.get("DocumentNumber")));
                valuemap.put("PaymentMode", safeTrim(cellValue.get("PaymentMode")));
                valuemap.put("Source", safeTrim(cellValue.get("Source")));
                valuemap.put("Amount", safeTrim(cellValue.get("Amount")));
                valuemap.put("backdate", safeTrim(cellValue.get("PaymentDate")));
//                valuemap.put("FileNameToAttach", safeTrim(cellValue.get("FileNameToAttach")));

//                valuemap.put("ChequeNumber", safeTrim(cellValue.get("ChequeNumber")));
                valuemap.put("ChequeDate", safeTrim(cellValue.get("TransactionDate")));
//                valuemap.put("SourceBank", safeTrim(cellValue.get("SourceBank")));
//                valuemap.put("DestinationBank", safeTrim(cellValue.get("DestinationBank")));
//                valuemap.put("Branch", safeTrim(cellValue.get("Branch")));

                valuemap.put("ReferenceNumber", safeTrim(cellValue.get("ReferenceNumber")));
                valuemap.put("ReceiptNumber", safeTrim(cellValue.get("ReceiptNumber")));
                valuemap.put("TDS", safeTrim(cellValue.get("TDS")));
                valuemap.put("ABBS", safeTrim(cellValue.get("ABBS")));
                valuemap.put("Remark", safeTrim(cellValue.get("Remark")));

                customerMapList.add(valuemap);
            }
        }
//        System.out.println(customerMapList);
        return customerMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

//    private String getPaymentJson(Map<String, String> customerDetails) {
//
//        String jsonString = null;
//
//        try {
//            CommonGetAPI commonGetAPI = new CommonGetAPI();
//            JSONObject paymentJson = new JSONObject();
//            String commonPaymentMode = null;
//
//            String customerType = customerDetails.get("SubscriberType");
//            String userName = customerDetails.get("CustomerUsername");
//            int invoiceId = 0;
//
//            if (userName != null && !"".equals(userName)) {
//                customerId = commonGetAPI.getCustomerId(userName, customerType);
//
//                if (customerId != 0) {
//                    paymentJson.put("customerid", customerId);
//
//                    String paymentMode = customerDetails.get("PaymentMode");
//                    CommonList commonList = new CommonList();
//                    commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
//                    paymentJson.put("paymode", commonPaymentMode);
//
////                    String documentNumber = customerDetails.get("DocumentNumber");
////                    CreditNote creditNote = new CreditNote();
////                    invoiceId = creditNote.getCustomerInvoiceId(customerId, documentNumber);
//
//                    List<Integer> invoiceList = new ArrayList<>();
//                    invoiceList.add(invoiceId);
//                    paymentJson.put("invoiceId", invoiceList);
//                }
//            }
//
//            // ************ FIXED DYNAMIC FIELDS ************
//
//            // Online source dynamic or default
//            paymentJson.put("onlinesource",
//                    customerDetails.getOrDefault("OnlineSource", "PHONEPE"));
//
//            String rawChequeDate = customerDetails.getOrDefault("ChequeDate", "11/25/25");
//

    /// / Convert to yyyy-MM-dd
//            String apiChequeDate = convertToApiDate(rawChequeDate);
//
//            paymentJson.put("chequedate", apiChequeDate);
//            paymentJson.put("chequedatestr", apiChequeDate);
//
//
//            float tempAmount = Float.valueOf(customerDetails.get("Amount"));
//            String amount = Utility.formattedDecimalNumber(tempAmount);
//            paymentJson.put("amount", amount);
//
//            paymentJson.put("referenceno", customerDetails.get("ReferenceNumber"));
//            paymentJson.put("reciptNo", customerDetails.get("ReceiptNumber"));
//            paymentJson.put("remark", customerDetails.get("Remark"));
//
//            // Payment general keys
//            paymentJson.put("type", "Payment");
//            paymentJson.put("paytype", customerDetails.getOrDefault("PayType", "advance")); // dynamic
//            paymentJson.put("bank", JSONObject.NULL);  // null like sample JSON
//
//            // TDS
//            boolean tds = Boolean.valueOf(customerDetails.get("TDS"));
//            float tdsAmount = 0f;
//            if (tds) {
//                tdsAmount = Float.valueOf(Utility.formattedDecimalNumber(tempAmount * 0.10f));
//            }
//            paymentJson.put("tdsAmount", tdsAmount);
//
//            // ABBS
//            boolean abbs = Boolean.valueOf(customerDetails.get("ABBS"));
//            float abbsAmount = 0f;
//            if (abbs) {
//                abbsAmount = Float.valueOf(Utility.formattedDecimalNumber(tempAmount * 0.10f));
//            }
//            paymentJson.put("abbsAmount", abbsAmount);
//
//            // file name
//            String fileName = customerDetails.get("FileNameToAttach");
//            paymentJson.put("filename",
//                    (fileName != null && !"".equals(fileName)) ? fileName : JSONObject.NULL);
//
//            // ************ paymentListPojos (Mandatory for API) ************
//            JSONObject pojo = new JSONObject();
//            pojo.put("tdsAmountAgainstInvoice", tdsAmount);
//            pojo.put("abbsAmountAgainstInvoice", abbsAmount);
//            pojo.put("amountAgainstInvoice", tempAmount);
//            pojo.put("invoiceId", invoiceId);
//
//            List<JSONObject> paymentList = new ArrayList<>();
//            paymentList.add(pojo);
//            paymentJson.put("paymentListPojos", paymentList);
//
//            jsonString = paymentJson.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(jsonString);
//        return jsonString;
//    }
    private String getPaymentJson(Map<String, String> customerDetails) {
        String jsonString = null;

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject paymentJson = new JSONObject();
            String commonPaymentMode = null;

            String customerType = customerDetails.get("SubscriberType");
            String userName = customerDetails.get("CustomerUsername");
            int invoiceId = 0; // default invoice id


//            if (userName != null && !"".equals(userName)) {
//                int customerId = commonGetAPI.getCustomerId(userName, customerType);
//
//                if (customerId != 0) {
//                    paymentJson.put("customerid", customerId);
//
//                    String paymentMode = customerDetails.get("PaymentMode");
//                    CommonList commonList = new CommonList();
////                    commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
//                    paymentJson.put("paymode", paymentMode);
//
//                    // invoiceId list
//                    List<Integer> invoiceList = new ArrayList<>();
//                    invoiceList.add(invoiceId);
//                    paymentJson.put("invoiceId", invoiceList);
//                }
//            }


            if (userName != null && !userName.trim().isEmpty()) {

                int customerId = commonGetAPI.getCustomerId(userName, customerType);

                // Fallback: if not found in primary source, get from CAF
                if (customerId == 0) {
                    customerId = commonGetAPI.getCAFCustomerId(userName);
                }

                if (customerId != 0) {
                    paymentJson.put("customerid", customerId);

                    String paymentMode = customerDetails.get("PaymentMode");
                    paymentJson.put("paymode", paymentMode);

                    // invoiceId list
                    List<Integer> invoiceList = new ArrayList<>();
                    invoiceList.add(invoiceId);
                    paymentJson.put("invoiceId", invoiceList);
                }
            }


            // ************ FIXED DYNAMIC FIELDS ************
            String onlineSource = customerDetails.get("Source");
            if (onlineSource == null || onlineSource.isEmpty()) {
                onlineSource = "PHONEPE";
            }

            paymentJson.put("onlinesource", customerDetails.get("PaymentMode"));

            String rawChequeDate = customerDetails.get("ChequeDate");
            if (rawChequeDate == null || rawChequeDate.isEmpty()) {
                rawChequeDate = "11/25/25";
            }
            String apiChequeDate = convertToApiDate(rawChequeDate); // convert to yyyy-MM-dd
            paymentJson.put("chequedate", apiChequeDate);
            paymentJson.put("chequedatestr", apiChequeDate);

            String amountStr = customerDetails.get("Amount");
            float tempAmount = 0f;
            if (amountStr != null && !amountStr.isEmpty()) {
                tempAmount = Float.parseFloat(amountStr);
            }
            String amount = Utility.formattedDecimalNumber(tempAmount);
            paymentJson.put("amount", amount);

            paymentJson.put("referenceno", customerDetails.get("ReferenceNumber"));
            paymentJson.put("reciptNo", customerDetails.get("ReceiptNumber"));
            paymentJson.put("remark", customerDetails.get("Remark"));

            // Payment general keys
            paymentJson.put("type", "Payment");

            String payType = customerDetails.get("PayType");
            if (payType == null || payType.isEmpty()) {
                payType = "advance";
            }
            paymentJson.put("paytype", payType);
            paymentJson.put("bank", JSONObject.NULL);  // null like sample JSON

            // TDS
            boolean tds = false;
            String tdsStr = customerDetails.get("TDS");
            if (tdsStr != null) {
                tds = Boolean.parseBoolean(tdsStr);
            }
            float tdsAmount = tds ? Float.parseFloat(Utility.formattedDecimalNumber(tempAmount * 0.10f)) : 0f;
            paymentJson.put("tdsAmount", tdsAmount);

            // ABBS
            boolean abbs = false;
            String abbsStr = customerDetails.get("ABBS");
            if (abbsStr != null) {
                abbs = Boolean.parseBoolean(abbsStr);
            }
            float abbsAmount = abbs ? Float.parseFloat(Utility.formattedDecimalNumber(tempAmount * 0.10f)) : 0f;
            paymentJson.put("abbsAmount", abbsAmount);

            // file name
            String fileName = customerDetails.get("FileNameToAttach");
            if (fileName == null || fileName.isEmpty()) {
                paymentJson.put("filename", JSONObject.NULL);
            } else {
                paymentJson.put("filename", fileName);
            }

            // ************ paymentListPojos (Mandatory for API) ************
            JSONObject pojo = new JSONObject();
            pojo.put("tdsAmountAgainstInvoice", tdsAmount);
            pojo.put("abbsAmountAgainstInvoice", abbsAmount);
            pojo.put("amountAgainstInvoice", tempAmount);
            pojo.put("invoiceId", invoiceId);

            List<JSONObject> paymentList = new ArrayList<>();
            paymentList.add(pojo);
            paymentJson.put("paymentListPojos", paymentList);

            jsonString = paymentJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(jsonString);
        return jsonString;
    }


//    private String convertToApiDate(String date) {
//        try {
//            // incoming format: MM/dd/yy
//            java.time.format.DateTimeFormatter inFormat = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yy");
//            java.time.format.DateTimeFormatter outFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//            return java.time.LocalDate.parse(date, inFormat).format(outFormat);
//
//        } catch (Exception e) {
//            // fallback: return as is or today's date
//            return "2025-11-25";
//        }
//    }

    private String convertToApiDate(String date) {
        try {
            // Incoming format: May 03 2024 14:15:00
            java.time.format.DateTimeFormatter inFormat =
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss", java.util.Locale.ENGLISH);

            java.time.format.DateTimeFormatter outFormat =
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return java.time.LocalDateTime.parse(date, inFormat)
                    .toLocalDate()
                    .format(outFormat);

        } catch (Exception e) {
            // fallback
            return "2025-11-25";
        }
    }


//    private void approvePayment(JSONObject response, Map<String, String> paymentDetails) {
//
//        try {
//            JSONObject approvePayment = new JSONObject();
//            CommonGetAPI commonGetAPI = new CommonGetAPI();
//            String commonPaymentMode = null;
//
//            String customerType = paymentDetails.get("SubscriberType");
//            String userName = paymentDetails.get("CustomerUsername");
//            int customerId = commonGetAPI.getCustomerId(userName, customerType);
//
//            String paymentMode = paymentDetails.get("PaymentMode");
//            CommonList commonList = new CommonList();
//            commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
//            String rawChequeDate = paymentDetails.get("ChequeDate");
//            if (rawChequeDate == null || rawChequeDate.isEmpty()) {
//                rawChequeDate = "11/25/25";
//            }
//            String apiChequeDate = convertToApiDate(rawChequeDate);
//            // Required remarks only
//            String remarks = "Approved by Migration";
//
//            // Build JSON EXACTLY as payload
//            approvePayment.put("customerid", customerId);
//            approvePayment.put("idlist", response.get("CreditDocId"));
//            approvePayment.put("invoiceNumber", JSONObject.NULL);
//            approvePayment.put("paymode", commonPaymentMode);
//            approvePayment.put("paystatus", "pending");
//            approvePayment.put("paytodate", apiChequeDate);
//            approvePayment.put("referenceno",  paymentDetails.get("ReferenceNumber"));
//            approvePayment.put("remarks", remarks);
//
//            // API Call
//            String apiURL = getAPIURL("cpm/payment/approve");
//            httpPost(apiURL, approvePayment.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    // Approve a past payment (make API call + logging)
//    private void approvePayment(JSONObject response, Map<String, String> paymentDetails) throws Exception {
//        // 1. Build the API URL
//        String apiURL = getAPIURL("cpm/payment/approve");
//
//        // 2. Build the payload
//        Map<String, Object> apiBody = new HashMap<>();
//        CommonGetAPI commonGetAPI = new CommonGetAPI();
//        CommonList commonList = new CommonList();
//
//        String customerType = paymentDetails.get("SubscriberType");
//        String userName = paymentDetails.get("CustomerUsername");
//        int customerId = commonGetAPI.getCustomerId(userName, customerType);
//
//        String paymentMode = paymentDetails.get("PaymentMode");

    /// /        String commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
//
//        String rawChequeDate = paymentDetails.get("ChequeDate");
//        if (rawChequeDate == null || rawChequeDate.isEmpty()) {
//            rawChequeDate = "11/25/25";
//        }
//        String apiChequeDate = convertToApiDate(rawChequeDate);
//
//        String remarks = "Approved by Migration";
//        System.out.print(response);
//        apiBody.put("customerid", customerId);
//        apiBody.put("idlist", response.get("CreditDocId"));
//        apiBody.put("invoiceNumber", JSONObject.NULL);
//        apiBody.put("paymode", paymentMode);
//        apiBody.put("paystatus", "pending");
//        apiBody.put("paytodate", apiChequeDate);
//        apiBody.put("referenceno", paymentDetails.get("ReferenceNumber"));
//        apiBody.put("remarks", remarks);
//
//        // 3. Convert Map to JSON string
//        String body = new JSONObject(apiBody).toString();
//
//        // 4. Make the POST request using your existing httpPost
//        JSONObject jsonResponse = httpPost(apiURL, body);
//
//        // 5. Check the status and log
//        int status = jsonResponse.optInt("status", -1);
//
//        if (status == 200) {
//            String message = "Payment approved successfully for Customer ID: " +
//                    userName + " : " + customerId;
//            System.out.println(message);
//            Utility.printLog("execution.log", logModuleName, "Payment Approved", message);
//        } else {
//            String errorMessage = "Failed to approve Payment for Customer ID: " +
//                    customerId + ". Status: " + status;
//            log.error(errorMessage);
//            Utility.printLog("execution.log", logModuleName, "Payment Approval Failed", errorMessage);
//        }
//    }


// Approve a past payment (make API call + logging)
    private void approvePayment(JSONObject response, Map<String, String> paymentDetails) {
        String apiURL = getAPIURL("cpm/payment/approve");
        CommonGetAPI commonGetAPI = new CommonGetAPI();
        CommonList commonList = new CommonList();

        String customerType = paymentDetails.get("SubscriberType");
        String userName = paymentDetails.get("CustomerUsername");
        int customerId = commonGetAPI.getCustomerId(userName, customerType);
        String paymentMode = paymentDetails.get("PaymentMode");

        String rawChequeDate = paymentDetails.get("ChequeDate");
        if (rawChequeDate == null || rawChequeDate.isEmpty()) {
            rawChequeDate = "11/25/25";
        }
        String apiChequeDate = convertToApiDate(rawChequeDate);

        String remarks = "Approved by Migration";

        // Build the payload once
        Map<String, Object> apiBody = new HashMap<>();
        apiBody.put("customerid", customerId);
        apiBody.put("idlist", response.get("CreditDocId"));
        apiBody.put("invoiceNumber", JSONObject.NULL);
        apiBody.put("paymode", paymentMode);
        apiBody.put("paystatus", "pending");
        apiBody.put("paytodate", apiChequeDate);
        apiBody.put("referenceno", paymentDetails.get("ReferenceNumber"));
        apiBody.put("remarks", remarks);

        String body = new JSONObject(apiBody).toString();

        // === Smart Retry Logic ===
        int maxRetries = 5;
        int retryCount = 0;
        long baseDelay = 300; // 300ms base delay

        while (retryCount < maxRetries) {
            try {
                JSONObject jsonResponse = httpPost(apiURL, body);
                int status = jsonResponse.optInt("status", -1);

                if (status == 200) {
                    String message = "Payment approved successfully for Customer ID: " +
                            userName + " : " + customerId;
                    System.out.println(message);
                    Utility.printLog("execution.log", logModuleName, "Payment Approved", message);
                    return;
                } else if (status == 417) {
                    // Backend not ready yet → wait and retry
                    retryCount++;
                    long delay = baseDelay * (long) Math.pow(2, retryCount); // exponential backoff
                    String msg = "Payment not ready (417) for " + userName +
                            ", retry " + retryCount + " after " + delay + "ms";
                    System.out.println(msg);
                    Utility.printLog("execution.log", logModuleName, "Payment Retry", msg);
                    Thread.sleep(delay);
                } else {
                    // Other error, no retry
                    String errorMessage = "Failed to approve Payment for Customer ID: " +
                            customerId + ". Status: " + status;
                    log.error(errorMessage);
                    Utility.printLog("execution.log", logModuleName, "Payment Approval Failed", errorMessage);
                    return;
                }
            } catch (Exception e) {
                retryCount++;
                long delay = baseDelay * (long) Math.pow(2, retryCount);
                String msg = "Exception while approving payment for " + userName +
                        ", attempt " + retryCount + ": " + e.getMessage();
                System.err.println(msg);
                Utility.printLog("execution.log", logModuleName, "Payment Retry Error", msg);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // If we reach here, all retries failed
        String errorMessage = "Approval failed after " + maxRetries +
                " retries for Customer ID: " + customerId;
        log.error(errorMessage);
        Utility.printLog("execution.log", logModuleName, "Payment Approval Failed", errorMessage);
    }


}
