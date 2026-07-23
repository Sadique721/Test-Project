package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.pojo.api.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
