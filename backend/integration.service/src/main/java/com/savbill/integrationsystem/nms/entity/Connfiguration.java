package com.savbill.integrationsystem.nms.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tblnmsconfiguration")
@Data
public class Connfiguration implements IBaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "base_url")
    private String baseurl;
    @Column(name = "port")
    private String port;
    @Column(name = "is_deleted")
    private Boolean isdeleted;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "mvno_id", length = 40, updatable = false)
    private Integer mvnoId;

    @Override
    public Serializable getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
