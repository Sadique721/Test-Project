package temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import utility.Constant;
import utility.Utility;

public class SimpleThreadPool1 {

    // Main method removed, as this is part of the class for thread execution
    public void executeThreadsBatch(List<Map<String, String>> list) {
        
        int totalThreads = Constant.TOTAL_THREADS;
        System.out.println("Total Threads = " + totalThreads);
        
        // Create a fixed-size thread pool
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        
        int totalRows = list.size();
        int batchSize = calculateBatchSize(totalRows);
        
        System.out.println("Total Rows = " + totalRows);
        System.out.println("Batch Size = " + batchSize);
        
        // Calculate number of batches
        int totalBatches = (totalRows + batchSize - 1) / batchSize; // Equivalent to Math.ceil(totalRows / batchSize)
        
        // Submit tasks to the thread pool
        for (int i = 0; i < totalBatches; i++) {
            List<Map<String, String>> batchList = createBatch(i, batchSize, list);
            Runnable worker = new WorkerThread("Batch-" + i, batchList);
            executor.execute(worker);
        }

        // Shutdown the executor and wait for all threads to finish
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow(); // Forcefully shut down if not completed in 60 minutes
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // After all threads finish, do any final tasks
        System.out.println("Finished all threads");
        finalizePostProcessing();
    }

    private int calculateBatchSize(int totalRows) {
        // Logic to decide batch size based on total rows
        if (totalRows > 10000) {
            return 10;
        } else if (totalRows > 1000) {
            return 5;
        } else {
            return 21;
        }
    }

    private List<Map<String, String>> createBatch(int batchIndex, int batchSize, List<Map<String, String>> list) {
        int start = batchIndex * batchSize;
        int end = Math.min(start + batchSize, list.size()); // Avoid out of bounds
        
        List<Map<String, String>> batchList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            batchList.add(list.get(i));
        }
        return batchList;
    }

    private void finalizePostProcessing() {
        // Any final steps after all threads have completed (e.g., updating a sheet or logging)
        System.out.println("Post-processing tasks completed.");
        // Utility.printLog("Multithread.log", "processmessage", "Last", "*********");
        Tasks2 task = new Tasks2("task 1");
        task.setDaemon(true);
        task.start();
        task.updateSheet1(); // Keep last call here
    }
}