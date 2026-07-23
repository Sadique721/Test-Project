package com.savbill.revenuemanagement.rabbitmq.messages;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class PlanUpdateCafApprovalMessage {
    private Integer cprId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
}