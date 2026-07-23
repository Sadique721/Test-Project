package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TBLMVLANVALIDATIONMAPPING")
@ApiModel(value = "VLAN Validation mapping entity", description = "This is Vlan management entity which is used to update VLAN validation mapping data.")
@Data
public class VLANValidationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated vlan Id", required = true)
    @Column(name = "VALIDATIONMAPPINGID", nullable = false)
    private Long validationMappingId;

    @Column(name = "VLANID", nullable = false, length = 100)
    private String vlanId;

    @Column(name = "RADIUS_ATTRIBUTE", nullable = false, length = 100)
    private String radiusAttribute;

    @Column(name = "REGEX", nullable = false, length = 100)
    private String regex;

    @Column(name = "REGEXVALUE", length = 1000)
    private String regexValue;

}
