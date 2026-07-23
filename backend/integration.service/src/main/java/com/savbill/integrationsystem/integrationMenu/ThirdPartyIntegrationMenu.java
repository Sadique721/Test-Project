package com.savbill.integrationsystem.integrationMenu;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.integrationMenuMapping.ThirdPartyIntegrationMenuMapping;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "tblmthirdpartymenu")
public class ThirdPartyIntegrationMenu  implements IBaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "client_name")
    private String clientName;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ThirdPartyIntegrationMenuMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "third_party_menu_id")
    private List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappingList;

    @Column(name = "status")
    private String status;

    @Column(name = "is_delete",nullable = false)
    private Boolean isDelete = false;

    @Column(name = "mvno_id")
    private Long mvnoId;


    @Override
    public Serializable getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
