package sanityCheck;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.*;

import utility.Constant;

public class ActCustomerSheetSanity {

    // sanitySheetCustomer
    public static void sanitySheetCustomer() {
        try {
            // Define the input and output directories
            String fs = Constant.FILE_SEPERATOR;
            String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

            // Define the input files
            String customerSheetPath = filePath + Constant.ACTCUSTOMERCSV; // Customer sheet is CSV

            // Step 1: Extract package names from the database
            Set<String> planPackageNames = extractPackageNamesFromDB();

            // Step 2: Extract all rows from Customer Sheet (CSV) and check for issues (duplicate usernames, invalid radiuspolicy, invalid param1, etc.)
            List<String> faultyDataRows = extractFaultyRowsFromCustomer(customerSheetPath, planPackageNames);

            // Step 3: Write faulty rows to a new CSV file
            String faultyDataFile = filePath + "faultydata_" + getTimestamp() + ".csv";
            writeRowsToCSV(faultyDataFile, faultyDataRows, customerSheetPath);

            // Step 4: Write valid rows (those not in faultyData) to a new CSV file
            String validDataFile = filePath + "MigrationCustomervalid" + ".csv";
            List<String> validDataRows = writeValidRowsToCSV(customerSheetPath, faultyDataRows, validDataFile);

            // Count the number of valid and invalid rows
            int validCount = validDataRows.size();
            int invalidCount = faultyDataRows.size();

            // Output the results
            System.out.println("Process completed.");
            System.out.println("Faulty data rows saved to: " + faultyDataFile);
            System.out.println("Valid data rows saved to: " + validDataFile);
            System.out.println("Total valid rows: " + validCount);
            System.out.println("Total invalid rows: " + invalidCount);

        } catch (Exception e) {
            e.printStackTrace(); // Print error details for debugging
        }
    }

    // Extract package names from the database
    private static Set<String> extractPackageNamesFromDB() throws SQLException {
        Set<String> packageNames = new HashSet<>();

        // Database connection setup (Assuming you're using MySQL or similar JDBC database)
        String dbUrl = Constant.URLCONVERGE;
        String dbUser = Constant.USERNAME;
        String dbPassword = Constant.PASSWORD;
        String sql = "select NAME from Savbillcpm.tblmpostpaidplan t";

        // Establish the database connection
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Fetch plan names and add them to the set
            while (rs.next()) {
                String planName = rs.getString("NAME").trim();
                packageNames.add(planName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching data from database", e);
        }

        return packageNames;
    }

    // Extract faulty rows from the Customer Sheet based on multiple validations
    private static List<String> extractFaultyRowsFromCustomer(String customerSheetPath, Set<String> planPackageNames) throws IOException {
        Map<String, List<String>> usernameMap = new HashMap<>();
        Set<String> faultyRowsSet = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        String line;
        String header = reader.readLine(); // Read the header row

        // Iterate through the CSV file and collect rows
        while ((line = reader.readLine()) != null) {
            String[] columns = parseCSVLine(line);

            // Ensure the row has enough columns before accessing them
            if (columns.length > 1) {
                String username = columns[1].trim(); // 2nd column (index 1) for username
                String radiusPolicy = columns.length > 5 ? columns[5].trim() : ""; // 6th column (index 5) for radiuspolicy (Plan Name)
                String param1 = columns.length > 7 ? columns[7].trim() : ""; // 8th column (index 7) for param1 (IP address)
                String status = columns.length > 3 ? columns[3].trim() : ""; // 4th column (index 3) for status
                String usedQuota = columns.length > 21 ? columns[21].trim() : ""; // 22nd column (index 21) for usedquota
                String startDate = columns.length > 22 ? columns[22].trim() : ""; // 23rd column (index 22) for startdate
                String endDate = columns.length > 23 ? columns[23].trim() : ""; // 24th column (index 23) for enddate

                boolean isFaulty = false;

                // Handling username with scientific notation (if applicable)
                if (username.matches("^[0-9]+\\.[0-9]+E[+-]?[0-9]+$")) {
                    try {
                        double value = Double.parseDouble(username);
                        username = String.format("%.0f", value); // Format to remove scientific notation
                    } catch (NumberFormatException e) {
                        // If it can't be parsed to a number, keep it as is
                    }
                }

                // Check if radiusPolicy is valid (i.e., exists in planPackageNames and is not empty)
                if (radiusPolicy.isEmpty() || !planPackageNames.contains(radiusPolicy)) {
                    isFaulty = true; // Mark as faulty if radiusPolicy is empty or not found in the plan sheet
                }

                // Check if param1 contains a valid IP address, or is empty
                if (!param1.isEmpty() && !isValidIP(param1)) {
                    isFaulty = true; // Mark as faulty if param1 is not a valid IP address
                }

                // Check if the status is valid (Y, N, or SUSPEND)
                if (status == null || status.isEmpty() || (!status.equals("Y") && !status.equals("N") && !status.equals("SUSPEND"))) {
                    isFaulty = true; // Mark as faulty if status is null, empty, or not one of the valid values
                }

                // Check if the usedQuota is faulty (contains letters or special characters)
                if (usedQuota.matches("^[0-9]+\\.[0-9]+E[+-]?[0-9]+$")) {
                    try {
                        double value = Double.parseDouble(usedQuota);
                        usedQuota = String.format("%.0f", value); // Format to remove scientific notation
                    } catch (NumberFormatException e) {
                        // If it can't be parsed to a number, keep it as is
                    }
                }

                // Check if usedQuota is valid (only digits)
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

    // Helper method to validate date format
    private static boolean isValidDateFormat(String date) {
        try {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date); // Validate the date format
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to write rows to CSV with headers dynamically from the input file
    private static void writeRowsToCSV(String filePath, List<String> rows, String customerSheetPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        String header = reader.readLine(); // Read header from the original sheet
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // Write the header from the original sheet
        writer.write(header);
        writer.newLine();

        // Write all rows with updated Sno (serial number)
        int sno = 1;
        for (String row : rows) {
            String[] columns = parseCSVLine(row);
            // Update the first column (Sno) with the incremented value
            columns[0] = String.valueOf(sno++);
            writer.write(String.join(",", columns));
            writer.newLine();
        }

        writer.close();
    }

    // Helper method to write valid rows to CSV
    private static List<String> writeValidRowsToCSV(String customerSheetPath, List<String> faultyRows, String validDataFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
        List<String> validRows = new ArrayList<>();
        String header = reader.readLine(); // Read the header row

        String line;
        while ((line = reader.readLine()) != null) {
            if (!faultyRows.contains(line)) {
                validRows.add(line);
            }
        }
        reader.close();
        writeRowsToCSV(validDataFile, validRows, customerSheetPath); // Add headers to valid data CSV
        return validRows;
    }

    // Helper method to get timestamp
    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}
