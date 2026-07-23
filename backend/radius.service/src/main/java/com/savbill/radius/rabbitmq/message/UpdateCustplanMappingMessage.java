package com.savbill.radius.rabbitmq.message;

import com.savbill.radius.dto.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
