package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceMappingMessage {
    private Long serviceId;
    private Integer customerId;
}
