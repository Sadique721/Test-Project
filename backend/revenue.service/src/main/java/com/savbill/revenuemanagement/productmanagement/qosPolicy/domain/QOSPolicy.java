//package com.savbill.revenuemanagement.productmanagement.qosPolicy.domain;
//
//
//import com.savbill.revenuemanagement.core.data.IBaseData2;
//import com.savbill.revenuemanagement.core.dto.common.Auditable;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Data;
//import org.hibernate.annotations.LazyCollection;
//import org.hibernate.annotations.LazyCollectionOption;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Data
//@Entity
//@Table(name = "tbl_qos_policy")
//
//public class QOSPolicy extends Auditable implements IBaseData2<Long> {
//
//    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String description;
//    private String basepolicyname;
//    private String thpolicyname;
//    private String baseparam1;
//    private String baseparam2;
//    private String baseparam3;
//    private String thparam1;
//    private String thparam2;
//    private String thparam3;
//    @Column(columnDefinition = "Boolean default false", nullable = false)
//    private Boolean isDeleted = false;
//
//    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
//    private Integer mvnoId;
//
//    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
//    private Long buId;
//
//    private String type;
//
//    private String qosspeed;
//
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = QOSPolicyGatewayMapping.class, cascade = CascadeType.ALL,orphanRemoval = true)
//    @JoinColumn(name = "qos_policy_id")
//    List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList;
//
////    private String type;
//
//    @JsonIgnore
//    @Override
//    public Long getPrimaryKey() {
//        return id;
//    }
//
//    @JsonIgnore
//    @Override
//    public void setDeleteFlag(boolean deleteFlag) {
//        this.isDeleted = deleteFlag;
//    }
//
//    @JsonIgnore
//    @Override
//    public boolean getDeleteFlag() {
//        return isDeleted;
//    }
//}
