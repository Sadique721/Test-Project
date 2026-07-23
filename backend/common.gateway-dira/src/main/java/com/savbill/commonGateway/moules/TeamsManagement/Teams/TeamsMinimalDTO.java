package com.savbill.commonGateway.moules.TeamsManagement.Teams;

public interface TeamsMinimalDTO {

    Long getId();
    String getName();
    String getStatus();
    Integer getMvnoId();
    Long getPartnerid();
    Boolean getIsDeleted();
}
