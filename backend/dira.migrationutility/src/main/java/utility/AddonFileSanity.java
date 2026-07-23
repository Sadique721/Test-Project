package utility;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ibm.icu.text.SimpleDateFormat;
import java.util.logging.*;

public class AddonFileSanity {

    // Initialize the logger
    private static final Logger logger = Logger.getLogger(AddonFileSanity.class.getName());

    public static void main(String[] args) {
        try {
            String fs = Constant.FILE_SEPERATOR;
            String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
            
            // Input paths
            String planSheetPath = filePath + Constant.ACTPLAN_DATA_FILE;
            String addonSheetPath = filePath + Constant.CSV;  // Addon file path

            // Step 1: Extract package names from the BasePlan sheet
            Set<String> basePlanPackageNames = extractPackageNamesFromBasePlan(planSheetPath);

            // Step 2: Extract Vod and Bod values from ActPlan file
            Set<String> vodPlans = extractPlansFromActPlan(planSheetPath, "vod");
            Set<String> bodPlans = extractPlansFromActPlan(planSheetPath, "bod");

            // Step 3: Extract rows from Addon File and check for issues (missing plans, invalid fields)
            List<String> faultyAddonRows = extractFaultyRowsFromAddon(addonSheetPath, basePlanPackageNames, vodPlans, bodPlans);

            // Step 4: Write faulty addon rows to a new CSV file
            String faultyAddonFile = filePath + "faultyAddon_" + getTimestamp() + ".csv";
            writeRowsToCSV(faultyAddonFile, faultyAddonRows);

            // Step 5: Write valid rows (addon rows that are not faulty) to a new CSV file
            String validAddonFile = filePath + "MigrationAddonValid.csv";
            writeValidRowsToCSV(addonSheetPath, faultyAddonRows, validAddonFile);

            // Log success messages
            logger.info("Addon file sanity check completed.");
            logger.info("Faulty addon rows saved to: " + faultyAddonFile);
            logger.info("Valid addon rows saved to: " + validAddonFile);
        } catch (Exception e) {
            logger.severe("An error occurred during the Addon file sanity check: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Extract package names from the BasePlan sheet (XLSX)
    private static Set<String> extractPackageNamesFromBasePlan(String planSheetPath) throws Exception {
        Set<String> packageNames = new HashSet<>();
        Workbook wb = new XSSFWorkbook(new FileInputStream(planSheetPath));
        Sheet sheet = wb.getSheet("BasePlan");

        if (sheet == null) {
            throw new Exception("BasePlan sheet not found!");
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;  // Skip header
            Cell packageNameCell = row.getCell(1);  // Column 2 (index 1) for BasePlan
            if (packageNameCell != null) {
                String planName = packageNameCell.toString().trim().toLowerCase();
                packageNames.add(planName);
            }
        }
        wb.close();
        return packageNames;
    }

    // Extract Vod or Bod plans from the ActPlan file (Column 2 for Vod and Bod)
    private static Set<String> extractPlansFromActPlan(String planSheetPath, String sheetName) throws Exception {
        Set<String> plans = new HashSet<>();
        Workbook wb = new XSSFWorkbook(new FileInputStream(planSheetPath));
        Sheet sheet = wb.getSheet(sheetName);

        if (sheet == null) {
            throw new Exception(sheetName + " sheet not found!");
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;  // Skip header
            Cell cell = row.getCell(1);  // Column 2 (index 1) for Vod/Bod
            if (cell != null) {
                String planName = cell.toString().trim().toLowerCase();
                plans.add(planName);
            }
        }
        wb.close();
        return plans;
    }

 // Validate the USEDQUOTA field
    private static boolean validateUsedQuota(String usedQuota) {
        // Allow empty or null as valid
        if (usedQuota == null || usedQuota.trim().isEmpty()) {
            return true; // Valid if it's empty or null
        }

        // Try parsing the number (handles scientific notation)
        try {
            // Parse the number as a double to handle scientific notation
            double quota = Double.parseDouble(usedQuota.trim());

            // Check if it's non-negative and a whole number (integer) by checking if there's no fractional part
            if (quota >= 0 && quota == (long) quota) {
                // If it's a non-negative whole number, store as long (if needed)
                long longQuota = (long) quota;  // Store as long if it's a valid whole number

                // Optionally, you can log or use the longQuota value here if you need it.
                // For now, we just return true as it's a valid quota value.
                return true;
            } else {
                return false;  // Invalid if it's negative or not a whole number
            }
        } catch (NumberFormatException e) {
            // If it couldn't be parsed, it's invalid
            return false;
        }
    }

    // Validate the date format (yyyy-MM-dd HH:mm:ss) and check if empty
    private static boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false; // Invalid if the date is empty
        }

        // Strictly match the format yyyy-MM-dd HH:mm:ss without milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        
        try {
            // Parse the date using the SimpleDateFormat
            sdf.parse(date);
            
            // Check for any milliseconds in the date (invalid if present)
            if (date.length() > 19) {  // Length of yyyy-MM-dd HH:mm:ss is 19 characters
                return false;  // Invalid if milliseconds or extra characters are present
            }
            
            return true;  // Valid if it matches the format exactly
        } catch (Exception e) {
            return false; // Invalid if it doesn't match the format
        }
    }

    // Extract rows from the Addon File and check for issues (existing plans, addon plans)
    private static List<String> extractFaultyRowsFromAddon(String addonFilePath, Set<String> basePlanPackageNames, Set<String> vodPlans, Set<String> bodPlans) throws IOException {
        List<String> faultyRows = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(addonFilePath));
        String line;
        reader.readLine();  // Skip header row

        // Iterate through the addon file
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;  // Skip empty lines
            }
            String[] columns = line.split(",");

