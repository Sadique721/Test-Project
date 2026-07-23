package com.savbill.cpm.modules.Pincode.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.core.dto.GenericRequestDTO;

@Data
public class PincodeDetailDTO {
    private GenericRequestDTO state;
    private GenericRequestDTO city;
    private GenericRequestDTO country;
    private GenericRequestDTO pincode;
    private List<GenericRequestDTO> areaList;

}
