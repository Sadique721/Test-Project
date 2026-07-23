package temp;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utility.Constant;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PackageComparison {

    public static void main(String[] args) throws Exception {
        // Define the input and output directories
    	String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		

        // Define the input files
        String planSheetPath = filePath + "Plandata.xlsx";
        String customerSheetPath = filePath + "MigrationCustomerWithBaseUsaegs.csv";

        // Step 1: Extract package names from Plan Sheet (XLSX) for comparison
        Set<String> planPackageNames = extractPackageNamesFromPlan(planSheetPath);

        // Step 2: Extract all rows from Customer Sheet (CSV) and check for issues (duplicate usernames, invalid radiuspolicy)
        List<String> faultyDataRows = extractFaultyRowsFromCustomer(customerSheetPath, planPackageNames);

        // Step 3: Write faulty rows to a new CSV file
        String faultyDataFile = filePath + "faultydata_" + getTimestamp() + ".csv";
        writeRowsToCSV(faultyDataFile, faultyDataRows);

        System.out.println("Process completed. Faulty data rows saved to: " + faultyDataFile);
    }

    // Extract package names from the Plan Sheet (XLSX file) from the 2nd column (index 1)
    private static Set<String> extractPackageNamesFromPlan(String planSheetPath) throws Exception {
        Set<String> packageNames = new HashSet<>();
        Workbook wb = new XSSFWorkbook(new FileInputStream(planSheetPath));
        Sheet sheet = wb.getSheetAt(0); // Assuming the Plan Sheet is the first sheet

        // Iterate through each row in the sheet (starting from row 1 to skip headers)
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            Cell packageNameCell = row.getCell(1); // 2nd column (index 1) for PACKAGE_NAME
            if (packageNameCell != null) {
                packageNames.add(packageNameCell.toString().trim());
            }
        }
        wb.close();
        return packageNames;
    }

    // Extract faulty rows from the Customer Sheet based on duplicate usernames or invalid radiuspolicy
    private static List<String> extractFaultyRowsFromCustomer(String customerSheetPath, Set<String> planPackageNames) throws IOException {
        Map<String, List<String>> usernameMap = new HashMap<>();
        List<String> faultyRows = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        String line;
        reader.readLine(); // Skip header row

        // Iterate through the CSV file and collect rows
        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");
            if (columns.length > 1) {
                String username = columns[1].trim(); // 2nd column (index 1) for username
                String radiusPolicy = columns[5].trim(); // 6th column (index 5) for radiuspolicy (Plan Name)

                // Check for duplicate usernames
                usernameMap.computeIfAbsent(username, k -> new ArrayList<>()).add(line);

                // Check if the radiuspolicy exists in the planPackageNames
                if (!planPackageNames.contains(radiusPolicy)) {
                    faultyRows.add(line); // Add row to faulty data if radiuspolicy does not match any plan
                }
            }
        }
        reader.close();

        // Check for duplicate usernames
        for (Map.Entry<String, List<String>> entry : usernameMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                // Add all duplicate rows to faulty rows list
                faultyRows.addAll(entry.getValue());
            }
        }

        return faultyRows;
    }

    // Write faulty rows to a new CSV file with the correct header
    private static void writeRowsToCSV(String outputFile, List<String> faultyRows) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        // Write the header
        String header = "sno,username,password,status,concurrentloginpolicy,radiuspolicy,additionalpolicy,param1,param2,param4,customeraltemailid,callingstationid,cui,macvalidation,msisdn,geolocation,param6,primarydns,secondarydns,primaryipv6dns,secondaryipc6dns,usedquota,startdate,enddate,cprid,migrationstatus";
        writer.write(header);
        writer.newLine(); // Move to the next line after writing header

        // Write the faulty rows
        for (String row : faultyRows) {
            writer.write(row + "\n");
        }

        writer.close();
    }

    // Get current timestamp in yyyyMMddHHmmss format
    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
