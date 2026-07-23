package com.savbill.integrationsystem.billgen.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tbltdebitdocumentdetail")
public class DebitDocDetails {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocdetailid")
    private Integer debitdocdetailid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;
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
    private Integer tax;
    @Column(name = "discount")
    private Integer discount;
    @Column(name = "totalamount")
    private Double totalamount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "startdate")
    private LocalDateTime startdate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "enddate")
    private LocalDateTime enddate;

    @Column(name = "prorationtype")
    private String prorationtype;
    @Column(name = "noofcycle")
    private Integer noofcycle;
    @Column(name = "planid")
    private String planId;

    @Column(name = "ledger_id")
    private String ledgerId = null;

    @Column(name = "iccode")
    private String icCode = null;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DebitDocDetails that = (DebitDocDetails) o;
        return getDebitdocdetailid() != null && Objects.equals(getDebitdocdetailid(), that.getDebitdocdetailid());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
