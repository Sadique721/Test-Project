//package com.savbill.integrationsystem.NewNMSIntegration.controller;
//
//
//import com.savbill.integrationsystem.NewNMSIntegration.dto.LoginRequestDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
//import com.savbill.integrationsystem.NewNMSIntegration.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<ONUResponseDTO> login(@RequestBody LoginRequestDTO request) {
//        return ResponseEntity.ok(authService.login(request));
//    }
//}
