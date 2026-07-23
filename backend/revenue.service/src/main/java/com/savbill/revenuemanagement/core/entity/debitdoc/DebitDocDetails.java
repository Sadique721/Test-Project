package com.savbill.revenuemanagement.core.entity.debitdoc;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbltdebitdocumentdetail")
public class DebitDocDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocdetailid")
    private Integer debitdocdetailid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;

//    @Column(name = "debitdocumentid")
//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "debitdocumentid")
//    @ToString.Exclude
//    private DebitDocument debitDocument;


    @Column(name = "chargeid")
    private Integer chargeid;
    @Column(name = "chargename")
    private String chargename;
    @Column(name = "description")
    private String description;
    @Column(name = "chargetype")
    private String chargetype;
    @Column(name = "chargecycle")
    private String chargecycle;
    @Column(name = "subtotal")
    private Double subtotal;
    @Column(name = "tax")
    private Double tax;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "totalamount")
    private Double totalamount;

    @Column(name = "startdate")
    private LocalDateTime startdate;
    @Column(name = "enddate")
    private LocalDateTime enddate;
    @Column(name = "prorationtype")
    private String prorationtype;
    @Column(name = "noofcycle")
    private Integer noofcycle;
    @Column(name = "planid")
    private String planId;

    @Column(name = "ledger_id")
    private String ledgerId=null;

    @Column(name = "iccode")
    private String icCode=null;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

    @Column(name = "cust_service_id")
    private Long custServiceId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "offer_price")
    private Double offerPrice;

    @Column(name = "mvnodebitdocumentid")
    private Integer mvnodebitdocumentid;

    @Column(name = "current_installment_no")
    private Integer currentInstallmentNo;

    @Column(name = "installment_charge")
    private Double installmentCharge;

    @DiffIgnore
    @Column(name = "installment_interest", nullable = false, length = 40)
    private Double installmentInterest;
}
