package com.savbill.inventorymanagement.modules.PostpaidPlanServiceAreaMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "tbltplanservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PostPaidPlanServiceAreaMapping extends Auditable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceId;

}
