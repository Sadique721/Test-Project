package com.savbill.partnermanagement.modules.Tax.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TBLMTIERTAX")
public class TaxTypeTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIERTAXID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "TAXGROUP", nullable = false, length = 40)
    private String taxGroup;

    @Column(name = "RATE", nullable = false, length = 40)
    private Double rate;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "TAXID")
    @ToString.Exclude
    private Tax tax;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

    public TaxTypeTier() {
    }

    @Column(name = "ledger_id")
    private String taxLedgerId;


    public TaxTypeTier(TaxTypeTier list) {
        this.id=list.id;
        this.name= list.name;
        this.tax=list.getTax();
        this.taxGroup= list.taxGroup;
        this.taxLedgerId= list.taxLedgerId;
        this.rate=list.getRate();
        this.isDelete=list.getIsDelete();
        this.beforeDiscount=list.getBeforeDiscount();
    }
}


