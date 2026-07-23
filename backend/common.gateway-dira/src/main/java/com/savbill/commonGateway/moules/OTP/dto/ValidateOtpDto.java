package com.savbill.commonGateway.moules.OTP.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Validate Otp",description = "This is data transfer object to validate otp")
public class ValidateOtpDto {
    @ApiModelProperty(notes = "This is username",required = false)
    private String username;
    @ApiModelProperty(notes = "This is generated otp",required = true)
    private String otp;
    @ApiModelProperty(notes = "This is for check validate otp for staff or customer",required = true)
    private Boolean otpForStaff;
}
