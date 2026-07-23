package com.savbill.ticketmanagement.core.modules.tickets.model;

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

