package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.TeamHierarchyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTeamHierarchyDTO
{
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private List<TeamHierarchyDTO> teamHierarchyDTO;
}
