package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.taskmanagement.core.dto.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
