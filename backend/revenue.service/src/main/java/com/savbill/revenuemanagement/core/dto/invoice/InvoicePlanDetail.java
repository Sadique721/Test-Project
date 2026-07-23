package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePlanDetail {

    private Integer planId;

    private String planName;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    private Double offerPrice;

    private String serviceName;

    private String connectionNo;

    private List<InvoiceChargeDetail> invoiceChargeDetails;
}
