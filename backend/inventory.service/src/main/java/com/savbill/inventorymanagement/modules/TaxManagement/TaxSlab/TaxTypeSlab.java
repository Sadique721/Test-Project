package com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab;

import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmslabtax")
public class TaxTypeSlab {

	/*
CREATE TABLE TBLMSLABTAX
  (
    SLABTAXID serial,
    NAME      VARCHAR(64) NOT NULL,
    RANGEFROM NUMERIC(16,4),
    RANGEUPTO NUMERIC(16,4),
    RATE      NUMERIC(10,2),
    TAXID     bigint UNSIGNED,
    PRIMARY KEY (SLABTAXID),
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );
 
	 */

    public TaxTypeSlab() {
        super();
    }

    public TaxTypeSlab(TaxTypeSlabPojo pojo, Tax tax) {
        this.id = pojo.getId();
        this.name = pojo.getName();
        this.rangeFrom = pojo.getRangeFrom();
        this.rangeUpTo = pojo.getRangeUpTo();
        this.rate = pojo.getRate();
//        this.tax = tax;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slabtaxid", nullable = false, length = 40)
    private Integer id;


    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "rangefrom", nullable = false, length = 40)
    private Double rangeFrom;

    @Column(name = "rangeupto", nullable = false, length = 40)
    private Double rangeUpTo;

    @Column(name = "rate", nullable = false, length = 40)
    private Double rate;


//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "taxid")
//    @ToString.Exclude
//    private Tax tax;

    @Column(name = "taxid")
    private Long taxid;

    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

}
