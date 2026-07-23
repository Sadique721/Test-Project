package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

@Data
public class ApplyChargeResponseDTO {
    private CustomersBasicDetailsPojo customersBasicDetails;
    private ApplyChargeRequestDTO basicChargeDetails;
}
