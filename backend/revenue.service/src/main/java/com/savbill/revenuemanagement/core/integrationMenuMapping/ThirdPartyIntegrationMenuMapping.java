package com.savbill.revenuemanagement.core.integrationMenuMapping;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblmthirdpartymenumappinng")
public class ThirdPartyIntegrationMenuMapping {
    @Id
    @Column(name = "id")
    private Long integrationMenuMappingId;

    @Column(name = "third_party_menu_id",nullable = false)
    private Long thirdPartyIntegrationMenuId;
;

    @ApiModelProperty(notes = "This is Third Party Integration Menu parameter name")
    @Column (name="third_party_param_name")
    private String thirdPartyParameterName;

    @ApiModelProperty(notes = "This is Third Party Integration Menu parameter value")
    @Column (name="third_party_param_value")
    private String thirdPartyParameterValue;

    @ApiModelProperty(notes = "This is a describe parameter name")
    @Column (name="third_party_parame_desc")
    private String thirdPartyParamDesc;

}
