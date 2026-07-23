package com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping;

import com.savbill.commonGateway.core.data.IBaseData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltteamusermapping")
public class TeamUserMapping implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name="team_id",nullable=false)
    private Long teamId;

    @Column (name="staffid", nullable = false)
    @ApiModelProperty(notes = "This is location id of location master table",hidden = true)
    private Long staffId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
