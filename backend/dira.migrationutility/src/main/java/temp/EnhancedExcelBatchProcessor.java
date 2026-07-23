package temp;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EnhancedExcelBatchProcessor {

    // This method updates cells in an Excel file, performing thread-safe operations by avoiding synchronization
    public void setCellValue2(String filePath, String fileName, String sheetName) {
        String fs = File.separator;
        File file = new File(filePath + fs + fileName);

        // Check if file exists and is not empty
        if (!file.exists() || file.length() == 0) {
            System.err.println("The file is either missing or empty: " + file.getAbsolutePath());
            return; // Exit early if the file is missing or empty
        }

        try (FileInputStream fis = new FileInputStream(file);
             OPCPackage opcPackage = OPCPackage.open(fis);  // Open the file as a ZIP package
             XSSFWorkbook workbook = new XSSFWorkbook(opcPackage)) {

            XSSFSheet sheet = workbook.getSheet(sheetName);
            UpdateSheet us = new UpdateSheet();
            Map<String, String> map = us.getRowList();
            Set<String> keys = map.keySet();
            Iterator<String> keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int rowIndex = Integer.parseInt(key);
                String[] colsAndValuesArray = map.get(key).split("#");

                for (String colAndValue : colsAndValuesArray) {
                    String[] temp = colAndValue.split(":");
                    String colName = temp[0];
                    String cellValue = temp[1];
                    int colIndex = getColumnIndex(sheet, colName);

                    // Ensure the row exists, if not, create it
                    Row row = sheet.getRow(rowIndex) != null ? sheet.getRow(rowIndex) : sheet.createRow(rowIndex);
                    Cell cell = row.getCell(colIndex) != null ? row.getCell(colIndex) : row.createCell(colIndex);

                    // Set the new value for the cell
                    cell.setCellValue(cellValue);
                }

                // Remove the row after updating it from the list
                us.removeRowFromList(key);
            }

            // Writing the changes back to the file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error opening or processing the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Dynamically adjusts batch size based on available system resources
    public void executeThreadsBatch(List<Map<String, String>> list) {
        int availableCores = Runtime.getRuntime().availableProcessors();
        int totalThreads = Math.min(availableCores, 10); // Use a maximum of 10 threads for batch processing
        System.out.println("Total Threads: " + totalThreads);

        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

        int totalRows = list.size();
        int batchSize = getBatchSize(totalRows);

        System.out.println("Total Rows: " + totalRows);
        System.out.println("Batch Size: " + batchSize);

        int totalBatches = (totalRows + batchSize - 1) / batchSize;

        for (int i = 0; i < totalBatches; i++) {
            List<Map<String, String>> batchList = createBatchList(i, batchSize, list);
            Runnable worker = new WorkerThread(i + "", batchList);
            executor.execute(worker); // Submit the task to the thread pool
        }

        executor.shutdown();
        try {
            // Wait for all tasks to finish
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                System.err.println("Timeout reached before all threads completed!");
            }
        } catch (InterruptedException e) {
            System.err.println("Thread execution interrupted: " + e.getMessage());
            e.printStackTrace();
        }

        // Final update after all threads finish processing
        Tasks2 finalTask = new Tasks2("Final Task");
        finalTask.setDaemon(true);
        finalTask.start();
        System.out.println("Finished all threads");
    }

    // Calculate the batch size dynamically based on the total rows
    private int getBatchSize(int totalRows) {
        if (totalRows > 10000) {
            return 10; // For very large datasets, process in smaller batches
        } else if (totalRows > 1000) {
            return 100; // For moderately large datasets
        } else {
            return 21; // Default batch size for smaller datasets
        }
    }

    // Creates a sublist (batch) from the main list based on the batch index and size
    private List<Map<String, String>> createBatchList(int batchIndex, int batchSize, List<Map<String, String>> list) {
        int start = batchIndex * batchSize;
        int end = Math.min(start + batchSize, list.size());
        return new ArrayList<>(list.subList(start, end));
    }

    // Get the column index based on the column name
    private int getColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0); // Assume the first row contains column headers
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                return cell.getColumnIndex();
            }
        }
        return -1; // Column not found
    }
}

class WorkerThread1 implements Runnable {
    private String taskId;
    private List<Map<String, String>> batchList;

    public WorkerThread1(String taskId, List<Map<String, String>> batchList) {
        this.taskId = taskId;
        this.batchList = batchList;
    }

    @Override
    public void run() {
        try {
            System.out.println("Processing task: " + taskId);
            // Simulate batch processing
            for (Map<String, String> row : batchList) {
                // Process each row (e.g., update Excel)
                // For example, update Excel file here
            }
        } catch (Exception e) {
            System.err.println("Error processing task " + taskId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class Tasks3 extends Thread {
    private String taskName;

    public Tasks3(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println("Executing final task: " + taskName);
        // Perform final update or cleanup here
    }
}

