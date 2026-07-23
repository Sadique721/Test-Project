package com.savbill.inventorymanagement.modules.PartnerManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tblmpartners")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class Partner extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "partnername", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "city", nullable = false, length = 40)
    private Integer city;

    @Column(name = "state", nullable = false, length = 40)
    private Integer state;

    @Column(name = "country", nullable = false, length = 40)
    private Integer country;

    @Column(name = "pincode", nullable = false, length = 40)
    private String pincode;

    @Column(name = "partner_type", nullable = false, length = 40)
    private String partnerType;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltpartnerservicearearel", joinColumns = {@JoinColumn(name = "partnerid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaList = new ArrayList<>();

    @Column(name = "parentpartnerid")
    private Long parentPartner;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "branch")
    private Long branch ;


	@Override
	public String toString() {
		return "Partner []";
	}

    public Partner(Integer id) {
        this.id = id;
    }

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }

    public Partner(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
