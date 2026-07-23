package temp;

import utility.Constant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanityWithValidation {
    // Regex for IP Address validation
    private static final String IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";

    // Regex for MAC Address validation
    private static final String MAC_REGEX = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$";

    // Set to keep track of duplicate usernames
    private static final Set<String> usernameSet = new HashSet<>();

    static String inputFilePath = Constant.BASE_PATH + File.separator + "TestData" + File.separator + "input" + File.separator + "MigrationCustomerWithBaseUsaegs.csv";
    static String validFilePath = Constant.BASE_PATH + File.separator + "TestData" + File.separator + "input" + File.separator + "valid.csv";
    static String invalidFilePath = Constant.BASE_PATH + File.separator + "TestData" + File.separator + "input" + File.separator + "invalid.csv";

    public static void main(String[] args) {
        try {
            fileProcessValidInvalid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fileProcessValidInvalid() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(inputFilePath));
        List<String[]> validRows = new ArrayList<>();
        List<String[]> invalidRows = new ArrayList<>();

        // First line is headers
        String[] headers = lines.get(0).split("\\s+");

        // Process data rows
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = parseComplexCsvLine(lines.get(i));

            if (validateRecord(headers, fields)) {
                validRows.add(fields);
            } else {
                invalidRows.add(fields);
            }
        }

        // Write results with headers
        writeCsvWithHeaders(validFilePath, headers, validRows);
        writeCsvWithHeaders(invalidFilePath, headers, invalidRows);

        System.out.println("Validation complete. Valid rows: " + validRows.size() +
                ", Invalid rows: " + invalidRows.size());
    }

    public static String[] parseComplexCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        boolean escapeNext = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escapeNext) {
                currentField.append(c);
                escapeNext = false;
                continue;
            }

            switch (c) {
                case '\\':
                    escapeNext = true;
                    break;
                case '"':
                    inQuotes = !inQuotes;
                    currentField.append(c);
                    break;
                case ',':
                    if (!inQuotes) {
                        fields.add(currentField.toString().trim());
                        currentField = new StringBuilder();
                    } else {
                        currentField.append(c);
                    }
                    break;
                default:
                    currentField.append(c);
            }
        }

        // Add last field
        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }

    private static boolean validateRecord(String[] headers, String[] fields) {
        // Find indexes for specific fields
        int usernameIndex = findHeaderIndex(headers, "username");
        int param1Index = findHeaderIndex(headers, "param1");
        int callingStationIdIndex = findHeaderIndex(headers, "callingstationid");
        int migrationStatus = findHeaderIndex(headers, "migrationstatus");

        // Validate only if all required indexes are found
        if (usernameIndex == -1 || param1Index == -1 || callingStationIdIndex == -1) {
            fields[migrationStatus] = "IndexNotFound";
            return false;
        }

        // Ensure fields array has enough elements
        if (fields.length <= Math.max(Math.max(usernameIndex, param1Index), callingStationIdIndex)) {
            fields[migrationStatus] = "OutOfIndex";
            return false;
        }

        String username = fields[usernameIndex].trim();
        String param1 = fields[param1Index].trim();
        String callingStationId = fields[callingStationIdIndex].trim();

        // Check for duplicate username
        if (usernameSet.contains(username)) {
            fields[migrationStatus] = "DuplicateUserName";
            return false;
        }
        usernameSet.add(username);

        // Validate IP address in param1 if not empty
        boolean isParam1Valid = !param1.isEmpty() || isValidIp(param1);
        if (!isParam1Valid){
            fields[migrationStatus] = "InvalidIp";
        }

        // Validate MAC address in callingStationId if not empty
        boolean isCallingStationIdValid = !callingStationId.isEmpty() || isValidMac(callingStationId);
        if (!isCallingStationIdValid){
            fields[migrationStatus] = "InvalidMac";
        }

        if (isParam1Valid && isCallingStationIdValid) {
            fields[migrationStatus] = "Success"; //we need to remove
        }

        return isParam1Valid && isCallingStationIdValid;
    }

    private static int findHeaderIndex(String[] headers, String targetHeader) {
        // Check if the first element contains all headers
        if (headers[0].contains(",")) {
            // Split headers[0] by commas to separate them into individual headers
            String[] headerArray = headers[0].split(",");

            // Compare each individual header in the array
            for (int i = 0; i < headerArray.length; i++) {
                if (headerArray[i].trim().equalsIgnoreCase(targetHeader.trim())) {
                    return i;
                }
            }
        }

        return -1;  // If no match is found
    }

    public static boolean isValidIp(String ip) {
        Pattern pattern = Pattern.compile(IP_REGEX);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()) {
            String[] parts = ip.split("\\.");
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isValidMac(String mac) {
        Pattern pattern = Pattern.compile(MAC_REGEX);
        return pattern.matcher(mac).matches();
    }

    public static void writeCsvWithHeaders(String fileName, String[] headers, List<String[]> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            // Write headers
            writer.write(String.join(",", headers));
            writer.newLine();

            // Write rows
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }
}