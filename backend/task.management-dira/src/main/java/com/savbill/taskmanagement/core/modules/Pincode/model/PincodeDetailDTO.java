package com.savbill.taskmanagement.core.modules.Pincode.model;


import com.savbill.taskmanagement.core.dto.GenericRequestDTO;
import lombok.Data;

import java.util.List;

@Data
public class PincodeDetailDTO {
    private GenericRequestDTO state;
    private GenericRequestDTO city;
    private GenericRequestDTO country;
    private GenericRequestDTO pincode;
    private List<GenericRequestDTO> areaList;

}
