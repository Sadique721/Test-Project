package com.savbill.revenuemanagement.core.entity.ladger;

import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltcreditdoctaxrel")
@AllArgsConstructor
@NoArgsConstructor
public class CreditDocTaxRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creditdoctaxid", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "CHARGEID")
    @OneToOne(cascade = CascadeType.ALL)
    private Charge charge;

    @JoinColumn(name = "CREDITDOCID")
    @OneToOne(cascade = CascadeType.ALL)
    private CreditDocument creditDocument;

    @Column(name = "tax_amount", nullable = false, length = 40)
    private Double taxAmount;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creditdocchargeid")
    private CreditDocChargeRel creditDocChargeRel;

    public CreditDocTaxRel(Charge charge, CreditDocument creditDocument, Double taxAmount, CreditDocChargeRel creditDocChargeRel) {
        this.charge = charge;
        this.creditDocument = creditDocument;
        this.taxAmount = taxAmount;
        this.creditDocChargeRel = creditDocChargeRel;
    }
}
