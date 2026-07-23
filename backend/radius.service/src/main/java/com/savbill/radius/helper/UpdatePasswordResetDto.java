package com.savbill.radius.helper;

import com.savbill.radius.kafka.CustomMessage;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Customer Update Password", description = "This is data transfer object for customer which is used to update customer data")
public class UpdatePasswordResetDto {

    private String username;

    private String password;

    private Integer id;

    private Long mvnoId;

    public UpdatePasswordResetDto(CustomMessage message) {

        Map<String, Object> map = message.getCustomerData();
        if(map.get("mvnoId") != null){
            this.setMvnoId(Long.parseLong(map.get("mvnoId").toString()));
        }
        if(map.get("id") != null){
            this.setId(Integer.parseInt(map.get("id").toString()));
        }
        if(map.get("password") != null){
            this.setPassword(map.get("password").toString());
        }
    }

    }
