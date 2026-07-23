package com.savbill.revenuemanagement.core.entity.partner;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "TBLPARTNERCOMMREL")
public class PartnerCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARNTERCOMMRELID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "CUSTOMERID", nullable = false, length = 40)
    private Integer customerid;

    @Column(name = "PARTNERID", nullable = false, length = 40)
    private Integer partnerid;

    @Column(name = "COMM_TYPE", nullable = false, length = 40)
    private String commtype;

    @Column(name = "COMM_REL_VALUE", nullable = false, length = 40)
    private Double commrelval;

    @Column(name = "COMM_VALUE", nullable = false, length = 40)
    private Double commval;

    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "BILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime billdate;

    @Column(name = "PROCESS_STATUS", nullable = false, length = 40)
    private String status;

}
