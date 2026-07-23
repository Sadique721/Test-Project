package com.savbill.cpm.core.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class CreditDocumentProjectionDTO {
    private Integer id;
    private String paymode;
    private String reciptNo;
    private Double amount;
    private LocalDate paymentdate;
    private String createdByName;
    private String status;
    private LocalDateTime   createdate;

    public CreditDocumentProjectionDTO(Integer id, String paymode, String reciptNo, Double amount,
                                       LocalDate paymentdate, String createdByName, String status,
                                       LocalDateTime  createdate) {
        this.id = id;
        this.paymode = paymode;
        this.reciptNo = reciptNo;
        this.amount = amount;
        this.paymentdate = paymentdate;
        this.createdByName = createdByName;
        this.status = status;
        this.createdate = createdate;
    }

    // ✅ Getters
    public Integer getId() { return id; }
    public String getPaymode() { return paymode; }
    public String getReciptNo() { return reciptNo; }
    public Double getAmount() { return amount; }
    public LocalDate getPaymentdate() { return paymentdate; }
    public String getCreatedByName() { return createdByName; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedate() { return createdate; }

    // ✅ Setters (optional if you need them)
    public void setId(Integer id) { this.id = id; }
    public void setPaymode(String paymode) { this.paymode = paymode; }
    public void setReciptNo(String reciptNo) { this.reciptNo = reciptNo; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setPaymentdate(LocalDate paymentdate) { this.paymentdate = paymentdate; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedate(LocalDateTime createdate) { this.createdate = createdate; }
}
