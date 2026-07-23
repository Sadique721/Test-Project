package utility;
import utility.Constant;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class SanityForApi {



        public static class DataSanityCheck {

            // Helper method to parse a CSV line considering quoted fields
            private static String[] parseCSVLine(String line) {
                return line.split(",", -1);
            }

            // Helper method to validate if a given IP address is in valid format
            private static boolean isValidIP(String ip) {
                String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                return Pattern.matches(ipPattern, ip);
            }

            // Helper method to validate if a MAC address is in valid format
            private static boolean isValidMAC(String mac) {
                String macPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
                return Pattern.matches(macPattern, mac);
            }

            // Helper method to validate the date format (yyyy-MM-dd HH:mm:ss)
            private static boolean isValidDateFormat(String date) {
                String datePattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
                return Pattern.matches(datePattern, date);
            }

            // Helper method to extract package names from the plan sheet (CSV)
            private static Set<String> extractPackageNamesFromPlan(String planSheetPath) throws IOException {
                Set<String> planPackageNames = new HashSet<>();
                BufferedReader reader = new BufferedReader(new FileReader(planSheetPath));
                String line;
                reader.readLine(); // Skip header row
                while ((line = reader.readLine()) != null) {
                    String[] columns = parseCSVLine(line);
                    if (columns.length > 0) {
                        planPackageNames.add(columns[0].trim()); // Assuming package name is the first column
                    }
                }
                reader.close();
                return planPackageNames;
            }

            // Method to extract and validate rows from the customer sheet
            private static List<String> extractFaultyRowsFromCustomer(String customerSheetPath, Set<String> planPackageNames, Map<String, Integer> columnFaultCount) throws IOException {
                Map<String, List<String>> usernameMap = new HashMap<>();
                Set<String> faultyRowsSet = new HashSet<>();
                Map<String, String> rowReasons = new HashMap<>();
                BufferedReader reader = new BufferedReader(new FileReader(customerSheetPath));
                String line;
                reader.readLine(); // Skip header row

                // Iterate through the CSV file and collect rows
                while ((line = reader.readLine()) != null) {
                    String[] columns = parseCSVLine(line);

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
                        StringBuilder reason = new StringBuilder();

                        String faultyColumn = "";

                        // Check if radiusPolicy is valid (i.e., exists in planPackageNames and is not empty)
                        if (radiusPolicy.isEmpty() || !planPackageNames.contains(radiusPolicy)) {
                            isFaulty = true;
                            faultyColumn = "RADIUSPOLICY";
                            reason.append("Invalid radiusPolicy; ");
                        }

                        // Check if param1 contains a valid IP address, or is empty
                        if (!param1.isEmpty() && !isValidIP(param1)) {
                            isFaulty = true;
                            faultyColumn = "PARAM1 (IP address)";
                            reason.append("Invalid IP address; ");
                        }

                        // Check if MAC address is valid (empty or contains only alphanumeric characters and colons)
                        if (!macAddress.isEmpty() && !isValidMAC(macAddress)) {
                            isFaulty = true;
                            faultyColumn = "CALLINGSTATIONID (MAC address)";
                            reason.append("Invalid MAC address; ");
                        }

                        // Check if the status is valid (Y, N, or SUSPEND)
                        if (status == null || status.isEmpty() || (!status.equals("Y") && !status.equals("N") && !status.equals("SUSPEND"))) {
                            isFaulty = true;
                            faultyColumn = "STATUS";
                            reason.append("Invalid status; ");
                        }

                        // Check if usedQuota is valid (only digits)
                        if (!usedQuota.isEmpty() && !usedQuota.matches("^[0-9]*$")) {
                            isFaulty = true;
                            faultyColumn = "USEDQUOTA";
                            reason.append("Invalid usedQuota; ");
                        }

                        // Check if start date and end date are in the correct format (yyyy-MM-dd HH:mm:ss) or if they are empty
                        if (startDate.isEmpty() || !isValidDateFormat(startDate)) {
                            isFaulty = true;
                            faultyColumn = "STARTDATE";
                            reason.append("Invalid start date format; ");
                        }

                        if (endDate.isEmpty() || !isValidDateFormat(endDate)) {
                            isFaulty = true;
                            faultyColumn = "ENDDATE";
                            reason.append("Invalid end date format; ");
                        }

                        // Track faults for the column
                        if (isFaulty && !faultyColumn.isEmpty()) {
                            columnFaultCount.put(faultyColumn, columnFaultCount.getOrDefault(faultyColumn, 0) + 1);
                        }

                        // If username is already present, add both the current and previous rows to faulty
                        if (usernameMap.containsKey(username)) {
                            usernameMap.get(username).add(line);
                            isFaulty = true;
                            reason.append("Duplicate username; ");
                        } else {
                            usernameMap.put(username, new ArrayList<>(Collections.singletonList(line)));
                        }

                        // If any issue is found, add the current row to faulty rows and store the reason
                        if (isFaulty) {
                            faultyRowsSet.add(line);
                            rowReasons.put(line, reason.toString().trim());
                        }
                    }
                }
                reader.close();

                // Add duplicate username rows to faulty data
                for (Map.Entry<String, List<String>> entry : usernameMap.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        faultyRowsSet.addAll(entry.getValue());
                    }
                }

                // Convert the set to a list to return
                return new ArrayList<>(faultyRowsSet);
            }

            // Method to write the fault summary to an Excel sheet
            private static void writeFaultSummaryToExcel(Map<String, Integer> columnFaultCount, String outputFilePath) throws IOException {
                Workbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("Fault Summary");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Faulty Column");
                headerRow.createCell(1).setCellValue("Reason");
                headerRow.createCell(2).setCellValue("Count");

                int rowIndex = 1;
                for (Map.Entry<String, Integer> entry : columnFaultCount.entrySet()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.createCell(1).setCellValue("Faults for " + entry.getKey());
                    row.createCell(2).setCellValue(entry.getValue());
                }

                FileOutputStream fileOut = new FileOutputStream(outputFilePath);
                wb.write(fileOut);
                fileOut.close();
                wb.close();
            }

            // Helper method to write valid rows to a CSV
            private static void writeRowsToCSV(String filePath, List<String> rows) throws IOException {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                for (String row : rows) {
                    writer.write(row);
                    writer.newLine();
                }
                writer.close();
            }

            // Helper method to write valid rows to a CSV (excluding faulty rows)
            private static void writeValidRowsToCSV(String inputFilePath, List<String> faultyRows, String outputFilePath) throws IOException {
                BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
                String line;
                reader.readLine(); // Skip header row
                writer.write(reader.readLine()); // Write header to the valid file
                writer.newLine();

                while ((line = reader.readLine()) != null) {
                    if (!faultyRows.contains(line)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                reader.close();
                writer.close();
            }

            // Get timestamp for filenames
            private static String getTimestamp() {
                return String.valueOf(System.currentTimeMillis());
            }

            // Main method to run the sanity check
            public static void sanitySheetCustomer() {
                try {
                    // Define paths
                    String fs = File.separator;
                    String filePath = "path_to_input_data"; // Define the base path for input files
                    String planSheetPath = filePath + fs + "ACTPLAN_DATA_FILE.csv";
                    String customerSheetPath = filePath + fs + "CUSTOMERSHEET.csv";

                    // Extract package names from the plan sheet
                    Set<String> planPackageNames = extractPackageNamesFromPlan(planSheetPath);

                    // Prepare the column fault count map
                    Map<String, Integer> columnFaultCount = new HashMap<>();

                    // Extract faulty rows from the customer sheet
                    List<String> faultyDataRows = extractFaultyRowsFromCustomer(customerSheetPath, planPackageNames, columnFaultCount);

                    // Write faulty rows to a CSV file
                    String faultyDataFile = filePath + fs + "faultydata_" + getTimestamp() + ".csv";
                    writeRowsToCSV(faultyDataFile, faultyDataRows);

                    // Write valid rows to a CSV file
                    String validDataFile = filePath + fs + "MigrationCustomervalid.csv";
                    writeValidRowsToCSV(customerSheetPath, faultyDataRows, validDataFile);

                    // Write fault summary to Excel
                    String faultSummaryFile = filePath + fs + "FaultSummary_" + getTimestamp() + ".xlsx";
                    writeFaultSummaryToExcel(columnFaultCount, faultSummaryFile);  // Ensure columnFaultCount is passed

                    System.out.println("Process completed. Faulty data rows saved to: " + faultyDataFile);
                    System.out.println("Valid data rows saved to: " + validDataFile);
                    System.out.println("Fault summary saved to: " + faultSummaryFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }







