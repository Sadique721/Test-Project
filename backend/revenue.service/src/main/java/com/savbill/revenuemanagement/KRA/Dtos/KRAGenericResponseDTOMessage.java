package com.savbill.revenuemanagement.KRA.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class KRAGenericResponseDTOMessage {
    private List<KRAGenericResponseDTO> ResponseDTO;
}