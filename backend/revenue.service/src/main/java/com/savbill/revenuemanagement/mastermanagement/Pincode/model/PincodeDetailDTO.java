package com.savbill.revenuemanagement.mastermanagement.Pincode.model;



import com.savbill.revenuemanagement.core.dto.common.GenericRequestDTO;
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
