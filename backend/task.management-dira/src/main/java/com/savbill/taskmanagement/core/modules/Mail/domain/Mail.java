package com.savbill.taskmanagement.core.modules.Mail.domain;

import com.savbill.taskmanagement.core.data.IBaseData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tblmailservice")
public class Mail implements IBaseData {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;

    @Column(name = "receiver", length = 100)
    private String receiver;

    @Column(name = "sender", length = 100)
    private String sender;

    @Column(name = "carbon_copy", length = 100)
    private String cc;

    @Column(name = "summary", length = 100)
    private String summary;

    @Column(name = "description", length = 500)
    private String desc;
    @Column(name = "is_delete", length = 1)
    private Boolean isDelete;

    @Column(name = "issue_id",length = 1)
    private Long issueid;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "folder", length = 100)
    private String folder;

    @Column(name = "is_new", length = 1)
    private Boolean isNew;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "mail_type")
    private String mailType;

    @ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="MVNOID", nullable = false)
    private Long mvnoId;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean isDelete) {
        this.isDelete = isDelete;
    }
    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }


}
