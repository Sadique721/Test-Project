package com.savbill.integrationsystem.rms.controller;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.rms.model.InwardRmsDto;
import com.savbill.integrationsystem.rms.service.InwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/SavbillIntegrationSystem/RMSSystem")
@Slf4j
public class InwardController {

    @Autowired
    InwardService inwardService;

    @PostMapping(value = "/Inward")
    public GenericDataDTO saveInwardFromRms(@RequestBody @Valid InwardRmsDto inwardRmsDto){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            inwardService.saveInwardFromRms(inwardRmsDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success to Save Product form RMS");
            genericDataDTO.setData(inwardRmsDto);
            return genericDataDTO;
        }catch (Exception e){
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Error to Save Product form RMS");
            genericDataDTO.setData(inwardRmsDto);
            return genericDataDTO;
        }
    }
}
