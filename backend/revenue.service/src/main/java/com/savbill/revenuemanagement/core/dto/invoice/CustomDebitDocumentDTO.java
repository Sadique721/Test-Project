package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CustomDebitDocumentDTO {
    private Integer id;
    private Double totalAmount;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;
    private String custName;
    private String docnumber;
    public CustomDebitDocumentDTO(Integer id, Double totalAmount, String custName, LocalDateTime billdate, String docnumber) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.custName = custName;
        this.billdate = billdate;
        this.docnumber = docnumber;
    }
}
