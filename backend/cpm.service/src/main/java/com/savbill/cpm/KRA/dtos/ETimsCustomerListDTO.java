package com.savbill.cpm.KRA.dtos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ETimsCustomerListDTO {
    private List<ETimsCustomerDTO> eTimsCustomerListDTO;
}
