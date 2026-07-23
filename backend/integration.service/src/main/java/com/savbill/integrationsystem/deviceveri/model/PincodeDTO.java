package com.savbill.integrationsystem.deviceveri.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class PincodeDTO extends Auditable<Long> implements IBaseDto {
    private Long id;
    private String pincode;
    private Long countryid;
    private Long stateid;
    private Long cityid;
    private Boolean isDeleted;
    private Integer createdbystaffid;
    private LocalDateTime createdate;
    private Integer lastmodifiedbystaffid;
    private LocalDateTime lastmodifieddate;
    private String createbyname;
    private String updatebyname;
    private String status;
    private Long mvnoid;

    @Override
    public Long getIdentityKey() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public Long getMvnoId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        // TODO Auto-generated method stub

    }
}
