package com.savbill.partnermanagement.customers;

import com.savbill.partnermanagement.modules.Tax.domain.TaxTypeTier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
@Table(name = "tblttrialdebitdocumenttaxrel")
public class TrialDebitDocumentTAXRel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trialdebitdoctaxid")
    private Integer trialdebitdoctaxid;

    @Column(name = "trialdebitdocumentid")
    private Integer trialdebitdocumentid;

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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name="startdate")
    private LocalDateTime startdate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name="enddate")
    private LocalDateTime enddate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "chargeid")
    private Integer chargeid;

    @Column(name = "taxledgerid")
    private String taxLedgerId;

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
     * @param trialDebitDocumentTAXRel
     */
    public TrialDebitDocumentTAXRel(TrialDebitDocumentTAXRel trialDebitDocumentTAXRel) {
        this.trialdebitdocumentid = trialDebitDocumentTAXRel.getTrialdebitdocumentid();
        this.taxid = trialDebitDocumentTAXRel.getTaxid();
        this.enddate = trialDebitDocumentTAXRel.getEnddate();
        this.startdate = trialDebitDocumentTAXRel.getStartdate();
        this.chargeid = trialDebitDocumentTAXRel.getChargeid();
        this.chargeAmount = trialDebitDocumentTAXRel.getChargeAmount();
        this.discount=trialDebitDocumentTAXRel.getDiscount();
    }

    public TrialDebitDocumentTAXRel(Double taxlevel,Integer chargeid,String taxName, Double percentage, Double amount){
        this.taxname=taxName;
        this.percentage=percentage;
        this.amount=amount;
        this.chargeid=chargeid;
        this.taxlevel=taxlevel;
    }

}

