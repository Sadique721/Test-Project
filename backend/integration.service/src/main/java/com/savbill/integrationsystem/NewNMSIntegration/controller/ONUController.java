//package com.savbill.integrationsystem.NewNMSIntegration.controller;
//
//
//
//import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
//import com.savbill.integrationsystem.NewNMSIntegration.dto.DynamicRequestDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.service.ONUService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1")
//@RequiredArgsConstructor
//public class ONUController {
//    private final ONUService onuService;
//
//
//
//    @PostMapping("/onu/add")
//    public ResponseEntity<ONUResponseDTO> addONU(@RequestBody DynamicRequestDTO request) {
//        request.setApiName(NMSIntegrationConstant.API_CONSTANT.ADD_ONU);
//        return ResponseEntity.ok(onuService.addONU(request, null));
//    }
//
//    @DeleteMapping("/onu/delete/{id}")
//    public ResponseEntity<ONUResponseDTO> deleteONU(@RequestBody DynamicRequestDTO requestDTO) {
//        requestDTO.setApiName(NMSIntegrationConstant.API_CONSTANT.DELETE_ONU); // Set the API name in the requestDTO.
//       return ResponseEntity.ok(onuService.deleteONU(requestDTO, null));
//    }
//
////    @PostMapping("/onu/details")
////    public ResponseEntity<ONUResponseDTO> getONUDetails(@RequestBody DynamicRequestDTO request) {
////        request.setApiName(NMSIntegrationConstant.API_CONSTANT.GET_ONU_DETAILS);
////        return ResponseEntity.ok(onuService.getONUDetails(request, null));
////    }
////
////    @PostMapping("/onu/update")
////    public ResponseEntity<ONUResponseDTO> updateONU(@RequestBody DynamicRequestDTO request) {
////        request.setApiName(NMSIntegrationConstant.API_CONSTANT.UPDATE_ONU);
////        return ResponseEntity.ok(onuService.updateONU(request, null));
////    }
//
//    @PostMapping("/handshake")
//    public ResponseEntity<ONUResponseDTO> handshake() {
//        return ResponseEntity.ok(onuService.handshake(null));
//    }
//}