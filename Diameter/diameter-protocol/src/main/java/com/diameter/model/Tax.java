
package com.diameter.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmtax")
//@EntityListeners(AuditableListener.class)
public class Tax
//        extends Auditable
{

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

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeTier> tieredList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "mvnoName")
    private String mvnoName;


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
