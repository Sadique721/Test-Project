package temp;
	import org.apache.poi.ss.usermodel.*;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;

	import java.io.*;
	import java.nio.file.*;
	import java.text.SimpleDateFormat;
	import java.util.*;

	public class CSVProcessor {

	    public static void main(String[] args) throws Exception {
	        // Step 1: Convert XLSX to CSV (using Apache POI to read XLSX)
	        convertXLSXToCSV("base_plan_template_savbillt-v1.xlsx", "plan.csv", 1);

	        // Step 2: Extract unique values from both CSVs
	        Set<String> file1Values = extractUniqueValuesFromCSV("plan.csv", 2);
	        Set<String> file2Values = extractUniqueValuesFromCSV("MigrationActCustomerWithBaseUsaegs.csv", 6);

	        // Step 3: Find unique values present only in the second file (sorted_file2_column.txt)
	        file2Values.removeAll(file1Values);

	        // Step 4: Create a unique output file with a timestamp
	        String outputFileName = "unique_values_" + getTimestamp() + ".csv";
	        writeToCSV(outputFileName, file2Values);

	        // Step 5: Clean the MigrationActCustomerWithBaseUsaegs.csv by removing BOM
	        removeBOM("MigrationActCustomerWithBaseUsaegs.csv", "MigrationCustomerWithBaseUsaegs.csv");

	        // Step 6: Convert the cleaned CSV to XLSX (using LibreOffice from Java)
	        convertCSVToXLSX("MigrationCustomerWithBaseUsaegs.csv");

	        // Step 7: Clean up intermediate files
	        cleanUpFiles("plan.csv", "sorted_file1_column.txt", "sorted_file2_column.txt", "unique_in_file2_only.txt");

	        // Inform user the process is complete
	        System.out.println("Process completed successfully. The unique values file is saved as " + outputFileName);
	    }

	    // Convert XLSX to CSV
	    private static void convertXLSXToCSV(String xlsxFile, String csvFile, int sheetIndex) throws Exception {
	        Workbook wb = new XSSFWorkbook(new FileInputStream(xlsxFile));
	        Sheet sheet = wb.getSheetAt(sheetIndex);

	        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
	        for (Row row : sheet) {
	            StringBuilder sb = new StringBuilder();
	            for (Cell cell : row) {
	                sb.append(cell.toString()).append(",");
	            }
	            writer.write(sb.toString().replaceAll(",$", "") + "\n");
	        }
	        writer.close();
	        wb.close();
	    }

	    // Extract unique values from a specific column of a CSV
	    private static Set<String> extractUniqueValuesFromCSV(String csvFile, int columnIndex) throws IOException {
	        Set<String> uniqueValues = new HashSet<>();
	        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
	        String line;
	        reader.readLine(); // Skip the header
	        while ((line = reader.readLine()) != null) {
	            String[] columns = line.split(",");
	            if (columns.length > columnIndex) {
	                uniqueValues.add(columns[columnIndex].trim());
	            }
	        }
	        reader.close();
	        return uniqueValues;
	    }

	    // Get current timestamp in yyyyMMddHHmmss format
	    private static String getTimestamp() {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	        return sdf.format(new Date());
	    }

	    // Write unique values to CSV
	    private static void writeToCSV(String outputFile, Set<String> values) throws IOException {
	        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
	        writer.write("Unique Values\n");
	        for (String value : values) {
	            writer.write(value + "\n");
	        }
	        writer.close();
	    }

	    // Remove BOM from CSV
	    private static void removeBOM(String inputFile, String outputFile) throws IOException {
	        InputStream inputStream = new FileInputStream(inputFile);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

	        // Skip BOM if present
	        int firstByte = reader.read();
	        if (firstByte == 0xEF) {
	            reader.read(); // Skip next two bytes for BOM
	            reader.read();
	        } else {
	            writer.write(firstByte);
	        }

	        String line;
	        while ((line = reader.readLine()) != null) {
	            writer.write(line + "\n");
	        }
	        reader.close();
	        writer.close();
	    }

	    // Convert CSV to XLSX using LibreOffice
	    private static void convertCSVToXLSX(String csvFile) throws IOException, InterruptedException {
	        ProcessBuilder processBuilder = new ProcessBuilder("libreoffice", "--headless", "--convert-to", "xlsx", csvFile);
	        processBuilder.inheritIO();
	        Process process = processBuilder.start();
	        process.waitFor();
	    }

	    // Clean up intermediate files
	    private static void cleanUpFiles(String... files) {
	        for (String file : files) {
	            try {
	                Files.deleteIfExists(Paths.get(file));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}



