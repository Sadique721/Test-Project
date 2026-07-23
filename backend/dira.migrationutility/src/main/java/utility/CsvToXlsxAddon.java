package utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CsvToXlsxAddon {
    public static void csvtoxlsxAddon() {
        String fs = Constant.FILE_SEPERATOR;
        String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
        String csvFilePath = filePath+Constant.CSVADDON;
        String xlsxFilePath = filePath+Constant.ACTCUSTOMER_ADDON_DATA_FILE;

        try {
            // Create a workbook for the XLSX file
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("MigrationCustomerWithAddonUsage");

            // Define cell style for borders
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Read the CSV file using Apache Commons CSV
            Reader reader = new FileReader(csvFilePath);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withQuote('"')   // Specify to treat double quotes as quote characters
                    .withIgnoreEmptyLines() // Ignore empty lines
                    .parse(reader);

            int rowNum = 0;

            // Iterate through each record (row) in the CSV
            for (CSVRecord record : records) {
                Row row = sheet.createRow(rowNum++);

                // Iterate through each field (column) in the CSV record
                for (int colNum = 0; colNum < record.size(); colNum++) {
                    String value = record.get(colNum);

                    // Treat the value as text to avoid Excel's scientific notation issue
                    Cell cell = row.createCell(colNum, CellType.STRING);
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle); // Apply border style to each cell
                }
            }

            // Auto-size the columns for better readability
            for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the XLSX file to disk
            try (FileOutputStream fileOut = new FileOutputStream(xlsxFilePath)) {
                workbook.write(fileOut);
            }

            workbook.close();
            reader.close();

            System.out.println("CSV to XLSX conversion completed with borders of addon customer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    }

