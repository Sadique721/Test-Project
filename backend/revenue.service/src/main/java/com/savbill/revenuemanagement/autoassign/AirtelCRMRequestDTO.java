package com.savbill.revenuemanagement.autoassign;

import com.savbill.revenuemanagement.core.dto.customer.CustPayDTOMessage;
import lombok.Data;

@Data
public class AirtelCRMRequestDTO {
    private AirtelAppToCRMDTO airtelAppToCRMDTO;

    private CustPayDTOMessage custPayDTOMessage;
}
