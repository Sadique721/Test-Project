package com.savbill.inventorymanagement.modules.CustomerServiceMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMapppingPojo;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;


@Entity
@Data
@Table(name = "tbltcustomerservicemapping")
@EntityListeners(AuditableListener.class)
public class CustomerServiceMapping extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "custid", nullable = false, length = 40)
    private Integer custId;

    @Column(name = "serviceid", nullable = false, length = 40)
    private Long serviceId;
    @Column(name = "connection_no")
    private String connectionNo;
    @Column(name = "partner_id")
    private Long partner;
    @Column(name = "pop")
    private String pop;
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDelete = false;
    @Column(name = "MVNOID")
    private Integer mvnoId;
    @Column(name = "buid")
    private Long buId;
    @Column(name = "status")
    private String status;
    @Transient
    private String serviceName;
    @Column(name = "static_or_pooled_ip")
    private String staticOrPooledIP;

    public CustomerServiceMapping(CustPlanMapppingPojo planMapppingPojo) {
    }
    public CustomerServiceMapping() {
    }
}
