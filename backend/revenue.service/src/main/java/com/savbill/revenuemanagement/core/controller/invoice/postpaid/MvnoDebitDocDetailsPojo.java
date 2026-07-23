package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MvnoDebitDocDetailsPojo {

    Integer debitdocumentid;
    String debitdocumentnumber;
    Integer subscriberid;
    LocalDateTime startdate;
    LocalDateTime enddate;
    Double totalamount;
    String username;

    public MvnoDebitDocDetailsPojo(Integer debitdocumentid, String debitdocumentnumber, Integer subscriberid,
                                   LocalDateTime startdate, LocalDateTime enddate, Double totalamount, String username) {
        this.debitdocumentid = debitdocumentid;
        this.debitdocumentnumber = debitdocumentnumber;
        this.subscriberid = subscriberid;
        this.startdate = startdate;
        this.enddate = enddate;
        this.totalamount = totalamount;
        this.username = username;
    }
}
