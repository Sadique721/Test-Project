package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganizationInvoiceRejectMesssage {
    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
