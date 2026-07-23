package com.savbill.commonGateway.rabbitmq.messages;

import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateMessage {

    private Integer id;

    private String name;

    private String status;

    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;

    public StateMessage(State obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.status=obj.getStatus();
        this.country=obj.getCountry();
        this.isDeleted=obj.getIsDeleted();
        this.mvnoId=obj.getMvnoId();
    }
}
