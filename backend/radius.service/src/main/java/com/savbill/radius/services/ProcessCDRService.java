package com.savbill.radius.services;

import com.savbill.radius.entity.ProcessCDR;
import com.savbill.radius.repository.ProcessCDRRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ProcessCDRService {

    @Autowired
    private ProcessCDRRepository processCDRRepository;

    public byte[] generateProcessCdrReport() throws IOException {
        // Fetch all entries from the table
        List<ProcessCDR> processCdrList = processCDRRepository.findAll();

        // Create a new workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Process CDR Report");

        // Create headers for the table
        int rowIndex = 0;
        XSSFRow headerRow = sheet.createRow(rowIndex++);

        headerRow.createCell(0).setCellValue("Id");
        headerRow.createCell(1).setCellValue("USERNAME");
        headerRow.createCell(2).setCellValue("SESSIONID");
        headerRow.createCell(3).setCellValue("FRAMEDIPADDRESS");
        headerRow.createCell(4).setCellValue("SESSIONAUTHRULE");
        headerRow.createCell(5).setCellValue("NASIPADDRESS");
        headerRow.createCell(6).setCellValue("REQUESTTYPE");
        headerRow.createCell(7).setCellValue("MACADDRESS");
        headerRow.createCell(8).setCellValue("NASPORTID");
        headerRow.createCell(9).setCellValue("FRAMED_IPV6_ADDRESS");
        headerRow.createCell(10).setCellValue("FRAMED_INTERFACE_ID");
        headerRow.createCell(11).setCellValue("DELEGATED_IPV6_PREFIX");
        headerRow.createCell(12).setCellValue("AGGREGATEKEY");
        headerRow.createCell(13).setCellValue("UPLOAD");
        headerRow.createCell(14).setCellValue("DOWNLOAD");
        headerRow.createCell(15).setCellValue("TOTAL");
        headerRow.createCell(16).setCellValue("CDRTIME");
        headerRow.createCell(17).setCellValue("ENDTIME");
        headerRow.createCell(18).setCellValue("STARTTIME");

        // Fill data rows
        for (ProcessCDR processCDR : processCdrList) {
            XSSFRow dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(processCDR.getId());
            dataRow.createCell(1).setCellValue(processCDR.getUSERNAME());
            dataRow.createCell(2).setCellValue(processCDR.getSESSIONID());
            dataRow.createCell(3).setCellValue(processCDR.getFRAMEDIPADDRESS());
            dataRow.createCell(4).setCellValue(processCDR.getSESSIONAUTHRULE());
            dataRow.createCell(5).setCellValue(processCDR.getNASIPADDRESS());
            dataRow.createCell(6).setCellValue(processCDR.getREQUESTTYPE());
            dataRow.createCell(7).setCellValue(processCDR.getMACADDRESS());
            dataRow.createCell(8).setCellValue(processCDR.getNASPORTID());
            dataRow.createCell(9).setCellValue(processCDR.getFRAMED_IPV6_ADDRESS());
            dataRow.createCell(10).setCellValue(processCDR.getFRAMED_INTERFACE_ID());
            dataRow.createCell(11).setCellValue(processCDR.getDELEGATED_IPV6_PREFIX());
            dataRow.createCell(12).setCellValue(processCDR.getAGGREGATEKEY());
            dataRow.createCell(13).setCellValue(processCDR.getUPLOAD());
            dataRow.createCell(14).setCellValue(processCDR.getDOWNLOAD());
            dataRow.createCell(15).setCellValue(processCDR.getTOTAL());
            dataRow.createCell(16).setCellValue(processCDR.getCDRTIME());
            dataRow.createCell(17).setCellValue(processCDR.getENDTIME());
            dataRow.createCell(18).setCellValue(processCDR.getSTARTTIME());
        }

        // Adjust column widths (optional)
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Create a byte array to store the Excel file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }
}
