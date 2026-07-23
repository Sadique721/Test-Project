package com.savbill.revenuemanagement.core.entity.partner;

import lombok.Data;

import java.util.List;

@Data
public class PartnerLedgerInfoPojo {
    private Double openingAmount;
    private List<PartnerLedgerDetailsDTO> debitCreditDetail;
    private List<PartnerLedgerDetailsPlanLevelDTO> partnerLedgerDetailsPlanLevelDTO;
    private List<PartnerLedgerDetailsServiceLevelDTO> partnerLedgerDetailsServiceLevelDTO;
    private Double closingBalance;
}
