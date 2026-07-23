package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.pojo.api.CustPlanMapppingDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganizationInvoiceRejectMesssage {
    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
