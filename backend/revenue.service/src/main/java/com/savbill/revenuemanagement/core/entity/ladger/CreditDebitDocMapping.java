package com.savbill.revenuemanagement.core.entity.ladger;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltcreditdebitmapping")
public class  CreditDebitDocMapping {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creddebtmappingid", nullable = false, length = 40)
    private Integer id;

    @DiffIgnore
    @Column(name="CREDITDOCID", length = 40)
    private Integer creditDocId;
    @DiffIgnore
    @Column(name="debitdocumentid", length = 40)
    private Integer debtDocId;

    @Transient
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;


    @Column(name = "adjustedamount", length = 40)
    private Double adjustedAmount;
    @DiffIgnore
    @Column(name = "amount", length = 40)
    private Double amount;
    @DiffIgnore
    @Column(name = "abbs_amount", length = 40)
    private Double abbsAmount;
    @DiffIgnore
    @Column(name = "tds_amount", length = 40)
    private Double tdsAmount;
    @DiffIgnore
    @Column(name="withdrawal_id", length = 40)
    private Integer withdrawId;

    @DiffIgnore
    @Column(name="trialdebitdocumentid", length = 40)
    private Integer trialDebitDocumentId;

    public CreditDebitDocMapping(CreditDebitDocMapping creditDebitDocMapping) {
        this.id = creditDebitDocMapping.getId();
        this.creditDocId = creditDebitDocMapping.getCreditDocId();
        this.debtDocId = creditDebitDocMapping.getDebtDocId();
        this.isDeleted = creditDebitDocMapping.getIsDeleted();
        this.adjustedAmount = creditDebitDocMapping.getAdjustedAmount();
        this.amount = creditDebitDocMapping.getAmount();
        this.abbsAmount = creditDebitDocMapping.getAbbsAmount();
        this.tdsAmount = creditDebitDocMapping.getTdsAmount();
        this.withdrawId = creditDebitDocMapping.getWithdrawId();
    }
}
