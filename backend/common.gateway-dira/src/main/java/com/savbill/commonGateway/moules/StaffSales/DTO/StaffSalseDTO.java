package com.savbill.commonGateway.moules.StaffSales.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffSalseDTO {

    private Long customerCount;
    private String name;
    private String cretedByName;

}
