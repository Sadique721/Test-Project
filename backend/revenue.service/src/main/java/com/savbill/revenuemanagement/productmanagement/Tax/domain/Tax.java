package com.savbill.revenuemanagement.productmanagement.Tax.domain;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "TBLMTAX")
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditableListener.class)
public class Tax extends Auditable {
    @Id
    @Column(name = "TAXID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 150)
    private String desc;

    @Column(name = "TAXTYPE", nullable = false, length = 40)
    private String taxtype;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeTier> tieredList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;


    public Tax() {
    }

    public Tax(Integer id) {
        this.id = id;
    }


    public Tax(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

}
