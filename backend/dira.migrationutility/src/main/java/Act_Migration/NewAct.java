package Act_Migration;

	import java.util.*;
	import java.util.concurrent.*;
	import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
	import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

	public class NewAct extends RestExecution {

	    private static final String LOG_FILE = "ActCustomer.log";
	    private static final String LOG_MODULE = "CreateActCustomer";

	    private final int THREAD_POOL_SIZE = Constant.THREAD_POOL_SIZE;
	    private final int BATCH_SIZE = Constant.BATCH_SIZE;
	    private final int RETRY_LIMIT = Constant.RETRY_LIMIT;
	    private final long RETRY_DELAY_MS = Constant.RETRY_DELAY_MS;

	    private static final AtomicInteger successCount = new AtomicInteger(0);
	    private static final AtomicInteger failureCount = new AtomicInteger(0);

	    private static final ExecutorService executorService = Executors.newFixedThreadPool(
	            Runtime.getRuntime().availableProcessors() * 2 // Dynamically scale threads
	    );

	    public void processCustomers(List<Map<String, String>> customerMapList) {
	        List<Callable<Void>> tasks = new ArrayList<>();
	        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
	        ReadWriteExcelFile excelWriter = new ReadWriteExcelFile();

	        for (Map<String, String> customerDetails : customerMapList) {
	            tasks.add(() -> {
	                try {
	                    processSingleCustomer(customerDetails, batchToWrite);
	                } catch (Exception e) {
	                    Utility.printLog(LOG_FILE, LOG_MODULE, "ERROR", "Error processing customer: " + e.getMessage());
	                }
	                return null;
	            });
	        }

	        executeTasksInBatches(tasks, batchToWrite, excelWriter);
	    }

	    private void processSingleCustomer(Map<String, String> customerDetails, List<Map<String, String>> batchToWrite) {
	        String userName = customerDetails.get("Username");
	        String rowIndex = customerDetails.get("RowIndex");
	        StopWatch timer = new StopWatch();

	        try {
	            timer.start();
	            if (!isCustomerExists(userName)) {
	                createCustomerWithRetry(customerDetails);
	                batchToWrite.add(customerDetails);

	                if (batchToWrite.size() >= BATCH_SIZE) {
	                    writeBatchToExcel(batchToWrite);
	                }
	            } else {
	                Utility.printLog(LOG_FILE, LOG_MODULE, "INFO", "Customer already exists: " + userName);
	            }
	        } finally {
	            timer.stop();
	        }
	    }

	    private boolean isCustomerExists(String userName) {
	        // Placeholder for checking if customer exists
	        return false;
	    }

	    private void createCustomerWithRetry(Map<String, String> customerDetails) {
	        String apiURL = getAPIURL("cpm/customers");
	        String apiBody = getPrepaidCustomerJson(customerDetails);
	        int attempts = 0;

	        while (attempts < RETRY_LIMIT) {
	            try {
	                JSONObject response = httpPost(apiURL, apiBody);
	                handleAPIResponse(response, customerDetails.get("RowIndex"), customerDetails);
	                return;
	            } catch (Exception e) {
	                attempts++;
	                if (attempts == RETRY_LIMIT) {
	                    failureCount.incrementAndGet();
	                    Utility.printLog(LOG_FILE, LOG_MODULE, "ERROR", "Failed after retries: " + e.getMessage());
	                }
	                try {
	                    Thread.sleep(RETRY_DELAY_MS * (long) Math.pow(2, attempts));
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt();
	                }
	            }
	        }
	    }

	    private void handleAPIResponse(JSONObject response, String rowIndex, Map<String, String> customerDetails) {
	        if (response.has("status") && response.getInt("status") == 200) {
	            successCount.incrementAndGet();
	            Utility.printLog(LOG_FILE, LOG_MODULE, "Success", "Customer added: " + customerDetails.get("Username"));
	        } else {
	            failureCount.incrementAndGet();
	            Utility.printLog(LOG_FILE, LOG_MODULE, "ERROR", "API Error for: " + customerDetails.get("Username"));
	        }
	    }

	    private void writeBatchToExcel(List<Map<String, String>> batchToWrite) {
	        ReadWriteExcelFile excelWriter = new ReadWriteExcelFile();
	        excelWriter.setMultipleColumnInActiveSheet(batchToWrite);
	        batchToWrite.clear();
	    }

	    private void executeTasksInBatches(List<Callable<Void>> tasks, List<Map<String, String>> batchToWrite,
	                                       ReadWriteExcelFile excelWriter) {
	        try {
	            executorService.invokeAll(tasks);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        } finally {
	            executorService.shutdown();
	            writeBatchToExcel(batchToWrite);
	            printFinalStatistics();
	        }
	    }

	    private void printFinalStatistics() {
	        System.out.println("Processing Complete!");
	        System.out.println("Success Count: " + successCount.get());
	        System.out.println("Failure Count: " + failureCount.get());
	    }

	    public List<Map<String, String>> readCustomerList() {
	        String sheetName = "ACustomer";
	        ReadData readData = new ReadData();
	        return readData.getActCustomerDataSheet(sheetName);
	    }
	    

	    private String getPrepaidCustomerJson(Map<String, String> customerDetails) {
	        JSONObject json = new JSONObject();
	        json.put("custtype", "Prepaid");
	        json.put("Username", customerDetails.get("Username"));
	        json.put("Email", customerDetails.get("Email"));
	        json.put("Plan", customerDetails.get("Plan"));
	        return json.toString();
	    }
	}



