package com.savbill.cpm.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanUpdateCafApprovalMessage {

    private Integer cprId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime expiryDate;

}
