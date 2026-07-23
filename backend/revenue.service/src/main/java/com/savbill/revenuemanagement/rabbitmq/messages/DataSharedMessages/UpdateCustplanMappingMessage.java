package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
