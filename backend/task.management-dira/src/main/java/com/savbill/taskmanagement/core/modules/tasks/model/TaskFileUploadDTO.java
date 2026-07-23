package com.savbill.taskmanagement.core.modules.tasks.model;


import lombok.Data;

import java.util.List;

@Data
public class TaskFileUploadDTO {

    private List<SectionUploadDTO> sections;

}
