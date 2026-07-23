package after_Migration_Payments_CheckAndGetList;

import commons.CommonGetAPI;
import utility.Constant;
import utility.ReadWriteExcelFile;
import api.ReadData;
import temp.UpdateSheet;
import java.util.List;
import java.util.Map;

/**
 * Java 8 friendly class to validate payment references from Excel
 * and write the results back to the sheet.
 */
public class Check_Payments {

    private UpdateSheet updateSheet = new UpdateSheet();
    private final CommonGetAPI commonGetAPI = new CommonGetAPI();

    /**
     * Entry method to validate payment references from Excel
     */
    public void validatePaymentsFromExcel() {

        // 1️⃣ Read Excel data
        ReadData readData = new ReadData();
        List<Map<String, String>> excelRows = readData.getSavanaCustomerDataSheet("Payment_Check");
        System.out.println("Started To read Payments");
        updateSheet.setActiveSheetName("Payment_Check");

        // 2️⃣ Process each row
        for (Map<String, String> row : excelRows) {

            String rowIndex = safeTrim(row.get("RowIndex"));
            String referenceNo = safeTrim(row.get("ReferenceNumber"));
            String username = safeTrim(row.get("CustomerUsername"));

            String status;
            String details;

            if (referenceNo.isEmpty()) {
                status = "INVALID_REFERENCE";
                details = "Blank reference number";
            } else {
                System.out.println("Getting Reference Number from System and Checking");

                // Lookup payment via cached API
                String paymentId = commonGetAPI.getPaymentByReferenceNo(referenceNo);

                if (paymentId != null) {
                    status = "FOUND";
                    details = "PaymentId=" + paymentId;
                } else {
                    status = "NOT_FOUND";
                    details = "No payment in CPM";
                }
            }

            // 3️⃣ Write back to UpdateSheet
            String updateValue = "MigrationStatus::" + status +
                    "#MigrationDetail::" + details;
            updateSheet.setRowList(rowIndex, updateValue);
        }

        // 4️⃣ Save Excel
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);

        System.out.println("Payment reference validation completed.");
    }

    /**
     * Safe trim helper
     */
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}












//
//
//
//package after_Migration_Payments_CheckAndGetList;
//
//import commons.CommonGetAPI;
//import utility.Constant;
//import utility.ReadWriteExcelFile;
//import api.ReadData;
//import temp.UpdateSheet;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
///**
// * Java 8 friendly multi-threaded class to validate payment references from Excel
// * and write the results back to the sheet.
// */
//public class Check_Payments {
//
//    private UpdateSheet updateSheet = new UpdateSheet();
//    private final CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//    /**
//     * Entry method to validate payment references from Excel (multi-threaded)
//     */
//    public void validatePaymentsFromExcel() {
//
//        // 1️⃣ Read Excel data
//        ReadData readData = new ReadData();
//        List<Map<String, String>> excelRows = readData.getSavanaCustomerDataSheet("Payment_Check");
//
//        updateSheet.setActiveSheetName("Payment_Check");
//
//        // 2️⃣ Setup thread pool
//        int threadCount = 10; // adjust as needed
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//
//        int totalRows = excelRows.size();
//        int chunkSize = (totalRows + threadCount - 1) / threadCount; // ceil
//
//        for (int i = 0; i < totalRows; i += chunkSize) {
//            final int start = i;
//            final int end = Math.min(i + chunkSize, totalRows);
//
//            executor.submit(() -> {
//                for (int j = start; j < end; j++) {
//                    Map<String, String> row = excelRows.get(j);
//
//                    String rowIndex = safeTrim(row.get("RowIndex"));
//                    String referenceNo = safeTrim(row.get("ReferenceNumber"));
//                    String username = safeTrim(row.get("CustomerUsername"));
//
//                    String status;
//                    String details;
//
//                    if (referenceNo.isEmpty()) {
//                        status = "INVALID_REFERENCE";
//                        details = "Blank reference number";
//                    } else {
//
//                        // Lookup payment via cached API
//                        String paymentId = commonGetAPI.getPaymentByReferenceNo(referenceNo);
//
//                        if (paymentId != null) {
//                            status = "FOUND";
//                            details = "PaymentId=" + paymentId;
//                        } else {
//                            status = "NOT_FOUND";
//                            details = "No payment in CPM";
//                        }
//                    }
//
//                    // 3️⃣ Thread-safe Excel update
//                    synchronized (updateSheet) {
//                        String updateValue = "MigrationStatus::" + status +
//                                "#MigrationDetail::" + details;
//                        updateSheet.setRowList(rowIndex, updateValue);
//                    }
//                }
//            });
//        }
//
//        // 4️⃣ Shutdown executor and wait for tasks to finish
//        executor.shutdown();
//        try {
//            executor.awaitTermination(1, TimeUnit.HOURS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 5️⃣ Save Excel
//        ReadWriteExcelFile rw = new ReadWriteExcelFile();
//        rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
//
//        System.out.println("Payment reference validation completed (optimized multi-threaded).");
//    }
//
//    /**
//     * Safe trim helper
//     */
//    private String safeTrim(String value) {
//        return value == null ? "" : value.trim();
//    }
//}
