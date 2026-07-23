package com.savbill.revenuemanagement.core.dto.invoice;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import lombok.Data;

@Data
public class XslManagementPojo extends Auditable {


    private Integer id;

    private String templatename;

    private String templatetype;

    private String status;

    private String jrxmlfile;

    private Boolean isDelete = false;
    
    private Integer mvnoId;
    private Integer lcoid;
    private String mvnoName;

    /*    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public String getTemplatetype() {
        return templatetype;
    }

    public void setTemplatetype(String templatetype) {
        this.templatetype = templatetype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJrxmlfile() {
        return jrxmlfile;
    }

    public void setJrxmlfile(String jrxmlfile) {
        this.jrxmlfile = jrxmlfile;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        this.updatedate = updatedate;
    }*/

    @Override
    public String toString() {
        return "XslManagementPojo{" +
                "id=" + id +
                ", templatename='" + templatename + '\'' +
                ", templatetype='" + templatetype + '\'' +
                ", status='" + status + '\'' +
                ", jrxmlfile='" + jrxmlfile + '\'' +
                ", createdate=" + super.getCreatedate() +
                ", updatedate=" + super.getUpdatedate() +
                '}';
    }
}
