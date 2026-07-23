package com.savbill.integrationsystem.billgen.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tbltdebitdocumenttaxrel")
public class DebitDocumentTAXRel {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "tax_ledger_id")
    private String taxLedgerId;


//    public DebitDocumentTAXRel(String taxName, Double percentage, Double amount){
//        this.taxname=taxName;
//        this.percentage=percentage;
//        this.amount=amount;
//        this.taxLedgerId=tac
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DebitDocumentTAXRel that = (DebitDocumentTAXRel) o;
        return getDebitdoctaxid() != null && Objects.equals(getDebitdoctaxid(), that.getDebitdoctaxid());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
