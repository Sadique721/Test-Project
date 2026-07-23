package com.savbill.revenuemanagement.core.entity.partner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltpartnercreditdebitmapping")
public class  PartnerCreditDebitMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creddebtmappingid", nullable = false, length = 40)
    private Integer id;

    @Column(name="creditdocumentid", length = 40)
    private Integer creditDocId;

    @Column(name="debitdocumentid", length = 40)
    private Integer debtDocId;

    @Transient
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "adjustedamount", length = 40)
    private Double adjustedAmount;

}
