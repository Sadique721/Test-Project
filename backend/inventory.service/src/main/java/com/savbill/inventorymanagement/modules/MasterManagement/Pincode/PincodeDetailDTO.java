package com.savbill.inventorymanagement.modules.MasterManagement.Pincode;

import com.savbill.inventorymanagement.core.dto.GenericRequestDTO;
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
