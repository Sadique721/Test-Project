package com.savbill.ticketmanagement.core.modules.PlanService.domain;

import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
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
    public CustomerServiceMapping() {
    }

    public CustomerServiceMapping(CustomerServiceMappingRevenue customerServiceMapping) {
        this.id =customerServiceMapping.getId();
        this.custId = customerServiceMapping.getCustId();
        this.serviceId = customerServiceMapping.getServiceId();
    }
}
