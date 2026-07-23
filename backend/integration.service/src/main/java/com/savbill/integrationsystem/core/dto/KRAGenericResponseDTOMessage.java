package com.savbill.integrationsystem.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class KRAGenericResponseDTOMessage {
    private List<KRAGenericResponseDTO> ResponseDTO;
}

