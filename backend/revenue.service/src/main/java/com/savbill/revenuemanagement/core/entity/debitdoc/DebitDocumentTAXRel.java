package com.savbill.revenuemanagement.core.entity.debitdoc;

import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbltdebitdocumenttaxrel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitDocumentTAXRel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdoctaxid")
    private Integer debitdoctaxid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;

    @Column(name = "taxid")
    private Integer taxid;

    @Column(name = "taxname")
    private String taxname;

    @Column(name = "description")
    private String description;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "taxlevel")
    private Double taxlevel;

    @Column(name="startdate")
    private LocalDateTime startdate;

    @Column(name="enddate")
    private LocalDateTime enddate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "chargeid")
    private Integer chargeid;

    @Column(name = "tax_ledger_id")
    private String taxLedgerId;

    @Column(name = "doc_detail_id")
    private Long documentDetailId;

    @Transient
    private Double chargeAmount;

    @Transient
    private String planName;

    @Transient
    private Double discount;


    @Transient
    private Double discountAmount;

    @Transient
    private List<TaxTypeTier> taxTypeTiers;
    /**
     * @retun copy of debitDocumentTAXRel
     * @param debitDocumentTAXRel
     */
    public DebitDocumentTAXRel(DebitDocumentTAXRel debitDocumentTAXRel) {
        this.debitdocumentid = debitDocumentTAXRel.getDebitdocumentid();
        this.taxid = debitDocumentTAXRel.getTaxid();
        this.enddate = debitDocumentTAXRel.getEnddate();
        this.startdate = debitDocumentTAXRel.getStartdate();
        this.chargeid = debitDocumentTAXRel.getChargeid();
        this.chargeAmount = debitDocumentTAXRel.getChargeAmount();
        this.discount=debitDocumentTAXRel.getDiscount();
        this.documentDetailId=debitDocumentTAXRel.getDocumentDetailId();
    }

    public DebitDocumentTAXRel(Double taxlevel,Integer chargeid,String taxName, Double percentage, Double amount){
        this.taxname=taxName;
        this.percentage=percentage;
        this.amount=amount;
        this.chargeid=chargeid;
        this.taxlevel=taxlevel;
    }

}
