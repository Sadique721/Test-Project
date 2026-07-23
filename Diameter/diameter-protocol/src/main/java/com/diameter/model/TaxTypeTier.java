package com.diameter.model;

import com.diameter.dto.TaxTypeTierPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblmtiertax")
public class TaxTypeTier {
	
    public TaxTypeTier(TaxTypeTierPojo pojo, Tax tax) {
        this.name = pojo.getName();
        this.rate = pojo.getRate();
        this.taxGroup = pojo.getTaxGroup();
        this.tax = tax;
        this.beforeDiscount=pojo.getBeforeDiscount();
        this.taxLedgerId=pojo.getLedgerId();
    }

    public TaxTypeTier() {
        // TODO Auto-generated constructor stub
    }

    @Id
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
    @EqualsAndHashCode.Exclude
    private Tax tax;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

    @DiffIgnore
    @Column(name = "ledger_id")
    private String taxLedgerId;


}


