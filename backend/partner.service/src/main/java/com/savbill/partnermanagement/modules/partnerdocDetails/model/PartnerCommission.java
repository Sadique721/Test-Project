//package com.savbill.partnermanagement.modules.partnerdocDetails.model;
//
//import lombok.Data;
//import lombok.ToString;
//import org.hibernate.annotations.CreationTimestamp;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@ToString
//@Table(name = "TBLPARTNERCOMMREL")
//public class PartnerCommission {
//
//
//	/*
//create table TBLPARTNERCOMMREL
//(
//	PARNTERCOMMRELID SERIAL PRIMARY KEY,
//	CUSTOMERID BIGINT UNSIGNED,
//	PARTNERID BIGINT UNSIGNED,
//	COMM_TYPE VARCHAR(100),
//	COMM_REL_VALUE NUMERIC(2),
//	COMM_VALUE NUMERIC(20,4),
//	CREATEDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
//	BILLDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
//	PROCESS_STATUS VARCHAR(50),
//	FOREIGN KEY (PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
//	FOREIGN KEY (CUSTOMERID) REFERENCES TBLCUSTOMERS(CUSTID)
//);
//	 */
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "PARNTERCOMMRELID", nullable = false, length = 40)
//    private Integer id;
//
//    @Column(name = "CUSTOMERID", nullable = false, length = 40)
//    private Integer customerid;
//
//    @Column(name = "PARTNERID", nullable = false, length = 40)
//    private Integer partnerid;
//
//    @Column(name = "COMM_TYPE", nullable = false, length = 40)
//    private String commtype;
//
//    @Column(name = "COMM_REL_VALUE", nullable = false, length = 40)
//    private Double commrelval;
//
//    @Column(name = "COMM_VALUE", nullable = false, length = 40)
//    private Double commval;
//
//    @CreationTimestamp
//    @Column(name = "CREATEDATE", nullable = false, updatable = false)
//    private LocalDateTime createdate;
//
//    @Column(name = "BILLDATE", nullable = false, length = 40)
//    @DateTimeFormat(pattern = "dd-MM-yyyy")
//    private LocalDateTime billdate;
//
//    @Column(name = "PROCESS_STATUS", nullable = false, length = 40)
//    private String status;
//
//}
