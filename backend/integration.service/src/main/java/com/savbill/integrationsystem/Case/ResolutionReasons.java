package com.savbill.integrationsystem.Case;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="tblcaseresolutions")
public class ResolutionReasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="res_id")
    private Long id;

    @Column(name="res_name")
    private String name;
    @Column(name="res_status")
    private String status;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ResoSubCategoryMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "res_id")
    List<ResoSubCategoryMapping> resoSubCategoryMappingList;


    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = RootCauseResolutionMapping.class,orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "resolution_id")
    private List<RootCauseResolutionMapping> rootCauseResolutionMappingList;


}
