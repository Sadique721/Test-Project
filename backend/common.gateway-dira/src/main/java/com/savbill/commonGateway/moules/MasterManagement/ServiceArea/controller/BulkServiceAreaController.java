package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.controller;


import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.BulkDownloadServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.BulkUploadServiceArea;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BULK_DOWNLOAD)
@Slf4j
public class BulkServiceAreaController {

    @Autowired
    private BulkDownloadServiceArea bulkDownloadServiceArea;

    @Autowired
    private BulkUploadServiceArea bulkUploadServiceArea;

    @GetMapping("/download")
    public ResponseEntity<byte[]> generateServiceAreaReport() {
        try {
            Integer mvnoId = bulkDownloadServiceArea.getMvnoIdFromCurrentStaff();
            // Generate the Excel workbook
            Workbook excelReport = bulkDownloadServiceArea.createExcelReport(mvnoId);

            // Write workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            excelReport.write(outputStream);
            excelReport.close();

            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "ServiceAreaReport.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Return file as response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadExcel(@RequestPart(value = "file", required = true) MultipartFile file) throws Throwable {
        try {
            Integer mvnoId = bulkUploadServiceArea.getMvnoIdFromCurrentStaff();
            Integer loggedInUserId = bulkUploadServiceArea.getLoggedInUserId();
            String loggedInUserName = null;
            if (loggedInUserId != -1) {
                loggedInUserName = bulkUploadServiceArea.getLoggedInUserName(loggedInUserId);
            }
            if (file.isEmpty()) {
                log.error("Error: File is empty. Please provide a non-empty file.");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("File is empty. Please provide a non-empty file.");
            }
            String message = bulkUploadServiceArea.uploadBulkData(file, mvnoId, loggedInUserId, loggedInUserName);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        catch (CustomValidationException ex){
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(ex.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}


