package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolyGoneDTO extends Auditable implements IBaseDto {
    private Long id;

    private Integer serviceAreaId;

    private String lat;

    private String lng;

    private Integer polyOrder;

    private String serviceAreaName ;

    private Integer mvnoId;

    private String polygoneName;



    @Override
    public Long getIdentityKey() {
        return null;
    }


}
