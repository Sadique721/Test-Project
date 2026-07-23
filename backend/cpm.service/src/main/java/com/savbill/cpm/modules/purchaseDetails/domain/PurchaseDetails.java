package com.savbill.cpm.modules.purchaseDetails.domain;

import com.savbill.cpm.core.data.IBaseData;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.savbill.cpm.modules.placeOrder.domain.Order;
import com.savbill.cpm.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_purchase_details")
@EntityListeners(AuditableListener.class)
public class PurchaseDetails extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchaseid")
    private Long id;
    @OneToOne
    @JoinColumn(name = "orderid")
    private Order order;
    @OneToOne
    @JoinColumn(name = "custid")
    private Customers customer;
    @OneToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;
    @OneToOne
    @JoinColumn(name = "pgid")
    private PaymentGateWay paymentGateWay;
    private Double amount = 0.0;
    private String paymentstatus;
    private String transid;
    private String pgResStatus;
    private String pgtransid;
    private String purchaseStatus;
    private LocalDateTime purchasedate;
    private LocalDateTime transResDate;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
