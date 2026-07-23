//package com.savbill.integrationsystem.rms.globalException;
//
//import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
//import com.savbill.integrationsystem.core.dto.GenericDataDTO;
//import com.savbill.integrationsystem.exceptions.BadRequestException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import javax.persistence.PersistenceException;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler({ PersistenceException.class })
//    public final GenericDataDTO handleException(Exception e){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//        genericDataDTO.setResponseMessage(e.getMessage());
//        return genericDataDTO;
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ONUResponseDTO> handleBadRequestException(BadRequestException ex) {
//        ONUResponseDTO response = new ONUResponseDTO();
//        response.setStatusCode(Integer.parseInt("error"));
//        response.setMessage(ex.getMessage());
//        return ResponseEntity.badRequest().body(response);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ONUResponseDTO> handleGlobalException(Exception ex) {
//        ONUResponseDTO response = new ONUResponseDTO();
//        response.setStatusCode(Integer.parseInt("error"));
//        response.setMessage("An unexpected error occurred");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}
