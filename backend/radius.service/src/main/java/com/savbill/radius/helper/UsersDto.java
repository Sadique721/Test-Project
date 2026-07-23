package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Username List",description = "This is Username List to identify online status")
public class UsersDto {
    @ApiModelProperty(notes = "Username list",required=true)
    private List<String> users;
}
