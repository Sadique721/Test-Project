package com.savbill.taskmanagement.core.modules.ResolutionReasons.domain;


import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.modules.tasks.domain.ResoSubCategoryMapping;
import com.savbill.taskmanagement.core.modules.tasks.domain.ResoultionFileMapping;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="tblcaseresolutions")
@NoArgsConstructor
public class ResolutionReasons extends Auditable implements IBaseData<Long> {

    @DiffIgnore
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
    @OneToMany(targetEntity = ResoSubCategoryMapping.class ,orphanRemoval = true,  cascade = CascadeType.ALL)
    @JoinColumn(name = "res_id")
    List<ResoSubCategoryMapping> resoSubCategoryMappingList;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;
    @DiffIgnore
    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = RootCauseResolutionMapping.class,orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "resolution_id" ,referencedColumnName = "res_id")
    private List<RootCauseResolutionMapping> rootCauseResolutionMappingList;

    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "resolution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ResoultionFileMapping> resoultionFileMappings = new ArrayList<>();

//    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
//    private Integer lcoId;

    public ResolutionReasons(Long id) {
        this.id = id;
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
