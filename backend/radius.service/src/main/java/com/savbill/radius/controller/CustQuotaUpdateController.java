package com.savbill.radius.controller;

import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.utils.RadiusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/SavbillRadius")
public class CustQuotaUpdateController {


    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    private APIResponseController aPIResponseController;
    private static final Logger log = LoggerFactory.getLogger(CustQuotaUpdateController.class);


    @GetMapping("/updateCustQuotaDetails")
    public ResponseEntity<Map<String, Object>> updateCustQuotaDetails(@RequestParam(name = "custId", required = false) Integer custId, @RequestParam(name = "cprId", required = false) Long cprId, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        try{
            int responseCode = RadiusConstants.SUCCESS;
            Boolean isCustQuotaUpdated = customerService.updateCustQuotaDetails(custId, cprId);
            map.put("response", isCustQuotaUpdated);
            return aPIResponseController.apiResponse(responseCode, map);
        }catch (Exception e){
            int responseCode = RadiusConstants.FAIL;
            map.put("response", false);
            log.error("something went wrong while updating customer quota "+ e.getMessage());
            return aPIResponseController.apiResponse(responseCode, map);

        }
    }
}
