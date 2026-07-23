package com.savbill.integrationsystem.Case;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblcaseupdatedetails")
public class CaseUpdateDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "updatedtlsid")
    private Long id;
    private String operation;
    private String entitytype;
    private String oldvalue;
    private String newvalue;
    private String attachment;
    private String filename;
    @ManyToOne
    @JoinColumn(name = "resolutionid")
    private ResolutionReasons resolution;
    private String remarktype;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "updateid")
    @ToString.Exclude
    private CaseUpdate caseUpdate;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

}
