package com.savbill.cpm.KRA.dtos;



import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.CustPlanMappingMessage;
import lombok.Data;

import java.util.List;

@Data
public class KRAGenericResponseDTOMessage {
    private List<KRAGenericResponseDTO> ResponseDTO;
}
