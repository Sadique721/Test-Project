package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.ticketmanagement.core.dto.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
