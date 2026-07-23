package com.savbill.commonGateway.rabbitmq.messages;


import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PincodeMessage {

    private Long id;

    private String pincode;

    private String status;

    private Boolean isDeleted;

    private Integer countryId;

    private Integer cityId;

    private Integer stateId;

    private Integer mvnoId;

    public PincodeMessage(PincodeDTO pincode) {
        this.id = pincode.getPincodeid();
        this.pincode=pincode.getPincode();
        this.status=pincode.getStatus();
        this.isDeleted=pincode.getIsDeleted();
        this.countryId=pincode.getCountryId();
        this.cityId=pincode.getCityId();
        this.stateId=pincode.getStateId();
        this.mvnoId=pincode.getMvnoId();
    }
}
