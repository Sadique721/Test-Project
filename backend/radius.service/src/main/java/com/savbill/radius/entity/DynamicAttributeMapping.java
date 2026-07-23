package com.savbill.radius.entity;

import com.savbill.radius.helper.DynamicAttributeMappingDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tblmdynamicattributemapping")
@ApiModel(value = "Coa DM client Group Profile Mapping ",description = "This is coa dm client group mapping entity.")
@Data
@NoArgsConstructor
public class DynamicAttributeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @Column(name = "customer_attribute")
    private String customerAttribute;

    @Column(name = "radius_attribute")
    private String radiusAttribute;

    @Column(name = "is_absence_accepted")
    private Boolean isAbsenceAccepted;

    public DynamicAttributeMapping(DynamicAttributeMappingDto attributeMappingDto, Long clientGroupId) {
        this.id = attributeMappingDto.getId();
        this.clientGroupId = clientGroupId;
        this.customerAttribute = attributeMappingDto.getCustomerAttribute();
        this.radiusAttribute = attributeMappingDto.getRadiusAttribute();
        this.isAbsenceAccepted = attributeMappingDto.getIsAbsenceAccepted();
    }
}
