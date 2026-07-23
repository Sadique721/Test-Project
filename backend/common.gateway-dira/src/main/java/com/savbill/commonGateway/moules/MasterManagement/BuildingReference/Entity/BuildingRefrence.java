package com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblmbuildingref")
public class BuildingRefrence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "mapping_from")
    private String mappingFrom;
    @Column(name = "mvnoid")
    private Integer mvnoId;
}
