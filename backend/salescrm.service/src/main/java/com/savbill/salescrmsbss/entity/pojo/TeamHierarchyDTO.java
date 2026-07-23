package com.savbill.salescrmsbss.entity.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamHierarchyDTO {

    private Long teamsId;

    private String TeamName;

    private String status;

    private Long parentTeamsId;
}
