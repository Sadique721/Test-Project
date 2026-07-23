package com.savbill.integrationsystem.deviceveri.domain;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmarea")
public class AreaData implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaid")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "COUNTRYID")
    private Long countryid;
    @Column(name = "STATEID")
    private Long stateid;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "createdbystaffid")
    private Integer createdbystaffid;
    @Column(name = "createdate")
    private LocalDateTime createdate;
    @Column(name = "lastmodifiedbystaffid")
    private Integer lastmodifiedbystaffid;
    @Column(name = "lastmodifieddate")
    private LocalDateTime lastmodifieddate;
    @Column(name = "createbyname")
    private String createbyname;
    @Column(name = "updatebyname")
    private String updatebyname;
    @Column(name = "CITYID")
    private Long cityid;
    @Column(name = "status")
    private String status;
    @Column(name = "pincodeid")
    private Long pincodeid;
    @Column(name = "MVNOID")
    private Long mvnoid;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getDeleteFlag() {
        // TODO Auto-generated method stub
        return false;
    }
}
