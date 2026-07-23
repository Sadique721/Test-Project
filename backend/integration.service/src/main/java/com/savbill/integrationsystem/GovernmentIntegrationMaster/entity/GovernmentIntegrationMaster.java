package com.savbill.integrationsystem.GovernmentIntegrationMaster.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tblmgovernmentintegrationmaster")
public class GovernmentIntegrationMaster extends Auditable<Long> implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 40)
    private String username;

    @Column(name = "password", length = 40)
    private String password;

    @Column(name = "mvnoid")
    private Long mvnoId;

//    @Column(name = "isdelete")
//    private Boolean isdelete;

    @Column(name = "isdelete")
    private Boolean isdelete;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = GovernmentAPIMappings.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "government_master_id")
    List<GovernmentAPIMappings> governmentAPIMappings;

    @Column(name = "status")
    private String status;
    
    @Column(name = "pan")
    private String pan;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isdelete = deleteFlag;

    }

    @Override
    public boolean getDeleteFlag() {
        return isdelete;
    }
}
