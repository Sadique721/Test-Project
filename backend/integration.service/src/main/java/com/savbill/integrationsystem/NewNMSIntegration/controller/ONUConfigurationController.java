//package com.savbill.integrationsystem.NewNMSIntegration.controller;
//
//import com.savbill.integrationsystem.NewNMSIntegration.dto.DynamicRequestDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.dto.WiFiConfigDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.service.ONUService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/onu")
//public class ONUConfigurationController {
//
//    @Autowired
//    private ONUService onuService;
//
//    @PostMapping("/configure")
//    public ResponseEntity<ONUResponseDTO> configureCustomer(@RequestBody DynamicRequestDTO request) {
//
//        ONUResponseDTO onuResponse = onuService.addONU(request, null);
//
//        if (!onuResponse.isSuccess()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(onuResponse);
//        }
//
//        ONUResponseDTO wanResponse = onuService.configureWAN(request);
//
//        if (!wanResponse.isSuccess()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(wanResponse);
//        }
//
//        return ResponseEntity.ok(wanResponse);
//    }
//
//    @PostMapping("/wifi")
//    public ResponseEntity<ONUResponseDTO> configureWiFi(@RequestBody WiFiConfigDTO request) {
//        ONUResponseDTO response = onuService.configureWiFi(request);
//        return response.isSuccess() ?
//                ResponseEntity.ok(response) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    @PutMapping("/pppoe")
//    public ResponseEntity<ONUResponseDTO> updatePPPoE(@RequestBody DynamicRequestDTO request) {
//        ONUResponseDTO response = onuService.updatePPPoECredentials(request);
//        return response.isSuccess() ?
//                ResponseEntity.ok(response) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//}
//
//
//
//
