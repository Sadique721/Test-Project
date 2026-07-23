package com.savbill.radius.controller;


import com.savbill.radius.services.ProcessCDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/SavbillRadius")
public class ProcessCDRController {

    @Autowired
    private ProcessCDRService processCDRService;



    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() {
        try {
            byte[] reportBytes = processCDRService.generateProcessCdrReport();
            String filename = "process_cdr_report.xlsx";


              // Convert byte array to Resource using ByteArrayResource
                        Resource reportResource = new ByteArrayResource(reportBytes);

                if (reportResource.exists() || reportResource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                            .body(reportResource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
