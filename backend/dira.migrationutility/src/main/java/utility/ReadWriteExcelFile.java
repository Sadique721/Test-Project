package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.monitorjbl.xlsx.StreamingReader;

import temp.UpdateSheet;

public class ReadWriteExcelFile {

	public static List<Map<String, String>> getSheet(String fileName, String sheetName, int startRow, int endRow) {
		String filePath = Constant.BASE_PATH + "\\TestData\\input\\";

		// Map<String, Map<String, String>> ContainerMap = new
		// HashMap<String,Map<String, String>>(); //Create map
		List<Map<String, String>> containerList = new ArrayList<Map<String, String>>(); // Create map

		try {

			File file = new File(filePath + fileName);
			FileInputStream fis = new FileInputStream(file);
			
//			(fileName.endsWith(".csv")
//			fileName.endsWith(".xlsx")
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			Cell cell = null;

			for (int nrow = startRow; nrow <= endRow; nrow++) {

				Map<String, String> map = new HashMap<String, String>(); // Create map

				Row row = sheet.getRow(0); // Get first row

				short minColIx = row.getFirstCellNum(); // get the first column index for a row
				short maxColIx = row.getLastCellNum(); // get the last column index for a row
				for (short colIx = minColIx; colIx < maxColIx; colIx++) { // loop from first to last index

					cell = sheet.getRow(0).getCell(colIx);
					String firstHeaderRow = String.valueOf(cell);

					cell = sheet.getRow(nrow - 1).getCell(colIx);
					String rowValue = String.valueOf(cell);

					map.put(firstHeaderRow, rowValue);

					// Cell cell = row.getCell(colIx); //get the cell
					// map.put(cell.getStringCellValue(),cell.getColumnIndex()); //add the cell
					// contents (name of column) and cell index to the map
				}

				// System.out.println("map : " + map);

				// cell = sheet.getRow(nrow).getCell(0);
				// String primaryKey = String.valueOf(cell);
				// System.out.println("primaryKey : " + primaryKey);

				// containerList.put(String.valueOf(nrow), map);
				containerList.add(map);

			}

			// System.out.println("containerList : " + containerList.toString());
			workbook.close();
			fis.close();

		} catch (FileNotFoundException e) {
			String msg = "Specifed file not found = " + filePath + fileName;
			System.out.println(msg);
			Utility.printLog("execution.log", "MAIN", msg, "");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return containerList;
	}

//	public static List<Map<String, String>> getSheetNew(String fileName, String sheetName) {
//
//		String fs = Constant.FILE_SEPERATOR;
//		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
//		List<Map<String, String>> containerList = new ArrayList<Map<String, String>>(); // Create map
//		DataFormatter dataFormatter = new DataFormatter();
//
//		InputStream is = null;
//		Workbook workbook = null;
//		try {
//
//			File file = new File(filePath + fileName);
//			// FileInputStream fis = new FileInputStream(file);
//
//			// XSSFWorkbook workbook = new XSSFWorkbook(fis);
//			// XSSFSheet sheet = workbook.getSheet(sheetName);
//
//			is = new FileInputStream(file);
//			workbook = StreamingReader.builder().rowCacheSize(1000).bufferSize(4096).open(is);
//			Sheet sheet = workbook.getSheet(sheetName);
//
//			// Get iterator to all the rows in current sheet
//			Iterator<Row> rowIterator = sheet.iterator();
//			boolean readFirstRow = true;
//			List<String> firstRow = new ArrayList<String>();
//
//			// Traversing over each row of XLSX file
//			while (rowIterator.hasNext()) {
//
//                    Map<String, String> map = new HashMap<String, String>(); // Create map
//                    Row row = rowIterator.next();
//
//                    // For each row, iterate through each columns
//                    Iterator<Cell> cellIterator = row.cellIterator();
//                    int columnIndex = 0;
//
//                    while (cellIterator.hasNext()) {
//
//                        // Cell firstRowcell = null;
//                        // firstRowcell = sheet.getRow(0).getCell(columnIndex);
//                        // String firstHeaderRow = String.valueOf(firstRowcell);
//                        // columnIndex++;
//
//                        Cell cell = cellIterator.next();
//                        String rowValue = dataFormatter.formatCellValue(cell);
//
//                        if (readFirstRow) {
//                            firstRow.add(rowValue.trim());
//                        }
//
//                        map.put(firstRow.get(columnIndex), rowValue.trim());
//                        columnIndex++;
//                    }
//
//				if (!map.isEmpty()) {
//					containerList.add(map);
//				}
//				readFirstRow = false;
//			}
//
//			containerList.remove(0);
//			// System.out.println("containerList : " + containerList.toString());
//			workbook.close();
//			is.close();
//		} catch (FileNotFoundException e) {
//			String msg = "Specifed file not found = " + filePath + fileName;
//			System.out.println(msg);
//			Utility.printLog("execution.log", "MAIN", msg, "");
//			// e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (is != null) {
//					is.close();
//				}
//				if (workbook != null) {
//					workbook.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return containerList;
//	}



    public static List<Map<String, String>> getSheetNew(String fileName, String sheetName) {

        String fs = Constant.FILE_SEPERATOR;
        String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
        List<Map<String, String>> containerList = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        InputStream is = null;
        Workbook workbook = null;
        try {

            File file = new File(filePath + fileName);
            is = new FileInputStream(file);
            workbook = StreamingReader.builder()
                    .rowCacheSize(1000)
                    .bufferSize(4096)
                    .open(is);

            Sheet sheet = workbook.getSheet(sheetName);

            Iterator<Row> rowIterator = sheet.iterator();
            boolean readFirstRow = true;
            List<String> firstRow = new ArrayList<>();

            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                Map<String, String> map = new HashMap<>();
                boolean skipRow = false;

                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    Cell cell = row.getCell(colIndex);

                    // Skip row if cell has ERROR
                    if (cell != null && cell.getCellTypeEnum() == CellType.ERROR) {
                        skipRow = true;
                        break;
                    }

                    String rowValue = cell == null ? "" : dataFormatter.formatCellValue(cell);

                    if (readFirstRow) {
                        firstRow.add(rowValue.trim());
                    } else {
                        if (colIndex < firstRow.size()) {
                            map.put(firstRow.get(colIndex), rowValue.trim());
                        }
                    }
                }

                if (!readFirstRow && !skipRow && !map.isEmpty()) {
                    containerList.add(map);
                }

                readFirstRow = false;
            }

            workbook.close();
            is.close();

        } catch (FileNotFoundException e) {
            String msg = "Specified file not found = " + filePath + fileName;
            System.out.println(msg);
            Utility.printLog("execution.log", "MAIN", msg, "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return containerList;
    }













    public synchronized void setCellValue(String filePath, String fileName, String sheetName, String rowNo,
			String colName, String cellValue) {

		String fs = Constant.FILE_SEPERATOR;
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		FileOutputStream fos = null;

		try {

			File file = new File(filePath + fs + fileName);
			fis = new FileInputStream(file);
			// ZipSecureFile.setMinInflateRatio(0.00009);

			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			Cell cell = null;

			int row = Integer.parseInt(rowNo) - 1;
			int col = getColumnIndex(sheet, colName);

			// Update the value of cell

			cell = sheet.getRow(row).getCell(col);
			
			
			// System.out.println("cell : " + cell);
			if (cell == null) {
				// System.out.println("creating a cell" );
				Row r = sheet.getRow(row);
				cell = r.getCell(col);
				cell = r.createCell(col);
			}
			cell.setCellValue(cellValue);

			fis.close();

			fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (workbook != null) {
					workbook.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private synchronized static int getColumnIndex(Sheet sheet, String columnName) {

		int columnIndex = -1;

		try {

			Map<String, Integer> map = new HashMap<String, Integer>(); // Create map
			// HSSFRow row = (HSSFRow) sheet.getRow(0); //Get first row

			Row row = sheet.getRow(0); // Get first row

			if (row == null) {
				// System.out.println("first row is null : " + row);
				return columnIndex;
			}

			// following is boilerplate from the java doc
			short minColIx = row.getFirstCellNum(); // get the first column index for a row
			// System.out.println("minColIx : " + minColIx);
			short maxColIx = row.getLastCellNum(); // get the last column index for a row
			// System.out.println("maxColIx : " + maxColIx);
			for (short colIx = minColIx; colIx < maxColIx; colIx++) { // loop from first to last index
				Cell cell = row.getCell(colIx); // get the cell
				map.put(cell.getStringCellValue(), cell.getColumnIndex()); // add the cell contents (name of column) and
																			// cell index to the map
			}
			// System.out.println("columnName : " + columnName);
			columnIndex = map.get(columnName);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnIndex;

	}

	public void updateCellValue(String fileName, String sheetName, String rowNumber, String columnName, String value) {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		rowNumber = String.valueOf(Integer.parseInt(rowNumber) + 1);
		XSSFWorkbook workbook = null;

		try {

			File file = new File(filePath + fileName);
			workbook = getWorkbook(fileName);

			updateOnlyCellValue(workbook, sheetName, rowNumber, columnName, value);
			updateOnlyCellValue(workbook, sheetName, rowNumber, "MigrationStatus", "Success");
			writeWorkbook(fileName, workbook);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Utility.waitInMilliseconds(2000);
	}

	public void setMigrationStatus(String sheetName, String row) {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		String fileName = Constant.INVENTORY_DATA_FILE;

		row = String.valueOf(Integer.parseInt(row) + 1);
		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		rw.setCellValue(filePath, fileName, sheetName, row, "MigrationStatus", "Success");
	}

	public void setMigrationStatus1(String sheetName) {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		String fileName = Constant.CUSTOMER_DATA_FILE;  //here i have change

		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		rw.setCellValue1(filePath, fileName, sheetName, "MigrationStatus", "Success");
	}

	public void setCellValue1(String filePath, String fileName, String sheetName, String colName, String cellValue) {

		String fs = Constant.FILE_SEPERATOR;
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		FileOutputStream fos = null;

		try {

			File file = new File(filePath + fs + fileName);
			fis = new FileInputStream(file);

			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			Cell cell = null;

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();

			int col = getColumnIndex(sheet, colName);
			Set<String> keys = map.keySet();
			Iterator<String> keyIter = keys.iterator();
			// System.out.println("map : " + map.toString());
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				int row = Integer.parseInt(key);
				// Update the value of cell

				cell = sheet.getRow(row).getCell(col);
				// System.out.println("cell : " + cell);
				if (cell == null) {
					// System.out.println("creating a cell" );
					Row r = sheet.getRow(row);
					cell = r.getCell(col);
					cell = r.createCell(col);
				}
				cell.setCellValue(cellValue);
				us.removeRowFromList(key);
				
			}

			fis.close();

			fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (workbook != null)
					workbook.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// For Thereding --------------------******************.......Act Threading................................//
	    public void setMultipleColumnInActiveSheet(List<Map<String, String>> dataBatch) {
	        SXSSFWorkbook wb = new SXSSFWorkbook();
	        Sheet sheet = wb.createSheet("ActCustomer Data");
	        
	        // Create headers if needed
	        Row headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Username");
	        headerRow.createCell(1).setCellValue("CPRID");

	        // Write data in a streaming way (row-by-row)
	        int rowNum = 1;  // Start writing data from row 1
	        for (Map<String, String> data : dataBatch) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(data.get("Username"));
	            row.createCell(1).setCellValue(data.get("CPRID"));
	        }

	        // Write the file to disk
	        try (FileOutputStream fileOut = new FileOutputStream("Actcustomer_data.xlsx")) { //here you can change 
	            wb.write(fileOut);
	        } catch (IOException e) {
	            System.err.println("Error writing Excel file: " + e.getMessage());
	        }

	        // Dispose of temporary files to save memory
	        wb.dispose();
	    }
	//--------------------******************.......Act Threading................................//

	
	
	
	public void setMultipleColumnInActiveSheet() {

		UpdateSheet us = new UpdateSheet();
		Map<String, String> map = us.getRowList();
		
		if (!map.isEmpty()) {
			String fs = Constant.FILE_SEPERATOR;
			String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
			String fileName = Constant.CUSTOMER_DATA_FILE;

			String sheetName = us.getActiveSheetName();
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			rw.setCellValue2(filePath, fileName, sheetName);
		}
	}
	
	
	
	//savana
	public void setMultipleColumnInActiveSheetSavana() {

		UpdateSheet us = new UpdateSheet();
		Map<String, String> map = us.getRowList();
		
		if (!map.isEmpty()) {
			String fs = Constant.FILE_SEPERATOR;
			String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
			String fileName = Constant.SAVANACUSTOMER_FILE;

			String sheetName = us.getActiveSheetName();
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			rw.setCellValue4(filePath, fileName, sheetName);
		}
	}
	
	
	public void setMultipleColumnInActiveSheetSavanaPayment() {

		UpdateSheet us = new UpdateSheet();
		Map<String, String> map = us.getRowList();
		
		if (!map.isEmpty()) {
			String fs = Constant.FILE_SEPERATOR;
			String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
			String fileName = Constant.PAYMENTFILE;

			String sheetName = us.getActiveSheetName();
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			rw.setCellValue4(filePath, fileName, sheetName);
		}
	}
		//Act SetMultipleCol
		public void setMultipleColumnInActiveSheetACT() {

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();
			
			if (!map.isEmpty()) {
				String fs = Constant.FILE_SEPERATOR;
				String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
				//Act File 
				String fileName = Constant.ACTCUSTOMER_DATA_FILE;   

				String sheetName = us.getActiveSheetName();

				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				rw.setCellValue4(filePath, fileName, sheetName);
			}
	}
		
		
		//savana
		public void setMultipleColumnInActiveSheetTumilLead() {

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();
			
			if (!map.isEmpty()) {
				String fs = Constant.FILE_SEPERATOR;
				String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
				String fileName = Constant.SALES_CRM_DATA_FILE;

				String sheetName = us.getActiveSheetName();
				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				rw.setCellValue4(filePath, fileName, sheetName);
			}
		}
//14 jan 
		  // Method to write multiple rows to the active Excel sheet
	    public void setMultipleColumnInActiveSheett(List<Map<String, String>> batchToWrite) {
	        String fs = Constant.FILE_SEPERATOR;
	        String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
	        String fileName = Constant.ACTCUSTOMER_DATA_FILE;
	        String sheetName = "MigrationCustomerWithBaseUsaegs"; // Update if needed

	        FileInputStream fis = null;
	        XSSFWorkbook workbook = null;
	        FileOutputStream fos = null;

	        try {
	            File file = new File(filePath + fs + fileName);
	            fis = new FileInputStream(file);
	            workbook = new XSSFWorkbook(fis);
	            Sheet sheet = workbook.getSheet(sheetName);
	            
	            // Get the header row to map column names to indices
	            Row headerRow = sheet.getRow(0); // Assuming the first row is the header
	            Map<String, Integer> columnIndexMap = new HashMap<>();
	            
	            // Populate the column index map
	            for (Cell headerCell : headerRow) {
	                columnIndexMap.put(headerCell.getStringCellValue(), headerCell.getColumnIndex());
	            }

	            // Start adding data from the second row onward (row 1)
	            int rowIndex = sheet.getPhysicalNumberOfRows(); // Get the next available row index

	            // Iterate through each customer in the batch and add their details to the sheet
	            for (Map<String, String> customerData : batchToWrite) {
	                Row row = sheet.createRow(rowIndex++);
	                
	                // Write each column's value based on column names
	                for (Map.Entry<String, String> entry : customerData.entrySet()) {
	                    String columnName = entry.getKey();
	                    String cellValue = entry.getValue();
	                    
	                    // Get the column index from the header row map
	                    Integer colIndex = columnIndexMap.get(columnName);
	                    
	                    if (colIndex != null) {
	                        // Create the cell if it's not present and set the value
	                        Cell cell = row.createCell(colIndex);
	                        cell.setCellValue(cellValue);
	                    }
	                }
	            }

	            // Write the updated workbook to the file
	            fos = new FileOutputStream(file);
	            workbook.write(fos);

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (fis != null) fis.close();
	                if (fos != null) fos.close();
	                if (workbook != null) workbook.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		//
		//Act SetMultipleCol
		public void setMultipleColumnInActiveSheetACTAddon() {

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();
			
			if (!map.isEmpty()) {
				String fs = Constant.FILE_SEPERATOR;
				String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
				//Act File 
				String fileName = Constant.ACTCUSTOMER_ADDON_DATA_FILE;   

				String sheetName = us.getActiveSheetName();

				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				rw.setCellValue2(filePath, fileName, sheetName);
			}
	}
		
// Tumil
		//savana
		public void setMultipleColumnInActiveSheetTumil() {

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();
			
			if (!map.isEmpty()) {
				String fs = Constant.FILE_SEPERATOR;
				String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
				String fileName = Constant.TUMIL_FILE;

				String sheetName = us.getActiveSheetName();
				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				rw.setCellValue4(filePath, fileName, sheetName);
			}
		}

    //---------------------------------------Done By Amit Prajapati------------------------------------------->

    public void setMultipleColumnInActiveSheetNew(UpdateSheet us,String fileName) {

        //UpdateSheet us = new UpdateSheet();
        Map<String, String> map = us.getRowList();
        
        if (!map.isEmpty()) {
            String fs = Constant.FILE_SEPERATOR;
            String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

            String sheetName = us.getActiveSheetName();
            ReadWriteExcelFile rw = new ReadWriteExcelFile();
            try {
				rw.setCellValue4_fast(filePath, fileName, sheetName);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

	//---------------------------------------------------------------------------------->	
	public void setCellValue2(String filePath, String fileName, String sheetName) {

		String fs = Constant.FILE_SEPERATOR;
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		FileOutputStream fos = null;

		try {

			File file = new File(filePath + fs + fileName);
			fis = new FileInputStream(file);

			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			Cell cell = null;

			UpdateSheet us = new UpdateSheet();
			Map<String, String> map = us.getRowList();
			
			Set<String> keys = map.keySet();
			Iterator<String> keyIter = keys.iterator();
			// System.out.println("map : " + map.toString());

			while (keyIter.hasNext()) {
				String key = keyIter.next();
				int row = Integer.parseInt(key);

				// Update the value of cell
				String colsAndValues = map.get(key);
				
				String colsAndValuesArray[] = colsAndValues.split("#");
				for (int i = 0; i < colsAndValuesArray.length; i++) {

					String colAndValue = colsAndValuesArray[i];

					String temp[] = colAndValue.split(":");
					String colName = temp[0];
					String cellValue = temp[1];
					int col = getColumnIndex(sheet, colName);

					cell = sheet.getRow(row).getCell(col);
					// System.out.println("cell : " + cell);
					if (cell == null) {
						// System.out.println("creating a cell" );
						Row r = sheet.getRow(row);
						cell = r.getCell(col);
						cell = r.createCell(col);
					}
					cell.setCellValue(cellValue);
				}
			us.removeRowFromList(key);
				
			}

			fis.close();

			fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (workbook != null)
					workbook.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
//new method -->after 13 jan
	public void setCellValue4(String filePath, String fileName, String sheetName) {

	    String fs = Constant.FILE_SEPERATOR;
	    FileInputStream fis = null;
	    XSSFWorkbook workbook = null;
	    FileOutputStream fos = null;

	    try {
	        // Open the Excel file
	        File file = new File(filePath + fs + fileName);
	        fis = new FileInputStream(file);
	        workbook = new XSSFWorkbook(fis);
	        XSSFSheet sheet = workbook.getSheet(sheetName);

	        UpdateSheet us = new UpdateSheet();
	        Map<String, String> map = us.getRowList();
	        
	        if (map.isEmpty()) {
	            System.out.println("No rows to update.");
	            return; // Exit if there's nothing to update
	        }

	        // Iterate through the map (row index -> column and value pairs)
	        for (Map.Entry<String, String> entry : map.entrySet()) {
	            String key = entry.getKey();  // Row index
	            int rowIndex = Integer.parseInt(key);
	            String colsAndValues = entry.getValue();  // Column-Value pairs

	            String[] colsAndValuesArray = colsAndValues.split("#");

	            for (String colAndValue : colsAndValuesArray) {
	                String[] temp = colAndValue.split("::");
	                String colName = temp[0];  // Column name (e.g., "cprid")
	                String cellValue = temp[1];  // The value to set

	                // Get the column index based on column name
	                int colIndex = getColumnIndex(sheet, colName);
	                
	                // Retrieve or create row
	                Row row = sheet.getRow(rowIndex);
	                if (row == null) {
	                    row = sheet.createRow(rowIndex);  // Create row if it doesn't exist
	                }

	                // Retrieve or create the cell
	                Cell cell = row.getCell(colIndex);
	                if (cell == null) {
	                    cell = row.createCell(colIndex);  // Create cell if it doesn't exist
	                }

	                // Set the cell value
	                cell.setCellValue(cellValue);
	            }

	            // Remove the updated row from the list (if needed)
	            us.removeRowFromList(key);  // Ensure that this does not affect the map while iterating
	        }

	        // Close the file input stream
	        fis.close();

	        // Save the changes back to the file
	        fos = new FileOutputStream(file);
	        workbook.write(fos);
	        workbook.close();
	        fos.close();

	        System.out.println("Excel file updated successfully.");

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        // Ensure all resources are closed
	        try {
	            if (fis != null) fis.close();
	            if (workbook != null) workbook.close();
	            if (fos != null) fos.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	//
	public void isExcelFileOpen(String fileName) throws Exception {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;

		File file = new File(filePath + fileName);
		boolean isFileUnlocked = false;
		boolean isFileNotFound = true;

		try {
			Files.move(Paths.get(file.getPath()), Paths.get(file.getPath()), StandardCopyOption.ATOMIC_MOVE);
			isFileUnlocked = true;
		} catch (NoSuchFileException fileNotFound) {
			fileNotFound.printStackTrace();
			isFileNotFound = false;
		} catch (Exception e) {
			e.printStackTrace();
			isFileUnlocked = false;
		}
		
		if (!isFileNotFound) {
			String fileNameWithPath = filePath + fileName;
			String message = "File is not found";
			ProductUtility.stopExecutionNew("execution.log", "ReadWriteExcel", message, fileNameWithPath);
		}

		if (!isFileUnlocked) {
			String fileNameWithPath = filePath + fileName;
			String message = "File is not closed";
			ProductUtility.stopExecutionNew("execution.log", "ReadWriteExcel", message, fileNameWithPath);
		}
	}

	public void updateOnlyCellValue(XSSFWorkbook workbook, String sheetName, String rowNo, String colName,
			String cellValue) {

		try {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			Cell cell = null;

			// rowNumber = String.valueOf(Integer.parseInt(rowNo) + 1);
			int row = Integer.parseInt(rowNo);
			int col = getColumnIndex(sheet, colName);

			// Update the value of cell

			cell = sheet.getRow(row).getCell(col);
			// System.out.println("cell : " + cell);
			if (cell == null) {
				// System.out.println("creating a cell" );
				Row r = sheet.getRow(row);
				cell = r.getCell(col);
				cell = r.createCell(col);
			}
			cell.setCellValue(cellValue);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public XSSFWorkbook getWorkbook(String fileName) {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		File file = new File(filePath + fileName);

		FileInputStream fis = null;
		XSSFWorkbook workbook = null;

		try {

			fis = new FileInputStream(file);
			workbook = new XSSFWorkbook(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return workbook;
	}

	public void writeWorkbook(String fileName, XSSFWorkbook workbook) {

		String fs = Constant.FILE_SEPERATOR;
		String filePath = Constant.BASE_PATH + fs + "TestData" + fs + "input" + fs;
		File file = new File(filePath + fileName);

		FileOutputStream fos = null;

		try {

			fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	//===============================================
	
	public void setCellValue4_fast(String filePath, String fileName, String sheetName) throws Exception {

	    String fullPath = filePath + File.separator + fileName;

	    // 1. FAST streaming reader
	    Workbook readerWb = StreamingReader.builder()
	            .rowCacheSize(200)
	            .bufferSize(8192)
	            .open(new File(fullPath));

	    Sheet readerSheet = readerWb.getSheet(sheetName);
	    if (readerSheet == null) {
	        System.out.println("Sheet not found.");
	        return;
	    }

	    // 2. Row update map
	    UpdateSheet us = new UpdateSheet();
	    Map<String, String> updateMap = us.getRowList();

	    // 3. New workbook for writing (FAST)
	    SXSSFWorkbook writeWb = new SXSSFWorkbook(200);
	    Sheet writeSheet = writeWb.createSheet(sheetName);

	    Iterator<Row> it = readerSheet.iterator();
	    if (!it.hasNext()) {
	        System.out.println("Sheet empty.");
	        return;
	    }

	    // =============================
	    // 4. Copy header row (NO STYLE)
	    // =============================
	    Row header = it.next();
	    Row newHeader = writeSheet.createRow(0);

	    for (Cell srcCell : header) {
	        int col = srcCell.getColumnIndex();
	        Cell destCell = newHeader.createCell(col);
	        destCell.setCellValue(srcCell.getStringCellValue());

	        // fixed column width
	        writeSheet.setColumnWidth(col, 4000);
	    }

	    // create column name → index map
	    Map<String, Integer> colIndexMap = getColumnNameIndexMapFromHeader(header);

	    // =============================
	    // 5. Copy remaining rows
	    // =============================
	    int writeRowNum = 1;

	    while (it.hasNext()) {
	        Row srcRow = it.next();
	        Row destRow = writeSheet.createRow(writeRowNum);

	        // copy row normally
	        copyRowSimple(srcRow, destRow);

	        // apply updates if any
	        String rowKey = String.valueOf(srcRow.getRowNum());
	        if (updateMap.containsKey(rowKey)) {

	            String[] updates = updateMap.get(rowKey).split("#");
	            for (String pair : updates) {
	                String[] tmp = pair.split("::");

	                String colName = tmp[0];
	                String newValue = tmp[1];

	                Integer colIndex = colIndexMap.get(colName);
	                if (colIndex == null) continue;

	                Cell c = destRow.getCell(colIndex);
	                if (c == null) c = destRow.createCell(colIndex);

	                c.setCellValue(newValue);
	            }
	        }

	        writeRowNum++;
	    }

	    // =============================
	    // 6. Save output
	    // =============================
	    String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
	    String outputFile = filePath + File.separator + "output" + File.separator + sheetName +"_output_" + timestamp + ".xlsx";

	   // String outputFile = filePath + File.separator + "output" + File.separator + "output.xlsx";
	    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
	        writeWb.write(fos);
	    }

	    writeWb.dispose();
	    readerWb.close();

	    System.out.println("FAST Excel update completed → " + outputFile);
	}


	/* =============================
	   SIMPLE ROW COPY (NO STYLE)
	   ============================= */
	private void copyRowSimple(Row src, Row dest) {
	    for (Cell srcCell : src) {

	        Cell destCell = dest.createCell(srcCell.getColumnIndex());

	        switch (srcCell.getCellTypeEnum()) {

	            case STRING:
	                destCell.setCellValue(srcCell.getStringCellValue());
	                break;

	            case NUMERIC:
	                destCell.setCellValue(srcCell.getNumericCellValue());
	                break;

	            case BOOLEAN:
	                destCell.setCellValue(srcCell.getBooleanCellValue());
	                break;

	            case FORMULA:
	                destCell.setCellFormula(srcCell.getCellFormula());
	                break;

	            case BLANK:
	                destCell.setCellType(Cell.CELL_TYPE_BLANK);
	                break;

	            case ERROR:
	                destCell.setCellErrorValue(srcCell.getErrorCellValue());
	                break;
	        }
	    }
	}


	/* =============================
	   GET COLUMN NAME → INDEX MAP
	   ============================= */
	private Map<String, Integer> getColumnNameIndexMapFromHeader(Row header) {
	    Map<String, Integer> map = new HashMap<>();
	    for (Cell c : header) {
	        map.put(c.getStringCellValue().trim(), c.getColumnIndex());
	    }
	    return map;
	}
	
	
	
	//===============================================	
	
}
