package com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tbltteamhierarchymapping")
public class TeamHierarchyMapping implements Serializable{


    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "team_id",nullable = false)
    Integer teamId;

    @Column(name = "hierarchy_id", nullable = false)
    Integer hierarchyId;

    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
}
