package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "Businessunit", description = "This is data transfer object for business unit which is used to create new businessunit")
public class BusinessUnitDto {
    @ApiModelProperty(notes = "Id of the business unit")
    private Long id;

    @ApiModelProperty(notes = "Name of the business unit")
    private String buname;

    @ApiModelProperty(notes = "This is business unit code")
    private String bucode;

    @ApiModelProperty(notes = "Status of business unit")
    private String status;

    @ApiModelProperty(notes = "Is Delete of business unit")
    private Boolean isDeleted = false;

    @ApiModelProperty(notes = "MVNOID of business unit")
    private Integer mvnoId;

    @ApiModelProperty(notes = "Plan binding type of businsess unit")
    private String planBindingType;
}
