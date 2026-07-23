package com.savbill.cpm.pojo.api;

import lombok.Data;

import com.savbill.cpm.model.common.Auditable;

import javax.validation.constraints.NotNull;

@Data
public class ClientsPojo extends Auditable {

    private Integer id;

    @NotNull
    private String clientip;

    @NotNull
    private String sharedkey;

    @NotNull
    private String timeout;

    @NotNull
    private String iptype;

    @NotNull
    private Integer clientgroupid;

    private Boolean isDelete = false;

    private String createDateString;
    private String updateDateString;


    @Override
    public String toString() {
        return "ClientPojo [id=" + id + ", clientip=" + clientip + ", sharedkey=" + sharedkey + ", timeout=" + timeout
                + ", iptype=" + iptype + ", createdate=" + getCreatedate() + ", updatedate=" + getUpdatedate()
                + ", clientgroupid=" + clientgroupid + "]";
    }
}
