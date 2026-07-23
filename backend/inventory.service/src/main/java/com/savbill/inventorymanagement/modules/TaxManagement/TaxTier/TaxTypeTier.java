package com.savbill.inventorymanagement.modules.TaxManagement.TaxTier;

import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblmtiertax")
public class TaxTypeTier {
	
	
	/*
	 CREATE TABLE TBLMTIERTAX
  (
    TIERTAXID serial,
    NAME      VARCHAR(64) NOT NULL,
    TAXGROUP  VARCHAR(10),
    RATE      NUMERIC(10,2),
    TAXID     bigint UNSIGNED,
    PRIMARY KEY (TIERTAXID),	
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );
 
	 */

    public TaxTypeTier(TaxTypeTierPojo pojo, Tax tax) {
        this.name = pojo.getName();
        this.rate = pojo.getRate();
        this.taxGroup = pojo.getTaxGroup();
//        this.tax = tax;
        this.beforeDiscount=pojo.getBeforeDiscount();
        this.taxLedgerId=pojo.getLedgerId();
    }

    public TaxTypeTier() {
        // TODO Auto-generated constructor stub
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tiertaxid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "taxgroup", nullable = false, length = 40)
    private String taxGroup;

    @Column(name = "rate", nullable = false, length = 40)
    private Double rate;

//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "taxid")
//    @ToString.Exclude
//    private Tax tax;

    @Column(name = "taxid")
    private Long taxid;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

    @Column(name = "ledger_id")
    private String taxLedgerId;


}


