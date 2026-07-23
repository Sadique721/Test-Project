package com.savbill.taskmanagement.core.modules.tasks.model;

import lombok.Data;

@Data
public class FileDetailsDTO {
    private String fileName;
    private String uniqueName;
    private String latitude;
    private String longitude;
    private Long ticketId;
    private String opticalRange;
}