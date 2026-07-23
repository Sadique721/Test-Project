package com.savbill.commonGateway.moules.OTP.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Generate Otp", description = "This is data transfer object to generate otp")
public class OTPGenerateDTO {


    private String username;

    private String password;

    private Boolean otpForStaff;
}