            // Ensure that we have enough columns (6th column is index 5 for Addon)
            if (columns.length > 5) {
                String existingPlan = columns[4].trim().toLowerCase();  // Existing Plan Column (5th column)
                String addonPlan = columns[5].trim().toLowerCase();    // Addon Plan Column (6th column)

                // Check if Existing Plan is in BasePlan
                if (!basePlanPackageNames.contains(existingPlan)) {
                    faultyRows.add(line);  // Mark as faulty if Existing Plan not found in BasePlan
                }

                // Check if Addon Plan is in Vod or Bod
                if (!vodPlans.contains(addonPlan) && !bodPlans.contains(addonPlan)) {
                    faultyRows.add(line);  // Mark as faulty if Addon Plan not found in Vod or Bod
                }

                // Validate USEDQUOTA
                String usedQuota = (columns.length > 6) ? columns[6].trim() : "";  // Safely access USEDQUOTA (index 6)
                if (!validateUsedQuota(usedQuota)) {
                    faultyRows.add(line);  // Mark as faulty if USEDQUOTA is invalid
                }

                // Validate STARTDATE and ENDDATE
                String startDate = (columns.length > 2) ? columns[2].trim() : "";
                String endDate = (columns.length > 3) ? columns[3].trim() : "";

                if (!validateDate(startDate) || !validateDate(endDate)) {
                    faultyRows.add(line);  // Mark as faulty if any of the dates are invalid
                }
            }
        }
        reader.close();
        return faultyRows;
    }

    // Write rows to a CSV file
    private static void writeRowsToCSV(String outputFile, List<String> rows) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        String header = "SNO,USERNAME,STARTDATE,ENDDATE,EXISTINGPLANNAME,ADDON,USEDQUOTA,CPRID,MIGRATONSTATUS";
        writer.write(header);
        writer.newLine();

        for (String row : rows) {
            writer.write(row + "\n");
        }
        writer.close();
    }

    // Write valid rows (addon rows not in faultyData) to a new CSV file
    private static void writeValidRowsToCSV(String addonFilePath, List<String> faultyRows, String outputFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(addonFilePath));
        List<String> validRows = new ArrayList<>();
        String line;
        reader.readLine();  // Skip header row

        // Collect all rows that are not faulty
        while ((line = reader.readLine()) != null) {
            if (!faultyRows.contains(line)) {
                validRows.add(line);
            }
        }
        reader.close();

        // Write valid rows to the output CSV
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        String header = "SNO,USERNAME,STARTDATE,ENDDATE,EXISTINGPLANNAME,ADDON,USEDQUOTA,CPRID,MIGRATONSTATUS";
        writer.write(header);
        writer.newLine();

        int sno = 1;
        for (String row : validRows) {
            String[] columns = row.split(",");
            columns[0] = String.valueOf(sno);  // Add the SNO column
            String updatedRow = String.join(",", columns);
            writer.write(updatedRow);
            writer.newLine();
            sno++;
        }
        writer.close();
    }

    // Get current timestamp in yyyyMMddHHmmss format
    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
