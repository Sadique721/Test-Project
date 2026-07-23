package com.savbill.revenuemanagement.core.dto.invoice;


import com.savbill.revenuemanagement.core.dto.customer.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlRootElement(name = "invoice")
@XmlType(propOrder = {"docnumber", "customer", "totalamount", "startdate", "endate", "paymentStatus", "adjustedAmount",
        "totalCustomerDiscount", "custRefName", "billableToName", "debitDocumentTAXRels"})
public class DebitDocumentDto {

    private String docnumber;

    private CustomerDto customer;

    private Double totalamount;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    private String paymentStatus;

    private Double adjustedAmount;

    private Double totalCustomerDiscount;

    private String custRefName;

    private String billableToName;

    private List<DebitDocumentTAXRelDto> debitDocumentTAXRels;
}
