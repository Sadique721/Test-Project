package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.monitorjbl.xlsx.StreamingReader;

import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class TempExcelDataReader {

	public static void main(String args[]) throws IOException, InvalidFormatException {
		
		StopWatch sw = new StopWatch();
		sw.start();
		//writeXLS(100);
		//read3();
		//read2();
		sw.stop();
		
		//"2024-05-27T10:31:10.171Z",
		
		String date = Utility.getCurrentDateTimeByProvidedFormat("YYYY-MM-dd HH:mm:ss.SSSZ");
				System.out.println("date : " + date);
		//System.out.println("Duration : " + sw.getTime());
		//read2();
		//readXLS();
		

	}
	
	public static void writeXLS(int ms) {
		
		String filePath =  Constant.BASE_PATH + "\\TestData\\input\\";
		String fileName = "DemographicMasterData.xlsx";
		String sheetName = "Country";
		
		
		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		
		rw.setCellValue(filePath, fileName, sheetName, "2", "MigrationStatus", "Success (" + ms + "ms)");
		
		
		
	}
	
	
	public static void read3() throws IOException {
		
		List<Map<String, String>> containerList = new ArrayList<Map<String, String>>(); //Create map
		
		String filePath =  Constant.BASE_PATH + "\\TestData\\input\\Reference\\";
		String fileName = "Migration V3.1.xlsx";
		String sheetName = "Geogaraphical Areas_2";
		//String fileName = "SroreDept transfer Testing data details 1-19-2023.xlsx";
		//String sheetName = "storeTransfer";
		DataFormatter dataFormatter = new DataFormatter();
		
		File myFile = new File(filePath + fileName);
		///FileInputStream fis = new FileInputStream(myFile);
		InputStream is = new FileInputStream(myFile);
		
		//Workbook workbook = StreamingReader.builder().open(is);
		//Workbook myWorkBook = StreamingReader.builder().open(is);
		Workbook myWorkBook = StreamingReader.builder().rowCacheSize(1000).bufferSize(4096).open(is);
		
		// Finds the workbook instance for XLSX file 
		//XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		
		// Return first sheet from the XLSX workbook 
		Sheet mySheet = myWorkBook.getSheet(sheetName);
		
		// Get iterator to all the rows in current sheet 
		Iterator<Row> rowIterator = mySheet.iterator();
		boolean firstRow = true;
		List<String> first = new ArrayList<String>();
		// Traversing over each row of XLSX file 
		while (rowIterator.hasNext()) {
			Map<String, String> map = new HashMap<String,String>(); //Create map
			Row row = rowIterator.next();
			
			// For each row, iterate through each columns 
			Iterator<Cell> cellIterator = row.cellIterator();
			int columnIndex=0;
			
			while (cellIterator.hasNext()) {
				
				
				//Cell firstRowcell = null;
			//	Row row0 = mySheet.getRow(0); //.getCell(columnIndex);
			//	Cell firstRowcell = row0.getCell(0);
			//	String firstHeaderRow = String.valueOf(firstRowcell);
			//	columnIndex++;
				
				Cell cell =  cellIterator.next();				
		        String value = dataFormatter.formatCellValue(cell);
		        
		        if(firstRow) {
					first.add(value);
				}
		        
		        map.put(first.get(columnIndex),value.trim());
				//System.out.print(value + "\t");
		        columnIndex++;
			} 
				if(!map.isEmpty()) {
					containerList.add(map);
				}
				//System.out.println(""); 
				firstRow = false;
		}
		
		//containerList.remove(0);			
	    System.out.println("containerList : " + containerList.toString());
		//System.out.println("first row = " + first.toString());
	    myWorkBook.close();
		is.close();
	}
	
	
	public static void read2() throws IOException {
		System.out.println("started");
		String filePath =  Constant.BASE_PATH + "\\TestData\\input\\Reference\\";
		String fileName = "Migration V3.0.xlsx";
		String sheetName = "Geogaraphical Areas";
		DataFormatter dataFormatter = new DataFormatter();
		
		File myFile = new File(filePath + fileName);
		FileInputStream fis = new FileInputStream(myFile);
		
		
		// Finds the workbook instance for XLSX file 
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		SXSSFWorkbook myWorkBook1 = new SXSSFWorkbook(myWorkBook);
		
		// Return first sheet from the XLSX workbook 
		SXSSFSheet mySheet = myWorkBook1.getSheet(sheetName);
		//XSSFSheet mySheet = myWorkBook.getSheet(sheetName);
		
		// Get iterator to all the rows in current sheet 
		Iterator<Row> rowIterator = mySheet.iterator();
		
		// Traversing over each row of XLSX file 
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			
			// For each row, iterate through each columns 
			Iterator<Cell> cellIterator = row.cellIterator();
			
			while (cellIterator.hasNext()) {
				Cell cell =  cellIterator.next();				
		        String value = dataFormatter.formatCellValue(cell);
				System.out.print(value + "\t");
				
			} System.out.println("");
		}
		System.out.println("ended");
	}
	
	
	
	public static void read1() throws IOException {
		
		String filePath =  Constant.BASE_PATH + "\\TestData\\input\\";
		String fileName = "Migration V3.2.xlsx";
		String sheetName = "Geogaraphical Areas";
		
		File myFile = new File(filePath + fileName);
		FileInputStream fis = new FileInputStream(myFile);
		
		// Finds the workbook instance for XLSX file 
		XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
		
		// Return first sheet from the XLSX workbook 
		XSSFSheet mySheet = myWorkBook.getSheet(sheetName);
		
		// Get iterator to all the rows in current sheet 
		Iterator<Row> rowIterator = mySheet.iterator();
		
		// Traversing over each row of XLSX file 
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			
			// For each row, iterate through each columns 
			Iterator<Cell> cellIterator = row.cellIterator();
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				switch (cell.getCellType()) { 
					case Cell.CELL_TYPE_STRING: 
						System.out.print(cell.getStringCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_NUMERIC: 
						System.out.print("N : "+cell.getNumericCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_BOOLEAN: 
						System.out.print(cell.getBooleanCellValue() + "\t");
						break;
					default:
				} 
			} System.out.println("");
		}
		
	}
	
	public static void readXLS() throws IOException {
		
		String filePath =  Constant.BASE_PATH + "\\TestData\\input\\";
		String fileName = "Migration V3.1.xlsx";
		String sheetName = "Geogaraphical Areas";

		InputStream file = new FileInputStream(new File(filePath + fileName));
		Workbook workbook = StreamingReader.builder().rowCacheSize(100) // number of rows to keep in memory
				.bufferSize(1) // index of sheet to use (defaults to 0)
				.open(file); // InputStream or File for XLSX file (required)

		//Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();
		Iterator<Row> rowIterator = workbook.getSheet(sheetName).rowIterator();
		
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();
			
			// For each row, iterate through each columns 
			Iterator<Cell> cellIterator = row.cellIterator();
			
			while (cellIterator.hasNext()) {
				//Cell cell = cellIterator.next();
				//String cellValue = dataFormatter.formatCellValue(cell);
				
				Cell cell = cellIterator.next();
				
				switch (cell.getCellType()) { 
					case Cell.CELL_TYPE_STRING: 
						System.out.print(cell.getStringCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_NUMERIC: 
						System.out.print(cell.getNumericCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_BOOLEAN: 
						System.out.print(cell.getBooleanCellValue() + "\t");
						break;
					default:
				} 
			} System.out.println("");
		}
		
	}


}
