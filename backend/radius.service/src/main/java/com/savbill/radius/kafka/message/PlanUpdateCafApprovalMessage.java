package com.savbill.radius.kafka.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class PlanUpdateCafApprovalMessage {

    private Integer cprId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime expiryDate;
}
