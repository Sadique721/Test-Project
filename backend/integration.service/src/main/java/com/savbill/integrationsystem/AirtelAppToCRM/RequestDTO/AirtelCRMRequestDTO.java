package com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO;

import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import lombok.Data;

@Data
public class AirtelCRMRequestDTO {
    private AirtelAppToCRMDTO airtelAppToCRMDTO;

    private CustPayDTOMessage custPayDTOMessage;
}
