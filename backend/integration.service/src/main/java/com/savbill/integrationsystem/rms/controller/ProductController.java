package com.savbill.integrationsystem.rms.controller;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.rms.model.ProductRmsDto;
import com.savbill.integrationsystem.rms.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/SavbillIntegrationSystem/RMSSystem")
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping(value = "/ProductCreate")
    public GenericDataDTO saveProductFromRms(@RequestBody @Valid ProductRmsDto productDto){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            productService.saveProductFromRms(productDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success to Save Product form RMS");
            genericDataDTO.setData(productDto);
            return genericDataDTO;
        }catch (Exception e){
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Error to Save Product form RMS");
            genericDataDTO.setData(productDto);
            return genericDataDTO;
        }
    }

}
