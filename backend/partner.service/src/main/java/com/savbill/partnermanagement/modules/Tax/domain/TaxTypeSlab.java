package com.savbill.partnermanagement.modules.Tax.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMSLABTAX")
public class TaxTypeSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SLABTAXID", nullable = false, length = 40)
    private Integer id;


    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "RANGEFROM", nullable = false, length = 40)
    private Double rangeFrom;

    @Column(name = "RANGEUPTO", nullable = false, length = 40)
    private Double rangeUpTo;

    @Column(name = "RATE", nullable = false, length = 40)
    private Double rate;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "TAXID")
    @ToString.Exclude
    private Tax tax;

    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

}
