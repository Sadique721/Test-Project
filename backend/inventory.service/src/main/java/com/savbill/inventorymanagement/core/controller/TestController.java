package com.savbill.inventorymanagement.core.controller;

import com.savbill.inventorymanagement.core.constants.Constants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "Test Management", description = "REST APIs related to Test!!!!", tags = "Test")
@RestController
@RequestMapping("/api/v1/SavbillInventoryManagement")
public class TestController {



    @Autowired
    private APIResponseController apiResponseController;

    @GetMapping("/test")
//    @PreAuthorize("validatePermission(\"" + "\",\"" + "\")")
    public ResponseEntity<Map<String, Object>> findAcctCdrByUserName() {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("response", "Test success");
            return apiResponseController.apiResponse(200, response);

        } catch (Exception e) {

            Integer responseCode = Constants.FAIL;
            response.put("error", e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);

        }
    }
}
