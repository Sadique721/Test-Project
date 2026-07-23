package temp;

import utility.Constant;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Sheetsanity {

    public static void main(String[] args) {
        try {
            // Define the input and output directories
            String fs = Constant.FILE_SEPERATOR;
            String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

            // Define the input files
            String planSheetPath = filePath + "MigrationActPlanData.xlsx"; // Plan sheet is XLSX
            String customerSheetPath = filePath + "MigrationCustomerWithBaseUsaegs.csv"; // Customer sheet is CSV

            // Step 1: Extract package names from the Plan Sheet (XLSX) for comparison
            Set<String> planPackageNames = extractPackageNamesFromPlan(planSheetPath);

            // Step 2: Extract all rows from Customer Sheet (CSV) and check for issues (duplicate usernames, invalid radiuspolicy, invalid param1, etc.)
            List<String> faultyDataRows = extractFaultyRowsFromCustomer(customerSheetPath, planPackageNames);

            // Step 3: Write faulty rows to a new CSV file
            String faultyDataFile = filePath + "faultydata_" + getTimestamp() + ".csv";
            writeRowsToCSV(faultyDataFile, faultyDataRows);

            // Step 4: Write valid rows (those not in faultyData) to a new CSV file
            String validDataFile = filePath + "valid_data_" + getTimestamp() + ".csv";
            writeValidRowsToCSV(customerSheetPath, faultyDataRows, validDataFile);

            System.out.println("Process completed. Faulty data rows saved to: " + faultyDataFile);
            System.out.println("Valid data rows saved to: " + validDataFile);
        } catch (Exception e) {
            e.printStackTrace(); // Print error details for debugging
        }
    }

    // Extract package names from the Plan Sheet (XLSX file)
    private static Set<String> extractPackageNamesFromPlan(String planSheetPath) throws Exception {
        Set<String> packageNames = new HashSet<>();
        Workbook wb = new XSSFWorkbook(new FileInputStream(planSheetPath));
        
        // Print sheet names for debugging
        int sheetCount = wb.getNumberOfSheets();
        System.out.println("Number of sheets: " + sheetCount);
        for (int i = 0; i < sheetCount; i++) {
            System.out.println("Sheet " + i + ": " + wb.getSheetName(i)); // Print sheet names
        }
        
        // Change to use the "Base Plan" sheet directly if known by name
        Sheet sheet = wb.getSheet("BasePlan"); // Replace with the correct sheet name if needed
        
        if (sheet == null) {
            throw new Exception("Sheet of plan is not found!");
        }
        
        // Continue with extracting package names as before
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

    // Extract faulty rows from the Customer Sheet based on multiple validations (username duplication, radiuspolicy existence, valid param1, etc.)
    private static List<String> extractFaultyRowsFromCustomer(String customerSheetPath, Set<String> planPackageNames) throws IOException {
        Map<String, List<String>> usernameMap = new HashMap<>();
        Set<String> faultyRowsSet = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        String line;
        reader.readLine(); // Skip header row

        // Iterate through the CSV file and collect rows
        while ((line = reader.readLine()) != null) {
            String[] columns = parseCSVLine(line); // Use our custom method for parsing CSV lines with quoted fields

            // Ensure the row has enough columns before accessing them
            if (columns.length > 1) {
                String username = columns[1].trim(); // 2nd column (index 1) for username
                String radiusPolicy = columns.length > 5 ? columns[5].trim() : ""; // 6th column (index 5) for radiuspolicy (Plan Name)
                String param1 = columns.length > 7 ? columns[7].trim() : ""; // 8th column (index 7) for param1 (IP address)
                String macAddress = columns.length > 11 ? columns[11].trim() : ""; // 12th column (index 11) for callingstationid (MAC address)
                String status = columns.length > 3 ? columns[3].trim() : ""; // 4th column (index 3) for status
                String usedQuota = columns.length > 21 ? columns[21].trim() : ""; // 22nd column (index 21) for usedquota
                String startDate = columns.length > 22 ? columns[22].trim() : ""; // 23rd column (index 22) for startdate
                String endDate = columns.length > 23 ? columns[23].trim() : ""; // 24th column (index 23) for enddate

                boolean isFaulty = false;

                // Check if the radiuspolicy exists in the planPackageNames
                if (!radiusPolicy.isEmpty() && !planPackageNames.contains(radiusPolicy)) {
                    isFaulty = true; // Mark as faulty if radiuspolicy does not exist in plan sheet
                }

                // Check if param1 contains a valid IP address, or is empty
                if (!param1.isEmpty() && !isValidIP(param1)) {
                    isFaulty = true; // Mark as faulty if param1 is not a valid IP address
                }

                // Check if MAC address is valid (empty or contains only alphanumeric characters and colons)
                if (!macAddress.isEmpty() && !isValidMAC(macAddress)) {
                    isFaulty = true; // Mark as faulty if MAC address is invalid
                }

                // Check if the status is valid (Y, N, or SUSPEND)
                if (!status.isEmpty() && !status.equals("Y") && !status.equals("N") && !status.equals("SUSPEND")) {
                    isFaulty = true; // Mark as faulty if status is not one of the valid values
                }

                // Check if the usedQuota is faulty (contains letters or special characters)
                if (!usedQuota.isEmpty() && !usedQuota.matches("^[0-9]*$")) {
                    isFaulty = true; // Mark as faulty if usedQuota contains non-numeric values or special characters
                }

                // Check if start date and end date are in the correct format (yyyy-MM-dd HH:mm:ss) or if they are empty
                if (startDate.isEmpty() || !isValidDateFormat(startDate)) {
                    isFaulty = true; // Mark as faulty if start date is empty or in the wrong format
                }

                if (endDate.isEmpty() || !isValidDateFormat(endDate)) {
                    isFaulty = true; // Mark as faulty if end date is empty or in the wrong format
                }

                // If username is already present, add both the current and previous rows to faulty
                if (usernameMap.containsKey(username)) {
                    usernameMap.get(username).add(line); // Add the current row to the list of faulty rows for this username
                    isFaulty = true; // Mark as faulty due to duplication
                } else {
                    usernameMap.put(username, new ArrayList<>(Collections.singletonList(line))); // Otherwise, store the first occurrence
                }

                // If any issue is found, add the current row to faulty rows
                if (isFaulty) {
                    faultyRowsSet.add(line);
                }
            }
        }
        reader.close();

        // Add duplicate username rows to faulty data
        for (Map.Entry<String, List<String>> entry : usernameMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                faultyRowsSet.addAll(entry.getValue()); // Add all duplicate rows to faulty rows
            }
        }

        // Convert the set to a list to return
        return new ArrayList<>(faultyRowsSet);
    }

    // Helper method to parse CSV lines and handle quoted fields (for param4 and other fields with commas inside quotes)
    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Regex to split on commas outside quotes
    }

    // Helper method to validate param1 (IP address format)
    private static boolean isValidIP(String param1) {
        return param1.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}$") && isValidIPParts(param1);
    }

    // Helper method to check that each part of the IP address is within valid ranges (0-255)
    private static boolean isValidIPParts(String param1) {
        String[] parts = param1.split("\\.");
        for (String part : parts) {
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) {
                return false; // Invalid IP if any part is outside the range 0-255
            }
        }
        return true;
    }

    // Helper method to validate MAC address format
    private static boolean isValidMAC(String mac) {
        return mac.matches("^[0-9A-Fa-f:]+$");
    }

    // Helper method to validate date format (yyyy-MM-dd HH:mm:ss) without milliseconds or empty values
    private static boolean isValidDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return false; // Invalid if the date is empty or null
        }

        try {
            // Remove any milliseconds (if present)
            if (date.contains(".")) {
                return false; // If there are milliseconds, it's invalid
            }

            // Now check if the date matches the required format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);  // Ensure strict parsing
            sdf.parse(date);  // Try to parse the date
            return true;  // Valid date format
        } catch (Exception e) {
            return false;  // Invalid format if exception occurs
        }
    }

    // Write rows to a new CSV file
    private static void writeRowsToCSV(String outputFile, List<String> rows) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        // Write the header
        String header = "SNo,USERNAME,PASSWORD,STATUS,CONCURRENTLOGINPOLICY,RADIUSPOLICY,ADDITIONALPOLICY,PARAM1,PARAM2,PARAM4,CUSTOMERALTEMAILID,CALLINGSTATIONID,CUI,MACVALIDATION,MSISDN,GEOLOCATION,PARAM6,PRIMARYDNS,SECONDARYDNS,PRIMARYIPV6DNS,SECONDARYIPV6DNS,USEDQUOTA,STARTDATE,ENDDATE,CPRID,MIGRATONSTATUS";
        writer.write(header);
        writer.newLine(); // Move to the next line after writing header

        // Write the rows
        for (String row : rows) {
            writer.write(row + "\n");
        }

        writer.close();
    }

    // Write valid rows (those not in faultyData) to a new CSV file
    private static void writeValidRowsToCSV(String customerSheetPath, List<String> faultyDataRows, String outputFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        List<String> validRows = new ArrayList<>();
        String line;
        reader.readLine(); // Skip header row

        // Collect all rows that are not faulty
        while ((line = reader.readLine()) != null) {
            if (!faultyDataRows.contains(line)) {
                validRows.add(line);
            }
        }
        reader.close();

        // Write valid rows to the output CSV
        writeRowsToCSV(outputFilePath, validRows);
    }

    // Get current timestamp in yyyyMMddHHmmss format
    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
